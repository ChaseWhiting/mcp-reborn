package net.minecraft.entity.ai.brain.declarative;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;


public interface BehaviorControl<E extends LivingEntity> {
    public Task.Status getStatus();

    public boolean tryStart(ServerWorld var1, E var2, long var3);

    public void tickOrStop(ServerWorld var1, E var2, long var3);

    public void doStop(ServerWorld var1, E var2, long var3);

    public String debugString();
}

