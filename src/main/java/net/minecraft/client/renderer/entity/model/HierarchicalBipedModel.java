package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HierarchicalBipedModel<T extends LivingEntity> extends HierarchicalModel<T> implements IHasArm, IHasHead {
    public ModelRenderer root;
    public ModelRenderer head;
    public ModelRenderer hat;
    public ModelRenderer body;
    public ModelRenderer rightArm;
    public ModelRenderer leftArm;
    public ModelRenderer rightLeg;
    public ModelRenderer leftLeg;

    public BipedModel.ArmPose leftArmPose = BipedModel.ArmPose.EMPTY;
    public BipedModel.ArmPose rightArmPose = BipedModel.ArmPose.EMPTY;
    public boolean crouching;
    public float swimAmount;

    public HierarchicalBipedModel(float scale) {
        this(scale, 0.0F, 64, 32);
    }

    public HierarchicalBipedModel(float scale, float yOffset, int textureWidth, int textureHeight) {
        super();
        this.texWidth = textureWidth;
        this.texHeight = textureHeight;

        this.root = new ModelRenderer(this);

        // Create the head part
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale);
        this.head.setPos(0.0F, 0.0F + yOffset, 0.0F);
        this.root.addChild(this.head);

        // Create the hat part
        this.hat = new ModelRenderer(this, 32, 0);
        this.hat.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale + 0.5F);
        this.hat.setPos(0.0F, 0.0F + yOffset, 0.0F);
        this.head.addChild(this.hat);

        // Create the body part
        this.body = new ModelRenderer(this, 16, 16);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, scale);
        this.body.setPos(0.0F, 0.0F + yOffset, 0.0F);
        this.root.addChild(this.body);

        // Create the arms
        this.rightArm = new ModelRenderer(this, 40, 16);
        this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
        this.rightArm.setPos(-5.0F, 2.0F + yOffset, 0.0F);
        this.body.addChild(this.rightArm);

        this.leftArm = new ModelRenderer(this, 40, 16);
        this.leftArm.mirror = true;
        this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
        this.leftArm.setPos(5.0F, 2.0F + yOffset, 0.0F);
        this.body.addChild(this.leftArm);

        // Create the legs
        this.rightLeg = new ModelRenderer(this, 0, 16);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
        this.rightLeg.setPos(-1.9F, 12.0F + yOffset, 0.0F);
        this.root.addChild(this.rightLeg);

        this.leftLeg = new ModelRenderer(this, 0, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
        this.leftLeg.setPos(1.9F, 12.0F + yOffset, 0.0F);
        this.root.addChild(this.leftLeg);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        this.rightArm.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.leftArm.xRot = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.rightLeg.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
    }

    @Override
    public ImmutableSet<ModelRenderer> getAllParts() {
        return ImmutableSet.of(root, head, hat, body, rightArm, leftArm, rightLeg, leftLeg);
    }

    @Override
    public ModelRenderer root() {
        return root;
    }

    // Additional helper methods like the ones in BipedModel
    public void translateToHand(HandSide side, MatrixStack matrixStack) {
        this.getArm(side).translateAndRotate(matrixStack);
    }

    protected ModelRenderer getArm(HandSide side) {
        return side == HandSide.LEFT ? this.leftArm : this.rightArm;
    }

    public ModelRenderer getHead() {
        return this.head;
    }
}
