package net.minecraft.client.animation;

import com.google.common.collect.Maps;
import com.google.common.collect.Lists; // Use Guava Lists instead of Apache Commons
import java.util.List;
import java.util.Map;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationDefinition {
    private final float lengthInSeconds;
    private final boolean looping;
    private final Map<String, List<AnimationChannel>> boneAnimations;

    public AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<AnimationChannel>> boneAnimations) {
        this.lengthInSeconds = lengthInSeconds;
        this.looping = looping;
        this.boneAnimations = boneAnimations;
    }

    public float getLengthInSeconds() {
        return lengthInSeconds;
    }

    public boolean isLooping() {
        return looping;
    }

    public Map<String, List<AnimationChannel>> getBoneAnimations() {
        return boneAnimations;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final float length;
        private final Map<String, List<AnimationChannel>> animationByBone = Maps.newHashMap();
        private boolean looping;

        public static AnimationDefinition.Builder withLength(float length) {
            return new AnimationDefinition.Builder(length);
        }

        private Builder(float length) {
            this.length = length;
        }

        public AnimationDefinition.Builder looping() {
            this.looping = true;
            return this;
        }

        public AnimationDefinition.Builder addAnimation(String bone, AnimationChannel animationChannel) {
            this.animationByBone.computeIfAbsent(bone, k -> Lists.newArrayList()).add(animationChannel); // Use Guava Lists.newArrayList()
            return this;
        }

        public AnimationDefinition build() {
            return new AnimationDefinition(this.length, this.looping, this.animationByBone);
        }
    }
}
