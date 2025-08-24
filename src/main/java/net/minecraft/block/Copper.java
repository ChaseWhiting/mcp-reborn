package net.minecraft.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class Copper {
    public static final Supplier<BiMap<Block, Block>> WAXABLES = Suppliers.memoize(() ->
            ImmutableBiMap.<Block, Block>builder()
                    .put( Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK)
                    .put(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER)
                    .put(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER)
                    .put(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER)
                    .put(Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER)
                    .put(Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER)
                    .put(Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER)
                    .put(Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER)
                    .put(Blocks.CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB)
                    .put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB)
                    .put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB)
                    .put(Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB)

                    .put(Blocks.CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS)
                    .put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS)
                    .put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS)
                    .put(Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS)
                    .put(Blocks.COPPER_TRAPDOOR, Blocks.WAXED_COPPER_TRAPDOOR)
                    .put(Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR)
                    .put(Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR)
                    .put(Blocks.OXIDIZED_COPPER_TRAPDOOR, Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR)

                    .put(Blocks.COPPER_DOOR, Blocks.WAXED_COPPER_DOOR)
                    .put(Blocks.WEATHERED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR)
                    .put(Blocks.EXPOSED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR)
                    .put(Blocks.OXIDIZED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR)


                    .put(Blocks.CHISELED_COPPER, Blocks.WAXED_CHISELED_COPPER)
                    .put(Blocks.WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_CHISELED_COPPER)
                    .put(Blocks.EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_CHISELED_COPPER)
                    .put(Blocks.OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_CHISELED_COPPER)


                    .put(Blocks.COPPER_GRATE, Blocks.WAXED_COPPER_GRATE)
                    .put(Blocks.EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER_GRATE)
                    .put(Blocks.WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER_GRATE)
                    .put(Blocks.OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER_GRATE)
                    .put(Blocks.COPPER_BULB, Blocks.WAXED_COPPER_BULB)
                    .put(Blocks.EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB)
                    .put(Blocks.WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB)
                    .put(Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB).build());



    public static final Supplier<BiMap<Block, Block>> BLOCK_TO_SLAB = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
            .put(Blocks.CUT_COPPER, Blocks.CUT_COPPER_SLAB)
            .put(Blocks.EXPOSED_CUT_COPPER, Blocks.EXPOSED_CUT_COPPER_SLAB)
            .put(Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER_SLAB)
            .put(Blocks.OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER_SLAB)

            .put(Blocks.WAXED_CUT_COPPER, Blocks.WAXED_CUT_COPPER_SLAB)
            .put(Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB)
            .put(Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB)
            .put(Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB).build());

    public static final Supplier<BiMap<Block, Block>> BLOCK_TO_CUT_SLAB = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
            .put(Blocks.COPPER_BLOCK, Blocks.CUT_COPPER_SLAB)
            .put(Blocks.EXPOSED_COPPER, Blocks.EXPOSED_CUT_COPPER_SLAB)
            .put(Blocks.WEATHERED_COPPER, Blocks.WEATHERED_CUT_COPPER_SLAB)
            .put(Blocks.OXIDIZED_COPPER, Blocks.OXIDIZED_CUT_COPPER_SLAB)

            .put(Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_CUT_COPPER_SLAB)
            .put(Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB)
            .put(Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB)
            .put(Blocks.WAXED_OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB).build());

    public static final Supplier<BiMap<Block, Block>> SLAB_TO_CHISELED = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
            .put(Blocks.CUT_COPPER_SLAB, Blocks.CHISELED_COPPER)
            .put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_CHISELED_COPPER)
            .put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CHISELED_COPPER)
            .put(Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CHISELED_COPPER)

            .put(Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_CHISELED_COPPER)
            .put(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CHISELED_COPPER)
            .put(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CHISELED_COPPER)
            .put(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CHISELED_COPPER).build());

    public static final Supplier<BiMap<Block, Block>> COPPER_TO_CHISELED = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
            .put(Blocks.COPPER_BLOCK, Blocks.CHISELED_COPPER)
            .put(Blocks.EXPOSED_COPPER, Blocks.EXPOSED_CHISELED_COPPER)
            .put(Blocks.WEATHERED_COPPER, Blocks.WEATHERED_CHISELED_COPPER)
            .put(Blocks.OXIDIZED_COPPER, Blocks.OXIDIZED_CHISELED_COPPER)

            .put(Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_CHISELED_COPPER)
            .put(Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_EXPOSED_CHISELED_COPPER)
            .put(Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_WEATHERED_CHISELED_COPPER)
            .put(Blocks.WAXED_OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_CHISELED_COPPER).build());

    public static final Supplier<BiMap<Block, Block>> COPPER_TO_CHISELED_S = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
            .put(Blocks.CUT_COPPER, Blocks.CHISELED_COPPER)
            .put(Blocks.EXPOSED_CUT_COPPER, Blocks.EXPOSED_CHISELED_COPPER)
            .put(Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_CHISELED_COPPER)
            .put(Blocks.OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_CHISELED_COPPER)

            .put(Blocks.WAXED_CUT_COPPER, Blocks.WAXED_CHISELED_COPPER)
            .put(Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CHISELED_COPPER)
            .put(Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CHISELED_COPPER)
            .put(Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CHISELED_COPPER).build());

    public static final Supplier<BiMap<Block, Block>> BLOCK_TO_CUT_COPPER = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
            .put(Blocks.COPPER_BLOCK, Blocks.CUT_COPPER)
            .put(Blocks.EXPOSED_COPPER, Blocks.EXPOSED_CUT_COPPER)
            .put(Blocks.WEATHERED_COPPER, Blocks.WEATHERED_CUT_COPPER)
            .put(Blocks.OXIDIZED_COPPER, Blocks.OXIDIZED_CUT_COPPER)

            .put(Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_CUT_COPPER)
            .put(Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER)
            .put(Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER)
            .put(Blocks.WAXED_OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER).build());

    public static final Supplier<BiMap<Block, Block>> BLOCK_TO_BULB = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
            .put(Blocks.COPPER_BLOCK, Blocks.COPPER_BULB)
            .put(Blocks.EXPOSED_COPPER, Blocks.EXPOSED_COPPER_BULB)
            .put(Blocks.WEATHERED_COPPER, Blocks.WEATHERED_COPPER_BULB)
            .put(Blocks.OXIDIZED_COPPER, Blocks.OXIDIZED_COPPER_BULB)

            .put(Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_COPPER_BULB)
            .put(Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER_BULB)
            .put(Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER_BULB)
            .put(Blocks.WAXED_OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER_BULB).build());

    public static final Supplier<BiMap<Block, Block>> BLOCK_TO_GRATE = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
            .put(Blocks.COPPER_BLOCK, Blocks.COPPER_GRATE)
            .put(Blocks.EXPOSED_COPPER, Blocks.EXPOSED_COPPER_GRATE)
            .put(Blocks.WEATHERED_COPPER, Blocks.WEATHERED_COPPER_GRATE)
            .put(Blocks.OXIDIZED_COPPER, Blocks.OXIDIZED_COPPER_GRATE)

            .put(Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_COPPER_GRATE)
            .put(Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER_GRATE)
            .put(Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER_GRATE)
            .put(Blocks.WAXED_OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER_GRATE).build());

    public static final Supplier<BiMap<Block, Block>> BLOCK_TO_STAIR = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
            .put(Blocks.CUT_COPPER, Blocks.CUT_COPPER_STAIRS)
            .put(Blocks.EXPOSED_CUT_COPPER, Blocks.EXPOSED_CUT_COPPER_STAIRS)
            .put(Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER_STAIRS)
            .put(Blocks.OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER_STAIRS)

            .put(Blocks.WAXED_CUT_COPPER, Blocks.WAXED_CUT_COPPER_STAIRS)
            .put(Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS)
            .put(Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS)
            .put(Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS).build());

    public static final Supplier<BiMap<Block, Block>> COPPER_BLOCK_TO_STAIR = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
            .put(Blocks.COPPER_BLOCK, Blocks.CUT_COPPER_STAIRS)
            .put(Blocks.EXPOSED_COPPER, Blocks.EXPOSED_CUT_COPPER_STAIRS)
            .put(Blocks.WEATHERED_COPPER, Blocks.WEATHERED_CUT_COPPER_STAIRS)
            .put(Blocks.OXIDIZED_COPPER, Blocks.OXIDIZED_CUT_COPPER_STAIRS)

            .put(Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_CUT_COPPER_STAIRS)
            .put(Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS)
            .put(Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS)
            .put(Blocks.WAXED_OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS).build());

    public static List<Block> COPPER_TRAPDOOR_STATES = List.of(Blocks.COPPER_TRAPDOOR, Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.OXIDIZED_COPPER_TRAPDOOR);
    public static List<Block> WAXED_COPPER_TRAPDOOR_STATES = List.of(Blocks.WAXED_COPPER_TRAPDOOR, Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);

    public static List<Block> COPPER_STATES = List.of(Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER);
    public static List<Block> WAXED_COPPER_STATES = List.of(Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_OXIDIZED_COPPER);
    public static List<Block> CUT_COPPER_STATES = List.of(Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER);
    public static List<Block> WAXED_CUT_COPPER_STATES = List.of(Blocks.WAXED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER);
    public static List<Block> CUT_COPPER_SLAB_STATES = List.of(Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB);
    public static List<Block> WAXED_CUT_COPPER_SLAB_STATES = List.of(Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB);
    public static List<Block> COPPER_GRATE_STATES = List.of(Blocks.COPPER_GRATE, Blocks.EXPOSED_COPPER_GRATE, Blocks.WEATHERED_COPPER_GRATE, Blocks.OXIDIZED_COPPER_GRATE);
    public static List<Block> WAXED_COPPER_GRATE_STATES = List.of(Blocks.WAXED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER_GRATE);
    public static List<Block> COPPER_BULB_STATES = List.of(Blocks.COPPER_BULB, Blocks.EXPOSED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB, Blocks.OXIDIZED_COPPER_BULB);
    public static List<Block> WAXED_COPPER_BULB_STATES = List.of(Blocks.WAXED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB);
    public static List<Block> CUT_COPPER_STAIR_STATES = List.of(Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS);
    public static List<Block> WAXED_CUT_COPPER_STAIR_STATES = List.of(Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
    public static List<Block> UNWAXED_STATES = List.of(Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER,Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER,Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB,Blocks.COPPER_GRATE, Blocks.EXPOSED_COPPER_GRATE, Blocks.WEATHERED_COPPER_GRATE, Blocks.OXIDIZED_COPPER_GRATE,Blocks.COPPER_BULB, Blocks.EXPOSED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB, Blocks.OXIDIZED_COPPER_BULB,Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.COPPER_DOOR, Blocks.EXPOSED_COPPER_DOOR, Blocks.WEATHERED_COPPER_DOOR, Blocks.OXIDIZED_COPPER_DOOR, Blocks.COPPER_TRAPDOOR, Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.OXIDIZED_COPPER_TRAPDOOR);
    public static List<Block> COPPER_DOOR_STATES = List.of(Blocks.COPPER_DOOR, Blocks.EXPOSED_COPPER_DOOR, Blocks.WEATHERED_COPPER_DOOR, Blocks.OXIDIZED_COPPER_DOOR);
    public static List<Block> WAXED_COPPER_DOOR_STATES = List.of(Blocks.WAXED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR);


    public static ItemStack getSlab(Block block) {
        Item item = BLOCK_TO_SLAB.get().get(block).asItem();
        return new ItemStack(item, 6);
    }

    public static ItemStack getCutSlab(Block block) {
        Item item = BLOCK_TO_SLAB.get().get(block).asItem();
        return new ItemStack(item, 2);
    }

    public static ItemStack get8Slab(Block block) {
        Item item = BLOCK_TO_CUT_SLAB.get().get(block).asItem();
        return new ItemStack(item, 8);
    }

    public static ItemStack getStair(Block block) {
        Item item = BLOCK_TO_STAIR.get().get(block).asItem();
        return new ItemStack(item, 4);
    }

    public static ItemStack getCutStair(Block block) {
        Item item = BLOCK_TO_STAIR.get().get(block).asItem();
        return new ItemStack(item, 1);
    }

    public static ItemStack getCopperStair(Block block) {
        Item item = COPPER_BLOCK_TO_STAIR.get().get(block).asItem();
        return new ItemStack(item, 4);
    }

    public static ItemStack getWaxed(Block block) {
        Item item = WAXABLES.get().get(block).asItem();
        return new ItemStack(item, 1);
    }

    public static ItemStack getCutCopper(Block block) {
        Item item = BLOCK_TO_CUT_COPPER.get().get(block).asItem();
        return new ItemStack(item, 4);
    }

    public static ItemStack getGrate(Block block) {
        Item item = BLOCK_TO_GRATE.get().get(block).asItem();
        return new ItemStack(item, 4);
    }

    public static ItemStack getBulb(Block block) {
        Item item = BLOCK_TO_BULB.get().get(block).asItem();
        return new ItemStack(item, 4);
    }

    public static ItemStack getChiseled(Block block) {
        Item item = SLAB_TO_CHISELED.get().get(block).asItem();
        return new ItemStack(item, 1);
    }

    public static ItemStack getCopperChiseled(Block block) {
        Item item = COPPER_TO_CHISELED.get().get(block).asItem();
        return new ItemStack(item, 4);
    }

    public static ItemStack getCutChiseled(Block block) {
        Item item = COPPER_TO_CHISELED_S.get().get(block).asItem();
        return new ItemStack(item, 1);
    }

    public static String resourceCraftingName(Block block) {
        ResourceLocation l = Registry.BLOCK.getKey(block);
        return l.toString().replace("minecraft:", "crafting_");
    }

    public static void registerRecipes(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map) {
        for (Block block : UNWAXED_STATES) {
            addShapelessRecipe(map, resourceCraftingName(block) + "_honeycomb",
                    getWaxed(block),
                    Ingredient.of(block),
                    Ingredient.of(Items.HONEYCOMB));
        }

        for (Block block : CUT_COPPER_STATES) {
            addShapedRecipe(3, 1, map, resourceCraftingName(block), getSlab(block),
                    new String[]{
                            "###"
                    },
                    Map.of('#', Ingredient.of(block)));
        }
        for (Block block : WAXED_CUT_COPPER_STATES) {
            addShapedRecipe(3, 1, map, resourceCraftingName(block), getSlab(block),
                    new String[]{
                            "###"
                    },
                    Map.of('#', Ingredient.of(block)));
        }
        for (Block block : COPPER_STATES) {
            addShapedRecipe(3, 3, map, resourceCraftingName(block), getBulb(block),
                    new String[]{
                            " # ",
                            "#X#",
                            " R "
                    },
                    Map.of('#', Ingredient.of(block),
                            'X', Ingredient.of(Items.BLAZE_ROD),
                            'R', Ingredient.of(Items.REDSTONE)));
        }
        for (Block block : COPPER_STATES) {
            addShapedRecipe(3, 3, map, resourceCraftingName(block) + "_grate", getGrate(block),
                    new String[]{
                            " # ",
                            "# #",
                            " # "
                    },
                    Map.of('#', Ingredient.of(block)));
        }
        for (Block block : CUT_COPPER_STATES) {
            addShapedRecipe(3, 3, map, resourceCraftingName(block) + "_stair", getStair(block),
                    new String[]{
                            "#  ",
                            "## ",
                            "###"
                    },
                    Map.of('#', Ingredient.of(block)));
        }
        for (Block block : WAXED_CUT_COPPER_STATES) {
            addShapedRecipe(3, 3, map, resourceCraftingName(block) + "_stair", getStair(block),
                    new String[]{
                            "#  ",
                            "## ",
                            "###"
                    },
                    Map.of('#', Ingredient.of(block)));
        }
        for (Block block : WAXED_COPPER_STATES) {
            addShapedRecipe(3, 3, map, resourceCraftingName(block) + "_grate", getGrate(block),
                    new String[]{
                            " # ",
                            "# #",
                            " # "
                    },
                    Map.of('#', Ingredient.of(block)));
        }
        for (Block block : COPPER_STATES) {
            addShapedRecipe(2, 2, map, resourceCraftingName(block) + "cut", getCutCopper(block),
                    new String[]{
                            "##",
                            "##"
                    },
                    Map.of('#', Ingredient.of(block)));
        }
        for (Block block : WAXED_COPPER_STATES) {
            addShapedRecipe(2, 2, map, resourceCraftingName(block) + "cut", getCutCopper(block),
                    new String[]{
                            "##",
                            "##"
                    },
                    Map.of('#', Ingredient.of(block)));
        }
        for (Block block : WAXED_COPPER_STATES) {
            addShapedRecipe(3, 3, map, resourceCraftingName(block), getBulb(block),
                    new String[]{
                            " # ",
                            "#X#",
                            " R "
                    },
                    Map.of('#', Ingredient.of(block),
                            'X', Ingredient.of(Items.BLAZE_ROD),
                            'R', Ingredient.of(Items.REDSTONE)));
        }
        for (Block block : CUT_COPPER_SLAB_STATES) {
            addShapedRecipe(1, 2, map, resourceCraftingName(block) + "_chiseled", getChiseled(block),
                    new String[]{
                            "#",
                            "#"
                    },
                    Map.of('#', Ingredient.of(block)));
        }
        for (Block block : WAXED_CUT_COPPER_SLAB_STATES) {
            addShapedRecipe(1, 2, map, resourceCraftingName(block) + "_chiseled", getChiseled(block),
                    new String[]{
                            "#",
                            "#"
                    },
                    Map.of('#', Ingredient.of(block)));
        }

        addShapedRecipe(2, 3, map, resourceCraftingName(Blocks.COPPER_DOOR), new ItemStack(Items.COPPER_DOOR, 3),
                new String[]{
                        "##",
                        "##",
                        "##"
                },
                Map.of('#', Ingredient.of(Items.COPPER_INGOT)));
        addShapedRecipe(3, 3, map, "copper_block_crafting", new ItemStack(Items.COPPER_BLOCK, 1),
                new String[]{
                        "###",
                        "###",
                        "###"
                },
                Map.of('#', Ingredient.of(Items.COPPER_INGOT)));
        addShapedRecipe(3, 2, map, "copper_trapdoor_crafting", new ItemStack(Blocks.COPPER_TRAPDOOR, 4),
                new String[]{
                        "###",
                        "###"
                },
                Map.of('#', Ingredient.of(Items.COPPER_INGOT)));
        addShapedRecipe(1, 3, map, "lightning_rod", new ItemStack(Items.LIGHTNING_ROD, 1),
                new String[]{
                        "#",
                        "#",
                        "#"
                },
                Map.of('#', Ingredient.of(Items.COPPER_INGOT)));

        for (Block block : COPPER_STATES) {
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_grate", Ingredient.of(block), getGrate(block));
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_cut_copper", Ingredient.of(block), getCutCopper(block));
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_copper_stair", Ingredient.of(block), getCopperStair(block));
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_cut_copper_slab", Ingredient.of(block), get8Slab(block));
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_chiseled_copper", Ingredient.of(block), getCopperChiseled(block));

        }
        for (Block block : WAXED_COPPER_STATES) {
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_waxed_grate", Ingredient.of(block), getGrate(block));
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_waxed_cut_copper", Ingredient.of(block), getCutCopper(block));
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_waxed_copper_stair", Ingredient.of(block), getCopperStair(block));
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_waxed_cut_copper_slab", Ingredient.of(block), get8Slab(block));
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_waxed_chiseled_copper", Ingredient.of(block), getCopperChiseled(block));

        }
        for (Block block : CUT_COPPER_STATES) {
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_copper_stair", Ingredient.of(block), getCutStair(block));
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_cut_copper_slab", Ingredient.of(block), getCutSlab(block));
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_chiseled_copper", Ingredient.of(block), getCutChiseled(block));
        }
        for (Block block : WAXED_CUT_COPPER_STATES) {
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_waxed_copper_stair", Ingredient.of(block), getCutStair(block));
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_waxed_cut_copper_slab", Ingredient.of(block), getCutSlab(block));
            addStonecuttingRecipe(map, resourceCraftingName(block) + "stonecutting_waxed_chiseled_copper", Ingredient.of(block), getCutChiseled(block));
        }
    }


    private static void addShapedRecipe(int width, int height, Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map,
                                        String recipeName, ItemStack result, String[] pattern, Map<Character, Ingredient> key) {
        ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);
        NonNullList<Ingredient> ingredients = NonNullList.withSize(3 * 3, Ingredient.EMPTY);
        int index = 0;
        for (String row : pattern) {
            for (char symbol : row.toCharArray()) {
                Ingredient ingredient = key.get(symbol);
                ingredients.set(index, ingredient != null ? ingredient : Ingredient.EMPTY);
                index++;
            }
        }
        ShapedRecipe shapedRecipe = new ShapedRecipe(recipeId, "", width, height, ingredients, result);
        map.computeIfAbsent(IRecipeType.CRAFTING, (type) -> ImmutableMap.builder())
                .put(recipeId, shapedRecipe);
    }


    private static void addShapelessRecipe(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map,
                                           String recipeName, ItemStack result, Ingredient... ingredients) {
        ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);

        NonNullList<Ingredient> ingredientList = NonNullList.create();
        Collections.addAll(ingredientList, ingredients);

        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(recipeId, "", result, ingredientList);

        map.computeIfAbsent(IRecipeType.CRAFTING, (type) -> ImmutableMap.builder())
                .put(recipeId, shapelessRecipe);
    }

    private static void addStonecuttingRecipe(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map,
                                              String recipeName, Ingredient input, ItemStack output) {
        ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);

        StonecuttingRecipe furnaceRecipe = new StonecuttingRecipe(recipeId, "", input, output);

        map.computeIfAbsent(IRecipeType.STONECUTTING, (type) -> ImmutableMap.builder())
                .put(recipeId, furnaceRecipe);
    }
}
