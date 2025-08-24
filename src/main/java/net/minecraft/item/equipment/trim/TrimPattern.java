package net.minecraft.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

import java.util.Objects;
import java.util.function.Supplier;

public class TrimPattern {
    private final ResourceLocation assetId;
    private final Supplier<Item> templateItem;
    private final ITextComponent description;
    private String descriptionId;

    public static final Codec<TrimPattern> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("asset_id").forGetter(TrimPattern::getAssetId),
            ResourceLocation.CODEC.fieldOf("template_item").forGetter(pattern -> pattern.getTemplateItem().get().getResourceLocation()),
            ExtraCodecs.COMPONENT.fieldOf("description").forGetter(TrimPattern::getDescription)
    ).apply(instance, (assetId, templateItemRL, description) ->
            new TrimPattern(assetId, () -> Registry.ITEM.get(templateItemRL), description)
    ));


    //public static final Codec<TrimPattern> CODEC = ArmorTrim.makeRegistryCodec(Registry.TRIM_PATTERN);

    public TrimPattern(ResourceLocation assetId, Supplier<Item> templateItem, ITextComponent description) {
        this.assetId = assetId;
        this.templateItem = templateItem;
        this.description = description;
    }

    public static ResourceLocation getKey(TrimPattern TrimPattern) {
        return Registry.TRIM_PATTERN.getKey(TrimPattern);
    }

    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("trim_pattern", Registry.TRIM_PATTERN.getKey(this));
        }

        return this.descriptionId;
    }

    public ResourceLocation getAssetId() {
        return new ResourceLocation("minecraft", "trims/models/armor/" + this.assetId.toString().replace("minecraft:", ""));
    }

    public String getAssetName() {
        return assetId.toString().replace("minecraft:", "");
    }


    public Supplier<Item> getTemplateItem() {
        return templateItem;
    }

    public ITextComponent getDescription() {
        return description;
    }

    public ITextComponent copyWithStyle(Supplier<TrimMaterial> holder) {
        return this.description.copy().withStyle(holder.get().getDescription().getStyle());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrimPattern that = (TrimPattern) o;
        return assetId.equals(that.assetId) &&
                templateItem.equals(that.templateItem) &&
                description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetId, templateItem, description);
    }

    @Override
    public String toString() {
        return "TrimPattern{" +
                "assetId=" + assetId +
                ", templateItem=" + templateItem +
                ", description=" + description +
                '}';
    }
}



