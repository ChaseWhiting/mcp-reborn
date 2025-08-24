package net.minecraft.entity.merchant.villager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import mcp.client.Start;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.bundle.BundleColour;
import net.minecraft.bundle.BundleItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.entity.villager.data.quest.Quest;
import net.minecraft.item.*;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.potion.*;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class VillagerTrades {

   public static int getRegularXPForLevel(int level) {
      if (level == 1) {
         return 2;
      }

      if (level == 2) {
         return 10;
      }

      if (level == 3) {
         return 17;
      }

      if (level == 4) {
         return 22;
      }

      return 24;
   }

   public static int getRareTradeXPForLevel(int level) {
      if (level == 1) {
         return 5;
      }

      if (level == 2) {
         return 15;
      }

      if (level == 3) {
         return 22;
      }

      if (level == 4) {
         return 27;
      }

      return 30;
   }

   public static final Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ITrade[]>> TRADES = Util.make(Maps.newHashMap(), (trades) -> {
      trades.put(VillagerProfession.FARMER, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.WHEAT, 20, 16, 2), new VillagerTrades.EmeraldForItemsTrade(Items.POTATO, 26, 16, 2), new VillagerTrades.EmeraldForItemsTrade(Items.CARROT, 22, 16, 2), new VillagerTrades.EmeraldForItemsTrade(Items.BEETROOT, 15, 16, 2), new VillagerTrades.ItemsForEmeraldsTrade(Items.BREAD, 1, 6, 16, 1)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Blocks.PUMPKIN, 6, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Items.PUMPKIN_PIE, 1, 4, 5), new VillagerTrades.ItemsForEmeraldsTrade(Items.APPLE, 1, 4, 16, 5)}, 3, new VillagerTrades.ITrade[]{new VillagerTrades.ItemsForEmeraldsTrade(Items.COOKIE, 3, 18, 10), new VillagerTrades.EmeraldForItemsTrade(Blocks.MELON, 4, 12, 20)}, 4, new VillagerTrades.ITrade[]{new VillagerTrades.ItemsForEmeraldsTrade(Blocks.CAKE, 1, 1, 12, 15), new VillagerTrades.SuspiciousStewForEmeraldTrade(Effects.NIGHT_VISION, 100, 15), new VillagerTrades.SuspiciousStewForEmeraldTrade(Effects.JUMP, 160, 15), new VillagerTrades.SuspiciousStewForEmeraldTrade(Effects.WEAKNESS, 140, 15), new VillagerTrades.SuspiciousStewForEmeraldTrade(Effects.BLINDNESS, 120, 15), new VillagerTrades.SuspiciousStewForEmeraldTrade(Effects.POISON, 280, 15), new VillagerTrades.SuspiciousStewForEmeraldTrade(Effects.SATURATION, 7, 15)}, 5, new VillagerTrades.ITrade[]{new VillagerTrades.ItemsForEmeraldsTrade(Items.GOLDEN_CARROT, 3, 3, 30), new VillagerTrades.ItemsForEmeraldsTrade(Items.GLISTERING_MELON_SLICE, 4, 3, 30)})));
      trades.put(VillagerProfession.FISHERMAN, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.STRING, 20, 16, 2), new VillagerTrades.EmeraldForItemsTrade(Items.COAL, 10, 16, 2), new VillagerTrades.ItemsForEmeraldsAndItemsTrade(Items.COD, 6, Items.COOKED_COD, 6, 16, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.COD_BUCKET, 3, 1, 16, 1)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.COD, 15, 16, 10), new VillagerTrades.ItemsForEmeraldsAndItemsTrade(Items.SALMON, 6, Items.COOKED_SALMON, 6, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Items.CAMPFIRE, 2, 1, 5)}, 3, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.SALMON, 13, 16, 20), new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.FISHING_ROD, 3, 3, 10, 0.2F)}, 4, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.TROPICAL_FISH, 6, 12, 30)}, 5, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.PUFFERFISH, 4, 12, 30), new VillagerTrades.EmeraldForVillageTypeItemTrade(1, 12, 30, ImmutableMap.<VillagerType, Item>builder().put(VillagerType.PLAINS, Items.OAK_BOAT).put(VillagerType.TAIGA, Items.SPRUCE_BOAT).put(VillagerType.SNOW, Items.SPRUCE_BOAT).put(VillagerType.DESERT, Items.JUNGLE_BOAT).put(VillagerType.JUNGLE, Items.JUNGLE_BOAT).put(VillagerType.SAVANNA, Items.ACACIA_BOAT).put(VillagerType.SWAMP, Items.DARK_OAK_BOAT).build())})));
      trades.put(VillagerProfession.SHEPHERD, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Blocks.WHITE_WOOL, 18, 16, 2), new VillagerTrades.EmeraldForItemsTrade(Blocks.BROWN_WOOL, 18, 16, 2), new VillagerTrades.EmeraldForItemsTrade(Blocks.BLACK_WOOL, 18, 16, 2), new VillagerTrades.EmeraldForItemsTrade(Blocks.GRAY_WOOL, 18, 16, 2), new VillagerTrades.ItemsForEmeraldsTrade(Items.SHEARS, 2, 1, 1)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.WHITE_DYE, 12, 16, 10), new VillagerTrades.EmeraldForItemsTrade(Items.GRAY_DYE, 12, 16, 10), new VillagerTrades.EmeraldForItemsTrade(Items.BLACK_DYE, 12, 16, 10), new VillagerTrades.EmeraldForItemsTrade(Items.LIGHT_BLUE_DYE, 12, 16, 10), new VillagerTrades.EmeraldForItemsTrade(Items.LIME_DYE, 12, 16, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.WHITE_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.ORANGE_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.MAGENTA_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIGHT_BLUE_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.YELLOW_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIME_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.PINK_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.GRAY_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIGHT_GRAY_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.CYAN_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.PURPLE_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BLUE_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BROWN_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.GREEN_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.RED_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BLACK_WOOL, 1, 1, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.WHITE_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.ORANGE_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.MAGENTA_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIGHT_BLUE_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.YELLOW_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIME_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.PINK_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.GRAY_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIGHT_GRAY_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.CYAN_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.PURPLE_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BLUE_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BROWN_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.GREEN_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.RED_CARPET, 1, 4, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BLACK_CARPET, 1, 4, 16, 5)}, 3, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.YELLOW_DYE, 12, 16, 20), new VillagerTrades.EmeraldForItemsTrade(Items.LIGHT_GRAY_DYE, 12, 16, 20), new VillagerTrades.EmeraldForItemsTrade(Items.ORANGE_DYE, 12, 16, 20), new VillagerTrades.EmeraldForItemsTrade(Items.RED_DYE, 12, 16, 20), new VillagerTrades.EmeraldForItemsTrade(Items.PINK_DYE, 12, 16, 20), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.WHITE_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.YELLOW_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.RED_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BLACK_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BLUE_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BROWN_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.CYAN_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.GRAY_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.GREEN_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIGHT_BLUE_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIGHT_GRAY_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIME_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.MAGENTA_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.ORANGE_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.PINK_BED, 3, 1, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.PURPLE_BED, 3, 1, 12, 10)}, 4, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.BROWN_DYE, 12, 16, 30), new VillagerTrades.EmeraldForItemsTrade(Items.PURPLE_DYE, 12, 16, 30), new VillagerTrades.EmeraldForItemsTrade(Items.BLUE_DYE, 12, 16, 30), new VillagerTrades.EmeraldForItemsTrade(Items.GREEN_DYE, 12, 16, 30), new VillagerTrades.EmeraldForItemsTrade(Items.MAGENTA_DYE, 12, 16, 30), new VillagerTrades.EmeraldForItemsTrade(Items.CYAN_DYE, 12, 16, 30), new VillagerTrades.ItemsForEmeraldsTrade(Items.WHITE_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.BLUE_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.LIGHT_BLUE_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.RED_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.PINK_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.GREEN_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.LIME_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.GRAY_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.BLACK_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.PURPLE_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.MAGENTA_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.CYAN_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.BROWN_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.YELLOW_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.ORANGE_BANNER, 3, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.LIGHT_GRAY_BANNER, 3, 1, 12, 15)}, 5, new VillagerTrades.ITrade[]{new VillagerTrades.ItemsForEmeraldsTrade(Items.PAINTING, 2, 3, 30)})));
      trades.put(VillagerProfession.FLETCHER, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.STICK, 32, 16, 2), new VillagerTrades.ItemsForEmeraldsTrade(Items.ARROW, 1, 16, 1), new VillagerTrades.ItemsForEmeraldsAndItemsTrade(Blocks.GRAVEL, 10, Items.FLINT, 10, 12, 1)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.FLINT, 26, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Items.BOW, 2, 1, 5)}, 3, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.STRING, 14, 16, 20), new VillagerTrades.ItemsForEmeraldsTrade(Items.CROSSBOW, 3, 1, 10)}, 4, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.FEATHER, 24, 16, 30), new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.BOW, 2, 3, 15)}, 5, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.TRIPWIRE_HOOK, 8, 12, 30), new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.CROSSBOW, 3, 3, 15), new VillagerTrades.ItemWithPotionForEmeraldsAndItemsTrade(Items.ARROW, 5, Items.TIPPED_ARROW, 5, 2, 12, 30)})));
      trades.put(VillagerProfession.LIBRARIAN, toIntMap(ImmutableMap.<Integer, VillagerTrades.ITrade[]>builder().put(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.PAPER, 24, 16, 2), new VillagerTrades.EnchantedBookForEmeraldsTrade(1), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BOOKSHELF, 9, 1, 12, 1)}).put(2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.BOOK, 4, 12, 10), new VillagerTrades.EnchantedBookForEmeraldsTrade(5), new VillagerTrades.ItemsForEmeraldsTrade(Items.LANTERN, 1, 1, 5)}).put(3, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.INK_SAC, 5, 12, 20), new VillagerTrades.EnchantedBookForEmeraldsTrade(10), new VillagerTrades.ItemsForEmeraldsTrade(Items.GLASS, 1, 4, 10)}).put(4, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.WRITABLE_BOOK, 2, 12, 30), new VillagerTrades.EnchantedBookForEmeraldsTrade(15), new VillagerTrades.ItemsForEmeraldsTrade(Items.CLOCK, 5, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.COMPASS, 4, 1, 15)}).put(5, new VillagerTrades.ITrade[]{new VillagerTrades.ItemsForEmeraldsTrade(Items.NAME_TAG, 20, 1, 30)}).build()));
      trades.put(VillagerProfession.CARTOGRAPHER, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.PAPER, 24, 16, 2), new VillagerTrades.ItemsForEmeraldsTrade(Items.MAP, 7, 1, 1)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.GLASS_PANE, 11, 16, 10), new VillagerTrades.EmeraldForMapTrade(13, Structure.OCEAN_MONUMENT, MapDecoration.Type.MONUMENT, 12, 5)}, 3, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.COMPASS, 1, 12, 20), new VillagerTrades.EmeraldForMapTrade(14, Structure.WOODLAND_MANSION, MapDecoration.Type.MANSION, 12, 10)}, 4, new VillagerTrades.ITrade[]{new VillagerTrades.ItemsForEmeraldsTrade(Items.ITEM_FRAME, 7, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.WHITE_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.BLUE_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.LIGHT_BLUE_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.RED_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.PINK_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.GREEN_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.LIME_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.GRAY_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.BLACK_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.PURPLE_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.MAGENTA_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.CYAN_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.BROWN_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.YELLOW_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.ORANGE_BANNER, 3, 1, 15), new VillagerTrades.ItemsForEmeraldsTrade(Items.LIGHT_GRAY_BANNER, 3, 1, 15)}, 5, new VillagerTrades.ITrade[]{new VillagerTrades.ItemsForEmeraldsTrade(Items.GLOBE_BANNER_PATTER, 8, 1, 30)})));
      trades.put(VillagerProfession.CLERIC, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.ROTTEN_FLESH, 32, 16, 2), new VillagerTrades.ItemsForEmeraldsTrade(Items.REDSTONE, 1, 2, 1)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.GOLD_INGOT, 3, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(Items.LAPIS_LAZULI, 1, 1, 5)}, 3, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.RABBIT_FOOT, 2, 12, 20), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.GLOWSTONE, 4, 1, 12, 10)}, 4, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.SCUTE, 4, 12, 30), new VillagerTrades.EmeraldForItemsTrade(Items.GLASS_BOTTLE, 9, 12, 30), new VillagerTrades.ItemsForEmeraldsTrade(Items.ENDER_PEARL, 5, 1, 15)}, 5, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.NETHER_WART, 22, 12, 30), new VillagerTrades.ItemsForEmeraldsTrade(Items.EXPERIENCE_BOTTLE, 3, 1, 30)})));
      trades.put(VillagerProfession.ARMORER, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.COAL, 15, 16, 2), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.IRON_LEGGINGS), 7, 1, 12, 1, 0.2F), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.IRON_BOOTS), 4, 1, 12, 1, 0.2F), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.IRON_HELMET), 5, 1, 12, 1, 0.2F), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.IRON_CHESTPLATE), 9, 1, 12, 1, 0.2F)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.IRON_INGOT, 4, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2F), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.CHAINMAIL_BOOTS), 1, 1, 12, 5, 0.2F), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.CHAINMAIL_LEGGINGS), 3, 1, 12, 5, 0.2F)}, 3, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.LAVA_BUCKET, 1, 12, 20), new VillagerTrades.EmeraldForItemsTrade(Items.DIAMOND, 1, 12, 20), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.CHAINMAIL_HELMET), 1, 1, 12, 10, 0.2F), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.CHAINMAIL_CHESTPLATE), 4, 1, 12, 10, 0.2F), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.SHIELD), 5, 1, 12, 10, 0.2F)}, 4, new VillagerTrades.ITrade[]{new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.DIAMOND_LEGGINGS, 14, 3, 15, 0.2F), new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.DIAMOND_BOOTS, 8, 3, 15, 0.2F)}, 5, new VillagerTrades.ITrade[]{new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.DIAMOND_HELMET, 8, 3, 30, 0.2F), new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.DIAMOND_CHESTPLATE, 16, 3, 30, 0.2F)})));
      trades.put(VillagerProfession.WEAPONSMITH, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.COAL, 15, 16, 2), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.IRON_AXE), 3, 1, 12, 1, 0.2F), new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.IRON_SWORD, 2, 3, 1)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.IRON_INGOT, 4, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2F)}, 3, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.FLINT, 24, 12, 20)}, 4, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.DIAMOND, 1, 12, 30), new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.DIAMOND_AXE, 12, 3, 15, 0.2F)}, 5, new VillagerTrades.ITrade[]{new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.DIAMOND_SWORD, 8, 3, 30, 0.2F)})));
      trades.put(VillagerProfession.TOOLSMITH, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.COAL, 15, 16, 2), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.STONE_AXE), 1, 1, 12, 1, 0.2F), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.STONE_SHOVEL), 1, 1, 12, 1, 0.2F), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.STONE_PICKAXE), 1, 1, 12, 1, 0.2F), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.STONE_HOE), 1, 1, 12, 1, 0.2F)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.IRON_INGOT, 4, 12, 10), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2F)}, 3, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.FLINT, 30, 12, 20), new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.IRON_AXE, 1, 3, 10, 0.2F), new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.IRON_SHOVEL, 2, 3, 10, 0.2F), new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.IRON_PICKAXE, 3, 3, 10, 0.2F), new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.DIAMOND_HOE), 4, 1, 3, 10, 0.2F)}, 4, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.DIAMOND, 1, 12, 30), new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.DIAMOND_AXE, 12, 3, 15, 0.2F), new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.DIAMOND_SHOVEL, 5, 3, 15, 0.2F)}, 5, new VillagerTrades.ITrade[]{new VillagerTrades.EnchantedItemForEmeraldsTrade(Items.DIAMOND_PICKAXE, 13, 3, 30, 0.2F)})));
      trades.put(VillagerProfession.BUTCHER, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.CHICKEN, 14, 16, 2), new VillagerTrades.EmeraldForItemsTrade(Items.PORKCHOP, 7, 16, 2), new VillagerTrades.EmeraldForItemsTrade(Items.RABBIT, 4, 16, 2), new VillagerTrades.ItemsForEmeraldsTrade(Items.RABBIT_STEW, 1, 1, 1)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.COAL, 15, 16, 2), new VillagerTrades.ItemsForEmeraldsTrade(Items.COOKED_PORKCHOP, 1, 5, 16, 5), new VillagerTrades.ItemsForEmeraldsTrade(Items.COOKED_CHICKEN, 1, 8, 16, 5)}, 3, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.MUTTON, 7, 16, 20), new VillagerTrades.EmeraldForItemsTrade(Items.BEEF, 10, 16, 20)}, 4, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.DRIED_KELP_BLOCK, 10, 12, 30)}, 5, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.SWEET_BERRIES, 10, 12, 30)})));
      trades.put(VillagerProfession.LEATHERWORKER, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.LEATHER, 6, 16, 2), new VillagerTrades.DyedArmorForEmeraldsTrade(Items.LEATHER_LEGGINGS, 3), new VillagerTrades.DyedArmorForEmeraldsTrade(Items.LEATHER_CHESTPLATE, 7)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.FLINT, 26, 12, 10), new VillagerTrades.DyedArmorForEmeraldsTrade(Items.LEATHER_HELMET, 5, 12, 5), new VillagerTrades.DyedArmorForEmeraldsTrade(Items.LEATHER_BOOTS, 4, 12, 5)}, 3, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.RABBIT_HIDE, 9, 12, 20), new VillagerTrades.DyedArmorForEmeraldsTrade(Items.LEATHER_CHESTPLATE, 7)}, 4, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.SCUTE, 4, 12, 30), new VillagerTrades.DyedArmorForEmeraldsTrade(Items.LEATHER_HORSE_ARMOR, 6, 12, 15), new BundleDyeTrade(null, 1, -1, 3, 12, null)}, 5, new VillagerTrades.ITrade[]{new VillagerTrades.ItemsForEmeraldsTrade(new ItemStack(Items.SADDLE), 6, 1, 12, 30, 0.2F), new VillagerTrades.DyedArmorForEmeraldsTrade(Items.LEATHER_HELMET, 5, 12, 30)})));

      trades.put(VillagerProfession.MASON, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.CLAY_BALL, 10, 16, 2), new VillagerTrades.ItemsForEmeraldsTrade(Items.BRICK, 1, 10, 16, 1)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Blocks.STONE, 20, 16, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.CHISELED_STONE_BRICKS, 1, 4, 16, 5)}, 3, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Blocks.GRANITE, 16, 16, 20), new VillagerTrades.EmeraldForItemsTrade(Blocks.ANDESITE, 16, 16, 20), new VillagerTrades.EmeraldForItemsTrade(Blocks.DIORITE, 16, 16, 20), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.POLISHED_ANDESITE, 1, 4, 16, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.POLISHED_DIORITE, 1, 4, 16, 10), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.POLISHED_GRANITE, 1, 4, 16, 10)}, 4, new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(Items.QUARTZ, 12, 12, 30), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.ORANGE_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.WHITE_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BLUE_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIGHT_BLUE_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.GRAY_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIGHT_GRAY_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BLACK_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.RED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.PINK_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.MAGENTA_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIME_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.GREEN_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.CYAN_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.PURPLE_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.YELLOW_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BROWN_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.ORANGE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.WHITE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BLUE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.GRAY_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BLACK_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.RED_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.PINK_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.MAGENTA_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.LIME_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.GREEN_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.CYAN_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.PURPLE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.YELLOW_GLAZED_TERRACOTTA, 1, 1, 12, 15), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.BROWN_GLAZED_TERRACOTTA, 1, 1, 12, 15)}, 5, new VillagerTrades.ITrade[]{new VillagerTrades.ItemsForEmeraldsTrade(Blocks.QUARTZ_PILLAR, 1, 1, 12, 30), new VillagerTrades.ItemsForEmeraldsTrade(Blocks.QUARTZ_BLOCK, 1, 1, 12, 30)})));

      trades.put(VillagerProfession.CARPENTER, Builder.builder()
              .addLevelOneTrades(List.of(
                      new ItemsForEmeraldsTrade(Items.WOODEN_AXE, 1, 1, 6, getRegularXPForLevel(1)),
                      new WoodItemsBundle(1, getRegularXPForLevel(1) + 3),
                      new RandomLogsForEmeraldsTrade(2, 4, 1, 3, 5, getRegularXPForLevel(1)),
                      new RandomLogsForEmeraldsTrade(8, 14, 2, 6, 2, getRegularXPForLevel(1) + 3)
              ))
              .addLevelTwoTrades(List.of(
                      new SellFromListForEmeralds(List.of(
                              Items.OAK_DOOR, Items.PALE_OAK_DOOR, Items.DARK_OAK_DOOR,
                              Items.ACACIA_DOOR, Items.BIRCH_DOOR, Items.SPRUCE_DOOR, Items.JUNGLE_DOOR
                      ), 2, 3, 1, 1, 6, getRegularXPForLevel(2) - 3),
                      new SellFromListForEmeralds(List.of(
                              Items.OAK_FENCE, Items.PALE_OAK_FENCE, Items.DARK_OAK_FENCE,
                              Items.ACACIA_FENCE, Items.BIRCH_FENCE, Items.SPRUCE_FENCE, Items.JUNGLE_FENCE
                      ), 3, 6, 2, 3, 14, getRegularXPForLevel(2) - 6)

              ))
              .addLevelThreeTrades(List.of(
                      new DamageableItemForEmeraldsTrade(Items.IRON_AXE, 14, 3, getRegularXPForLevel(3),
                              RandomValueRange.between(0.4F, 0.57F)),

                      new SellFromListForEmeralds(List.of(
                              Items.OAK_LOG, Items.PALE_OAK_LOG, Items.DARK_OAK_LOG,
                              Items.ACACIA_LOG, Items.BIRCH_LOG, Items.SPRUCE_LOG, Items.JUNGLE_LOG
                      ), 1, 2, 1, 2, 8, getRegularXPForLevel(3) - 6),

                      new LogsAndEmeraldsForPaper(6, getRegularXPForLevel(3) - 6)
              ))
              .addLevelFourTrades(List.of(
                      new BuyFromListForEmeralds(List.of(
                              Items.CRIMSON_STEM, Items.CRIMSON_HYPHAE, Items.STRIPPED_CRIMSON_STEM, Items.STRIPPED_CRIMSON_HYPHAE,
                              Items.CRIMSON_FENCE, Items.CRIMSON_FENCE_GATE, Items.CRIMSON_DOOR, Items.CRIMSON_PLANKS, Items.CRIMSON_SIGN,
                              Items.CRIMSON_SLAB, Items.CRIMSON_TRAPDOOR
                      ), 8, 16, 2, 4, 7, getRegularXPForLevel(4) - 5),
                      new BuyFromListForEmeralds(List.of(
                              Items.WARPED_STEM, Items.WARPED_HYPHAE, Items.STRIPPED_WARPED_STEM, Items.STRIPPED_WARPED_HYPHAE,
                              Items.WARPED_FENCE, Items.WARPED_FENCE_GATE, Items.WARPED_DOOR, Items.WARPED_PLANKS, Items.WARPED_SIGN,
                              Items.WARPED_SLAB, Items.WARPED_TRAPDOOR
                      ), 8, 16, 2, 4, 7, getRegularXPForLevel(4) - 5),

                      new SellFromListForEmeralds(List.of(
                              Items.OAK_STAIRS, Items.PALE_OAK_STAIRS, Items.DARK_OAK_STAIRS,
                              Items.ACACIA_STAIRS, Items.BIRCH_STAIRS, Items.SPRUCE_STAIRS, Items.JUNGLE_STAIRS
                      ), 4, 6, 2, 3, 8, getRegularXPForLevel(4) - 9)

              ))
              .addLevelFiveTrades(List.of(
                      new DamageableItemForEmeraldsTrade(Items.DIAMOND_AXE, 33, 3, getRareTradeXPForLevel(5),
                              RandomValueRange.between(0.15F, 0.33F)),

                      new SellFromListForEmeralds(List.of(
                              Items.CRAFTING_TABLE,
                              Items.LECTERN,
                              Items.CHEST,
                              Items.BARREL,
                              Items.BOOKSHELF,
                              Items.BEEHIVE
                      ), 1, 1, 2, 3, 3, getRareTradeXPForLevel(5) - 10)
              ))
              .build());

      trades.put(VillagerProfession.BEEKEEPER, Builder.builder()
              .addLevelOneTrades(List.of(
                      new VillagerTrades.EmeraldForItemsTrade(Items.HONEYCOMB, 8, 5, 2),
                      new VillagerTrades.ItemsForEmeraldsTrade(Items.GLASS_BOTTLE, 1, 4, 7, 2),
                      new VillagerTrades.ItemsForEmeraldsTrade(Items.SHEARS, 2, 1, 8, 2)
              ))
              .addLevelTwoTrades(List.of(
                      new VillagerTrades.ItemsForEmeraldsTrade(Items.HONEY_BOTTLE, 1, 1, 12, 7),
                      new VillagerTrades.ItemsForEmeraldsTrade(Items.HONEY_BLOCK, 2, 3, 6, 10),
                      new VillagerTrades.ItemsForEmeraldsTrade(Items.HONEYCOMB_BLOCK, 3, 4, 6, 10)
              ))
              .addLevelThreeTrades(List.of(
                      new ItemsForItemsTrade(Items.BEE_POLLEN, 1, Items.BONE_MEAL, 2, 64, 0),
                      new ItemsForEmeraldsAndItemsTrade(Items.BEE_POLLEN, 14, 2, Items.HONEYCOMB, 3, 8, 13),
                      new EmeraldsForFlowersTrade(1, 5, 5, 4),
                      new FlowersForEmeraldTrade(6, 12, 4),
                      new VillagerTrades.ItemsForEmeraldsTrade(Items.CAMPFIRE, 3, 1, 4, 15)
              ))
              .addLevelFourTrades(List.of(
                      new ItemsForEmeraldsAndItemsTrade(Items.COOKIE, 12, 2, Items.HONEYCOMB_COOKIE, 8, 12, 16),
                      new VillagerTrades.ItemsForEmeraldsTrade(Items.HONEYCOMB_BLOCK, 4, 2, 10, 20),
                      new VillagerTrades.ItemsForEmeraldsTrade(Items.BEE_SPAWN_EGG, 35, 1, 3, 40),
                      new EmeraldsForFlowersTrade(1, 5, 5, 4)
              ))
              .addLevelFiveTrades(List.of(
                      new EmeraldsForFlowersTrade(1, 5, 5, 4),
                      new FlowersForEmeraldTrade(6, 5, 4),
                      new VillagerTrades.ItemsForEmeraldsTrade(Items.BEEHIVE, 3, 1, 2, 20),
                      new VillagerTrades.ItemsForEmeraldsTrade(Items.BEE_NEST, 3, 1, 2, 20)
              )).build());

      trades.put(VillagerProfession.WAXWORKER, toIntMap(ImmutableMap.of(
              1, new VillagerTrades.ITrade[]{
                      new VillagerTrades.EmeraldForItemsTrade(Items.HONEYCOMB, 8, 5, 2),
                      new ItemsForEmeraldsAndItemsTrade(Items.HONEYCOMB, 1, 2, Items.HONEYCOMB_BLOCK, 3, 8, 5),
                      new ItemsForEmeraldsAndItemsTrade(Items.HONEY_BOTTLE, 3, 1, Items.HONEY_BLOCK, 2, 8, 5)
              },
              2, new VillagerTrades.ITrade[]{
                      new VillagerTrades.ItemsForEmeraldsTrade(Items.BEE_POLLEN, 1, 2, 4, 2),
              },
              3, new VillagerTrades.ITrade[]{
                      new ItemsForItemsTrade(Items.HONEY_BOTTLE, 1, Items.HONEYCOMB, 1, 2, 0),
                      new ItemsForItemsTrade(Items.HONEYCOMB, 2, Items.HONEY_BOTTLE, 1, 2, 0),
                      new EmeraldsForFlowersTrade(2, 6, 4, 4)


              },
              4, new VillagerTrades.ITrade[]{
                      new ItemsForItemsTrade(Items.HONEY_BOTTLE, 12, Items.ROYAL_JELLY, 1, 4, 0),
              },
              5, new VillagerTrades.ITrade[] {

              })));

   });

   public static final Int2ObjectMap<VillagerTrades.IQuest[]> QUESTS = toInt(ImmutableMap.of(
           1, new VillagerTrades.IQuest[] {
                   new VillagerTrades.CollectItemsQuest(new ItemStack(Items.EMERALD), 1, new ItemStack(Items.BEEF), 3)
           }
   ));
   public static final Int2ObjectMap<VillagerTrades.ITrade[]> WANDERING_TRADER_TRADES = toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{new VillagerTrades.ItemsForEmeraldsTrade(Items.SEA_PICKLE, 2, 1, 5, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.SLIME_BALL, 4, 1, 5, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.GLOWSTONE, 2, 1, 5, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.NAUTILUS_SHELL, 5, 1, 5, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.FERN, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.SUGAR_CANE, 1, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.PUMPKIN, 1, 1, 4, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.KELP, 3, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.CACTUS, 3, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.DANDELION, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.POPPY, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.BLUE_ORCHID, 1, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.ALLIUM, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.AZURE_BLUET, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.RED_TULIP, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.ORANGE_TULIP, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.WHITE_TULIP, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.PINK_TULIP, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.OXEYE_DAISY, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.CORNFLOWER, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.WHEAT_SEEDS, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.BEETROOT_SEEDS, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.PUMPKIN_SEEDS, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.MELON_SEEDS, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.ACACIA_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.BIRCH_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.DARK_OAK_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.JUNGLE_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.OAK_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.SPRUCE_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.RED_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.WHITE_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.BLUE_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.PINK_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.BLACK_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.GREEN_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.LIGHT_GRAY_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.MAGENTA_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.YELLOW_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.GRAY_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.PURPLE_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.LIGHT_BLUE_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.LIME_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.ORANGE_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.BROWN_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.CYAN_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.VINE, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.BROWN_MUSHROOM, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.RED_MUSHROOM, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.LILY_PAD, 1, 2, 5, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.SAND, 1, 8, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.RED_SAND, 1, 4, 6, 1)}, 2, new VillagerTrades.ITrade[]{new VillagerTrades.ItemsForEmeraldsTrade(Items.TROPICAL_FISH_BUCKET, 5, 1, 4, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.PUFFERFISH_BUCKET, 5, 1, 4, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.PACKED_ICE, 3, 1, 6, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.BLUE_ICE, 6, 1, 6, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.GUNPOWDER, 1, 1, 8, 1), new VillagerTrades.ItemsForEmeraldsTrade(Items.PODZOL, 3, 3, 6, 1), new BundleDyeTrade(null, 3, 64, 6, 1, new ItemStack(Items.EMERALD, 10))}));


   public static final List<Pair<ITrade[], Integer>> WANDERING_TRADER_WARM_BIOME_TRADES = ImmutableList.<Pair<ITrade[], Integer>>builder()
           .add(Pair.of(new ITrade[]{
                   new EmeraldForItems(Items.TALL_DRY_GRASS, 3, 3, 1, 1),
                   new EmeraldForItems(Items.CACTUS_FLOWER, 12, 3, 1, 2),
                   new EmeraldForItems(Items.COOKED_RABBIT, 2, 2, 1, 2),
                   new EmeraldForItems(Items.SAND, 16, 4, 1, 1),
                   new EmeraldForItems(Items.RED_SAND, 16, 4, 1, 1),
                   new EmeraldForItems(Items.ACACIA_SAPLING, 4, 2, 1, 1),
                   new EmeraldForItems(Items.JUNGLE_SAPLING, 6, 2, 1, 2),
                   new EmeraldForItems(Items.MELON, 2, 1, 1, 2),
                   new EmeraldForItems(Items.TERRACOTTA, 16, 2, 1, 1)
           }, 2))
           .add(Pair.of(
                   concat(logsExceptThese(Blocks.ACACIA_LOG, Blocks.JUNGLE_LOG), new ITrade[]{
                           new ItemsForEmeraldsTrade(VillagerTrades.potion(Potions.STRONG_STRENGTH), 5, 1, 1, 1),
                           new EnchantedItemForEmeraldsTrade(Items.IRON_SHOVEL, 1, 1, 1, 0.2f),
                           new ItemsForEmeraldsTrade(Items.GUNPOWDER, 1, 4, 2, 1),
                           new ItemsForEmeraldsTrade(Items.PODZOL, 3, 3, 6, 1),
                           new ItemsForEmeraldsTrade(Items.PACKED_ICE, 1, 1, 6, 1),
                           new ItemsForEmeraldsTrade(Items.BLUE_ICE, 6, 1, 6, 1)
           }), 2))
           .add(Pair.of(new ITrade[]{
                   new ItemsForEmeraldsTrade(Items.BIRCH_SAPLING, 5, 1, 8, 1),
                   new ItemsForEmeraldsTrade(Items.DARK_OAK_SAPLING, 5, 1, 8, 1),
                   new ItemsForEmeraldsTrade(Items.OAK_SAPLING, 5, 1, 8, 1),
                   new ItemsForEmeraldsTrade(Items.SPRUCE_SAPLING, 5, 1, 8, 1),
                   new ItemsForEmeraldsTrade(Items.PALE_OAK_SAPLING, 5, 1, 8, 1),

                   new ItemsForEmeraldsTrade(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1),
                   new ItemsForEmeraldsTrade(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1),
                   new ItemsForEmeraldsTrade(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1),
                   new ItemsForEmeraldsTrade(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1),
                   new ItemsForEmeraldsTrade(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1),
                   new ItemsForEmeraldsTrade(Items.VINE, 1, 3, 4, 1),
                   new ItemsForEmeraldsTrade(Items.PALE_HANGING_MOSS, 1, 3, 4, 1),
                   new ItemsForEmeraldsTrade(Items.BROWN_MUSHROOM, 1, 3, 4, 1),
                   new ItemsForEmeraldsTrade(Items.RED_MUSHROOM, 1, 3, 4, 1),
                   new ItemsForEmeraldsTrade(Items.LILY_PAD, 1, 5, 2, 1),

//                   new ItemsForEmeraldsTrade(Items.DANDELION, 1, 1, 12, 1),
//                   new ItemsForEmeraldsTrade(Items.POPPY, 1, 1, 12, 1),
//                   new ItemsForEmeraldsTrade(Items.BLUE_ORCHID, 1, 1, 8, 1),
//                   new ItemsForEmeraldsTrade(Items.ALLIUM, 1, 1, 12, 1),
//                   new ItemsForEmeraldsTrade(Items.AZURE_BLUET, 1, 1, 12, 1),
//                   new ItemsForEmeraldsTrade(Items.RED_TULIP, 1, 1, 12, 1),
//                   new ItemsForEmeraldsTrade(Items.ORANGE_TULIP, 1, 1, 12, 1),
//                   new ItemsForEmeraldsTrade(Items.WHITE_TULIP, 1, 1, 12, 1),
//                   new ItemsForEmeraldsTrade(Items.PINK_TULIP, 1, 1, 12, 1),
//                   new ItemsForEmeraldsTrade(Items.OXEYE_DAISY, 1, 1, 12, 1),
//                   new ItemsForEmeraldsTrade(Items.CORNFLOWER, 1, 1, 12, 1),
//                   new ItemsForEmeraldsTrade(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1),
//                   new ItemsForEmeraldsTrade(Items.OPEN_EYEBLOSSOM, 1, 1, 7, 1),
                   new EmeraldsForFlowersTrade(1, 1, 7, 1),
                   new EmeraldsForFlowersTrade(1, 1, 7, 1),

           }, 5)).build();


   public static final List<Pair<ITrade[], Integer>> WANDERING_TRADER_TRADES_ = ImmutableList.<Pair<ITrade[], Integer>>builder()
           .add(

                   Pair.of(new ITrade[]{
                   new EmeraldForItems(VillagerTrades.potion(Potions.WATER), 2, 1, 1),
                   new EmeraldForItems(Items.WATER_BUCKET, 1, 2, 1, 2),
                   new EmeraldForItems(Items.MILK_BUCKET, 1, 2, 1, 2),
                   new EmeraldForItems(Items.FERMENTED_SPIDER_EYE, 1, 2, 1, 3),
                   new EmeraldForItems(Items.BAKED_POTATO, 4, 2, 1),
                   new EmeraldForItems(Items.HAY_BLOCK, 1, 2, 1)}, 2))
           .add(
                   Pair.of(new ITrade[]{
                           new ItemsForEmeraldsTrade(Items.PACKED_ICE, 1, 1, 6, 1),
                           new ItemsForEmeraldsTrade(Items.BLUE_ICE, 6, 1, 6, 1),
                           new ItemsForEmeraldsTrade(Items.GUNPOWDER, 1, 4, 2, 1),
                           new ItemsForEmeraldsTrade(Items.PODZOL, 3, 3, 6, 1),
                           new ItemsForEmeraldsTrade(Blocks.ACACIA_LOG, 1, 8, 4, 1),
                           new ItemsForEmeraldsTrade(Blocks.BIRCH_LOG, 1, 8, 4, 1),
                           new ItemsForEmeraldsTrade(Blocks.DARK_OAK_LOG, 1, 8, 4, 1),
                           new ItemsForEmeraldsTrade(Blocks.JUNGLE_LOG, 1, 8, 4, 1),
                           new ItemsForEmeraldsTrade(Blocks.OAK_LOG, 1, 8, 4, 1),
                           new ItemsForEmeraldsTrade(Blocks.SPRUCE_LOG, 1, 8, 4, 1),
                           //new ItemsForEmeraldsTrade(Blocks.CHERRY_LOG, 1, 8, 4, 1),
                           //new ItemsForEmeraldsTrade(Blocks.MANGROVE_LOG, 1, 8, 4, 1),
                           new ItemsForEmeraldsTrade(Blocks.PALE_OAK_LOG, 1, 8, 4, 1),
                           new EnchantedItemForEmeraldsTrade(Items.IRON_PICKAXE, 1, 1, 1, 0.2f),
                           new ItemsForEmeraldsTrade(VillagerTrades.potion(Potions.LONG_INVISIBILITY), 5, 1, 1, 1)}, 2))
           .add(
                   Pair.of(new ITrade[]{
                           new ItemsForEmeraldsTrade(Items.TROPICAL_FISH_BUCKET, 3, 1, 4, 1),
                           new ItemsForEmeraldsTrade(Items.PUFFERFISH_BUCKET, 3, 1, 4, 1),
                           new ItemsForEmeraldsTrade(Items.SEA_PICKLE, 2, 1, 5, 1),
                           new ItemsForEmeraldsTrade(Items.SLIME_BALL, 4, 1, 5, 1),
                           new ItemsForEmeraldsTrade(Items.GLOWSTONE, 2, 1, 5, 1),
                           new ItemsForEmeraldsTrade(Items.NAUTILUS_SHELL, 5, 1, 5, 1),
                           new ItemsForEmeraldsTrade(Items.FERN, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.SUGAR_CANE, 1, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.PUMPKIN, 1, 1, 4, 1),
                           new ItemsForEmeraldsTrade(Items.KELP, 3, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.CACTUS, 3, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.DANDELION, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.POPPY, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.BLUE_ORCHID, 1, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.ALLIUM, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.AZURE_BLUET, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.RED_TULIP, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.ORANGE_TULIP, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.WHITE_TULIP, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.PINK_TULIP, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.OXEYE_DAISY, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.CORNFLOWER, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1),
                           new ItemsForEmeraldsTrade(Items.OPEN_EYEBLOSSOM, 1, 1, 7, 1),
                           new ItemsForEmeraldsTrade(Items.WHEAT_SEEDS, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.BEETROOT_SEEDS, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.PUMPKIN_SEEDS, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.MELON_SEEDS, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.ACACIA_SAPLING, 5, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.BIRCH_SAPLING, 5, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.DARK_OAK_SAPLING, 5, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.JUNGLE_SAPLING, 5, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.OAK_SAPLING, 5, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.SPRUCE_SAPLING, 5, 1, 8, 1),
                           //new ItemsForEmeraldsTrade(Items.CHERRY_SAPLING, 5, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.PALE_OAK_SAPLING, 5, 1, 8, 1),
                           //new ItemsForEmeraldsTrade(Items.MANGROVE_PROPAGULE, 5, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.RED_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.WHITE_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.BLUE_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.PINK_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.BLACK_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.GREEN_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.LIGHT_GRAY_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.MAGENTA_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.YELLOW_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.GRAY_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.PURPLE_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.LIGHT_BLUE_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.LIME_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.ORANGE_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.BROWN_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.CYAN_DYE, 1, 3, 12, 1),
                           new ItemsForEmeraldsTrade(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1),
                           new ItemsForEmeraldsTrade(Items.VINE, 1, 3, 4, 1),
                           new ItemsForEmeraldsTrade(Items.PALE_HANGING_MOSS, 1, 3, 4, 1),
                           new ItemsForEmeraldsTrade(Items.BROWN_MUSHROOM, 1, 3, 4, 1),
                           new ItemsForEmeraldsTrade(Items.RED_MUSHROOM, 1, 3, 4, 1),
                           new ItemsForEmeraldsTrade(Items.LILY_PAD, 1, 5, 2, 1),
                           //new ItemsForEmeraldsTrade(Items.SMALL_DRIPLEAF, 1, 2, 5, 1),
                           new ItemsForEmeraldsTrade(Items.SAND, 1, 8, 8, 1),
                           new ItemsForEmeraldsTrade(Items.RED_SAND, 1, 4, 6, 1),
                           //new ItemsForEmeraldsTrade(Items.POINTED_DRIPSTONE, 1, 2, 5, 1),
                           //new ItemsForEmeraldsTrade(Items.ROOTED_DIRT, 1, 2, 5, 1),
                           //new ItemsForEmeraldsTrade(Items.MOSS_BLOCK, 1, 2, 5, 1),
                           new ItemsForEmeraldsTrade(Items.PALE_MOSS_BLOCK, 1, 2, 5, 1),
                           new ItemsForEmeraldsTrade(Items.WILDFLOWERS, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.TALL_DRY_GRASS, 1, 1, 12, 1),
                           new ItemsForEmeraldsTrade(Items.FIREFLY_BUSH, 3, 1, 12, 1)}, 5))
           .build();

   public static final Map<RegistryKey<Biome>, List<Pair<ITrade[], Integer>>> BIOME_TO_WANDERING_TRADER_TRADES = Util.make(new HashMap<>(), map -> {

      map.put(Biomes.DESERT, WANDERING_TRADER_WARM_BIOME_TRADES);
      map.put(Biomes.DESERT_HILLS, WANDERING_TRADER_WARM_BIOME_TRADES);
      map.put(Biomes.DESERT_LAKES, WANDERING_TRADER_WARM_BIOME_TRADES);
      map.put(Biomes.JUNGLE, WANDERING_TRADER_WARM_BIOME_TRADES);
      map.put(Biomes.JUNGLE_EDGE, WANDERING_TRADER_WARM_BIOME_TRADES);
      map.put(Biomes.JUNGLE_HILLS, WANDERING_TRADER_WARM_BIOME_TRADES);
      map.put(Biomes.BAMBOO_JUNGLE, WANDERING_TRADER_WARM_BIOME_TRADES);
      map.put(Biomes.MODIFIED_JUNGLE, WANDERING_TRADER_WARM_BIOME_TRADES);
      map.put(Biomes.BAMBOO_JUNGLE_HILLS, WANDERING_TRADER_WARM_BIOME_TRADES);


   });


   public static <T> T[] concat(T[] t, T[] t2) {
      return Start.concat(t, t2);
   }

   public static ITrade[] logsExceptThese(Block... except) {
      IItemProvider[] logs = logsExcept(except);

      List<ITrade> trades = new ArrayList<>();

      for (IItemProvider prv : logs) {
         trades.add(new ItemsForEmeraldsTrade(prv, 1, 8, 4, 1));
      }

      return trades.toArray(new ITrade[]{});
   }

   public static IItemProvider[] logsExcept(IItemProvider... except) {
      IItemProvider[] logs = new IItemProvider[]{
              Items.OAK_LOG,
              Items.SPRUCE_LOG,
              Items.BIRCH_LOG,
              Items.ACACIA_LOG,
              Items.DARK_OAK_LOG,
              Items.JUNGLE_LOG,
              Items.PALE_OAK_LOG};

      List<IItemProvider> items = new ArrayList<>(Arrays.stream(logs).toList());

      for (IItemProvider prv : logs) {
         if (Arrays.stream(except).anyMatch(e -> prv == e)) {
            items.remove(prv);
         }
      }


      return items.toArray(new IItemProvider[]{});
   }


   public static Int2ObjectMap<ITrade[]> convertToInt2ObjectMap(List<Pair<ITrade[], Integer>> tradePairs) {
      Int2ObjectOpenHashMap<List<ITrade>> intermediateMap = new Int2ObjectOpenHashMap<>();

      for (Pair<ITrade[], Integer> pair : tradePairs) {
         int key = pair.getSecond();
         ITrade[] trades = pair.getFirst();

         intermediateMap.computeIfAbsent(key, k -> new ArrayList<>())
                 .addAll(Arrays.asList(trades));
      }

      // flatten to arrays
      Int2ObjectOpenHashMap<ITrade[]> finalMap = new Int2ObjectOpenHashMap<>();
      intermediateMap.forEach((level, tradeList) -> finalMap.put(level, tradeList.toArray(new ITrade[0])));

      return finalMap;
   }


   private static ItemStack potion(Potion holder) {
      return PotionUtils.ofPotion(holder);
   }


   static class EmeraldForItems
           implements ITrade {
      private final ItemStack itemStack;
      private final int maxUses;
      private final int villagerXp;
      private final int emeraldAmount;
      private final float priceMultiplier;

      public EmeraldForItems(IItemProvider itemLike, int priceCount, int maxUses, int xp) {
         this(itemLike, priceCount, maxUses, xp, 1);
      }

      public EmeraldForItems(IItemProvider itemLike, int priceCount, int maxUses, int xp, int emeraldAmount) {
         this(new ItemStack(itemLike.asItem(), priceCount), maxUses, xp, emeraldAmount);
      }

      public EmeraldForItems(ItemStack itemCost, int n, int n2, int n3) {
         this.itemStack = itemCost;
         this.maxUses = n;
         this.villagerXp = n2;
         this.emeraldAmount = n3;
         this.priceMultiplier = 0.05f;
      }

      @Override
      public MerchantOffer getOffer(Entity entity, Random randomSource) {
         return new MerchantOffer(this.itemStack, new ItemStack(Items.EMERALD, this.emeraldAmount), this.maxUses, this.villagerXp, this.priceMultiplier);
      }
   }



   private static Int2ObjectMap<VillagerTrades.ITrade[]> toIntMap(ImmutableMap<Integer, VillagerTrades.ITrade[]> p_221238_0_) {
      return new Int2ObjectOpenHashMap<>(p_221238_0_);
   }

   private static Int2ObjectMap<VillagerTrades.IQuest[]> toInt(ImmutableMap<Integer, VillagerTrades.IQuest[]> p_221238_0_) {
      return new Int2ObjectOpenHashMap<>(p_221238_0_);
   }


   public static class CollectItemsQuest implements IQuest {
      private final ItemStack requiredItem;
      private final int requiredCount;
      private final ItemStack rewardItem;
      private final int rewardCount;
      private Quest fq;

      public CollectItemsQuest(ItemStack requiredItem, int requiredCount, ItemStack rewardItem, int rewardCount) {
         this.requiredItem = requiredItem;
         this.requiredCount = requiredCount;
         this.rewardItem = rewardItem;
         this.rewardCount = rewardCount;

         fq = new Quest("Quest", "test");
         fq.addReward(rewardItem);
         fq.addReward(requiredItem);
      }



      @Nullable
      @Override
      public QuestOffer getQuest(Entity entity, Random random) {
         return new QuestOffer(fq);
      }
   }



















   private static Item getRandomFlower(int seed) {
      List<Item> flowers = ItemTags.FLOWERS.getValues().stream()
              .map(Item::asItem)
              .collect(Collectors.toList());
      return flowers.get(new Random(seed).nextInt(flowers.size()));
   }

   static class DyedArmorForEmeraldsTrade implements VillagerTrades.ITrade {
      private final Item item;
      private final int value;
      private final int maxUses;
      private final int villagerXp;

      public DyedArmorForEmeraldsTrade(Item p_i50540_1_, int p_i50540_2_) {
         this(p_i50540_1_, p_i50540_2_, 12, 1);
      }

      public DyedArmorForEmeraldsTrade(Item p_i50541_1_, int p_i50541_2_, int p_i50541_3_, int p_i50541_4_) {
         this.item = p_i50541_1_;
         this.value = p_i50541_2_;
         this.maxUses = p_i50541_3_;
         this.villagerXp = p_i50541_4_;
      }

      public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
         ItemStack itemstack = new ItemStack(Items.EMERALD, this.value);
         ItemStack itemstack1 = new ItemStack(this.item);
         if (this.item instanceof DyeableArmorItem) {
            List<DyeItem> list = Lists.newArrayList();
            list.add(getRandomDye(p_221182_2_));
            if (p_221182_2_.nextFloat() > 0.7F) {
               list.add(getRandomDye(p_221182_2_));
            }

            if (p_221182_2_.nextFloat() > 0.8F) {
               list.add(getRandomDye(p_221182_2_));
            }

            itemstack1 = IDyeableArmorItem.dyeArmor(itemstack1, list);
         }

         return new MerchantOffer(itemstack, itemstack1, this.maxUses, this.villagerXp, 0.2F);
      }

      private static DyeItem getRandomDye(Random p_221232_0_) {
         return DyeItem.byColor(DyeColor.byId(p_221232_0_.nextInt(16)));
      }
   }

   static class EmeraldForItemsTrade implements VillagerTrades.ITrade {
      private final Item item;
      private final int cost;
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;

      public EmeraldForItemsTrade(IItemProvider itemProvider, int cost, int maxUses, int villagerXp) {
         this.item = itemProvider.asItem();
         this.cost = cost;
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
      }

      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         ItemStack itemStack = new ItemStack(this.item, this.cost);
         return new MerchantOffer(itemStack, new ItemStack(Items.EMERALD), this.maxUses, this.villagerXp, this.priceMultiplier);
      }
   }

   public static class FlowersForEmeraldTrade implements VillagerTrades.ITrade {
      private final int cost;
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;

      public FlowersForEmeraldTrade(int cost, int maxUses, int villagerXp) {
         this.cost = cost;
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
      }

      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         Item randomFlower = getRandomFlower(random);
         ItemStack itemStack = new ItemStack(randomFlower, this.cost);
         return new MerchantOffer(itemStack, new ItemStack(Items.EMERALD), this.maxUses, this.villagerXp, this.priceMultiplier);
      }

      private static Item getRandomFlower(Random random) {
         List<Item> flowers = ItemTags.FLOWERS.getValues().stream()
                 .map(Item::asItem)
                 .filter(item -> item != Items.WITHER_ROSE)
                 .collect(Collectors.toList());
         return flowers.get(random.nextInt(flowers.size()));
      }
   }

   public static class EmeraldsForFlowersTrade implements VillagerTrades.ITrade {
      private final int minCount;
      private final int maxCount;
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;

      public EmeraldsForFlowersTrade(int minCount, int maxCount, int maxUses, int villagerXp) {
         this.minCount = minCount;
         this.maxCount = maxCount;
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
      }

      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         Item randomFlower = getRandomFlower(random);
         int count = minCount + random.nextInt(maxCount - minCount + 1);
         ItemStack sellingStack = new ItemStack(randomFlower, count);
         return new MerchantOffer(new ItemStack(Items.EMERALD, 1), sellingStack, maxUses, villagerXp, priceMultiplier);
      }

      private static Item getRandomFlower(Random random) {
         List<Item> flowers = ItemTags.FLOWERS.getValues().stream()
                 .map(Item::asItem)
                 .filter(item -> item != Items.WITHER_ROSE)
                 .collect(Collectors.toList());
         return flowers.get(random.nextInt(flowers.size()));
      }
   }

   public static class RandomLogsForEmeraldsTrade implements VillagerTrades.ITrade {
      private final int count;
      private final int emeraldCount;

      private final int maxCount;
      private final int maxEmeraldCount;
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;

      public RandomLogsForEmeraldsTrade(int count, int maxCount, int minEmeraldCount, int maxEmeraldCount, int maxUses, int villagerXp) {
         this.count = count;
         this.emeraldCount = minEmeraldCount;
         this.maxCount = maxCount;
         this.maxEmeraldCount = maxEmeraldCount;
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
      }

      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         int count = MathHelper.randomBetweenInclusive(random, this.count, maxCount);
         List<Block> woodenLogs = List.of(
                 Blocks.OAK_LOG, Blocks.PALE_OAK_LOG, Blocks.DARK_OAK_LOG,
                 Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.SPRUCE_LOG
         );
         ItemStack buyingStack = new ItemStack(woodenLogs.get(random.nextInt(woodenLogs.size())), count);

         // Bias emeralds proportionally with some randomness
         float emeraldsPerLog = 0.25f; // Adjust for balance
         float variance = 0.2f; // Random fluctuation, 20% of log count
         int emeralds = Math.round(count * emeraldsPerLog + (random.nextFloat() - 0.5f) * variance * count);
         emeralds = MathHelper.clamp(emeralds, emeraldCount, maxEmeraldCount);

         return new MerchantOffer(buyingStack, new ItemStack(Items.EMERALD, emeralds), maxUses, villagerXp, priceMultiplier);
      }


   }

   public static class SellFromListForEmeralds implements VillagerTrades.ITrade {
      private final int count;
      private final int emeraldCount;

      private final int maxCount;
      private final int maxEmeraldCount;
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;
      private final List<IItemProvider> items;

      public SellFromListForEmeralds(List<IItemProvider> items, int count, int maxCount, int minEmeraldCount, int maxEmeraldCount, int maxUses, int villagerXp) {
         this.count = count;
         this.emeraldCount = minEmeraldCount;
         this.maxCount = maxCount;
         this.maxEmeraldCount = maxEmeraldCount;
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
         this.items = items;
      }

      public SellFromListForEmeralds(ITag<IItemProvider> items, int count, int maxCount, int minEmeraldCount, int maxEmeraldCount, int maxUses, int villagerXp) {
         this.count = count;
         this.emeraldCount = minEmeraldCount;
         this.maxCount = maxCount;
         this.maxEmeraldCount = maxEmeraldCount;
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
         this.items = items.getValues();
      }

      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         int count = MathHelper.randomBetweenInclusive(random, this.count, maxCount);
         ItemStack sellingStack = new ItemStack(items.get(random.nextInt(items.size())), count);

         return new MerchantOffer(new ItemStack(Items.EMERALD, MathHelper.randomBetweenInclusive(random, emeraldCount, maxEmeraldCount)), sellingStack, maxUses, villagerXp, priceMultiplier);
      }


   }

   public static class BuyFromListForEmeralds implements VillagerTrades.ITrade {
      private final int count;
      private final int emeraldCount;

      private final int maxCount;
      private final int maxEmeraldCount;
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;
      private final List<IItemProvider> items;

      public BuyFromListForEmeralds(List<IItemProvider> items, int count, int maxCount, int minEmeraldCount, int maxEmeraldCount, int maxUses, int villagerXp) {
         this.count = count;
         this.emeraldCount = minEmeraldCount;
         this.maxCount = maxCount;
         this.maxEmeraldCount = maxEmeraldCount;
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
         this.items = items;
      }

      public BuyFromListForEmeralds(ITag<IItemProvider> items, int count, int maxCount, int minEmeraldCount, int maxEmeraldCount, int maxUses, int villagerXp) {
         this.count = count;
         this.emeraldCount = minEmeraldCount;
         this.maxCount = maxCount;
         this.maxEmeraldCount = maxEmeraldCount;
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
         this.items = items.getValues();
      }

      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         int count = MathHelper.randomBetweenInclusive(random, this.count, maxCount);
         ItemStack buyingStack = new ItemStack(items.get(random.nextInt(items.size())), count);

         return new MerchantOffer(buyingStack, new ItemStack(Items.EMERALD, MathHelper.randomBetweenInclusive(random, emeraldCount, maxEmeraldCount)), maxUses, villagerXp, priceMultiplier);
      }


   }

   static class EmeraldForMapTrade implements VillagerTrades.ITrade {
      private final int emeraldCost;
      private final Structure<?> destination;
      private final MapDecoration.Type destinationType;
      private final int maxUses;
      private final int villagerXp;

      public EmeraldForMapTrade(int p_i231575_1_, Structure<?> p_i231575_2_, MapDecoration.Type p_i231575_3_, int p_i231575_4_, int p_i231575_5_) {
         this.emeraldCost = p_i231575_1_;
         this.destination = p_i231575_2_;
         this.destinationType = p_i231575_3_;
         this.maxUses = p_i231575_4_;
         this.villagerXp = p_i231575_5_;
      }

      @Nullable
      public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
         if (!(p_221182_1_.level instanceof ServerWorld)) {
            return null;
         } else {
            ServerWorld serverworld = (ServerWorld)p_221182_1_.level;
            BlockPos blockpos = serverworld.findNearestMapFeature(this.destination, p_221182_1_.blockPosition(), 100, true);
            if (blockpos != null) {
               ItemStack itemstack = FilledMapItem.create(serverworld, blockpos.getX(), blockpos.getZ(), (byte)2, true, true);
               FilledMapItem.renderBiomePreviewMap(serverworld, itemstack);
               MapData.addTargetDecoration(itemstack, blockpos, "+", this.destinationType);
               itemstack.setHoverName(new TranslationTextComponent("filled_map." + this.destination.getFeatureName().toLowerCase(Locale.ROOT)));
               return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(Items.COMPASS), itemstack, this.maxUses, this.villagerXp, 0.2F);
            } else {
               return null;
            }
         }
      }
   }

   static class EmeraldForVillageTypeItemTrade implements VillagerTrades.ITrade {
      private final Map<VillagerType, Item> trades;
      private final int cost;
      private final int maxUses;
      private final int villagerXp;

      public EmeraldForVillageTypeItemTrade(int p_i50538_1_, int p_i50538_2_, int p_i50538_3_, Map<VillagerType, Item> p_i50538_4_) {
         Registry.VILLAGER_TYPE.stream().filter((p_221188_1_) -> {
            return !p_i50538_4_.containsKey(p_221188_1_);
         }).findAny().ifPresent((p_221189_0_) -> {
            throw new IllegalStateException("Missing trade for villager type: " + Registry.VILLAGER_TYPE.getKey(p_221189_0_));
         });
         this.trades = p_i50538_4_;
         this.cost = p_i50538_1_;
         this.maxUses = p_i50538_2_;
         this.villagerXp = p_i50538_3_;
      }

      @Nullable
      public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
         if (p_221182_1_ instanceof IVillagerDataHolder) {
            ItemStack itemstack = new ItemStack(this.trades.get(((IVillagerDataHolder)p_221182_1_).getVillagerData().getType()), this.cost);
            return new MerchantOffer(itemstack, new ItemStack(Items.EMERALD), this.maxUses, this.villagerXp, 0.05F);
         } else {
            return null;
         }
      }
   }

   static class EnchantedBookForEmeraldsTrade implements VillagerTrades.ITrade {
      private final int villagerXp;

      public EnchantedBookForEmeraldsTrade(int p_i50537_1_) {
         this.villagerXp = p_i50537_1_;
      }

      public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
         List<Enchantment> list = Registry.ENCHANTMENT.stream().filter(Enchantment::isTradeable).collect(Collectors.toList());
         Enchantment enchantment = list.get(p_221182_2_.nextInt(list.size()));
         int i = MathHelper.nextInt(p_221182_2_, enchantment.getMinLevel(), enchantment.getMaxLevel());
         ItemStack itemstack = EnchantedBookItem.createForEnchantment(new EnchantmentData(enchantment, i));
         int j = 2 + p_221182_2_.nextInt(5 + i * 10) + 3 * i;
         if (enchantment.isTreasureOnly()) {
            j *= 2;
         }

         if (j > 64) {
            j = 64;
         }

         return new MerchantOffer(new ItemStack(Items.EMERALD, j), new ItemStack(Items.BOOK), itemstack, 12, this.villagerXp, 0.2F);
      }
   }

   static class EnchantedItemForEmeraldsTrade implements VillagerTrades.ITrade {
      private final ItemStack itemStack;
      private final int baseEmeraldCost;
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;

      public EnchantedItemForEmeraldsTrade(Item p_i50535_1_, int p_i50535_2_, int p_i50535_3_, int p_i50535_4_) {
         this(p_i50535_1_, p_i50535_2_, p_i50535_3_, p_i50535_4_, 0.05F);
      }

      public EnchantedItemForEmeraldsTrade(Item p_i50536_1_, int p_i50536_2_, int p_i50536_3_, int p_i50536_4_, float p_i50536_5_) {
         this.itemStack = new ItemStack(p_i50536_1_);
         this.baseEmeraldCost = p_i50536_2_;
         this.maxUses = p_i50536_3_;
         this.villagerXp = p_i50536_4_;
         this.priceMultiplier = p_i50536_5_;
      }

      public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
         int i = 5 + p_221182_2_.nextInt(15);
         ItemStack itemstack = EnchantmentHelper.enchantItem(p_221182_2_, new ItemStack(this.itemStack.getItem()), i, false);
         int j = Math.min(this.baseEmeraldCost + i, 64);
         ItemStack itemstack1 = new ItemStack(Items.EMERALD, j);
         return new MerchantOffer(itemstack1, itemstack, this.maxUses, this.villagerXp, this.priceMultiplier);
      }
   }

   public interface ITrade {
      @Nullable
      MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_);
   }

   public interface IQuest {
      @Nullable
      QuestOffer getQuest(Entity entity, Random random);
   }

   static class ItemWithPotionForEmeraldsAndItemsTrade implements VillagerTrades.ITrade {
      private final ItemStack toItem;
      private final int toCount;
      private final int emeraldCost;
      private final int maxUses;
      private final int villagerXp;
      private final Item fromItem;
      private final int fromCount;
      private final float priceMultiplier;

      public ItemWithPotionForEmeraldsAndItemsTrade(Item p_i50526_1_, int p_i50526_2_, Item p_i50526_3_, int p_i50526_4_, int p_i50526_5_, int p_i50526_6_, int p_i50526_7_) {
         this.toItem = new ItemStack(p_i50526_3_);
         this.emeraldCost = p_i50526_5_;
         this.maxUses = p_i50526_6_;
         this.villagerXp = p_i50526_7_;
         this.fromItem = p_i50526_1_;
         this.fromCount = p_i50526_2_;
         this.toCount = p_i50526_4_;
         this.priceMultiplier = 0.05F;
      }

      public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
         ItemStack itemstack = new ItemStack(Items.EMERALD, this.emeraldCost);
         List<Potion> list = Registry.POTION.stream().filter((p_221218_0_) -> {
            return !p_221218_0_.getEffects().isEmpty() && PotionBrewing.isBrewablePotion(p_221218_0_);
         }).collect(Collectors.toList());
         Potion potion = list.get(p_221182_2_.nextInt(list.size()));
         ItemStack itemstack1 = PotionUtils.setPotion(new ItemStack(this.toItem.getItem(), this.toCount), potion);
         return new MerchantOffer(itemstack, new ItemStack(this.fromItem, this.fromCount), itemstack1, this.maxUses, this.villagerXp, this.priceMultiplier);
      }
   }

   static class ItemsForEmeraldsAndItemsTrade implements VillagerTrades.ITrade {
      private final ItemStack fromItem;
      private final int fromCount;
      private final int emeraldCost;
      private final ItemStack toItem;
      private final int toCount;
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;

      public ItemsForEmeraldsAndItemsTrade(IItemProvider fromItemProvider, int fromCount, Item toItem, int toCount, int maxUses, int villagerXp) {
         this(fromItemProvider, fromCount, 1, toItem, toCount, maxUses, villagerXp);
      }

      public ItemsForEmeraldsAndItemsTrade(IItemProvider fromItemProvider, int fromCount, int emeraldCost, Item toItem, int toCount, int maxUses, int villagerXp) {
         this.fromItem = new ItemStack(fromItemProvider);
         this.fromCount = fromCount;
         this.emeraldCost = emeraldCost;
         this.toItem = new ItemStack(toItem);
         this.toCount = toCount;
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
      }

      @Nullable
      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         return new MerchantOffer(
                 new ItemStack(Items.EMERALD, this.emeraldCost),
                 new ItemStack(this.fromItem.getItem(), this.fromCount),
                 new ItemStack(this.toItem.getItem(), this.toCount),
                 this.maxUses,
                 this.villagerXp,
                 this.priceMultiplier
         );
      }
   }

   static class LogsAndEmeraldsForPaper implements VillagerTrades.ITrade {
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;

      public LogsAndEmeraldsForPaper(int maxUses, int villagerXp) {
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
      }

      @Nullable
      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         List<Block> woodenLogs = List.of(
                 Blocks.OAK_LOG, Blocks.PALE_OAK_LOG, Blocks.DARK_OAK_LOG,
                 Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.SPRUCE_LOG,

                 Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_PALE_LOG, Blocks.STRIPPED_DARK_OAK_LOG,
                 Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_SPRUCE_LOG,

                 Blocks.CRIMSON_HYPHAE,
                 Blocks.STRIPPED_CRIMSON_HYPHAE,
                 Blocks.CRIMSON_STEM,
                 Blocks.STRIPPED_CRIMSON_STEM,
                 Blocks.WARPED_HYPHAE,
                 Blocks.STRIPPED_WARPED_HYPHAE,
                 Blocks.WARPED_STEM,
                 Blocks.STRIPPED_WARPED_STEM
         );

         return new MerchantOffer(
                 new ItemStack(Items.EMERALD, MathHelper.randomBetweenInclusive(random, 1, 2)),
                 new ItemStack(woodenLogs.get(random.nextInt(woodenLogs.size())), 1),
                 new ItemStack(Items.PAPER, 3),
                 this.maxUses,
                 this.villagerXp,
                 this.priceMultiplier
         );
      }
   }

   static class WoodItemsBundle implements VillagerTrades.ITrade {
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;

      public WoodItemsBundle(int maxUses, int villagerXp) {
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
      }

      @Nullable
      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         List<ItemStack> fullList = new ArrayList<>();

         fullList.add(new ItemStack(Items.OAK_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.PALE_OAK_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.DARK_OAK_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.ACACIA_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.BIRCH_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.JUNGLE_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.SPRUCE_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.STRIPPED_OAK_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.STRIPPED_PALE_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.STRIPPED_DARK_OAK_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.STRIPPED_ACACIA_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.STRIPPED_BIRCH_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.STRIPPED_JUNGLE_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.STRIPPED_SPRUCE_LOG, MathHelper.randomBetweenInclusive(random, 3, 6)));
         fullList.add(new ItemStack(Items.WOODEN_AXE));
         fullList.add(new ItemStack(Items.PAPER, MathHelper.randomBetweenInclusive(random, 2, 5)));

         Collections.shuffle(fullList, random);

         int itemsToKeep = MathHelper.randomBetweenInclusive(random, 5, 10);
         List<ItemStack> selected = fullList.subList(0, Math.min(itemsToKeep, fullList.size()));

         ItemStack bundle = new ItemStack(Items.BUNDLE, 1);

         int totalWeight = selected.stream()
                 .mapToInt(item -> item.getWeight(item, bundle))
                 .sum();

         while (totalWeight > 64) {
            selected.remove(random.nextInt(selected.size()));
            totalWeight = selected.stream()
                    .mapToInt(item -> item.getWeight(item, bundle))
                    .sum();
         }

         BundleItem.setItems(selected, bundle);

         return new MerchantOffer(
                 new ItemStack(Items.EMERALD, MathHelper.randomBetweenInclusive(random, 4, 9)),
                 bundle,
                 this.maxUses,
                 this.villagerXp,
                 this.priceMultiplier
         );
      }

   }

   static class BundleDyeTrade implements VillagerTrades.ITrade {
      private final ItemStack fromItem;

      private final ItemStack toItem;
      private final ItemStack cost;
      private final int maxUses;
      private final int dyeCost;
      private int weight;
      private final int villagerXp;
      private final float priceMultiplier;

      public BundleDyeTrade(@Nullable DyeItem dye, int dyeCost, ItemStack cost, int maxUses, int villagerXp) {
         this(dye, dyeCost, 64, maxUses, villagerXp, cost);
      }

      public BundleDyeTrade(@Nullable DyeItem dye, int maxUses, int villagerXp) {
         this(dye, 1, 64, maxUses, villagerXp, null);
      }

      public BundleDyeTrade(@Nullable DyeItem dye, int dyeCost, int bundleWeight, int maxUses, int villagerXp, @Nullable ItemStack cost) {
         this.fromItem = dye == null ? null : new ItemStack(dye);
         ItemStack bundle = new ItemStack(Items.BUNDLE, 1);
         if (dye != null) {
            BundleItem.setColour(bundle, dye.getDyeColor());
         }
         this.dyeCost = dyeCost;
         this.cost = cost;
         this.weight = bundleWeight;
         if (bundleWeight > 0) {
            BundleItem.setWeight(bundle, bundleWeight);
         }
         this.toItem = bundle;
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
      }

      private static DyeColor getRandomDye(Random random) {
         List<DyeColor> dyes = Arrays.stream(DyeColor.values())
                 .collect(Collectors.toList());
         return dyes.get(random.nextInt(dyes.size()));
      }

      @Nullable
      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         MerchantOffer offer;
         if (this.weight <= 0) {
            this.weight = new RandomValueRange(32, 88).getInt(random);
            BundleItem.setWeight(toItem, weight);
         }
         if (fromItem == null) {
            DyeColor color = getRandomDye(random);
            BundleItem.setColour(toItem, BundleColour.byDye(color));
            offer = new MerchantOffer(
                    cost != null ? cost : new ItemStack(Items.LEATHER, new RandomValueRange(8, 12).getInt(random)),
                    new ItemStack(DyeItem.byColor(color), dyeCost),
                    toItem,
                    this.maxUses,
                    this.villagerXp,
                    this.priceMultiplier
            );
         } else {
            fromItem.setCount(dyeCost);
            offer = new MerchantOffer(
                    cost != null ? cost : new ItemStack(Items.LEATHER, new RandomValueRange(8, 12).getInt(random)),
                    fromItem,
                    toItem,
                    this.maxUses,
                    this.villagerXp,
                    this.priceMultiplier
            );
         }

         return offer;
      }
   }

   static class ItemsForItemsTrade implements VillagerTrades.ITrade {
      private final ItemStack fromItem;
      private final int fromCount;
      private final ItemStack toItem;
      private final int toCount;
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;

      public ItemsForItemsTrade(IItemProvider fromItemProvider, int fromCount, Item toItem, int toCount, int maxUses, int villagerXp) {
         this.fromItem = new ItemStack(fromItemProvider);
         this.fromCount = fromCount;
         this.toItem = new ItemStack(toItem);
         this.toCount = toCount;
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = 0.05F;
      }

      @Nullable
      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         MerchantOffer merchantOffer = new MerchantOffer(
                 new ItemStack(this.fromItem.getItem(), this.fromCount),
                 new ItemStack(this.toItem.getItem(), this.toCount),
                 this.maxUses,
                 this.villagerXp,
                 this.priceMultiplier
         );
         merchantOffer.rewardExp = false;
         return merchantOffer;
      }
   }



   static class ItemsForEmeraldsTrade implements VillagerTrades.ITrade {
      final ItemStack itemStack;
      final int emeraldCost;
      final int numberOfItems;
      final int maxUses;
      final int villagerXp;
      final float priceMultiplier;

      public ItemsForEmeraldsTrade(Block block, int emeraldCost, int numberOfItems, int maxUses, int villagerXp) {
         this(new ItemStack(block), emeraldCost, numberOfItems, maxUses, villagerXp);
      }


      public ItemsForEmeraldsTrade(IItemProvider block, int emeraldCost, int numberOfItems, int maxUses, int villagerXp) {
         this(new ItemStack(block), emeraldCost, numberOfItems, maxUses, villagerXp);
      }

      public ItemsForEmeraldsTrade(Item item, int emeraldCost, int numberOfItems, int villagerXp) {
         this(new ItemStack(item), emeraldCost, numberOfItems, 12, villagerXp);
      }

      public ItemsForEmeraldsTrade(Item item, int emeraldCost, int numberOfItems, int maxUses, int villagerXp) {
         this(new ItemStack(item), emeraldCost, numberOfItems, maxUses, villagerXp);
      }

      public ItemsForEmeraldsTrade(ItemStack itemStack, int emeraldCost, int numberOfItems, int maxUses, int villagerXp) {
         this(itemStack, emeraldCost, numberOfItems, maxUses, villagerXp, 0.05F);
      }

      public ItemsForEmeraldsTrade(ItemStack itemStack, int emeraldCost, int numberOfItems, int maxUses, int villagerXp, float priceMultiplier) {
         this.itemStack = itemStack;
         this.emeraldCost = emeraldCost;
         this.numberOfItems = numberOfItems;
         this.maxUses = maxUses;
         this.villagerXp = villagerXp;
         this.priceMultiplier = priceMultiplier;
      }

      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         return new MerchantOffer(
                 new ItemStack(Items.EMERALD, this.emeraldCost),
                 new ItemStack(this.itemStack.getItem(), this.numberOfItems),
                 this.maxUses,
                 this.villagerXp,
                 this.priceMultiplier
         );
      }
   }

   static class DamageableItemForEmeraldsTrade extends ItemsForEmeraldsTrade {
      private final RandomValueRange valueRange;

      public DamageableItemForEmeraldsTrade(Item item, int emeraldCost, int maxUses, int villagerXp, RandomValueRange randomValueRange) {
         this(new ItemStack(item), emeraldCost, maxUses, villagerXp, randomValueRange);
      }

      public DamageableItemForEmeraldsTrade(ItemStack itemStack, int emeraldCost, int maxUses, int villagerXp, RandomValueRange randomValueRange) {
         this(itemStack, emeraldCost, maxUses, villagerXp, 0.05F, randomValueRange);
      }

      public DamageableItemForEmeraldsTrade(ItemStack itemStack, int emeraldCost, int maxUses, int villagerXp, float priceMultiplier, RandomValueRange randomValueRange) {
          super(itemStack, emeraldCost, 1, maxUses, villagerXp, priceMultiplier);
          if (!itemStack.isDamageableItem()) {
             throw new IllegalArgumentException("This is for damageable items!");
          }

          this.valueRange = randomValueRange;
      }

      @Override
      public MerchantOffer getOffer(Entity trader, Random random) {
         ItemStack stack = new ItemStack(this.itemStack.getItem(), 1);

         stack.setDamageValue(MathHelper.floor((1.0F - this.valueRange.getFloat(random)) * (float) stack.getMaxDamage()));

         return new MerchantOffer(
                 new ItemStack(Items.EMERALD, this.emeraldCost),
                 stack,
                 this.maxUses,
                 this.villagerXp,
                 this.priceMultiplier
         );
      }
   }




   static class SuspiciousStewForEmeraldTrade implements VillagerTrades.ITrade {
      final Effect effect;
      final int duration;
      final int xp;
      private final float priceMultiplier;

      public SuspiciousStewForEmeraldTrade(Effect p_i50527_1_, int p_i50527_2_, int p_i50527_3_) {
         this.effect = p_i50527_1_;
         this.duration = p_i50527_2_;
         this.xp = p_i50527_3_;
         this.priceMultiplier = 0.05F;
      }

      @Nullable
      public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
         ItemStack itemstack = new ItemStack(Items.SUSPICIOUS_STEW, 1);
         SuspiciousStewItem.saveMobEffect(itemstack, this.effect, this.duration);
         return new MerchantOffer(new ItemStack(Items.EMERALD, 1), itemstack, 12, this.xp, this.priceMultiplier);
      }
   }


   public static class Builder {
      private final LevelTrades levelOneTrades = new LevelTrades();
      private final LevelTrades levelTwoTrades = new LevelTrades();
      private final LevelTrades levelThreeTrades = new LevelTrades();
      private final LevelTrades levelFourTrades = new LevelTrades();
      private final LevelTrades levelFiveTrades = new LevelTrades();

      public static Builder builder() {
         return new Builder();
      }

      private Builder() {

      }

      public Builder addLevelOneTrade(ITrade trade) {
         this.levelOneTrades.addTrade(trade);
         return this;
      }

      public Builder addLevelTwoTrade(ITrade trade) {
         this.levelTwoTrades.addTrade(trade);
         return this;
      }

      public Builder addLevelThreeTrade(ITrade trade) {
         this.levelThreeTrades.addTrade(trade);
         return this;
      }

      public Builder addLevelFourTrade(ITrade trade) {
         this.levelFourTrades.addTrade(trade);
         return this;
      }

      public Builder addLevelFiveTrade(ITrade trade) {
         this.levelFiveTrades.addTrade(trade);
         return this;
      }

      public Builder addLevelOneTrades(List<ITrade> trades) {
         trades.forEach(this.levelOneTrades::addTrade);
         return this;
      }

      public Builder addLevelTwoTrades(List<ITrade> trades) {
         trades.forEach(this.levelTwoTrades::addTrade);
         return this;
      }

      public Builder addLevelThreeTrades(List<ITrade> trades) {
         trades.forEach(this.levelThreeTrades::addTrade);
         return this;
      }

      public Builder addLevelFourTrades(List<ITrade> trades) {
         trades.forEach(this.levelFourTrades::addTrade);
         return this;
      }

      public Builder addLevelFiveTrades(List<ITrade> trades) {
         trades.forEach(this.levelFiveTrades::addTrade);
         return this;
      }

      public Int2ObjectMap<VillagerTrades.ITrade[]> build() {
         return new Int2ObjectOpenHashMap<>(ImmutableMap.of(
                 1, this.levelOneTrades.getTrades().toArray(new VillagerTrades.ITrade[0]),
                 2, this.levelTwoTrades.getTrades().toArray(new VillagerTrades.ITrade[0]),
                 3, this.levelThreeTrades.getTrades().toArray(new VillagerTrades.ITrade[0]),
                 4, this.levelFourTrades.getTrades().toArray(new VillagerTrades.ITrade[0]),
                 5, this.levelFiveTrades.getTrades().toArray(new VillagerTrades.ITrade[0])
         ));
      }
   }

   private static class LevelTrades {
      private final List<VillagerTrades.ITrade> trades = new ArrayList<>();

      public List<ITrade> getTrades() {
         return trades;
      }

      public void addTrade(VillagerTrades.ITrade trade) {
         this.trades.add(trade);
      }
   }
}