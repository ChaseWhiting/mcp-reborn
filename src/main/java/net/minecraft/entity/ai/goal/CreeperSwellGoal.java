package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.CreeperEntity;

public class CreeperSwellGoal extends Goal {
   private final CreeperEntity creeper;
   private LivingEntity target;

   public CreeperSwellGoal(CreeperEntity p_i1655_1_) {
      this.creeper = p_i1655_1_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {

      LivingEntity livingentity = this.creeper.getTarget();
      boolean canSee = livingentity != null && this.creeper.getSensing().canSee(livingentity);
      boolean flag = livingentity != null && this.creeper.distanceToSqr(livingentity) < (canSee ? 26 : 17) && livingentity instanceof VillagerEntity && creeper.veryHardmode();
      return this.creeper.getSwellDir() > 0 || livingentity != null && this.creeper.distanceToSqr(livingentity) < 9.0D || flag;
   }

   public void start() {
      this.creeper.getNavigation().stop();
      this.target = this.creeper.getTarget();
   }

   public void stop() {
      this.target = null;
   }

   public void tick() {
      if (this.target == null) {
         this.creeper.setSwellDir(-1);
      } else {
         double distanceSq = this.creeper.distanceToSqr(this.target);

         if (this.target instanceof VillagerEntity && this.creeper.veryHardmode()) {
            // Villager-specific behavior: swell if within 26 blocks, regardless of line of sight
            if (distanceSq > 26.0D) {
               this.creeper.setSwellDir(-1);
            } else {
               this.creeper.setSwellDir(1);
            }
         } else {
            // Default behavior: swell if within 7 blocks and can see the target
            if (distanceSq > 49.0D || !this.creeper.getSensing().canSee(this.target)) {
               this.creeper.setSwellDir(-1);
            } else {
               this.creeper.setSwellDir(1);
            }
         }
      }
   }
}
