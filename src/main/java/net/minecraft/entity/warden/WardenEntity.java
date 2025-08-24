package net.minecraft.entity.warden;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.*;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.SonicBoom;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.DynamicGameEventListener;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.entity.warden.event.GameEventListener;
import net.minecraft.entity.warden.event.position.EntityPositionSource;
import net.minecraft.entity.warden.event.vibrations.VibrationListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.VisibleForTesting;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;

public class WardenEntity extends Monster implements VibrationListener.VibrationListenerConfig {


    private DynamicGameEventListener<VibrationListener> dynamicGameEventListener;
    private AngerManagement angerManagement = new AngerManagement(this::canTargetEntity, Collections.emptyList());

    private static final DataParameter<Integer> CLIENT_ANGER_LEVEL = EntityDataManager.defineId(WardenEntity.class, DataSerializers.INT);
    private int tendrilAnimation;
    private int tendrilAnimationO;
    private int heartAnimation;
    private int heartAnimationO;
    private boolean canUseSonicBoom;
    public AnimationState roarAnimationState = new AnimationState();
    public AnimationState sniffAnimationState = new AnimationState();
    public AnimationState emergeAnimationState = new AnimationState();
    public AnimationState diggingAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState sonicBoomAnimationState = new AnimationState();

    public WardenEntity(EntityType<? extends Monster> entity, World world) {
        super(entity, world);
        //this.dynamicGameEventListener = new DynamicGameEventListener<>(new VibrationListener(new EntityPositionSource(this, this.getEyeHeight()), this.veryHardmode() ? 64 : 16, this));
        this.xpReward = 5;
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathNodeType.UNPASSABLE_RAIL, 0.0f);
        this.setPathfindingMalus(PathNodeType.DAMAGE_OTHER, 8.0f);
        this.setPathfindingMalus(PathNodeType.LAVA, 8.0f);
        this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, 0.0f);
        this.setPathfindingMalus(PathNodeType.DANGER_FIRE, 0.0f);

        if (this.veryHardmode()) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000D);
            this.setHealth(this.getMaxHealth());
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35F);
            this.xpReward = 20;
        }
    }

    public boolean canUseSonicBoom() {
        return canUseSonicBoom;
    }

    public static boolean checkSpawnRules(EntityType<WardenEntity> type, IServerWorld world, SpawnReason reason, BlockPos pos, Random random) {
        return world.getBlockState(pos.below()).is(List.of(Blocks.SCULK, Blocks.SCULK_CATALYST, Blocks.SCULK_VEIN)) && isDarkEnoughToSpawn(world, pos, random) && pos.getY() <= 30 && pos.getY() >= 10
                && checkNearbySculkBlocks(world, pos) && world.getEntitiesOfClass(WardenEntity.class, new AxisAlignedBB(pos).inflate(64, 40, 64)).isEmpty();
    }

    public boolean canDisableShield() {
        return true;
    }

    public int getDisableShieldTime() {
        return this.veryHardmode() ? 15 * 20 : super.getDisableShieldTime();
    }

    public String getChunkSection() {
        int x = (int) this.getX();
        int y = (int) this.getY();
        int z = (int) this.getZ();
        return String.format("%s, %s, %s", x >> 4, y >> 4, z >> 4);
    }

    public boolean hasPose(Pose pose) {
        return this.getPose() == pose;
    }

    @Override
    public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
        return super.checkSpawnObstruction(p_205019_1_) && p_205019_1_.noCollision(this, this.getType().getDimensions().makeBoundingBox(this.position()));
    }

    @Override
    public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
        return 0f;
    }

    public boolean isInvulnerableTo(DamageSource source) {
        if (this.isDiggingOrEmerging() && !(source.isBypassInvul())) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    private boolean isDiggingOrEmerging() {
        return this.hasPose(Pose.DIGGING) || this.hasPose(Pose.EMERGING);
    }

    public boolean canRide(Entity entity) {
        return false;
    }

    protected float nextStep() {
        return this.moveDist + 0.55f;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 500.0).add(Attributes.MOVEMENT_SPEED, 0.3f).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ATTACK_KNOCKBACK, 1.5).add(Attributes.ATTACK_DAMAGE, 30.0);
    }

    public float getSoundVolume() {
        return 4f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.hasPose(Pose.ROARING) || this.isDiggingOrEmerging()) {
            return null;
        }
        return this.getAngerLevel().getAmbientSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.WARDEN_STEP, 10.0f, 1.0f);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.level.broadcastEntityEvent(this, (byte)4);
        this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 10.0f, this.getVoicePitch());
        SonicBoom.setCooldown(this, veryHardmode() ? 20 : 40);
        return super.doHurtTarget(entity);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLIENT_ANGER_LEVEL, 0);
    }

    public int getClientAngerLevel() {
        return this.entityData.get(CLIENT_ANGER_LEVEL);
    }

    private void syncClientAngerLevel() {
        this.entityData.set(CLIENT_ANGER_LEVEL, this.getActiveAnger());
    }


    @Override
    public void tick() {
        if (firstTick && this.level.isLoaded(this.blockPosition()) && this.level instanceof ServerWorld) {
            LOGGER.info("Registering VibrationListener for Warden after world load...");
            this.dynamicGameEventListener = new DynamicGameEventListener<>(
                    new VibrationListener(new EntityPositionSource(this, this.getEyeHeight()), this.veryHardmode() ? 64 : 16, this)
            );
            this.updateDynamicGameEventListener(DynamicGameEventListener::move);
        }
        if (this.dynamicGameEventListener == null) {
            this.dynamicGameEventListener = new DynamicGameEventListener<>(
                    new VibrationListener(new EntityPositionSource(this, this.getEyeHeight()), this.veryHardmode() ? 64 : 16, this)
            );
            this.updateDynamicGameEventListener(DynamicGameEventListener::move);
        }
        World level = this.level;
        if (level instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)level;
            if (this.dynamicGameEventListener != null) {
                this.dynamicGameEventListener.getListener().tick(serverWorld);
            }


            if (this.isPersistenceRequired() || this.requiresCustomPersistence()) {
                WardenAi.setDigCooldown(this);
            }
        }
        super.tick();
        if (this.level.isClientSide()) {
            if (this.tickCount % this.getHeartBeatDelay() == 0) {
                this.heartAnimation = 10;
                if (!this.isSilent()) {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_HEARTBEAT, this.getSoundSource(), 5.0f, this.getVoicePitch(), false);
                }
            }
            this.tendrilAnimationO = this.tendrilAnimation;
            if (this.tendrilAnimation > 0) {
                --this.tendrilAnimation;
            }
            this.heartAnimationO = this.heartAnimation;
            if (this.heartAnimation > 0) {
                --this.heartAnimation;
            }
            switch (this.getPose()) {
                case EMERGING: {
                    this.clientDiggingParticles(this.emergeAnimationState);
                    break;
                }
                case DIGGING: {
                    this.clientDiggingParticles(this.diggingAnimationState);
                }
            }
        }
    }

    public boolean dampensVibrations() {
        return true;
    }

    public BlockPos section() {
        return new BlockPos((int)this.getX() >> 4, (int)this.getY() >> 4, (int)this.getZ() >> 4);
    }

    @Override
    protected void customServerAiStep() {
        ServerWorld serverWorld = (ServerWorld)this.level;
        String s = this.getChunkSection();

        serverWorld.getProfiler().push("wardenBrain");
        this.getBrain().tick(serverWorld, this);
        this.level.getProfiler().pop();
        super.customServerAiStep();

        if ((this.tickCount + this.getId()) % 120 == 0) {
            //WardenEntity.applyDarknessAround(serverWorld, this.position(), this, 20);
        }
        if (this.tickCount % 20 == 0) {
            this.angerManagement.tick(serverWorld, this::canTargetEntity);
            this.syncClientAngerLevel();
        }
        WardenAi.updateActivity(this);
    }

    @Override
    protected Brain<WardenEntity> makeBrain(Dynamic<?> dynamic) {
        return WardenAi.makeBrain(this, dynamic);
    }

    public Brain<WardenEntity> getBrain() {
        return (Brain<WardenEntity>) super.getBrain();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPacketSender.sendEntityBrain(this);
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 4) {
            this.roarAnimationState.stop();
            this.attackAnimationState.start(this.tickCount);
        } else if (by == 61) {
            this.tendrilAnimation = 10;
        } else if (by == 62) {
            this.sonicBoomAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(by);
        }
    }

    public List<Block> WARDEN_BREAKABLE_BLOCKS;

    public void aiStep() {
        super.aiStep();
        if (WARDEN_BREAKABLE_BLOCKS == null && this.level.isServerSide) {
            List<Block> blockb = new ArrayList<>();
            for (Block block : Registry.BLOCK) {
                if (block instanceof TerracottaBlock || block instanceof ConcreteBlock || block instanceof ConcretePowderBlock || block instanceof AbstractGlassBlock || block instanceof PaneBlock || block instanceof WeatheringCopper || block instanceof CoralBlock) blockb.add(block);
            }
            WARDEN_BREAKABLE_BLOCKS = ImmutableList.<Block>builder().addAll(BlockTags.LOGS.getValues()).addAll(BlockTags.PLANKS.getValues())
                    .addAll(BlockTags.WOOL.getValues())
                    .addAll(BlockTags.BASE_STONE_OVERWORLD.getValues())
                    .addAll(BlockTags.BASE_STONE_NETHER.getValues())
                    .addAll(BlockTags.WALLS.getValues())
                    .addAll(List.of(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.GRASS_PATH, Blocks.MUD))
                    .addAll(BlockTags.ICE.getValues())
                    .addAll(BlockTags.STAIRS.getValues())
                    .addAll(BlockTags.SLABS.getValues())
                    .addAll(BlockTags.FENCES.getValues())
                    .addAll(BlockTags.FENCE_GATES.getValues())
                    .addAll(BlockTags.SAND.getValues())
                    .addAll(BlockTags.STONE_BRICKS.getValues())
                    .addAll(BlockTags.BEDS.getValues())
                    .addAll(BlockTags.CAMPFIRES.getValues())
                    .addAll(BlockTags.DOORS.getValues())
                    .addAll(List.of(Blocks.SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE))
                    .addAll(List.of(Blocks.HAY_BLOCK))
                    .addAll(List.of(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Blocks.COBWEB, Blocks.COCOA, Blocks.CAKE, Blocks.CACTUS))
                    .addAll(BlockTags.LEAVES.getValues())
                    .add(Blocks.IRON_BARS)
                    .addAll(blockb)
                    .addAll(List.of(Blocks.FARMLAND, Blocks.COMPOSTER, Blocks.CRAFTING_TABLE, Blocks.SMOKER, Blocks.BLAST_FURNACE, Blocks.CARTOGRAPHY_TABLE, Blocks.FLETCHING_TABLE, Blocks.GRINDSTONE, Blocks.LECTERN, Blocks.SMITHING_TABLE, Blocks.STONECUTTER))
                    .addAll(List.of(Blocks.END_STONE, Blocks.END_ROD, Blocks.ENDER_CHEST, Blocks.CAULDRON, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.BREWING_STAND))
                    .addAll(List.of(Blocks.HONEY_BLOCK, Blocks.HONEYCOMB_BLOCK, Blocks.SPONGE, Blocks.WET_SPONGE))
                    .addAll(List.of(Blocks.IRON_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.LAPIS_BLOCK, Blocks.GOLD_BLOCK, Blocks.COAL_BLOCK))
                    .addAll(List.of(Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.DIAMOND_ORE, Blocks.LAPIS_ORE, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE))
                    .add(Blocks.NETHER_BRICKS)
                    .add(Blocks.CHISELED_NETHER_BRICKS)
                    .add(Blocks.CRACKED_NETHER_BRICKS)
                    .addAll(List.of(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.LODESTONE, Blocks.RESPAWN_ANCHOR, Blocks.GLOWSTONE))
                    .addAll(List.of(Blocks.CLAY, Blocks.TORCH, Blocks.WALL_TORCH, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.LANTERN, Blocks.SOUL_LANTERN))
                    .addAll(List.of(Blocks.PISTON, Blocks.PISTON_HEAD, Blocks.MOVING_PISTON, Blocks.STICKY_PISTON))
                    .add(Blocks.BOOKSHELF)
                    .add(Blocks.GRAVEL)
                    .add(Blocks.CUT_SANDSTONE)
                    .addAll(List.of(Blocks.SMOOTH_STONE, Blocks.BRICKS))
                    .build();
        }


        boolean wall = this.isInWall() || this.wouldSuffocateAtTargetPose(this.getPose());
        boolean flag = this.tickCount % (20 - (this.getHeartBeatDelay() / 4)) == 0 && (this.getAngerManagement().highestAnger > 70 || wall);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.canDig() && flag && (this.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).isEmpty() || wall)) {
            for (BlockPos position : BlockPos.withinManhattan(this.blockPosition().relative(this.getDirection()).above(), 1, 1, 1)) {

                    if (getTarget() == null) {
                        if (position.getY() < this.getY()) continue;
                    } else {
                        if (this.getTarget() != null && this.getTarget().getY() + 1 < this.getY() && !this.closerThan(this.getTarget(), 10, 1.85) && this.closerThan(this.getTarget(), 6.5, 90)) {
                            position = position.below();
                            Vector3d movement = this.getDeltaMovement();
                            double yMovement = movement.y;

                            Vector3d newMovement = movement.scale(0.89);
                            newMovement = new Vector3d(newMovement.x, yMovement, newMovement.z);

                            this.setDeltaMovement(newMovement);

                        }
                    }
                    if (this.getTarget() != null && this.getTarget().getY() - 1 > this.getY()) {
                        position = position.above();
                    }

                if (WARDEN_BREAKABLE_BLOCKS == null || !level.getBlockState(position).is(WARDEN_BREAKABLE_BLOCKS)) continue;

                if (level.destroyBlock(position, true, this, 512, false)) {
                    this.level.broadcastEntityEvent(this, (byte)4);
                }
            }
        }
    }

    public void makeStuckInBlock(BlockState p_213295_1_, Vector3d p_213295_2_) {
        if (this.veryHardmode() && p_213295_1_.is(Blocks.COBWEB)) return;


        super.makeStuckInBlock(p_213295_1_, p_213295_2_);
    }

    public boolean canDig() {
        return this.veryHardmode();
    }

    protected boolean wouldSuffocateAtTargetPose(Pose pose) {
        AxisAlignedBB aABB = this.getDimensions(pose).makeBoundingBox(this.position());
        return !this.level.noBlockCollision(this, aABB, (b, p ) -> true);
    }

    private int getHeartBeatDelay() {
        float f = (float)this.getClientAngerLevel() / (float)AngerLevel.ANGRY.getMinimumAnger();
        return 40 - MathHelper.floor(MathHelper.clamp(f, 0.0f, 1.0f) * 30.0f);
    }

    public float getTendrilAnimation(float f) {
        return MathHelper.lerp(f, this.tendrilAnimationO, this.tendrilAnimation) / 10.0f;
    }

    public float getHeartAnimation(float f) {
        return MathHelper.lerp(f, this.heartAnimationO, this.heartAnimation) / 10.0f;
    }

    private void clientDiggingParticles(AnimationState animationState) {
        if ((float)animationState.getAccumulatedTime() < 4500.0f) {
            Random randomSource = this.getRandom();
            BlockState blockState = this.getBlockStateOn();
            if (blockState.getRenderShape() != BlockRenderType.INVISIBLE) {
                for (int i = 0; i < 30; ++i) {
                    double d = this.getX() + (double)MathHelper.randomBetween(randomSource, -0.7f, 0.7f);
                    double d2 = this.getY();
                    double d3 = this.getZ() + (double)MathHelper.randomBetween(randomSource, -0.7f, 0.7f);
                    this.level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockState), d, d2, d3, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            switch (this.getPose()) {
                case ROARING: {
                    this.roarAnimationState.start(this.tickCount);
                    break;
                }
                case SNIFFING: {
                    this.sniffAnimationState.start(this.tickCount);
                    break;
                }
                case EMERGING: {
                    this.emergeAnimationState.start(this.tickCount);
                    break;
                }
                case DIGGING: {
                    this.diggingAnimationState.start(this.tickCount);
                }
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    public boolean ignoreExplosion() {
        return this.isDiggingOrEmerging();
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerWorld> biConsumer) {
        World level = this.level;
        if (level instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)level;
            if (dynamicGameEventListener == null) return;
            biConsumer.accept(this.dynamicGameEventListener, serverWorld);
        }
    }

    @Override
    public ImmutableList<GameEvent> getListenableEvents() {
        return GameEvent.WARDEN_CAN_LISTEN;
    }

    @Override
    public boolean canTriggerAvoidVibration() {
        return true;
    }

    @Contract(value="null->false")
    public boolean canTargetEntity(@Nullable Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;
        LivingEntity livingEntity = (LivingEntity)entity;
        if (this.level != entity.level) return false;
        if (!EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(entity)) return false;
        if (this.isAlliedTo(entity)) return false;
        if (livingEntity.getType() == EntityType.ARMOR_STAND) return false;
        if (livingEntity.getType() == EntityType.WARDEN) return false;
        if (livingEntity.isInvulnerable()) return false;
        if (livingEntity.isDeadOrDying()) return false;
        if (!this.level.getWorldBorder().isWithinBounds(livingEntity.getBoundingBox())) return false;
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("CanUseSonicBoom", this.canUseSonicBoom);
        AngerManagement.codec(this::canTargetEntity).encodeStart(NBTDynamicOps.INSTANCE, this.angerManagement).resultOrPartial(arg_0 -> (LOGGER).error(arg_0)).ifPresent(tag -> compoundTag.put("anger", tag));
        VibrationListener.codec(this).encodeStart(NBTDynamicOps.INSTANCE, this.dynamicGameEventListener.getListener()).resultOrPartial(arg_0 -> (LOGGER).error(arg_0)).ifPresent(tag -> compoundTag.put("listener", tag));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.canUseSonicBoom = compoundTag.getBoolean("CanUseSonicBoom");
        if (compoundTag.contains("anger")) {
            AngerManagement.codec(this::canTargetEntity).parse(NBTDynamicOps.INSTANCE, compoundTag.get("anger"))
                    .resultOrPartial(arg_0 -> (LOGGER).error(arg_0))
                    .ifPresent(angerManagement -> {
                        this.angerManagement = angerManagement;
                    });
            this.syncClientAngerLevel();
        }

        if (compoundTag.contains("listener", 10)) {
            if (this.dynamicGameEventListener == null) return;
            if (this.firstTick || !this.level.isLoaded(this.blockPosition())) {
                LOGGER.warn("Delaying VibrationListener update for Warden because the world is still loading.");

                this.level.getServer().execute(() -> {
                    if (this.isAlive() && this.level.isLoaded(this.blockPosition())) {
                        LOGGER.info("Applying delayed Warden VibrationListener update...");
                        VibrationListener.codec(this).parse(NBTDynamicOps.INSTANCE, compoundTag.getCompound("listener"))
                                .resultOrPartial(arg_0 -> (LOGGER).error(arg_0))
                                .ifPresent(vibrationListener -> this.dynamicGameEventListener.updateListener((VibrationListener)vibrationListener, this.level));
                    }
                });
                return;
            }

            VibrationListener.codec(this).parse(NBTDynamicOps.INSTANCE, compoundTag.getCompound("listener"))
                    .resultOrPartial(arg_0 -> (LOGGER).error(arg_0))
                    .ifPresent(vibrationListener -> this.dynamicGameEventListener.updateListener((VibrationListener)vibrationListener, this.level));
        }
    }


    private void playListeningSound() {
        if (!this.hasPose(Pose.ROARING)) {
            this.playSound(this.getAngerLevel().getListeningSound(), 10.0f, this.getVoicePitch());
        }
    }

    public AngerLevel getAngerLevel() {
        return AngerLevel.byAnger(this.getActiveAnger());
    }

    private int getActiveAnger() {
        return this.angerManagement.getActiveAnger(this.getTarget());
    }

    public void clearAnger(Entity entity) {
        this.angerManagement.clearAnger(entity);
    }

    public void increaseAngerAt(@Nullable Entity entity) {
        this.increaseAngerAt(entity, 35, true);
    }

    @VisibleForTesting
    public void increaseAngerAt(@Nullable Entity entity, int n, boolean bl) {
        if (!this.isNoAi() && this.canTargetEntity(entity)) {
            if (this.veryHardmode()) {
                n *= 1.5;
            }
            WardenAi.setDigCooldown(this);
            boolean bl2 = !(this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null) instanceof PlayerEntity);
            int n2 = this.angerManagement.increaseAnger(entity, n);
            if (entity instanceof PlayerEntity && bl2 && AngerLevel.byAnger(n2).isAngry()) {
                this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }
            if (bl) {
                this.playListeningSound();
            }
        }
    }

    public Optional<LivingEntity> getEntityAngryAt() {
        if (this.getAngerLevel().isAngry()) {
            return this.angerManagement.getActiveEntity();
        }
        return Optional.empty();
    }

    @Override
    @Nullable
    public LivingEntity getTarget() {
        return this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }


    @Override
    public boolean removeWhenFarAway(double d) {
        return false;
    }

    @Override
    public @org.jetbrains.annotations.Nullable ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @org.jetbrains.annotations.Nullable ILivingEntityData p_213386_4_, @org.jetbrains.annotations.Nullable CompoundNBT p_213386_5_) {
        this.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);

        if (p_213386_3_ == SpawnReason.TRAP_TRIGGERED) {
            this.setPose(Pose.EMERGING);
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.IS_EMERGING, Unit.INSTANCE, WardenAi.EMERGE_DURATION);
            this.playSound(SoundEvents.WARDEN_AGITATED, 5.0f, 1.0f);
        }

        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        boolean bl = super.hurt(damageSource, f);
        if (!(this.level.isClientSide || this.isNoAi() || this.isDiggingOrEmerging())) {
            Entity entity = damageSource.getEntity();
            this.increaseAngerAt(entity, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
            if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                if (!(damageSource instanceof IndirectEntityDamageSource) || this.closerThan(livingEntity, 5.0)) {
                    this.setAttackTarget(livingEntity);
                }
            }
        }
        return bl;
    }

    public void setAttackTarget(LivingEntity livingEntity) {
        this.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
        this.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, livingEntity);
        this.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        SonicBoom.setCooldown(this, 200);
    }

    @Override
    public EntitySize getDimensions(Pose pose) {
        EntitySize entityDimensions = super.getDimensions(pose);
        if (this.isDiggingOrEmerging()) {
            return EntitySize.fixed(entityDimensions.width, 1.0f);
        }
        return entityDimensions;
    }

    public boolean isPushable() {
        return !this.isDiggingOrEmerging() && super.isPushable();
    }

    @Override
    protected void doPush(Entity entity) {
        if (!this.isNoAi() && !this.getBrain().hasMemoryValue(MemoryModuleType.TOUCH_COOLDOWN)) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
            this.increaseAngerAt(entity);
            WardenAi.setDisturbanceLocation(this, entity.blockPosition());
        }
        super.doPush(entity);
    }

    @Override
    public boolean shouldListen(ServerWorld serverWorld, GameEventListener gameEventListener, BlockPos blockPos, GameEvent gameEvent, GameEvent.Context context) {
        LivingEntity livingEntity;
        if (this.isNoAi() || this.isDeadOrDying() || this.getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN) || this.isDiggingOrEmerging() || !serverWorld.getWorldBorder().isWithinBounds(blockPos)) {
            return false;
        }
        Entity entity = context.sourceEntity;
        return !(entity instanceof LivingEntity) || this.canTargetEntity(livingEntity = (LivingEntity)entity);
    }

    @Override
    public void onSignalReceive(ServerWorld serverWorld, GameEventListener gameEventListener, BlockPos blockPos, GameEvent gameEvent, @Nullable Entity entity, @Nullable Entity entity2, float f) {
        if (this.isDeadOrDying()) {
            return;
        }
        this.brain.setMemoryWithExpiry(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
        System.out.println("Warden: Signal Received: " + blockPos.toShortString() + " " + gameEvent.getName() + " " + gameEventListener.getDeliveryMode().name());
        serverWorld.broadcastEntityEvent(this, (byte)61);
        this.playSound(SoundEvents.WARDEN_TENDRIL_CLICKS, 5.0f, this.getVoicePitch());
        BlockPos blockPos2 = blockPos;
        if (entity2 != null) {
            if (this.closerThan(entity2, 30.0)) {
                if (this.getBrain().hasMemoryValue(MemoryModuleType.RECENT_PROJECTILE)) {
                    if (this.canTargetEntity(entity2)) {
                        blockPos2 = entity2.blockPosition();
                    }
                    this.increaseAngerAt(entity2);
                } else {
                    this.increaseAngerAt(entity2, 10, true);
                }
            }
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
        } else {
            this.increaseAngerAt(entity);
        }
        if (!this.getAngerLevel().isAngry()) {
            Optional<LivingEntity> optional = this.angerManagement.getActiveEntity();
            if (entity2 != null || optional.isEmpty() || optional.get() == entity) {
                WardenAi.setDisturbanceLocation(this, blockPos2);
            }
        }
    }

    @VisibleForTesting
    public AngerManagement getAngerManagement() {
        return this.angerManagement;
    }

    @Override
    protected PathNavigator createNavigation(World level) {
        return new GroundPathNavigator(this, level){

            @Override
            protected PathFinder createPathFinder(int n) {
                this.nodeEvaluator = new WalkNodeProcessor();
                this.nodeEvaluator.setCanPassDoors(true);
                return new PathFinder(this.nodeEvaluator, n){
                    @Override
                    protected float distance(PathPoint node, PathPoint node2) {
                        return node.distanceToXZ(node2);
                    }
                };
            }
        };
    }
}
