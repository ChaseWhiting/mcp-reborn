package net.minecraft.entity.monster.creaking;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CreakingEyesLayer;
import net.minecraft.util.ResourceLocation;

public class CreakingRenderer extends MobRenderer<CreakingEntity, CreakingModel<CreakingEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/creaking.png");

    public CreakingRenderer(EntityRendererManager manager) {
        super(manager, new CreakingModel<>(), 0.5F);
        this.addLayer(new CreakingEyesLayer<>(this));
    }

    public ResourceLocation getTextureLocation(CreakingEntity p_234791_) {
        return TEXTURE;
    }
}
