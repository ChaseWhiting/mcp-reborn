package net.minecraft.client.animation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.client.animation.definitions.Animation;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.util.math.vector.Vector3f;

@OnlyIn(Dist.CLIENT)
public class KeyframeAnimations {
    public static void animate(HierarchicalModel<?> model, AnimationDefinition animationDefinition, long currentTime, float partialTicks, Vector3f tempVector) {
        float elapsedSeconds = getElapsedSeconds(animationDefinition, currentTime);

        // Iterate over each bone and its animation channels
        for (Map.Entry<String, List<AnimationChannel>> entry : animationDefinition.getBoneAnimations().entrySet()) {
            Optional<ModelRenderer> optional = model.getAnyDescendantWithName(entry.getKey());
            List<AnimationChannel> animationChannels = entry.getValue();

            optional.ifPresent((modelPart) -> {
                animationChannels.forEach((animationChannel) -> {
                    Keyframe[] keyframes = animationChannel.getKeyframes();

                    // Binary search to find the current keyframe index
                    int i = Math.max(0, MathHelper.binarySearch(0, keyframes.length, (index) -> {
                        return elapsedSeconds <= keyframes[index].getTimestamp();
                    }) - 1);
                    int j = Math.min(keyframes.length - 1, i + 1);

                    Keyframe keyframeStart = keyframes[i];
                    Keyframe keyframeEnd = keyframes[j];
                    float deltaTime = elapsedSeconds - keyframeStart.getTimestamp();
                    float progress;

                    // Calculate progress between the two keyframes
                    if (j != i) {
                        progress = MathHelper.clamp(deltaTime / (keyframeEnd.getTimestamp() - keyframeStart.getTimestamp()), 0.0F, 1.0F);
                    } else {
                        progress = 0.0F;
                    }

                    // Get the start and end vectors from the two keyframes
                    Vector3f startVector = keyframeStart.getTarget();    // Target values for the start keyframe
                    Vector3f endVector = keyframeEnd.getTarget();     // Target values for the end keyframe

                    tempVector.set(
                            MathHelper.lerp(progress, startVector.x(), endVector.x()),
                            MathHelper.lerp(progress, startVector.y(), endVector.y()),
                            MathHelper.lerp(progress, startVector.z(), endVector.z())
                    );

                    // Use the interpolation from the end keyframe to apply the transformation
                    keyframeEnd.getInterpolation().apply(tempVector, progress, keyframes, i, j, partialTicks);

                    // Apply the result to the modelPart using the channel's target (preserving the vector set at the end)
                    animationChannel.getTarget().apply(modelPart, tempVector);
                });
            });
        }
    }

    private static float getElapsedSeconds(AnimationDefinition animationDefinition, long currentTime) {
        float seconds = (float) currentTime / 1000.0F;
        return animationDefinition.isLooping() ? seconds % animationDefinition.getLengthInSeconds() : seconds;
    }

    public static Vector3f posVec(float x, float y, float z) {
        return Animation.posVec(x, y, z);
    }

    public static Vector3f degreeVec(float x, float y, float z) {
        return Animation.degreeVec(x, y, z);
    }

    public static Vector3f scaleVec(double x, double y, double z) {
        return Animation.scaleVec(x, y, z);
    }
}
