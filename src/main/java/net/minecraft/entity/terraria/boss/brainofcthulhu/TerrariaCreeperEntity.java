package net.minecraft.entity.terraria.boss.brainofcthulhu;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.terraria.boss.eyeofcthulhu.EyeOfCthulhuEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class TerrariaCreeperEntity extends Monster {
    // Cooldowns and circling variables
    public int circleCooldown = 0;   // Cooldown between circling phases
    public int circleDuration = 160; // Duration of the circling phase (8 seconds)
    public boolean isCircling = false;
    public double time = 0; // Time variable for oscillation

    public TerrariaCreeperEntity(EntityType<? extends TerrariaCreeperEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlyingMovementController(this, 360, true);
        this.setNoGravity(true);
    }

    @Override
    protected PathNavigator createNavigation(World world) {
        FlyingPathNavigator navigator = new FlyingPathNavigator(this, world);
        navigator.setCanOpenDoors(false);
        navigator.setCanFloat(true);
        return navigator;
    }

    @Override
    public void tick() {
        super.tick();

        // Ensure we're targeting the player
        PlayerEntity target = (PlayerEntity) this.getTarget();
        if (target != null) {
            double distanceToPlayer = this.distanceTo(target);

            // Hovering behavior around the player, maintaining a specific distance
            if (circleCooldown <= 0 && circleDuration > 0) {
                isCircling = true;
                circleAroundPlayer(target, 3.5, 0.2); // Hover at 3.5 blocks radius, speed 0.2

                // Countdown circling duration
                circleDuration--;
                if (circleDuration <= 0) {
                    isCircling = false;
                    circleCooldown = 100; // Reset cooldown (5 seconds between circling)
                    circleDuration = 160; // Reset duration for the next circling phase
                }
            } else if (circleCooldown > 0) {
                circleCooldown--; // Countdown cooldown between circling phases
            }

            // Swoop in to attack periodically
            if (distanceToPlayer > 1.0 && this.tickCount % 100 == 0) { // Swoop every 5 seconds
                Vector3d swoopPosition = new Vector3d(target.getX(), target.getY() + 1, target.getZ());
                swoopTowardsPlayer(swoopPosition);
            }
        }
    }

    // Circle around player method based on oscillating movement
    public void circleAroundPlayer(PlayerEntity player, double radius, double speed) {
        Vector3d playerPos = player.position();
        double angleIncrement = speed / radius;
        double currentAngle = (this.tickCount * angleIncrement) % (2 * Math.PI);

        // Calculate target X, Z for circular movement
        double targetX = playerPos.x + radius * Math.cos(currentAngle);
        double targetZ = playerPos.z + radius * Math.sin(currentAngle);
        double targetY = playerPos.y + 3; // Hover 3 blocks above the player

        Vector3d targetPos = new Vector3d(targetX, targetY, targetZ);
        Vector3d direction = targetPos.subtract(this.position()).normalize().scale(speed);
        this.setDeltaMovement(direction);
    }

    // Swooping toward the player with adjusted Y-level for attacks
    private void swoopTowardsPlayer(Vector3d targetPos) {
        Vector3d direction = targetPos.subtract(this.position()).normalize().scale(0.8); // Adjust swoop speed as needed
        this.setDeltaMovement(direction);
    }

    // Helper method to ensure AI stays stable when no target is available
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }



    public static class AttackPlayerGoal extends Goal {
        private final TerrariaCreeperEntity mob;
        private final World world;
        private int attackCooldown = 20; // Initial cooldown, matching MeleeAttackGoal's attack interval

        public AttackPlayerGoal(TerrariaCreeperEntity eye, World world) {
            this.mob = eye;
            this.world = world;
        }

        @Override
        public boolean canUse() {
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
            return this.mob.getTarget() != null && this.mob.distanceToSqr(this.mob.getTarget()) < 38 * 38;
        }

        protected double getAttackReachSqr(LivingEntity p_179512_1_) {
            // Reduce the reach factor multiplier for closer range, 1.2 is a close range example
            return (double)(this.mob.getBbWidth() * 0.5F * this.mob.getBbWidth() * 0.5F + p_179512_1_.getBbWidth());
        }

        private void performAttack(LivingEntity target) {


            this.mob.swing(Hand.MAIN_HAND); // Simulate swing animation
            target.hurt(DamageSource.mobAttack(this.mob), (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE));
        }

        private void resetAttackCooldown() {
            this.attackCooldown = 15; // Reset cooldown to 20 ticks (matching MeleeAttackGoal)
        }
    }





    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2f)
                .add(Attributes.ARMOR, 4)
                .add(Attributes.ARMOR_TOUGHNESS, 1)
                .add(Attributes.MAX_HEALTH, 16)
                .add(Attributes.FLYING_SPEED, 0.8F)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 4)
                .add(Attributes.FOLLOW_RANGE, 32);
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
}
