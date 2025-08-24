package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.Unit;
import net.minecraft.world.server.ServerWorld;


public class IsInWaterSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.IS_IN_WATER);
    }

    @Override
    protected void doTick(ServerWorld serverLevel, LivingEntity livingEntity) {
        if (livingEntity.isInWater()) {
            livingEntity.getBrain().setMemory(MemoryModuleType.IS_IN_WATER, Unit.INSTANCE);
        } else {
            livingEntity.getBrain().eraseMemory(MemoryModuleType.IS_IN_WATER);
        }
    }
}

