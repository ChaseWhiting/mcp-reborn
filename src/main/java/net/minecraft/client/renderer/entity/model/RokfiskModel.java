package net.minecraft.client.renderer.entity.model;// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.animation.definitions.RokfiskAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.NewHierarchicalModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.entity.passive.fish.RokfiskEntity;

public class RokfiskModel<T extends RokfiskEntity> extends NewHierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart body_front;
	private final ModelPart head;
	private final ModelPart fin_left;
	private final ModelPart fin_right;
	private final ModelPart body_back;
	private final ModelPart fin_back_2;
	private final ModelPart tail;
	private final ModelPart fin_back_1;

	public RokfiskModel(ModelPart root) {
		this.root = root.getChild("root");
		this.body_front = this.root.getChild("body_front");
		this.head = this.body_front.getChild("head");
		this.fin_left = this.body_front.getChild("fin_left");
		this.fin_right = this.body_front.getChild("fin_right");
		this.body_back = this.body_front.getChild("body_back");
		this.fin_back_2 = this.body_back.getChild("fin_back_2");
		this.tail = this.body_back.getChild("tail");
		this.fin_back_1 = this.body_front.getChild("fin_back_1");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 21.0F, 0.0F));

		PartDefinition body_front = root.addOrReplaceChild("body_front", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -4.5F, 0.0F, 5.0F, 7.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, -9.0F));

		PartDefinition head = body_front.addOrReplaceChild("head", CubeListBuilder.create().texOffs(22, 29).addBox(-2.0F, -3.0F, -4.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, 0.0F));

		PartDefinition fin_left = body_front.addOrReplaceChild("fin_left", CubeListBuilder.create().texOffs(-4, 30).addBox(-2.0F, 0.0F, 0.0F, 5.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 2.5F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition fin_right = body_front.addOrReplaceChild("fin_right", CubeListBuilder.create().texOffs(-4, 34).addBox(-3.0F, 0.0F, 0.0F, 5.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 2.5F, 1.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition body_back = body_front.addOrReplaceChild("body_back", CubeListBuilder.create().texOffs(0, 16).addBox(-2.5F, -3.5F, 0.0F, 5.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 9.0F));

		PartDefinition fin_back_2 = body_back.addOrReplaceChild("fin_back_2", CubeListBuilder.create().texOffs(19, -4).addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.5F, 0.0F));

		PartDefinition tail = body_back.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(18, 9).addBox(0.0F, -3.5F, 0.0F, 0.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 8.0F));

		PartDefinition fin_back_1 = body_front.addOrReplaceChild("fin_back_1", CubeListBuilder.create().texOffs(19, -1).addBox(0.0F, -1.0F, 0.0F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.5F, 4.0F));

		return LayerDefinition.create(meshdefinition, 48, 48);
	}

	@Override
	public void setupAnim(RokfiskEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);

//		float f = 1.0F;
//		float f1 = 1.0F;
//		if (!entity.isInLava()) {
//			f = 1.3F;
//			f1 = 1.7F;
//		}
//
//
//		this.body_back.yRot = -f * 0.25F * MathHelper.sin(f1 * 0.6F * ageInTicks);

		this.animate(entity.swimState, RokfiskAnimation.SWIM, ageInTicks);
		this.animate(entity.flopState, RokfiskAnimation.FLOP, ageInTicks);
	}

	public ModelPart root() {
		return this.root;
	}

	@Override
	public void renderToBuffer(MatrixStack poseStack, IVertexBuilder vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}