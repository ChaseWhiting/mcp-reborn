package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public interface ITickList<T> {
   boolean hasScheduledTick(BlockPos p_205359_1_, T p_205359_2_);

   default void scheduleTick(BlockPos p_205360_1_, T block, int timeTillTick) {
      this.scheduleTick(p_205360_1_, block, timeTillTick, TickPriority.NORMAL);
   }

   void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_);

   boolean willTickThisTick(BlockPos p_205361_1_, T p_205361_2_);
}