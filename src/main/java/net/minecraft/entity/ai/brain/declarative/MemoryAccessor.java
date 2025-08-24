/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.K1
 */
package net.minecraft.entity.ai.brain.declarative;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.K1;
import java.util.Optional;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;

public final class MemoryAccessor<F extends K1, Value> {
    private final Brain<?> brain;
    private final MemoryModuleType<Value> memoryType;
    private final App<F, Value> value;

    public MemoryAccessor(Brain<?> brain, MemoryModuleType<Value> memoryModuleType, App<F, Value> app) {
        this.brain = brain;
        this.memoryType = memoryModuleType;
        this.value = app;
    }

    public App<F, Value> value() {
        return this.value;
    }

    public void set(Value Value2) {
        this.brain.setMemory(this.memoryType, Optional.of(Value2));
    }

    public void setOrErase(Optional<Value> optional) {
        this.brain.setMemory(this.memoryType, optional);
    }

    public void setWithExpiry(Value Value2, long l) {
        this.brain.setMemoryWithExpiry(this.memoryType, Value2, l);
    }

    public void erase() {
        this.brain.eraseMemory(this.memoryType);
    }
}

