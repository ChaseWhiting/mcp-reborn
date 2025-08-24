/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.declarative.BehaviorControl;
import net.minecraft.world.server.ServerWorld;

public class DoNothing
implements BehaviorControl<LivingEntity> {
    private final int minDuration;
    private final int maxDuration;
    private Task.Status status = Task.Status.STOPPED;
    private long endTimestamp;

    public DoNothing(int n, int n2) {
        this.minDuration = n;
        this.maxDuration = n2;
    }

    @Override
    public Task.Status getStatus() {
        return this.status;
    }

    @Override
    public final boolean tryStart(ServerWorld serverLevel, LivingEntity livingEntity, long l) {
        this.status = Task.Status.RUNNING;
        int n = this.minDuration + serverLevel.getRandom().nextInt(this.maxDuration + 1 - this.minDuration);
        this.endTimestamp = l + (long)n;
        return true;
    }

    @Override
    public final void tickOrStop(ServerWorld serverLevel, LivingEntity livingEntity, long l) {
        if (l > this.endTimestamp) {
            this.doStop(serverLevel, livingEntity, l);
        }
    }

    @Override
    public final void doStop(ServerWorld serverLevel, LivingEntity livingEntity, long l) {
        this.status = Task.Status.STOPPED;
    }

    @Override
    public String debugString() {
        return this.getClass().getSimpleName();
    }
}

