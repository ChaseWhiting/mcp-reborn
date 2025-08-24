package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class CountDownCooldownTicks extends Task<LivingEntity> {
    private final MemoryModuleType<Integer> cooldownTicks;

    public CountDownCooldownTicks(MemoryModuleType<Integer> memoryModuleType) {
        super(ImmutableMap.of(memoryModuleType, MemoryModuleStatus.VALUE_PRESENT));
        this.cooldownTicks = memoryModuleType;
    }

    private Optional<Integer> getCooldownTickMemory(LivingEntity livingEntity) {
        return livingEntity.getBrain().getMemory(this.cooldownTicks);
    }

    @Override
    protected boolean timedOut(long time) {
        return false;
    }

    @Override
    protected boolean canStillUse(ServerWorld serverLevel, LivingEntity livingEntity, long l) {
        Optional<Integer> optional = this.getCooldownTickMemory(livingEntity);
        return optional.isPresent() && optional.get() > 0;
    }

    @Override
    protected void tick(ServerWorld serverLevel, LivingEntity livingEntity, long l) {
        Optional<Integer> optional = this.getCooldownTickMemory(livingEntity);
        livingEntity.getBrain().setMemory(this.cooldownTicks, optional.get() - 1);
    }

    @Override
    protected void stop(ServerWorld serverLevel, LivingEntity livingEntity, long l) {
        livingEntity.getBrain().eraseMemory(this.cooldownTicks);
    }
}
