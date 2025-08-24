package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class RunIf<E extends LivingEntity>
extends Task<E> {
    private final Predicate<E> predicate;
    private final Task<? super E> wrappedBehavior;
    private final boolean checkWhileRunningAlso;

    public RunIf(Map<MemoryModuleType<?>, MemoryModuleStatus> map, Predicate<E> predicate, Task<? super E> behavior, boolean bl) {
        super(RunIf.mergeMaps(map, behavior.entryCondition));
        this.predicate = predicate;
        this.wrappedBehavior = behavior;
        this.checkWhileRunningAlso = bl;
    }

    private static Map<MemoryModuleType<?>, MemoryModuleStatus> mergeMaps(Map<MemoryModuleType<?>, MemoryModuleStatus> map, Map<MemoryModuleType<?>, MemoryModuleStatus> map2) {
        HashMap hashMap = Maps.newHashMap();
        hashMap.putAll(map);
        hashMap.putAll(map2);
        return hashMap;
    }

    public RunIf(Predicate<E> predicate, Task<? super E> behavior, boolean bl) {
        this(ImmutableMap.of(), predicate, behavior, bl);
    }

    public RunIf(Predicate<E> predicate, Task<? super E> behavior) {
        this(ImmutableMap.of(), predicate, behavior, false);
    }

    public RunIf(Map<MemoryModuleType<?>, MemoryModuleStatus> map, Task<? super E> behavior) {
        this(map, livingEntity -> true, behavior, false);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverLevel, E e) {
        return this.predicate.test(e) && this.wrappedBehavior.checkExtraStartConditions(serverLevel, e);
    }

    @Override
    protected boolean canStillUse(ServerWorld serverLevel, E e, long l) {
        return this.checkWhileRunningAlso && this.predicate.test(e) && this.wrappedBehavior.canStillUse(serverLevel, e, l);
    }

    @Override
    protected boolean timedOut(long l) {
        return false;
    }

    @Override
    protected void start(ServerWorld serverLevel, E e, long l) {
        this.wrappedBehavior.start(serverLevel, e, l);
    }

    @Override
    protected void tick(ServerWorld serverLevel, E e, long l) {
        this.wrappedBehavior.tick(serverLevel, e, l);
    }

    @Override
    protected void stop(ServerWorld serverLevel, E e, long l) {
        this.wrappedBehavior.stop(serverLevel, e, l);
    }

    @Override
    public String toString() {
        return "RunIf: " + this.wrappedBehavior;
    }
}

