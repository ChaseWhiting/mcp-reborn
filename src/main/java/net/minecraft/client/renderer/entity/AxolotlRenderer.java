package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import net.minecraft.client.renderer.entity.model.AxolotlModel;
import net.minecraft.entity.axolotl.AxolotlEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import java.util.Map;

public class AxolotlRenderer extends MobRenderer<AxolotlEntity, AxolotlModel<AxolotlEntity>> {

    private static final Map<AxolotlEntity.AxolotlVariant, ResourceLocation> TEXTURE_BY_TYPE =
            Util.make(Maps.newHashMap(), hashMap -> {
                for (AxolotlEntity.AxolotlVariant variant : AxolotlEntity.AxolotlVariant.BY_ID) {
                    hashMap.put(variant, new ResourceLocation(String.format("textures/entity/axolotl/axolotl_%s.png", variant.getName())));
                }
            });

    public AxolotlRenderer(EntityRendererManager manager) {
        super(manager, new AxolotlModel(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(AxolotlEntity entity) {
        return TEXTURE_BY_TYPE.get(entity.getVariant());
    }
}
