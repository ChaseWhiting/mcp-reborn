package net.minecraft.block;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public interface IGrowable {
   boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_);

   boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_);

   void performBonemeal(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_);



   public static boolean hasSpreadableNeighbourPos(IWorldReader levelReader, BlockPos blockPos, BlockState blockState) {
      return getSpreadableNeighbourPos(Direction.Plane.HORIZONTAL.stream().collect(Collectors.toList()), levelReader, blockPos, blockState).isPresent();
   }

   public static Optional<BlockPos> findSpreadableNeighbourPos(World level, BlockPos blockPos, BlockState blockState) {
      return getSpreadableNeighbourPos(Direction.Plane.HORIZONTAL.shuffledCopy(level.random), level, blockPos, blockState);
   }

   private static Optional<BlockPos> getSpreadableNeighbourPos(List<Direction> list, IWorldReader levelReader, BlockPos blockPos, BlockState blockState) {
      for (Direction direction : list) {
         BlockPos blockPos2 = blockPos.relative(direction);
         if (!levelReader.isEmptyBlock(blockPos2) || !blockState.canSurvive(levelReader, blockPos2)) continue;
         return Optional.of(blockPos2);
      }
      return Optional.empty();
   }
}