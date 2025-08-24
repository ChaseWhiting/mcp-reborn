package net.minecraft.entity.terraria.monster.demoneye;// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.Monster;
import org.jetbrains.annotations.NotNull;

public class EyeOfCthulhuModel<T extends Monster> extends HierarchicalModel<T> {
	protected final ModelRenderer root;
	protected final ModelRenderer head;
	private final ModelRenderer bottom_head;
	private final ModelRenderer teeth;
	private final ModelRenderer tooth6_r1;
	private final ModelRenderer tooth7_r1;
	private final ModelRenderer tooth6_r2;
	private final ModelRenderer tooth5_r1;
	private final ModelRenderer tooth4_r1;
	private final ModelRenderer tooth3_r1;
	private final ModelRenderer tooth2_r1;
	private final ModelRenderer tooth1_r1;
	private final ModelRenderer middle_head;
	private final ModelRenderer teeth2;
	private final ModelRenderer tooth7_r2;
	private final ModelRenderer tooth6_r3;
	private final ModelRenderer tooth5_r2;
	private final ModelRenderer tooth4_r2;
	private final ModelRenderer tooth3_r2;
	private final ModelRenderer tooth2_r2;
	private final ModelRenderer center_eye;
	private final ModelRenderer tail;
	private final ModelRenderer tail1;
	private final ModelRenderer tail1_r1;
	private final ModelRenderer tail2;
	private final ModelRenderer tail2_r1;
	private final ModelRenderer tail3;
	private final ModelRenderer tail3_r1;
	private final ModelRenderer tail4;
	private final ModelRenderer tail4_r1;
	private final ModelRenderer tail5;
	private final ModelRenderer tail5_r1;

	public EyeOfCthulhuModel() {
		texWidth = 256;
		texHeight = 256;

		root = new ModelRenderer(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);


		head = new ModelRenderer(this);
		head.setPos(0.0F, -3.0F, 0.1F);
		root.addChild(head);
		head.texOffs(80, 38).addBox(-11.5F, -20.5F, 11.4F, 23.0F, 23.0F, 1.0F, 0.0F, false);

		bottom_head = new ModelRenderer(this, "bottom_head");
		bottom_head.setPos(0.0F, -6.5F, 11.5F);
		head.addChild(bottom_head);
		bottom_head.texOffs(148, 55).addBox(-12.0F, -2.5F, -23.6F, 24.0F, 12.0F, 24.0F, 0.0F, false);

		teeth = new ModelRenderer(this);
		teeth.setPos(0.0F, -5.0F, -19.0F);
		bottom_head.addChild(teeth);
		teeth.texOffs(96, 104).addBox(-11.0F, 2.25F, -4.35F, 22.0F, 1.0F, 23.0F, 0.0F, false);

		tooth6_r1 = new ModelRenderer(this);
		tooth6_r1.setPos(9.5F, 3.25F, 8.5F);
		teeth.addChild(tooth6_r1);
		setRotationAngle(tooth6_r1, 0.7812F, 0.4031F, 0.1475F);
		tooth6_r1.texOffs(248, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, true);

		tooth7_r1 = new ModelRenderer(this);
		tooth7_r1.setPos(6.5F, 3.25F, 10.0F);
		teeth.addChild(tooth7_r1);
		setRotationAngle(tooth7_r1, 0.7675F, 0.1859F, 0.0124F);
		tooth7_r1.texOffs(248, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, true);

		tooth6_r2 = new ModelRenderer(this);
		tooth6_r2.setPos(-6.5F, 3.25F, 10.0F);
		teeth.addChild(tooth6_r2);
		setRotationAngle(tooth6_r2, 0.7675F, -0.1859F, -0.0124F);
		tooth6_r2.texOffs(248, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

		tooth5_r1 = new ModelRenderer(this);
		tooth5_r1.setPos(-9.5F, 3.25F, 8.5F);
		teeth.addChild(tooth5_r1);
		setRotationAngle(tooth5_r1, 0.7812F, -0.4031F, -0.1475F);
		tooth5_r1.texOffs(248, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

		tooth4_r1 = new ModelRenderer(this);
		tooth4_r1.setPos(-9.5F, 2.75F, 4.0F);
		teeth.addChild(tooth4_r1);
		setRotationAngle(tooth4_r1, 0.5267F, -0.2163F, -0.0289F);
		tooth4_r1.texOffs(248, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

		tooth3_r1 = new ModelRenderer(this);
		tooth3_r1.setPos(9.5F, 2.75F, 4.0F);
		teeth.addChild(tooth3_r1);
		setRotationAngle(tooth3_r1, 0.5267F, 0.2163F, 0.0289F);
		tooth3_r1.texOffs(248, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, true);

		tooth2_r1 = new ModelRenderer(this);
		tooth2_r1.setPos(9.5F, 3.5F, 0.0F);
		teeth.addChild(tooth2_r1);
		setRotationAngle(tooth2_r1, 0.3522F, 0.2163F, 0.0289F);
		tooth2_r1.texOffs(248, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, true);

		tooth1_r1 = new ModelRenderer(this);
		tooth1_r1.setPos(-9.5F, 3.5F, 0.0F);
		teeth.addChild(tooth1_r1);
		setRotationAngle(tooth1_r1, 0.3522F, -0.2163F, -0.0289F);
		tooth1_r1.texOffs(248, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

		middle_head = new ModelRenderer(this, "middle_head");
		middle_head.setPos(0.0F, -11.5F, 10.5F);
		head.addChild(middle_head);
		middle_head.texOffs(0, 80).addBox(-12.0F, -9.5F, -22.6F, 24.0F, 12.0F, 24.0F, 0.0F, false);

		teeth2 = new ModelRenderer(this);
		teeth2.setPos(0.0F, 5.5F, -18.0F);
		middle_head.addChild(teeth2);
		teeth2.texOffs(96, 104).addBox(-11.0F, -3.75F, -4.35F, 22.0F, 1.0F, 23.0F, 0.0F, false);

		tooth7_r2 = new ModelRenderer(this);
		tooth7_r2.setPos(3.5F, -3.25F, 8.5F);
		teeth2.addChild(tooth7_r2);
		setRotationAngle(tooth7_r2, -0.7812F, 0.4031F, -0.1475F);
		tooth7_r2.texOffs(239, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, true);

		tooth6_r3 = new ModelRenderer(this);
		tooth6_r3.setPos(-3.5F, -3.25F, 8.5F);
		teeth2.addChild(tooth6_r3);
		setRotationAngle(tooth6_r3, -0.7812F, -0.4031F, 0.1475F);
		tooth6_r3.texOffs(239, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

		tooth5_r2 = new ModelRenderer(this);
		tooth5_r2.setPos(-3.0F, -3.25F, 4.0F);
		teeth2.addChild(tooth5_r2);
		setRotationAngle(tooth5_r2, -0.5267F, -0.2163F, 0.0289F);
		tooth5_r2.texOffs(239, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

		tooth4_r2 = new ModelRenderer(this);
		tooth4_r2.setPos(3.0F, -3.25F, 4.0F);
		teeth2.addChild(tooth4_r2);
		setRotationAngle(tooth4_r2, -0.5267F, 0.2163F, -0.0289F);
		tooth4_r2.texOffs(239, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, true);

		tooth3_r2 = new ModelRenderer(this);
		tooth3_r2.setPos(6.0F, -3.5F, 0.0F);
		teeth2.addChild(tooth3_r2);
		setRotationAngle(tooth3_r2, -0.3522F, 0.2163F, -0.0289F);
		tooth3_r2.texOffs(239, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, true);

		tooth2_r2 = new ModelRenderer(this);
		tooth2_r2.setPos(-6.0F, -3.5F, 0.0F);
		teeth2.addChild(tooth2_r2);
		setRotationAngle(tooth2_r2, -0.3522F, -0.2163F, 0.0289F);
		tooth2_r2.texOffs(239, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

		center_eye = new ModelRenderer(this);
		center_eye.setPos(0.0F, -9.25F, 10.9F);
		head.addChild(center_eye);
		center_eye.texOffs(94, 69).addBox(-8.0F, -7.75F, -23.5F, 16.0F, 16.0F, 1.0F, 0.0F, false);

		tail = new ModelRenderer(this);
		tail.setPos(-0.3333F, 0.25F, 12.4833F);
		head.addChild(tail);


		tail1 = new ModelRenderer(this, "tail1");
		tail1.setPos(0.3333F, -4.5F, -1.8333F);
		tail.addChild(tail1);


		tail1_r1 = new ModelRenderer(this);
		tail1_r1.setPos(0.0F, -0.5F, 5.0F);
		tail1.addChild(tail1_r1);
		setRotationAngle(tail1_r1, 0.0F, 1.5708F, 0.0F);
		tail1_r1.texOffs(0, 37).addBox(-35.0F, -5.5F, 0.0F, 39.0F, 12.0F, 0.0F, 0.0F, false);

		tail2 = new ModelRenderer(this, "tail2");
		tail2.setPos(5.3333F, -8.5F, 18.6667F);
		tail.addChild(tail2);


		tail2_r1 = new ModelRenderer(this);
		tail2_r1.setPos(0.0F, -3.5F, -15.5F);
		tail2.addChild(tail2_r1);
		setRotationAngle(tail2_r1, 0.0F, 1.5708F, 0.0F);
		tail2_r1.texOffs(0, 148).addBox(-35.0F, -6.5F, 0.0F, 39.0F, 20.0F, 0.0F, 0.0F, false);

		tail3 = new ModelRenderer(this, "tail3");
		tail3.setPos(-3.6667F, -13.5F, -0.3333F);
		tail.addChild(tail3);


		tail3_r1 = new ModelRenderer(this);
		tail3_r1.setPos(0.0F, 1.5F, 3.5F);
		tail3.addChild(tail3_r1);
		setRotationAngle(tail3_r1, 0.0F, 1.5708F, 0.0F);
		tail3_r1.texOffs(2, 193).addBox(-33.0F, -6.5F, 0.0F, 37.0F, 10.0F, 0.0F, 0.0F, false);

		tail4 = new ModelRenderer(this, "tail4");
		tail4.setPos(-8.6667F, -7.0F, -0.8333F);
		tail.addChild(tail4);


		tail4_r1 = new ModelRenderer(this);
		tail4_r1.setPos(0.0F, -5.0F, 4.0F);
		tail4.addChild(tail4_r1);
		setRotationAngle(tail4_r1, 0.0F, 1.5708F, 0.0F);
		tail4_r1.texOffs(143, 195).addBox(-31.0F, -6.5F, 0.0F, 35.0F, 14.0F, 0.0F, 0.0F, false);

		tail5 = new ModelRenderer(this, "tail5");
		tail5.setPos(8.8333F, -7.5F, -0.3333F);
		tail.addChild(tail5);


		tail5_r1 = new ModelRenderer(this);
		tail5_r1.setPos(0.0F, -1.5F, 3.0F);
		tail5.addChild(tail5_r1);
		setRotationAngle(tail5_r1, 0.0F, 1.5708F, 0.0F);
		tail5_r1.texOffs(137, 155).addBox(-36.0F, -14.5F, 0.0F, 40.0F, 22.0F, 0.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.animateWalk(EyeOfCthulhuModelAnimation.IDLE, limbSwing, limbSwingAmount, 4f, 6f);
		this.headTurn(this.root, netHeadYaw, headPitch);
	}

	@Override
	public ImmutableSet<ModelRenderer> getAllParts() {
		return ImmutableSet.of(this.tail1, tail2, tail3, tail4, tail5, head);
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