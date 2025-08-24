package net.minecraft.entity.ai.goal.newgoals;

import net.minecraft.entity.Creature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.TargetingConditions;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class TemptGoal
extends Goal {
    private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().ignoreLineOfSight();
    private static final double DEFAULT_STOP_DISTANCE = 2.5;
    private final TargetingConditions targetingConditions;
    protected final Mob mob;
    protected final double speedModifier;
    private double px;
    private double py;
    private double pz;
    private double pRotX;
    private double pRotY;
    @Nullable
    protected PlayerEntity player;
    private int calmDown;
    private boolean isRunning;
    private final Predicate<ItemStack> items;
    private final boolean canScare;
    private final double stopDistance;

    public TemptGoal(Creature pathfinderMob, double d, Predicate<ItemStack> predicate, boolean bl) {
        this((Mob)pathfinderMob, d, predicate, bl, 2.5);
    }

    public TemptGoal(Creature pathfinderMob, double d, Predicate<ItemStack> predicate, boolean bl, double d2) {
        this((Mob)pathfinderMob, d, predicate, bl, d2);
    }

    TemptGoal(Mob mob, double d, Predicate<ItemStack> predicate, boolean bl, double d2) {
        this.mob = mob;
        this.speedModifier = d;
        this.items = predicate;
        this.canScare = bl;
        this.stopDistance = d2;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.targetingConditions = TEMPT_TARGETING.copy().selector((livingEntity, serverLevel) -> this.shouldFollow(livingEntity));
    }

    @Override
    public boolean canUse() {
        if (this.calmDown > 0) {
            --this.calmDown;
            return false;
        }
        this.player = TemptGoal.getServerLevel(this.mob).getNearestPlayer(this.targetingConditions.range(this.mob.getAttributeValue(Attributes.TEMPT_RANGE)), this.mob);
        return this.player != null;
    }

    private boolean shouldFollow(LivingEntity livingEntity) {
        return this.items.test(livingEntity.getMainHandItem()) || this.items.test(livingEntity.getOffhandItem());
    }

    @Override
    public boolean canContinueToUse() {
        if (this.canScare()) {
            if (this.mob.distanceToSqr(this.player) < 36.0) {
                if (this.player.distanceToSqr(this.px, this.py, this.pz) > 0.010000000000000002) {
                    return false;
                }
                if (Math.abs((double)this.player.xRot - this.pRotX) > 5.0 || Math.abs((double)this.player.getYRot() - this.pRotY) > 5.0) {
                    return false;
                }
            } else {
                this.px = this.player.getX();
                this.py = this.player.getY();
                this.pz = this.player.getZ();
            }
            this.pRotX = this.player.xRot;
            this.pRotY = this.player.getYRot();
        }
        return this.canUse();
    }

    protected boolean canScare() {
        return this.canScare;
    }

    @Override
    public void start() {
        this.px = this.player.getX();
        this.py = this.player.getY();
        this.pz = this.player.getZ();
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.player = null;
        this.stopNavigation();
        this.calmDown = TemptGoal.reducedTickDelay(100);
        this.isRunning = false;
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(this.player, this.mob.getMaxHeadYRot() + 20, this.mob.getMaxHeadXRot());
        if (this.mob.distanceToSqr(this.player) < this.stopDistance * this.stopDistance) {
            this.stopNavigation();
        } else {
            this.navigateTowards(this.player);
        }
    }

    protected void stopNavigation() {
        this.mob.getNavigation().stop();
    }

    protected void navigateTowards(PlayerEntity player) {
        this.mob.getNavigation().moveTo(player, this.speedModifier);
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public static class ForNonPathfinders
    extends TemptGoal {
        public ForNonPathfinders(Mob mob, double d, Predicate<ItemStack> predicate, boolean bl, double d2) {
            super(mob, d, predicate, bl, d2);
        }

        @Override
        protected void stopNavigation() {
            this.mob.getMoveControl().setWait();
        }

        @Override
        protected void navigateTowards(PlayerEntity player) {
            Vector3d vec3 = player.getEyePosition(1.0F).subtract(this.mob.position()).scale(this.mob.getRandom().nextDouble()).add(this.mob.position());
            this.mob.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, this.speedModifier);
        }
    }
}
