package net.minecraft.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.Copper;
import net.minecraft.block.family.SimpleWoodFamily;
import net.minecraft.block.family.WoodFamilies;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.item.dyeable.IDyeableBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager extends JsonReloadListener {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private static final Logger LOGGER = LogManager.getLogger();
   private Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes = ImmutableMap.of();
   private boolean hasErrors;

   public RecipeManager() {
      super(GSON, "recipes");
   }


   public static final List<Pair<Set<Item>, List<String>>> ITEM_UNLOCK_RECIPE_MAP = Util.make(new ArrayList<>(), list -> {

      list.add(Pair.of(Set.of(Items.WITHER_BONE, Items.FORTIFIED_WITHER_BONE), List.of("wither_helmet", "wither_chestplate", "wither_leggings", "wither_boots")));
      list.add(Pair.of(Set.of(Items.WITHER_BONE), List.of("withered_bones_coal", "wither_cutlass")));
      list.add(Pair.of(Set.of(Items.FORTIFIED_WITHER_BONE), List.of("fortified_bones_coal")));
      list.add(Pair.of(Set.of(Items.WITHER_SKELETON_RIBCAGE), List.of("wither_chestplate", "fortified_bones_from_ribcage")));

      list.add(Pair.of(Set.of(Items.RAW_GOLD, Items.RAW_COPPER), List.of("craft_raw_rose_gold")));
      list.add(Pair.of(Set.of(Items.RAW_ROSE_GOLD), List.of("rose_gold_ingot_raw")));
      list.add(Pair.of(Set.of(Items.ROSE_GOLD_INGOT), List.of("rose_gold_sword", "rose_gold_pickaxe", "rose_gold_axe", "rose_gold_shovel", "rose_gold_hoe")));

   });

   @Override
   protected void apply(Map<ResourceLocation, JsonElement> jsonElements, IResourceManager resourceManager, IProfiler profiler) {
      this.hasErrors = false;
      Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map = Maps.newHashMap();


      for (Entry<ResourceLocation, JsonElement> entry : jsonElements.entrySet()) {
         ResourceLocation resourcelocation = entry.getKey();
         try {
            IRecipe<?> irecipe = fromJson(resourcelocation, JSONUtils.convertToJsonObject(entry.getValue(), "top element"));
            map.computeIfAbsent(irecipe.getType(), (type) -> ImmutableMap.builder())
                    .put(resourcelocation, irecipe);
         } catch (IllegalArgumentException | JsonParseException e) {
            LOGGER.error("Parsing error loading recipe {}", resourcelocation, e);
         }
      }

      addHardcodedShapelessRecipes(map);

      addSpecialRecipe(map, new BundleColourChangeRecipe(new ResourceLocation("minecraft", "bundle_dye")));
      addSpecialRecipe(map, new DecoratedPotRecipe(new ResourceLocation("minecraft", "decorated_pot")));


      // === Load custom recipes from fixed directory ===
      // CHANGE THIS PATH TO YOUR ACTUAL FOLDER
      String customRecipeDirPath = "G:\\MCP-Reborn-1.16-MOJO\\src\\main\\resources\\data\\minecraft\\customRecipes";
      File customRecipeDir = new File(customRecipeDirPath);

      if (customRecipeDir.exists() && customRecipeDir.isDirectory()) {
         try (Stream<Path> paths = Files.walk(customRecipeDir.toPath())) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                       try (FileReader reader = new FileReader(path.toFile())) {
                          JsonElement json = JSONUtils.fromJson(GSON, reader, JsonElement.class);

                          // Build a ResourceLocation based on relative path inside 'customRecipes'
                          String relPath = customRecipeDir.toPath().relativize(path).toString().replace(File.separatorChar, '/');
                          String recipeName = relPath.substring(0, relPath.length() - ".json".length());
                          ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);

                          // Do not overwrite existing recipes from datapacks/vanilla
                          if (!jsonElements.containsKey(recipeId)) {
                             IRecipe<?> recipe = fromJson(recipeId, JSONUtils.convertToJsonObject(json, "top element"));
                             map.computeIfAbsent(recipe.getType(), type -> ImmutableMap.builder())
                                     .put(recipeId, recipe);
                             LOGGER.info("Loaded custom recipe: {}", recipeId);
                          } else {
                             LOGGER.warn("Custom recipe '{}' skipped (already exists from datapack/vanilla)", recipeId);
                          }
                       } catch (Exception e) {
                          LOGGER.error("Error loading custom recipe from file {}", path.toString(), e);
                          this.hasErrors = true;
                       }
                    });
         } catch (IOException e) {
            LOGGER.error("Error walking custom recipes directory {}", customRecipeDirPath, e);
            this.hasErrors = true;
         }
      } else {
         LOGGER.warn("Custom recipe directory '{}' does not exist or is not a directory.", customRecipeDirPath);
      }

      this.recipes = map.entrySet().stream()
              .collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> entry.getValue().build()));



      LOGGER.info("Loaded {} recipes (including hardcoded recipes)", (int) map.size());
   }

   private void addSpecialRecipe(Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map, SpecialRecipe recipe) {
      map.computeIfAbsent(IRecipeType.CRAFTING, (type) -> ImmutableMap.builder())
              .put(recipe.getId(), recipe);
   }

   private void addHardcodedShapelessRecipes(Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map) {
      addShapedRecipe(3, 3, map, "tinted_glass", new ItemStack(Items.TINTED_GLASS, 2), new String[]
              {
                      " C ",
                      "CGC",
                      " C "
              }, Map.of('C', Ingredient.of(Items.AMETHYST_SHARD), 'G', Ingredient.of(Items.GLASS)));


      addShapelessRecipe(map, "yellow_dye_from_wildflowers",
              new ItemStack(Items.YELLOW_DYE, 1),
              Ingredient.of(Items.WILDFLOWERS));
      addShapelessRecipe(map, "create_drumsticks",
              new ItemStack(Items.DRUMSTICK, 2),
              Ingredient.of(Items.CHICKEN),
              Ingredient.of(Items.CUTTING_KNIFE));

      addShapelessRecipe(map, "fried_egg",
              new ItemStack(Items.FRIED_EGG, 1),
              Item.EGGS,
              Ingredient.of(Items.HEATED_FRYING_PAN));
      addShapelessRecipe(map, "fried_egg_double",
              new ItemStack(Items.FRIED_EGGS_DOUBLE, 1),
              Item.EGGS,
              Item.EGGS,
              Ingredient.of(Items.HEATED_FRYING_PAN));
      addShapelessRecipe(map, "fried_egg_triple",
              new ItemStack(Items.FRIED_EGGS_TRIPLE, 1),
              Item.EGGS,
              Item.EGGS,
              Item.EGGS,
              Ingredient.of(Items.HEATED_FRYING_PAN));
      addPaintingRecipes(map);

      addShapedRecipe(3, 3, map, "wither_cutlass", new ItemStack(Items.WITHER_BONE_CUTLASS), new String[]{
              " IF",
              "BCI",
              "BB "
      }, Map.of('I', Ingredient.of(Items.IRON_NUGGET), 'C', Ingredient.of(Items.CHARCOAL), 'F', Ingredient.of(Items.FLINT), 'B', Ingredient.of(Items.WITHER_BONE)));

      addShapelessRecipe(map, "resin_block_to_resin",
              new ItemStack(Items.RESIN_CLUMP, 9),
              Ingredient.of(Blocks.RESIN_BLOCK.asItem()));
      addShapedRecipe(3, 3,
              map,
              "resin_clump_to_block",
              new ItemStack(Items.RESIN_BLOCK, 1),
              new String[]{
                      "###",
                      "###",
                      "###"
              },
              Map.of('#', Ingredient.of(Items.RESIN_CLUMP))
      );



      addShapelessRecipe(map, "fortified_bones_from_ribcage",
              new ItemStack(Items.FORTIFIED_WITHER_BONE, 3),
              Ingredient.of(Items.WITHER_SKELETON_RIBCAGE));

      addShapelessRecipe(map, "fortified_bones_coal",
              new ItemStack(Items.COAL, 4),
              Ingredient.of(Items.FORTIFIED_WITHER_BONE));

      addShapelessRecipe(map, "withered_bones_coal",
              new ItemStack(Items.COAL, 2),
              Ingredient.of(Items.WITHER_BONE));
//      addShapedRecipe(3, 3, map, "wither_chestplate", new ItemStack(Items.WITHER_BONE_CHESTPLATE), new String[]{
//              "F F",
//              "FCF",
//              "BAB"
//      }, Map.of('A', Ingredient.of(Items.NETHERITE_SCRAP), 'C', Ingredient.of(Items.WITHER_SKELETON_RIBCAGE), 'F', Ingredient.of(Items.FORTIFIED_WITHER_BONE), 'B', Ingredient.of(Items.WITHER_BONE)));

      addShapedRecipe(3, 3, map, "wither_leggings", new ItemStack(Items.WITHER_BONE_LEGGINGS), new String[]{
              "FFF",
              "BAB",
              "F F"
      }, Map.of('A', Ingredient.of(Items.NETHERITE_SCRAP),'F', Ingredient.of(Items.FORTIFIED_WITHER_BONE), 'B', Ingredient.of(Items.WITHER_BONE)));

      addShapedRecipe(3, 2, map, "wither_boots", new ItemStack(Items.WITHER_BONE_BOOTS), new String[]{
              "BAB",
              "FAF"
      }, Map.of('A', Ingredient.of(Items.NETHERITE_SCRAP),'F', Ingredient.of(Items.FORTIFIED_WITHER_BONE), 'B', Ingredient.of(Items.WITHER_BONE)));

      addShapedRecipe(3, 3, map, "wither_helmet", new ItemStack(Items.WITHER_BONE_HELMET), new String[]{
              "FAF",
              "BFB",
              "B B"
      }, Map.of('A', Ingredient.of(Items.NETHERITE_SCRAP),'F', Ingredient.of(Items.FORTIFIED_WITHER_BONE), 'B', Ingredient.of(Items.WITHER_BONE)));


      addShapedRecipe(1, 2,
              map,
              "bundle",
              new ItemStack(Items.BUNDLE, 1),
              new String[]{
                      "#",
                      "X"
              },
              Map.of('#', Ingredient.of(Items.STRING),
                      'X', Ingredient.of(Items.LEATHER))
      );

      addShapedRecipe(3, 3,
              map,
              "fleeting_arrow",
              new ItemStack(Items.FLEETING_ARROW, 4),
              new String[]{
                      " Z ",
                      "AXA",
                      " A "
              },
              Map.of('X', Ingredient.of(Items.STICK),
                      'Z', Ingredient.of(Items.FLINT),
                      'A', Ingredient.of(Items.ROADRUNNER_FEATHER))
      );
      addShapedRecipe(3, 3,
              map,
              "meep_arrow",
              new ItemStack(Items.MEEP_ARROW, 4),
              new String[]{
                      " Z ",
                      "AXA",
                      " A "
              },
              Map.of('X', Ingredient.of(Items.STICK),
                      'Z', Ingredient.of(Items.FLINT),
                      'A', Ingredient.of(Items.MEEP_FEATHER))
      );
      addShapedRecipe(3, 3,
              map,
              "flower_crown",
              new ItemStack(Items.FLOWER_CROWN, 1),
              new String[]{
                      "Z#Z",
                      "#X#",
                      "Z#Z"
              },
              Map.of('#', Ingredient.of(ItemTags.SMALL_FLOWERS),
                      'X', Ingredient.of(Items.CREAKING_HEART_ITEM),
                      'Z', Ingredient.of(Items.GRASS))
      );
      addShapedRecipe(1, 3,
              map,
              "cutting_knife",
              new ItemStack(Items.CUTTING_KNIFE, 1),
              new String[]{
                      "#",
                      "Z",
                      "X"
              },
              Map.of('#', Ingredient.of(Items.IRON_NUGGET),
                      'X', Ingredient.of(Items.STICK),
                      'Z', Ingredient.of(Items.IRON_INGOT))
      );
      addShapedRecipe(3, 3,
              map,
              "frying_pan",
              new ItemStack(Items.FRYING_PAN, 1),
              new String[]{
                      "###",
                      "###",
                      " X "
              },
              Map.of('#', Ingredient.of(Items.IRON_INGOT),
                      'X', Ingredient.of(Items.STICK))
      );

      addShapedRecipe(3, 2,
              map,
              "turtle_helmet_from_leather",
              new ItemStack(Items.TURTLE_HELMET, 1),
              new String[]{
                      "###",
                      "X X"
              },
              Map.of('#', Ingredient.of(Items.SCUTE),
                      'X', Ingredient.of(Items.LEATHER))
      );

      addShapedRecipe(3, 3,
              map,
              "turtle_leggings",
           new ItemStack(Items.TURTLE_LEGGINGS, 1),
              new String[]{
                      "XZX",
                      "# #",
                      "# #"
              },
              Map.of('#', Ingredient.of(Items.SCUTE),
                      'X', Ingredient.of(Items.LEATHER),
                      'Z', Ingredient.of(Items.TOASTED_SCUTE))
      );
      addShapedRecipe(3, 2,
              map,
              "turtle_boots",
              new ItemStack(Items.TURTLE_BOOTS, 1),
              new String[]{
                      "# #",
                      "# #"
              },
              Map.of('#', Ingredient.of(Items.SCUTE))
      );
      addShapedRecipe(3, 3,
              map,
              "turtle_chestplate",
              new ItemStack(Items.TURTLE_CHESTPLATE, 1),
              new String[]{
                      "X X",
                      "#Z#",
                      "###"
              },
              Map.of('#', Ingredient.of(Items.SCUTE),
                      'X', Ingredient.of(Items.TURTLE_HELMET),
                      'Z', Ingredient.of(Items.TOASTED_SCUTE))
      );

      addFurnaceRecipe(
              map,
              "toasted_scute",
              Ingredient.of(Items.SCUTE),
              new ItemStack(Items.TOASTED_SCUTE, 1),
              0.2f, 200
      );


      addShapedRecipe(3, 3,
              map,
              "dead_leaves",
              new ItemStack(Items.DEAD_LEAVES, 1),
              new String[]{
                      "###",
                      "###",
                      "###"
              },
              Map.of('#', Ingredient.of(Items.LEAF_LITTER))
      );

      addFurnaceRecipe(
              map,
              "leaf_litter_from_leaves",
              Ingredient.of(ItemTags.LEAVES),
              new ItemStack(Items.LEAF_LITTER, 1),
              0.7f, 200
      );
      addFurnaceRecipe(
              map,
              "cooked_drumstick",
              Ingredient.of(Items.DRUMSTICK),
              new ItemStack(Items.COOKED_DRUMSTICK, 1),
              0.35f, 200
      );
      addSmokingRecipe(
              map,
              "cooked_drumstick_smoker",
              Ingredient.of(Items.DRUMSTICK),
              new ItemStack(Items.COOKED_DRUMSTICK, 1),
              0.35f
      );
      addFurnaceRecipe(
              map,
              "frying_pan_heated",
              Ingredient.of(Items.FRYING_PAN),
              new ItemStack(Items.HEATED_FRYING_PAN, 1),
              0f, 7 * 20
      );


      createPaintTubeRecipe("white", Items.WHITE_DYE, Items.WHITE_PAINT_TUBE, map);
      createPaintTubeRecipe("orange", Items.ORANGE_DYE, Items.ORANGE_PAINT_TUBE, map);
      createPaintTubeRecipe("magenta", Items.MAGENTA_DYE, Items.MAGENTA_PAINT_TUBE, map);
      createPaintTubeRecipe("light_blue", Items.LIGHT_BLUE_DYE, Items.LIGHT_BLUE_PAINT_TUBE, map);
      createPaintTubeRecipe("yellow", Items.YELLOW_DYE, Items.YELLOW_PAINT_TUBE, map);
      createPaintTubeRecipe("lime", Items.LIME_DYE, Items.LIME_PAINT_TUBE, map);
      createPaintTubeRecipe("pink", Items.PINK_DYE, Items.PINK_PAINT_TUBE, map);
      createPaintTubeRecipe("gray", Items.GRAY_DYE, Items.GRAY_PAINT_TUBE, map);
      createPaintTubeRecipe("light_gray", Items.LIGHT_GRAY_DYE, Items.LIGHT_GRAY_PAINT_TUBE, map);
      createPaintTubeRecipe("cyan", Items.CYAN_DYE, Items.CYAN_PAINT_TUBE, map);
      createPaintTubeRecipe("purple", Items.PURPLE_DYE, Items.PURPLE_PAINT_TUBE, map);
      createPaintTubeRecipe("blue", Items.BLUE_DYE, Items.BLUE_PAINT_TUBE, map);
      createPaintTubeRecipe("brown", Items.BROWN_DYE, Items.BROWN_PAINT_TUBE, map);
      createPaintTubeRecipe("green", Items.GREEN_DYE, Items.GREEN_PAINT_TUBE, map);
      createPaintTubeRecipe("red", Items.RED_DYE, Items.RED_PAINT_TUBE, map);
      createPaintTubeRecipe("black", Items.BLACK_DYE, Items.BLACK_PAINT_TUBE, map);


      addCopperRecipes(map);

      addWoodFamilyRecipe(map, WoodFamilies.PALE_OAK);

      WoodCutting.registerRecipes(map);
      addShapedRecipe(3, 3, map, "woodcutter_block_recipe", new ItemStack(Items.WOODCUTTER), new String[]{
              " I ",
              "IBI",
              "WCW",
      }, Map.of('I', Ingredient.of(Items.IRON_INGOT),
              'B', Ingredient.of(Items.IRON_BLOCK),
              'W', Ingredient.of(ItemTags.LOGS_THAT_BURN),
              'C', Ingredient.of(Items.COBBLESTONE)));

      addShapedRecipe(1, 2, map,
              "candle",
              new ItemStack(Items.CANDLE),
              new String[]{
                      "S",
                      "H"
              },
              Map.of('S', Ingredient.of(Items.STRING),
                      'H', Ingredient.of(Items.HONEYCOMB)));
      for (Item item : Registry.BLOCK.stream().filter(block -> block instanceof CandleBlock).map(Block::asItem).toList()) {

         for (DyeColor dyeColor : DyeColor.values()) {
            if (IDyeableBlock.canAcceptDye(dyeColor, (IDyeableBlock)((BlockItem)item).getBlock())) {
               Item candleColouredStack = Registry.ITEM.get(new ResourceLocation("minecraft", dyeColor.getName() + "_candle"));
               addShapelessRecipe(map,
                       item.getDescriptionId().replace("block.minecraft.", "") + "_recipe_dye_" + dyeColor.getName(),
                       new ItemStack(candleColouredStack, 1),
                       Ingredient.of(DyeItem.byColor(dyeColor)),
                       Ingredient.of(Registry.ITEM.stream().filter(item1 -> item1 instanceof BlockItem blockItem && blockItem.getBlock() instanceof CandleBlock && item1 != candleColouredStack).map(ItemStack::new))
               );
            }

         }

      }

      for (HarnessItem harnessItem : Registry.ITEM.stream().filter(item -> item instanceof HarnessItem).map(this::castToHarness).toList()) {
         addShapedRecipe(3, 2, map, "harness_" + harnessItem.getColor().getName() + "_recipe",
                 new ItemStack(harnessItem), new String[]{
                         "LLL",
                         "GWG",
                 }, Map.of('L', Ingredient.of(Items.LEATHER), 'G', Ingredient.of(Items.GLASS), 'W', Ingredient.of(harnessItem.getWoolColorBlock())));

         for (DyeColor dyeColor : DyeColor.values()) {
            if (dyeColor == harnessItem.getColor()) continue;

            String recipeName = harnessItem.getColor().getName() + "_harness_to_" + dyeColor.getName() + "_recipe";
            HarnessItem newHarness = castToHarness(Registry.ITEM.get(ResourceLocation.withDefaultNamespace(dyeColor.getName() + "_harness")));

            addShapelessRecipe(map, recipeName, newHarness.getDefaultInstance().copyWithCount(1), Ingredient.of(harnessItem), Ingredient.of(DyeItem.byColor(dyeColor)));
         }
      }
   }

   public HarnessItem castToHarness(Item item) {
      return (HarnessItem) item;
   }

   private static void addWoodFamilyRecipe(Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map, SimpleWoodFamily woodFamily) {
      woodFamily.getLogBlock().ifPresent(logBlock -> {
         woodFamily.getPlankBlock().ifPresent(plankBlock -> {
            addShapelessRecipe(map, woodFamily.getWoodName() + "_planks_from_log", new ItemStack(plankBlock.asItem(), 4), Ingredient.of(logBlock.asItem()));
         });

         woodFamily.getWoodBlock().ifPresent(woodBlock -> {
            addShapedRecipe(2, 2,map, woodFamily.getWoodName() + "_wood_from_logs",
                    new ItemStack(woodBlock, 3),
                    new String[]{
                            "##",
                            "##"
                    },
                    Map.of('#', Ingredient.of(logBlock.asItem())));

            woodFamily.getPlankBlock().ifPresent(plankBlock -> {
               addShapelessRecipe(map, woodFamily.getWoodName() + "_planks_from_wood", new ItemStack(plankBlock.asItem(), 4), Ingredient.of(woodBlock.asItem()));
            });


         });
      });

      woodFamily.getStrippedLogBlock().ifPresent(strippedLogBlock -> {
         woodFamily.getPlankBlock().ifPresent(plankBlock -> {
            addShapelessRecipe(map, woodFamily.getWoodName() + "_planks_from_stripped_log", new ItemStack(plankBlock.asItem(), 4), Ingredient.of(strippedLogBlock.asItem()));
         });

         woodFamily.getStrippedWoodBlock().ifPresent(strippedWood -> {
            addShapedRecipe(2, 2,map, woodFamily.getWoodName() + "_stripped_wood_from_stripped_logs",
                    new ItemStack(strippedWood, 3),
                    new String[]{
                            "##",
                            "##"
                    },
                    Map.of('#', Ingredient.of(strippedLogBlock.asItem())));

            woodFamily.getPlankBlock().ifPresent(plankBlock -> {
               addShapelessRecipe(map, woodFamily.getWoodName() + "_planks_from_stripped_wood", new ItemStack(plankBlock.asItem(), 4), Ingredient.of(strippedWood.asItem()));
            });


         });
      });

      woodFamily.getPlankBlock().ifPresent(plankBlock -> {
//         addShapedRecipe(1, 2,map, woodFamily.getWoodName() + "_sticks_from_planks",
//                 new ItemStack(Items.STICK, 4),
//                 new String[]{
//                         "#",
//                         "#"
//                 },
//                 Map.of('#', Ingredient.of(plankBlock.asItem())));

         woodFamily.getStairsBlock().ifPresent(stairsBlock -> {
            addShapedRecipe(3, 3,map, woodFamily.getWoodName() + "_stairs_from_planks",
                    new ItemStack(stairsBlock, 4),
                    new String[]{
                            "#  ",
                            "## ",
                            "###"
                    },
                    Map.of('#', Ingredient.of(plankBlock.asItem())));
         });

         woodFamily.getSlabBlock().ifPresent(slabBlock -> {
            addShapedRecipe(3, 1,map, woodFamily.getWoodName() + "_slabs_from_planks",
                    new ItemStack(slabBlock, 6),
                    new String[]{
                            "###"
                    },
                    Map.of('#', Ingredient.of(plankBlock.asItem())));
         });

         woodFamily.getDoorBlock().ifPresent(doorBlock -> {
            addShapedRecipe(2, 3,map, woodFamily.getWoodName() + "_door_from_planks",
                    new ItemStack(doorBlock, 3),
                    new String[]{
                            "##",
                            "##",
                            "##"
                    },
                    Map.of('#', Ingredient.of(plankBlock.asItem())));
         });

         woodFamily.getFenceBlock().ifPresent(fenceBlock -> {
            addShapedRecipe(3, 2,map, woodFamily.getWoodName() + "_fence_from_planks",
                    new ItemStack(fenceBlock, 3),
                    new String[]{
                            "#S#",
                            "#S#",
                    },
                    Map.of('#', Ingredient.of(plankBlock.asItem()),
                            'S', Ingredient.of(Items.STICK)));
         });

         woodFamily.getFenceGateBlock().ifPresent(fenceGateBlock -> {
            addShapedRecipe(3, 2,map, woodFamily.getWoodName() + "_fence_gate_from_planks",
                    new ItemStack(fenceGateBlock, 1),
                    new String[]{
                            "S#S",
                            "S#S",
                    },
                    Map.of('#', Ingredient.of(plankBlock.asItem()),
                            'S', Ingredient.of(Items.STICK)));
         });

         woodFamily.getTrapdoorBlock().ifPresent(trapdoorBlock -> {
            addShapedRecipe(3, 2,map, woodFamily.getWoodName() + "_trapdoor_from_planks",
                    new ItemStack(trapdoorBlock, 3),
                    new String[]{
                            "###",
                            "###",
                    },
                    Map.of('#', Ingredient.of(plankBlock.asItem())));
         });

         woodFamily.getPressurePlateBlock().ifPresent(pressurePlateBlock -> {
            addShapedRecipe(2, 1,map, woodFamily.getWoodName() + "_pressure_plate_from_planks",
                    new ItemStack(pressurePlateBlock, 1),
                    new String[]{
                            "##"
                    },
                    Map.of('#', Ingredient.of(plankBlock.asItem())));
         });

         woodFamily.getButtonBlock().ifPresent(buttonBlock -> {
            addShapelessRecipe(map, woodFamily.getWoodName() + "_button_from_planks",
                    new ItemStack(buttonBlock, 1), Ingredient.of(plankBlock));
         });

         woodFamily.getSignBlock().ifPresent(signBlock -> {
            addShapedRecipe(3, 3,map, woodFamily.getWoodName() + "_sign_from_planks",
                    new ItemStack(signBlock, 3),
                    new String[]{
                            "###",
                            "###",
                            " S ",
                    },
                    Map.of('#', Ingredient.of(plankBlock.asItem()),
                            'S', Ingredient.of(Items.STICK)));
         });

      });
   }

   public void createPaintTubeRecipe(String id, Item dye, Item output, Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map) {
      addShapedRecipe(3, 3,
              map,
              id + "_paint_tube",
              new ItemStack(output, 1),
              new String[]{
                      " C ",
                      "AXA",
                      " Z "
              },
              Map.of('X', Ingredient.of(dye),
                      'Z', Ingredient.of(Items.BUCKET),
                      'C', Ingredient.of(Items.STRING),
                      'A', Ingredient.of(Items.IRON_NUGGET))
      );
   }

   public static void addSwordRecipe(String id, Item ingot, Item resulting, Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map) {
      addShapedRecipe(1, 3, map, id + "_sword",
              new ItemStack(resulting),
              new String[]{
                      "I",
                      "I",
                      "S"
              }, Map.of('S', Ingredient.of(Items.STICK), 'I', Ingredient.of(ingot)));
   }

   public static void addAxeRecipe(String id, Item ingot, Item resulting, Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map) {
      addShapedRecipe(3, 3, map, id + "_axe",
              new ItemStack(resulting),
              new String[]{
                      "II ",
                      "IS ",
                      " S "
              }, Map.of('S', Ingredient.of(Items.STICK), 'I', Ingredient.of(ingot)));
   }

   public static void addPickaxeRecipe(String id, Item ingot, Item resulting, Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map) {
      addShapedRecipe(3, 3, map, id + "_pickaxe",
              new ItemStack(resulting),
              new String[]{
                      "III",
                      " S ",
                      " S "
              }, Map.of('S', Ingredient.of(Items.STICK), 'I', Ingredient.of(ingot)));
   }

   public static void addShovelRecipe(String id, Item ingot, Item resulting, Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map) {
      addShapedRecipe(1, 3, map, id + "_shovel",
              new ItemStack(resulting),
              new String[]{
                      "I",
                      "S",
                      "S"
              }, Map.of('S', Ingredient.of(Items.STICK), 'I', Ingredient.of(ingot)));
   }

   public static void addHoeRecipe(String id, Item ingot, Item resulting, Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map) {
      addShapedRecipe(2, 3, map, id + "_hoe",
              new ItemStack(resulting),
              new String[]{
                      "II",
                      " S",
                      " S"
              }, Map.of('S', Ingredient.of(Items.STICK), 'I', Ingredient.of(ingot)));
   }

   public static void addIngotToolRecipes(String id, Item ingot, Item sword, Item pickaxe, Item axe, Item shovel, Item hoe, Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map) {

      addSwordRecipe(id, ingot, sword, map);
      addPickaxeRecipe(id, ingot, pickaxe, map);
      addAxeRecipe(id, ingot, axe, map);
      addShovelRecipe(id, ingot, shovel, map);
      addHoeRecipe(id, ingot, hoe, map);

   }

   public static void addCopperRecipes(Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map) {

      addIngotToolRecipes("rose_gold", Items.ROSE_GOLD_INGOT, Items.ROSE_GOLD_SWORD, Items.ROSE_GOLD_PICKAXE, Items.ROSE_GOLD_AXE, Items.ROSE_GOLD_SHOVEL, Items.ROSE_GOLD_HOE, map);

      addShapedRecipe(3, 3, map, "golden_powered_rail",
              new ItemStack(Items.GOLDEN_POWERED_RAIL, 12),
              new String[]{
                      "I I",
                      "ISI",
                      "IRI"
              }, Map.of('S', Ingredient.of(Items.STICK), 'I', Ingredient.of(Items.GOLD_INGOT),
                      'R', Ingredient.of(Items.REDSTONE)));

      Copper.registerRecipes(map);

      Smithing.registerRecipes(map);

      addShapelessRecipe(map, "craft_raw_rose_gold", new ItemStack(Items.RAW_ROSE_GOLD, 2), Ingredient.of(Items.RAW_GOLD),
              Ingredient.of(Items.RAW_GOLD),
              Ingredient.of(Items.RAW_GOLD),
              Ingredient.of(Items.RAW_GOLD),
              Ingredient.of(Items.RAW_COPPER),
              Ingredient.of(Items.RAW_COPPER),
              Ingredient.of(Items.RAW_COPPER));

      addFurnaceRecipe(
              map,
              "iron_ingot_raw",
              Ingredient.of(Items.RAW_IRON),
              new ItemStack(Items.IRON_INGOT, 1),
              0.7f, 200
      );

      addFurnaceRecipe(
              map,
              "gold_ingot_raw",
              Ingredient.of(Items.RAW_GOLD),
              new ItemStack(Items.GOLD_INGOT, 1),
              1f, 200
      );

      addFurnaceRecipe(
              map,
              "rose_gold_ingot_raw",
              Ingredient.of(Items.RAW_ROSE_GOLD),
              new ItemStack(Items.ROSE_GOLD_INGOT, 1),
              1f, 200
      );

      addFurnaceRecipe(
              map,
              "copper_ingot_raw",
              Ingredient.of(Items.RAW_COPPER, Items.COPPER_ORE),
              new ItemStack(Items.COPPER_INGOT, 1),
              0.7f, 200
      );

      addBlastingRecipe(
              map,
              "iron_ingot_raw",
              Ingredient.of(Items.RAW_IRON),
              new ItemStack(Items.IRON_INGOT, 1),
              0.7f, 100
      );

      addBlastingRecipe(
              map,
              "gold_ingot_raw",
              Ingredient.of(Items.RAW_GOLD),
              new ItemStack(Items.GOLD_INGOT, 1),
              1f, 100
      );

      addBlastingRecipe(
              map,
              "rose_gold_ingot_raw_blasting",
              Ingredient.of(Items.RAW_ROSE_GOLD),
              new ItemStack(Items.ROSE_GOLD_INGOT, 1),
              1f, 100
      );

      addBlastingRecipe(
              map,
              "copper_ingot_raw",
              Ingredient.of(Items.RAW_COPPER, Items.COPPER_ORE),
              new ItemStack(Items.COPPER_INGOT, 1),
              0.7f, 100
      );



      addShapedRecipe(1, 3, map, "spyglass_recipe", new ItemStack(Items.SPYGLASS),
              new String[]{
                      "A",
                      "C",
                      "C"
              }, Map.of('A', Ingredient.of(Items.AMETHYST_SHARD),
                      'C', Ingredient.of(Items.COPPER_INGOT)));

   }


   public static void addPaintingRecipes(Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map) {
      addShapelessRecipe(map, "meditative_painting",
              PaintingType.withVariant(PaintingType.MEDITATIVE),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.ROSE_BUSH)
      );
      addShapelessRecipe(map, "earth_painting",
              PaintingType.withVariant(PaintingType.EARTH),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.COARSE_DIRT)
      );
      addShapelessRecipe(map, "wind_painting",
              PaintingType.withVariant(PaintingType.WIND),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.FEATHER)
      );
      addShapelessRecipe(map, "fire_painting",
              PaintingType.withVariant(PaintingType.FIRE),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.FIRE_CHARGE)
      );
      addShapelessRecipe(map, "water_painting",
              PaintingType.withVariant(PaintingType.WATER),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.WATER_BUCKET)
      );


      addShapelessRecipe(map, "alban_painting",
              PaintingType.withVariant(PaintingType.ALBAN),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.LIME_WOOL)
      );
      addShapelessRecipe(map, "aztec_painting",
              PaintingType.withVariant(PaintingType.AZTEC),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.QUARTZ_BLOCK)
      );
      addShapelessRecipe(map, "aztec2_painting",
              PaintingType.withVariant(PaintingType.AZTEC2),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.SMOOTH_QUARTZ)
      );
      addShapelessRecipe(map, "bomb_painting",
              PaintingType.withVariant(PaintingType.BOMB),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.TNT)
      );
      addShapelessRecipe(map, "kebab_painting",
              PaintingType.withVariant(PaintingType.KEBAB),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.BREAD)
      );
      addShapelessRecipe(map, "plant_painting",
              PaintingType.withVariant(PaintingType.PLANT),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.TALL_GRASS)
      );
      addShapelessRecipe(map, "wasteland_painting",
              PaintingType.withVariant(PaintingType.WASTELAND),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.ORANGE_WOOL)
      );
      addShapelessRecipe(map, "graham_painting",
              PaintingType.withVariant(PaintingType.GRAHAM),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.LIGHT_BLUE_WOOL)
      );
      addShapelessRecipe(map, "praire_ride_painting",
              PaintingType.withVariant(PaintingType.PRAIRE_RIDE),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.SADDLE)
      );
      addShapelessRecipe(map, "wanderer_painting",
              PaintingType.withVariant(PaintingType.WANDERER),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.BROWN_WOOL)
      );
      addShapelessRecipe(map, "courbet_painting",
              PaintingType.withVariant(PaintingType.COURBET),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.WHITE_WOOL)
      );
      addShapelessRecipe(map, "creebet_painting",
              PaintingType.withVariant(PaintingType.CREEBET),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.CREEPER_HEAD)
      );
      addShapelessRecipe(map, "pool_painting",
              PaintingType.withVariant(PaintingType.POOL),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.LIGHT_GRAY_WOOL)
      );
      addShapelessRecipe(map, "sea_painting",
              PaintingType.withVariant(PaintingType.SEA),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(ItemTags.SMALL_FLOWERS),
              Ingredient.of(ItemTags.SMALL_FLOWERS)
      );
      addShapelessRecipe(map, "sunset_painting",
              PaintingType.withVariant(PaintingType.SUNSET),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.BLUE_WOOL)
      );
      addShapelessRecipe(map, "baroque_painting",
              PaintingType.withVariant(PaintingType.BAROQUE),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.CAKE)
      );
      addShapelessRecipe(map, "bust_painting",
              PaintingType.withVariant(PaintingType.BUST),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.GREEN_WOOL)
      );
      addShapelessRecipe(map, "humble_painting",
              PaintingType.withVariant(PaintingType.HUMBLE),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.EMERALD)
      );
      addShapelessRecipe(map, "match_painting",
              PaintingType.withVariant(PaintingType.MATCH),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.FLINT_AND_STEEL)
      );
      addShapelessRecipe(map, "skull_and_roses_painting",
              PaintingType.withVariant(PaintingType.SKULL_AND_ROSES),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.SKELETON_SKULL)
      );
      addShapelessRecipe(map, "stage_painting",
              PaintingType.withVariant(PaintingType.STAGE),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.SPIDER_EYE)
      );
      addShapelessRecipe(map, "void_painting",
              PaintingType.withVariant(PaintingType.VOID),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.END_STONE)
      );
      addShapelessRecipe(map, "wither_painting",
              PaintingType.withVariant(PaintingType.WITHER),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.SOUL_SAND)
      );
      addShapelessRecipe(map, "changing_painting",
              PaintingType.withVariant(PaintingType.CHANGING),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.LEATHER_BOOTS)
      );
      addShapelessRecipe(map, "fighters_painting",
              PaintingType.withVariant(PaintingType.FIGHTERS),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.LEATHER)
      );
      addShapelessRecipe(map, "finding_painting",
              PaintingType.withVariant(PaintingType.FINDING),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.MOSSY_STONE_BRICKS)
      );
      addShapelessRecipe(map, "lowmist_painting",
              PaintingType.withVariant(PaintingType.LOWMIST),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.MOSSY_COBBLESTONE)
      );
      addShapelessRecipe(map, "passage_painting",
              PaintingType.withVariant(PaintingType.PASSAGE),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.QUARTZ_PILLAR)
      );
      addShapelessRecipe(map, "bouquet_painting",
              PaintingType.withVariant(PaintingType.BOUQUET),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.SWEET_BERRIES)
      );
      addShapelessRecipe(map, "cavebird_painting",
              PaintingType.withVariant(PaintingType.CAVEBIRD),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.OAK_WOOD)
      );
      addShapelessRecipe(map, "cotan_painting",
              PaintingType.withVariant(PaintingType.COTAN),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.GLISTERING_MELON_SLICE)
      );
      addShapelessRecipe(map, "endboss_painting",
              PaintingType.withVariant(PaintingType.ENDBOSS),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.GREEN_STAINED_GLASS)
      );
      addShapelessRecipe(map, "fern_painting",
              PaintingType.withVariant(PaintingType.FERN),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.FERN)
      );
      addShapelessRecipe(map, "owlemons_painting",
              PaintingType.withVariant(PaintingType.OWLEMONS),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.GRAY_STAINED_GLASS)
      );
      addShapelessRecipe(map, "sunflowers_painting",
              PaintingType.withVariant(PaintingType.SUNFLOWERS),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.SUNFLOWER)
      );
      addShapelessRecipe(map, "tides_painting",
              PaintingType.withVariant(PaintingType.TIDES),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.WARPED_FUNGUS)
      );
      addShapelessRecipe(map, "backyard_painting",
              PaintingType.withVariant(PaintingType.BACKYARD),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.INFESTED_MOSSY_STONE_BRICKS)
      );
      addShapelessRecipe(map, "pond_painting",
              PaintingType.withVariant(PaintingType.POND),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.IRON_BARS)
      );
      addShapelessRecipe(map, "donkey_kong_painting",
              PaintingType.withVariant(PaintingType.DONKEY_KONG),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.RED_CONCRETE)
      );
      addShapelessRecipe(map, "skeleton_painting",
              PaintingType.withVariant(PaintingType.SKELETON),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.BONE)
      );
      addShapelessRecipe(map, "burning_skull_painting",
              PaintingType.withVariant(PaintingType.BURNING_SKULL),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.STONE)
      );
      addShapelessRecipe(map, "orb_painting",
              PaintingType.withVariant(PaintingType.ORB),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.LANTERN)
      );
      addShapelessRecipe(map, "pigscene_painting",
              PaintingType.withVariant(PaintingType.PIGSCENE),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.PORKCHOP)
      );
      addShapelessRecipe(map, "pointer_painting",
              PaintingType.withVariant(PaintingType.POINTER),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.SPRUCE_SAPLING)
      );
      addShapelessRecipe(map, "unpacked_painting",
              PaintingType.withVariant(PaintingType.UNPACKED),
              Ingredient.of(PaintingType.withNoVariant()),
              Ingredient.of(Items.OAK_LEAVES)
      );

   }


   public static Ingredient createFilteredPaintingIngredient(PaintingType resultMotive) {
      // Define a predicate for filtering paintings
      Predicate<ItemStack> predicate = stack -> {
         if (stack.getItem() != Items.PAINTING) {
            return false; // Only allow paintings
         }

         CompoundNBT tag = stack.getTag();
         if (tag != null && tag.contains("EntityTag")) {
            CompoundNBT entityTag = tag.getCompound("EntityTag");
            if (entityTag.contains("Motive")) {
               String motiveName = entityTag.getString("Motive");
               // Exclude paintings with the same motive as the result
               return !motiveName.equals(resultMotive.getName());
            }
         }

         return true; // Allow paintings without a motive or with a different motive
      };

      // Create a custom Ingredient using a single item with filtering applied
      return Ingredient.of(Stream.of(new ItemStack(Items.PAINTING))
              .filter(predicate)
              .toArray(ItemStack[]::new));
   }


   private static void addFurnaceRecipe(Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map,
                                        String recipeName, Ingredient input, ItemStack output,
                                        float experience, int cookingTime) {
      // Create a unique ID for the recipe
      ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);

      // Create the furnace recipe
      FurnaceRecipe furnaceRecipe = new FurnaceRecipe(recipeId, "", input, output, experience, cookingTime);

      // Add it to the smelting recipe map
      map.computeIfAbsent(IRecipeType.SMELTING, (type) -> ImmutableMap.builder())
              .put(recipeId, furnaceRecipe);
   }

   private static void addBlastingRecipe(Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map,
                                        String recipeName, Ingredient input, ItemStack output,
                                        float experience, int cookingTime) {
      // Create a unique ID for the recipe
      ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);

      // Create the furnace recipe
      BlastingRecipe furnaceRecipe = new BlastingRecipe(recipeId, "", input, output, experience, cookingTime);

      // Add it to the smelting recipe map
      map.computeIfAbsent(IRecipeType.BLASTING, (type) -> ImmutableMap.builder())
              .put(recipeId, furnaceRecipe);
   }

   private static void addSmokingRecipe(Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map,
                                        String recipeName, Ingredient input, ItemStack output,
                                        float experience) {
      // Create a unique ID for the recipe
      ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);

      // Create the furnace recipe
      SmokingRecipe furnaceRecipe = new SmokingRecipe(recipeId, "", input, output, experience, 100);

      // Add it to the smelting recipe map
      map.computeIfAbsent(IRecipeType.SMOKING, (type) -> ImmutableMap.builder())
              .put(recipeId, furnaceRecipe);
   }

   private static void addStonecuttingRecipe(Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map,
                                        String recipeName, Ingredient input, ItemStack output) {
      ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);

      StonecuttingRecipe furnaceRecipe = new StonecuttingRecipe(recipeId, "", input, output);

      map.computeIfAbsent(IRecipeType.STONECUTTING, (type) -> ImmutableMap.builder())
              .put(recipeId, furnaceRecipe);
   }



   private static void addShapelessRecipe(Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map,
                                          String recipeName, ItemStack result, Ingredient... ingredients) {
      ResourceLocation recipeId = new ResourceLocation("minecraft", recipeName);
      NonNullList<Ingredient> ingredientList = NonNullList.create();
      Collections.addAll(ingredientList, ingredients);

      ShapelessRecipe shapelessRecipe = new ShapelessRecipe(recipeId, "", result, ingredientList);

      map.computeIfAbsent(IRecipeType.CRAFTING, (type) -> ImmutableMap.builder())
              .put(recipeId, shapelessRecipe);
   }

   static void addShapedRecipe(int width, int height, Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map,
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




   public <C extends IInventory, T extends IRecipe<C>> Optional<T> getRecipeFor(IRecipeType<T> p_215371_1_, C p_215371_2_, World p_215371_3_) {
      return this.byType(p_215371_1_).values().stream().flatMap((p_215372_3_) -> {
         return Util.toStream(p_215371_1_.tryMatch(p_215372_3_, p_215371_3_, p_215371_2_));
      }).findFirst();
   }

   public <C extends IInventory, T extends IRecipe<C>> List<T> getAllRecipesFor(IRecipeType<T> p_241447_1_) {
      return this.byType(p_241447_1_).values().stream().map((p_241453_0_) -> {
         return (T) p_241453_0_;
      }).collect(Collectors.toList());
   }

   public <C extends IInventory, T extends IRecipe<C>> List<T> getRecipesFor(IRecipeType<T> p_215370_1_, C p_215370_2_, World p_215370_3_) {
      return this.byType(p_215370_1_).values().stream().flatMap((p_215380_3_) -> {
         return Util.toStream(p_215370_1_.tryMatch(p_215380_3_, p_215370_3_, p_215370_2_));
      }).sorted(Comparator.comparing((p_215379_0_) -> {
         return p_215379_0_.getResultItem().getDescriptionId();
      })).collect(Collectors.toList());
   }

   private <C extends IInventory, T extends IRecipe<C>> Map<ResourceLocation, IRecipe<C>> byType(IRecipeType<T> p_215366_1_) {
      return (Map)this.recipes.getOrDefault(p_215366_1_, Collections.emptyMap());
   }

   public <C extends IInventory, T extends IRecipe<C>> NonNullList<ItemStack> getRemainingItemsFor(IRecipeType<T> p_215369_1_, C p_215369_2_, World p_215369_3_) {
      Optional<T> optional = this.getRecipeFor(p_215369_1_, p_215369_2_, p_215369_3_);
      if (optional.isPresent()) {
         return optional.get().getRemainingItems(p_215369_2_);
      } else {
         NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_215369_2_.getContainerSize(), ItemStack.EMPTY);

         for(int i = 0; i < nonnulllist.size(); ++i) {
            nonnulllist.set(i, p_215369_2_.getItem(i));
         }

         return nonnulllist;
      }
   }

   public Optional<? extends IRecipe<?>> byKey(ResourceLocation p_215367_1_) {
      return this.recipes.values().stream().map((p_215368_1_) -> {
         return p_215368_1_.get(p_215367_1_);
      }).filter(Objects::nonNull).findFirst();
   }

   public Collection<IRecipe<?>> getRecipes() {

      return this.recipes.values().stream().flatMap((p_215376_0_) -> {
         return p_215376_0_.values().stream();
      }).collect(Collectors.toSet());
   }

   public Stream<ResourceLocation> getRecipeIds() {
      return this.recipes.values().stream().flatMap((p_215375_0_) -> {
         return p_215375_0_.keySet().stream();
      });
   }

   public static IRecipe<?> fromJson(ResourceLocation p_215377_0_, JsonObject p_215377_1_) {
      String s = JSONUtils.getAsString(p_215377_1_, "type");
      return Registry.RECIPE_SERIALIZER.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
         return new JsonSyntaxException("Invalid or unsupported recipe type '" + s + "'");
      }).fromJson(p_215377_0_, p_215377_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void replaceRecipes(Iterable<IRecipe<?>> p_223389_1_) {
      this.hasErrors = false;
      Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> map = Maps.newHashMap();
      p_223389_1_.forEach((p_223392_1_) -> {
         Map<ResourceLocation, IRecipe<?>> map1 = map.computeIfAbsent(p_223392_1_.getType(), (p_223390_0_) -> {
            return Maps.newHashMap();
         });
         IRecipe<?> irecipe = map1.put(p_223392_1_.getId(), p_223392_1_);
         if (irecipe != null) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + p_223392_1_.getId());
         }
      });
      this.recipes = ImmutableMap.copyOf(map);
   }
}
