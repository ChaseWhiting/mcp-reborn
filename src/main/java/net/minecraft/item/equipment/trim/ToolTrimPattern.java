package net.minecraft.item.equipment.trim;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

import java.util.Objects;
import java.util.function.Supplier;

public class ToolTrimPattern {
    private final ResourceLocation assetId;
    private final Supplier<Item> templateItem;
    private final ITextComponent description;
    private String descriptionId;
    private final float id;

//    public static final Codec<ToolTrimPattern> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            ResourceLocation.CODEC.fieldOf("asset_id").forGetter(ToolTrimPattern::getAssetId),
//            ResourceLocation.CODEC.fieldOf("template_item").forGetter(pattern -> pattern.getTemplateItem().get().getResourceLocation()),
//            ExtraCodecs.COMPONENT.fieldOf("description").forGetter(ToolTrimPattern::getDescription)
//    ).apply(instance, (assetId, templateItemRL, description) ->
//            new ToolTrimPattern(assetId, () -> Registry.ITEM.get(templateItemRL), description)
//    ));

    public ToolTrimPattern(ResourceLocation assetId, Supplier<Item> templateItem, ITextComponent description, float id) {
        this.assetId = assetId;
        this.templateItem = templateItem;
        this.description = description;
        this.id = id;
    }

    public static ResourceLocation getKey(ToolTrimPattern TrimPattern) {
        return Registry.TOOL_TRIM_PATTERN.getKey(TrimPattern);
    }

    public float getID() {
        return id;
    }

    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("tool_trim_pattern", Registry.TOOL_TRIM_PATTERN.getKey(this));
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
        ToolTrimPattern that = (ToolTrimPattern) o;
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
        return "ToolTrimPattern{" +
                "assetId=" + assetId +
                ", templateItem=" + templateItem +
                ", description=" + description +
                '}';
    }
}



