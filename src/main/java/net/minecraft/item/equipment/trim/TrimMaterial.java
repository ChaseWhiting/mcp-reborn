package net.minecraft.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

import java.util.Map;
import java.util.function.Supplier;

public class TrimMaterial {
    private final String assetName;
    private final Supplier<Item> ingredient;
    private final float itemModelIndex;
    private final Map<ArmorMaterial, String> overrideArmorMaterials;
    private final ITextComponent description;
    private String descriptionId;

    public static final Codec<TrimMaterial> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("asset_name").forGetter(TrimMaterial::getAssetName),
            ResourceLocation.CODEC.fieldOf("ingredient").forGetter(trimMaterial -> trimMaterial.ingredient.get().getResourceLocation()),
            Codec.FLOAT.fieldOf("item_model_index").forGetter(TrimMaterial::getItemModelIndex),
            Codec.unboundedMap(ArmorMaterial.CODEC, Codec.STRING).optionalFieldOf("override_armor_materials", Map.of()).forGetter(TrimMaterial::getOverrideArmorMaterials),
            ExtraCodecs.COMPONENT.fieldOf("description").forGetter(TrimMaterial::getDescription)
    ).apply(instance, (asset, ingredient, index, override, description) -> {
        return new TrimMaterial(asset, () -> Registry.ITEM.get(ingredient), index, override, description);
    }));

    public static ResourceLocation getKey(TrimMaterial trimMaterial) {
        return Registry.TRIM_MATERIAL.getKey(trimMaterial);
    }

    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("trim_material", Registry.TRIM_MATERIAL.getKey(this));
        }

        return this.descriptionId;
    }

    //public static final Codec<TrimMaterial> CODEC = ArmorTrim.makeRegistryCodec(Registry.TRIM_MATERIAL);


    public TrimMaterial(String assetName, Supplier<Item> ingredient, float itemModelIndex, Map<ArmorMaterial, String> overrideArmorMaterials, ITextComponent description) {
        this.assetName = assetName;
        this.ingredient = ingredient;
        this.itemModelIndex = itemModelIndex;
        this.overrideArmorMaterials = overrideArmorMaterials;
        this.description = description;
    }

    public String getAssetName() {
        return assetName;
    }

    public Supplier<Item> getIngredient() {
        return ingredient;
    }

    public float getItemModelIndex() {
        return itemModelIndex;
    }

    public Map<ArmorMaterial, String> getOverrideArmorMaterials() {
        return overrideArmorMaterials;
    }

    public ITextComponent getDescription() {
        return description;
    }

    public static TrimMaterial create(String string, Item item, float f, ITextComponent component, Map<ArmorMaterial, String> map) {
        return new TrimMaterial(string, () -> item, f, map, component);
    }
}
