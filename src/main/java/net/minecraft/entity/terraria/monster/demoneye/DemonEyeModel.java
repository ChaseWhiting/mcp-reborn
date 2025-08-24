package net.minecraft.entity.terraria.monster.demoneye;// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import org.jetbrains.annotations.NotNull;

public class DemonEyeModel extends HierarchicalModel<DemonEyeEntity> {
	private final ModelRenderer root;
	private final ModelRenderer head;
	private final ModelRenderer right_side_r1;
	private final ModelRenderer left_side_r1;
	private final ModelRenderer tail;
	private final ModelRenderer tail3;
	private final ModelRenderer tail3_r1;
	private final ModelRenderer tail1;
	private final ModelRenderer tail1_r1;
	private final ModelRenderer tail2;
	private final ModelRenderer tail2_r1;

	public DemonEyeModel() {
		texWidth = 64;
		texHeight = 32;

		root = new ModelRenderer(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);
		

		head = new ModelRenderer(this);
		head.setPos(0.0F, -3.0F, 0.1F);
		root.addChild(head);
		head.texOffs(0, 0).addBox(-3.0F, -3.0F, -3.1F, 6.0F, 6.0F, 6.0F, 0.0F, false);
		head.texOffs(0, 27).addBox(-2.0F, -2.0F, -3.6F, 4.0F, 4.0F, 1.0F, 0.0F, false);
		head.texOffs(20, 26).addBox(-2.5F, -2.5F, 2.4F, 5.0F, 5.0F, 1.0F, 0.0F, false);

		right_side_r1 = new ModelRenderer(this);
		right_side_r1.setPos(-2.75F, 0.0F, 0.15F);
		head.addChild(right_side_r1);
		setRotationAngle(right_side_r1, 0.0F, 1.5708F, 0.0F);
		right_side_r1.texOffs(0, 19).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, 0.0F, true);

		left_side_r1 = new ModelRenderer(this);
		left_side_r1.setPos(2.75F, 0.0F, 0.15F);
		head.addChild(left_side_r1);
		setRotationAngle(left_side_r1, 0.0F, -1.5708F, 0.0F);
		left_side_r1.texOffs(22, 19).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, 0.0F, false);

		tail = new ModelRenderer(this);
		tail.setPos(-0.3333F, 0.25F, 2.9833F);
		head.addChild(tail);
		

		tail3 = new ModelRenderer(this, "tail3");
		tail3.setPos(-1.6667F, 0.5F, 0.1667F);
		tail.addChild(tail3);
		

		tail3_r1 = new ModelRenderer(this);
		tail3_r1.setPos(0.0F, 0.0F, 0.0F);
		tail3.addChild(tail3_r1);
		setRotationAngle(tail3_r1, 0.0F, 1.5708F, 0.0F);
		tail3_r1.texOffs(42, 17).addBox(-5.0F, -2.0F, 0.0F, 9.0F, 4.0F, 0.0F, 0.0F, false);

		tail1 = new ModelRenderer(this, "tail1");
		tail1.setPos(1.8333F, -0.5F, 0.1667F);
		tail.addChild(tail1);
		

		tail1_r1 = new ModelRenderer(this);
		tail1_r1.setPos(0.0F, 0.0F, 0.0F);
		tail1.addChild(tail1_r1);
		setRotationAngle(tail1_r1, 0.0F, 1.5708F, 0.0F);
		tail1_r1.texOffs(24, 0).addBox(-7.0F, -0.5F, 0.0F, 11.0F, 3.0F, 0.0F, 0.0F, false);

		tail2 = new ModelRenderer(this, "tail2");
		tail2.setPos(-0.1667F, -1.5F, 0.1667F);
		tail.addChild(tail2);
		

		tail2_r1 = new ModelRenderer(this);
		tail2_r1.setPos(0.0F, 0.0F, 0.0F);
		tail2.addChild(tail2_r1);
		setRotationAngle(tail2_r1, 0.0F, 1.5708F, 0.0F);
		tail2_r1.texOffs(38, 26).addBox(-8.0F, -1.0F, 0.0F, 12.0F, 3.0F, 0.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(@NotNull DemonEyeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		//this.animate(entity.tails, DemonEyeAnimation.IDLE, ageInTicks);
		this.animateWalk(DemonEyeAnimation.IDLE, limbSwing, limbSwingAmount, 4f, 6f);
		this.headTurn(this.root, netHeadYaw, headPitch);
	}

	@Override
	public ImmutableSet<ModelRenderer> getAllParts() {
		return ImmutableSet.of(this.tail1, tail2, tail3, head);
	}

	@Override
	public ModelRenderer root() {
		return this.root;
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}