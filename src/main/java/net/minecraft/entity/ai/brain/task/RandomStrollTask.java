package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.Creature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.declarative.TaskBuilder;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.entity.ai.RandomPositionGenerator;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class RandomStrollTask {

    private static final int MAX_XZ_DIST = 10;
    private static final int MAX_Y_DIST = 7;

    public static Task<Creature> stroll(float speed) {
        return stroll(speed, true);
    }

    public static Task<Creature> stroll(float speed, boolean allowWater) {
        return createTask(speed, entity -> RandomPositionGenerator.getLandPos(entity, 10, 7),
                allowWater ? entity -> true : entity -> !entity.isInWater());
    }

    public static Task<Creature> fly(float speed) {
        return createTask(speed, entity -> getTargetFlyPos(entity, 10, 7), entity -> true);
    }

    public static Task<Creature> swim(float speed) {
        return createTask(speed, RandomStrollTask::getTargetSwimPos, LivingEntity::isInWater);
    }

    private static Task<Creature> createTask(float speed, Function<Creature, Vector3d> posFinder, Predicate<Creature> condition) {
        Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemory = new HashMap<>();
        requiredMemory.put(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT);

        return new TaskBuilder.Builder<Creature>()
                .extraStartConditionsBehavior((world, entity) -> condition.test(entity))
                .startBehavior((world, entity, time) -> {
                    Optional<Vector3d> targetPos = Optional.ofNullable(posFinder.apply(entity));
                    targetPos.ifPresent(pos -> entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, speed, 0)));
                })
                .build(requiredMemory);
    }

    public static Vector3d getTargetSwimPos(Creature entity) {
        Vector3d lastPos = null;
        Vector3d currentPos = null;
        int[][] swimDistanceTiers = {{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

        for (int[] tier : swimDistanceTiers) {
            currentPos = (lastPos == null) ?
                    getRandomSwimmablePos(entity, tier[0], tier[1]) :
                    entity.position().add(entity.position().vectorTo(lastPos).normalize().scale(tier[0]));

            if (currentPos == null || entity.level.getFluidState(new BlockPos(currentPos)).isEmpty()) {
                return lastPos;
            }
            lastPos = currentPos;
        }
        return currentPos;
    }

    public static Vector3d getTargetFlyPos(Creature entity, int xzRange, int yRange) {
        Vector3d viewDirection = entity.getViewVector(0.0f);
        return AirAndWaterRandomPos.getPos(entity, xzRange, yRange, -2, viewDirection.x, viewDirection.z, Math.PI / 2);
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
