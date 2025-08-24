package net.minecraft.client.renderer.entity.newrenderers.ghast;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.NewHierarchicalModel;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.newmodels.NewEntityModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.GhastRenderState;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomSource;

public class NewGhastModel extends NewHierarchicalModel<GhastEntity> {
    private final ModelPart[] tentacles = new ModelPart[9];
    private final ModelPart root;

    public NewGhastModel(ModelPart modelPart) {
        super();
        this.root = modelPart;
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = modelPart.getChild(PartNames.tentacle(i));
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f), PartPose.offset(0.0f, 17.6f, 0.0f));
        RandomSource randomSource = RandomSource.create(1660L);
        for (int i = 0; i < 9; ++i) {
            float f = (((float)(i % 3) - (float)(i / 3 % 2) * 0.5f + 0.25f) / 2.0f * 2.0f - 1.0f) * 5.0f;
            float f2 = ((float)(i / 3) / 2.0f * 2.0f - 1.0f) * 5.0f;
            int n = randomSource.nextInt(7) + 8;
            partDefinition.addOrReplaceChild(PartNames.tentacle(i), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, n, 2.0f), PartPose.offset(f, 24.6f, f2));
        }
        return LayerDefinition.create(meshDefinition, 64, 32).apply(MeshTransformer.scaling(4.5f));
    }

    @Override
    public void setupAnim(GhastEntity t, float f, float f2, float ageInTicks, float f4, float f5) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        animateTentacles(ageInTicks, tentacles);
    }

    public static void animateTentacles(float ageInTicks, ModelPart[] modelPartArray) {
        for (int i = 0; i < modelPartArray.length; ++i) {
            modelPartArray[i].xRot = 0.2f * MathHelper.sin(ageInTicks * 0.3f + (float)i) + 0.4f;
        }
    }

    @Override
    public ModelPart root() {
        return root;
    }


}
