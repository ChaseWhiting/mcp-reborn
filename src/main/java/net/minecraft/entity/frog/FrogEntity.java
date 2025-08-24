package net.minecraft.entity.frog;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.SmoothSwimmingMoveControl;
import net.minecraft.entity.axolotl.AxolotlEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;

public class FrogEntity extends Animal implements WarmColdVariantHolder {
    public static final Ingredient TEMPTATION_ITEM = Ingredient.of(Items.SLIME_BALL);
    public static final List<Block> FROG_PREFER_JUMP_TO = List.of(Blocks.LILY_PAD);
    private static final DataParameter<WarmColdVariant> DATA_VARIANT_ID = EntityDataManager.defineId(FrogEntity.class, DataSerializers.WARM_COLD_VARIANT);
    private static final DataParameter<OptionalInt> DATA_TONGUE_TARGET_ID = EntityDataManager.defineId(FrogEntity.class, DataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final int FROG_FALL_DAMAGE_REDUCTION = 5;

    protected static final ImmutableList<SensorType<? extends Sensor<? super FrogEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.FROG_ATTACKABLES, SensorType.FROG_TEMPTATIONS, SensorType.IS_IN_WATER);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.IS_IN_WATER, MemoryModuleType.IS_PREGNANT, MemoryModuleType.IS_PANICKING, MemoryModuleType.UNREACHABLE_TONGUE_TARGETS);

    public final AnimationState jumpAnimationState = new AnimationState();
    public final AnimationState croakAnimationState = new AnimationState();
    public final AnimationState tongueAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();
    public final AnimationState swimIdleAnimationState = new AnimationState();

    public FrogEntity(EntityType<? extends Animal> entityType, World level) {
        super(entityType, level);
        this.lookControl = new FrogLookControl(this);
        this.setPathfindingMalus(PathNodeType.WATER, 4.0f);
        this.setPathfindingMalus(PathNodeType.TRAPDOOR, -1.0f);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02f, 0.1f, true);
        this.maxUpStep = 1.0f;
    }

    public static boolean checkFrogSpawnRules(EntityType<? extends FrogEntity> entityType, IWorld levelAccessor, SpawnReason mobSpawnType, BlockPos blockPos, Random randomSource) {
        return levelAccessor.getRawBrightness(blockPos, 0) > 8 && levelAccessor.getBlockState(blockPos.below()).is(List.of(Blocks.MUD, Blocks.GRASS_BLOCK));
    }

    @Override
    public void spawnChildFromBreeding(ServerWorld level, Animal animal) {
        ServerPlayerEntity player = this.getLoveCause();
        if (player == null) {
            player = animal.getLoveCause();
        }
        if (player != null) {
            player.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(player, this, animal, null);
        }
        this.setAge(6000);
        animal.setAge(6000);
        this.resetLove();
        animal.resetLove();
        this.getBrain().setMemory(MemoryModuleType.IS_PREGNANT, Unit.INSTANCE);
        level.broadcastEntityEvent(this, (byte)18);
        if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            level.addFreshEntity(new ExperienceOrbEntity(level, this.getX(), this.getY(), this.getZ(), random.nextInt(7) + 1));
        }
    }

    protected Brain.BrainCodec<FrogEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return FrogAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    public Brain<FrogEntity> getBrain() {
        return (Brain<FrogEntity>) super.getBrain();
    }


    class FrogLookControl
            extends LookController {
        FrogLookControl(Mob mob) {
            super(mob);
        }

        @Override
        protected boolean resetXRotOnTick() {
            return FrogEntity.this.getTongueTarget().isEmpty();
        }
    }

    public void eraseTongueTarget() {
        this.entityData.set(DATA_TONGUE_TARGET_ID, OptionalInt.empty());
    }

    public Optional<Entity> getTongueTarget() {
        return this.entityData.get(DATA_TONGUE_TARGET_ID).stream().mapToObj(this.level::getEntity).filter(Objects::nonNull).findFirst();
    }

    public void setTongueTarget(Entity entity) {
        this.entityData.set(DATA_TONGUE_TARGET_ID, OptionalInt.of(entity.getId()));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT_ID, WarmColdVariant.TEMPERATE);
        this.entityData.define(DATA_TONGUE_TARGET_ID, OptionalInt.empty());
    }

    @Override
    public int getHeadRotSpeed() {
        return 35;
    }

    @Override
    public int getMaxHeadYRot() {
        return 5;
    }

    @Override
    public WarmColdVariant getVariant() {
        return this.entityData.get(DATA_VARIANT_ID);
    }

    @Override
    public void setVariant(WarmColdVariant frogVariant) {
        this.entityData.set(DATA_VARIANT_ID, frogVariant);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.putVariantToTag(compoundTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setVariant(this.getVariantFromTag(compoundTag));
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    private boolean isMovingOnLand() {
        return this.onGround && this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6 && !this.isInWaterOrBubble();
    }

    private boolean isMovingInWater() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6 && this.isInWaterOrBubble();
    }

    @Override
    public void tick() {
        if (this.level.isClientSide()) {
            if (this.isMovingOnLand()) {
                this.walkAnimationState.startIfStopped(this.tickCount);
            } else {
                this.walkAnimationState.stop();
            }
            if (this.isMovingInWater()) {
                this.swimIdleAnimationState.stop();
                this.swimAnimationState.startIfStopped(this.tickCount);
            } else if (this.isInWaterOrBubble()) {
                this.swimAnimationState.stop();
                this.swimIdleAnimationState.startIfStopped(this.tickCount);
            } else {
                this.swimAnimationState.stop();
                this.swimIdleAnimationState.stop();
            }
        }
        super.tick();
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            Pose pose = this.getPose();
            if (pose == Pose.LONG_JUMPING) {
                this.jumpAnimationState.start(this.tickCount);
            } else {
                this.jumpAnimationState.stop();
            }
            if (pose == Pose.CROAKING) {
                this.croakAnimationState.start(this.tickCount);
            } else {
                this.croakAnimationState.stop();
            }
            if (pose == Pose.USING_TONGUE) {
                this.tongueAnimationState.start(this.tickCount);
            } else {
                this.tongueAnimationState.stop();
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    @Nullable
    public AgeableEntity getBreedOffspring(ServerWorld serverLevel, AgeableEntity ageableMob) {
        FrogEntity frog = EntityType.FROG.create(serverLevel);
        if (frog != null) {
            FrogAi.initMemories(frog, serverLevel.getRandom());
        }
        return frog;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void setBaby(boolean bl) {
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("frogBrain");
        this.getBrain().tick((ServerWorld) this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("frogActivityUpdate");
        FrogAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld serverLevelAccessor, DifficultyInstance difficultyInstance, SpawnReason mobSpawnType, @Nullable ILivingEntityData spawnGroupData, @Nullable CompoundNBT compoundTag) {

        FrogAi.initMemories(this, serverLevelAccessor.getRandom());
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.MAX_HEALTH, 10.0).add(Attributes.ATTACK_DAMAGE, 10.0);
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FROG_AMBIENT;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.FROG_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.FROG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.FROG_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPacketSender.sendEntityBrain(this);
    }

    @Override
    protected int calculateFallDamage(float damageToDeal, float damageMultiplier) {
        return super.calculateFallDamage(damageToDeal, damageMultiplier) - FROG_FALL_DAMAGE_REDUCTION;
    }

    @Override
    public void travel(Vector3d vec3) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(vec3);
        }
    }

    @Override
    public boolean canCutCorner(PathNodeType blockPathTypes) {
        return super.canCutCorner(blockPathTypes) && blockPathTypes != PathNodeType.WATER_BORDER;
    }

    public static boolean canEat(LivingEntity livingEntity) {
        SlimeEntity slime;
        if (livingEntity instanceof SlimeEntity && (slime = (SlimeEntity) livingEntity).getSize() != 1) {
            return false;
        }
        return livingEntity.getType().is(EntityType.SLIME) || livingEntity instanceof AxolotlEntity && livingEntity.isBaby();
    }

    @Override
    protected PathNavigator createNavigation(World level) {
        return new FrogPathNavigation(this, level);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return TEMPTATION_ITEM.test(itemStack);
    }

    static class FrogPathNavigation
            extends AmphibiousPathNavigation {
        FrogPathNavigation(FrogEntity frog, World level) {
            super(frog, level);
        }

        @Override
        protected PathFinder createPathFinder(int n) {
            this.nodeEvaluator = new FrogNodeEvaluator(true);
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, n);
        }
    }

    static class FrogNodeEvaluator
            extends AmphibiousNodeEvaluator {
        private final BlockPos.Mutable belowPos = new BlockPos.Mutable();

        public FrogNodeEvaluator(boolean bl) {
            super(bl);
        }

        @Override
        public PathPoint getStart() {
            if (!this.mob.isInWater()) {
                return super.getStart();
            }
            return this.getNode(new BlockPos(MathHelper.floor(this.mob.getBoundingBox().minX), MathHelper.floor(this.mob.getBoundingBox().minY), MathHelper.floor(this.mob.getBoundingBox().minZ)));
        }

        @Override
        public PathNodeType getBlockPathType(IBlockReader blockGetter, int n, int n2, int n3) {
            this.belowPos.set(n, n2 - 1, n3);
            BlockState blockState = blockGetter.getBlockState(this.belowPos);
            if (blockState.is(FROG_PREFER_JUMP_TO)) {
                return PathNodeType.OPEN;
            }
            return super.getBlockPathType(blockGetter, n, n2, n3);
        }
    }
}
