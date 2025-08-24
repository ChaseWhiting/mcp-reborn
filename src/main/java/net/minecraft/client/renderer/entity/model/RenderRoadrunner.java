package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.passive.roadrunner.RoadrunnerEntity;
import net.minecraft.util.ResourceLocation;

public class RenderRoadrunner extends MobRenderer<RoadrunnerEntity, RoadrunnerModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/roadrunner.png");
    private static final ResourceLocation TEXTURE_MEEP = new ResourceLocation("textures/entity/roadrunner_meep.png");

    public RenderRoadrunner(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new RoadrunnerModel(), 0.3F);
        //this.addLayer(new RoadrunnerHeldItemLayer(this));

    }

    public ResourceLocation getTextureLocation(RoadrunnerEntity entity) {
        return entity.isMeep() ? TEXTURE_MEEP : TEXTURE;
    }
}