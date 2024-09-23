package net.minecraft.item;

import net.minecraft.entity.Mob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

@FunctionalInterface
public interface MobSpawnHandler {
    void onMobSpawn(Mob mob, ServerWorld world, BlockPos pos, PlayerEntity player);
}