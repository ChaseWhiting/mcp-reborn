package net.minecraft.entity.frog;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class TadpoleRenderer extends MobRenderer<TadpoleEntity, TadpoleModel<TadpoleEntity>> {

    public TadpoleRenderer(EntityRendererManager manager) {
        super(manager, new TadpoleModel<>(manager.bakeLayer(ModelLayers.TADPOLE)), 0.14f);
    }

    @Override
    public ResourceLocation getTextureLocation(TadpoleEntity entity) {
        return new ResourceLocation("textures/entity/tadpole/tadpole.png");
    }
}
