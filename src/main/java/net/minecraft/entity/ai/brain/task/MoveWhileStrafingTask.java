package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class MoveWhileStrafingTask extends Task<MobEntity> {
   private final float speedModifier;

   public MoveWhileStrafingTask(float speedModifier) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED,
              MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED,
              MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT,
              MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.REGISTERED));
      this.speedModifier = speedModifier;
   }

   protected void start(ServerWorld world, MobEntity entity, long gameTime) {
      if (entity.getBrain().getMemory(MemoryModuleType.WALK_TARGET).isPresent()) {
         return; // Skip if strafing
      }

      LivingEntity attackTarget = entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
      if (BrainUtil.canSee(entity, attackTarget) && BrainUtil.isWithinAttackRange(entity, attackTarget, 1)) {
         this.clearWalkTarget(entity);
      } else {
         this.setWalkAndLookTarget(entity, attackTarget);
      }
   }

   private void setWalkAndLookTarget(LivingEntity entity, LivingEntity target) {
      Brain<?> brain = entity.getBrain();
      brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(target, true));
      WalkTarget walkTarget = new WalkTarget(new EntityPosWrapper(target, false), this.speedModifier, 0);
      brain.setMemory(MemoryModuleType.WALK_TARGET, walkTarget);
   }

   private void clearWalkTarget(LivingEntity entity) {
      entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
   }
}
