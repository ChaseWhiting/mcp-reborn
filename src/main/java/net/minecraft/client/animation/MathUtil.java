package net.minecraft.client.animation;

public class MathUtil {
    public static float catmullRom(float t, float p0, float p1, float p2, float p3) {
        return 0.5F * (2.0F * p1 + (p2 - p0) * t + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * t * t + (3.0F * p1 - p0 - 3.0F * p2 + p3) * t * t * t);
    }
}
