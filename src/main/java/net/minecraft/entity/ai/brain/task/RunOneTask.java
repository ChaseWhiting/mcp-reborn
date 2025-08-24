package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.declarative.RunOne;
import net.minecraft.world.server.ServerWorld;

public class RunOneTask<E extends LivingEntity> extends Task<E> {
    private final RunOne<E> runOneBehavior;

    public RunOneTask(RunOne<E> runOneBehavior) {
        super(ImmutableMap.of());
        this.runOneBehavior = runOneBehavior;
    }

    @Override
    public Status getStatus() {
        return runOneBehavior.getStatus();
    }



    @Override
    public void doStop(ServerWorld p_220380_1_, E p_220380_2_, long p_220380_3_) {
        runOneBehavior.doStop(p_220380_1_, p_220380_2_, p_220380_3_);
    }

    @Override
    public void tickOrStop(ServerWorld p_220377_1_, E p_220377_2_, long p_220377_3_) {
        runOneBehavior.tickOrStop(p_220377_1_, p_220377_2_, p_220377_3_);
    }



    @Override
    public boolean tryStart(ServerWorld p_220378_1_, E p_220378_2_, long p_220378_3_) {
        return runOneBehavior.tryStart(p_220378_1_, p_220378_2_, p_220378_3_);
    }
}
