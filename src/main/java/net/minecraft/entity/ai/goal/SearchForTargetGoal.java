package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Creature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class SearchForTargetGoal<T extends Creature> extends Goal {
    private final T mob;
    private final Class<? extends LivingEntity> targetClass;
    private final double speedModifier;
    private final double searchRadius;
    private final Predicate<LivingEntity> targetPredicate;
    private final Predicate<LivingEntity> nPredicate = (EntityPredicates.NO_CREATIVE_OR_SPECTATOR::test);
    private final boolean requiresNoTarget;
    private int searchCooldown = 80;
    private BlockPos targetPosition;

    public SearchForTargetGoal(T mob, Class<? extends LivingEntity> targetClass, double speedModifier, double searchRadius, @Nullable Predicate<LivingEntity> targetPredicate, boolean requiresNoTarget) {
        this.mob = mob;
        this.targetClass = targetClass;
        this.speedModifier = speedModifier;
        this.searchRadius = searchRadius;
        if (targetPredicate != null) {
            this.targetPredicate = targetPredicate.and(nPredicate);
        } else {
            this.targetPredicate = nPredicate;
        }
        this.requiresNoTarget = requiresNoTarget;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getNavigation().isInProgress() || !this.mob.getNavigation().isDone()) {
            return false;
        }

        if (!this.mob.veryHardmode()) {
            return false;
        }

        if (--searchCooldown > 0) {
            return false;
        } else {
            searchCooldown = MathHelper.nextInt(this.mob.getRandom(), 40, 120);
        }


        if (this.requiresNoTarget && this.mob.getTarget() != null) {
            return false;
        }


        List<LivingEntity> nearbyTargets = this.mob.level.getEntitiesOfClass(targetClass,
                new AxisAlignedBB(this.mob.blockPosition()).inflate(this.searchRadius),
                targetPredicate);


        if (!nearbyTargets.isEmpty()) {
            LivingEntity targetEntity = nearbyTargets.get(this.mob.getRandom().nextInt(nearbyTargets.size()));
            Vector3d targetVec = null;
            for (int i = 0; i < 10; i++) {
                targetVec = RandomPositionGenerator.getPosTowards(this.mob, 26, 16, targetEntity.position());
                if (targetVec != null) {
                    break;
                }
            }

            if (targetVec != null) {
                this.targetPosition = new BlockPos(targetVec);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && this.mob.getTarget() == null;
    }

    @Override
    public void start() {
        if (this.targetPosition != null) {
            this.mob.getNavigation().moveTo(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ(), this.speedModifier);
        }
    }

    @Override
    public void stop() {
        this.targetPosition = null;
    }

    @Override
    public void tick() {
        if (this.mob.getTarget() != null && this.requiresNoTarget) {
            this.stop();
        }

        if (this.targetPosition != null && this.mob.getNavigation().isInProgress()) {
            this.mob.getNavigation().moveTo(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ(), this.speedModifier);
        }
    }
}
