package net.minecraft.entity.allay;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.DynamicGameEventListener;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.entity.warden.event.GameEventListener;
import net.minecraft.entity.warden.event.position.EntityPositionSource;
import net.minecraft.entity.warden.event.position.PositionSource;
import net.minecraft.entity.warden.event.vibrations.VibrationListener;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class AllayEntity extends Creature implements InventoryCarrier {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Vector3i ITEM_PICKUP_REACH = new Vector3i(1, 1, 1);
    private long duplicationCooldown;
    private float holdingItemAnimationTicks;
    private float holdingItemAnimationTicks0;
    private float dancingAnimationTicks;
    private float spinningAnimationTicks;
    private float spinningAnimationTicks0;
    @Nullable
    private BlockPos jukeboxPos;

    private static final Ingredient DUPLICATION_ITEM = Ingredient.of(Items.DIAMOND);


    private static final DataParameter<Boolean> DATA_DANCING = EntityDataManager.defineId(AllayEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_CAN_DUPLICATE = EntityDataManager.defineId(AllayEntity.class, DataSerializers.BOOLEAN);

    private final Inventory inventory = new Inventory(1);
    protected static final ImmutableList<SensorType<? extends Sensor<? super AllayEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NEAREST_ITEMS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.PATH, MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.HURT_BY, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.LIKED_PLAYER, MemoryModuleType.LIKED_NOTEBLOCK_POSITION, MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleType.IS_PANICKING);
    public static final ImmutableList<Float> THROW_SOUND_PITCHES = ImmutableList.of(0.5625f,
            0.625f, 0.75f, 0.9375f, 1.0f,
            1.0f, 1.125f, 1.25f, 1.5f, 1.875f, 2.0f,
            2.25f, 2.5f, 3.0f, 3.75f, 4.0f);
    private DynamicGameEventListener<VibrationListener> dynamicVibrationListener;
    private final VibrationListener.VibrationListenerConfig vibrationListenerConfig;
    private DynamicGameEventListener<JukeboxListener> dynamicJukeboxListener;

    public AllayEntity(EntityType<AllayEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlyingMovementController(this, 20, true);
        this.vibrationListenerConfig = new AllayVibrationListenerConfig();

    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerWorld> biConsumer) {
        World level = this.level;
        if (level instanceof ServerWorld && this.tickCount > 100) {
            if ((this.dynamicJukeboxListener == null || this.dynamicVibrationListener == null) && this.level.isLoaded(this.blockPosition())) {
                EntityPositionSource entityPositionSource = new EntityPositionSource(this, this.getEyeHeight());

                this.dynamicVibrationListener = new DynamicGameEventListener<VibrationListener>(new VibrationListener(entityPositionSource, 16, this.vibrationListenerConfig));
                this.dynamicJukeboxListener = new DynamicGameEventListener<JukeboxListener>(new JukeboxListener(entityPositionSource, GameEvent.JUKEBOX_PLAY.getNotificationRadius()));
            }
            ServerWorld serverWorld = (ServerWorld)level;
            biConsumer.accept(this.dynamicVibrationListener, serverWorld);
            biConsumer.accept(this.dynamicJukeboxListener, serverWorld);
        }
    }

    protected Brain.BrainCodec<AllayEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return AllayAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    public Brain<AllayEntity> getBrain() {
        return (Brain<AllayEntity>) super.getBrain();
    }


    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.FLYING_SPEED, 0.1f).add(Attributes.MOVEMENT_SPEED, 0.1f).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected PathNavigator createNavigation(World level) {
        FlyingPathNavigator flyingPathNavigation = new FlyingPathNavigator(this, level);
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(true);
        flyingPathNavigation.setCanPassDoors(true);
        return flyingPathNavigation;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANCING, false);
        this.entityData.define(DATA_CAN_DUPLICATE, true);
    }

    @Override
    public void travel(Vector3d vector3D) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02f, vector3D);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8f));
            } else if (this.isInLava()) {
                this.moveRelative(0.02f, vector3D);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
            } else {
                this.moveRelative(this.getSpeed(), vector3D);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.91f));
            }
        }
        this.calculateEntityAnimation(this, false);
    }

    @Override
    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
        return false;
    }

    public boolean hurt(DamageSource source, float damage) {
        Optional<Entity> optional = Optional.ofNullable(source.getEntity());
        if (optional.isPresent() && optional.get() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) optional.get();
            Optional<UUID> optionalUUID = this.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
            if (optionalUUID.isPresent() && player.getUUID().equals(optionalUUID.get())) {
                return false;
            }
        }
        return super.hurt(source, damage);
    }

    public void playStepSound(BlockPos pos, BlockState state){}

    @Override
    protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return this.hasItemInSlot(EquipmentSlotType.MAINHAND) ? SoundEvents.ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("allayBrain");
        this.getBrain().tick((ServerWorld)this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("allayActivityUpdate");
        AllayAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide && this.isAlive() && this.tickCount % 10 == 0) {
            this.heal(1.0f);
        }
        if (this.isDancing() && this.shouldStopDancing() && this.tickCount % 20 == 0) {
            this.setDancing(false);
            this.jukeboxPos = null;
        }
        this.updateDuplicationCooldown();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.holdingItemAnimationTicks0 = this.holdingItemAnimationTicks;
            this.holdingItemAnimationTicks = this.hasItemInHand() ? MathHelper.clamp(this.holdingItemAnimationTicks + 1.0f, 0.0f, 5.0f) : MathHelper.clamp(this.holdingItemAnimationTicks - 1.0f, 0.0f, 5.0f);
            if (this.isDancing()) {
                this.dancingAnimationTicks += 1.0f;
                this.spinningAnimationTicks0 = this.spinningAnimationTicks;
                this.spinningAnimationTicks = this.isSpinning() ? (this.spinningAnimationTicks += 1.0f) : (this.spinningAnimationTicks -= 1.0f);
                this.spinningAnimationTicks = MathHelper.clamp(this.spinningAnimationTicks, 0.0f, 15.0f);
            } else {
                this.dancingAnimationTicks = 0.0f;
                this.spinningAnimationTicks = 0.0f;
                this.spinningAnimationTicks0 = 0.0f;
            }
        } else {
            if (this.dynamicVibrationListener != null) {
                this.dynamicVibrationListener.getListener().tick(this.level);
            } else {
                if (this.level.isLoaded(this.blockPosition()) && this.tickCount > 100) {
                    EntityPositionSource entityPositionSource = new EntityPositionSource(this, this.getEyeHeight());
                    this.dynamicVibrationListener = new DynamicGameEventListener<VibrationListener>(new VibrationListener(entityPositionSource, 16, this.vibrationListenerConfig));
                    this.updateDynamicGameEventListener(DynamicGameEventListener::move);
                }
            }
            if (this.isPanicking()) {
                this.setDancing(false);
            }
        }
    }

    @Override
    public boolean canPickUpLoot() {
        return !this.isOnPickupCooldown() && this.hasItemInHand();
    }

    public boolean hasItemInHand() {
        return !this.getItemInHand(Hand.MAIN_HAND).isEmpty();
    }

    @Override
    public boolean canTakeItem(ItemStack itemStack) {
        return false;
    }

    private boolean isOnPickupCooldown() {
        return this.getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleStatus.VALUE_PRESENT);
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        ItemStack itemStack2 = this.getItemInHand(Hand.MAIN_HAND);
        if (this.isDancing() && this.isDuplicationItem(itemStack) && this.canDuplicate()) {
            this.duplicateAllay();
            this.level.broadcastEntityEvent(this, (byte)18);
            this.level.playSound(player, this, SoundEvents.AMETHYST_BLOCK_CHIME, SoundCategory.NEUTRAL, 2.0f, 1.0f);
            this.removeInteractionItem(player, itemStack);
            return ActionResultType.SUCCESS;
        }
        if (itemStack2.isEmpty() && !itemStack.isEmpty()) {
            ItemStack itemStack3 = itemStack.copy();
            itemStack3.setCount(1);
            this.setItemInHand(Hand.MAIN_HAND, itemStack3);
            this.removeInteractionItem(player, itemStack);
            this.level.playSound(player, this, SoundEvents.ALLAY_ITEM_GIVEN, SoundCategory.NEUTRAL, 2.0f, 1.0f);
            this.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, player.getUUID());
            return ActionResultType.SUCCESS;
        }
        if (!itemStack2.isEmpty() && interactionHand == Hand.MAIN_HAND && itemStack.isEmpty()) {
            this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
            this.level.playSound(player, this, SoundEvents.ALLAY_ITEM_TAKEN, SoundCategory.NEUTRAL, 2.0f, 1.0f);
            this.swing(Hand.MAIN_HAND);
            for (ItemStack itemStack4 : this.getInventory().removeAllItems()) {
                BrainUtil.throwItem(this, itemStack4, this.position());
            }
            this.getBrain().eraseMemory(MemoryModuleType.LIKED_PLAYER);
            player.addItem(itemStack2);
            return ActionResultType.SUCCESS;
        }
        return super.mobInteract(player, interactionHand);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public Vector3i getPickupReach() {
        return ITEM_PICKUP_REACH;
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemStack) {
        ItemStack itemStack2 = this.getItemInHand(Hand.MAIN_HAND);
        return !itemStack2.isEmpty() && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.inventory.canAddItem(itemStack) && this.allayConsidersItemEqual(itemStack2, itemStack);
    }
    private boolean allayConsidersItemEqual(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack.sameItem(itemStack2) && !this.hasNonMatchingPotion(itemStack, itemStack2);
    }

    private boolean hasNonMatchingPotion(ItemStack itemStack, ItemStack itemStack2) {
        boolean bl;
        boolean bl2;
        CompoundNBT compoundTag = itemStack.getTag();
        boolean bl3 = bl2 = compoundTag != null && compoundTag.contains("Potion");
        if (!bl2) {
            return false;
        }
        CompoundNBT compoundTag2 = itemStack2.getTag();
        boolean bl4 = bl = compoundTag2 != null && compoundTag2.contains("Potion");
        if (!bl) {
            return true;
        }
        INBT tag = compoundTag.get("Potion");
        INBT tag2 = compoundTag2.get("Potion");
        return tag != null && tag2 != null && !tag.equals(tag2);
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        InventoryCarrier.pickUpItem(this, this, itemEntity);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPacketSender.sendEntityBrain(this);
    }

    public boolean isDancing() {
        return this.entityData.get(DATA_DANCING);
    }

    public boolean isPanicking() {
        return this.brain.getMemory(MemoryModuleType.IS_PANICKING).isPresent();
    }

    public void setDancing(boolean bl) {
        if (this.level.isClientSide || !this.isEffectiveAi() || bl && this.isPanicking()) {
            return;
        }
        this.entityData.set(DATA_DANCING, bl);
    }

    private boolean shouldStopDancing() {
        return this.jukeboxPos == null || !this.jukeboxPos.closerToCenterThan(this.position(), GameEvent.JUKEBOX_PLAY.getNotificationRadius()) || !this.level.getBlockState(this.jukeboxPos).is(Blocks.JUKEBOX);
    }

    public float getHoldingItemAnimationProgress(float f) {
        return MathHelper.lerp(f, this.holdingItemAnimationTicks0, this.holdingItemAnimationTicks) / 5.0f;
    }

    public boolean isSpinning() {
        float f = this.dancingAnimationTicks % 55.0f;
        return f < 15.0f;
    }

    public float getSpinningProgress(float f) {
        return MathHelper.lerp(f, this.spinningAnimationTicks0, this.spinningAnimationTicks) / 15.0f;
    }

    @Override
    public boolean equipmentHasChanged(ItemStack itemStack, ItemStack itemStack2) {
        return !this.allayConsidersItemEqual(itemStack, itemStack2);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
        ItemStack itemStack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
        if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack)) {
            this.spawnAtLocation(itemStack);
            this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return false;
    }


    @Override
    public void addAdditionalSaveData(CompoundNBT compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.writeInventoryToTag(compoundTag);
        if ((this.dynamicJukeboxListener == null || this.dynamicVibrationListener == null) && this.level.isLoaded(this.blockPosition()) && this.level instanceof ServerWorld) {
            EntityPositionSource entityPositionSource = new EntityPositionSource(this, this.getEyeHeight());

            this.dynamicVibrationListener = new DynamicGameEventListener<VibrationListener>(new VibrationListener(entityPositionSource, 16, this.vibrationListenerConfig));
            this.dynamicJukeboxListener = new DynamicGameEventListener<JukeboxListener>(new JukeboxListener(entityPositionSource, GameEvent.JUKEBOX_PLAY.getNotificationRadius()));
        }
        VibrationListener.codec(this.vibrationListenerConfig).encodeStart(NBTDynamicOps.INSTANCE, this.dynamicVibrationListener.getListener()).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(tag -> compoundTag.put("listener", tag));
        compoundTag.putLong("DuplicationCooldown", this.duplicationCooldown);
        compoundTag.putBoolean("CanDuplicate", this.canDuplicate());
    }


    @Override
    public void readAdditionalSaveData(CompoundNBT compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.readInventoryFromTag(compoundTag);
        this.duplicationCooldown = compoundTag.getInt("DuplicationCooldown");
        this.entityData.set(DATA_CAN_DUPLICATE, compoundTag.getBoolean("CanDuplicate"));

//        if (compoundTag.contains("listener", 10)) {
//            if (this.dynamicVibrationListener == null || this.dynamicJukeboxListener == null || this.vibrationListenerConfig == null) return;
//            if (this.firstTick || !this.level.isLoaded(this.blockPosition())) {
//                LOGGER.warn("Delaying VibrationListener update for Allay because the world is still loading.");
//
//                // Schedule a task to run later when the world is stable
//                this.level.getServer().execute(() -> {
//                    if (this.isAlive() && this.level.isLoaded(this.blockPosition())) {
//                        LOGGER.info("Applying delayed Allay VibrationListener update...");
//                        VibrationListener.codec(this.vibrationListenerConfig)
//                                .parse(NBTDynamicOps.INSTANCE, compoundTag.getCompound("listener"))
//                                .resultOrPartial(LOGGER::error)
//                                .ifPresent(vibrationListener ->
//                                        this.dynamicVibrationListener.updateListener((VibrationListener)vibrationListener, this.level));
//                    }
//                });
//                return;
//            }
//
//            VibrationListener.codec(this.vibrationListenerConfig)
//                    .parse(NBTDynamicOps.INSTANCE, compoundTag.getCompound("listener"))
//                    .resultOrPartial(LOGGER::error)
//                    .ifPresent(vibrationListener ->
//                            this.dynamicVibrationListener.updateListener((VibrationListener)vibrationListener, this.level));
//        }
    }

    @Override
    protected boolean shouldStayCloseToLeashHolder() {
        return false;
    }

    private void updateDuplicationCooldown() {
        if (this.duplicationCooldown > 0L) {
            --this.duplicationCooldown;
        }
        if (!this.level.isClientSide() && this.duplicationCooldown == 0L && !this.canDuplicate()) {
            this.entityData.set(DATA_CAN_DUPLICATE, true);
        }
    }

    private boolean isDuplicationItem(ItemStack itemStack) {
        return DUPLICATION_ITEM.test(itemStack);
    }

    private void duplicateAllay() {
        AllayEntity allay = EntityType.ALLAY.create(this.level);
        if (allay != null) {
            allay.moveTo(this.position());
            allay.setPersistenceRequired();
            allay.resetDuplicationCooldown();
            this.resetDuplicationCooldown();
            this.level.addFreshEntity(allay);
        }
    }

    private void resetDuplicationCooldown() {
        this.duplicationCooldown = 6000L;
        this.entityData.set(DATA_CAN_DUPLICATE, false);
    }

    private boolean canDuplicate() {
        return this.entityData.get(DATA_CAN_DUPLICATE);
    }

    private void removeInteractionItem(PlayerEntity player, ItemStack itemStack) {
        if (!player.abilities.instabuild) {
            itemStack.shrink(1);
        }
    }

    @Override
    public Vector3d getLeashOffset() {
        return new Vector3d(0.0, (double)this.getEyeHeight() * 0.6, (double)this.getBbWidth() * 0.1);
    }

    @Override
    public double getMyRidingOffset() {
        return 0.4;
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 18) {
            for (int i = 0; i < 3; ++i) {
                this.spawnHeartParticle();
            }
        } else {
            super.handleEntityEvent(by);
        }
    }

    private void spawnHeartParticle() {
        double d = this.random.nextGaussian() * 0.02;
        double d2 = this.random.nextGaussian() * 0.02;
        double d3 = this.random.nextGaussian() * 0.02;
        this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d, d2, d3);
    }


    class AllayVibrationListenerConfig
            implements VibrationListener.VibrationListenerConfig {
        AllayVibrationListenerConfig() {
        }

        @Override
        public boolean shouldListen(ServerWorld serverWorld, GameEventListener gameEventListener, BlockPos blockPos, GameEvent gameEvent, GameEvent.Context context) {
            if (AllayEntity.this.isNoAi()) {
                return false;
            }
            Optional<GlobalPos> optional = AllayEntity.this.getBrain().getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
            if (optional.isEmpty()) {
                return true;
            }
            GlobalPos globalPos = optional.get();
            return globalPos.dimension().equals(serverWorld.dimension()) && globalPos.pos().equals(blockPos);
        }

        @Override
        public void onSignalReceive(ServerWorld serverWorld, GameEventListener gameEventListener, BlockPos blockPos, GameEvent gameEvent, @Nullable Entity entity, @Nullable Entity entity2, float f) {
            if (gameEvent == GameEvent.NOTE_BLOCK_PLAY) {
                AllayAi.hearNoteblock(AllayEntity.this, new BlockPos(blockPos));
            }
        }

        @Override
        public ImmutableList<GameEvent> getListenableEvents() {
            return GameEvent.ALLAY_CAN_LISTEN;
        }
    }

    class JukeboxListener
            implements GameEventListener {
        private final PositionSource listenerSource;
        private final int listenerRadius;

        public JukeboxListener(PositionSource positionSource, int n) {
            this.listenerSource = positionSource;
            this.listenerRadius = n;
        }

        @Override
        public PositionSource getListenerSource() {
            return this.listenerSource;
        }

        @Override
        public int getListenerRadius() {
            return this.listenerRadius;
        }

        @Override
        public boolean handleGameEvent(ServerWorld serverWorld, GameEvent gameEvent, GameEvent.Context context, Vector3d vector3D) {
            if (gameEvent == GameEvent.JUKEBOX_PLAY) {
                AllayEntity.this.setJukeboxPlaying(new BlockPos(vector3D), true);
                return true;
            }
            if (gameEvent == GameEvent.JUKEBOX_STOP_PLAY) {
                AllayEntity.this.setJukeboxPlaying(new BlockPos(vector3D), false);
                return true;
            }
            return false;
        }
    }

    public void setJukeboxPlaying(BlockPos blockPos, boolean bl) {
        if (bl) {
            if (!this.isDancing()) {
                this.jukeboxPos = blockPos;
                this.setDancing(true);
            }
        } else if (blockPos.equals(this.jukeboxPos) || this.jukeboxPos == null) {
            this.jukeboxPos = null;
            this.setDancing(false);
        }
    }

}

