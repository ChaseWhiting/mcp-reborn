package net.minecraft.entity.monster;

import java.util.*;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class EndermanEntity extends Monster implements IAngerable {
   private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", (double)0.15F, AttributeModifier.Operation.ADDITION);
   private static final DataParameter<Optional<BlockState>> DATA_CARRY_STATE = EntityDataManager.defineId(EndermanEntity.class, DataSerializers.BLOCK_STATE);
   private static final DataParameter<Boolean> DATA_CREEPY = EntityDataManager.defineId(EndermanEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> DATA_STARED_AT = EntityDataManager.defineId(EndermanEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> ATTACKING_TICKS = EntityDataManager.defineId(EndermanEntity.class, DataSerializers.INT);
   private static final DataParameter<Integer> HURT_TICKS = EntityDataManager.defineId(EndermanEntity.class, DataSerializers.INT);

   private static final Predicate<LivingEntity> ENDERMITE_SELECTOR = (p_213626_0_) -> {
      return p_213626_0_ instanceof EndermiteEntity;
   };
   private int lastStareSound = Integer.MIN_VALUE;
   private int targetChangeTime;
   private static final RangedInteger PERSISTENT_ANGER_TIME = TickRangeConverter.rangeOfSeconds(20, 39);
   private int remainingPersistentAngerTime;
   public AnimationState holdingBlockState = new AnimationState();
   public AnimationState attackState = new AnimationState();
   public AnimationState hurtState = new AnimationState();

   private UUID persistentAngerTarget;

   public EndermanEntity(EntityType<? extends EndermanEntity> p_i50210_1_, World p_i50210_2_) {
      super(p_i50210_1_, p_i50210_2_);
      if (this.veryHardmode()) {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(90D);
         this.setHealth(this.getMaxHealth());
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35F);
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(9D);
         this.xpReward = 20;
      }
      this.maxUpStep = 1.0F;
      this.setPathfindingMalus(PathNodeType.WATER, -1.0F);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new EndermanEntity.StareGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D, 0.0F));
      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.goalSelector.addGoal(1, new EndermanEntity.PlaceBlockGoal(this));
      this.goalSelector.addGoal(1, new EndermanEntity.TakeBlockGoal(this));
      this.targetSelector.addGoal(1, new EndermanEntity.FindPlayerGoal(this, this::isAngryAt));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EndermiteEntity.class, 10, true, false, ENDERMITE_SELECTOR));
      this.targetSelector.addGoal(4, new ResetAngerGoal<>(this, false));
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, (double)0.3F).add(Attributes.ATTACK_DAMAGE, 7.0D).add(Attributes.FOLLOW_RANGE, 64.0D);
   }





   public void setTarget(@Nullable LivingEntity p_70624_1_) {
      super.setTarget(p_70624_1_);
      ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
      if (p_70624_1_ == null) {
         this.targetChangeTime = 0;
         this.entityData.set(DATA_CREEPY, false);
         this.entityData.set(DATA_STARED_AT, false);
         modifiableattributeinstance.removeModifier(SPEED_MODIFIER_ATTACKING);
      } else {
         this.targetChangeTime = this.tickCount;
         this.entityData.set(DATA_CREEPY, true);
         if (!modifiableattributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING)) {
            modifiableattributeinstance.addTransientModifier(SPEED_MODIFIER_ATTACKING);
         }
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_CARRY_STATE, Optional.empty());
      this.entityData.define(DATA_CREEPY, false);
      this.entityData.define(DATA_STARED_AT, false);
      this.entityData.define(ATTACKING_TICKS, 0);
      this.entityData.define(HURT_TICKS, 0);


   }

   public void setAttackTicks(int time) {
      this.entityData.set(ATTACKING_TICKS, time);
   }

   public int getAttackTicks() {
      return this.entityData.get(ATTACKING_TICKS);
   }

   public void setHurtTicks(int time) {
      this.entityData.set(HURT_TICKS, time);
   }

   public int getHurtTicks() {
      return this.entityData.get(HURT_TICKS);
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
   }

   public void setRemainingPersistentAngerTime(int p_230260_1_) {
      this.remainingPersistentAngerTime = p_230260_1_;
   }

   public int getRemainingPersistentAngerTime() {
      return this.remainingPersistentAngerTime;
   }

   public void setPersistentAngerTarget(@Nullable UUID p_230259_1_) {
      this.persistentAngerTarget = p_230259_1_;
   }

   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public void playStareSound() {
      if (this.tickCount >= this.lastStareSound + 400) {
         this.lastStareSound = this.tickCount;
         if (!this.isSilent()) {
            this.level.playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENDERMAN_STARE, this.getSoundSource(), 2.5F, 1.0F, false);
         }
      }

   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_CREEPY.equals(p_184206_1_) && this.hasBeenStaredAt()) {
         if (this.level.isClientSide) {
            this.playStareSound();
         }
         if (this.veryHardmode()) {
            this.level.getNearbyPlayers((new EntityPredicate()).range(32.0D).allowUnseeable().ignoreInvisibilityTesting(), this, this.getBoundingBox().inflate(32, 20, 32)).forEach(this::addBlindness);
         }
      }

      super.onSyncedDataUpdated(p_184206_1_);
   }

   public void addBlindness(PlayerEntity player) {
      player.addEffect(new EffectInstance(Effects.BLINDNESS, 15 * 20, 0));

// Set a random body rotation (yaw) between -180 and 180 degrees (full circle)
      player.yRot = (MathHelper.nextFloat(this.random, -180F, 180F));

// Set the head yaw to match the body so that the head faces the same random direction
      player.setYHeadRot(player.yBodyRot);

// Set the head pitch to look up randomly between 0 (straight ahead) and -90 (looking up)
      player.xRot = (MathHelper.nextFloat(new Random(), -90F, 0F));
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      BlockState blockstate = this.getCarriedBlock();
      if (blockstate != null) {
         p_213281_1_.put("carriedBlockState", NBTUtil.writeBlockState(blockstate));
      }

      this.addPersistentAngerSaveData(p_213281_1_);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      BlockState blockstate = null;
      if (p_70037_1_.contains("carriedBlockState", 10)) {
         blockstate = NBTUtil.readBlockState(p_70037_1_.getCompound("carriedBlockState"));
         if (blockstate.isAir()) {
            blockstate = null;
         }
      }

      this.setCarriedBlock(blockstate);
      this.readPersistentAngerSaveData((ServerWorld)this.level, p_70037_1_);
   }

   private boolean isLookingAtMe(PlayerEntity p_70821_1_) {
      ItemStack itemstack = p_70821_1_.inventory.armor.get(3);
      if (itemstack.getItem() == Blocks.CARVED_PUMPKIN.asItem() || itemstack.getItem() == Blocks.WHITE_CARVED_PUMPKIN.asItem()) {
         return false;
      } else {
         Vector3d vector3d = p_70821_1_.getViewVector(1.0F).normalize();
         Vector3d vector3d1 = new Vector3d(this.getX() - p_70821_1_.getX(), this.getEyeY() - p_70821_1_.getEyeY(), this.getZ() - p_70821_1_.getZ());
         double d0 = vector3d1.length();
         vector3d1 = vector3d1.normalize();
         double d1 = vector3d.dot(vector3d1);
         return d1 > 1.0D - 0.025D / d0 ? p_70821_1_.canSee(this) : false;
      }
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 2.55F;
   }

   public void aiStep() {
      if (this.level.isClientSide) {
         for(int i = 0; i < 2; ++i) {
            this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
         }
      }

      this.jumping = false;
      if (!this.level.isClientSide) {
         this.updatePersistentAnger((ServerWorld)this.level, true);
      }

      super.aiStep();
   }

   public boolean isSensitiveToWater() {
      return Boolean.TRUE;
   }

   protected void customServerAiStep() {
      if (this.level.isDay() && this.tickCount >= this.targetChangeTime + 600) {
         float f = this.getBrightness();
         if (f > 0.5F && this.level.canSeeSky(this.blockPosition()) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
            this.setTarget((LivingEntity)null);
            this.teleport();
         }
      }

      super.customServerAiStep();
   }

   protected boolean teleport() {
      if (!this.level.isClientSide() && this.isAlive()) {
         double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
         double d1 = this.getY() + (double)(this.random.nextInt(64) - 32);
         double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
         return this.teleport(d0, d1, d2);
      } else {
         return false;
      }
   }

   private boolean teleportTowards(Entity p_70816_1_) {
      Vector3d vector3d = new Vector3d(this.getX() - p_70816_1_.getX(), this.getY(0.5D) - p_70816_1_.getEyeY(), this.getZ() - p_70816_1_.getZ());
      vector3d = vector3d.normalize();
      double d0 = 16.0D;
      double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.x * d0;
      double d2 = this.getY() + (double)(this.random.nextInt((int) d0) - 8) - vector3d.y * d0;
      double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.z * d0;
      return this.teleport(d1, d2, d3);
   }

   private boolean teleportBehind(Entity target) {
      // Get the target's forward direction vector
      Vector3d forwardVector = target.getLookAngle().normalize();

      // Define the distance behind the target to teleport to
      double distanceBehind = 5.0D; // Adjust this value for how far behind you want to teleport

      // Calculate the position to teleport to, behind the target
      double targetX = target.getX() - forwardVector.x * distanceBehind;
      double targetY = target.getY() + 0.5D; // You can adjust this based on the height difference
      double targetZ = target.getZ() - forwardVector.z * distanceBehind;

      // Add some randomness to make it more natural (optional)
      double dX = targetX + (this.random.nextDouble() - 0.5D) * 2.0D; // Adjust randomness as needed
      double dY = targetY + (this.random.nextDouble() - 0.5D) * 2.0D;
      double dZ = targetZ + (this.random.nextDouble() - 0.5D) * 2.0D;

      // Perform the teleport and return the result
      return this.teleport(dX, dY, dZ);
   }

   private boolean teleport(double p_70825_1_, double p_70825_3_, double p_70825_5_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_70825_1_, p_70825_3_, p_70825_5_);

      while(blockpos$mutable.getY() > 0 && !this.level.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
         blockpos$mutable.move(Direction.DOWN);
      }

      BlockState blockstate = this.level.getBlockState(blockpos$mutable);
      boolean flag = blockstate.getMaterial().blocksMotion();
      boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
      if (flag && !flag1) {
         boolean flag2 = this.randomTeleport(p_70825_1_, p_70825_3_, p_70825_5_, true);
         if (flag2 && !this.isSilent()) {
            this.level.playSound((PlayerEntity)null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
         }

         return flag2;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return this.isCreepy() ? SoundEvents.ENDERMAN_SCREAM : SoundEvents.ENDERMAN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENDERMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDERMAN_DEATH;
   }

   protected void dropCustomDeathLoot(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
      super.dropCustomDeathLoot(p_213333_1_, p_213333_2_, p_213333_3_);
      BlockState blockstate = this.getCarriedBlock();
      if (blockstate != null) {
         this.spawnAtLocation(blockstate.getBlock());
      }

   }

   public void setCarriedBlock(@Nullable BlockState p_195406_1_) {
      this.entityData.set(DATA_CARRY_STATE, Optional.ofNullable(p_195406_1_));
   }

   @Nullable
   public BlockState getCarriedBlock() {
      return this.entityData.get(DATA_CARRY_STATE).orElse((BlockState)null);
   }

   public boolean hurt(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (source instanceof IndirectEntityDamageSource) {
         for (int i = 0; i < 64; ++i) {
            if (this.teleport()) {
               return true;
            }
         }
         return false;
      } else {

         boolean flag = super.hurt(source, amount);
         if (!this.level.isClientSide() && !(source.getEntity() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
            this.teleport();
         }



         return flag;
      }
   }

   public boolean isCreepy() {
      return this.entityData.get(DATA_CREEPY);
   }

   public boolean hasBeenStaredAt() {
      return this.entityData.get(DATA_STARED_AT);
   }

   public void setBeingStaredAt() {
      this.entityData.set(DATA_STARED_AT, true);
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.getCarriedBlock() != null;
   }

   public class FindPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {
      private final EndermanEntity enderman;
      private PlayerEntity pendingTarget;
      private int aggroTime;
      private int teleportTime;
      private int stareGracePeriod; // Add a grace period to keep aggro for a short time
      private final EntityPredicate startAggroTargetConditions;
      private final EntityPredicate continueAggroTargetConditions = (new EntityPredicate()).allowUnseeable();

      public FindPlayerGoal(EndermanEntity enderman, @Nullable Predicate<LivingEntity> targetPredicate) {
         super(enderman, PlayerEntity.class, 10, false, false, targetPredicate);
         this.enderman = enderman;
         this.startAggroTargetConditions = (new EntityPredicate()).range(this.getFollowDistance()).selector((player) -> enderman.isLookingAtMe((PlayerEntity) player));
         this.stareGracePeriod = 20; // This can be adjusted for how long the Enderman stays aggro after looking away
      }

      public boolean canUse() {
         this.pendingTarget = this.enderman.level.getNearestPlayer(this.startAggroTargetConditions, this.enderman);
         return this.pendingTarget != null;
      }

      public void start() {
         this.aggroTime = 5;
         this.teleportTime = 0;
         this.enderman.setBeingStaredAt();
         this.stareGracePeriod = 20; // Reset the grace period when the player first stares at the enderman
         if (this.enderman.veryHardmode()) {
            this.enderman.addEffect(new EffectInstance(Effects.INVISIBILITY, 30 * 20, 0, false, true));
         }
      }

      public void stop() {
         if (this.enderman.veryHardmode()) {
            if (this.pendingTarget != null) {
               this.enderman.teleportBehind(this.pendingTarget);
            }
            this.enderman.removeEffect(Effects.INVISIBILITY);
         }
         this.pendingTarget = null;
         super.stop();
      }

      public boolean canContinueToUse() {
         if (this.pendingTarget != null) {
            // Check if the stare grace period is still active
            if (!this.enderman.isLookingAtMe(this.pendingTarget) && --this.stareGracePeriod <= 0) {
               return false;
            } else {
               this.enderman.lookAt(this.pendingTarget, 10.0F, 10.0F);
               return true;
            }
         } else {
            return this.target != null && this.continueAggroTargetConditions.test(this.enderman, this.target) ? true : super.canContinueToUse();
         }
      }

      public void tick() {
         if (this.enderman.getTarget() == null) {
            super.setTarget((LivingEntity) null);
         }

         if (this.pendingTarget != null) {
            if (--this.aggroTime <= 0) {
               this.target = this.pendingTarget;
               this.pendingTarget = null;
               super.start();
            }
         } else {
            if (this.target != null && !this.enderman.isPassenger()) {
               if (this.enderman.isLookingAtMe((PlayerEntity) this.target)) {
                  if (this.target.distanceToSqr(this.enderman) < 16.0D) {
                     this.enderman.teleport();
                  }

                  this.teleportTime = 0;
               } else if (this.target.distanceToSqr(this.enderman) > 256.0D && this.teleportTime++ >= 30
                       && (this.enderman.teleportTowards(this.target) || this.enderman.teleportBehind(this.target) && this.enderman.veryHardmode())) {
                  this.teleportTime = 0;
               }
            }
            super.tick();
         }
      }
   }


   static class PlaceBlockGoal extends Goal {
      private final EndermanEntity enderman;

      public PlaceBlockGoal(EndermanEntity p_i45843_1_) {
         this.enderman = p_i45843_1_;
      }

      public boolean canUse() {
         if (this.enderman.getCarriedBlock() == null) {
            return false;
         } else if (!this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
         } else {
            return this.enderman.getRandom().nextInt(this.enderman.getTarget() != null && this.enderman.veryHardmode() ? 150 : 2000) == 0;
         }
      }

      public void tick() {
         Random random = this.enderman.getRandom();
         World world = this.enderman.level;
         int i = MathHelper.floor(this.enderman.getX() - 1.0D + random.nextDouble() * 2.0D);
         int j = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 2.0D);
         int k = MathHelper.floor(this.enderman.getZ() - 1.0D + random.nextDouble() * 2.0D);
         BlockPos blockpos = new BlockPos(i, j, k);
         BlockState blockstate = world.getBlockState(blockpos);
         BlockPos blockpos1 = blockpos.below();
         BlockState blockstate1 = world.getBlockState(blockpos1);
         BlockState blockstate2 = this.enderman.getCarriedBlock();
         if (blockstate2 != null) {
            blockstate2 = Block.updateFromNeighbourShapes(blockstate2, this.enderman.level, blockpos);
            if (this.canPlaceBlock(world, blockpos, blockstate2, blockstate, blockstate1, blockpos1)) {
               world.setBlock(blockpos, blockstate2, 3);
               this.enderman.setCarriedBlock((BlockState)null);
            }

         }
      }

      private boolean canPlaceBlock(World p_220836_1_, BlockPos p_220836_2_, BlockState p_220836_3_, BlockState p_220836_4_, BlockState p_220836_5_, BlockPos p_220836_6_) {
         return p_220836_4_.isAir() && !p_220836_5_.isAir() && !p_220836_5_.is(Blocks.BEDROCK) && p_220836_5_.isCollisionShapeFullBlock(p_220836_1_, p_220836_6_) && p_220836_3_.canSurvive(p_220836_1_, p_220836_2_) && p_220836_1_.getEntities(this.enderman, AxisAlignedBB.unitCubeFromLowerCorner(Vector3d.atLowerCornerOf(p_220836_2_))).isEmpty();
      }
   }

   static class StareGoal extends Goal {
      private final EndermanEntity enderman;
      private LivingEntity target;

      public StareGoal(EndermanEntity p_i50520_1_) {
         this.enderman = p_i50520_1_;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean canUse() {
         this.target = this.enderman.getTarget();
         if (!(this.target instanceof PlayerEntity)) {
            return false;
         } else {
            double d0 = this.target.distanceToSqr(this.enderman);
            return d0 > 256.0D ? false : this.enderman.isLookingAtMe((PlayerEntity)this.target);
         }
      }

      public void start() {
         this.enderman.getNavigation().stop();
      }

      public void tick() {
         this.enderman.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
      }
   }

   static class TakeBlockGoal extends Goal {
      private final EndermanEntity enderman;

      public TakeBlockGoal(EndermanEntity p_i45841_1_) {
         this.enderman = p_i45841_1_;
      }

      public boolean canUse() {
         if (this.enderman.getCarriedBlock() != null) {
            return false;
         } else if (!this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
         } else {
            return this.enderman.getRandom().nextInt(this.enderman.veryHardmode() ? 5 : 20) == 0;
         }
      }

      public void tick() {
         Random random = this.enderman.getRandom();
         World world = this.enderman.level;
         int i = MathHelper.floor(this.enderman.getX() - 2.0D + random.nextDouble() * 4.0D);
         int j = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 3.0D);
         int k = MathHelper.floor(this.enderman.getZ() - 2.0D + random.nextDouble() * 4.0D);
         BlockPos blockpos = new BlockPos(i, j, k);
         BlockState blockstate = world.getBlockState(blockpos);
         Block block = blockstate.getBlock();
         Vector3d vector3d = new Vector3d((double)MathHelper.floor(this.enderman.getX()) + 0.5D, (double)j + 0.5D, (double)MathHelper.floor(this.enderman.getZ()) + 0.5D);
         Vector3d vector3d1 = new Vector3d((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D);
         BlockRayTraceResult blockraytraceresult = world.clip(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, this.enderman));
         boolean flag = blockraytraceresult.getBlockPos().equals(blockpos);
         if (block.is(BlockTags.ENDERMAN_HOLDABLE) && flag) {
            world.removeBlock(blockpos, false);
            this.enderman.setCarriedBlock(blockstate.getBlock().defaultBlockState());
         }

      }
   }

   public void tick() {
      super.tick();
      if (this.getAttackTicks() > 0) {
         this.setAttackTicks(this.getAttackTicks() - 1);
      }
      if (this.hurtTime > 0) {
         this.setHurtTicks(this.hurtTime);
      }
//      if (this.getHurtTicks() > 0) {
//         this.setHurtTicks(this.getHurtTicks() - 1);
//      }
      if (this.level.isClientSide) setupAnimations();
   }

   public void setupAnimations() {
      this.holdingBlockState.animateWhen(this.getCarriedBlock() != null, this.tickCount);
      this.attackState.animateWhen(this.getAttackTicks() > 0, this.tickCount);
      this.hurtState.animateWhen(this.getHurtTicks() > 0, this.tickCount);
   }

   public boolean doHurtTarget(Entity entity) {
      this.setAttackTicks(8);
      return super.doHurtTarget(entity);
   }
}