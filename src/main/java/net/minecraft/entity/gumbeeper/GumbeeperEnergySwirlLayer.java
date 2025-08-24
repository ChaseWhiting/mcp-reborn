package net.minecraft.entity.gumbeeper;

import net.minecraft.client.renderer.entity.layers.EnergyLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ResourceLocation;

public class GumbeeperEnergySwirlLayer extends EnergyLayer<GumbeeperEntity, GumbeeperModel> {
    private static final ResourceLocation POWER_LOCATION = new ResourceLocation("minecraft", "textures/entity/gumbeeper_charged.png");
    private final GumbeeperModel model = new GumbeeperModel(1.0F);

    public GumbeeperEnergySwirlLayer(GumbeeperRenderer renderer) {
        super(renderer);
    }

    protected float xOffset(float f) {
        return f * 0.01F;
    }

    protected ResourceLocation getTextureLocation() {
        return POWER_LOCATION;
    }

    protected EntityModel<GumbeeperEntity> model() {
        return this.model;
    }
}