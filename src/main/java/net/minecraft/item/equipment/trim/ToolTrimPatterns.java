package net.minecraft.item.equipment.trim;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;

public class ToolTrimPatterns {
    public static final ToolTrimPattern DUMMY_TOOL_TRIM_PATTERN = register(Items.AIR, registryKey("dummy"), 0);



    public static final RegistryKey<ToolTrimPattern> EDGE = ToolTrimPatterns.registryKey("edge");
    public static final RegistryKey<ToolTrimPattern> COAT = ToolTrimPatterns.registryKey("coat");

    public static void bootstrap( ) {
        register(Items.EDGE_TOOL_TRIM_SMITHING_TEMPLATE, EDGE, 1F);
        register(Items.COAT_TOOL_TRIM_SMITHING_TEMPLATE, COAT, 2F);

    }

    public static Optional<ToolTrimPattern> getFromTemplate(DynamicRegistries registryAccess, ItemStack itemStack) {
        return registryAccess.registryOrThrow(Registry.TOOL_TRIM_PATTERN_REGISTRY).stream().filter(reference -> itemStack.getItem() == (reference.getTemplateItem().get())).findFirst();
    }

    public static Optional<ToolTrimPattern> getFromTemplate(ItemStack itemStack) {
        return (Registry.TOOL_TRIM_PATTERN).stream().filter(reference -> itemStack.getItem() == (reference.getTemplateItem().get())).findFirst();
    }

    private static ToolTrimPattern register(Item item, RegistryKey<ToolTrimPattern> resourceKey, float id) {
        ToolTrimPattern trimPattern = new ToolTrimPattern(resourceKey.location(), () -> item, new TranslationTextComponent(Util.makeDescriptionId("tool_trim_pattern", resourceKey.location())), id);
        return Registry.register(Registry.TOOL_TRIM_PATTERN, resourceKey.location(), trimPattern);
    }

    private static RegistryKey<ToolTrimPattern> registryKey(String string) {
        return RegistryKey.create(Registry.TOOL_TRIM_PATTERN_REGISTRY, new ResourceLocation(string));
    }
}