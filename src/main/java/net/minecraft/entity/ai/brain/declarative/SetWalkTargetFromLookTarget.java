package net.minecraft.entity.ai.brain.declarative;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.IPosWrapper;

import java.util.function.Function;
import java.util.function.Predicate;

public class SetWalkTargetFromLookTarget {

    public static NewOneShot<LivingEntity> create(float f, int n) {
        return SetWalkTargetFromLookTarget.create(livingEntity -> true, livingEntity -> Float.valueOf(f), n);
    }

    public static NewOneShot<LivingEntity> create(Predicate<LivingEntity> predicate, Function<LivingEntity, Float> function, int n) {
        return BehaviorHelper.create(instance ->
                instance.group(instance.absent(MemoryModuleType.WALK_TARGET), instance.present(MemoryModuleType.LOOK_TARGET))
                        .apply(instance, (memoryAccessor, memoryAccessor2) -> (serverLevel, livingEntity, l) -> {
                            if (!predicate.test(livingEntity)) {
                                return false;
                            }
                            memoryAccessor.set(new WalkTarget(
                                    (IPosWrapper) instance.get(memoryAccessor2),
                                    function.apply(livingEntity), // No need for explicit float cast
                                    n
                            ));
                            return true;
                        })
        );
    }

}
