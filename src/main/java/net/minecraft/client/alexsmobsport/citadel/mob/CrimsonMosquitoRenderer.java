package net.minecraft.client.alexsmobsport.citadel.mob;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.monster.crimson_mosquito.CrimsonMosquitoEntity;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class CrimsonMosquitoRenderer extends MobRenderer<CrimsonMosquitoEntity, ModelCrimsonMosquito> {

    private static final Map<Integer, ResourceLocation> TEXTURE_MAP =
            ImmutableMap.of(
                            CrimsonMosquitoEntity.BloodType.RED.id(), create("crimson_mosquito"),
                            CrimsonMosquitoEntity.BloodType.BLUE.id(), create("crimson_mosquito_blue"),
                    CrimsonMosquitoEntity.BloodType.ENDER_BLOOD.id(), create("crimson_mosquito_ender"),
                            CrimsonMosquitoEntity.BloodType.RESIN.id(), create("crimson_mosquito_resin"));


    private static ResourceLocation create(String s) {
        return new ResourceLocation("textures/entity/" + s + ".png");
    }

    public CrimsonMosquitoRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelCrimsonMosquito(), 0.6F);
        this.addLayer(new LayerCrimsonMosquitoBlood(this));
    }

    @Override
    protected void scale(CrimsonMosquitoEntity p_225620_1_, MatrixStack matrixStackIn, float p_225620_3_) {
        float mosScale = p_225620_1_.prevMosquitoScale + (p_225620_1_.getMosquitoScale() - p_225620_1_.prevMosquitoScale) * p_225620_3_;
        matrixStackIn.scale(mosScale * 1.2F, mosScale * 1.2F, mosScale * 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(CrimsonMosquitoEntity entity) {
        return TEXTURE_MAP.getOrDefault(entity.getBloodType(), create("crimson_mosquito"));
    }
}
