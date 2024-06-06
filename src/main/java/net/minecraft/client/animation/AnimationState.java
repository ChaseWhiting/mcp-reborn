package net.minecraft.client.animation;

import java.util.function.Consumer;
import net.minecraft.util.math.MathHelper;

public class AnimationState {
    private static final long STOPPED = Long.MAX_VALUE;
    private long lastTime = Long.MAX_VALUE;
    private long accumulatedTime;

    public void start(int ticks) {
        this.lastTime = (long) ticks * 1000L / 20L;
        this.accumulatedTime = 0L;
    }

    public void startIfStopped(int ticks) {
        if (!this.isStarted()) {
            this.start(ticks);
        }
    }

    public void animateWhen(boolean condition, int ticks) {
        if (condition) {
            this.startIfStopped(ticks);
        } else {
            this.stop();
        }
    }

    public void stop() {
        this.lastTime = Long.MAX_VALUE;
    }

    public void ifStarted(Consumer<AnimationState> consumer) {
        if (this.isStarted()) {
            consumer.accept(this);
        }
    }

    public void updateTime(float partialTicks, float multiplier) {
        if (this.isStarted()) {
            long currentTime = MathHelper.floor((double) (partialTicks * 1000.0F / 20.0F));
            this.accumulatedTime += (long) ((float) (currentTime - this.lastTime) * multiplier);
            this.lastTime = currentTime;
        }
    }

    public long getAccumulatedTime() {
        return this.accumulatedTime;
    }

    public boolean isStarted() {
        return this.lastTime != Long.MAX_VALUE;
    }
}
