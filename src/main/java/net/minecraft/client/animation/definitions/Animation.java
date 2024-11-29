package net.minecraft.client.animation.definitions;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Interpolations;
import net.minecraft.client.animation.Targets;
import net.minecraft.util.math.vector.Vector3f;

public abstract class Animation {

    public static Vector3f posVec(float x, float y, float z) {
        return new Vector3f(x, -y, z);
    }

    public static Vector3f degreeVec(float x, float y, float z) {
        return new Vector3f(x * ((float) Math.PI / 180F), y * ((float) Math.PI / 180F), z * ((float) Math.PI / 180F));
    }

    public static Vector3f scaleVec(double x, double y, double z) {
        return new Vector3f((float) (x - 1.0D), (float) (y - 1.0D), (float) (z - 1.0D));
    }

    public abstract ImmutableSet<AnimationDefinition> getAnimations();

    public static AnimationChannel.Interpolation CATMULLROM = Interpolations.CATMULLROM;
    public static AnimationChannel.Interpolation LINEAR = Interpolations.LINEAR;

    public static AnimationChannel.Target ROTATION = Targets.ROTATION;
}
