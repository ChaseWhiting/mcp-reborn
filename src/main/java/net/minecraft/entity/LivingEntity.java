package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.enchantment.LavaWalkerEnchantment;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.tool.terraria.AccessoryHolderItem;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.fallout.Radiation;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public abstract class LivingEntity extends Entity {
    private static final UUID SPEED_MODIFIER_SPRINTING_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final UUID SPEED_MODIFIER_SOUL_SPEED_UUID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");
    private static final AttributeModifier SPEED_MODIFIER_SPRINTING = new AttributeModifier(SPEED_MODIFIER_SPRINTING_UUID, "Sprinting speed boost", (double) 0.3F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    protected static final DataParameter<Byte> DATA_LIVING_ENTITY_FLAGS = EntityDataManager.defineId(LivingEntity.class, DataSerializers.BYTE);
    static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager.defineId(LivingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> DATA_EFFECT_COLOR_ID = EntityDataManager.defineId(LivingEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> DATA_EFFECT_AMBIENCE_ID = EntityDataManager.defineId(LivingEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> DATA_ARROW_COUNT_ID = EntityDataManager.defineId(LivingEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> DATA_STINGER_COUNT_ID = EntityDataManager.defineId(LivingEntity.class, DataSerializers.INT);
    private static final DataParameter<Optional<BlockPos>> SLEEPING_POS_ID = EntityDataManager.defineId(LivingEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntitySize SLEEPING_DIMENSIONS = EntitySize.fixed(0.2F, 0.2F);
    private final AttributeModifierManager attributes;
    private final CombatTracker combatTracker = new CombatTracker(this);
    private final Map<Effect, EffectInstance> activeEffects = Maps.newHashMap();
    private final NonNullList<ItemStack> lastHandItemStacks = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> lastArmorItemStacks = NonNullList.withSize(4, ItemStack.EMPTY);
    public boolean swinging;
    public Hand swingingArm;
    public int swingTime;
    public int removeArrowTime;
    public int removeStingerTime;
    public int hurtTime;
    public int hurtDuration;
    public float hurtDir;
    public int deathTime;
    public float oAttackAnim;
    public float attackAnim;
    protected int attackStrengthTicker;
    public float animationSpeedOld;
    public float animationSpeed;
    public float animationPosition;
    public final int invulnerableDuration = 20;
    public final float timeOffs;
    public final float rotA;
    public float yBodyRot;
    public float yBodyRotO;
    public float yHeadRot;
    public float yHeadRotO;
    public float flyingSpeed = 0.02F;
    @Nullable
    protected PlayerEntity lastHurtByPlayer;
    protected int lastHurtByPlayerTime;
    public boolean dead;
    protected int noActionTime;
    protected float oRun;
    protected float run;
    protected float animStep;
    protected float animStepO;
    protected float rotOffs;
    protected int deathScore;
    protected float lastHurt;
    public static final Predicate<LivingEntity> PLAYER_NOT_WEARING_DISGUISE_ITEM = $$0 -> {
        if (!($$0 instanceof PlayerEntity)) {
            return true;
        }
        PlayerEntity $$1 = (PlayerEntity) $$0;
        ItemStack $$3 = $$1.getItemBySlot(EquipmentSlotType.HEAD);
        return $$3.getItem() != Items.CARVED_PUMPKIN && $$3.getItem() != Items.WHITE_CARVED_PUMPKIN;
    };


    public boolean isJumping() {
        return jumping;
    }

    protected boolean jumping;
    public float xxa;
    public float yya;
    public float zza;
    protected CompoundNBT nbtData = new CompoundNBT();
    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;
    protected double lyHeadRot;
    protected int lerpHeadSteps;
    private boolean effectsDirty = true;
    @Nullable
    private LivingEntity lastHurtByMob;
    private int lastHurtByMobTimestamp;
    private LivingEntity lastHurtMob;
    private int lastHurtMobTimestamp;
    private float speed;
    private int noJumpDelay;
    private float absorptionAmount;
    protected ItemStack useItem = ItemStack.EMPTY;
    protected int useItemRemaining;
    protected int fallFlyTicks;
    private BlockPos lastPos;
    private Optional<BlockPos> lastClimbablePos = Optional.empty();
    private DamageSource lastDamageSource;
    private long lastDamageStamp;
    public Radiation.RadiationManager radiationManager;
    protected int autoSpinAttackTicks;
    private float swimAmount;
    private float swimAmountO;
    public int rads;
    public float radResistance;
    protected Brain<?> brain;

    protected LivingEntity(EntityType<? extends LivingEntity> p_i48577_1_, World p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
        this.attributes = new AttributeModifierManager(GlobalEntityTypeAttributes.getSupplier(p_i48577_1_));
        this.setHealth(this.getMaxHealth());
        this.blocksBuilding = true;
        this.rotA = (float) ((Math.random() + 1.0D) * (double) 0.01F);
        this.reapplyPosition();
        this.timeOffs = (float) Math.random() * 12398.0F;
        this.yRot = (float) (Math.random() * (double) ((float) Math.PI * 2F));
        this.yHeadRot = this.yRot;
        this.maxUpStep = 0.6F;
        this.radiationManager = new Radiation.RadiationManager(this);
        NBTDynamicOps nbtdynamicops = NBTDynamicOps.INSTANCE;
        this.brain = this.makeBrain(new Dynamic<>(nbtdynamicops, nbtdynamicops.createMap(ImmutableMap.of(nbtdynamicops.createString("memories"), nbtdynamicops.emptyMap()))));
    }

    public Brain<?> getBrain() {
        return this.brain;
    }

    public String getRegistryName() {
        return Registry.ENTITY_TYPE.getKey(this.getType()).toString();
    }

    protected Brain.BrainCodec<?> brainProvider() {
        return Brain.provider(ImmutableList.of(), ImmutableList.of());
    }

    protected Brain<?> makeBrain(Dynamic<?> p_213364_1_) {
        return this.brainProvider().makeBrain(p_213364_1_);
    }

    public void kill() {
        this.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public boolean canAttackType(EntityType<?> p_213358_1_) {
        return true;
    }

    protected void defineSynchedData() {
        this.entityData.define(DATA_LIVING_ENTITY_FLAGS, (byte) 0);
        this.entityData.define(DATA_EFFECT_COLOR_ID, 0);
        this.entityData.define(DATA_EFFECT_AMBIENCE_ID, false);
        this.entityData.define(DATA_ARROW_COUNT_ID, 0);
        this.entityData.define(DATA_STINGER_COUNT_ID, 0);
        this.entityData.define(DATA_HEALTH_ID, 1.0F);
        this.entityData.define(SLEEPING_POS_ID, Optional.empty());
    }

    public void setRads(int rads) {
        this.radiationManager.setRads(rads);
    }

    public int getRads() {
        return this.radiationManager.getRads();
    }

    public void addRads(int rads) {
        this.setRads(this.radiationManager.getRads() + rads);
    }

    public CompoundNBT getNBT() {
        return this.nbtData;
    }

    public static AttributeModifierMap.MutableAttribute createLivingAttributes() {
        return AttributeModifierMap.builder().add(Attributes.MAX_HEALTH).add(Attributes.KNOCKBACK_RESISTANCE).add(Attributes.MOVEMENT_SPEED).add(Attributes.ARMOR).add(Attributes.ARMOR_TOUGHNESS);
    }

    protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
        if (!this.isInWater()) {
            this.updateInWaterStateAndDoWaterCurrentPushing();
        }

        if (!this.level.isClientSide && p_184231_3_ && this.fallDistance > 0.0F) {
            this.removeSoulSpeed();
            this.tryAddSoulSpeed();
        }

        if (!this.level.isClientSide && this.fallDistance > 3.0F && p_184231_3_) {
            float f = (float) MathHelper.ceil(this.fallDistance - 3.0F);
            if (!p_184231_4_.isAir()) {
                double d0 = Math.min((double) (0.2F + f / 15.0F), 2.5D);
                int i = (int) (150.0D * d0);
                ((ServerWorld) this.level).sendParticles(new BlockParticleData(ParticleTypes.BLOCK, p_184231_4_), this.getX(), this.getY(), this.getZ(), i, 0.0D, 0.0D, 0.0D, (double) 0.15F);
            }
        }

        super.checkFallDamage(p_184231_1_, p_184231_3_, p_184231_4_, p_184231_5_);
    }

    public boolean canBreatheUnderwater() {
        return this.getMobType() == CreatureAttribute.UNDEAD;
    }

    @OnlyIn(Dist.CLIENT)
    public float getSwimAmount(float p_205015_1_) {
        return MathHelper.lerp(p_205015_1_, this.swimAmountO, this.swimAmount);
    }

    public void baseTick() {
        this.oAttackAnim = this.attackAnim;
        if (this.firstTick) {
            this.getSleepingPos().ifPresent(this::setPosToBed);
        }

        if (this.canSpawnSoulSpeedParticle()) {
            this.spawnSoulSpeedParticle();
        }

        super.baseTick();
        this.level.getProfiler().push("livingEntityBaseTick");
        boolean flag = this instanceof PlayerEntity;
        if (this.isAlive()) {
            if (this.isInWall()) {
                this.hurt(DamageSource.IN_WALL, 1.0F);
            } else if (flag && !this.level.getWorldBorder().isWithinBounds(this.getBoundingBox())) {
                double d0 = this.level.getWorldBorder().getDistanceToBorder(this) + this.level.getWorldBorder().getDamageSafeZone();
                if (d0 < 0.0D) {
                    double d1 = this.level.getWorldBorder().getDamagePerBlock();
                    if (d1 > 0.0D) {
                        this.hurt(DamageSource.IN_WALL, (float) Math.max(1, MathHelper.floor(-d0 * d1)));
                    }
                }
            }
        }

        if (this.fireImmune() || this.level.isClientSide) {
            this.clearFire();
        }

        boolean flag1 = flag && ((PlayerEntity) this).abilities.invulnerable;
        if (this.isAlive()) {
            if (this.isEyeInFluid(FluidTags.WATER) && !this.level.getBlockState(new BlockPos(this.getX(), this.getEyeY(), this.getZ())).is(Blocks.BUBBLE_COLUMN)) {
                if (!this.canBreatheUnderwater()
                        && !EffectUtils.hasWaterBreathing(this)
                        && level.getGameRules().getBoolean(GameRules.RULE_DROWNING)
                        && !flag1) {
                    this.setAirSupply(this.decreaseAirSupply(this.getAirSupply()));
                    if (this.getAirSupply() == -20) {
                        this.setAirSupply(0);
                        Vector3d vector3d = this.getDeltaMovement();

                        for (int i = 0; i < 8; ++i) {
                            double d2 = this.random.nextDouble() - this.random.nextDouble();
                            double d3 = this.random.nextDouble() - this.random.nextDouble();
                            double d4 = this.random.nextDouble() - this.random.nextDouble();
                            this.level.addParticle(ParticleTypes.BUBBLE, this.getX() + d2, this.getY() + d3, this.getZ() + d4, vector3d.x, vector3d.y, vector3d.z);
                        }

                        this.hurt(DamageSource.DROWN, 2.0F);
                    }
                }

                if (!this.level.isClientSide && this.isPassenger() && this.getVehicle() != null && !this.getVehicle().rideableUnderWater()) {
                    this.stopRiding();
                }
            } else if (this.getAirSupply() < this.getMaxAirSupply()) {
                this.setAirSupply(this.increaseAirSupply(this.getAirSupply()));
            }

            if (!this.level.isClientSide) {
                BlockPos blockpos = this.blockPosition();
                if (!Objects.equal(this.lastPos, blockpos)) {
                    this.lastPos = blockpos;
                    this.onChangedBlock(blockpos);
                }
            }
        }

        if (this.isAlive() && this.isInWaterRainOrBubble()) {
            this.clearFire();
        }

        if (this.hurtTime > 0) {
            --this.hurtTime;
        }

        if (this.invulnerableTime > 0 && !(this instanceof ServerPlayerEntity)) {
            --this.invulnerableTime;
        }

        if (this.isDeadOrDying()) {
            this.tickDeath();
        }

        if (this.lastHurtByPlayerTime > 0) {
            --this.lastHurtByPlayerTime;
        } else {
            this.lastHurtByPlayer = null;
        }

        if (this.lastHurtMob != null && !this.lastHurtMob.isAlive()) {
            this.lastHurtMob = null;
        }

        if (this.lastHurtByMob != null) {
            if (!this.lastHurtByMob.isAlive()) {
                this.setLastHurtByMob((LivingEntity) null);
            } else if (this.tickCount - this.lastHurtByMobTimestamp > 100) {
                this.setLastHurtByMob((LivingEntity) null);
            }
        }

        this.tickEffects();
        this.animStepO = this.animStep;
        this.yBodyRotO = this.yBodyRot;
        this.yHeadRotO = this.yHeadRot;
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
        this.level.getProfiler().pop();
    }

    public boolean canSpawnSoulSpeedParticle() {
        return this.tickCount % 5 == 0 && this.getDeltaMovement().x != 0.0D && this.getDeltaMovement().z != 0.0D && !this.isSpectator() && EnchantmentHelper.hasSoulSpeed(this) && this.onSoulSpeedBlock();
    }

    protected void spawnSoulSpeedParticle() {
        Vector3d vector3d = this.getDeltaMovement();
        this.level.addParticle(ParticleTypes.SOUL, this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(), this.getY() + 0.1D, this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(), vector3d.x * -0.2D, 0.1D, vector3d.z * -0.2D);
        float f = this.random.nextFloat() * 0.4F + this.random.nextFloat() > 0.9F ? 0.6F : 0.0F;
        this.playSound(SoundEvents.SOUL_ESCAPE, f, 0.6F + this.random.nextFloat() * 0.4F);
    }

    protected boolean onSoulSpeedBlock() {
        return this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).is(BlockTags.SOUL_SPEED_BLOCKS);
    }

    protected float getBlockSpeedFactor() {
        return this.onSoulSpeedBlock() && EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, this) > 0 ? 1.0F : super.getBlockSpeedFactor();
    }

    protected boolean shouldRemoveSoulSpeed(BlockState p_230295_1_) {
        return !p_230295_1_.isAir() || this.isFallFlying();
    }

    protected void removeSoulSpeed() {
        ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (modifiableattributeinstance != null) {
            if (modifiableattributeinstance.getModifier(SPEED_MODIFIER_SOUL_SPEED_UUID) != null) {
                modifiableattributeinstance.removeModifier(SPEED_MODIFIER_SOUL_SPEED_UUID);
            }

        }
    }

    protected void tryAddSoulSpeed() {
        if (!this.getBlockStateOn().isAir()) {
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, this);
            if (i > 0 && this.onSoulSpeedBlock()) {
                ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                if (modifiableattributeinstance == null) {
                    return;
                }

                modifiableattributeinstance.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_SOUL_SPEED_UUID, "Soul speed boost", (double) (0.03F * (1.0F + (float) i * 0.35F)), AttributeModifier.Operation.ADDITION));
                if (this.getRandom().nextFloat() < 0.04F) {
                    ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.FEET);
                    itemstack.hurtAndBreak(1, this, (p_233654_0_) -> {
                        p_233654_0_.broadcastBreakEvent(EquipmentSlotType.FEET);
                    });
                }
            }
        }

    }

    protected void onChangedBlock(BlockPos p_184594_1_) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FROST_WALKER, this);
        if (i > 0) {
            FrostWalkerEnchantment.onEntityMoved(this, this.level, p_184594_1_, i);
        }
        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.LAVA_WALKER, this);
        if (j > 0) {
            LavaWalkerEnchantment.onEntityMoved(this, this.level, p_184594_1_, j);
        }

        if (this.shouldRemoveSoulSpeed(this.getBlockStateOn())) {
            this.removeSoulSpeed();
        }

        this.tryAddSoulSpeed();
    }

    public boolean isBaby() {
        return false;
    }

    public float getScale() {
        return this.isBaby() ? 0.5F : 1.0F;
    }

    protected boolean isAffectedByFluids() {
        return true;
    }

    public boolean rideableUnderWater() {
        return false;
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove();

            for (int i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level.addParticle(ParticleTypes.POOF, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), d0, d1, d2);
            }
        }

    }

    protected boolean shouldDropExperience() {
        return !this.isBaby();
    }

    protected boolean shouldDropLoot() {
        return !this.isBaby();
    }

    protected int decreaseAirSupply(int p_70682_1_) {
        int i = EnchantmentHelper.getRespiration(this);
        return i > 0 && this.random.nextInt(i + 1) > 0 ? p_70682_1_ : p_70682_1_ - 1;
    }

    protected int increaseAirSupply(int p_207300_1_) {
        return Math.min(p_207300_1_ + 4, this.getMaxAirSupply());
    }

    protected int getExperienceReward(PlayerEntity p_70693_1_) {
        return 0;
    }

    public void dropLoot(List<WeightedItemStack> itemsToDrop) {
        dropLoot(itemsToDrop, 0, null);
    }

    public void dropLoot(List<WeightedItemStack> itemsToDrop, float y, @Nullable LootContext context) {
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            // Iterate through each WeightedItemStack in the list
            for (WeightedItemStack selectedItem : itemsToDrop) {
                if (selectedItem != null && this.level instanceof ServerWorld) {
                    // Generate the list of item stacks based on the context
                    LootContext context1 = new LootContext.Builder((ServerWorld) this.level)
                            .withRandom(random) // Assuming `random` is the Random instance
                            .create(LootParameterSets.EMPTY);

                    // Generate random items for the selected item
                    List<ItemStack> generatedItems = selectedItem.generateRandomItems(context == null ? context1 : context);

                    // Drop each generated item stack
                    for (ItemStack stack : generatedItems) {
                        if (!stack.isEmpty()) {
                            ItemEntity itemEntity = this.spawnAtLocation(stack, y);
                        }
                    }
                }
            }
        }
    }

    protected boolean isAlwaysExperienceDropper() {
        return false;
    }

    public Random getRandom() {
        return this.random;
    }

    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    public float nextFloat() {
        return random.nextFloat();
    }

    public int nextInt() {
        return random.nextInt();
    }

    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    @Nullable
    public LivingEntity getLastHurtByMob() {
        return this.lastHurtByMob;
    }

    public int getLastHurtByMobTimestamp() {
        return this.lastHurtByMobTimestamp;
    }

    public void setLastHurtByPlayer(@Nullable PlayerEntity p_230246_1_) {
        this.lastHurtByPlayer = p_230246_1_;
        this.lastHurtByPlayerTime = this.tickCount;
    }

    public void setLastHurtByMob(@Nullable LivingEntity p_70604_1_) {
        this.lastHurtByMob = p_70604_1_;
        this.lastHurtByMobTimestamp = this.tickCount;
    }

    @Nullable
    public LivingEntity getLastHurtMob() {
        return this.lastHurtMob;
    }

    public int getLastHurtMobTimestamp() {
        return this.lastHurtMobTimestamp;
    }

    public void setLastHurtMob(Entity p_130011_1_) {
        if (p_130011_1_ instanceof LivingEntity) {
            this.lastHurtMob = (LivingEntity) p_130011_1_;
        } else {
            this.lastHurtMob = null;
        }

        this.lastHurtMobTimestamp = this.tickCount;
    }

    public int getNoActionTime() {
        return this.noActionTime;
    }

    public void setNoActionTime(int p_213332_1_) {
        this.noActionTime = p_213332_1_;
    }

    protected void playEquipSound(ItemStack p_184606_1_) {
        if (!p_184606_1_.isEmpty()) {
            SoundEvent soundevent = SoundEvents.ARMOR_EQUIP_GENERIC;
            Item item = p_184606_1_.getItem();
            if (item instanceof ArmorItem) {
                soundevent = ((ArmorItem) item).getMaterial().getEquipSound();
            } else if (item == Items.ELYTRA) {
                soundevent = SoundEvents.ARMOR_EQUIP_ELYTRA;
            }

            this.playSound(soundevent, 1.0F, 1.0F);
        }
    }

    public void addAdditionalSaveData(CompoundNBT nbt) {
        this.radiationManager.addAdditionalSaveData(nbt);
        nbt.putFloat("Health", this.getHealth());
        nbt.putShort("HurtTime", (short) this.hurtTime);
        nbt.putInt("HurtByTimestamp", this.lastHurtByMobTimestamp);
        nbt.putShort("DeathTime", (short) this.deathTime);
        nbt.putFloat("AbsorptionAmount", this.getAbsorptionAmount());
        nbt.put("Attributes", this.getAttributes().save());
        if (!this.activeEffects.isEmpty()) {
            ListNBT listnbt = new ListNBT();

            for (EffectInstance effectinstance : this.activeEffects.values()) {
                listnbt.add(effectinstance.save(new CompoundNBT()));
            }

            nbt.put("ActiveEffects", listnbt);
        }

        nbt.putBoolean("FallFlying", this.isFallFlying());
        this.getSleepingPos().ifPresent((p_213338_1_) -> {
            nbt.putInt("SleepingX", p_213338_1_.getX());
            nbt.putInt("SleepingY", p_213338_1_.getY());
            nbt.putInt("SleepingZ", p_213338_1_.getZ());
        });
        DataResult<INBT> dataresult = this.brain.serializeStart(NBTDynamicOps.INSTANCE);
        dataresult.resultOrPartial(LOGGER::error).ifPresent((p_233636_1_) -> {
            nbt.put("Brain", p_233636_1_);
        });
    }

    public void readAdditionalSaveData(CompoundNBT nbt) {
        this.radiationManager.readAdditionalSaveData(nbt);
        this.setAbsorptionAmount(nbt.getFloat("AbsorptionAmount"));
        if (nbt.contains("Attributes", 9) && this.level != null && !this.level.isClientSide) {
            this.getAttributes().load(nbt.getList("Attributes", 10));
        }

        if (nbt.contains("ActiveEffects", 9)) {
            ListNBT listnbt = nbt.getList("ActiveEffects", 10);

            for (int i = 0; i < listnbt.size(); ++i) {
                CompoundNBT compoundnbt = listnbt.getCompound(i);
                EffectInstance effectinstance = EffectInstance.load(compoundnbt);
                if (effectinstance != null) {
                    this.activeEffects.put(effectinstance.getEffect(), effectinstance);
                }
            }
        }

        if (nbt.contains("Health", 99)) {
            this.setHealth(nbt.getFloat("Health"));
        }

        this.hurtTime = nbt.getShort("HurtTime");
        this.deathTime = nbt.getShort("DeathTime");


        this.lastHurtByMobTimestamp = nbt.getInt("HurtByTimestamp");
        if (nbt.contains("Team", 8)) {
            String s = nbt.getString("Team");
            ScorePlayerTeam scoreplayerteam = this.level.getScoreboard().getPlayerTeam(s);
            boolean flag = scoreplayerteam != null && this.level.getScoreboard().addPlayerToTeam(this.getStringUUID(), scoreplayerteam);
            if (!flag) {
                LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", (Object) s);
            }
        }

        if (nbt.getBoolean("FallFlying")) {
            this.setSharedFlag(7, true);
        }

        if (nbt.contains("SleepingX", 99) && nbt.contains("SleepingY", 99) && nbt.contains("SleepingZ", 99)) {
            BlockPos blockpos = new BlockPos(nbt.getInt("SleepingX"), nbt.getInt("SleepingY"), nbt.getInt("SleepingZ"));
            this.setSleepingPos(blockpos);
            this.entityData.set(DATA_POSE, Pose.SLEEPING);
            if (!this.firstTick) {
                this.setPosToBed(blockpos);
            }
        }

        if (nbt.contains("Brain", 10)) {
            this.brain = this.makeBrain(new Dynamic<>(NBTDynamicOps.INSTANCE, nbt.get("Brain")));
        }
        this.nbtData = nbt;
    }

    protected void tickEffects() {
        Iterator<Effect> iterator = this.activeEffects.keySet().iterator();

        try {
            while (iterator.hasNext()) {
                Effect effect = iterator.next();
                EffectInstance effectinstance = this.activeEffects.get(effect);
                if (!effectinstance.tick(this, () -> {
                    this.onEffectUpdated(effectinstance, true);
                })) {
                    if (!this.level.isClientSide) {
                        iterator.remove();
                        this.onEffectRemoved(effectinstance);
                    }
                } else if (effectinstance.getDuration() % 600 == 0) {
                    this.onEffectUpdated(effectinstance, false);
                }
            }
        } catch (ConcurrentModificationException concurrentmodificationexception) {
        }

        if (this.effectsDirty) {
            if (!this.level.isClientSide) {
                this.updateInvisibilityStatus();
            }

            this.effectsDirty = false;
        }

        int i = this.entityData.get(DATA_EFFECT_COLOR_ID);
        boolean flag1 = this.entityData.get(DATA_EFFECT_AMBIENCE_ID);
        if (i > 0) {
            boolean flag;
            if (this.isInvisible()) {
                flag = this.random.nextInt(15) == 0;
            } else {
                flag = this.random.nextBoolean();
            }

            if (flag1) {
                flag &= this.random.nextInt(5) == 0;
            }

            if (flag && i > 0) {
                double d0 = (double) (i >> 16 & 255) / 255.0D;
                double d1 = (double) (i >> 8 & 255) / 255.0D;
                double d2 = (double) (i >> 0 & 255) / 255.0D;
                this.level.addParticle(flag1 ? ParticleTypes.AMBIENT_ENTITY_EFFECT : ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), d0, d1, d2);
            }
        }

    }

    protected void updateInvisibilityStatus() {
        if (this.activeEffects.isEmpty()) {
            this.removeEffectParticles();
            this.setInvisible(false);
        } else {
            Collection<EffectInstance> collection = this.activeEffects.values();
            this.entityData.set(DATA_EFFECT_AMBIENCE_ID, areAllEffectsAmbient(collection));
            this.entityData.set(DATA_EFFECT_COLOR_ID, PotionUtils.getColor(collection));
            this.setInvisible(this.hasEffect(Effects.INVISIBILITY));
        }

    }

    public double getVisibilityPercent(@Nullable Entity attacker) {
        double d0 = 1.0D;
        if (this.isDiscrete()) {
            d0 *= 0.8D;
        }

        if (this.isInvisible()) {
            float f = this.getArmorCoverPercentage();
            if (f < 0.1F) {
                f = 0.1F;
            }

            d0 *= 0.7D * (double) f;
        }

        if (attacker != null) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.HEAD);
            Item item = itemstack.getItem();
            EntityType<?> entitytype = attacker.getType();
            if (attacker instanceof ISkeleton && (item == Items.SKELETON_SKULL || item == Items.WITHER_SKELETON_SKULL) || entitytype == EntityType.ZOMBIE && item == Items.ZOMBIE_HEAD || entitytype == EntityType.CREEPER && item == Items.CREEPER_HEAD) {
                d0 *= 0.1D;
            }
        }

        return d0;
    }

    public boolean canAttack(LivingEntity p_213336_1_) {
        return true;
    }

    public boolean canAttack(LivingEntity p_213344_1_, EntityPredicate p_213344_2_) {
        return p_213344_2_.test(this, p_213344_1_);
    }

    public static boolean areAllEffectsAmbient(Collection<EffectInstance> p_184593_0_) {
        for (EffectInstance effectinstance : p_184593_0_) {
            if (!effectinstance.isAmbient()) {
                return false;
            }
        }

        return true;
    }

    protected void removeEffectParticles() {
        this.entityData.set(DATA_EFFECT_AMBIENCE_ID, false);
        this.entityData.set(DATA_EFFECT_COLOR_ID, 0);
    }

    public boolean removeAllEffects() {
        if (this.level.isClientSide) {
            return false;
        } else {
            Iterator<EffectInstance> iterator = this.activeEffects.values().iterator();

            boolean flag;
            for (flag = false; iterator.hasNext(); flag = true) {
                this.onEffectRemoved(iterator.next());
                iterator.remove();
            }

            return flag;
        }
    }

    public Collection<EffectInstance> getActiveEffects() {
        return this.activeEffects.values();
    }

    public Map<Effect, EffectInstance> getActiveEffectsMap() {
        return this.activeEffects;
    }

    public boolean hasEffect(Effect p_70644_1_) {
        return this.activeEffects.containsKey(p_70644_1_);
    }

    @Nullable
    public EffectInstance getEffect(Effect p_70660_1_) {
        return this.activeEffects.get(p_70660_1_);
    }

    public boolean addEffect(EffectInstance p_195064_1_) {
        if (!this.canBeAffected(p_195064_1_)) {
            return false;
        } else {
            EffectInstance effectinstance = this.activeEffects.get(p_195064_1_.getEffect());
            if (effectinstance == null) {
                this.activeEffects.put(p_195064_1_.getEffect(), p_195064_1_);
                this.onEffectAdded(p_195064_1_);
                return true;
            } else if (effectinstance.update(p_195064_1_)) {
                this.onEffectUpdated(effectinstance, true);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean canBeAffected(EffectInstance p_70687_1_) {
        if (this instanceof IWitherMob && p_70687_1_.getEffect() == Effects.WITHER) {
            return false;
        }
        if (p_70687_1_.getEffect() == Effects.CONFUSED) {
            if (this instanceof IWitherMob || this instanceof EnderDragonEntity ||
                    this instanceof EndermanEntity ||
                    this instanceof ShulkerEntity ||
                    this instanceof MagmaCubeEntity ||
                    this instanceof WitchEntity ||
                    this instanceof CaveSpiderEntity ||

            this instanceof SpiderEntity || this instanceof HuskEntity || this instanceof RavagerEntity || this instanceof HoglinEntity || this instanceof IronGolemEntity) {

                if ((this instanceof SpiderEntity && !(this instanceof CaveSpiderEntity)) || this instanceof HuskEntity || this instanceof RavagerEntity || this instanceof IronGolemEntity) {
                    return this.nextFloat() < 0.45F;
                }

                return false;
            }
        }
        if (this.getMobType() == CreatureAttribute.UNDEAD) {
            Effect effect = p_70687_1_.getEffect();
            if (effect == Effects.REGENERATION || effect == Effects.POISON) {
                return false;
            }
        }

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void forceAddEffect(EffectInstance p_233646_1_) {
        if (this.canBeAffected(p_233646_1_)) {
            EffectInstance effectinstance = this.activeEffects.put(p_233646_1_.getEffect(), p_233646_1_);
            if (effectinstance == null) {
                this.onEffectAdded(p_233646_1_);
            } else {
                this.onEffectUpdated(p_233646_1_, true);
            }

        }
    }

    public boolean isInvertedHealAndHarm() {
        return this.getMobType() == CreatureAttribute.UNDEAD;
    }

    @Nullable
    public EffectInstance removeEffectNoUpdate(@Nullable Effect p_184596_1_) {
        return this.activeEffects.remove(p_184596_1_);
    }

    public boolean removeEffect(Effect p_195063_1_) {
        EffectInstance effectinstance = this.removeEffectNoUpdate(p_195063_1_);
        if (effectinstance != null) {
            this.onEffectRemoved(effectinstance);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeEffects(Effect... effects) {
        for (Effect effect : effects) {
            EffectInstance instance = this.removeEffectNoUpdate(effect);
            if (instance != null) {
                this.onEffectRemoved(instance);
                return true;
            } else {
                continue;
            }
        }
        return false;
    }

    protected void onEffectAdded(EffectInstance p_70670_1_) {
        this.effectsDirty = true;
        if (!this.level.isClientSide) {
            p_70670_1_.getEffect().addAttributeModifiers(this, this.getAttributes(), p_70670_1_.getAmplifier());
        }

    }

    protected void onEffectUpdated(EffectInstance p_70695_1_, boolean p_70695_2_) {
        this.effectsDirty = true;
        if (p_70695_2_ && !this.level.isClientSide) {
            Effect effect = p_70695_1_.getEffect();
            effect.removeAttributeModifiers(this, this.getAttributes(), p_70695_1_.getAmplifier());
            effect.addAttributeModifiers(this, this.getAttributes(), p_70695_1_.getAmplifier());
        }

    }

    protected void onEffectRemoved(EffectInstance p_70688_1_) {
        this.effectsDirty = true;
        if (!this.level.isClientSide) {
            p_70688_1_.getEffect().removeAttributeModifiers(this, this.getAttributes(), p_70688_1_.getAmplifier());
        }

    }

    public void heal(float p_70691_1_) {
        float f = this.getHealth();
        if (f > 0.0F) {
            this.setHealth(f + p_70691_1_);
        }

    }

    public float getHealth() {
        return this.entityData.get(DATA_HEALTH_ID);
    }

    public void setHealth(float p_70606_1_) {
        this.entityData.set(DATA_HEALTH_ID, MathHelper.clamp(p_70606_1_, 0.0F, this.getMaxHealth()));
    }

    public boolean isDeadOrDying() {
        return this.getHealth() <= 0.0F;
    }

    public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
        if (this.isInvulnerableTo(p_70097_1_)) {
            return false;
        } else if (this.level.isClientSide) {
            return false;
        } else if (this.isDeadOrDying()) {
            return false;
        } else if (p_70097_1_.isFire() && this.hasEffect(Effects.FIRE_RESISTANCE)) {
            return false;
        } else {
            if (this.isSleeping() && !this.level.isClientSide) {
                this.stopSleeping();
            }

            this.noActionTime = 0;
            float f = p_70097_2_;
            if ((p_70097_1_ == DamageSource.ANVIL || p_70097_1_ == DamageSource.FALLING_BLOCK) && !this.getItemBySlot(EquipmentSlotType.HEAD).isEmpty()) {
                this.getItemBySlot(EquipmentSlotType.HEAD).hurtAndBreak((int) (p_70097_2_ * 4.0F + this.random.nextFloat() * p_70097_2_ * 2.0F), this, (p_233653_0_) -> {
                    p_233653_0_.broadcastBreakEvent(EquipmentSlotType.HEAD);
                });
                p_70097_2_ *= 0.75F;
            }

            boolean flag = false;
            float f1 = 0.0F;
            if (p_70097_2_ > 0.0F && this.isDamageSourceBlocked(p_70097_1_)) {
                this.hurtCurrentlyUsedShield(p_70097_2_);
                f1 = p_70097_2_;
                p_70097_2_ = 0.0F;
                if (!p_70097_1_.isProjectile()) {
                    Entity entity = p_70097_1_.getDirectEntity();
                    if (entity instanceof LivingEntity) {
                        this.blockUsingShield((LivingEntity) entity);
                    }
                }

                flag = true;
            }

            this.animationSpeed = 1.5F;
            boolean flag1 = true;
            if ((float) this.invulnerableTime > 10.0F) {
                if (p_70097_2_ <= this.lastHurt) {
                    return false;
                }

                this.actuallyHurt(p_70097_1_, p_70097_2_ - this.lastHurt);
                this.lastHurt = p_70097_2_;
                flag1 = false;
            } else {
                this.lastHurt = p_70097_2_;
                this.invulnerableTime = p_70097_1_.isExplosion() && (p_70097_1_.getEntity() instanceof FireworkRocketEntity || p_70097_1_.getDirectEntity() instanceof FireworkRocketEntity) ? 5 : invulnerableDuration;
                if (this instanceof PlayerEntity) {
                    PlayerEntity player = this.asPlayer();
                    if (player.getAccessoryHolder() != null && player.hasItemInHolder(player.getAccessoryHolder(), Items.CROSS_NECKLACE)) {
                        AccessoryHolderItem.getContents(player.getAccessoryHolder()).filter(item -> item.get() == Items.CROSS_NECKLACE).forEach(item -> {
                            item.hurt(1, player);
                        });
                        this.invulnerableTime = 40;
                    }
                }
                this.actuallyHurt(p_70097_1_, p_70097_2_);
                this.hurtDuration = 10;
                this.hurtTime = this.hurtDuration;
            }

            this.hurtDir = 0.0F;
            Entity entity1 = p_70097_1_.getEntity();
            if (entity1 != null) {
                if (entity1 instanceof LivingEntity) {
                    this.setLastHurtByMob((LivingEntity) entity1);
                }

                if (entity1 instanceof PlayerEntity) {
                    this.lastHurtByPlayerTime = 100;
                    this.lastHurtByPlayer = (PlayerEntity) entity1;
                } else if (entity1 instanceof WolfEntity) {
                    WolfEntity wolfentity = (WolfEntity) entity1;
                    if (wolfentity.isTame()) {
                        this.lastHurtByPlayerTime = 100;
                        LivingEntity livingentity = wolfentity.getOwner();
                        if (livingentity != null && livingentity.getType() == EntityType.PLAYER) {
                            this.lastHurtByPlayer = (PlayerEntity) livingentity;
                        } else {
                            this.lastHurtByPlayer = null;
                        }
                    }
                }
            }

            if (flag1) {
                if (flag) {
                    this.level.broadcastEntityEvent(this, (byte) 29);
                } else if (p_70097_1_ instanceof EntityDamageSource && ((EntityDamageSource) p_70097_1_).isThorns()) {
                    this.level.broadcastEntityEvent(this, (byte) 33);
                } else {
                    byte b0;
                    if (p_70097_1_ == DamageSource.DROWN) {
                        b0 = 36;
                    } else if (p_70097_1_.isFire()) {
                        b0 = 37;
                    } else if (p_70097_1_ == DamageSource.SWEET_BERRY_BUSH) {
                        b0 = 44;
                    } else {
                        b0 = 2;
                    }

                    this.level.broadcastEntityEvent(this, b0);
                }

                if (p_70097_1_ != DamageSource.DROWN && (!flag || p_70097_2_ > 0.0F)) {
                    this.markHurt();
                }

                if (entity1 != null) {
                    double d1 = entity1.getX() - this.getX();

                    double d0;
                    for (d0 = entity1.getZ() - this.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
                        d1 = (Math.random() - Math.random()) * 0.01D;
                    }

                    this.hurtDir = (float) (MathHelper.atan2(d0, d1) * (double) (180F / (float) Math.PI) - (double) this.yRot);
                    this.knockback(0.4F, d1, d0);
                } else {
                    this.hurtDir = (float) ((int) (Math.random() * 2.0D) * 180);
                }
            }

            if (this.isDeadOrDying()) {
                if (!this.checkTotemDeathProtection(p_70097_1_)) {
                    SoundEvent soundevent = this.getDeathSound();
                    if (this instanceof CreakingEntity) {
                        soundevent = null;
                    }
                    if (flag1 && soundevent != null) {
                        this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
                    }

                    this.die(p_70097_1_);
                }
            } else if (flag1) {
                this.playHurtSound(p_70097_1_);
            }

            boolean flag2 = !flag || p_70097_2_ > 0.0F;
            if (flag2) {
                this.lastDamageSource = p_70097_1_;
                this.lastDamageStamp = this.level.getGameTime();
            }

            if (this instanceof ServerPlayerEntity) {
                CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayerEntity) this, p_70097_1_, f, p_70097_2_, flag);
                if (f1 > 0.0F && f1 < 3.4028235E37F) {
                    ((ServerPlayerEntity) this).awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(f1 * 10.0F));
                }
            }

            if (entity1 instanceof ServerPlayerEntity) {
                CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity) entity1, this, p_70097_1_, f, p_70097_2_, flag);
            }

            return flag2;
        }
    }

    protected void blockUsingShield(LivingEntity p_190629_1_) {
        p_190629_1_.blockedByShield(this);
    }

    protected void blockedByShield(LivingEntity p_213371_1_) {
        p_213371_1_.knockback(0.5F, p_213371_1_.getX() - this.getX(), p_213371_1_.getZ() - this.getZ());
    }

    private boolean checkTotemDeathProtection(DamageSource p_190628_1_) {
        if (p_190628_1_.isBypassInvul()) {
            return false;
        } else {
            ItemStack itemstack = null;

            for (Hand hand : Hand.values()) {
                ItemStack itemstack1 = this.getItemInHand(hand);
                if (itemstack1.getItem() == Items.TOTEM_OF_UNDYING) {
                    itemstack = itemstack1.copy();
                    itemstack1.shrink(1);
                    break;
                }
            }

            if (itemstack != null) {
                if (this instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) this;
                    serverplayerentity.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                    CriteriaTriggers.USED_TOTEM.trigger(serverplayerentity, itemstack);
                }

                this.setHealth(1.0F);
                this.removeAllEffects();
                this.addEffect(new EffectInstance(Effects.REGENERATION, 900, 1));
                this.addEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
                this.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 800, 0));
                this.level.broadcastEntityEvent(this, (byte) 35);
            }

            return itemstack != null;
        }
    }

    @Nullable
    public DamageSource getLastDamageSource() {
        if (this.level.getGameTime() - this.lastDamageStamp > 40L) {
            this.lastDamageSource = null;
        }

        return this.lastDamageSource;
    }

    protected void playHurtSound(DamageSource p_184581_1_) {
        SoundEvent soundevent = this.getHurtSound(p_184581_1_);
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
        }

    }

    private boolean isDamageSourceBlocked(DamageSource p_184583_1_) {
        Entity entity = p_184583_1_.getDirectEntity();
        boolean flag = false;
        if (entity instanceof AbstractArrowEntity) {
            AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity) entity;
            if (abstractarrowentity.getPierceLevel() > 0) {
                flag = true;
            }
        }

        if (!p_184583_1_.isBypassArmor() && this.isBlocking() && !flag) {
            Vector3d vector3d2 = p_184583_1_.getSourcePosition();
            if (vector3d2 != null) {
                Vector3d vector3d = this.getViewVector(1.0F);
                Vector3d vector3d1 = vector3d2.vectorTo(this.position()).normalize();
                vector3d1 = new Vector3d(vector3d1.x, 0.0D, vector3d1.z);
                if (vector3d1.dot(vector3d) < 0.0D) {
                    return true;
                }
            }
        }

        return false;
    }

    @OnlyIn(Dist.CLIENT)
    private void breakItem(ItemStack p_70669_1_) {
        if (!p_70669_1_.isEmpty()) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_BREAK, this.getSoundSource(), 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F, false);
            }

            this.spawnItemParticles(p_70669_1_, 5);
        }

    }

    public void die(DamageSource p_70645_1_) {
        if (!this.removed && !this.dead) {
            Entity entity = p_70645_1_.getEntity();
            LivingEntity livingentity = this.getKillCredit();
            if (this.deathScore >= 0 && livingentity != null) {
                livingentity.awardKillScore(this, this.deathScore, p_70645_1_);
            }

            if (this.isSleeping()) {
                this.stopSleeping();
            }

            this.dead = true;
            this.getCombatTracker().recheckStatus();
            if (this.level instanceof ServerWorld) {
                if (entity != null) {
                    entity.killed((ServerWorld) this.level, this);
                }

                this.dropAllDeathLoot(p_70645_1_);
                this.createWitherRose(livingentity);
            }

            this.level.broadcastEntityEvent(this, (byte) 3);
            this.setPose(Pose.DYING);
        }
    }

    protected void createWitherRose(@Nullable LivingEntity p_226298_1_) {
        if (!this.level.isClientSide) {
            boolean flag = false;
            if (p_226298_1_ instanceof WitherEntity) {
                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    BlockPos blockpos = this.blockPosition();
                    BlockState blockstate = Blocks.WITHER_ROSE.defaultBlockState();
                    if (this.level.getBlockState(blockpos).isAir() && blockstate.canSurvive(this.level, blockpos)) {
                        this.level.setBlock(blockpos, blockstate, 3);
                        flag = true;
                    }
                }

                if (!flag) {
                    ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), new ItemStack(Items.WITHER_ROSE));
                    this.level.addFreshEntity(itementity);
                }
            }

        }
    }

    protected void dropAllDeathLoot(DamageSource p_213345_1_) {
        Entity entity = p_213345_1_.getEntity();
        int i;
        if (entity instanceof PlayerEntity) {
            i = EnchantmentHelper.getMobLooting((LivingEntity) entity);
        } else {
            i = 0;
        }

        boolean flag = this.lastHurtByPlayerTime > 0;
        if (this.shouldDropLoot() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.dropFromLootTable(p_213345_1_, flag);
            this.dropCustomDeathLoot(p_213345_1_, i, flag);
        }

        this.dropEquipment();
        this.dropExperience();
    }

    protected void dropEquipment() {
    }

    protected void dropExperience() {
        if (!this.level.isClientSide && (this.isAlwaysExperienceDropper() || this.lastHurtByPlayerTime > 0 && this.shouldDropExperience() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
            int i = this.getExperienceReward(this.lastHurtByPlayer);

            while (i > 0) {
                int j = ExperienceOrbEntity.getExperienceValue(i);
                i -= j;
                this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.getX(), this.getY(), this.getZ(), j));
            }
        }

    }

    protected void dropCustomDeathLoot(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {

    }

    public ResourceLocation getLootTable() {
        return this.getType().getDefaultLootTable();
    }

    protected void dropFromLootTable(DamageSource p_213354_1_, boolean p_213354_2_) {
        ResourceLocation resourcelocation = this.getLootTable();
        LootTable loottable = this.level.getServer().getLootTables().get(resourcelocation);
        LootContext.Builder lootcontext$builder = this.createLootContext(p_213354_2_, p_213354_1_);
        if (this instanceof IDropsCustomLoot) {
            this.dropLoot(((IDropsCustomLoot) this).getDropItems(), 0, lootcontext$builder.create(LootParameterSets.ENTITY));
        }
        loottable.getRandomItems(lootcontext$builder.create(LootParameterSets.ENTITY), this::spawnAtLocation);
    }

    protected LootContext.Builder createLootContext(boolean p_213363_1_, DamageSource p_213363_2_) {
        LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld) this.level)).withRandom(this.random).withParameter(LootParameters.THIS_ENTITY, this).withParameter(LootParameters.ORIGIN, this.position()).withParameter(LootParameters.DAMAGE_SOURCE, p_213363_2_).withOptionalParameter(LootParameters.KILLER_ENTITY, p_213363_2_.getEntity()).withOptionalParameter(LootParameters.DIRECT_KILLER_ENTITY, p_213363_2_.getDirectEntity());
        if (p_213363_1_ && this.lastHurtByPlayer != null) {
            lootcontext$builder = lootcontext$builder.withParameter(LootParameters.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
        }

        return lootcontext$builder;
    }


    public boolean hasLineOfSight(Entity $$0, RayTraceContext.BlockMode $$1, RayTraceContext.FluidMode $$2, DoubleSupplier $$3) {
        if ($$0.level() != this.level()) {
            return false;
        }
        Vector3d $$4 = new Vector3d(this.getX(), this.getEyeY(), this.getZ());
        Vector3d $$5 = new Vector3d($$0.getX(), $$3.getAsDouble(), $$0.getZ());
        if ($$5.distanceTo($$4) > 128.0) {
            return false;
        }
        return this.level.clip(new RayTraceContext($$4, $$5, $$1, $$2, this)).getType() == RayTraceResult.Type.MISS;
    }


    public void knockback(float strength, double xRatio, double zRatio) {
        // Adjust the knockback strength based on the entity's knockback resistance attribute
        strength = (float) ((double) strength * (1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));

        // If the adjusted knockback strength is greater than 0, apply the knockback
        if (strength > 0.0F) {
            this.hasImpulse = true;

            // Get the current motion vector of the entity
            Vector3d currentMotion = this.getDeltaMovement();

            // Create a normalized vector from the x and z ratios, scaled by the knockback strength
            Vector3d knockbackVector = (new Vector3d(xRatio, 0.0D, zRatio)).normalize().scale((double) strength);

            // Set the new motion vector, adjusting for the knockback
            this.setDeltaMovement(
                    currentMotion.x / 2.0D - knockbackVector.x,
                    this.onGround ? Math.min(0.4D, currentMotion.y / 2.0D + (double) strength) : currentMotion.y,
                    currentMotion.z / 2.0D - knockbackVector.z
            );
        }
    }

    // Custom knockback method to include vertical knockback
    public void knockbackEntity(LivingEntity entity, float strength, double xRatio, double zRatio) {
        strength = (float) ((double) strength * (1.0D - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));

        if (strength > 0.0F) {
            entity.hasImpulse = true;
            Vector3d currentMotion = entity.getDeltaMovement();
            Vector3d knockbackVector = (new Vector3d(xRatio, 0.0D, zRatio)).normalize().scale((double) strength);

            entity.setDeltaMovement(
                    currentMotion.x / 2.0D - knockbackVector.x,
                    Math.min(0.9D, currentMotion.y / 2.0D + (double) strength), // Adjusted to ensure upward knockback
                    currentMotion.z / 2.0D - knockbackVector.z
            );

            System.out.println("Knockback vector: " + new Vector3d(xRatio, 0.0D, zRatio).normalize().scale((double) strength));
        }
    }


    @Nullable
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.GENERIC_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    protected SoundEvent getFallDamageSound(int p_184588_1_) {
        return p_184588_1_ > 4 ? SoundEvents.GENERIC_BIG_FALL : SoundEvents.GENERIC_SMALL_FALL;
    }

    protected SoundEvent getDrinkingSound(ItemStack p_213351_1_) {
        return p_213351_1_.getDrinkingSound();
    }

    public SoundEvent getEatingSound(ItemStack p_213353_1_) {
        return p_213353_1_.getEatingSound();
    }

    public void setOnGround(boolean p_230245_1_) {
        super.setOnGround(p_230245_1_);
        if (p_230245_1_) {
            this.lastClimbablePos = Optional.empty();
        }

    }

    public Optional<BlockPos> getLastClimbablePos() {
        return this.lastClimbablePos;
    }

    public boolean onClimbable() {
        if (this.isSpectator()) {
            return false;
        } else {
            BlockPos blockpos = this.blockPosition();
            BlockState blockstate = this.getFeetBlockState();
            Block block = blockstate.getBlock();
            if (block.is(BlockTags.CLIMBABLE)) {
                this.lastClimbablePos = Optional.of(blockpos);
                return true;
            } else if (block instanceof TrapDoorBlock && this.trapdoorUsableAsLadder(blockpos, blockstate)) {
                this.lastClimbablePos = Optional.of(blockpos);
                return true;
            } else {
                return false;
            }
        }
    }

    public BlockState getFeetBlockState() {
        return this.level.getBlockState(this.blockPosition());
    }

    private boolean trapdoorUsableAsLadder(BlockPos p_184604_1_, BlockState p_184604_2_) {
        if (p_184604_2_.getValue(TrapDoorBlock.OPEN)) {
            BlockState blockstate = this.level.getBlockState(p_184604_1_.below());
            if (blockstate.is(Blocks.LADDER) && blockstate.getValue(LadderBlock.FACING) == p_184604_2_.getValue(TrapDoorBlock.FACING)) {
                return true;
            }
        }

        return false;
    }

    public boolean isAlive() {
        return !this.removed && this.getHealth() > 0.0F;
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
        boolean flag = super.causeFallDamage(p_225503_1_, p_225503_2_);
        int i = this.calculateFallDamage(p_225503_1_, p_225503_2_);
        if (i > 0) {
            this.playSound(this.getFallDamageSound(i), 1.0F, 1.0F);
            this.playBlockFallSound();
            this.hurt(DamageSource.FALL, (float) i);
            return true;
        } else {
            return flag;
        }
    }

    protected int calculateFallDamage(float p_225508_1_, float p_225508_2_) {
        EffectInstance effectinstance = this.getEffect(Effects.JUMP);
        float f = effectinstance == null ? 0.0F : (float) (effectinstance.getAmplifier() + 1);
        return MathHelper.ceil((p_225508_1_ - 3.0F - f) * p_225508_2_);
    }

    protected void playBlockFallSound() {
        if (!this.isSilent()) {
            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY() - (double) 0.2F);
            int k = MathHelper.floor(this.getZ());
            BlockState blockstate = this.level.getBlockState(new BlockPos(i, j, k));
            if (!blockstate.isAir()) {
                SoundType soundtype = blockstate.getSoundType();
                this.playSound(soundtype.getFallSound(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public void animateHurt() {
        this.hurtDuration = 10;
        this.hurtTime = this.hurtDuration;
        this.hurtDir = 0.0F;
    }

    public int getArmorValue() {
        return MathHelper.floor(this.getAttributeValue(Attributes.ARMOR));
    }

    protected void hurtArmor(DamageSource p_230294_1_, float p_230294_2_) {
    }

    protected void hurtCurrentlyUsedShield(float p_184590_1_) {
    }

    protected float getDamageAfterArmorAbsorb(DamageSource p_70655_1_, float p_70655_2_) {
        if (!p_70655_1_.isBypassArmor()) {
            this.hurtArmor(p_70655_1_, p_70655_2_);
            p_70655_2_ = CombatRules.getDamageAfterAbsorb(p_70655_2_, (float) this.getArmorValue(), (float) this.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        }

        return p_70655_2_;
    }

    protected float getDamageAfterMagicAbsorb(DamageSource p_70672_1_, float p_70672_2_) {
        if (p_70672_1_.isBypassMagic()) {
            return p_70672_2_;
        } else {
            if (this.hasEffect(Effects.DAMAGE_RESISTANCE) && p_70672_1_ != DamageSource.OUT_OF_WORLD) {
                int i = (this.getEffect(Effects.DAMAGE_RESISTANCE).getAmplifier() + 1) * 5;
                int j = 25 - i;
                float f = p_70672_2_ * (float) j;
                float f1 = p_70672_2_;
                p_70672_2_ = Math.max(f / 25.0F, 0.0F);
                float f2 = f1 - p_70672_2_;
                if (f2 > 0.0F && f2 < 3.4028235E37F) {
                    if (this instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) this).awardStat(Stats.DAMAGE_RESISTED, Math.round(f2 * 10.0F));
                    } else if (p_70672_1_.getEntity() instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) p_70672_1_.getEntity()).awardStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(f2 * 10.0F));
                    }
                }
            }

            if (p_70672_2_ <= 0.0F) {
                return 0.0F;
            } else {
                int k = EnchantmentHelper.getDamageProtection(this.getArmorSlots(), p_70672_1_);
                if (k > 0) {
                    p_70672_2_ = CombatRules.getDamageAfterMagicAbsorb(p_70672_2_, (float) k);
                }

                return p_70672_2_;
            }
        }
    }

    protected void actuallyHurt(DamageSource p_70665_1_, float p_70665_2_) {
        if (!this.isInvulnerableTo(p_70665_1_)) {
            p_70665_2_ = this.getDamageAfterArmorAbsorb(p_70665_1_, p_70665_2_);
            p_70665_2_ = this.getDamageAfterMagicAbsorb(p_70665_1_, p_70665_2_);
            float f2 = Math.max(p_70665_2_ - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (p_70665_2_ - f2));
            float f = p_70665_2_ - f2;
            if (f > 0.0F && f < 3.4028235E37F && p_70665_1_.getEntity() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) p_70665_1_.getEntity()).awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(f * 10.0F));
            }

            if (f2 != 0.0F) {
                float f1 = this.getHealth();
                this.setHealth(f1 - f2);
                this.getCombatTracker().recordDamage(p_70665_1_, f1, f2);
                this.setAbsorptionAmount(this.getAbsorptionAmount() - f2);
            }
        }
    }

    public CombatTracker getCombatTracker() {
        return this.combatTracker;
    }

    @Nullable
    public LivingEntity getKillCredit() {
        if (this.combatTracker.getKiller() != null) {
            return this.combatTracker.getKiller();
        } else if (this.lastHurtByPlayer != null) {
            return this.lastHurtByPlayer;
        } else {
            return this.lastHurtByMob != null ? this.lastHurtByMob : null;
        }
    }

    public final float getMaxHealth() {
        return (float) this.getAttributeValue(Attributes.MAX_HEALTH);
    }

    public final int getArrowCount() {
        return this.entityData.get(DATA_ARROW_COUNT_ID);
    }

    public final void setArrowCount(int p_85034_1_) {
        this.entityData.set(DATA_ARROW_COUNT_ID, p_85034_1_);
    }

    public final int getStingerCount() {
        return this.entityData.get(DATA_STINGER_COUNT_ID);
    }

    public final void setStingerCount(int p_226300_1_) {
        this.entityData.set(DATA_STINGER_COUNT_ID, p_226300_1_);
    }

    private int getCurrentSwingDuration() {
        if (EffectUtils.hasDigSpeed(this)) {
            return 6 - (1 + EffectUtils.getDigSpeedAmplification(this));
        } else {
            return this.hasEffect(Effects.DIG_SLOWDOWN) ? 6 + (1 + this.getEffect(Effects.DIG_SLOWDOWN).getAmplifier()) * 2 : 6;
        }
    }

    public void swing(Hand p_184609_1_) {
        this.swing(p_184609_1_, false);
    }

    public void swing(Hand p_226292_1_, boolean p_226292_2_) {
        if (!this.swinging || this.swingTime >= this.getCurrentSwingDuration() / 2 || this.swingTime < 0) {
            this.swingTime = -1;
            this.swinging = true;
            this.swingingArm = p_226292_1_;
            if (this.level instanceof ServerWorld) {
                SAnimateHandPacket sanimatehandpacket = new SAnimateHandPacket(this, p_226292_1_ == Hand.MAIN_HAND ? 0 : 3);
                ServerChunkProvider serverchunkprovider = ((ServerWorld) this.level).getChunkSource();
                if (p_226292_2_) {
                    serverchunkprovider.broadcastAndSend(this, sanimatehandpacket);
                } else {
                    serverchunkprovider.broadcast(this, sanimatehandpacket);
                }
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte p_70103_1_) {
        switch (p_70103_1_) {
            case 2:
            case 33:
            case 36:
            case 37:
            case 44:
                boolean flag1 = p_70103_1_ == 33;
                boolean flag2 = p_70103_1_ == 36;
                boolean flag3 = p_70103_1_ == 37;
                boolean flag = p_70103_1_ == 44;
                this.animationSpeed = 1.5F;
                this.invulnerableTime = invulnerableDuration;
                this.hurtDuration = 10;
                this.hurtTime = this.hurtDuration;
                this.hurtDir = 0.0F;
                if (flag1) {
                    this.playSound(SoundEvents.THORNS_HIT, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                }

                DamageSource damagesource;
                if (flag3) {
                    damagesource = DamageSource.ON_FIRE;
                } else if (flag2) {
                    damagesource = DamageSource.DROWN;
                } else if (flag) {
                    damagesource = DamageSource.SWEET_BERRY_BUSH;
                } else {
                    damagesource = DamageSource.GENERIC;
                }

                SoundEvent soundevent1 = this.getHurtSound(damagesource);
                if (soundevent1 != null) {
                    this.playSound(soundevent1, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                }

                this.hurt(DamageSource.GENERIC, 0.0F);
                break;
            case 3:
                SoundEvent soundevent = this.getDeathSound();
                if (soundevent != null) {
                    this.playSound(soundevent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                }

                if (!(this instanceof PlayerEntity)) {
                    this.setHealth(0.0F);
                    this.die(DamageSource.GENERIC);
                }
                break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 31:
            case 32:
            case 34:
            case 35:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 45:
            case 53:
            default:
                super.handleEntityEvent(p_70103_1_);
                break;
            case 29:
                this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.level.random.nextFloat() * 0.4F);
                break;
            case 30:
                this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
                break;
            case 46:
                int i = 128;

                for (int j = 0; j < 128; ++j) {
                    double d0 = (double) j / 127.0D;
                    float f = (this.random.nextFloat() - 0.5F) * 0.2F;
                    float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
                    float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
                    double d1 = MathHelper.lerp(d0, this.xo, this.getX()) + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                    double d2 = MathHelper.lerp(d0, this.yo, this.getY()) + this.random.nextDouble() * (double) this.getBbHeight();
                    double d3 = MathHelper.lerp(d0, this.zo, this.getZ()) + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                    this.level.addParticle(ParticleTypes.PORTAL, d1, d2, d3, (double) f, (double) f1, (double) f2);
                }
                break;
            case 47:
                this.breakItem(this.getItemBySlot(EquipmentSlotType.MAINHAND));
                break;
            case 48:
                this.breakItem(this.getItemBySlot(EquipmentSlotType.OFFHAND));
                break;
            case 49:
                this.breakItem(this.getItemBySlot(EquipmentSlotType.HEAD));
                break;
            case 50:
                this.breakItem(this.getItemBySlot(EquipmentSlotType.CHEST));
                break;
            case 51:
                this.breakItem(this.getItemBySlot(EquipmentSlotType.LEGS));
                break;
            case 52:
                this.breakItem(this.getItemBySlot(EquipmentSlotType.FEET));
                break;
            case 54:
                HoneyBlock.showJumpParticles(this);
                break;
            case 55:
                this.swapHandItems();
        }

    }

    @OnlyIn(Dist.CLIENT)
    private void swapHandItems() {
        ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.OFFHAND);
        this.setItemSlot(EquipmentSlotType.OFFHAND, this.getItemBySlot(EquipmentSlotType.MAINHAND));
        this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack);
    }

    protected void outOfWorld() {
        this.hurt(DamageSource.OUT_OF_WORLD, 4.0F);
    }

    protected void updateSwingTime() {
        int i = this.getCurrentSwingDuration();
        if (this.swinging) {
            ++this.swingTime;
            if (this.swingTime >= i) {
                this.swingTime = 0;
                this.swinging = false;
            }
        } else {
            this.swingTime = 0;
        }

        this.attackAnim = (float) this.swingTime / (float) i;
    }

    @Nullable
    public ModifiableAttributeInstance getAttribute(Attribute p_110148_1_) {
        return this.getAttributes().getInstance(p_110148_1_);
    }

    public double getAttributeValue(Attribute p_233637_1_) {
        return this.getAttributes().getValue(p_233637_1_);
    }

    public double getAttributeBaseValue(Attribute p_233638_1_) {
        return this.getAttributes().getBaseValue(p_233638_1_);
    }

    public AttributeModifierManager getAttributes() {
        return this.attributes;
    }

    public CreatureAttribute getMobType() {
        return CreatureAttribute.UNDEFINED;
    }

    public ItemStack getMainHandItem() {
        return this.getItemBySlot(EquipmentSlotType.MAINHAND);
    }

    public ItemStack getOffhandItem() {
        return this.getItemBySlot(EquipmentSlotType.OFFHAND);
    }

    public boolean isHolding(Item p_233631_1_) {
        return this.isHolding((p_233632_1_) -> {
            return p_233632_1_ == p_233631_1_;
        });
    }

    public boolean isHoldingAbstractCrossbow(Item item) {
        return this.isHolding((item1) -> {
            return item1 instanceof AbstractCrossbowItem;
        });
    }

    public boolean isHolding(Predicate<Item> p_233634_1_) {
        return p_233634_1_.test(this.getMainHandItem().getItem()) || p_233634_1_.test(this.getOffhandItem().getItem());
    }

    public ItemStack getItemInHand(Hand p_184586_1_) {
        if (p_184586_1_ == Hand.MAIN_HAND) {
            return this.getItemBySlot(EquipmentSlotType.MAINHAND);
        } else if (p_184586_1_ == Hand.OFF_HAND) {
            return this.getItemBySlot(EquipmentSlotType.OFFHAND);
        } else {
            throw new IllegalArgumentException("Invalid hand " + p_184586_1_);
        }
    }

    public void setItemInHand(Hand p_184611_1_, ItemStack p_184611_2_) {
        if (p_184611_1_ == Hand.MAIN_HAND) {
            this.setItemSlot(EquipmentSlotType.MAINHAND, p_184611_2_);
        } else {
            if (p_184611_1_ != Hand.OFF_HAND) {
                throw new IllegalArgumentException("Invalid hand " + p_184611_1_);
            }

            this.setItemSlot(EquipmentSlotType.OFFHAND, p_184611_2_);
        }

    }

    public boolean hasItemInSlot(EquipmentSlotType p_190630_1_) {
        return !this.getItemBySlot(p_190630_1_).isEmpty();
    }

    public abstract Iterable<ItemStack> getArmorSlots();

    public abstract ItemStack getItemBySlot(EquipmentSlotType p_184582_1_);

    public abstract void setItemSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_);

    public float getArmorCoverPercentage() {
        Iterable<ItemStack> iterable = this.getArmorSlots();
        int i = 0;
        int j = 0;

        for (ItemStack itemstack : iterable) {
            if (!itemstack.isEmpty()) {
                ++j;
            }

            ++i;
        }

        return i > 0 ? (float) j / (float) i : 0.0F;
    }

    public void setSprinting(boolean p_70031_1_) {
        super.setSprinting(p_70031_1_);
        ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (modifiableattributeinstance.getModifier(SPEED_MODIFIER_SPRINTING_UUID) != null) {
            modifiableattributeinstance.removeModifier(SPEED_MODIFIER_SPRINTING);
        }

        if (p_70031_1_) {
            modifiableattributeinstance.addTransientModifier(SPEED_MODIFIER_SPRINTING);
        }

    }

    protected float getSoundVolume() {
        return 1.0F;
    }

    protected float getVoicePitch() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    protected boolean isImmobile() {
        return this.isDeadOrDying();
    }

    public void push(Entity p_70108_1_) {
        if (!this.isSleeping()) {
            super.push(p_70108_1_);
        }

    }

    private void dismountVehicle(Entity p_233628_1_) {
        Vector3d vector3d;
        if (!p_233628_1_.removed && !this.level.getBlockState(p_233628_1_.blockPosition()).getBlock().is(BlockTags.PORTALS)) {
            vector3d = p_233628_1_.getDismountLocationForPassenger(this);
        } else {
            vector3d = new Vector3d(p_233628_1_.getX(), p_233628_1_.getY() + (double) p_233628_1_.getBbHeight(), p_233628_1_.getZ());
        }

        this.teleportTo(vector3d.x, vector3d.y, vector3d.z);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldShowName() {
        return this.isCustomNameVisible();
    }

    protected float getJumpPower() {
        return 0.42F * this.getBlockJumpFactor();
    }

    protected void jumpFromGround() {
        float f = this.getJumpPower();
        if (this.hasEffect(Effects.JUMP)) {
            f += 0.1F * (float) (this.getEffect(Effects.JUMP).getAmplifier() + 1);
        }

        Vector3d vector3d = this.getDeltaMovement();
        boolean flag = this.hasEffect(Effects.GRAVITATION);
        this.setDeltaMovement(vector3d.x, flag ? -f : f, vector3d.z);
        if (this.isSprinting()) {
            float f1 = this.yRot * ((float) Math.PI / 180F);
            this.setDeltaMovement(this.getDeltaMovement().add((double) (-MathHelper.sin(f1) * 0.2F), 0.0D, (double) (MathHelper.cos(f1) * 0.2F)));
        }

        this.hasImpulse = true;
    }

    @OnlyIn(Dist.CLIENT)
    protected void goDownInWater() {
        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double) -0.04F, 0.0D));
    }

    protected void jumpInLiquid(ITag<Fluid> p_180466_1_) {
        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double) 0.04F, 0.0D));
    }

    protected float getWaterSlowDown() {
        return 0.8F;
    }

    public boolean canStandOnFluid(Fluid p_230285_1_) {
        return false;
    }

    public void travel(Vector3d p_213352_1_) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.hasEffect(Effects.CONFUSED) && !(this instanceof PlayerEntity)) {
                // Scale down the reversed movement vector to reduce backward movement
                double scaleFactor = 0.4 + (this.random.nextDouble() * 0.2); // Scale factor between 0.4 and 0.6

                // Reduce the backward motion and add jitter for x and z directions
                double newX = -p_213352_1_.x * scaleFactor + (this.random.nextDouble() - 0.5) * 0.4;
                double newZ = -p_213352_1_.z * scaleFactor + (this.random.nextDouble() - 0.5) * 0.4;

                // Apply these changes to create the new movement vector
                p_213352_1_ = new Vector3d(newX, p_213352_1_.y, newZ);
            }


            double d0 = 0.08D;
            boolean flag = this.getDeltaMovement().y <= 0.0D;
            if (flag && this.hasEffect(Effects.SLOW_FALLING)) {
                d0 = 0.01D;
                this.fallDistance = 0.0F;
            }

            if (this.hasEffect(Effects.GRAVITATION)) {
                d0 = -d0;
            }

            FluidState fluidstate = this.level.getFluidState(this.blockPosition());
            if (this.isInWater() && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate.getType())) {
                double d8 = this.getY();
                float f5 = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
                float f6 = 0.02F;
                float f7 = (float) EnchantmentHelper.getDepthStrider(this);
                if (f7 > 3.0F) {
                    f7 = 3.0F;
                }

                if (!this.onGround) {
                    f7 *= 0.5F;
                }

                if (f7 > 0.0F) {
                    f5 += (0.54600006F - f5) * f7 / 3.0F;
                    f6 += (this.getSpeed() - f6) * f7 / 3.0F;
                }

                if (this.hasEffect(Effects.DOLPHINS_GRACE)) {
                    f5 = 0.96F;
                }

                this.moveRelative(f6, p_213352_1_);
                this.move(MoverType.SELF, this.getDeltaMovement());
                Vector3d vector3d6 = this.getDeltaMovement();
                if (this.horizontalCollision && this.onClimbable()) {
                    vector3d6 = new Vector3d(vector3d6.x, 0.2D, vector3d6.z);
                }

                this.setDeltaMovement(vector3d6.multiply((double) f5, (double) 0.8F, (double) f5));
                Vector3d vector3d2 = this.getFluidFallingAdjustedMovement(d0, flag, this.getDeltaMovement());
                this.setDeltaMovement(vector3d2);
                if (this.horizontalCollision && this.isFree(vector3d2.x, vector3d2.y + (double) 0.6F - this.getY() + d8, vector3d2.z)) {
                    this.setDeltaMovement(vector3d2.x, (double) 0.3F, vector3d2.z);
                }
            } else if (this.isInLava() && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate.getType())) {
                double d7 = this.getY();
                this.moveRelative(0.02F, p_213352_1_);
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.getFluidHeight(FluidTags.LAVA) <= this.getFluidJumpThreshold()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, (double) 0.8F, 0.5D));
                    Vector3d vector3d3 = this.getFluidFallingAdjustedMovement(d0, flag, this.getDeltaMovement());
                    this.setDeltaMovement(vector3d3);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
                }

                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -d0 / 4.0D, 0.0D));
                }

                Vector3d vector3d4 = this.getDeltaMovement();
                if (this.horizontalCollision && this.isFree(vector3d4.x, vector3d4.y + (double) 0.6F - this.getY() + d7, vector3d4.z)) {
                    this.setDeltaMovement(vector3d4.x, (double) 0.3F, vector3d4.z);
                }
            } else if (this.isFallFlying()) { // Checks if the player is currently flying with Elytra
                ElytraItem.glide(this, d0);
            } else {
                BlockPos blockpos = this.getBlockPosBelowThatAffectsMyMovement();
                float f3 = this.level.getBlockState(blockpos).getBlock().getFriction();
                float f4 = this.onGround ? f3 * 0.91F : 0.91F;
                Vector3d vector3d5 = this.handleRelativeFrictionAndCalculateMovement(p_213352_1_, f3);
                double d2 = vector3d5.y;
                if (this.hasEffect(Effects.LEVITATION)) {
                    d2 += (0.05D * (double) (this.getEffect(Effects.LEVITATION).getAmplifier() + 1) - vector3d5.y) * 0.2D;
                    this.fallDistance = 0.0F;
                } else if (this.level.isClientSide && !this.level.hasChunkAt(blockpos)) {
                    if (this.getY() > 0.0D) {
                        d2 = -0.1D;
                    } else {
                        d2 = 0.0D;
                    }
                } else if (!this.isNoGravity()) {
                    d2 -= d0;
                }

                this.setDeltaMovement(vector3d5.x * (double) f4, d2 * (double) 0.98F, vector3d5.z * (double) f4);
            }
        }

        this.calculateEntityAnimation(this, this instanceof IFlyingAnimal);
    }

    public void calculateEntityAnimation(LivingEntity p_233629_1_, boolean p_233629_2_) {
        p_233629_1_.animationSpeedOld = p_233629_1_.animationSpeed;
        double d0 = p_233629_1_.getX() - p_233629_1_.xo;
        double d1 = p_233629_2_ ? p_233629_1_.getY() - p_233629_1_.yo : 0.0D;
        double d2 = p_233629_1_.getZ() - p_233629_1_.zo;
        float f = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 4.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        p_233629_1_.animationSpeed += (f - p_233629_1_.animationSpeed) * 0.4F;
        p_233629_1_.animationPosition += p_233629_1_.animationSpeed;
    }

    public Vector3d handleRelativeFrictionAndCalculateMovement(Vector3d p_233633_1_, float p_233633_2_) {
        this.moveRelative(this.getFrictionInfluencedSpeed(p_233633_2_), p_233633_1_);
        this.setDeltaMovement(this.handleOnClimbable(this.getDeltaMovement()));
        this.move(MoverType.SELF, this.getDeltaMovement());
        Vector3d vector3d = this.getDeltaMovement();
        if ((this.horizontalCollision || this.jumping) && this.onClimbable()) {
            vector3d = new Vector3d(vector3d.x, 0.2D, vector3d.z);
        }

        return vector3d;
    }

    public Vector3d getFluidFallingAdjustedMovement(double p_233626_1_, boolean p_233626_3_, Vector3d p_233626_4_) {
        if (!this.isNoGravity() && !this.isSprinting()) {
            double d0;
            if (p_233626_3_ && Math.abs(p_233626_4_.y - 0.005D) >= 0.003D && Math.abs(p_233626_4_.y - p_233626_1_ / 16.0D) < 0.003D) {
                d0 = -0.003D;
            } else {
                d0 = p_233626_4_.y - p_233626_1_ / 16.0D;
            }

            return new Vector3d(p_233626_4_.x, d0, p_233626_4_.z);
        } else {
            return p_233626_4_;
        }
    }

    private Vector3d handleOnClimbable(Vector3d p_213362_1_) {
        if (this.onClimbable()) {
            this.fallDistance = 0.0F;
            float f = 0.15F;
            double d0 = MathHelper.clamp(p_213362_1_.x, (double) -0.15F, (double) 0.15F);
            double d1 = MathHelper.clamp(p_213362_1_.z, (double) -0.15F, (double) 0.15F);
            double d2 = Math.max(p_213362_1_.y, (double) -0.15F);
            if (d2 < 0.0D && !this.getFeetBlockState().is(Blocks.SCAFFOLDING) && this.isSuppressingSlidingDownLadder() && this instanceof PlayerEntity) {
                d2 = 0.0D;
            }

            p_213362_1_ = new Vector3d(d0, d2, d1);
        }

        return p_213362_1_;
    }

    private float getFrictionInfluencedSpeed(float p_213335_1_) {
        return this.onGround ? this.getSpeed() * (0.21600002F / (p_213335_1_ * p_213335_1_ * p_213335_1_)) : this.flyingSpeed;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float p_70659_1_) {
        this.speed = p_70659_1_;
    }

    public boolean doHurtTarget(Entity p_70652_1_) {
        this.setLastHurtMob(p_70652_1_);
        return false;
    }

    public void tick() {
        super.tick();
        radiationManager.tick();
        ServerPlayerEntity player = DebugUtils.findLocalPlayer(this.level);
        if (player != null)
            DebugPacketSender.sendBrainDebugData(player, this);
        this.updatingUsingItem();
        this.updateSwimAmount();
        if (!this.level.isClientSide) {
            int i = this.getArrowCount();
            if (i > 0) {
                if (this.removeArrowTime <= 0) {
                    this.removeArrowTime = 20 * (30 - i);
                }

                --this.removeArrowTime;
                if (this.removeArrowTime <= 0) {
                    this.setArrowCount(i - 1);
                }
            }

            int j = this.getStingerCount();
            if (j > 0) {
                if (this.removeStingerTime <= 0) {
                    this.removeStingerTime = 20 * (30 - j);
                }

                --this.removeStingerTime;
                if (this.removeStingerTime <= 0) {
                    this.setStingerCount(j - 1);
                }
            }

            this.detectEquipmentUpdates();
            if (this.tickCount % 20 == 0) {
                this.getCombatTracker().recheckStatus();
            }

            if (!this.glowing) {
                boolean flag = this.hasEffect(Effects.GLOWING);
                if (this.getSharedFlag(6) != flag) {
                    this.setSharedFlag(6, flag);
                }
            }

            if (this.isSleeping() && !this.checkBedExists()) {
                this.stopSleeping();
            }
        }

        this.aiStep();
        double d0 = this.getX() - this.xo;
        double d1 = this.getZ() - this.zo;
        float f = (float) (d0 * d0 + d1 * d1);
        float f1 = this.yBodyRot;
        float f2 = 0.0F;
        this.oRun = this.run;
        float f3 = 0.0F;
        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float) Math.sqrt((double) f) * 3.0F;
            float f4 = (float) MathHelper.atan2(d1, d0) * (180F / (float) Math.PI) - 90.0F;
            float f5 = MathHelper.abs(MathHelper.wrapDegrees(this.yRot) - f4);
            if (95.0F < f5 && f5 < 265.0F) {
                f1 = f4 - 180.0F;
            } else {
                f1 = f4;
            }
        }

        if (this.attackAnim > 0.0F) {
            f1 = this.yRot;
        }

        if (!this.onGround) {
            f3 = 0.0F;
        }

        this.run += (f3 - this.run) * 0.3F;
        this.level.getProfiler().push("headTurn");
        f2 = this.tickHeadTurn(f1, f2);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("rangeChecks");

        while (this.yRot - this.yRotO < -180.0F) {
            this.yRotO -= 360.0F;
        }

        while (this.yRot - this.yRotO >= 180.0F) {
            this.yRotO += 360.0F;
        }

        while (this.yBodyRot - this.yBodyRotO < -180.0F) {
            this.yBodyRotO -= 360.0F;
        }

        while (this.yBodyRot - this.yBodyRotO >= 180.0F) {
            this.yBodyRotO += 360.0F;
        }

        while (this.xRot - this.xRotO < -180.0F) {
            this.xRotO -= 360.0F;
        }

        while (this.xRot - this.xRotO >= 180.0F) {
            this.xRotO += 360.0F;
        }

        while (this.yHeadRot - this.yHeadRotO < -180.0F) {
            this.yHeadRotO -= 360.0F;
        }

        while (this.yHeadRot - this.yHeadRotO >= 180.0F) {
            this.yHeadRotO += 360.0F;
        }

        this.level.getProfiler().pop();
        this.animStep += f2;
        if (this.isFallFlying()) {
            ++this.fallFlyTicks;
        } else {
            this.fallFlyTicks = 0;
        }

        if (this.isSleeping()) {
            this.xRot = 0.0F;
        }

    }

    private void detectEquipmentUpdates() {
        Map<EquipmentSlotType, ItemStack> map = this.collectEquipmentChanges();
        if (map != null) {
            this.handleHandSwap(map);
            if (!map.isEmpty()) {
                this.handleEquipmentChanges(map);
            }
        }

    }

    @Nullable
    private Map<EquipmentSlotType, ItemStack> collectEquipmentChanges() {
        Map<EquipmentSlotType, ItemStack> map = null;

        for (EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
            ItemStack itemstack;
            switch (equipmentslottype.getType()) {
                case HAND:
                    itemstack = this.getLastHandItem(equipmentslottype);
                    break;
                case ARMOR:
                    itemstack = this.getLastArmorItem(equipmentslottype);
                    break;
                default:
                    continue;
            }

            ItemStack itemstack1 = this.getItemBySlot(equipmentslottype);
            if (!ItemStack.matches(itemstack1, itemstack)) {
                if (map == null) {
                    map = Maps.newEnumMap(EquipmentSlotType.class);
                }

                map.put(equipmentslottype, itemstack1);
                if (!itemstack.isEmpty()) {
                    this.getAttributes().removeAttributeModifiers(itemstack.getAttributeModifiers(equipmentslottype));
                }

                if (!itemstack1.isEmpty()) {
                    this.getAttributes().addTransientAttributeModifiers(itemstack1.getAttributeModifiers(equipmentslottype));
                }
            }
        }

        return map;
    }

    private void handleHandSwap(Map<EquipmentSlotType, ItemStack> p_241342_1_) {
        ItemStack itemstack = p_241342_1_.get(EquipmentSlotType.MAINHAND);
        ItemStack itemstack1 = p_241342_1_.get(EquipmentSlotType.OFFHAND);
        if (itemstack != null && itemstack1 != null && ItemStack.matches(itemstack, this.getLastHandItem(EquipmentSlotType.OFFHAND)) && ItemStack.matches(itemstack1, this.getLastHandItem(EquipmentSlotType.MAINHAND))) {
            ((ServerWorld) this.level).getChunkSource().broadcast(this, new SEntityStatusPacket(this, (byte) 55));
            p_241342_1_.remove(EquipmentSlotType.MAINHAND);
            p_241342_1_.remove(EquipmentSlotType.OFFHAND);
            this.setLastHandItem(EquipmentSlotType.MAINHAND, itemstack.copy());
            this.setLastHandItem(EquipmentSlotType.OFFHAND, itemstack1.copy());
        }

    }

    private void handleEquipmentChanges(Map<EquipmentSlotType, ItemStack> p_241344_1_) {
        List<Pair<EquipmentSlotType, ItemStack>> list = Lists.newArrayListWithCapacity(p_241344_1_.size());
        p_241344_1_.forEach((p_241341_2_, p_241341_3_) -> {
            ItemStack itemstack = p_241341_3_.copy();
            list.add(Pair.of(p_241341_2_, itemstack));
            switch (p_241341_2_.getType()) {
                case HAND:
                    this.setLastHandItem(p_241341_2_, itemstack);
                    break;
                case ARMOR:
                    this.setLastArmorItem(p_241341_2_, itemstack);
            }

        });
        ((ServerWorld) this.level).getChunkSource().broadcast(this, new SEntityEquipmentPacket(this.getId(), list));
    }

    private ItemStack getLastArmorItem(EquipmentSlotType p_241346_1_) {
        return this.lastArmorItemStacks.get(p_241346_1_.getIndex());
    }

    private void setLastArmorItem(EquipmentSlotType p_241343_1_, ItemStack p_241343_2_) {
        this.lastArmorItemStacks.set(p_241343_1_.getIndex(), p_241343_2_);
    }

    private ItemStack getLastHandItem(EquipmentSlotType p_241347_1_) {
        return this.lastHandItemStacks.get(p_241347_1_.getIndex());
    }

    private void setLastHandItem(EquipmentSlotType p_241345_1_, ItemStack p_241345_2_) {
        this.lastHandItemStacks.set(p_241345_1_.getIndex(), p_241345_2_);
    }

    protected float tickHeadTurn(float p_110146_1_, float p_110146_2_) {
        float f = MathHelper.wrapDegrees(p_110146_1_ - this.yBodyRot);
        this.yBodyRot += f * 0.3F;
        float f1 = MathHelper.wrapDegrees(this.yRot - this.yBodyRot);
        boolean flag = f1 < -90.0F || f1 >= 90.0F;
        if (f1 < -75.0F) {
            f1 = -75.0F;
        }

        if (f1 >= 75.0F) {
            f1 = 75.0F;
        }

        this.yBodyRot = this.yRot - f1;
        if (f1 * f1 > 2500.0F) {
            this.yBodyRot += f1 * 0.2F;
        }

        if (flag) {
            p_110146_2_ *= -1.0F;
        }

        return p_110146_2_;
    }

    public void aiStep() {
        if (this.noJumpDelay > 0) {
            --this.noJumpDelay;
        }

        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.setPacketCoordinates(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d2 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d4 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d6 = MathHelper.wrapDegrees(this.lerpYRot - (double) this.yRot);
            this.yRot = (float) ((double) this.yRot + d6 / (double) this.lerpSteps);
            this.xRot = (float) ((double) this.xRot + (this.lerpXRot - (double) this.xRot) / (double) this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d2, d4);
            this.setRot(this.yRot, this.xRot);
        } else if (!this.isEffectiveAi()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }

        if (this.lerpHeadSteps > 0) {
            this.yHeadRot = (float) ((double) this.yHeadRot + MathHelper.wrapDegrees(this.lyHeadRot - (double) this.yHeadRot) / (double) this.lerpHeadSteps);
            --this.lerpHeadSteps;
        }

        Vector3d vector3d = this.getDeltaMovement();
        double d1 = vector3d.x;
        double d3 = vector3d.y;
        double d5 = vector3d.z;
        if (Math.abs(vector3d.x) < 0.003D) {
            d1 = 0.0D;
        }

        if (Math.abs(vector3d.y) < 0.003D) {
            d3 = 0.0D;
        }

        if (Math.abs(vector3d.z) < 0.003D) {
            d5 = 0.0D;
        }

        this.setDeltaMovement(d1, d3, d5);
        this.level.getProfiler().push("ai");
        if (this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;
        } else if (this.isEffectiveAi()) {
            this.level.getProfiler().push("newAi");
            this.serverAiStep();
            this.level.getProfiler().pop();
        }

        this.level.getProfiler().pop();
        this.level.getProfiler().push("jump");
        if (this.jumping && this.isAffectedByFluids()) {
            double d7;
            if (this.isInLava()) {
                d7 = this.getFluidHeight(FluidTags.LAVA);
            } else {
                d7 = this.getFluidHeight(FluidTags.WATER);
            }

            boolean flag = this.isInWater() && d7 > 0.0D;
            double d8 = this.getFluidJumpThreshold();
            if (!flag || this.onGround && !(d7 > d8)) {
                if (!this.isInLava() || this.onGround && !(d7 > d8)) {
                    if ((this.onGround || flag && d7 <= d8) && this.noJumpDelay == 0) {
                        this.jumpFromGround();
                        this.noJumpDelay = 10;
                    }
                } else {
                    this.jumpInLiquid(FluidTags.LAVA);
                }
            } else {
                this.jumpInLiquid(FluidTags.WATER);
            }
        } else {
            this.noJumpDelay = 0;
        }

        this.level.getProfiler().pop();
        this.level.getProfiler().push("travel");
        this.xxa *= 0.98F;
        this.zza *= 0.98F;
        this.updateFallFlying();
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        this.travel(new Vector3d((double) this.xxa, (double) this.yya, (double) this.zza));
        this.level.getProfiler().pop();
        this.level.getProfiler().push("push");
        if (this.autoSpinAttackTicks > 0) {
            --this.autoSpinAttackTicks;
            this.checkAutoSpinAttack(axisalignedbb, this.getBoundingBox());
        }

        this.pushEntities();
        this.level.getProfiler().pop();
        if (!this.level.isClientSide && this.isSensitiveToWater() && this.isInWaterRainOrBubble()) {
            this.hurt(DamageSource.DROWN, 1.0F);
        }

    }

    public boolean isSensitiveToWater() {
        return false;
    }

    private void updateFallFlying() {
        boolean flag = this.getSharedFlag(7);
        if (flag && !this.onGround && !this.isPassenger() && !this.hasEffect(Effects.LEVITATION)) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.CHEST);
            if (itemstack.getItem() == Items.ELYTRA && ElytraItem.isFlyEnabled(itemstack)) {
                flag = true;
                if (!this.level.isClientSide && (this.fallFlyTicks + 1) % 20 == 0) {
                    itemstack.hurtAndBreak(1, this, (p_233652_0_) -> {
                        p_233652_0_.broadcastBreakEvent(EquipmentSlotType.CHEST);
                    });
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }

        if (!this.level.isClientSide) {
            this.setSharedFlag(7, flag);
        }

    }

    protected void serverAiStep() {
    }

    protected void pushEntities() {
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox(), EntityPredicates.pushableBy(this));
        if (!list.isEmpty()) {
            int i = this.level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
            if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
                int j = 0;

                for (int k = 0; k < list.size(); ++k) {
                    if (!list.get(k).isPassenger()) {
                        ++j;
                    }
                }

                if (j > i - 1) {
                    this.hurt(DamageSource.CRAMMING, 6.0F);
                }
            }

            for (int l = 0; l < list.size(); ++l) {
                Entity entity = list.get(l);
                this.doPush(entity);
            }
        }

    }

    protected void checkAutoSpinAttack(AxisAlignedBB p_204801_1_, AxisAlignedBB p_204801_2_) {
        AxisAlignedBB axisalignedbb = p_204801_1_.minmax(p_204801_2_);
        List<Entity> list = this.level.getEntities(this, axisalignedbb);
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = list.get(i);
                if (entity instanceof LivingEntity) {
                    this.doAutoAttackOnTouch((LivingEntity) entity);
                    this.autoSpinAttackTicks = 0;
                    this.setDeltaMovement(this.getDeltaMovement().scale(-0.2D));
                    break;
                }
            }
        } else if (this.horizontalCollision) {
            this.autoSpinAttackTicks = 0;
        }

        if (!this.level.isClientSide && this.autoSpinAttackTicks <= 0) {
            this.setLivingEntityFlag(4, false);
        }

    }

    protected void doPush(Entity p_82167_1_) {
        p_82167_1_.push(this);
    }

    protected void doAutoAttackOnTouch(LivingEntity p_204804_1_) {
    }

    public void startAutoSpinAttack(int p_204803_1_) {
        this.autoSpinAttackTicks = p_204803_1_;
        if (!this.level.isClientSide) {
            this.setLivingEntityFlag(4, true);
        }

    }

    public boolean isAutoSpinAttack() {
        return (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 4) != 0;
    }

    public void stopRiding() {
        Entity entity = this.getVehicle();
        super.stopRiding();
        if (entity != null && entity != this.getVehicle() && !this.level.isClientSide) {
            this.dismountVehicle(entity);
        }

    }

    public void rideTick() {
        super.rideTick();
        this.oRun = this.run;
        this.run = 0.0F;
        this.fallDistance = 0.0F;
    }

    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
        this.lerpX = p_180426_1_;
        this.lerpY = p_180426_3_;
        this.lerpZ = p_180426_5_;
        this.lerpYRot = (double) p_180426_7_;
        this.lerpXRot = (double) p_180426_8_;
        this.lerpSteps = p_180426_9_;
    }

    @OnlyIn(Dist.CLIENT)
    public void lerpHeadTo(float p_208000_1_, int p_208000_2_) {
        this.lyHeadRot = (double) p_208000_1_;
        this.lerpHeadSteps = p_208000_2_;
    }

    public void setJumping(boolean p_70637_1_) {
        this.jumping = p_70637_1_;
    }

    public void onItemPickup(ItemEntity p_233630_1_) {
        PlayerEntity playerentity = p_233630_1_.getThrower() != null ? this.level.getPlayerByUUID(p_233630_1_.getThrower()) : null;
        if (playerentity instanceof ServerPlayerEntity) {
            CriteriaTriggers.ITEM_PICKED_UP_BY_ENTITY.trigger((ServerPlayerEntity) playerentity, p_233630_1_.getItem(), this);
        }

    }

    public void take(Entity p_71001_1_, int p_71001_2_) {
        if (!p_71001_1_.removed && !this.level.isClientSide && (p_71001_1_ instanceof ItemEntity || p_71001_1_ instanceof AbstractArrowEntity || p_71001_1_ instanceof ExperienceOrbEntity)) {
            ((ServerWorld) this.level).getChunkSource().broadcast(p_71001_1_, new SCollectItemPacket(p_71001_1_.getId(), this.getId(), p_71001_2_));
        }

    }

    public boolean canSee(Entity p_70685_1_) {
        Vector3d vector3d = new Vector3d(this.getX(), this.getEyeY(), this.getZ());
        Vector3d vector3d1 = new Vector3d(p_70685_1_.getX(), p_70685_1_.getEyeY(), p_70685_1_.getZ());
        return this.level.clip(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() == RayTraceResult.Type.MISS;
    }

    public float getViewYRot(float p_195046_1_) {
        return p_195046_1_ == 1.0F ? this.yHeadRot : MathHelper.lerp(p_195046_1_, this.yHeadRotO, this.yHeadRot);
    }

    @OnlyIn(Dist.CLIENT)
    public float getAttackAnim(float p_70678_1_) {
        float f = this.attackAnim - this.oAttackAnim;
        if (f < 0.0F) {
            ++f;
        }

        return this.oAttackAnim + f * p_70678_1_;
    }

    public boolean isEffectiveAi() {
        return !this.level.isClientSide;
    }

    public boolean isPickable() {
        return !this.removed;
    }

    public boolean isPushable() {
        return this.isAlive() && !this.isSpectator() && !this.onClimbable();
    }

    protected void markHurt() {
        this.hurtMarked = this.random.nextDouble() >= this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
    }

    public float getYHeadRot() {
        return this.yHeadRot;
    }

    public void setYHeadRot(float p_70034_1_) {
        this.yHeadRot = p_70034_1_;
    }

    public void setYBodyRot(float p_181013_1_) {
        this.yBodyRot = p_181013_1_;
    }

    protected Vector3d getRelativePortalPosition(Direction.Axis p_241839_1_, TeleportationRepositioner.Result p_241839_2_) {
        return resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(p_241839_1_, p_241839_2_));
    }

    public static Vector3d resetForwardDirectionOfRelativePortalPosition(Vector3d p_242288_0_) {
        return new Vector3d(p_242288_0_.x, p_242288_0_.y, 0.0D);
    }

    public float getAbsorptionAmount() {
        return this.absorptionAmount;
    }

    public void setAbsorptionAmount(float p_110149_1_) {
        if (p_110149_1_ < 0.0F) {
            p_110149_1_ = 0.0F;
        }

        this.absorptionAmount = p_110149_1_;
    }


    public void onEnterCombat() {
    }

    public void onLeaveCombat() {
    }

    protected void updateEffectVisibility() {
        this.effectsDirty = true;
    }

    public abstract HandSide getMainArm();

    public boolean isUsingItem() {
        return (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
    }

    public Hand getUsedItemHand() {
        return (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    private void updatingUsingItem() {
        if (this.isUsingItem()) {
            if (ItemStack.isSameIgnoreDurability(this.getItemInHand(this.getUsedItemHand()), this.useItem)) {
                this.useItem = this.getItemInHand(this.getUsedItemHand());
                this.useItem.onUseTick(this.level, this, this.getUseItemRemainingTicks());
                if (this.shouldTriggerItemUseEffects()) {
                    this.triggerItemUseEffects(this.useItem, 5);
                }

                if (--this.useItemRemaining == 0 && !this.level.isClientSide && !this.useItem.useOnRelease()) {
                    this.completeUsingItem();
                }
            } else {
                this.stopUsingItem();
            }
        }

    }

    private boolean shouldTriggerItemUseEffects() {
        int i = this.getUseItemRemainingTicks();
        Food food = this.useItem.getItem().getFoodProperties();
        boolean flag = food != null && food.isFastFood();
        flag = flag | i <= this.useItem.getUseDuration() - 7;
        return flag && i % 4 == 0;
    }

    private void updateSwimAmount() {
        this.swimAmountO = this.swimAmount;
        if (this.isVisuallySwimming()) {
            this.swimAmount = Math.min(1.0F, this.swimAmount + 0.09F);
        } else {
            this.swimAmount = Math.max(0.0F, this.swimAmount - 0.09F);
        }

    }

    protected void setLivingEntityFlag(int p_204802_1_, boolean p_204802_2_) {
        int i = this.entityData.get(DATA_LIVING_ENTITY_FLAGS);
        if (p_204802_2_) {
            i = i | p_204802_1_;
        } else {
            i = i & ~p_204802_1_;
        }

        this.entityData.set(DATA_LIVING_ENTITY_FLAGS, (byte) i);
    }

    public void startUsingItem(Hand p_184598_1_) {
        ItemStack itemstack = this.getItemInHand(p_184598_1_);
        if (!itemstack.isEmpty() && !this.isUsingItem()) {
            this.useItem = itemstack;
            this.useItemRemaining = itemstack.getUseDuration();
            if (!this.level.isClientSide) {
                this.setLivingEntityFlag(1, true);
                this.setLivingEntityFlag(2, p_184598_1_ == Hand.OFF_HAND);
            }

        }
    }

    public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
        super.onSyncedDataUpdated(p_184206_1_);
        if (SLEEPING_POS_ID.equals(p_184206_1_)) {
            if (this.level.isClientSide) {
                this.getSleepingPos().ifPresent(this::setPosToBed);
            }
        } else if (DATA_LIVING_ENTITY_FLAGS.equals(p_184206_1_) && this.level.isClientSide) {
            if (this.isUsingItem() && this.useItem.isEmpty()) {
                this.useItem = this.getItemInHand(this.getUsedItemHand());
                if (!this.useItem.isEmpty()) {
                    this.useItemRemaining = this.useItem.getUseDuration();
                }
            } else if (!this.isUsingItem() && !this.useItem.isEmpty()) {
                this.useItem = ItemStack.EMPTY;
                this.useItemRemaining = 0;
            }
        }

    }

    public void lookAt(EntityAnchorArgument.Type p_200602_1_, Vector3d p_200602_2_) {
        super.lookAt(p_200602_1_, p_200602_2_);
        this.yHeadRotO = this.yHeadRot;
        this.yBodyRot = this.yHeadRot;
        this.yBodyRotO = this.yBodyRot;
    }

    protected void triggerItemUseEffects(ItemStack p_226293_1_, int p_226293_2_) {
        if (!p_226293_1_.isEmpty() && this.isUsingItem()) {
            if (p_226293_1_.getUseAnimation() == UseAction.DRINK) {
                this.playSound(this.getDrinkingSound(p_226293_1_), 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }

            if (p_226293_1_.getUseAnimation() == UseAction.EAT) {
                this.spawnItemParticles(p_226293_1_, p_226293_2_);
                this.playSound(this.getEatingSound(p_226293_1_), 0.5F + 0.5F * (float) this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

        }
    }

    private void spawnItemParticles(ItemStack p_195062_1_, int p_195062_2_) {
        for (int i = 0; i < p_195062_2_; ++i) {
            Vector3d vector3d = new Vector3d(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vector3d = vector3d.xRot(-this.xRot * ((float) Math.PI / 180F));
            vector3d = vector3d.yRot(-this.yRot * ((float) Math.PI / 180F));
            double d0 = (double) (-this.random.nextFloat()) * 0.6D - 0.3D;
            Vector3d vector3d1 = new Vector3d(((double) this.random.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
            vector3d1 = vector3d1.xRot(-this.xRot * ((float) Math.PI / 180F));
            vector3d1 = vector3d1.yRot(-this.yRot * ((float) Math.PI / 180F));
            vector3d1 = vector3d1.add(this.getX(), this.getEyeY(), this.getZ());
            this.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, p_195062_1_), vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y + 0.05D, vector3d.z);
        }

    }

    protected void completeUsingItem() {
        Hand hand = this.getUsedItemHand();
        if (!this.useItem.equals(this.getItemInHand(hand))) {
            this.releaseUsingItem();
        } else {
            if (!this.useItem.isEmpty() && this.isUsingItem()) {
                this.triggerItemUseEffects(this.useItem, 16);
                ItemStack itemstack = this.useItem.finishUsingItem(this.level, this);
                if (itemstack != this.useItem) {
                    this.setItemInHand(hand, itemstack);
                }

                this.stopUsingItem();
            }

        }
    }

    public ItemStack getUseItem() {
        return this.useItem;
    }

    public int getUseItemRemainingTicks() {
        return this.useItemRemaining;
    }

    public int getTicksUsingItem() {
        return this.isUsingItem() ? this.useItem.getUseDuration() - this.getUseItemRemainingTicks() : 0;
    }

    public void releaseUsingItem() {
        if (!this.useItem.isEmpty()) {
            this.useItem.releaseUsing(this.level, this, this.getUseItemRemainingTicks());
            if (this.useItem.useOnRelease()) {
                this.updatingUsingItem();
            }
        }

        this.stopUsingItem();
    }

    public void stopUsingItem() {
        if (!this.level.isClientSide) {
            this.setLivingEntityFlag(1, false);
        }

        this.useItem = ItemStack.EMPTY;
        this.useItemRemaining = 0;
    }

    public boolean isBlocking() {
        if (this.isUsingItem() && !this.useItem.isEmpty()) {
            Item item = this.useItem.getItem();
            if (item.getUseAnimation(this.useItem) != UseAction.BLOCK) {
                return false;
            } else {
                return item.getUseDuration(this.useItem) - this.useItemRemaining >= 5;
            }
        } else {
            return false;
        }
    }

    public boolean isSuppressingSlidingDownLadder() {
        return this.isShiftKeyDown();
    }

    public boolean isFallFlying() {
        return this.getSharedFlag(7);
    }

    public boolean isVisuallySwimming() {
        return super.isVisuallySwimming() || !this.isFallFlying() && this.getPose() == Pose.FALL_FLYING;
    }

    @OnlyIn(Dist.CLIENT)
    public int getFallFlyingTicks() {
        return this.fallFlyTicks;
    }

    public boolean randomTeleport(double p_213373_1_, double p_213373_3_, double p_213373_5_, boolean p_213373_7_) {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        double d3 = p_213373_3_;
        boolean flag = false;
        BlockPos blockpos = new BlockPos(p_213373_1_, p_213373_3_, p_213373_5_);
        World world = this.level;
        if (world.hasChunkAt(blockpos)) {
            boolean flag1 = false;

            while (!flag1 && blockpos.getY() > 0) {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = world.getBlockState(blockpos1);
                if (blockstate.getMaterial().blocksMotion()) {
                    flag1 = true;
                } else {
                    --d3;
                    blockpos = blockpos1;
                }
            }

            if (flag1) {
                this.teleportTo(p_213373_1_, d3, p_213373_5_);
                if (world.noCollision(this) && !world.containsAnyLiquid(this.getBoundingBox())) {
                    flag = true;
                }
            }
        }

        if (!flag) {
            this.teleportTo(d0, d1, d2);
            return false;
        } else {
            if (p_213373_7_) {
                world.broadcastEntityEvent(this, (byte) 46);
            }

            if (this instanceof Creature) {
                ((Creature) this).getNavigation().stop();
            }

            return true;
        }
    }

    public boolean isAffectedByPotions() {
        return true;
    }

    public boolean attackable() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void setRecordPlayingNearby(BlockPos p_191987_1_, boolean p_191987_2_) {
    }

    public boolean canTakeItem(ItemStack p_213365_1_) {
        return false;
    }

    public IPacket<?> getAddEntityPacket() {
        return new SSpawnMobPacket(this);
    }

    public EntitySize getDimensions(Pose p_213305_1_) {
        return p_213305_1_ == Pose.SLEEPING ? SLEEPING_DIMENSIONS : super.getDimensions(p_213305_1_).scale(this.getScale());
    }

    public ImmutableList<Pose> getDismountPoses() {
        return ImmutableList.of(Pose.STANDING);
    }

    public AxisAlignedBB getLocalBoundsForPose(Pose p_233648_1_) {
        EntitySize entitysize = this.getDimensions(p_233648_1_);
        return new AxisAlignedBB((double) (-entitysize.width / 2.0F), 0.0D, (double) (-entitysize.width / 2.0F), (double) (entitysize.width / 2.0F), (double) entitysize.height, (double) (entitysize.width / 2.0F));
    }

    public Optional<BlockPos> getSleepingPos() {
        return this.entityData.get(SLEEPING_POS_ID);
    }

    public void setSleepingPos(BlockPos p_213369_1_) {
        this.entityData.set(SLEEPING_POS_ID, Optional.of(p_213369_1_));
    }

    public void clearSleepingPos() {
        this.entityData.set(SLEEPING_POS_ID, Optional.empty());
    }

    public boolean isSleeping() {
        return this.getSleepingPos().isPresent();
    }

    public void startSleeping(BlockPos p_213342_1_) {
        if (this.isPassenger()) {
            this.stopRiding();
        }

        BlockState blockstate = this.level.getBlockState(p_213342_1_);
        if (blockstate.getBlock() instanceof BedBlock) {
            this.level.setBlock(p_213342_1_, blockstate.setValue(BedBlock.OCCUPIED, Boolean.valueOf(true)), 3);
        }

        this.setPose(Pose.SLEEPING);
        this.setPosToBed(p_213342_1_);
        this.setSleepingPos(p_213342_1_);
        this.setDeltaMovement(Vector3d.ZERO);
        this.hasImpulse = true;
    }

    private void setPosToBed(BlockPos p_213370_1_) {
        this.setPos((double) p_213370_1_.getX() + 0.5D, (double) p_213370_1_.getY() + 0.6875D, (double) p_213370_1_.getZ() + 0.5D);
    }

    private boolean checkBedExists() {
        return this.getSleepingPos().map((p_241350_1_) -> {
            return this.level.getBlockState(p_241350_1_).getBlock() instanceof BedBlock;
        }).orElse(false);
    }

    public void stopSleeping() {
        this.getSleepingPos().filter(this.level::hasChunkAt).ifPresent((p_241348_1_) -> {
            BlockState blockstate = this.level.getBlockState(p_241348_1_);
            if (blockstate.getBlock() instanceof BedBlock) {
                this.level.setBlock(p_241348_1_, blockstate.setValue(BedBlock.OCCUPIED, Boolean.valueOf(false)), 3);
                Vector3d vector3d1 = BedBlock.findStandUpPosition(this.getType(), this.level, p_241348_1_, this.yRot).orElseGet(() -> {
                    BlockPos blockpos = p_241348_1_.above();
                    return new Vector3d((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.1D, (double) blockpos.getZ() + 0.5D);
                });
                Vector3d vector3d2 = Vector3d.atBottomCenterOf(p_241348_1_).subtract(vector3d1).normalize();
                float f = (float) MathHelper.wrapDegrees(MathHelper.atan2(vector3d2.z, vector3d2.x) * (double) (180F / (float) Math.PI) - 90.0D);
                this.setPos(vector3d1.x, vector3d1.y, vector3d1.z);
                this.yRot = f;
                this.xRot = 0.0F;
            }

        });
        Vector3d vector3d = this.position();
        this.setPose(Pose.STANDING);
        this.setPos(vector3d.x, vector3d.y, vector3d.z);
        this.clearSleepingPos();
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public Direction getBedOrientation() {
        BlockPos blockpos = this.getSleepingPos().orElse((BlockPos) null);
        return blockpos != null ? BedBlock.getBedOrientation(this.level, blockpos) : null;
    }

    public boolean isInWall() {
        return !this.isSleeping() && super.isInWall();
    }

    protected final float getEyeHeight(Pose p_213316_1_, EntitySize p_213316_2_) {
        return p_213316_1_ == Pose.SLEEPING ? 0.2F : this.getStandingEyeHeight(p_213316_1_, p_213316_2_);
    }

    protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
        return super.getEyeHeight(p_213348_1_, p_213348_2_);
    }

    public ItemStack getProjectile(ItemStack p_213356_1_) {
        return ItemStack.EMPTY;
    }

    public ItemStack eat(World p_213357_1_, ItemStack p_213357_2_) {
        if (p_213357_2_.isEdible()) {
            p_213357_1_.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), this.getEatingSound(p_213357_2_), SoundCategory.NEUTRAL, 1.0F, 1.0F + (p_213357_1_.random.nextFloat() - p_213357_1_.random.nextFloat()) * 0.4F);
            this.addEatEffect(p_213357_2_, p_213357_1_, this);
            if (!(this instanceof PlayerEntity) || !((PlayerEntity) this).abilities.instabuild) {
                p_213357_2_.shrink(1);
            }
        }

        return p_213357_2_;
    }

    private void addEatEffect(ItemStack p_213349_1_, World p_213349_2_, LivingEntity p_213349_3_) {
        Item item = p_213349_1_.getItem();
        if (item.isEdible()) {
            for (Pair<EffectInstance, Float> pair : item.getFoodProperties().getEffects()) {
                if (!p_213349_2_.isClientSide && pair.getFirst() != null && p_213349_2_.random.nextFloat() < pair.getSecond()) {
                    p_213349_3_.addEffect(new EffectInstance(pair.getFirst()));
                }
            }
        }

    }

    private static byte entityEventForEquipmentBreak(EquipmentSlotType p_213350_0_) {
        switch (p_213350_0_) {
            case MAINHAND:
                return 47;
            case OFFHAND:
                return 48;
            case HEAD:
                return 49;
            case CHEST:
                return 50;
            case FEET:
                return 52;
            case LEGS:
                return 51;
            default:
                return 47;
        }
    }

    public void broadcastBreakEvent(EquipmentSlotType p_213361_1_) {
        this.level.broadcastEntityEvent(this, entityEventForEquipmentBreak(p_213361_1_));
    }

    public void broadcastBreakEvent(Hand p_213334_1_) {
        this.broadcastBreakEvent(p_213334_1_ == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND);
    }

    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getBoundingBoxForCulling() {
        if (this.getItemBySlot(EquipmentSlotType.HEAD).getItem() == Items.DRAGON_HEAD) {
            float f = 0.5F;
            return this.getBoundingBox().inflate(0.5D, 0.5D, 0.5D);
        } else {
            return super.getBoundingBoxForCulling();
        }
    }
}