package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.model.FrogModel;
import net.minecraft.entity.frog.FrogEntity;
import net.minecraft.util.ResourceLocation;

public class FrogRenderer extends MobRenderer<FrogEntity, FrogModel<FrogEntity>>  {
    private static final ResourceLocation TEMPERATE = create("temperate");
    private static final ResourceLocation WARM = create("warm");
    private static final ResourceLocation COLD = create("cold");


    private static ResourceLocation create(String id) {
        String s = String.valueOf("textures/entity/frog/" + id + "_frog.png");
        return new ResourceLocation(s);
    }

    public FrogRenderer(EntityRendererManager  manager) {
        super(manager, new FrogModel<>(manager.bakeLayer(ModelLayers.FROG)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(FrogEntity entity) {
        return switch (entity.getVariant()) {
            case TEMPERATE -> TEMPERATE;
            case WARM -> WARM;
            case COLD -> COLD;
        };
    }
}
