package net.minecraft.client.animation;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
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

    public AnimationChannel(Target target, Keyframe... keyframes) {
        this.target = target;
        this.keyframes = keyframes;
    }

    public interface Interpolation {
        Vector3f apply(Vector3f vector, float progress, Keyframe[] keyframes, int startIndex, int endIndex, float factor);
    }

    public static class Interpolations {
        public static final Interpolation LINEAR = (vector, progress, keyframes, startIndex, endIndex, factor) -> {
            Vector3f startVector = keyframes[startIndex].getTarget();
            Vector3f endVector = keyframes[endIndex].getTarget();
            return new Vector3f(
                MathHelper.lerp(progress, startVector.x(), endVector.x()) * factor,
                MathHelper.lerp(progress, startVector.y(), endVector.y()) * factor,
                MathHelper.lerp(progress, startVector.z(), endVector.z()) * factor
            );
        };

        public static final Interpolation CATMULLROM = (vector, progress, keyframes, startIndex, endIndex, factor) -> {
            Vector3f vector1 = keyframes[Math.max(0, startIndex - 1)].getTarget();
            Vector3f vector2 = keyframes[startIndex].getTarget();
            Vector3f vector3 = keyframes[endIndex].getTarget();
            Vector3f vector4 = keyframes[Math.min(keyframes.length - 1, endIndex + 1)].getTarget();
            return new Vector3f(
                MathUtil.catmullRom(progress, vector1.x(), vector2.x(), vector3.x(), vector4.x()) * factor,
                MathUtil.catmullRom(progress, vector1.y(), vector2.z(), vector3.y(), vector4.y()) * factor,
                MathUtil.catmullRom(progress, vector1.z(), vector2.z(), vector3.z(), vector4.z()) * factor
            );
        };
    }

    public interface Target {
        void apply(ModelRenderer modelPart, Vector3f vector);
    }

    public static class Targets {
        public static final Target POSITION = (modelPart, vector) -> {
            modelPart.xRot += vector.z();
            modelPart.yRot += vector.y();
            modelPart.zRot += vector.z();
        };
        public static final Target ROTATION = (modelPart, vector) -> {
            modelPart.xRot += vector.x();
            modelPart.yRot += vector.y();
            modelPart.zRot += vector.z();
        };
        public static final Target SCALE = (modelPart, vector) -> {
            if (modelPart instanceof ModelRenderer) {
                ((ModelRenderer) modelPart).applyScale(vector);
            }
        };
    }
}
