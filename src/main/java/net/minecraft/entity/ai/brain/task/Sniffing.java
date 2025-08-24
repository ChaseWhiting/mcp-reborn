package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.warden.WardenAi;
import net.minecraft.entity.warden.WardenEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;


public class Sniffing<E extends WardenEntity>
extends Task<E> {
    private static final double ANGER_FROM_SNIFFING_MAX_DISTANCE_XZ = 6.0;
    private static final double ANGER_FROM_SNIFFING_MAX_DISTANCE_Y = 20.0;

    public Sniffing(int n) {
        super(ImmutableMap.<MemoryModuleType<?>, MemoryModuleStatus>builder()
                .put(MemoryModuleType.IS_SNIFFING, MemoryModuleStatus.VALUE_PRESENT)
                .put(MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_ABSENT)
                .put(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT)
                .put(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED)
                .put(MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleStatus.REGISTERED)
                .put(MemoryModuleType.DISTURBANCE_LOCATION, MemoryModuleStatus.REGISTERED)
                .put(MemoryModuleType.SNIFF_COOLDOWN, MemoryModuleStatus.REGISTERED)
                .build(), n);
    }

    @Override
    protected boolean canStillUse(ServerWorld serverWorld, E e, long l) {
        return true;
    }

    @Override
    protected void start(ServerWorld serverWorld, E e, long l) {
        e.playSound(SoundEvents.WARDEN_SNIFF, 5.0f, 1.0f);
    }

    @Override
    protected void stop(ServerWorld serverWorld, E e, long l) {
        if (e.hasPose(Pose.SNIFFING)) {
            e.setPose(Pose.STANDING);
        }
        e.getBrain().eraseMemory(MemoryModuleType.IS_SNIFFING);
        e.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE).filter(arg_0 -> e.canTargetEntity(arg_0)).ifPresent(livingEntity -> {
            double d = 6;
            double b = 20;
            if (e.veryHardmode()) {
                d = 16;
                b = 30;
            }
            if (e.closerThan(livingEntity, d, b)) {
                e.increaseAngerAt(livingEntity);
            }
            if (!e.getBrain().hasMemoryValue(MemoryModuleType.DISTURBANCE_LOCATION)) {
                WardenAi.setDisturbanceLocation(e, livingEntity.blockPosition());
            }
        });
    }
}

