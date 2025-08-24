package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CowEntity extends Animal implements WarmColdVariantHolder {
   public CowEntity(EntityType<? extends CowEntity> p_i48567_1_, World p_i48567_2_) {
      super(p_i48567_1_, p_i48567_2_);
   }
   private static final DataParameter<WarmColdVariant> VARIANT = EntityDataManager.defineId(CowEntity.class, DataSerializers.WARM_COLD_VARIANT);


   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(VARIANT, WarmColdVariant.TEMPERATE);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(Items.WHEAT), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, (double)0.2F);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.COW_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.COW_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COW_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   public ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
      ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
      if (itemstack.getItem() == Items.BUCKET && !this.isBaby()) {
         p_230254_1_.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
         ItemStack itemstack1 = DrinkHelper.createFilledResult(itemstack, p_230254_1_, Items.MILK_BUCKET.getDefaultInstance());
         p_230254_1_.setItemInHand(p_230254_2_, itemstack1);
         return ActionResultType.sidedSuccess(this.level.isClientSide);
      } else {
         return super.mobInteract(p_230254_1_, p_230254_2_);
      }
   }

   public CowEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
      CowEntity cow = EntityType.COW.create(p_241840_1_);
      cow.setVariant(this.random.nextBoolean() ? this.getVariant() : ((CowEntity)p_241840_2_).getVariant());
      return cow;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return this.isBaby() ? p_213348_2_.height * 0.95F : 1.3F;
   }

   @Override
   public void setVariant(WarmColdVariant variant) {
      this.entityData.set(VARIANT, variant);
   }

   public void addAdditionalSaveData(CompoundNBT nbt) {
      super.addAdditionalSaveData(nbt);
      this.putVariantToTag(nbt);
   }

   public void readAdditionalSaveData(CompoundNBT nbt) {
      super.readAdditionalSaveData(nbt);
      this.setVariant(this.getVariantFromTag(nbt));
   }

   @Override
   public WarmColdVariant getVariant() {
      return this.entityData.get(VARIANT);
   }
}