package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.netherinvasion.NetherInvasion;
import net.minecraft.world.netherinvasion.NetherInvasionManager;
import net.minecraft.world.netherinvasion.invader.AbstractNetherInvaderEntity;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class InvasionCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("invasion")
                .requires(source -> source.hasPermission(3))
                .then(Commands.literal("start")
                        .then(Commands.argument("omenlvl", IntegerArgumentType.integer(0))
                                .executes(context -> startRaid(context.getSource(), IntegerArgumentType.getInteger(context, "omenlvl")))))
                .then(Commands.literal("stop")
                        .executes(context -> stopRaid(context.getSource())))
                .then(Commands.literal("check")
                        .executes(context -> checkRaid(context.getSource())))
                .then(Commands.literal("sound")
                        .then(Commands.argument("type", ComponentArgument.textComponent())
                                .executes(context -> playRaidSound(context.getSource(), ComponentArgument.getComponent(context, "type")))))
                .then(Commands.literal("spawnleader")
                        .executes(context -> spawnRaidLeader(context.getSource())))
                .then(Commands.literal("setomen")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                .executes(context -> setBadOmenLevel(context.getSource(), IntegerArgumentType.getInteger(context, "level")))))
                .then(Commands.literal("glow")
                        .executes(context -> makeRaidersGlow(context.getSource()))));
    }

    private static int makeRaidersGlow(CommandSource source) throws CommandSyntaxException {
        NetherInvasion raid = getRaid(getPlayer(source));
        if (raid != null) {
            for (AbstractNetherInvaderEntity raider : raid.getAllNetherInvaders()) {
                raider.addEffect(new EffectInstance(Effects.GLOWING, 1000, 1));
            }
        }
        return 1;
    }

    private static int setBadOmenLevel(CommandSource source, int level) throws CommandSyntaxException {
        NetherInvasion raid = getRaid(getPlayer(source));
        if (raid != null) {
            int maxBadOmenLevel = raid.getMaxBadOmenLevel();
            if (level > maxBadOmenLevel) {
                source.sendFailure(new StringTextComponent("Sorry, the max bad omen level you can set is " + maxBadOmenLevel));
            } else {
                int currentLevel = raid.getBadOmenLevel();
                raid.setBadOmenLevel(level);
                source.sendSuccess(new StringTextComponent("Changed village's bad omen level from " + currentLevel + " to " + level), false);
            }
        } else {
            source.sendFailure(new StringTextComponent("No raid found here"));
        }
        return 1;
    }

    private static int spawnRaidLeader(CommandSource source) {
        source.sendSuccess(new StringTextComponent("Spawned a raid captain"), false);
        AbstractNetherInvaderEntity raider = EntityType.PIGLIN_BRUTE.create(source.getEntity().getCommandSenderWorld());
        raider.setPatrolLeader(true);
        if (raider instanceof PiglinBruteEntity) {
            PiglinBruteEntity brute = (PiglinBruteEntity) raider;
            brute.setItemSlot(EquipmentSlotType.HEAD, Raid.getLeaderBannerInstance());
            brute.setImmuneToZombification(true);
            brute.setPos(source.getPosition().x(), source.getPosition().y(), source.getPosition().z());
           brute.finalizeSpawn((IServerWorld) source.getEntity().getCommandSenderWorld(), source.getEntity().getCommandSenderWorld().getCurrentDifficultyAt(new BlockPos(source.getPosition())), SpawnReason.COMMAND, (ILivingEntityData) null, (CompoundNBT) null);
            source.getEntity().getCommandSenderWorld().addFreshEntity(brute);
        }
        return 1;
    }

    private static int playRaidSound(CommandSource source, ITextComponent type) {
        if (type != null && "local".equals(type.getString())) {
            source.getEntity().getCommandSenderWorld().playSound(null, new BlockPos(source.getPosition().add(5.0D, 0.0D, 0.0D)), SoundEvents.RAID_HORN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
        }
        return 1;
    }

    private static int startRaid(CommandSource source, int omenLevel) throws CommandSyntaxException {
        ServerPlayerEntity player = getPlayer(source);
        BlockPos blockPos = new BlockPos(player.getX(), player.getY(), player.getZ());
        ServerWorld serverWorld = (ServerWorld) player.getCommandSenderWorld();
        NetherInvasionManager raidManager = serverWorld.getInvasions();
        NetherInvasion raid = raidManager.getInvasionAt(blockPos);
        if (raid != null) {
            source.sendFailure(new StringTextComponent("Raid already started close by"));
            return -1;
        } else {
            raid = raidManager.createOrExtendRaid(player);
            if (raid != null) {
                raid.setBadOmenLevel(omenLevel);
                source.sendSuccess(new StringTextComponent("Created a raid in your local village"), false);
            } else {
                source.sendFailure(new StringTextComponent("Failed to create a raid in your local village"));
            }
            return 1;
        }
    }

    private static int stopRaid(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = getPlayer(source);
        BlockPos blockPos = new BlockPos(player.getX(), player.getY(), player.getZ());
        ServerWorld serverWorld = (ServerWorld) player.getCommandSenderWorld();
        NetherInvasionManager raidManager = serverWorld.getInvasions();
        NetherInvasion raid = raidManager.getInvasionAt(blockPos);
        if (raid != null) {
            raid.stop();
            source.sendSuccess(new StringTextComponent("Stopped raid"), false);
            return 1;
        } else {
            source.sendFailure(new StringTextComponent("No raid here"));
            return -1;
        }
    }

    private static int checkRaid(CommandSource source) throws CommandSyntaxException {
        NetherInvasion raid = getRaid(getPlayer(source));
        if (raid != null) {
            StringBuilder message = new StringBuilder();
            message.append("Found a started raid! ");
            source.sendSuccess(new StringTextComponent(message.toString()), false);
            message = new StringBuilder();
            message.append("Num groups spawned: ");
            message.append(raid.getGroupsSpawned());
            message.append(" Bad omen level: ");
            message.append(raid.getBadOmenLevel());
            message.append(" Num mobs: ");
            message.append(raid.getTotalNetherInvadersAlive());
            message.append(" Raid health: ");
            message.append(raid.getHealthOfLivingNetherInvaders());
            message.append(" / ");
            message.append(raid.getTotalNetherInvadersAlive());
            source.sendSuccess(new StringTextComponent(message.toString()), false);
            return 1;
        } else {
            source.sendFailure(new StringTextComponent("Found no started raids"));
            return 0;
        }
    }

    @Nullable
    private static NetherInvasion getRaid(ServerPlayerEntity player) {
        ServerWorld serverWorld = (ServerWorld) player.getCommandSenderWorld();
        NetherInvasionManager raidManager = serverWorld.getInvasions();
        return raidManager.getInvasionAt(new BlockPos(player.getX(), player.getY(), player.getZ()));
    }

    private static ServerPlayerEntity getPlayer(CommandSource source) throws CommandSyntaxException {
        return source.getPlayerOrException();
    }
}
