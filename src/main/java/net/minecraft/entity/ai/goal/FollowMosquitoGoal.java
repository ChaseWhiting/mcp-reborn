package net.minecraft.entity.ai.goal;

import net.minecraft.entity.monster.crimson_mosquito.CrimsonMosquitoEntity;

import java.util.List;

public class FollowMosquitoGoal extends Goal {
   private final CrimsonMosquitoEntity animal;
   private CrimsonMosquitoEntity parent;
   private final double speedModifier;
   private int timeToRecalcPath;

   public FollowMosquitoGoal(CrimsonMosquitoEntity p_i1626_1_, double p_i1626_2_) {
      this.animal = p_i1626_1_;
      this.speedModifier = p_i1626_2_;
   }

   public boolean canUse() {
      if (this.animal.getAge() >= 0) {
         return false;
      } else {
         List<CrimsonMosquitoEntity> list = this.animal.level.getEntitiesOfClass(CrimsonMosquitoEntity.class, this.animal.getBoundingBox().inflate(16.0D, 16.0D, 16.0D));
         CrimsonMosquitoEntity animalentity = null;
         double d0 = Double.MAX_VALUE;

         for(CrimsonMosquitoEntity animalentity1 : list) {
            if (animalentity1.getAge() >= 0) {
               double d1 = this.animal.distanceToSqr(animalentity1);
               if (!(d1 > d0)) {
                  d0 = d1;
                  animalentity = animalentity1;
               }
            }
         }

         if (animalentity == null) {
            return false;
         } else if (d0 < 9.0D) {
            return false;
         } else {
            this.parent = animalentity;
            return true;
         }
      }
   }

   public boolean canContinueToUse() {
      if (this.animal.getAge() >= 0) {
         return false;
      } else if (!this.parent.isAlive()) {
         return false;
      } else {
         double d0 = this.animal.distanceToSqr(this.parent);
         return !(d0 < 9.0D) && !(d0 > 256.0D);
      }
   }

   public void start() {
      this.timeToRecalcPath = 0;
   }

   public void stop() {
      this.parent = null;
   }

   public void tick() {
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = 10;
         this.animal.getMoveControl().setWantedPosition(this.parent.blockPosition().above(), this.speedModifier);
      }
   }
}