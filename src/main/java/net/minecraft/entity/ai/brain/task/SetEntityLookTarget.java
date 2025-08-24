package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntities;
import net.minecraft.util.math.EntityPosWrapper;

import java.util.Optional;
import java.util.function.Predicate;

public class SetEntityLookTarget extends Task<LivingEntity> {
    private final Predicate<LivingEntity> targetPredicate;
    private final float maxDistanceSquared;

    public SetEntityLookTarget(Predicate<LivingEntity> predicate, float maxDistance) {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT,
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.VALUE_PRESENT
        ));

        this.targetPredicate = predicate;
        this.maxDistanceSquared = maxDistance * maxDistance;
    }

    @Override
    protected void start(net.minecraft.world.server.ServerWorld world, LivingEntity entity, long time) {
        NearestVisibleLivingEntities nearestVisibleLivingEntities = entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get();
        Optional<LivingEntity> optional = nearestVisibleLivingEntities.findClosest(targetPredicate.and(livingEntity -> livingEntity.distanceToSqr(entity) <= (double)maxDistanceSquared && !entity.hasPassenger(livingEntity)));
        if (optional.isEmpty()) {
            return;
        }
        entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(optional.get(), true));


    }
}
