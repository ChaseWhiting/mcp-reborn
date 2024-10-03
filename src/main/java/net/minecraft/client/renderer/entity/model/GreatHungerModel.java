package net.minecraft.client.renderer.entity.model;// Made with Blockbench 4.11.0
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.GreatHungerEntity;
import net.minecraft.util.math.MathHelper;

public class GreatHungerModel<T extends GreatHungerEntity> extends EntityModel<T> {
	private final ModelRenderer head;
	private final ModelRenderer mouth_bottom;
	private final ModelRenderer mouth_top;
	private final ModelRenderer body;
	private final ModelRenderer left_front_leg;
	private final ModelRenderer left_back_leg;
	private final ModelRenderer right_front_leg;
	private final ModelRenderer right_back_leg;


	public GreatHungerModel() {
		texWidth = 64;
		texHeight = 32;

		head = new ModelRenderer(this);
		head.setPos(0.0F, 20.0F, -1.0F);


		mouth_bottom = new ModelRenderer(this);
		mouth_bottom.setPos(0.0F, -1.5F, 3.0F);
		head.addChild(mouth_bottom);
		mouth_bottom.texOffs(0, 13).addBox(-4.5F, -1.5F, -9.0F, 9.0F, 3.0F, 10.0F, 0.0F, false);

		mouth_top = new ModelRenderer(this);
		mouth_top.setPos(0.0F, -2.5F, 2.5F);
		head.addChild(mouth_top);
		mouth_top.texOffs(0, 0).addBox(-4.5F, -3.5F, -8.5F, 9.0F, 3.0F, 10.0F, 0.0F, false);
		mouth_top.texOffs(54, 0).addBox(3.554F, -2.43F, -4.47F, 1.0F, 2.0F, 4.0F, 0.0F, false);
		mouth_top.texOffs(54, 0).addBox(-4.6135F, -2.43F, -4.47F, 1.0F, 2.0F, 4.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setPos(0.0F, 24.0F, 0.0F);
		body.texOffs(40, 24).addBox(-2.9611F, -4.1158F, -3.268F, 6.0F, 2.0F, 6.0F, 0.0F, false);

		left_front_leg = new ModelRenderer(this);
		left_front_leg.setPos(2.5F, 21.0F, -2.0F);
		left_front_leg.texOffs(0, 27).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

		left_back_leg = new ModelRenderer(this);
		left_back_leg.setPos(2.5F, 21.0F, 2.0F);
		left_back_leg.texOffs(0, 27).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

		right_front_leg = new ModelRenderer(this);
		right_front_leg.setPos(-2.5F, 21.0F, -2.0F);
		right_front_leg.texOffs(8, 27).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

		right_back_leg = new ModelRenderer(this);
		right_back_leg.setPos(-2.5F, 21.0F, 2.0F);
		right_back_leg.texOffs(8, 27).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// Default leg animations
		this.right_back_leg.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.right_front_leg.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.left_back_leg.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.left_front_leg.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

		// Check if the entity is jumping
		if (entity.isJumping()) {
			float jumpTiltAngle = (float) Math.toRadians(15.0F); // Tilt back by 15 degrees
			float jumpTiltAngle2 = (float) Math.toRadians(35.0F); // Tilt back by 15 degrees

			float legTiltBackAmount = (float) Math.toRadians(20.0F); // Legs tilt back by 20 degrees

			// Make the whole body tilt back during jump
			this.body.xRot -= jumpTiltAngle;
			this.head.xRot -= jumpTiltAngle2;
			// Tilt the legs backward during the jump
			this.right_back_leg.xRot -= legTiltBackAmount;
			this.right_front_leg.xRot -= legTiltBackAmount;
			this.left_back_leg.xRot -= legTiltBackAmount;
			this.left_front_leg.xRot -= legTiltBackAmount;
		}

		// Head and body sway and tilt logic
		this.getMainParts().forEach(model -> {
			float headTiltAmplitude = 0.09F;
			float bodyTiltAmplitude = 0.02F;  // Body tilts less than the head
			float swayAmplitude = 0.05F;  // Controls how much the model sways
			float swayFrequency = 0.1F;

			if (model == this.head) {
				// Head tilts more aggressively, especially in the "swallow item" state
				if (entity.getCurrentState() == GreatHungerEntity.State.SWALLOW_ITEM) {
					headTiltAmplitude = 0.18F;  // Tilt the head forward more in "swallow item" state
				}
				if (entity.getCurrentState() == GreatHungerEntity.State.FIGHT) {
					headTiltAmplitude = 0.21F;
					swayFrequency = 0.2F;
					swayAmplitude = 0.02F;
				}
				model.xRot = MathHelper.cos(ageInTicks * swayFrequency) * headTiltAmplitude;
			} else if (model == this.body) {
				// Body tilts less than the head
				model.xRot = MathHelper.cos(ageInTicks * swayFrequency) * bodyTiltAmplitude;
			}

			model.yRot = MathHelper.sin(ageInTicks * swayFrequency) * swayAmplitude;
		});

		// Mouth animation logic
		if (entity.getTarget() != null || entity.getCurrentState() == GreatHungerEntity.State.FIGHT) {
			this.openMouth(entity, 25, -37, 0.02F);
		} else if (entity.getCurrentState() == GreatHungerEntity.State.SWALLOW_ITEM || entity.getCurrentState() == GreatHungerEntity.State.SWALLOW_ITEM) {
			this.openMouth(entity, 17, -40, 0.02F);
		} else {
			// Smoothly close the mouth if not swallowing
			float targetProgress = 0.0F;  // Fully closed
			entity.setClientSideSwallowTicks(MathHelper.lerp(0.05F, entity.getClientSideSwallowTicks(), targetProgress));  // Smooth transition back to closed state

			// Smoothly reset the mouth's rotations as the progress moves towards 0
			this.mouth_bottom.xRot = MathHelper.lerp(entity.getClientSideSwallowTicks(), 0.0F, (float) Math.toRadians(17.0F));
			this.mouth_top.xRot = MathHelper.lerp(entity.getClientSideSwallowTicks(), 0.0F, (float) Math.toRadians(-40.0F));
		}
	}

	public void openMouth(T entity, float bottomDegrees, float topDegrees, float transition) {

		// Smooth mouth opening/closing
		float targetProgress = 1.0F;  // 1 = fully open, 0 = fully closed

		// Lerp between current and target progress based on delta time (lerp factor should be low for smoothness)
		entity.setClientSideSwallowTicks(MathHelper.lerp(transition, entity.getClientSideSwallowTicks(), targetProgress));  // Smooth transition based on target progress

		// Set the mouth's rotations smoothly with correct directions
		this.mouth_bottom.xRot = MathHelper.lerp(entity.getClientSideSwallowTicks(), 0.0F, (float) Math.toRadians(bottomDegrees));  // Adjust for wider opening
		this.mouth_top.xRot = MathHelper.lerp(entity.getClientSideSwallowTicks(), 0.0F, (float) Math.toRadians(topDegrees));    // Adjust for wider opening
	}

	public ImmutableList<ModelRenderer> getMainParts() {
		return ImmutableList.of(this.head,this.body);
	}

	public ImmutableList<ModelRenderer> allParts() {
		return ImmutableList.of(this.head,this.body,this.left_back_leg,this.left_front_leg,this.right_back_leg,this.right_front_leg);
	}



	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		head.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		left_front_leg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		left_back_leg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		right_front_leg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		right_back_leg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}