/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.declarative;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public abstract class OneShot<E extends LivingEntity> implements net.minecraft.entity.ai.brain.declarative.BehaviorControl<E>, net.minecraft.entity.ai.brain.declarative.Trigger<E> {
    private Task.Status status = Task.Status.STOPPED;

    @Override
    public final Task.Status getStatus() {
        return this.status;
    }

    @Override
    public final boolean tryStart(ServerWorld serverLevel, E e, long l) {
        if (this.trigger(serverLevel, e, l)) {
            this.status = Task.Status.RUNNING;
            return true;
        }
        return false;
    }

    @Override
    public final void tickOrStop(ServerWorld serverLevel, E e, long l) {
        this.doStop(serverLevel, e, l);
    }

    @Override
    public final void doStop(ServerWorld serverLevel, E e, long l) {
        this.status = Task.Status.STOPPED;
    }

    @Override
    public String debugString() {
        return this.getClass().getSimpleName();
    }
}

