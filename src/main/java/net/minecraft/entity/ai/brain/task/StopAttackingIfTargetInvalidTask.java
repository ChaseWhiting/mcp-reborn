package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.memory.MemStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class StopAttackingIfTargetInvalidTask<E extends Mob> extends Task<E> {
    private final Predicate<LivingEntity> predicate;
    private final boolean shouldStopIfTired;
    private final BiConsumer<E, LivingEntity> onStop;

    public StopAttackingIfTargetInvalidTask(Predicate<LivingEntity> predicate, BiConsumer<E, LivingEntity> biConsumer, boolean bl) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemStatus.PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemStatus.REGISTERED));
        this.predicate = predicate;
        this.shouldStopIfTired = bl;
        this.onStop = biConsumer;
    }

    @Override
    protected void start(ServerWorld world, E e, long time) {
        LivingEntity livingEntity = (LivingEntity) e.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        if (!e.canAttack(livingEntity) || shouldStopIfTired && isTiredOfTryingToReachTarget(e, Optional.of(time))
        || !livingEntity.isAlive() || livingEntity.level != e.level || predicate.test(livingEntity)) {
            onStop.accept(e, livingEntity);
            e.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        }
    }

    private static boolean isTiredOfTryingToReachTarget(LivingEntity livingEntity, Optional<Long> optional) {
        return optional.isPresent() && livingEntity.level.getGameTime() - optional.get() > 200L;
    }
}
