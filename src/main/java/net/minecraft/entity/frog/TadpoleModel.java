package net.minecraft.entity.frog;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.model.AgeableListModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.math.MathHelper;

public class TadpoleModel<T extends TadpoleEntity> extends AgeableListModel<T> {

    private final ModelPart root;
    private final ModelPart tail;

    public TadpoleModel(ModelPart modelPart) {
        super(true, 8.0f, 3.35f);
        this.root = modelPart;
        this.tail = modelPart.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        float f = 0.0f;
        float f2 = 22.0f;
        float f3 = -3.0f;
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, -1.0f, 0.0f, 3.0f, 2.0f, 3.0f), PartPose.offset(0.0f, 22.0f, -3.0f));
        partDefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, -1.0f, 0.0f, 0.0f, 2.0f, 7.0f), PartPose.offset(0.0f, 22.0f, 0.0f));
        return LayerDefinition.create(meshDefinition, 16, 16);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.root);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.tail);
    }

    @Override
    public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        float f6 = p_225597_1_.isInWater() ? 1.0F : 1.5f;
        this.tail.yRot = -f6 * 0.25f * MathHelper.sin(0.3f * p_225597_4_);
    }
}
