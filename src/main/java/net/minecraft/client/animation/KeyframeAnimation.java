package net.minecraft.client.animation;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class KeyframeAnimation {
    private final AnimationDefinition definition;
    private final List<Entry> entries;
    private final Vector3f scratchVector = new Vector3f();

    private KeyframeAnimation(AnimationDefinition animationDefinition, List<Entry> list) {
        this.definition = animationDefinition;
        this.entries = list;
    }

    static KeyframeAnimation bake(ModelPart modelPart, AnimationDefinition animationDefinition) {
        ArrayList<Entry> arrayList = new ArrayList<Entry>();
        Function<String, ModelPart> function = modelPart.createPartLookup();
        for (Map.Entry<String, List<AnimationChannel>> entry : animationDefinition.getBoneAnimations().entrySet()) {
            String string = entry.getKey();
            List<AnimationChannel> list = entry.getValue();
            ModelPart modelPart2 = function.apply(string);
            if (modelPart2 == null) {
                throw new IllegalArgumentException("Cannot animate " + string + ", which does not exist in model");
            }
            for (AnimationChannel animationChannel : list) {
                arrayList.add(new Entry(modelPart2, animationChannel.getTarget2(), animationChannel.getKeyframes()));
            }
        }
        return new KeyframeAnimation(animationDefinition, List.copyOf(arrayList));
    }

    public void applyStatic() {
        this.apply(0L, 1.0f);
    }

    public void applyWalk(float f, float f2, float f3, float f4) {
        long l = (long)(f * 50.0f * f3);
        float f5 = Math.min(f2 * f4, 1.0f);
        this.apply(l, f5);
    }

    public void apply(AnimationState animationState, float f) {
        this.apply(animationState, f, 1.0f);
    }

    public void apply(AnimationState animationState2, float f, float f2) {
        animationState2.ifStarted(animationState -> this.apply((long)((float)animationState.getTimeInMillis(f) * f2), 1.0f));
    }

    public void apply(long l, float f) {
        float f2 = this.getElapsedSeconds(l);
        for (Entry entry : this.entries) {
            entry.apply(f2, f, this.scratchVector);
        }
    }

    private float getElapsedSeconds(long l) {
        float f = (float)l / 1000.0f;
        return this.definition.isLooping() ? f % this.definition.getLengthInSeconds() : f;
    }

    record Entry(ModelPart part, AnimationChannel.Target2 target, Keyframe[] keyframes) {
        public void apply(float f, float f2, Vector3f vector3f) {
            int n2 = Math.max(0, MathHelper.binarySearch(0, this.keyframes.length, n -> f <= this.keyframes[n].getTimestamp()) - 1);
            int n3 = Math.min(this.keyframes.length - 1, n2 + 1);
            Keyframe keyframe = this.keyframes[n2];
            Keyframe keyframe2 = this.keyframes[n3];
            float f3 = f - keyframe.getTimestamp();
            float f4 = n3 != n2 ? MathHelper.clamp(f3 / (keyframe2.getTimestamp() - keyframe.getTimestamp()), 0.0f, 1.0f) : 0.0f;
            keyframe2.getInterpolation().apply(vector3f, f4, this.keyframes, n2, n3, f2);
            this.target.apply(this.part, vector3f);
        }
    }
}

