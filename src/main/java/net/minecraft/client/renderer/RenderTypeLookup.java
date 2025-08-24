package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTypeLookup {
   private static final Map<Block, RenderType> TYPE_BY_BLOCK = Util.make(Maps.newHashMap(), (hashMap) -> {
      RenderType rendertype = RenderType.tripwire();
      hashMap.put(Blocks.TRIPWIRE, rendertype);
      RenderType rendertype1 = RenderType.cutoutMipped();
      hashMap.put(Blocks.GRASS_BLOCK, rendertype1);
      hashMap.put(Blocks.IRON_BARS, rendertype1);
      hashMap.put(Blocks.GLASS_PANE, rendertype1);
      hashMap.put(Blocks.TRIPWIRE_HOOK, rendertype1);
      hashMap.put(Blocks.HOPPER, rendertype1);
      hashMap.put(Blocks.CHAIN, rendertype1);
      hashMap.put(Blocks.JUNGLE_LEAVES, rendertype1);
      hashMap.put(Blocks.OAK_LEAVES, rendertype1);
      hashMap.put(Blocks.APPLE_LEAVES, rendertype1);
      hashMap.put(Blocks.DEAD_LEAVES, rendertype1);
      hashMap.put(Blocks.SPRUCE_LEAVES, rendertype1);
      hashMap.put(Blocks.ACACIA_LEAVES, rendertype1);
      hashMap.put(Blocks.BIRCH_LEAVES, rendertype1);
      hashMap.put(Blocks.DARK_OAK_LEAVES, rendertype1);
      hashMap.put(Blocks.PALE_OAK_LEAVES, rendertype1);
      RenderType rendertype2 = RenderType.cutout();


      hashMap.put(Blocks.CATTAILS, rendertype2);
      hashMap.put(Blocks.SCULK_SENSOR, rendertype2);
      hashMap.put(Blocks.SCULK_SHRIEKER, rendertype2);
      hashMap.put(Blocks.SCULK_VEIN, rendertype2);

      hashMap.put(Blocks.OAK_SAPLING, rendertype2);
      hashMap.put(Blocks.DEAD_SAPLING, rendertype2);
      hashMap.put(Blocks.APPLE_SAPLING, rendertype2);
      hashMap.put(Blocks.PALE_OAK_SAPLING, rendertype2);
      hashMap.put(Blocks.PALE_HANGING_MOSS, rendertype2);
      hashMap.put(Blocks.PALE_MOSS_CARPET, rendertype2);
      hashMap.put(Blocks.SPRUCE_SAPLING, rendertype2);
      hashMap.put(Blocks.BIRCH_SAPLING, rendertype2);
      hashMap.put(Blocks.JUNGLE_SAPLING, rendertype2);
      hashMap.put(Blocks.ACACIA_SAPLING, rendertype2);
      hashMap.put(Blocks.DARK_OAK_SAPLING, rendertype2);
      hashMap.put(Blocks.GLASS, rendertype2);
      hashMap.put(Blocks.WHITE_BED, rendertype2);
      hashMap.put(Blocks.ORANGE_BED, rendertype2);
      hashMap.put(Blocks.MAGENTA_BED, rendertype2);
      hashMap.put(Blocks.LIGHT_BLUE_BED, rendertype2);
      hashMap.put(Blocks.YELLOW_BED, rendertype2);
      hashMap.put(Blocks.LIME_BED, rendertype2);
      hashMap.put(Blocks.PINK_BED, rendertype2);
      hashMap.put(Blocks.GRAY_BED, rendertype2);
      hashMap.put(Blocks.LIGHT_GRAY_BED, rendertype2);
      hashMap.put(Blocks.CYAN_BED, rendertype2);
      hashMap.put(Blocks.PURPLE_BED, rendertype2);
      hashMap.put(Blocks.BLUE_BED, rendertype2);
      hashMap.put(Blocks.BROWN_BED, rendertype2);
      hashMap.put(Blocks.GREEN_BED, rendertype2);
      hashMap.put(Blocks.RED_BED, rendertype2);
      hashMap.put(Blocks.BLACK_BED, rendertype2);
      hashMap.put(Blocks.GOLDEN_POWERED_RAIL, rendertype2);
      hashMap.put(Blocks.DETECTOR_RAIL, rendertype2);
      hashMap.put(Blocks.COBWEB, rendertype2);
      hashMap.put(Blocks.GRASS, rendertype2);
      hashMap.put(Blocks.FERN, rendertype2);
      hashMap.put(Blocks.DEAD_BUSH, rendertype2);
      hashMap.put(Blocks.SEAGRASS, rendertype2);
      hashMap.put(Blocks.TALL_SEAGRASS, rendertype2);
      hashMap.put(Blocks.DANDELION, rendertype2);
      hashMap.put(Blocks.POPPY, rendertype2);
      hashMap.put(Blocks.BLUE_ORCHID, rendertype2);
      hashMap.put(Blocks.ALLIUM, rendertype2);
      hashMap.put(Blocks.AZURE_BLUET, rendertype2);
      hashMap.put(Blocks.RED_TULIP, rendertype2);
      hashMap.put(Blocks.ORANGE_TULIP, rendertype2);
      hashMap.put(Blocks.WHITE_TULIP, rendertype2);
      hashMap.put(Blocks.PINK_TULIP, rendertype2);
      hashMap.put(Blocks.OXEYE_DAISY, rendertype2);
      hashMap.put(Blocks.CORNFLOWER, rendertype2);
      hashMap.put(Blocks.CLOSED_EYEBLOSSOM, rendertype2);
      hashMap.put(Blocks.OPEN_EYEBLOSSOM, rendertype2);
      hashMap.put(Blocks.PINK_PETALS, rendertype2);
      hashMap.put(Blocks.WILDFLOWERS, rendertype2);

      hashMap.put(Blocks.FROGSPAWN, rendertype2);


      hashMap.put(Blocks.LEAF_LITTER, rendertype2);
      hashMap.put(Blocks.PALE_LEAF_PILE, rendertype2);
      hashMap.put(Blocks.WITHER_ROSE, rendertype2);
      hashMap.put(Blocks.LILY_OF_THE_VALLEY, rendertype2);
      hashMap.put(Blocks.BROWN_MUSHROOM, rendertype2);
      hashMap.put(Blocks.RED_MUSHROOM, rendertype2);
      hashMap.put(Blocks.TORCH, rendertype2);
      hashMap.put(Blocks.WALL_TORCH, rendertype2);
      hashMap.put(Blocks.SOUL_TORCH, rendertype2);
      hashMap.put(Blocks.SOUL_WALL_TORCH, rendertype2);
      hashMap.put(Blocks.FIRE, rendertype2);
      hashMap.put(Blocks.SOUL_FIRE, rendertype2);
      hashMap.put(Blocks.HELLFIRE, rendertype2);

      hashMap.put(Blocks.SPAWNER, rendertype2);
      hashMap.put(Blocks.REDSTONE_WIRE, rendertype2);
      hashMap.put(Blocks.WHEAT, rendertype2);
      hashMap.put(Blocks.OAK_DOOR, rendertype2);
      hashMap.put(Blocks.PALE_OAK_DOOR, rendertype2);
      hashMap.put(Blocks.LADDER, rendertype2);
      hashMap.put(Blocks.RAIL, rendertype2);
      hashMap.put(Blocks.IRON_DOOR, rendertype2);
      hashMap.put(Blocks.REDSTONE_TORCH, rendertype2);
      hashMap.put(Blocks.REDSTONE_WALL_TORCH, rendertype2);
      hashMap.put(Blocks.CACTUS, rendertype2);
      hashMap.put(Blocks.SUGAR_CANE, rendertype2);
      hashMap.put(Blocks.REPEATER, rendertype2);
      hashMap.put(Blocks.OAK_TRAPDOOR, rendertype2);
      hashMap.put(Blocks.PALE_OAK_TRAPDOOR, rendertype2);
      hashMap.put(Blocks.SPRUCE_TRAPDOOR, rendertype2);
      hashMap.put(Blocks.BIRCH_TRAPDOOR, rendertype2);
      hashMap.put(Blocks.JUNGLE_TRAPDOOR, rendertype2);
      hashMap.put(Blocks.ACACIA_TRAPDOOR, rendertype2);
      hashMap.put(Blocks.DARK_OAK_TRAPDOOR, rendertype2);
      hashMap.put(Blocks.CRIMSON_TRAPDOOR, rendertype2);
      hashMap.put(Blocks.WARPED_TRAPDOOR, rendertype2);
      hashMap.put(Blocks.ATTACHED_PUMPKIN_STEM, rendertype2);
      hashMap.put(Blocks.ATTACHED_MELON_STEM, rendertype2);
      hashMap.put(Blocks.PUMPKIN_STEM, rendertype2);
      hashMap.put(Blocks.MELON_STEM, rendertype2);
      hashMap.put(Blocks.VINE, rendertype2);
      //hashMap.put(Blocks.BLOOMING_IVY, rendertype2);
      hashMap.put(Blocks.LILY_PAD, rendertype2);
      hashMap.put(Blocks.NETHER_WART, rendertype2);
      hashMap.put(Blocks.BREWING_STAND, rendertype2);
      hashMap.put(Blocks.COCOA, rendertype2);
      hashMap.put(Blocks.BEACON, rendertype2);
      hashMap.put(Blocks.FLOWER_POT, rendertype2);
      hashMap.put(Blocks.POTTED_OAK_SAPLING, rendertype2);
      hashMap.put(Blocks.POTTED_SPRUCE_SAPLING, rendertype2);
      hashMap.put(Blocks.POTTED_BIRCH_SAPLING, rendertype2);
      hashMap.put(Blocks.POTTED_JUNGLE_SAPLING, rendertype2);
      hashMap.put(Blocks.POTTED_ACACIA_SAPLING, rendertype2);
      hashMap.put(Blocks.POTTED_DARK_OAK_SAPLING, rendertype2);
      hashMap.put(Blocks.POTTED_FERN, rendertype2);
      hashMap.put(Blocks.POTTED_DANDELION, rendertype2);
      hashMap.put(Blocks.POTTED_POPPY, rendertype2);
      hashMap.put(Blocks.POTTED_BLUE_ORCHID, rendertype2);
      hashMap.put(Blocks.POTTED_ALLIUM, rendertype2);
      hashMap.put(Blocks.POTTED_AZURE_BLUET, rendertype2);
      hashMap.put(Blocks.POTTED_RED_TULIP, rendertype2);
      hashMap.put(Blocks.POTTED_ORANGE_TULIP, rendertype2);
      hashMap.put(Blocks.POTTED_WHITE_TULIP, rendertype2);
      hashMap.put(Blocks.POTTED_PINK_TULIP, rendertype2);
      hashMap.put(Blocks.POTTED_OXEYE_DAISY, rendertype2);
      hashMap.put(Blocks.POTTED_CORNFLOWER, rendertype2);
      hashMap.put(Blocks.POTTED_LILY_OF_THE_VALLEY, rendertype2);
      hashMap.put(Blocks.POTTED_WITHER_ROSE, rendertype2);
      hashMap.put(Blocks.POTTED_RED_MUSHROOM, rendertype2);
      hashMap.put(Blocks.POTTED_BROWN_MUSHROOM, rendertype2);
      hashMap.put(Blocks.POTTED_DEAD_BUSH, rendertype2);
      hashMap.put(Blocks.POTTED_CACTUS, rendertype2);
      hashMap.put(Blocks.CARROTS, rendertype2);
      hashMap.put(Blocks.POTATOES, rendertype2);
      hashMap.put(Blocks.COMPARATOR, rendertype2);
      hashMap.put(Blocks.ACTIVATOR_RAIL, rendertype2);
      hashMap.put(Blocks.IRON_TRAPDOOR, rendertype2);
      hashMap.put(Blocks.SUNFLOWER, rendertype2);
      hashMap.put(Blocks.LILAC, rendertype2);
      hashMap.put(Blocks.ROSE_BUSH, rendertype2);
      hashMap.put(Blocks.PEONY, rendertype2);
      hashMap.put(Blocks.TALL_GRASS, rendertype2);
      hashMap.put(Blocks.LARGE_FERN, rendertype2);
      hashMap.put(Blocks.SPRUCE_DOOR, rendertype2);
      hashMap.put(Blocks.BIRCH_DOOR, rendertype2);
      hashMap.put(Blocks.JUNGLE_DOOR, rendertype2);
      hashMap.put(Blocks.ACACIA_DOOR, rendertype2);
      hashMap.put(Blocks.DARK_OAK_DOOR, rendertype2);
      hashMap.put(Blocks.END_ROD, rendertype2);
      hashMap.put(Blocks.CHORUS_PLANT, rendertype2);
      hashMap.put(Blocks.CHORUS_FLOWER, rendertype2);
      hashMap.put(Blocks.BEETROOTS, rendertype2);
      hashMap.put(Blocks.KELP, rendertype2);
      hashMap.put(Blocks.KELP_PLANT, rendertype2);
      hashMap.put(Blocks.TURTLE_EGG, rendertype2);
      hashMap.put(Blocks.DEAD_TUBE_CORAL, rendertype2);
      hashMap.put(Blocks.DEAD_BRAIN_CORAL, rendertype2);
      hashMap.put(Blocks.DEAD_BUBBLE_CORAL, rendertype2);
      hashMap.put(Blocks.DEAD_FIRE_CORAL, rendertype2);
      hashMap.put(Blocks.DEAD_HORN_CORAL, rendertype2);
      hashMap.put(Blocks.TUBE_CORAL, rendertype2);
      hashMap.put(Blocks.BRAIN_CORAL, rendertype2);
      hashMap.put(Blocks.BUBBLE_CORAL, rendertype2);
      hashMap.put(Blocks.FIRE_CORAL, rendertype2);
      hashMap.put(Blocks.HORN_CORAL, rendertype2);
      hashMap.put(Blocks.DEAD_TUBE_CORAL_FAN, rendertype2);
      hashMap.put(Blocks.DEAD_BRAIN_CORAL_FAN, rendertype2);
      hashMap.put(Blocks.DEAD_BUBBLE_CORAL_FAN, rendertype2);
      hashMap.put(Blocks.DEAD_FIRE_CORAL_FAN, rendertype2);
      hashMap.put(Blocks.DEAD_HORN_CORAL_FAN, rendertype2);
      hashMap.put(Blocks.TUBE_CORAL_FAN, rendertype2);
      hashMap.put(Blocks.BRAIN_CORAL_FAN, rendertype2);
      hashMap.put(Blocks.BUBBLE_CORAL_FAN, rendertype2);
      hashMap.put(Blocks.FIRE_CORAL_FAN, rendertype2);
      hashMap.put(Blocks.HORN_CORAL_FAN, rendertype2);
      hashMap.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, rendertype2);
      hashMap.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, rendertype2);
      hashMap.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, rendertype2);
      hashMap.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, rendertype2);
      hashMap.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, rendertype2);
      hashMap.put(Blocks.TUBE_CORAL_WALL_FAN, rendertype2);
      hashMap.put(Blocks.BRAIN_CORAL_WALL_FAN, rendertype2);
      hashMap.put(Blocks.BUBBLE_CORAL_WALL_FAN, rendertype2);
      hashMap.put(Blocks.FIRE_CORAL_WALL_FAN, rendertype2);
      hashMap.put(Blocks.HORN_CORAL_WALL_FAN, rendertype2);
      hashMap.put(Blocks.SEA_PICKLE, rendertype2);
      hashMap.put(Blocks.CONDUIT, rendertype2);
      hashMap.put(Blocks.BAMBOO_SAPLING, rendertype2);
      hashMap.put(Blocks.BAMBOO, rendertype2);
      hashMap.put(Blocks.POTTED_BAMBOO, rendertype2);
      hashMap.put(Blocks.SCAFFOLDING, rendertype2);
      hashMap.put(Blocks.STONECUTTER, rendertype2);
      hashMap.put(Blocks.WOODCUTTER, rendertype2);

      hashMap.put(Blocks.LANTERN, rendertype2);
      hashMap.put(Blocks.SOUL_LANTERN, rendertype2);
      hashMap.put(Blocks.CAMPFIRE, rendertype2);
      hashMap.put(Blocks.SOUL_CAMPFIRE, rendertype2);
      hashMap.put(Blocks.SWEET_BERRY_BUSH, rendertype2);
      hashMap.put(Blocks.WEEPING_VINES, rendertype2);
      hashMap.put(Blocks.WEEPING_VINES_PLANT, rendertype2);
      hashMap.put(Blocks.TWISTING_VINES, rendertype2);
      hashMap.put(Blocks.TWISTING_VINES_PLANT, rendertype2);
      hashMap.put(Blocks.NETHER_SPROUTS, rendertype2);
      hashMap.put(Blocks.CRIMSON_FUNGUS, rendertype2);
      hashMap.put(Blocks.WARPED_FUNGUS, rendertype2);
      hashMap.put(Blocks.CRIMSON_ROOTS, rendertype2);
      hashMap.put(Blocks.WARPED_ROOTS, rendertype2);
      hashMap.put(Blocks.POTTED_CRIMSON_FUNGUS, rendertype2);
      hashMap.put(Blocks.POTTED_WARPED_FUNGUS, rendertype2);
      hashMap.put(Blocks.POTTED_CRIMSON_ROOTS, rendertype2);
      hashMap.put(Blocks.POTTED_WARPED_ROOTS, rendertype2);
      hashMap.put(Blocks.CRIMSON_DOOR, rendertype2);
      hashMap.put(Blocks.WARPED_DOOR, rendertype2);
      RenderType renderType3 = RenderType.translucent();
      hashMap.put(Blocks.ICE, renderType3);
      hashMap.put(Blocks.LIGHTNING_ROD, renderType3);
      hashMap.put(Blocks.FIREFLY_BUSH, rendertype2);
      hashMap.put(Blocks.CACTUS_FLOWER, rendertype2);
      hashMap.put(Blocks.SHORT_DRY_GRASS, rendertype2);
      hashMap.put(Blocks.TALL_DRY_GRASS, rendertype2);
      hashMap.put(Blocks.RESIN_CLUMP, rendertype2);

      hashMap.put(Blocks.COPPER_GRATE, renderType3);
      hashMap.put(Blocks.EXPOSED_COPPER_GRATE, renderType3);
      hashMap.put(Blocks.WEATHERED_COPPER_GRATE, renderType3);
      hashMap.put(Blocks.COPPER_DOOR, renderType3);
      hashMap.put(Blocks.EXPOSED_COPPER_DOOR, renderType3);
      hashMap.put(Blocks.WEATHERED_COPPER_DOOR, renderType3);
      hashMap.put(Blocks.OXIDIZED_COPPER_DOOR, renderType3);
      hashMap.put(Blocks.WAXED_COPPER_DOOR, renderType3);
      hashMap.put(Blocks.WAXED_EXPOSED_COPPER_DOOR, renderType3);
      hashMap.put(Blocks.WAXED_WEATHERED_COPPER_DOOR, renderType3);
      hashMap.put(Blocks.WAXED_OXIDIZED_COPPER_DOOR, renderType3);
      hashMap.put(Blocks.COPPER_TRAPDOOR, renderType3);
      hashMap.put(Blocks.EXPOSED_COPPER_TRAPDOOR, renderType3);
      hashMap.put(Blocks.WEATHERED_COPPER_TRAPDOOR, renderType3);
      hashMap.put(Blocks.OXIDIZED_COPPER_TRAPDOOR, renderType3);
      hashMap.put(Blocks.WAXED_COPPER_TRAPDOOR, renderType3);
      hashMap.put(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, renderType3);
      hashMap.put(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, renderType3);
      hashMap.put(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, renderType3);
      hashMap.put(Blocks.OXIDIZED_COPPER_GRATE, renderType3);
      hashMap.put(Blocks.WAXED_COPPER_GRATE, renderType3);
      hashMap.put(Blocks.WAXED_EXPOSED_COPPER_GRATE, renderType3);
      hashMap.put(Blocks.WAXED_WEATHERED_COPPER_GRATE, renderType3);
      hashMap.put(Blocks.WAXED_OXIDIZED_COPPER_GRATE, renderType3);
      hashMap.put(Blocks.NETHER_PORTAL, renderType3);
      hashMap.put(Blocks.WHITE_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.ORANGE_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.MAGENTA_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.LIGHT_BLUE_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.YELLOW_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.LIME_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.PINK_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.GRAY_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.LIGHT_GRAY_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.CYAN_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.PURPLE_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.BLUE_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.BROWN_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.GREEN_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.RED_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.BLACK_STAINED_GLASS, renderType3);
      hashMap.put(Blocks.WHITE_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.ORANGE_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.MAGENTA_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.YELLOW_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.LIME_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.PINK_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.GRAY_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.CYAN_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.PURPLE_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.BLUE_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.BROWN_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.GREEN_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.RED_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.BLACK_STAINED_GLASS_PANE, renderType3);
      hashMap.put(Blocks.SLIME_BLOCK, renderType3);
      hashMap.put(Blocks.HONEY_BLOCK, renderType3);
      hashMap.put(Blocks.FROSTED_ICE, renderType3);
      hashMap.put(Blocks.COOLED_MAGMA, renderType3);
      hashMap.put(Blocks.BUBBLE_COLUMN, renderType3);
      hashMap.put(Blocks.TINTED_GLASS, renderType3);

   });
   private static final Map<Fluid, RenderType> TYPE_BY_FLUID = Util.make(Maps.newHashMap(), (p_228392_0_) -> {
      RenderType rendertype = RenderType.translucent();
      p_228392_0_.put(Fluids.FLOWING_WATER, rendertype);
      p_228392_0_.put(Fluids.WATER, rendertype);
   });
   private static boolean renderCutout;

   public static RenderType getChunkRenderType(BlockState p_228390_0_) {
      Block block = p_228390_0_.getBlock();
      if (block instanceof LeavesBlock) {
         return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
      } else {
         RenderType rendertype = TYPE_BY_BLOCK.get(block);
         return rendertype != null ? rendertype : RenderType.solid();
      }
   }

   public static RenderType getMovingBlockRenderType(BlockState p_239221_0_) {
      Block block = p_239221_0_.getBlock();
      if (block instanceof LeavesBlock) {
         return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
      } else {
         RenderType rendertype = TYPE_BY_BLOCK.get(block);
         if (rendertype != null) {
            return rendertype == RenderType.translucent() ? RenderType.translucentMovingBlock() : rendertype;
         } else {
            return RenderType.solid();
         }
      }
   }

   public static RenderType getRenderType(BlockState p_239220_0_, boolean p_239220_1_) {
      RenderType rendertype = getChunkRenderType(p_239220_0_);
      if (rendertype == RenderType.translucent()) {
         if (!Minecraft.useShaderTransparency()) {
            return Atlases.translucentCullBlockSheet();
         } else {
            return p_239220_1_ ? Atlases.translucentCullBlockSheet() : Atlases.translucentItemSheet();
         }
      } else {
         return Atlases.cutoutBlockSheet();
      }
   }

   public static RenderType getRenderType(ItemStack p_239219_0_, boolean p_239219_1_) {
      Item item = p_239219_0_.getItem();
      if (item instanceof BlockItem) {
         Block block = ((BlockItem)item).getBlock();
         return getRenderType(block.defaultBlockState(), p_239219_1_);
      } else {
         return p_239219_1_ ? Atlases.translucentCullBlockSheet() : Atlases.translucentItemSheet();
      }
   }

   public static RenderType getRenderLayer(FluidState p_228391_0_) {
      RenderType rendertype = TYPE_BY_FLUID.get(p_228391_0_.getType());
      return rendertype != null ? rendertype : RenderType.solid();
   }

   public static void setFancy(boolean p_228393_0_) {
      renderCutout = p_228393_0_;
   }
}