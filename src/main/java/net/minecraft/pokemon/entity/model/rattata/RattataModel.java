package net.minecraft.pokemon.entity.model.rattata;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.pokemon.entity.RattataEntity;

public class RattataModel extends HierarchicalModel<RattataEntity> {
	private final ModelRenderer root;
	private final ModelRenderer body;
	private final ModelRenderer head;
	private final ModelRenderer rightEar;
	private final ModelRenderer rightEar_r1;
	private final ModelRenderer leftEar;
	private final ModelRenderer leftEar_r1;
	private final ModelRenderer rightWhisker;
	private final ModelRenderer rightWhisker_r1;
	private final ModelRenderer leftWhisker;
	private final ModelRenderer leftWhisker_r1;
	private final ModelRenderer nose;
	private final ModelRenderer nose_r1;
	private final ModelRenderer tooth;
	private final ModelRenderer tooth_r1;
	private final ModelRenderer tail;
	private final ModelRenderer tailUpper;
	private final ModelRenderer tailUpper2;
	private final ModelRenderer rightLeg;
	private final ModelRenderer leftLeg;
	private final ModelRenderer upperRightLeg;
	private final ModelRenderer upperLeftLeg;

	public RattataModel() {
		texWidth = 48;
		texHeight = 32;

		root = new ModelRenderer(this, "root");
		root.setPos(0.0F, 23.5F, 0.0F);
		

		body = new ModelRenderer(this, "body");
		body.setPos(0.0F, 0.0F, 0.0F);
		root.addChild(body);
		body.texOffs(16, 20).addBox(-2.5F, -6.0F, -4.5F, 5.0F, 4.0F, 8.0F, 0.0F, false);

		head = new ModelRenderer(this, "head");
		head.setPos(0.0F, -5.25F, -3.75F);
		body.addChild(head);
		head.texOffs(0, 0).addBox(-2.5F, -4.0F, -5.0F, 5.0F, 5.0F, 5.0F, -0.2F, false);

		rightEar = new ModelRenderer(this, "rightEar");
		rightEar.setPos(-2.4303F, -3.3295F, -1.5292F);
		head.addChild(rightEar);
		

		rightEar_r1 = new ModelRenderer(this);
		rightEar_r1.setPos(0.4303F, 1.0795F, -1.2208F);
		rightEar.addChild(rightEar_r1);
		setRotationAngle(rightEar_r1, 0.0317F, 0.3477F, 0.0928F);
		rightEar_r1.texOffs(0, 19).addBox(-3.0F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, 0.0F, true);

		leftEar = new ModelRenderer(this, "leftEar");
		leftEar.setPos(2.4303F, -3.3295F, -1.5292F);
		head.addChild(leftEar);
		

		leftEar_r1 = new ModelRenderer(this);
		leftEar_r1.setPos(-0.4303F, 1.0795F, -1.2208F);
		leftEar.addChild(leftEar_r1);
		setRotationAngle(leftEar_r1, 0.0317F, -0.3477F, -0.0928F);
		leftEar_r1.texOffs(0, 19).addBox(-1.0F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, 0.0F, false);

		rightWhisker = new ModelRenderer(this, "rightWhisker");
		rightWhisker.setPos(-2.3543F, -0.1193F, -2.6185F);
		head.addChild(rightWhisker);
		

		rightWhisker_r1 = new ModelRenderer(this);
		rightWhisker_r1.setPos(-0.1457F, -0.1307F, -0.6315F);
		rightWhisker.addChild(rightWhisker_r1);
		setRotationAngle(rightWhisker_r1, -0.0406F, 0.4346F, -0.0962F);
		rightWhisker_r1.texOffs(15, 0).addBox(-3.5F, -0.5F, 0.0F, 4.0F, 1.0F, 0.0F, 0.0F, false);

		leftWhisker = new ModelRenderer(this, "leftWhisker");
		leftWhisker.setPos(2.3543F, -0.1193F, -2.6185F);
		head.addChild(leftWhisker);
		

		leftWhisker_r1 = new ModelRenderer(this);
		leftWhisker_r1.setPos(0.1457F, -0.1307F, -0.6315F);
		leftWhisker.addChild(leftWhisker_r1);
		setRotationAngle(leftWhisker_r1, -0.0406F, -0.4346F, 0.0962F);
		leftWhisker_r1.texOffs(15, 0).addBox(-0.5F, -0.5F, 0.0F, 4.0F, 1.0F, 0.0F, 0.0F, true);

		nose = new ModelRenderer(this, "nose");
		nose.setPos(0.0F, 5.25F, 3.75F);
		head.addChild(nose);
		

		nose_r1 = new ModelRenderer(this);
		nose_r1.setPos(-1.5F, -4.25F, -9.0F);
		nose.addChild(nose_r1);
		setRotationAngle(nose_r1, 0.1309F, 0.0F, 0.0F);
		nose_r1.texOffs(0, 10).addBox(0.0F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, -0.2111F, false);

		tooth = new ModelRenderer(this, "tooth");
		tooth.setPos(0.0F, -4.6585F, -9.4388F);
		nose.addChild(tooth);
		

		tooth_r1 = new ModelRenderer(this);
		tooth_r1.setPos(0.0F, 1.6585F, -0.8112F);
		tooth.addChild(tooth_r1);
		setRotationAngle(tooth_r1, 0.1745F, 0.0F, 0.0F);
		tooth_r1.texOffs(0, 14).addBox(-1.0F, -2.0F, 1.0F, 2.0F, 2.0F, 0.0F, 0.0F, false);

		tail = new ModelRenderer(this, "tail");
		tail.setPos(0.0F, -5.0F, 3.5F);
		body.addChild(tail);
		tail.texOffs(0, 28).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 3.0F, 0.0F, false);

		tailUpper = new ModelRenderer(this, "tailUpper");
		tailUpper.setPos(-0.5F, 0.0F, 2.0F);
		tail.addChild(tailUpper);
		tailUpper.texOffs(6, 26).addBox(0.5F, -0.5F, 0.0F, 0.0F, 1.0F, 5.0F, -0.02F, false);

		tailUpper2 = new ModelRenderer(this, "tailUpper2");
		tailUpper2.setPos(0.0F, 0.0F, 5.0F);
		tailUpper.addChild(tailUpper2);
		tailUpper2.texOffs(0, 27).addBox(0.5F, -1.5F, -0.5F, 0.0F, 2.0F, 2.0F, -0.01F, false);

		rightLeg = new ModelRenderer(this, "rightLeg");
		rightLeg.setPos(-2.0F, -3.0F, 3.0F);
		root.addChild(rightLeg);
		rightLeg.texOffs(0, 23).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

		leftLeg = new ModelRenderer(this, "leftLeg");
		leftLeg.setPos(2.0F, -3.0F, 3.0F);
		root.addChild(leftLeg);
		leftLeg.texOffs(0, 23).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

		upperRightLeg = new ModelRenderer(this, "upperRightLeg");
		upperRightLeg.setPos(-2.0F, -3.0F, -4.0F);
		root.addChild(upperRightLeg);
		upperRightLeg.texOffs(0, 23).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

		upperLeftLeg = new ModelRenderer(this, "upperLeftLeg");
		upperLeftLeg.setPos(2.0F, -3.0F, -4.0F);
		root.addChild(upperLeftLeg);
		upperLeftLeg.texOffs(0, 23).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(RattataEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
		this.animate(entity.idleAnimation, RattataAnimation.IDLE, ageInTicks);
	}

	@Override
	public ImmutableSet<ModelRenderer> getAllParts() {
		return ImmutableSet.of(this.head, this.body, this.leftLeg, this.upperLeftLeg, this.rightLeg, this.upperRightLeg, this.tail);
	}

	@Override
	public ModelRenderer root() {
		return this.root;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}