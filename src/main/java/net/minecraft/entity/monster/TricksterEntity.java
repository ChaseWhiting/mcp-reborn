package net.minecraft.entity.monster;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowEntity;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.tool.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TricksterEntity extends SpellcastingIllagerEntity {

    public boolean hasRealAttacks = true;
    @Nullable
    public TricksterEntity realCopy = null;
    public int despawnTimer = 1200;
    @Nullable
    private UUID realCopyUUID = null;

    public TricksterEntity(EntityType<? extends TricksterEntity> p_i50207_1_, World p_i50207_2_) {
        super(p_i50207_1_, p_i50207_2_);
        this.xpReward = 20;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D).add(Attributes.FOLLOW_RANGE, 45.0D).add(Attributes.MAX_HEALTH, 45.0D).add(Attributes.ARMOR, 12d);
    }

    public void setAsFalse(TricksterEntity tricksterEntity) {
        this.hasRealAttacks = false;
        this.realCopyUUID = tricksterEntity.getUUID();
        this.realCopy = tricksterEntity;
        if (tricksterEntity.hasCustomName()) {
            this.setCustomName(tricksterEntity.getCustomName());
            this.setCustomNameVisible(tricksterEntity.isCustomNameVisible());
        }
        if (tricksterEntity.persistenceRequired) {
            this.setPersistenceRequired();
        }
        this.setDropChance(EquipmentSlotType.MAINHAND, 0.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(3, new MoveUntilInRangeGoal());
        this.goalSelector.addGoal(3, new SummonDecoysSpell());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PlayerEntity.class, 12.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new FireworkBlastSpell());
        this.goalSelector.addGoal(2, new BlindnessFireworkSpell());
        this.goalSelector.addGoal(4, new DeadlyFireworksSpell());
        this.goalSelector.addGoal(5, new FireworkRainSpell());
        this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(120 * 20));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(25 * 20));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
        if (p_70037_1_.contains("HasRealAttacks")) {
            this.hasRealAttacks = p_70037_1_.getBoolean("HasRealAttacks");
        }
        this.despawnTimer = p_70037_1_.getInt("DespawnTimer");
        if (p_70037_1_.contains("RealCopyUUID")) {
            this.realCopyUUID = p_70037_1_.getUUID("RealCopyUUID");
        }
        super.readAdditionalSaveData(p_70037_1_);
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.TRICKSTER_CELEBRATE;
    }

    public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
        p_213281_1_.putBoolean("HasRealAttacks", this.hasRealAttacks);
        p_213281_1_.putInt("DespawnTimer", this.despawnTimer);
        if (realCopyUUID != null) {
            p_213281_1_.putUUID("RealCopyUUID", this.realCopyUUID);
        }
        super.addAdditionalSaveData(p_213281_1_);
    }

    protected void customServerAiStep() {
        ServerWorld serverWorld = (ServerWorld) this.level;
        super.customServerAiStep();
        if (!hasRealAttacks) {
            if (despawnTimer-- < 0 && !persistenceRequired) {
                ItemStack goodbyeFirework = new FireworkUtility.Builder().setFlightDuration(2).addExplosion(FireworkRocketItem.Shape.BURST, new DyeColor[]{DyeColor.BLUE, DyeColor.PURPLE}, new DyeColor[]{DyeColor.CYAN}, true, true).build();
                FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(this.level, this, this.getX(), this.getY() + 0.5, this.getZ(), goodbyeFirework);
                fireworkRocket.setNoDamage();
                level.addFreshEntity(fireworkRocket);
                this.remove();
            }


            if (realCopy == null && this.realCopyUUID != null) {
                Entity living = serverWorld.getEntity(this.realCopyUUID);
                if (living instanceof TricksterEntity) {
                    TricksterEntity tricksterEntity = living.as(TricksterEntity.class);
                    this.realCopy = tricksterEntity;
                    this.realCopyUUID = tricksterEntity.getUUID();
                } else {
                    this.despawnTimer = -1;
                }
            } else if (realCopy != null && !realCopy.isAlive()) {
                despawnTimer -= 10;
            }

            if (realCopy != null && realCopy.isAlive() && despawnTimer > 40) {
                if (realCopy.getTarget() != null && realCopy.getTarget() != this.getTarget()) {
                    this.setTarget(null);
                    this.setTarget(realCopy.getTarget());
                }
            }

        }

    }

    public boolean isAlliedTo(@NotNull Entity entity) {
        if (entity == this) {
            return true;
        } else if (super.isAlliedTo(entity)) {
            return true;
        } else if (entity instanceof VexEntity) {
            return this.isAlliedTo(((VexEntity) entity).getOwner());
        } else if (entity instanceof LivingEntity && ((LivingEntity) entity).getMobType() == CreatureAttribute.ILLAGER) {
            return this.getTeam() == null && entity.getTeam() == null;
        } else {
            return false;
        }
    }


    @Override
    public boolean isInvulnerableTo(DamageSource p_180431_1_) {
        if (p_180431_1_.getEntity() != null && p_180431_1_.getEntity() instanceof CustomArrowEntity && ((CustomArrowEntity) p_180431_1_.getEntity()).getArrowType() == CustomArrowType.FIREWORK)
            return true;

        if (p_180431_1_.getDirectEntity() != null && p_180431_1_.getDirectEntity() instanceof TricksterEntity)
            return true;

        return super.isInvulnerableTo(p_180431_1_) || p_180431_1_.getMsgId().equals("fireworks") || p_180431_1_.getMsgId().equals("explosion.player");
    }

    public boolean hurt(DamageSource source, float damage) {
        if (!this.hasRealAttacks) {
            return super.hurt(source, damage * 3);
        } else {
            return super.hurt(source, damage);
        }
    }

    public int getCooldownForSpell(int originalCooldown) {
        int c = veryHardmode() ? TickRangeConverter.rangeOfSeconds(5, 9).randomValue(this.random) : 0;

        return (originalCooldown - c) * 20;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.TRICKSTER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.TRICKSTER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.TRICKSTER_HURT;
    }

    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    public void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_) {
    }

    private class MoveUntilInRangeGoal extends Goal {
        private int ticksUntilNextRecalculation = 0;

        private MoveUntilInRangeGoal() {
        }

        @Override
        public boolean canUse() {
            LivingEntity target = TricksterEntity.this.getTarget();
            if (target == null) return false;

            return (TricksterEntity.this.distanceTo(target) > 16 || !TricksterEntity.this.canSee(target));
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = TricksterEntity.this.getTarget();
            if (target == null) return false;

            return (TricksterEntity.this.distanceTo(target) > 16 || !TricksterEntity.this.canSee(target) ||
                    TricksterEntity.this.getNavigation().isDone()) && !TricksterEntity.this.isCastingSpell();
        }

        @Override
        public void start() {
            LivingEntity target = TricksterEntity.this.getTarget();

            if (target != null) {
                TricksterEntity.this.getNavigation().moveTo(target, 0.7f);
            }

        }

        @Override
        public void tick() {
            LivingEntity target = TricksterEntity.this.getTarget();
            if (ticksUntilNextRecalculation-- < 0) {
                if (target != null) {
                    TricksterEntity.this.getNavigation().moveTo(target, 0.7f);
                    ticksUntilNextRecalculation = TickRangeConverter.rangeOfSeconds(2, 5).randomValue(TricksterEntity.this.random);
                }
            }
        }

        @Override
        public void stop() {
            TricksterEntity.this.getNavigation().stop();
        }
    }

    class FireworkRainSpell extends FireworkSpellGoal {
        private FireworkRainSpell() {
        }

        @Override
        public boolean canUse() {
            return super.canUse() && TricksterEntity.this.getHealth() > 15;
        }

        protected int getCastingTime() {
            return 40;
        }

        protected int getCastingInterval() {
            return TricksterEntity.this.getCooldownForSpell(10);
        }

        protected void performSpellCasting() {
            LivingEntity livingentity = TricksterEntity.this.getTarget();
            assert livingentity != null;
            double d1 = Math.max(livingentity.getY() + 4D, TricksterEntity.this.getY()) + 7.0D;
            float f = (float) MathHelper.atan2(livingentity.getZ() - TricksterEntity.this.getZ(), livingentity.getX() - TricksterEntity.this.getX());
            if (TricksterEntity.this.distanceToSqr(livingentity) < 9.0D) {
                for (int i = 0; i < 5; ++i) {
                    float f1 = f + (float) i * (float) Math.PI * 0.4F;
                    this.createSpellEntity(TricksterEntity.this.getX() + (double) MathHelper.cos(f1) * 1.5D, TricksterEntity.this.getZ() + (double) MathHelper.sin(f1) * 1.5D, d1);
                }

                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 2.0F / 8.0F + 1.2566371F;
                    this.createSpellEntity(TricksterEntity.this.getX() + (double) MathHelper.cos(f2) * 2.5D, TricksterEntity.this.getZ() + (double) MathHelper.sin(f2) * 2.5D, d1);
                }
            } else {
                for (int i = 0; i < 5; ++i) {
                    float f1 = f + (float) i * (float) Math.PI * 0.4F;
                    this.createSpellEntity(livingentity.getX() + (double) MathHelper.cos(f1) * 1.5D, livingentity.getZ() + (double) MathHelper.sin(f1) * 1.5D, d1);
                }

                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 2.0F / 8.0F + 1.2566371F;
                    this.createSpellEntity(livingentity.getX() + (double) MathHelper.cos(f2) * 2.5D, livingentity.getZ() + (double) MathHelper.sin(f2) * 2.5D, d1);
                }
            }

        }

        private void createSpellEntity(double x, double z, double y) {
            BlockPos blockpos = new BlockPos(x, y, z);
            ItemStack stack = this.getRocket(this.getSpell());

            CustomArrowEntity customArrowEntity = new CustomArrowEntity(TricksterEntity.this.level, TricksterEntity.this);
            customArrowEntity.setArrowType(CustomArrowType.FIREWORK);

            customArrowEntity.setFireworkStack(stack);
            customArrowEntity.setDeltaMovement(new Vector3d(0, -0.03, 0));
            customArrowEntity.moveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            if (!TricksterEntity.this.hasRealAttacks) {
                customArrowEntity.setNoDamage();
            }
            TricksterEntity.this.level.addFreshEntity(customArrowEntity);
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        protected SpellType getSpell() {
            return SpellType.FIREWORK_LAUNCH;
        }
    }

    class CastingSpellGoal extends CastingASpellGoal {
        private CastingSpellGoal() {
        }

        public void tick() {
            if (TricksterEntity.this.getTarget() != null) {
                TricksterEntity.this.getLookControl().setLookAt(TricksterEntity.this.getTarget(), (float) TricksterEntity.this.getMaxHeadYRot(), (float) TricksterEntity.this.getMaxHeadXRot());
            }

        }
    }

    abstract class FireworkSpellGoal extends UseSpellGoal {

        public void start() {
            super.start();
            if (TricksterEntity.this.getMainHandItem().isEmpty()) {
                TricksterEntity.this.setItemInHand(Hand.MAIN_HAND, new ItemStack(Items.FIREWORK_STAFF));
            }
        }

        public boolean canUse() {
            if (TricksterEntity.this.getTarget() == null || !TricksterEntity.this.getTarget().isAlive()) {
                return false;
            }

            boolean flag;

            if (this instanceof FireworkRainSpell || this instanceof DeadlyFireworksSpell) {
                flag = TricksterEntity.this.distanceTo(TricksterEntity.this.getTarget()) <= 28;
            } else {
                flag = TricksterEntity.this.canSee(TricksterEntity.this.getTarget()) && TricksterEntity.this.distanceTo(TricksterEntity.this.getTarget()) <= 17;

            }

            return super.canUse() && flag;
        }

        public void stop() {
            super.stop();
            if (TricksterEntity.this.getMainHandItem().get() == Items.FIREWORK_STAFF) {
                TricksterEntity.this.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);

            }
        }

        public ItemStack getRocket(SpellType type) {
            FireworkUtility.Builder builder = new FireworkUtility.Builder();

            return switch (type) {
                case BLINDNESS -> builder.setFlightDuration(1)
                        .addExplosion(FireworkRocketItem.Shape.BURST,
                                new DyeColor[]{DyeColor.BLACK},
                                new DyeColor[]{DyeColor.GRAY}, true, false)
                        .build();
                case FIREWORK_LAUNCH -> builder.setFlightDuration(1)
                        .addExplosion(FireworkRocketItem.Shape.SMALL_BALL,
                                new DyeColor[]{DyeColor.GREEN, DyeColor.PURPLE},
                                new DyeColor[]{DyeColor.PURPLE}, true, true)
                        .build();
                case FIREWORK_CIRCLE -> builder.setFlightDuration(1)
                        .addExplosion(FireworkRocketItem.Shape.LARGE_BALL,
                                new DyeColor[]{DyeColor.RED, DyeColor.BLACK},
                                new DyeColor[]{DyeColor.RED, DyeColor.BLACK}, true, true)
                        .build();
                default -> builder.setFlightDuration(1)
                        .addExplosion(FireworkRocketItem.Shape.BURST,
                                new DyeColor[]{DyeColor.RED, DyeColor.BLUE},
                                new DyeColor[]{DyeColor.PINK, DyeColor.PURPLE}, false, true)
                        .build();

            };

        }
    }


    class SummonDecoysSpell extends FireworkSpellGoal {

        private SummonDecoysSpell() {
        }

        @Override
        public boolean canUse() {
            LivingEntity target = TricksterEntity.this.getTarget();
            return super.canUse() && target != null && target.isAlive() && target.hasEffect(Effects.BLINDNESS) && Objects.requireNonNull(target.getEffect(Effects.BLINDNESS)).getDuration() > 5 * 20 && TricksterEntity.this.hasRealAttacks && TricksterEntity.this.realCopy == null && target.distanceTo(TricksterEntity.this) > 6;
        }

        @Override
        protected void performSpellCasting() {
            LivingEntity target = TricksterEntity.this.getTarget();


            if (target != null) {
                World world = target.level;
                TricksterEntity decoyOne = EntityType.TRICKSTER.create(world);
                TricksterEntity decoyTwo = EntityType.TRICKSTER.create(world);
                if (decoyOne == null || decoyTwo == null) return;
                for (TricksterEntity tricksterEntity : List.of(decoyOne, decoyTwo)) {
                    tricksterEntity.setAsFalse(TricksterEntity.this);
                    tricksterEntity.setTarget(target);


                    tricksterEntity.setHealth(TricksterEntity.this.getHealth());
                    tricksterEntity.moveTo(TricksterEntity.this.position().add(0, 1, 0));
                    for (EffectInstance effectInstance : TricksterEntity.this.getActiveEffects()) {
                        if (effectInstance != null) {
                            tricksterEntity.addEffect(effectInstance);
                        }
                    }
                    world.addFreshEntity(tricksterEntity);
                }


            }
        }

        @Override
        protected int getCastingTime() {
            return 5 * 20;
        }

        @Override
        protected int getCastWarmupTime() {
            return 60;
        }

        @Override
        protected int getCastingInterval() {
            return TricksterEntity.this.getCooldownForSpell(60);
        }


        @Override
        protected SpellType getSpell() {
            return SpellType.BLINDNESS;
        }


        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

    }


    class FireworkBlastSpell extends FireworkSpellGoal {

        private FireworkBlastSpell() {
        }

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                int i = TricksterEntity.this.random.nextInt(6) + 1;
                return TricksterEntity.this.random.nextInt(8) + 1 > i && TricksterEntity.this.getTarget() != null;
            }
        }

        protected int getCastingTime() {
            return 2 * 20;
        }

        protected int getCastingInterval() {
            return TricksterEntity.this.getCooldownForSpell(10);
        }

        protected void performSpellCasting() {
            ServerWorld serverworld = (ServerWorld) TricksterEntity.this.level;
            ItemStack firework = this.getRocket(this.getSpell());
            int randomVal = serverworld.random.nextInt(5) != 0 ? 2 : 3;
            for (int i = 0; i < randomVal; ++i) {
                LivingEntity target = TricksterEntity.this.getTarget();
                CustomArrowEntity customArrowEntity = new CustomArrowEntity(serverworld, TricksterEntity.this);

                if (!TricksterEntity.this.hasRealAttacks) {
                    customArrowEntity.setNoDamage();
                }
                BlockPos blockpos;
                assert target != null;
                boolean isHoldingShield = (target.getUseItem().getItem() instanceof ShieldItem || target.getUseItem().getItem() instanceof AbstractShieldItem);

                if (isHoldingShield) {
                    float yawDegrees = target.yRot;

                    double yawRadians = Math.toRadians(yawDegrees);

                    double offsetX = -Math.sin(yawRadians) * -12;
                    double offsetZ = Math.cos(yawRadians) * -12;

                    Vector3d targetPosition = target.position();

                    Vector3d behindPosition = new Vector3d(
                            targetPosition.x + offsetX,
                            targetPosition.y + 1,
                            targetPosition.z + offsetZ
                    );

                    blockpos = new BlockPos(
                            behindPosition.x,
                            behindPosition.y,
                            behindPosition.z
                    );
                } else {
                    blockpos = TricksterEntity.this.blockPosition().offset(
                            -3 + TricksterEntity.this.random.nextInt(7),
                            2.5,
                            -3 + TricksterEntity.this.random.nextInt(7)
                    );
                }

                double d0 = target.getX() - blockpos.getX();
                double d1 = target.getY(0.3333333333333333D) - customArrowEntity.getY();
                double d2 = target.getZ() - blockpos.getZ();
                double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);

                customArrowEntity.setArrowType(CustomArrowType.FIREWORK);
                customArrowEntity.setFireworkStack(firework);

                float inaccuracy = (float) (14 - TricksterEntity.this.level.getDifficulty().getId() * 4);

                if (isHoldingShield) {
                    inaccuracy /= 2;
                }

                customArrowEntity.moveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ());

                customArrowEntity.shoot(d0, d1 + d3 * 0.2F, d2, isHoldingShield ? 1.6f : 1.35F, inaccuracy);
                serverworld.addFreshEntityWithPassengers(customArrowEntity);
            }
        }


        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        protected SpellType getSpell() {
            return SpellType.FIREWORK;
        }
    }

    class BlindnessFireworkSpell extends FireworkSpellGoal {

        private BlindnessFireworkSpell() {
        }

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                return TricksterEntity.this.getTarget() != null && !TricksterEntity.this.getTarget().hasEffect(Effects.BLINDNESS);
            }
        }

        protected int getCastingTime() {
            return 3 * 20;
        }

        protected int getCastingInterval() {
            return TricksterEntity.this.getCooldownForSpell(15);
        }

        protected void performSpellCasting() {
            ServerWorld serverworld = (ServerWorld) TricksterEntity.this.level;
            ItemStack firework = this.getRocket(this.getSpell());
            LivingEntity target = TricksterEntity.this.getTarget();
            CustomArrowEntity customArrowEntity = new CustomArrowEntity(serverworld, TricksterEntity.this);
            BlockPos blockpos;
            assert target != null;
            boolean isHoldingShield = (target.getUseItem().getItem() instanceof ShieldItem || target.getUseItem().getItem() instanceof AbstractShieldItem);

            if (isHoldingShield) {
                float yRotDegrees = target.yRot;

                double yRotRadians = Math.toRadians(yRotDegrees);

                double offsetX = -Math.sin(yRotRadians);
                double offsetZ = Math.cos(yRotRadians);

                Vector3d fireworkPosition = target.position()
                        .add(offsetX * -12, 2, offsetZ * -12);

                customArrowEntity.moveTo(fireworkPosition.x, fireworkPosition.y, fireworkPosition.z);
            } else {
                blockpos = TricksterEntity.this.blockPosition().offset(0, 1.5, 0);
                customArrowEntity.moveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            }

            double d0 = target.getX() - customArrowEntity.getX();
            double d1 = target.getY(0.3333333333333333D) - customArrowEntity.getY();
            double d2 = target.getZ() - customArrowEntity.getZ();
            double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);

            customArrowEntity.setArrowType(CustomArrowType.FIREWORK);
            customArrowEntity.setFireworkStack(firework);
            if (!TricksterEntity.this.hasRealAttacks) {
                customArrowEntity.setNoDamage();
            }
            float inaccuracy = (float) (8 - TricksterEntity.this.level.getDifficulty().getId() * 4);
            if (inaccuracy > 2) {
                inaccuracy /= 2;
            }

            customArrowEntity.giveBlindness();

            customArrowEntity.shoot(d0, d1 + d3 * 0.2F, d2, 1.6F, inaccuracy);
            serverworld.addFreshEntityWithPassengers(customArrowEntity);
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        protected SpellType getSpell() {
            return SpellType.BLINDNESS;
        }
    }


    class DeadlyFireworksSpell extends FireworkSpellGoal {
        private DeadlyFireworksSpell() {
        }

        @Override
        public boolean canUse() {
            return super.canUse() && TricksterEntity.this.getTarget() != null && TricksterEntity.this.getHealth() < 15;
        }

        @Override
        protected int getCastingTime() {
            return 4 * 20;
        }

        @Override
        protected int getCastingInterval() {
            return TricksterEntity.this.getCooldownForSpell(15);
        }

        @Override
        protected void performSpellCasting() {
            LivingEntity target = TricksterEntity.this.getTarget();
            if (target == null) {
                return;
            }

            ServerWorld serverworld = (ServerWorld) TricksterEntity.this.level;
            double radius = 9.2;
            double targetX = target.getX();
            double targetY = target.getY(0.3333333333333333D);
            double targetZ = target.getZ();

            ItemStack firework = this.getRocket(this.getSpell());

            for (int i = 0; i < 6; i++) {
                double angle = Math.toRadians((360.0 / 6) * i);
                double fireworkX = targetX + radius * Math.cos(angle);
                double fireworkZ = targetZ + radius * Math.sin(angle);
                double fireworkY = targetY + 2;

                CustomArrowEntity fireworkEntity = new CustomArrowEntity(serverworld, TricksterEntity.this);
                fireworkEntity.setArrowType(CustomArrowType.FIREWORK);
                fireworkEntity.setFireworkStack(firework);
                if (!TricksterEntity.this.hasRealAttacks) {
                    fireworkEntity.setNoDamage();
                }
                double d0 = targetX - fireworkX;
                double d1 = targetY - fireworkY;
                double d2 = targetZ - fireworkZ;

                fireworkEntity.moveTo(fireworkX, fireworkY, fireworkZ);
                fireworkEntity.shoot(d0, d1, d2, 1.5F, 0);
                serverworld.addFreshEntityWithPassengers(fireworkEntity);
            }
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected SpellType getSpell() {
            return SpellType.FIREWORK_CIRCLE;
        }
    }

}