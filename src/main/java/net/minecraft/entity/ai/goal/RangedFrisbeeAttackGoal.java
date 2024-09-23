package net.minecraft.entity.ai.goal;

import net.minecraft.entity.IFrisbeeUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.util.RangedInteger;
import java.util.EnumSet;

public class RangedFrisbeeAttackGoal<T extends Monster & IFrisbeeUser> extends Goal {
   private static final RangedInteger PATHFINDING_DELAY_RANGE = new RangedInteger(20, 40);
   private final T mob;
   private final double speedModifier;
   private final float attackRadiusSqr;
   private int seeTime;
   private int attackDelay;
   private int updatePathDelay;

   public RangedFrisbeeAttackGoal(T mob, double speedModifier, float attackRadius) {
      this.mob = mob;
      this.speedModifier = speedModifier;
      this.attackRadiusSqr = attackRadius * attackRadius;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      return this.isValidTarget();
   }

   public boolean canContinueToUse() {
      return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone());
   }

   private boolean isValidTarget() {
      return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
   }

   public void stop() {
      super.stop();
      this.mob.setAggressive(false);
      this.mob.setTarget(null);
      this.seeTime = 0;
   }

   public void tick() {
      LivingEntity target = this.mob.getTarget();
      if (target != null) {
         boolean canSeeTarget = this.mob.getSensing().canSee(target);
         boolean hadSeenTarget = this.seeTime > 0;
         if (canSeeTarget != hadSeenTarget) {
            this.seeTime = 0;
         }

         if (canSeeTarget) {
            ++this.seeTime;
         } else {
            --this.seeTime;
         }

         double distanceToTarget = this.mob.distanceToSqr(target);
         boolean shouldMoveCloser = (distanceToTarget > this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;

         if (shouldMoveCloser) {
            --this.updatePathDelay;
            if (this.updatePathDelay <= 0) {
               this.mob.getNavigation().moveTo(target, this.speedModifier);
               this.updatePathDelay = PATHFINDING_DELAY_RANGE.randomValue(this.mob.getRandom());
            }
         } else {
            this.updatePathDelay = 0;
            this.mob.getNavigation().stop();
         }

         this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

         // Perform the frisbee throw
         if (canSeeTarget && this.attackDelay == 0) {
            this.performAttack(target);
         }
      }
   }

   private void performAttack(LivingEntity target) {
      this.mob.performFrisbeeThrow(target, 1.0F);
      this.attackDelay = 20 + this.mob.getRandom().nextInt(20);  // Delay before the next attack
   }
}
