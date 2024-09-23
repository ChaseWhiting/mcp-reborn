package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CreateCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("create")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("entity", StringArgumentType.greedyString())
                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                .executes(context -> {
                                    String entityName = StringArgumentType.getString(context, "entity");
                                    if (entityName.equalsIgnoreCase("all")) {
                                        return spawnAllEntities(context);
                                    } else if (entityName.equalsIgnoreCase("monsters")){
                                        return spawnMonsters(context);
                                    } else {
                                        return spawnEntity(context, entityName);
                                    }
                                })
                        )
        );
    }

    private static CompletableFuture<Suggestions> suggestEntityTypes(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        builder.suggest("all");
        builder.suggest("monsters");
        return builder.buildFuture();
    }

    private static int spawnEntity(CommandContext<CommandSource> context, String entityName) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();
        Entity entity = source.getEntity() != null ? source.getEntity() : source.getServer().overworld().getRandomPlayer();
        if (entity == null) {
            source.sendFailure(new StringTextComponent("No player found to use as reference for position."));
            return 0;
        }

        ResourceLocation entityLocation = new ResourceLocation(entityName);
        EntityType<?> entityType = Registry.ENTITY_TYPE.getOptional(entityLocation).orElse(null);
        if (entityType == null) {
            source.sendFailure(new StringTextComponent("Entity type not found: " + entityName));
            return 0;
        }

        spreadEntities(world, List.of(entityType), entity.blockPosition(), 10); // Spread within 10 block radius
        source.sendSuccess(new StringTextComponent("Spawned entity: " + entityName), true);
        return 1;
    }

    private static int spawnAllEntities(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();
        Entity entity = source.getEntity() != null ? source.getEntity() : source.getServer().overworld().getRandomPlayer();
        if (entity == null) {
            source.sendFailure(new StringTextComponent("No player found to use as reference for position."));
            return 0;
        }

        List<EntityType<?>> entityTypes = StreamSupport.stream(Registry.ENTITY_TYPE.spliterator(), false)
                .filter(entityType -> entityType != EntityType.ENDER_DRAGON && entityType != EntityType.WITHER && entityType != EntityType.LIGHTNING_BOLT && entityType != EntityType.TNT)
                .collect(Collectors.toList());

        spreadEntities(world, entityTypes, entity.blockPosition(), 10); // Spread within 10 block radius
        source.sendSuccess(new StringTextComponent("Spawned " + entityTypes.size() + " entities."), true);
        return 1;
    }

    private static int spawnMonsters(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();
        Entity entity = source.getEntity() != null ? source.getEntity() : source.getServer().overworld().getRandomPlayer();
        if (entity == null) {
            source.sendFailure(new StringTextComponent("No player found to use as reference for position."));
            return 0;
        }

        List<EntityType<?>> monsters = Registry.ENTITY_TYPE.stream()
                .filter(EntityType::canSummon)
                .filter(entityType -> entityType.getCategory() == EntityClassification.MONSTER)
                .collect(Collectors.toList());

        for (int i = 0; i < monsters.size(); i++) {
            EntityType<?> entityType = monsters.get(i);
            Vector3d pos = entity.position();
            BlockPos pos1;
            pos1 = entityType.getEntity(world) instanceof EnderDragonEntity ? new BlockPos(pos.add(0, 0, i * -3)) : new BlockPos(pos.add(0,0,i * 4));

            Entity newEntity = entityType.create(world);
            newEntity.setPos(pos1); // Corrected method to set position
            ((LivingEntity)newEntity).setHealth(1F);
            ((Mob)newEntity).setNoAi(true);
            ((Mob)newEntity).setItemSlot(EquipmentSlotType.HEAD, new ItemStack(Items.LEATHER_HELMET, 1));
            ((Mob)newEntity).setInvulnerable(true);
            world.addFreshEntity(newEntity);
        }
        source.sendSuccess(new StringTextComponent("Spawned " + monsters.size() + " monsters."), true);
        return 1;
    }

    private static void spreadEntities(ServerWorld world, List<EntityType<?>> entityTypes, BlockPos centerPos, double radius) {
        Random random = new Random();
        for (EntityType<?> entityType : entityTypes) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = radius * Math.sqrt(random.nextDouble());
            double xOffset = distance * Math.cos(angle);
            double zOffset = distance * Math.sin(angle);

            BlockPos newPos = centerPos.offset(xOffset, 0, zOffset);
            Entity newEntity = entityType.create(world);
            if (newEntity != null) {
                newEntity.setPos(newPos.getX(), newPos.getY(), newPos.getZ());
                world.addFreshEntity(newEntity);
            }
        }
    }
}
