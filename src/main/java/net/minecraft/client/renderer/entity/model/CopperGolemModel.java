package net.minecraft.client.renderer.entity.model;// Made with Blockbench 4.12.2
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.animation.definitions.CopperGolemAnimation;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.CopperGolemEntity;

public class CopperGolemModel extends HierarchicalModel<CopperGolemEntity> {
	private final ModelRenderer root;
	private final ModelRenderer body;
	private final ModelRenderer head;
	private final ModelRenderer rod;
	private final ModelRenderer nose;
	private final ModelRenderer leftArm;
	private final ModelRenderer rightArm;
	private final ModelRenderer rightLeg;
	private final ModelRenderer leftLeg;

	public CopperGolemModel() {
		texWidth = 64;
		texHeight = 64;

		root = new ModelRenderer(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new ModelRenderer(this, "body");
		body.setPos(0.0F, -5.0F, 0.0F);
		root.addChild(body);
		body.texOffs(40, 0).addBox(-4.0F, -5.0F, -2.0F, 8.0F, 5.0F, 4.0F, 0.0F, false);

		head = new ModelRenderer(this, "head");
		head.setPos(0.0F, -5.0F, 0.0F);
		body.addChild(head);
		head.texOffs(0, 0).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 5.0F, 8.0F, 0.001F, true);

		rod = new ModelRenderer(this, "rod");
		rod.setPos(0.0F, -5.0F, 0.0F);
		head.addChild(rod);
		rod.texOffs(56, 10).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
		rod.texOffs(40, 9).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

		nose = new ModelRenderer(this, "nose");
		nose.setPos(0.0F, -1.0F, -4.0F);
		head.addChild(nose);
		nose.texOffs(56, 15).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

		leftArm = new ModelRenderer(this, "leftArm");
		leftArm.setPos(4.0F, -5.0F, 0.0F);
		body.addChild(leftArm);
		leftArm.texOffs(10, 17).addBox(0.0F, -1.0F, -1.5F, 2.0F, 10.0F, 3.0F, 0.0F, false);

		rightArm = new ModelRenderer(this, "rightArm");
		rightArm.setPos(-4.0F, -5.0F, 0.0F);
		body.addChild(rightArm);
		rightArm.texOffs(0, 17).addBox(-2.0F, -1.0F, -1.5F, 2.0F, 10.0F, 3.0F, 0.0F, false);

		rightLeg = new ModelRenderer(this, "rightLeg");
		rightLeg.setPos(-2.0F, -5.0F, 0.0F);
		root.addChild(rightLeg);
		rightLeg.texOffs(20, 17).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 5.0F, 3.0F, 0.0F, false);

		leftLeg = new ModelRenderer(this, "leftLeg");
		leftLeg.setPos(2.0F, -5.0F, 0.0F);
		root.addChild(leftLeg);
		leftLeg.texOffs(34, 17).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 5.0F, 3.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(CopperGolemEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		if (entity.isOxidized()) return;
		this.headTurn(head, netHeadYaw, headPitch);
		this.animateWalk(CopperGolemAnimation.walk, limbSwing, limbSwingAmount, 3f, 2f);
		this.animate(entity.buttonPush, CopperGolemAnimation.press_button_down, entity.tickCount);
		this.animate(entity.spinningHead, CopperGolemAnimation.spin_head, entity.tickCount);

	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelRenderer root() {
		return this.root;
	}

	@Override
	public ImmutableSet<ModelRenderer> getAllParts() {
		return ImmutableSet.of(this.root, this.head, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg, this.rod);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}