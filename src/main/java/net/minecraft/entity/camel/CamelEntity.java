package net.minecraft.entity.camel;

import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.leashable.Leashable;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class CamelEntity extends AbstractContainerAnimalEntity {
    public static final float BABY_SCALE = 0.45f;
    public static final int DASH_COOLDOWN_TICKS = 55;
    public static final int MAX_HEAD_Y_ROT = 30;
    private static final float RUNNING_SPEED_BONUS = 0.1f;
    private static final float DASH_VERTICAL_MOMENTUM = 1.4285f;
    private static final float DASH_HORIZONTAL_MOMENTUM = 22.2222f;
    private static final int DASH_MINIMUM_DURATION_TICKS = 5;
    private static final int SITDOWN_DURATION_TICKS = 40;
    private static final int STANDUP_DURATION_TICKS = 52;
    private static final int IDLE_MINIMAL_DURATION_TICKS = 80;
    private static final float SITTING_HEIGHT_DIFFERENCE = 1.43f;
    public static final DataParameter<Boolean> DASH = EntityDataManager.defineId(CamelEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Optional<DyeColor>> CARPET_COLOUR = EntityDataManager.defineId(CamelEntity.class, DataSerializers.OPTIONAL_DYE_COLOR);
    public static final DataParameter<Long> LAST_POSE_CHANGE_TICK = EntityDataManager.defineId(CamelEntity.class, DataSerializers.LONG);
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState sitPoseAnimationState = new AnimationState();
    public final AnimationState sitUpAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState dashAnimationState = new AnimationState();
    private static final EntitySize SITTING_DIMENSIONS = EntitySize.scalable(EntityType.CAMEL.getWidth(), EntityType.CAMEL.getHeight() - 1.43f);
    private int dashCooldown = 0;
    private int idleAnimationTimeout = 0;

    public int getDashCooldown() {
        return dashCooldown;
    }

    public CamelEntity(EntityType<? extends CamelEntity> type, World world) {
        super(type, world);

        this.moveControl = new CamelMoveControl();
        this.lookControl = new CamelLookControl();
        GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.getNavigation();
        groundPathNavigator.setCanFloat(true);
        this.maxUpStep = 1.5f;
    }

    @OnlyIn(Dist.CLIENT)
    public float getEyeHeight(Pose p_213307_1_) {
        return 2.275f;
    }

    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putLong("LastPoseTick", this.entityData.get(LAST_POSE_CHANGE_TICK));

        if (this.getCarpetColor().isPresent()) {
            nbt.putString("CarpetColor", this.getCarpetColor().get().getName());
        }
    }


    @Override
    public void onElasticLeashPull() {
        super.onElasticLeashPull();
        if (this.isCamelSitting() && !this.isInPoseTransition() && this.canCamelChangePose()) {
            this.standUp();
        }
    }

    @Override
    public Vector3d[] getQuadLeashOffsets() {
        return Leashable.createQuadLeashOffsets(this, 0.02, 0.48, 0.25, 0.82);
    }



    public static boolean checkCamelSpawnRules(EntityType<? extends CamelEntity> entityType, IWorld levelAccessor, SpawnReason mobSpawnType, BlockPos blockPos, Random randomSource) {
        return levelAccessor.getRawBrightness(blockPos, 0) > 8 && levelAccessor.getBlockState(blockPos.below()).is(List.of(Blocks.SAND));
    }

    protected float getSoundVolume() {
        return 0.8F;
    }

    public int getAmbientSoundInterval() {
        return 400;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        long l = compoundTag.getLong("LastPoseTick");
        if (l < 0L) {
            this.setPose(Pose.SITTING);
        }
        this.resetLastPoseChangeTick(l);

        if (compoundTag.contains("CarpetColor")) {
            this.entityData.set(CARPET_COLOUR, Optional.of(DyeColor.byName(compoundTag.getString("CarpetColor"), DyeColor.RED)));
        }
    }

    @Override
    public Vector3d[] getVisualChestLocations() {
        AxisAlignedBB bb = this.getBoundingBox().inflate(0.35D);
        Vector3d center = bb.getCenter();

        float yaw = this.yBodyRot;
        double rad = Math.toRadians(yaw);

        double offset = 0.35;
        double[][] localOffsets = new double[][]{
                { offset,  offset}, // front-right
                {-offset,  offset}, // front-left
                { offset, -offset}, // back-right
                {-offset, -offset}, // back-left
        };

        Vector3d[] result = new Vector3d[4];
        for (int i = 0; i < 4; i++) {
            double xOff = localOffsets[i][0];
            double zOff = localOffsets[i][1];

            double rotatedX = xOff * Math.cos(rad) - zOff * Math.sin(rad);
            double rotatedZ = xOff * Math.sin(rad) + zOff * Math.cos(rad);

            result[i] = new Vector3d(
                    center.x + rotatedX,
                    center.y + 0.15,
                    center.z + rotatedZ
            );
        }
        return result;
    }

    public Vector3d getCarpetDismountLocation() {
        return new Vector3d(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }



    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return HorseEntity.createBaseHorseAttributes().add(Attributes.MAX_HEALTH, 32.0).add(Attributes.MOVEMENT_SPEED, 0.09f).add(Attributes.JUMP_STRENGTH, 0.42f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DASH, false);
        entityData.define(LAST_POSE_CHANGE_TICK, 0L);
        this.entityData.define(CARPET_COLOUR, Optional.empty());
    }

    @Override
    public @Nullable ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        CamelAi.initMemories(this, p_213386_1_.getRandom());
        this.resetLastPoseChangeTickToFullStand(p_213386_1_.getLevel().getGameTime());

        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }


    protected Brain.BrainCodec<CamelEntity> brainProvider() {
        return CamelAi.brainProvider();
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return CamelAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    public Brain<CamelEntity> getBrain() {
        return (Brain<CamelEntity>) super.getBrain();
    }

    @Override
    public EntitySize getDimensions(Pose pose) {
        return pose == Pose.SITTING ? SITTING_DIMENSIONS.scale(this.getScale()) : super.getDimensions(pose);
    }

    public float getScale() {
        return this.isBaby() ? BABY_SCALE : 1.0F;
    }

    @Override
    protected void customServerAiStep() {
        IProfiler profilerFiller = level.getProfiler();
        profilerFiller.push("camelBrain");
        Brain<CamelEntity> brain = this.getBrain();
        brain.tick((ServerWorld)level, this);
        profilerFiller.pop();
        profilerFiller.push("camelActivityUpdate");
        CamelAi.updateActivity(this);
        profilerFiller.pop();
        super.customServerAiStep();
    }

    public boolean isDashing() {
        return this.entityData.get(DASH);
    }

    public void setDashing(boolean bl) {
        this.entityData.set(DASH, bl);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isDashing() && this.dashCooldown < 55 && (this.onGround || this.isInWater())) {
            this.setDashing(false);
        }
        if (this.dashCooldown > 0) {
            --this.dashCooldown;
            if (this.dashCooldown == 0) {
                this.level.playSound(null, this.blockPosition(), SoundEvents.CAMEL_DASH_READY, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
        if (this.level.isClientSide()) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
        switch (this.getPose()) {
            case STANDING: {
                this.sitAnimationState.stop();
                this.sitPoseAnimationState.stop();
                this.dashAnimationState.animateWhen(this.isDashing(), this.tickCount);
                this.sitUpAnimationState.animateWhen(this.isInPoseTransition(), this.tickCount);
                this.walkAnimationState.animateWhen(this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6, this.tickCount);
                break;
            }
            case SITTING: {
                this.walkAnimationState.stop();
                this.sitUpAnimationState.stop();
                this.dashAnimationState.stop();
                if (this.isCamelSitting()) {
                    this.sitAnimationState.startIfStopped(this.tickCount);
                    this.sitPoseAnimationState.stop();
                    break;
                }
                this.sitAnimationState.stop();
                this.sitPoseAnimationState.startIfStopped(this.tickCount);
                break;
            }
            default: {
                this.walkAnimationState.stop();
                this.sitAnimationState.stop();
                this.sitPoseAnimationState.stop();
                this.sitUpAnimationState.stop();
                this.dashAnimationState.stop();
            }
        }
    }

    protected void clampHeadRotationToBody() {
        float f = this.getMaxHeadYRot();
        float f2 = this.getYHeadRot();
        float f3 = MathHelper.wrapDegrees(this.yBodyRot - f2);
        float f4 = MathHelper.clamp(MathHelper.wrapDegrees(this.yBodyRot - f2), -f, f);
        float f5 = f2 + f3 - f4;
        this.setYHeadRot(f5);
    }


    public Optional<DyeColor> getCarpetColor() {
        return this.entityData.get(CARPET_COLOUR);
    }


    @Override
    public void travel(Vector3d vec3) {
        if (this.refuseToMove() && this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0, 1.0, 0.0));
            vec3 = vec3.multiply(0.0, 1.0, 0.0);
        }
        super.travel(vec3);
    }

    public boolean refuseToMove() {
        return this.isCamelSitting() || this.isInPoseTransition();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CAMEL_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CAMEL_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.CAMEL_HURT;
    }


    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        if (blockState.is(List.of(Blocks.SAND, Blocks.RED_SAND))) {
            this.playSound(SoundEvents.CAMEL_STEP_SAND, 1.0f, 1.0f);
        } else {
            this.playSound(SoundEvents.CAMEL_STEP, 1.0f, 1.0f);
        }
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.get() == Items.CACTUS_FLOWER || itemStack.get() == Items.CHORUS_FRUIT || itemStack.get() == Items.CHORUS_FLOWER;
    }

    public boolean canCamelChangePose() {
        return this.wouldNotSuffocateAtTargetPose(this.isCamelSitting() ? Pose.STANDING : Pose.SITTING);
    }

    protected boolean wouldNotSuffocateAtTargetPose(Pose pose) {
        AxisAlignedBB aABB = this.getDimensions(pose).makeBoundingBox(this.position());
        return this.level().noBlockCollision(this, aABB, (b, p ) -> true);
    }

    @Override
    @Nullable
    public CamelEntity getBreedOffspring(ServerWorld serverLevel, AgeableEntity ageableMob) {
        return EntityType.CAMEL.create(serverLevel);
    }

    public boolean hurt(DamageSource source, float damage) {
        boolean hurt = super.hurt(source, damage);

        if (hurt) {
            this.standUpInstantly();
            return true;
        }
        return false;
    }

    @Override
    public Vector3d getLeashOffset() {
        return new Vector3d(0.0D,
                this.getBodyAnchorAnimationYOffset(true, getEyeHeight(), this.getDimensions(this.getPose()), this.getScale()) - 0.2f * getScale(),
                            this.getDimensions(this.getPose()).width * 0.56f);
    }


    @Override
    public Vector3d getLeashOffset(float f) {
        EntitySize entityDimensions = this.getDimensions(this.getPose());
        float f2 = this.getScale();
        return new Vector3d(0.0, this.getBodyAnchorAnimationYOffset(true, f, entityDimensions, f2) - (double)(0.2f * f2), entityDimensions.width * 0.56f);
    }


    private double getBodyAnchorAnimationYOffset(boolean bl, float f, EntitySize entityDimensions, float f2) {
        double d = entityDimensions.height - 0.375f * f2;
        float f3 = f2 * 1.43f;
        float f4 = f3 - f2 * 0.2f;
        float f5 = f3 - f4;
        boolean bl2 = this.isInPoseTransition();
        boolean bl3 = this.isCamelSitting();
        if (bl2) {
            float f6;
            int n;
            int n2;
            int n3 = n2 = bl3 ? 40 : 52;
            if (bl3) {
                n = 28;
                f6 = bl ? 0.5f : 0.1f;
            } else {
                n = bl ? 24 : 32;
                f6 = bl ? 0.6f : 0.35f;
            }
            float f7 = MathHelper.clamp((float)this.getPoseTime() + f, 0.0f, (float)n2);
            boolean bl4 = f7 < (float)n;
            float f8 = bl4 ? f7 / (float)n : (f7 - (float)n) / (float)(n2 - n);
            float f9 = f3 - f6 * f4;
            d += bl3 ? (double)MathHelper.lerp(f8, bl4 ? f3 : f9, bl4 ? f9 : f5) : (double)MathHelper.lerp(f8, bl4 ? f5 - f3 : f5 - f9, bl4 ? f5 - f9 : 0.0f);
        }
        if (bl3 && !bl2) {
            d += (double)f5;
        }
        return d;
    }

    @Override
    public int getMaxHeadYRot() {
        return 30;
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPacketSender.sendEntityBrain(this);
    }

    public boolean isCamelSitting() {
        return this.entityData.get(LAST_POSE_CHANGE_TICK) < 0L;
    }

    public boolean isPanicking() {
        if (this.brain.hasMemoryValue(MemoryModuleType.IS_PANICKING)) {
            return this.brain.getMemory(MemoryModuleType.IS_PANICKING).isPresent();
        }
        return false;
    }

    public boolean isCamelVisuallySitting() {
        return this.getPoseTime() < 0L != this.isCamelSitting();
    }

    public boolean isInPoseTransition() {
        long l = this.getPoseTime();
        return l < (long)(this.isCamelSitting() ? 40 : 52);
    }

    private boolean isVisuallySittingDown() {
        return this.isCamelSitting() && this.getPoseTime() < 40L && this.getPoseTime() >= 0L;
    }

    public void sitDown() {
        if (this.isCamelSitting()) {
            return;
        }
        this.makeSound(SoundEvents.CAMEL_SIT);
        this.setPose(Pose.SITTING);
        this.resetLastPoseChangeTick(-this.level.getGameTime());
    }

    public void standUp() {
        if (!this.isCamelSitting()) {
            return;
        }
        this.makeSound(SoundEvents.CAMEL_STAND);
        this.setPose(Pose.STANDING);
        this.resetLastPoseChangeTick(this.level.getGameTime());
    }

    public void standUpInstantly() {
        this.setPose(Pose.STANDING);
        this.resetLastPoseChangeTickToFullStand(this.level.getGameTime());
    }


    public void resetLastPoseChangeTick(long l) {
        this.entityData.set(LAST_POSE_CHANGE_TICK, l);
    }

    private void resetLastPoseChangeTickToFullStand(long l) {
        this.resetLastPoseChangeTick(Math.max(0L, l - 52L - 1L));
    }

    public long getPoseTime() {
        return this.level.getGameTime() - Math.abs(this.entityData.get(LAST_POSE_CHANGE_TICK));
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> entityDataAccessor) {
        if (!this.firstTick && DASH.equals(entityDataAccessor)) {
            this.dashCooldown = this.dashCooldown == 0 ? 55 : this.dashCooldown;
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    protected BodyController createBodyControl() {
        return new CamelBodyRotationControl(this);
    }

    class CamelMoveControl
            extends MovementController {
        public CamelMoveControl() {
            super(CamelEntity.this);
        }

        @Override
        public void tick() {
            if (this.operation == MovementController.Action.MOVE_TO && !CamelEntity.this.isLeashed() && CamelEntity.this.isCamelSitting() && !CamelEntity.this.isInPoseTransition() && CamelEntity.this.canCamelChangePose()) {
                CamelEntity.this.standUp();
            }
            super.tick();
        }
    }

    class CamelLookControl
            extends LookController {
        CamelLookControl() {
            super(CamelEntity.this);
        }

        @Override
        public void tick() {
            if (!CamelEntity.this.hasOnePlayerPassenger()) {
                super.tick();
            }
        }
    }

    class CamelBodyRotationControl
            extends BodyController {
        public CamelBodyRotationControl(CamelEntity camel2) {
            super(camel2);
        }

        @Override
        public void clientTick() {
            if (!CamelEntity.this.refuseToMove()) {
                super.clientTick();
            }
        }
    }

}
