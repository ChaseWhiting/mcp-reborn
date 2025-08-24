package net.minecraft.entity.ai.brain.declarative;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.UniformInt;
import net.minecraft.util.math.EntityPosWrapper;

import java.util.function.Function;

public class BabyFollowAdult {
    public static NewOneShot<LivingEntity> create(UniformInt uniformInt, float f) {
        return BabyFollowAdult.create(uniformInt, livingEntity -> Float.valueOf(f), MemoryModuleType.NEAREST_VISIBLE_ADULT_NON_AGEABLE, false);
    }

    public static NewOneShot<LivingEntity> create(UniformInt uniformInt, Function<LivingEntity, Float> function, MemoryModuleType<? extends LivingEntity> memoryModuleType, boolean bl) {
        return BehaviorHelper.create(instance -> instance.group(instance.present(memoryModuleType), instance.registered(MemoryModuleType.LOOK_TARGET), instance.absent(MemoryModuleType.WALK_TARGET)).apply(instance, (memoryAccessor, memoryAccessor2, memoryAccessor3) -> (serverLevel, livingEntity, l) -> {
            if (!livingEntity.isBaby()) {
                return false;
            }
            LivingEntity livingEntity2 = (LivingEntity)instance.get(memoryAccessor);
            if (livingEntity.closerThan(livingEntity2, uniformInt.getMaxValue() + 1) && !livingEntity.closerThan(livingEntity2, uniformInt.getMinValue())) {
                WalkTarget walkTarget = new WalkTarget(new EntityPosWrapper(livingEntity2, bl, bl), ((Float)function.apply(livingEntity)).floatValue(), uniformInt.getMinValue() - 1);
                memoryAccessor2.set(new EntityPosWrapper(livingEntity2, true, bl));
                memoryAccessor3.set(walkTarget);
                return true;
            }
            return false;
        }));
    }
}
