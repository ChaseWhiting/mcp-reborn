
package net.minecraft.entity.sniffer;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vec3;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sniffer
extends Animal {
    private static final int DIGGING_PARTICLES_DELAY_TICKS = 1700;
    private static final int DIGGING_PARTICLES_DURATION_TICKS = 6000;
    private static final int DIGGING_PARTICLES_AMOUNT = 30;
    private static final int DIGGING_DROP_SEED_OFFSET_TICKS = 120;
    private static final int SNIFFER_BABY_AGE_TICKS = 48000;
    private static final float DIGGING_BB_HEIGHT_OFFSET = 0.4f;
    //private static final EntitySize DIGGING_DIMENSIONS = EntitySize.scalable(EntityType.SNIFFER.getWidth(), EntityType.SNIFFER.getHeight() - 0.4f);
    private static final DataParameter<State> DATA_STATE = EntityDataManager.defineId(Sniffer.class, DataSerializers.SNIFFER_STATE);
    private static final DataParameter<Integer> DATA_DROP_SEED_AT_TICK = EntityDataManager.defineId(Sniffer.class, DataSerializers.INT);
    public final AnimationState feelingHappyAnimationState = new AnimationState();
    public final AnimationState scentingAnimationState = new AnimationState();
    public final AnimationState sniffingAnimationState = new AnimationState();
    public final AnimationState diggingAnimationState = new AnimationState();
    public final AnimationState risingAnimationState = new AnimationState();

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.1f).add(Attributes.MAX_HEALTH, 14.0);
    }

    public Sniffer(EntityType<? extends Animal> entityType, World level) {
        super(entityType, level);
        this.entityData.define(DATA_STATE, State.IDLING);
        this.entityData.define(DATA_DROP_SEED_AT_TICK, 0);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathNodeType.WATER, -1.0f);
        this.setPathfindingMalus(PathNodeType.DANGER_OTHER, -1.0f);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize entityDimensions) {
        return this.getDimensions(pose).height * 0.6f;
    }

    @Override
    public void onPathfindingStart() {
        super.onPathfindingStart();
        if (this.isOnFire() || this.isInWater()) {
            this.setPathfindingMalus(PathNodeType.WATER, 0.0f);
        }
    }

    @Override
    public void onPathfindingDone() {
        this.setPathfindingMalus(PathNodeType.WATER, -1.0f);
    }

//    @Override
//    public EntitySize getDimensions(Pose pose) {
//        if (this.entityData.hasItem(DATA_STATE) && this.getState() == State.DIGGING) {
//            return DIGGING_DIMENSIONS.scale(this.getScale());
//        }
//        return super.getDimensions(pose);
//    }

    public boolean isPanicking() {
        return this.brain.getMemory(MemoryModuleType.IS_PANICKING).isPresent();
    }

    public boolean isSearching() {
        return this.getState() == State.SEARCHING;
    }

    public boolean isTempted() {
        return this.brain.getMemory(MemoryModuleType.IS_TEMPTED).orElse(false);
    }

    public boolean canSniff() {
        return !this.isTempted() && !this.isPanicking() && !this.isInWater() && !this.isInLove() && this.onGround && !this.isPassenger();
    }

    public boolean canPlayDiggingSound() {
        return this.getState() == State.DIGGING || this.getState() == State.SEARCHING;
    }

    private BlockPos getHeadBlock() {
        Vector3d vec3 = this.getHeadPosition();
        return BlockPos.containing(vec3.x(), this.getY() + (double)0.2f, vec3.z());
    }

    private Vector3d getHeadPosition() {
        return this.position().add(this.getForward().scale(2.25));
    }

    private State getState() {
        return this.entityData.get(DATA_STATE);
    }

    private Sniffer setState(State state) {
        this.entityData.set(DATA_STATE, state);
        return this;
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> entityDataAccessor) {
        if (DATA_STATE.equals(entityDataAccessor)) {
            State state = this.getState();
            this.resetAnimations();
            switch (state) {
                case SCENTING: {
                    this.scentingAnimationState.startIfStopped(this.tickCount);
                    break;
                }
                case SNIFFING: {
                    this.sniffingAnimationState.startIfStopped(this.tickCount);
                    break;
                }
                case DIGGING: {
                    this.diggingAnimationState.startIfStopped(this.tickCount);
                    break;
                }
                case RISING: {
                    this.risingAnimationState.startIfStopped(this.tickCount);
                    break;
                }
                case FEELING_HAPPY: {
                    this.feelingHappyAnimationState.startIfStopped(this.tickCount);
                }
            }
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    private void resetAnimations() {
        this.diggingAnimationState.stop();
        this.sniffingAnimationState.stop();
        this.risingAnimationState.stop();
        this.feelingHappyAnimationState.stop();
        this.scentingAnimationState.stop();
    }
//
//    public Sniffer transitionTo(State state) {
//        switch (state) {
//            case IDLING: {
//                this.setState(State.IDLING);
//                break;
//            }
//            case SCENTING: {
//                this.setState(State.SCENTING).onScentingStart();
//                break;
//            }
//            case SNIFFING: {
//                this.playSound(SoundEvents.SNIFFER_SNIFFING, 1.0f, 1.0f);
//                this.setState(State.SNIFFING);
//                break;
//            }
//            case SEARCHING: {
//                this.setState(State.SEARCHING);
//                break;
//            }
//            case DIGGING: {
//                this.setState(State.DIGGING).onDiggingStart();
//                break;
//            }
//            case RISING: {
//                this.playSound(SoundEvents.SNIFFER_DIGGING_STOP, 1.0f, 1.0f);
//                this.setState(State.RISING);
//                break;
//            }
//            case FEELING_HAPPY: {
//                this.playSound(SoundEvents.SNIFFER_HAPPY, 1.0f, 1.0f);
//                this.setState(State.FEELING_HAPPY);
//            }
//        }
//        return this;
//    }

//    private Sniffer onScentingStart() {
//        this.playSound(SoundEvents.SNIFFER_SCENTING, 1.0f, this.isBaby() ? 1.3f : 1.0f);
//        return this;
//    }
//
//    private Sniffer onDiggingStart() {
//        this.entityData.set(DATA_DROP_SEED_AT_TICK, this.tickCount + 120);
//        this.level.broadcastEntityEvent(this, (byte)63);
//        return this;
//    }
//
//    public Sniffer onDiggingComplete(boolean bl) {
//        if (bl) {
//            this.storeExploredPosition(this.getOnPos());
//        }
//        return this;
//    }
//
//    Optional<BlockPos> calculateDigPosition() {
//        return IntStream.range(0, 5).mapToObj(n -> RandomPositionGenerator.getPos(this, 10 + 2 * n, 3)).filter(Objects::nonNull).map(Vector3d::containing).filter(blockPos -> this.level.getWorldBorder().isWithinBounds((BlockPos)blockPos)).map(BlockPos::below).filter(this::canDig).findFirst();
//    }
//
//    boolean canDig() {
//        return !this.isPanicking() && !this.isTempted() && !this.isBaby() && !this.isInWater() && this.onGround && !this.isPassenger() && this.canDig(this.getHeadBlock().below());
//    }
//
//    private boolean canDig(BlockPos blockPos) {
//        return this.level().getBlockState(blockPos).is(BlockTags.SNIFFER_DIGGABLE_BLOCK) && this.getExploredPositions().noneMatch(globalPos -> GlobalPos.of(this.level.dimension(), blockPos).equals(globalPos)) && Optional.ofNullable(this.getNavigation().createPath(blockPos, 1)).map(Path::canReach).orElse(false) != false;
//    }

//    private void dropSeed() {
//        if (this.level().isClientSide() || this.entityData.get(DATA_DROP_SEED_AT_TICK) != this.tickCount) {
//            return;
//        }
//        ServerWorld serverLevel = (ServerWorld)this.level();
//        LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(BuiltInLootTables.SNIFFER_DIGGING);
//        LootParams lootParams = new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, this.getHeadPosition()).withParameter(LootContextParams.THIS_ENTITY, this).create(LootContextParamSets.GIFT);
//        ObjectArrayList<ItemStack> objectArrayList = lootTable.getRandomItems(lootParams);
//        BlockPos blockPos = this.getHeadBlock();
//        for (ItemStack itemStack : objectArrayList) {
//            ItemEntity itemEntity = new ItemEntity(serverLevel, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack);
//            itemEntity.setDefaultPickUpDelay();
//            serverLevel.addFreshEntity(itemEntity);
//        }
//        this.playSound(SoundEvents.SNIFFER_DROP_SEED, 1.0f, 1.0f);
//    }

    private Sniffer emitDiggingParticles(AnimationState animationState) {
        boolean bl;
        boolean bl2 = bl = animationState.getAccumulatedTime() > 1700L && animationState.getAccumulatedTime() < 6000L;
        if (bl) {
            BlockPos blockPos = this.getHeadBlock();
            BlockState blockState = this.level().getBlockState(blockPos.below());
            if (blockState.getRenderShape() != BlockRenderType.INVISIBLE) {
                for (int i = 0; i < 30; ++i) {
                    Vector3d vec3 = Vec3.atCenterOf(blockPos).add(0.0, -0.65f, 0.0);
                    this.level().addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockState), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
                }
                if (this.tickCount % 10 == 0) {
                    this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), blockState.getSoundType().getHitSound(), this.getSoundSource(), 0.5f, 0.5f, false);
                }
            }
        }
        if (this.tickCount % 10 == 0) {
            this.level().gameEvent(GameEvent.ENTITY_SHAKE, this.getHeadBlock(), GameEvent.Context.of(this));
        }
        return this;
    }

    private Sniffer storeExploredPosition(BlockPos blockPos) {
        List<GlobalPos> list = this.getExploredPositions().limit(20L).collect(Collectors.toList());
        list.add(0, GlobalPos.of(this.level().dimension(), blockPos));
        this.getBrain().setMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, list);
        return this;
    }

    private Stream<GlobalPos> getExploredPositions() {
        return this.getBrain().getMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS).stream().flatMap(Collection::stream);
    }

    @Override
    protected void jumpFromGround() {
        double d;
        super.jumpFromGround();
        double d2 = this.moveControl.getSpeedModifier();
        if (d2 > 0.0 && (d = this.getDeltaMovement().horizontalDistanceSqr()) < 0.01) {
            this.moveRelative(0.1f, new Vec3(0.0, 0.0, 1.0));
        }
    }
//
//    @Override
//    public void spawnChildFromBreeding(ServerWorld serverLevel, Animal animal) {
//        ItemStack itemStack = new ItemStack(Items.SNIFFER_EGG);
//        ItemEntity itemEntity = new ItemEntity(serverLevel, this.position().x(), this.position().y(), this.position().z(), itemStack);
//        itemEntity.setDefaultPickUpDelay();
//        this.finalizeSpawnChildFromBreeding(serverLevel, animal, null);
//        this.playSound(SoundEvents.SNIFFER_EGG_PLOP, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 0.5f);
//        serverLevel.addFreshEntity(itemEntity);
//    }

    public void finalizeSpawnChildFromBreeding(ServerWorld serverLevel, Animal animal, @Nullable AgeableEntity ageableMob) {
        Optional.ofNullable(this.getLoveCause()).or(() -> Optional.ofNullable(animal.getLoveCause())).ifPresent(serverPlayer -> {
            serverPlayer.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger((ServerPlayerEntity) serverPlayer, this, animal, ageableMob);
        });
        this.setAge(6000);
        animal.setAge(6000);
        this.resetLove();
        animal.resetLove();
        serverLevel.broadcastEntityEvent(this, (byte)18);
        if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            serverLevel.addFreshEntity(new ExperienceOrbEntity(serverLevel, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }

//    @Override
//    public void die(DamageSource damageSource) {
//        this.transitionTo(State.IDLING);
//        super.die(damageSource);
//    }

//    @Override
//    public void tick() {
//        switch (this.getState()) {
//            case DIGGING: {
//                this.emitDiggingParticles(this.diggingAnimationState).dropSeed();
//                break;
//            }
//            case SEARCHING: {
//                this.playSearchingSound();
//            }
//        }
//        super.tick();
//    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        boolean bl = this.isFood(itemStack);
        ActionResultType interactionResult = super.mobInteract(player, interactionHand);
        if (interactionResult.consumesAction() && bl) {
            this.level.playSound(null, this, this.getEatingSound(itemStack), SoundCategory.NEUTRAL, 1.0f, MathHelper.randomBetween(this.level().random, 0.8f, 1.2f));
        }
        return interactionResult;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 1.8;
    }

    @Override
    public float getNameTagOffsetY() {
        return super.getNameTagOffsetY() + 0.3f;
    }

    private void playSearchingSound() {
        if (this.level().isClientSide() && this.tickCount % 20 == 0) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.SNIFFER_SEARCHING, this.getSoundSource(), 1.0f, 1.0f, false);
        }
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.SNIFFER_STEP, 0.15f, 1.0f);
    }

    @Override
    public SoundEvent getEatingSound(ItemStack itemStack) {
        return SoundEvents.SNIFFER_EAT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return Set.of(State.DIGGING, State.SEARCHING).contains((Object)this.getState()) ? null : SoundEvents.SNIFFER_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SNIFFER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SNIFFER_DEATH;
    }

    @Override
    public int getMaxHeadYRot() {
        return 50;
    }

    @Override
    public void setBaby(boolean bl) {
        this.setAge(bl ? -48000 : 0);
    }

    @Override
    public AgeableEntity getBreedOffspring(ServerWorld serverLevel, AgeableEntity ageableMob) {
        return null;
    }

    @Override
    public boolean canMate(Animal animal) {
        if (animal instanceof Sniffer) {
            Sniffer sniffer = (Sniffer)animal;
            Set<State> set = Set.of(State.IDLING, State.SCENTING, State.FEELING_HAPPY);
            return set.contains((Object)this.getState()) && set.contains((Object)sniffer.getState()) && super.canMate(animal);
        }
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(0.6f);
    }

//    @Override
//    public boolean isFood(ItemStack itemStack) {
//        return itemStack.is(ItemTags.SNIFFER_FOOD);
//    }

//    @Override
//    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
//        return SnifferAi.makeBrain(this.brainProvider().makeBrain(dynamic));
//    }

    public Brain<Sniffer> getBrain() {
        return (Brain<Sniffer>) super.getBrain();
    }
//
//    protected Brain.BrainCodec<Sniffer> brainProvider() {
//        return Brain.provider(SnifferAi.MEMORY_TYPES, SnifferAi.SENSOR_TYPES);
//    }

    @Override
    protected void customServerAiStep() {
//        this.level().getProfiler().push("snifferBrain");
//        this.getBrain().tick((ServerWorld) this.level(), this);
//        this.level().getProfiler().popPush("snifferActivityUpdate");
//        //SnifferAi.updateActivity(this);
//        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPacketSender.sendEntityBrain(this);
    }

    public static enum State {
        IDLING,
        FEELING_HAPPY,
        SCENTING,
        SNIFFING,
        SEARCHING,
        DIGGING,
        RISING;

    }
}

