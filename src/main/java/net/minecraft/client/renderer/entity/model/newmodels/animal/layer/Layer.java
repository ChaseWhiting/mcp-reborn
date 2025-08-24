package net.minecraft.client.renderer.entity.model.newmodels.animal.layer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.entity.model.newmodels.EquipmentClientInfo;
import net.minecraft.item.dyeable.Dyeable;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public record Layer(ResourceLocation textureId, Optional<Dyeable> dyeable, boolean usePlayerTexture) {

        public Layer(ResourceLocation resourceLocation) {
            this(resourceLocation, Optional.empty(), false);
        }


        public ResourceLocation getTextureLocation(EquipmentClientInfo.LayerType layerType) {
            return this.textureId.withPath(string -> "textures/entity/equipment/" + layerType.getSerializedName() + "/" + string + ".png");
        }
    }