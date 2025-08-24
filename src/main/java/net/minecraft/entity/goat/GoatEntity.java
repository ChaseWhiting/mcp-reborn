package net.minecraft.entity.goat;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyDirtBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class GoatEntity extends Animal {
    public static final EntitySize LONG_JUMPING_DIMENSIONS = EntitySize.scalable(0.9F, 1.3F).scale(0.7F);
    private static final int ADULT_ATTACK_DAMAGE = 2;
    private static final int BABY_ATTACK_DAMAGE = 1;
    protected static final ImmutableList<SensorType<? extends Sensor<? super GoatEntity>>> SENSOR_TYPES =
            ImmutableList.of(SensorType.NEAREST_SAME_MOB, SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.GOAT_TEMPTATIONS, SensorType.NEAREST_ADULT);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES =
            ImmutableList.of(
                    MemoryModuleType.LOOK_TARGET,
                    MemoryModuleType.VISIBLE_LIVING_ENTITIES,
                    MemoryModuleType.WALK_TARGET,
                    MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                    MemoryModuleType.PATH,
                    MemoryModuleType.ATE_RECENTLY,
                    MemoryModuleType.BREED_TARGET,
                    MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS,
                    MemoryModuleType.LONG_JUMP_MID_JUMP,
                    MemoryModuleType.TEMPTING_PLAYER,
                    MemoryModuleType.NEAREST_VISIBLE_ADULT,
                    MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
                    MemoryModuleType.IS_TEMPTED,
                    MemoryModuleType.RAM_COOLDOWN_TICKS,
                    MemoryModuleType.RAM_TARGET,
                    MemoryModuleType.NEAREST_SAME_ENTITY,
                    MemoryModuleType.IS_PANICKING);

    private static final List<Block> GOATS_SPAWNABLE_ON = List.of(Blocks.STONE, Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.PACKED_ICE, Blocks.GRAVEL);



    public static boolean checkGoatSpawnRules(EntityType<? extends GoatEntity> entityType, IWorld levelAccessor, SpawnReason mobSpawnType, BlockPos blockPos, Random randomSource) {



        boolean isValid = blockPos.getY() > 129 ? true : ((Util.contains(GOATS_SPAWNABLE_ON, levelAccessor.getBlockState(blockPos.below()).getBlock())
                || Util.contains(GOATS_SPAWNABLE_ON, levelAccessor.getBlockState(blockPos).getBlock()))
                ||
                levelAccessor.getBlockState(blockPos.below()).getBlock() == Blocks.GRASS_BLOCK
                        && levelAccessor.getBlockState(blockPos.below()).getValue(SnowyDirtBlock.SNOWY) && blockPos.getY() > 90)
                && levelAccessor.getRawBrightness(blockPos, 0) > 8;
        return isValid;
    }

    public static final int GOAT_FALL_DAMAGE_REDUCTION = 10;
    public static final double GOAT_SCREAMING_CHANCE = 0.02;
    public static final double UNIHORN_CHANCE = (double)0.1f;
    private boolean isLoweringHead;
    private int lowerHeadTick;

    private static final DataParameter<Boolean> IS_SCREAMING_GOAT = EntityDataManager.defineId(GoatEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_HAS_LEFT_HORN = EntityDataManager.defineId(GoatEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_HAS_RIGHT_HORN = EntityDataManager.defineId(GoatEntity.class, DataSerializers.BOOLEAN);


    public GoatEntity(EntityType<? extends Animal> goat, World world) {
        super(goat, world);
        this.getNavigation().setCanFloat(true);
    }

    public ItemStack createHorn() {
        Random randomSource = new Random(this.getUUID().hashCode());
        List<Instrument> horns = this.isScreamingGoat() ?
                List.of(Instruments.YEARN_GOAT_HORN, Instruments.ADMIRE_GOAT_HORN, Instruments.CALL_GOAT_HORN, Instruments.DREAM_GOAT_HORN) :
                List.of(Instruments.PONDER_GOAT_HORN, Instruments.FEEL_GOAT_HORN, Instruments.SEEK_GOAT_HORN, Instruments.SING_GOAT_HORN);
        return InstrumentItem.setRandom(new ItemStack(Items.GOAT_HORN), horns, randomSource);
    }

    @Override
    protected Brain.BrainCodec<GoatEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<GoatEntity> makeBrain(Dynamic<?> dynamic) {
        return (Brain<GoatEntity>) GoatAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.2f).add(Attributes.ATTACK_DAMAGE, ADULT_ATTACK_DAMAGE);
    }

    protected void ageBoundaryReached() {
        if (this.isBaby()) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BABY_ATTACK_DAMAGE);
            this.removeHorns();
        } else {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ADULT_ATTACK_DAMAGE);
            this.addHorns();
        }
    }

    @Override
    protected int calculateFallDamage(float damageToDeal, float damageMultiplier) {
        return super.calculateFallDamage(damageToDeal, damageMultiplier) - 10;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isScreamingGoat()) {
            return SoundEvents.GOAT_SCREAMING_AMBIENT;
        }
        return SoundEvents.GOAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (this.isScreamingGoat()) {
            return SoundEvents.GOAT_SCREAMING_HURT;
        }
        return SoundEvents.GOAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        if (this.isScreamingGoat()) {
            return SoundEvents.GOAT_SCREAMING_DEATH;
        }
        return SoundEvents.GOAT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.GOAT_STEP, 0.15f, 1.0f);
    }

    protected SoundEvent getMilkingSound() {
        if (this.isScreamingGoat()) {
            return SoundEvents.GOAT_SCREAMING_MILK;
        }
        return SoundEvents.GOAT_MILK;
    }

    @Override
    @Nullable
    public GoatEntity getBreedOffspring(ServerWorld serverLevel, AgeableEntity ageableMob) {
        GoatEntity goat = EntityType.GOAT.create(serverLevel);
        if (goat != null) {
            AgeableEntity ageableMob2;
            GoatAi.initMemories(goat, serverLevel.getRandom());
            AgeableEntity ageableMob3 = serverLevel.getRandom().nextBoolean() ? this : ageableMob;
            boolean bl = ageableMob3 instanceof GoatEntity && ((GoatEntity)(ageableMob2 = ageableMob3)).isScreamingGoat() || serverLevel.getRandom().nextDouble() < 0.02;
            goat.setScreamingGoat(bl);
        }
        return goat;
    }

    public Brain<GoatEntity> getBrain() {
        return (Brain<GoatEntity>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("goatBrain");
        this.getBrain().tick((ServerWorld) this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("goatActivityUpdate");
        GoatAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public int getMaxHeadYRot() {
        return 15;
    }

    @Override
    public void setYHeadRot(float f) {
        int n = this.getMaxHeadYRot();
        float f2 = MathHelper.degreesDifference(this.yBodyRot, f);
        float f3 = MathHelper.clamp(f2, (float)(-n), (float)n);
        super.setYHeadRot(this.yBodyRot + f3);
    }


    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.getItem() == (Items.BUCKET) && !this.isBaby()) {
            player.playSound(this.getMilkingSound(), 1.0f, 1.0f);
            ItemStack itemStack2 = DrinkHelper.createFilledResult(itemStack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(interactionHand, itemStack2);
            return ActionResultType.sidedSuccess(this.level.isClientSide);
        }
        ActionResultType interactionResult = super.mobInteract(player, interactionHand);
        if (interactionResult.consumesAction() && this.isFood(itemStack)) {
            this.level.playSound(null, this, this.getEatingSound(itemStack), SoundCategory.NEUTRAL, 1.0f, MathHelper.randomBetween(this.level.random, 0.8f, 1.2f));
        }
        return interactionResult;
    }

    public ILivingEntityData finalizeSpawn(IServerWorld world,
                                           DifficultyInstance difficulty,
                                           SpawnReason reason,
                                           @Nullable ILivingEntityData spawnData,
                                           @Nullable CompoundNBT data) {

        Random randomSource = world.getRandom();
        GoatAi.initMemories(this, randomSource);
        this.setScreamingGoat(randomSource.nextDouble() < GOAT_SCREAMING_CHANCE);
        this.ageBoundaryReached();
        if (!this.isBaby() && (double)randomSource.nextFloat() < (double)UNIHORN_CHANCE) {
            DataParameter<Boolean> entityDataAccessor = randomSource.nextBoolean() ? DATA_HAS_LEFT_HORN : DATA_HAS_RIGHT_HORN;
            this.entityData.set(entityDataAccessor, false);
        }

        return super.finalizeSpawn(world, difficulty, reason, spawnData, data);
    }


    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPacketSender.sendEntityBrain(this);
    }

    @Override
    public EntitySize getDimensions(Pose pose) {
        return pose == Pose.LONG_JUMPING ? LONG_JUMPING_DIMENSIONS.scale(this.getScale()) : super.getDimensions(pose);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("IsScreamingGoat", this.isScreamingGoat());
        compoundTag.putBoolean("HasLeftHorn", this.hasLeftHorn());
        compoundTag.putBoolean("HasRightHorn", this.hasRightHorn());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setScreamingGoat(compoundTag.getBoolean("IsScreamingGoat"));
        this.entityData.set(DATA_HAS_LEFT_HORN, compoundTag.getBoolean("HasLeftHorn"));
        this.entityData.set(DATA_HAS_RIGHT_HORN, compoundTag.getBoolean("HasRightHorn"));
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 58) {
            this.isLoweringHead = true;
        } else if (by == 59) {
            this.isLoweringHead = false;
        } else {
            super.handleEntityEvent(by);
        }
    }

    @Override
    public void aiStep() {
        this.lowerHeadTick = this.isLoweringHead ? ++this.lowerHeadTick : (this.lowerHeadTick -= 2);
        this.lowerHeadTick = MathHelper.clamp(this.lowerHeadTick, 0, 20);
        super.aiStep();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_SCREAMING_GOAT, false);
        this.entityData.define(DATA_HAS_LEFT_HORN, true);
        this.entityData.define(DATA_HAS_RIGHT_HORN, true);
    }


    public boolean hasLeftHorn() {
        return this.entityData.get(DATA_HAS_LEFT_HORN);
    }

    public boolean hasRightHorn() {
        return this.entityData.get(DATA_HAS_RIGHT_HORN);
    }

    public boolean dropHorn() {
        boolean bl = this.hasLeftHorn();
        boolean bl2 = this.hasRightHorn();
        if (!bl && !bl2) {
            return false;
        }
        DataParameter<Boolean> entityDataAccessor = !bl ? DATA_HAS_RIGHT_HORN : (!bl2 ? DATA_HAS_LEFT_HORN : (this.random.nextBoolean() ? DATA_HAS_LEFT_HORN : DATA_HAS_RIGHT_HORN));
        this.entityData.set(entityDataAccessor, false);
        Vector3d vec3 = this.position();
        ItemStack itemStack = this.createHorn();
        double d = MathHelper.randomBetween(this.random, -0.2f, 0.2f);
        double d2 = MathHelper.randomBetween(this.random, 0.3f, 0.7f);
        double d3 = MathHelper.randomBetween(this.random, -0.2f, 0.2f);
        ItemEntity itemEntity = new ItemEntity(this.level, vec3.x(), vec3.y(), vec3.z(), itemStack, d, d2, d3);
        this.level.addFreshEntity(itemEntity);
        return true;
    }


    public void addHorns() {
        this.entityData.set(DATA_HAS_LEFT_HORN, true);
        this.entityData.set(DATA_HAS_RIGHT_HORN, true);
    }

    public void removeHorns() {
        this.entityData.set(DATA_HAS_LEFT_HORN, false);
        this.entityData.set(DATA_HAS_RIGHT_HORN, false);
    }


    public boolean isScreamingGoat() {
        return this.entityData.get(IS_SCREAMING_GOAT);
    }

    public void setScreamingGoat(boolean bl) {
        this.entityData.set(IS_SCREAMING_GOAT, bl);
    }

    public float getRammingXHeadRot() {
        return (float)this.lowerHeadTick / 20.0f * 30.0f * ((float)Math.PI / 180);
    }


}
