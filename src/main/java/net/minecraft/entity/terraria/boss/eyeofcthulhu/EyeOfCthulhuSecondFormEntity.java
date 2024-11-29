package net.minecraft.entity.terraria.boss.eyeofcthulhu;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.terraria.boss.BossPhase;
import net.minecraft.entity.terraria.monster.demoneye.DemonEyeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EyeOfCthulhuSecondFormEntity extends EyeOfCthulhuEntity {
    public static final DataParameter<Boolean> PHASE_2 = EntityDataManager.defineId(EyeOfCthulhuSecondFormEntity.class, DataSerializers.BOOLEAN);
    private int swoopCooldown = 0;
    private int hoverTime = 60;
    private int hoverCounter = 0;
    private boolean turning = false;
    private BossPhase currentPhase = BossPhase.BELOW_MINIMUM;
    private BossPhase oldPhase = BossPhase.BELOW_MINIMUM;

    public EyeOfCthulhuSecondFormEntity(EntityType<? extends EyeOfCthulhuSecondFormEntity> type, World world) {
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

    public boolean isHealthBelowPercentage(double percentage) {
        double currentHealth = getHealth();
        double maxHealth = this.getHealthMax();

        double thresholdHealth = (percentage / 100) * maxHealth;


        return currentHealth < thresholdHealth;
    }


    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
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
        this.setInPhase2(true);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(10);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(70);
        this.hoverDistance = 16;
        this.hoverHeight = 4;
        switch (difficulty) {
            case EASY -> {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(700);
                this.getAttribute(Attributes.ARMOR).setBaseValue(0);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(0);
                this.setHealth(350);
            }
            case NORMAL -> {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1200);
                this.getAttribute(Attributes.ARMOR).setBaseValue(0);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(0);
                this.setHealth(780);
            }
            case HARD -> {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1600);
                this.getAttribute(Attributes.ARMOR).setBaseValue(0);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(0);
                this.setHealth(1040);
            }

        }


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




    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("HoverDistance", this.hoverDistance); // Save hover distance
        nbt.putInt("HoverHeight", this.hoverHeight); // Save hover height
        nbt.putDouble("Armor", this.getAttribute(Attributes.ARMOR).getBaseValue());
        nbt.putDouble("ArmorToughness", this.getAttribute(Attributes.ARMOR_TOUGHNESS).getBaseValue());
        nbt.putString("OldPhase", this.oldPhase.name);
        nbt.putString("CurrentPhase", this.currentPhase.name);
        nbt.putBoolean("Phase2", this.isInPhase2());

        nbt.putInt("CircleCooldown", this.circleCooldown); // Save circle cooldown
        nbt.putInt("CircleDuration", this.circleDuration); // Save circle duration
        nbt.putBoolean("IsCircling", this.isCircling); // Save circling state
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        this.hoverDistance = nbt.getInt("HoverDistance"); // Load hover distance
        this.hoverHeight = nbt.getInt("HoverHeight"); // Load hover height
        this.oldPhase = BossPhase.fromName(nbt.getString("OldPhase"));
        this.currentPhase = BossPhase.fromName(nbt.getString("CurrentPhase"));
        setInPhase2(nbt.getBoolean("Phase2"));
        if (!this.getNavigation().canFloat()) this.getNavigation().setCanFloat(true);
        if (!isNoGravity()) this.setNoGravity(true);
        this.getAttribute(Attributes.ARMOR).setBaseValue(nbt.getDouble("Armor"));
        this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(nbt.getDouble("ArmorToughness"));

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

    @Override
    public void tick() {
        super.tick();

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
            // Circling behavior during the second phase
            if (isCircling) {
                // Set oscillation ranges for circling
                double minRadius = 5.0;
                double maxRadius = 7.6;
                double minSpeed = 0.2;
                double maxSpeed = 0.32;

                // Calculate radius and speed for circling using sine wave oscillation
                double circleRadius = minRadius + (maxRadius - minRadius) * 0.5 * (1 + Math.sin(time));
                double circleSpeed = minSpeed + (maxSpeed - minSpeed) * 0.5 * (1 + Math.sin(time));

                // Circle around the player
                circleAroundPlayer(target, circleRadius, circleSpeed);

                // Increment time for continuous oscillation
                time += 0.1;

                // Decrease circling duration
                circleDuration--;

                // Stop circling if duration is reached
                if (circleDuration <= 0) {
                    isCircling = false;
                    circleCooldown = 10;      // Cooldown before circling again
                    circleDuration = 70;      // Reset circle duration
                }
            }

            // Aggressive dashing behavior
            if (dashCooldown <= 0 && circleCooldown <= 0) {
                int maxDashes = this.timeSincePlayerTakenDamage > 10 * 20 ? 7 : 4;
                if (this.advancedMode() && chainDashCounter < maxDashes) {
                    // Perform chain dashes in Expert mode
                    int cooldown = timeSincePlayerTakenDamage > 12 * 20 ? 10 : 15;
                    performDash(target.position().add(0, 1.4, 0), calculateDashSpeed(1.5, this.getHealth(), this.level.getDifficulty()));
                    dashCooldown = cooldown;           // Short cooldown for chain dashes
                    chainDashCounter++;
                    playSound(SoundEvents.EXPERT_ROAR, 2.5f, 1.0f);

                } else {
                    // Single dash in normal mode or after chain dashes
                    performDash(target.position().add(0, 0.7, 0), 1.5);
                    dashCooldown = 40;            // Longer cooldown after chain dash sets
                    chainDashCounter = 0;         // Reset chain dash counter after completing dashes
                    playSound(advancedMode() ? SoundEvents.EXPERT_ROAR : SoundEvents.ROAR, 2.5f, 1.0f);
                }

                // Start circling cooldown after each dash or chain dash sequence
                circleCooldown = 10;
                isCircling = false;               // Stop circling after dashing
            } else if (dashCooldown > 0) {
                // Count down dash cooldown
                dashCooldown--;
            }

            // Enable circling after dash cooldown and circling cooldown are complete
            if (dashCooldown > 0 && circleCooldown <= 0 && !isCircling) {
                isCircling = true;
            }

            // Countdown for circleCooldown to enable circling after dashing
            if (circleCooldown > 0) {
                circleCooldown--;
            }

            // Reset counters if both cooldowns and circling are complete to prevent stall
            if (dashCooldown <= 0 && circleCooldown <= 0 && !isCircling) {
                chainDashCounter = 0;
                dashCooldown = 40;    // Reinitialize dash cooldown to ensure continuous activity
            }
        }
    }

    int getMaxDashesBasedOnHealth() {
        if (isHealthBelowPercentage(10)) return 8;
        if (isHealthBelowPercentage(25)) return 7;
        if (isHealthBelowPercentage(45)) return 6;
        if (isHealthBelowPercentage(65)) return 5;
        return 4;
    }

    public void getAttackDamageForHealth() {
        double damage = switch (this.level.getDifficulty()) {
            case PEACEFUL -> 4;
            case EASY -> 12;
            case NORMAL -> 16;
            case HARD -> 22;
        };


        if (this.isHealthBelowPercentage(5)) {
            damage *= 1.65;
        } else if (this.isHealthBelowPercentage(30)) {
            damage *= 1.45;
        } else if (this.isHealthBelowPercentage(55)) {
            damage *= 1.25;
        }

        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
    }

    /**
     * Calculates the dash speed based on health lost and difficulty level.
     * @param baseSpeed The initial dash speed (e.g., 1.5).
     * @param currentHealth The current health of the mob.
     * @param difficulty The difficulty level (EASY, NORMAL, HARD).
     * @return The calculated dash speed after considering health lost.
     */
    public static double calculateDashSpeed(double baseSpeed, double currentHealth, Difficulty difficulty) {
        double maxHealth;

        // Set maxHealth based on the difficulty level
        switch (difficulty) {
            case EASY:
                maxHealth = 350.0;
                break;
            case NORMAL:
                maxHealth = 780.0;
                break;
            case HARD:
                maxHealth = 1040.0;
                break;
            default:
                throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        }

        // Calculate percentage of health lost
        double healthLostPercent = ((maxHealth - currentHealth) / maxHealth) * 100;

        // Calculate increments of 10% lost health
        int tenPercentIncrements = (int) (healthLostPercent / 5);

        // Calculate total increment to add to the base speed (0.06 per 10% of health lost)
        double totalIncrement = tenPercentIncrements * 0.06;

        // Return the final dash speed
        return baseSpeed + totalIncrement;
    }

    // Spawn Servants
    public void spawnServants(int count) {
        List<DemonEyeEntity> demonEyeEntityList = level.getEntitiesOfClass(DemonEyeEntity.class, this.getBoundingBox().inflate(30));
        if (demonEyeEntityList.size() >= 7) return;
        for (int i = 0; i < count; i++) {
            DemonEyeEntity servant = new DemonEyeEntity(EntityType.DEMON_EYE, this.level);
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


    // Helper method for hovering motion
    private void hoverMotion(double dx, double dy, double dz) {
        double maxSpeed = 0.5;  // Define the maximum speed for hovering
        Vector3d velocity = this.getDeltaMovement();

        // Calculate the direction vector and normalize it
        Vector3d direction = new Vector3d(dx, dy, dz).normalize();

        // Adjust X and Z velocities based on the normalized direction and max speed
        double targetX = direction.x * maxSpeed;
        double targetZ = direction.z * maxSpeed;

        // Smoothly adjust the current velocity towards the target velocity in the X and Z directions
        velocity = new Vector3d(
                velocity.x + (targetX - velocity.x) * 0.1,
                velocity.y,  // Keep Y velocity separate for vertical adjustments
                velocity.z + (targetZ - velocity.z) * 0.1
        );

        // Adjust Y velocity separately to control floating motion
        if (dy > 0 && velocity.y < maxSpeed) {
            velocity = velocity.add(0, 0.03, 0);
        } else if (dy < 0 && velocity.y > -maxSpeed) {
            velocity = velocity.subtract(0, 0.03, 0);
        }

        this.setDeltaMovement(velocity);
    }


    // Helper method for swooping towards player
    private void swoopTowardsPlayer(double dx, double dy, double dz) {
        Vector3d velocity = new Vector3d(dx, dy, dz).normalize().scale(1); // Swoop speed
        this.setDeltaMovement(velocity);
        this.turning = true;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 0.83f));
        this.goalSelector.addGoal(1, new AttackPlayerGoal(this, this.level));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }
}
