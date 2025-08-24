package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class StartAttacking<E extends Mob>
extends Task<E> {
    private final Predicate<E> canAttackPredicate;
    private final Function<E, Optional<? extends LivingEntity>> targetFinderFunction;

    public StartAttacking(Predicate<E> predicate, Function<E, Optional<? extends LivingEntity>> function) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleStatus.REGISTERED));
        this.canAttackPredicate = predicate;
        this.targetFinderFunction = function;
    }

    public StartAttacking(Function<E, Optional<? extends LivingEntity>> function) {
        this((E mob) -> true, function);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverLevel, E e) {
        if (!this.canAttackPredicate.test(e)) {
            return false;
        }
        Optional<? extends LivingEntity> optional = this.targetFinderFunction.apply(e);
        if (optional.isPresent()) {
            return ((LivingEntity)e).canAttack(optional.get());
        }
        return false;
    }

    @Override
    protected void start(ServerWorld serverLevel, E e, long l) {
        this.targetFinderFunction.apply(e).ifPresent(livingEntity -> this.setAttackTarget(e, (LivingEntity)livingEntity));
    }

    private void setAttackTarget(E e, LivingEntity livingEntity) {
        ((LivingEntity)e).getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, livingEntity);
        ((LivingEntity)e).getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }
}

