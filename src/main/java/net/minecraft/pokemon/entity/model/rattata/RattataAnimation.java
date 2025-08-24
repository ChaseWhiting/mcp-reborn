package net.minecraft.pokemon.entity.model.rattata;// Save this class in your mod and generate all required imports

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/**
 * Made with Blockbench 4.11.2
 * Exported for Minecraft version 1.19 or later with Mojang mappings
 * @author Author
 */
public class RattataAnimation {
	public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(3.7917F).looping()
		.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.7917F, KeyframeAnimations.degreeVec(0.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.7917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.3333F, KeyframeAnimations.posVec(0.0F, -0.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.7917F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.7917F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.7917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("rightEar", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(2.2083F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.3333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -2.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 1.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.5833F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("leftEar", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(2.875F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 8.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -1.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("rightWhisker", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.8333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.3333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.2083F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.3333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.5833F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.7917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("leftWhisker", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.8333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.0417F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.4167F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.625F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("tooth", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7917F, KeyframeAnimations.degreeVec(6.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.2083F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.0F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.7917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0833F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.0833F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.875F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.7917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("tailUpper", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.25F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.9583F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.0417F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.7917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("tailUpper2", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.25F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.9583F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.0417F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.7917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.375F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.6667F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.7917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(2.0F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.7917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("upperRightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.625F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.5833F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("upperLeftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0833F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(3.7917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();

	public static final AnimationDefinition ATTACK = AnimationDefinition.Builder.withLength(1.1667F)
		.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5417F, KeyframeAnimations.degreeVec(-35.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7917F, KeyframeAnimations.degreeVec(-50.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.375F, KeyframeAnimations.posVec(0.0F, 4.5F, -2.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.2083F, KeyframeAnimations.degreeVec(37.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.3333F, KeyframeAnimations.degreeVec(47.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7083F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0417F, KeyframeAnimations.degreeVec(32.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("tailUpper", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.3333F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0417F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(-17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("tailUpper2", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.6667F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0417F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.375F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.75F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9583F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.375F, KeyframeAnimations.posVec(0.0F, 2.5F, -2.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5417F, KeyframeAnimations.posVec(0.0F, 0.5F, -1.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9167F, KeyframeAnimations.posVec(0.0F, 0.05F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.375F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.75F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9583F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.375F, KeyframeAnimations.posVec(0.0F, 2.5F, -2.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5417F, KeyframeAnimations.posVec(0.0F, 0.5F, -1.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9167F, KeyframeAnimations.posVec(0.0F, 0.05F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("upperRightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.375F, KeyframeAnimations.degreeVec(-123.33F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5417F, KeyframeAnimations.degreeVec(-57.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("upperRightLeg", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.posVec(0.0F, 5.5F, -1.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7917F, KeyframeAnimations.posVec(0.0F, 3.94F, 2.37F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9167F, KeyframeAnimations.posVec(0.0F, 3.12F, 1.93F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("upperLeftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.375F, KeyframeAnimations.degreeVec(-123.33F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5417F, KeyframeAnimations.degreeVec(-57.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("upperLeftLeg", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.posVec(0.0F, 5.5F, -1.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7917F, KeyframeAnimations.posVec(0.0F, 3.94F, 2.37F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9167F, KeyframeAnimations.posVec(0.0F, 3.12F, 1.93F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();

	public static final AnimationDefinition TACKLE = AnimationDefinition.Builder.withLength(1.1667F)
		.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.2917F, KeyframeAnimations.degreeVec(-10.0F, 30.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4167F, KeyframeAnimations.posVec(0.0F, 2.5F, -5.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5833F, KeyframeAnimations.posVec(0.0F, 2.5F, -10.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.4167F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.6667F, KeyframeAnimations.degreeVec(42.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0833F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.3333F, KeyframeAnimations.degreeVec(0.0F, -20.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("tailUpper", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.3333F, KeyframeAnimations.degreeVec(0.0F, -20.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, -32.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.875F, KeyframeAnimations.degreeVec(0.0F, -7.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("tailUpper2", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.875F, KeyframeAnimations.degreeVec(0.0F, 17.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.3333F, KeyframeAnimations.degreeVec(24.6657F, -4.2085F, 9.0795F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.6667F, KeyframeAnimations.degreeVec(64.6657F, -4.2085F, 9.0795F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.8333F, KeyframeAnimations.degreeVec(19.6657F, -4.2085F, 9.0795F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.3333F, KeyframeAnimations.posVec(1.5F, 1.0F, -3.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5833F, KeyframeAnimations.posVec(1.13F, 1.38F, -9.38F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.6667F, KeyframeAnimations.posVec(1.0F, 1.5F, -8.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.8333F, KeyframeAnimations.posVec(0.5F, 1.0F, -5.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.3333F, KeyframeAnimations.degreeVec(25.0F, 47.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.6667F, KeyframeAnimations.degreeVec(52.5F, 47.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.3333F, KeyframeAnimations.posVec(2.0F, 1.0F, -5.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5833F, KeyframeAnimations.posVec(0.75F, 1.38F, -10.62F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.6667F, KeyframeAnimations.posVec(1.0F, 1.5F, -9.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.5F, -3.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("upperRightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.2917F, KeyframeAnimations.degreeVec(-49.1911F, 15.6623F, -2.0766F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.degreeVec(-45.8717F, 24.6122F, -3.2632F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7917F, KeyframeAnimations.degreeVec(-15.8717F, 24.6122F, -3.2632F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("upperRightLeg", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.2917F, KeyframeAnimations.posVec(-1.45F, 1.59F, -2.18F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.posVec(-1.5F, 2.5F, -5.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5833F, KeyframeAnimations.posVec(-1.5F, 2.5F, -8.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7917F, KeyframeAnimations.posVec(-1.0F, 1.5F, -5.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.1667F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("upperLeftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.degreeVec(-35.0F, 30.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7917F, KeyframeAnimations.degreeVec(-2.5F, 30.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0833F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("upperLeftLeg", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.posVec(-1.5F, 2.5F, -6.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5833F, KeyframeAnimations.posVec(-1.5F, 2.5F, -10.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7917F, KeyframeAnimations.posVec(-0.5F, 1.0F, -6.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0833F, KeyframeAnimations.posVec(-0.5F, 0.0F, -1.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.125F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();
}