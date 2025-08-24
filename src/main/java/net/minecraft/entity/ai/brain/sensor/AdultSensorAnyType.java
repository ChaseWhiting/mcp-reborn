package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.tags.EntityTypeTags;

import java.util.Optional;

public class AdultSensorAnyType
extends AdultSensor {
    @Override
    protected void setNearestVisibleAdult(LivingEntity livingEntity2, NearestVisibleLivingEntities nearestVisibleLivingEntities) {
        Optional<LivingEntity> optional = nearestVisibleLivingEntities.findClosest(livingEntity -> livingEntity.getType().is(EntityTypeTags.FOLLOWABLE_FRIENDLY_MOBS) && !livingEntity.isBaby()).map(LivingEntity.class::cast);
        livingEntity2.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_NON_AGEABLE, optional);
    }
}
