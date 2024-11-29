// Decompiled with: CFR 0.152
// Class Version: 17
package net.minecraft.entity.monster;


import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;

public class RayTracingRenderer
extends LivingRenderer<RayTracing, PlayerModel<RayTracing>> {
    private static final ResourceLocation RAY_LOCATION = new ResourceLocation("textures/entity/player/wide/ray.png");

    public RayTracingRenderer(EntityRendererManager $$0) {
        super($$0, new PlayerModel(0.5f, false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(RayTracing $$0) {
        return RAY_LOCATION;
    }
}
