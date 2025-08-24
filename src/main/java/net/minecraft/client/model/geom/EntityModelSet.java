package net.minecraft.client.model.geom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinitions;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;

import java.util.Map;

public class EntityModelSet extends ReloadListener<Object> {
    private Map<ModelLayerLocation, LayerDefinition> roots = ImmutableMap.of();

    public EntityModelSet() {
        this.roots = ImmutableMap.copyOf(LayerDefinitions.createRoots());
    }

    public ModelPart bakeLayer(ModelLayerLocation modelLayerLocation) {
        LayerDefinition layerDefinition = this.roots.get(modelLayerLocation);
        if (layerDefinition == null) {
            throw new IllegalArgumentException("No model for layer " + modelLayerLocation);
        }
        return layerDefinition.bakeRoot();
    }

    @Override
    protected Object prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
        return new Object();
    }

    @Override
    protected void apply(Object p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
        this.roots = ImmutableMap.copyOf(LayerDefinitions.createRoots());
    }
}
