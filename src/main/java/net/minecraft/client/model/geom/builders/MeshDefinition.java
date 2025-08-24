package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.List;
import java.util.function.UnaryOperator;

public class MeshDefinition {
    private final PartDefinition root;

    public PartDefinition getRoot() {
        return this.root;
    }

    public MeshDefinition() {
        this(new PartDefinition(ImmutableList.of(), PartPose.ZERO));
    }

    private MeshDefinition(PartDefinition partDefinition) {
        this.root = partDefinition;
    }

    public MeshDefinition transformed(UnaryOperator<PartPose> unaryOperator) {
        return new MeshDefinition(this.root.transformed(unaryOperator));
    }

    public MeshDefinition apply(MeshTransformer meshTransformer) {
        return meshTransformer.apply(this);
    }

}

