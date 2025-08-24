package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.warden.WardenAi;
import net.minecraft.entity.warden.WardenEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.server.ServerWorld;


public class Roar
extends Task<WardenEntity> {
    private static final int TICKS_BEFORE_PLAYING_ROAR_SOUND = 25;
    private static final int ROAR_ANGER_INCREASE = 20;

    public Roar() {
        super(ImmutableMap.of(MemoryModuleType.ROAR_TARGET, MemStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemStatus.VALUE_ABSENT, MemoryModuleType.ROAR_SOUND_COOLDOWN, MemStatus.REGISTERED, MemoryModuleType.ROAR_SOUND_DELAY, MemStatus.REGISTERED), WardenAi.ROAR_DURATION);
    }

    @Override
    protected void start(ServerWorld serverWorld, WardenEntity warden, long l) {
        Brain<WardenEntity> brain = warden.getBrain();
        brain.setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_DELAY, Unit.INSTANCE, 25L);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        LivingEntity livingEntity = warden.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).get();
        BrainUtil.lookAtEntity(warden, livingEntity);
        warden.setPose(Pose.ROARING);
        warden.increaseAngerAt(livingEntity, 20, false);
    }

    @Override
    protected boolean canStillUse(ServerWorld serverWorld, WardenEntity warden, long l) {
        if (warden.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_COOLDOWN)) return false;

        return warden.hasPose(Pose.ROARING);
    }

    @Override
    protected void tick(ServerWorld serverWorld, WardenEntity warden, long l) {
        if (warden.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_DELAY) || warden.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_COOLDOWN)) {
            return;
        }
        warden.getBrain().setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_COOLDOWN, Unit.INSTANCE, WardenAi.ROAR_DURATION - 25);
        warden.playSound(SoundEvents.WARDEN_ROAR, 3.0f, 1.0f);
    }

    @Override
    protected void stop(ServerWorld serverWorld, WardenEntity warden, long l) {
        if (warden.hasPose(Pose.ROARING)) {
            warden.setPose(Pose.STANDING);
        }
        warden.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).ifPresent(warden::setAttackTarget);
        warden.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
    }

}

