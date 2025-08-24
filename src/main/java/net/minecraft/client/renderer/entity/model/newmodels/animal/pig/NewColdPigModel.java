package net.minecraft.client.renderer.entity.model.newmodels.animal.pig;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class NewColdPigModel extends NewPigModel {
    public NewColdPigModel(ModelPart modelPart) {
        super(modelPart);
    }


    public static LayerDefinition createBodyLayer(CubeDeformation cubeDeformation) {
        MeshDefinition meshDefinition = NewColdPigModel.createBasePigModel(cubeDeformation);
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f).texOffs(28, 32).addBox(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f, new CubeDeformation(0.5f)), PartPose.offsetAndRotation(0.0f, 11.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
