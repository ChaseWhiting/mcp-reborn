/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.animation.definitions;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.*;

public class BatAnimation extends Animation {
    @Override
    public ImmutableSet<AnimationDefinition> getAnimations() {
        return ImmutableSet.of(BAT_RESTING, BAT_FLYING, PALE_BAT_FLYING);
    }

    public static final AnimationDefinition BAT_RESTING = AnimationDefinition.Builder.withLength(0.5f).looping().addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(180.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.5f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(180.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.5f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("feet", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, -10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_wing", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 1.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_wing_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, -120.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_wing", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 1.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_wing_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 120.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).build();


    public static final AnimationDefinition PALE_BAT_FLYING = AnimationDefinition.Builder.withLength(0.7f)
            .looping()
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0f, KeyframeAnimations.degreeVec(80.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.15f, KeyframeAnimations.degreeVec(86.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45f, KeyframeAnimations.degreeVec(84, 0, 0), CATMULLROM),
                    new Keyframe(0.7f, KeyframeAnimations.degreeVec(80.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0f, KeyframeAnimations.degreeVec(75.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.35f, KeyframeAnimations.degreeVec(80.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7f, KeyframeAnimations.degreeVec(75.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("right_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 45.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),  // Reduced downward angle
                    new Keyframe(0.2f, KeyframeAnimations.degreeVec(0.0f, -40.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, 55.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7f, KeyframeAnimations.degreeVec(0.0f, 45.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)))  // Upper angle remains higher
            .addAnimation("right_wing_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 12.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.06f, KeyframeAnimations.degreeVec(0.0f, 60.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25f, KeyframeAnimations.degreeVec(0.0f, -95.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),  // Adjusted tip rotation
                    new Keyframe(0.7f, KeyframeAnimations.degreeVec(0.0f, 12.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("left_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, -45.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2f, KeyframeAnimations.degreeVec(0.0f, 40.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, -55.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7f, KeyframeAnimations.degreeVec(0.0f, -45.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("left_wing_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, -12.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.06f, KeyframeAnimations.degreeVec(0.0f, -60.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25f, KeyframeAnimations.degreeVec(0.0f, 95.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),  // Adjusted tip rotation
                    new Keyframe(0.7f, KeyframeAnimations.degreeVec(0.0f, -12.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)))
            .build();





    public static final AnimationDefinition BAT_FLYING = AnimationDefinition.Builder.withLength(0.5f).looping().addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.125f, KeyframeAnimations.degreeVec(20.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.125f, KeyframeAnimations.posVec(0.0f, 2.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, KeyframeAnimations.posVec(0.0f, 1.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.375f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4583f, KeyframeAnimations.posVec(0.0f, -1.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(40.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, KeyframeAnimations.degreeVec(52.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(40.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.125f, KeyframeAnimations.posVec(0.0f, 2.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, KeyframeAnimations.posVec(0.0f, 1.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.375f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4583f, KeyframeAnimations.posVec(0.0f, -1.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("feet", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(10.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.125f, KeyframeAnimations.degreeVec(-21.25f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, KeyframeAnimations.degreeVec(-12.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(10.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 85.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.125f, KeyframeAnimations.degreeVec(0.0f, -55.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, KeyframeAnimations.degreeVec(0.0f, 50.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.375f, KeyframeAnimations.degreeVec(0.0f, 70.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, 85.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_wing_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 10.5f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0417f, KeyframeAnimations.degreeVec(0.0f, 65.5f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2083f, KeyframeAnimations.degreeVec(0.0f, -135.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, 10.5f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, -85.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.125f, KeyframeAnimations.degreeVec(0.0f, 55.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, KeyframeAnimations.degreeVec(0.0f, -50.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.375f, KeyframeAnimations.degreeVec(0.0f, -70.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, -85.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_wing_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, -10.5f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0417f, KeyframeAnimations.degreeVec(0.0f, -65.5f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2083f, KeyframeAnimations.degreeVec(0.0f, 135.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, -10.5f, 0.0f), AnimationChannel.Interpolations.LINEAR))).build();
}
