/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

public class TargetingConditions {
    public static final TargetingConditions DEFAULT = TargetingConditions.forCombat();
    private static final double MIN_VISIBILITY_DISTANCE_FOR_INVISIBLE_TARGET = 2.0;
    private final boolean isCombat;
    private double range = -1.0;
    private boolean checkLineOfSight = true;
    private boolean testInvisible = true;
    @Nullable
    private Selector selector;

    private TargetingConditions(boolean bl) {
        this.isCombat = bl;
    }

    public static TargetingConditions forCombat() {
        return new TargetingConditions(true);
    }

    public static TargetingConditions forNonCombat() {
        return new TargetingConditions(false);
    }

    public TargetingConditions copy() {
        TargetingConditions targetingConditions = this.isCombat ? TargetingConditions.forCombat() : TargetingConditions.forNonCombat();
        targetingConditions.range = this.range;
        targetingConditions.checkLineOfSight = this.checkLineOfSight;
        targetingConditions.testInvisible = this.testInvisible;
        targetingConditions.selector = this.selector;
        return targetingConditions;
    }

    public TargetingConditions range(double d) {
        this.range = d;
        return this;
    }

    public TargetingConditions ignoreLineOfSight() {
        this.checkLineOfSight = false;
        return this;
    }

    public TargetingConditions ignoreInvisibilityTesting() {
        this.testInvisible = false;
        return this;
    }

    public TargetingConditions selector(@Nullable Selector predicate) {
        this.selector = predicate;
        return this;
    }

    public boolean test(@Nullable LivingEntity livingEntity, LivingEntity livingEntity2) {
        ServerWorld serverLevel;
        if (livingEntity != null && livingEntity.level instanceof ServerWorld serverWorld) {
            serverLevel = serverWorld;
        } else if (livingEntity2.level instanceof ServerWorld serverWorld){
            serverLevel = serverWorld;
        } else {
            return false;
        }

        if (livingEntity == livingEntity2) {
            return false;
        }
        if (!livingEntity2.canBeSeenByAnyone()) {
            return false;
        }
        if (this.selector != null && !this.selector.test(livingEntity2, serverLevel)) {
            return false;
        }
        if (livingEntity == null) {
            if (this.isCombat && (!livingEntity2.canBeSeenAsEnemy() || serverLevel.getDifficulty() == Difficulty.PEACEFUL)) {
                return false;
            }
        } else {
            Mob mob;
            if (this.isCombat && (!livingEntity.canAttack(livingEntity2) || !livingEntity.canAttackType(livingEntity2.getType()) || livingEntity.isAlliedTo(livingEntity2))) {
                return false;
            }
            if (this.range > 0.0) {
                double d = this.testInvisible ? livingEntity2.getVisibilityPercent(livingEntity) : 1.0;
                double d2 = Math.max(this.range * d, 2.0);
                double d3 = livingEntity.distanceToSqr(livingEntity2.getX(), livingEntity2.getY(), livingEntity2.getZ());
                if (d3 > d2 * d2) {
                    return false;
                }
            }
            if (this.checkLineOfSight && livingEntity instanceof Mob && !(mob = (Mob)livingEntity).getSensing().canSee(livingEntity2)) {
                return false;
            }
        }
        return true;
    }

    public boolean test(ServerWorld world, @Nullable LivingEntity livingEntity, LivingEntity livingEntity2) {
        ServerWorld serverLevel = world;


        if (livingEntity == livingEntity2) {
            return false;
        }
        if (!livingEntity2.canBeSeenByAnyone()) {
            return false;
        }
        if (this.selector != null && !this.selector.test(livingEntity2, serverLevel)) {
            return false;
        }
        if (livingEntity == null) {
            if (this.isCombat && (!livingEntity2.canBeSeenAsEnemy() || serverLevel.getDifficulty() == Difficulty.PEACEFUL)) {
                return false;
            }
        } else {
            Mob mob;
            if (this.isCombat && (!livingEntity.canAttack(livingEntity2) || !livingEntity.canAttackType(livingEntity2.getType()) || livingEntity.isAlliedTo(livingEntity2))) {
                return false;
            }
            if (this.range > 0.0) {
                double d = this.testInvisible ? livingEntity2.getVisibilityPercent(livingEntity) : 1.0;
                double d2 = Math.max(this.range * d, 2.0);
                double d3 = livingEntity.distanceToSqr(livingEntity2.getX(), livingEntity2.getY(), livingEntity2.getZ());
                if (d3 > d2 * d2) {
                    return false;
                }
            }
            if (this.checkLineOfSight && livingEntity instanceof Mob && !(mob = (Mob)livingEntity).getSensing().canSee(livingEntity2)) {
                return false;
            }
        }
        return true;
    }

    private boolean canBeSeenByAnyone(LivingEntity entity) {
        return !entity.isSpectator() && entity.isAlive();
    }

    private boolean canBeSeenAsEnemy(LivingEntity entity) {
        return !entity.isInvulnerable() && canBeSeenByAnyone(entity);
    }


    @FunctionalInterface
    public static interface Selector {
        public boolean test(LivingEntity var1, ServerWorld var2);
    }
}

