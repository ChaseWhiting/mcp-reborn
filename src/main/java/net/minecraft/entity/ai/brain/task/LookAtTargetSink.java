package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class LookAtTargetSink extends Task<Mob> {

    public LookAtTargetSink(int minTime, int maxTime) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_PRESENT), minTime, maxTime);
    }

    @Override
    protected boolean canStillUse(ServerWorld world, Mob mob, long time) {
        return mob.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).filter(iPosWrapper -> iPosWrapper.isVisibleBy(mob)).isPresent();
    }

    @Override
    protected void stop(ServerWorld world, Mob mob, long time) {
        mob.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void tick(ServerWorld world, Mob mob, long time) {
        mob.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).ifPresent(positionTracker -> mob.getLookControl().setLookAt(positionTracker.currentPosition()));
    }
}
