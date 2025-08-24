package net.minecraft.client.model.geom.builders;

@FunctionalInterface
public interface MeshTransformer {
    public static final MeshTransformer IDENTITY = meshDefinition -> meshDefinition;

    public static MeshTransformer scaling(float f) {
        float f2 = 24.016f * (1.0f - f);
        return meshDefinition -> meshDefinition.transformed(partPose -> partPose.scaled(f).translated(0.0f, f2, 0.0f));
    }

    public MeshDefinition apply(MeshDefinition var1);
}

