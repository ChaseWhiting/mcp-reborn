package net.minecraft.client.model.geom.builders;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.MaterialDefinition;

public class LayerDefinition {
    private final MeshDefinition mesh;
    private final MaterialDefinition material;

    private LayerDefinition(MeshDefinition meshDefinition, MaterialDefinition materialDefinition) {
        this.mesh = meshDefinition;
        this.material = materialDefinition;
    }

    public LayerDefinition apply(MeshTransformer meshTransformer) {
        return new LayerDefinition(meshTransformer.apply(this.mesh), this.material);
    }

    public ModelPart bakeRoot() {
        return this.mesh.getRoot().bake(this.material.xTexSize, this.material.yTexSize);
    }

    public static LayerDefinition create(MeshDefinition meshDefinition, int n, int n2) {
        return new LayerDefinition(meshDefinition, new MaterialDefinition(n, n2));
    }
}

