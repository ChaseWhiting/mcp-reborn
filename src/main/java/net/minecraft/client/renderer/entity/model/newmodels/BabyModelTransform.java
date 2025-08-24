package net.minecraft.client.renderer.entity.model.newmodels;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public record BabyModelTransform(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, float babyHeadScale, float babyBodyScale, float bodyYOffset, Set<String> headParts) implements MeshTransformer
{
    public BabyModelTransform(Set<String> set) {
        this(false, 5.0f, 2.0f, set);
    }

    public BabyModelTransform(boolean bl, float f, float f2, Set<String> set) {
        this(bl, f, f2, 2.0f, 2.0f, 24.0f, set);
    }

    @Override
    public MeshDefinition apply(MeshDefinition meshDefinition) {
        float f = this.scaleHead ? 1.5f / this.babyHeadScale : 1.0f;
        float f2 = 1.0f / this.babyBodyScale;
        UnaryOperator<PartPose> unaryOperator = partPose -> partPose.translated(0.0f, this.babyYHeadOffset, this.babyZHeadOffset).scaled(f);
        UnaryOperator<PartPose> unaryOperator2 = partPose -> partPose.translated(0.0f, this.bodyYOffset, 0.0f).scaled(f2);
        MeshDefinition meshDefinition2 = new MeshDefinition();
        for (Map.Entry<String, PartDefinition> entry : meshDefinition.getRoot().getChildren()) {
            String string = entry.getKey();
            PartDefinition partDefinition = entry.getValue();
            meshDefinition2.getRoot().addOrReplaceChild(string, partDefinition.transformed(this.headParts.contains(string) ? unaryOperator : unaryOperator2));
        }
        return meshDefinition2;
    }
}

