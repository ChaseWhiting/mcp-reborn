package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public interface IHasRadiation {

   int getRadDistance(IBlockReader reader, BlockPos pos, BlockState state);

   int getTicksPerRad();

   void tickRadiation(World world, IBlockReader reader, BlockPos pos, BlockState state);
}