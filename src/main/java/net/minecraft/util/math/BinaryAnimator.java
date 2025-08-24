package net.minecraft.util.math;


public class BinaryAnimator {
    private final int animationLength;
    private final EasingFunction easingFunction;
    private int ticks;
    private int ticksOld;

    public BinaryAnimator(int n, EasingFunction easingFunction) {
        this.animationLength = n;
        this.easingFunction = easingFunction;
    }

    public BinaryAnimator(int n) {
        this(n, f -> f);
    }

    public void tick(boolean bl) {
        this.ticksOld = this.ticks;
        if (bl) {
            if (this.ticks < this.animationLength) {
                ++this.ticks;
            }
        } else if (this.ticks > 0) {
            --this.ticks;
        }
    }

    public float getFactor(float f) {
        float f2 = MathHelper.lerp(f, this.ticksOld, this.ticks) / (float)this.animationLength;
        return this.easingFunction.apply(f2);
    }

    public static interface EasingFunction {
        public float apply(float var1);
    }
}

