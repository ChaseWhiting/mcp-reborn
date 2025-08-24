package net.minecraft.entity.monster.creaking;

import net.minecraft.client.animation.definitions.CreakingAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.NewHierarchicalModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreakingModel<T extends CreakingEntity> extends NewHierarchicalModel<CreakingEntity> {
    public static final List<ModelPart> NO_PARTS = List.of();
    private final ModelPart head;
    private final ModelPart root;
    private final List<ModelPart> headParts;

    public CreakingModel(ModelPart modelPart) {
        ModelPart modelPart2 = modelPart.getChild("root");
        root = modelPart2;
        ModelPart modelPart3 = modelPart2.getChild("upper_body");
        this.head = modelPart3.getChild("head");
        this.headParts = List.of(this.head);
    }


    private static MeshDefinition createMesh() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition partDefinition2 = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f));
        PartDefinition partDefinition3 = partDefinition2.addOrReplaceChild("upper_body", CubeListBuilder.create(), PartPose.offset(-1.0f, -19.0f, 0.0f));
        partDefinition3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0f, -10.0f, -3.0f, 6.0f, 10.0f, 6.0f).texOffs(28, 31).addBox(-3.0f, -13.0f, -3.0f, 6.0f, 3.0f, 6.0f).texOffs(12, 40).addBox(3.0f, -13.0f, 0.0f, 9.0f, 14.0f, 0.0f).texOffs(34, 12).addBox(-12.0f, -14.0f, 0.0f, 9.0f, 14.0f, 0.0f), PartPose.offset(-3.0f, -11.0f, 0.0f));
        partDefinition3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(0.0f, -3.0f, -3.0f, 6.0f, 13.0f, 5.0f).texOffs(24, 0).addBox(-6.0f, -4.0f, -3.0f, 6.0f, 7.0f, 5.0f), PartPose.offset(0.0f, -7.0f, 1.0f));
        partDefinition3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(22, 13).addBox(-2.0f, -1.5f, -1.5f, 3.0f, 21.0f, 3.0f).texOffs(46, 0).addBox(-2.0f, 19.5f, -1.5f, 3.0f, 4.0f, 3.0f), PartPose.offset(-7.0f, -9.5f, 1.5f));
        partDefinition3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(30, 40).addBox(0.0f, -1.0f, -1.5f, 3.0f, 16.0f, 3.0f).texOffs(52, 12).addBox(0.0f, -5.0f, -1.5f, 3.0f, 4.0f, 3.0f).texOffs(52, 19).addBox(0.0f, 15.0f, -1.5f, 3.0f, 4.0f, 3.0f), PartPose.offset(6.0f, -9.0f, 0.5f));
        partDefinition2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(42, 40).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 16.0f, 3.0f).texOffs(45, 55).addBox(-1.5f, 15.7f, -4.5f, 5.0f, 0.0f, 9.0f), PartPose.offset(1.5f, -16.0f, 0.5f));
        partDefinition2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 34).addBox(-3.0f, -1.5f, -1.5f, 3.0f, 19.0f, 3.0f).texOffs(45, 46).addBox(-5.0f, 17.2f, -4.5f, 5.0f, 0.0f, 9.0f).texOffs(12, 34).addBox(-3.0f, -4.5f, -1.5f, 3.0f, 3.0f, 3.0f), PartPose.offset(-1.0f, -17.5f, 0.5f));
        return meshDefinition;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = CreakingModel.createMesh();
        return LayerDefinition.create(meshDefinition, 64, 64);
    }



    @Override
    public void setupAnim(@NotNull CreakingEntity creaking, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);


        this.head.xRot = creaking.xRot * ((float)Math.PI / 180);
        this.head.yRot = creaking.yRot * ((float)Math.PI / 180);
        float speedFactor = creaking.hasTarget() ? 0.55f : 0.85f;

        float adjustedBaseSpeed = 5.5f * speedFactor;

        if (creaking.canMove() && limbSwingAmount > 0.1f) {
            this.animateWalk(CreakingAnimation.CREAKING_WALK, limbSwing, limbSwingAmount, adjustedBaseSpeed, 3.0f);
        }

        this.animate(creaking.attackAnimationState, CreakingAnimation.CREAKING_ATTACK, ageInTicks);
        this.animate(creaking.invulnerabilityAnimationState, CreakingAnimation.CREAKING_INVULNERABLE, ageInTicks);
        this.animate(creaking.deathAnimationState, CreakingAnimation.CREAKING_DEATH, ageInTicks);

        headTurn(this.head, netHeadYaw, headPitch);

    }

    @Override
    public ModelPart root() {
        return this.root;
    }


    public List<ModelPart> getHeadModelParts(CreakingEntity entity) {
        if (!entity.eyesGlowing) {
            return NO_PARTS;
        }
        return this.headParts;
    }
}
