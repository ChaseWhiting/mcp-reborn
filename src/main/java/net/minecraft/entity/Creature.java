package net.minecraft.entity;

import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class Creature extends Mob {
   protected Creature(EntityType<? extends Creature> entity, World world) {
      super(entity, world);
   }
   private AvoidEntityGoal creeperAvoidGoal = new AvoidEntityGoal<>(this, CreeperEntity.class, (creeper) -> {
      return ((CreeperEntity) creeper).getSwell() > 0;
   }, 7.6F, this instanceof IronGolemEntity ? 0.85D : 1.34D, this instanceof IronGolemEntity ? 0.85D : 1.34D, (creeper) -> !(this instanceof CreeperEntity) && this.veryHardmode());
   public WaterSaveGoal waterSaveGoal = new WaterSaveGoal(this);

   public float getWalkTargetValue(BlockPos p_180484_1_) {
      return this.getWalkTargetValue(p_180484_1_, this.level);
   }

   public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      return 0.0F;
   }

   public boolean checkSpawnRules(IWorld p_213380_1_, SpawnReason p_213380_2_) {
      return this.getWalkTargetValue(this.blockPosition(), p_213380_1_) >= 0.0F;
   }

   public boolean isPathFinding() {
      return !this.getNavigation().isDone();
   }

   public void tick() {
      super.tick();


      if (!this.goalSelector.getAvailableGoals().anyMatch((goal) -> goal.getGoal() == creeperAvoidGoal) && creeperAvoidGoal != null && !(this instanceof VillagerEntity)) {
         this.goalSelector.addGoal(0, creeperAvoidGoal);
          if (this.canHaveLadderGoal()) {
              this.goalSelector.addGoal(6, new LadderClimbGoal(this));
          }
          this.goalSelector.addGoal(0, waterSaveGoal);
      }
   }

   public int getMaxFallDistance() {
      if (this.getGoalSelector().getAvailableGoals().anyMatch(goal -> goal.getGoal() == waterSaveGoal)) {
         return (int) ( 3 + (this.getHealth() - 1));
      }
      return super.getMaxFallDistance();
   }

   public boolean canHaveLadderGoal() {
      return true;
   }



   protected boolean shouldStayCloseToLeashHolder() {
      return true;
   }


   protected void onLeashDistance(float p_142017_1_) {
   }



   @Override
   public void closeRangeLeashBehaviour(Entity entity) {
      super.closeRangeLeashBehaviour(entity);
      if (this.shouldStayCloseToLeashHolder() && !this.isPanicking()) {
         this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
         float f = 2.0f;
         float f2 = this.distanceTo(entity);
         Vector3d vec3 = new Vector3d(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ()).normalize().scale(Math.max(f2 - 2.0f, 0.0f));
         this.getNavigation().moveTo(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z, this.followLeashSpeed());
      }
   }

   @Override
   public void whenLeashedTo(Entity entity) {
      this.setHomeTo(entity.blockPosition(), (int)this.leashElasticDistance() - 1);
      super.whenLeashedTo(entity);
   }

   public boolean isPanicking() {
      if (this.brain.hasMemoryValue(MemoryModuleType.IS_PANICKING)) {
         return this.brain.getMemory(MemoryModuleType.IS_PANICKING).isPresent();
      }
      for (PrioritizedGoal wrappedGoal : this.goalSelector.getAvailableGoals().toList()) {
         if (!wrappedGoal.isRunning() || !(wrappedGoal.getGoal() instanceof PanicGoal)) continue;
         return true;
      }
      return false;
   }



   protected double followLeashSpeed() {
      return 1.0;
   }
}