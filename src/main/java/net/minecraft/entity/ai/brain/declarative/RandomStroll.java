package net.minecraft.entity.ai.brain.declarative;

import net.minecraft.entity.Creature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.task.AirAndWaterRandomPos;
import net.minecraft.entity.ai.brain.task.RandomStrollTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class RandomStroll {

    public static NewOneShot<Creature> stroll(float f) {
        return RandomStroll.stroll(f, true);
    }

    public static NewOneShot<Creature> stroll(float f, boolean bl) {
        return RandomStroll.strollFlyOrSwim(f, pathfinderMob -> RandomPositionGenerator.getPos(pathfinderMob, 10, 7), bl ? pathfinderMob -> true : pathfinderMob -> !pathfinderMob.isInWater());
    }

    public static Task<Creature> stroll(float f, int n, int n2) {
        return RandomStroll.strollFlyOrSwim(f, pathfinderMob -> RandomPositionGenerator.getPos(pathfinderMob, n, n2), pathfinderMob -> true);
    }

    public static Task<Creature> fly(float f) {
        return RandomStroll.strollFlyOrSwim(f, pathfinderMob -> RandomStrollTask.getTargetFlyPos(pathfinderMob, 10, 7), pathfinderMob -> true);
    }

    public static Task<Creature> swim(float f) {
        return RandomStroll.strollFlyOrSwim(f, RandomStrollTask::getTargetSwimPos, Entity::isInWater);
    }

    private static NewOneShot<Creature> strollFlyOrSwim(float f, Function<Creature, Vector3d> function, Predicate<Creature> predicate) {
        return BehaviorHelper.create(instance ->
                instance.group(instance.absent(MemoryModuleType.WALK_TARGET))
                        .apply(instance, memoryAccessor -> (serverLevel, pathfinderMob, l) -> {
                            if (!predicate.test(pathfinderMob)) {
                                return false;
                            }
                            Optional<Vector3d> optional = Optional.ofNullable(function.apply(pathfinderMob));
                            memoryAccessor.setOrErase(optional.map(vec3 -> new WalkTarget(vec3, f, 0)));
                            return true;
                        })
        );
    }

    public static Task<Creature> flyTest(float f) {
        return RandomStroll.strollFlyOrSwimTest(f, pathfinderMob -> RandomStroll.getTargetFlyPos(pathfinderMob, 10, 7), pathfinderMob -> true);
    }
    private static NewOneShot<Creature> strollFlyOrSwimTest(float f, Function<Creature, Vector3d> function, Predicate<Creature> predicate) {
        return BehaviorHelper.create(instance -> instance.group(instance.absent(MemoryModuleType.WALK_TARGET)).apply(instance, memoryAccessor -> (serverLevel, pathfinderMob, l) -> {
            if (!predicate.test((Creature)pathfinderMob)) {
                return false;
            }
            Optional<Vector3d> optional = Optional.ofNullable((Vector3d)function.apply(pathfinderMob));
            memoryAccessor.setOrErase(optional.map(vec3 -> new WalkTarget((Vector3d) vec3, f, 0)));
            return true;
        }));
    }

    @Nullable
    private static Vector3d getTargetFlyPos(Creature pathfinderMob, int n, int n2) {
        Vector3d vec3 = pathfinderMob.getViewVector(0.0f);
        return AirAndWaterRandomPos.getPos(pathfinderMob, n, n2, -2, vec3.x, vec3.z, 1.5707963705062866);
    }

}
