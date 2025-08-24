package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.axolotl.AxolotlEntity;
import net.minecraft.world.server.ServerWorld;

public class ValidatePlayDead extends Task<AxolotlEntity> {

    public ValidatePlayDead() {
        super(ImmutableMap.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean timedOut(long time) {
        return false;
    }

    @Override
    protected void start(ServerWorld serverWorld, AxolotlEntity axolotl, long t) {
        Brain<AxolotlEntity> brain = (Brain<AxolotlEntity>) axolotl.getBrain();
        int n = brain.getMemory(MemoryModuleType.PLAY_DEAD_TICKS).get();
        if (n <= 0) {
            brain.eraseMemory(MemoryModuleType.PLAY_DEAD_TICKS);
            brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
            brain.useDefaultActivity();
        } else {
            brain.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, n - 1);
        }
    }
}
