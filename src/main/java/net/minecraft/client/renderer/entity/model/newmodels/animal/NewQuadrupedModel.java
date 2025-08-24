package net.minecraft.client.renderer.entity.model.newmodels.animal;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.NewHierarchicalModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class NewQuadrupedModel <T extends Entity> extends NewHierarchicalModel<T> {
    protected final ModelPart root;
    protected final ModelPart head;
    protected final ModelPart body;
    protected final ModelPart rightHindLeg;
    protected final ModelPart leftHindLeg;
    protected final ModelPart rightFrontLeg;
    protected final ModelPart leftFrontLeg;

    protected NewQuadrupedModel(ModelPart modelPart) {
        super();
        this.root = modelPart;
        this.head = modelPart.getChild("head");
        this.body = modelPart.getChild("body");
        this.rightHindLeg = modelPart.getChild("right_hind_leg");
        this.leftHindLeg = modelPart.getChild("left_hind_leg");
        this.rightFrontLeg = modelPart.getChild("right_front_leg");
        this.leftFrontLeg = modelPart.getChild("left_front_leg");
    }

    public static MeshDefinition createBodyMesh(int n, boolean bl, boolean bl2, CubeDeformation cubeDeformation) {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, cubeDeformation), PartPose.offset(0.0f, 18 - n, -6.0f));
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f, cubeDeformation), PartPose.offsetAndRotation(0.0f, 17 - n, 2.0f, 1.5707964f, 0.0f, 0.0f));
        NewQuadrupedModel.createLegs(partDefinition, bl, bl2, n, cubeDeformation);
        return meshDefinition;
    }

    static void createLegs(PartDefinition partDefinition, boolean bl, boolean bl2, int n, CubeDeformation cubeDeformation) {
        CubeListBuilder cubeListBuilder = CubeListBuilder.create().mirror(bl2).texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, (float)n, 4.0f, cubeDeformation);
        CubeListBuilder cubeListBuilder2 = CubeListBuilder.create().mirror(bl).texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, (float)n, 4.0f, cubeDeformation);
        partDefinition.addOrReplaceChild("right_hind_leg", cubeListBuilder, PartPose.offset(-3.0f, 24 - n, 7.0f));
        partDefinition.addOrReplaceChild("left_hind_leg", cubeListBuilder2, PartPose.offset(3.0f, 24 - n, 7.0f));
        partDefinition.addOrReplaceChild("right_front_leg", cubeListBuilder, PartPose.offset(-3.0f, 24 - n, -5.0f));
        partDefinition.addOrReplaceChild("left_front_leg", cubeListBuilder2, PartPose.offset(3.0f, 24 - n, -5.0f));
    }

    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        headTurn(head, netHeadYaw, headPitch);

        float f = limbSwing;
        float f2 = limbSwingAmount;
        this.rightHindLeg.xRot = MathHelper.cos(f * 0.6662f) * 1.4f * f2;
        this.leftHindLeg.xRot = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * f2;
        this.rightFrontLeg.xRot = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * f2;
        this.leftFrontLeg.xRot = MathHelper.cos(f * 0.6662f) * 1.4f * f2;

    }
}
