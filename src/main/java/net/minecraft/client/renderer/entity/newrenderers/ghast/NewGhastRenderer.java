package net.minecraft.client.renderer.entity.newrenderers.ghast;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.GhastRenderState;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.util.ResourceLocation;

public class NewGhastRenderer extends MobRenderer<GhastEntity, NewGhastModel> {
    private static final ResourceLocation GHAST_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/ghast/ghast.png");
    private static final ResourceLocation GHAST_SHOOTING_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/ghast/ghast_shooting.png");

    @Override
    public ResourceLocation getTextureLocation(GhastEntity ghast) {
        if (ghast.isCharging()) {
            return GHAST_SHOOTING_LOCATION;
        }
        return GHAST_LOCATION;
    }

    public NewGhastRenderer(EntityRendererManager context) {
        super(context, new NewGhastModel(context.bakeLayer(ModelLayers.GHAST)), 1.5f);
    }

//    //@Override
//    public void extractRenderState(GhastEntity ghast, GhastRenderState ghastRenderState, float f) {
//        //super.extractRenderState(ghast, ghastRenderState, f);
//        ghastRenderState.isCharging = ghast.isCharging();
//    }
}
