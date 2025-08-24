package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.Set;

public class AdultSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }

    @Override
    protected void doTick(ServerWorld serverLevel, LivingEntity livingEntity) {
        livingEntity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent(nearestVisibleLivingEntities -> this.setNearestVisibleAdult(livingEntity, (NearestVisibleLivingEntities)nearestVisibleLivingEntities));
    }

    protected void setNearestVisibleAdult(LivingEntity livingEntity, NearestVisibleLivingEntities nearestVisibleLivingEntities) {
        Optional<LivingEntity> optional = nearestVisibleLivingEntities.findClosest(livingEntity2 -> livingEntity2.getType() == livingEntity.getType() && !livingEntity2.isBaby()).map(LivingEntity.class::cast);
        livingEntity.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_NON_AGEABLE, optional);
    }
}

