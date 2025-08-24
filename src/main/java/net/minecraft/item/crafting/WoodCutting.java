package net.minecraft.item.crafting;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.family.SimpleWoodFamily;
import net.minecraft.block.family.WoodFamilies;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
@SuppressWarnings("all")
public class WoodCutting {

    public static void registerRecipes(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map) {
        addWoodcuttingRecipe(map, "sticks_from_planks_woodcutting", Ingredient.of(ItemTags.PLANKS), new ItemStack(Items.STICK, 4));

        for (SimpleWoodFamily simpleWoodFamily : List.of(WoodFamilies.PALE_OAK, WoodFamilies.OAK,
                WoodFamilies.ACACIA, WoodFamilies.JUNGLE,
                WoodFamilies.SPRUCE, WoodFamilies.BIRCH,
                WoodFamilies.DARK_OAK, WoodFamilies.CRIMSON, WoodFamilies.WARPED)) {
            addWoodRecipes(map, simpleWoodFamily);
        }

        addWoodcuttingRecipe(map, "item_frame_to_sticks", Ingredient.of(Items.ITEM_FRAME), new ItemStack(Items.STICK, 8));
        addWoodcuttingRecipe(map, "bamboo_to_sticks", Items.BAMBOO, new ItemStack(Items.STICK, 2));
    }


    private static void addWoodRecipes(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map, SimpleWoodFamily family) {
        Block plankblock = family.getPlankBlock().get();
        Block logBlock = family.getLogBlock().get();
        Block strippedLogBlock = family.getStrippedLogBlock().get();
        Block woodBlock = family.getWoodBlock().get();
        Block strippedWoodBlock = family.getStrippedWoodBlock().get();
        String id = family.getWoodName();


        addWoodcuttingRecipe(map,id + "_logs_to_stripped_woodcutting", Ingredient.of(logBlock), strippedLogBlock);

        addWoodcuttingRecipe(map,id + "_logs_to_wood_woodcutting", Ingredient.of(logBlock), woodBlock);
        addWoodcuttingRecipe(map,id + "_wood_to_stripped_woodcutting", Ingredient.of(woodBlock), strippedWoodBlock);

        addWoodcuttingRecipe(map,id + "_logs_to_sticks_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), new ItemStack(Items.STICK, 16));

        addWoodcuttingRecipe(map,id + "_planks_to_stairs_woodcutting", Ingredient.of(plankblock), family.getStairsBlock().get());
        addWoodcuttingRecipe(map,id + "_logs_to_stairs_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), family.getStairsBlock().get(), 4);
        addWoodcuttingRecipe(map,id + "_logs_to_planks_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), plankblock, 4);

        addWoodcuttingRecipe(map,id + "_planks_to_slabs_woodcutting", Ingredient.of(plankblock), family.getSlabBlock().get(), 2);
        addWoodcuttingRecipe(map,id + "_logs_to_slabs_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), family.getSlabBlock().get(), 8);

        addWoodcuttingRecipe(map,id + "_planks_to_fence_woodcutting", Ingredient.of(plankblock), family.getFenceBlock().get(), 3);
        addWoodcuttingRecipe(map,id + "_logs_to_fence_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), family.getFenceBlock().get(), 12);

        addWoodcuttingRecipe(map,id + "_planks_to_fence_gate_woodcutting", Ingredient.of(plankblock), family.getFenceGateBlock().get(), 2);
        addWoodcuttingRecipe(map,id + "_logs_to_fence_gate_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), family.getFenceGateBlock().get(), 8);

        addWoodcuttingRecipe(map,id + "_planks_to_button_woodcutting", Ingredient.of(plankblock), family.getButtonBlock().get(), 8);
        addWoodcuttingRecipe(map,id + "_logs_to_button_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), family.getButtonBlock().get(), 32);

        addWoodcuttingRecipe(map,id + "_logs_to_door_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), family.getDoorBlock().get(), 2);

        addWoodcuttingRecipe(map,id + "_logs_to_trapdoor_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), family.getTrapdoorBlock().get(), 4);
        addWoodcuttingRecipe(map,id + "_planks_to_trapdoor_woodcutting", Ingredient.of(plankblock), family.getTrapdoorBlock().get(), 1);

        addWoodcuttingRecipe(map,id + "_logs_to_pressure_plate_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), family.getPressurePlateBlock().get(), 6 * 4);
        addWoodcuttingRecipe(map,id + "_planks_to_pressure_plate_woodcutting", Ingredient.of(plankblock), family.getPressurePlateBlock().get(), 6);

        addWoodcuttingRecipe(map, id + "_logs_to_signs_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), family.getSignBlock().get(), 4);
        addWoodcuttingRecipe(map, id + "_planks_to_signs_woodcutting",
                Ingredient.of(plankblock), family.getSignBlock().get(), 1);

        addWoodcuttingRecipe(map, id + "_bowl_woodcutting", Ingredient.of(plankblock), new ItemStack(Items.BOWL, 8));
        addWoodcuttingRecipe(map, id + "_logs_bowl_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), new ItemStack(Items.BOWL, 8 * 4));

        addWoodcuttingRecipe(map, id + "_chest_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), new ItemStack(Items.CHEST, 1));

        addWoodcuttingRecipe(map, id + "_barrel_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), new ItemStack(Items.BARREL, 1));

        addWoodcuttingRecipe(map, id + "_crafting_table_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), new ItemStack(Items.CRAFTING_TABLE, 1));

        addWoodcuttingRecipe(map, id + "_sword_woodcutting", Ingredient.of(plankblock), new ItemStack(Items.WOODEN_SWORD));
        addWoodcuttingRecipe(map, id + "_pickaxe_woodcutting", Ingredient.of(plankblock), new ItemStack(Items.WOODEN_PICKAXE));
        addWoodcuttingRecipe(map, id + "_axe_woodcutting", Ingredient.of(plankblock), new ItemStack(Items.WOODEN_AXE));
        addWoodcuttingRecipe(map, id + "_shovel_woodcutting", Ingredient.of(plankblock), new ItemStack(Items.WOODEN_SHOVEL));
        addWoodcuttingRecipe(map, id + "_hoe_woodcutting", Ingredient.of(plankblock), new ItemStack(Items.WOODEN_HOE));
        addWoodcuttingRecipe(map, id + "_sign_to_sticks_woodcutting", Ingredient.of(family.getSignBlock().get()), new ItemStack(Items.STICK, 4));
        addWoodcuttingRecipe(map, id + "_log_to_ladder_woodcutting",
                Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock), new ItemStack(Items.LADDER, 6));
        addWoodcuttingRecipe(map, id + "_plank_to_ladder_woodcutting", Ingredient.of(plankblock), new ItemStack(Items.LADDER, 1));

        if (family.getBoatItem().isPresent()) {
            addWoodcuttingRecipe(map, id + "_boat_woodcutting",
                    Ingredient.of(logBlock, strippedLogBlock, woodBlock, strippedWoodBlock),
                    family.getBoatItem().get().getDefaultInstance());
        }


    }



    private static void addWoodcuttingRecipe(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map,
                                             String recipeName, Ingredient input, ItemStack output) {
        ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);

        WoodcuttingRecipe furnaceRecipe = new WoodcuttingRecipe(recipeId, "", input, output);

        map.computeIfAbsent(IRecipeType.WOODCUTTING, (type) -> ImmutableMap.builder())
                .put(recipeId, furnaceRecipe);
    }

    private static void addWoodcuttingRecipe(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map,
                                             String recipeName, IItemProvider input, ItemStack output) {
        ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);

        WoodcuttingRecipe furnaceRecipe = new WoodcuttingRecipe(recipeId, "", Ingredient.of(input), output);

        map.computeIfAbsent(IRecipeType.WOODCUTTING, (type) -> ImmutableMap.builder())
                .put(recipeId, furnaceRecipe);
    }



    private static void addWoodcuttingRecipe(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map,
            String recipeName, Ingredient input, Block output) {
        ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);

        WoodcuttingRecipe furnaceRecipe = new WoodcuttingRecipe(recipeId, "", input, new ItemStack(output, 1));

        map.computeIfAbsent(IRecipeType.WOODCUTTING, (type) -> ImmutableMap.builder())
                .put(recipeId, furnaceRecipe);
    }

    private static void addWoodcuttingRecipe(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map,
            String recipeName, Ingredient input, Block output, int count) {
        ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);

        WoodcuttingRecipe furnaceRecipe = new WoodcuttingRecipe(recipeId, "", input, new ItemStack(output, count));

        map.computeIfAbsent(IRecipeType.WOODCUTTING, (type) -> ImmutableMap.builder())
                .put(recipeId, furnaceRecipe);
    }
}
