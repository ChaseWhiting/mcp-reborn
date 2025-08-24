package net.minecraft.entity.monster;

import net.minecraft.entity.Creature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SkeletonEntity extends AbstractSkeletonEntity {
   public SkeletonEntity(EntityType<? extends SkeletonEntity> p_i50194_1_, World p_i50194_2_) {
      super(p_i50194_1_, p_i50194_2_);
      //this.jumpControl = new SkeletonJumpController();
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SKELETON_DEATH;
   }

   public SoundEvent getStepSound() {
      return SoundEvents.SKELETON_STEP;
   }

   public boolean convertsInWater() {
      return true;
   }

//   public void strafe(float a, float b) {
//      if (this.isPassenger() && this.getRootVehicle() instanceof SpiderEntity creature && veryHardmode()) {
//         creature.strafe(a, b);
//      } else {
//         super.strafe(a, b);
//      }
//   }
//
//   class SkeletonJumpController extends JumpController {
//
//      public SkeletonJumpController() {
//         super(SkeletonEntity.this);
//      }
//
//      @Override
//      public void jump() {
//         if (SkeletonEntity.this.veryHardmode() && SkeletonEntity.this.isPassenger() && SkeletonEntity.this.getRootVehicle() instanceof SpiderEntity spider) {
//            spider.getJumpControl().jump();
//         } else {
//            super.jump();
//         }
//      }
//   }

   protected void dropCustomDeathLoot(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
      super.dropCustomDeathLoot(p_213333_1_, p_213333_2_, p_213333_3_);
      Entity entity = p_213333_1_.getEntity();
      if (entity instanceof CreeperEntity) {
         CreeperEntity creeperentity = (CreeperEntity)entity;
         if (creeperentity.canDropMobsSkull()) {
            creeperentity.increaseDroppedSkulls();
            this.spawnAtLocation(Items.SKELETON_SKULL);
         }
      }

   }

   @Override
   public void onCrossbowAttackPerformed() {
      this.noActionTime = 0;
   }
}