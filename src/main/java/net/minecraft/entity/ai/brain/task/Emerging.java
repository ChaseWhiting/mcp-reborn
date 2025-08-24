
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.warden.WardenEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;


public class Emerging<E extends WardenEntity>
extends Task<E> {
    public Emerging(int n) {
        super(ImmutableMap.of(MemoryModuleType.IS_EMERGING, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED), n);
    }

    @Override
    protected boolean canStillUse(ServerWorld serverWorld, E e, long l) {
        return true;
    }

    @Override
    protected void start(ServerWorld serverWorld, E e, long l) {
        ((Entity)e).setPose(Pose.EMERGING);
        ((Entity)e).playSound(SoundEvents.WARDEN_EMERGE, 5.0f, 1.0f);
    }

    @Override
    protected void stop(ServerWorld serverWorld, E e, long l) {
        if (((WardenEntity)e).hasPose(Pose.EMERGING)) {
            ((Entity)e).setPose(Pose.STANDING);
        }
    }
}

