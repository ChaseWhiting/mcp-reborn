package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SameEntitySensor extends Sensor<LivingEntity> {
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_SAME_ENTITY);
   }

   protected void doTick(ServerWorld p_212872_1_, LivingEntity p_212872_2_) {
      if (p_212872_2_.isBaby()) return;
      p_212872_2_.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((p_234118_2_) -> {
         this.setNearestVisibleAdult(p_212872_2_, p_234118_2_);
      });
   }

   private void setNearestVisibleAdult(LivingEntity p_234116_1_, List<LivingEntity> p_234116_2_) {
      Optional<AgeableEntity> optional = p_234116_2_.stream().filter((p_234115_1_) -> {
         return p_234115_1_.getType() == p_234116_1_.getType();
      }).map((p_234117_0_) -> {
         return (AgeableEntity)p_234117_0_;
      }).findFirst();
      p_234116_1_.getBrain().setMemory(MemoryModuleType.NEAREST_SAME_ENTITY, optional);
   }
}