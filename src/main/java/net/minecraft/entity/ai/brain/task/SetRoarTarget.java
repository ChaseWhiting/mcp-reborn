package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.warden.WardenEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.function.Function;

public class SetRoarTarget extends Task<WardenEntity> {
   private final Function<WardenEntity, Optional<? extends LivingEntity>> function;

   public SetRoarTarget(Function<WardenEntity, Optional<? extends LivingEntity>> function) {
      super(ImmutableMap.of(MemoryModuleType.ROAR_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleStatus.REGISTERED));
      this.function = function;
   }

   protected void start(ServerWorld p_212831_1_, WardenEntity p_212831_2_, long p_212831_3_) {
      Optional<? extends LivingEntity> optional = function.apply(p_212831_2_);
      if (optional.filter(p_212831_2_::canTargetEntity).isEmpty()) {
         return;
      }
      p_212831_2_.getBrain().setMemory(MemoryModuleType.ROAR_TARGET, optional.get());
      p_212831_2_.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
   }
}