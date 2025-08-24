package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.Creature;
import net.minecraft.entity.Mob;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;


public class GoalUtils {
    public static boolean hasGroundPathNavigation(Mob mob) {
        return mob.getNavigation() instanceof GroundPathNavigator;
    }

    public static boolean mobRestricted(Creature pathfinderMob, int n) {
        return pathfinderMob.hasRestriction() && pathfinderMob.getRestrictCenter().closerToCenterThan(pathfinderMob.position(), (double)(pathfinderMob.getRestrictRadius() + (float)n) + 1.0);
    }

    public static boolean isOutsideLimits(BlockPos blockPos, Creature pathfinderMob) {
        return blockPos.getY() < 1 || blockPos.getY() > pathfinderMob.level.getMaxBuildHeight();
    }

    public static boolean isRestricted(boolean bl, Creature pathfinderMob, BlockPos blockPos) {
        return bl && !pathfinderMob.isWithinRestriction(blockPos);
    }

    public static boolean isNotStable(PathNavigator pathNavigation, BlockPos blockPos) {
        return !pathNavigation.isStableDestination(blockPos);
    }

    public static boolean isWater(Creature pathfinderMob, BlockPos blockPos) {
        return pathfinderMob.level.getFluidState(blockPos).is(FluidTags.WATER);
    }

    public static boolean hasMalus(Creature pathfinderMob, BlockPos blockPos) {
        return pathfinderMob.getPathfindingMalus(WalkNodeProcessor.getBlockPathTypeStatic(pathfinderMob.level, blockPos.mutable())) != 0.0f;
    }

    public static boolean isSolid(Creature pathfinderMob, BlockPos blockPos) {
        return pathfinderMob.level.getBlockState(blockPos).getMaterial().isSolid();
    }
}

