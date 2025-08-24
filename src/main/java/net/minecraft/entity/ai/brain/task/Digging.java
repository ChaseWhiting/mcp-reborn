package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.brain.memory.MemStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.warden.WardenEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;


public class Digging<E extends WardenEntity>
extends Task<E> {
    public Digging(int n) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemStatus.VALUE_ABSENT,MemoryModuleType.WALK_TARGET, MemStatus.VALUE_ABSENT), n);
    }

    @Override
    protected boolean canStillUse(ServerWorld serverWorld, E e, long l) {
        return true;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverWorld, E e) {
        return e.isOnGround() || e.isInWater() || e.isInLava();
    }

    @Override
    protected void start(ServerWorld serverWorld, E e, long l) {
        if (e.isOnGround()) {
            e.setPose(Pose.DIGGING);
            e.playSound(SoundEvents.WARDEN_DIG, 5.0f, 1.0f);
        } else {
            e.playSound(SoundEvents.WARDEN_AGITATED, 5.0f, 1.0f);
            this.stop(serverWorld, e, l);
        }
    }

    @Override
    protected void stop(ServerWorld serverWorld, E e, long l) {
        e.remove();
    }
}

