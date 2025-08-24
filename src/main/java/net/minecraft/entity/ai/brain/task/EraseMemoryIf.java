package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Predicate;


public class EraseMemoryIf<E extends LivingEntity>
extends Task<E> {
    private final Predicate<E> predicate;
    private final MemoryModuleType<?> memoryType;

    public EraseMemoryIf(Predicate<E> predicate, MemoryModuleType<?> memoryModuleType) {
        super(ImmutableMap.of(memoryModuleType, MemoryModuleStatus.VALUE_PRESENT));
        this.predicate = predicate;
        this.memoryType = memoryModuleType;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverLevel, E e) {
        return this.predicate.test(e);
    }

    @Override
    protected void start(ServerWorld serverLevel, E e, long l) {
        ((LivingEntity)e).getBrain().eraseMemory(this.memoryType);
    }
}

