package net.minecraft.client.animation;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.util.math.vector.Vector3f;

@OnlyIn(Dist.CLIENT)
public class AnimationChannel {
    private final Target target;
    private final Keyframe[] keyframes;

    public Keyframe[] getKeyframes() {
        return keyframes;
    }

    public Target getTarget() {
        return target;
    }

    public Target2 getTarget2() {
        if (this.target == Targets.POSITION) {
            return Targets2.POSITION;
        }

        if (this.target == Targets.ROTATION) {
            return Targets2.ROTATION;
        }

        if (this.target == Targets.SCALE) {
            return Targets2.SCALE;
        }


        return Targets2.POSITION;
    }

    public AnimationChannel(Target target, Keyframe... keyframes) {
        this.target = target;
        this.keyframes = keyframes;
    }

    public AnimationChannel(String target, Keyframe... keyframes) {
        this.target = switch (target) {
            case "rotation", "rot", "ROTATION" -> Targets.ROTATION;
            case "position", "pos", "POSITION" -> Targets.POSITION;
            default -> Targets.SCALE;
        };
        this.keyframes = keyframes;
    }

    public interface Interpolation {
        Vector3f apply(Vector3f vector, float progress, Keyframe[] keyframes, int startIndex, int endIndex, float factor);
    }

    public interface Target {
        void apply(ModelRenderer modelPart, Vector3f vector);
    }

    public interface Target2 {
        void apply(ModelPart modelPart, Vector3f vector);
    }

    public static class Targets {
        public static final AnimationChannel.Target POSITION = net.minecraft.client.animation.Targets.POSITION;
        public static final AnimationChannel.Target POSITION_OFFSET = net.minecraft.client.animation.Targets.POSITION_OFFSET;

        public static final AnimationChannel.Target POSITION_ADD = net.minecraft.client.animation.Targets.POSITION_ADD;
        public static final AnimationChannel.Target POSITION_REAL = net.minecraft.client.animation.Targets.POSITION_REAL;

        public static final AnimationChannel.Target ROTATION = net.minecraft.client.animation.Targets.ROTATION;
        public static final AnimationChannel.Target SCALE = net.minecraft.client.animation.Targets.SCALE;

    }

    public static class Targets2 {

        public static final AnimationChannel.Target2 POSITION = net.minecraft.client.animation.Targets2.POSITION;

        public static final AnimationChannel.Target2 ROTATION = net.minecraft.client.animation.Targets2.ROTATION;
        public static final AnimationChannel.Target2 SCALE = net.minecraft.client.animation.Targets2.SCALE;
    }

    public static class Interpolations {
        public static final AnimationChannel.Interpolation LINEAR = net.minecraft.client.animation.Interpolations.LINEAR;

        public static final AnimationChannel.Interpolation CATMULLROM = net.minecraft.client.animation.Interpolations.CATMULLROM;

        public static final AnimationChannel.Interpolation EXPONENTIAL = net.minecraft.client.animation.Interpolations.EXPONENTIAL;

    }

}
