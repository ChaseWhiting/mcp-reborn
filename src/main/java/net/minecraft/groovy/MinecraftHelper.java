package net.minecraft.groovy;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class MinecraftHelper {
    private final MinecraftServer server;
    private final PlayerEntity executor;

    public PlayerEntity executor() {
        return executor;
    }

    public MinecraftHelper(MinecraftServer server, PlayerEntity executor) {
        this.server = server;
        this.executor = executor;
    }

    public World getWorld() {
        return server.getLevel(World.OVERWORLD);
    }

    public void spawnEntity(Entity entity) {
        World world = getWorld();
        world.addFreshEntity(entity);
    }

    public Entity createEntity(EntityType<?> type, BlockPos pos) {
        World world = getWorld();
        Entity entity = type.create(world);
        if (entity != null) {
            entity.setPos(pos.getX(), pos.getY(), pos.getZ());
        }
        return entity;
    }

    public ServerPlayerEntity getPlayer(String name) {
        return server.getPlayerList().getPlayerByName(name);
    }

    public ServerPlayerEntity getPlayer() {
        return server.getPlayerList().getRandomPlayer();
    }

    public void command(CommandSource source, String command) {
        server.getCommands().performCommand(source, command);
    }

    public void command(String command) {
        server.getCommands().performCommand(server.createCommandSourceStack(), command);
    }

    public void schedule(Runnable runnable, int ticksDelay) {
        ServerWorld serverWorld = (ServerWorld) this.getWorld();
        serverWorld.getServer().tell(new TickDelayedTask(serverWorld.getServer().getTickCount() + ticksDelay, runnable));
    }

    public void schedule(int ticksDelay, Runnable runnable) {
        ServerWorld serverWorld = (ServerWorld) this.getWorld();
        serverWorld.getServer().tell(new TickDelayedTask(serverWorld.getServer().getTickCount() + ticksDelay, runnable));
    }

    public UUID getUserUUID(String user) throws Exception {
        return MinecraftUUIDFetcher.getUUIDFromUsername(user).getUUID();
    }

    public UUIDData getUUIDDataFromPlayer(String user) throws Exception {
        return MinecraftUUIDFetcher.getUUIDFromUsername(user);
    }

    public PlayerEntity getPlayerFromUUID(UUID uuid) {
        return getWorld().getPlayerByUUID(uuid);
    }



    public void sendMessageToPlayer(PlayerEntity player, String message) {
        player.sendMessage(new StringTextComponent(message), player.getUUID());
    }

    public void teleportPlayer(PlayerEntity player, BlockPos pos) {
        player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
    }

    public void healPlayer(PlayerEntity player, float amount) {
        player.heal(amount);
    }

    public void giveItemToPlayer(PlayerEntity player, ItemStack itemStack) {
        if (!player.inventory.add(itemStack)) {
            player.drop(itemStack, false);
        }
    }

    public void broadcastMessage(String message) {
        server.getPlayerList().broadcastMessage(new StringTextComponent(message), ChatType.SYSTEM, executor.getUUID());
    }

    public boolean playerHasItem(PlayerEntity player, Item item) {
        return player.inventory.contains(new ItemStack(item));
    }

    public BlockPos getPlayerPosition(PlayerEntity player) {
        return player.blockPosition();
    }

    public long getWorldTime() {
        return getWorld().getDayTime();
    }
}
