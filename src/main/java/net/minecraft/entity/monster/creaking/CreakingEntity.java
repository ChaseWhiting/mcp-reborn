package net.minecraft.entity.monster.creaking;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;

import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.EnumSet;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;

public class CreakingEntity extends Monster {
    // Entity Data Keys
    private static final DataParameter<Boolean> CAN_MOVE = EntityDataManager.defineId(CreakingEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_ACTIVE = EntityDataManager.defineId(CreakingEntity.class, DataSerializers.BOOLEAN);
    @OnlyIn(Dist.CLIENT)
    public int getAttackAnimationRemainingTicks() {
        return attackAnimationRemainingTicks;
    }
    @OnlyIn(Dist.CLIENT)
    public void setAttackAnimationRemainingTicks(int attackAnimationRemainingTicks) {
        this.attackAnimationRemainingTicks = attackAnimationRemainingTicks;
    }

    // Attack properties
    @OnlyIn(Dist.CLIENT)
    public int attackAnimationRemainingTicks;
    private static final int ATTACK_ANIMATION_DURATION = 8;
    public int maxAttackAnimationTicks = 8;        // Total number of ticks for the full attack animation
    private PlayerEntity creakingTarget = null;



    private float headLerpProgress = 0.0f;
    private float bodyLerpProgress = 0.0f;
    private float rightArmLerpProgress = 0.0f;
    private float leftArmLerpProgress = 0.0f;
    private float rightLegLerpProgress = 0.0f;
    private float leftLegLerpProgress = 0.0f;

    public float getHeadLerpProgress() {
        return headLerpProgress;
    }

    public void setHeadLerpProgress(float headLerpProgress) {
        this.headLerpProgress = headLerpProgress;
    }

    public float getBodyLerpProgress() {
        return bodyLerpProgress;
    }

    public void setBodyLerpProgress(float bodyLerpProgress) {
        this.bodyLerpProgress = bodyLerpProgress;
    }

    public float getRightArmLerpProgress() {
        return rightArmLerpProgress;
    }

    public void setRightArmLerpProgress(float rightArmLerpProgress) {
        this.rightArmLerpProgress = rightArmLerpProgress;
    }

    public float getLeftArmLerpProgress() {
        return leftArmLerpProgress;
    }

    public void setLeftArmLerpProgress(float leftArmLerpProgress) {
        this.leftArmLerpProgress = leftArmLerpProgress;
    }

    public float getRightLegLerpProgress() {
        return rightLegLerpProgress;
    }

    public void setRightLegLerpProgress(float rightLegLerpProgress) {
        this.rightLegLerpProgress = rightLegLerpProgress;
    }

    public float getLeftLegLerpProgress() {
        return leftLegLerpProgress;
    }

    public void setLeftLegLerpProgress(float leftLegLerpProgress) {
        this.leftLegLerpProgress = leftLegLerpProgress;
    }

    // Call this function in the entity's tick to update the lerping values over time
    public void updateLerpProgress() {
        this.headLerpProgress = (this.headLerpProgress + 0.05f) % 1.0f;
        this.bodyLerpProgress = (this.bodyLerpProgress + 0.05f) % 1.0f;
        this.rightArmLerpProgress = (this.rightArmLerpProgress + 0.05f) % 1.0f;
        this.leftArmLerpProgress = (this.leftArmLerpProgress + 0.05f) % 1.0f;
        this.rightLegLerpProgress = (this.rightLegLerpProgress + 0.05f) % 1.0f;
        this.leftLegLerpProgress = (this.leftLegLerpProgress + 0.05f) % 1.0f;
    }


    public CreakingEntity(EntityType<? extends Monster> type, World world) {
        super(type, world);
        this.xpReward = 5;

        // Custom entity movement, jump, and look control logic
        this.lookControl = new CreakingLookController(this);
        this.moveControl = new CreakingMoveController(this);
        this.jumpControl = new CreakingJumpController(this);
    }

    // Define entity data
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CAN_MOVE, true);
        this.entityData.define(IS_ACTIVE, false);
    }

    // Setup goals (replacing brain system with goals)
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CreakingAttackGoal(this, 0.93D));
        //this.goalSelector.addGoal(3, new RandomWalkingGoal(this, 0.6D));
    }

    // Attribute definition for the entity
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    // Check if the entity can move
    public boolean canMove() {
        return this.entityData.get(CAN_MOVE);
    }

    // Check if the entity is active
    public boolean isActive() {
        return this.entityData.get(IS_ACTIVE);
    }

    public void setIsActive(boolean isActive) {
        this.entityData.set(IS_ACTIVE, isActive);
    }

    // Custom AI step logic to control movement and behavior
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackAnimationRemainingTicks > 0) {
            --this.attackAnimationRemainingTicks;
        }

        if (!this.level.isClientSide) {
            boolean canMove = this.canMove();
            boolean shouldMove = this.checkCanMove();
            if (shouldMove != canMove) {
                if (!shouldMove) {
                    this.playSound(SoundEvents.CREAKING_FREEZE, 1.0F, 1.0F);
                } else {
                    this.stopInPlace();
                        this.playSound(SoundEvents.CREAKING_UNFREEZE, 1.0F, 1.0F);
                }
            }
            this.entityData.set(CAN_MOVE, shouldMove);
        }
    }

    public boolean isSilent() {
        return !this.isActive();
    }

    // Play attack sound and handle attack animation
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.setAttackAnimationRemainingTicks(ATTACK_ANIMATION_DURATION);
            this.playSound(SoundEvents.CREAKING_ATTACK, 1.0F, 1.0F);
        } else {
            super.handleEntityEvent(id);
        }
    }

    // Check if the entity should move
    private boolean checkCanMove() {
        // Find all players within a 32.0D radius
        List<PlayerEntity> nearbyPlayers = this.level.getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().inflate(32.0D));

        // If no players are nearby
        if (nearbyPlayers.isEmpty()) {
            if (this.isActive()) {
                this.playSound(SoundEvents.CREAKING_ACTIVATE, 1.0F, 1.0F);  // Deactivate sound effect
                this.setIsActive(false);  // Set entity to inactive
            }
            return true;
        }

        // Set up a predicate to filter players based on disguise or other criteria
        Predicate<LivingEntity> visibilityPredicate = this.isActive()
                ? LivingEntity.PLAYER_NOT_WEARING_DISGUISE_ITEM
                : entity -> true;  // If inactive, no filtering

        // Loop through each nearby player
        for (PlayerEntity player : nearbyPlayers) {
            // Use the modified canEntitySee method to check if the player can see this entity
            if (!this.canEntitySee(player, 0.63, false, true, visibilityPredicate, this::getEyeY, this::getY, () -> (this.getEyeY() + this.getY()) / 2.0)) {
                continue;  // If the player cannot see, continue to the next one
            }

            // If the entity is already active, prevent movement
            if (this.isActive()) {
                return false;
            }

            // Check if the player is close enough (within 144 units squared)
            if (player.distanceToSqr(this) < 144.0) {
                this.playSound(SoundEvents.CREAKING_DEACTIVATE, 1.0F, 1.0F);  // Activation sound effect
                this.setIsActive(true);  // Set entity to active
                return false;
            }
        }

        return true;  // If no players meet the criteria, return true (can move)
    }

    // Creaking-specific AI for targeting and attacking
    static class CreakingAttackGoal extends Goal {
        private final CreakingEntity mob;
        private final double speedModifier;

        public CreakingAttackGoal(CreakingEntity mob, double speedModifier) {
            this.mob = mob;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            this.mob.creakingTarget = mob.level.getNearestPlayer(mob, 32.0D);
            return this.mob.creakingTarget != null && !playerLookingAtMe(this.mob.creakingTarget, mob);
        }

        @Override
        public boolean canContinueToUse() {
            return this.mob.creakingTarget != null && !playerLookingAtMe(this.mob.creakingTarget, mob);
        }

        @Override
        public void tick() {
            if (this.mob.creakingTarget != null && !playerLookingAtMe(this.mob.creakingTarget, mob)) {
                super.tick();
                mob.getNavigation().moveTo(this.mob.creakingTarget, speedModifier);
                if (mob.distanceToSqr(this.mob.creakingTarget) < mob.getBbWidth() * 2.0D * mob.getBbWidth() * 2.0D && mob.tick(35)) {
                    mob.doHurtTarget(this.mob.creakingTarget);
                    mob.playSound(SoundEvents.CREAKING_ATTACK, 1.0F, 1.0F);
                }
            } else {
                this.mob.stopInPlace();
            }
        }



        private boolean playerLookingAtMe(PlayerEntity player, Mob mob) {
            boolean flag = !((CreakingEntity)mob).checkCanMove();
            if (flag) {
                ((CreakingEntity)mob).stopInPlace();
            }
           return flag;
        }
    }


    public void knockback(float $$0, double $$1, double $$2) {
    }

    public boolean canEntitySee(LivingEntity entity, double tolerance, boolean useDistance, boolean useVisualClip,
                                Predicate<LivingEntity> visibilityPredicate, DoubleSupplier ... heightSuppliers) {
        // Check if the entity passes the provided predicate (e.g., if the entity is valid for visibility testing)
        if (!visibilityPredicate.test(entity)) {
            return false;
        }

        // Get the entity's view vector (direction it's looking at), normalized
        Vector3d viewDirection = entity.getViewVector(1.0f).normalize();

        // Iterate through the given height suppliers (probably different possible heights or y-positions to test)
        for (DoubleSupplier heightSupplier : heightSuppliers) {
            // Calculate the vector from the entity to the current object's position
            Vector3d directionToTarget = new Vector3d(this.getX() - entity.getX(),
                    heightSupplier.getAsDouble() - entity.getEyeY(),
                    this.getZ() - entity.getZ());
            // Get the distance to the target object
            double distanceToTarget = directionToTarget.length();

            // Normalize the direction vector (to get a unit vector)
            directionToTarget = directionToTarget.normalize();

            // Compute the dot product between the entity's view direction and the direction to the target
            double dotProduct = viewDirection.dot(directionToTarget);

            // If using distance, adjust the tolerance based on the distance to the target
            double adjustedTolerance = useDistance ? distanceToTarget : 1.0;

            // Check if the dot product is within the acceptable range based on the tolerance
            // (dotProduct > 1 - tolerance/adjustedTolerance)
            if (dotProduct <= 1.0 - tolerance / adjustedTolerance) {
                continue;  // If not, continue to the next heightSupplier
            }

            // Check if the entity has a line of sight to this object
            return entity.hasLineOfSight(this,
                    useVisualClip ? RayTraceContext.BlockMode.VISUAL : RayTraceContext.BlockMode.COLLIDER,
                    RayTraceContext.FluidMode.NONE,
                    heightSupplier);
        }

        // If none of the height checks pass, return false
        return false;
    }

    private boolean playerLookingAtMe(PlayerEntity player, Mob mob) {
        double deltaX = mob.getX() - player.getX();
        double deltaZ = mob.getZ() - player.getZ();
        double yaw = MathHelper.wrapDegrees(player.yRot);
        double mobAngle = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0D);

        double angleDiff = Math.abs(yaw - mobAngle);
        boolean looking = angleDiff < 60D;
        if (looking) {
            ((CreakingEntity)mob).stopInPlace();
            //mob.getMoveControl().setWantedPosition(null, 0);
        }
        return looking;  // Player is looking at the mob
    }



    public boolean doHurtTarget(Entity entity) {
        if (super.doHurtTarget(entity)) {
            this.attackAnimationRemainingTicks = 8;
            this.level.broadcastEntityEvent(this, (byte)4);
            return true;
        }

        return false;
    }

    @Override
    public boolean isPushable() {
        return super.isPushable() && this.canMove();
    }

    public void stopInPlace() {
        this.getNavigation().stop();
        this.setXxa(0.0f);
        this.setYya(0.0f);
        this.setSpeed(0.0f);
    }

    public void push(Entity entity) {}

    // Custom movement controller for the entity
    static class CreakingMoveController extends MovementController {
        private final CreakingEntity mob;

        public CreakingMoveController(CreakingEntity mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void tick() {
            if (mob.canMove()) {
                super.tick();
            }
        }
    }

    // Custom look controller for the entity
    static class CreakingLookController extends LookController {
        private final CreakingEntity mob;

        public CreakingLookController(CreakingEntity mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void tick() {
            if (mob.canMove()) {
                super.tick();
            } else {
                this.hasWanted = false;
            }
        }
    }

    // Custom jump controller for the entity
    static class CreakingJumpController extends JumpController {
        private final CreakingEntity mob;

        public CreakingJumpController(CreakingEntity mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void tick() {
            if (mob.canMove()) {
                super.tick();
            } else {
                mob.setJumping(false);  // Prevent jumping if not allowed to move
            }
        }
    }

    // Sound for steps
    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.CREAKING_STEP, 0.15F, 1.0F);
    }

    // Custom sounds for ambient, hurt, and death
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isActive() ? SoundEvents.CREAKING_AMBIENT : null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.CREAKING_SWAY;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREAKING_DEATH;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Logic for damage handling
        if (source.getEntity() instanceof PlayerEntity && ((PlayerEntity) source.getEntity()).isCreative()) {
            return super.hurt(source, amount);
        } else {
            return false;  // Invulnerable to non-creative mode damage
        }
    }
}
