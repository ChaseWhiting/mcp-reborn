package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;


public class BlazeAnimation {
    public static final AnimationDefinition WALKING = AnimationDefinition.Builder.withLength(1.5F).looping()
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5417F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.875F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3333F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.625F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR) // Added loop-back

            ))
            .addAnimation("stick2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(17.2119F, -1.6262F, -0.4666F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR) // Added loop-back
            ))
            .addAnimation("stick3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR) // Added loop-back
            ))
            .addAnimation("stick4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR) // Added loop-back
            ))
            .addAnimation("stick5", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3333F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR) // Added loop-back
            ))
            .addAnimation("stick6", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR) // Added loop-back
            ))
            .addAnimation("stick7", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3333F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR) // Added loop-back
            ))
            .addAnimation("stick8", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR) // Added loop-back
            ))
            .addAnimation("stick9", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(35.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(37.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.875F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.125F, KeyframeAnimations.degreeVec(35.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.2917F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.375F, KeyframeAnimations.degreeVec(32.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick10", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7917F, KeyframeAnimations.degreeVec(32.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0417F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.25F, KeyframeAnimations.degreeVec(32.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4583F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick11", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7083F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0833F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.25F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4167F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick12", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(35.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.875F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0417F, KeyframeAnimations.degreeVec(32.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.2083F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.375F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    public static final AnimationDefinition DEATH = AnimationDefinition.Builder.withLength(1.0F)
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 12.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 12.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(17.7531F, -9.5327F, 9.4649F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7083F, KeyframeAnimations.degreeVec(10.2531F, -9.5327F, 9.4649F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F, KeyframeAnimations.degreeVec(12.7531F, -9.5327F, 9.4649F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.875F, KeyframeAnimations.degreeVec(-2.2469F, -9.5327F, 9.4649F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9583F, KeyframeAnimations.degreeVec(5.2531F, -9.5327F, 9.4649F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-5.001F, -10.8718F, -3.164F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, -10.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7083F, KeyframeAnimations.posVec(0.0F, -16.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F, KeyframeAnimations.posVec(0.0F, -17.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9583F, KeyframeAnimations.posVec(0.0F, -18.5F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0417F, KeyframeAnimations.degreeVec(36.9384F, 3.7317F, -17.1097F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.degreeVec(35.9901F, 9.6849F, -25.2038F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.degreeVec(68.4901F, 9.6849F, -25.2038F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5417F, KeyframeAnimations.degreeVec(50.9901F, 9.6849F, -25.2038F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(69.7297F, 15.0736F, -32.5949F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.875F, KeyframeAnimations.degreeVec(84.7297F, 15.0736F, -32.5949F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(77.2297F, 15.0736F, -32.5949F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick1", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.posVec(0.0F, -1.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.posVec(-2.0F, -5.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(-2.0F, -10.5F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5417F, KeyframeAnimations.posVec(-2.0F, -13.0F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.625F, KeyframeAnimations.posVec(-2.0F, -17.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F, KeyframeAnimations.posVec(-2.0F, -21.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.875F, KeyframeAnimations.posVec(-2.0F, -22.5F, -1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9583F, KeyframeAnimations.posVec(-2.0F, -25.0F, -1.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0417F, KeyframeAnimations.degreeVec(12.5F, 0.0F, -20.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.degreeVec(33.625F, 5.7358F, -46.9409F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.degreeVec(39.1139F, -8.4446F, -78.2794F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.degreeVec(39.5026F, 5.9194F, -95.6478F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(39.8694F, -0.4761F, -87.9475F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick2", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.posVec(0.0F, -3.5F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.posVec(0.0F, -6.5F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.posVec(0.0F, -9.0F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(0.0F, -14.0F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.posVec(1.5F, -26.5F, -3.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.posVec(1.5F, -26.0F, -3.5F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.degreeVec(18.7423F, -12.6796F, -57.1009F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.degreeVec(14.9092F, -17.0468F, -42.2468F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5417F, KeyframeAnimations.degreeVec(33.7423F, -12.6796F, -57.1009F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(35.6945F, -2.6585F, -71.5973F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.degreeVec(53.0182F, 4.6383F, -81.7549F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick3", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.posVec(0.0F, -3.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.posVec(1.5F, -13.5F, 1.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5417F, KeyframeAnimations.posVec(1.5F, -21.0F, 0.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.posVec(1.5F, -24.5F, 0.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.posVec(1.5F, -25.25F, 0.5F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0417F, KeyframeAnimations.degreeVec(10.1178F, -22.9824F, -32.062F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.125F, KeyframeAnimations.degreeVec(-5.7523F, -6.2797F, -83.8503F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(-14.3995F, -2.6755F, -69.2424F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.degreeVec(-1.3127F, -20.1085F, -78.6232F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7083F, KeyframeAnimations.degreeVec(-9.4126F, -7.4145F, -91.2168F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9583F, KeyframeAnimations.degreeVec(-10.5456F, -5.6772F, -81.3037F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick4", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0417F, KeyframeAnimations.posVec(0.0F, -5.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.125F, KeyframeAnimations.posVec(0.0F, -9.5F, 1.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -12.0F, 1.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(0.0F, -14.0F, 1.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5417F, KeyframeAnimations.posVec(0.0F, -19.0F, 1.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7083F, KeyframeAnimations.posVec(1.5F, -25.0F, 1.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9583F, KeyframeAnimations.posVec(1.5F, -25.25F, 1.5F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick5", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.degreeVec(17.5F, 17.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.degreeVec(51.3966F, 45.5739F, 29.9222F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7083F, KeyframeAnimations.degreeVec(93.8966F, 45.5739F, 29.9222F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F, KeyframeAnimations.degreeVec(126.007F, 52.8751F, 9.7299F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9583F, KeyframeAnimations.degreeVec(113.507F, 52.8751F, 9.7299F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(108.6938F, 48.2493F, 6.7374F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick5", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.posVec(0.0F, -3.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.posVec(0.0F, -4.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4583F, KeyframeAnimations.posVec(-2.0F, -12.0F, -3.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.posVec(-2.0F, -15.0F, -3.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7083F, KeyframeAnimations.posVec(-2.0F, -16.0F, -3.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F, KeyframeAnimations.posVec(-2.0F, -21.5F, -3.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick6", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(4.6293F, 24.5948F, 21.0097F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.degreeVec(7.9821F, 23.7695F, 29.1825F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.degreeVec(14.1337F, -5.2713F, 56.3745F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3333F, KeyframeAnimations.degreeVec(12.3311F, -8.7239F, 71.0842F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.degreeVec(7.505F, -13.1013F, 96.1654F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5417F, KeyframeAnimations.degreeVec(3.1512F, -14.7401F, 114.1207F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7083F, KeyframeAnimations.degreeVec(7.505F, -13.1013F, 96.1654F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.degreeVec(11.1022F, -10.2491F, 78.5297F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(28.6022F, -10.2491F, 78.5297F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick6", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.posVec(0.0F, -3.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.posVec(0.0F, -5.5F, -1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3333F, KeyframeAnimations.posVec(1.0F, -8.5F, -1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(1.0F, -10.5F, -1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5417F, KeyframeAnimations.posVec(1.0F, -13.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7083F, KeyframeAnimations.posVec(1.0F, -17.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.posVec(1.0F, -19.5F, -1.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick7", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0417F, KeyframeAnimations.degreeVec(-35.7693F, -13.5663F, 6.4607F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.degreeVec(-63.6382F, -35.2883F, -20.1798F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3333F, KeyframeAnimations.degreeVec(-81.1382F, -35.2883F, -20.1798F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(-88.124F, -35.8638F, 4.4652F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick7", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0417F, KeyframeAnimations.posVec(0.0F, -3.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.posVec(-0.5F, -11.5F, 2.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3333F, KeyframeAnimations.posVec(1.5F, -18.0F, 2.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.posVec(1.5F, -21.5F, 2.5F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick8", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 12.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3333F, KeyframeAnimations.degreeVec(-75.9257F, -7.7671F, 11.216F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.degreeVec(-81.9084F, -6.2127F, 1.6318F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5417F, KeyframeAnimations.degreeVec(-89.4084F, -6.2127F, 1.6318F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick8", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.posVec(0.0F, -5.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.posVec(0.0F, -10.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.posVec(-0.4F, -11.0F, 0.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(-2.0F, -20.0F, 2.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5417F, KeyframeAnimations.posVec(-2.0F, -21.0F, 2.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.posVec(-2.0F, -21.25F, 2.5F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick9", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -10.0F, -10.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.degreeVec(20.627F, -11.8039F, -23.5733F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.degreeVec(35.0253F, 38.9267F, -69.237F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.625F, KeyframeAnimations.degreeVec(41.344F, 53.8115F, -61.4885F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(70.1885F, 65.5077F, -28.4656F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick9", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(-3.5F, -10.5F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.625F, KeyframeAnimations.posVec(-3.5F, -12.5F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.posVec(-3.5F, -13.5F, -2.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick10", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.degreeVec(0.0F, 7.5F, 17.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.degreeVec(23.8958F, -0.5702F, 81.5314F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(5.544F, 5.059F, 65.2451F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(6.6607F, 3.4553F, 80.2011F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick10", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.posVec(0.0F, -3.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.posVec(3.5F, -8.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.posVec(3.0F, -8.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.posVec(4.5F, -10.5F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4583F, KeyframeAnimations.posVec(4.5F, -13.0F, -2.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick11", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0417F, KeyframeAnimations.degreeVec(-7.096F, -18.7472F, 31.1728F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.degreeVec(-29.2397F, -28.1465F, 76.287F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.degreeVec(-37.4686F, -14.2119F, 98.6463F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick11", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0417F, KeyframeAnimations.posVec(0.0F, -2.5F, 1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.posVec(3.0F, -5.5F, 2.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(4.5F, -13.0F, 2.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick12", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0417F, KeyframeAnimations.degreeVec(-16.2407F, -6.608F, -21.5561F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.degreeVec(-21.2407F, -6.608F, -21.5561F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.degreeVec(-25.0685F, -12.2766F, -43.6429F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.degreeVec(-32.1663F, -19.9627F, -62.8878F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.degreeVec(-44.8295F, -34.3927F, -51.7013F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7917F, KeyframeAnimations.degreeVec(-72.3295F, -34.3927F, -51.7013F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.875F, KeyframeAnimations.degreeVec(-61.6969F, -31.0078F, -53.6031F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9583F, KeyframeAnimations.degreeVec(-54.1969F, -31.0078F, -53.6031F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick12", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.posVec(0.0F, -3.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.posVec(-1.5F, -6.5F, 1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(-1.5F, -9.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.posVec(-1.5F, -10.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7917F, KeyframeAnimations.posVec(-1.5F, -13.0F, 2.5F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    public static final AnimationDefinition CHARGE = AnimationDefinition.Builder.withLength(1.0417F)
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.degreeVec(9.2F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0417F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0417F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.degreeVec(-7.4366F, 0.9762F, 7.4366F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0833F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(-7.5F, 0.0F, -7.5F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0417F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(7.4366F, 0.9762F, -7.4366F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0417F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 7.5F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick5", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0417F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(22.0182F, 4.7511F, -11.5752F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick6", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0417F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(22.0182F, -4.7511F, 11.5752F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick7", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.125F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2917F, KeyframeAnimations.degreeVec(20.941F, -8.4211F, 20.941F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick8", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0833F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(20.941F, 8.4211F, -20.941F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick9", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.125F, KeyframeAnimations.degreeVec(12.3159F, -2.1539F, 9.7676F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(11.9384F, -3.7317F, 17.1097F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F, KeyframeAnimations.degreeVec(11.5752F, -4.7511F, 22.0182F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9583F, KeyframeAnimations.degreeVec(10.8682F, -6.2127F, 29.4084F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick10", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0417F, KeyframeAnimations.degreeVec(12.3159F, 2.1539F, -9.7676F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7083F, KeyframeAnimations.degreeVec(11.9384F, 3.7317F, -17.1097F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F, KeyframeAnimations.degreeVec(11.3608F, 5.2483F, -24.4776F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.degreeVec(10.8682F, 6.2127F, -29.4084F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick11", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0833F, KeyframeAnimations.degreeVec(4.9811F, -0.4352F, 4.9811F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.degreeVec(-10.0189F, -0.4352F, 4.9811F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("stick12", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.125F, KeyframeAnimations.degreeVec(4.9811F, 0.4352F, -4.9811F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.degreeVec(-12.5189F, 0.4352F, -4.9811F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();


    public static final AnimationDefinition WILDFIRE_WALKING = AnimationDefinition.Builder.withLength(1.4583F).looping()
            .addAnimation("shield1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("shield1", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.3333F, KeyframeAnimations.posVec(0.0F, 0.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4583F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("shield2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("shield2", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.3333F, KeyframeAnimations.posVec(0.0F, 0.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4583F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("shield3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("shield3", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.3333F, KeyframeAnimations.posVec(0.0F, 0.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4583F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("shield4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("shield4", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.3333F, KeyframeAnimations.posVec(0.0F, 0.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4583F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.posVec(0.0F, 1.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3333F, KeyframeAnimations.posVec(0.0F, -0.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.625F, KeyframeAnimations.posVec(0.0F, 1.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.3333F, KeyframeAnimations.posVec(0.0F, 0.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4583F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2083F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7917F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.2083F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4583F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    public static final AnimationDefinition general = AnimationDefinition.Builder.withLength(4.0F).looping()
            .addAnimation("shield1", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, -3.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("shield3", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.52F, KeyframeAnimations.posVec(0.0F, 3.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.52F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("shield4", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -3.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, -3.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("shield2", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.52F, KeyframeAnimations.posVec(0.0F, -2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.52F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 360.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();
}