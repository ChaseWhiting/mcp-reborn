package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.*;
import net.minecraft.entity.monster.bogged.BoggedEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.GameRules;

public class HurtByTargetGoal extends TargetGoal {
   private static final EntityPredicate HURT_BY_TARGETING = (new EntityPredicate()).allowUnseeable().ignoreInvisibilityTesting();
   private boolean alertSameType;
   private int timestamp;
   private final Class<?>[] toIgnoreDamage;
   private Class<?>[] toIgnoreAlert;

   public HurtByTargetGoal(Creature p_i50317_1_, Class<?>... p_i50317_2_) {
      super(p_i50317_1_, true);
      this.toIgnoreDamage = p_i50317_2_;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean canUse() {
      int i = this.mob.getLastHurtByMobTimestamp();
      LivingEntity livingentity = this.mob.getLastHurtByMob();

      if (i != this.timestamp && livingentity != null) {
         // Check if the attacker is a trusted player and allow retaliation
         if (this.mob instanceof BoggedEntity && ((BoggedEntity) this.mob).isAlliedTo(livingentity) && EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
            return true;  // Allow attack even if it's a trusted player
         }

         if (this.mob instanceof BoggedEntity && livingentity instanceof BoggedEntity) {
            BoggedEntity mobBogged = (BoggedEntity) this.mob;
            BoggedEntity livingBogged = (BoggedEntity) livingentity;

            // Get the trusted players' UUIDs of both entities
            List<UUID> mobTrustedPlayers = mobBogged.getTrustedPlayer();
            List<UUID> livingTrustedPlayers = livingBogged.getTrustedPlayer();

            // Check if both entities have trusted players
            if (!mobTrustedPlayers.isEmpty() && !livingTrustedPlayers.isEmpty()) {
               // Check if any UUIDs are common between both trusted player lists
               for (UUID mobTrustedUUID : mobTrustedPlayers) {
                  if (livingTrustedPlayers.contains(mobTrustedUUID)) {
                     return false;  // Don't attack if there is a common trusted player
                  }
               }
               return true;  // Attack if no common trusted player is found
            }

            return true;  // Allow attacking if either has no trusted player
         }


         // Prevent retaliation if the mob is a player and universal anger is enabled
         if (livingentity.getType() == EntityType.PLAYER && this.mob.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
            return false;
         } else {
            // Check if the attacker is from a class we should ignore
            for (Class<?> oclass : this.toIgnoreDamage) {
               if (oclass.isAssignableFrom(livingentity.getClass())) {
                  return false;
               }
            }



            // veryHardmode check: prevent monsters from attacking each other, but allow retaliation from BoggedEntity
            if (this.mob.veryHardmode()) {
               // Prevent monsters attacking each other unless attacked by a BoggedEntity
               if (this.mob instanceof Monster && livingentity instanceof Monster) {
                  // Allow the attack if the attacker is a BoggedEntity (retaliation)
                  if (livingentity instanceof BoggedEntity) {
                     return this.canAttack(livingentity, HURT_BY_TARGETING); // BoggedEntity can be attacked by other monsters
                  }
                  return false;  // No attack if both are monsters
               }
            }

            // Continue with regular attack logic
            return this.canAttack(livingentity, HURT_BY_TARGETING);
         }
      } else {
         return false;
      }
   }


   public HurtByTargetGoal setAlertOthers(Class<?>... p_220794_1_) {
      this.alertSameType = true;
      this.toIgnoreAlert = p_220794_1_;
      return this;
   }

   public void start() {
      this.mob.setTarget(this.mob.getLastHurtByMob());
      this.targetMob = this.mob.getTarget();
      this.timestamp = this.mob.getLastHurtByMobTimestamp();
      this.unseenMemoryTicks = 300;
      if (this.alertSameType) {
         this.alertOthers();
      }

      super.start();
   }

   protected void alertOthers() {
      double d0 = this.getFollowDistance();
      AxisAlignedBB axisalignedbb = AxisAlignedBB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0D, d0);
      List<Mob> list = this.mob.level.getLoadedEntitiesOfClass(this.mob.getClass(), axisalignedbb);
      Iterator iterator = list.iterator();

      while(true) {
         Mob mobentity;
         while(true) {
            if (!iterator.hasNext()) {
               return;
            }

            mobentity = (Mob)iterator.next();
            if (this.mob != mobentity && mobentity.getTarget() == null && (!(this.mob instanceof TameableEntity) || ((TameableEntity)this.mob).getOwner() == ((TameableEntity)mobentity).getOwner()) && !mobentity.isAlliedTo(this.mob.getLastHurtByMob())) {
               if (this.toIgnoreAlert == null) {
                  break;
               }

               boolean flag = false;

               for(Class<?> oclass : this.toIgnoreAlert) {
                  if (mobentity.getClass() == oclass) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  break;
               }
            }
         }

         this.alertOther(mobentity, this.mob.getLastHurtByMob());
      }
   }

   protected void alertOther(Mob p_220793_1_, LivingEntity p_220793_2_) {
      p_220793_1_.setTarget(p_220793_2_);
   }
}