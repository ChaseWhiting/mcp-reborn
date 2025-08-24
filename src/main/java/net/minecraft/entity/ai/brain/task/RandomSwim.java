package net.minecraft.entity.ai.brain.task;


import net.minecraft.entity.Creature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class RandomSwim
extends RandomStroll {
    public RandomSwim(float f) {
        super(f);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverLevel, Creature pathfinderMob) {
        return pathfinderMob.isInWaterOrBubble();
    }

    @Override
    protected Vector3d getTargetPos(Creature pathfinderMob) {
        Vector3d vec3 = getRandomSwimmablePos(pathfinderMob, this.maxHorizontalDistance, this.maxVerticalDistance);
        if (vec3 != null && pathfinderMob.level.getFluidState(new BlockPos(vec3)).isEmpty()) {
            return null;
        }
        return vec3;
    }

    @Nullable
    public static Vector3d getRandomSwimmablePos(Creature pathfinderMob, int n, int n2) {
        Vector3d vec3 = RandomPositionGenerator.getPos(pathfinderMob, n, n2);
        int n3 = 0;
        while (vec3 != null && !pathfinderMob.level.getBlockState(new BlockPos(vec3)).isPathfindable(pathfinderMob.level, new BlockPos(vec3), PathType.WATER) && n3++ < 10) {
            vec3 = RandomPositionGenerator.getPos(pathfinderMob, n, n2);
        }
        return vec3;
    }
}

