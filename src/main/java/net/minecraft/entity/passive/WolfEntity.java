package net.minecraft.entity.passive;

import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.ShamanEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WolfEntity extends TameableEntity implements IAngerable {
   private static final DataParameter<Boolean> DATA_INTERESTED_ID = EntityDataManager.defineId(WolfEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> DATA_COLLAR_COLOR = EntityDataManager.defineId(WolfEntity.class, DataSerializers.INT);
   private static final DataParameter<Integer> DATA_REMAINING_ANGER_TIME = EntityDataManager.defineId(WolfEntity.class, DataSerializers.INT);
   public static final Predicate<LivingEntity> PREY_SELECTOR = (p_213440_0_) -> {
      EntityType<?> entitytype = p_213440_0_.getType();
      return entitytype == EntityType.SHEEP || entitytype == EntityType.RABBIT || entitytype == EntityType.FOX;
   };

   private final Predicate<LivingEntity> SHAMAN_SELECTOR = (entity) -> {
      EntityType<?> entityType = entity.getType();
      boolean flag = entityType == EntityType.VILLAGER || entityType == EntityType.IRON_GOLEM || entityType == EntityType.PLAYER;
      return flag && this.isFromShaman();
   };
   private float interestedAngle;
   private float interestedAngleO;
   private boolean isWet;
   private boolean isShaking;
   private boolean fromShaman;
   private int shamanSpawnTimer = 1200;
   private float shakeAnim;
   private float shakeAnimO;
   private ShamanEntity shaman;
   private WolfFollowShamanGoal followShamanGoal;
   private static final RangedInteger PERSISTENT_ANGER_TIME = TickRangeConverter.rangeOfSeconds(20, 39);
   private UUID persistentAngerTarget;

   public WolfEntity(EntityType<? extends WolfEntity> p_i50240_1_, World p_i50240_2_) {
      super(p_i50240_1_, p_i50240_2_);
      this.setTame(false);
   }

   public boolean isFromShaman() {
      return fromShaman;
   }

   public boolean isShaman(LivingEntity entity) {
      return SHAMAN_SELECTOR.test(entity) && isFromShaman();
   }

   public void setShaman(ShamanEntity shaman) {
      this.shaman = shaman;
   }
   @Nullable
   public ShamanEntity getShaman() {
      if(shaman != null) {
         return shaman;
      } else {
         return null;
      }
   }

   public void setFromShaman(boolean val, int value) {
      shamanSpawnTimer = value;
      fromShaman = val;
   }

   public void setFromShaman(boolean val) {
      fromShaman = val;
   }

   class WolfFollowShamanGoal extends Goal {
      private WolfEntity wolf;
      private ShamanEntity shaman;
      private final IWorldReader level;
      private final double speedModifier = 1.0D;
      private final PathNavigator navigation;
      private int timeToRecalcPath;
      private final float stopDistance = 2.0F;
      private final float startDistance = 7.0F;
      private float oldWaterCost;
      private final boolean canFly = false;

      public WolfFollowShamanGoal(WolfEntity wolf) {
         this.shaman = wolf.getShaman();
         this.level = wolf.level;
         this.navigation = wolf.getNavigation();
         this.wolf = wolf;
      }

      public boolean canUse() {
         if (shaman == null) {
            return false;
         } else if (shaman.isSpectator()) {
            return false;
         } else if (this.wolf.isOrderedToSit()) {
            return false;
         } else if (this.wolf.distanceToSqr(shaman) < (double)(this.startDistance * this.startDistance)) {
            return false;
         } else {
             return true;
         }
      }

      public boolean canContinueToUse() {
         if (this.navigation.isDone()) {
            return false;
         } else if (this.wolf.isOrderedToSit()) {
            return false;
         } else {
            return !(this.wolf.distanceToSqr(shaman) <= (double)(this.stopDistance * this.stopDistance));
         }
      }

      public void start() {
         this.timeToRecalcPath = 0;
         this.oldWaterCost = this.wolf.getPathfindingMalus(PathNodeType.WATER);
         this.wolf.setPathfindingMalus(PathNodeType.WATER, 0.0F);
      }

      public void stop() {
         this.shaman = null;
         this.navigation.stop();
         this.wolf.setPathfindingMalus(PathNodeType.WATER, this.oldWaterCost);
      }

      public void tick() {
         this.wolf.getLookControl().setLookAt(this.wolf, 10.0F, (float)this.wolf.getMaxHeadXRot());
         if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.wolf.isLeashed() && !this.wolf.isPassenger()) {
               if (this.wolf.distanceToSqr(this.wolf) >= 144.0D) {
                  this.teleportToShaman();
               } else {
                  this.navigation.moveTo(this.wolf, this.speedModifier);
               }

            }
         }
      }

      private void teleportToShaman() {
         BlockPos blockpos = this.shaman.blockPosition();

         for(int i = 0; i < 10; ++i) {
            int j = this.randomIntInclusive(-3, 3);
            int k = this.randomIntInclusive(-1, 1);
            int l = this.randomIntInclusive(-3, 3);
            boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
               return;
            }
         }

      }

      private boolean maybeTeleportTo(int xCoord, int yCoord, int zCoord) {
         if (Math.abs((double)xCoord - this.shaman.getX()) < 2.0D && Math.abs((double)zCoord - this.shaman.getZ()) < 2.0D) {
            return false;
         } else if (!this.canTeleportTo(new BlockPos(xCoord, yCoord, zCoord))) {
            return false;
         } else {
            this.wolf.moveTo((double)xCoord + 0.5D, (double)yCoord, (double)zCoord + 0.5D, this.wolf.yRot, this.wolf.xRot);
            this.navigation.stop();
            return true;
         }
      }

      private boolean canTeleportTo(BlockPos pos) {
         PathNodeType pathnodetype = WalkNodeProcessor.getBlockPathTypeStatic(this.level, pos.mutable());
         if (pathnodetype != PathNodeType.WALKABLE) {
            return false;
         } else {
            BlockState blockstate = this.level.getBlockState(pos.below());
            if (!this.canFly && blockstate.getBlock() instanceof LeavesBlock) {
               return false;
            } else {
               BlockPos blockpos = pos.subtract(this.wolf.blockPosition());
               return this.level.noCollision(this.wolf, this.wolf.getBoundingBox().move(blockpos));
            }
         }
      }

      private int randomIntInclusive(int min, int max) {
         return this.wolf.getRandom().nextInt(max - min + 1) + min;
      }
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, new SitGoal(this));
      this.goalSelector.addGoal(3, new WolfEntity.AvoidEntityGoal(this, LlamaEntity.class, 24.0F, 1.5D, 1.5D));
      this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
      this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(9, new BegGoal(this, 8.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
      this.targetSelector.addGoal(5, new NonTamedTargetGoal<>(this, Animal.class, false, PREY_SELECTOR));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 12, false, true, entity -> {
         return this.getOwner() != null && entity.as(Mob.class).getTarget() == this.getOwner();
      }));
      this.targetSelector.addGoal(6, new NonTamedTargetGoal<>(this, TurtleEntity.class, false, TurtleEntity.BABY_ON_LAND_SELECTOR));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 12, true, false, this::isShaman));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, 12, true, false, this::isShaman));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, 12, true, false, this::isShaman));
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, AbstractSkeletonEntity.class, false));
      this.targetSelector.addGoal(8, new ResetAngerGoal<>(this, true));
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.3F).add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_INTERESTED_ID, false);
      this.entityData.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
      this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      if (this.getShaman() != null)
         p_213281_1_.putUUID("Shaman", this.getShaman().getUUID());
      p_213281_1_.putBoolean("fromShaman", this.isFromShaman());
      p_213281_1_.putByte("CollarColor", (byte)this.getCollarColor().getId());
      this.addPersistentAngerSaveData(p_213281_1_);
   }

   public void readAdditionalSaveData(CompoundNBT compound) {
      super.readAdditionalSaveData(compound);
      if(compound.contains("Shaman")) {
         this.setShamanUUID(compound.getUUID("Shaman"));
      }
      if(compound.contains("fromShaman")) {
         this.setFromShaman(compound.getBoolean("fromShaman"));
      }
      if (compound.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(compound.getInt("CollarColor")));
      }

      this.readPersistentAngerSaveData((ServerWorld)this.level, compound);
   }

   private void setShamanUUID(UUID shaman) {
      this.getShaman().setUUID(shaman);
   }

   protected SoundEvent getAmbientSound() {
      if (this.isAngry()) {
         return SoundEvents.WOLF_GROWL;
      } else if (this.random.nextInt(3) == 0) {
         return this.isTame() && this.getHealth() < 10.0F ? SoundEvents.WOLF_WHINE : SoundEvents.WOLF_PANT;
      } else {
         return SoundEvents.WOLF_AMBIENT;
      }
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.WOLF_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WOLF_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level.isClientSide && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround) {
         this.isShaking = true;
         this.shakeAnim = 0.0F;
         this.shakeAnimO = 0.0F;
         this.level.broadcastEntityEvent(this, (byte)8);
      }

      if (!this.level.isClientSide) {
         this.updatePersistentAnger((ServerWorld)this.level, true);
      }

   }

   public void tick() {
      super.tick();

      if (shaman != null) {
         followShamanGoal = new WolfFollowShamanGoal(this);
         if(!this.goalSelector.getAvailableGoals().anyMatch(Predicate.isEqual(followShamanGoal))) {
            this.goalSelector.addGoal(2, followShamanGoal);
         }
      } else {
         this.goalSelector.removeGoal(followShamanGoal);
      }

      if(isFromShaman()) {
         if (shamanSpawnTimer-- < 0) {
            this.remove();
         }
      }
      if (this.isAlive()) {
         this.interestedAngleO = this.interestedAngle;
         if (this.isInterested()) {
            this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
         } else {
            this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
         }

         if (this.isInWaterRainOrBubble()) {
            this.isWet = true;
            if (this.isShaking && !this.level.isClientSide) {
               this.level.broadcastEntityEvent(this, (byte)56);
               this.cancelShake();
            }
         } else if ((this.isWet || this.isShaking) && this.isShaking) {
            if (this.shakeAnim == 0.0F) {
               this.playSound(SoundEvents.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

            this.shakeAnimO = this.shakeAnim;
            this.shakeAnim += 0.05F;
            if (this.shakeAnimO >= 2.0F) {
               this.isWet = false;
               this.isShaking = false;
               this.shakeAnimO = 0.0F;
               this.shakeAnim = 0.0F;
            }

            if (this.shakeAnim > 0.4F) {
               float f = (float)this.getY();
               int i = (int)(MathHelper.sin((this.shakeAnim - 0.4F) * (float)Math.PI) * 7.0F);
               Vector3d vector3d = this.getDeltaMovement();

               for(int j = 0; j < i; ++j) {
                  float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                  float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                  this.level.addParticle(ParticleTypes.SPLASH, this.getX() + (double)f1, (double)(f + 0.8F), this.getZ() + (double)f2, vector3d.x, vector3d.y, vector3d.z);
               }
            }
         }

      }
   }

   private void cancelShake() {
      this.isShaking = false;
      this.shakeAnim = 0.0F;
      this.shakeAnimO = 0.0F;
   }

   public void die(DamageSource p_70645_1_) {
      this.isWet = false;
      this.isShaking = false;
      this.shakeAnimO = 0.0F;
      this.shakeAnim = 0.0F;
      super.die(p_70645_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isWet() {
      return this.isWet;
   }

   @OnlyIn(Dist.CLIENT)
   public float getWetShade(float p_70915_1_) {
      return Math.min(0.5F + MathHelper.lerp(p_70915_1_, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.5F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public float getBodyRollAngle(float p_70923_1_, float p_70923_2_) {
      float f = (MathHelper.lerp(p_70923_1_, this.shakeAnimO, this.shakeAnim) + p_70923_2_) / 1.8F;
      if (f < 0.0F) {
         f = 0.0F;
      } else if (f > 1.0F) {
         f = 1.0F;
      }

      return MathHelper.sin(f * (float)Math.PI) * MathHelper.sin(f * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadRollAngle(float p_70917_1_) {
      return MathHelper.lerp(p_70917_1_, this.interestedAngleO, this.interestedAngle) * 0.15F * (float)Math.PI;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.8F;
   }

   public int getMaxHeadXRot() {
      return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         Entity entity = p_70097_1_.getEntity();
         this.setOrderedToSit(false);
         if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
            p_70097_2_ = (p_70097_2_ + 1.0F) / 2.0F;
         }

         return super.hurt(p_70097_1_, p_70097_2_);
      }
   }

   public boolean doHurtTarget(Entity p_70652_1_) {
      boolean flag = p_70652_1_.hurt(DamageSource.mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
      if (flag) {
         this.doEnchantDamageEffects(this, p_70652_1_);
      }

      return flag;
   }

   public void setTame(boolean p_70903_1_) {
      super.setTame(p_70903_1_);
      if (p_70903_1_) {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
         this.setHealth(20.0F);
      } else {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0D);
      }

      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
   }

   public ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
      ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
      Item item = itemstack.getItem();
      if (this.level.isClientSide) {
         boolean flag = this.isOwnedBy(p_230254_1_) || this.isTame() || item == Items.BONE && !this.isTame() && !this.isAngry();
         return flag ? ActionResultType.CONSUME : ActionResultType.PASS;
      } else {
         if (this.isTame()) {
            if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
               if (!p_230254_1_.abilities.instabuild) {
                  itemstack.shrink(1);
               }

               this.heal((float)item.getFoodProperties().getNutrition());
               return ActionResultType.SUCCESS;
            }

            if (!(item instanceof DyeItem)) {
               ActionResultType actionresulttype = super.mobInteract(p_230254_1_, p_230254_2_);
               if ((!actionresulttype.consumesAction() || this.isBaby()) && this.isOwnedBy(p_230254_1_)) {
                  this.setOrderedToSit(!this.isOrderedToSit());
                  this.jumping = false;
                  this.navigation.stop();
                  this.setTarget((LivingEntity)null);
                  return ActionResultType.SUCCESS;
               }

               return actionresulttype;
            }

            DyeColor dyecolor = ((DyeItem)item).getDyeColor();
            if (dyecolor != this.getCollarColor()) {
               this.setCollarColor(dyecolor);
               if (!p_230254_1_.abilities.instabuild) {
                  itemstack.shrink(1);
               }

               return ActionResultType.SUCCESS;
            }
         } else if (item == Items.BONE && !this.isAngry()) {
            if (!p_230254_1_.abilities.instabuild) {
               itemstack.shrink(1);
            }

            if (this.random.nextInt(3) == 0) {
               this.tame(p_230254_1_);
               this.navigation.stop();
               this.setTarget((LivingEntity)null);
               this.setOrderedToSit(true);
               this.level.broadcastEntityEvent(this, (byte)7);
            } else {
               this.level.broadcastEntityEvent(this, (byte)6);
            }

            return ActionResultType.SUCCESS;
         }

         return super.mobInteract(p_230254_1_, p_230254_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 8) {
         this.isShaking = true;
         this.shakeAnim = 0.0F;
         this.shakeAnimO = 0.0F;
      } else if (p_70103_1_ == 56) {
         this.cancelShake();
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getTailAngle() {
      if (this.isAngry()) {
         return 1.5393804F;
      } else {
         return this.isTame() ? (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F);
      }
   }

   public boolean isFood(ItemStack p_70877_1_) {
      Item item = p_70877_1_.getItem();
      return item.isEdible() && item.getFoodProperties().isMeat();
   }

   public int getMaxSpawnClusterSize() {
      return 8;
   }

   public int getRemainingPersistentAngerTime() {
      return this.entityData.get(DATA_REMAINING_ANGER_TIME);
   }

   public void setRemainingPersistentAngerTime(int p_230260_1_) {
      this.entityData.set(DATA_REMAINING_ANGER_TIME, p_230260_1_);
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
   }

   @Nullable
   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public void setPersistentAngerTarget(@Nullable UUID p_230259_1_) {
      this.persistentAngerTarget = p_230259_1_;
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
   }

   public void setCollarColor(DyeColor p_175547_1_) {
      this.entityData.set(DATA_COLLAR_COLOR, p_175547_1_.getId());
   }

   public WolfEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
      WolfEntity wolfentity = EntityType.WOLF.create(p_241840_1_);
      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         wolfentity.setOwnerUUID(uuid);
         wolfentity.setTame(true);
      }

      return wolfentity;
   }

   public void setIsInterested(boolean p_70918_1_) {
      this.entityData.set(DATA_INTERESTED_ID, p_70918_1_);
   }

   public boolean canMate(Animal p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (!this.isTame()) {
         return false;
      } else if (!(p_70878_1_ instanceof WolfEntity)) {
         return false;
      } else {
         WolfEntity wolfentity = (WolfEntity)p_70878_1_;
         if (!wolfentity.isTame()) {
            return false;
         } else if (wolfentity.isInSittingPose()) {
            return false;
         } else {
            return this.isInLove() && wolfentity.isInLove();
         }
      }
   }

   public boolean isInterested() {
      return this.entityData.get(DATA_INTERESTED_ID);
   }

   public boolean wantsToAttack(LivingEntity p_142018_1_, LivingEntity p_142018_2_) {
      if (!(p_142018_1_ instanceof CreeperEntity) && !(p_142018_1_ instanceof GhastEntity)) {
         if (p_142018_1_ instanceof WolfEntity) {
            WolfEntity wolfentity = (WolfEntity)p_142018_1_;
            return !wolfentity.isTame() || wolfentity.getOwner() != p_142018_2_;
         } else if (p_142018_1_ instanceof PlayerEntity && p_142018_2_ instanceof PlayerEntity && !((PlayerEntity)p_142018_2_).canHarmPlayer((PlayerEntity)p_142018_1_)) {
            return false;
         } else if (p_142018_1_ instanceof AbstractHorseEntity && ((AbstractHorseEntity)p_142018_1_).isTamed()) {
            return false;
         } else {
            return !(p_142018_1_ instanceof TameableEntity) || !((TameableEntity)p_142018_1_).isTame();
         }
      } else {
         return false;
      }
   }

   public boolean canBeLeashed(PlayerEntity p_184652_1_) {
      return !this.isAngry() && super.canBeLeashed(p_184652_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public Vector3d getLeashOffset() {
      return new Vector3d(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   class AvoidEntityGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.AvoidEntityGoal<T> {
      private final WolfEntity wolf;

      public AvoidEntityGoal(WolfEntity p_i47251_2_, Class<T> p_i47251_3_, float p_i47251_4_, double p_i47251_5_, double p_i47251_7_) {
         super(p_i47251_2_, p_i47251_3_, p_i47251_4_, p_i47251_5_, p_i47251_7_);
         this.wolf = p_i47251_2_;
      }

      public boolean canUse() {
         if (super.canUse() && this.toAvoid instanceof LlamaEntity) {
            return !this.wolf.isTame() && this.avoidLlama((LlamaEntity)this.toAvoid);
         } else {
            return false;
         }
      }

      private boolean avoidLlama(LlamaEntity p_190854_1_) {
         return p_190854_1_.getStrength() >= WolfEntity.this.random.nextInt(5);
      }

      public void start() {
         WolfEntity.this.setTarget((LivingEntity)null);
         super.start();
      }

      public void tick() {
         WolfEntity.this.setTarget((LivingEntity)null);
         super.tick();
      }
   }
}