package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.warden.WardenEntity;
import net.minecraft.world.server.ServerWorld;


public class WardenEntitySensor
extends NearestLivingEntitySensor<WardenEntity> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.copyOf((Iterable)Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    @Override
    protected void doTick(ServerWorld serverWorld, WardenEntity warden) {
        super.doTick(serverWorld, warden);
        WardenEntitySensor.getClosest(warden, livingEntity -> livingEntity.getType() == EntityType.PLAYER).or(() -> WardenEntitySensor.getClosest(warden, livingEntity -> livingEntity.getType() != EntityType.PLAYER)).ifPresentOrElse(livingEntity -> warden.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, livingEntity), () -> warden.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE));
    }

    private static Optional<LivingEntity> getClosest(WardenEntity warden, Predicate<LivingEntity> predicate) {
        return warden.getBrain().getMemory(MemoryModuleType.LIVING_ENTITIES).stream().flatMap(Collection::stream).filter(warden::canTargetEntity).filter(predicate).findFirst();
    }

    @Override
    protected int radiusXZ(WardenEntity warden) {
        return warden.veryHardmode() ? 64 : 24;
    }

    @Override
    protected int radiusY(WardenEntity warden) {
        return this.radiusXZ(warden);
    }
}

