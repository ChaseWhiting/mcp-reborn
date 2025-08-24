package net.minecraft.client.renderer.entity.model.goat;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.goat.GoatEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class GoatModel extends HierarchicalModel<GoatEntity> {
    private final ModelRenderer root;
    private final ModelRenderer leftFrontLeg;
    private final ModelRenderer rightFrontLeg;
    private final ModelRenderer leftHindLeg;
    private final ModelRenderer rightHindLeg;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer head_r1;
    private final ModelRenderer left_horn;
    private final ModelRenderer right_horn;
    private final ModelRenderer leftEar;
    private final ModelRenderer rightEar;
    private final ModelRenderer goatee;

    public GoatModel() {
        texWidth = 64;
        texHeight = 64;

        root = new ModelRenderer(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        leftFrontLeg = new ModelRenderer(this);
        leftFrontLeg.setPos(0.5F, -10.0F, -6.0F);
        root.addChild(leftFrontLeg);
        leftFrontLeg.texOffs(35, 2).addBox(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F, 0.0F, false);

        rightFrontLeg = new ModelRenderer(this);
        rightFrontLeg.setPos(-3.5F, -10.0F, -6.0F);
        root.addChild(rightFrontLeg);
        rightFrontLeg.texOffs(49, 2).addBox(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F, 0.0F, false);

        leftHindLeg = new ModelRenderer(this);
        leftHindLeg.setPos(0.5F, -10.0F, 4.0F);
        root.addChild(leftHindLeg);
        leftHindLeg.texOffs(36, 29).addBox(0.0F, 4.0F, 0.0F, 3.0F, 6.0F, 3.0F, 0.0F, false);

        rightHindLeg = new ModelRenderer(this);
        rightHindLeg.setPos(-3.5F, -10.0F, 4.0F);
        root.addChild(rightHindLeg);
        rightHindLeg.texOffs(49, 29).addBox(0.0F, 4.0F, 0.0F, 3.0F, 6.0F, 3.0F, 0.0F, false);

        body = new ModelRenderer(this);
        body.setPos(-0.5F, 0.0F, 0.0F);
        root.addChild(body);
        body.texOffs(1, 1).addBox(-4.0F, -17.0F, -7.0F, 9.0F, 11.0F, 16.0F, 0.0F, false);
        body.texOffs(0, 28).addBox(-5.0F, -18.0F, -8.0F, 11.0F, 14.0F, 11.0F, 0.0F, false);

        head = new ModelRenderer(this);
        head.setPos(1.0F, -18.0F, -8.0F);
        body.addChild(head);


        head_r1 = new ModelRenderer(this);
        head_r1.setPos(0.0F, 0.0F, 0.0F);
        head.addChild(head_r1);
        setRotationAngle(head_r1, 0.9599F, 0.0F, 0.0F);
        head_r1.texOffs(34, 46).addBox(-3.0F, -4.0F, -8.0F, 5.0F, 7.0F, 10.0F, 0.0F, false);

        left_horn = new ModelRenderer(this);
        left_horn.setPos(0.0F, 8.0F, 8.0F);
        head.addChild(left_horn);
        left_horn.texOffs(12, 55).addBox(-0.01F, -16.0F, -10.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

        right_horn = new ModelRenderer(this);
        right_horn.setPos(0.0F, 8.0F, 8.0F);
        head.addChild(right_horn);
        right_horn.texOffs(12, 55).addBox(-2.99F, -16.0F, -10.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

        leftEar = new ModelRenderer(this);
        leftEar.setPos(-0.5F, 18.0F, 8.0F);
        head.addChild(leftEar);
        leftEar.texOffs(2, 61).addBox(2.5F, -21.0F, -10.0F, 3.0F, 2.0F, 1.0F, 0.0F, true);

        rightEar = new ModelRenderer(this);
        rightEar.setPos(-0.5F, 18.0F, 8.0F);
        head.addChild(rightEar);
        rightEar.texOffs(2, 61).addBox(-5.5F, -21.0F, -10.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);

        goatee = new ModelRenderer(this);
        goatee.setPos(0.0F, 8.0F, 8.0F);
        head.addChild(goatee);
        goatee.texOffs(23, 52).addBox(-0.5F, -3.0F, -14.0F, 0.0F, 7.0F, 5.0F, 0.0F, false);
    }


    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }


    @Override
    public void setupAnim(GoatEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);

        // Hide horns for baby goats
        this.left_horn.visible = !((AgeableEntity) entity).isBaby();
        this.right_horn.visible = !((AgeableEntity) entity).isBaby();

        this.rightHindLeg.xRot = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
        this.leftHindLeg.xRot = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 1.4f * limbSwingAmount;
        this.rightFrontLeg.xRot = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 1.4f * limbSwingAmount;
        this.leftFrontLeg.xRot = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
        // Ramming head rotation
        float rammingHeadRotation = ((GoatEntity) entity).getRammingXHeadRot();
        if (rammingHeadRotation != 0.0F) {
            this.head.xRot = rammingHeadRotation;
        }
    }

    @Override
    public void renderToBuffer(@NotNull MatrixStack matrixStack, @NotNull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.young) {
            matrixStack.pushPose();
            float f5 = 1.5F / 2.0F;
            matrixStack.scale(f5);

            matrixStack.translate(0.0D, 2.5 / 16.0F, 2.0 / 16.0F);
            this.getHead().render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            matrixStack.popPose();
            matrixStack.pushPose();
            float f1 = 1.0f / 2.0f;
            matrixStack.scale(f1);
            matrixStack.translate(0.0D, 24 / 16.0F, 0.0D);
            this.bodyParts().forEach(modelRenderer -> {
                modelRenderer.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            });
            matrixStack.popPose();

        } else {
            super.renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }

    }

    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

    }

    @Override
    public ImmutableSet<ModelRenderer> getAllParts() {
        return ImmutableSet.of(this.root, this.head, this.goatee, this.body, this.left_horn, this.right_horn, this.leftHindLeg, this.rightHindLeg, this.leftFrontLeg, this.rightFrontLeg, this.leftEar, this.rightEar);
    }

    public ModelRenderer getHead() {
        return head;
    }

    public ImmutableSet<ModelRenderer> bodyParts() {
        return ImmutableSet.of(this.body, this.leftFrontLeg, this.leftHindLeg, this.rightFrontLeg, this.rightHindLeg);
    }

    @Override
    public ModelRenderer root() {
        return this.root;
    }

}