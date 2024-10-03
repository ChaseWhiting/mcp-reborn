package net.minecraft.entity.monster.creaking;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.EnumSet;

public class CreakingEntity extends Monster {
    public CreakingEntity(EntityType<? extends Monster> type, World world) {
        super(type, world);
        this.xpReward = 5;
    }

    private PlayerEntity creakingTarget = null;


    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        nbt.put("HeartPos", NBTUtil.writeBlockPos(this.creakingHeartPosition));
    }

    public void readAdditionalSaveData(CompoundNBT nbt) {
        if (nbt.contains("HeartPos")) {
            this.creakingHeartPosition = NBTUtil.readBlockPos(nbt.getCompound("HeartPos"));
        }
    }



    private BlockPos creakingHeartPosition = BlockPos.ZERO;

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CreakingAttackGoal(this, 1.3D));
    }

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
            this.mob.creakingTarget = mob.level.getNearestSurvivalPlayer(mob, 20);
            return this.mob.creakingTarget != null && !playerLookingAtMe(this.mob.creakingTarget, mob);
        }

        @Override
        public boolean canContinueToUse() {
            return this.mob.creakingTarget != null && !playerLookingAtMe(this.mob.creakingTarget, mob);
        }

        @Override
        public void tick() {
            if (this.mob.creakingTarget != null && !playerLookingAtMe(this.mob.creakingTarget, mob)) {
                mob.getNavigation().moveTo(this.mob.creakingTarget, speedModifier);
                //mob.getLookControl().setLookAt(this.mob.creakingTarget, 30.0F, 30.0F);

                if (mob.distanceToSqr(this.mob.creakingTarget) < mob.getBbWidth() * 2.0D * mob.getBbWidth() * 2.0D) {
                    mob.doHurtTarget(this.mob.creakingTarget);
                }
            } else {
                this.mob.getNavigation().stop();
            }
        }

        // Helper method to check if the player is looking at the mob
        private boolean playerLookingAtMe(PlayerEntity player, Mob mob) {
            double deltaX = mob.getX() - player.getX();
            double deltaZ = mob.getZ() - player.getZ();
            double yaw = MathHelper.wrapDegrees(player.yRot);
            double mobAngle = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0D);

            double angleDiff = Math.abs(yaw - mobAngle);
            return angleDiff < 60.0D; // If the angle is within the player's 60-degree field of view
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof PlayerEntity && ((PlayerEntity) source.getEntity()).isCreative()) {
            // Can only be damaged in Creative Mode, dies in one hit
            return super.hurt(source, amount);
        } else {
            // Creaking is invulnerable in Survival Mode unless its heart is destroyed
            sendParticlesToHeart();
            return false;
        }
    }

    private boolean playerLookingAtMe(PlayerEntity player, Mob mob) {
        double deltaX = mob.getX() - player.getX();
        double deltaZ = mob.getZ() - player.getZ();
        double yaw = MathHelper.wrapDegrees(player.yRot);
        double mobAngle = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0D);

        double angleDiff = Math.abs(yaw - mobAngle);
        return angleDiff < 60.0D; // If the angle is within the player's 60-degree field of view
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.creakingTarget != null) {
            this.setTarget(this.creakingTarget);
            if (this.playerLookingAtMe(creakingTarget.asPlayer(), this)) {
                this.setNoAi(true);
            } else {
                this.setNoAi(false);
            }
        }
        // Despawn during daytime
        if (this.level.isDay() && !this.level.isClientSide && !this.persistenceRequired) {
            this.remove();
        }

        // Check for distance to the creaking heart and limit movement to 32 blocks
        if (creakingHeartPosition != null) {
            double distanceToHeart = this.blockPosition().distSqr(creakingHeartPosition);
            if (distanceToHeart > 31 * 31) {
                this.getNavigation().stop(); // Prevent moving beyond 32 blocks
            }
        }


    }

    private void sendParticlesToHeart() {
        // Logic to send particles from the Creaking to its heart
        if (creakingHeartPosition != null) {
            double heartX = creakingHeartPosition.getX() + 0.5;
            double heartY = creakingHeartPosition.getY() + 1;
            double heartZ = creakingHeartPosition.getZ() + 0.5;

            for (int i = 0; i < 10; ++i) {
                this.level.addParticle(ParticleTypes.HEART,
                        this.getX() + this.random.nextDouble() * this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * this.getBbHeight(),
                        this.getZ() + this.random.nextDouble() * this.getBbWidth(),
                        heartX - this.getX(),
                        heartY - this.getY(),
                        heartZ - this.getZ());
            }
        }
    }
}
