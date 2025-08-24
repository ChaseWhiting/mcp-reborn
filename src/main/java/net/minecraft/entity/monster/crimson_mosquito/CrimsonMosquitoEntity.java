package net.minecraft.entity.monster.crimson_mosquito;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityBoundSoundInstance;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.EntityMosquitoSpit;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.fluid.Fluid;
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
import net.minecraft.pathfinding.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import org.nd4j.common.util.MathUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class CrimsonMosquitoEntity extends Monster implements EntityBoundSounds {

    protected static final EntitySize FLIGHT_SIZE = EntitySize.fixed(1.2F, 1.0F);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.defineId(CrimsonMosquitoEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SHOOTING = EntityDataManager.defineId(CrimsonMosquitoEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BLOOD_LEVEL = EntityDataManager.defineId(CrimsonMosquitoEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> AGE = EntityDataManager.defineId(CrimsonMosquitoEntity.class, DataSerializers.INT);

    private static final DataParameter<Integer> BLOOD_TYPE = EntityDataManager.defineId(CrimsonMosquitoEntity.class, DataSerializers.INT);

    private static final DataParameter<Boolean> SHRINKING = EntityDataManager.defineId(CrimsonMosquitoEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FROM_FLY = EntityDataManager.defineId(CrimsonMosquitoEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> MOSQUITO_SCALE = EntityDataManager.defineId(CrimsonMosquitoEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> SICK = EntityDataManager.defineId(CrimsonMosquitoEntity.class, DataSerializers.BOOLEAN);
    private static final Predicate<Animal> WARM_BLOODED = (mob) -> {
        return !(mob instanceof StriderEntity);
    };
    public float prevFlyProgress;
    public float flyProgress;
    public float prevShootProgress;
    public float shootProgress;
    public int shootingTicks;
    public int randomWingFlapTick = 0;
    private int flightTicks = 0;
    private int sickTicks = 0;
    private boolean prevFlying = false;
    private int spitCooldown = 0;
    private int loopSoundTick = 0;
    private int drinkTime = 0;
    public float prevMosquitoScale = 1F;
    private final BuzzSoundTracker buzzSoundTracker;
    private boolean usedToPlayers;
    private int timeNearPlayers;

    private boolean tempted;

    private int inLove;
    private UUID loveCause;
    private int breedCooldown;

    public CrimsonMosquitoEntity(EntityType<CrimsonMosquitoEntity> type, World world) {
        super(type, world);
        this.moveControl = new MoveHelperController(this);
        this.setPathfindingMalus(PathNodeType.WATER, -1.0F);
        this.setPathfindingMalus(PathNodeType.LAVA, 0F);
        this.setPathfindingMalus(PathNodeType.DANGER_FIRE, 0F);
        this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, 0F);
        this.buzzSoundTracker = new BuzzSoundTracker(this);
    }


    public static boolean checkMosquitoSpawnRules(EntityType<CrimsonMosquitoEntity> type, IServerWorld world, SpawnReason reason, BlockPos position, Random random) {
        BlockPos pos = position.below();
        boolean spawnBlock = world.getBlockState(pos).isSolidRender(world, pos);

        return reason == SpawnReason.SPAWNER || spawnBlock && world.getBlockState(pos).isValidSpawn(world, pos, type) && isDarkEnoughToSpawn(world, position, random)
                && (world.getBlockState(pos).is(List.of(Blocks.CRIMSON_NYLIUM, Blocks.NETHERRACK, Blocks.NETHER_WART_BLOCK, Blocks.CRIMSON_STEM, Blocks.CRIMSON_HYPHAE)));
    }

    protected PathNavigator createNavigation(World p_175447_1_) {
        return new LavaPathNavigator(this, p_175447_1_);
    }

    static class LavaPathNavigator extends GroundPathNavigator {
        LavaPathNavigator(CrimsonMosquitoEntity p_i231565_1_, World p_i231565_2_) {
            super(p_i231565_1_, p_i231565_2_);
        }

        protected PathFinder createPathFinder(int p_179679_1_) {
            this.nodeEvaluator = new WalkNodeProcessor();
            return new PathFinder(this.nodeEvaluator, p_179679_1_);
        }

        protected boolean hasValidPathType(PathNodeType p_230287_1_) {
            return p_230287_1_ != PathNodeType.LAVA && p_230287_1_ != PathNodeType.DAMAGE_FIRE && p_230287_1_ != PathNodeType.DANGER_FIRE ? super.hasValidPathType(p_230287_1_) : true;
        }

        public boolean isStableDestination(BlockPos p_188555_1_) {
            return this.level.getBlockState(p_188555_1_).is(Blocks.LAVA) || super.isStableDestination(p_188555_1_);
        }
    }


    public static enum BloodType {
        RED(0),
        BLUE(1),
        RESIN(2),
        ENDER_BLOOD(3),
        LAVA(4);

        final int id;

        BloodType(int id) {
            this.id = id;
        }

        public int id() {return id;}
    }

    public int getBloodType() {
        return MathUtils.clamp(this.entityData.get(BLOOD_TYPE), 0, 4);
    }


    protected void customServerAiStep() {
        if (this.getAge() < 0) {
            this.inLove = 0;
        }



        super.customServerAiStep();
    }

    public void aiStep() {
        super.aiStep();
        if (this.getAge() < 0) {
            this.inLove = 0;
        }
        if (this.breedCooldown > 0) {
            breedCooldown--;
        }

        if (this.inLove > 0) {
            --this.inLove;
            if (this.inLove % 10 == 0) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
            }
        }

        if (this.level.isClientSide && this.getBloodType() == 3) {
            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }

    }



    public boolean canFallInLove() {
        return this.inLove <= 0 && this.breedCooldown <= 0;
    }

    public void setInLove(@Nullable PlayerEntity p_146082_1_) {
        this.inLove = 600;
        if (p_146082_1_ != null) {
            this.loveCause = p_146082_1_.getUUID();
        }

        this.level.broadcastEntityEvent(this, (byte)18);
    }

    public void spawnChildFromBreeding(ServerWorld world, CrimsonMosquitoEntity mosquito) {
        boolean spawnedAnyChild = false;
        for (int i = 0; i < random.nextInt(2) + 1; i++) {
            CrimsonMosquitoEntity baby = EntityType.CRIMSON_MOSQUITO.create(world);
            if (baby == null) continue;
            baby.setAge(-18000);
            baby.setPersistenceRequired();
            baby.moveTo(this.getRandomX(0.7), this.getY(), this.getRandomZ(0.7));
            world.addFreshEntityWithPassengers(baby);
            spawnedAnyChild = true;
        }

        if (spawnedAnyChild) {
            ServerPlayerEntity serverplayerentity = this.getLoveCause();
            if (serverplayerentity == null && mosquito.getLoveCause() != null) {
                serverplayerentity = mosquito.getLoveCause();
            }

            if (serverplayerentity != null) {
                serverplayerentity.awardStat(Stats.ANIMALS_BRED);
            }

            this.breedCooldown = 6000;
            mosquito.breedCooldown = 6000;
            this.resetLove();
            mosquito.resetLove();

            if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                world.addFreshEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
            }
        }
    }

    @Nullable
    public ServerPlayerEntity getLoveCause() {
        if (this.loveCause == null) {
            return null;
        } else {
            PlayerEntity playerentity = this.level.getPlayerByUUID(this.loveCause);
            return playerentity instanceof ServerPlayerEntity ? (ServerPlayerEntity)playerentity : null;
        }
    }

    public boolean isInLove() {
        return this.inLove > 0 && this.breedCooldown <= 0;
    }

    public void resetLove() {
        this.inLove = 0;
    }

    public boolean canMate(CrimsonMosquitoEntity p_70878_1_) {
        if (p_70878_1_ == this) {
            return false;
        } else if (p_70878_1_.getClass() != this.getClass()) {
            return false;
        } else {
            return this.isInLove() && p_70878_1_.isInLove();
        }
    }

    public static AttributeModifierMap.MutableAttribute createCrimsonMosquitoAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public boolean canRiderInteract() {
        return true;
    }

    public void setTempted(boolean t) {
        this.tempted = t;
    }

    public boolean isTempted() {
        return tempted;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MosquitoBreedGoal(this, 1.1));
        this.goalSelector.addGoal(1, new FollowMosquitoGoal(this, 0.7F));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1F, Ingredient.of(Items.SPIDER_EYE, Items.ROTTEN_FLESH), false) {
            private final EntityPredicate TEMP_TARGETING = (new EntityPredicate()).range(24.0D).allowInvulnerable().allowSameTeam().allowNonAttackable().allowUnseeable();

            public boolean canUse() {
                if (this.calmDown > 0) {
                    --this.calmDown;
                    return false;
                } else {
                    this.player = this.mob.level.getNearestPlayer(TEMP_TARGETING, this.mob);
                    if (this.player == null) {
                        return false;
                    } else {
                        return this.shouldFollowItem(this.player.getMainHandItem()) || this.shouldFollowItem(this.player.getOffhandItem());
                    }
                }
            }


            public void tick() {
                this.mob.getLookControl().setLookAt(this.player, 30F, 30F);
                if (this.mob.distanceToSqr(this.player) < 6.25D) {
                    this.mob.getNavigation().stop();
                } else {
                    this.mob.getMoveControl().setWantedPosition(this.player.blockPosition(), 1F);
                }

            }

            public void start() {
                super.start();
                if (!((CrimsonMosquitoEntity)mob).isTempted()) {
                    ((CrimsonMosquitoEntity)mob).setTempted(true);
                }
            }

            public void stop() {
                super.stop();
                this.calmDown = 400;
                if (((CrimsonMosquitoEntity)mob).isTempted()) {
                    ((CrimsonMosquitoEntity)mob).setTempted(false);
                }
            }
        });
        this.goalSelector.addGoal(2, new CrimsonMosquitoEntity.FlyTowardsTarget(this));
        this.goalSelector.addGoal(2, new FlyAwayFromTarget(this));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1F, Ingredient.of(Items.CHICKEN, Items.RABBIT), false) {
            private final EntityPredicate TEMP_TARGETING = (new EntityPredicate()).range(16.0D).allowInvulnerable().allowSameTeam().allowNonAttackable().allowUnseeable();

            public boolean canUse() {
                if (this.calmDown > 0) {
                    --this.calmDown;
                    return false;
                } else {
                    this.player = this.mob.level.getNearestPlayer(TEMP_TARGETING, this.mob);
                    if (this.player == null) {
                        return false;
                    } else {
                        return this.shouldFollowItem(this.player.getMainHandItem()) || this.shouldFollowItem(this.player.getOffhandItem());
                    }
                }
            }


            public void tick() {
                this.mob.getLookControl().setLookAt(this.player, 30F, 30F);
                if (this.mob.distanceToSqr(this.player) < 6.25D) {
                    this.mob.getNavigation().stop();
                } else {
                    this.mob.getMoveControl().setWantedPosition(this.player.blockPosition(), 1F);
                }

            }

            public void stop() {
                super.stop();
                this.calmDown = 400;
            }
        });
        this.goalSelector.addGoal(3, new CrimsonMosquitoEntity.RandomFlyGoal(this));
        this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 32f));
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, PlayerEntity.class, 10, true, false, entity -> {
            if (this.isTempted()) return false;

            if (this.usedToPlayers) return false;

            return !this.isBaby();
        }));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, LivingEntity.class, 50, false, true, entity -> {
            if (entity instanceof EndermanEntity || entity instanceof EnderDragonEntity || entity instanceof EndermiteEntity || entity instanceof ShulkerEntity) {
                if (this.getBloodType() == 3) return false;
            }

            if (this.isTempted()) return false;

            return !(entity instanceof CrimsonMosquitoEntity) && (this.isBaby() ? entity.isBaby() : true);
        }));
    }



    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("FlightTicks", this.flightTicks);
        compound.putInt("SickTicks", this.sickTicks);
        compound.putFloat("MosquitoScale", this.getMosquitoScale());
        compound.putBoolean("Flying", this.isFlying());
        compound.putBoolean("Shrinking", this.isShrinking());
        compound.putBoolean("IsFromFly", this.isFromFly());
        compound.putInt("BloodLevel", this.getBloodLevel());
        compound.putBoolean("Sick", this.isSick());
        compound.putInt("Age", this.getAge());
        compound.putBoolean("UsedToPlayers", this.usedToPlayers);
        compound.putInt("TimeNearPlayers", this.timeNearPlayers);

        compound.putInt("InLove", this.inLove);
        if (this.loveCause != null) {
            compound.putUUID("LoveCause", this.loveCause);
        }
        compound.putInt("BreedCooldown", this.breedCooldown);

        compound.putInt("BloodType", this.getBloodType());
    }

    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.flightTicks = compound.getInt("FlightTicks");
        this.sickTicks = compound.getInt("SickTicks");
        this.setMosquitoScale(compound.getFloat("MosquitoScale"));
        this.setFlying(compound.getBoolean("Flying"));
        this.setShrink(compound.getBoolean("Shrinking"));
        this.setFromFly(compound.getBoolean("IsFromFly"));
        this.setBloodLevel(compound.getInt("BloodLevel"));
        this.setSick(compound.getBoolean("Sick"));
        this.setAge(compound.getInt("Age"));
        this.usedToPlayers = compound.getBoolean("UsedToPlayers");
        this.timeNearPlayers = compound.getInt("TimeNearPlayers");

        this.inLove = compound.getInt("InLove");
        this.loveCause = compound.hasUUID("LoveCause") ? compound.getUUID("LoveCause") : null;
        this.breedCooldown = compound.getInt("BreedCooldown");
        this.entityData.set(BLOOD_TYPE, compound.getInt("BloodType"));
    }

    private void spit(LivingEntity target) {
        if (this.isSick()) return;

        EntityMosquitoSpit mosquitoSpitEntity = new EntityMosquitoSpit(this.level, this);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - mosquitoSpitEntity.getY();
        double d2 = target.getZ() - this.getZ();
        float f = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
        mosquitoSpitEntity.shoot(d0, d1 + (double) f, d2, isBaby() ? 1.3F : 1.5F, 10.0F);
        mosquitoSpitEntity.setType(this.getBloodType());
        if (!this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }
        if (this.getBloodLevel() > 0) {
            this.setBloodLevel(this.getBloodLevel() - 1);
        }
        this.level.addFreshEntity(mosquitoSpitEntity);

    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.FALL || source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || source == DamageSource.LAVA || source.isFire() || super.isInvulnerableTo(source);
    }

    public float getSoundVolume() {
        return this.isBaby() ? 0.5F : 1F;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && this.getRootVehicle() == source.getEntity().getRootVehicle()) {
            return super.hurt(source, amount * 0.333F);
        }

        if (this.flightTicks < 0) this.flightTicks = 0;

        return super.hurt(source, amount);
    }

    public void setBloodType(int t) {
        entityData.set(BLOOD_TYPE, t);
    }

    private boolean teleport(LivingEntity entity, double p_70825_1_, double p_70825_3_, double p_70825_5_) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_70825_1_, p_70825_3_, p_70825_5_);

        while(blockpos$mutable.getY() > 0 && !this.level.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
            blockpos$mutable.move(Direction.DOWN);
        }

        BlockState blockstate = this.level.getBlockState(blockpos$mutable);
        boolean flag = blockstate.getMaterial().blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            boolean flag2 = entity.randomTeleport(p_70825_1_, p_70825_3_, p_70825_5_, true);
            this.level.gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(entity));
            if (flag2 && !this.isSilent()) {
                this.level.playSound((PlayerEntity)null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return flag2;
        } else {
            return false;
        }
    }

    protected boolean teleport(LivingEntity entity) {
        if (!this.level.isClientSide() && entity.isAlive() && this.isAlive()) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 24.0D;
            double d1 = this.getY() + (double)(this.random.nextInt(24) - 8);
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 24.0D;
            return this.teleport(entity, d0, d1, d2);
        } else {
            return false;
        }
    }

    @Override
    public void rideTick() {
        assert this.getVehicle() != null;
        Entity entity = this.getVehicle();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else {
            this.setDeltaMovement(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                Entity mount = this.getVehicle();
                if (mount instanceof LivingEntity) {
                    this.yBodyRot = ((LivingEntity) mount).yBodyRot;
                    this.yHeadRot = ((LivingEntity) mount).yHeadRot;
                    this.yRotO = mount.yRotO;
                    float radius = isBaby() ? 0.45F : 1F;
                    float angle = (0.01745329251F * ((LivingEntity) mount).yBodyRot);
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    double extraZ = radius * MathHelper.cos(angle);

                    this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getEyeHeight() * 0.25F, mount.getY()), mount.getZ() + extraZ);
                    if (!mount.isAlive() || mount instanceof PlayerEntity && ((PlayerEntity)mount).isCreative()) {
                        this.stopRiding();
                    }
                    int delay = this.isBaby() ? 35 : 20;

                    if (drinkTime % delay == 0 && !level.isClientSide && this.isAlive()) {
                        if (mount.hurt(DamageSource.mobAttack(this), 2.0F)) {
                            if (this.getAge() >= 0 && this.getBloodLevel() < 3 && this.getBloodType() == 3) {
                                label1: for (int i = 0; i < 64; ++i) {
                                    if (this.teleport((LivingEntity) mount)) {
                                        break label1;
                                    }
                                }
                            }
                            this.playSound(SoundEvents.HONEY_DRINK, this.getSoundVolume(), this.getVoicePitch());
                            this.setBloodLevel(this.getBloodLevel() + 1);
                            if (this.getBloodType() != 3) {
                                if ((mount instanceof ShulkerEntity || mount instanceof EndermanEntity || mount instanceof EndermiteEntity)) {
                                    if (getBloodType() != 1) {
                                        this.entityData.set(BLOOD_TYPE, 1);
                                    }
                                } else if (mount instanceof CreakingEntity && this.getBloodType() != 2) {
                                    this.entityData.set(BLOOD_TYPE, 2);
                                } else if (!(mount instanceof CreakingEntity)) {
                                    if (mount instanceof StriderEntity || mount instanceof MagmaCubeEntity) {
                                        this.entityData.set(BLOOD_TYPE, 4);
                                    } else {
                                        this.entityData.set(BLOOD_TYPE, 0);
                                    }
                                }
                            }

                            this.stealEffectsFromMob(mount);

                            if (this.getBloodLevel() > (isBaby() ? 1 : 3)) {
                                this.setAge(this.getAge() + (20 * 20));
                                this.stopRiding();
                                ServerWorld serverWorld = (ServerWorld)level;
                                for (ServerPlayerEntity serverPlayerEntity : serverWorld.players()) {
                                    serverPlayerEntity.connection.send(new MosquitoDismount(this.getId(), mount.getId()));
                                }
                                this.setFlying(false);
                                this.flightTicks = -15;
                            }
                        }
                    }
                    if (drinkTime > 81 && !level.isClientSide) {
                        drinkTime = -20 - random.nextInt(20);
                        this.stopRiding();
                        ServerWorld serverWorld = (ServerWorld)level;
                        for (ServerPlayerEntity serverPlayerEntity : serverWorld.players()) {
                            serverPlayerEntity.connection.send(new MosquitoDismount(this.getId(), mount.getId()));
                        }
                        this.setFlying(false);
                        this.flightTicks = -15;
                    }
                }
            }
        }
    }

    private void stealEffectsFromMob(Entity mount) {
        LivingEntity source = (LivingEntity) mount;
        Collection<EffectInstance> activeEffects = new ArrayList<>(source.getActiveEffects());

        for (EffectInstance instance : activeEffects) {
            Effect effect = instance.getEffect();

            int reducedDuration = (int) (instance.getDuration() * (1.0 - 1.0 / 1.4));
            int extendedDuration = (int) (instance.getDuration() * (1.0 + 1.0 / 1.4));

            int amplifier = instance.getAmplifier();
            boolean ambient = instance.isAmbient();
            boolean visible = instance.isVisible();
            boolean showIcon = instance.showIcon();

            EffectInstance transferredEffect;

            if (this.hasEffect(effect)) {
                EffectInstance current = this.getEffect(effect);
                this.removeEffect(effect);

                int newDuration = (int) (current.getDuration() + (instance.getDuration() / 1.4));
                int newAmplifier = Math.max(current.getAmplifier(), amplifier);

                transferredEffect = new EffectInstance(effect, newDuration, newAmplifier, ambient, visible, showIcon);
            } else {
                transferredEffect = new EffectInstance(effect, extendedDuration, amplifier, ambient, visible, showIcon);
            }

            this.addEffect(transferredEffect);

            source.removeEffect(effect);

            if (instance.getDuration() >= 50) {
                EffectInstance reducedEffect = new EffectInstance(effect, reducedDuration, amplifier, ambient, visible, showIcon);
                source.addEffect(reducedEffect);
            }
        }

        Collection<EffectInstance> mobEffects = new ArrayList<>(this.getActiveEffects());

        for (EffectInstance eff : mobEffects) {
            EffectInstance instance = eff;
            Effect effect = instance.getEffect();
            if (effect.isBeneficial()) continue;

            int currentDuration = instance.getDuration();
            int transferAmount = (int) (currentDuration * 0.3);

            int amplifier = instance.getAmplifier();
            boolean ambient = instance.isAmbient();
            boolean visible = instance.isVisible();
            boolean showIcon = instance.showIcon();

            int amountToTransfer;
            if (source.hasEffect(effect)) {
                amountToTransfer = (source.getEffect(effect).getDuration() + transferAmount);

            } else {
                amountToTransfer = transferAmount;
            }

            EffectInstance toAdd = new EffectInstance(effect, amountToTransfer, amplifier, ambient, visible, showIcon);

            source.removeEffect(effect);
            source.addEffect(toAdd);

            EffectInstance toAddToMe = new EffectInstance(effect, currentDuration - (int) (transferAmount * 0.5), amplifier, ambient, visible, showIcon);

            this.removeEffect(effect);
            this.addEffect(toAddToMe);
        }

    }





    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(SHOOTING, false);
        this.entityData.define(SICK, false);
        this.entityData.define(BLOOD_LEVEL, 0);
        this.entityData.define(BLOOD_TYPE, BloodType.RED.id);
        this.entityData.define(AGE, 0);
        this.entityData.define(SHRINKING, false);
        this.entityData.define(FROM_FLY, false);
        this.entityData.define(MOSQUITO_SCALE, 1F);
    }

    public boolean isBaby() {
        return this.getAge() < 0;
    }

    public int getAge() {
        return this.entityData.get(AGE);
    }

    public void setAge(int age) {
        this.entityData.set(AGE, age);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING).booleanValue();
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public void setupShooting() {
        this.entityData.set(SHOOTING, true);
        this.shootingTicks = 5;
    }

    public int getBloodLevel() {
        return Math.min(this.entityData.get(BLOOD_LEVEL).intValue(), this.isBaby() ? this.getBloodType() == 4 ? 3 : 2 : this.getBloodType() == 4 ? 8 : 4);
    }

    public void setBloodLevel(int bloodLevel) {
        this.entityData.set(BLOOD_LEVEL, bloodLevel);
    }

    public boolean isShrinking() {
        return this.entityData.get(SHRINKING).booleanValue();
    }

    public boolean isFromFly() { return this.entityData.get(FROM_FLY).booleanValue(); }

    public void setShrink(boolean shrink) {
        this.entityData.set(SHRINKING, shrink);
    }

    public void setFromFly(boolean fromFly) {
        this.entityData.set(FROM_FLY, fromFly);
    }

    public float getMosquitoScale() {
        return this.entityData.get(MOSQUITO_SCALE);
    }

    public void setMosquitoScale(float scale) {
        this.entityData.set(MOSQUITO_SCALE, scale);
    }


    public boolean isSick() {
        return this.entityData.get(SICK).booleanValue();
    }

    public void setSick(boolean shrink) {
        this.entityData.set(SICK, shrink);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.MOSQUITO_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.MOSQUITO_DIE;
    }

    public void tick() {
        super.tick();
        boolean shooting = entityData.get(SHOOTING);
        if (prevFlying != this.isFlying()) {
            //this.recalculateSize();
        }
        if (shooting && shootProgress < 5) {
            shootProgress += 1;
        }
        if (!shooting && shootProgress > 0) {
            shootProgress -= 1;
        }
        if (this.isFlying() && flyProgress < 5) {
            flyProgress += 1;
        }
        if (!this.isFlying() && flyProgress > 0) {
            flyProgress -= 1;
        }
        if (!level.isClientSide && this.isPassenger()) {
            this.setFlying(false);
        }
        if (!level.isClientSide) {
            if (isFlying()) {
                this.setNoGravity(true);
            } else {
                this.setNoGravity(false);
            }
        }
        if (this.flyProgress == 0 && random.nextInt(200) == 0) {
            randomWingFlapTick = 5 + random.nextInt(15);
        }
        if (randomWingFlapTick > 0) {
            randomWingFlapTick--;
        }
        if (!level.isClientSide && isOnGround() && !this.isFlying()) {
            BlockPos blockBelow = this.blockPosition().below();
            BlockState belowState = level.getBlockState(blockBelow);
            boolean isOnLava = belowState.is(Blocks.LAVA) || level.getFluidState(blockBelow).is(FluidTags.LAVA);

            int takeoffChance = isOnLava ? 100 : 5;

            if ((flightTicks >= 0 && random.nextInt(takeoffChance) == 0) || this.getTarget() != null) {
                this.setFlying(true);
                this.setDeltaMovement(this.getDeltaMovement().add(
                        (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F,
                        0.5D,
                        (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F
                ));
                this.onGround = false;
            }
        }

        if (flightTicks < 0) {
            flightTicks++;
        }

        if (this.isTempted() && this.getTarget() != null) {
            this.setTarget(null);
        }

        if (isFlying() & !level.isClientSide) {
            flightTicks++;
            if (flightTicks > 200 && (this.getTarget() == null || !this.getTarget().isAlive())) {
                BlockPos above = this.getGroundPosition(this.blockPosition().above());
                if (level.getFluidState(above).isEmpty() && !level.getBlockState(above).isAir()) {
                    this.getDeltaMovement().add(0, -0.2D, 0);
                    if (this.isOnGround()) {
                        this.setFlying(false);
                        flightTicks = -150 - random.nextInt(200);
                    }
                }
            }
        }
        prevMosquitoScale = this.getMosquitoScale();


        this.setAge(this.getAge() + 1);

        if (this.isBaby()) {
            if (level.getNearestSurvivalPlayer(this, 12D) != null) {
                this.timeNearPlayers +=1;
            }
            if (timeNearPlayers >= 6000) this.usedToPlayers = true;
        }

        this.setMosquitoScale(this.getScaleForAge(this.getAge()));

        if (!level.isClientSide && shootingTicks > 0) {
            shootingTicks--;
            if (shootingTicks == 0) {
                boolean use = true;
                if (this.usedToPlayers && this.getTarget() != null && this.getTarget() instanceof PlayerEntity) {
                    use = false;
                }

                if (use) {
                    if (this.getTarget() != null && this.getBloodLevel() > 0) {
                        this.spit(this.getTarget());
                    }
                    this.entityData.set(SHOOTING, false);
                }
            }
        }
        this.buzzSoundTracker.tick();
        if(!isPassenger()){
            drinkTime = 0;
        }
        if(isPassenger() || drinkTime < 0){
            if(isPassenger() && drinkTime < 0){
                drinkTime = 0;
            }
            drinkTime++;
        }
        if (this.getBloodType() == 4 && this.isAlive() && this.isEyeInFluid(FluidTags.LAVA) && this.tick(MathHelper.randomBetweenInclusive(random, 15, 30)) && this.getBloodLevel() < (this.isBaby() ? 4 : 9) && !level.isClientSide) {
            this.playSound(SoundEvents.HONEY_DRINK, this.getSoundVolume(), this.getVoicePitch());
            this.setBloodLevel(this.getBloodLevel() + 1);
        }
        prevFlyProgress = flyProgress;
        prevShootProgress = shootProgress;
        prevFlying = this.isFlying();




        if (this.isInLava()) {
            ISelectionContext iselectioncontext = ISelectionContext.of(this);
            if (iselectioncontext.isAbove(FlowingFluidBlock.STABLE_SHAPE, this.blockPosition(), true) && !this.level.getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
                this.onGround = true;
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D).add(0.0D, 0.05D, 0.0D));
            }
        }
    }




    public boolean canStandOnFluid(Fluid p_230285_1_) {
        return p_230285_1_.is(FluidTags.LAVA);
    }

    public boolean isAffectedByFluids() {
        return false;
    }





    public float getScaleForAge(int ageTicks) {
        final int minAge = -18000;
        final float minScale = 0.15F;
        final float maxScale = 1.0F;

        int clampedAge = Math.max(minAge, Math.min(ageTicks, 0));

        float progress = (float)(clampedAge - minAge) / -minAge;

        return minScale + (maxScale - minScale) * progress;
    }




    @Override
    public void handleEntityEvent(byte id) {
        if (id == 79) {
            for (int i = 0; i < 27; i++) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                double d3 = 10d;
                this.level.addParticle(ParticleTypes.EXPLOSION, this.getRandomX(1.6D), this.getY() + random.nextFloat() * 3.4F, this.getRandomZ(1.6D), d0, d1, d2);

            }
        } else if (id == 18) {
            for(int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
        return false;
    }

    @Override
    protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
    }

    public CreatureAttribute getMobType() {
        return CreatureAttribute.ARTHROPOD;
    }

    @Override
    public EntitySize getDimensions(Pose pose) {
        float scale = this.getMosquitoScale();

        float width;
        float height;

        if (isFlying()) {
            width = lerp(scale, 0.4F, 1.2F);
            height = lerp(scale, 0.25F, 1.8F);
        } else {
            width = lerp(scale, 0.4F, 1.25F);
            height = lerp(scale, 0.25F, 1.15F);
        }

        return EntitySize.scalable(width, height);
    }

    private float lerp(float scale, float min, float max) {
        return min + (max - min) * ((scale - 0.15F) / (1.0F - 0.15F));
    }


    public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
        if (AGE.equals(p_184206_1_)) {
            if (!this.horizontalCollision) {
                this.refreshDimensions();
            }

        }

        super.onSyncedDataUpdated(p_184206_1_);
    }

    public void travel(Vector3d v3d) {
        if (this.onGround && !this.isFlying()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            v3d = Vector3d.ZERO;
        }
        super.travel(v3d);

    }

    private BlockPos getGroundPosition(BlockPos radialPos) {
        while (radialPos.getY() > 1 && level.isEmptyBlock(radialPos)) {
            radialPos = radialPos.below();
        }

        return radialPos;
    }

    public boolean isTargetBlocked(Vector3d target) {
        Vector3d Vector3d = new Vector3d(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new RayTraceContext(Vector3d, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, this)).getType() != RayTraceResult.Type.MISS;

    }

    public boolean isFood(ItemStack stack) {
        return Ingredient.of(Items.CHICKEN, Items.RABBIT, Items.SPIDER_EYE, Items.ROTTEN_FLESH).test(stack);
    }

    public ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
        if (this.isFood(itemstack)) {
            int i = this.getAge();
            if (!this.level.isClientSide && i > 0 && this.canFallInLove()) {
                this.usePlayerItem(p_230254_1_, itemstack);
                this.setInLove(p_230254_1_);

                return ActionResultType.SUCCESS;
            }

            if (this.isBaby()) {
                this.usePlayerItem(p_230254_1_, itemstack);
                this.setAge(this.getAge() + (40 * 20));
                return ActionResultType.sidedSuccess(this.level.isClientSide);
            }

            if (this.level.isClientSide) {
                return ActionResultType.CONSUME;
            }
        }

        return super.mobInteract(p_230254_1_, p_230254_2_);
    }

    protected void usePlayerItem(PlayerEntity p_175505_1_, ItemStack p_175505_2_) {
        if (!p_175505_1_.abilities.instabuild) {
            p_175505_2_.shrink(1);
        }

    }

    static class RandomFlyGoal extends Goal {
        private final CrimsonMosquitoEntity parentEntity;
        private BlockPos target = null;

        public RandomFlyGoal(CrimsonMosquitoEntity mosquito) {
            this.parentEntity = mosquito;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            MovementController movementController = this.parentEntity.moveControl;
            if (!parentEntity.isFlying() || parentEntity.getTarget() != null) return false;
            if (!movementController.hasWanted() || target == null) {
                target = getBlockInViewMosquito();
                if (target != null) {
                    this.parentEntity.moveControl.setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null &&
                    parentEntity.isFlying() &&
                    parentEntity.distanceToSqr(Vector3d.atCenterOf(target)) > 2.4D &&
                    parentEntity.getMoveControl().hasWanted() &&
                    !parentEntity.horizontalCollision;
        }

        public void stop() {
            target = null;
        }
        public void tick() {
            if (target == null) {
                target = getBlockInViewMosquito();
            }
            if (target != null) {
                this.parentEntity.moveControl.setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if (parentEntity.distanceToSqr(Vector3d.atCenterOf(target)) < 2.5F) {
                    target = null;
                }

            }
        }

        public BlockPos getBlockInViewMosquito() {
            float radius = 1 + parentEntity.random.nextInt(5);
            float neg = parentEntity.random.nextBoolean() ? 1 : -1;
            float renderYawOffset = parentEntity.yBodyRot;
            float angle = (0.01745329251F * renderYawOffset) + 3.15F + (parentEntity.random.nextFloat() * neg);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            BlockPos radialPos = new BlockPos(parentEntity.getX() + extraX, parentEntity.getY() + 2, parentEntity.getZ() + extraZ);
            BlockPos ground = parentEntity.getGroundPosition(radialPos);
            int up = parentEntity.isSick() ? 2 : 6;
            BlockPos newPos = ground.above(1 + parentEntity.random.nextInt(up));
            Vector3d centeredPos = Vector3d.atCenterOf(newPos);
            if (!parentEntity.isTargetBlocked(centeredPos) &&
                    parentEntity.distanceToSqr(centeredPos) > 6.0D) {
                return newPos;
            }
            return null;
        }



    }

    public class FlyTowardsTarget extends Goal {
        private final CrimsonMosquitoEntity parentEntity;

        public FlyTowardsTarget(CrimsonMosquitoEntity mosquito) {
            this.parentEntity = mosquito;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (!parentEntity.isFlying() || parentEntity.getBloodLevel() > 0 || parentEntity.drinkTime < 0) {
                return false;
            }
            return !parentEntity.isPassenger() && parentEntity.getTarget() != null && !isBittenByMosquito(parentEntity.getTarget());
        }

        public boolean canContinueToUse() {
            return parentEntity.drinkTime >= 0 && parentEntity.getTarget() != null && !isBittenByMosquito(parentEntity.getTarget()) && !parentEntity.horizontalCollision && parentEntity.getBloodLevel() == 0 && parentEntity.isFlying() && parentEntity.moveControl.hasWanted();
        }

        public boolean isBittenByMosquito(Entity entity) {
            if (this.parentEntity.usedToPlayers && entity instanceof PlayerEntity) return true;

            for (Entity e : entity.getPassengers()) {
                if (e instanceof CrimsonMosquitoEntity) {
                    return true;
                }
            }
            return false;
        }



        public void stop() {
        }

        public void tick() {
            if (parentEntity.getTarget() != null) {
                this.parentEntity.moveControl.setWantedPosition(parentEntity.getTarget().getX(), parentEntity.getTarget().getY(), parentEntity.getTarget().getZ(), 1.0D);
                if (parentEntity.getBoundingBox().inflate(0.3F, 0.3F, 0.3F).intersects(parentEntity.getTarget().getBoundingBox()) && !isBittenByMosquito(parentEntity.getTarget()) && parentEntity.drinkTime == 0) {
                    parentEntity.startRiding(parentEntity.getTarget(), true);
                    if (!parentEntity.level.isClientSide) {
                        ServerWorld serverWorld = (ServerWorld)level;
                        for (ServerPlayerEntity serverPlayerEntity : serverWorld.players()) {
                            serverPlayerEntity.connection.send(new MosquitoMountMob(parentEntity.getId(), parentEntity.getTarget().getId()));
                        }
                    }
                }
            }
        }
    }

    static class FlyAwayFromTarget extends Goal {
        private final CrimsonMosquitoEntity parentEntity;
        private int spitCooldown = 0;
        private BlockPos shootPos = null;

        public FlyAwayFromTarget(CrimsonMosquitoEntity mosquito) {
            this.parentEntity = mosquito;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (!parentEntity.isFlying() || parentEntity.getBloodLevel() <= 0 && parentEntity.drinkTime >= 0) {
                return false;
            }
            if (!parentEntity.isPassenger() && parentEntity.getTarget() != null) {
                shootPos = getBlockInTargetsViewMosquito(parentEntity.getTarget());
                return true;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return parentEntity.getTarget() != null &&( parentEntity.getBloodLevel() > 0 || parentEntity.drinkTime < 0) && parentEntity.isFlying() && !parentEntity.horizontalCollision;
        }

        public void resetTask() {
            spitCooldown = 20;
        }

        public void tick() {
            if (spitCooldown > 0) {
                spitCooldown--;
            }
            if (parentEntity.getTarget() != null) {
                if (shootPos == null) {
                    shootPos = getBlockInTargetsViewMosquito(parentEntity.getTarget());
                } else {
                    this.parentEntity.moveControl.setWantedPosition(shootPos.getX() + 0.5D, shootPos.getY() + 0.5D, shootPos.getZ() + 0.5D, 1.0D);
                    this.parentEntity.lookAt(parentEntity.getTarget(), 30.0F, 30.0F);
                    if (parentEntity.distanceToSqr(Vector3d.atCenterOf(shootPos)) < 2.5F) {
                        if (spitCooldown == 0 && parentEntity.getBloodLevel() > 0) {
                            parentEntity.setupShooting();
                            spitCooldown = 20;
                        }
                        shootPos = null;
                    }
                }
            }

        }

        public BlockPos getBlockInTargetsViewMosquito(LivingEntity target) {
            float radius = 4 + parentEntity.random.nextInt(5);
            float neg = parentEntity.random.nextBoolean() ? 1 : -1;
            float angle = (0.01745329251F * (target.yHeadRot + 90F + parentEntity.random.nextInt(180)));
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            BlockPos radialPos = new BlockPos(target.getX() + extraX, target.getY() + 1, target.getZ() + extraZ);
            BlockPos ground = radialPos;
            if (parentEntity.distanceToSqr(Vector3d.atCenterOf(ground)) > 30) {
                if (!parentEntity.isTargetBlocked(Vector3d.atCenterOf(ground)) && parentEntity.distanceToSqr(Vector3d.atCenterOf(ground)) > 6) {
                    return ground;
                }
            }
            return parentEntity.blockPosition();
        }
    }

    static class MoveHelperController extends MovementController {
        private final CrimsonMosquitoEntity parentEntity;

        private double currentTargetX;
        private double currentTargetY;
        private double currentTargetZ;
        private boolean usingAlternativeTarget = false;

        private int stuckTicks = 0;
        private Vector3d lastPos = Vector3d.ZERO;

        public MoveHelperController(CrimsonMosquitoEntity sunbird) {
            super(sunbird);
            this.parentEntity = sunbird;
        }

        @Override
        public void tick() {
            // Detect stuck movement
            Vector3d currentPos = parentEntity.position();
            if (currentPos.distanceToSqr(lastPos) < 0.0025D) {
                stuckTicks++;
            } else {
                stuckTicks = 0;
            }
            lastPos = currentPos;

            // Try to resolve being stuck
            if (stuckTicks > 10) {
                tryFindAlternativeTarget();
                stuckTicks = 0;
            }

            // Select which coordinates to fly toward
            double targetX = usingAlternativeTarget ? currentTargetX : wantedX;
            double targetY = usingAlternativeTarget ? currentTargetY : wantedY;
            double targetZ = usingAlternativeTarget ? currentTargetZ : wantedZ;

            // Reset if we reached the alternative target
            if (usingAlternativeTarget && parentEntity.position().distanceToSqr(new Vector3d(currentTargetX, currentTargetY, currentTargetZ)) < 1.5D) {
                usingAlternativeTarget = false;
            }

            if (speedModifier >= 1 && parentEntity.isBaby()) {
                speedModifier = 0.35D;
            }

            if (parentEntity.isFlying()) {
                if (this.operation == Action.STRAFE) {
                    Vector3d vector3d = new Vector3d(targetX - parentEntity.getX(), targetY - parentEntity.getY(), targetZ - parentEntity.getZ());
                    double d0 = vector3d.length();
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(0, vector3d.scale(this.speedModifier * 0.05D / d0).y, 0));

// Base movement calculations
                    float f = (float) this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                    float f1 = (float) this.speedModifier * f;

                    float f2 = this.strafeForwards;
                    float f3 = this.strafeRight;
                    float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);
                    if (f4 < 1.0F) {
                        f4 = 1.0F;
                    }

                    f4 = f1 / f4;
                    f2 *= f4;
                    f3 *= f4;
                    float f5 = MathHelper.sin(this.mob.yRot * ((float) Math.PI / 180F));
                    float f6 = MathHelper.cos(this.mob.yRot * ((float) Math.PI / 180F));
                    float f7 = f2 * f6 - f3 * f5;
                    float f8 = f3 * f6 + f2 * f5;
                    this.strafeForwards = 1.0F;
                    this.strafeRight = 0.0F;

                    this.mob.setSpeed(f1);
                    this.mob.setXxa(this.strafeForwards);
                    this.mob.setZza(this.strafeRight);
                    this.operation = Action.WAIT;
                } else if (this.operation == Action.MOVE_TO) {
                    Vector3d toTarget = new Vector3d(targetX - parentEntity.getX(), targetY - parentEntity.getY(), targetZ - parentEntity.getZ());
                    double d0 = toTarget.length();
                    AxisAlignedBB aabb = parentEntity.getBoundingBox();
                    double avgEdgeLength = (aabb.maxX - aabb.minX + aabb.maxY - aabb.minY + aabb.maxZ - aabb.minZ) / 3.0;


                    if (d0 < avgEdgeLength) {
                        this.operation = Action.WAIT;
                        parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
                    } else {
                        parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(toTarget.scale(this.speedModifier * 0.05D / d0)));

                        rotateTowardMovement();
                    }
                }
            } else {
                operation = Action.WAIT;
                this.mob.setSpeed(0);
                this.mob.setXxa(0);
                this.mob.setZza(0);
            }
        }

        private void tryFindAlternativeTarget() {
            // Try nearby offsets to find a spot the mob can fly toward
            double[][] offsets = {
                    {1, 0, 0}, {-1, 0, 0},
                    {0, 1, 0}, {0, -1, 0},
                    {0, 0, 1}, {0, 0, -1},
                    {1, 1, 0}, {-1, 1, 0},
                    {1, 0, 1}, {0, 1, 1},
            };

            for (double[] offset : offsets) {
                Vector3d offsetVec = new Vector3d(offset[0], offset[1], offset[2]);
                Vector3d candidate = parentEntity.position().add(offsetVec);
                if (canReach(offsetVec, 3)) {
                    currentTargetX = candidate.x;
                    currentTargetY = candidate.y;
                    currentTargetZ = candidate.z;
                    usingAlternativeTarget = true;
                    this.operation = Action.MOVE_TO;
                    return;
                }
            }
        }

        private void rotateTowardMovement() {
            if (parentEntity.getTarget() == null) {
                Vector3d motion = parentEntity.getDeltaMovement();
                parentEntity.yRot = -((float) MathHelper.atan2(motion.x, motion.z)) * (180F / (float) Math.PI);
                parentEntity.yBodyRot = parentEntity.yRot;
            } else {
                double dx = parentEntity.getTarget().getX() - parentEntity.getX();
                double dz = parentEntity.getTarget().getZ() - parentEntity.getZ();
                parentEntity.yRot = -((float) MathHelper.atan2(dx, dz)) * (180F / (float) Math.PI);
                parentEntity.yBodyRot = parentEntity.yRot;
            }
        }

        private boolean canReach(Vector3d offset, int steps) {
            AxisAlignedBB aabb = this.parentEntity.getBoundingBox();

            for (int i = 1; i < steps; ++i) {
                aabb = aabb.move(offset);
                if (!this.parentEntity.level.noCollision(this.parentEntity, aabb)) {
                    return false;
                }
            }

            return true;
        }
    }



    private static class BuzzSoundTracker {
        private static final int BUZZ_INTERVAL_TICKS = 98;
        private static final int BUZZ_POOL_SIZE = 4;
        private float buzzVolumes = 0F;
        private float buzzPitches = 0F;
        private final CrimsonMosquitoEntity mosquito;
        private final List<EntityBoundSoundInstance> buzzPool = new ArrayList<>();
        private SoundHandler soundHandler;
        private int tickCounter = 0;
        float[] PITCHES = new float[]{0.85F, 0.9F, 0.95F, 1F, 1.05F};
        float[] VOLUMES = new float[]{-0.1F, 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F, 1F, 1.1F};

        private BuzzSoundTracker(CrimsonMosquitoEntity mosquito) {
            this.mosquito = mosquito;

            for (int i = 0; i < BUZZ_POOL_SIZE; i++) {
                buzzPool.add(createBuzzSound());
            }
        }

        private EntityBoundSoundInstance createBuzzSound() {
            Random random = new Random(mosquito.getUUID().hashCode() & mosquito.blockPosition().hashCode());
            if (buzzVolumes == 0F) {
                buzzVolumes = VOLUMES[random.nextInt(VOLUMES.length)];
                buzzPitches = PITCHES[random.nextInt(PITCHES.length)];
            }

            return new EntityBoundSoundInstance(
                    SoundEvents.MOSQUITO_LOOP,
                    mosquito.getSoundSource(),
                    mosquito.getSoundVolume() + buzzVolumes,
                    buzzPitches,
                    mosquito
            );
        }

        private void tick() {
            if (mosquito.level.isClientSide) {
                if (soundHandler == null) {
                    soundHandler = Minecraft.getInstance().getSoundManager();
                }

                if (mosquito.isFlying() && !mosquito.isDeadOrDying()) {
                    if (tickCounter % BUZZ_INTERVAL_TICKS == 0) {
                        int index = (tickCounter / BUZZ_INTERVAL_TICKS) % BUZZ_POOL_SIZE;
                        EntityBoundSoundInstance buzz = buzzPool.get(index);


                        if (!soundHandler.isActive(buzz)) {
                            soundHandler.play(buzz);
                        }
                    }


                    tickCounter++;
                } else {
                    for (EntityBoundSoundInstance buzz : buzzPool) {
                        if (soundHandler.isActive(buzz)) {
                            soundHandler.stop(buzz);
                        }
                        soundHandler.stopIfQueued(buzz);
                    }
                    tickCounter = 0;
                }
            }
        }
    }

}

