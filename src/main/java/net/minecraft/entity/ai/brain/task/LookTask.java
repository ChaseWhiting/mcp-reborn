package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class LookTask extends Task<Mob> {
   public LookTask(int p_i50358_1_, int p_i50358_2_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_PRESENT), p_i50358_1_, p_i50358_2_);
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, Mob p_212834_2_, long p_212834_3_) {
      return p_212834_2_.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).filter((p_220485_1_) -> {
         return p_220485_1_.isVisibleBy(p_212834_2_);
      }).isPresent();
   }

   protected void stop(ServerWorld p_212835_1_, Mob p_212835_2_, long p_212835_3_) {
      p_212835_2_.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void tick(ServerWorld p_212833_1_, Mob p_212833_2_, long p_212833_3_) {
      p_212833_2_.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).ifPresent((p_220484_1_) -> {
         p_212833_2_.getLookControl().setLookAt(p_220484_1_.currentPosition());
      });
   }
}