package net.minecraft.entity.ai.brain.declarative;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;

import java.util.List;
import java.util.Map;

public class RunOne<E extends LivingEntity>
extends GateBehavior<E> {
    public RunOne(List<Pair<? extends BehaviorControl<? super E>, Integer>> list) {
        this(ImmutableMap.of(), list);
    }

    public RunOne(Map<MemoryModuleType<?>, MemoryModuleStatus> map, List<Pair<? extends BehaviorControl<? super E>, Integer>> list) {
        super(map, ImmutableSet.of(), GateBehavior.OrderPolicy.SHUFFLED, GateBehavior.RunningPolicy.RUN_ONE, list);
    }
}

