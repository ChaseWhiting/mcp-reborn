package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class NearestVisibleLivingEntitySensor
extends Sensor<LivingEntity> {
    protected abstract boolean isMatchingEntity(LivingEntity var1, LivingEntity var2);

    protected abstract MemoryModuleType<LivingEntity> getMemory();

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(this.getMemory());
    }

    @Override
    protected void doTick(ServerWorld serverLevel, LivingEntity livingEntity) {
        livingEntity.getBrain().setMemory(this.getMemory(), this.getNearestEntity(livingEntity));
    }

    private Optional<LivingEntity> getNearestEntity(LivingEntity livingEntity) {
        return this.getVisibleEntities(livingEntity).flatMap(list -> list.stream().filter(livingEntity2 -> this.isMatchingEntity(livingEntity, (LivingEntity)livingEntity2)).min(Comparator.comparingDouble(livingEntity::distanceToSqr)));
    }

    protected Optional<List<LivingEntity>> getVisibleEntities(LivingEntity livingEntity) {
        return livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
    }
}

