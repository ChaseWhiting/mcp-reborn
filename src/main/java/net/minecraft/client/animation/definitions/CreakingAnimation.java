package net.minecraft.client.animation.definitions;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.*;

import static net.minecraft.client.animation.AnimationChannel.Targets.POSITION;
import static net.minecraft.client.animation.AnimationChannel.Targets.SCALE;

public class CreakingAnimation extends Animation {
    public static final AnimationDefinition CREAKING_WALK = AnimationDefinition.Builder
            .withLength(1.125f)
            .looping()
            .addAnimation("upper_body", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(26.8802f, -23.399f, -9.0616f), Interpolations.LINEAR),
                    new Keyframe(0.125f, degreeVec(-2.2093f, 5.9119f, 0.0675f), Interpolations.LINEAR),
                    new Keyframe(0.5417f, degreeVec(23.0778f, 14.2906f, 4.6066f), Interpolations.LINEAR),
                    new Keyframe(0.7083f, degreeVec(-10.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.875f, degreeVec(7.5f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.125f, degreeVec(26.8802f, -23.399f, -9.0616f), Interpolations.LINEAR)))
            .addAnimation("head", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.0417f, degreeVec(-17.5f, -62.5f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.0833f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.4167f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.4583f, degreeVec(0.0f, 15.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.5f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.0417f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.0833f, degreeVec(-37.1532f, 81.1131f, -28.3621f), Interpolations.LINEAR),
                    new Keyframe(1.125f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("right_arm", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(12.5f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.25f, degreeVec(-32.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.875f, degreeVec(12.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.125f, degreeVec(-15.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("left_arm", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(-15.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.125f, degreeVec(10.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.5417f, degreeVec(-25.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.75f, degreeVec(-9.0923f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.7917f, degreeVec(-15.137f, -66.7758f, 13.9603f), Interpolations.LINEAR),
                    new Keyframe(0.8333f, degreeVec(-9.0923f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.0f, degreeVec(10.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.125f, degreeVec(-15.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("left_leg", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.25f, degreeVec(30.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.375f, degreeVec(49.8924f, -3.8282f, 3.2187f), Interpolations.LINEAR),
                    new Keyframe(0.5f, degreeVec(17.5f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.625f, degreeVec(-56.5613f, -12.2403f, -8.7374f), Interpolations.LINEAR),
                    new Keyframe(0.9167f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.125f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("right_leg", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(25.5305f, 11.3125f, 5.3525f), Interpolations.LINEAR),
                    new Keyframe(0.125f, degreeVec(-49.5628f, 7.3556f, 6.7933f), Interpolations.LINEAR),
                    new Keyframe(0.25f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.4583f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.9167f, degreeVec(30.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.0417f, degreeVec(55.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(1.125f, degreeVec(25.5305f, 11.3125f, 5.3525f), Interpolations.LINEAR)))
            .build();



    public static final AnimationDefinition CREAKING_ATTACK = AnimationDefinition.Builder
            .withLength(0.375f)
            .addAnimation("right_arm", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(21.1693f, 37.7555f, 122.3122f), Interpolations.LINEAR),
                    new Keyframe(0.125f, degreeVec(-60.349f, -31.3213f, -16.4846f), Interpolations.LINEAR),
                    new Keyframe(0.25f, degreeVec(-54.3924f, -22.7992f, -27.3049f), Interpolations.LINEAR),
                    new Keyframe(0.2917f, degreeVec(-53.0231f, -26.8304f, -30.5649f), Interpolations.LINEAR),
                    new Keyframe(0.375f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("left_arm", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(15.0f, 0.0f, -10.0f), Interpolations.LINEAR),
                    new Keyframe(0.125f, degreeVec(22.4229f, 1.9113f, -14.6211f), Interpolations.LINEAR),
                    new Keyframe(0.25f, degreeVec(38.9206f, -9.562f, -4.6084f), Interpolations.LINEAR),
                    new Keyframe(0.2917f, degreeVec(51.4206f, -9.562f, -4.6084f), Interpolations.LINEAR),
                    new Keyframe(0.375f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("upper_body", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(-12.5f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.125f, degreeVec(11.1575f, 0.2125f, -2.2036f), Interpolations.LINEAR),
                    new Keyframe(0.25f, degreeVec(22.5f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.2917f, degreeVec(25.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.375f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR))).build();



    public static final AnimationDefinition CREAKING_INVULNERABLE = AnimationDefinition.Builder
            .withLength(0.2917f)
            .addAnimation("upper_body", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.0833f, degreeVec(-5.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.1667f, degreeVec(5.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.25f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("upper_body", new AnimationChannel(Targets.POSITION,
                    new Keyframe(0.0f, posVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.0833f, posVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.25f, posVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("right_arm", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.0833f, degreeVec(17.5f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.1667f, degreeVec(-15.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.25f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("right_arm", new AnimationChannel(Targets.POSITION,
                    new Keyframe(0.0f, posVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.25f, posVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("left_arm", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.0833f, degreeVec(20.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.1667f, degreeVec(-15.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.25f, degreeVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .addAnimation("left_arm", new AnimationChannel(Targets.POSITION,
                    new Keyframe(0.0f, posVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR),
                    new Keyframe(0.25f, posVec(0.0f, 0.0f, 0.0f), Interpolations.LINEAR)))
            .build();


    public static final AnimationDefinition CREAKING_DEATH = AnimationDefinition.Builder
            .withLength(2.25f)
            .addAnimation("upper_body", new AnimationChannel(ROTATION,
                    new Keyframe(0.0f, degreeVec(0.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.0833f, degreeVec(-40.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.1667f, degreeVec(-5.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.2917f, degreeVec(7.5f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.5833f, degreeVec(16.25f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.6667f, degreeVec(29.0814f, 62.5516f, 26.5771f), LINEAR),
                    new Keyframe(0.75f, degreeVec(12.2115f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(1.0f, degreeVec(10.25f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(1.0417f, degreeVec(-47.64f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(1.125f, degreeVec(21.96f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(1.25f, degreeVec(12.5f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(2.25f, degreeVec(17.3266f, 7.9022f, -0.1381f), LINEAR)))
            .addAnimation("upper_body", new AnimationChannel(POSITION,
                    new Keyframe(0.0f, posVec(0.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.0833f, posVec(0.0f, 0.557f, 1.2659f), LINEAR),
                    new Keyframe(0.1667f, posVec(0.0f, -2.0889f, -0.3493f), LINEAR),
                    new Keyframe(0.2917f, posVec(0.0f, 0.0f, 0.0f), LINEAR)))
            .addAnimation("upper_body", new AnimationChannel(SCALE,
                    new Keyframe(0.0f, scaleVec(1.0, 1.0, 1.0), LINEAR),
                    new Keyframe(0.0833f, scaleVec(1.0, 1.1f, 1.0), LINEAR),
                    new Keyframe(0.1667f, scaleVec(1.0, 0.9f, 1.0), LINEAR),
                    new Keyframe(0.2917f, scaleVec(1.0, 1.0, 1.0), LINEAR)))
            .addAnimation("right_arm", new AnimationChannel(ROTATION,
                    new Keyframe(0.0f, degreeVec(0.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.2917f, degreeVec(-10.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.5f, degreeVec(0.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(1.25f, degreeVec(-10.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(1.5417f, degreeVec(-10.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(1.5833f, degreeVec(-12.1479f, -34.3927f, 6.9326f), LINEAR),
                    new Keyframe(1.6667f, degreeVec(-10.0f, 0.0f, 0.0f), LINEAR)))
            .addAnimation("right_arm", new AnimationChannel(POSITION,
                    new Keyframe(0.0f, posVec(0.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.2917f, posVec(0.0f, 0.0f, 0.0f), LINEAR)))
            .addAnimation("left_arm", new AnimationChannel(ROTATION,
                    new Keyframe(0.0f, degreeVec(0.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.2917f, degreeVec(-10.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.5f, degreeVec(0.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.8333f, degreeVec(-4.4444f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.875f, degreeVec(-26.7402f, -78.831f, 26.3025f), LINEAR),
                    new Keyframe(0.9583f, degreeVec(-5.5556f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(1.25f, degreeVec(-10.0f, 0.0f, 0.0f), LINEAR)))
            .addAnimation("left_arm", new AnimationChannel(POSITION,
                    new Keyframe(0.0f, posVec(0.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.2917f, posVec(0.0f, 0.0f, 0.0f), LINEAR)))
            .addAnimation("head", new AnimationChannel(ROTATION,
                    new Keyframe(0.0f, degreeVec(0.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.0833f, degreeVec(-5.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.2917f, degreeVec(10.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.5f, degreeVec(2.5f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.5417f, degreeVec(5.5f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.5833f, degreeVec(-67.4168f, -12.9552f, -8.0231f), LINEAR),
                    new Keyframe(0.6667f, degreeVec(8.5f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(1.0f, degreeVec(10.773f, -29.5608f, -5.3627f), LINEAR),
                    new Keyframe(1.25f, degreeVec(10.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(1.7917f, degreeVec(10.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(1.8333f, degreeVec(12.9625f, 39.2735f, 8.2901f), LINEAR),
                    new Keyframe(1.9167f, degreeVec(10.0f, 0.0f, 0.0f), LINEAR)))
            .addAnimation("head", new AnimationChannel(POSITION,
                    new Keyframe(0.0f, posVec(0.0f, 0.0f, 0.0f), LINEAR),
                    new Keyframe(0.2917f, posVec(0.0f, 0.0f, 0.0f), LINEAR)))
            .build();


    @Override
    public ImmutableSet<AnimationDefinition> getAnimations() {
        return ImmutableSet.of(CREAKING_WALK, CREAKING_ATTACK, CREAKING_INVULNERABLE, CREAKING_DEATH);
    }
}

