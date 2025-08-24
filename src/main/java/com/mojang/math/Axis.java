package com.mojang.math;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@FunctionalInterface
public interface Axis {
    public static final Axis XN = f -> new Quaternionf().rotationX(-f);
    public static final Axis XP = f -> new Quaternionf().rotationX(f);
    public static final Axis YN = f -> new Quaternionf().rotationY(-f);
    public static final Axis YP = f -> new Quaternionf().rotationY(f);
    public static final Axis ZN = f -> new Quaternionf().rotationZ(-f);
    public static final Axis ZP = f -> new Quaternionf().rotationZ(f);

    public static Axis of(Vector3f vector3f) {
        return f -> new Quaternionf().rotationAxis(f, (Vector3fc)vector3f);
    }

    public Quaternionf rotation(float var1);

    default public Quaternionf rotationDegrees(float f) {
        return this.rotation(f * ((float)Math.PI / 180));
    }
}