package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public interface IHasRadiation {

   int getRadDistance(IBlockReader reader, BlockPos pos, BlockState state);

   int getTicksPerRad();

   void tickRadiation(World world, IBlockReader reader, BlockPos pos, BlockState state);
}