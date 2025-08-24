package net.minecraft.entity.ai.goal;

import net.minecraft.entity.*;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.EnumSet;
import java.util.List;

import java.util.function.Predicate;

public class CallForHelpIfAttackedByGoal extends TargetGoal {
   private final Predicate<LivingEntity> attackPredicate;
   private int lastAttackTimestamp;
   private final float searchDistance;

   public CallForHelpIfAttackedByGoal(Creature mob, Predicate<LivingEntity> attackPredicate, float searchDistance) {
      super(mob, true);
      this.attackPredicate = attackPredicate;
      this.setFlags(EnumSet.of(Flag.TARGET));
      this.searchDistance = searchDistance;
   }

   @Override
   public boolean canUse() {
      LivingEntity attacker = this.mob.getLastHurtByMob();
      int timestamp = this.mob.getLastHurtByMobTimestamp();
      if (attacker != null && timestamp != this.lastAttackTimestamp && this.attackPredicate.test(attacker)) {
         return true;
      }
      return false;
   }

   @Override
   public void start() {
      LivingEntity attacker = this.mob.getLastHurtByMob();
      this.mob.setTarget(attacker);
      this.lastAttackTimestamp = this.mob.getLastHurtByMobTimestamp();
      this.alertAllies(attacker);
      super.start();
   }

   private void alertAllies(LivingEntity attacker) {
      double range = this.getFollowDistance();
      AxisAlignedBB box = AxisAlignedBB.unitCubeFromLowerCorner(this.mob.position()).inflate(range, 10.0D, range);
      List<Mob> nearbyMobs = this.mob.level.getLoadedEntitiesOfClass(this.mob.getClass(), box);

      for (Mob ally : nearbyMobs) {
         if (ally != this.mob && ally.getTarget() == null && !ally.isAlliedTo(attacker)) {
            ally.setTarget(attacker);
         }
      }
   }

   @Override
   protected double getFollowDistance() {
      return this.searchDistance;
   }
}
