package net.minecraft.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.netherinvasion.invader.AbstractNetherInvaderEntity;
import net.minecraft.world.netherinvasion.invader.MoveTowardsInvasionGoal;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.logging.Logger;

public class PiglinBruteEntity extends AbstractPiglinEntity implements ICrossbowUser {
    private static final DataParameter<Boolean> DATA_IS_CHARGING_CROSSBOW = EntityDataManager.defineId(PiglinBruteEntity.class, DataSerializers.BOOLEAN);
    protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinBruteEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_BRUTE_SPECIFIC_SENSOR);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.HOME);
    public PiglinBruteEntity(EntityType<? extends PiglinBruteEntity> p_i241917_1_, World p_i241917_2_) {
        super(p_i241917_1_, p_i241917_2_);
        this.xpReward = 20;
    }

    protected static Map<Enchantment, Integer> RANDOM_CROSSBOW_ENCHANT = new HashMap<>();

    static {
        Random random = new Random();
        RANDOM_CROSSBOW_ENCHANT.put(Enchantments.MULTISHOT, 1);
        RANDOM_CROSSBOW_ENCHANT.put(Enchantments.QUICK_CHARGE, random.nextInt(2 + 1));
        RANDOM_CROSSBOW_ENCHANT.put(Enchantments.PIERCING, random.nextInt(2 + 1));
        RANDOM_CROSSBOW_ENCHANT.put(Enchantments.UNBREAKING, random.nextInt(2 + 1));
    }

    private ItemStack storedCrossbow = new ItemStack(Items.GILDED_CROSSBOW);

    private ItemStack storedAxe = new ItemStack(Items.GOLDEN_AXE);

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 50.0D).add(Attributes.MOVEMENT_SPEED, (double) 0.35F).add(Attributes.ATTACK_DAMAGE, 7.0D).add(Attributes.FOLLOW_RANGE, 16D);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }

    public void registerGoals() {

        this.goalSelector.addGoal(3, new MoveTowardsInvasionGoal<>(this));
        this.goalSelector.addGoal(2, new AbstractNetherInvaderEntity.FindTargetGoal(this, 10.0F));
        this.goalSelector.addGoal(2, new AbstractNetherInvaderEntity.PromoteLeaderGoal<>(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));

    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficultyInstance, SpawnReason spawnReason, @Nullable ILivingEntityData iLivingEntityData, @Nullable CompoundNBT compoundNBT) {
        PiglinBruteBrain.initMemories(this);
        this.populateDefaultEquipmentSlots(difficultyInstance);
        this.setImmuneToZombification(true);
        EnchantmentData enchantmentData = this.getRandomCrossbowEnchant();
        if (enchantmentData != null) {
            Enchantment enchantment = enchantmentData.enchantment;
            int enchantmentLevel = (Integer) enchantmentData.level;

            storedCrossbow.enchant(enchantment, enchantmentLevel);
        }
        this.setDropChance(EquipmentSlotType.MAINHAND, 0.02F);
        return super.finalizeSpawn(world, difficultyInstance, spawnReason, iLivingEntityData, compoundNBT);
    }

    public EnchantmentData getRandomCrossbowEnchant() {
        List<Enchantment> enchantments = new ArrayList<>(RANDOM_CROSSBOW_ENCHANT.keySet());
        Enchantment randomEnchantment = enchantments.get(random.nextInt(enchantments.size()));
        int level = RANDOM_CROSSBOW_ENCHANT.get(randomEnchantment);
        return new EnchantmentData(randomEnchantment, level);
    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
        this.setItemSlot(EquipmentSlotType.MAINHAND, storedAxe);


    }

    public void dealWithItems() {
        if (this.getTarget() != null) {
            if (this.getTarget() instanceof AbstractNetherInvaderEntity) {
                this.setTarget(null);
            }
            List<AbstractPiglinEntity> piglins2 = this.level.getEntitiesOfClass(AbstractPiglinEntity.class, this.getBoundingBox().inflate(1D, 1D, 1D), e -> e != this);
            List<AbstractPiglinEntity> piglins = this.level.getEntitiesOfClass(AbstractPiglinEntity.class, this.getTarget().getBoundingBox().inflate(1.1D, 3D, 1.1D), e -> e != this);
            if (this.distanceTo(this.getTarget()) < 2.5F) {
                if (this.storedAxe != null) {
                    this.setItemSlot(EquipmentSlotType.MAINHAND, this.storedAxe);
                } else {
                    this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
                }
            } else if (piglins.isEmpty()) {
                if (this.storedCrossbow == null) {
                    this.storedCrossbow = this.getItemBySlot(EquipmentSlotType.MAINHAND).copy();
                }
                this.maybeUseCrossbow();
            } else {
                if (this.storedAxe != null) {
                    this.setItemSlot(EquipmentSlotType.MAINHAND, this.storedAxe);
                } else {
                    this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
                }
            }
        }
    }


    private void maybeUseCrossbow() {
        if (this.level.random.nextFloat() < 0.06F) {
            if (this.storedAxe == null) {
                this.storedAxe = this.getItemBySlot(EquipmentSlotType.MAINHAND).copy();
            }
            if (this.storedCrossbow == null) {
                this.storedCrossbow = new ItemStack(Items.GILDED_CROSSBOW);
            }
            this.setItemSlot(EquipmentSlotType.MAINHAND, this.storedCrossbow);
        }
    }

    @Override
    public void tick() {
        super.tick();
        dealWithItems();
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);

        if (this.storedCrossbow != null) {
            CompoundNBT crossbowNBT = new CompoundNBT();
            this.storedCrossbow.save(crossbowNBT);
            compoundNBT.put("StoredCrossbow", crossbowNBT);
        }

        if (this.storedAxe != null) {
            CompoundNBT axeNBT = new CompoundNBT();
            this.storedAxe.save(axeNBT);
            compoundNBT.put("StoredAxe", axeNBT);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);

        if (compoundNBT.contains("StoredCrossbow")) {
            this.storedCrossbow = ItemStack.of(compoundNBT.getCompound("StoredCrossbow"));
        }

        if (compoundNBT.contains("StoredAxe")) {
            this.storedAxe = ItemStack.of(compoundNBT.getCompound("StoredAxe"));
        }
    }

    protected Brain.BrainCodec<PiglinBruteEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    protected Brain<?> makeBrain(Dynamic<?> p_213364_1_) {
        return PiglinBruteBrain.makeBrain(this, this.brainProvider().makeBrain(p_213364_1_));
    }

    public Brain<PiglinBruteEntity> getBrain() {
        return (Brain<PiglinBruteEntity>) super.getBrain();
    }

    public boolean canHunt() {
        return false;
    }

    public boolean wantsToPickUp(ItemStack p_230293_1_) {
        return p_230293_1_.getItem() == Items.GOLDEN_AXE ? super.wantsToPickUp(p_230293_1_) : false;
    }

    protected void customServerAiStep() {
        this.level.getProfiler().push("piglinBruteBrain");
        this.getBrain().tick((ServerWorld) this.level, this);
        this.level.getProfiler().pop();
        PiglinBruteBrain.updateActivity(this);
        PiglinBruteBrain.maybePlayActivitySound(this);
        super.customServerAiStep();
    }

    @OnlyIn(Dist.CLIENT)
    public PiglinAction getArmPose() {
        if (this.isAggressive() && this.isHoldingMeleeWeapon()) {
            return PiglinAction.ATTACKING_WITH_MELEE_WEAPON;
        } else if (this.isChargingCrossbow()) {
            return PiglinAction.CROSSBOW_CHARGE;
        } else {
            return this.isAggressive() && this.isHolding(Items.CROSSBOW) || this.isAggressive() && this.isHolding(Items.GILDED_CROSSBOW) ? PiglinAction.CROSSBOW_HOLD : PiglinAction.DEFAULT;
        }
    }

    public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
        boolean flag = super.hurt(p_70097_1_, p_70097_2_);
        if (this.level.isClientSide) {
            return false;
        } else {
            if (flag && p_70097_1_.getEntity() instanceof LivingEntity) {
                PiglinBruteBrain.wasHurtBy(this, (LivingEntity) p_70097_1_.getEntity());
            }

            return flag;
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PIGLIN_BRUTE_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.PIGLIN_BRUTE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PIGLIN_BRUTE_DEATH;
    }

    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
        this.playSound(SoundEvents.PIGLIN_BRUTE_STEP, 0.15F, 1.0F);
    }

    protected void playAngrySound() {
        this.playSound(SoundEvents.PIGLIN_BRUTE_ANGRY, 1.0F, this.getVoicePitch());
    }

    protected void playConvertedSound() {
        this.playSound(SoundEvents.PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED, 1.0F, this.getVoicePitch());
    }

    // ICrossbowUser implementation
    @Override
    public void setChargingCrossbow(boolean isCharging) {
        this.getEntityData().set(DATA_IS_CHARGING_CROSSBOW, isCharging);
    }

    private boolean isChargingCrossbow() {
        return this.entityData.get(DATA_IS_CHARGING_CROSSBOW);
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity target, ItemStack crossbow, ProjectileEntity projectile, float soundPitch) {
        this.shootCrossbowProjectile(this, target, projectile, soundPitch, 1.6F);
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        return this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse((LivingEntity) null);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHARGING_CROSSBOW, false);
    }

    @Override
    public void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_) {

    }

    @Override
    public boolean isImmuneToZombification() {
        return true;
    }

    public void performCrossbowAttack(LivingEntity target, float velocity) {
        Hand hand;
        ItemStack itemstack;

        if (this.isHolding(Items.CROSSBOW)) {
            hand = ProjectileHelper.getWeaponHoldingHand(this, Items.CROSSBOW);
            itemstack = this.getItemInHand(hand);
            CrossbowItem.performShooting(this.level, this, hand, itemstack, velocity, (float) (14 - this.level.getDifficulty().getId() * 4));
        } else if (this.isHolding(Items.GILDED_CROSSBOW)) {
            hand = ProjectileHelper.getWeaponHoldingHand(this, Items.GILDED_CROSSBOW);
            itemstack = this.getItemInHand(hand);
            GildedCrossbowItem.performShooting(this.level, this, hand, itemstack, velocity, (float) (14 - this.level.getDifficulty().getId() * 4));
        } else if (AbstractCrossbowItem.isHoldingAbstractCrossbowItem(this)) {
            hand = AbstractCrossbowItem.getHandHoldingAbstractCrossbowItem(this);
            itemstack = this.getItemInHand(hand);
            AbstractCrossbowItem.performShooting(this.level, this, hand, itemstack, velocity, (float) (14 - this.level.getDifficulty().getId() * 4));
        }

        this.onCrossbowAttackPerformed();
    }

    @Override
    public boolean canFireProjectileWeapon(ShootableItem shootable) {
        return shootable == Items.CROSSBOW || shootable == Items.GILDED_CROSSBOW || shootable instanceof AbstractCrossbowItem;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        this.performCrossbowAttack(this, 1.6F);
    }
}
