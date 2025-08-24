package net.minecraft.entity.passive.fish;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RandomLavaSwimmingGoal;
import net.minecraft.entity.passive.LavaMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.LavaSwimmerPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class AbstractLavaFishEntity extends LavaMobEntity {
   private static final DataParameter<Boolean> FROM_BUCKET = EntityDataManager.defineId(AbstractLavaFishEntity.class, DataSerializers.BOOLEAN);

   public AbstractLavaFishEntity(EntityType<? extends AbstractLavaFishEntity> p_i48855_1_, World p_i48855_2_) {
      super(p_i48855_1_, p_i48855_2_);
      this.moveControl = new AbstractLavaFishEntity.MoveHelperController(this);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.65F;
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.ARMOR, 5D).add(Attributes.ARMOR_TOUGHNESS, 2D).add(Attributes.KNOCKBACK_RESISTANCE, 0.1);
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket();
   }


   public boolean removeWhenFarAway(double p_213397_1_) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   public int getMaxSpawnClusterSize() {
      return 8;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(FROM_BUCKET, false);
   }

   public boolean fromBucket() {
      return this.entityData.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean p_203706_1_) {
      this.entityData.set(FROM_BUCKET, p_203706_1_);
   }

   public void addAdditionalSaveData(CompoundNBT nbt) {
      super.addAdditionalSaveData(nbt);
      nbt.putBoolean("FromBucket", this.fromBucket());
   }

   public void readAdditionalSaveData(CompoundNBT nbt) {
      super.readAdditionalSaveData(nbt);
      this.setFromBucket(nbt.getBoolean("FromBucket"));
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PlayerEntity.class, 8.0F, 1.6D, 1.4D, EntityPredicates.NO_SPECTATORS::test));
      this.goalSelector.addGoal(4, new AbstractLavaFishEntity.SwimGoal(this));
   }

   protected PathNavigator createNavigation(World p_175447_1_) {
      return new LavaSwimmerPathNavigator(this, p_175447_1_);
   }

   public void travel(Vector3d p_213352_1_) {
      if (this.isEffectiveAi() && this.isInLava()) {
         this.moveRelative(0.01F, p_213352_1_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(p_213352_1_);
      }

   }

   public void aiStep() {
      if (!this.isInLava() && this.onGround && this.verticalCollision) {
         this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), (double)0.4F, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F)));
         this.onGround = false;
         this.hasImpulse = true;
         this.playSound(this.getFlopSound(), this.getSoundVolume(), this.getVoicePitch());
      }

      super.aiStep();
   }

   protected ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
      ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
      if (itemstack.getItem() == Items.LAVA_BUCKET && this.isAlive()) {
         this.playSound(SoundEvents.BUCKET_FILL_FISH, 1.0F, 1.0F);
         itemstack.shrink(1);
         ItemStack itemstack1 = this.getBucketItemStack();
         this.saveToBucketTag(itemstack1);
         if (!this.level.isClientSide) {
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)p_230254_1_, itemstack1);
         }

         if (itemstack.isEmpty()) {
            p_230254_1_.setItemInHand(p_230254_2_, itemstack1);
         } else if (!p_230254_1_.inventory.add(itemstack1)) {
            p_230254_1_.drop(itemstack1, false);
         }

         this.remove();
         return ActionResultType.sidedSuccess(this.level.isClientSide);
      } else {
         return super.mobInteract(p_230254_1_, p_230254_2_);
      }
   }

   protected void saveToBucketTag(ItemStack p_204211_1_) {
      if (this.hasCustomName()) {
         p_204211_1_.setHoverName(this.getCustomName());
      }

   }

   protected abstract ItemStack getBucketItemStack();

   protected boolean canRandomSwim() {
      return true;
   }

   protected abstract SoundEvent getFlopSound();

   protected SoundEvent getSwimSound() {
      return SoundEvents.FISH_SWIM;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
   }

   public boolean isOnFire() {
      return false;
   }

   static class MoveHelperController extends MovementController {
      private final AbstractLavaFishEntity fish;

      MoveHelperController(AbstractLavaFishEntity p_i48857_1_) {
         super(p_i48857_1_);
         this.fish = p_i48857_1_;
      }

      public void tick() {
         if (this.fish.isEyeInFluid(FluidTags.LAVA)) {
            this.fish.setDeltaMovement(this.fish.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
         }

         if (this.operation == Action.MOVE_TO && !this.fish.getNavigation().isDone()) {
            float f = (float)(this.speedModifier * this.fish.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.fish.setSpeed(MathHelper.lerp(0.125F, this.fish.getSpeed(), f));
            double d0 = this.wantedX - this.fish.getX();
            double d1 = this.wantedY - this.fish.getY();
            double d2 = this.wantedZ - this.fish.getZ();
            if (d1 != 0.0D) {
               double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
               this.fish.setDeltaMovement(this.fish.getDeltaMovement().add(0.0D, (double)this.fish.getSpeed() * (d1 / d3) * 0.1D, 0.0D));
            }

            if (d0 != 0.0D || d2 != 0.0D) {
               float f1 = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
               this.fish.yRot = this.rotlerp(this.fish.yRot, f1, 90.0F);
               this.fish.yBodyRot = this.fish.yRot;
            }

         } else {
            this.fish.setSpeed(0.0F);
         }
      }
   }

   static class SwimGoal extends RandomLavaSwimmingGoal {
      private final AbstractLavaFishEntity fish;

      public SwimGoal(AbstractLavaFishEntity p_i48856_1_) {
         super(p_i48856_1_, 1.0D, 40);
         this.fish = p_i48856_1_;
      }

      public boolean canUse() {
         return this.fish.canRandomSwim() && super.canUse();
      }
   }
}