package net.minecraft.entity.ai.brain.declarative;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.EraseMemoryIf;
import net.minecraft.entity.ai.brain.task.Task;

import java.util.function.Predicate;

public class EraseMemory {


    public static <T extends LivingEntity> Task<T> eraseIf(Predicate<T> predicate, MemoryModuleType<?> memoryModuleType) {
        return new EraseMemoryIf<>(predicate, memoryModuleType);
    }
}
