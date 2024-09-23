package net.minecraft.data;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.data.StockModelShapes.FLAT_HANDHELD_ITEM;
import static net.minecraft.data.StockModelShapes.FLAT_ITEM;

public class ItemModelProvider {
   private final BiConsumer<ResourceLocation, Supplier<JsonElement>> output;

   public ItemModelProvider(BiConsumer<ResourceLocation, Supplier<JsonElement>> p_i232519_1_) {
      this.output = p_i232519_1_;
   }

   private void generateFlatItem(Item p_240076_1_, ModelsUtil p_240076_2_) {
      p_240076_2_.create(ModelsResourceUtil.getModelLocation(p_240076_1_), ModelTextures.layer0(p_240076_1_), this.output);
   }

   private void generateFlatItem(Item p_240077_1_, String p_240077_2_, ModelsUtil p_240077_3_) {
      p_240077_3_.create(ModelsResourceUtil.getModelLocation(p_240077_1_, p_240077_2_), ModelTextures.layer0(ModelTextures.getItemTexture(p_240077_1_, p_240077_2_)), this.output);
   }

   private void generateFlatItem(Item p_240075_1_, Item p_240075_2_, ModelsUtil p_240075_3_) {
      p_240075_3_.create(ModelsResourceUtil.getModelLocation(p_240075_1_), ModelTextures.layer0(p_240075_2_), this.output);
   }

   public void run() {
      this.generateFlatItem(Items.ACACIA_BOAT, FLAT_ITEM);
      this.generateFlatItem(Items.APPLE, FLAT_ITEM);
      this.generateFlatItem(Items.TOFFEE_APPLE, FLAT_ITEM);
      this.generateFlatItem(Items.ARMOR_STAND, FLAT_ITEM);
      this.generateFlatItem(Items.ARROW, FLAT_ITEM);
      this.generateFlatItem(Items.BONE_ARROW, FLAT_ITEM);
      this.generateFlatItem(Items.POISON_ARROW, FLAT_ITEM);
      this.generateFlatItem(Items.BURNING_ARROW, FLAT_ITEM);
      this.generateFlatItem(Items.FIREWORK_ARROW, FLAT_ITEM);
      this.generateFlatItem(Items.TELEPORTATION_ARROW, FLAT_ITEM);
      this.generateFlatItem(Items.GILDED_ARROW, FLAT_ITEM);
      this.generateFlatItem(Items.HEALING_ARROW, FLAT_ITEM);
      this.generateFlatItem(Items.BAKED_POTATO, FLAT_ITEM);
      this.generateFlatItem(Items.BAMBOO, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.BEEF, FLAT_ITEM);
      this.generateFlatItem(Items.BEETROOT, FLAT_ITEM);
      this.generateFlatItem(Items.BEETROOT_SOUP, FLAT_ITEM);
      this.generateFlatItem(Items.BIRCH_BOAT, FLAT_ITEM);
      this.generateFlatItem(Items.BLACK_DYE, FLAT_ITEM);

      this.generateFlatItem(Items.BLAZE_POWDER, FLAT_ITEM);
      this.generateFlatItem(Items.BLAZE_ROD, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.BLUE_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.BONE_MEAL, FLAT_ITEM);
      this.generateFlatItem(Items.BOOK, FLAT_ITEM);
      this.generateFlatItem(Items.BOWL, FLAT_ITEM);
      this.generateFlatItem(Items.BREAD, FLAT_ITEM);
      this.generateFlatItem(Items.BRICK, FLAT_ITEM);
      this.generateFlatItem(Items.BROWN_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.BUCKET, FLAT_ITEM);
      this.generateFlatItem(Items.CARROT_ON_A_STICK, StockModelShapes.FLAT_HANDHELD_ROD_ITEM);
      this.generateFlatItem(Items.WARPED_FUNGUS_ON_A_STICK, StockModelShapes.FLAT_HANDHELD_ROD_ITEM);
      this.generateFlatItem(Items.CHAINMAIL_BOOTS, FLAT_ITEM);
      this.generateFlatItem(Items.CHAINMAIL_CHESTPLATE, FLAT_ITEM);
      this.generateFlatItem(Items.CHAINMAIL_HELMET, FLAT_ITEM);
      this.generateFlatItem(Items.CHAINMAIL_LEGGINGS, FLAT_ITEM);
      this.generateFlatItem(Items.CHARCOAL, FLAT_ITEM);
      this.generateFlatItem(Items.CHEST_MINECART, FLAT_ITEM);
      this.generateFlatItem(Items.CHICKEN, FLAT_ITEM);
      this.generateFlatItem(Items.CHORUS_FRUIT, FLAT_ITEM);
      this.generateFlatItem(Items.CLAY_BALL, FLAT_ITEM);

      for(int i = 1; i < 64; ++i) {
         this.generateFlatItem(Items.CLOCK, String.format("_%02d", i), FLAT_ITEM);
      }

      this.generateFlatItem(Items.COAL, FLAT_ITEM);
      this.generateFlatItem(Items.COD_BUCKET, FLAT_ITEM);
      this.generateFlatItem(Items.COMMAND_BLOCK_MINECART, FLAT_ITEM);

      for(int j = 0; j < 32; ++j) {
         if (j != 16) {
            this.generateFlatItem(Items.COMPASS, String.format("_%02d", j), FLAT_ITEM);
         }
      }

      this.generateFlatItem(Items.COOKED_BEEF, FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_CHICKEN, FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_COD, FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_MUTTON, FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_PORKCHOP, FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_RABBIT, FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_SALMON, FLAT_ITEM);
      this.generateFlatItem(Items.COOKIE, FLAT_ITEM);
      this.generateFlatItem(Items.HONEYCOMB_COOKIE, FLAT_ITEM);
      this.generateFlatItem(Items.CREEPER_BANNER_PATTERN, FLAT_ITEM);
      this.generateFlatItem(Items.CYAN_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.DARK_OAK_BOAT, FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND, FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_AXE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_BOOTS, FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_CHESTPLATE, FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_HELMET, FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_HOE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_HORSE_ARMOR, FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_LEGGINGS, FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_PICKAXE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_SHOVEL, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_SWORD, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.MACE, StockModelShapes.FLAT_HANDHELD_ROD_ITEM);
      this.generateFlatItem(Items.DRAGON_BREATH, FLAT_ITEM);
      this.generateFlatItem(Items.DRIED_KELP, FLAT_ITEM);
      this.generateFlatItem(Items.EGG, FLAT_ITEM);
      this.generateFlatItem(Items.EMERALD, FLAT_ITEM);
      this.generateFlatItem(Items.ENCHANTED_BOOK, FLAT_ITEM);
      this.generateFlatItem(Items.ENDER_EYE, FLAT_ITEM);
      this.generateFlatItem(Items.ENDER_PEARL, FLAT_ITEM);
      this.generateFlatItem(Items.END_CRYSTAL, FLAT_ITEM);
      this.generateFlatItem(Items.EXPERIENCE_BOTTLE, FLAT_ITEM);
      this.generateFlatItem(Items.FERMENTED_SPIDER_EYE, FLAT_ITEM);
      this.generateFlatItem(Items.FIREWORK_ROCKET, FLAT_ITEM);
      this.generateFlatItem(Items.FIRE_CHARGE, FLAT_ITEM);
      this.generateFlatItem(Items.FLINT, FLAT_ITEM);
      this.generateFlatItem(Items.FLINT_AND_STEEL, FLAT_ITEM);
      this.generateFlatItem(Items.FLOWER_BANNER_PATTERN, FLAT_ITEM);
      this.generateFlatItem(Items.FURNACE_MINECART, FLAT_ITEM);
      this.generateFlatItem(Items.GHAST_TEAR, FLAT_ITEM);
      this.generateFlatItem(Items.GLASS_BOTTLE, FLAT_ITEM);
      this.generateFlatItem(Items.GLISTERING_MELON_SLICE, FLAT_ITEM);
      this.generateFlatItem(Items.GLOBE_BANNER_PATTER, FLAT_ITEM);
      this.generateFlatItem(Items.GLOWSTONE_DUST, FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_APPLE, FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_AXE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_BOOTS, FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_CARROT, FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_CHESTPLATE, FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_HELMET, FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_HOE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_HORSE_ARMOR, FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_LEGGINGS, FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_PICKAXE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_SHOVEL, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_SWORD, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLD_INGOT, FLAT_ITEM);
      this.generateFlatItem(Items.GOLD_NUGGET, FLAT_ITEM);
      this.generateFlatItem(Items.GRAY_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.GREEN_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.GUNPOWDER, FLAT_ITEM);
      this.generateFlatItem(Items.HEART_OF_THE_SEA, FLAT_ITEM);
      this.generateFlatItem(Items.HONEYCOMB, FLAT_ITEM);
      this.generateFlatItem(Items.HONEY_BOTTLE, FLAT_ITEM);
      this.generateFlatItem(Items.BEE_POLLEN, FLAT_ITEM);
      this.generateFlatItem(Items.ROYAL_JELLY, FLAT_ITEM);
      this.generateFlatItem(Items.HOPPER_MINECART, FLAT_ITEM);
      this.generateFlatItem(Items.INK_SAC, FLAT_ITEM);
      this.generateFlatItem(Items.IRON_AXE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_BOOTS, FLAT_ITEM);
      this.generateFlatItem(Items.IRON_CHESTPLATE, FLAT_ITEM);
      this.generateFlatItem(Items.IRON_HELMET, FLAT_ITEM);
      this.generateFlatItem(Items.IRON_HOE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_HORSE_ARMOR, FLAT_ITEM);
      this.generateFlatItem(Items.IRON_INGOT, FLAT_ITEM);
      this.generateFlatItem(Items.IRON_LEGGINGS, FLAT_ITEM);
      this.generateFlatItem(Items.IRON_NUGGET, FLAT_ITEM);
      this.generateFlatItem(Items.IRON_PICKAXE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_SHOVEL, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_SWORD, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.ITEM_FRAME, FLAT_ITEM);
      this.generateFlatItem(Items.JUNGLE_BOAT, FLAT_ITEM);
      this.generateFlatItem(Items.KNOWLEDGE_BOOK, FLAT_ITEM);
      this.generateFlatItem(Items.LAPIS_LAZULI, FLAT_ITEM);
      this.generateFlatItem(Items.LAVA_BUCKET, FLAT_ITEM);
      this.generateFlatItem(Items.LEATHER, FLAT_ITEM);
      this.generateFlatItem(Items.LEATHER_HORSE_ARMOR, FLAT_ITEM);
      this.generateFlatItem(Items.LIGHT_BLUE_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.LIGHT_GRAY_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.LIME_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.MAGENTA_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.MAGMA_CREAM, FLAT_ITEM);
      this.generateFlatItem(Items.MAP, FLAT_ITEM);
      this.generateFlatItem(Items.MELON_SLICE, FLAT_ITEM);
      this.generateFlatItem(Items.MILK_BUCKET, FLAT_ITEM);
      this.generateFlatItem(Items.MINECART, FLAT_ITEM);
      this.generateFlatItem(Items.MOJANG_BANNER_PATTERN, FLAT_ITEM);
      this.generateFlatItem(Items.MUSHROOM_STEW, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_11, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_13, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_BLOCKS, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_CAT, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_CHIRP, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_FAR, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_MALL, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_MELLOHI, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_PIGSTEP, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_STAL, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_STRAD, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_WAIT, FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_WARD, FLAT_ITEM);
      this.generateFlatItem(Items.MUTTON, FLAT_ITEM);
      this.generateFlatItem(Items.NAME_TAG, FLAT_ITEM);
      this.generateFlatItem(Items.NAUTILUS_SHELL, FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_AXE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_BOOTS, FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_CHESTPLATE, FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_HELMET, FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_HOE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_INGOT, FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_LEGGINGS, FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_PICKAXE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_SCRAP, FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_SHOVEL, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_SWORD, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHER_BRICK, FLAT_ITEM);
      this.generateFlatItem(Items.NETHER_STAR, FLAT_ITEM);
      this.generateFlatItem(Items.OAK_BOAT, FLAT_ITEM);
      this.generateFlatItem(Items.ORANGE_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.PAINTING, FLAT_ITEM);
      this.generateFlatItem(Items.PAPER, FLAT_ITEM);
      this.generateFlatItem(Items.PHANTOM_MEMBRANE, FLAT_ITEM);
      this.generateFlatItem(Items.PIGLIN_BANNER_PATTERN, FLAT_ITEM);
      this.generateFlatItem(Items.PINK_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.POISONOUS_POTATO, FLAT_ITEM);
      this.generateFlatItem(Items.POPPED_CHORUS_FRUIT, FLAT_ITEM);
      this.generateFlatItem(Items.PORKCHOP, FLAT_ITEM);
      this.generateFlatItem(Items.PRISMARINE_CRYSTALS, FLAT_ITEM);
      this.generateFlatItem(Items.PRISMARINE_SHARD, FLAT_ITEM);
      this.generateFlatItem(Items.PUFFERFISH, FLAT_ITEM);
      this.generateFlatItem(Items.PUFFERFISH_BUCKET, FLAT_ITEM);
      this.generateFlatItem(Items.PUMPKIN_PIE, FLAT_ITEM);
      this.generateFlatItem(Items.PURPLE_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.QUARTZ, FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT, FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT_FOOT, FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT_HIDE, FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT_STEW, FLAT_ITEM);
      this.generateFlatItem(Items.RED_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.ROTTEN_FLESH, FLAT_ITEM);
      this.generateFlatItem(Items.SADDLE, FLAT_ITEM);
      this.generateFlatItem(Items.SALMON, FLAT_ITEM);
      this.generateFlatItem(Items.SALMON_BUCKET, FLAT_ITEM);
      this.generateFlatItem(Items.SCUTE, FLAT_ITEM);
      this.generateFlatItem(Items.SHEARS, FLAT_ITEM);
      this.generateFlatItem(Items.SHULKER_SHELL, FLAT_ITEM);
      this.generateFlatItem(Items.SKULL_BANNER_PATTERN, FLAT_ITEM);
      this.generateFlatItem(Items.SLIME_BALL, FLAT_ITEM);
      this.generateFlatItem(Items.SNOWBALL, FLAT_ITEM);
      this.generateFlatItem(Items.SPECTRAL_ARROW, FLAT_ITEM);
      this.generateFlatItem(Items.SPIDER_EYE, FLAT_ITEM);
      this.generateFlatItem(Items.SPRUCE_BOAT, FLAT_ITEM);
      this.generateFlatItem(Items.STICK, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_AXE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_HOE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_PICKAXE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_SHOVEL, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_SWORD, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.SUGAR, FLAT_ITEM);
      this.generateFlatItem(Items.SUSPICIOUS_STEW, FLAT_ITEM);
      this.generateFlatItem(Items.TNT_MINECART, FLAT_ITEM);
      this.generateFlatItem(Items.TOTEM_OF_UNDYING, FLAT_ITEM);
      this.generateFlatItem(Items.TRIDENT, FLAT_ITEM);
      this.generateFlatItem(Items.TROPICAL_FISH, FLAT_ITEM);
      this.generateFlatItem(Items.TROPICAL_FISH_BUCKET, FLAT_ITEM);
      this.generateFlatItem(Items.TURTLE_HELMET, FLAT_ITEM);
      this.generateFlatItem(Items.WATER_BUCKET, FLAT_ITEM);
      this.generateFlatItem(Items.WHEAT, FLAT_ITEM);
      this.generateFlatItem(Items.WHITE_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.WOODEN_AXE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_HOE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_PICKAXE, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_SHOVEL, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_SWORD, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WRITABLE_BOOK, FLAT_ITEM);
      this.generateFlatItem(Items.WRITTEN_BOOK, FLAT_ITEM);
      this.generateFlatItem(Items.YELLOW_DYE, FLAT_ITEM);
      this.generateFlatItem(Items.DEBUG_STICK, Items.STICK, FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE, FLAT_ITEM);
   }
}