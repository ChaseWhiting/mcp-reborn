package net.minecraft.entity.passive;

import java.util.*;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.time.TimeUtil;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PolarBearEntity extends Animal implements IAngerable {
    private static final DataParameter<Boolean> DATA_STANDING_ID = EntityDataManager.defineId(PolarBearEntity.class, DataSerializers.BOOLEAN);
    private float clientSideStandAnimationO;
    private float clientSideStandAnimation;
    private int warningSoundTicks;
    private static final RangedInteger PERSISTENT_ANGER_TIME = TickRangeConverter.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;


    private static final Ingredient TEMPT_INGREDIENT = Ingredient.of(Items.COD);
    private static final DataParameter<Boolean> DATA_TRUSTING = EntityDataManager.defineId(PolarBearEntity.class, DataSerializers.BOOLEAN);

    public int cannotReceiveFoodFor = 0;

    public boolean isTrusting() {
        return this.entityData.get(DATA_TRUSTING);
    }

    public void setTrusting(boolean trusting) {
        if (trusting == false) {
            this.getAttribute(Attributes.ARMOR).setBaseValue(0D);
        }

        this.entityData.set(DATA_TRUSTING, trusting);
        //this.reassessTrustingGoals();
    }

    public PolarBearEntity(EntityType<? extends PolarBearEntity> p_i50249_1_, World p_i50249_2_) {
        super(p_i50249_1_, p_i50249_2_);
    }

    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return EntityType.POLAR_BEAR.create(p_241840_1_);
    }

    public boolean isFood(ItemStack p_70877_1_) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 41) {
            this.spawnTrustingParticles(true);
        } else if (p_70103_1_ == 40) {
            this.spawnTrustingParticles(false);
        } else {
            super.handleEntityEvent(p_70103_1_);
        }

    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new PolarBearEntity.MeleeAttackGoal());
        this.goalSelector.addGoal(1, new PolarBearEntity.PanicGoal());
//        this.goalSelector.addGoal(3, new FindItemsGoal(this, 1F, TEMPT_INGREDIENT) {
//            @Override
//            public void move(List<ItemEntity> list) {
//                if (!mob.closerThan(list.get(0), 1.6D)) {
//                    if (mob.canSee(list.get(0))) {
//                        super.move(list);
//                    } else {
//                        stop();
//                    }
//                } else {
//                    list.get(0).remove();
//
//                    PolarBearEntity pb = (PolarBearEntity) mob;
//                   pb.playSound(SoundEvents.POLAR_BEAR_EAT, 1.1F, 1.0F);
//                   if (random.nextInt(5) == 0) {
//                        pb.setTrusting(true);
//                        pb.spawnTrustingParticles(true);
//                        pb.level.broadcastEntityEvent(pb, (byte) 41);
//                    } else {
//                        pb.spawnTrustingParticles(false);
//                        pb.level.broadcastEntityEvent(pb, (byte) 40);
//                    }
//                }
//            }
//
//            @Override
//            public boolean canUse() {
//                return super.canUse() && !mob.isBaby() && !((PolarBearEntity) mob).isTrusting();
//            }
//
//            @Override
//            public boolean canContinueToUse() {
//                return super.canContinueToUse() && !mob.isBaby() && !((PolarBearEntity) mob).isTrusting();
//            }
//
//            @Override
//            public List<ItemEntity> findNearbyItems() {
//                return super.findNearbyItems().stream().filter(item -> item.getThrower() != null && mob.level instanceof ServerWorld sv && sv.getPlayerByUUID(item.getThrower()) != null)
//                        .filter(item -> item.getItem().getCount() == 1 && !item.hasPickUpDelay()).toList();
//            }
//
//
//        });
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new RandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new PolarBearEntity.HurtByTargetGoal());
        this.targetSelector.addGoal(2, new PolarBearEntity.AttackPlayerGoal());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, 60, true, true, this::canAttackEnemy));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, FoxEntity.class, 10, true, true, (Predicate<LivingEntity>) null));
        this.targetSelector.addGoal(5, new ResetAngerGoal<>(this, false));
    }

    public boolean canAttackEnemy(LivingEntity p_233680_1_) {
        return isTrusting() && !(p_233680_1_ instanceof CreeperEntity) && EntityPredicates.ATTACK_ALLOWED.test(p_233680_1_) && !p_233680_1_.hasCustomName();
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.FOLLOW_RANGE, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    private void spawnTrustingParticles(boolean b) {
        IParticleData iparticledata = ParticleTypes.HEART;
        if (!b) {
            iparticledata = ParticleTypes.SMOKE;
        }
        for (int i = 0; i < 7; ++i) {
            double d0 = this.getRandom().nextGaussian() * 0.02D;
            double d1 = this.getRandom().nextGaussian() * 0.02D;
            double d2 = this.getRandom().nextGaussian() * 0.02D;
            this.level.addParticle(iparticledata, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    public static boolean checkPolarBearSpawnRules(EntityType<PolarBearEntity> p_223320_0_, IWorld p_223320_1_, SpawnReason p_223320_2_, BlockPos p_223320_3_, Random p_223320_4_) {
        Optional<RegistryKey<Biome>> optional = p_223320_1_.getBiomeName(p_223320_3_);
        if (!Objects.equals(optional, Optional.of(Biomes.FROZEN_OCEAN)) && !Objects.equals(optional, Optional.of(Biomes.DEEP_FROZEN_OCEAN))) {
            return checkAnimalSpawnRules(p_223320_0_, p_223320_1_, p_223320_2_, p_223320_3_, p_223320_4_);
        } else {
            return p_223320_1_.getRawBrightness(p_223320_3_, 0) > 8 && p_223320_1_.getBlockState(p_223320_3_.below()).is(Blocks.ICE);
        }
    }

    public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
        super.readAdditionalSaveData(p_70037_1_);
        this.readPersistentAngerSaveData((ServerWorld) this.level, p_70037_1_);
        this.setTrusting(p_70037_1_.getBoolean("Trusting"));
        this.cannotReceiveFoodFor = p_70037_1_.getInt("CannotReceiveFoodFor");
    }

    public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
        super.addAdditionalSaveData(p_213281_1_);
        this.addPersistentAngerSaveData(p_213281_1_);
        p_213281_1_.putBoolean("Trusting", this.isTrusting());
        p_213281_1_.putInt("CannotReceiveFoodFor", this.cannotReceiveFoodFor);
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
    }

    public void setRemainingPersistentAngerTime(int p_230260_1_) {
        this.remainingPersistentAngerTime = p_230260_1_;
    }

    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    public void setPersistentAngerTarget(@Nullable UUID p_230259_1_) {
        this.persistentAngerTarget = p_230259_1_;
    }

    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    protected SoundEvent getAmbientSound() {
        return this.isBaby() ? SoundEvents.POLAR_BEAR_AMBIENT_BABY : SoundEvents.POLAR_BEAR_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.0F, this.getVoicePitch());
            this.warningSoundTicks = 40;
        }

    }

    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (cannotReceiveFoodFor > 0 || isBaby()) {
            return super.mobInteract(player, hand);
        }

        if (!this.isTrusting() && TEMPT_INGREDIENT.test(itemstack) && player.distanceToSqr(this) < 16.0D && !this.isAngry()) {
            this.usePlayerItem(player, itemstack);
            if (!this.level.isClientSide) {
                playSound(SoundEvents.POLAR_BEAR_EAT, 1.1F, 1.0F);
                if (this.random.nextInt(6) == 0) {
                    this.getAttribute(Attributes.ARMOR).setBaseValue(12D);
                    this.setTrusting(true);
                    this.spawnTrustingParticles(true);
                    this.level.broadcastEntityEvent(this, (byte)41);
                } else {
                    this.spawnTrustingParticles(false);
                    this.level.broadcastEntityEvent(this, (byte)40);
                }
            }

            return ActionResultType.sidedSuccess(this.level.isClientSide);
        } else if (isTrusting() && TEMPT_INGREDIENT.test(itemstack) && this.getHealth() < this.getMaxHealth() && !isAngryAt(player)){
            this.usePlayerItem(player, itemstack);
            if (!this.level.isClientSide) {
                playSound(SoundEvents.POLAR_BEAR_EAT, 1.1F, 1.0F);
                this.heal(5F);
            }

            return ActionResultType.sidedSuccess(this.level.isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STANDING_ID, false);
        this.entityData.define(DATA_TRUSTING, false);
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.clientSideStandAnimation != this.clientSideStandAnimationO) {
                this.refreshDimensions();
            }

            this.clientSideStandAnimationO = this.clientSideStandAnimation;
            if (this.isStanding()) {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
            } else {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }

        if (!this.level.isClientSide) {
            this.updatePersistentAnger((ServerWorld) this.level, true);
            if (this.getAttribute(Attributes.ARMOR).getBaseValue() != 0D && this.isTrusting()) {
                getAttribute(Attributes.ARMOR).setBaseValue(12D);

            } else if (this.isTrusting() && getAttribute(Attributes.ARMOR).getBaseValue() > 0D) {
                getAttribute(Attributes.ARMOR).setBaseValue(0D);
            }
            if (cannotReceiveFoodFor > 0) {
                cannotReceiveFoodFor--;
            }
        }

    }

    public boolean hurt(DamageSource source, float damage) {
        if (source.getMsgId().toLowerCase().contains("player") && source.getEntity() instanceof PlayerEntity player && !player.isCreative()) {
            setTrusting(false);
            this.cannotReceiveFoodFor = random.nextInt(TimeUtil.minutesToTicks(2.5D), TimeUtil.minutesToTicks(8.5D));

            for (PolarBearEntity pb : level.getEntitiesOfClass(PolarBearEntity.class, this.getBoundingBox().inflate(12D, 12D, 12D), e -> e != this && !e.isBaby() && e.isTrusting())) {
                pb.setTrusting(false);
                pb.cannotReceiveFoodFor = random.nextInt(TimeUtil.minutesToTicks(2.5D), TimeUtil.minutesToTicks(8.5D));
                pb.setTarget(this.getLastHurtByMob());
            }
        }

        return super.hurt(source, damage);
    }

    public EntitySize getDimensions(Pose p_213305_1_) {
        if (this.clientSideStandAnimation > 0.0F) {
            float f = this.clientSideStandAnimation / 6.0F;
            float f1 = 1.0F + f;
            return super.getDimensions(p_213305_1_).scale(1.0F, f1);
        } else {
            return super.getDimensions(p_213305_1_);
        }
    }

    public boolean doHurtTarget(Entity target) {
        boolean flag = target.hurt(DamageSource.mobAttack(this), (float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            this.doEnchantDamageEffects(this, target);
        }

        return flag;
    }

    public boolean isStanding() {
        return this.entityData.get(DATA_STANDING_ID);
    }

    public void setStanding(boolean p_189794_1_) {
        this.entityData.set(DATA_STANDING_ID, p_189794_1_);
    }

    @OnlyIn(Dist.CLIENT)
    public float getStandingAnimationScale(float p_189795_1_) {
        return MathHelper.lerp(p_189795_1_, this.clientSideStandAnimationO, this.clientSideStandAnimation) / 6.0F;
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        if (p_213386_4_ == null) {
            p_213386_4_ = new AgeableEntity.AgeableData(1.0F);
        }

        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }

    class AttackPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {
        public AttackPlayerGoal() {
            super(PolarBearEntity.this, PlayerEntity.class, 20, true, true, (Predicate<LivingEntity>) null);
        }

        public boolean canUse() {
            if (PolarBearEntity.this.isBaby() || PolarBearEntity.this.isTrusting()) {
                return false;
            } else {
                if (super.canUse()) {
                    for (PolarBearEntity polarbearentity : PolarBearEntity.this.level.getEntitiesOfClass(PolarBearEntity.class, PolarBearEntity.this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D))) {
                        if (polarbearentity.isBaby()) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.5D;
        }
    }

    class HurtByTargetGoal extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
        public HurtByTargetGoal() {
            super(PolarBearEntity.this);
        }

        public void start() {
            super.start();
            if (PolarBearEntity.this.isBaby()) {
                this.alertOthers();
                this.stop();
            }

        }

        protected void alertOther(Mob p_220793_1_, LivingEntity p_220793_2_) {
            if (p_220793_1_ instanceof PolarBearEntity pb && !p_220793_1_.isBaby()) {
                super.alertOther(p_220793_1_, p_220793_2_);
                if (p_220793_2_ instanceof PlayerEntity) {
                    if (PolarBearEntity.this.isTrusting() || pb.isTrusting()) {
                        pb.setTrusting(false);
                        PolarBearEntity.this.setTrusting(false);
                    }
                }
            }

        }
    }

    class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {
        public MeleeAttackGoal() {
            super(PolarBearEntity.this, 1.25D, true);
        }

        protected void checkAndPerformAttack(LivingEntity p_190102_1_, double p_190102_2_) {
            double d0 = this.getAttackReachSqr(p_190102_1_);
            if (p_190102_2_ <= d0 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(p_190102_1_);
                PolarBearEntity.this.setStanding(false);
            } else if (p_190102_2_ <= d0 * 2.0D) {
                if (this.isTimeToAttack()) {
                    PolarBearEntity.this.setStanding(false);
                    this.resetAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    PolarBearEntity.this.setStanding(true);
                    PolarBearEntity.this.playWarningSound();
                }
            } else {
                this.resetAttackCooldown();
                PolarBearEntity.this.setStanding(false);
            }

        }

        public void stop() {
            PolarBearEntity.this.setStanding(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity p_179512_1_) {
            return (double) (4.0F + p_179512_1_.getBbWidth());
        }
    }

    class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
        public PanicGoal() {
            super(PolarBearEntity.this, 2.0D);
        }

        public boolean canUse() {
            return !PolarBearEntity.this.isBaby() && !PolarBearEntity.this.isOnFire() ? false : super.canUse();
        }
    }
}