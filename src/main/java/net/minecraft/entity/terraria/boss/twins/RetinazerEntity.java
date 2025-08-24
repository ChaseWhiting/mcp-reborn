package net.minecraft.entity.terraria.boss.twins;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.terraria.boss.BossEntity;
import net.minecraft.entity.terraria.boss.BossPhase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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
import org.jetbrains.annotations.Nullable;

public class RetinazerEntity extends BossEntity {
    private BossPhase currentPhase = BossPhase.MAX_HEALTH;
    private int laserCooldown = 0; // Cooldown for firing lasers
    private int chargeCooldown = 0; // Cooldown for charging
    public static final DataParameter<Boolean> PHASE_2 = EntityDataManager.defineId(RetinazerEntity.class, DataSerializers.BOOLEAN);
    private int barrageCooldown = 0; // Cooldown for barrage in phase 2
    public double time = 0;
    private static final double TARGET_DISTANCE = 6.0;  // Desired horizontal distance from player
    private static final double HEIGHT_OFFSET = 7.0;    // Height above the ground or relative to player
    private boolean reverseDiagonal = false;
    private boolean alternateZDirection = false;
    private int switchTimer = 0;                            // Timer for switching diagonal positions
    private int switchInterval = getRandomSwitchInterval(); // Random interval for diagonal switching
    // Tracks which diagonal position the boss should stay on
    public RetinazerEntity(EntityType<? extends RetinazerEntity> entityType, World world) {
        super(entityType, world);
    }


    private static final float ROTATION_SPEED = 14f;  // Adjust rotation speed for smoother or faster turn
    private static final double MOVE_SPEED = 0.7;      // Adjust movement speed toward the player


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
        this.setInPhase2(false);

        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(10);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(70);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getHealthMax());
        switch (difficulty) {
            case EASY -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8);
                this.getAttribute(Attributes.ARMOR).setBaseValue(12);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(3);
            }
            case NORMAL -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(14);
                this.getAttribute(Attributes.ARMOR).setBaseValue(18);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(6);
            }
            case HARD -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(18);
                this.getAttribute(Attributes.ARMOR).setBaseValue(24);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(9);
            }
        }
        this.setHealth(this.getHealthMax());
    }

    public float getHealthMax() {
        float maxHealth;
        {
            maxHealth = switch (this.level.getDifficulty()) {
                case PEACEFUL -> 0.0f;
                case EASY -> 900.0f;
                case NORMAL -> 1100.0f;
                case HARD -> 1400.0f;
            };
        }

        return maxHealth;
    }


    public boolean isInPhase2() {
        return this.entityData.get(PHASE_2);
    }
    public void setInPhase2(boolean b) {
        this.entityData.set(PHASE_2, b);
        this.entityData.clearDirty();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE_2, false);
    }

    private void faceAndMoveTowardPlayer(PlayerEntity targetPlayer) {
        // Calculate direction to the player
        double deltaX = targetPlayer.getX() - this.getX();
        double deltaZ = targetPlayer.getZ() - this.getZ();
        float targetYaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;

        // Smoothly adjust yaw to face the player
        this.yRot = approachAngle(this.yRot, targetYaw, ROTATION_SPEED);

        // Determine the diagonal offset based on the current position
        double offsetX = reverseDiagonal ? TARGET_DISTANCE : -TARGET_DISTANCE;
        double offsetZ = alternateZDirection ? TARGET_DISTANCE : -TARGET_DISTANCE;

        // Calculate the desired target position along the diagonal axis
        double targetX = targetPlayer.getX() + offsetX;
        double targetZ = targetPlayer.getZ() + offsetZ;
        double targetY = targetPlayer.getY() + HEIGHT_OFFSET;

        // Check if the player has crossed to the opposite side
        // If so, reverse the diagonal position to mirror the target distance
        if ((deltaX < 0 && !reverseDiagonal) || (deltaX > 0 && reverseDiagonal)) {
            reverseDiagonal = !reverseDiagonal;
        }

        // Set the target position based on the fixed diagonal distance
        Vector3d targetPosition = new Vector3d(targetX, targetY, targetZ);

        // Calculate movement vector toward the target position and apply smoothing
        Vector3d movementVector = targetPosition.subtract(this.position()).normalize().scale(MOVE_SPEED);
        Vector3d currentMovement = this.getDeltaMovement();
        Vector3d smoothMovement = new Vector3d(
                currentMovement.x + 0.25 * (movementVector.x - currentMovement.x),
                currentMovement.y + 0.25 * (movementVector.y - currentMovement.y),
                currentMovement.z + 0.25 * (movementVector.z - currentMovement.z)
        );

        this.setDeltaMovement(smoothMovement);
    }

    private int getRandomSwitchInterval() {
        return 7 + random.nextInt(7); // Returns a random interval between 7 and 13 seconds
    }

    private float approachAngle(float current, float target, float speed) {
        float delta = wrapAngleTo180(target - current);
        if (delta > speed) delta = speed;
        if (delta < -speed) delta = -speed;
        return current + delta;
    }

    private float wrapAngleTo180(float angle) {
        angle = angle % 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }

    @Override
    public void tick() {
        super.tick();
        PlayerEntity target = (PlayerEntity) this.getTarget();

        if (target == null) return;
        // Handle phase transitions
        updatePhase();

        // Increment the switch timer
        switchTimer++;

        // Check if it's time to switch the diagonal position
        if (switchTimer >= switchInterval * 20) {  // Convert seconds to game ticks
            switchTimer = 0;
            switchInterval = getRandomSwitchInterval(); // Reset with a new random interval
            alternateZDirection = !alternateZDirection; // Flip the Z direction
            this.reverseDiagonal = !reverseDiagonal;
        }

        this.time += 0.05; // Adjust this for faster or slower oscillation

        // Execute behavior based on current phase
        if (!isInPhase2()) {
            // Phase 1: Basic attacks with Eye Lasers and periodic charges
            handlePhase1(target);
        } else {
            // Phase 2: Advanced attacks with Death Lasers and barrages
            handlePhase2(target);
        }
    }

    @Override
    public int getMaxHeadXRot() {
        return 360; // Full vertical rotation
    }

    @Override
    public int getMaxHeadYRot() {
        return 360; // Full horizontal rotation
    }

    @Override
    public int getHeadRotSpeed() {
        return 20; // Fast rotation speed
    }

    private void updatePhase() {
        // Transition to Phase 2 when health is below 40%
        if (!isInPhase2() && this.getHealth() < this.getHealthMax() / 1.5) {
            enterPhase2();
        }
    }

    public boolean advancedMode() {
        return this.level.getDifficulty() == Difficulty.NORMAL || this.level.getDifficulty() == Difficulty.HARD || this.veryHardmode();
    }

    private void enterPhase2() {
        this.setInPhase2(true);
        this.getAttribute(Attributes.ARMOR).setBaseValue(20); // Increase armor in Phase 2
        this.barrageCooldown = 0; // Reset barrage cooldown
        // Add visual/sound effects for transformation
        this.playSound(SoundEvents.ROAR, 3f, 1.0f);
    }

    private void handlePhase1(PlayerEntity target) {
        // Keep Retinazer diagonally above the player
        //positionDiagonallyAbove(target);
        faceAndMoveTowardPlayer(target);
        // Fire lasers periodically
        if (laserCooldown <= 0) {
            fireEyeLaser(target);
            laserCooldown = this.advancedMode() ? 25 : 40; // Faster cooldown in expert mode
        } else {
            laserCooldown--;
        }

        // Charge at the player occasionally
        if (chargeCooldown <= 0) {
            //performCharge(target);
            chargeCooldown = this.advancedMode() ? 80 : 100; // Faster charge in expert mode
        } else {
            chargeCooldown--;
        }
    }

    private void handlePhase2(PlayerEntity target) {
        // Align vertically or horizontally based on attack pattern
        if (barrageCooldown > 0) {
            alignHorizontallyWith(target);
        } else {
            alignVerticallyWith(target);
        }

        // Alternate between continuous firing and burst attacks
        if (barrageCooldown <= 0) {
            fireDeathLaser(target);
            laserCooldown = Math.max(15, laserCooldown - 1); // Shorter intervals as health decreases
        } else {
            performBarrage(target);
            barrageCooldown--;
        }
    }

    private void positionDiagonallyAbove(PlayerEntity target) {
        Vector3d targetPos = target.position();
        double hoverDistance = 10.0;
        double hoverHeight = 5.0;
        Vector3d hoverPos = targetPos.add(hoverDistance, hoverHeight, hoverDistance);
        moveToPosition(hoverPos);
    }

    private void moveToPosition(Vector3d position) {
        Vector3d direction = position.subtract(this.position()).normalize().scale(0.6);
        this.setDeltaMovement(direction);
    }

    private void fireEyeLaser(PlayerEntity target) {
        // Logic to handle firing an Eye Laser, e.g., setting up a projectile
        // Placeholder for projectile shooting logic
    }

    private void performCharge(PlayerEntity target) {
        // Charge towards the player position with increased speed
        Vector3d direction = target.position().subtract(this.position()).normalize().scale(2.0);
        this.setDeltaMovement(direction);
    }

    private void alignHorizontallyWith(PlayerEntity target) {
        Vector3d targetPos = target.position();
        Vector3d hoverPos = new Vector3d(targetPos.x, this.getY(), targetPos.z);
        moveToPosition(hoverPos);
    }

    private void alignVerticallyWith(PlayerEntity target) {
        Vector3d targetPos = target.position();
        Vector3d hoverPos = new Vector3d(this.getX(), targetPos.y + 10, this.getZ());
        moveToPosition(hoverPos);
    }

    private void fireDeathLaser(PlayerEntity target) {
        // Logic to handle firing a Death Laser in Phase 2
        // Placeholder for more powerful and accurate laser
    }

    private void performBarrage(PlayerEntity target) {
        // Rapid-firing multiple weaker lasers
        for (int i = 0; i < 5; i++) {
            fireDeathLaser(target); // Fires a quick barrage of Death Lasers
        }
        barrageCooldown = this.advancedMode() ? 60 : 80; // Reduce time between barrages in expert mode
    }

    @Override
    public Item getTreasureBag() {
        return null; // Replace with actual treasure bag item when available
    }
}
