package net.minecraft.item.equipment.trim;

import net.minecraft.item.*;
import net.minecraft.item.tool.SwordItem;
import net.minecraft.item.tool.ToolItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ToolTrim {

    public static final List<Item> TRIMMABLE_TOOLS = Util.make(new ArrayList<>(), list -> {
        for (Item item : Registry.ITEM) {
            if (item instanceof ToolItem || item instanceof SwordItem || item == Items.CROSSBOW || item instanceof SpyglassItem) {
                list.add(item);
            }
        }
    });




    private static final Logger LOGGER = LogManager.getLogger();
    public static final String TAG_TRIM_ID = "Trim";
    private static final ITextComponent UPGRADE_TITLE = new TranslationTextComponent(Util.makeDescriptionId("item", new ResourceLocation("smithing_tool_template.upgrade"))).withStyle(TextFormatting.GRAY);
    private final Supplier<TrimMaterial> material;
    private final Supplier<ToolTrimPattern> toolTrimPattern;

    public ToolTrim(Supplier<TrimMaterial> holder, Supplier<ToolTrimPattern> holder2) {
        this.material = holder;
        this.toolTrimPattern = holder2;
    }

    public ToolTrim(TrimMaterial material, ToolTrimPattern pattern) {
        this(() -> material, () -> pattern);
    }

    private String getColorPaletteSuffix(ArmorMaterial armorMaterial) {
        Map<ArmorMaterial, String> map = this.material.get().getOverrideArmorMaterials();
        if (armorMaterial != null && map.containsKey(armorMaterial)) {
            return map.get(armorMaterial);
        }
        return this.material.get().getAssetName();
    }

    public boolean hasPatternAndMaterial(Supplier<ToolTrimPattern> holder, Supplier<TrimMaterial> holder2) {
        return holder == this.toolTrimPattern && holder2 == this.material;
    }

    public boolean hasPatternAndMaterial(ToolTrimPattern holder, TrimMaterial holder2) {
        return holder == this.toolTrimPattern.get() && holder2 == this.material.get();
    }

    public Supplier<ToolTrimPattern> toolPattern() {
        return this.toolTrimPattern;
    }

    public Supplier<TrimMaterial> material() {
        return this.material;
    }

    public ToolTrimPattern getToolPattern() {
        return this.toolTrimPattern.get();
    }

    public TrimMaterial getMaterial() {
        return this.material.get();
    }

    public boolean equals(Object object) {
        if (!(object instanceof ToolTrim armorTrim)) {
            return false;
        }
        return armorTrim.toolTrimPattern == this.toolTrimPattern && armorTrim.material == this.material;
    }



    public static boolean setTrim(DynamicRegistries registry, ItemStack itemStack, ToolTrim trim) {
        if (TRIMMABLE_TOOLS.contains(itemStack.getItem())) {
            CompoundNBT trimTag = new CompoundNBT();
            trimTag.putString("tool_pattern", Registry.TOOL_TRIM_PATTERN.getKey(trim.getToolPattern()).toString());
            trimTag.putString("material", Registry.TRIM_MATERIAL.getKey(trim.getMaterial()).toString());

            CompoundNBT tag = itemStack.getOrCreateTag();
            tag.put(TAG_TRIM_ID, trimTag);
            itemStack.setTag(tag);
            return true;
        }

        return false;
    }


    public static Optional<ToolTrim> getToolTrim(DynamicRegistries dynamicRegistries, ItemStack itemStack) {
        if (TRIMMABLE_TOOLS.contains(itemStack.getItem()) && itemStack.hasTag() && itemStack.getTag().contains(TAG_TRIM_ID, 10)) {
            CompoundNBT trimTag = itemStack.getTag().getCompound(TAG_TRIM_ID);

            String patternId = trimTag.getString("tool_pattern");
            String materialId = trimTag.getString("material");

            // Lookup in registry - adjust for your registry type!
            ToolTrimPattern pattern = Registry.TOOL_TRIM_PATTERN.get(new ResourceLocation(patternId));
            TrimMaterial material = Registry.TRIM_MATERIAL.get(new ResourceLocation(materialId));

            if (pattern != null && material != null) {
                return Optional.of(new ToolTrim(material, pattern));
            }
        }
        return Optional.empty();
    }



    public static void appendUpgradeHoverText(ItemStack itemStack, DynamicRegistries registryAccess, List<ITextComponent> list) {
        Optional<ToolTrim> optional = ToolTrim.getToolTrim(registryAccess, itemStack);
        if (optional.isPresent()) {
            ToolTrim armorTrim = optional.get();
            list.add(UPGRADE_TITLE);
            list.add(new StringTextComponent(" ").append(armorTrim.toolPattern().get().copyWithStyle(armorTrim.material())));
            list.add(new StringTextComponent(" ").append(armorTrim.material().get().getDescription()));
        }
    }
}
