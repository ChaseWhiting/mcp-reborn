package net.minecraft.entity.ai.brain.task;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.entity.Creature;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;


public class AirAndWaterRandomPos {
    @Nullable
    public static Vector3d getPos(Creature pathfinderMob, int n, int n2, int n3, double d, double d2, double d3) {
        boolean bl = GoalUtils.mobRestricted(pathfinderMob, n);
        return generateRandomPos(pathfinderMob, () -> AirAndWaterRandomPos.generateRandomPos(pathfinderMob, n, n2, n3, d, d2, d3, bl));
    }

    @Nullable
    public static BlockPos generateRandomPos(Creature pathfinderMob, int n, int n2, int n3, double d, double d2, double d3, boolean bl) {
        BlockPos blockPos2 = generateRandomDirectionWithinRadians(pathfinderMob.getRandom(), n, n2, n3, d, d2, d3);
        if (blockPos2 == null) {
            return null;
        }
        BlockPos blockPos3 = generateRandomPosTowardDirection(pathfinderMob, n, pathfinderMob.getRandom(), blockPos2);
        if (GoalUtils.isOutsideLimits(blockPos3, pathfinderMob) || GoalUtils.isRestricted(bl, pathfinderMob, blockPos3)) {
            return null;
        }
        if (GoalUtils.hasMalus(pathfinderMob, blockPos3 = moveUpOutOfSolid(blockPos3, pathfinderMob.level.getMaxBuildHeight(), blockPos -> GoalUtils.isSolid(pathfinderMob, blockPos)))) {
            return null;
        }
        return blockPos3;
    }

    @Nullable
    public static BlockPos generateRandomDirectionWithinRadians(Random randomSource, int n, int n2, int n3, double d, double d2, double d3) {
        double d4 = MathHelper.atan2(d2, d) - 1.5707963705062866;
        double d5 = d4 + (double)(2.0f * randomSource.nextFloat() - 1.0f) * d3;
        double d6 = Math.sqrt(randomSource.nextDouble()) * (double)MathHelper.SQRT_OF_TWO * (double)n;
        double d7 = -d6 * Math.sin(d5);
        double d8 = d6 * Math.cos(d5);
        if (Math.abs(d7) > (double)n || Math.abs(d8) > (double)n) {
            return null;
        }
        int n4 = randomSource.nextInt(2 * n2 + 1) - n2 + n3;
        return new BlockPos(d7, (double)n4, d8);
    }

    public static BlockPos generateRandomPosTowardDirection(Creature pathfinderMob, int n, Random randomSource, BlockPos blockPos) {
        int n2 = blockPos.getX();
        int n3 = blockPos.getZ();
        if (pathfinderMob.hasRestriction() && n > 1) {
            BlockPos blockPos2 = pathfinderMob.getRestrictCenter();
            n2 = pathfinderMob.getX() > (double)blockPos2.getX() ? (n2 -= randomSource.nextInt(n / 2)) : (n2 += randomSource.nextInt(n / 2));
            n3 = pathfinderMob.getZ() > (double)blockPos2.getZ() ? (n3 -= randomSource.nextInt(n / 2)) : (n3 += randomSource.nextInt(n / 2));
        }
        return new BlockPos((double)n2 + pathfinderMob.getX(), (double)blockPos.getY() + pathfinderMob.getY(), (double)n3 + pathfinderMob.getZ());
    }

    @VisibleForTesting
    public static BlockPos moveUpOutOfSolid(BlockPos blockPos, int n, Predicate<BlockPos> predicate) {
        if (predicate.test(blockPos)) {
            BlockPos blockPos2 = blockPos.above();
            while (blockPos2.getY() < n && predicate.test(blockPos2)) {
                blockPos2 = blockPos2.above();
            }
            return blockPos2;
        }
        return blockPos;
    }

    @Nullable
    public static Vector3d generateRandomPos(Creature pathfinderMob, Supplier<BlockPos> supplier) {
        return generateRandomPos(supplier, pathfinderMob::getWalkTargetValue);
    }

    @Nullable
    public static Vector3d generateRandomPos(Supplier<BlockPos> supplier, ToDoubleFunction<BlockPos> toDoubleFunction) {
        double d = Double.NEGATIVE_INFINITY;
        BlockPos blockPos = null;
        for (int i = 0; i < 10; ++i) {
            double d2;
            BlockPos blockPos2 = supplier.get();
            if (blockPos2 == null || !((d2 = toDoubleFunction.applyAsDouble(blockPos2)) > d)) continue;
            d = d2;
            blockPos = blockPos2;
        }
        return blockPos != null ? Vector3d.atBottomCenterOf(blockPos) : null;
    }
}

