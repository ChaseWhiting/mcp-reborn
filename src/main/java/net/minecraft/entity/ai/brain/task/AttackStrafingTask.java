package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class AttackStrafingTask<E extends MobEntity> extends Task<E> {
   private final int tooCloseDistance;
   private final int shootingRangeDistance;
   private final float strafeSpeed;
   private final int mobCloseDistance;

   public AttackStrafingTask(int tooCloseDistance, int shootingRangeDistance, float strafeSpeed, int mobCloseDistance) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED,
              MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED,
              MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT,
              MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.VALUE_PRESENT));
      this.tooCloseDistance = tooCloseDistance;
      this.shootingRangeDistance = shootingRangeDistance;
      this.strafeSpeed = strafeSpeed;
      this.mobCloseDistance = mobCloseDistance;
   }

   protected boolean checkExtraStartConditions(ServerWorld world, E entity) {
      return /*this.isTargetVisible(entity) && this.isTargetTooClose(entity) ||*/ this.isTargetVisible(entity) && this.getTarget(entity).closerThan(entity,shootingRangeDistance);
   }

   protected boolean canStillUse(ServerWorld world, E entity, long gameTime) {
      return entity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.getTarget(entity).closerThan(entity, shootingRangeDistance);
   }

   protected void start(ServerWorld world, E entity, long gameTime) {
      if (isTargetTooClose(entity)) {
         float strafeDirection = this.determineStrafeDirection(entity);
         entity.getMoveControl().strafe(-this.strafeSpeed, strafeDirection); // Strafe backward and to the side
      }
      entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(this.getTarget(entity), true));
      entity.yRot = MathHelper.rotateIfNecessary(entity.yRot, entity.yHeadRot, 0.0F);

      // Set strafing walk target to maintain strafing memory
      WalkTarget walkTarget = new WalkTarget(new EntityPosWrapper(this.getTarget(entity), false), this.strafeSpeed, 0);
      entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walkTarget);
   }

   protected void stop(ServerWorld world, E entity, long gameTime) {
      entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
   }

   private boolean isTargetVisible(E entity) {
      return entity.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get().contains(this.getTarget(entity));
   }

   private boolean isTargetTooClose(E entity) {
      LivingEntity target = this.getTarget(entity);
      double distance = target.distanceToSqr(entity);
      return distance < (double) this.tooCloseDistance * this.tooCloseDistance;
   }

   private float determineStrafeDirection(E entity) {
      List<MobEntity> nearbyMobs = entity.level.getEntitiesOfClass(MobEntity.class, entity.getBoundingBox().inflate(this.mobCloseDistance));
      for (MobEntity mob : nearbyMobs) {
         if (mob != entity && mob instanceof AbstractPiglinEntity) {
            // Determine strafe direction to avoid the nearby mob
            // If the nearby mob is on the left side, strafe to the right (+0.4F), and vice versa
            double deltaX = mob.getX() - entity.getX();
            return deltaX > 0 ? 0.4F : -0.4F;
         }
      }
      return 0.0F; // No other mob close, strafe straight backward
   }

   private LivingEntity getTarget(E entity) {
      return entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }
}
