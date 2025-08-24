package net.minecraft.entity.ai.brain.declarative;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GateBehavior<E extends LivingEntity>
implements BehaviorControl<E> {
    private final Map<MemoryModuleType<?>, MemoryModuleStatus> entryCondition;
    private final Set<MemoryModuleType<?>> exitErasedMemories;
    private final OrderPolicy orderPolicy;
    private final RunningPolicy runningPolicy;
    private final ShufflingList<BehaviorControl<? super E>> behaviors = new ShufflingList<>();
    private Task.Status status = Task.Status.STOPPED;

    public GateBehavior(Map<MemoryModuleType<?>, MemoryModuleStatus> map, Set<MemoryModuleType<?>> set, OrderPolicy orderPolicy, RunningPolicy runningPolicy, List<Pair<? extends BehaviorControl<? super E>, Integer>> list) {
        this.entryCondition = map;
        this.exitErasedMemories = set;
        this.orderPolicy = orderPolicy;
        this.runningPolicy = runningPolicy;
        list.forEach(pair -> this.behaviors.add((BehaviorControl)pair.getFirst(), (Integer)pair.getSecond()));
    }

    @Override
    public Task.Status getStatus() {
        return this.status;
    }

    private boolean hasRequiredMemories(E e) {
        for (Map.Entry<MemoryModuleType<?>, MemoryModuleStatus> entry : this.entryCondition.entrySet()) {
            MemoryModuleType<?> memoryModuleType = entry.getKey();
            MemoryModuleStatus memoryStatus = entry.getValue();
            if (((LivingEntity)e).getBrain().checkMemory(memoryModuleType, memoryStatus)) continue;
            return false;
        }
        return true;
    }

    @Override
    public final boolean tryStart(ServerWorld serverLevel, E e, long l) {
        if (this.hasRequiredMemories(e)) {
            this.status = Task.Status.RUNNING;
            this.orderPolicy.apply(this.behaviors);
            this.runningPolicy.apply(this.behaviors.stream(), serverLevel, e, l);
            return true;
        }
        return false;
    }

    @Override
    public final void tickOrStop(ServerWorld serverLevel, E e, long l) {
        this.behaviors.stream().filter(behaviorControl -> behaviorControl.getStatus() == Task.Status.RUNNING).forEach(behaviorControl -> behaviorControl.tickOrStop(serverLevel, e, l));
        if (this.behaviors.stream().noneMatch(behaviorControl -> behaviorControl.getStatus() == Task.Status.RUNNING)) {
            this.doStop(serverLevel, e, l);
        }
    }

    @Override
    public final void doStop(ServerWorld serverLevel, E e, long l) {
        this.status = Task.Status.STOPPED;
        this.behaviors.stream().filter(behaviorControl -> behaviorControl.getStatus() == Task.Status.RUNNING).forEach(behaviorControl -> behaviorControl.doStop(serverLevel, e, l));
        this.exitErasedMemories.forEach(((LivingEntity)e).getBrain()::eraseMemory);
    }

    @Override
    public String debugString() {
        return this.getClass().getSimpleName();
    }

    public String toString() {
        Set set = this.behaviors.stream().filter(behaviorControl -> behaviorControl.getStatus() == Task.Status.RUNNING).collect(Collectors.toSet());
        return "(" + this.getClass().getSimpleName() + "): " + set;
    }

    public static enum OrderPolicy {
        ORDERED(shufflingList -> {}),
        SHUFFLED(ShufflingList::shuffle);

        private final Consumer<ShufflingList<?>> consumer;

        private OrderPolicy(Consumer<ShufflingList<?>> consumer) {
            this.consumer = consumer;
        }

        public void apply(ShufflingList<?> shufflingList) {
            this.consumer.accept(shufflingList);
        }
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static enum RunningPolicy {
        RUN_ONE{

            @Override
            public <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> stream, ServerWorld serverLevel, E e, long l) {
                stream.filter(behaviorControl -> behaviorControl.getStatus() == Task.Status.STOPPED).filter(behaviorControl -> behaviorControl.tryStart(serverLevel, e, l)).findFirst();
            }
        }
        ,
        TRY_ALL{

            @Override
            public <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> stream, ServerWorld serverLevel, E e, long l) {
                stream.filter(behaviorControl -> behaviorControl.getStatus() == Task.Status.STOPPED).forEach(behaviorControl -> behaviorControl.tryStart(serverLevel, e, l));
            }
        };


        public abstract <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> var1, ServerWorld var2, E var3, long var4);
    }
}

