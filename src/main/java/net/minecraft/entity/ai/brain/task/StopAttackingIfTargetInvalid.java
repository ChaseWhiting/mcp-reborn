package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class StopAttackingIfTargetInvalid<E extends Mob>
extends Task<E> {
    private static final int TIMEOUT_TO_GET_WITHIN_ATTACK_RANGE = 200;
    private final Predicate<LivingEntity> stopAttackingWhen;
    private final Consumer<E> onTargetErased;

    public StopAttackingIfTargetInvalid(Predicate<LivingEntity> predicate, Consumer<E> consumer) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleStatus.REGISTERED));
        this.stopAttackingWhen = predicate;
        this.onTargetErased = consumer;
    }

    public StopAttackingIfTargetInvalid(Predicate<LivingEntity> predicate) {
        this(predicate, mob -> {});
    }

    public StopAttackingIfTargetInvalid(Consumer<E> consumer) {
        this((LivingEntity livingEntity) -> false, consumer);
    }

    public StopAttackingIfTargetInvalid() {
        this((LivingEntity livingEntity) -> false, mob -> {});
    }

    @Override
    protected void start(ServerWorld serverLevel, E e, long l) {
        LivingEntity livingEntity = this.getAttackTarget(e);
        if (!((LivingEntity)e).canAttack(livingEntity)) {
            this.clearAttackTarget(e);
            return;
        }
        if (StopAttackingIfTargetInvalid.isTiredOfTryingToReachTarget(e)) {
            this.clearAttackTarget(e);
            return;
        }
        if (this.isCurrentTargetDeadOrRemoved(e)) {
            this.clearAttackTarget(e);
            return;
        }
        if (this.isCurrentTargetInDifferentLevel(e)) {
            this.clearAttackTarget(e);
            return;
        }
        if (this.stopAttackingWhen.test(this.getAttackTarget(e))) {
            this.clearAttackTarget(e);
            return;
        }
    }

    private boolean isCurrentTargetInDifferentLevel(E e) {
        return this.getAttackTarget(e).level != ((Mob)e).level;
    }

    private LivingEntity getAttackTarget(E e) {
        return ((LivingEntity)e).getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    private static <E extends LivingEntity> boolean isTiredOfTryingToReachTarget(E e) {
        Optional<Long> optional = e.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        return optional.isPresent() && e.level.getGameTime() - optional.get() > 200L;
    }

    private boolean isCurrentTargetDeadOrRemoved(E e) {
        Optional<LivingEntity> optional = ((LivingEntity)e).getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        return optional.isPresent() && !optional.get().isAlive();
    }

    protected void clearAttackTarget(E e) {
        this.onTargetErased.accept(e);
        ((LivingEntity)e).getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
    }
}

