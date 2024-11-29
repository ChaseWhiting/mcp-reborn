package net.minecraft.client.animation.definitions;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.*;

public class AllayAnimation extends Animation {
    public static final AnimationDefinition ALLAY_FLAP = AnimationDefinition.Builder
            .withLength(2.0f)
            .looping()
            .addAnimation("right_wing", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(0.0f, -45.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.5f, degreeVec(0.0f, 45.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.0f, degreeVec(0.0f, -45.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("left_wing", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(0.0f, 45.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.5f, degreeVec(0.0f, -45.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.0f, degreeVec(0.0f, 45.0f, 0.0f), Interpolations.LINEAR)))
            .build();

    public static final AnimationDefinition ALLAY_HOLD_ITEM = AnimationDefinition.Builder
            .withLength(1.0f)
            .addAnimation("right_arm", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(-90.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.5f, degreeVec(-45.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.0f, degreeVec(-90.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("left_arm", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(-90.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.5f, degreeVec(-45.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.0f, degreeVec(-90.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .build();

    public static final AnimationDefinition ALLAY_DANCE = AnimationDefinition.Builder
            .withLength(3.0f)
            .looping()
            .addAnimation("root", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(0.0f, 0.0f, 10.0f), Interpolations.LINEAR),
                    new Keyframe(1.5f, degreeVec(0.0f, 0.0f, -10.0f), Interpolations.LINEAR),
                    new Keyframe(3.0f, degreeVec(0.0f, 0.0f, 10.0f), Interpolations.LINEAR)))
            .build();

    @Override
    public ImmutableSet<AnimationDefinition> getAnimations() {
        return ImmutableSet.of(ALLAY_FLAP, ALLAY_HOLD_ITEM, ALLAY_DANCE);
    }
}
