package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.declarative.ShufflingList;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GateBehavior<E extends LivingEntity>
extends Task<E> {
    private final Set<MemoryModuleType<?>> exitErasedMemories;
    private final OrderPolicy orderPolicy;
    private final RunningPolicy runningPolicy;
    private final ShufflingList<Task<? super E>> behaviors = new ShufflingList();

    public GateBehavior(Map<MemoryModuleType<?>, MemoryModuleStatus> map, Set<MemoryModuleType<?>> set, OrderPolicy orderPolicy, RunningPolicy runningPolicy, List<Pair<Task<? super E>, Integer>> list) {
        super(map);
        this.exitErasedMemories = set;
        this.orderPolicy = orderPolicy;
        this.runningPolicy = runningPolicy;
        list.forEach(pair -> this.behaviors.add((Task<? super E>) pair.getFirst(), (Integer)pair.getSecond()));
    }

    @Override
    protected boolean canStillUse(ServerWorld serverLevel, E e, long l) {
        return this.behaviors.stream().filter(behavior -> behavior.getStatus() == Task.Status.RUNNING).anyMatch(behavior -> behavior.canStillUse(serverLevel, e, l));
    }

    @Override
    protected boolean timedOut(long l) {
        return false;
    }

    @Override
    protected void start(ServerWorld serverLevel, E e, long l) {
        this.orderPolicy.apply(this.behaviors);
        this.runningPolicy.apply(this.behaviors.stream(), serverLevel, e, l);
    }

    @Override
    protected void tick(ServerWorld serverLevel, E e, long l) {
        this.behaviors.stream().filter(behavior -> behavior.getStatus() == Task.Status.RUNNING).forEach(behavior -> behavior.tickOrStop(serverLevel, e, l));
    }

    @Override
    protected void stop(ServerWorld serverLevel, E e, long l) {
        this.behaviors.stream().filter(behavior -> behavior.getStatus() == Task.Status.RUNNING).forEach(behavior -> behavior.doStop(serverLevel, e, l));
        this.exitErasedMemories.forEach(((LivingEntity)e).getBrain()::eraseMemory);
    }

    @Override
    public String toString() {
        Set set = this.behaviors.stream().filter(behavior -> behavior.getStatus() == Task.Status.RUNNING).collect(Collectors.toSet());
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

    public static enum RunningPolicy {
        RUN_ONE{

            @Override
            public <E extends LivingEntity> void apply(Stream<Task<? super E>> stream, ServerWorld serverLevel, E e, long l) {
                stream.filter(behavior -> behavior.getStatus() == Task.Status.STOPPED).filter(behavior -> behavior.tryStart(serverLevel, e, l)).findFirst();
            }
        }
        ,
        TRY_ALL{

            @Override
            public <E extends LivingEntity> void apply(Stream<Task<? super E>> stream, ServerWorld serverLevel, E e, long l) {
                stream.filter(behavior -> behavior.getStatus() == Task.Status.STOPPED).forEach(behavior -> behavior.tryStart(serverLevel, e, l));
            }
        };


        public abstract <E extends LivingEntity> void apply(Stream<Task<? super E>> var1, ServerWorld var2, E var3, long var4);
    }
}

