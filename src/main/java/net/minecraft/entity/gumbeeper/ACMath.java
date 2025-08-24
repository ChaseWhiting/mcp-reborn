package net.minecraft.entity.gumbeeper;

import net.minecraft.client.alexsmobsport.citadel.advanced.Animation;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;

public class ACMath {

    public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    public static final Direction[] NOT_UP_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN};
    public static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);

    public static final float QUARTER_PI = ((float)Math.PI / 4F);

    public static float smin(float a, float b, float k) {
        float h = Math.max(k - Math.abs(a - b), 0.0F) / k;
        return Math.min(a, b) - h * h * k * (1.0F / 4.0F);
    }

    public static float cullAnimationTick(int tick, float amplitude, Animation animation, float partialTick, int startOffset) {
        return cullAnimationTick(tick, amplitude, animation, partialTick, startOffset, animation.getDuration() - startOffset);
    }

    public static float cullAnimationTick(int tick, float amplitude, Animation animation, float partialTick, int startOffset, int endAt) {
        float i = MathHelper.clamp(tick + partialTick - startOffset, 0, endAt);
        float f = (float) Math.sin((i / (float) (endAt)) * Math.PI) * amplitude;
        return ACMath.smin(f, 1.0F, 0.1F);
    }



    public static float walkValue(float limbSwing, float limbSwingAmount, float speed, float offset, float degree, boolean inverse) {
        return (float) ((Math.cos(limbSwing * speed + offset) * degree * limbSwingAmount) * (inverse ? -1 : 1));
    }

    public static float approachRotation(float current, float target, float max) {
        float f = MathHelper.wrapDegrees(target - current);
        if (f > max) {
            f = max;
        }

        if (f < -max) {
            f = -max;
        }

        return MathHelper.wrapDegrees(current + f);
    }
}