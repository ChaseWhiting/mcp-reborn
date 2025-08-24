package net.minecraft.block;

import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Fallable {
    default public void onBroken(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity){}

    default public void onLand(World world, BlockPos pos, BlockState state, BlockState state2, FallingBlockEntity fallingBlockEntity) {}
}
