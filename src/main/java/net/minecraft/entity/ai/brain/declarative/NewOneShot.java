package net.minecraft.entity.ai.brain.declarative;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;

public abstract class NewOneShot<E extends LivingEntity> extends Task<E> implements Trigger<E> {


    public NewOneShot(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i51504_1_) {
        super(p_i51504_1_);
    }

    @Override
    public boolean tryStart(ServerWorld p_220378_1_, E p_220378_2_, long p_220378_3_) {
        if (this.trigger(p_220378_1_, p_220378_2_, p_220378_3_)) {
            this.status = Status.RUNNING;
            return true;
        }
        return false;
    }

    @Override
    public void tickOrStop(ServerWorld p_220377_1_, E p_220377_2_, long p_220377_3_) {
        this.doStop(p_220377_1_, p_220377_2_, p_220377_3_);
    }

    @Override
    public void doStop(ServerWorld p_220380_1_, E p_220380_2_, long p_220380_3_) {
        this.status = Status.STOPPED;
    }
}
