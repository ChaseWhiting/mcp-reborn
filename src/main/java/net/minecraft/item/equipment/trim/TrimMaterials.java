package net.minecraft.item.equipment.trim;

import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;
import java.util.Optional;

public class TrimMaterials {
    public static final TrimMaterial DUMMY_TRIM_MATERIAL = register(registryKey("dummy"), Items.AIR, Style.EMPTY, 0.0F);




    public static final RegistryKey<TrimMaterial> QUARTZ = TrimMaterials.registryKey("quartz");
    public static final RegistryKey<TrimMaterial> IRON = TrimMaterials.registryKey("iron");
    public static final RegistryKey<TrimMaterial> NETHERITE = TrimMaterials.registryKey("netherite");
    public static final RegistryKey<TrimMaterial> REDSTONE = TrimMaterials.registryKey("redstone");
    public static final RegistryKey<TrimMaterial> COPPER = TrimMaterials.registryKey("copper");
    public static final RegistryKey<TrimMaterial> GOLD = TrimMaterials.registryKey("gold");
    public static final RegistryKey<TrimMaterial> EMERALD = TrimMaterials.registryKey("emerald");
    public static final RegistryKey<TrimMaterial> DIAMOND = TrimMaterials.registryKey("diamond");
    public static final RegistryKey<TrimMaterial> LAPIS = TrimMaterials.registryKey("lapis");
    public static final RegistryKey<TrimMaterial> AMETHYST = TrimMaterials.registryKey("amethyst");
    public static final RegistryKey<TrimMaterial> RESIN = TrimMaterials.registryKey("resin");
    public static final RegistryKey<TrimMaterial> SCULK = TrimMaterials.registryKey("sculk");
    public static final RegistryKey<TrimMaterial> ROSE_GOLD = TrimMaterials.registryKey("rose_gold");


    public static void bootstrap() {
        TrimMaterials.register(QUARTZ, Items.QUARTZ, Style.EMPTY.withColor(14931140), 0.1f);
        TrimMaterials.register(IRON, Items.IRON_INGOT, Style.EMPTY.withColor(0xECECEC), 0.2f, Map.of(ArmorMaterial.IRON, "iron_darker"));
        TrimMaterials.register(NETHERITE, Items.NETHERITE_INGOT, Style.EMPTY.withColor(6445145), 0.3f, Map.of(ArmorMaterial.NETHERITE, "netherite_darker"));
        TrimMaterials.register(REDSTONE, Items.REDSTONE, Style.EMPTY.withColor(9901575), 0.4f);
        TrimMaterials.register(COPPER, Items.COPPER_INGOT, Style.EMPTY.withColor(11823181), 0.5f);
        TrimMaterials.register(GOLD, Items.GOLD_INGOT, Style.EMPTY.withColor(14594349), 0.6f, Map.of(ArmorMaterial.GOLD, "gold_darker"));
        TrimMaterials.register(EMERALD, Items.EMERALD, Style.EMPTY.withColor(1155126), 0.7f);
        TrimMaterials.register(DIAMOND, Items.DIAMOND, Style.EMPTY.withColor(7269586), 0.8f, Map.of(ArmorMaterial.DIAMOND, "diamond_darker"));
        TrimMaterials.register(LAPIS, Items.LAPIS_LAZULI, Style.EMPTY.withColor(4288151), 0.9f);
        TrimMaterials.register(AMETHYST, Items.AMETHYST_SHARD, Style.EMPTY.withColor(10116294), 1.0f);
        TrimMaterials.register(RESIN, Items.RESIN_BRICK, Style.EMPTY.withColor(16545810), 1.1f);
        TrimMaterials.register(SCULK, Items.ECHO_SHARD, Style.EMPTY.withColor(0x29dfeb), 1.2f);
        TrimMaterials.register(ROSE_GOLD, Items.ROSE_GOLD_INGOT, Style.EMPTY.withColor(0xfd9480), 1.3F, Map.of(ArmorMaterial.ROSE_GOLD, "rose_gold_darker"));
    }

    private static RegistryKey<TrimMaterial> registryKey(String string) {
        return RegistryKey.create(Registry.TRIM_MATERIAL_REGISTRY, new ResourceLocation(string));
    }

    public static Optional<TrimMaterial> getFromIngredient(DynamicRegistries registryAccess, ItemStack itemStack) {
        return registryAccess.registryOrThrow(Registry.TRIM_MATERIAL_REGISTRY).stream().filter(reference -> itemStack.getItem() == (reference.getIngredient().get())).findFirst();
    }

    public static Optional<TrimMaterial> getFromIngredient(ItemStack itemStack) {
        return (Registry.TRIM_MATERIAL).stream().filter(reference -> itemStack.getItem() == (reference.getIngredient().get())).findFirst();
    }

    private static TrimMaterial register(RegistryKey<TrimMaterial> resourceKey, Item item, Style style, float f) {
        return TrimMaterials.register(resourceKey, item, style, f, Map.of());
    }

    private static TrimMaterial register(RegistryKey<TrimMaterial> resourceKey, Item item, Style style, float f, Map<ArmorMaterial, String> map) {
        TrimMaterial trimMaterial = TrimMaterial.create(resourceKey.location().getPath(), item, f, new TranslationTextComponent(Util.makeDescriptionId("trim_material", resourceKey.location())).withStyle(style), map);
        return Registry.register(Registry.TRIM_MATERIAL, resourceKey.location(), trimMaterial);
    }

}
