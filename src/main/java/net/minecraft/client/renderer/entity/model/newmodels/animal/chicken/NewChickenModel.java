package net.minecraft.client.renderer.entity.model.newmodels.animal.chicken;

import net.minecraft.client.animation.AnimationState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.ChickenAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.NewHierarchicalModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.model.newmodels.BabyModelTransform;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.math.MathHelper;

import java.util.Set;

public class NewChickenModel extends NewHierarchicalModel<ChickenEntity> {
    public static final String RED_THING = "red_thing";
    public final ModelPart root;
    public static final float Y_OFFSET = 16.0f;
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(false, 5.0f, 2.0f, 2.0f, 1.99f, 24.0f, Set.of("head", "beak", "red_thing"));
    private final ModelPart head;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart body;


    public NewChickenModel(ModelPart modelPart) {
        super();
        this.root = modelPart;
        this.head = modelPart.getChild("head");
        this.rightLeg = modelPart.getChild("right_leg");
        this.leftLeg = modelPart.getChild("left_leg");
        this.rightWing = modelPart.getChild("right_wing");
        this.leftWing = modelPart.getChild("left_wing");
        this.body = modelPart.getChild("body");

    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = NewChickenModel.createBaseChickenModel();
        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    protected static MeshDefinition createBaseChickenModel() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition partDefinition2 = partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f), PartPose.offset(0.0f, 15.0f, -4.0f));
        partDefinition2.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(14, 0).addBox(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f), PartPose.ZERO);
        partDefinition2.addOrReplaceChild(RED_THING, CubeListBuilder.create().texOffs(14, 4).addBox(-1.0f, -2.0f, -3.0f, 2.0f, 2.0f, 2.0f), PartPose.ZERO);
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 9).addBox(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f), PartPose.offsetAndRotation(0.0f, 16.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        CubeListBuilder cubeListBuilder = CubeListBuilder.create().texOffs(26, 0).addBox(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        partDefinition.addOrReplaceChild("right_leg", cubeListBuilder, PartPose.offset(-2.0f, 19.0f, 1.0f));
        partDefinition.addOrReplaceChild("left_leg", cubeListBuilder, PartPose.offset(1.0f, 19.0f, 1.0f));
        partDefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(24, 13).addBox(0.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), PartPose.offset(-4.0f, 13.0f, 0.0f));
        partDefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(24, 13).addBox(-1.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), PartPose.offset(4.0f, 13.0f, 0.0f));
        return meshDefinition;
    }

    @Override
    public void setupAnim(ChickenEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        float flap = (MathHelper.sin(entity.flap) + 1.0F) * entity.flapSpeed;
        this.head.xRot = headPitch * ((float)Math.PI / 180);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180);

        this.rightLeg.xRot = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
        this.leftLeg.xRot = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 1.4f * limbSwingAmount;
        this.rightWing.zRot = ageInTicks;
        this.leftWing.zRot = -ageInTicks;
    }

    public ModelPart root() {
        return root;
    }
}
