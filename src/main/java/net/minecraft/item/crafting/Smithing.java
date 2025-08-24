package net.minecraft.item.crafting;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.WeatheringCopper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.SmithingTemplateItem;
import net.minecraft.item.equipment.trim.TrimPattern;
import net.minecraft.item.equipment.trim.TrimPatterns;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Smithing {

    private static final Map<Item, Item> DIAMOND_TO_NETHERITE = Util.make(new HashMap<>(), map -> {
        map.put(Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
        map.put(Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
        map.put(Items.DIAMOND_AXE, Items.NETHERITE_AXE);
        map.put(Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
        map.put(Items.DIAMOND_HOE, Items.NETHERITE_HOE);
        map.put(Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
        map.put(Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
        map.put(Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
        map.put(Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);

        map.put(Items.SHIELD, Items.NETHERITE_SHIELD);
    });

    private static final Map<Item, Item> IRON_TO_DIAMOND = Util.make(new HashMap<>(), map -> {
        map.put(Items.IRON_SWORD, Items.DIAMOND_SWORD);
        map.put(Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE);
        map.put(Items.IRON_AXE, Items.DIAMOND_AXE);
        map.put(Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL);
        map.put(Items.IRON_HOE, Items.DIAMOND_HOE);
        map.put(Items.IRON_HELMET, Items.DIAMOND_HELMET);
        map.put(Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE);
        map.put(Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS);
        map.put(Items.IRON_BOOTS, Items.DIAMOND_BOOTS);
    });

    public static void registerRecipes(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map) {
        for (TrimPattern trimPattern : Registry.TRIM_PATTERN.stream().filter(trim -> trim != TrimPatterns.DUMMY_TRIM_PATTERN).collect(Collectors.toList())) {
            addSmithingTrimRecipe(map, trimPattern.getAssetName().replace("minecraft:", "")+ "_trim", trimPattern.getTemplateItem().get());


            if (trimPattern != Registry.TRIM_PATTERN.get(TrimPatterns.BOLT)) {
                RecipeManager.addShapedRecipe(3, 3,
                        map,
                        trimPattern.getAssetName().replace("minecraft:", "") + "_trim_duplicate",
                        new ItemStack(trimPattern.getTemplateItem().get(), 2),
                        new String[]{
                                "#T#",
                                "#X#",
                                "###"
                        },
                        Map.of('#', Ingredient.of(Items.DIAMOND),
                                'T', Ingredient.of(trimPattern.getTemplateItem().get()),
                                'X', Ingredient.of(((SmithingTemplateItem)trimPattern.getTemplateItem().get()).getDuplicationItem()))
                );
            } else {
                RecipeManager.addShapedRecipe(3, 3,
                        map,
                        trimPattern.getAssetName().replace("minecraft:", "") + "_trim_duplicate",
                        new ItemStack(trimPattern.getTemplateItem().get(), 2),
                        new String[]{
                                "#T#",
                                "#X#",
                                "###"
                        },
                        Map.of('#', Ingredient.of(Items.DIAMOND),
                                'T', Ingredient.of(trimPattern.getTemplateItem().get()),
                                'X', Ingredient.of(Registry.ITEM.stream().filter(item -> item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof WeatheringCopper).map(ItemStack::new)))
                );
            }
        }
        RecipeManager.addShapedRecipe(3, 3,
                map,
                "netherite_upgrade_duplicate",
                new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 2),
                new String[]{
                        "#T#",
                        "#X#",
                        "###"
                },
                Map.of('#', Ingredient.of(Items.DIAMOND),
                        'T', Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        'X', Ingredient.of(((SmithingTemplateItem)Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).getDuplicationItem()))
        );

        for (Map.Entry<Item, Item> entry : DIAMOND_TO_NETHERITE.entrySet()) {
            addSmithingUpgradeRecipe(map, entry.getKey().getDescriptionId().replace("minecraft:", "").replace("item.minecraft.", "") + "_netherite_upgrade", entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Item, Item> entry : IRON_TO_DIAMOND.entrySet()) {
            addSmithingDiamondUpgradeRecipe(map, entry.getKey().getDescriptionId().replace("minecraft:", "").replace("item.minecraft.", "") + "_diamond_upgrade", entry.getKey(), entry.getValue());
        }
    }



    public static void addSmithingTrimRecipe(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map, String recipeID, Item trimPattern) {
        map.computeIfAbsent(IRecipeType.NEW_SMITHING, (type) -> ImmutableMap.builder())
                .put(new ResourceLocation(recipeID), new SmithingTrimRecipe(new ResourceLocation(recipeID),
                        Ingredient.of(trimPattern),
                        Ingredient.of(ArmorTrim.TRIMMABLE_ARMOR.stream().map(ItemStack::new)),
                        Ingredient.of(ArmorTrim.TRIM_MATERIALS.stream().map(ItemStack::new))));

        map.computeIfAbsent(IRecipeType.NEW_SMITHING, (type) -> ImmutableMap.builder())
                .put(new ResourceLocation(recipeID + "_glow"), new SmithingGlowTrimRecipe(new ResourceLocation(recipeID + "_glow"),
                        Ingredient.of(trimPattern),
                        Ingredient.of(ArmorTrim.TRIMMABLE_ARMOR.stream().map(ItemStack::new)),
                        Ingredient.of(Items.GLOWSTONE_DUST)));
    }

    public static void addSmithingUpgradeRecipe(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map, String recipeID, Item base, Item result) {
        map.computeIfAbsent(IRecipeType.NEW_SMITHING, (type) -> ImmutableMap.builder())
                .put(new ResourceLocation(recipeID), new SmithingTransformRecipe(new ResourceLocation(recipeID),
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(base),
                        Ingredient.of(Items.NETHERITE_INGOT),
                        new ItemStack(result)));
    }

    public static void addSmithingDiamondUpgradeRecipe(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map, String recipeID, Item base, Item result) {
        map.computeIfAbsent(IRecipeType.NEW_SMITHING, (type) -> ImmutableMap.builder())
                .put(new ResourceLocation(recipeID), new SmithingDiamondTransformRecipe(new ResourceLocation(recipeID),
                        Ingredient.of(Items.DIAMOND_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(base),
                        Ingredient.of(result),
                        new ItemStack(result)));
    }
}
