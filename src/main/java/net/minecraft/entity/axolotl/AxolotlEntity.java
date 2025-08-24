package net.minecraft.entity.axolotl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.controller.SmoothSwimmingLookControl;
import net.minecraft.entity.ai.controller.SmoothSwimmingMoveControl;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.passive.AxolotlVariantSpawnRules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;

public class AxolotlEntity extends Animal implements Bucketable, VariantHolder<AxolotlEntity.AxolotlVariant> {
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super AxolotlEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_ATTACKABLES, SensorType.AXOLOTL_TEMPTATIONS);
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.HAS_HUNTING_COOLDOWN);
    private static final DataParameter<Integer> DATA_VARIANT = EntityDataManager.defineId(AxolotlEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> DATA_PLAYING_DEAD = EntityDataManager.defineId(AxolotlEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FROM_BUCKET = EntityDataManager.defineId(AxolotlEntity.class, DataSerializers.BOOLEAN);
    public static final double PLAYER_REGEN_DETECTION_RANGE = 20.0;
    public static final int RARE_VARIANT_CHANCE = 1200;
    private static final int AXOLOTL_TOTAL_AIR_SUPPLY = 6000;
    public static final String VARIANT_TAG = "Variant";
    private static final int REHYDRATE_AIR_SUPPLY = 1800;
    private static final int REGEN_BUFF_MAX_DURATION = 2400;
    private final Map<String, Vector3f> modelRotationValues = Maps.newHashMap();
    private static final int REGEN_BUFF_BASE_DURATION = 100;

    public AxolotlEntity(EntityType<? extends AxolotlEntity> entityType, World level) {
        super(entityType, level);
        this.setPathfindingMalus(PathNodeType.WATER, 0.0f);
        this.moveControl = new AxolotlMoveControl(this);
        this.lookControl = new AxolotlLookControl(this, 20);
        this.maxUpStep = 1.0f;
    }

    public Map<String, Vector3f> getModelRotationValues() {
        return this.modelRotationValues;
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, IWorldReader levelReader) {
        return 0.0f;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
        this.entityData.define(DATA_PLAYING_DEAD, false);
        this.entityData.define(FROM_BUCKET, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt(VARIANT_TAG, this.getVariant().getId());
        compoundTag.putBoolean("FromBucket", this.fromBucket());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setVariant(AxolotlVariant.BY_ID[compoundTag.getInt(VARIANT_TAG)]);
        this.setFromBucket(compoundTag.getBoolean("FromBucket"));
    }

    @Override
    public void playAmbientSound() {
        if (this.isPlayingDead()) {
            return;
        }
        super.playAmbientSound();
    }

    @Override
    public void baseTick() {
        int n = this.getAirSupply();
        super.baseTick();
        if (!this.isNoAi()) {
            this.handleAirSupply(n);
        }
    }

    protected void handleAirSupply(int n) {
        if (this.isAlive() && !this.isInWaterRainOrBubble()) {
            this.setAirSupply(n - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(DamageSource.DRY_OUT, 2.0f);
            }
        } else {
            this.setAirSupply(this.getMaxAirSupply());
        }
    }

    public void rehydrate() {
        int n = this.getAirSupply() + 1800;
        this.setAirSupply(Math.min(n, this.getMaxAirSupply()));
    }

    public boolean isDryingOut() {
        return this.getAirSupply() < this.getMaxAirSupply();
    }

    @Override
    public int getMaxAirSupply() {
        return 6000;
    }

    public AxolotlVariant getVariant() {
        return AxolotlVariant.BY_ID[this.entityData.get(DATA_VARIANT)];
    }

    public void setVariant(AxolotlVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    static boolean useRareVariant(Random random) {
        return random.nextInt(1200) == 0;
    }

    @Override
    public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
        return p_205019_1_.isUnobstructed(this);
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
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public CreatureAttribute getMobType() {
        return CreatureAttribute.WATER;
    }

    public void setPlayingDead(boolean bl) {
        this.entityData.set(DATA_PLAYING_DEAD, bl);
    }

    public boolean isPlayingDead() {
        return this.entityData.get(DATA_PLAYING_DEAD);
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean bl) {
        this.entityData.set(FROM_BUCKET, bl);
    }


    @Override
    @Nullable
    public AgeableEntity getBreedOffspring(ServerWorld serverLevel, AgeableEntity ageableMob) {
        AxolotlEntity axolotl = EntityType.AXOLOTL.create(serverLevel);
        if (axolotl != null) {
            AxolotlVariant variant = AxolotlEntity.useRareVariant(this.random) ? AxolotlVariant.getRareSpawnVariant(this.random) : (this.random.nextBoolean() ? this.getVariant() : ((AxolotlEntity)ageableMob).getVariant());
            axolotl.setVariant(variant);
            axolotl.setPersistenceRequired();
        }
        return axolotl;
    }

    public double getMeleeAttackRangeSqr(LivingEntity livingEntity) {
        return 1.5 + (double)livingEntity.getBbWidth() * 2.0;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.get() == Items.TROPICAL_FISH_BUCKET;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("axolotlBrain");
        this.getBrain().tick((ServerWorld) this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("axolotlActivityUpdate");
        AxolotlAi.updateActivity(this);
        this.level.getProfiler().pop();
        if (!this.isNoAi()) {
            Optional<Integer> optional = this.getBrain().getMemory(MemoryModuleType.PLAY_DEAD_TICKS);
            this.setPlayingDead(optional.isPresent() && optional.get() > 0);
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 14.0).add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected PathNavigator createNavigation(World p_175447_1_) {
        return new AxolotlPathNavigation(this, p_175447_1_);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean bl = entity.hurt(DamageSource.mobAttack(this), (int)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (bl) {
            this.doEnchantDamageEffects(this, entity);
            this.playSound(SoundEvents.AXOLOTL_ATTACK, 1.0f, 1.0f);
        }
        return bl;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        float f2 = this.getHealth();
        if (!(this.level.isClientSide || this.isNoAi() || this.level.random.nextInt(3) != 0 || !((float)this.level.random.nextInt(3) < f) && !(f2 / this.getMaxHealth() < 0.5f) || !(f < f2) || !this.isInWater() || damageSource.getEntity() == null && damageSource.getDirectEntity() == null || this.isPlayingDead())) {
            this.brain.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, 200);
        }
        return super.hurt(damageSource, f);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize entityDimensions) {
        return entityDimensions.height * 0.655f;
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand interactionHand) {
        return Bucketable.bucketMobPickup(player, interactionHand, this).orElse(super.mobInteract(player, interactionHand));
    }

    static class AxolotlMoveControl
            extends SmoothSwimmingMoveControl {
        private final AxolotlEntity axolotl;

        public AxolotlMoveControl(AxolotlEntity axolotl) {
            super(axolotl, 85, 10, 0.1f, 0.5f, false);
            this.axolotl = axolotl;
        }

        @Override
        public void tick() {
            if (!this.axolotl.isPlayingDead()) {
                super.tick();
            }
        }
    }

    @Override
    public void saveToBucketTag(ItemStack itemStack) {
        Bucketable.saveDefaultDataToBucketTag(this, itemStack);
        CompoundNBT compoundTag = itemStack.getOrCreateTag();
        compoundTag.putInt(VARIANT_TAG, this.getVariant().getId());
        compoundTag.putInt("Age", this.getAge());
        Brain<AxolotlEntity> brain = this.getBrain();
        if (brain.hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN)) {
            compoundTag.putLong("HuntingCooldown", brain.getTimeUntilExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN));
        }
    }

    @Override
    public void loadFromBucketTag(CompoundNBT compoundTag) {
        Bucketable.loadDefaultDataFromBucketTag(this, compoundTag);
        this.setVariant(AxolotlVariant.BY_ID[compoundTag.getInt(VARIANT_TAG)]);
        if (compoundTag.contains("Age")) {
            this.setAge(compoundTag.getInt("Age"));
        }
        if (compoundTag.contains("HuntingCooldown")) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, compoundTag.getLong("HuntingCooldown"));
        }
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.AXOLOTL_BUCKET);
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_AXOLOTL;
    }


    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.isPlayingDead() && super.canBeSeenAsEnemy();
    }

    class AxolotlLookControl
            extends SmoothSwimmingLookControl {
        public AxolotlLookControl(AxolotlEntity axolotl2, int n) {
            super(axolotl2, n);
        }

        @Override
        public void tick() {
            if (!AxolotlEntity.this.isPlayingDead()) {
                super.tick();
            }
        }
    }



    public static void onStopAttacking(AxolotlEntity axolotl) {
        Entity entity;
        DamageSource damageSource;
        Optional<LivingEntity> optional = axolotl.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if (!optional.isPresent()) {
            return;
        }
        World level = axolotl.level;
        LivingEntity livingEntity = optional.get();
        if (livingEntity.isDeadOrDying() && (damageSource = livingEntity.getLastDamageSource()) != null && (entity = damageSource.getEntity()) != null && entity.getType() == EntityType.PLAYER) {
            PlayerEntity player = (PlayerEntity) entity;
            List<PlayerEntity> list = level.getEntitiesOfClass(PlayerEntity.class, axolotl.getBoundingBox().inflate(20.0));
            if (list.contains(player)) {
                axolotl.applySupportingEffects(player);
            }
        }
    }

    public void applySupportingEffects(PlayerEntity player) {
        int n;
        EffectInstance mobEffectInstance = player.getEffect(Effects.REGENERATION);
        int n2 = n = mobEffectInstance != null ? mobEffectInstance.getDuration() : 0;
        if (n < 2400) {
            n = Math.min(2400, 100 + n);
            player.addEffect(new EffectInstance(Effects.REGENERATION, n, 0));
        }
        player.removeEffect(Effects.DIG_SLOWDOWN);
    }

    public static AxolotlVariant getRandomAxolotlVariant(World serverLevelAccessor, BlockPos blockPos) {
        if (serverLevelAccessor.getBiomeName(blockPos).isEmpty()) return AxolotlVariantSpawnRules.getVariant(Biomes.PLAINS, serverLevelAccessor.random);
        RegistryKey<Biome> holder = serverLevelAccessor.getBiomeName(blockPos).get();
        return AxolotlVariantSpawnRules.getVariant(holder, serverLevelAccessor.getRandom());
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld serverLevelAccessor, DifficultyInstance difficultyInstance, SpawnReason mobSpawnType, @Nullable ILivingEntityData spawnGroupData, @Nullable CompoundNBT compoundTag) {
        boolean bl = false;
        if (mobSpawnType == SpawnReason.BUCKET) {
            return spawnGroupData;
        }
        if (spawnGroupData instanceof AxolotlGroupData) {
            if (((AxolotlGroupData)spawnGroupData).getGroupSize() >= 2) {
                bl = true;
            }
        } else {
            spawnGroupData = new AxolotlGroupData(AxolotlVariant.getCommonSpawnVariant(this.level.random), AxolotlVariant.getCommonSpawnVariant(this.level.random));
        }
        this.setVariant(getRandomAxolotlVariant(this.level, blockPosition()));
        if (bl) {
            this.setAge(-24000);
        }
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.AXOLOTL_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.AXOLOTL_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.AXOLOTL_IDLE_WATER : SoundEvents.AXOLOTL_IDLE_AIR;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.AXOLOTL_SPLASH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.AXOLOTL_SWIM;
    }

    protected Brain.BrainCodec<AxolotlEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return AxolotlAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    public Brain<AxolotlEntity> getBrain() {
        return (Brain<AxolotlEntity>) super.getBrain();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPacketSender.sendEntityBrain(this);
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return !this.fromBucket() && !this.hasCustomName();
    }



    public static enum AxolotlVariant {
        LUCY(0, "lucy", true),
        WILD(1, "wild", true),
        GOLD(2, "gold", true),
        CYAN(3, "cyan", true),
        BLUE(4, "blue", false);

        public static final AxolotlVariant[] BY_ID;
        private final int id;
        private final String name;
        private final boolean common;

        private AxolotlVariant(int n2, String string2, boolean bl) {
            this.id = n2;
            this.name = string2;
            this.common = bl;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static AxolotlVariant getCommonSpawnVariant(Random random) {
            return AxolotlVariant.getSpawnVariant(random, true);
        }

        public static AxolotlVariant getRareSpawnVariant(Random random) {
            return AxolotlVariant.getSpawnVariant(random, false);
        }

        private static AxolotlVariant getSpawnVariant(Random random, boolean bl) {
            AxolotlVariant[] variantArray = (AxolotlVariant[]) Arrays.stream(BY_ID).filter(variant -> variant.common == bl).toArray(AxolotlVariant[]::new);
            return Util.getRandom(variantArray, random);
        }

        static {
            BY_ID = (AxolotlVariant[])Arrays.stream(AxolotlVariant.values()).sorted(Comparator.comparingInt(AxolotlVariant::getId)).toArray(AxolotlVariant[]::new);
        }
    }

    static class AxolotlPathNavigation
            extends SwimmerPathNavigator {
        AxolotlPathNavigation(AxolotlEntity axolotl, World level) {
            super(axolotl, level);
        }

        @Override
        protected boolean canUpdatePath() {
            return true;
        }

        @Override
        protected PathFinder createPathFinder(int n) {
            this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
            return new PathFinder(this.nodeEvaluator, n);
        }

        @Override
        public boolean isStableDestination(BlockPos blockPos) {
            return !this.level.getBlockState(blockPos.below()).isAir();
        }
    }

    public static class AxolotlGroupData
            extends AgeableEntity.AgeableData {
        public final AxolotlVariant[] types;

        public AxolotlGroupData(AxolotlVariant ... variantArray) {
            super(false);
            this.types = variantArray;
        }

        public AxolotlVariant getVariant(Random random) {
            if (AxolotlEntity.useRareVariant(random)) {
                return AxolotlVariant.getRareSpawnVariant(random);
            }
            return this.types[random.nextInt(this.types.length)];
        }
    }
}
