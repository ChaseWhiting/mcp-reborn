package net.minecraft.entity.allay;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.NewHierarchicalModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class AllayModel extends NewHierarchicalModel<AllayEntity> implements IHasArm {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart right_wing;
    private final ModelPart left_wing;
    private static final float FLYING_ANIMATION_X_ROT = 0.7853982f;
    private static final float MAX_HAND_HOLDING_ITEM_X_ROT_RAD = -1.134464f;
    private static final float MIN_HAND_HOLDING_ITEM_X_ROT_RAD = -1.0471976f;

    public AllayModel(ModelPart modelPart) {
        super(RenderType::entityTranslucent);
        this.root = modelPart.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.right_arm = this.body.getChild("right_arm");
        this.left_arm = this.body.getChild("left_arm");
        this.right_wing = this.body.getChild("right_wing");
        this.left_wing = this.body.getChild("left_wing");
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition partDefinition2 = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0f, 23.5f, 0.0f));
        partDefinition2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, -5.0f, -2.5f, 5.0f, 5.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, -3.99f, 0.0f));
        PartDefinition partDefinition3 = partDefinition2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-1.5f, 0.0f, -1.0f, 3.0f, 4.0f, 2.0f, new CubeDeformation(0.0f)).texOffs(0, 16).addBox(-1.5f, 0.0f, -1.0f, 3.0f, 5.0f, 2.0f, new CubeDeformation(-0.2f)), PartPose.offset(0.0f, -4.0f, 0.0f));
        partDefinition3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(23, 0).addBox(-0.75f, -0.5f, -1.0f, 1.0f, 4.0f, 2.0f, new CubeDeformation(-0.01f)), PartPose.offset(-1.75f, 0.5f, 0.0f));
        partDefinition3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(23, 6).addBox(-0.25f, -0.5f, -1.0f, 1.0f, 4.0f, 2.0f, new CubeDeformation(-0.01f)), PartPose.offset(1.75f, 0.5f, 0.0f));
        partDefinition3.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0f, 1.0f, 0.0f, 0.0f, 5.0f, 8.0f, new CubeDeformation(0.0f)), PartPose.offset(-0.5f, 0.0f, 0.6f));
        partDefinition3.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0f, 1.0f, 0.0f, 0.0f, 5.0f, 8.0f, new CubeDeformation(0.0f)), PartPose.offset(0.5f, 0.0f, 0.6f));
        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public void setupAnim(AllayEntity allay, float f, float f2, float f3, float f4, float f5) {
        float f6;
        float f7;
        float f8;
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float f9 = f3 * 20.0f * ((float)Math.PI / 180) + f;
        float f10 = MathHelper.cos(f9) * (float)Math.PI * 0.15f + f2;
        float f11 = f3 - (float)allay.tickCount;
        float f12 = f3 * 9.0f * ((float)Math.PI / 180);
        float f13 = Math.min(f2 / 0.3f, 1.0f);
        float f14 = 1.0f - f13;
        float f15 = allay.getHoldingItemAnimationProgress(f11);
        if (allay.isDancing()) {
            f8 = f3 * 8.0f * ((float)Math.PI / 180) + f2;
            f7 = MathHelper.cos(f8) * 16.0f * ((float)Math.PI / 180);
            f6 = allay.getSpinningProgress(f11);
            float f16 = MathHelper.cos(f8) * 14.0f * ((float)Math.PI / 180);
            float f17 = MathHelper.cos(f8) * 30.0f * ((float)Math.PI / 180);
            this.root.yRot = allay.isSpinning() ? (float)Math.PI * 4 * f6 : this.root.yRot;
            this.root.zRot = f7 * (1.0f - f6);
            this.head.yRot = f17 * (1.0f - f6);
            this.head.zRot = f16 * (1.0f - f6);
        } else {
            this.head.xRot = f5 * ((float)Math.PI / 180);
            this.head.yRot = f4 * ((float)Math.PI / 180);
        }
        this.right_wing.xRot = 0.43633232f * (1.0f - f13);
        this.right_wing.yRot = -0.7853982f + f10;
        this.left_wing.xRot = 0.43633232f * (1.0f - f13);
        this.left_wing.yRot = 0.7853982f - f10;
        this.body.xRot = f13 * 0.7853982f;
        f8 = f15 * MathHelper.lerp(f13, -1.0471976f, -1.134464f);
        this.root.y += (float)Math.cos(f12) * 0.25f * f14;
        this.right_arm.xRot = f8;
        this.left_arm.xRot = f8;
        f7 = f14 * (1.0f - f15);
        f6 = 0.43633232f - MathHelper.cos(f12 + 4.712389f) * (float)Math.PI * 0.075f * f7;
        this.left_arm.zRot = -f6;
        this.right_arm.zRot = f6;
        this.right_arm.yRot = 0.27925268f * f15;
        this.left_arm.yRot = -0.27925268f * f15;
    }

    @Override
    public void translateToHand(HandSide humanoidArm, MatrixStack poseStack) {
        float f = 1.0f;
        float f2 = 3.0f;
        this.root.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        poseStack.translate(0.0f, 0.0625f, 0.1875f);
        poseStack.mulPose(Vector3f.XP.rotation(this.right_arm.xRot));
        poseStack.scale(0.7f, 0.7f, 0.7f);
        poseStack.translate(0.0625f, 0.0f, 0.0f);
    }
}
