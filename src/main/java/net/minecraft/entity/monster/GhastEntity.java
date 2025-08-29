package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.happy_ghast.HappyGhastEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.random.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class GhastEntity extends FlyingEntity implements IMob {
   private static final DataParameter<Boolean> DATA_IS_CHARGING = EntityDataManager.defineId(GhastEntity.class, DataSerializers.BOOLEAN);
   private int explosionPower = 1;

   public GhastEntity(EntityType<? extends GhastEntity> p_i50206_1_, World p_i50206_2_) {
      super(p_i50206_1_, p_i50206_2_);
      this.xpReward = 5;
      this.moveControl = new GhastEntity.MoveHelperController(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(5, new GhastEntity.RandomFlyGoal(this));
      this.goalSelector.addGoal(7, new GhastEntity.LookAroundGoal(this));
      this.goalSelector.addGoal(7, new GhastEntity.FireballAttackGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, (p_213812_1_) -> {
         return Math.abs(p_213812_1_.getY() - this.getY()) <= 4.0D;
      }));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, VillagerEntity.class, 10, true, false, (p_213812_1_) -> {
         return true;
      }));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, 10, true, false, (p_213812_1_) -> {
         return true;
      }));
      this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Animal.class, 10, true, false, (p_213812_1_) -> {
         return !(p_213812_1_ instanceof HoglinEntity) && !(p_213812_1_ instanceof ZoglinEntity) && !(p_213812_1_ instanceof HappyGhastEntity);
      }));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isCharging() {
      return this.entityData.get(DATA_IS_CHARGING);
   }

   public void setCharging(boolean p_175454_1_) {
      this.entityData.set(DATA_IS_CHARGING, p_175454_1_);
   }

   public int getExplosionPower() {
      return this.explosionPower;
   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (p_70097_1_.getDirectEntity() instanceof FireballEntity && p_70097_1_.getEntity() instanceof PlayerEntity) {
         super.hurt(p_70097_1_, 1000.0F);
         return true;
      } else {
         return super.hurt(p_70097_1_, p_70097_2_);
      }
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_IS_CHARGING, false);
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FOLLOW_RANGE, 100.0D);
   }

   public SoundCategory getSoundSource() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.GHAST_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.GHAST_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.GHAST_DEATH;
   }

   protected float getSoundVolume() {
      return 5.0F;
   }

   public static boolean checkGhastSpawnRules(EntityType<GhastEntity> p_223368_0_, IWorld p_223368_1_, SpawnReason p_223368_2_, BlockPos p_223368_3_, Random p_223368_4_) {
      return p_223368_1_.getDifficulty() != Difficulty.PEACEFUL && p_223368_4_.nextInt(20) == 0 && checkMobSpawnRules(p_223368_0_, p_223368_1_, p_223368_2_, p_223368_3_, p_223368_4_);
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("ExplosionPower", this.explosionPower);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("ExplosionPower", 99)) {
         this.explosionPower = p_70037_1_.getInt("ExplosionPower");
      }

   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 2.6F;
   }

   static class FireballAttackGoal extends Goal {
      private final GhastEntity ghast;
      public int chargeTime;

      public FireballAttackGoal(GhastEntity p_i45837_1_) {
         this.ghast = p_i45837_1_;
      }

      public boolean canUse() {
         return this.ghast.getTarget() != null;
      }

      public void start() {
         this.chargeTime = 0;
      }

      public void stop() {
         this.ghast.setCharging(false);
      }

      public void tick() {
         LivingEntity livingentity = this.ghast.getTarget();
         double d0 = 64.0D;
         double b = ghast.veryHardmode() ? 8192D : 4096D;
         if (livingentity.distanceToSqr(this.ghast) < b && (this.ghast.canSee(livingentity) || ghast.veryHardmode())) {
            World world = this.ghast.level;
            ++this.chargeTime;
            if (this.chargeTime == 10 && !this.ghast.isSilent()) {
               world.levelEvent((PlayerEntity)null, 1015, this.ghast.blockPosition(), 0);
            }

            if (this.chargeTime == 20) {
               double d1 = 4.0D;
               Vector3d vector3d = this.ghast.getViewVector(1.0F);
               double d2 = livingentity.getX() - (this.ghast.getX() + vector3d.x * 4.0D);
               double d3 = livingentity.getY(0.5D) - (0.5D + this.ghast.getY(0.5D));
               double d4 = livingentity.getZ() - (this.ghast.getZ() + vector3d.z * 4.0D);
               if (!this.ghast.isSilent()) {
                  world.levelEvent((PlayerEntity)null, 1016, this.ghast.blockPosition(), 0);
               }

               FireballEntity fireballentity = new FireballEntity(world, this.ghast, d2, d3, d4);
               fireballentity.explosionPower = this.ghast.getExplosionPower();
               fireballentity.setPos(this.ghast.getX() + vector3d.x * 4.0D, this.ghast.getY(0.5D) + 0.5D, fireballentity.getZ() + vector3d.z * 4.0D);
               world.addFreshEntity(fireballentity);
               this.chargeTime = -40;
            }
         } else if (this.chargeTime > 0) {
            --this.chargeTime;
         }

         this.ghast.setCharging(this.chargeTime > 10);
      }
   }

   static class LookAroundGoal extends Goal {
      private final GhastEntity ghast;

      public LookAroundGoal(GhastEntity p_i45839_1_) {
         this.ghast = p_i45839_1_;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         if (this.ghast.getTarget() == null) {
            Vector3d vector3d = this.ghast.getDeltaMovement();
            this.ghast.yRot = -((float)MathHelper.atan2(vector3d.x, vector3d.z)) * (180F / (float)Math.PI);
            this.ghast.yBodyRot = this.ghast.yRot;
         } else {
            LivingEntity livingentity = this.ghast.getTarget();
            double d0 = 64.0D;
            if (livingentity.distanceToSqr(this.ghast) < 4096.0D) {
               double d1 = livingentity.getX() - this.ghast.getX();
               double d2 = livingentity.getZ() - this.ghast.getZ();
               this.ghast.yRot = -((float)MathHelper.atan2(d1, d2)) * (180F / (float)Math.PI);
               this.ghast.yBodyRot = this.ghast.yRot;
            }
         }

      }
   }

   static class MoveHelperController extends MovementController {
      private final GhastEntity ghast;
      private int floatDuration;

      public MoveHelperController(GhastEntity p_i45838_1_) {
         super(p_i45838_1_);
         this.ghast = p_i45838_1_;
      }

      public void tick() {
         if (this.operation == MovementController.Action.MOVE_TO) {
            if (this.floatDuration-- <= 0) {
               this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
               Vector3d vector3d = new Vector3d(this.wantedX - this.ghast.getX(), this.wantedY - this.ghast.getY(), this.wantedZ - this.ghast.getZ());
               double d0 = vector3d.length();
               vector3d = vector3d.normalize();
               if (this.canReach(vector3d, MathHelper.ceil(d0))) {
                  this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(vector3d.scale(0.1D)));
               } else {
                  this.operation = MovementController.Action.WAIT;
               }
            }

         }
      }

      private boolean canReach(Vector3d p_220673_1_, int p_220673_2_) {
         AxisAlignedBB axisalignedbb = this.ghast.getBoundingBox();

         for(int i = 1; i < p_220673_2_; ++i) {
            axisalignedbb = axisalignedbb.move(p_220673_1_);
            if (!this.ghast.level.noCollision(this.ghast, axisalignedbb)) {
               return false;
            }
         }

         return true;
      }
   }

   static class RandomFlyGoal extends Goal {
      private final GhastEntity ghast;

      public RandomFlyGoal(GhastEntity p_i45836_1_) {
         this.ghast = p_i45836_1_;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         MovementController movementcontroller = this.ghast.getMoveControl();
         if (!movementcontroller.hasWanted()) {
            return true;
         } else {
            double d0 = movementcontroller.getWantedX() - this.ghast.getX();
            double d1 = movementcontroller.getWantedY() - this.ghast.getY();
            double d2 = movementcontroller.getWantedZ() - this.ghast.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         Random random = this.ghast.getRandom();
         double d0 = this.ghast.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double d1 = this.ghast.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double d2 = this.ghast.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
         this.ghast.getMoveControl().setWantedPosition(d0, d1, d2, 1.0D);
      }
   }


   public static class RandomFloatAroundGoal
           extends Goal {
      private static final int MAX_ATTEMPTS = 64;
      private final Mob ghast;
      private final int distanceToBlocks;

      public RandomFloatAroundGoal(Mob mob) {
         this(mob, 0);
      }

      public RandomFloatAroundGoal(Mob mob, int n) {
         this.ghast = mob;
         this.distanceToBlocks = n;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      @Override
      public boolean canUse() {
         if (!ghast.getPassengers().isEmpty()) return false;

         double d;
         double d2;
         MovementController moveControl = this.ghast.getMoveControl();
         if (!moveControl.hasWanted()) {
            return true;
         }
         double d3 = moveControl.getWantedX() - this.ghast.getX();
         double d4 = d3 * d3 + (d2 = moveControl.getWantedY() - this.ghast.getY()) * d2 + (d = moveControl.getWantedZ() - this.ghast.getZ()) * d;
         return d4 < 1.0 || d4 > 3600.0;
      }

      @Override
      public boolean canContinueToUse() {
         return false;
      }

      @Override
      public void start() {
         Vector3d vec3 = RandomFloatAroundGoal.getSuitableFlyToPosition(this.ghast, this.distanceToBlocks);
         this.ghast.getMoveControl().setWantedPosition(vec3.x(), vec3.y(), vec3.z(), 1.0);
      }

      public static Vector3d getSuitableFlyToPosition(Mob mob, int n) {
         BlockPos blockPos;
         int n2;
         World level = mob.level();
         RandomSource randomSource = RandomSource.create((RandomSource.create(mob.getRandom().nextLong()).forkPositional().at(mob.blockPosition()).nextLong()));
         Vector3d vec3 = mob.position();
         Vector3d vec32 = null;
         for (int i = 0; i < 64; ++i) {
            vec32 = RandomFloatAroundGoal.chooseRandomPositionWithRestriction(mob, vec3, randomSource);
            if (vec32 == null || !RandomFloatAroundGoal.isGoodTarget(level, vec32, n)) continue;
            return vec32;
         }
         if (vec32 == null) {
            vec32 = RandomFloatAroundGoal.chooseRandomPosition(vec3, randomSource);
         }
         if ((n2 = level.getHeight(Heightmap.Type.MOTION_BLOCKING, (blockPos = BlockPos.containing(vec32)).getX(), blockPos.getZ())) < blockPos.getY() && n2 > 0) {
            vec32 = new Vector3d(vec32.x(), mob.getY() - Math.abs(mob.getY() - vec32.y()), vec32.z());
         }
         return vec32;
      }

      private static boolean isGoodTarget(World level, Vector3d vec3, int n) {
         if (n <= 0) {
            return true;
         }
         BlockPos blockPos = BlockPos.containing(vec3);
         if (!level.getBlockState(blockPos).isAir()) {
            return false;
         }
         for (Direction direction : Direction.values()) {
            for (int i = 1; i < n; ++i) {
               BlockPos blockPos2 = blockPos.relative(direction, i);
               if (level.getBlockState(blockPos2).isAir()) continue;
               return true;
            }
         }
         return false;
      }

      private static Vector3d chooseRandomPosition(Vector3d vec3, RandomSource randomSource) {
         double d = vec3.x() + (double)((randomSource.nextFloat() * 2.0f - 1.0f) * 16.0f);
         double d2 = vec3.y() + (double)((randomSource.nextFloat() * 2.0f - 1.0f) * 16.0f);
         double d3 = vec3.z() + (double)((randomSource.nextFloat() * 2.0f - 1.0f) * 16.0f);
         return new Vector3d(d, d2, d3);
      }

      @Nullable
      private static Vector3d chooseRandomPositionWithRestriction(Mob mob, Vector3d vec3, RandomSource randomSource) {
         Vector3d vec32 = RandomFloatAroundGoal.chooseRandomPosition(vec3, randomSource);
         if (mob.hasHome() && !mob.isWithinHome(vec32)) {
            return null;
         }
         return vec32;
      }
   }



   public static class GhastMoveControl
           extends MovementController {
      private final Mob ghast;
      private int floatDuration;
      private final boolean careful;
      private final Predicate<Mob> shouldBeStopped;

      public GhastMoveControl(Mob mob, boolean bl, Predicate<Mob> booleanSupplier) {
         super(mob);
         this.ghast = mob;
         this.careful = bl;
         this.shouldBeStopped = booleanSupplier;
      }

      @Override
      public void tick() {
         if (this.shouldBeStopped.test(mob)) {
            this.operation = MovementController.Action.WAIT;
            this.ghast.stopInPlace();
         }
         if (this.operation != MovementController.Action.MOVE_TO) {
            return;
         }
         if (this.floatDuration-- <= 0) {
            this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
            Vector3d vec3 = new Vector3d(this.wantedX - this.ghast.getX(), this.wantedY - this.ghast.getY(), this.wantedZ - this.ghast.getZ());
            if (this.canReach(vec3)) {
               this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(vec3.normalize().scale(this.ghast.getAttributeValue(Attributes.FLYING_SPEED) * 5.0 / 3.0)));
            } else {
               this.operation = MovementController.Action.WAIT;
            }
         }
      }

      private boolean canReach(Vector3d vec3) {
         AxisAlignedBB aABB = this.ghast.getBoundingBox();
         AxisAlignedBB aABB2 = aABB.move(vec3);
         if (this.careful) {
            for (BlockPos blockPos2 : BlockPos.betweenClosed(aABB2.inflate(1.0))) {
               if (this.blockTraversalPossible(this.ghast.level(), null, null, blockPos2, false, false)) continue;
               return false;
            }
         }
         boolean bl = this.ghast.isInWater();
         boolean bl2 = this.ghast.isInLava();
         Vector3d vec32 = this.ghast.position();
         Vector3d vec33 = vec32.add(vec3);
         return IWorld.forEachBlockIntersectedBetween(vec32, vec33, aABB2, (blockPos, n) -> {
            if (aABB.intersects(blockPos)) {
               return true;
            }
            return this.blockTraversalPossible(this.ghast.level, vec32, vec33, blockPos, bl, bl2);
         });
      }

      private boolean blockTraversalPossible(IWorld world, @Nullable Vector3d start, @Nullable Vector3d end, BlockPos pos, boolean allowWater, boolean allowLava) {
         BlockState blockState = world.getBlockState(pos);

         if (blockState.isAir()) {
            return true;
         }

         boolean hasVectors = start != null && end != null;

         boolean noCollision;
         if (hasVectors) {
            // Check if the Ghast can move through the block without colliding with its shape
            noCollision = !this.ghast.collidedWithShapeMovingFrom(
                    start,
                    end,
                    blockState.getCollisionShape(world, pos).move(new Vector3d(pos)).toAabbs()
            );
         } else {
            // No vectors given, so just check if the block has an empty collision shape
            noCollision = blockState.getCollisionShape(world, pos).isEmpty();
         }

         if (!this.careful) {
            return noCollision;
         }

         // Unsafe blocks the Ghast should avoid
         if (blockState.is(List.of(Blocks.CACTUS, Blocks.FIRE, Blocks.MAGMA_BLOCK, Blocks.SWEET_BERRY_BUSH, Blocks.WITHER_ROSE))) {
            return false;
         }

         FluidState fluidState = world.getFluidState(pos);

         if (!fluidState.isEmpty()) {
            // If vectors are given and there's no collision with fluid, continue
            if (!(hasVectors && !this.ghast.collidedWithFluid(fluidState, pos, start, end))) {
               if (fluidState.is(FluidTags.WATER)) {
                  return allowWater;
               }
               if (fluidState.is(FluidTags.LAVA)) {
                  return allowLava;
               }
            }
         }

         return noCollision;
      }

   }
}