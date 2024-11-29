package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.monster.PillagerCaptainEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.*;
import net.minecraft.potion.Effects;
import net.minecraft.util.RangedInteger;

public class RangedCrossbowAttackGoal<T extends Monster & IRangedAttackMob & ICrossbowUser> extends Goal {
   public static final RangedInteger PATHFINDING_DELAY_RANGE = new RangedInteger(20, 40);
   private final T mob;
   private RangedCrossbowAttackGoal.CrossbowState crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
   private final double speedModifier;
   private final float attackRadiusSqr;
   private int seeTime;
   private int attackDelay;
   private int updatePathDelay;

   public RangedCrossbowAttackGoal(T p_i50322_1_, double p_i50322_2_, float p_i50322_4_) {
      this.mob = p_i50322_1_;
      this.speedModifier = p_i50322_2_;
      this.attackRadiusSqr = p_i50322_4_ * p_i50322_4_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      if (mob.hasEffect(Effects.CONFUSED)) return false;

      return this.isValidTarget() && this.isHoldingCrossbow();
   }

   private boolean isHoldingCrossbow() {
      return this.mob.isHolding(Items.CROSSBOW) || this.mob.isHolding(Items.GILDED_CROSSBOW) || AbstractCrossbowItem.isHoldingAbstractCrossbowItem(this.mob);
   }

   public boolean canContinueToUse() {
      if (mob.hasEffect(Effects.CONFUSED)) return false;

      return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingCrossbow() || !this.mob.getNavigation().isDone() && AbstractCrossbowItem.isHoldingAbstractCrossbowItem(this.mob);
   }

   private boolean isValidTarget() {
      return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
   }

   public void stop() {
      super.stop();
      this.mob.setAggressive(false);
      this.mob.setTarget((LivingEntity)null);
      this.seeTime = 0;
      if (this.mob.isUsingItem()) {
         this.mob.stopUsingItem();
         this.mob.setChargingCrossbow(false);
         if (this.mob.getUseItem().getItem() == Items.GILDED_CROSSBOW) {
            GildedCrossbowItem.setCharged(this.mob.getUseItem(), false);
         } else if (this.mob.getUseItem().get() == Items.CROSSBOW ){
            CrossbowItem.setCharged(this.mob.getUseItem(), false);
         } else if (this.mob.getUseItem().get() instanceof AbstractCrossbowItem) {
            AbstractCrossbowItem.setCharged(this.mob.getUseItem(), false);
         }
      }
   }

   public void tick() {
      LivingEntity livingentity = this.mob.getTarget();
      if (livingentity != null) {
         boolean canSeeTarget = this.mob.getSensing().canSee(livingentity);
         boolean flag1 = this.seeTime > 0;
         if (canSeeTarget != flag1) {
            this.seeTime = 0;
         }

         if (canSeeTarget) {
            ++this.seeTime;
         } else {
            --this.seeTime;
         }

         double d0 = this.mob.distanceToSqr(livingentity);
         boolean flag2 = (d0 > (double)this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
         if (flag2) {
            --this.updatePathDelay;
            if (this.updatePathDelay <= 0) {
               this.mob.getNavigation().moveTo(livingentity, this.canRun() ? this.speedModifier : this.speedModifier * 0.5D);
               this.updatePathDelay = PATHFINDING_DELAY_RANGE.randomValue(this.mob.getRandom());
            }
         } else {
            this.updatePathDelay = 0;
            this.mob.getNavigation().stop();
         }

         this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
         switch (this.crossbowState) {
            case UNCHARGED:
               if (!flag2) {
                  startCharging();
               }
               break;
            case CHARGING:
               if (!this.mob.isUsingItem()) {
                  this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
               } else {
                  handleCharging();
               }
               break;
            case CHARGED:
               --this.attackDelay;
               if (this.attackDelay == 0) {
                  this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK;
               }
               break;
            case READY_TO_ATTACK:
               if (canSeeTarget) {
                  performAttack(livingentity);
               }
               break;
         }
      }
   }

   private void startCharging() {
      if (this.mob.isHolding(Items.GILDED_CROSSBOW)) {
         this.mob.startUsingItem(ProjectileHelper.getWeaponHoldingHand(this.mob, Items.GILDED_CROSSBOW));
      } else if (this.mob.isHolding(Items.CROSSBOW)){
         this.mob.startUsingItem(ProjectileHelper.getWeaponHoldingHand(this.mob, Items.CROSSBOW));
      } else if (AbstractCrossbowItem.isHoldingAbstractCrossbowItem(this.mob)) {
         this.mob.startUsingItem(AbstractCrossbowItem.getHandHoldingAbstractCrossbowItem(this.mob));
      }
      this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.CHARGING;
      this.mob.setChargingCrossbow(true);
   }

   private void handleCharging() {
      int i = this.mob.getTicksUsingItem();
      ItemStack itemstack = this.mob.getUseItem();
      if (itemstack.getItem() == Items.CROSSBOW) {
         if (i >= CrossbowItem.getChargeDuration(itemstack)) {
            finishCharging();
         }
      } else if (itemstack.getItem() == Items.GILDED_CROSSBOW) {
         if (i >= GildedCrossbowItem.getChargeDuration(itemstack)) {
            finishCharging();
         }
      } else if (itemstack.getItem() instanceof AbstractCrossbowItem) {
         if (i >= AbstractCrossbowItem.getChargeDuration(itemstack)) {
            finishCharging();
         }
      }
   }

   private void finishCharging() {
      this.mob.releaseUsingItem();
      this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.CHARGED;
      if (this.mob instanceof PillagerCaptainEntity) {
         this.attackDelay = 10 + this.mob.getRandom().nextInt(10);
      } else if (this.mob instanceof AbstractSkeletonEntity){
         this.attackDelay = 8 + this.mob.getRandom().nextInt(12);
      } else {
         this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
      }
      this.mob.setChargingCrossbow(false);
   }

   private void performAttack(LivingEntity target) {
      this.mob.performRangedAttack(target, 1.0F);
      if (this.mob.isHolding(Items.GILDED_CROSSBOW)) {
         ItemStack itemstack1 = this.mob.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this.mob, Items.GILDED_CROSSBOW));
         GildedCrossbowItem.setCharged(itemstack1, false);
      } else if (this.mob.isHolding(Items.CROSSBOW)) {
         ItemStack itemstack1 = this.mob.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this.mob, Items.CROSSBOW));
         CrossbowItem.setCharged(itemstack1, false);
      } else if (AbstractCrossbowItem.isHoldingAbstractCrossbowItem(this.mob)) {
         ItemStack itemstack1 = this.mob.getItemInHand(AbstractCrossbowItem.getHandHoldingAbstractCrossbowItem(this.mob));
         AbstractCrossbowItem.setCharged(itemstack1, false);
      }
      this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
   }

   private boolean canRun() {
      return this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
   }

   static enum CrossbowState {
      UNCHARGED,
      CHARGING,
      CHARGED,
      READY_TO_ATTACK;
   }
}
