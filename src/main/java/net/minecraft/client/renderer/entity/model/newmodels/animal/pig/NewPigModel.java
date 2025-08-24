package net.minecraft.client.renderer.entity.model.newmodels.animal.pig;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.model.newmodels.BabyModelTransform;
import net.minecraft.client.renderer.entity.model.newmodels.animal.NewQuadrupedModel;
import net.minecraft.entity.passive.PigEntity;

import java.util.Set;

public class NewPigModel extends NewQuadrupedModel<PigEntity> {
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(false, 4.0f, 4.0f, Set.of("head"));

    public NewPigModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation cubeDeformation) {
        return LayerDefinition.create(NewPigModel.createBasePigModel(cubeDeformation), 64, 64);
    }

    protected static MeshDefinition createBasePigModel(CubeDeformation cubeDeformation) {
        MeshDefinition meshDefinition = NewQuadrupedModel.createBodyMesh(6, true, false, cubeDeformation);
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, cubeDeformation).texOffs(16, 16).addBox(-2.0f, 0.0f, -9.0f, 4.0f, 3.0f, 1.0f, cubeDeformation), PartPose.offset(0.0f, 12.0f, -6.0f));
        return meshDefinition;
    }
}
