package net.minecraft.entity.monster.enderiophage;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.client.renderer.entity.model.Maths;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.DirectPathNavigator;
import net.minecraft.entity.ai.controller.FlightMoveController;
import net.minecraft.entity.ai.controller.GroundPathNavigatorWide;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.MosquitoDismount;
import net.minecraft.network.play.server.MosquitoMountMob;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class EntityEnderiophage extends Animal implements IMob, IFlyingAnimal, IShearable {

    private static final DataParameter<Float> PHAGE_PITCH = EntityDataManager.defineId(EntityEnderiophage.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> CAPSID_COLOUR = EntityDataManager.defineId(EntityEnderiophage.class, DataSerializers.INT);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.defineId(EntityEnderiophage.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> MISSING_EYE = EntityDataManager.defineId(EntityEnderiophage.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BROKEN_CAPSID = EntityDataManager.defineId(EntityEnderiophage.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> REPAIRED_CAPSID = EntityDataManager.defineId(EntityEnderiophage.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ALLOWED_TO_BE_REPAIRED = EntityDataManager.defineId(EntityEnderiophage.class, DataSerializers.BOOLEAN);

    private static final DataParameter<Float> PHAGE_SCALE = EntityDataManager.defineId(EntityEnderiophage.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> VARIANT = EntityDataManager.defineId(EntityEnderiophage.class, DataSerializers.INT);
    private static final Predicate<LivingEntity> ENDERGRADE_OR_INFECTED = (entity) -> {
        return entity.hasEffect(Effects.ENDER_FLU) || entity instanceof BlazeEntity;
    };
    public float prevPhagePitch;
    public float tentacleAngle;
    public float lastTentacleAngle;
    public float phageRotation;
    public float prevFlyProgress;
    public float flyProgress;
    public int breedingTimer;
    public int passengerIndex = 0;
    public int eyeStealCooldown = (random.nextInt(12000) + 12000);
    public float prevEnderiophageScale = 1F;
    private float rotationVelocity;
    private int slowDownTicks = 0;
    private float randomMotionSpeed;
    private boolean isLandNavigator;
    private int timeFlying = 0;
    private int fleeAfterStealTime = 0;
    private int attachTime = 0;
    private int dismountCooldown = 0;
    private int squishCooldown = 0;
    private Creature angryEnderman = null;

    public EntityEnderiophage(EntityType<? extends EntityEnderiophage> type, World world) {
        super(type, world);
        this.rotationVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
        switchNavigator(false);
        this.xpReward = 5;
        this.setCanPickUpLoot(true);
    }

    public void setAllowedToBeRepaired(boolean b) {
        entityData.set(ALLOWED_TO_BE_REPAIRED, b);
    }

    public boolean isAllowedToBeRepaired() {
        return this.entityData.get(ALLOWED_TO_BE_REPAIRED);
    }

    public boolean canHoldItem(ItemStack item) {
        return item.getItem() == Items.ENDER_EYE && isMissingEye() && !isCapsidBroken();
    }

    private void dropItemStack(ItemStack p_213486_1_) {
        ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), p_213486_1_);
        this.level.addFreshEntity(itementity);
    }

    protected void pickUpItem(ItemEntity item) {
        ItemStack itemstack = item.getItem();
        PlayerEntity playerentity = item.getThrower() != null ? this.level.getPlayerByUUID(item.getThrower()) : null;
        if (playerentity instanceof ServerPlayerEntity && item.isEdible()) {
            CriteriaTriggers.ENDERIOPHAGE_TAKE_EYE.trigger((ServerPlayerEntity) playerentity);
        }
        if (this.canHoldItem(itemstack) && item.isEdible()) {
            int i = itemstack.getCount();
            if (i > 1) {
                this.dropItemStack(itemstack.split(i - 1));
            }
            item.remove();
            if (age == 0) {
                setInLove(null);
            } else {
                this.setMissingEye(false);
                this.playSound(SoundEvents.ENDER_EYE_DEATH, this.getSoundVolume(), this.getVoicePitch());
                this.level.broadcastEntityEvent(this, (byte) 18);
            }
        }

    }

    public Vector3i getPickupReach() {
        return new Vector3i(0, 0, 0);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MOVEMENT_SPEED, 0.15F).add(Attributes.ATTACK_DAMAGE, 2F);
    }

    public static boolean canEnderiophageSpawn(EntityType<? extends Animal> animal, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        if (!(worldIn instanceof ServerWorld)) return false;
        return (pos.getX() >= 128 || pos.getX() <= -128 || pos.getZ() >= 128 || pos.getZ() <= -128) || ((ServerWorld) worldIn).dragonFight() != null && ((ServerWorld) worldIn).dragonFight().hasPreviouslyKilledDragon();
    }

    public boolean checkSpawnRules(IWorld worldIn, SpawnReason spawnReasonIn) {
        return EntityType.rollSpawn(2, this.getRandom(), spawnReasonIn);
    }

    private void doInitialPosing(IServerWorld world) {
        BlockPos down = this.getPhageGround(this.blockPosition());
        this.setPos(down.getX() + 0.5F, down.getY() + 1, down.getZ() + 0.5F);
    }

    @Nullable
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if (reason == SpawnReason.NATURAL) {
            doInitialPosing(worldIn);
        }
        setSkinForDimension();
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public int getMaxSpawnClusterSize() {
        return 2;
    }

    public float getPhageScale() {
        return this.entityData.get(PHAGE_SCALE);
    }

    public void setPhageScale(float scale) {
        this.entityData.set(PHAGE_SCALE, scale);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    public boolean isFood(ItemStack food) {
        return food.getItem() == Items.ENDER_EYE && this.isMissingEye();
    }

    @Nullable
    @Override
    public EntityEnderiophage getBreedOffspring(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return EntityType.ENDERIOPHAGE.create(serverWorld);
    }

    public void setInLove(@Nullable PlayerEntity player) {
        this.inLove = 3600;
        if (player != null) {
            this.loveCause = player.getUUID();
        }

        this.level.broadcastEntityEvent(this, (byte) 18);
        this.setMissingEye(false);
        this.playSound(SoundEvents.ENDER_EYE_DEATH, this.getSoundVolume(), this.getVoicePitch());
    }

    public void spawnChildFromBreeding(ServerWorld world, Animal enderiophage) {
        EntityEnderiophage phage = this.getBreedOffspring(world, enderiophage);
        if (phage != null) {
            ServerPlayerEntity serverplayerentity = this.getLoveCause();
            if (serverplayerentity == null && enderiophage.getLoveCause() != null) {
                serverplayerentity = enderiophage.getLoveCause();
            }

            if (serverplayerentity != null) {
                serverplayerentity.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this, enderiophage, phage);
            }

            boolean failedExperiment = random.nextFloat() <= 0.05F;

            for (PlayerEntity player : level.getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().inflate(16D, 16D, 16D),
                    player -> player.isAlive() && player.canSee(this) && player instanceof ServerPlayerEntity)) {
                CriteriaTriggers.WATCH_ENDERIOPHAGE_BREED_TRIGGER.trigger(((ServerPlayerEntity) player));

                if (failedExperiment) {
                    CriteriaTriggers.FAILED_EXPERIMENT.trigger((ServerPlayerEntity) player);
                }
            }

            this.setAge(6000);
            enderiophage.setAge(6000);
            this.resetLove();
            enderiophage.resetLove();
            phage.setAge(6000);
            phage.resetLove();
            phage.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            phage.prevEnderiophageScale = 0.2F;
            phage.setPhageScale(0.2F);
            phage.setCapsidBroken(failedExperiment);
            if (failedExperiment) {
                phage.setMissingEye(true);
            }
            phage.setVariant(this.getVariant());
            this.setStandardFleeTime();
            ((EntityEnderiophage) enderiophage).setStandardFleeTime();
            world.addFreshEntityWithPassengers(phage);
            world.broadcastEntityEvent(this, (byte) 18);
            if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                world.addFreshEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(14) + 6));
            }

        }
    }

    public boolean isInLove() {
        return super.isInLove() && !isMissingEye();
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 18) {
            for (int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level.addParticle(ParticleTypes.DNA, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
            }
        } else {
            super.handleEntityEvent(p_70103_1_);
        }

    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FindItemsGoal(this, 1.5F, Ingredient.of(Items.ENDER_EYE)) {
            public void move(List<ItemEntity> list) {
                if (list.isEmpty()) return;
                ItemEntity targetItem = list.get(0);
                if (!targetItem.isEdible()) stop();
                double distance = EntityEnderiophage.this.distanceTo(targetItem);
                if (distance <= 8.0) {
                    targetItem.setNoGravity(true);
                    double dx = EntityEnderiophage.this.getX() - targetItem.getX();
                    double dy = (EntityEnderiophage.this.getY() + EntityEnderiophage.this.getBbHeight() / 2) - (targetItem.getY() + targetItem.getBbHeight() / 2);
                    double dz = EntityEnderiophage.this.getZ() - targetItem.getZ();
                    double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (length > 0) {
                        dx /= length;
                        dy /= length;
                        dz /= length;
                        double pullStrength = 0.012;
                        targetItem.setDeltaMovement(
                                targetItem.getDeltaMovement().add(dx * pullStrength, dy * pullStrength, dz * pullStrength)
                        );
                        targetItem.hasImpulse = true;
                    }
                } else {
                    targetItem.setNoGravity(false);
                }

                if (EntityEnderiophage.this.isFlying()) {
                    EntityEnderiophage.this.getMoveControl().setWantedPosition(targetItem.blockPosition(), this.getMoveSpeed());
                } else {
                    EntityEnderiophage.this.getNavigation().moveTo(targetItem, getMoveSpeed());

                    if (EntityEnderiophage.this.getNavigation().getPath() != null && !EntityEnderiophage.this.getNavigation().getPath().canReach()) {
                        EntityEnderiophage.this.setFlying(true);
                    }
                }
            }


            @Override
            public double searchDistanceXZ() {
                return 48D;
            }

            @Override
            public double searchDistanceY() {
                return 48D;
            }

            public boolean canUse() {
                return EntityEnderiophage.this.isMissingEye() && EntityEnderiophage.this.getTarget() == null && !findNearbyItems().isEmpty() && findNearbyItems().get(0).isEdible() && !EntityEnderiophage.this.isCapsidBroken();
            }

            public boolean canContinueToUse() {
                return super.canContinueToUse() && EntityEnderiophage.this.isMissingEye() && !EntityEnderiophage.this.isCapsidBroken();
            }
        });
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new FlyTowardsTarget(this));
        this.goalSelector.addGoal(0, new EnderiophageBreedGoal(this, 1.3D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.3D, Ingredient.of(Items.ENDER_EYE), false) {

            @Override
            public boolean canUse() {
                return super.canUse() && EntityEnderiophage.this.isMissingEye();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && EntityEnderiophage.this.isMissingEye();
            }
        });
        this.goalSelector.addGoal(2, new AIWalkIdle());
        this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, EndermanEntity.class, 15, true, true, null) {
            public boolean canUse() {
                return EntityEnderiophage.this.isMissingEye() && super.canUse() && !EntityEnderiophage.this.isCapsidBroken();
            }

            public boolean canContinueToUse() {
                return EntityEnderiophage.this.isMissingEye() && super.canContinueToUse() && !EntityEnderiophage.this.isCapsidBroken();
            }
        });
        this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, LivingEntity.class, 15, true, true, ENDERGRADE_OR_INFECTED) {
            public boolean canUse() {
                return !EntityEnderiophage.this.isMissingEye() && EntityEnderiophage.this.fleeAfterStealTime == 0 && super.canUse();
            }

            public boolean canContinueToUse() {
                return !EntityEnderiophage.this.isMissingEye() && super.canContinueToUse();
            }
        });
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this, EndermanEntity.class));

    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MovementController(this);
            this.navigation = new GroundPathNavigatorWide(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveController(this, 1F, false, true);
            this.navigation = new DirectPathNavigator(this, level());
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(CAPSID_COLOUR, -1);
        this.entityData.define(PHAGE_PITCH, 0F);
        this.entityData.define(PHAGE_SCALE, 1F);
        this.entityData.define(FLYING, false);
        this.entityData.define(MISSING_EYE, false);
        this.entityData.define(BROKEN_CAPSID, false);
        this.entityData.define(REPAIRED_CAPSID, false);
        this.entityData.define(ALLOWED_TO_BE_REPAIRED, true);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    public boolean hasRepairedCapsid() {
        return entityData.get(REPAIRED_CAPSID);
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isInOverworld() {
        return this.level().dimension() == World.OVERWORLD && !this.isNoAi();
    }

    public boolean isInNether() {
        return this.level().dimension() == World.NETHER && !this.isNoAi();
    }

    public void setStandardFleeTime() {
        this.fleeAfterStealTime = 20;
    }


    public void die(DamageSource source) {
        Entity entity = this.getVehicle();
        if (entity != null && entity != source.getEntity() && entity instanceof LivingEntity && !((LivingEntity) entity).hasEffect(Effects.ENDER_FLU)) {
            if (this.getTarget() != source.getEntity() && source.getEntity() instanceof ServerPlayerEntity && source.getEntity() != null) {
                CriteriaTriggers.ENDERIOPHAGE_KILLED_WHILE_INFECTING.trigger(((ServerPlayerEntity) source.getEntity()));
            }
        }

        if (source.getEntity() instanceof ServerPlayerEntity && source.getEntity() != null && this.breedingTimer >= 35) {
            CriteriaTriggers.ENDERIOPHAGE_KILLED_WHILE_BREEDING.trigger(((ServerPlayerEntity) source.getEntity()));
        }

        super.die(source);
    }

    public void rideTick() {
        Entity entity = this.getVehicle();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else {
            this.setDeltaMovement(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                attachTime++;
                Entity mount = this.getVehicle();
                if (mount instanceof LivingEntity) {
                    passengerIndex = mount.getPassengers().indexOf(this);
                    this.yBodyRot = ((LivingEntity) mount).yBodyRot;
                    this.yRot = (((LivingEntity) mount).yRot);
                    this.yHeadRot = ((LivingEntity) mount).yHeadRot;
                    this.yRotO = ((LivingEntity) mount).yHeadRot;
                    float radius = mount.getBbWidth();
                    float angle = (Maths.STARTING_ANGLE * (((LivingEntity) mount).yBodyRot + passengerIndex * 90F));
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    double extraZ = radius * MathHelper.cos(angle);
                    this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getEyeHeight() * 0.25F, mount.getY()), mount.getZ() + extraZ);
                    if (!mount.isAlive() || mount instanceof PlayerEntity && ((PlayerEntity) mount).isCreative()) {
                        this.removeVehicle();
                    }
                    this.setPhagePitch(0F);
                    if (!this.level().isClientSide && attachTime > 15) {
                        LivingEntity target = (LivingEntity) mount;
                        float dmg = 1F;
                        if (target.getHealth() > target.getMaxHealth() * 0.2F) {
                            dmg = 6F;
                        }
                        if ((target.getHealth() < 1.5D || mount.hurt(DamageSource.mobAttack(this), dmg)) && mount instanceof LivingEntity) {
                            dismountCooldown = 100;
                            if (mount instanceof EndermanEntity) {
                                this.setMissingEye(false);
                                this.gameEvent(GameEvent.EAT);
                                this.playSound(SoundEvents.ENDER_EYE_DEATH, this.getSoundVolume(), this.getVoicePitch());
                                this.heal(5);
                                this.inLove = 2400;

                                if (random.nextFloat() <= 0.05F) {
                                    ItemEntity item = this.spawnAtLocation(new ItemStack(Items.BLAZE_POWDER, random.nextInt(3) + 1));

                                    if (item != null) {
                                        item.setEdible(false);
                                        item.fromEnderiophage = true;
                                    }
                                }

                                ((EndermanEntity) mount).addEffect(new EffectInstance(Effects.BLINDNESS, 400));
                                for (PlayerEntity player : level.getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().inflate(12D, 12D, 12D))) {
                                    if (player instanceof ServerPlayerEntity && player.isAlive() && player.canSee(this)) {
                                        CriteriaTriggers.ENDERIOPHAGE_STEAL_EYE.trigger((ServerPlayerEntity) player);
                                    }
                                }
                                this.fleeAfterStealTime = 400;
                                this.setFlying(true);
                                this.angryEnderman = (Creature) mount;
                            } else {
                                if (random.nextInt(3) == 0) {
                                    if (!this.isMissingEye()) {
                                        if (target.getEffect(Effects.ENDER_FLU.get()) == null) {
                                            target.addEffect(new EffectInstance(Effects.ENDER_FLU.get(), target instanceof BlazeEntity ? 6000 : 12000, target instanceof BlazeEntity ? 1 : 0));
                                        } else {
                                            EffectInstance inst = target.getEffect(Effects.ENDER_FLU.get());
                                            int duration = 12000;
                                            int level = 0;
                                            if (inst != null) {
                                                duration = inst.getDuration();
                                                level = inst.getAmplifier();
                                            }
                                            target.removeEffect(Effects.ENDER_FLU.get());
                                            target.addEffect(new EffectInstance(Effects.ENDER_FLU.get(), duration, Math.min(level + 1, 4)));
                                        }
                                        this.heal(5);
                                        this.gameEvent(GameEvent.ENTITY_ROAR);
                                        this.playSound(SoundEvents.ITEM_BREAK, this.getSoundVolume(), this.getVoicePitch());
                                        this.setMissingEye(true);

                                        if (target instanceof BlazeEntity) {
                                            this.fleeAfterStealTime = 400;
                                        }

                                        if (!(mount instanceof PlayerEntity) && !(mount instanceof IronGolemEntity)) {
                                            for (PlayerEntity player : level.getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().inflate(8D, 12D, 8D),
                                                    player -> player.isAlive() && player.canSee(this) && player instanceof ServerPlayerEntity)) {
                                                CriteriaTriggers.ENDERIOPHAGE_INFECT_MOBS.trigger(((ServerPlayerEntity) player));

                                                if (target instanceof BlazeEntity) {
                                                    CriteriaTriggers.ENDERIOPHAGE_ATTACK_BLAZE.trigger(((ServerPlayerEntity) player));
                                                }
                                            }
                                        }
                                    }
                                    if (!this.level().isClientSide) {
                                        this.setTarget(null);
                                        this.setLastHurtMob(null);
                                        this.setLastHurtByMob(null);
                                        this.goalSelector.getRunningGoals().forEach(Goal::stop);
                                        this.targetSelector.getRunningGoals().forEach(Goal::stop);
                                    }
                                }
                            }
                        }
                        if (((LivingEntity) mount).getHealth() <= 0 || this.fleeAfterStealTime > 0 || this.isMissingEye() && !(mount instanceof EndermanEntity) || !this.isMissingEye() && mount instanceof EndermanEntity) {
                            this.removeVehicle();
                            this.setTarget(null);
                            dismountCooldown = 100;
                            if (level instanceof ServerWorld) {
                                ServerWorld serverWorld = (ServerWorld) level;
                                for (ServerPlayerEntity serverPlayerEntity : serverWorld.players()) {
                                    serverPlayerEntity.connection.send(new MosquitoDismount(this.getId(), mount.getId()));
                                }
                            }
                            this.setFlying(true);
                        }
                    }
                }

            }
        }

    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public void onSpawnFromEffect() {
        prevEnderiophageScale = 0.2F;
        this.setPhageScale(0.2F);
    }

    public void setSkinForDimension() {
        if (isInNether()) {
            this.setVariant(2);
        } else if (isInOverworld()) {
            this.setVariant(1);
        } else {
            this.setVariant(0);
        }
    }

    public int getCapsidColour() {
        if (this.getVariant() == 1 || this.hasRepairedCapsid()) return this.entityData.get(CAPSID_COLOUR);

        return -1;
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if ((this.getVariant() == 1 || this.entityData.get(REPAIRED_CAPSID)) && player instanceof ServerPlayerEntity) {
            if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof StainedGlassBlock) {
                DyeColor color = ((StainedGlassBlock) ((BlockItem) stack.getItem()).getBlock()).getColor();

                if (this.getCapsidColour() == color.getId()) return super.mobInteract(player, hand);

                this.setCapsidColour(color.getId());
                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                CriteriaTriggers.CHANGE_CAPSID_COLOUR.trigger((ServerPlayerEntity) player);

                return ActionResultType.SUCCESS;
            }
        }

        if (player instanceof ServerPlayerEntity && this.isAllowedToBeRepaired()) {
            if (this.isCapsidBroken() && stack.getItem() instanceof BlockItem && ((BlockItem)stack.getItem()).getBlock() instanceof AbstractGlassBlock) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                if (block instanceof StainedGlassBlock) {
                    DyeColor color = ((StainedGlassBlock) ((BlockItem) stack.getItem()).getBlock()).getColor();
                    this.setCapsidBroken(false);
                    this.entityData.set(REPAIRED_CAPSID, true);
                    this.setCapsidColour(color.getId());
                    CriteriaTriggers.CHANGE_CAPSID_COLOUR.trigger((ServerPlayerEntity) player);
                    CriteriaTriggers.RESTORATION.trigger(((ServerPlayerEntity) player));
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    return ActionResultType.SUCCESS;

                } else if (block instanceof GlassBlock) {
                    this.setCapsidBroken(false);
                    this.entityData.set(REPAIRED_CAPSID, true);
                    CriteriaTriggers.RESTORATION.trigger(((ServerPlayerEntity) player));
                    this.setCapsidColour(-1);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    return ActionResultType.SUCCESS;
                }
            }
        }

        if (stack.getItem() == Items.SHEARS && player instanceof ServerPlayerEntity && this.eyeStealCooldown == 0 && this.getTarget() != player) {
            if (!this.level.isClientSide && this.readyForShearing()) {
                this.shear(player);
                this.gameEvent(GameEvent.SHEAR, player);
                stack.hurtAndBreak(1, player, (p_213613_1_) -> {
                    p_213613_1_.broadcastBreakEvent(hand);
                });
                return ActionResultType.SUCCESS;
            } else {
                return ActionResultType.CONSUME;
            }
        }
        return super.mobInteract(player, hand);
    }


    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENDERIOPHAGE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDERIOPHAGE_HURT;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENDERIOPHAGE_WALK, 0.4F, 1.0F);
    }

    protected float nextStep() {
        return this.moveDist + 0.3F;
    }

    public void tick() {
        super.tick();
        prevEnderiophageScale = this.getPhageScale();

        if (isCapsidBroken() && tick(20)) {
            this.hurt(DamageSource.DRY_OUT, 1F);
            this.fleeAfterStealTime = 25;
        }

        float extraMotionSlow = 1.0F;
        float extraMotionSlowY = 1.0F;
        if (slowDownTicks > 0) {
            slowDownTicks--;
            extraMotionSlow = 0.33F;
            extraMotionSlowY = 0.1F;
        }
        if (dismountCooldown > 0) {
            dismountCooldown--;
        }
        if (squishCooldown > 0) {
            squishCooldown--;
        }
        if (eyeStealCooldown > 0) {
            eyeStealCooldown--;
        }
        if (!this.level().isClientSide) {

            for (PlayerEntity player : level.getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().inflate(12D, 12D, 12D))) {
                if (player instanceof ServerPlayerEntity && player.isAlive() && player.canSee(this)) {
                    CriteriaTriggers.ENCOUNTER_ENDERIOPHAGE.trigger((ServerPlayerEntity) player);
                }
            }

            if (!this.isPassenger() && attachTime != 0) {
                attachTime = 0;
            }
            if (fleeAfterStealTime > 0) {
                if (angryEnderman != null) {
                    Vector3d vec = this.getBlockInViewAway(angryEnderman.position(), 10);
                    if (fleeAfterStealTime < 5) {
                        if (angryEnderman instanceof IAngerable) {
                            ((IAngerable) angryEnderman).stopBeingAngry();
                        }
                        try {
                            angryEnderman.getGoalSelector().getRunningGoals().forEach(Goal::stop);
                            angryEnderman.targetSelector.getRunningGoals().forEach(Goal::stop);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        angryEnderman = null;
                    }
                    if (vec != null) {
                        this.setFlying(true);
                        this.getMoveControl().setWantedPosition(vec.x, vec.y, vec.z, 1.3F);
                    }
                }
                fleeAfterStealTime--;
            }
        }
        this.yBodyRot = this.yRot;
        this.yHeadRot = this.yRot;
        this.setPhagePitch(-90F);
        if (this.isAlive() && (this.isFlying() && randomMotionSpeed > 0.75F && this.getDeltaMovement().lengthSqr() > 0.02D || isCapsidBroken())) {
            if (this.level().isClientSide) {
                float pitch = -this.getPhagePitch() / 90F;
                float radius = this.getBbWidth() * 0.2F * -pitch;
                float angle = (Maths.STARTING_ANGLE * this.yRot);
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraY = 0.2F - (1 - pitch) * 0.15F;
                double extraZ = radius * MathHelper.cos(angle);
                double motX = extraX * 8 + random.nextGaussian() * 0.05F;
                double motY = -0.1F;
                double motZ = extraZ + random.nextGaussian() * 0.05F;
                this.level().addParticle(ParticleTypes.DNA, this.getX() + extraX, this.getY() + extraY, this.getZ() + extraZ, motX, motY, motZ);
            }
        }
        prevPhagePitch = this.getPhagePitch();
        prevFlyProgress = flyProgress;

        if (isFlying()) {
            if (flyProgress < 5F) {
                flyProgress++;
            }
        } else {
            if (flyProgress > 0F) {
                flyProgress--;
            }
        }

        this.lastTentacleAngle = this.tentacleAngle;
        this.phageRotation += this.rotationVelocity;
        if ((double) this.phageRotation > (Math.PI * 2D)) {
            if (this.level().isClientSide) {
                this.phageRotation = 6.28318530718F;
            } else {
                this.phageRotation = (float) ((double) this.phageRotation - (Math.PI * 2D));
                if (this.random.nextInt(10) == 0) {
                    this.rotationVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
                }
                this.level().broadcastEntityEvent(this, (byte) 19);
            }
        }
        if (this.phageRotation < Math.PI) {
            float f = (float) (this.phageRotation / Math.PI);
            this.tentacleAngle = MathHelper.sin((float) (f * f * Math.PI)) * 4.275F;
            if ((double) f > 0.75D) {
                if (squishCooldown == 0 && this.isFlying()) {
                    squishCooldown = 20;
                    this.playSound(SoundEvents.ENDERIOPHAGE_SQUISH, 3F, this.getVoicePitch());
                }
                this.randomMotionSpeed = 1.0F;
            } else {
                randomMotionSpeed = 0.01F;
            }
        }
        if (!this.level().isClientSide) {
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
            if (this.isFlying()) {
                this.setDeltaMovement(this.getDeltaMovement().x * this.randomMotionSpeed * extraMotionSlow, this.getDeltaMovement().y * this.randomMotionSpeed * extraMotionSlowY, this.getDeltaMovement().z * this.randomMotionSpeed * extraMotionSlow);
                timeFlying++;
                if (this.onGround && timeFlying > 100) {
                    this.setFlying(false);
                }
            } else {
                timeFlying = 0;
            }
            if (this.isMissingEye() && this.getTarget() != null) {
                if (!(this.getTarget() instanceof EndermanEntity)) {
                    this.setTarget(null);
                }
            }
        }
        if (!this.onGround && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }
        if (this.isFlying()) {
            float phageDist = -(float) ((Math.abs(this.getDeltaMovement().x()) + Math.abs(this.getDeltaMovement().z())) * 6F);
            this.incrementPhagePitch(phageDist * 1);
            this.setPhagePitch(MathHelper.clamp(this.getPhagePitch(), -90, 10));
            float plateau = 2;
            if (this.getPhagePitch() > plateau) {
                this.decrementPhagePitch(phageDist * Math.abs(this.getPhagePitch()) / 90);
            }
            if (this.getPhagePitch() < -plateau) {
                this.incrementPhagePitch(phageDist * Math.abs(this.getPhagePitch()) / 90);
            }
            if (this.getPhagePitch() > 2F) {
                this.decrementPhagePitch(1);
            } else if (this.getPhagePitch() < -2) {
                this.incrementPhagePitch(1);
            }
            if (this.horizontalCollision) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.2F, 0));
            }
        } else {
            if (this.getPhagePitch() > 0F) {
                float decrease = Math.min(2, this.getPhagePitch());
                this.decrementPhagePitch(decrease);
            }
            if (this.getPhagePitch() < 0F) {
                float decrease = Math.min(2, -this.getPhagePitch());
                this.incrementPhagePitch(decrease);
            }
        }
        if (this.getPhageScale() < 1F) {
            this.setPhageScale(this.getPhageScale() + 0.05F);
        }
    }

    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Flying", this.isFlying());
        compound.putBoolean("MissingEye", this.isMissingEye());
        compound.putInt("Variant", this.getVariant());
        compound.putInt("SlowDownTicks", slowDownTicks);
        compound.putInt("CapsidColour", this.getCapsidColour());
        compound.putInt("EyeStealCooldown", this.eyeStealCooldown);
        compound.putBoolean("BrokenCapsid", this.isCapsidBroken());
        compound.putBoolean("RepairedCapsid", this.hasRepairedCapsid());
        compound.putBoolean("AllowedToBeRepaired", this.isAllowedToBeRepaired());
    }

    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.setMissingEye(compound.getBoolean("MissingEye"));
        this.setVariant(compound.getInt("Variant"));
        this.slowDownTicks = compound.getInt("SlowDownTicks");
        this.entityData.set(CAPSID_COLOUR, compound.getInt("CapsidColour"));
        this.eyeStealCooldown = compound.getInt("EyeStealCooldown");
        this.entityData.set(BROKEN_CAPSID, compound.getBoolean("BrokenCapsid"));
        this.entityData.set(REPAIRED_CAPSID, compound.getBoolean("RepairedCapsid"));
        this.setAllowedToBeRepaired(compound.getBoolean("AllowedToBeRepaired"));
    }

    public boolean isMissingEye() {
        if (this.isCapsidBroken()) return true;

        return this.entityData.get(MISSING_EYE);
    }

    public void setMissingEye(boolean missingEye) {
        this.entityData.set(MISSING_EYE, missingEye);
    }

    public void setCapsidBroken(boolean broken) {
        if (broken) {
            this.entityData.set(REPAIRED_CAPSID, false);
        }

        this.entityData.set(BROKEN_CAPSID, broken);
    }

    public boolean isCapsidBroken() {
        return this.entityData.get(BROKEN_CAPSID);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public float getPhagePitch() {
        return entityData.get(PHAGE_PITCH);
    }

    public void setCapsidColour(int colour) {
        this.entityData.set(CAPSID_COLOUR, colour);
    }

    public void setPhagePitch(float pitch) {
        entityData.set(PHAGE_PITCH, pitch);
    }

    public void incrementPhagePitch(float pitch) {
        entityData.set(PHAGE_PITCH, getPhagePitch() + pitch);
    }

    public void decrementPhagePitch(float pitch) {
        entityData.set(PHAGE_PITCH, getPhagePitch() - pitch);
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 1.8F;
    }


    private boolean isOverWaterOrVoid() {
        BlockPos position = this.blockPosition();
        while (position.getY() > -63 && !level().getBlockState(position).isSolidRender(level, position)) {
            position = position.below();
        }
        return !level().getFluidState(position).isEmpty() || position.getY() < -63;
    }

    public Vector3d getBlockInViewAway(Vector3d fleePos, float radiusAdd) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24) - radiusAdd;
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (Maths.STARTING_ANGLE * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos((int) (fleePos.x() + extraX), 0, (int) (fleePos.z() + extraZ));
        BlockPos ground = getPhageGround(radialPos);
        int distFromGround = (int) this.getY() - ground.getY();
        int flightHeight = 6 + this.getRandom().nextInt(10);
        BlockPos newPos = ground.above(distFromGround > 8 || fleeAfterStealTime > 0 ? flightHeight : this.getRandom().nextInt(6) + 5);
        if (!this.isTargetBlocked(Vector3d.atCenterOf(newPos)) && this.distanceToSqr(Vector3d.atCenterOf(newPos)) > 1) {
            return Vector3d.atCenterOf(newPos);
        }
        return null;
    }

    private BlockPos getPhageGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), (int) this.getY(), in.getZ());
        while (position.getY() > -63 && !level().getBlockState(position).isSolidRender(level, position)) {
            position = position.below();
        }
        if (position.getY() < -62) {
            return position.above(120 + random.nextInt(5));
        }

        return position;
    }

    public Vector3d getBlockGrounding(Vector3d fleePos) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24);
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (Maths.STARTING_ANGLE * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, getY(), fleePos.z() + extraZ);
        BlockPos ground = this.getPhageGround(radialPos);
        if (ground.getY() <= -63) {
            return Vector3d.upFromBottomCenterOf(ground, 110 + random.nextInt(20));
        } else {
            ground = this.blockPosition();
            while (ground.getY() > -63 && !level().getBlockState(ground).isSolidRender(level, ground)) {
                ground = ground.below();
            }
        }
        if (!this.isTargetBlocked(Vector3d.atCenterOf(ground.above()))) {
            return Vector3d.atCenterOf(ground);
        }
        return null;
    }

    public boolean isTargetBlocked(Vector3d target) {
        Vector3d Vector3d = new Vector3d(this.getX(), this.getEyeY(), this.getZ());
        return this.level().clip(new RayTraceContext(Vector3d, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() != RayTraceResult.Type.MISS;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            if (entity instanceof EndermanEntity) {
                amount = (amount + 1.0F) * 0.35F;
                angryEnderman = (EndermanEntity) entity;
            }
            return super.hurt(source, amount);
        }
    }

    @Override
    public void shear(SoundCategory p_230263_1_) {
        this.eyeStealCooldown = random.nextInt(12000) + 12000;
        this.setMissingEye(true);
        this.level.playSound((PlayerEntity) null, this, SoundEvents.SHEEP_SHEAR, p_230263_1_, 1.0F, 1.0F);
        this.playSound(this.getHurtSound(null), this.getSoundVolume(), this.getVoicePitch());
    }

    public void shear(PlayerEntity player) {
        this.shear(SoundCategory.PLAYERS);

        if (!player.isCreative()) {
            for (EntityEnderiophage enderiophage : this.level.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(32, 32, 32))) {
                if (enderiophage != this && !enderiophage.isMissingEye() && enderiophage.getTarget() == null) {
                    enderiophage.setTarget(player);
                }
            }
        }

        CriteriaTriggers.DISSECT_ENDERIOPHAGE.trigger((ServerPlayerEntity) player);
        if (random.nextFloat() <= (!this.hasRepairedCapsid() ? 0.05F : 0.99F)) {
            CriteriaTriggers.BROKEN_CAPSID.trigger((ServerPlayerEntity) player);
            if (hasRepairedCapsid()) {
                CriteriaTriggers.DESTROY_CAPSID.trigger((ServerPlayerEntity) player);
                setAllowedToBeRepaired(false);
            }
            this.setCapsidBroken(true);
            this.playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.0F);
        }

        ItemEntity itementity = this.spawnAtLocation(Items.ENDER_EYE, 1);
        if (itementity != null) {
            itementity.setEdible(false);
            itementity.setDeltaMovement(itementity.getDeltaMovement().add((double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double) (this.random.nextFloat() * 0.05F), (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)));
        }

    }

    @Override
    public boolean readyForShearing() {
        return !isMissingEye() && this.eyeStealCooldown == 0 && this.getTarget() == null;
    }


    private class AIWalkIdle extends Goal {
        protected final EntityEnderiophage phage;
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget = false;

        public AIWalkIdle() {
            super();
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.phage = EntityEnderiophage.this;
        }

        @Override
        public boolean canUse() {
            if (this.phage.isVehicle() || (phage.getTarget() != null && phage.getTarget().isAlive()) || this.phage.isPassenger()) {
                return false;
            } else {
                if (this.phage.getRandom().nextInt(30) != 0 && !phage.isFlying() && phage.fleeAfterStealTime == 0) {
                    return false;
                }
                if (this.phage.onGround) {
                    this.flightTarget = random.nextInt(12) == 0;
                } else {
                    this.flightTarget = random.nextInt(5) > 0 && phage.timeFlying < 100;
                }
                if (phage.fleeAfterStealTime > 0) {
                    this.flightTarget = true;
                }
                Vector3d lvt_1_1_ = this.getPosition();
                if (lvt_1_1_ == null) {
                    return false;
                } else {
                    this.x = lvt_1_1_.x;
                    this.y = lvt_1_1_.y;
                    this.z = lvt_1_1_.z;
                    return true;
                }
            }
        }

        public void tick() {
            if (flightTarget) {
                phage.getMoveControl().setWantedPosition(x, y, z, fleeAfterStealTime == 0 ? 1.3F : 1F);
            } else {
                this.phage.getNavigation().moveTo(this.x, this.y, this.z, fleeAfterStealTime == 0 ? 1.3F : 1F);
            }
            if (!flightTarget && isFlying() && phage.onGround) {
                phage.setFlying(false);
            }
            if (isFlying() && phage.onGround && phage.timeFlying > 100 && phage.fleeAfterStealTime == 0) {
                phage.setFlying(false);
            }
        }

        @Nullable
        protected Vector3d getPosition() {
            Vector3d vector3d = phage.position();
            if (phage.isOverWaterOrVoid()) {
                flightTarget = true;
            }
            if (flightTarget) {
                if (phage.timeFlying < 50 || fleeAfterStealTime > 0 || phage.isOverWaterOrVoid()) {
                    return phage.getBlockInViewAway(vector3d, 0);
                } else {
                    return phage.getBlockGrounding(vector3d);
                }
            } else {
                return RandomPositionGenerator.getLandPos(this.phage, 10, 7);
            }
        }

        public boolean canContinueToUse() {
            if (flightTarget) {
                return phage.isFlying() && phage.distanceToSqr(x, y, z) > 2F;
            } else {
                return (!this.phage.getNavigation().isDone()) && !this.phage.isVehicle();
            }
        }

        public void start() {
            if (flightTarget) {
                phage.setFlying(true);
                phage.getMoveControl().setWantedPosition(x, y, z, fleeAfterStealTime == 0 ? 1.3F : 1F);
            } else {
                this.phage.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
        }

        public void stop() {
            this.phage.getNavigation().stop();
            super.stop();
        }
    }

    public static class FlyTowardsTarget extends Goal {
        private final EntityEnderiophage parentEntity;

        public FlyTowardsTarget(EntityEnderiophage phage) {
            this.parentEntity = phage;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {

            return !parentEntity.isPassenger() && parentEntity.getTarget() != null && !isBittenByPhage(parentEntity.getTarget()) && parentEntity.fleeAfterStealTime == 0;
        }

        public boolean canContinueToUse() {
            return parentEntity.getTarget() != null && !isBittenByPhage(parentEntity.getTarget()) && !parentEntity.horizontalCollision && !parentEntity.isPassenger() && parentEntity.isFlying() && parentEntity.getMoveControl().hasWanted() && parentEntity.fleeAfterStealTime == 0 && (parentEntity.getTarget() instanceof EndermanEntity || !parentEntity.isMissingEye());
        }

        public boolean isBittenByPhage(Entity entity) {
            int phageCount = 0;
            for (Entity e : entity.getPassengers()) {
                if (e instanceof EntityEnderiophage) {
                    phageCount++;
                }
            }
            return phageCount > 3;
        }

        public void stop() {
        }

        public void tick() {
            if (parentEntity.getTarget() != null) {
                float width = parentEntity.getTarget().getBbWidth() + parentEntity.getBbWidth() + 2;
                boolean isWithinReach = parentEntity.distanceToSqr(parentEntity.getTarget()) < width * width;
                if (parentEntity.isFlying() || isWithinReach) {
                    this.parentEntity.getMoveControl().setWantedPosition(parentEntity.getTarget().getX(), parentEntity.getTarget().getY(), parentEntity.getTarget().getZ(), isWithinReach ? 1.6D : 1.0D);
                } else {
                    this.parentEntity.getNavigation().moveTo(parentEntity.getTarget().getX(), parentEntity.getTarget().getY(), parentEntity.getTarget().getZ(), 1.2D);
                }
                if (parentEntity.getTarget().getY() > this.parentEntity.getY() + 1.2F) {
                    parentEntity.setFlying(true);
                }
                if (parentEntity.dismountCooldown == 0 && parentEntity.getBoundingBox().inflate(0.3, 0.3, 0.3).intersects(parentEntity.getTarget().getBoundingBox()) && !isBittenByPhage(parentEntity.getTarget())) {
                    parentEntity.startRiding(parentEntity.getTarget(), true);
                    if (!parentEntity.level().isClientSide) {
                        ServerWorld serverWorld = (ServerWorld) parentEntity.level;
                        for (ServerPlayerEntity serverPlayerEntity : serverWorld.players()) {
                            serverPlayerEntity.connection.send(new MosquitoMountMob(parentEntity.getId(), parentEntity.getTarget().getId()));
                        }
                    }
                }
            }
        }
    }

}