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

public class TrimPatterns {
    public static final TrimPattern DUMMY_TRIM_PATTERN = register(Items.AIR, registryKey("dummy"));



    public static final RegistryKey<TrimPattern> SENTRY = TrimPatterns.registryKey("sentry");
    public static final RegistryKey<TrimPattern> DUNE = TrimPatterns.registryKey("dune");
    public static final RegistryKey<TrimPattern> COAST = TrimPatterns.registryKey("coast");
    public static final RegistryKey<TrimPattern> WILD = TrimPatterns.registryKey("wild");
    public static final RegistryKey<TrimPattern> WARD = TrimPatterns.registryKey("ward");
    public static final RegistryKey<TrimPattern> EYE = TrimPatterns.registryKey("eye");
    public static final RegistryKey<TrimPattern> VEX = TrimPatterns.registryKey("vex");
    public static final RegistryKey<TrimPattern> TIDE = TrimPatterns.registryKey("tide");
    public static final RegistryKey<TrimPattern> SNOUT = TrimPatterns.registryKey("snout");
    public static final RegistryKey<TrimPattern> RIB = TrimPatterns.registryKey("rib");
    public static final RegistryKey<TrimPattern> SPIRE = TrimPatterns.registryKey("spire");
    public static final RegistryKey<TrimPattern> WAYFINDER = TrimPatterns.registryKey("wayfinder");
    public static final RegistryKey<TrimPattern> SHAPER = TrimPatterns.registryKey("shaper");
    public static final RegistryKey<TrimPattern> SILENCE = TrimPatterns.registryKey("silence");
    public static final RegistryKey<TrimPattern> RAISER = TrimPatterns.registryKey("raiser");
    public static final RegistryKey<TrimPattern> HOST = TrimPatterns.registryKey("host");
    public static final RegistryKey<TrimPattern> BOLT = TrimPatterns.registryKey("bolt");
    public static final RegistryKey<TrimPattern> FLOW = TrimPatterns.registryKey("flow");

    public static void bootstrap( ) {
        TrimPatterns.register(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, SENTRY);
        TrimPatterns.register(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, DUNE);
        TrimPatterns.register(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, COAST);
        TrimPatterns.register(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, WILD);
        TrimPatterns.register(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, WARD);
        TrimPatterns.register(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, EYE);
        TrimPatterns.register(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, VEX);
        TrimPatterns.register(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, TIDE);
        TrimPatterns.register(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, SNOUT);
        TrimPatterns.register(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, RIB);
        TrimPatterns.register(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, SPIRE);
        TrimPatterns.register(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, WAYFINDER);
        TrimPatterns.register(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, SHAPER);
        TrimPatterns.register(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, SILENCE);
        TrimPatterns.register(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, RAISER);
        TrimPatterns.register(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, HOST);
        TrimPatterns.register(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, BOLT);
        TrimPatterns.register(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, FLOW);
    }

    public static Optional<TrimPattern> getFromTemplate(DynamicRegistries registryAccess, ItemStack itemStack) {
        return registryAccess.registryOrThrow(Registry.TRIM_PATTERN_REGISTRY).stream().filter(reference -> itemStack.getItem() == (reference.getTemplateItem().get())).findFirst();
    }

    public static Optional<TrimPattern> getFromTemplate(ItemStack itemStack) {
        return (Registry.TRIM_PATTERN).stream().filter(reference -> itemStack.getItem() == (reference.getTemplateItem().get())).findFirst();
    }

    private static TrimPattern register(Item item, RegistryKey<TrimPattern> resourceKey) {
        TrimPattern trimPattern = new TrimPattern(resourceKey.location(), () -> item, new TranslationTextComponent(Util.makeDescriptionId("trim_pattern", resourceKey.location())));
        return Registry.register(Registry.TRIM_PATTERN, resourceKey.location(), trimPattern);
    }

    private static RegistryKey<TrimPattern> registryKey(String string) {
        return RegistryKey.create(Registry.TRIM_PATTERN_REGISTRY, new ResourceLocation(string));
    }
}