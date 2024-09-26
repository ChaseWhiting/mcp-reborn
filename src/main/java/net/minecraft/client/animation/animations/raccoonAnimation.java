package net.minecraft.client.animation.animations;

import net.minecraft.client.animation.*;
public class raccoonAnimation {
	public static final AnimationDefinition TEST = AnimationDefinition.Builder.withLength(2.8333F).looping()
		.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5833F, KeyframeAnimations.degreeVec(-49.1066F, -20.7048F, 22.2077F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.5417F, KeyframeAnimations.degreeVec(-42.5F, 40.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.9583F, KeyframeAnimations.degreeVec(-47.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.4583F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();





	public static final AnimationDefinition FROG_TONGUE = AnimationDefinition.Builder.withLength(0.5F).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0833F, KeyframeAnimations.degreeVec(-60.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167F, KeyframeAnimations.degreeVec(-60.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.SCALE, new Keyframe(0.0F, KeyframeAnimations.degreeVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0833F, KeyframeAnimations.degreeVec(0.998F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167F, KeyframeAnimations.degreeVec(0.998F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5F, KeyframeAnimations.degreeVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0833F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.SCALE, new Keyframe(0.0833F, KeyframeAnimations.scaleVec(1.0D, 1.0D, 1.0D), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1667F, KeyframeAnimations.scaleVec(0.5D, 1.0D, 5.0D), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167F, KeyframeAnimations.scaleVec(1.0D, 1.0D, 1.0D), AnimationChannel.Interpolations.LINEAR))).build();

}