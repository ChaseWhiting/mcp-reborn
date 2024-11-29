package net.minecraft.entity.terraria.boss.eyeofcthulhu;

import net.minecraft.block.BlockState;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.terraria.boss.BossEntity;
import net.minecraft.entity.terraria.boss.BossPhase;
import net.minecraft.entity.terraria.monster.demoneye.DemonEyeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EyeOfCthulhuEntity extends BossEntity {
    public AnimationState tails = new AnimationState();
    public static final DataParameter<Boolean> PHASE_2 = EntityDataManager.defineId(EyeOfCthulhuEntity.class, DataSerializers.BOOLEAN);
    private int swoopCooldown = 0;
    private int hoverTime = 60;
    private int hoverCounter = 0;
    private boolean turning = false;
    private BossPhase currentPhase = BossPhase.MAX_HEALTH;
    private BossPhase oldPhase;
    private final ServerBossInfo bossEvent = (ServerBossInfo) (new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS)).setPlayBossMusic(true).setDarkenScreen(true);

    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setPercent(this.getHealth() / this.getHealthMax());
    }

    public float getHealthMax() {
        float maxHealth = 0;
        if (this instanceof EyeOfCthulhuSecondFormEntity) {
            maxHealth = switch (this.level.getDifficulty()) {
                case PEACEFUL -> 0.0f;
                case EASY -> 350.0f;
                case NORMAL -> 780.0f;
                case HARD -> 1040.0f;
            };
        } else {
            maxHealth = switch (this.level.getDifficulty()) {
                case PEACEFUL -> 0.0f;
                case EASY -> 700.0f;
                case NORMAL -> 1200.0f;
                case HARD -> 1600.0f;
            };
        }

        return maxHealth;
    }



    public void setCustomName(@javax.annotation.Nullable ITextComponent p_200203_1_) {
        super.setCustomName(p_200203_1_);
        this.bossEvent.setName(this.getDisplayName());
    }

    public void startSeenByPlayer(ServerPlayerEntity p_184178_1_) {
        super.startSeenByPlayer(p_184178_1_);
        this.bossEvent.addPlayer(p_184178_1_);
    }

    public void stopSeenByPlayer(ServerPlayerEntity p_184203_1_) {
        super.stopSeenByPlayer(p_184203_1_);
        this.bossEvent.removePlayer(p_184203_1_);
    }

    private int targetPlayerId;

    public EyeOfCthulhuEntity(EntityType<? extends EyeOfCthulhuEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlyingMovementController(this, 360, true);
        this.getNavigation().setCanFloat(true);
        this.setNoGravity(true);
        this.setInPhase2(false);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE_2, false);
    }

    protected PathNavigator createNavigation(World world) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, world) {
            public boolean isStableDestination(BlockPos pos) {
                for (int i = 0; i <= 10; i++) {
                    BlockPos checkPos = pos.below(i);
                    if (!this.level.getBlockState(checkPos).isAir()) {
                        return true;
                    }
                }
                return false;
            }

            public void tick() {
                super.tick();
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    public boolean shouldFlee() {
        long time = this.level.getDayTime() % 24000;
        return time >= 20 && time <= 14000;
    }

    public SoundEvent getDeathSound() {
        return SoundEvents.NPC_KILLED_1;
    }

    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.NPC_HURT_1;
    }



    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        if (this instanceof EyeOfCthulhuSecondFormEntity) return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
        this.setInPhase2(false);
        this.setDefaults(p_213386_2_.getDifficulty());
        this.getNavigation().setCanFloat(true);
        this.setNoGravity(true);
        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
        return false;
    }

    protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.KNOCKBACK_RESISTANCE)
                .add(Attributes.ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS)
                .add(Attributes.MAX_HEALTH)
                .add(Attributes.FLYING_SPEED, 0.8F)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.FOLLOW_RANGE);
    }

    public void setDefaults(Difficulty difficulty) {
        this.setInPhase2(false);

        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(10);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(70);
        this.hoverDistance = 16;
        this.hoverHeight = 4;
        switch (difficulty) {
            case EASY -> {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(700);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8);
                this.getAttribute(Attributes.ARMOR).setBaseValue(12);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(3);
            }
            case NORMAL -> {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1200);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(14);
                this.getAttribute(Attributes.ARMOR).setBaseValue(18);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(6);
            }
            case HARD -> {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1600);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(18);
                this.getAttribute(Attributes.ARMOR).setBaseValue(24);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(9);
            }
        }
        this.setHealth(this.getMaxHealth());
    }

    public void updateOnPhaseSwitched() {
        this.setInPhase2(true);
        Difficulty difficulty = this.level.getDifficulty();
        this.getAttribute(Attributes.ARMOR).setBaseValue(0);
        this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(0);
        this.hoverDistance = 45;
        this.hoverHeight = 2;
        switch (difficulty) {
            case EASY -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(12);
            case NORMAL -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(16);
            case HARD -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(22);
        }
    }

    int hoverDistance; // Preferred distance to start hovering
    int hoverHeight; // Preferred height above player
    // Add new fields
    public int servantSpawnCooldown = 0; // Controls spawning of servants
    public int dashCooldown = 0; // Controls timing of dashes
    public int chainDashCounter = 0; // Used in Expert chain dashes
    public int timeSincePlayerTakenDamage = 0;


    // Define variables for cooldown and duration
    public int circleCooldown = 0;   // Cooldown between circling phases
    public int circleDuration = 160; // Duration of the circling phase (e.g., 8 seconds)
    public boolean isCircling = false;
    public double time = 0;

    @Override
    public Item getTreasureBag() {
        return Items.EYE_OF_CTHULHU_BAG;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("DamageTimer", this.timeSincePlayerTakenDamage); // Save hover distance
        if (this instanceof EyeOfCthulhuSecondFormEntity) return;

        nbt.putInt("HoverDistance", this.hoverDistance); // Save hover distance
        nbt.putInt("HoverHeight", this.hoverHeight); // Save hover height
        nbt.putDouble("Armor", this.getAttribute(Attributes.ARMOR).getBaseValue());
        nbt.putDouble("ArmorToughness", this.getAttribute(Attributes.ARMOR_TOUGHNESS).getBaseValue());
        nbt.putString("OldPhase", this.oldPhase.name);
        nbt.putString("CurrentPhase", this.currentPhase.name);
        nbt.putBoolean("Phase2", this.isInPhase2());

        // Save circling variables
        nbt.putInt("CircleCooldown", this.circleCooldown); // Save circle cooldown
        nbt.putInt("CircleDuration", this.circleDuration); // Save circle duration
        nbt.putBoolean("IsCircling", this.isCircling); // Save circling state
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
        this.timeSincePlayerTakenDamage = nbt.getInt("DamageTimer");
        if (this instanceof EyeOfCthulhuSecondFormEntity) return;
        this.hoverDistance = nbt.getInt("HoverDistance"); // Load hover distance
        this.hoverHeight = nbt.getInt("HoverHeight"); // Load hover height
        this.oldPhase = BossPhase.fromName(nbt.getString("OldPhase"));
        this.currentPhase = BossPhase.fromName(nbt.getString("CurrentPhase"));
        setInPhase2(nbt.getBoolean("Phase2"));
        if (!this.getNavigation().canFloat()) this.getNavigation().setCanFloat(true);
        if (!isNoGravity()) this.setNoGravity(true);
        this.getAttribute(Attributes.ARMOR).setBaseValue(nbt.getDouble("Armor"));
        this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(nbt.getDouble("ArmorToughness"));

        // Load circling variables
        this.circleCooldown = nbt.getInt("CircleCooldown"); // Load circle cooldown
        this.circleDuration = nbt.getInt("CircleDuration"); // Load circle duration
        this.isCircling = nbt.getBoolean("IsCircling"); // Load circling state
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isInPhase2() {
        return this.entityData.get(PHASE_2) && this.currentPhase == BossPhase.BELOW_MINIMUM;
    }
    @OnlyIn(Dist.CLIENT)
    public void setInPhase2(boolean b) {
        this.entityData.set(PHASE_2, b);
        this.entityData.clearDirty();
    }

    private boolean animationStarted = false;  // New flag for animation start

    public boolean isInWall() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getTarget() != null) timeSincePlayerTakenDamage++;
        boolean isNearGround = false;
        BlockPos mobPosition = this.blockPosition();
        for (int yOffset = -1; yOffset >= -2; yOffset--) {
            BlockPos checkPos = mobPosition.offset(0, yOffset, 0);
            if (this.level.getBlockState(checkPos).getMaterial().isSolid()) {
                isNearGround = true;
                break;
            }
        }
        this.noPhysics = this.getTarget() == null ? false : !canSee(getTarget()) || !isNearGround || this.distanceTo(this.getTarget()) > 20 || this.timeSincePlayerTakenDamage > 15 * 20;
        if (this instanceof EyeOfCthulhuSecondFormEntity) return;

        if (this.isInPhase2() && this.level instanceof ServerWorld) {
            EyeOfCthulhuSecondFormEntity eye = this.convertTo(EntityType.EYE_OF_CTHULHU_SECOND_FORM, false);
            eye.updateOnPhaseSwitched();
            ListNBT uuidList = new ListNBT();
            for (UUID uuid : playersWhoDealtDamage) {
                CompoundNBT uuidNBT = new CompoundNBT();
                uuidNBT.putUUID("UUID", uuid);
                uuidList.add(uuidNBT);
            }
            CompoundNBT nbt = new CompoundNBT();
            nbt.put("PlayersWhoDealtDamage", uuidList);
            eye.addAdditionalSaveData(nbt);
            eye.playSound(SoundEvents.ROAR, 3f, 1.0f);
            eye.finalizeSpawn((IServerWorld) this.level, new DifficultyInstance(this.level.getDifficulty(), 1, 1, 1, this.veryHardmode()), SpawnReason.CONVERSION, null, this.saveWithoutId(new CompoundNBT()));
            return;
        }

        this.currentPhase = BossPhase.getCurrentPhase(this, this.level.getDifficulty());
        if (this.oldPhase != this.currentPhase) {
            this.updateOnPhaseSwitched();
        }
        this.oldPhase = this.currentPhase;

        // Manage fleeing
        if (this.shouldFlee()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.02, 0));
            if (this.getY() > 120) {
                this.remove();
            }
            return;
        }

        // Manage servant spawn timer
        if (servantSpawnCooldown > 0) {
            servantSpawnCooldown--;
        }



        // Dash mechanics based on phase
        PlayerEntity target = (PlayerEntity) this.getTarget();
        if (target != null) {
            // Spawn servants in first phase
            if (this.currentPhase == BossPhase.MAX_HEALTH && servantSpawnCooldown <= 0) {
                spawnServants(2 + this.random.nextInt(3)); // Spawn 3-4 servants
                servantSpawnCooldown = 200; // Reset spawn cooldown
            }

            // Dash logic: dash when cooldown is zero and start circling cooldown
            if (dashCooldown <= 0 && circleCooldown <= 0) {
                performDash(target.position(), 1.5);
                this.playSound(SoundEvents.ROAR, 2f, 1.0f);
                dashCooldown = 55;           // Reset dash cooldown
                circleCooldown = 12;         // Set circling cooldown to 2 seconds (adjust as needed)
                isCircling = false;          // Stop circling after dash
            } else if (dashCooldown > 0) {
                dashCooldown--;              // Count down dash cooldown
            }

            // Circling logic: circle only if dash cooldown is active and circle cooldown is complete
            if (dashCooldown > 0 && circleCooldown <= 0) {
                // Start circling
                isCircling = true;

                // Set oscillation ranges for circling
                double minRadius = 4.0;
                double maxRadius = 6.6;
                double minSpeed = 0.17;
                double maxSpeed = 0.28;

                // Calculate radius and speed for circling using sine wave oscillation
                double circleRadius = minRadius + (maxRadius - minRadius) * 0.5 * (1 + Math.sin(time));
                double circleSpeed = minSpeed + (maxSpeed - minSpeed) * 0.5 * (1 + Math.sin(time));

                // Circle around the player
                circleAroundPlayer(target, circleRadius, circleSpeed);

                // Increment time for continuous oscillation
                time += 0.05;

                // Decrease circling duration
                circleDuration--;

                // Stop circling if duration is reached
                if (circleDuration <= 0) {
                    isCircling = false;
                    circleCooldown = 40;      // Set cooldown before it can circle again after 8 seconds
                    circleDuration = 160;     // Reset circle duration
                }
            }

            // Countdown for circleCooldown to enable circling after dashing
            if (circleCooldown > 0) {
                circleCooldown--;
            }
        }
    }

    public void circleAroundPlayer(PlayerEntity player, double radius, double speed) {
        Vector3d playerPos = player.position();
        double angleIncrement = speed / radius;
        double currentAngle = (this.tickCount * angleIncrement) % (2 * Math.PI);

        // Base target X and Z for circular movement
        double targetX = playerPos.x + radius * Math.cos(currentAngle);
        double targetZ = playerPos.z + radius * Math.sin(currentAngle);

        // Start with the Y level at a desired hover height above the player
        double targetY = playerPos.y + 5; // Hover 5 blocks above player

        // Adjust for obstacles
        targetY = adjustHeightForObstacles(new BlockPos(targetX, targetY, targetZ), targetY);

        // Calculate the final direction vector for movement
        Vector3d targetPos = new Vector3d(targetX, targetY, targetZ);
        Vector3d currentPos = this.position();
        Vector3d direction = targetPos.subtract(currentPos).normalize().scale(speed);

        // Explicitly set the Y-component to help ensure elevation
        direction = new Vector3d(direction.x, (targetY - currentPos.y) * 0.1, direction.z);
        this.setDeltaMovement(direction);
    }


    public double adjustHeightForObstacles(BlockPos targetPos, double baseY) {
        World world = this.level;
        int maxHeightOffset = 6; // Max blocks above/below to check for obstacles
        int hoverHeight = 5; // Desired hover height above ground/player

        // Check upwards for obstacles and adjust Y if necessary
        for (int i = 0; i <= maxHeightOffset; i++) {
            BlockPos checkPosUp = targetPos.above(i);
            BlockPos checkPosDown = targetPos.below(i);

            if (!world.getBlockState(checkPosUp).isAir()) {
                return checkPosUp.getY() + hoverHeight; // Move up if obstacle detected above
            } else if (!world.getBlockState(checkPosDown).isAir()) {
                return checkPosDown.getY() + hoverHeight; // Move up if obstacle detected below
            }
        }
        return baseY; // No obstacles detected; return default hover height
    }



    // Spawn Servants
    public void spawnServants(int count) {
        List<DemonEyeEntity> demonEyeEntityList = level.getEntitiesOfClass(DemonEyeEntity.class, this.getBoundingBox().inflate(30));
        if (demonEyeEntityList.size() >= 7) return;
        for (int i = 0; i < count; i++) {
            DemonEyeEntity servant = new DemonEyeEntity(EntityType.DEMON_EYE, this.level);
            servant.finalizeSpawn((IServerWorld) level, new DifficultyInstance(this.level.getDifficulty(), 0, 0, 0, this.veryHardmode()), SpawnReason.MOB_SUMMONED, null, null);
            servant.setPos(this.getX() + this.random.nextDouble() - 0.5, this.getY(), this.getZ() + this.random.nextDouble() - 0.5);
            this.level.addFreshEntity(servant);
        }
    }

    // Dash helper method
    public void performDash(Vector3d targetPos, double speed) {
        Vector3d currentPos = this.position();
        Vector3d direction = targetPos.subtract(currentPos).normalize().scale(speed);
        this.setDeltaMovement(direction);
        this.turning = true; // Update turning state for visual effect
    }

    public boolean advancedMode() {
        return this.level.getDifficulty() == Difficulty.NORMAL || this.level.getDifficulty() == Difficulty.HARD || this.veryHardmode();
    }


    // Helper method for swooping towards player
    private void swoopTowardsPlayer(double dx, double dy, double dz) {
        Vector3d velocity = new Vector3d(dx, dy, dz).normalize().scale(1); // Swoop speed
        this.setDeltaMovement(velocity);
        this.turning = true;
    }

    @Override
    public int getMaxHeadXRot() {
        return 180; // Full vertical rotation
    }

    @Override
    public int getMaxHeadYRot() {
        return 360; // Full horizontal rotation
    }

    @Override
    public int getHeadRotSpeed() {
        return 20; // Fast rotation speed
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 0.83f));
        this.goalSelector.addGoal(1, new AttackPlayerGoal(this, this.level));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static class AttackPlayerGoal extends Goal {
        private final EyeOfCthulhuEntity mob;
        private final World world;
        private int attackCooldown = 20; // Initial cooldown, matching MeleeAttackGoal's attack interval

        public AttackPlayerGoal(EyeOfCthulhuEntity eye, World world) {
            this.mob = eye;
            this.world = world;
        }

        @Override
        public boolean canUse() {
            if (mob.shouldFlee()) return false;
            PlayerEntity player = this.world.getNearestSurvivalPlayer(this.mob, 38.0);
            if (player != null) {
                this.mob.setTarget(player);
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            PlayerEntity target = (PlayerEntity) mob.getTarget();
            if (target != null) {
                mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
        }

        @Override
        public void tick() {
            PlayerEntity target = (PlayerEntity) mob.getTarget();
            if (target != null) {
                Vector3d targetPos = target.position();
                Vector3d currentPos = mob.position();
                double distanceToPlayer = currentPos.distanceTo(targetPos);

                mob.setNoGravity(true); // Enable floating behavior
                mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

                // Hover or swoop towards the player
                if (distanceToPlayer <= 10.0 && mob.swoopCooldown <= 0) {
                    mob.swoopTowardsPlayer(targetPos.x - currentPos.x, targetPos.y - currentPos.y, targetPos.z - currentPos.z);
                    mob.swoopCooldown = 100; // Reset swoop cooldown
                }

                // Check if in range to attack
                if (distanceToPlayer <= this.getAttackReachSqr(target) && this.attackCooldown <= 0) {
                    this.performAttack(target);
                    this.resetAttackCooldown();
                } else {
                    this.attackCooldown--; // Decrement attack cooldown each tick
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            if (mob.shouldFlee()) return false;
            return this.mob.getTarget() != null && this.mob.distanceToSqr(this.mob.getTarget()) < 38 * 38;
        }

        protected double getAttackReachSqr(LivingEntity p_179512_1_) {
            // Reduce the reach factor multiplier for closer range, 1.2 is a close range example
            if (this.mob instanceof EyeOfCthulhuSecondFormEntity) {
                return (double)(this.mob.getBbWidth() * 0.7F * this.mob.getBbWidth() * 0.7F + p_179512_1_.getBbWidth());
            }
            return (double)(this.mob.getBbWidth() * 0.5F * this.mob.getBbWidth() * 0.5F + p_179512_1_.getBbWidth());
        }

        private void performAttack(LivingEntity target) {
            this.mob.swing(Hand.MAIN_HAND); // Simulate swing animation
            target.hurt(DamageSource.mobAttack(this.mob), (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE));
            mob.timeSincePlayerTakenDamage = 0;
        }

        private void resetAttackCooldown() {
            this.attackCooldown = 15; // Reset cooldown to 20 ticks (matching MeleeAttackGoal)
        }
    }

}
