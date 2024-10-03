package net.minecraft.entity.monster.creaking;// Made with Blockbench 4.11.0
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class CreakingModel<T extends CreakingEntity> extends EntityModel<CreakingEntity> {
	private final ModelRenderer leg_left;
	private final ModelRenderer leg_right;
	private final ModelRenderer body;
	private final ModelRenderer large;
	private final ModelRenderer small;
	private final ModelRenderer head;
	private final ModelRenderer left_side_leaf;
	private final ModelRenderer right_side_leaf;
	private final ModelRenderer arm_left;
	private final ModelRenderer arm_right;

	public CreakingModel() {
		texWidth = 64;
		texHeight = 64;

		leg_left = new ModelRenderer(this);
		leg_left.setPos(2.25F, 6.5F, 0.75F);
		leg_left.texOffs(12, 44).addBox(-1.5F, 0.5F, -1.5F, 3.0F, 17.0F, 3.0F, 0.0F, false);

		leg_right = new ModelRenderer(this);
		leg_right.setPos(-1.9993F, 3.2485F, 0.0007F);
		leg_right.texOffs(0, 39).addBox(-1.5002F, -1.2485F, -1.4992F, 3.0F, 22.0F, 3.0F, 0.0F, false);
		leg_right.texOffs(52, 26).addBox(-1.4998F, 0.7485F, -1.5008F, 3.0F, 1.0F, 3.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setPos(0.0F, 24.0F, 0.0F);


		large = new ModelRenderer(this);
		large.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(large);
		large.texOffs(0, 11).addBox(-0.5F, -30.0F, -2.0F, 6.0F, 13.0F, 5.0F, 0.0F, false);

		small = new ModelRenderer(this);
		small.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(small);
		small.texOffs(17, 0).addBox(-6.5F, -31.0F, -2.0F, 6.0F, 7.0F, 5.0F, 0.0F, false);

		head = new ModelRenderer(this);
		head.setPos(-3.5246F, -6.9557F, 0.4018F);
		head.texOffs(40, 0).addBox(-2.9997F, -12.9854F, -2.9991F, 6.0F, 13.0F, 6.0F, 0.0F, false);
		head.texOffs(40, 19).addBox(-3.0003F, -9.5146F, -3.0009F, 6.0F, 0.0F, 6.0F, 0.0F, false);

		left_side_leaf = new ModelRenderer(this);
		left_side_leaf.setPos(3.5246F, 30.4557F, -0.4018F);
		head.addChild(left_side_leaf);
		left_side_leaf.texOffs(24, 53).addBox(-0.5243F, -39.9411F, -0.0972F, 6.0F, 11.0F, 0.0F, 0.0F, false);

		right_side_leaf = new ModelRenderer(this);
		right_side_leaf.setPos(3.5246F, 30.4557F, -0.4018F);
		head.addChild(right_side_leaf);
		right_side_leaf.texOffs(24, 41).addBox(-12.0243F, -41.9411F, -0.0972F, 6.0F, 12.0F, 0.0F, 0.0F, false);

		arm_left = new ModelRenderer(this);
		arm_left.setPos(7.0004F, -4.5015F, 0.5F);
		arm_left.texOffs(52, 39).addBox(-1.5004F, -4.4985F, -1.5F, 3.0F, 22.0F, 3.0F, 0.0F, false);
		arm_left.texOffs(52, 26).addBox(-1.4996F, -2.0015F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);

		arm_right = new ModelRenderer(this);
		arm_right.setPos(-8.0F, -5.5F, 0.5F);
		arm_right.texOffs(40, 39).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 22.0F, 3.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(CreakingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.animateHeadLookTarget(netHeadYaw, headPitch);
		this.arm_right.xRot = MathHelper.cos(limbSwing * 0.8662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		this.arm_left.xRot = MathHelper.cos(limbSwing * 0.8662F) * 2.0F * limbSwingAmount * 0.5F;

	}

	private void animateHeadLookTarget(float yaw, float pitch) {
		this.head.xRot = pitch * 0.017453292F;
		this.head.yRot = yaw * 0.017453292F;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		leg_left.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leg_right.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		head.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		arm_left.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		arm_right.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}