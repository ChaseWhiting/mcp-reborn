package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.Random;

public class PaleGardenBatEntity extends BatEntity {
   private static final DataParameter<Boolean> DATA_RESTING = EntityDataManager.defineId(PaleGardenBatEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> RESTING_TIME = EntityDataManager.defineId(PaleGardenBatEntity.class, DataSerializers.INT);
   private static final DataParameter<Integer> RESTING_COOLDOWN = EntityDataManager.defineId(PaleGardenBatEntity.class, DataSerializers.INT);


   private BlockPos targetPosition;
   public AnimationState flyingAnimationState = new AnimationState();
   public AnimationState restingAnimationState = new AnimationState();
   private RangedInteger restCooldownTime = TickRangeConverter.rangeOfSeconds(20, 40);
   private RangedInteger timeToRest = TickRangeConverter.rangeOfSeconds(16, 40);

   public PaleGardenBatEntity(EntityType<? extends PaleGardenBatEntity> entityType, World world) {
      super(entityType, world);
     this.setResting(true); // Start resting by default
   }

   public void setRestingCooldown(int cooldown) {
      this.entityData.set(RESTING_COOLDOWN, cooldown);
   }

   public int getRestingCooldown() {
      return entityData.get(RESTING_COOLDOWN);
   }

   public void setRestingTime(int time) {
      this.entityData.set(RESTING_TIME, time);
   }

   public int getRestingTime() {
      return entityData.get(RESTING_TIME);
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return this.isResting() && this.random.nextInt(4) != 0 ? null : SoundEvents.PALE_HANGING_MOSS_IDLE;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.PALE_HANGING_MOSS_IDLE;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PALE_HANGING_MOSS_IDLE;
   }

   protected float getVoicePitch() {
      return 1.0F;
   }

   protected float getSoundVolume() {
      return 0.5F;
   }


   public void addAdditionalSaveData(CompoundNBT nbt) {
      super.addAdditionalSaveData(nbt);
      nbt.putInt("RestTime", this.getRestingTime());
      nbt.putBoolean("Resting", this.isResting());
      nbt.putInt("RestCooldown", this.getRestingCooldown());
      if (targetPosition != null) {
         nbt.put("TargetPos", NBTUtil.writeBlockPos(this.targetPosition));
      }
   }

   public void readAdditionalSaveData(CompoundNBT nbt) {
      super.readAdditionalSaveData(nbt);
      if (nbt.contains("RestTime")) {
         this.setRestingTime(nbt.getInt("RestTime"));
         this.setResting(nbt.getBoolean("Resting"));
         this.setRestingCooldown(nbt.getInt("RestCooldown"));
      }
      if (nbt.contains("TargetPos")) {
         this.targetPosition = NBTUtil.readBlockPos(nbt.getCompound("TargetPos"));
      }
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_RESTING, true);
      this.entityData.define(RESTING_COOLDOWN, 0);
      this.entityData.define(RESTING_TIME, 200);
   }

   private void setupAnimationStates() {
      if (this.flyingAnimationState == null || this.restingAnimationState == null) {
         this.flyingAnimationState = new AnimationState();
         this.restingAnimationState = new AnimationState();
      }
      if (this.isResting() || this.getDeltaMovement() == Vector3d.ZERO) {
         this.flyingAnimationState.stop();
         this.restingAnimationState.startIfStopped(this.tickCount);
      } else {
         this.restingAnimationState.stop();
         this.flyingAnimationState.startIfStopped(this.tickCount);
      }
   }

   public void tick() {
      super.tick();
      if (getRestingCooldown() > 0) {
         this.setRestingCooldown(this.getRestingCooldown() - 1);
      }

      if (this.isResting()) {
         this.setDeltaMovement(Vector3d.ZERO);
         this.setPosRaw(this.getX(), (double) MathHelper.floor(this.getY()) + 1.0D - (double) this.getBbHeight(), this.getZ());

         if (this.getRestingTime() > 0) {
            this.setRestingTime(this.getRestingTime() - 1);
         } else {
            // Stop resting once time has elapsed
            this.setResting(false);
            this.setupAnimationStates();
            this.setRestingCooldown(restCooldownTime.randomValue(this.random));  // Set rest cooldown
         }
      } else {
         // Adjust the bat's movement when not resting
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
         manageHeightInBiome(); // Handle Y-axis logic in the biome
      }
      this.setupAnimationStates();
   }

   private void manageHeightInBiome() {
      if (this.getY() < 80 || this.getY() > 100) {
         Optional<RegistryKey<Biome>> biomeKey = this.level.getBiomeName(this.blockPosition());
         if (biomeKey.isPresent() && biomeKey.get() == Biomes.PALE_GARDEN) {
            if (this.getY() < 80) {
               this.setDeltaMovement(this.getDeltaMovement().add(0, 0.2D, 0));
            } else if (this.getY() > 100) {
               this.setDeltaMovement(this.getDeltaMovement().add(0, -0.2D, 0));
            }
         }
      }
   }

   public boolean isValidBlockState(BlockPos blockPos) {
      // Check valid resting spots (blocks)
      BlockState blockState = this.level.getBlockState(blockPos);
      return blockState.is(Blocks.PALE_OAK_LOG) || blockState.is(Blocks.PALE_OAK_LEAVES) || blockState.is(Blocks.PALE_HANGING_MOSS);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if (this.isResting()) {

         handleRestingLogic();
      } else {

         handleMovementLogic();
      }
   }

   private void handleRestingLogic() {
      BlockPos blockpos = this.blockPosition();
      BlockPos blockAbove = blockpos.above();

      if (this.getRestingTime() <= 0 || !isValidBlockState(blockAbove)) {
         this.setResting(false);
         this.setupAnimationStates();
         this.setRestingCooldown(restCooldownTime.randomValue(this.random));
         if (!this.isSilent()) {
            this.level.levelEvent(null, 1025, blockpos, 0);
         }
      }
   }

   private void handleMovementLogic() {
      // Handle wandering and resting logic when not resting
      BlockPos blockAbove = this.blockPosition().above();

      if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerThan(this.position(), 2.0D)) {
         this.targetPosition = generateRandomTargetPosition();
      }

      moveTowardsTarget();
      if (this.random.nextInt(35) == 0 && isValidBlockState(blockAbove)) {
         this.setRestingTime(timeToRest.randomValue(this.random));
         this.setResting(true);
      }
   }

   private BlockPos generateRandomTargetPosition() {
      return new BlockPos(this.getX() + this.random.nextInt(7) - this.random.nextInt(7),
              this.getY() + this.random.nextInt(6) - 2.0D,
              this.getZ() + this.random.nextInt(7) - this.random.nextInt(7));
   }

   private void moveTowardsTarget() {
      double d2 = this.targetPosition.getX() + 0.5D - this.getX();
      double d0 = this.targetPosition.getY() + 0.1D - this.getY();
      double d1 = this.targetPosition.getZ() + 0.5D - this.getZ();
      Vector3d movement = this.getDeltaMovement().add(
              (Math.signum(d2) * 0.5D - this.getDeltaMovement().x) * 0.1F,
              (Math.signum(d0) * 0.7F - this.getDeltaMovement().y) * 0.1F,
              (Math.signum(d1) * 0.5D - this.getDeltaMovement().z) * 0.1F
      );
      this.setDeltaMovement(movement);

      float f = (float) (MathHelper.atan2(movement.z, movement.x) * (180F / Math.PI)) - 90.0F;
      this.yRot = MathHelper.wrapDegrees(f - this.yRot);
      this.zza = 0.5F;
   }

   public static boolean checkSpawnRules(EntityType<PaleGardenBatEntity> entityType, IServerWorld world, SpawnReason reason, BlockPos pos, Random random) {

      return false;
//      if (pos.getY() >= 95 || pos.getY() < 83) {
//         return false;
//      } else {
//         return random.nextInt(5) == 0 && checkNearbyBlocks(world, pos);
//      }
   }

   public boolean isResting() {
      return this.entityData.get(DATA_RESTING);
   }

   public void setResting(boolean p_82236_1_) {
      this.entityData.set(DATA_RESTING, p_82236_1_);
      this.setupAnimationStates();

   }

   private static boolean isHalloween() {
      LocalDate localDate = LocalDate.now();
      int day = localDate.get(ChronoField.DAY_OF_MONTH);
      int month = localDate.get(ChronoField.MONTH_OF_YEAR);
      return (month == 10 && day >= 20) || (month == 11 && day <= 3);
   }
}
