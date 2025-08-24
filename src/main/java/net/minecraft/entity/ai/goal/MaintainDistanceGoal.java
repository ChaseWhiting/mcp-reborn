package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class MaintainDistanceGoal extends Goal {
    private final Mob mob;
    private final double speedModifier;
    private final float minDistance;
    private final float maxDistance;
    private LivingEntity target;

    public MaintainDistanceGoal(Mob mob, double speedModifier, float minDistance, float maxDistance) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        this.target = this.mob.getTarget();
        return this.target != null && this.target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target.isAlive();
    }

    @Override
    public void tick() {
        if (this.target == null) {
            return;
        }

        double distanceSqr = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());

        if ((distanceSqr > this.maxDistance * this.maxDistance || distanceSqr < this.minDistance * this.minDistance) && mob.getNavigation().isDone()) {
            // Pick a random point within the 14-20 block range
            Vector3d targetPosition = this.findRandomPositionWithinRange();
            if (targetPosition != null) {
                this.mob.getNavigation().moveTo(targetPosition.x, targetPosition.y, targetPosition.z, this.speedModifier);
            }
        } else {
            this.mob.getNavigation().stop();
        }
    }

    private Vector3d findRandomPositionWithinRange() {
        double angle = this.mob.getRandom().nextDouble() * Math.PI * 2; // Random angle
        double radius = this.minDistance + (this.mob.getRandom().nextDouble() * (this.maxDistance - this.minDistance)); // Random radius within range

        double offsetX = Math.cos(angle) * radius;
        double offsetZ = Math.sin(angle) * radius;

        return new Vector3d(
                this.target.getX() + offsetX,
                this.target.getY(),
                this.target.getZ() + offsetZ
        );
    }
}
