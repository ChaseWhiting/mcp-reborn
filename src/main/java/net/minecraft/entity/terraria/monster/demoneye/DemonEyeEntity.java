package net.minecraft.entity.terraria.monster.demoneye;

import net.minecraft.block.BlockState;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.terraria.ITerrariaMob;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DemonEyeEntity extends Monster implements ITerrariaMob {
    public AnimationState tails = new AnimationState();

    private int swoopCooldown = 0;
    private int hoverTime = 60;
    private int hoverCounter = 0;
    private boolean turning = false;

    public DemonEyeEntity(EntityType<? extends DemonEyeEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlyingMovementController(this, 180, true);
        this.getNavigation().setCanFloat(true);
        this.setNoGravity(true);
    }

    protected PathNavigator createNavigation(World world) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, world) {
            public boolean isStableDestination(BlockPos pos) {
                for (int i = 0; i <= 15; i++) {
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
        this.hoverDistance = random.nextInt(11) + 6;
        this.hoverHeight = random.nextInt(3) + 1;

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
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2)
                .add(Attributes.ARMOR, 2)
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.FLYING_SPEED, 0.8F)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 38.0D);
    }

    private int hoverDistance; // Preferred distance to start hovering
    private int hoverHeight; // Preferred height above player


    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("HoverDistance", this.hoverDistance); // Save hover distance
        nbt.putInt("HoverHeight", this.hoverHeight); // Save hover height
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        this.hoverDistance = nbt.getInt("HoverDistance"); // Load hover distance
        this.hoverHeight = nbt.getInt("HoverHeight"); // Load hover height
        if (!this.getNavigation().canFloat()) this.getNavigation().setCanFloat(true);
        if (!isNoGravity()) this.setNoGravity(true);
    }



    @Override
    public void tick() {
        super.tick();
        if (this.shouldFlee()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.02, 0));
            if (this.getY() > 120) {
                this.remove();
            }
        }
        // Client-side animations
        if (this.level.isClientSide) {
            this.tails.startIfStopped(this.tickCount);
        }


        // Collision handling
        if (this.horizontalCollision) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.5, 1, -0.5));
            this.turning = true;
        }
        if (this.verticalCollision) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1, -0.5, 1));
        }

        // Ceiling avoidance
        BlockPos abovePos = this.blockPosition().above();
        if (!this.level.isEmptyBlock(abovePos)) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.1, 0));
        }

        // Ground avoidance
        BlockPos belowPos = this.blockPosition().below();
        if (!this.level.isEmptyBlock(belowPos) && this.getY() - belowPos.getY() < 2) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.008, 0));
        }

        // Hover and swoop mechanics
        if (this.swoopCooldown > 0) {
            this.swoopCooldown--;
        }

        PlayerEntity target = (PlayerEntity) this.getTarget();
        if (target != null) {
            this.lookControl.setLookAt(target.position().add(0, 1, 0));
            Vector3d targetPos = target.position().add(0, hoverHeight, 0); // Hover above player by hoverHeight
            Vector3d currentPos = this.position();

            double distanceX = targetPos.x - currentPos.x;
            double distanceY = targetPos.y - currentPos.y;
            double distanceZ = targetPos.z - currentPos.z;
            double distanceToPlayer = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);

            // Custom hover logic
            if (distanceToPlayer > hoverDistance) {
                this.hoverMotion(distanceX, distanceY, distanceZ);
            } else if (swoopCooldown <= 0) {
                // Swoop down
                this.swoopTowardsPlayer(distanceX, distanceY, distanceZ);
                swoopCooldown = 100;
            }
        }
    }

    // Helper method for hovering motion
    private void hoverMotion(double dx, double dy, double dz) {
        double maxSpeed = 0.25;  // Define the maximum speed for hovering
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
        Vector3d velocity = new Vector3d(dx, dy, dz).normalize().scale(0.5); // Swoop speed
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
        private final DemonEyeEntity mob;
        private final World world;
        private int attackCooldown = 20; // Initial cooldown, matching MeleeAttackGoal's attack interval

        public AttackPlayerGoal(DemonEyeEntity eye, World world) {
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
                if (distanceToPlayer > 10.0) {
                    mob.hoverMotion(targetPos.x - currentPos.x, targetPos.y - currentPos.y, targetPos.z - currentPos.z);
                } else if (distanceToPlayer <= 10.0 && mob.swoopCooldown <= 0) {
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
            return (double)(this.mob.getBbWidth() * 1.2F * this.mob.getBbWidth() * 1.2F + p_179512_1_.getBbWidth());
        }

        private void performAttack(LivingEntity target) {
            this.mob.swing(Hand.MAIN_HAND); // Simulate swing animation
            target.hurt(DamageSource.mobAttack(this.mob), (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE));
        }

        private void resetAttackCooldown() {
            this.attackCooldown = 15; // Reset cooldown to 20 ticks (matching MeleeAttackGoal)
        }
    }
}
