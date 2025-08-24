package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.declarative.BehaviorControl;
import net.minecraft.world.server.ServerWorld;

public class TaskBehaviorWrapper<E extends LivingEntity> implements BehaviorControl<E> {
    private final Task<E> task;

    public TaskBehaviorWrapper(Task<E> task) {
        this.task = task;
    }

    @Override
    public Task.Status getStatus() {
        return task.getStatus(); // Adjust this if needed
    }

    @Override
    public boolean tryStart(ServerWorld world, E entity, long time) {
        return task.tryStart(world, entity, time);
    }

    @Override
    public void tickOrStop(ServerWorld world, E entity, long time) {
        task.tickOrStop(world, entity, time);
    }

    @Override
    public void doStop(ServerWorld world, E entity, long time) {
        task.doStop(world, entity, time);
    }

    @Override
    public String debugString() {
        return "Wrapped Task: " + task.getClass().getSimpleName();
    }
}
