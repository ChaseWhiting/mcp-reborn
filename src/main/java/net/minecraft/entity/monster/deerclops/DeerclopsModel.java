package net.minecraft.entity.monster.deerclops;// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.animation.definitions.CreakingAnimation;
import net.minecraft.client.animation.definitions.DeerclopsAnimation;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Mob;

public class DeerclopsModel<T extends Mob> extends HierarchicalModel<T> {
	private final ModelRenderer root;
	private final ModelRenderer right_leg;
	private final ModelRenderer claw_right;
	private final ModelRenderer claw_right_2_r1;
	private final ModelRenderer claw_right_1_r1;
	private final ModelRenderer knee_right;
	private final ModelRenderer knee_right_tip_r1;
	private final ModelRenderer knee_right_upper_r1;
	private final ModelRenderer knee_right_lower_r1;
	private final ModelRenderer left_leg;
	private final ModelRenderer claw_left;
	private final ModelRenderer claw_left_2_r1;
	private final ModelRenderer claw_left_1_r1;
	private final ModelRenderer knee_left;
	private final ModelRenderer knee_left_tip_r1;
	private final ModelRenderer knee_left_upper_r1;
	private final ModelRenderer knee_left_lower_r1;
	private final ModelRenderer body;
	private final ModelRenderer bottom_half;
	private final ModelRenderer center;
	private final ModelRenderer left_arm;
	private final ModelRenderer left_arm_inner;
	private final ModelRenderer left_arm_inner_r1;
	private final ModelRenderer inner_left_arm;
	private final ModelRenderer left_arm_outer_r1;
	private final ModelRenderer left_arm_center_r1;
	private final ModelRenderer left_hand;
	private final ModelRenderer left_hand_inner;
	private final ModelRenderer claw_left_4_r1;
	private final ModelRenderer claw_left_3_r1;
	private final ModelRenderer claw_left_2_r2;
	private final ModelRenderer claw_left_1_r2;
	private final ModelRenderer hand_left_r1;
	private final ModelRenderer right_arm;
	private final ModelRenderer right_arm_inner;
	private final ModelRenderer right_arm_inner_r1;
	private final ModelRenderer inner_right_arm;
	private final ModelRenderer right_arm_outer_r1;
	private final ModelRenderer right_arm_center_r1;
	private final ModelRenderer right_hand;
	private final ModelRenderer right_hand_inner;
	private final ModelRenderer claw_right_4_r1;
	private final ModelRenderer claw_right_3_r1;
	private final ModelRenderer claw_right_2_r2;
	private final ModelRenderer claw_right_1_r2;
	private final ModelRenderer hand_right_r1;
	private final ModelRenderer head;
	private final ModelRenderer jaw;
	private final ModelRenderer jaw_nose_r1;
	private final ModelRenderer jaw_lower_tip_r1;
	private final ModelRenderer jaw_inner_main_r1;
	private final ModelRenderer jaw_main_upper_r1;
	private final ModelRenderer jaw2;
	private final ModelRenderer jaw_main_r1;
	private final ModelRenderer main;
	private final ModelRenderer head_upper_r1;
	private final ModelRenderer head_jaw_lower_r1;
	private final ModelRenderer antlers;
	private final ModelRenderer antler_left;
	private final ModelRenderer antler_left_tip5_r1;
	private final ModelRenderer antler_left_tip4_r1;
	private final ModelRenderer antler_left_tip3_r1;
	private final ModelRenderer antler_left_tip2_r1;
	private final ModelRenderer antler_left_tip1_r1;
	private final ModelRenderer antler_left_main_r1;
	private final ModelRenderer antler_right;
	private final ModelRenderer antler_right_tip5_r1;
	private final ModelRenderer antler_right_tip4_r1;
	private final ModelRenderer antler_right_tip3_r1;
	private final ModelRenderer antler_right_tip2_r1;
	private final ModelRenderer antler_right_tip1_r1;
	private final ModelRenderer antler_right_main_r1;
	private final ModelRenderer eye;
	private final ModelRenderer left_ear;
	private final ModelRenderer left_ear_r1;
	private final ModelRenderer right_ear;
	private final ModelRenderer right_ear_r1;

	public DeerclopsModel() {
		texWidth = 128;
		texHeight = 128;

		root = new ModelRenderer(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);


		right_leg = new ModelRenderer(this, "right_leg");
		right_leg.setPos(-3.7736F, -18.0F, 0.5405F);
		root.addChild(right_leg);
		setRotationAngle(right_leg, 0.0F, -0.0873F, 0.0F);
		right_leg.texOffs(116, 64).addBox(-1.3257F, 10.0F, 0.4924F, 3.0F, 8.0F, 3.0F, 0.0F, true);

		claw_right = new ModelRenderer(this, "claw_right");
		claw_right.setPos(5.4243F, 18.0F, 1.4924F);
		right_leg.addChild(claw_right);


		claw_right_2_r1 = new ModelRenderer(this, "claw_right_1");
		claw_right_2_r1.setPos(-5.52F, 0.25F, -1.35F);
		claw_right.addChild(claw_right_2_r1);
		setRotationAngle(claw_right_2_r1, 0.0F, -0.1745F, 0.0F);
		claw_right_2_r1.texOffs(25, 41).addBox(-0.88F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, true);

		claw_right_1_r1 = new ModelRenderer(this, "claw_right_2");
		claw_right_1_r1.setPos(-4.27F, 0.25F, -1.35F);
		claw_right.addChild(claw_right_1_r1);
		setRotationAngle(claw_right_1_r1, 0.0F, 0.1745F, 0.0F);
		claw_right_1_r1.texOffs(25, 41).addBox(-0.88F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, true);

		knee_right = new ModelRenderer(this, "knee_right");
		knee_right.setPos(5.4243F, 15.5F, 1.4924F);
		right_leg.addChild(knee_right);
		knee_right.texOffs(117, 45).addBox(-6.25F, -8.5F, -0.75F, 2.0F, 3.0F, 2.0F, 0.0F, true);

		knee_right_tip_r1 = new ModelRenderer(this, "knee_right_tip");
		knee_right_tip_r1.setPos(-3.1278F, -13.2428F, -1.6269F);
		knee_right.addChild(knee_right_tip_r1);
		setRotationAngle(knee_right_tip_r1, -0.1309F, 0.0F, 0.0F);
		knee_right_tip_r1.texOffs(117, 44).addBox(-2.5584F, -3.9363F, -0.5668F, 1.0F, 4.0F, 1.0F, 0.0F, true);

		knee_right_upper_r1 = new ModelRenderer(this, "knee_right_upper");
		knee_right_upper_r1.setPos(-4.6278F, -6.9928F, -2.3769F);
		knee_right.addChild(knee_right_upper_r1);
		setRotationAngle(knee_right_upper_r1, -0.0436F, 0.0F, 0.0F);
		knee_right_upper_r1.texOffs(120, 30).addBox(-1.5584F, -6.9363F, -0.5668F, 2.0F, 7.0F, 2.0F, 0.0F, true);

		knee_right_lower_r1 = new ModelRenderer(this, "knee_right_lower");
		knee_right_lower_r1.setPos(-5.1181F, -6.9091F, 0.2864F);
		knee_right.addChild(knee_right_lower_r1);
		setRotationAngle(knee_right_lower_r1, 1.3963F, 0.0F, 0.0F);
		knee_right_lower_r1.texOffs(116, 68).addBox(-1.6182F, -3.9394F, -0.5394F, 3.0F, 4.0F, 3.0F, 0.0F, true);

		left_leg = new ModelRenderer(this, "left_leg");
		left_leg.setPos(3.7736F, -18.0F, 0.5405F);
		root.addChild(left_leg);
		setRotationAngle(left_leg, 0.0F, 0.0873F, 0.0F);
		left_leg.texOffs(116, 64).addBox(-1.6743F, 10.0F, 0.4924F, 3.0F, 8.0F, 3.0F, 0.0F, false);

		claw_left = new ModelRenderer(this, "claw_left");
		claw_left.setPos(-5.4243F, 18.0F, 1.4924F);
		left_leg.addChild(claw_left);


		claw_left_2_r1 = new ModelRenderer(this, "claw_left_1");
		claw_left_2_r1.setPos(5.52F, 0.25F, -1.35F);
		claw_left.addChild(claw_left_2_r1);
		setRotationAngle(claw_left_2_r1, 0.0F, 0.1745F, 0.0F);
		claw_left_2_r1.texOffs(25, 41).addBox(-0.12F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		claw_left_1_r1 = new ModelRenderer(this, "claw_left_2");
		claw_left_1_r1.setPos(4.27F, 0.25F, -1.35F);
		claw_left.addChild(claw_left_1_r1);
		setRotationAngle(claw_left_1_r1, 0.0F, -0.1745F, 0.0F);
		claw_left_1_r1.texOffs(25, 41).addBox(-0.12F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		knee_left = new ModelRenderer(this, "knee_left");
		knee_left.setPos(-5.4243F, 15.5F, 1.4924F);
		left_leg.addChild(knee_left);
		knee_left.texOffs(117, 45).addBox(4.25F, -8.5F, -0.75F, 2.0F, 3.0F, 2.0F, 0.0F, false);

		knee_left_tip_r1 = new ModelRenderer(this, "knee_left_tip");
		knee_left_tip_r1.setPos(3.1278F, -13.2428F, -1.6269F);
		knee_left.addChild(knee_left_tip_r1);
		setRotationAngle(knee_left_tip_r1, -0.1309F, 0.0F, 0.0F);
		knee_left_tip_r1.texOffs(117, 44).addBox(1.5584F, -3.9363F, -0.5668F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		knee_left_upper_r1 = new ModelRenderer(this, "knee_left_upper");
		knee_left_upper_r1.setPos(4.6278F, -6.9928F, -2.3769F);
		knee_left.addChild(knee_left_upper_r1);
		setRotationAngle(knee_left_upper_r1, -0.0436F, 0.0F, 0.0F);
		knee_left_upper_r1.texOffs(120, 30).addBox(-0.4416F, -6.9363F, -0.5668F, 2.0F, 7.0F, 2.0F, 0.0F, false);

		knee_left_lower_r1 = new ModelRenderer(this, "knee_left_lower");
		knee_left_lower_r1.setPos(5.1181F, -6.9091F, 0.2864F);
		knee_left.addChild(knee_left_lower_r1);
		setRotationAngle(knee_left_lower_r1, 1.3963F, 0.0F, 0.0F);
		knee_left_lower_r1.texOffs(116, 68).addBox(-1.3818F, -3.9394F, -0.5394F, 3.0F, 4.0F, 3.0F, 0.0F, false);

		body = new ModelRenderer(this, "body");
		body.setPos(0.0F, -28.5F, 0.0F);
		root.addChild(body);


		bottom_half = new ModelRenderer(this, "bottom_half");
		bottom_half.setPos(0.0F, 28.5F, 0.0F);
		body.addChild(bottom_half);
		bottom_half.texOffs(0, 101).addBox(-5.0F, -20.0F, -5.0F, 10.0F, 2.0F, 10.0F, 0.0F, false);
		bottom_half.texOffs(0, 114).addBox(-7.0F, -22.0F, -6.0F, 14.0F, 2.0F, 12.0F, 0.0F, false);

		center = new ModelRenderer(this, "center");
		center.setPos(0.0F, 28.5F, 0.0F);
		body.addChild(center);
		center.texOffs(68, 100).addBox(-8.0F, -36.0F, -7.0F, 16.0F, 14.0F, 14.0F, 0.0F, false);
		center.texOffs(98, 87).addBox(-7.0F, -35.0F, 6.5F, 14.0F, 12.0F, 1.0F, 0.0F, false);

		left_arm = new ModelRenderer(this, "left_arm");
		left_arm.setPos(6.5F, -29.0F, -3.0F);
		center.addChild(left_arm);


		left_arm_inner = new ModelRenderer(this, "left_arm_inner");
		left_arm_inner.setPos(2.5F, 1.5F, 0.0F);
		left_arm.addChild(left_arm_inner);


		left_arm_inner_r1 = new ModelRenderer(this, "left_arm_inner_1");
		left_arm_inner_r1.setPos(-1.5F, 0.0F, 0.0F);
		left_arm_inner.addChild(left_arm_inner_r1);
		setRotationAngle(left_arm_inner_r1, 0.1504F, 0.5944F, 0.5261F);
		left_arm_inner_r1.texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 10.0F, 2.0F, 2.0F, 0.0F, false);

		inner_left_arm = new ModelRenderer(this, "inner_left_arm");
		inner_left_arm.setPos(0.0F, 0.0F, 0.0F);
		left_arm_inner.addChild(inner_left_arm);


		left_arm_outer_r1 = new ModelRenderer(this, "left_arm_outer");
		left_arm_outer_r1.setPos(4.75F, 7.0F, -12.75F);
		inner_left_arm.addChild(left_arm_outer_r1);
		setRotationAngle(left_arm_outer_r1, 1.3553F, 0.7305F, 1.8062F);
		left_arm_outer_r1.texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 9.0F, 2.0F, 2.0F, 0.0F, false);

		left_arm_center_r1 = new ModelRenderer(this, "left_arm_center");
		left_arm_center_r1.setPos(5.0F, 2.75F, -5.0F);
		inner_left_arm.addChild(left_arm_center_r1);
		setRotationAngle(left_arm_center_r1, 1.2433F, 1.1185F, 1.698F);
		left_arm_center_r1.texOffs(0, 0).addBox(-0.1168F, -1.4029F, -0.6632F, 10.0F, 2.0F, 2.0F, 0.0F, false);

		left_hand = new ModelRenderer(this, "left_hand");
		left_hand.setPos(4.0F, 11.5F, -18.5F);
		inner_left_arm.addChild(left_hand);


		left_hand_inner = new ModelRenderer(this, "left_hand_inner");
		left_hand_inner.setPos(-0.234F, 0.6633F, -0.1389F);
		left_hand.addChild(left_hand_inner);


		claw_left_4_r1 = new ModelRenderer(this, "claw_left_4");
		claw_left_4_r1.setPos(-3.766F, 3.3367F, -1.6111F);
		left_hand_inner.addChild(claw_left_4_r1);
		setRotationAngle(claw_left_4_r1, -0.3229F, 0.3602F, 0.5681F);
		claw_left_4_r1.texOffs(1, 24).addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		claw_left_3_r1 = new ModelRenderer(this, "claw_left_3");
		claw_left_3_r1.setPos(-1.516F, 4.3367F, -3.3611F);
		left_hand_inner.addChild(claw_left_3_r1);
		setRotationAngle(claw_left_3_r1, -0.5428F, -0.061F, 0.1426F);
		claw_left_3_r1.texOffs(1, 24).addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		claw_left_2_r2 = new ModelRenderer(this, "claw_left_2");
		claw_left_2_r2.setPos(0.484F, 2.8367F, -3.8611F);
		left_hand_inner.addChild(claw_left_2_r2);
		setRotationAngle(claw_left_2_r2, -0.6952F, -0.2029F, -0.0247F);
		claw_left_2_r2.texOffs(1, 24).addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		claw_left_1_r2 = new ModelRenderer(this, "claw_left_1");
		claw_left_1_r2.setPos(3.484F, 3.3367F, -1.8611F);
		left_hand_inner.addChild(claw_left_1_r2);
		setRotationAngle(claw_left_1_r2, -0.2441F, -0.4126F, -0.629F);
		claw_left_1_r2.texOffs(1, 24).addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		hand_left_r1 = new ModelRenderer(this, "hand_left");
		hand_left_r1.setPos(-0.016F, 1.3367F, 0.1389F);
		left_hand_inner.addChild(hand_left_r1);
		setRotationAngle(hand_left_r1, 0.8727F, 0.0F, 0.0F);
		hand_left_r1.texOffs(0, 24).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);

		right_arm = new ModelRenderer(this, "right_arm");
		right_arm.setPos(-6.5F, -29.0F, -3.0F);
		center.addChild(right_arm);


		right_arm_inner = new ModelRenderer(this, "right_arm_inner");
		right_arm_inner.setPos(-2.5F, 1.5F, 0.0F);
		right_arm.addChild(right_arm_inner);


		right_arm_inner_r1 = new ModelRenderer(this, "right_arm_inner_1");
		right_arm_inner_r1.setPos(1.5F, 0.0F, 0.0F);
		right_arm_inner.addChild(right_arm_inner_r1);
		setRotationAngle(right_arm_inner_r1, 0.1504F, -0.5944F, -0.5261F);
		right_arm_inner_r1.texOffs(0, 0).addBox(-9.0F, -2.0F, -1.0F, 10.0F, 2.0F, 2.0F, 0.0F, true);

		inner_right_arm = new ModelRenderer(this, "inner_right_arm");
		inner_right_arm.setPos(0.0F, 0.0F, 0.0F);
		right_arm_inner.addChild(inner_right_arm);


		right_arm_outer_r1 = new ModelRenderer(this, "right_arm_outer");
		right_arm_outer_r1.setPos(-4.75F, 7.0F, -12.75F);
		inner_right_arm.addChild(right_arm_outer_r1);
		setRotationAngle(right_arm_outer_r1, 1.3553F, -0.7305F, -1.8062F);
		right_arm_outer_r1.texOffs(0, 0).addBox(-8.0F, -2.0F, -1.0F, 9.0F, 2.0F, 2.0F, 0.0F, true);

		right_arm_center_r1 = new ModelRenderer(this, "right_arm_center");
		right_arm_center_r1.setPos(-5.0F, 2.75F, -5.0F);
		inner_right_arm.addChild(right_arm_center_r1);
		setRotationAngle(right_arm_center_r1, 1.2433F, -1.1185F, -1.698F);
		right_arm_center_r1.texOffs(0, 0).addBox(-9.8832F, -1.4029F, -0.6632F, 10.0F, 2.0F, 2.0F, 0.0F, true);

		right_hand = new ModelRenderer(this, "right_hand");
		right_hand.setPos(-4.0F, 11.5F, -18.5F);
		inner_right_arm.addChild(right_hand);


		right_hand_inner = new ModelRenderer(this, "right_hand_inner");
		right_hand_inner.setPos(0.234F, 0.6633F, -0.1389F);
		right_hand.addChild(right_hand_inner);


		claw_right_4_r1 = new ModelRenderer(this, "claw_right_4");
		claw_right_4_r1.setPos(3.766F, 3.3367F, -1.6111F);
		right_hand_inner.addChild(claw_right_4_r1);
		setRotationAngle(claw_right_4_r1, -0.3229F, -0.3602F, -0.5681F);
		claw_right_4_r1.texOffs(1, 24).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, true);

		claw_right_3_r1 = new ModelRenderer(this, "claw_right_3");
		claw_right_3_r1.setPos(1.516F, 4.3367F, -3.3611F);
		right_hand_inner.addChild(claw_right_3_r1);
		setRotationAngle(claw_right_3_r1, -0.5428F, 0.061F, -0.1426F);
		claw_right_3_r1.texOffs(1, 24).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, true);

		claw_right_2_r2 = new ModelRenderer(this, "claw_right_2");
		claw_right_2_r2.setPos(-0.484F, 2.8367F, -3.8611F);
		right_hand_inner.addChild(claw_right_2_r2);
		setRotationAngle(claw_right_2_r2, -0.6952F, 0.2029F, 0.0247F);
		claw_right_2_r2.texOffs(1, 24).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, true);

		claw_right_1_r2 = new ModelRenderer(this, "claw_right_1");
		claw_right_1_r2.setPos(-3.484F, 3.3367F, -1.8611F);
		right_hand_inner.addChild(claw_right_1_r2);
		setRotationAngle(claw_right_1_r2, -0.2441F, 0.4126F, 0.629F);
		claw_right_1_r2.texOffs(1, 24).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, true);

		hand_right_r1 = new ModelRenderer(this, "hand_right");
		hand_right_r1.setPos(0.016F, 1.3367F, 0.1389F);
		right_hand_inner.addChild(hand_right_r1);
		setRotationAngle(hand_right_r1, 0.8727F, 0.0F, 0.0F);
		hand_right_r1.texOffs(0, 24).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F, true);

		head = new ModelRenderer(this, "head");
		head.setPos(0.0F, -33.128F, -5.815F);
		center.addChild(head);
		setRotationAngle(head, 0.1745F, 0.0F, 0.0F);


		jaw = new ModelRenderer(this, "jaw");
		jaw.setPos(0.03F, -0.2608F, -3.9671F);
		head.addChild(jaw);


		jaw_nose_r1 = new ModelRenderer(this, "jaw_nose");
		jaw_nose_r1.setPos(1.47F, -0.338F, 4.5884F);
		jaw.addChild(jaw_nose_r1);
		setRotationAngle(jaw_nose_r1, 1.1781F, 0.0F, 0.0F);
		jaw_nose_r1.texOffs(62, 46).addBox(-5.0F, -8.6194F, -2.9134F, 7.0F, 3.0F, 2.0F, 0.0F, false);

		jaw_lower_tip_r1 = new ModelRenderer(this, "jaw_lower_tip");
		jaw_lower_tip_r1.setPos(2.71F, 4.912F, -3.1616F);
		jaw.addChild(jaw_lower_tip_r1);
		setRotationAngle(jaw_lower_tip_r1, 0.6981F, 0.0F, 0.0F);
		jaw_lower_tip_r1.texOffs(19, 31).addBox(-5.68F, 0.0F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);

		jaw_inner_main_r1 = new ModelRenderer(this, "jaw_inner_main");
		jaw_inner_main_r1.setPos(1.71F, 4.412F, -4.1616F);
		jaw.addChild(jaw_inner_main_r1);
		setRotationAngle(jaw_inner_main_r1, 0.6981F, 0.0F, 0.0F);
		jaw_inner_main_r1.texOffs(0, 71).addBox(-5.68F, -6.0F, -1.0F, 8.0F, 8.0F, 2.0F, 0.0F, false);

		jaw_main_upper_r1 = new ModelRenderer(this, "jaw_main_upper");
		jaw_main_upper_r1.setPos(1.47F, 3.162F, -2.6616F);
		jaw.addChild(jaw_main_upper_r1);
		setRotationAngle(jaw_main_upper_r1, -0.48F, 0.0F, 0.0F);
		jaw_main_upper_r1.texOffs(42, 0).addBox(-5.0F, -4.0F, -1.0F, 7.0F, 6.0F, 4.0F, 0.0F, false);

		jaw2 = new ModelRenderer(this, "jaw2");
		jaw2.setPos(1.47F, 3.162F, -12.6616F);
		jaw.addChild(jaw2);


		jaw_main_r1 = new ModelRenderer(this, "jaw_main");
		jaw_main_r1.setPos(0.74F, 1.25F, 7.5F);
		jaw2.addChild(jaw_main_r1);
		setRotationAngle(jaw_main_r1, 0.6981F, 0.0F, 0.0F);
		jaw_main_r1.texOffs(1, 62).addBox(-5.68F, -5.0F, -1.0F, 7.0F, 7.0F, 2.0F, 0.0F, false);

		main = new ModelRenderer(this, "main");
		main.setPos(0.0F, -0.4704F, 0.0193F);
		head.addChild(main);


		head_upper_r1 = new ModelRenderer(this, "head_upper");
		head_upper_r1.setPos(0.5F, -1.6284F, -4.898F);
		main.addChild(head_upper_r1);
		setRotationAngle(head_upper_r1, -0.2618F, 0.0F, 0.0F);
		head_upper_r1.texOffs(65, 0).addBox(-5.0F, -9.0F, -1.0F, 9.0F, 9.0F, 6.0F, 0.0F, false);

		head_jaw_lower_r1 = new ModelRenderer(this, "head_jaw_lower");
		head_jaw_lower_r1.setPos(0.0F, 1.8716F, -5.648F);
		main.addChild(head_jaw_lower_r1);
		setRotationAngle(head_jaw_lower_r1, -0.2618F, 0.0F, 0.0F);
		head_jaw_lower_r1.texOffs(96, 0).addBox(-5.0F, -4.0F, -1.0F, 10.0F, 4.0F, 6.0F, 0.0F, false);

		antlers = new ModelRenderer(this, "antlers");
		antlers.setPos(0.5F, -1.6284F, -4.898F);
		main.addChild(antlers);


		antler_left = new ModelRenderer(this, "antler_left");
		antler_left.setPos(0.0F, 0.0F, 0.0F);
		antlers.addChild(antler_left);


		antler_left_tip5_r1 = new ModelRenderer(this, "antler_left_tip_5");
		antler_left_tip5_r1.setPos(7.25F, -13.0F, 8.5F);
		antler_left.addChild(antler_left_tip5_r1);
		setRotationAngle(antler_left_tip5_r1, -0.4175F, 0.1833F, 0.4325F);
		antler_left_tip5_r1.texOffs(1, 45).addBox(-0.3F, -5.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		antler_left_tip4_r1 = new ModelRenderer(this, "antler_left_tip_4");
		antler_left_tip4_r1.setPos(2.75F, -11.0F, 9.0F);
		antler_left.addChild(antler_left_tip4_r1);
		setRotationAngle(antler_left_tip4_r1, -1.303F, -0.8221F, 0.5322F);
		antler_left_tip4_r1.texOffs(1, 45).addBox(-0.3F, -5.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		antler_left_tip3_r1 = new ModelRenderer(this, "antler_left_tip_3");
		antler_left_tip3_r1.setPos(2.75F, -11.0F, 9.0F);
		antler_left.addChild(antler_left_tip3_r1);
		setRotationAngle(antler_left_tip3_r1, -0.8216F, -0.0968F, 0.7128F);
		antler_left_tip3_r1.texOffs(1, 45).addBox(-0.3F, -5.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		antler_left_tip2_r1 = new ModelRenderer(this, "antler_left_tip_2");
		antler_left_tip2_r1.setPos(3.75F, -11.0F, 9.0F);
		antler_left.addChild(antler_left_tip2_r1);
		setRotationAngle(antler_left_tip2_r1, -0.3953F, -0.195F, 0.5077F);
		antler_left_tip2_r1.texOffs(1, 45).addBox(-0.3F, -7.0F, -1.0F, 1.0F, 7.0F, 1.0F, 0.0F, false);

		antler_left_tip1_r1 = new ModelRenderer(this, "antler_left_tip_1");
		antler_left_tip1_r1.setPos(3.25F, -11.5F, 8.0F);
		antler_left.addChild(antler_left_tip1_r1);
		setRotationAngle(antler_left_tip1_r1, -0.2246F, 0.3977F, 1.0805F);
		antler_left_tip1_r1.texOffs(1, 45).addBox(-0.3F, -5.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		antler_left_main_r1 = new ModelRenderer(this, "antler_left_main");
		antler_left_main_r1.setPos(2.0F, -6.0F, 4.0F);
		antler_left.addChild(antler_left_main_r1);
		setRotationAngle(antler_left_main_r1, -0.6048F, -0.088F, 0.3363F);
		antler_left_main_r1.texOffs(11, 45).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

		antler_right = new ModelRenderer(this, "antler_right");
		antler_right.setPos(-1.0F, 0.0F, 0.0F);
		antlers.addChild(antler_right);


		antler_right_tip5_r1 = new ModelRenderer(this, "antler_right_tip_5");
		antler_right_tip5_r1.setPos(-7.25F, -13.0F, 8.5F);
		antler_right.addChild(antler_right_tip5_r1);
		setRotationAngle(antler_right_tip5_r1, -0.4175F, -0.1833F, -0.4325F);
		antler_right_tip5_r1.texOffs(1, 45).addBox(-0.7F, -5.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, true);

		antler_right_tip4_r1 = new ModelRenderer(this, "antler_right_tip_4");
		antler_right_tip4_r1.setPos(-2.75F, -11.0F, 9.0F);
		antler_right.addChild(antler_right_tip4_r1);
		setRotationAngle(antler_right_tip4_r1, -1.303F, 0.8221F, -0.5322F);
		antler_right_tip4_r1.texOffs(1, 45).addBox(-0.7F, -5.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, true);

		antler_right_tip3_r1 = new ModelRenderer(this, "antler_right_tip_3");
		antler_right_tip3_r1.setPos(-2.75F, -11.0F, 9.0F);
		antler_right.addChild(antler_right_tip3_r1);
		setRotationAngle(antler_right_tip3_r1, -0.8216F, 0.0968F, -0.7128F);
		antler_right_tip3_r1.texOffs(1, 45).addBox(-0.7F, -5.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, true);

		antler_right_tip2_r1 = new ModelRenderer(this, "antler_right_tip_2");
		antler_right_tip2_r1.setPos(-3.75F, -11.0F, 9.0F);
		antler_right.addChild(antler_right_tip2_r1);
		setRotationAngle(antler_right_tip2_r1, -0.3953F, 0.195F, -0.5077F);
		antler_right_tip2_r1.texOffs(1, 45).addBox(-0.7F, -7.0F, -1.0F, 1.0F, 7.0F, 1.0F, 0.0F, true);

		antler_right_tip1_r1 = new ModelRenderer(this, "antler_right_tip_1");
		antler_right_tip1_r1.setPos(-3.25F, -11.5F, 8.0F);
		antler_right.addChild(antler_right_tip1_r1);
		setRotationAngle(antler_right_tip1_r1, -0.2246F, -0.3977F, -1.0805F);
		antler_right_tip1_r1.texOffs(1, 45).addBox(-0.7F, -5.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, true);

		antler_right_main_r1 = new ModelRenderer(this, "antler_right_main");
		antler_right_main_r1.setPos(-2.0F, -6.0F, 4.0F);
		antler_right.addChild(antler_right_main_r1);
		setRotationAngle(antler_right_main_r1, -0.6048F, 0.088F, -0.3363F);
		antler_right_main_r1.texOffs(11, 45).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, true);

		eye = new ModelRenderer(this, "eye");
		eye.setPos(0.0F, 38.4996F, 3.667F);
		main.addChild(eye);
		eye.texOffs(0, 92).addBox(-3.0F, -47.25F, -9.5F, 6.0F, 6.0F, 2.0F, 0.0F, false);

		left_ear = new ModelRenderer(this, "left_ear");
		left_ear.setPos(4.0687F, -8.2315F, 0.7017F);
		main.addChild(left_ear);


		left_ear_r1 = new ModelRenderer(this, "left_ear_1");
		left_ear_r1.setPos(0.4313F, 1.4815F, 0.2983F);
		left_ear.addChild(left_ear_r1);
		setRotationAngle(left_ear_r1, -0.0812F, -0.1546F, 0.9226F);
		left_ear_r1.texOffs(39, 29).addBox(-2.0F, -1.0F, -2.0F, 6.0F, 1.0F, 3.0F, 0.0F, false);

		right_ear = new ModelRenderer(this, "right_ear");
		right_ear.setPos(-4.0687F, -8.2315F, 0.7017F);
		main.addChild(right_ear);


		right_ear_r1 = new ModelRenderer(this, "right_ear_1");
		right_ear_r1.setPos(-0.4313F, 1.4815F, 0.2983F);
		right_ear.addChild(right_ear_r1);
		setRotationAngle(right_ear_r1, -0.0812F, 0.1546F, -0.9226F);
		right_ear_r1.texOffs(39, 29).addBox(-4.0F, -1.0F, -2.0F, 6.0F, 1.0F, 3.0F, 0.0F, true);
	}

	@Override
	public void setupAnim(Mob entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.animateWalk(DeerclopsAnimation.WALKING, limbSwing, limbSwingAmount, 3.5f, 2.0f);


	}

	@Override
	public ImmutableSet<ModelRenderer> getAllParts() {
		return ImmutableSet.of(
				right_leg, left_leg, body, bottom_half, center,
				left_arm, right_arm, head, antlers, // Add other parts as needed
				claw_left, claw_right, knee_right, knee_left
		);
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