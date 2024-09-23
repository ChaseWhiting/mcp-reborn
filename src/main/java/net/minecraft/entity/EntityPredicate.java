package net.minecraft.entity;

import net.minecraft.potion.Effects;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityPredicate {
   public static final EntityPredicate DEFAULT = new EntityPredicate();
   private double range = -1.0D;
   private boolean allowInvulnerable;
   private boolean allowSameTeam;
   private boolean allowUnseeable;
   private boolean allowNonAttackable;
   private boolean testInvisible = true;
   private Predicate<LivingEntity> selector;

   public EntityPredicate range(double p_221013_1_) {
      this.range = p_221013_1_;
      return this;
   }

   public EntityPredicate allowInvulnerable() {
      this.allowInvulnerable = true;
      return this;
   }

   public EntityPredicate allowSameTeam() {
      this.allowSameTeam = true;
      return this;
   }

   public EntityPredicate allowUnseeable() {
      this.allowUnseeable = true;
      return this;
   }

   public EntityPredicate allowNonAttackable() {
      this.allowNonAttackable = true;
      return this;
   }

   public EntityPredicate ignoreInvisibilityTesting() {
      this.testInvisible = false;
      return this;
   }

   public EntityPredicate selector(@Nullable Predicate<LivingEntity> p_221012_1_) {
      this.selector = p_221012_1_;
      return this;
   }

   public boolean test(@Nullable LivingEntity attacker, LivingEntity target) {
      // If attacker and target are the same entity, return false
      if (attacker == target) {
         return false;
      }

      // If target is a spectator, return false
      if (target.isSpectator()) {
         return false;
      }

      // If target is dead, return false
      if (!target.isAlive()) {
         return false;
      }

      // If invulnerable entities aren't allowed and the target is invulnerable, return false
      if (!this.allowInvulnerable && target.isInvulnerable()) {
         return false;
      }

      // If there's a specific selector, and the target doesn't meet its conditions, return false
      if (this.selector != null && !this.selector.test(target)) {
         return false;
      }

      if (attacker != null) {
         // If non-attackable entities aren't allowed and attacker can't attack the target, return false
         if (!this.allowNonAttackable) {
            if (!attacker.canAttack(target)) {
               return false;
            }

            // If attacker can't attack this type of entity, return false
            if (!attacker.canAttackType(target.getType())) {
               return false;
            }
         }

         // If entities on the same team aren't allowed to attack each other, return false
         if (!this.allowSameTeam && attacker.isAlliedTo(target)) {
            return false;
         }

         // If the attack range is greater than 0, check if the target is within range
         if (this.range > 0.0D) {
            // Calculate the visibility multiplier for the target (if the attacker needs to see it)
            double visibilityMultiplier = this.testInvisible ? target.getVisibilityPercent(attacker) : 1.0D;

            // Check if the attacker has the blindness effect
//            if (attacker.hasEffect(Effects.BLINDNESS)) {
//               // Reduce the attack range if the mob is blind
//               visibilityMultiplier *= 0.2D; // Adjust this value based on how much you want to reduce the range
//            }

            // Calculate the maximum attack range, at least 2.0 units
            double maxRange = Math.max(this.range * visibilityMultiplier, 2.0D);

            // Calculate the distance between attacker and target
            double distanceSquared = attacker.distanceToSqr(target.getX(), target.getY(), target.getZ());

            // If the target is outside the attack range, return false
            if (distanceSquared > maxRange * maxRange) {
               return false;
            }
         }

         // If unseeable entities aren't allowed and the attacker is a mob that can't see the target, return false
         if (!this.allowUnseeable && attacker instanceof Mob && !((Mob)attacker).getSensing().canSee(target)) {
            return false;
         }
      }

      // If none of the conditions returned false, return true
      return true;
   }
}