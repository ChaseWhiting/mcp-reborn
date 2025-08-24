package net.minecraft.entity.allay;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class AllayRenderer extends MobRenderer<AllayEntity, AllayModel> {

    public AllayRenderer(EntityRendererManager manager) {
        super(manager, new AllayModel(manager.bakeLayer(ModelLayers.ALLAY)), 0.4F);
        this.addLayer(new HeldItemLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(AllayEntity entity) {
        return new ResourceLocation("textures/entity/allay/allay.png");
    }

    @Override
    protected int getBlockLightLevel(AllayEntity p_225624_1_, BlockPos p_225624_2_) {
        return 15;
    }
}
