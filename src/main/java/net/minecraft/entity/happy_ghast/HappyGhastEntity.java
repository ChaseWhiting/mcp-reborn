package net.minecraft.entity.happy_ghast;

import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.newgoals.TemptGoal;
import net.minecraft.entity.camel.CamelAi;
import net.minecraft.entity.leashable.Leashable;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.profiler.IProfiler;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import org.joml.Vector2d;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

public class HappyGhastEntity extends Animal {
    public static final float BABY_SCALE = 0.2375f;
    public static final int WANDER_GROUND_DISTANCE = 16;
    public static final int SMALL_RESTRICTION_RADIUS = 32;
    public static final int LARGE_RESTRICTION_RADIUS = 64;
    public static final int RESTRICTION_RADIUS_BUFFER = 16;
    public static final int FAST_HEALING_TICKS = 20;
    public static final int SLOW_HEALING_TICKS = 600;
    public static final int MAX_PASSANGERS = 4;
    private static final int STILL_TIMEOUT_ON_LOAD_GRACE_PERIOD = 60;
    private static final int MAX_STILL_TIMEOUT = 10;
    public static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.0f;
    public static final Predicate<ItemStack> IS_FOOD = itemStack -> itemStack.is(Items.SNOWBALL);
    private int leashHolderTime = 0;
    private int serverStillTimeout;
    private static final DataParameter<Boolean> IS_LEASH_HOLDER = EntityDataManager.defineId(HappyGhastEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> STAYS_STILL = EntityDataManager.defineId(HappyGhastEntity.class, DataSerializers.BOOLEAN);
    private static final float MAX_SCALE = 1.0f;

    public boolean isRidden() {
        return !this.getPassengers().isEmpty();
    }

    public HappyGhastEntity(EntityType<? extends HappyGhastEntity> entityType, World level) {
        super(entityType, level);
        this.moveControl = new GhastEntity.GhastMoveControl(this, true, ghast -> ghast.as(HappyGhastEntity.class).isOnStillTimeout());
        this.lookControl = new HappyGhastLookControl();
    }

    private void setServerStillTimeout(int n) {
        this.serverStillTimeout = n;
        this.syncStayStillFlag();
    }

    private PathNavigator createBabyNavigation(World level) {
        return new BabyFlyingPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new HappyGhastFloatGoal());
        this.goalSelector.addGoal(4, new TemptGoal.ForNonPathfinders(this, 1.0, itemStack -> this.isWearingBodyArmor() || this.isBaby() ? IS_FOOD.test((ItemStack)itemStack) : itemStack.is(Items.SNOWBALL), false, 7.0));
        this.goalSelector.addGoal(5, new GhastEntity.RandomFloatAroundGoal(this, 16));
    }


    public boolean isWearingBodyArmor() {
        return !this.getItemBySlot(EquipmentSlotType.CHEST).isEmpty();
    }

    private void adultGhastSetup() {
        this.moveControl = new GhastEntity.GhastMoveControl(this, true, ghast -> ghast.as(HappyGhastEntity.class).isOnStillTimeout());
        this.lookControl = new HappyGhastLookControl();
        this.navigation = this.createNavigation(this.level());
        World level = this.level();
        if (level instanceof ServerWorld serverWorld) {
            this.removeAllGoals(goal -> true);
            this.registerGoals();
            this.getBrain().stopAll(serverWorld, this);
            this.brain.clearMemories();
        }
    }

    @SuppressWarnings("unchecked")
    public Brain<HappyGhastEntity> getBrain() {
        return (Brain<HappyGhastEntity>) super.getBrain();
    }

    private void babyGhastSetup() {
        this.moveControl = new FlyingMovementController(this, 180, true);
        this.lookControl = new LookController(this);
        this.navigation = this.createBabyNavigation(this.level());
        this.setServerStillTimeout(0);
        this.removeAllGoals(goal -> true);
    }

    @Override
    protected void ageBoundaryReached() {
        if (this.isBaby()) {
            this.babyGhastSetup();
        } else {
            this.adultGhastSetup();
        }
        super.ageBoundaryReached();
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.TEMPT_RANGE, 16.0).add(Attributes.FLYING_SPEED, 0.05).add(Attributes.MOVEMENT_SPEED, 0.05).add(Attributes.FOLLOW_RANGE, 16.0).add(Attributes.CAMERA_DISTANCE, 8.0);
    }

    @Override
    protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    public void travel(Vector3d vec3) {
        float f = (float)this.getAttributeValue(Attributes.FLYING_SPEED) * 5.0f / 3.0f;
        this.travelFlying(vec3, f, f, f);
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, IWorldReader levelReader) {
        if (!levelReader.isEmptyBlock(blockPos)) {
            return 0.0f;
        }
        if (levelReader.isEmptyBlock(blockPos.below()) && !levelReader.isEmptyBlock(blockPos.below(2))) {
            return 10.0f;
        }
        return 5.0f;
    }

    @Override
    public boolean canBreatheUnderwater() {
        if (this.isBaby()) {
            return true;
        }
        return super.canBreatheUnderwater();
    }

    @Override
    protected boolean shouldStayCloseToLeashHolder() {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
    }

    @Override
    public float getVoicePitch() {
        return 1.0f;
    }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    public int getAmbientSoundInterval() {
        int n = super.getAmbientSoundInterval();
        if (this.isVehicle()) {
            return n * 6;
        }
        return n;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isBaby() ? SoundEvents.GHASTLING_AMBIENT : SoundEvents.HAPPY_GHAST_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return this.isBaby() ? SoundEvents.GHASTLING_HURT : SoundEvents.HAPPY_GHAST_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isBaby() ? SoundEvents.GHASTLING_DEATH : SoundEvents.HAPPY_GHAST_DEATH;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    @Nullable
    public AgeableEntity getBreedOffspring(ServerWorld serverLevel, AgeableEntity ageableMob) {
        return EntityType.HAPPY_GHAST.create(serverLevel);
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Override
    public float getScale() {
        return this.isBaby() ? 0.2375f : 1.0f;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return IS_FOOD.test(itemStack);
    }

//    @Override
//    public boolean canUseSlot(EquipmentSlotType equipmentSlot) {
//        if (equipmentSlot == EquipmentSlot.BODY) {
//            return this.isAlive() && !this.isBaby();
//        }
//        return super.canUseSlot(equipmentSlot);
//    }
//
//    @Override
//    protected boolean canDispenserEquipIntoSlot(EquipmentSlot equipmentSlot) {
//        return equipmentSlot == EquipmentSlot.BODY;
//    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand interactionHand) {
        ActionResultType interactionResult;
        if (this.isBaby()) {
            return super.mobInteract(player, interactionHand);
        }
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (!itemStack.isEmpty() && (interactionResult = itemStack.interactLivingEntity(player, this, interactionHand)).consumesAction()) {
            return interactionResult;
        }
        if (this.isWearingBodyArmor() && !player.isSecondaryUseActive()) {
            this.doPlayerRide(player);
            return ActionResultType.SUCCESS;
        }
        return super.mobInteract(player, interactionHand);
    }

    private void doPlayerRide(PlayerEntity player) {
        if (!this.level().isClientSide) {
            player.startRiding(this);
            this.stopInPlace();
            this.setServerStillTimeout(5);
            this.getMoveControl().setWait();
        }
    }

    @Override
    protected void addPassenger(Entity entity) {
        if (!this.isVehicle()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.HARNESS_GOGGLES_DOWN, this.getSoundSource(), 1.0f, 1.0f);
        }
        super.addPassenger(entity);
        if (!this.level().isClientSide) {
            if (!this.scanPlayerAboveGhast()) {
                this.setServerStillTimeout(0);
            } else if (this.serverStillTimeout > 10) {
                this.setServerStillTimeout(10);
            }
        }
    }

    @Override
    protected void removePassenger(Entity entity) {
        super.removePassenger(entity);
        if (!this.level().isClientSide) {
            this.setServerStillTimeout(10);
        }
        if (!this.isVehicle()) {
            this.clearHome();
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.HARNESS_GOGGLES_UP, this.getSoundSource(), 1.0f, 1.0f);
        }
    }


    @Override
    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengers().size() < 4;
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (this.isWearingBodyArmor() && !this.isOnStillTimeout() && entity instanceof PlayerEntity) {
            return (PlayerEntity) entity;
        }
        return super.getControllingPassenger();
    }

    @Nullable
    public Entity getFirstPassenger() {
        return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
    }

    @Override
    protected Vector3d getRiddenInput(PlayerEntity player, Vector3d vec3) {
        float f = player.xxa;
        float f2 = 0.0f;
        float f3 = 0.0f;
        if (player.zza != 0.0f) {
            float f4 = MathHelper.cos(player.xRot * ((float)Math.PI / 180));
            float f5 = -MathHelper.sin(player.xRot * ((float)Math.PI / 180));
            if (player.zza < 0.0f) {
                f4 *= -0.5f;
                f5 *= -0.5f;
            }
            f3 = f5;
            f2 = f4;
        }
        if (player.isJumping()) {
            f3 += 0.5f;
        }
        return new Vector3d(f, f3, f2).scale((double)3.9f * this.getAttributeValue(Attributes.FLYING_SPEED));
    }

    protected Vector2d getRiddenRotation(LivingEntity livingEntity) {
        return new Vector2d(livingEntity.xRot * 0.5f, livingEntity.getYRot());
    }

    @Override
    protected void tickRidden(PlayerEntity player, Vector3d vec3) {
        super.tickRidden(player, vec3);
        Vector2d vec2 = this.getRiddenRotation(player);
        float f = this.getYRot();
        float f2 = MathHelper.wrapFlDegrees((float) (vec2.y - f));
        float f3 = 0.08f;
        this.setRot(f += f2 * 0.08f, (float) vec2.x);
        this.yBodyRot = this.yHeadRot = f;
        this.yRotO = this.yHeadRot;
    }

    protected Brain.BrainCodec<HappyGhastEntity> brainProvider() {
        return HappyGhastAi.brainProvider();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return HappyGhastAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep() {
        ServerWorld serverLevel = (ServerWorld) level;
        if (this.isBaby()) {
            IProfiler profilerFiller = this.level.getProfiler();
            profilerFiller.push("happyGhastBrain");
            this.getBrain().tick(serverLevel, this);
            profilerFiller.pop();
            profilerFiller.push("happyGhastActivityUpdate");
            HappyGhastAi.updateActivity(this);
            profilerFiller.pop();
        }
        this.checkRestriction();
        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            return;
        }
        if (this.leashHolderTime > 0) {
            --this.leashHolderTime;
        }
        this.setLeashHolder(this.leashHolderTime > 0);
        if (this.serverStillTimeout > 0) {
            if (this.tickCount > 60) {
                --this.serverStillTimeout;
            }
            this.setServerStillTimeout(this.serverStillTimeout);
        }
        if (this.scanPlayerAboveGhast()) {
            this.setServerStillTimeout(10);
        }
    }

    @Override
    public void aiStep() {
        if (!this.level().isClientSide) {
            this.setRequiresPrecisePosition(this.isOnStillTimeout());
        }
        super.aiStep();
        this.continuousHeal();
    }

    private int getHappyGhastRestrictionRadius() {
        if (!this.isBaby() && this.getItemBySlot(EquipmentSlotType.CHEST).isEmpty()) {
            return 64;
        }
        return 32;
    }

    private void checkRestriction() {
        if (this.isLeashed() || this.isVehicle()) {
            return;
        }
        int n = this.getHappyGhastRestrictionRadius();
        if (this.hasHome() && this.getHomePosition().closerThan(this.blockPosition(), n + 16) && n == this.getHomeRadius()) {
            return;
        }
        this.setHomeTo(this.blockPosition(), n);
    }

    private void continuousHeal() {
        ServerWorld serverLevel;
        block5: {
            block4: {
                World level = this.level();
                if (!(level instanceof ServerWorld serverWorld)) break block4;
                serverLevel = serverWorld;
                if (this.isAlive() && this.deathTime == 0 && this.getMaxHealth() != this.getHealth()) break block5;
            }
            return;
        }
        boolean bl = serverLevel.dimensionType().natural() && (this.isInClouds() || serverLevel.isRainingAt(this.blockPosition()));
        if (this.tickCount % (bl ? 20 : 600) == 0) {
            this.heal(1.0f);
        }
    }

    public boolean isInClouds() {
        Optional<Integer> optional = Optional.of(120);
        int n = optional.get();
        if (this.getY() + (double)this.getBbHeight() < (double)n) {
            return false;
        }
        int n2 = n + 4;
        return this.getY() <= (double)n2;
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPacketSender.sendEntityBrain(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_LEASH_HOLDER, false);
        entityData.define(STAYS_STILL, false);
    }

    private void setLeashHolder(boolean bl) {
        this.entityData.set(IS_LEASH_HOLDER, bl);
    }

    public boolean isLeashHolder() {
        return this.entityData.get(IS_LEASH_HOLDER);
    }

    private void syncStayStillFlag() {
        this.entityData.set(STAYS_STILL, this.serverStillTimeout > 0);
    }

    public boolean staysStill() {
        return this.entityData.get(STAYS_STILL);
    }

    private boolean isControlledByRider() {
        return this.isVehicle() && this.getControllingPassenger() instanceof PlayerEntity;
    }

    @Override
    public boolean supportQuadLeashAsHolder() {
        return true;
    }

    @Override
    public Vector3d[] getQuadLeashHolderOffsets() {
        return Leashable.createQuadLeashOffsets(this, -0.03125, 0.4375, 0.46875, 0.03125);
    }

    @Override
    public Vector3d getLeashOffset() {
        return Vector3d.ZERO;
    }

    @Override
    public double leashElasticDistance() {
        return 10.0;
    }

    @Override
    public double leashSnapDistance() {
        return 16.0;
    }

    @Override
    public void onElasticLeashPull() {
        super.onElasticLeashPull();
        this.getMoveControl().setWait();
    }

    @Override
    public void notifyLeashHolder(Leashable leashable) {
        if (leashable.supportQuadLeash()) {
            this.leashHolderTime = 5;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putInt("still_timeout", this.serverStillTimeout);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.setServerStillTimeout(valueInput.getInt("still_timeout"));
    }

    public boolean isOnStillTimeout() {
        return this.staysStill() || this.serverStillTimeout > 0;
    }

    private boolean scanPlayerAboveGhast() {
        AxisAlignedBB aABB = this.getBoundingBox();
        AxisAlignedBB aABB2 = new AxisAlignedBB(aABB.minX - 1.0, aABB.maxY, aABB.minZ - 1.0, aABB.maxX + 1.0, aABB.maxY + aABB.getYsize() / 2.0, aABB.maxZ + 1.0);
        for (PlayerEntity player : this.level().players()) {
            Entity entity;
            if (player.isSpectator() || (entity = player.getRootVehicle()) instanceof HappyGhastEntity || !aABB2.contains(entity.position())) continue;
            return true;
        }
        return false;
    }

    @Override
    protected BodyController createBodyControl() {
        return new HappyGhastBodyRotationControl();
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) {
        if (this.isBaby() || !this.isAlive()) {
            return false;
        }
        if (this.level().isClientSide() && entity instanceof PlayerEntity && entity.position().y >= this.getBoundingBox().maxY) {
            return true;
        }
        if (this.isVehicle() && entity instanceof HappyGhastEntity) {
            return true;
        }
        return this.isOnStillTimeout();
    }

    @Override
    public boolean isFlyingVehicle() {
        return !this.isBaby();
    }


    public static void faceMovementDirection(Mob mob) {
        if (mob.getTarget() == null) {
            Vector3d vec3 = mob.getDeltaMovement();
            mob.yRot = (-((float)MathHelper.atan2(vec3.x, vec3.z)) * 57.295776f);
            mob.yBodyRot = mob.yRot;
        } else {
            LivingEntity livingEntity = mob.getTarget();
            double d = 64.0;
            if (livingEntity.distanceToSqr(mob) < 4096.0) {
                double d2 = livingEntity.getX() - mob.getX();
                double d3 = livingEntity.getZ() - mob.getZ();
                mob.yRot = (-((float)MathHelper.atan2(d2, d3)) * 57.295776f);
                mob.yBodyRot = mob.yRot;
            }
        }
    }


    class HappyGhastLookControl
            extends LookController {
        HappyGhastLookControl() {
            super(HappyGhastEntity.this);
        }

        @Override
        public void tick() {
            if (HappyGhastEntity.this.isRidden()) return;

            if (HappyGhastEntity.this.isOnStillTimeout()) {
                float f = HappyGhastLookControl.wrapDegrees90(HappyGhastEntity.this.getYRot());
                HappyGhastEntity.this.setYRot(HappyGhastEntity.this.getYRot() - f);
                HappyGhastEntity.this.setYHeadRot(HappyGhastEntity.this.getYRot());
                return;
            }
            if (this.lookAtCooldown > 0) {
                --this.lookAtCooldown;
                double d = this.wantedX - HappyGhastEntity.this.getX();
                double d2 = this.wantedZ - HappyGhastEntity.this.getZ();
                HappyGhastEntity.this.setYRot(-((float)MathHelper.atan2(d, d2)) * 57.295776f);
                HappyGhastEntity.this.yHeadRot = HappyGhastEntity.this.yBodyRot = HappyGhastEntity.this.getYRot();
                return;
            }
            HappyGhastEntity.faceMovementDirection(this.mob);
        }

        public static float wrapDegrees90(float f) {
            float f2 = f % 90.0f;
            if (f2 >= 45.0f) {
                f2 -= 90.0f;
            }
            if (f2 < -45.0f) {
                f2 += 90.0f;
            }
            return f2;
        }
    }

    static class BabyFlyingPathNavigation
            extends FlyingPathNavigator {
        public BabyFlyingPathNavigation(HappyGhastEntity happyGhast, World level) {
            super(happyGhast, level);
            this.setCanOpenDoors(false);
            this.setCanFloat(true);
            //this.setRequiredPathLength(48.0f);
        }

        @Override
        protected boolean canMoveDirectly(Vector3d vec3, Vector3d vec32, int x, int y, int z) {
            return BabyFlyingPathNavigation.isClearForMovementBetween(this.mob, vec3, vec32, false);
        }
    }

    class HappyGhastFloatGoal
            extends SwimGoal {
        public HappyGhastFloatGoal() {
            super(HappyGhastEntity.this);
        }

        @Override
        public boolean canUse() {
            return !HappyGhastEntity.this.isOnStillTimeout() && super.canUse();
        }
    }

    class HappyGhastBodyRotationControl
            extends BodyController {
        public HappyGhastBodyRotationControl() {
            super(HappyGhastEntity.this);
        }

        @Override
        public void clientTick() {
            if (HappyGhastEntity.this.isVehicle()) {
                HappyGhastEntity.this.yBodyRot = HappyGhastEntity.this.yHeadRot = HappyGhastEntity.this.getYRot();
            }
            super.clientTick();
        }
    }


}
