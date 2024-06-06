package net.minecraft.client.animation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.util.math.vector.Vector3f;

@OnlyIn(Dist.CLIENT)
public class KeyframeAnimations {
    public static void animate(HierarchicalModel<?> model, AnimationDefinition animationDefinition, long currentTime, float partialTicks, Vector3f tempVector) {
        float elapsedSeconds = getElapsedSeconds(animationDefinition, currentTime);
        for (Map.Entry<String, List<AnimationChannel>> entry : animationDefinition.getBoneAnimations().entrySet()) {
            Optional<ModelRenderer> optional = model.getAnyDescendantWithName(entry.getKey());
            List<AnimationChannel> animationChannels = entry.getValue();
            optional.ifPresent((modelPart) -> {
                animationChannels.forEach((animationChannel) -> {
                    Keyframe[] keyframes = animationChannel.getKeyframes();
                    int i = Math.max(0, MathHelper.binarySearch(0, keyframes.length, (index) -> {
                        return elapsedSeconds <= keyframes[index].getTimestamp();
                    }) - 1);
                    int j = Math.min(keyframes.length - 1, i + 1);
                    Keyframe keyframe = keyframes[i];
                    Keyframe keyframe1 = keyframes[j];
                    float deltaTime = elapsedSeconds - keyframe.getTimestamp();
                    float progress;
                    if (j != i) {
                        progress = MathHelper.clamp(deltaTime / (keyframe1.getTimestamp() - keyframe.getTimestamp()), 0.0F, 1.0F);
                    } else {
                        progress = 0.0F;
                    }

                    keyframe1.getInterpolation().apply(tempVector, progress, keyframes, i, j, partialTicks);
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
        return new Vector3f(x, -y, z);
    }

    public static Vector3f degreeVec(float x, float y, float z) {
        return new Vector3f(x * ((float) Math.PI / 180F), y * ((float) Math.PI / 180F), z * ((float) Math.PI / 180F));
    }

    public static Vector3f scaleVec(double x, double y, double z) {
        return new Vector3f((float) (x - 1.0D), (float) (y - 1.0D), (float) (z - 1.0D));
    }
}
