package net.minecraft.client.renderer.entity.model;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.WildfireEntity;
import net.minecraft.util.math.MathHelper;

public class HoveringInfernoModel<T extends WildfireEntity> extends HierarchicalModel<T> {
	private final ModelRenderer root;
	private final ModelRenderer shield1;
	private final ModelRenderer shield2;
	private final ModelRenderer shield3;
	private final ModelRenderer shield4;
	private final ModelRenderer head;
	private final ModelRenderer body;

	public HoveringInfernoModel() {
		texWidth = 128;
		texHeight = 128;

		root = new ModelRenderer(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);


		shield1 = new ModelRenderer(this, "shield1");
		shield1.setPos(0.0F, -10.5F, -10.5F);
		root.addChild(shield1);
		setRotationAngle(shield1, -0.2618F, 0.0F, 0.0F);
		shield1.texOffs(0, 82).addBox(-5.0F, -10.5F, -1.0F, 10.0F, 21.0F, 2.0F, 0.0F, false);

		shield2 = new ModelRenderer(this, "shield2");
		shield2.setPos(0.0F, -10.5F, 10.5F);
		root.addChild(shield2);
		setRotationAngle(shield2, 0.2618F, 0.0F, 0.0F);
		shield2.texOffs(0, 82).addBox(-5.0F, -10.5F, -1.0F, 10.0F, 21.0F, 2.0F, 0.0F, false);

		shield3 = new ModelRenderer(this, "shield3");
		shield3.setPos(-10.5F, -10.5F, 0.0F);
		root.addChild(shield3);
		setRotationAngle(shield3, 0.0F, -1.5708F, 0.2618F);
		shield3.texOffs(0, 82).addBox(-5.0F, -10.5F, -1.0F, 10.0F, 21.0F, 2.0F, 0.0F, false);

		shield4 = new ModelRenderer(this, "shield4");
		shield4.setPos(10.5F, -10.5F, 0.0F);
		root.addChild(shield4);
		setRotationAngle(shield4, 0.0F, 1.5708F, -0.2618F);
		shield4.texOffs(0, 82).addBox(-5.0F, -10.5F, -1.0F, 10.0F, 21.0F, 2.0F, 0.0F, true);

		head = new ModelRenderer(this, "head");
		head.setPos(0.0F, -25.75F, 0.0F);
		root.addChild(head);
		head.texOffs(0, 0).addBox(-4.0F, -3.75F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
		head.texOffs(32, 0).addBox(-4.0F, -4.75F, -4.0F, 8.0F, 9.0F, 8.0F, 0.3F, false);

		body = new ModelRenderer(this, "body");
		body.setPos(0.0F, 7.75F, 0.0F);
		head.addChild(body);
		body.texOffs(92, 16).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 20.0F, 4.0F, 0.0F, false);
	}

	@Override
	public ImmutableSet<ModelRenderer> getAllParts() {
		return ImmutableSet.of(this.shield1,this.shield2,this.shield3,this.shield4,this.head,this.body);
	}

	@Override
	public ModelRenderer root() {
		return this.root;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.animateWalk(BlazeAnimation.WILDFIRE_WALKING, limbSwing, limbSwingAmount, 1.5f, 1.5f);

		float rotationSpeed = ageInTicks * 0.11F; // Rotation speed of the shields
		float radius = 10.5F; // Circle radius

		// Universal inward tilt for all shields
		float inwardTilt = (float) (-15 * (Math.PI / 180f));

		// Shield 1 (North, facing South)
		float shield1Angle = rotationSpeed;
		float shield1X = radius * MathHelper.cos(shield1Angle);
		float shield1Z = radius * MathHelper.sin(shield1Angle);
		shield1.setPos(shield1X, -10.5F, shield1Z);
		shield1.yRot = (float) Math.atan2(-shield1X, -shield1Z); // Correct inward rotation
		shield1.xRot = inwardTilt; // Tilt inward
		shield1.zRot = 0.0F; // No Z-axis tilt

		// Shield 2 (South, facing North)
		float shield2Angle = rotationSpeed + (float) Math.PI; // 180-degree offset
		float shield2X = radius * MathHelper.cos(shield2Angle);
		float shield2Z = radius * MathHelper.sin(shield2Angle);
		shield2.setPos(shield2X, -10.5F, shield2Z);
		shield2.yRot = (float) Math.atan2(-shield2X, -shield2Z); // Correct inward rotation
		shield2.xRot = inwardTilt; // Tilt inward
		shield2.zRot = 0.0F; // No Z-axis tilt

		// Shield 3 (East, facing West)
		float shield3Angle = rotationSpeed + (float) (Math.PI / 2); // 90-degree offset
		float shield3X = radius * MathHelper.cos(shield3Angle);
		float shield3Z = radius * MathHelper.sin(shield3Angle);
		shield3.setPos(shield3X, -10.5F, shield3Z);
		shield3.yRot = (float) Math.atan2(-shield3X, -shield3Z); // Correct inward rotation
		shield3.xRot = inwardTilt; // Tilt inward
		shield3.zRot = 0.0F; // Reset Z-axis tilt

		// Shield 4 (West, facing East)
		float shield4Angle = rotationSpeed + (float) (3 * Math.PI / 2); // 270-degree offset
		float shield4X = radius * MathHelper.cos(shield4Angle);
		float shield4Z = radius * MathHelper.sin(shield4Angle);
		shield4.setPos(shield4X, -10.5F, shield4Z);
		shield4.yRot = (float) Math.atan2(-shield4X, -shield4Z); // Correct inward rotation
		shield4.xRot = inwardTilt; // Tilt inward
		shield4.zRot = 0.0F; // Reset Z-axis tilt

		this.animate(entity.shieldsSpin, BlazeAnimation.general, ageInTicks);
	}





	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}