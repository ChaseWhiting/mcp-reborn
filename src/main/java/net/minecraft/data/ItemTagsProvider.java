package net.minecraft.data;

import java.nio.file.Path;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ItemTagsProvider extends TagsProvider<Item> {
   private final Function<ITag.INamedTag<Block>, ITag.Builder> blockTags;

   public ItemTagsProvider(DataGenerator p_i232552_1_, BlockTagsProvider p_i232552_2_) {
      super(p_i232552_1_, Registry.ITEM);
      this.blockTags = p_i232552_2_::getOrCreateRawBuilder;
   }

   protected void addTags() {
      this.copy(BlockTags.WOOL, ItemTags.WOOL);
      this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
      this.copy(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
      this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
      this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
      this.copy(BlockTags.CARPETS, ItemTags.CARPETS);
      this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
      this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
      this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
      this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
      this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
      this.copy(BlockTags.DOORS, ItemTags.DOORS);
      this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
      this.copy(BlockTags.OAK_LOGS, ItemTags.OAK_LOGS);
      this.copy(BlockTags.DARK_OAK_LOGS, ItemTags.DARK_OAK_LOGS);
      this.copy(BlockTags.BIRCH_LOGS, ItemTags.BIRCH_LOGS);
      this.copy(BlockTags.ACACIA_LOGS, ItemTags.ACACIA_LOGS);
      this.copy(BlockTags.SPRUCE_LOGS, ItemTags.SPRUCE_LOGS);
      this.copy(BlockTags.JUNGLE_LOGS, ItemTags.JUNGLE_LOGS);
      this.copy(BlockTags.CRIMSON_STEMS, ItemTags.CRIMSON_STEMS);
      this.copy(BlockTags.WARPED_STEMS, ItemTags.WARPED_STEMS);
      this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
      this.copy(BlockTags.LOGS, ItemTags.LOGS);
      this.copy(BlockTags.SAND, ItemTags.SAND);
      this.copy(BlockTags.SLABS, ItemTags.SLABS);
      this.copy(BlockTags.WALLS, ItemTags.WALLS);
      this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
      this.copy(BlockTags.ANVIL, ItemTags.ANVIL);
      this.copy(BlockTags.RAILS, ItemTags.RAILS);
      this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
      this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
      this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
      this.copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
      this.copy(BlockTags.BEDS, ItemTags.BEDS);
      this.copy(BlockTags.FENCES, ItemTags.FENCES);
      this.copy(BlockTags.TALL_FLOWERS, ItemTags.TALL_FLOWERS);
      this.copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
      this.copy(BlockTags.GOLD_ORES, ItemTags.GOLD_ORES);
      this.copy(BlockTags.SOUL_FIRE_BASE_BLOCKS, ItemTags.SOUL_FIRE_BASE_BLOCKS);
      this.tag(ItemTags.BANNERS).add(Items.WHITE_BANNER, Items.ORANGE_BANNER, Items.MAGENTA_BANNER, Items.LIGHT_BLUE_BANNER, Items.YELLOW_BANNER, Items.LIME_BANNER, Items.PINK_BANNER, Items.GRAY_BANNER, Items.LIGHT_GRAY_BANNER, Items.CYAN_BANNER, Items.PURPLE_BANNER, Items.BLUE_BANNER, Items.BROWN_BANNER, Items.GREEN_BANNER, Items.RED_BANNER, Items.BLACK_BANNER);
      this.tag(ItemTags.BOATS).add(Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT);
      this.tag(ItemTags.FISHES).add(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH);
      this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
      this.tag(ItemTags.CREEPER_DROP_MUSIC_DISCS).add(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT);
      this.tag(ItemTags.MUSIC_DISCS).addTag(ItemTags.CREEPER_DROP_MUSIC_DISCS).add(Items.MUSIC_DISC_PIGSTEP);
      this.tag(ItemTags.COALS).add(Items.COAL, Items.CHARCOAL);
      this.tag(ItemTags.ARROWS).add(Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW, Items.BONE_ARROW, Items.POISON_ARROW, Items.BURNING_ARROW, Items.TELEPORTATION_ARROW, Items.HEALING_ARROW, Items.FIREWORK_ARROW, Items.GILDED_ARROW);
      this.tag(ItemTags.LECTERN_BOOKS).add(Items.WRITTEN_BOOK, Items.WRITABLE_BOOK);
      this.tag(ItemTags.BEACON_PAYMENT_ITEMS).add(Items.NETHERITE_INGOT, Items.EMERALD, Items.DIAMOND, Items.GOLD_INGOT, Items.IRON_INGOT);
      this.tag(ItemTags.PIGLIN_REPELLENTS).add(Items.SOUL_TORCH).add(Items.SOUL_LANTERN).add(Items.SOUL_CAMPFIRE);
      this.tag(ItemTags.PIGLIN_LOVED).addTag(ItemTags.GOLD_ORES).add(Items.GOLD_BLOCK, Items.GILDED_BLACKSTONE, Items.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT, Items.BELL, Items.CLOCK, Items.GOLDEN_CARROT, Items.GLISTERING_MELON_SLICE, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR, Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GILDED_ARROW, Items.GILDED_CROSSBOW);
      this.tag(ItemTags.NON_FLAMMABLE_WOOD).add(Items.WARPED_STEM, Items.STRIPPED_WARPED_STEM, Items.WARPED_HYPHAE, Items.STRIPPED_WARPED_HYPHAE, Items.CRIMSON_STEM, Items.STRIPPED_CRIMSON_STEM, Items.CRIMSON_HYPHAE, Items.STRIPPED_CRIMSON_HYPHAE, Items.CRIMSON_PLANKS, Items.WARPED_PLANKS, Items.CRIMSON_SLAB, Items.WARPED_SLAB, Items.CRIMSON_PRESSURE_PLATE, Items.WARPED_PRESSURE_PLATE, Items.CRIMSON_FENCE, Items.WARPED_FENCE, Items.CRIMSON_TRAPDOOR, Items.WARPED_TRAPDOOR, Items.CRIMSON_FENCE_GATE, Items.WARPED_FENCE_GATE, Items.CRIMSON_STAIRS, Items.WARPED_STAIRS, Items.CRIMSON_BUTTON, Items.WARPED_BUTTON, Items.CRIMSON_DOOR, Items.WARPED_DOOR, Items.CRIMSON_SIGN, Items.WARPED_SIGN);
      this.tag(ItemTags.STONE_TOOL_MATERIALS).add(Items.COBBLESTONE, Items.BLACKSTONE);
      this.tag(ItemTags.STONE_CRAFTING_MATERIALS).add(Items.COBBLESTONE, Items.BLACKSTONE);
   }

   protected void copy(ITag.INamedTag<Block> p_240521_1_, ITag.INamedTag<Item> p_240521_2_) {
      ITag.Builder itag$builder = this.getOrCreateRawBuilder(p_240521_2_);
      ITag.Builder itag$builder1 = this.blockTags.apply(p_240521_1_);
      itag$builder1.getEntries().forEach(itag$builder::add);
   }

   protected Path getPath(ResourceLocation p_200431_1_) {
      return this.generator.getOutputFolder().resolve("data/" + p_200431_1_.getNamespace() + "/tags/items/" + p_200431_1_.getPath() + ".json");
   }

   public String getName() {
      return "Item Tags";
   }
}