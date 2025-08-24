
package net.minecraft.entity.ai.brain.declarative;

import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Unit;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;

import java.util.Optional;
import javax.annotation.Nullable;


public interface MemoryCondition<F extends K1, Value> {
    public MemoryModuleType<Value> memory();

    public MemoryModuleStatus condition();

    @Nullable
    public MemoryAccessor<F, Value> createAccessor(Brain<?> var1, Optional<Value> var2);


    public class Absent<Value> implements MemoryCondition<Const.Mu<Unit>, Value> {
        private final MemoryModuleType<Value> memoryType;

        public Absent(MemoryModuleType<Value> memoryType) {
            this.memoryType = memoryType;
        }

        @Override
        public MemoryModuleType<Value> memory() {
            return memoryType;
        }

        @Override
        public MemoryModuleStatus condition() {
            return MemoryModuleStatus.VALUE_ABSENT;
        }

        @Override
        public MemoryAccessor<Const.Mu<Unit>, Value> createAccessor(Brain<?> var1, Optional<Value> var2) {
            if (var2.isPresent()) {
                return null;
            }
            return new MemoryAccessor<>(var1, memory(), Const.create(Unit.INSTANCE));
        }
    }

    public class Present<Value> implements MemoryCondition<IdF.Mu, Value> {
        private final MemoryModuleType<Value> memoryType;

        public Present(MemoryModuleType<Value> memoryType) {
            this.memoryType = memoryType;
        }

        @Override
        public MemoryModuleType<Value> memory() {
            return memoryType;
        }

        @Override
        public MemoryModuleStatus condition() {
            return MemoryModuleStatus.VALUE_PRESENT;
        }

        @Override
        public MemoryAccessor<IdF.Mu, Value> createAccessor(Brain<?> var1, Optional<Value> var2) {
            return new MemoryAccessor<>(var1, memory(), IdF.create(var2.get()));
        }
    }

    public class Registered<Value> implements MemoryCondition<OptionalBox.Mu, Value> {
        private final MemoryModuleType<Value> memoryType;

        public Registered(MemoryModuleType<Value> memoryType) {
            this.memoryType = memoryType;
        }

        @Override
        public MemoryModuleType<Value> memory() {
            return memoryType;
        }

        @Override
        public MemoryModuleStatus condition() {
            return MemoryModuleStatus.REGISTERED;
        }

        @Override
        public @org.jetbrains.annotations.Nullable MemoryAccessor<OptionalBox.Mu, Value> createAccessor(Brain<?> var1, Optional<Value> var2) {
            return new MemoryAccessor<>(var1, memory(), OptionalBox.create(var2));
        }
    }

}

