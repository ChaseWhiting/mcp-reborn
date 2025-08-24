package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;

import java.util.Comparator;
import java.util.List;
import java.util.Set;


public class NearestLivingEntitySensor<T extends LivingEntity>
extends Sensor<T> {
    @Override
    protected void doTick(ServerWorld serverWorld, T t) {
        AxisAlignedBB aABB = (t).getBoundingBox().inflate(this.radiusXZ(t), this.radiusY(t), this.radiusXZ(t));
        List<LivingEntity> list = serverWorld.getEntitiesOfClass(LivingEntity.class, aABB, livingEntity2 -> livingEntity2 != t && livingEntity2.isAlive());
        list.sort(Comparator.comparingDouble(arg_0 -> t.distanceToSqr(arg_0)));
        Brain<?> brain = ((LivingEntity)t).getBrain();
        brain.setMemory(MemoryModuleType.LIVING_ENTITIES, list);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, new NearestVisibleLivingEntities(t, list));
    }

    protected int radiusXZ(T t) {
        return 16;
    }

    protected int radiusY(T t) {
        return 16;
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}

