package net.minecraft.entity.boss.sovereign;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.boss.sovereign.InfernalSovereignEntity;

public class InfernalSovereignModel<T extends InfernalSovereignEntity> extends HierarchicalModel<T> {
	private final ModelRenderer root;
	private final ModelRenderer body;
	private final ModelRenderer body_r1;
	private final ModelRenderer left_arm;
	private final ModelRenderer left_arm_r1;
	private final ModelRenderer inner_left_arm;
	private final ModelRenderer hand4_left_arm_r1;
	private final ModelRenderer hand3_left_arm_r1;
	private final ModelRenderer hand2_left_arm_r1;
	private final ModelRenderer hand1_left_arm_r1;
	private final ModelRenderer inner_left_arm_r1;
	private final ModelRenderer right_arm;
	private final ModelRenderer left_arm_r2;
	private final ModelRenderer inner_right_arm2;
	private final ModelRenderer hand4_left_arm_r2;
	private final ModelRenderer hand3_left_arm_r2;
	private final ModelRenderer hand_left_arm_r1;
	private final ModelRenderer hand1_left_arm_r2;
	private final ModelRenderer inner_left_arm_r2;
	private final ModelRenderer left_leg;
	private final ModelRenderer left_leg_r1;
	private final ModelRenderer left_inner_leg;
	private final ModelRenderer left_inner_leg_r1;
	private final ModelRenderer left_inner_inner_leg;
	private final ModelRenderer left_inner_inner_leg_r1;
	private final ModelRenderer right_leg;
	private final ModelRenderer right_leg_r1;
	private final ModelRenderer right_inner_leg;
	private final ModelRenderer right_inner_leg_r1;
	private final ModelRenderer right_inner_inner_leg;
	private final ModelRenderer right_inner_inner_leg_r1;
	private final ModelRenderer head;
	private final ModelRenderer head_r1;
	private final ModelRenderer horn;
	private final ModelRenderer horn_r1;
	private final ModelRenderer horn_r2;
	private final ModelRenderer horn_r3;
	private final ModelRenderer horn2;
	private final ModelRenderer horn2_r1;
	private final ModelRenderer horn2_r2;
	private final ModelRenderer horn2_r3;
	private final ModelRenderer crown;
	private final ModelRenderer crown_r1;

	public InfernalSovereignModel() {
		texWidth = 64;
		texHeight = 64;

		root = new ModelRenderer(this, "root");
		root.setPos(0.0F, 24.0F, 3.5F);
		

		body = new ModelRenderer(this, "body");
		body.setPos(0.0F, -16.5F, 1.0F);
		root.addChild(body);
		

		body_r1 = new ModelRenderer(this);
		body_r1.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(body_r1);
		setRotationAngle(body_r1, 0.1745F, 0.0F, 0.0F);
		body_r1.texOffs(22, 32).addBox(-6.0F, -16.0F, -6.0F, 12.0F, 23.0F, 9.0F, 0.0F, false);

		left_arm = new ModelRenderer(this, "left_arm");
		left_arm.setPos(5.5F, -8.0F, -4.5F);
		body.addChild(left_arm);
		setRotationAngle(left_arm, 0.0F, 0.2182F, 0.3054F);
		

		left_arm_r1 = new ModelRenderer(this);
		left_arm_r1.setPos(-1.75F, -1.0F, 0.5F);
		left_arm.addChild(left_arm_r1);
		setRotationAngle(left_arm_r1, 0.0F, 0.0F, 0.6545F);
		left_arm_r1.texOffs(32, 0).addBox(-2.0F, -3.0F, -1.0F, 13.0F, 3.0F, 3.0F, 0.0F, false);

		inner_left_arm = new ModelRenderer(this);
		inner_left_arm.setPos(7.75F, 4.5F, 1.0F);
		left_arm.addChild(inner_left_arm);
		setRotationAngle(inner_left_arm, 0.035F, 0.2595F, 0.1355F);
		

		hand4_left_arm_r1 = new ModelRenderer(this);
		hand4_left_arm_r1.setPos(2.0F, 3.25F, 1.5F);
		inner_left_arm.addChild(hand4_left_arm_r1);
		setRotationAngle(hand4_left_arm_r1, 1.2107F, 0.0543F, 1.4275F);
		hand4_left_arm_r1.texOffs(0, 62).addBox(-1.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, 0.0F, false);

		hand3_left_arm_r1 = new ModelRenderer(this);
		hand3_left_arm_r1.setPos(1.0F, 3.75F, -0.75F);
		inner_left_arm.addChild(hand3_left_arm_r1);
		setRotationAngle(hand3_left_arm_r1, 1.2069F, -0.0076F, 1.2643F);
		hand3_left_arm_r1.texOffs(0, 62).addBox(-1.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, 0.0F, false);

		hand2_left_arm_r1 = new ModelRenderer(this);
		hand2_left_arm_r1.setPos(5.75F, 3.75F, -0.75F);
		inner_left_arm.addChild(hand2_left_arm_r1);
		setRotationAngle(hand2_left_arm_r1, -0.1337F, 0.1184F, 1.546F);
		hand2_left_arm_r1.texOffs(0, 62).addBox(-1.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, 0.0F, false);

		hand1_left_arm_r1 = new ModelRenderer(this);
		hand1_left_arm_r1.setPos(5.75F, 3.75F, 0.75F);
		inner_left_arm.addChild(hand1_left_arm_r1);
		setRotationAngle(hand1_left_arm_r1, -0.1229F, 0.1295F, 1.6333F);
		hand1_left_arm_r1.texOffs(0, 62).addBox(-1.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, 0.0F, false);

		inner_left_arm_r1 = new ModelRenderer(this);
		inner_left_arm_r1.setPos(0.0F, 2.5F, 0.0F);
		inner_left_arm.addChild(inner_left_arm_r1);
		setRotationAngle(inner_left_arm_r1, 0.0F, 0.0F, 0.6545F);
		inner_left_arm_r1.texOffs(0, 0).addBox(-2.0F, -4.0F, -2.0F, 8.0F, 4.0F, 4.0F, 0.0F, false);

		right_arm = new ModelRenderer(this, "right_arm");
		right_arm.setPos(-5.5F, -10.75F, -3.0F);
		body.addChild(right_arm);
		setRotationAngle(right_arm, 0.0F, -0.2182F, -0.3054F);
		

		left_arm_r2 = new ModelRenderer(this);
		left_arm_r2.setPos(1.75F, -1.0F, 0.5F);
		right_arm.addChild(left_arm_r2);
		setRotationAngle(left_arm_r2, 0.0F, 0.0F, -0.6545F);
		left_arm_r2.texOffs(32, 0).addBox(-11.0F, -3.0F, -1.0F, 13.0F, 3.0F, 3.0F, 0.0F, true);

		inner_right_arm2 = new ModelRenderer(this);
		inner_right_arm2.setPos(-7.75F, 4.5F, 1.0F);
		right_arm.addChild(inner_right_arm2);
		setRotationAngle(inner_right_arm2, 0.2691F, -0.209F, -0.2177F);
		

		hand4_left_arm_r2 = new ModelRenderer(this);
		hand4_left_arm_r2.setPos(-2.0F, 2.75F, 1.5F);
		inner_right_arm2.addChild(hand4_left_arm_r2);
		setRotationAngle(hand4_left_arm_r2, 1.2297F, -0.1295F, -1.6333F);
		hand4_left_arm_r2.texOffs(0, 62).addBox(-4.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, 0.0F, true);

		hand3_left_arm_r2 = new ModelRenderer(this);
		hand3_left_arm_r2.setPos(-2.5F, 3.75F, -0.75F);
		inner_right_arm2.addChild(hand3_left_arm_r2);
		setRotationAngle(hand3_left_arm_r2, 1.2297F, -0.1295F, -1.6333F);
		hand3_left_arm_r2.texOffs(0, 62).addBox(-4.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, 0.0F, true);

		hand_left_arm_r1 = new ModelRenderer(this);
		hand_left_arm_r1.setPos(-5.75F, 3.75F, -0.75F);
		inner_right_arm2.addChild(hand_left_arm_r1);
		setRotationAngle(hand_left_arm_r1, -0.1521F, -0.0934F, -1.3723F);
		hand_left_arm_r1.texOffs(0, 62).addBox(-4.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, 0.0F, true);

		hand1_left_arm_r2 = new ModelRenderer(this);
		hand1_left_arm_r2.setPos(-5.75F, 3.75F, 0.75F);
		inner_right_arm2.addChild(hand1_left_arm_r2);
		setRotationAngle(hand1_left_arm_r2, -0.1521F, -0.0934F, -1.3723F);
		hand1_left_arm_r2.texOffs(0, 62).addBox(-4.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, 0.0F, true);

		inner_left_arm_r2 = new ModelRenderer(this);
		inner_left_arm_r2.setPos(0.0F, 2.5F, 0.0F);
		inner_right_arm2.addChild(inner_left_arm_r2);
		setRotationAngle(inner_left_arm_r2, 0.0F, 0.0F, -0.6545F);
		inner_left_arm_r2.texOffs(0, 0).addBox(-6.0F, -4.0F, -2.0F, 8.0F, 4.0F, 4.0F, 0.0F, true);

		left_leg = new ModelRenderer(this, "left_leg");
		left_leg.setPos(3.1657F, -10.1311F, 0.5494F);


		left_leg_r1 = new ModelRenderer(this);
		left_leg_r1.setPos(-0.6657F, 1.6311F, -0.5494F);
		left_leg.addChild(left_leg_r1);
		setRotationAngle(left_leg_r1, 0.1495F, 0.1701F, -0.0718F);
		left_leg_r1.texOffs(0, 25).addBox(-1.0F, -2.0F, -1.0F, 3.0F, 6.0F, 3.0F, 0.0F, false);

		left_inner_leg = new ModelRenderer(this);
		left_inner_leg.setPos(0.5689F, 5.2626F, 0.4082F);
		left_leg.addChild(left_inner_leg);
		

		left_inner_leg_r1 = new ModelRenderer(this);
		left_inner_leg_r1.setPos(-1.2346F, 0.8685F, -0.2077F);
		left_inner_leg.addChild(left_inner_leg_r1);
		setRotationAngle(left_inner_leg_r1, 0.6294F, 0.1701F, -0.0718F);
		left_inner_leg_r1.texOffs(0, 46).addBox(0.0F, -2.0F, 0.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);

		left_inner_inner_leg = new ModelRenderer(this);
		left_inner_inner_leg.setPos(0.618F, 3.023F, 2.417F);
		left_inner_leg.addChild(left_inner_inner_leg);
		

		left_inner_inner_leg_r1 = new ModelRenderer(this);
		left_inner_inner_leg_r1.setPos(-1.8526F, 0.3456F, -0.1247F);
		left_inner_inner_leg.addChild(left_inner_inner_leg_r1);
		setRotationAngle(left_inner_inner_leg_r1, 0.9349F, 0.1701F, -0.0718F);
		left_inner_inner_leg_r1.texOffs(0, 41).addBox(1.0F, 0.0F, 0.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		right_leg = new ModelRenderer(this, "right_leg");
		right_leg.setPos(-3.1657F, -10.1311F, 3.0494F);
		setRotationAngle(right_leg, 0.1745F, 0.0F, 0.0F);
		

		right_leg_r1 = new ModelRenderer(this);
		right_leg_r1.setPos(0.6657F, 1.6311F, -0.5494F);
		right_leg.addChild(right_leg_r1);
		setRotationAngle(right_leg_r1, 0.1495F, -0.1701F, 0.0718F);
		right_leg_r1.texOffs(0, 25).addBox(-2.0F, -2.0F, -1.0F, 3.0F, 6.0F, 3.0F, 0.0F, true);

		right_inner_leg = new ModelRenderer(this);
		right_inner_leg.setPos(-0.5689F, 5.2626F, 0.4082F);
		right_leg.addChild(right_inner_leg);
		

		right_inner_leg_r1 = new ModelRenderer(this);
		right_inner_leg_r1.setPos(1.2346F, 0.8685F, -0.2077F);
		right_inner_leg.addChild(right_inner_leg_r1);
		setRotationAngle(right_inner_leg_r1, 0.6294F, -0.1701F, 0.0718F);
		right_inner_leg_r1.texOffs(0, 46).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 6.0F, 2.0F, 0.0F, true);

		right_inner_inner_leg = new ModelRenderer(this);
		right_inner_inner_leg.setPos(-0.618F, 3.023F, 2.417F);
		right_inner_leg.addChild(right_inner_inner_leg);
		

		right_inner_inner_leg_r1 = new ModelRenderer(this);
		right_inner_inner_leg_r1.setPos(1.8526F, 0.3456F, -0.1247F);
		right_inner_inner_leg.addChild(right_inner_inner_leg_r1);
		setRotationAngle(right_inner_inner_leg_r1, 0.9349F, -0.1701F, 0.0718F);
		right_inner_inner_leg_r1.texOffs(0, 41).addBox(-2.0F, 0.0F, 0.0F, 1.0F, 4.0F, 1.0F, 0.0F, true);

		head = new ModelRenderer(this, "head");
		head.setPos(0.0F, -13.5F, -7.0F);
		body.addChild(head);
		

		head_r1 = new ModelRenderer(this);
		head_r1.setPos(1.0F, 1.5F, 1.5F);
		head.addChild(head_r1);
		setRotationAngle(head_r1, 0.2618F, 0.0F, 0.0F);
		head_r1.texOffs(32, 12).addBox(-5.0F, -10.8377F, -3.8631F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		horn = new ModelRenderer(this);
		horn.setPos(0.0F, 0.0F, 1.0F);
		head.addChild(horn);
		setRotationAngle(horn, 0.1745F, 0.0F, 0.0F);
		

		horn_r1 = new ModelRenderer(this);
		horn_r1.setPos(-9.5F, -7.5F, 0.0F);
		horn.addChild(horn_r1);
		setRotationAngle(horn_r1, 0.4373F, -0.2182F, 1.2937F);
		horn_r1.texOffs(19, 34).addBox(-3.0F, -2.0F, 0.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);

		horn_r2 = new ModelRenderer(this);
		horn_r2.setPos(-6.5F, -5.0F, 0.0F);
		horn.addChild(horn_r2);
		setRotationAngle(horn_r2, 0.4852F, 0.0192F, 0.8236F);
		horn_r2.texOffs(15, 29).addBox(-3.0F, -2.0F, -1.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);

		horn_r3 = new ModelRenderer(this);
		horn_r3.setPos(-4.0F, -5.0F, 0.0F);
		horn.addChild(horn_r3);
		setRotationAngle(horn_r3, 0.6545F, 0.0F, 0.0F);
		horn_r3.texOffs(9, 35).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);

		horn2 = new ModelRenderer(this);
		horn2.setPos(0.0F, 0.0F, 1.0F);
		head.addChild(horn2);
		setRotationAngle(horn2, 0.1745F, 0.0F, 0.0F);
		

		horn2_r1 = new ModelRenderer(this);
		horn2_r1.setPos(9.5F, -7.5F, 0.0F);
		horn2.addChild(horn2_r1);
		setRotationAngle(horn2_r1, 0.4373F, 0.2182F, -1.2937F);
		horn2_r1.texOffs(19, 34).addBox(-1.0F, -2.0F, 0.0F, 4.0F, 1.0F, 1.0F, 0.0F, true);

		horn2_r2 = new ModelRenderer(this);
		horn2_r2.setPos(6.5F, -5.0F, 0.0F);
		horn2.addChild(horn2_r2);
		setRotationAngle(horn2_r2, 0.4852F, -0.0192F, -0.8236F);
		horn2_r2.texOffs(15, 29).addBox(-1.0F, -2.0F, -1.0F, 4.0F, 2.0F, 2.0F, 0.0F, true);

		horn2_r3 = new ModelRenderer(this);
		horn2_r3.setPos(4.0F, -5.0F, 0.0F);
		horn2.addChild(horn2_r3);
		setRotationAngle(horn2_r3, 0.6545F, 0.0F, 0.0F);
		horn2_r3.texOffs(9, 35).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 3.0F, 3.0F, 0.0F, true);

		crown = new ModelRenderer(this);
		crown.setPos(1.0F, 1.5F, 1.5F);
		head.addChild(crown);
		

		crown_r1 = new ModelRenderer(this);
		crown_r1.setPos(0.0F, 0.0F, 0.0F);
		crown.addChild(crown_r1);
		setRotationAngle(crown_r1, 0.2618F, 0.0F, 0.0F);
		crown_r1.texOffs(0, 11).addBox(-5.0F, -14.8377F, -3.8631F, 8.0F, 6.0F, 8.0F, 0.2F, false);

		root.addChildren(this.left_leg, this.right_leg);
	}

	@Override
	public ImmutableSet<ModelRenderer> getAllParts() {
		return ImmutableSet.of(this.left_leg, this.left_arm, this.right_leg, this.right_arm, this.head, this.crown, this.body, this.horn, this.horn2);
	}

	@Override
	public ModelRenderer root() {
		return this.root;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		//previously the render function, render code was moved to a method below
		this.animate(entity.attackingAnimation, InfernalAnimation.PUNCH, ageInTicks);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}