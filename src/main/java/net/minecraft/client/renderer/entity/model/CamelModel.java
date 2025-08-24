package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.animation.definitions.CamelAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.NewHierarchicalModel;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.entity.Entity;
import net.minecraft.entity.camel.CamelEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class CamelModel<T extends CamelEntity> extends NewHierarchicalModel<CamelEntity> {
    public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.45f);


    private static final float WALK_ANIMATION_SPEED_FACTOR = 400.0f;
    private static final float MIN_WALK_ANIMATION_SPEED = 0.3f;
    private static final float MAX_WALK_ANIMATION_SPEED = 2.0f;
    private static final String SADDLE = "saddle";
    private static final String BRIDLE = "bridle";
    private static final String REINS = "reins";
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart[] saddleParts;
    private final ModelPart[] ridingParts;


    private final ModelPart chests;
    private final ModelPart left_chests;
    private final ModelPart left_chest_1;
    private final ModelPart left_chest_2;
    private final ModelPart right_chests;
    private final ModelPart right_chest_1;
    private final ModelPart right_chest_2;

    public CamelModel(ModelPart modelPart) {
        this.root = modelPart;
        ModelPart modelPart2 = modelPart.getChild("body");
        this.head = modelPart2.getChild("head");
        this.saddleParts = new ModelPart[]{modelPart2.getChild(SADDLE), this.head.getChild(BRIDLE)};
        this.ridingParts = new ModelPart[]{this.head.getChild(REINS)};


        this.chests = modelPart2.getChild("chests");
        this.left_chests = this.chests.getChild("left_chests");
        this.left_chest_1 = this.left_chests.getChild("left_chest_1");
        this.left_chest_2 = this.left_chests.getChild("left_chest_2");
        this.right_chests = this.chests.getChild("right_chests");
        this.right_chest_1 = this.right_chests.getChild("right_chest_1");
        this.right_chest_2 = this.right_chests.getChild("right_chest_2");
    }

        public static LayerDefinition createBodyLayer(CubeDeformation cubeDeformation) {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root1 = meshDefinition.getRoot();
        PartDefinition body = root1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-7.5f, -12.0f, -23.5f, 15.0f, 12.0f, 27.0f), PartPose.offset(0.0f, 4.0f, 9.5f));
        body.addOrReplaceChild("hump", CubeListBuilder.create().texOffs(74, 0).addBox(-4.5f, -5.0f, -5.5f, 9.0f, 5.0f, 11.0f), PartPose.offset(0.0f, -12.0f, -10.0f));
        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(122, 0).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 0.0f), PartPose.offset(0.0f, -9.0f, 3.5f));
        PartDefinition head1 = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(60, 24).addBox(-3.5f, -7.0f, -15.0f, 7.0f, 8.0f, 19.0f).texOffs(21, 0).addBox(-3.5f, -21.0f, -15.0f, 7.0f, 14.0f, 7.0f).texOffs(50, 0).addBox(-2.5f, -21.0f, -21.0f, 5.0f, 5.0f, 6.0f), PartPose.offset(0.0f, -3.0f, -19.5f));
        head1.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(45, 0).addBox(-0.5f, 0.5f, -1.0f, 3.0f, 1.0f, 2.0f), PartPose.offset(3.0f, -21.0f, -9.5f));
        head1.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(67, 0).addBox(-2.5f, 0.5f, -1.0f, 3.0f, 1.0f, 2.0f), PartPose.offset(-3.0f, -21.0f, -9.5f));
        root1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(58, 16).addBox(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), PartPose.offset(4.9f, 1.0f, 9.5f));
        root1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(94, 16).addBox(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), PartPose.offset(-4.9f, 1.0f, 9.5f));
        root1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), PartPose.offset(4.9f, 1.0f, -10.5f));
        root1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 26).addBox(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), PartPose.offset(-4.9f, 1.0f, -10.5f));
        body.addOrReplaceChild(SADDLE, CubeListBuilder.create().texOffs(74, 64).addBox(-4.5f, -17.0f, -15.5f, 9.0f, 5.0f, 11.0f, cubeDeformation).texOffs(92, 114).addBox(-3.5f, -20.0f, -15.5f, 7.0f, 3.0f, 11.0f, cubeDeformation).texOffs(0, 89).addBox(-7.5f, -12.0f, -23.5f, 15.0f, 12.0f, 27.0f, cubeDeformation), PartPose.offset(0.0f, 0.0f, 0.0f));
        head1.addOrReplaceChild(REINS, CubeListBuilder.create().texOffs(98, 42).addBox(3.51f, -18.0f, -17.0f, 0.0f, 7.0f, 15.0f).texOffs(84, 57).addBox(-3.5f, -18.0f, -2.0f, 7.0f, 7.0f, 0.0f).texOffs(98, 42).addBox(-3.51f, -18.0f, -17.0f, 0.0f, 7.0f, 15.0f), PartPose.offset(0.0f, 0.0f, 0.0f));
        head1.addOrReplaceChild(BRIDLE, CubeListBuilder.create().texOffs(60, 87).addBox(-3.5f, -7.0f, -15.0f, 7.0f, 8.0f, 19.0f, cubeDeformation).texOffs(21, 64).addBox(-3.5f, -21.0f, -15.0f, 7.0f, 14.0f, 7.0f, cubeDeformation).texOffs(50, 64).addBox(-2.5f, -21.0f, -21.0f, 5.0f, 5.0f, 6.0f, cubeDeformation).texOffs(74, 70).addBox(2.5f, -19.0f, -18.0f, 1.0f, 2.0f, 2.0f).texOffs(74, 70).mirror().addBox(-3.5f, -19.0f, -18.0f, 1.0f, 2.0f, 2.0f), PartPose.offset(0.0f, 0.0f, 0.0f));


            PartDefinition chests = body.addOrReplaceChild("chests", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, -10.5F));
            PartDefinition left_chests = chests.addOrReplaceChild("left_chests", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
            PartDefinition left_chest_1 = left_chests.addOrReplaceChild("left_chest_1", CubeListBuilder.create().texOffs(0, 91).mirror().addBox(-4.0F, 0.0F, -1.5F, 8.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(8.5F, -31.0F, 7.0F, 0.0F, 1.5708F, 0.0F));
            PartDefinition left_chest_2 = left_chests.addOrReplaceChild("left_chest_2", CubeListBuilder.create().texOffs(0, 91).mirror().addBox(-4.0F, 0.0F, -1.5F, 8.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(8.5F, -31.0F, -6.0F, 0.0F, 1.5708F, 0.0F));
            PartDefinition right_chests = chests.addOrReplaceChild("right_chests", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
            PartDefinition right_chest_1 = right_chests.addOrReplaceChild("right_chest_1", CubeListBuilder.create().texOffs(0, 91).addBox(-4.0F, 0.0F, -1.5F, 8.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.5F, -31.0F, -6.0F, 0.0F, -1.5708F, 0.0F));
            PartDefinition right_chest_2 = right_chests.addOrReplaceChild("right_chest_2", CubeListBuilder.create().texOffs(0, 91).addBox(-4.0F, 0.0F, -1.5F, 8.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.5F, -31.0F, 7.0F, 0.0F, -1.5708F, 0.0F));


        return LayerDefinition.create(meshDefinition, 128, 128);
    }

    @Override
    public void setupAnim(CamelEntity t, float f, float f2, float f3, float f4, float f5) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(t, f4, f5, f3);
        this.toggleInvisibleParts(t);
        float f6 = (float)((Entity)t).getDeltaMovement().horizontalDistanceSqr();
        float f7 = MathHelper.clamp(f6 * 400.0f, 0.3f, 2.0f);
        this.animate(((CamelEntity)t).walkAnimationState, CamelAnimation.CAMEL_WALK, f3, f7);
        this.animate(((CamelEntity)t).sitAnimationState, CamelAnimation.CAMEL_SIT, f3, 1.0f);
        this.animate(((CamelEntity)t).sitPoseAnimationState, CamelAnimation.CAMEL_SIT_POSE, f3, 1.0f);
        this.animate(((CamelEntity)t).sitUpAnimationState, CamelAnimation.CAMEL_STANDUP, f3, 1.0f);
        this.animate(((CamelEntity)t).idleAnimationState, CamelAnimation.CAMEL_IDLE, f3, 1.0f);
        this.animate(((CamelEntity)t).dashAnimationState, CamelAnimation.CAMEL_DASH, f3, 1.0f);
    }

    private void applyHeadRotation(CamelEntity t, float f, float f2, float f3) {
        f = MathHelper.clamp(f, -30.0f, 30.0f);
        f2 = MathHelper.clamp(f2, -25.0f, 45.0f);
        if (((CamelEntity)t).getDashCooldown() > 0) {
            float f4 = f3 - (float)((CamelEntity)t).tickCount;
            float f5 = 45.0f * ((float)((CamelEntity)t).getDashCooldown() - f4) / 55.0f;
            f2 = MathHelper.clamp(f2 + f5, -25.0f, 70.0f);
        }
        this.head.yRot = f * ((float)Math.PI / 180);
        this.head.xRot = f2 * ((float)Math.PI / 180);
    }

    private void toggleInvisibleParts(CamelEntity t) {
        boolean bl = t.getCarpetColor().isPresent() || t.getLeashHolder() != null;
        boolean bl2 = t.getCarpetColor().isPresent() || t.getLeashHolder() != null;
        for (ModelPart modelPart : this.saddleParts) {
            modelPart.visible = bl;
        }
        for (ModelPart modelPart : this.ridingParts) {
            modelPart.visible = bl2 && bl;
        }

        this.chests.visible = t.hasChest();
    }

    @Override
    public void renderToBuffer(@NotNull MatrixStack matrixStack, @NotNull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.young) {
            matrixStack.pushPose();
            matrixStack.scale(0.45454544f, 0.41322312f, 0.45454544f);
            matrixStack.translate(0f, 2.0625f, 0f);
            super.renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            matrixStack.popPose();
        } else {
            super.renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
