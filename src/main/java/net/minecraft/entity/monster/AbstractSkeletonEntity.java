package net.minecraft.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.bogged.BoggedEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractSkeletonEntity extends Monster implements IRangedAttackMob, ISkeleton, ICrossbowUser {
   private static final DataParameter<Boolean> IS_CHARGING_CROSSBOW = EntityDataManager.defineId(AbstractSkeletonEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> DATA_DROWNED_CONVERSION_ID = EntityDataManager.defineId(AbstractSkeletonEntity.class, DataSerializers.BOOLEAN);




   private int inWaterTime;
   private int conversionTime;










   @OnlyIn(Dist.CLIENT)
   public boolean isChargingCrossbow() {
      return this.entityData.get(IS_CHARGING_CROSSBOW);
   }

   public void setChargingCrossbow(boolean charging) {
      this.entityData.set(IS_CHARGING_CROSSBOW, charging);
   }



   public void shootCrossbowProjectile(LivingEntity target, ItemStack crossbow, ProjectileEntity projectile, float velocity) {
      this.shootCrossbowProjectile(this, target, projectile, velocity, 1.6F);
   }

   public final RangedCrossbowAttackGoal<AbstractSkeletonEntity> rangedCrossbowAttackGoal = new RangedCrossbowAttackGoal<>(this, 1.17D, 16.0F);
   public final RangedBowAttackGoal<AbstractSkeletonEntity> bowGoal = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);
   final AdvancedBowAttackGoal<AbstractSkeletonEntity> bowGoalAdvanced = new AdvancedBowAttackGoal<>(this, 1.0D, 20, 15.0F);
   public final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, this.veryHardmode()) {
      public void stop() {
         super.stop();
         AbstractSkeletonEntity.this.setAggressive(false);
      }

      public void start() {
         super.start();
         AbstractSkeletonEntity.this.setAggressive(true);
      }
   };

   private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = (p_213697_0_) -> {
      return p_213697_0_ == Difficulty.HARD || p_213697_0_ == Difficulty.NORMAL;
   };
   private final BreakDoorGoal breakDoorGoal = new BreakDoorGoal(this, DOOR_BREAKING_PREDICATE.and(difficulty -> this.veryHardmode()));
   private final AdvancedBreakDoorGoal breakDoorGoal1 = new AdvancedBreakDoorGoal(this, DOOR_BREAKING_PREDICATE.and(difficulty -> this.veryHardmode()));

   protected AbstractSkeletonEntity(EntityType<? extends AbstractSkeletonEntity> p_i48555_1_, World p_i48555_2_) {
      super(p_i48555_1_, p_i48555_2_);
      this.reassessWeaponGoal();
   }



   public ImmutableSet<Class<? extends Entity>> getAttackableEntities() {
      return ImmutableSet.of(PlayerEntity.class, VillagerEntity.class, IronGolemEntity.class);
   }

   public boolean isAttackable(Entity entity) {
      return getAttackableEntities().contains(entity.getClass()) && EntityPredicates.ATTACK_ALLOWED.test(entity) && EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(entity) && this.getSensing().canSee(entity);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(2, new RestrictSunGoal(this));
      this.goalSelector.addGoal(2, new SearchForTargetGoal<>(this, PlayerEntity.class, 1.13D, 30, EntityPredicates.NO_CREATIVE_OR_SPECTATOR::test, true));
      this.goalSelector.addGoal(3, new SearchForTargetGoal<>(this, VillagerEntity.class, 1.13D, 30, null, true));

      this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, WolfEntity.class, 6.0F, 1.0D, 1.2D));
      this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, IronGolemEntity.class, 4.5F, 1.0D, 1.2D));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 15, true, false, (entity) -> {

         return !entity.getPassengers().isEmpty() && entity.getPassengers().get(0) != null && this.getAttackableEntities().contains(entity.getPassengers().get(0).getClass());
      }));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, VillagerEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_ON_LAND_SELECTOR));
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D);
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   protected boolean convertsInWater() {
      return false;
   }



   public void tick() {
      super.tick();

      if (!this.level.isClientSide && this.isAlive() && !this.isNoAi()) {
         if (this.isUnderWaterConverting()) {
            --this.conversionTime;
            if (this.conversionTime < 0) {
               this.doUnderWaterConversion();
            }
         } else if (this.convertsInWater()) {
            if (this.isEyeInFluid(FluidTags.WATER)) {
               ++this.inWaterTime;
               if (this.inWaterTime >= getSkeletonConversionTime()[0]) {
                  this.startUnderWaterConversion(getSkeletonConversionTime()[1]);
               }
            } else {
               this.inWaterTime = -1;
            }
         }
      }

      if (!this.veryHardmode()) {
         if (breakDoorGoal != null) {
            if (this.goalSelector.getAvailableGoals().anyMatch((goal) -> goal.getGoal() != breakDoorGoal)) {
               this.goalSelector.addGoal(1, breakDoorGoal);
            }
         }
      } else {
         if (breakDoorGoal1 != null) {
            if (this.goalSelector.getAvailableGoals().anyMatch((goal) -> goal.getGoal() != breakDoorGoal1)) {
               this.goalSelector.addGoal(1, breakDoorGoal1);
            }
         }
      }
   }

   public int[] getSkeletonConversionTime() {
      return new int[]{600, 300};
   }

   public abstract SoundEvent getStepSound();

   public CreatureAttribute getMobType() {
      return CreatureAttribute.UNDEAD;
   }

   public void aiStep() {
      boolean flag = this.isSunBurnTick();
      if (flag && burnsInDay()) {
         ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.HEAD);
         if (!itemstack.isEmpty()) {
            if (itemstack.isDamageableItem()) {
               itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
               if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                  this.broadcastBreakEvent(EquipmentSlotType.HEAD);
                  this.setItemSlot(EquipmentSlotType.HEAD, ItemStack.EMPTY);
               }
            }

            flag = false;
         }

         if (flag) {
            this.setSecondsOnFire(4);
         }
      }

      super.aiStep();
   }

   public void rideTick() {
      super.rideTick();
      if (this.getVehicle() instanceof Creature) {
         Creature creatureentity = (Creature)this.getVehicle();
         this.yBodyRot = creatureentity.yBodyRot;
      }

   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
      super.populateDefaultEquipmentSlots(p_180481_1_);
      Random random1 = new Random(Util.getMillis());
      boolean flag = (this instanceof StrayEntity);
      // Determine the item to be used for the main hand slot
      Item selectedItem;
      if (random.nextFloat() < 0.09 && !flag && !(this instanceof BoggedEntity)) {
         selectedItem = random1.nextFloat() < 0.097 ? Items.GILDED_CROSSBOW : Items.CROSSBOW;
      } else {
         selectedItem = Items.BOW;
      }

// Set the selected item in the main hand slot
      this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(selectedItem));



      // For SkeletonEntity, randomly decide whether to give BONE_ARROW in the OFFHAND
      if (this instanceof SkeletonEntity) {
         if (this.random.nextBoolean()) {
            this.setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.BONE_ARROW, this.random.nextInt(6) + 1));
         }
      }
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      p_213386_4_ = super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      this.populateDefaultEquipmentSlots(p_213386_2_);
      this.populateDefaultEquipmentEnchantments(p_213386_2_);
      this.reassessWeaponGoal();
      this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * p_213386_2_.getSpecialMultiplier());
      if (p_213386_2_.isVeryDifficult()) {
         this.setCanPickUpLoot(true);
      }
      if (this.getItemBySlot(EquipmentSlotType.HEAD).isEmpty()) {
         LocalDate localdate = LocalDate.now();
         int i = localdate.get(ChronoField.DAY_OF_MONTH);
         int j = localdate.get(ChronoField.MONTH_OF_YEAR);
         if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
            this.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.armorDropChances[EquipmentSlotType.HEAD.getIndex()] = 0.0F;
         }
      }

      return p_213386_4_;
   }

   public void reassessWeaponGoal() {
      if (this.level != null && !this.level.isClientSide) {
         this.goalSelector.removeGoal(this.meleeGoal);
         this.goalSelector.removeGoal(this.bowGoal);
         this.goalSelector.removeGoal(this.bowGoalAdvanced);
         this.goalSelector.removeGoal(this.rangedCrossbowAttackGoal);
         ItemStack itemstack = this.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this, Items.BOW));
         ItemStack itemStack1 = this.getItemInHand(ProjectileHelper.getWeaponHoldingCrossbow(this));
         if (itemStack1.getItem() instanceof ICrossbowItem) {
            this.goalSelector.addGoal(3, this.rangedCrossbowAttackGoal);
            return;
         }

         if (itemstack.getItem() == Items.BOW) {
            int i = 20;
            if (this.level.getDifficulty() != Difficulty.HARD) {
               i = 40;
            }
            if (this.veryHardmode()) {
               i = i == 40 ? 12 : 5;
            }

            this.bowGoal.setMinAttackInterval(i);
            this.bowGoalAdvanced.setMinAttackInterval(i);
            this.goalSelector.addGoal(4, this.veryHardmode() ? this.bowGoalAdvanced : bowGoal);
         } else {
            this.goalSelector.addGoal(4, this.meleeGoal);
         }

      }
   }

   public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      ItemStack itemStack = this.getItemInHand(ProjectileHelper.getWeaponHoldingCrossbow(this));
      if (itemStack.getItem() instanceof ICrossbowItem) {
         this.performCrossbowAttack(this, 1.6F);
         return;
      }

      if (this.veryHardmode() && (p_82196_1_ instanceof VillagerEntity /*|| p_82196_1_ instanceof IronGolemEntity*/)) {
         // Perform the hard attack if in very hard mode and the target is a Villager or Iron Golem
         performHardAttack(p_82196_1_, p_82196_2_);
      } else {
         // Normal attack logic
         regularAttack(p_82196_1_, p_82196_2_);
      }
   }

   private void regularAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this, Items.BOW)));
      AbstractArrowEntity abstractarrowentity = this.getArrow(itemstack, p_82196_2_);
      double d0 = p_82196_1_.getX() - this.getX();
      double d1 = p_82196_1_.getY(0.3333333333333333D) - abstractarrowentity.getY();
      double d2 = p_82196_1_.getZ() - this.getZ();
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      float inaccuracy = (float)(14 - this.level.getDifficulty().getId() * 4);

      if (this.veryHardmode()) {
         inaccuracy /= 2;  // Halves the inaccuracy, making it more accurate
      }
      SoundEvent event = this instanceof BoggedEntity ? SoundEvents.BOGGED_SHOOT : SoundEvents.SKELETON_SHOOT;

      abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, inaccuracy);
      this.playSound(event, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level.addFreshEntity(abstractarrowentity);
   }


   public void performHardAttack(LivingEntity target, float p_82196_2_) {
      ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this, Items.BOW)));
      AbstractArrowEntity abstractarrowentity = this.getArrow(itemstack, p_82196_2_);

      double d0 = target.getX() - this.getX();
      double d1 = target.getY(0.3333333333333333D) - abstractarrowentity.getY();
      double d2 = target.getZ() - this.getZ();

      // Get the target's velocity
      Vector3d targetVelocity = target.getDeltaMovement();

      // Calculate the distance to the target
      double distance = MathHelper.sqrt(d0 * d0 + d2 * d2);

      // Calculate the time to intercept based on the projectile speed
      double speed = 1.6F;  // Projectile speed
      double timeToIntercept = distance / speed;

      // Predict target position based on its velocity
      d0 += targetVelocity.x * timeToIntercept;
      d2 += targetVelocity.z * timeToIntercept;

      // Adjust for less y movement
      d1 += targetVelocity.y * timeToIntercept * 0.5;

      double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
      float inaccuracy = (float)(14 - this.level.getDifficulty().getId() * 4);

      if (this.veryHardmode()) {
         inaccuracy /= 2;  // Halves the inaccuracy, making it more accurate
      }
      SoundEvent event = this instanceof BoggedEntity ? SoundEvents.BOGGED_SHOOT : SoundEvents.SKELETON_SHOOT;
      abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, (float) speed, inaccuracy);
      this.playSound(event, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level.addFreshEntity(abstractarrowentity);
   }

   protected AbstractArrowEntity getArrow(ItemStack p_213624_1_, float p_213624_2_) {
      return ProjectileHelper.getMobArrow(this, p_213624_1_, p_213624_2_);
   }

   public boolean canFireProjectileWeapon(ShootableItem p_230280_1_) {
      return p_230280_1_ == Items.BOW || p_230280_1_ == Items.BONE_BOW || p_230280_1_ instanceof ICrossbowItem;
   }





   public void addAdditionalSaveData(CompoundNBT compoundNBT) {
      super.addAdditionalSaveData(compoundNBT);
      compoundNBT.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
      compoundNBT.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
   }




   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.reassessWeaponGoal();
      this.inWaterTime = p_70037_1_.getInt("InWaterTime");
      if (p_70037_1_.contains("DrownedConversionTime", 99) && p_70037_1_.getInt("DrownedConversionTime") > -1) {
         this.startUnderWaterConversion(p_70037_1_.getInt("DrownedConversionTime"));
      }

   }

   public boolean isUnderWaterConverting() {
      return this.getEntityData().get(DATA_DROWNED_CONVERSION_ID);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_DROWNED_CONVERSION_ID, false);
      this.entityData.define(IS_CHARGING_CROSSBOW, false);
   }

   public void setItemSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {
      super.setItemSlot(p_184201_1_, p_184201_2_);
      if (!this.level.isClientSide) {
         this.reassessWeaponGoal();
      }

   }


   private void startUnderWaterConversion(int p_204704_1_) {
      this.conversionTime = p_204704_1_;
      this.getEntityData().set(DATA_DROWNED_CONVERSION_ID, true);
   }

   protected void doUnderWaterConversion() {
      this.convertToSkeletonType(EntityType.BOGGED);
      if (!this.isSilent()) {
         this.level.levelEvent((PlayerEntity)null, 1040, this.blockPosition(), 0);
      }

   }

   public boolean burnsInDay() {
      return true;
   }

   protected void convertToSkeletonType(EntityType<? extends AbstractSkeletonEntity> mob) {
      AbstractSkeletonEntity skeleton = this.convertTo(mob, true);
      if (skeleton != null) {
         if (skeleton instanceof BoggedEntity) {
            ((BoggedEntity)skeleton).regrowMushrooms();
         }
      }

   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 1.74F;
   }

   public double getMyRidingOffset() {
      return -0.6D;
   }
}