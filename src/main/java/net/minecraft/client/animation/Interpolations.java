package net.minecraft.client.animation;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Random;

public class Interpolations {
    public static final AnimationChannel.Interpolation LINEAR = (result, t, keyframes, start, end, scale) -> {
        Vector3f startPos = keyframes[start].getTarget();
        Vector3f endPos = keyframes[end].getTarget();
        return startPos.lerp(endPos, t, result).mulA(scale);
    };

    public static final AnimationChannel.Interpolation CATMULLROM = (vector, progress, keyframes, startIndex, endIndex, factor) -> {
        Vector3f vector1 = keyframes[Math.max(0, startIndex - 1)].getTarget();
        Vector3f vector2 = keyframes[startIndex].getTarget();
        Vector3f vector3 = keyframes[endIndex].getTarget();
        Vector3f vector4 = keyframes[Math.min(keyframes.length - 1, endIndex + 1)].getTarget();
        return new Vector3f(
                catmullRom(progress, vector1.x(), vector2.x(), vector3.x(), vector4.x()) * factor,
                catmullRom(progress, vector1.y(), vector2.z(), vector3.y(), vector4.y()) * factor,
                catmullRom(progress, vector1.z(), vector2.z(), vector3.z(), vector4.z()) * factor
        );


    };

    public static float catmullRom(float t, float p0, float p1, float p2, float p3) {
        return 0.5F * (2.0F * p1 + (p2 - p0) * t + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * t * t + (3.0F * p1 - p0 - 3.0F * p2 + p3) * t * t * t);
    }

    public static final AnimationChannel.Interpolation EXPONENTIAL = (vector, progress, keyframes, startIndex, endIndex, factor) -> {
        float t = (float) Math.pow(progress, 2); // Exponential easing
        Vector3f startVector = keyframes[startIndex].getTarget();
        Vector3f endVector = keyframes[endIndex].getTarget();

        Vector3f result = new Vector3f(
                MathHelper.lerp(t, startVector.x(), endVector.x()) * factor,
                MathHelper.lerp(t, startVector.y(), endVector.y()) * factor,
                MathHelper.lerp(t, startVector.z(), endVector.z()) * factor
        );
        return result;
    };

    public static final AnimationChannel.Interpolation BOUNCE = (vector, progress, keyframes, startIndex, endIndex, factor) -> {
        float t = (float) Math.abs(Math.sin(6.28 * progress * (1 - progress))); // Simplistic bounce effect
        Vector3f startVector = keyframes[startIndex].getTarget();
        Vector3f endVector = keyframes[endIndex].getTarget();

        Vector3f result = new Vector3f(
                MathHelper.lerp(t, startVector.x(), endVector.x()) * factor,
                MathHelper.lerp(t, startVector.y(), endVector.y()) * factor,
                MathHelper.lerp(t, startVector.z(), endVector.z()) * factor
        );
        return result;
    };

    public static final AnimationChannel.Interpolation WEIRD = (vector, progress, keyframes, startIndex, endIndex, factor) -> {
        Random random = new Random((long) (progress * 10000)); // Seeded with progress for reproducibility
        float t = progress;

        Vector3f startVector = keyframes[startIndex].getTarget();
        Vector3f endVector = keyframes[endIndex].getTarget();

        // Random offset for weirdness
        float xOffset = (random.nextFloat() - 0.5f) * 2 * (1 - progress); // Gets smaller as progress increases
        float yOffset = (random.nextFloat() - 0.5f) * 2 * progress;       // Gets larger as progress increases
        float zOffset = (random.nextFloat() - 0.5f) * 2;                  // Stays chaotic throughout

        // Interpolated values with chaos added
        Vector3f result = new Vector3f(
                MathHelper.lerp(t, startVector.x(), endVector.x()) * factor + xOffset,
                MathHelper.lerp(t, startVector.y(), endVector.y()) * factor + yOffset,
                MathHelper.lerp(t, startVector.z(), endVector.z()) * factor + zOffset
        );

        return result;
    };
}
