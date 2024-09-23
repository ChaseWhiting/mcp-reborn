package net.minecraft.entity.herobrine;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class HerobrineEntity extends Monster implements ICrossbowUser {
    private static final DataParameter<Boolean> IS_CHARGING_CROSSBOW = EntityDataManager.defineId(HerobrineEntity.class, DataSerializers.BOOLEAN);
    private static final ResourceLocation HEROBRINE_TEXTURE = new ResourceLocation("textures/entity/herobrine.png");
    private AttackGoal meleeAttackGoal = new AttackGoal(this);
    public HerobrineEntity(EntityType<? extends HerobrineEntity> herobrine, World world) {
        super(herobrine, world);
        this.applyOpenDoorsAbility();
        this.setCanPickUpLoot(true);
    }

    private void applyOpenDoorsAbility() {
        if (GroundPathHelper.hasGroundPathNavigation(this)) {
            ((GroundPathNavigator)this.getNavigation()).setCanOpenDoors(true);
        }

    }

    protected void customServerAiStep() {
        super.customServerAiStep();

        this.updateSwingTime();
        this.yHeadRot = this.yRot;
    }

    protected void updatePlayerPose() {
        if (this.canEnterPose(Pose.SWIMMING)) {
            Pose pose;
            if (this.isFallFlying()) {
                pose = Pose.FALL_FLYING;
            } else if (this.isSleeping()) {
                pose = Pose.SLEEPING;
            } else if (this.isSwimming()) {
                pose = Pose.SWIMMING;
            } else if (this.isAutoSpinAttack()) {
                pose = Pose.SPIN_ATTACK;
            } else if (this.isShiftKeyDown()) {
                pose = Pose.CROUCHING;
            } else {
                pose = Pose.STANDING;
            }

            Pose pose1;
            if (!this.isSpectator() && !this.isPassenger() && !this.canEnterPose(pose)) {
                if (this.canEnterPose(Pose.CROUCHING)) {
                    pose1 = Pose.CROUCHING;
                } else {
                    pose1 = Pose.SWIMMING;
                }
            } else {
                pose1 = pose;
            }

            this.setPose(pose1);
        }
    }

    @Nullable
    public ILivingEntityData finalizeSpawn(IServerWorld iServerWorld, DifficultyInstance difficulty, SpawnReason spawnReason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        if (this.random.nextFloat() < 0.2) {
            this.setItemInHand(Hand.MAIN_HAND, new ItemStack(Items.CROSSBOW));
        } else {
            this.setItemInHand(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND_SWORD));
        }

        return super.finalizeSpawn(iServerWorld, difficulty, spawnReason, data, compoundNBT);
    }


    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(this, 1.0D, 8.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.addGoal(1, new RandomWalkingGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
    }




    @OnlyIn(Dist.CLIENT)
    public boolean isChargingCrossbow() {
        return this.entityData.get(IS_CHARGING_CROSSBOW);
    }

    public void setChargingCrossbow(boolean value) {
        this.entityData.set(IS_CHARGING_CROSSBOW, value);
    }

    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    public void performRangedAttack(LivingEntity entity, float v) {
        this.performCrossbowAttack(this, 1.6F);
    }

    public void shootCrossbowProjectile(LivingEntity entity, ItemStack stack, ProjectileEntity projectileEntity, float v) {
        this.shootCrossbowProjectile(this, entity, projectileEntity, v, 1.6F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CHARGING_CROSSBOW, false);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 90.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.42D)
                .add(Attributes.FOLLOW_RANGE, 130.0D);
    }

    private boolean isAboveGround() {
        return this.onGround || this.fallDistance < this.maxUpStep && !this.level.noCollision(this, this.getBoundingBox().move(0.0D, (double) (this.fallDistance - this.maxUpStep), 0.0D));
    }

    public float getCurrentItemAttackStrengthDelay() {
        return (float) (1.0D / this.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0D);
    }

    public float getAttackStrengthScale(float p_184825_1_) {
        return MathHelper.clamp(((float) this.attackStrengthTicker + p_184825_1_) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
    }

    public void resetAttackStrengthTicker() {
        this.attackStrengthTicker = 0;
    }

    public void sweepAttack() {
        double d0 = (double) (-MathHelper.sin(this.yRot * ((float) Math.PI / 180F)));
        double d1 = (double) MathHelper.cos(this.yRot * ((float) Math.PI / 180F));
        if (this.level instanceof ServerWorld) {
            ((ServerWorld) this.level).sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + d0, this.getY(0.5D), this.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
        }

    }

    public void attack(Entity entity1) {
        if (entity1.isAttackable()) {
            if (!entity1.skipAttackInteraction(this)) {
                float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float f1;
                if (entity1 instanceof LivingEntity) {
                    f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity) entity1).getMobType());
                } else {
                    f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), CreatureAttribute.UNDEFINED);
                }

                float f2 = this.getAttackStrengthScale(0.5F);
                f = f * (0.2F + f2 * f2 * 0.8F);
                f1 = f1 * f2;
                this.resetAttackStrengthTicker();
                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag = f2 > 0.9F;
                    boolean sprinting = false;
                    int i = 0;
                    i = i + EnchantmentHelper.getKnockbackBonus(this);
                    if (this.isSprinting() && flag) {
                        this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);
                        ++i;
                        sprinting = true;
                    }

                    boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround && !this.onClimbable() && !this.isInWater() && !this.hasEffect(Effects.BLINDNESS) && !this.isPassenger() && entity1 instanceof LivingEntity;
                    flag2 = flag2 && !this.isSprinting();
                    if (flag2) {
                        f *= 1.5F;
                    }

                    f = f + f1;
                    boolean holdingSword = false;
                    double d0 = (double) (this.walkDist - this.walkDistO);
                    if (flag && !flag2 && !sprinting && this.onGround && d0 < (double) this.getSpeed()) {
                        ItemStack itemstack = this.getItemInHand(Hand.MAIN_HAND);
                        if (itemstack.getItem() instanceof SwordItem) {
                            holdingSword = true;
                        }
                    }

                    float f4 = 0.0F;
                    boolean hasFireAspect = false;
                    int j = EnchantmentHelper.getFireAspect(this);
                    if (entity1 instanceof LivingEntity) {
                        f4 = ((LivingEntity) entity1).getHealth();
                        if (j > 0 && !entity1.isOnFire()) {
                            hasFireAspect = true;
                            entity1.setSecondsOnFire(1);
                        }
                    }

                    Vector3d vector3d = entity1.getDeltaMovement();
                    boolean mobAttack = entity1.hurt(DamageSource.mobAttack(this), f);
                    if (mobAttack) {
                        if (i > 0) {
                            if (entity1 instanceof LivingEntity) {
                                ((LivingEntity) entity1).knockback((float) i * 0.5F, (double) MathHelper.sin(this.yRot * ((float) Math.PI / 180F)), (double) (-MathHelper.cos(this.yRot * ((float) Math.PI / 180F))));
                            } else {
                                entity1.push((double) (-MathHelper.sin(this.yRot * ((float) Math.PI / 180F)) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(this.yRot * ((float) Math.PI / 180F)) * (float) i * 0.5F));
                            }

                            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                            this.setSprinting(false);
                        }

                        if (holdingSword) {
                            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * f;

                            for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, entity1.getBoundingBox().inflate(1.0D, 0.25D, 1.0D))) {
                                if (livingentity != this && livingentity != entity1 && !this.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).isMarker()) && this.distanceToSqr(livingentity) < 9.0D) {
                                    livingentity.knockback(0.4F, (double) MathHelper.sin(this.yRot * ((float) Math.PI / 180F)), (double) (-MathHelper.cos(this.yRot * ((float) Math.PI / 180F))));
                                    livingentity.hurt(DamageSource.mobAttack(this), f3);
                                }
                            }

                            this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F);
                            this.sweepAttack();
                        }

                        if (entity1 instanceof ServerPlayerEntity && entity1.hurtMarked) {
                            ((ServerPlayerEntity) entity1).connection.send(new SEntityVelocityPacket(entity1));
                            entity1.hurtMarked = false;
                            entity1.setDeltaMovement(vector3d);
                        }

                        if (flag2) {
                            this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F);
                            this.crit(entity1);
                        }

                        if (!flag2 && !holdingSword) {
                            if (flag) {
                                this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0F, 1.0F);
                            } else {
                                this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0F, 1.0F);
                            }
                        }

                        if (f1 > 0.0F) {
                            this.magicCrit(entity1);
                        }

                        this.setLastHurtMob(entity1);
                        if (entity1 instanceof LivingEntity) {
                            EnchantmentHelper.doPostHurtEffects((LivingEntity) entity1, this);
                        }

                        EnchantmentHelper.doPostDamageEffects(this, entity1);
                        ItemStack itemstack1 = this.getMainHandItem();
                        Entity entity = entity1;
                        if (entity1 instanceof EnderDragonPartEntity) {
                            entity = ((EnderDragonPartEntity) entity1).parentMob;
                        }

                        if (!this.level.isClientSide && !itemstack1.isEmpty() && entity instanceof LivingEntity) {
                            itemstack1.hurtEnemy((LivingEntity) entity, this);
                            if (itemstack1.isEmpty()) {
                                this.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (entity1 instanceof LivingEntity) {
                            float f5 = f4 - ((LivingEntity) entity1).getHealth();

                            if (j > 0) {
                                entity1.setSecondsOnFire(j * 4);
                            }

                            if (this.level instanceof ServerWorld && f5 > 2.0F) {
                                int k = (int) ((double) f5 * 0.5D);
                                ((ServerWorld) this.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity1.getX(), entity1.getY(0.5D), entity1.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }


                    } else {
                        this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0F, 1.0F);
                        if (hasFireAspect) {
                            entity1.clearFire();
                        }
                    }
                }

            }
        }
    }

    public void crit(Entity p_71009_1_) {
    }

    public void magicCrit(Entity p_71047_1_) {
    }



    public ResourceLocation getSkinTextureLocation() {
        return HEROBRINE_TEXTURE;
    }

    public float getStandingEyeHeight(Pose pose, EntitySize size) {
        return switch (pose) {
            case SWIMMING, FALL_FLYING, SPIN_ATTACK -> 0.4F;
            case CROUCHING -> 1.27F;
            default -> 1.62F;
        };
    }
//
//    public void travel(Vector3d p_213352_1_) {
//        double d0 = this.getX();
//        double d1 = this.getY();
//        double d2 = this.getZ();
//        if (this.isSwimming() && !this.isPassenger()) {
//            double d3 = this.getLookAngle().y;
//            double d4 = d3 < -0.2D ? 0.085D : 0.06D;
//            if (d3 <= 0.0D || this.jumping || !this.level.getBlockState(new BlockPos(this.getX(), this.getY() + 1.0D - 0.1D, this.getZ())).getFluidState().isEmpty()) {
//                Vector3d vector3d1 = this.getDeltaMovement();
//                this.setDeltaMovement(vector3d1.add(0.0D, (d3 - vector3d1.y) * d4, 0.0D));
//            }
//        }
//
//        if (!this.isPassenger()) {
//            double d5 = this.getDeltaMovement().y;
//            float f = this.flyingSpeed;
//            this.flyingSpeed = this.getSpeed() * (float) (this.isSprinting() ? 2 : 1);
//            super.travel(p_213352_1_);
//            Vector3d vector3d = this.getDeltaMovement();
//            this.setDeltaMovement(vector3d.x, d5 * 0.6D, vector3d.z);
//            this.flyingSpeed = f;
//            this.fallDistance = 0.0F;
//            this.setSharedFlag(7, false);
//        } else {
//            super.travel(p_213352_1_);
//        }
//
//
//    }

    public void tick() {
        super.tick();

        ItemStack handItem = this.getMainHandItem();
        if (handItem.getItem() == Items.CROSSBOW && !this.goalSelector.getAvailableGoals().anyMatch(Predicate.isEqual(meleeAttackGoal))) {
            this.goalSelector.removeGoal(meleeAttackGoal);
        } else {
            this.goalSelector.addGoal(1, meleeAttackGoal);
        }

       // this.updatePlayerPose();
    }

    private boolean isPvpAllowed() {
        return this.level.getServer().isPvpAllowed();
    }

//    public boolean hurt(DamageSource source, float damageAmount) {
//
//        if (this.isInvulnerableTo(source)) {
//            return false;
//        } else if (!source.isBypassInvul()) {
//            return false;
//        } else {
//            this.noActionTime = 0;
//            if (this.isDeadOrDying()) {
//                return false;
//            } else {
//                if (source.scalesWithDifficulty()) {
//                    if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
//                        damageAmount = 0.0F;
//                    }
//
//                    if (this.level.getDifficulty() == Difficulty.EASY) {
//                        damageAmount = Math.min(damageAmount / 2.0F + 1.0F, damageAmount);
//                    }
//
//                    if (this.level.getDifficulty() == Difficulty.HARD) {
//                        damageAmount = damageAmount * 3.0F / 2.0F;
//                    }
//                }
//
//                return damageAmount == 0.0F ? false : super.hurt(source, damageAmount);
//            }
//        }
//    }



    protected SoundEvent getHurtSound(DamageSource source) {
        if (source == DamageSource.ON_FIRE) {
            return SoundEvents.PLAYER_HURT_ON_FIRE;
        } else if (source == DamageSource.DROWN) {
            return SoundEvents.PLAYER_HURT_DROWN;
        } else {
            return source == DamageSource.SWEET_BERRY_BUSH ? SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH : SoundEvents.PLAYER_HURT;
        }
    }


    @Override
    protected void jumpFromGround() {
        float f = this.getJumpPower();
        if (this.hasEffect(Effects.JUMP)) {
            f += 0.1F * (float)(this.getEffect(Effects.JUMP).getAmplifier() + 1);
        }

        Vector3d vector3d = this.getDeltaMovement();
        this.setDeltaMovement(vector3d.x, (double)f, vector3d.z);
        if (this.isSprinting()) {
            float f1 = this.yRot * ((float)Math.PI / 180F);
            this.setDeltaMovement(this.getDeltaMovement().add((double)(-MathHelper.sin(f1) * 0.2F), 0.0D, (double)(MathHelper.cos(f1) * 0.2F)));
        }

        this.hasImpulse = true;
    }

    public boolean doHurtTarget(Entity entity) {

        return super.doHurtTarget(entity);
    }

    class AttackGoal extends MeleeAttackGoal {
        private Entity entity;
        private HerobrineEntity herobrine;
        public AttackGoal(HerobrineEntity herobrine) {
            super(herobrine, 0.8017D, true);
            this.herobrine = herobrine;
        }

        @Override
        public boolean canUse() {
            List<PlayerEntity> players = this.herobrine.level.getEntitiesOfClass(PlayerEntity.class, this.herobrine.getBoundingBox().inflate(8));
            if (tryCounterShield()) {
                return true;
            }
            if (this.mob.getTarget() != null) {
                entity = this.mob.getTarget();
            } else {
                return false;
            }
            return super.canUse() && entity != null && facingTarget(entity) || super.canUse() && !players.isEmpty();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.mob.getTarget() != null) {
                entity = this.mob.getTarget();
            }

            if (entity == null || !entity.isAlive() || !this.mob.getSensing().canSee(entity)) {
                return false;
            }

            return super.canContinueToUse();
        }

        private boolean facingTarget(Entity entity1) {
            if (entity1 == null) {
                return false;
            }

            // Check if Herobrine is facing the target
            Vector3d lookVec = this.herobrine.getViewVector(1.0F).normalize();
            Vector3d vecToTarget = new Vector3d(entity1.getX() - this.herobrine.getX(), entity1.getEyeY() - this.herobrine.getEyeY(), entity1.getZ() - this.herobrine.getZ()).normalize();

            double dotProduct = lookVec.dot(vecToTarget);
            double threshold = Math.cos(Math.toRadians(48)); // 30 degrees field of view to consider "facing"

            return dotProduct > threshold;
        }

        @Override
        public void stop() {
            super.stop();
         //   this.herobrine.setSprinting(false);
        }

        @Override
        public void start() {
            super.start();
            //this.herobrine.setSprinting(true);
        }

        @Override
        public void tick() {
            super.tick();

            if (this.herobrine.getTarget() != null) {
                this.herobrine.getLookControl().setLookAt(this.herobrine.getTarget(), 30.0F, 30.0F);
                if (!tryCounterShield()) {
                    this.herobrine.yRot = MathHelper.rotateIfNecessary(herobrine.yRot, herobrine.yHeadRot, 0.0F);

//                    if (this.mob.tickCount % 20 == 0 && this.mob.isOnGround() && facingTarget(this.entity)) {
//                        this.herobrine.jumpFromGround();
//                    }
                }

            }

        }


        public boolean tryCounterShield() {
            if (entity != null && entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

                // Check if the target is holding and using a shield
                if (livingEntity.getItemBySlot(EquipmentSlotType.MAINHAND).getItem() instanceof ShieldItem) {
                    if (livingEntity.isUsingItem() && livingEntity.getUseItem().getItem() instanceof ShieldItem) {

                        // Calculate the position behind the target
                        Vector3d targetLookVec = livingEntity.getLookAngle();
                        Vector3d behindPosition = new Vector3d(
                                livingEntity.getX() - targetLookVec.x * 2,
                                livingEntity.getY(),
                                livingEntity.getZ() - targetLookVec.z * 2
                        );

                        // Move Herobrine to the position behind the target
                        this.herobrine.getNavigation().moveTo(behindPosition.x, behindPosition.y, behindPosition.z, 0.5);

                        // Ensure Herobrine is sprinting while trying to move behind the target
                        this.herobrine.setSprinting(true);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target, double dis) {
            double d0 = this.getAttackReachSqr(target);
            if (facingTarget(target)) {
                if (dis <= d0 && ticksUntilNextAttack <= 0) {
                    this.resetAttackCooldown();
                    this.mob.swing(Hand.MAIN_HAND);
                    this.mob.doHurtTarget(target);
                }
            }
        }

        @Override
        protected double getAttackReachSqr(LivingEntity livingEntity) {
            return 8.96D;
        }
    }

}
