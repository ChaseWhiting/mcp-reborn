package net.minecraft.client.model.geom;


import net.minecraft.util.ResourceLocation;

public final class ModelLayerLocation {
    private final ResourceLocation model;
    private final String layer;

    public ModelLayerLocation(ResourceLocation resourceLocation, String string) {
        this.model = resourceLocation;
        this.layer = string;
    }

    public ResourceLocation getModel() {
        return this.model;
    }

    public String getLayer() {
        return this.layer;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ModelLayerLocation) {
            ModelLayerLocation modelLayerLocation = (ModelLayerLocation)object;
            return this.model.equals(modelLayerLocation.model) && this.layer.equals(modelLayerLocation.layer);
        }
        return false;
    }

    public int hashCode() {
        int n = this.model.hashCode();
        n = 31 * n + this.layer.hashCode();
        return n;
    }

    public String toString() {
        return this.model + "#" + this.layer;
    }
}

