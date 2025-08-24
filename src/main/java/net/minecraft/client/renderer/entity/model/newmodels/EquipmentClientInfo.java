package net.minecraft.client.renderer.entity.model.newmodels;

import com.mojang.serialization.Codec;
import net.minecraft.util.IStringSerializable;

public class EquipmentClientInfo {
    public static enum LayerType implements IStringSerializable
    {
        HUMANOID("humanoid"),
        HUMANOID_LEGGINGS("humanoid_leggings"),
        WINGS("wings"),
        WOLF_BODY("wolf_body"),
        HORSE_BODY("horse_body"),
        LLAMA_BODY("llama_body"),
        PIG_SADDLE("pig_saddle"),
        STRIDER_SADDLE("strider_saddle"),
        CAMEL_SADDLE("camel_saddle"),
        HORSE_SADDLE("horse_saddle"),
        DONKEY_SADDLE("donkey_saddle"),
        MULE_SADDLE("mule_saddle"),
        ZOMBIE_HORSE_SADDLE("zombie_horse_saddle"),
        SKELETON_HORSE_SADDLE("skeleton_horse_saddle"),
        HAPPY_GHAST_BODY("happy_ghast_body");

        public static final Codec<LayerType> CODEC;
        private final String id;

        private LayerType(String string2) {
            this.id = string2;
        }

        @Override
        public String getSerializedName() {
            return this.id;
        }

        public String trimAssetPrefix() {
            return "trims/entity/" + this.id;
        }

        static {
            CODEC = IStringSerializable.fromEnum(LayerType::values);
        }
    }
}
