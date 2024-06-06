package net.minecraft.client.animation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.util.math.vector.Vector3f;

@OnlyIn(Dist.CLIENT)
public class Keyframe {
    private final float timestamp;
    private final Vector3f target;
    private final AnimationChannel.Interpolation interpolation;

    public Keyframe(float timestamp, Vector3f target, AnimationChannel.Interpolation interpolation) {
        this.timestamp = timestamp;
        this.target = target;
        this.interpolation = interpolation;
    }

    public float getTimestamp() {
        return timestamp;
    }

    public Vector3f getTarget() {
        return target;
    }

    public AnimationChannel.Interpolation getInterpolation() {
        return interpolation;
    }
}
