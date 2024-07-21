package net.minecraft.groovy;

import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class MinecraftHelper {
    private final MinecraftServer server;

    public MinecraftHelper(MinecraftServer server) {
        this.server = server;
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

    public void command(CommandSource source, String command) {
        server.getCommands().performCommand(source, command);
    }

    public void command(String command) {
        server.getCommands().performCommand(server.createCommandSourceStack(), command);
    }
}
