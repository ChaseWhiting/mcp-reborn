package net.minecraft.entity.monster.creaking;// Made with Blockbench 4.11.0
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class CreakingModel<T extends CreakingEntity> extends EntityModel<CreakingEntity> {
	private final ModelRenderer root;
	private final ModelRenderer upper_body;
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer right_arm;
	private final ModelRenderer left_arm;
	private final ModelRenderer right_leg;
	private final ModelRenderer left_leg;

	public CreakingModel() {
		texWidth = 64;
		texHeight = 64;

		// Root part
		root = new ModelRenderer(this);
		root.setPos(0.0F, 24.0F, 0.0F);

		// Upper body
		upper_body = new ModelRenderer(this);
		upper_body.setPos(0.0F, -19.0F, 0.0F);
		root.addChild(upper_body);

		// Head
		head = new ModelRenderer(this);
		head.setPos(-3.0F, -11.0F, 0.0F);
		upper_body.addChild(head);
		head.texOffs(0, 0).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F);
		head.texOffs(28, 31).addBox(-3.0F, -13.0F, -3.0F, 6.0F, 3.0F, 6.0F);
		head.texOffs(12, 40).addBox(3.0F, -13.0F, 0.0F, 9.0F, 14.0F, 0.0F);
		head.texOffs(34, 12).addBox(-12.0F, -14.0F, 0.0F, 9.0F, 14.0F, 0.0F);

		// Body
		body = new ModelRenderer(this);
		body.setPos(0.0F, -7.0F, 1.0F);
		upper_body.addChild(body);
		body.texOffs(0, 16).addBox(0.0F, -3.0F, -3.0F, 6.0F, 13.0F, 5.0F);
		body.texOffs(24, 0).addBox(-6.0F, -4.0F, -3.0F, 6.0F, 7.0F, 5.0F);

		// Right arm
		right_arm = new ModelRenderer(this);
		right_arm.setPos(-7.0F, -9.5F, 1.5F);
		upper_body.addChild(right_arm);
		right_arm.texOffs(22, 13).addBox(-2.0F, -1.5F, -1.5F, 3.0F, 21.0F, 3.0F);
		right_arm.texOffs(46, 0).addBox(-2.0F, 19.5F, -1.5F, 3.0F, 4.0F, 3.0F);

		// Left arm
		left_arm = new ModelRenderer(this);
		left_arm.setPos(6.0F, -9.0F, 0.5F);
		upper_body.addChild(left_arm);
		left_arm.texOffs(30, 40).addBox(0.0F, -1.0F, -1.5F, 3.0F, 16.0F, 3.0F);
		left_arm.texOffs(52, 12).addBox(0.0F, -5.0F, -1.5F, 3.0F, 4.0F, 3.0F);
		left_arm.texOffs(52, 19).addBox(0.0F, 15.0F, -1.5F, 3.0F, 4.0F, 3.0F);

		// Left leg
		left_leg = new ModelRenderer(this);
		left_leg.setPos(2.4F, -16.0F, 0.5F);
		root.addChild(left_leg);
		left_leg.texOffs(42, 40).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 16.0F, 3.0F);
		left_leg.texOffs(45, 55).addBox(-1.5F, 15.7F, -4.5F, 5.0F, 0.0F, 9.0F);

		// Right leg
		right_leg = new ModelRenderer(this);
		right_leg.setPos(0F, -17.5F, 0.5F);
		root.addChild(right_leg);
		right_leg.texOffs(0, 34).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 19.0F, 3.0F);
		right_leg.texOffs(45, 46).addBox(-5.0F, 17.2F, -4.5F, 5.0F, 0.0F, 9.0F);
		right_leg.texOffs(12, 34).addBox(-3.0F, -4.5F, -1.5F, 3.0F, 3.0F, 3.0F);
	}

	@Override
	public void setupAnim(CreakingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// Reset all poses
		this.root.reset();

		// Animate the model based on movement
		//if (entity.canMove()) {
			animateWalk(limbSwing, limbSwingAmount, entity);
//		}

		// Additional attack or invulnerability animations
		// Attack animation (if the entity is attacking)
		if (entity.attackAnimationRemainingTicks > 0) {
			float attackProgress = 1.0f - (float) entity.attackAnimationRemainingTicks / (float) entity.maxAttackAnimationTicks;
			animateAttack(ageInTicks, attackProgress, entity);
		}

		if (entity.isInvulnerable()) {
			animateInvulnerability();
		}

		// Head look controls
		//animateHeadLookTarget(netHeadYaw, headPitch);
	}

	private void animateWalk(float limbSwing, float limbSwingAmount, CreakingEntity entity) {
		if (entity.canMove()) {
			// Animation progress for a smooth, continuous cycle
			float animationProgress = (limbSwing % 2.5f) / 2.5f; // 3.0f for a smoother, slower cycle

			// Define angular values with less intensity for arms and synchronized head/body movement
			float maxSwing = (float) Math.toRadians(20.0f);  // Reduced intensity for arms
			float legSwing = (float) Math.toRadians(30.0f);  // Reduced intensity for legs

			float bodySwing = (float) Math.toRadians(8.0f);  // Reduced swing range for body/head

			// Use sinusoidal motion for smooth, continuous movement
			float bodySwingAmount = MathHelper.sin(animationProgress * (float) Math.PI * 2) * bodySwing;
			float armSwingAmount = MathHelper.sin(animationProgress * (float) Math.PI * 2) * maxSwing;
			float legSwingAmount = MathHelper.sin(animationProgress * (float) Math.PI * 2) * legSwing;

			// Body and head movement (synchronized, less intense)
			this.body.xRot = bodySwingAmount;
			this.head.xRot = bodySwingAmount;  // Head follows body exactly with same intensity

			// Arms swinging (less intense)
			this.right_arm.xRot = armSwingAmount * 0.75f * limbSwingAmount;  // Arms swing with reduced intensity
			this.left_arm.xRot = -armSwingAmount * 0.75f * limbSwingAmount;  // Left arm opposite to right arm

			// Legs swinging (same as before, strong swing)
			this.right_leg.xRot = legSwingAmount * 1.4f * limbSwingAmount;  // Legs remain intense
			this.left_leg.xRot = -legSwingAmount * 1.4f * limbSwingAmount;  // Left leg opposite to right leg
		}
	}

	private void animateAttack(float ageInTicks, float attackProgress, CreakingEntity entity) {
		// attackProgress represents the animation cycle progress, typically from 0 to 1

		// Maximum rotation values based on the keyframe data you provided
		float maxRightArmSwingX = (float) Math.toRadians(60.0f);  // Right arm swings forward (xRot)
		float maxRightArmSwingZ = (float) Math.toRadians(30.0f);  // Right arm swings outward (zRot)
		float maxLeftArmSwingX = (float) Math.toRadians(-15.0f);  // Left arm swings slightly backward (xRot)
		float maxLeftArmSwingZ = (float) Math.toRadians(-10.0f);  // Left arm swings outward (zRot)
		float maxBodyTilt = (float) Math.toRadians(12.5f);        // Body tilts forward during the attack

		// Use sinusoidal motion to smoothly animate the swing during the attack phase
		float attackSwingAmount = MathHelper.sin(attackProgress * (float) Math.PI);  // Smooth swing using sine wave

		// Right arm swings forward (xRot) and outward (zRot)
		this.right_arm.xRot = -attackSwingAmount * maxRightArmSwingX;
		this.right_arm.zRot = attackSwingAmount * maxRightArmSwingZ;  // Add outward rotation

		// Left arm swings backward (xRot) and slightly outward (zRot)
		this.left_arm.xRot = attackSwingAmount * maxLeftArmSwingX;
		this.left_arm.zRot = attackSwingAmount * maxLeftArmSwingZ;  // Add outward rotation for left arm

		// Body tilts forward during the attack
		this.body.xRot = attackSwingAmount * maxBodyTilt;
		this.head.xRot = attackSwingAmount * maxBodyTilt;

		// As attack progresses, gradually return everything to neutral (rotation 0)
		float returnToNeutralAmount = 1.0f - attackProgress;  // When attackProgress is 1, we're at the end

		// Right arm returning to neutral
		this.right_arm.xRot *= returnToNeutralAmount;
		this.right_arm.zRot *= returnToNeutralAmount;  // Reset outward rotation

		// Left arm returning to neutral
		this.left_arm.xRot *= returnToNeutralAmount;
		this.left_arm.zRot *= returnToNeutralAmount;  // Reset outward rotation

		// Body returning to neutral
		this.body.xRot *= returnToNeutralAmount;
		this.body.xRot *= returnToNeutralAmount;
	}

	private void animateInvulnerability() {
		// Example invulnerability animation logic
		this.body.yRot = MathHelper.cos(0.3F);  // Slight oscillation effect
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
