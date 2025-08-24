package net.minecraft.entity.frog;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;


public class Croak
extends Task<FrogEntity> {
    private static final int CROAK_TICKS = 60;
    private static final int TIME_OUT_DURATION = 100;
    private int croakCounter;

    public  Croak() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT), 100);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverLevel, FrogEntity frog) {
        return frog.getPose() == Pose.STANDING;
    }

    @Override
    protected boolean canStillUse(ServerWorld serverLevel, FrogEntity frog, long l) {
        return this.croakCounter < 60;
    }

    @Override
    protected void start(ServerWorld serverLevel, FrogEntity frog, long l) {
        if (frog.isInWaterOrBubble() || frog.isInLava()) {
            return;
        }
        frog.setPose(Pose.CROAKING);
        this.croakCounter = 0;
    }

    @Override
    protected void stop(ServerWorld serverLevel, FrogEntity frog, long l) {
        frog.setPose(Pose.STANDING);
    }

    @Override
    protected void tick(ServerWorld serverLevel, FrogEntity frog, long l) {
        ++this.croakCounter;
    }

}

