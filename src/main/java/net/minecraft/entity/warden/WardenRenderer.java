package net.minecraft.entity.warden;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class WardenRenderer<T extends WardenEntity, M extends WardenModel<T>> extends MobRenderer<WardenEntity, WardenModel<WardenEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/warden/warden.png");
    private static final ResourceLocation BIOLUMINESCENT_LAYER_TEXTURE = new ResourceLocation("textures/entity/warden/warden_bioluminescent_layer.png");
    private static final ResourceLocation HEART_TEXTURE = new ResourceLocation("textures/entity/warden/warden_heart.png");
    private static final ResourceLocation PULSATING_SPOTS_TEXTURE_1 = new ResourceLocation("textures/entity/warden/warden_pulsating_spots_1.png");
    private static final ResourceLocation PULSATING_SPOTS_TEXTURE_2 = new ResourceLocation("textures/entity/warden/warden_pulsating_spots_2.png");

    public final LivingEntityEmissiveLayer<WardenEntity, WardenModel<WardenEntity>> BODY = new LivingEntityEmissiveLayer<WardenEntity, WardenModel<WardenEntity>>(this, TEXTURE, (warden, f, f2) -> warden.getTendrilAnimation(f), WardenModel::getTendrilsLayerModelParts, RenderType::entityTranslucentEmissive, false);

    public WardenRenderer(EntityRendererManager manager) {
        super(manager, new WardenModel<>(manager.bakeLayer(ModelLayers.WARDEN)), 0.9f);
        this.addLayer(BODY);

        this.addLayer(new LivingEntityEmissiveLayer<>(this, BIOLUMINESCENT_LAYER_TEXTURE, (warden, f, f2) -> 1.0f, WardenModel::getBioluminescentLayerModelParts, RenderType::entityTranslucentEmissive, false));
        this.addLayer(new LivingEntityEmissiveLayer<>(this, PULSATING_SPOTS_TEXTURE_1, (warden, f, f2) -> Math.max(0.0f, MathHelper.cos(f2 * 0.045f) * 0.25f), WardenModel::getPulsatingSpotsLayerModelParts, RenderType::entityTranslucentEmissive, false));
        this.addLayer(new LivingEntityEmissiveLayer<>(this, PULSATING_SPOTS_TEXTURE_2, (warden, f, f2) -> Math.max(0.0f, MathHelper.cos(f2 * 0.045f + (float) Math.PI) * 0.25f), WardenModel::getPulsatingSpotsLayerModelParts, RenderType::entityTranslucentEmissive, false));
        this.addLayer(new LivingEntityEmissiveLayer<>(this, HEART_TEXTURE, (warden, f, f2) -> warden.getHeartAnimation(f), WardenModel::getHeartLayerModelParts, RenderType::entityTranslucentEmissive, false));

    }



    @Override
    public ResourceLocation getTextureLocation(WardenEntity entity) {
        return TEXTURE;
    }
}
