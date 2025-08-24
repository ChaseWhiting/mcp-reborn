package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.RangedInteger;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Function;

public class AxolotlFollowAdultTask<E extends AgeableEntity> extends Task<E> {
   private final RangedInteger followRange;
   private final Function<LivingEntity, Float> speedModifier;

   public AxolotlFollowAdultTask(RangedInteger p_i231508_1_, Function<LivingEntity, Float> p_i231508_2_) {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.followRange = p_i231508_1_;
      this.speedModifier = p_i231508_2_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, E p_212832_2_) {
      if (!p_212832_2_.isBaby()) {
         return false;
      } else {
         AgeableEntity ageableentity = this.getNearestAdult(p_212832_2_);
         return p_212832_2_.closerThan(ageableentity, (double)(this.followRange.getMaxInclusive() + 1)) && !p_212832_2_.closerThan(ageableentity, (double)this.followRange.getMinInclusive());
      }
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      BrainUtil.setWalkAndLookTargetMemories(p_212831_2_, this.getNearestAdult(p_212831_2_), this.speedModifier.apply(p_212831_2_), this.followRange.getMinInclusive() - 1);
   }

   private AgeableEntity getNearestAdult(E p_233852_1_) {
      return p_233852_1_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
   }
}