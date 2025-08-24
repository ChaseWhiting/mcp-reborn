package net.minecraft.client.renderer.entity.model;


import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.animation.definitions.WoodpeckerAnimations;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.WoodpeckerEntity;

public class WoodpeckerModel extends HierarchicalModel<WoodpeckerEntity> {
	private final ModelRenderer root;
	private final ModelRenderer body;
	private final ModelRenderer body_r1;
	private final ModelRenderer body_r2;
	private final ModelRenderer left_wing;
	private final ModelRenderer right_wing;
	private final ModelRenderer tail;
	private final ModelRenderer tail_r1;
	private final ModelRenderer head;
	private final ModelRenderer head_r1;
	private final ModelRenderer beak;
	private final ModelRenderer beak_r1;
	private final ModelRenderer accessory;
	private final ModelRenderer accessory_r1;
	private final ModelRenderer left_foot;
	private final ModelRenderer right_foot;

	public WoodpeckerModel() {
		texWidth = 32;
		texHeight = 32;

		root = new ModelRenderer(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new ModelRenderer(this, "body");
		body.setPos(0.0F, -4.1794F, -0.4667F);
		root.addChild(body);
		

		body_r1 = new ModelRenderer(this);
		body_r1.setPos(0.0F, -0.6578F, -2.2852F);
		body.addChild(body_r1);
		setRotationAngle(body_r1, 0.2182F, 0.0F, 0.0F);
		body_r1.texOffs(12, 16).addBox(-1.5F, -2.0F, -0.9281F, 3.0F, 4.0F, 2.0F, -0.001F, false);

		body_r2 = new ModelRenderer(this);
		body_r2.setPos(0.0F, 0.4294F, 0.4667F);
		body.addChild(body_r2);
		setRotationAngle(body_r2, -0.1745F, 0.0F, 0.0F);
		body_r2.texOffs(0, 22).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);

		left_wing = new ModelRenderer(this, "left_wing");
		left_wing.setPos(2.25F, -2.0706F, -0.7833F);
		body.addChild(left_wing);
		setRotationAngle(left_wing, 0.0F, 0.0F, -0.3054F);
		left_wing.texOffs(7, 3).addBox(-0.25F, 0.0F, -1.5F, 0.0F, 5.0F, 8.0F, 0.0F, false);

		right_wing = new ModelRenderer(this, "right_wing");
		right_wing.setPos(-2.25F, -2.0706F, -0.7833F);
		body.addChild(right_wing);
		setRotationAngle(right_wing, 0.0F, 0.0F, 0.3054F);
		right_wing.texOffs(7, 3).addBox(0.25F, 0.0F, -1.5F, 0.0F, 5.0F, 8.0F, 0.0F, true);

		tail = new ModelRenderer(this, "tail");
		tail.setPos(0.0F, -1.0706F, 3.4667F);
		body.addChild(tail);
		

		tail_r1 = new ModelRenderer(this);
		tail_r1.setPos(0.0F, 0.0497F, 0.3439F);
		tail.addChild(tail_r1);
		setRotationAngle(tail_r1, -0.1745F, 0.0F, 0.0F);
		tail_r1.texOffs(8, 5).addBox(-2.0F, 0.0149F, -0.205F, 4.0F, 0.0F, 6.0F, 0.0F, false);

		head = new ModelRenderer(this, "head");
		head.setPos(0.0F, -1.8232F, -2.6286F);
		body.addChild(head);
		setRotationAngle(head, 0.0436F, 0.0F, 0.0F);
		

		head_r1 = new ModelRenderer(this);
		head_r1.setPos(-0.5F, -0.2473F, 0.0953F);
		head.addChild(head_r1);
		setRotationAngle(head_r1, 0.2182F, 0.0F, 0.0F);
		head_r1.texOffs(0, 0).addBox(-1.0F, -2.9F, -3.0F, 3.0F, 3.0F, 4.0F, 0.0F, false);

		beak = new ModelRenderer(this, "beak");
		beak.setPos(0.0F, 8.0027F, 3.5953F);
		head.addChild(beak);
		

		beak_r1 = new ModelRenderer(this);
		beak_r1.setPos(0.0F, -7.5F, -7.25F);
		beak.addChild(beak_r1);
		setRotationAngle(beak_r1, 0.2182F, 0.0F, 0.0F);
		beak_r1.texOffs(0, 17).addBox(-1.0F, -0.821F, -3.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);

		accessory = new ModelRenderer(this);
		accessory.setPos(0.0F, 8.0027F, 3.5953F);
		head.addChild(accessory);
		

		accessory_r1 = new ModelRenderer(this);
		accessory_r1.setPos(0.0F, -9.25F, -3.75F);
		accessory.addChild(accessory_r1);
		setRotationAngle(accessory_r1, 0.2182F, 0.0F, 0.0F);
		accessory_r1.texOffs(0, 1).addBox(0.0F, -3.9F, -2.97F, 0.0F, 4.0F, 6.0F, 0.0F, false);

		left_foot = new ModelRenderer(this, "left_foot");
		left_foot.setPos(1.0F, -1.26F, 1.5F);
		root.addChild(left_foot);
		left_foot.texOffs(2, 1).addBox(-0.5F, -1.75F, 0.0F, 1.0F, 3.0F, 0.0F, 0.0F, false);
		left_foot.texOffs(10, 0).addBox(-0.5F, 1.25F, -2.0F, 1.0F, 0.0F, 2.0F, 0.0F, false);

		right_foot = new ModelRenderer(this, "right_foot");
		right_foot.setPos(-1.0F, -1.26F, 1.5F);
		root.addChild(right_foot);
		right_foot.texOffs(2, 1).addBox(-0.5F, -1.75F, 0.0F, 1.0F, 3.0F, 0.0F, 0.0F, true);
		right_foot.texOffs(10, 0).addBox(-0.5F, 1.25F, -2.0F, 1.0F, 0.0F, 2.0F, 0.0F, true);
	}



	@Override
	public void setupAnim(WoodpeckerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		if (!entity.isPecking()) {
            this.left_wing.zRot = (float) Math.toRadians(-17.5);
            this.right_wing.zRot = (float) Math.toRadians(17.5);
        } else {
			this.left_wing.zRot = (float) Math.toRadians(-22.5);
			this.right_wing.zRot = (float) Math.toRadians(22.5);
		}
        if (!entity.isFlying()) {
			this.animateWalk(WoodpeckerAnimations.WALK, limbSwing, limbSwingAmount, 2f, 3f);
        } else {
			this.animate(entity.flyingState, WoodpeckerAnimations.FLYING, ageInTicks);
        }


            this.animate(entity.peckingState, this.getPeckSide(entity), ageInTicks);



    }

	private AnimationDefinition getPeckSide(WoodpeckerEntity entity) {
		return WoodpeckerAnimations.PECKING;
	}

	@Override
	public ImmutableSet<ModelRenderer> getAllParts() {
		return ImmutableSet.of(this.root, this.head, this.body, this.left_foot, this.right_foot, this.tail, this.left_wing, this.right_wing);
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