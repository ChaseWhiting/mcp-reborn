package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.bundle.BundleItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowType;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.dagger.DesolateDaggerItem;
import net.minecraft.item.dyeable.PaintTubeItem;
import net.minecraft.item.equipment.trim.SmithingTemplateItem;
import net.minecraft.item.equipment.trim.ToolTrimPatterns;
import net.minecraft.item.equipment.trim.TrimPatterns;
import net.minecraft.item.tool.*;
import net.minecraft.item.tool.terraria.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pokemon.entity.Pokemon;
import net.minecraft.pokemon.item.pokeball.MasterBallItem;
import net.minecraft.pokemon.item.pokeball.PokeballItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.terraria.item.OneDropGroup;
import net.minecraft.terraria.item.TreasureBagItem;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.AbstractCrossbowBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
@SuppressWarnings("all")
public class Items {
   public static final Item AIR = registerBlock(Blocks.AIR, new AirItem(Blocks.AIR, new Item.Properties()));
   public static final Item DECORATED_POT = registerBlock(Blocks.DECORATED_POT);
   public static final Item STONE = registerBlock(Blocks.STONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item GRANITE = registerBlock(Blocks.GRANITE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_GRANITE = registerBlock(Blocks.POLISHED_GRANITE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DIORITE = registerBlock(Blocks.DIORITE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_DIORITE = registerBlock(Blocks.POLISHED_DIORITE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item ANDESITE = registerBlock(Blocks.ANDESITE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_ANDESITE = registerBlock(Blocks.POLISHED_ANDESITE, ItemGroup.TAB_BUILDING_BLOCKS);


   public static final Item GRASS_BLOCK = registerBlock(Blocks.GRASS_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DIRT = registerBlock(Blocks.DIRT, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item MUD = registerBlock(Blocks.MUD, ItemGroup.TAB_BUILDING_BLOCKS);





   public static final Item COARSE_DIRT = registerBlock(Blocks.COARSE_DIRT, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PODZOL = registerBlock(Blocks.PODZOL, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CRIMSON_NYLIUM = registerBlock(Blocks.CRIMSON_NYLIUM, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item WARPED_NYLIUM = registerBlock(Blocks.WARPED_NYLIUM, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item COBBLESTONE = registerBlock(Blocks.COBBLESTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item OAK_PLANKS = registerBlock(Blocks.OAK_PLANKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PALE_OAK_PLANKS = registerBlock(Blocks.PALE_OAK_PLANKS, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item SPRUCE_PLANKS = registerBlock(Blocks.SPRUCE_PLANKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BIRCH_PLANKS = registerBlock(Blocks.BIRCH_PLANKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item JUNGLE_PLANKS = registerBlock(Blocks.JUNGLE_PLANKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item ACACIA_PLANKS = registerBlock(Blocks.ACACIA_PLANKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DARK_OAK_PLANKS = registerBlock(Blocks.DARK_OAK_PLANKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CRIMSON_PLANKS = registerBlock(Blocks.CRIMSON_PLANKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item WARPED_PLANKS = registerBlock(Blocks.WARPED_PLANKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item OAK_SAPLING = registerBlock(Blocks.OAK_SAPLING, ItemGroup.TAB_NATURAL);
   public static final Item APPLE_SAPLING = registerBlock(Blocks.APPLE_SAPLING, ItemGroup.TAB_NATURAL);

   public static final Item DEAD_SAPLING = registerBlock(Blocks.DEAD_SAPLING, ItemGroup.TAB_NATURAL);

   public static final Item PALE_OAK_SAPLING = registerBlock(Blocks.PALE_OAK_SAPLING, ItemGroup.TAB_NATURAL);


   public static final Item SPRUCE_SAPLING = registerBlock(Blocks.SPRUCE_SAPLING, ItemGroup.TAB_NATURAL);
   public static final Item BIRCH_SAPLING = registerBlock(Blocks.BIRCH_SAPLING, ItemGroup.TAB_NATURAL);
   public static final Item JUNGLE_SAPLING = registerBlock(Blocks.JUNGLE_SAPLING, ItemGroup.TAB_NATURAL);
   public static final Item ACACIA_SAPLING = registerBlock(Blocks.ACACIA_SAPLING, ItemGroup.TAB_NATURAL);
   public static final Item DARK_OAK_SAPLING = registerBlock(Blocks.DARK_OAK_SAPLING, ItemGroup.TAB_NATURAL);
   public static final Item BEDROCK = registerBlock(Blocks.BEDROCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SAND = registerBlock(Blocks.SAND, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item RED_SAND = registerBlock(Blocks.RED_SAND, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item GRAVEL = registerBlock(Blocks.GRAVEL, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item GOLD_ORE = registerBlock(Blocks.GOLD_ORE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SUSPICIOUS_SAND = registerBlock(Blocks.SUSPICIOUS_SAND, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SUSPICIOUS_GRAVEL = registerBlock(Blocks.SUSPICIOUS_GRAVEL, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SUSPICIOUS_CLAY = registerBlock(Blocks.SUSPICIOUS_CLAY, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SUSPICIOUS_SOUL_SOIL = registerBlock(Blocks.SUSPICIOUS_SOUL_SOIL, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SUSPICIOUS_CRACKED_NETHER_BRICKS = registerBlock(Blocks.SUSPICIOUS_CRACKED_NETHER_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item COPPER_ORE = registerBlock(Blocks.COPPER_ORE, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item IRON_ORE = registerBlock(Blocks.IRON_ORE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item COAL_ORE = registerBlock(Blocks.COAL_ORE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item NETHER_GOLD_ORE = registerBlock(Blocks.NETHER_GOLD_ORE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item OAK_LOG = registerBlock(Blocks.OAK_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SPRUCE_LOG = registerBlock(Blocks.SPRUCE_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BIRCH_LOG = registerBlock(Blocks.BIRCH_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item JUNGLE_LOG = registerBlock(Blocks.JUNGLE_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item ACACIA_LOG = registerBlock(Blocks.ACACIA_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DARK_OAK_LOG = registerBlock(Blocks.DARK_OAK_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PALE_OAK_LOG = registerBlock(Blocks.PALE_OAK_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PALE_OAK_WOOD = registerBlock(Blocks.PALE_OAK_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item PALE_OAK_TRAPDOOR = registerBlock(Blocks.PALE_OAK_TRAPDOOR, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PALE_OAK_LEAVES = registerBlock(Blocks.PALE_OAK_LEAVES, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEAD_LEAVES = registerBlock(Blocks.DEAD_LEAVES, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item CRIMSON_STEM = registerBlock(Blocks.CRIMSON_STEM, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item WARPED_STEM = registerBlock(Blocks.WARPED_STEM, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_OAK_LOG = registerBlock(Blocks.STRIPPED_OAK_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_PALE_LOG = registerBlock(Blocks.STRIPPED_PALE_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_SPRUCE_LOG = registerBlock(Blocks.STRIPPED_SPRUCE_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_BIRCH_LOG = registerBlock(Blocks.STRIPPED_BIRCH_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_JUNGLE_LOG = registerBlock(Blocks.STRIPPED_JUNGLE_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_ACACIA_LOG = registerBlock(Blocks.STRIPPED_ACACIA_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_DARK_OAK_LOG = registerBlock(Blocks.STRIPPED_DARK_OAK_LOG, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_CRIMSON_STEM = registerBlock(Blocks.STRIPPED_CRIMSON_STEM, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_WARPED_STEM = registerBlock(Blocks.STRIPPED_WARPED_STEM, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_OAK_WOOD = registerBlock(Blocks.STRIPPED_OAK_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_PALE_WOOD = registerBlock(Blocks.STRIPPED_PALE_OAK_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item STRIPPED_SPRUCE_WOOD = registerBlock(Blocks.STRIPPED_SPRUCE_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_BIRCH_WOOD = registerBlock(Blocks.STRIPPED_BIRCH_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_JUNGLE_WOOD = registerBlock(Blocks.STRIPPED_JUNGLE_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_ACACIA_WOOD = registerBlock(Blocks.STRIPPED_ACACIA_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_DARK_OAK_WOOD = registerBlock(Blocks.STRIPPED_DARK_OAK_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_CRIMSON_HYPHAE = registerBlock(Blocks.STRIPPED_CRIMSON_HYPHAE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRIPPED_WARPED_HYPHAE = registerBlock(Blocks.STRIPPED_WARPED_HYPHAE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item OAK_WOOD = registerBlock(Blocks.OAK_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SPRUCE_WOOD = registerBlock(Blocks.SPRUCE_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BIRCH_WOOD = registerBlock(Blocks.BIRCH_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item JUNGLE_WOOD = registerBlock(Blocks.JUNGLE_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item ACACIA_WOOD = registerBlock(Blocks.ACACIA_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DARK_OAK_WOOD = registerBlock(Blocks.DARK_OAK_WOOD, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CRIMSON_HYPHAE = registerBlock(Blocks.CRIMSON_HYPHAE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item WARPED_HYPHAE = registerBlock(Blocks.WARPED_HYPHAE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item OAK_LEAVES = registerBlock(Blocks.OAK_LEAVES, ItemGroup.TAB_NATURAL);
   public static final Item APPLE_LEAVES = registerBlock(Blocks.APPLE_LEAVES, ItemGroup.TAB_NATURAL);

   public static final Item SPRUCE_LEAVES = registerBlock(Blocks.SPRUCE_LEAVES, ItemGroup.TAB_NATURAL);
   public static final Item BIRCH_LEAVES = registerBlock(Blocks.BIRCH_LEAVES, ItemGroup.TAB_NATURAL);
   public static final Item JUNGLE_LEAVES = registerBlock(Blocks.JUNGLE_LEAVES, ItemGroup.TAB_NATURAL);
   public static final Item ACACIA_LEAVES = registerBlock(Blocks.ACACIA_LEAVES, ItemGroup.TAB_NATURAL);
   public static final Item DARK_OAK_LEAVES = registerBlock(Blocks.DARK_OAK_LEAVES, ItemGroup.TAB_NATURAL);
   public static final Item SPONGE = registerBlock(Blocks.SPONGE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item WET_SPONGE = registerBlock(Blocks.WET_SPONGE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item LAPIS_ORE = registerBlock(Blocks.LAPIS_ORE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item LAPIS_BLOCK = registerBlock(Blocks.LAPIS_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DISPENSER = registerBlock(Blocks.DISPENSER, ItemGroup.TAB_FUNCTIONAL);
   public static final Item FAN_BLOCK_ITEM = registerBlock(Blocks.FAN_BLOCK, ItemGroup.TAB_FUNCTIONAL);
   public static final Item SANDSTONE = registerBlock(Blocks.SANDSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CHISELED_SANDSTONE = registerBlock(Blocks.CHISELED_SANDSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CUT_SANDSTONE = registerBlock(Blocks.CUT_SANDSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item NOTE_BLOCK = registerBlock(Blocks.NOTE_BLOCK, ItemGroup.TAB_FUNCTIONAL);
   public static final Item GOLDEN_POWERED_RAIL = registerBlock(Blocks.GOLDEN_POWERED_RAIL, ItemGroup.TAB_REDSTONE);
   public static final Item DETECTOR_RAIL = registerBlock(Blocks.DETECTOR_RAIL, ItemGroup.TAB_REDSTONE);
   public static final Item STICKY_PISTON = registerBlock(Blocks.STICKY_PISTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item COBWEB = registerBlock(Blocks.COBWEB, ItemGroup.TAB_NATURAL);
   public static final Item GRASS = registerBlock(Blocks.GRASS, ItemGroup.TAB_NATURAL);
   public static final Item FERN = registerBlock(Blocks.FERN, ItemGroup.TAB_NATURAL);
   public static final Item DEAD_BUSH = registerBlock(Blocks.DEAD_BUSH, ItemGroup.TAB_NATURAL);
   public static final Item SEAGRASS = registerBlock(Blocks.SEAGRASS, ItemGroup.TAB_NATURAL);
   public static final Item SEA_PICKLE = registerBlock(Blocks.SEA_PICKLE, ItemGroup.TAB_NATURAL);
   public static final Item PISTON = registerBlock(Blocks.PISTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item BUCKET = registerItem("bucket", new BucketItem(Fluids.EMPTY, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_MISC)));
   public static final Item GLASS_BOTTLE = registerItem("glass_bottle", new GlassBottleItem((new Item.Properties()).tab(ItemGroup.TAB_MISC)));

   // COLOURED BLOCKS
   public static final Item WHITE_WOOL = colouredBlock(Blocks.WHITE_WOOL);
   public static final Item LIGHT_GRAY_WOOL = colouredBlock(Blocks.LIGHT_GRAY_WOOL);
   public static final Item GRAY_WOOL = colouredBlock(Blocks.GRAY_WOOL);
   public static final Item BLACK_WOOL = colouredBlock(Blocks.BLACK_WOOL);
   public static final Item BROWN_WOOL = colouredBlock(Blocks.BROWN_WOOL);
   public static final Item RED_WOOL = colouredBlock(Blocks.RED_WOOL);
   public static final Item ORANGE_WOOL = colouredBlock(Blocks.ORANGE_WOOL);
   public static final Item YELLOW_WOOL = colouredBlock(Blocks.YELLOW_WOOL);
   public static final Item LIME_WOOL = colouredBlock(Blocks.LIME_WOOL);
   public static final Item GREEN_WOOL = colouredBlock(Blocks.GREEN_WOOL);
   public static final Item CYAN_WOOL = colouredBlock(Blocks.CYAN_WOOL);
   public static final Item LIGHT_BLUE_WOOL = colouredBlock(Blocks.LIGHT_BLUE_WOOL);
   public static final Item BLUE_WOOL = colouredBlock(Blocks.BLUE_WOOL);
   public static final Item PURPLE_WOOL = colouredBlock(Blocks.PURPLE_WOOL);
   public static final Item MAGENTA_WOOL = colouredBlock(Blocks.MAGENTA_WOOL);
   public static final Item PINK_WOOL = colouredBlock(Blocks.PINK_WOOL);

   public static final Item WHITE_CARPET = registerBlock(Blocks.WHITE_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_GRAY_CARPET = registerBlock(Blocks.LIGHT_GRAY_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GRAY_CARPET = registerBlock(Blocks.GRAY_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLACK_CARPET = registerBlock(Blocks.BLACK_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BROWN_CARPET = registerBlock(Blocks.BROWN_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item RED_CARPET = registerBlock(Blocks.RED_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item ORANGE_CARPET = registerBlock(Blocks.ORANGE_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item YELLOW_CARPET = registerBlock(Blocks.YELLOW_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIME_CARPET = registerBlock(Blocks.LIME_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GREEN_CARPET = registerBlock(Blocks.GREEN_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item CYAN_CARPET = registerBlock(Blocks.CYAN_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_BLUE_CARPET = registerBlock(Blocks.LIGHT_BLUE_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLUE_CARPET = registerBlock(Blocks.BLUE_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PURPLE_CARPET = registerBlock(Blocks.PURPLE_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item MAGENTA_CARPET = registerBlock(Blocks.MAGENTA_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PINK_CARPET = registerBlock(Blocks.PINK_CARPET, ItemGroup.TAB_COLOURED_BLOCKS);

   public static final Item TERRACOTTA = registerBlock(Blocks.TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item WHITE_TERRACOTTA = registerBlock(Blocks.WHITE_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_GRAY_TERRACOTTA = registerBlock(Blocks.LIGHT_GRAY_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GRAY_TERRACOTTA = registerBlock(Blocks.GRAY_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLACK_TERRACOTTA = registerBlock(Blocks.BLACK_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BROWN_TERRACOTTA = registerBlock(Blocks.BROWN_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item RED_TERRACOTTA = registerBlock(Blocks.RED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item ORANGE_TERRACOTTA = registerBlock(Blocks.ORANGE_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item YELLOW_TERRACOTTA = registerBlock(Blocks.YELLOW_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIME_TERRACOTTA = registerBlock(Blocks.LIME_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GREEN_TERRACOTTA = registerBlock(Blocks.GREEN_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item CYAN_TERRACOTTA = registerBlock(Blocks.CYAN_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_BLUE_TERRACOTTA = registerBlock(Blocks.LIGHT_BLUE_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLUE_TERRACOTTA = registerBlock(Blocks.BLUE_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PURPLE_TERRACOTTA = registerBlock(Blocks.PURPLE_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item MAGENTA_TERRACOTTA = registerBlock(Blocks.MAGENTA_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PINK_TERRACOTTA = registerBlock(Blocks.PINK_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);


   public static final Item WHITE_CONCRETE = registerBlock(Blocks.WHITE_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_GRAY_CONCRETE = registerBlock(Blocks.LIGHT_GRAY_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GRAY_CONCRETE = registerBlock(Blocks.GRAY_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLACK_CONCRETE = registerBlock(Blocks.BLACK_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BROWN_CONCRETE = registerBlock(Blocks.BROWN_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item RED_CONCRETE = registerBlock(Blocks.RED_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item ORANGE_CONCRETE = registerBlock(Blocks.ORANGE_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item YELLOW_CONCRETE = registerBlock(Blocks.YELLOW_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIME_CONCRETE = registerBlock(Blocks.LIME_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GREEN_CONCRETE = registerBlock(Blocks.GREEN_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item CYAN_CONCRETE = registerBlock(Blocks.CYAN_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_BLUE_CONCRETE = registerBlock(Blocks.LIGHT_BLUE_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLUE_CONCRETE = registerBlock(Blocks.BLUE_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PURPLE_CONCRETE = registerBlock(Blocks.PURPLE_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item MAGENTA_CONCRETE = registerBlock(Blocks.MAGENTA_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PINK_CONCRETE = registerBlock(Blocks.PINK_CONCRETE, ItemGroup.TAB_COLOURED_BLOCKS);

   public static final Item WHITE_CONCRETE_POWDER = registerBlock(Blocks.WHITE_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_GRAY_CONCRETE_POWDER = registerBlock(Blocks.LIGHT_GRAY_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GRAY_CONCRETE_POWDER = registerBlock(Blocks.GRAY_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLACK_CONCRETE_POWDER = registerBlock(Blocks.BLACK_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BROWN_CONCRETE_POWDER = registerBlock(Blocks.BROWN_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item RED_CONCRETE_POWDER = registerBlock(Blocks.RED_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item ORANGE_CONCRETE_POWDER = registerBlock(Blocks.ORANGE_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item YELLOW_CONCRETE_POWDER = registerBlock(Blocks.YELLOW_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIME_CONCRETE_POWDER = registerBlock(Blocks.LIME_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GREEN_CONCRETE_POWDER = registerBlock(Blocks.GREEN_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item CYAN_CONCRETE_POWDER = registerBlock(Blocks.CYAN_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_BLUE_CONCRETE_POWDER = registerBlock(Blocks.LIGHT_BLUE_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLUE_CONCRETE_POWDER = registerBlock(Blocks.BLUE_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PURPLE_CONCRETE_POWDER = registerBlock(Blocks.PURPLE_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item MAGENTA_CONCRETE_POWDER = registerBlock(Blocks.MAGENTA_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PINK_CONCRETE_POWDER = registerBlock(Blocks.PINK_CONCRETE_POWDER, ItemGroup.TAB_COLOURED_BLOCKS);




   public static final Item WHITE_GLAZED_TERRACOTTA = registerBlock(Blocks.WHITE_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_GRAY_GLAZED_TERRACOTTA = registerBlock(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GRAY_GLAZED_TERRACOTTA = registerBlock(Blocks.GRAY_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLACK_GLAZED_TERRACOTTA = registerBlock(Blocks.BLACK_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BROWN_GLAZED_TERRACOTTA = registerBlock(Blocks.BROWN_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item RED_GLAZED_TERRACOTTA = registerBlock(Blocks.RED_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item ORANGE_GLAZED_TERRACOTTA = registerBlock(Blocks.ORANGE_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item YELLOW_GLAZED_TERRACOTTA = registerBlock(Blocks.YELLOW_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIME_GLAZED_TERRACOTTA = registerBlock(Blocks.LIME_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GREEN_GLAZED_TERRACOTTA = registerBlock(Blocks.GREEN_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item CYAN_GLAZED_TERRACOTTA = registerBlock(Blocks.CYAN_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_BLUE_GLAZED_TERRACOTTA = registerBlock(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLUE_GLAZED_TERRACOTTA = registerBlock(Blocks.BLUE_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PURPLE_GLAZED_TERRACOTTA = registerBlock(Blocks.PURPLE_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item MAGENTA_GLAZED_TERRACOTTA = registerBlock(Blocks.MAGENTA_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PINK_GLAZED_TERRACOTTA = registerBlock(Blocks.PINK_GLAZED_TERRACOTTA, ItemGroup.TAB_COLOURED_BLOCKS);

   public static final Item GLASS = registerBlock(Blocks.GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item TINTED_GLASS = registerBlock(Blocks.TINTED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);

   public static final Item WHITE_STAINED_GLASS = registerBlock(Blocks.WHITE_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_GRAY_STAINED_GLASS = registerBlock(Blocks.LIGHT_GRAY_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GRAY_STAINED_GLASS = registerBlock(Blocks.GRAY_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLACK_STAINED_GLASS = registerBlock(Blocks.BLACK_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BROWN_STAINED_GLASS = registerBlock(Blocks.BROWN_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item RED_STAINED_GLASS = registerBlock(Blocks.RED_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item ORANGE_STAINED_GLASS = registerBlock(Blocks.ORANGE_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item YELLOW_STAINED_GLASS = registerBlock(Blocks.YELLOW_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIME_STAINED_GLASS = registerBlock(Blocks.LIME_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GREEN_STAINED_GLASS = registerBlock(Blocks.GREEN_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item CYAN_STAINED_GLASS = registerBlock(Blocks.CYAN_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_BLUE_STAINED_GLASS = registerBlock(Blocks.LIGHT_BLUE_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLUE_STAINED_GLASS = registerBlock(Blocks.BLUE_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PURPLE_STAINED_GLASS = registerBlock(Blocks.PURPLE_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item MAGENTA_STAINED_GLASS = registerBlock(Blocks.MAGENTA_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PINK_STAINED_GLASS = registerBlock(Blocks.PINK_STAINED_GLASS, ItemGroup.TAB_COLOURED_BLOCKS);

   public static final Item GLASS_PANE = registerBlock(Blocks.GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);

   public static final Item WHITE_STAINED_GLASS_PANE = registerBlock(Blocks.WHITE_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_GRAY_STAINED_GLASS_PANE = registerBlock(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GRAY_STAINED_GLASS_PANE = registerBlock(Blocks.GRAY_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLACK_STAINED_GLASS_PANE = registerBlock(Blocks.BLACK_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BROWN_STAINED_GLASS_PANE = registerBlock(Blocks.BROWN_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item RED_STAINED_GLASS_PANE = registerBlock(Blocks.RED_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item ORANGE_STAINED_GLASS_PANE = registerBlock(Blocks.ORANGE_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item YELLOW_STAINED_GLASS_PANE = registerBlock(Blocks.YELLOW_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIME_STAINED_GLASS_PANE = registerBlock(Blocks.LIME_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item GREEN_STAINED_GLASS_PANE = registerBlock(Blocks.GREEN_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item CYAN_STAINED_GLASS_PANE = registerBlock(Blocks.CYAN_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item LIGHT_BLUE_STAINED_GLASS_PANE = registerBlock(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item BLUE_STAINED_GLASS_PANE = registerBlock(Blocks.BLUE_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PURPLE_STAINED_GLASS_PANE = registerBlock(Blocks.PURPLE_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item MAGENTA_STAINED_GLASS_PANE = registerBlock(Blocks.MAGENTA_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);
   public static final Item PINK_STAINED_GLASS_PANE = registerBlock(Blocks.PINK_STAINED_GLASS_PANE, ItemGroup.TAB_COLOURED_BLOCKS);


   public static final Item SHULKER_BOX = registerBlock(new BlockItem(Blocks.SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item WHITE_SHULKER_BOX = registerBlock(new BlockItem(Blocks.WHITE_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item LIGHT_GRAY_SHULKER_BOX = registerBlock(new BlockItem(Blocks.LIGHT_GRAY_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item GRAY_SHULKER_BOX = registerBlock(new BlockItem(Blocks.GRAY_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item BLACK_SHULKER_BOX = registerBlock(new BlockItem(Blocks.BLACK_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item BROWN_SHULKER_BOX = registerBlock(new BlockItem(Blocks.BROWN_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item RED_SHULKER_BOX = registerBlock(new BlockItem(Blocks.RED_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item ORANGE_SHULKER_BOX = registerBlock(new BlockItem(Blocks.ORANGE_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item YELLOW_SHULKER_BOX = registerBlock(new BlockItem(Blocks.YELLOW_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item LIME_SHULKER_BOX = registerBlock(new BlockItem(Blocks.LIME_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item GREEN_SHULKER_BOX = registerBlock(new BlockItem(Blocks.GREEN_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item CYAN_SHULKER_BOX = registerBlock(new BlockItem(Blocks.CYAN_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item LIGHT_BLUE_SHULKER_BOX = registerBlock(new BlockItem(Blocks.LIGHT_BLUE_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item BLUE_SHULKER_BOX = registerBlock(new BlockItem(Blocks.BLUE_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item PURPLE_SHULKER_BOX = registerBlock(new BlockItem(Blocks.PURPLE_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item MAGENTA_SHULKER_BOX = registerBlock(new BlockItem(Blocks.MAGENTA_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item PINK_SHULKER_BOX = registerBlock(new BlockItem(Blocks.PINK_SHULKER_BOX, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));

   public static final Item WHITE_BED = registerBlock(new BedItem(Blocks.WHITE_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item LIGHT_GRAY_BED = registerBlock(new BedItem(Blocks.LIGHT_GRAY_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item GRAY_BED = registerBlock(new BedItem(Blocks.GRAY_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item BLACK_BED = registerBlock(new BedItem(Blocks.BLACK_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item BROWN_BED = registerBlock(new BedItem(Blocks.BROWN_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item RED_BED = registerBlock(new BedItem(Blocks.RED_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item ORANGE_BED = registerBlock(new BedItem(Blocks.ORANGE_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item YELLOW_BED = registerBlock(new BedItem(Blocks.YELLOW_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item LIME_BED = registerBlock(new BedItem(Blocks.LIME_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item GREEN_BED = registerBlock(new BedItem(Blocks.GREEN_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item CYAN_BED = registerBlock(new BedItem(Blocks.CYAN_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item LIGHT_BLUE_BED = registerBlock(new BedItem(Blocks.LIGHT_BLUE_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item BLUE_BED = registerBlock(new BedItem(Blocks.BLUE_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item PURPLE_BED = registerBlock(new BedItem(Blocks.PURPLE_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item MAGENTA_BED = registerBlock(new BedItem(Blocks.MAGENTA_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item PINK_BED = registerBlock(new BedItem(Blocks.PINK_BED, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COLOURED_BLOCKS)));

   public static final Item WHITE_BANNER = registerItem("white_banner", new BannerItem(Blocks.WHITE_BANNER, Blocks.WHITE_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item LIGHT_GRAY_BANNER = registerItem("light_gray_banner", new BannerItem(Blocks.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item GRAY_BANNER = registerItem("gray_banner", new BannerItem(Blocks.GRAY_BANNER, Blocks.GRAY_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item BLACK_BANNER = registerItem("black_banner", new BannerItem(Blocks.BLACK_BANNER, Blocks.BLACK_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item BROWN_BANNER = registerItem("brown_banner", new BannerItem(Blocks.BROWN_BANNER, Blocks.BROWN_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item RED_BANNER = registerItem("red_banner", new BannerItem(Blocks.RED_BANNER, Blocks.RED_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item ORANGE_BANNER = registerItem("orange_banner", new BannerItem(Blocks.ORANGE_BANNER, Blocks.ORANGE_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item YELLOW_BANNER = registerItem("yellow_banner", new BannerItem(Blocks.YELLOW_BANNER, Blocks.YELLOW_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item LIME_BANNER = registerItem("lime_banner", new BannerItem(Blocks.LIME_BANNER, Blocks.LIME_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item GREEN_BANNER = registerItem("green_banner", new BannerItem(Blocks.GREEN_BANNER, Blocks.GREEN_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item CYAN_BANNER = registerItem("cyan_banner", new BannerItem(Blocks.CYAN_BANNER, Blocks.CYAN_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item LIGHT_BLUE_BANNER = registerItem("light_blue_banner", new BannerItem(Blocks.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item BLUE_BANNER = registerItem("blue_banner", new BannerItem(Blocks.BLUE_BANNER, Blocks.BLUE_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item PURPLE_BANNER = registerItem("purple_banner", new BannerItem(Blocks.PURPLE_BANNER, Blocks.PURPLE_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item MAGENTA_BANNER = registerItem("magenta_banner", new BannerItem(Blocks.MAGENTA_BANNER, Blocks.MAGENTA_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   public static final Item PINK_BANNER = registerItem("pink_banner", new BannerItem(Blocks.PINK_BANNER, Blocks.PINK_WALL_BANNER, (new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COLOURED_BLOCKS)));

   // COMBAT
   public static final Item WOODEN_SWORD = registerItem("wooden_sword", new SwordItem(ItemTier.WOOD, 3, -2.4F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item STONE_SWORD = registerItem("stone_sword", new SwordItem(ItemTier.STONE, 3, -2.4F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item IRON_SWORD = registerItem("iron_sword", new SwordItem(ItemTier.IRON, 3, -2.4F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item GOLDEN_SWORD = registerItem("golden_sword", new SwordItem(ItemTier.GOLD, 3, -2.4F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item ROSE_GOLD_SWORD = registerItem("rose_gold_sword", new SwordItem(ItemTier.ROSE_GOLD, 3, -2.4F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));

   public static final Item DIAMOND_SWORD = registerItem("diamond_sword", new SwordItem(ItemTier.DIAMOND, 3, -2.4F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item NETHERITE_SWORD = registerItem("netherite_sword", new SwordItem(ItemTier.NETHERITE, 3, -2.4F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
   public static final Item WITHER_BONE_CUTLASS = registerItem("wither_bone_cutlass", new SwordItem(ItemTier.WITHER_BONE, 2, -2.45F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));



   public static final Item WOODEN_AXE = registerItem("wooden_axe", new AxeItem(ItemTier.WOOD, 6.0F, -3.2F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item STONE_AXE = registerItem("stone_axe", new AxeItem(ItemTier.STONE, 7.0F, -3.2F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item IRON_AXE = registerItem("iron_axe", new AxeItem(ItemTier.IRON, 6.0F, -3.1F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item GOLDEN_AXE = registerItem("golden_axe", new AxeItem(ItemTier.GOLD, 6.0F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item ROSE_GOLD_AXE = registerItem("rose_gold_axe", new AxeItem(ItemTier.ROSE_GOLD, 6.5F, -3.05F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));

   public static final Item DIAMOND_AXE = registerItem("diamond_axe", new AxeItem(ItemTier.DIAMOND, 5.0F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item NETHERITE_AXE = registerItem("netherite_axe", new AxeItem(ItemTier.NETHERITE, 5.0F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
   public static final Item BASEBALL_BAT = registerItem("baseball_bat", new BatItem(-2.8F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item DESOLATE_DAGGER = registerItem("desolate_dagger", new DesolateDaggerItem());

   public static final Item TRIDENT = registerItem("trident", new TridentItem((new Item.Properties()).durability(250).tab(ItemGroup.TAB_COMBAT)));
   public static final Item MACE = registerItem("mace", new MaceItem(ItemTier.DIAMOND, new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
   public static final Item SHIELD = registerItem("shield", new ShieldItem((new Item.Properties()).durability(336).tab(ItemGroup.TAB_COMBAT)));
   public static final Item NETHERITE_SHIELD = registerItem("netherite_shield", new AbstractShieldItem((new Item.Properties()).durability(504).tab(ItemGroup.TAB_COMBAT), AbstractShieldItem.ShieldType.NETHERITE));
   public static final Item SHIELD_OF_CTHULHU = registerItem("shield_of_cthulhu", new AbstractShieldItem((new Item.Properties().rarity(Rarity.REDD)).durability(2600).tab(ItemGroup.TAB_COMBAT), AbstractShieldItem.ShieldType.CTHULHU));


   public static final Item LEATHER_HELMET = registerItem("leather_helmet", new DyeableArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item LEATHER_CHESTPLATE = registerItem("leather_chestplate", new DyeableArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.CHEST, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item LEATHER_LEGGINGS = registerItem("leather_leggings", new DyeableArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.LEGS, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item LEATHER_BOOTS = registerItem("leather_boots", new DyeableArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.FEET, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));

   public static final Item CHAINMAIL_HELMET = registerItem("chainmail_helmet", new ArmorItem(ArmorMaterial.CHAIN, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item CHAINMAIL_CHESTPLATE = registerItem("chainmail_chestplate", new ArmorItem(ArmorMaterial.CHAIN, EquipmentSlotType.CHEST, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item CHAINMAIL_LEGGINGS = registerItem("chainmail_leggings", new ArmorItem(ArmorMaterial.CHAIN, EquipmentSlotType.LEGS, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item CHAINMAIL_BOOTS = registerItem("chainmail_boots", new ArmorItem(ArmorMaterial.CHAIN, EquipmentSlotType.FEET, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));

   public static final Item IRON_HELMET = registerItem("iron_helmet", new ArmorItem(ArmorMaterial.IRON, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item IRON_CHESTPLATE = registerItem("iron_chestplate", new ArmorItem(ArmorMaterial.IRON, EquipmentSlotType.CHEST, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item IRON_LEGGINGS = registerItem("iron_leggings", new ArmorItem(ArmorMaterial.IRON, EquipmentSlotType.LEGS, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item IRON_BOOTS = registerItem("iron_boots", new ArmorItem(ArmorMaterial.IRON, EquipmentSlotType.FEET, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));

   public static final Item GOLDEN_HELMET = registerItem("golden_helmet", new ArmorItem(ArmorMaterial.GOLD, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item GOLDEN_CHESTPLATE = registerItem("golden_chestplate", new ArmorItem(ArmorMaterial.GOLD, EquipmentSlotType.CHEST, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item GOLDEN_LEGGINGS = registerItem("golden_leggings", new ArmorItem(ArmorMaterial.GOLD, EquipmentSlotType.LEGS, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item GOLDEN_BOOTS = registerItem("golden_boots", new ArmorItem(ArmorMaterial.GOLD, EquipmentSlotType.FEET, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));

   public static final Item ROSE_GOLD_HELMET = registerItem("rose_gold_helmet", new ArmorItem(ArmorMaterial.ROSE_GOLD, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item ROSE_GOLD_CHESTPLATE = registerItem("rose_gold_chestplate", new ArmorItem(ArmorMaterial.ROSE_GOLD, EquipmentSlotType.CHEST, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item ROSE_GOLD_LEGGINGS = registerItem("rose_gold_leggings", new ArmorItem(ArmorMaterial.ROSE_GOLD, EquipmentSlotType.LEGS, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item ROSE_GOLD_BOOTS = registerItem("rose_gold_boots", new ArmorItem(ArmorMaterial.ROSE_GOLD, EquipmentSlotType.FEET, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));


   public static final Item DIAMOND_HELMET = registerItem("diamond_helmet", new ArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item DIAMOND_CHESTPLATE = registerItem("diamond_chestplate", new ArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.CHEST, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item DIAMOND_LEGGINGS = registerItem("diamond_leggings", new ArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.LEGS, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item DIAMOND_BOOTS = registerItem("diamond_boots", new ArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.FEET, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));

   public static final Item NETHERITE_HELMET = registerItem("netherite_helmet", new ArmorItem(ArmorMaterial.NETHERITE, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
   public static final Item NETHERITE_CHESTPLATE = registerItem("netherite_chestplate", new ArmorItem(ArmorMaterial.NETHERITE, EquipmentSlotType.CHEST, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
   public static final Item NETHERITE_LEGGINGS = registerItem("netherite_leggings", new ArmorItem(ArmorMaterial.NETHERITE, EquipmentSlotType.LEGS, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
   public static final Item NETHERITE_BOOTS = registerItem("netherite_boots", new ArmorItem(ArmorMaterial.NETHERITE, EquipmentSlotType.FEET, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
   public static final Item TURTLE_HELMET = registerItem("turtle_helmet", new ArmorItem(ArmorMaterial.TURTLE, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item TURTLE_CHESTPLATE = registerItem("turtle_chestplate", new ArmorItem(ArmorMaterial.TURTLE, EquipmentSlotType.CHEST, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item TURTLE_LEGGINGS = registerItem("turtle_leggings", new ArmorItem(ArmorMaterial.TURTLE, EquipmentSlotType.LEGS, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item TURTLE_BOOTS = registerItem("turtle_boots", new ArmorItem(ArmorMaterial.TURTLE, EquipmentSlotType.FEET, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item BURNT_TURTLE_HELMET = registerItem("burnt_turtle_helmet", new ArmorItem(ArmorMaterial.BURNT_TURTLE, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item BURNT_TURTLE_CHESTPLATE = registerItem("burnt_turtle_chestplate", new ArmorItem(ArmorMaterial.BURNT_TURTLE, EquipmentSlotType.CHEST, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item BURNT_TURTLE_LEGGINGS = registerItem("burnt_turtle_leggings", new ArmorItem(ArmorMaterial.BURNT_TURTLE, EquipmentSlotType.LEGS, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item BURNT_TURTLE_BOOTS = registerItem("burnt_turtle_boots", new ArmorItem(ArmorMaterial.BURNT_TURTLE, EquipmentSlotType.FEET, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item FLOWER_CROWN = registerItem("flower_crown", new ArmorItem(ArmorMaterial.FLOWER_CROWN, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));

   public static final Item WITHER_BONE_HELMET = registerItem("wither_bone_helmet", new ArmorItem(ArmorMaterial.WITHERED, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).rarity(Rarity.EPIC).fireResistant()));
   public static final Item WITHER_BONE_CHESTPLATE = registerItem("wither_bone_chestplate", new ArmorItem(ArmorMaterial.WITHERED, EquipmentSlotType.CHEST, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).rarity(Rarity.EPIC).fireResistant()));
   public static final Item WITHER_BONE_LEGGINGS = registerItem("wither_bone_leggings", new ArmorItem(ArmorMaterial.WITHERED, EquipmentSlotType.LEGS, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).rarity(Rarity.EPIC).fireResistant()));
   public static final Item WITHER_BONE_BOOTS = registerItem("wither_bone_boots", new ArmorItem(ArmorMaterial.WITHERED, EquipmentSlotType.FEET, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).rarity(Rarity.EPIC).fireResistant()));



   public static final Item IRON_HORSE_ARMOR = registerItem("iron_horse_armor", new HorseArmorItem(5, "iron", (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COMBAT)));
   public static final Item GOLDEN_HORSE_ARMOR = registerItem("golden_horse_armor", new HorseArmorItem(7, "gold", (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COMBAT)));
   public static final Item DIAMOND_HORSE_ARMOR = registerItem("diamond_horse_armor", new HorseArmorItem(11, "diamond", (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COMBAT)));
   public static final Item LEATHER_HORSE_ARMOR = registerItem("leather_horse_armor", new DyeableHorseArmorItem(3, "leather", (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COMBAT)));

   public static final Item TOTEM_OF_UNDYING = registerItem("totem_of_undying", new Item((new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COMBAT).rarity(Rarity.UNCOMMON)));
   public static final Item TNT = registerBlock(Blocks.TNT, ItemGroup.TAB_FUNCTIONAL);
   public static final Item END_CRYSTAL = registerItem("end_crystal", new EnderCrystalItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT).rarity(Rarity.RARE)));
   public static final Item SNOWBALL = registerItem("snowball", new SnowballItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COMBAT)));
   public static final Item EGG = registerItem("egg", new TemperateEggItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COMBAT)));
   public static final Item WARM_EGG = registerItem("brown_egg", new WarmEggItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COMBAT)));
   public static final Item COLD_EGG = registerItem("blue_egg", new ColdEggItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COMBAT)));


   public static final Item BOW = registerItem("bow", new BowItem((new Item.Properties()).durability(384).tab(ItemGroup.TAB_COMBAT)));
   public static final Item AERIAL_BANE = registerItem("aerial_bane", new AerialBaneItem((new Item.Properties()).durability(1438).tab(ItemGroup.TAB_COMBAT).rarity(Rarity.YELLOWW)));

   public static final Item BONE_BOW = registerItem("bone_bow", new BoneBowItem((new Item.Properties()).durability(586).tab(ItemGroup.TAB_COMBAT)));

   public static final Item CROSSBOW = registerItem("crossbow", new CrossbowItem((new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COMBAT).durability(326)));

   public static final Item GILDED_CROSSBOW = registerItem("gilded_crossbow", new GildedCrossbowItem((new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_COMBAT).durability(296)));
   public static final Item DIAMOND_CROSSBOW = (new AbstractCrossbowBuilder.Builder("diamond_crossbow").id(0)
           .properties(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_COMBAT).durability(418))
           .chargeDuration(new int[]{45, 4})
           .isCrit(false)
           .shootingPower(new float[]{2F, 3.2F})
           .range(25)
           .addDamage(4)
           .build());


   public static final Item FROZEN_CROSSBOW = (new AbstractCrossbowBuilder.Builder("frozen_crossbow").id(1)
           .properties(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_COMBAT).durability(195))
           .chargeDuration(new int[]{50, 8})
           .isCrit(false)
           .shootingPower(new float[]{1.8F, 2F})
           .range(20)
           .effects(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 7 * 20, 1), new EffectInstance(Effects.BLINDNESS, 3 * 20, 0))
           .build());

   public static final Item FIREWORK_ROCKET = registerItem("firework_rocket", new FireworkRocketItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item ARROW = registerItem("arrow", new ArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item BONE_ARROW = registerItem("bone_arrow", new BoneArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item SPECTRAL_ARROW = registerItem("spectral_arrow", new SpectralArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
   public static final Item BURNING_ARROW = registerItem("burning_arrow", new CustomArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT), CustomArrowType.BURNING));
   public static final Item TELEPORTATION_ARROW = registerItem("teleportation_arrow", new CustomArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT), CustomArrowType.TELEPORTATION));
   public static final Item HEALING_ARROW = registerItem("healing_arrow", new CustomArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT), CustomArrowType.HEALING));
   public static final Item FIREWORK_ARROW = registerItem("firework_arrow", new CustomArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT), CustomArrowType.FIREWORK));
   public static final Item POISON_ARROW = registerItem("poison_arrow", new CustomArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT), CustomArrowType.POISON));
   public static final Item GILDED_ARROW = registerItem("gilded_arrow", new CustomArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT), CustomArrowType.GILDED));
   public static final Item FLEETING_ARROW = registerItem("fleeting_arrow", new CustomArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT), CustomArrowType.FLEETING));
   public static final Item MEEP_ARROW = registerItem("meep_arrow", new CustomArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT), CustomArrowType.MEEP));
   public static final Item JESTER_ARROW = registerItem("jester_arrow", new CustomArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT), CustomArrowType.JESTER) {

   });

   public static final Item TIPPED_ARROW = registerItem("tipped_arrow", new TippedArrowItem((new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));


   // FOODS

   public static final Item BLUEBERRIES = registerItem("blueberries", new Item((new Item.Properties()).tab(ItemGroup.TAB_MISC).food(Foods.BLUEBERRIES)));
   public static final Item APPLE = registerItem("apple", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.APPLE)));
   public static final Item GOLDEN_APPLE = registerItem("golden_apple", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).rarity(Rarity.EPIC).food(Foods.GOLDEN_APPLE)));
   public static final Item ENCHANTED_GOLDEN_APPLE = registerItem("enchanted_golden_apple", new EnchantedGoldenAppleItem((new Item.Properties()).tab(ItemGroup.TAB_FOOD).rarity(Rarity.LEGENDARY).food(Foods.ENCHANTED_GOLDEN_APPLE)));
   public static final Item MELON_SLICE = registerItem("melon_slice", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.MELON_SLICE)));
   public static final Item SWEET_BERRIES = registerItem("sweet_berries", new BlockNamedItem(Blocks.SWEET_BERRY_BUSH, (new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.SWEET_BERRIES)));
   public static final Item CHORUS_FRUIT = registerItem("chorus_fruit", new ChorusFruitItem((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS).food(Foods.CHORUS_FRUIT)));
   public static final Item CARROT = registerItem("carrot", new BlockNamedItem(Blocks.CARROTS, (new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.CARROT)));
   public static final Item GOLDEN_CARROT = registerItem("golden_carrot", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.GOLDEN_CARROT)));
   public static final Item POTATO = registerItem("potato", new BlockNamedItem(Blocks.POTATOES, (new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.POTATO)));
   public static final Item BAKED_POTATO = registerItem("baked_potato", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.BAKED_POTATO)));
   public static final Item POISONOUS_POTATO = registerItem("poisonous_potato", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.POISONOUS_POTATO)));
   public static final Item BEETROOT = registerItem("beetroot", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.BEETROOT)));
   public static final Item DRIED_KELP = registerItem("dried_kelp", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.DRIED_KELP)));
   public static final Item BEEF = registerItem("beef", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.BEEF)));
   public static final Item COOKED_BEEF = registerItem("cooked_beef", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.COOKED_BEEF)));
   public static final Item PORKCHOP = registerItem("porkchop", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.PORKCHOP)));
   public static final Item COOKED_PORKCHOP = registerItem("cooked_porkchop", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.COOKED_PORKCHOP)));
   public static final Item MUTTON = registerItem("mutton", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.MUTTON)));
   public static final Item COOKED_MUTTON = registerItem("cooked_mutton", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.COOKED_MUTTON)));
   public static final Item CHICKEN = registerItem("chicken", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.CHICKEN)));
   public static final Item COOKED_CHICKEN = registerItem("cooked_chicken", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.COOKED_CHICKEN)));

   public static final Item DRUMSTICK = registerItem("drumstick", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.DRUMSTICK)));
   public static final Item COOKED_DRUMSTICK = registerItem("cooked_drumstick", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.COOKED_DRUMSTICK)));

   public static final Item RABBIT = registerItem("rabbit", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.RABBIT)));
   public static final Item COOKED_RABBIT = registerItem("cooked_rabbit", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.COOKED_RABBIT)));
   public static final Item COD = registerItem("cod", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.COD)));
   public static final Item COOKED_COD = registerItem("cooked_cod", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.COOKED_COD)));
   public static final Item SALMON = registerItem("salmon", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.SALMON)));
   public static final Item COOKED_SALMON = registerItem("cooked_salmon", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.COOKED_SALMON)));
   public static final Item TROPICAL_FISH = registerItem("tropical_fish", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.TROPICAL_FISH)));
   public static final Item PUFFERFISH = registerItem("pufferfish", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.PUFFERFISH)));
   public static final Item BREAD = registerItem("bread", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.BREAD)));
   public static final Item COOKIE = registerItem("cookie", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.COOKIE)));
   public static final Item CAKE = registerBlock(new BlockItem(Blocks.CAKE, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_FOOD)));
   public static final Item PUMPKIN_PIE = registerItem("pumpkin_pie", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.PUMPKIN_PIE)));
   public static final Item ROTTEN_FLESH = registerItem("rotten_flesh", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.ROTTEN_FLESH)));
   public static final Item SPIDER_EYE = registerItem("spider_eye", new Item((new Item.Properties()).tab(ItemGroup.TAB_FOOD).food(Foods.SPIDER_EYE)));
   public static final Item MUSHROOM_STEW = registerItem("mushroom_stew", new SoupItem((new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_FOOD).food(Foods.MUSHROOM_STEW)));
   public static final Item BEETROOT_SOUP = registerItem("beetroot_soup", new SoupItem((new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_FOOD).food(Foods.BEETROOT_SOUP)));
   public static final Item RABBIT_STEW = registerItem("rabbit_stew", new SoupItem((new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_FOOD).food(Foods.RABBIT_STEW)));
   public static final Item MILK_BUCKET = registerItem("milk_bucket", new MilkBucketItem((new Item.Properties()).craftRemainder(BUCKET).stacksTo(1).tab(ItemGroup.TAB_FOOD)));
   public static final Item HONEY_BOTTLE = registerItem("honey_bottle", new HoneyBottleItem((new Item.Properties()).craftRemainder(GLASS_BOTTLE).food(Foods.HONEY_BOTTLE).tab(ItemGroup.TAB_FOOD).stacksTo(16)));
   public static final Item LIGHTNING_IN_A_BOTTLE = registerItem("lightning_in_a_bottle", new LightningInABottleItem((new Item.Properties()).food(Foods.LIGHTNING).craftRemainder(GLASS_BOTTLE).tab(ItemGroup.TAB_FOOD).stacksTo(1)));
   public static final Item ROYAL_JELLY = registerItem("royal_jelly", new RoyalJellyBottleItem((new Item.Properties()).craftRemainder(GLASS_BOTTLE).food(Foods.ROYAL_JELLY).tab(ItemGroup.TAB_FOOD).stacksTo(8)));
   public static final Item HONEYCOMB_COOKIE = registerItem("honeycomb_cookie", new HoneyFoodItem(Foods.HONEYCOMB_COOKIE, 40, null, Effects.POISON, Effects.HUNGER));
   public static final Item TOFFEE_APPLE = registerItem("toffee_apple", new HoneyFoodItem(Foods.TOFFEE_APPLE, 60, null, Effects.POISON, Effects.HUNGER));
   public static final Item POTION = registerItem("potion", new PotionItem((new Item.Properties()).stacksTo(3).tab(ItemGroup.TAB_FOOD)));
   public static final Item SPLASH_POTION = registerItem("splash_potion", new SplashPotionItem((new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_FOOD)));
   public static final Item LINGERING_POTION = registerItem("lingering_potion", new LingeringPotionItem((new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_FOOD)));

   public static final Item CUTTING_KNIFE = registerItem("cutting_knife", new SwordItem(ItemTier.IRON, 0, -3.4f, new Item.Properties().tab(ItemGroup.TAB_FOOD).durability(240)));
   public static final Item FRYING_PAN = registerItem("frying_pan", new Item(new Item.Properties().tab(ItemGroup.TAB_FOOD).stacksTo(1)));
   public static final Item HEATED_FRYING_PAN = registerItem("heated_frying_pan", new Item(new Item.Properties().craftRemainder(FRYING_PAN).tab(ItemGroup.TAB_FOOD).stacksTo(1)));
   public static final Item FRIED_EGG = registerItem("fried_egg", new Item((new Item.Properties()).food(Foods.FRIED_EGG).tab(ItemGroup.TAB_FOOD).stacksTo(16)));
   public static final Item FRIED_EGGS_DOUBLE = registerItem("fried_eggs_double", new Item((new Item.Properties()).food(Foods.FRIED_EGGS_DOUBLE).tab(ItemGroup.TAB_FOOD).stacksTo(16)));
   public static final Item FRIED_EGGS_TRIPLE = registerItem("fried_eggs_triple", new Item((new Item.Properties()).food(Foods.FRIED_EGGS_TRIPLE).tab(ItemGroup.TAB_FOOD).stacksTo(16)));

















   public static final Item DANDELION = registerBlock(Blocks.DANDELION, ItemGroup.TAB_NATURAL);
   public static final Item POPPY = registerBlock(Blocks.POPPY, ItemGroup.TAB_NATURAL);
   public static final Item BLUE_ORCHID = registerBlock(Blocks.BLUE_ORCHID, ItemGroup.TAB_NATURAL);
   public static final Item ALLIUM = registerBlock(Blocks.ALLIUM, ItemGroup.TAB_NATURAL);
   public static final Item AZURE_BLUET = registerBlock(Blocks.AZURE_BLUET, ItemGroup.TAB_NATURAL);
   public static final Item RED_TULIP = registerBlock(Blocks.RED_TULIP, ItemGroup.TAB_NATURAL);
   public static final Item ORANGE_TULIP = registerBlock(Blocks.ORANGE_TULIP, ItemGroup.TAB_NATURAL);
   public static final Item WHITE_TULIP = registerBlock(Blocks.WHITE_TULIP, ItemGroup.TAB_NATURAL);
   public static final Item PINK_TULIP = registerBlock(Blocks.PINK_TULIP, ItemGroup.TAB_NATURAL);
   public static final Item OXEYE_DAISY = registerBlock(Blocks.OXEYE_DAISY, ItemGroup.TAB_NATURAL);
   public static final Item CORNFLOWER = registerBlock(Blocks.CORNFLOWER, ItemGroup.TAB_NATURAL);
   public static final Item LILY_OF_THE_VALLEY = registerBlock(Blocks.LILY_OF_THE_VALLEY, ItemGroup.TAB_NATURAL);
   public static final Item WITHER_ROSE = registerBlock(Blocks.WITHER_ROSE, ItemGroup.TAB_NATURAL);
   public static final Item BROWN_MUSHROOM = registerBlock(Blocks.BROWN_MUSHROOM, ItemGroup.TAB_NATURAL);
   public static final Item RED_MUSHROOM = registerBlock(Blocks.RED_MUSHROOM, ItemGroup.TAB_NATURAL);
   public static final Item CRIMSON_FUNGUS = registerBlock(Blocks.CRIMSON_FUNGUS, ItemGroup.TAB_NATURAL);
   public static final Item OPEN_EYEBLOSSOM = registerBlock(Blocks.OPEN_EYEBLOSSOM, ItemGroup.TAB_NATURAL);
   public static final Item CLOSED_EYEBLOSSOM = registerBlock(Blocks.CLOSED_EYEBLOSSOM, ItemGroup.TAB_NATURAL);
   public static final Item PINK_PETALS = registerBlock(Blocks.PINK_PETALS, ItemGroup.TAB_NATURAL);
   public static final Item WILDFLOWERS = registerBlock(Blocks.WILDFLOWERS, ItemGroup.TAB_NATURAL);
   public static final Item PALE_LEAF_PILE = registerBlock(Blocks.PALE_LEAF_PILE, ItemGroup.TAB_NATURAL);
   public static final Item LEAF_LITTER = registerBlock(Blocks.LEAF_LITTER, ItemGroup.TAB_NATURAL);



   public static final Item WARPED_FUNGUS = registerBlock(Blocks.WARPED_FUNGUS, ItemGroup.TAB_NATURAL);
   public static final Item CRIMSON_ROOTS = registerBlock(Blocks.CRIMSON_ROOTS, ItemGroup.TAB_NATURAL);
   public static final Item WARPED_ROOTS = registerBlock(Blocks.WARPED_ROOTS, ItemGroup.TAB_NATURAL);
   public static final Item NETHER_SPROUTS = registerBlock(Blocks.NETHER_SPROUTS, ItemGroup.TAB_NATURAL);
   public static final Item WEEPING_VINES = registerBlock(Blocks.WEEPING_VINES, ItemGroup.TAB_NATURAL);
   public static final Item TWISTING_VINES = registerBlock(Blocks.TWISTING_VINES, ItemGroup.TAB_NATURAL);
   public static final Item SUGAR_CANE = registerBlock(Blocks.SUGAR_CANE, ItemGroup.TAB_NATURAL);
   public static final Item KELP = registerBlock(Blocks.KELP, ItemGroup.TAB_NATURAL);
   public static final Item BAMBOO = registerBlock(Blocks.BAMBOO, ItemGroup.TAB_NATURAL);
   public static final Item GOLD_BLOCK = registerBlock(Blocks.GOLD_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item ROSE_GOLD_BLOCK = registerBlock(Blocks.ROSE_GOLD_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item IRON_BLOCK = registerBlock(Blocks.IRON_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item OAK_SLAB = registerBlock(Blocks.OAK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PALE_OAK_SLAB = registerBlock(Blocks.PALE_OAK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item SPRUCE_SLAB = registerBlock(Blocks.SPRUCE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BIRCH_SLAB = registerBlock(Blocks.BIRCH_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item JUNGLE_SLAB = registerBlock(Blocks.JUNGLE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item ACACIA_SLAB = registerBlock(Blocks.ACACIA_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DARK_OAK_SLAB = registerBlock(Blocks.DARK_OAK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CRIMSON_SLAB = registerBlock(Blocks.CRIMSON_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item WARPED_SLAB = registerBlock(Blocks.WARPED_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STONE_SLAB = registerBlock(Blocks.STONE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SMOOTH_STONE_SLAB = registerBlock(Blocks.SMOOTH_STONE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SANDSTONE_SLAB = registerBlock(Blocks.SANDSTONE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CUT_STANDSTONE_SLAB = registerBlock(Blocks.CUT_SANDSTONE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PETRIFIED_OAK_SLAB = registerBlock(Blocks.PETRIFIED_OAK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item COBBLESTONE_SLAB = registerBlock(Blocks.COBBLESTONE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BRICK_SLAB = registerBlock(Blocks.BRICK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STONE_BRICK_SLAB = registerBlock(Blocks.STONE_BRICK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item NETHER_BRICK_SLAB = registerBlock(Blocks.NETHER_BRICK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item QUARTZ_SLAB = registerBlock(Blocks.QUARTZ_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item RED_SANDSTONE_SLAB = registerBlock(Blocks.RED_SANDSTONE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CUT_RED_SANDSTONE_SLAB = registerBlock(Blocks.CUT_RED_SANDSTONE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PURPUR_SLAB = registerBlock(Blocks.PURPUR_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PRISMARINE_SLAB = registerBlock(Blocks.PRISMARINE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PRISMARINE_BRICK_SLAB = registerBlock(Blocks.PRISMARINE_BRICK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DARK_PRISMARINE_SLAB = registerBlock(Blocks.DARK_PRISMARINE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SMOOTH_QUARTZ = registerBlock(Blocks.SMOOTH_QUARTZ, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SMOOTH_RED_SANDSTONE = registerBlock(Blocks.SMOOTH_RED_SANDSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SMOOTH_SANDSTONE = registerBlock(Blocks.SMOOTH_SANDSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SMOOTH_STONE = registerBlock(Blocks.SMOOTH_STONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BRICKS = registerBlock(Blocks.BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item BOOKSHELF = registerBlock(Blocks.BOOKSHELF, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item MOSSY_COBBLESTONE = registerBlock(Blocks.MOSSY_COBBLESTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item OBSIDIAN = registerBlock(Blocks.OBSIDIAN, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item TORCH = registerBlock(new WallOrFloorItem(Blocks.TORCH, Blocks.WALL_TORCH, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item END_ROD = registerBlock(Blocks.END_ROD, ItemGroup.TAB_NATURAL);
   public static final Item CHORUS_PLANT = registerBlock(Blocks.CHORUS_PLANT, ItemGroup.TAB_NATURAL);
   public static final Item CHORUS_FLOWER = registerBlock(Blocks.CHORUS_FLOWER, ItemGroup.TAB_NATURAL);
   public static final Item PURPUR_BLOCK = registerBlock(Blocks.PURPUR_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PURPUR_PILLAR = registerBlock(Blocks.PURPUR_PILLAR, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PURPUR_STAIRS = registerBlock(Blocks.PURPUR_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item END_GATEWAY = registerBlock(Blocks.END_GATEWAY, ItemGroup.TAB_NATURAL);
   public static final Item SPAWNER = registerBlock(Blocks.SPAWNER);
   public static final Item OAK_STAIRS = registerBlock(Blocks.OAK_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PALE_OAK_STAIRS = registerBlock(Blocks.PALE_OAK_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item CHEST = registerBlock(Blocks.CHEST, ItemGroup.TAB_NATURAL);
   public static final Item DIAMOND_ORE = registerBlock(Blocks.DIAMOND_ORE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DIAMOND_BLOCK = registerBlock(Blocks.DIAMOND_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CRAFTING_TABLE = registerBlock(Blocks.CRAFTING_TABLE, ItemGroup.TAB_NATURAL);
   public static final Item HONEY_EXTRACTOR = registerBlock(Blocks.HONEY_EXTRACTOR, ItemGroup.TAB_NATURAL);
   public static final Item FARMLAND = registerBlock(Blocks.FARMLAND, ItemGroup.TAB_NATURAL);
   public static final Item FURNACE = registerBlock(Blocks.FURNACE, ItemGroup.TAB_NATURAL);
   public static final Item LADDER = registerBlock(Blocks.LADDER, ItemGroup.TAB_NATURAL);
   public static final Item RAIL = registerBlock(Blocks.RAIL, ItemGroup.TAB_REDSTONE);
   public static final Item COBBLESTONE_STAIRS = registerBlock(Blocks.COBBLESTONE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item LEVER = registerBlock(Blocks.LEVER, ItemGroup.TAB_FUNCTIONAL);
   public static final Item STONE_PRESSURE_PLATE = registerBlock(Blocks.STONE_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item OAK_PRESSURE_PLATE = registerBlock(Blocks.OAK_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item SPRUCE_PRESSURE_PLATE = registerBlock(Blocks.SPRUCE_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item BIRCH_PRESSURE_PLATE = registerBlock(Blocks.BIRCH_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item JUNGLE_PRESSURE_PLATE = registerBlock(Blocks.JUNGLE_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item ACACIA_PRESSURE_PLATE = registerBlock(Blocks.ACACIA_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item DARK_OAK_PRESSURE_PLATE = registerBlock(Blocks.DARK_OAK_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item CRIMSON_PRESSURE_PLATE = registerBlock(Blocks.CRIMSON_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item WARPED_PRESSURE_PLATE = registerBlock(Blocks.WARPED_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item POLISHED_BLACKSTONE_PRESSURE_PLATE = registerBlock(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item REDSTONE_ORE = registerBlock(Blocks.REDSTONE_ORE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item REDSTONE_TORCH = registerBlock(new WallOrFloorItem(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item SNOW = registerBlock(Blocks.SNOW, ItemGroup.TAB_NATURAL);
   public static final Item ICE = registerBlock(Blocks.ICE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SNOW_BLOCK = registerBlock(Blocks.SNOW_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CACTUS = registerBlock(Blocks.CACTUS, ItemGroup.TAB_NATURAL);
   public static final Item CLAY = registerBlock(Blocks.CLAY, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item JUKEBOX = registerBlock(Blocks.JUKEBOX, ItemGroup.TAB_NATURAL);
   public static final Item OAK_FENCE = registerBlock(Blocks.OAK_FENCE, ItemGroup.TAB_NATURAL);
   public static final Item PALE_OAK_FENCE = registerBlock(Blocks.PALE_OAK_FENCE, ItemGroup.TAB_NATURAL);
   public static final Item PALE_OAK_FENCE_GATE = registerBlock(Blocks.PALE_OAK_FENCE_GATE, ItemGroup.TAB_NATURAL);

   public static final Item SPRUCE_FENCE = registerBlock(Blocks.SPRUCE_FENCE, ItemGroup.TAB_NATURAL);
   public static final Item BIRCH_FENCE = registerBlock(Blocks.BIRCH_FENCE, ItemGroup.TAB_NATURAL);
   public static final Item JUNGLE_FENCE = registerBlock(Blocks.JUNGLE_FENCE, ItemGroup.TAB_NATURAL);
   public static final Item ACACIA_FENCE = registerBlock(Blocks.ACACIA_FENCE, ItemGroup.TAB_NATURAL);
   public static final Item DARK_OAK_FENCE = registerBlock(Blocks.DARK_OAK_FENCE, ItemGroup.TAB_NATURAL);
   public static final Item CRIMSON_FENCE = registerBlock(Blocks.CRIMSON_FENCE, ItemGroup.TAB_NATURAL);
   public static final Item WARPED_FENCE = registerBlock(Blocks.WARPED_FENCE, ItemGroup.TAB_NATURAL);
   public static final Item PUMPKIN = registerBlock(Blocks.PUMPKIN, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CARVED_PUMPKIN = registerBlock(Blocks.CARVED_PUMPKIN, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item WHITE_PUMPKIN = registerBlock(Blocks.WHITE_PUMPKIN, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item WHITE_CARVED_PUMPKIN = registerBlock(Blocks.WHITE_CARVED_PUMPKIN, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item NETHERRACK = registerBlock(Blocks.NETHERRACK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SOUL_SAND = registerBlock(Blocks.SOUL_SAND, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SOUL_SOIL = registerBlock(Blocks.SOUL_SOIL, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BASALT = registerBlock(Blocks.BASALT, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_BASALT = registerBlock(Blocks.POLISHED_BASALT, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SOUL_TORCH = registerBlock(new WallOrFloorItem(Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item GLOWSTONE = registerBlock(Blocks.GLOWSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item JACK_O_LANTERN = registerBlock(Blocks.JACK_O_LANTERN, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item WHITE_JACK_O_LANTERN = registerBlock(Blocks.WHITE_JACK_O_LANTERN, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item OAK_TRAPDOOR = registerBlock(Blocks.OAK_TRAPDOOR, ItemGroup.TAB_FUNCTIONAL);
   public static final Item SPRUCE_TRAPDOOR = registerBlock(Blocks.SPRUCE_TRAPDOOR, ItemGroup.TAB_FUNCTIONAL);
   public static final Item BIRCH_TRAPDOOR = registerBlock(Blocks.BIRCH_TRAPDOOR, ItemGroup.TAB_FUNCTIONAL);
   public static final Item JUNGLE_TRAPDOOR = registerBlock(Blocks.JUNGLE_TRAPDOOR, ItemGroup.TAB_FUNCTIONAL);
   public static final Item ACACIA_TRAPDOOR = registerBlock(Blocks.ACACIA_TRAPDOOR, ItemGroup.TAB_FUNCTIONAL);
   public static final Item DARK_OAK_TRAPDOOR = registerBlock(Blocks.DARK_OAK_TRAPDOOR, ItemGroup.TAB_FUNCTIONAL);
   public static final Item CRIMSON_TRAPDOOR = registerBlock(Blocks.CRIMSON_TRAPDOOR, ItemGroup.TAB_FUNCTIONAL);
   public static final Item WARPED_TRAPDOOR = registerBlock(Blocks.WARPED_TRAPDOOR, ItemGroup.TAB_FUNCTIONAL);
   public static final Item INFESTED_STONE = registerBlock(Blocks.INFESTED_STONE, ItemGroup.TAB_NATURAL);
   public static final Item INFESTED_COBBLESTONE = registerBlock(Blocks.INFESTED_COBBLESTONE, ItemGroup.TAB_NATURAL);
   public static final Item INFESTED_STONE_BRICKS = registerBlock(Blocks.INFESTED_STONE_BRICKS, ItemGroup.TAB_NATURAL);
   public static final Item INFESTED_MOSSY_STONE_BRICKS = registerBlock(Blocks.INFESTED_MOSSY_STONE_BRICKS, ItemGroup.TAB_NATURAL);
   public static final Item INFESTED_CRACKED_STONE_BRICKS = registerBlock(Blocks.INFESTED_CRACKED_STONE_BRICKS, ItemGroup.TAB_NATURAL);
   public static final Item INFESTED_CHISELED_STONE_BRICKS = registerBlock(Blocks.INFESTED_CHISELED_STONE_BRICKS, ItemGroup.TAB_NATURAL);
   public static final Item STONE_BRICKS = registerBlock(Blocks.STONE_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item MOSSY_STONE_BRICKS = registerBlock(Blocks.MOSSY_STONE_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CRACKED_STONE_BRICKS = registerBlock(Blocks.CRACKED_STONE_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CHISELED_STONE_BRICKS = registerBlock(Blocks.CHISELED_STONE_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BROWN_MUSHROOM_BLOCK = registerBlock(Blocks.BROWN_MUSHROOM_BLOCK, ItemGroup.TAB_NATURAL);
   public static final Item RED_MUSHROOM_BLOCK = registerBlock(Blocks.RED_MUSHROOM_BLOCK, ItemGroup.TAB_NATURAL);
   public static final Item MUSHROOM_STEM = registerBlock(Blocks.MUSHROOM_STEM, ItemGroup.TAB_NATURAL);
   public static final Item IRON_BARS = registerBlock(Blocks.IRON_BARS, ItemGroup.TAB_NATURAL);
   public static final Item CHAIN = registerBlock(Blocks.CHAIN, ItemGroup.TAB_NATURAL);
   public static final Item MELON = registerBlock(Blocks.MELON, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item VINE = registerBlock(Blocks.VINE, ItemGroup.TAB_NATURAL);
   //public static final Item BLOOMING_IVY = registerBlock(Blocks.BLOOMING_IVY, ItemGroup.TAB_NATURAL);

   public static final Item OAK_FENCE_GATE = registerBlock(Blocks.OAK_FENCE_GATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item SPRUCE_FENCE_GATE = registerBlock(Blocks.SPRUCE_FENCE_GATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item BIRCH_FENCE_GATE = registerBlock(Blocks.BIRCH_FENCE_GATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item JUNGLE_FENCE_GATE = registerBlock(Blocks.JUNGLE_FENCE_GATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item ACACIA_FENCE_GATE = registerBlock(Blocks.ACACIA_FENCE_GATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item DARK_OAK_FENCE_GATE = registerBlock(Blocks.DARK_OAK_FENCE_GATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item CRIMSON_FENCE_GATE = registerBlock(Blocks.CRIMSON_FENCE_GATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item WARPED_FENCE_GATE = registerBlock(Blocks.WARPED_FENCE_GATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item BRICK_STAIRS = registerBlock(Blocks.BRICK_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STONE_BRICK_STAIRS = registerBlock(Blocks.STONE_BRICK_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item MYCELIUM = registerBlock(Blocks.MYCELIUM, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item LILY_PAD = registerBlock(new LilyPadItem(Blocks.LILY_PAD, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item DUCKWEED = registerBlock(new DuckweedItem(Blocks.DUCKWEED, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));

   public static final Item NETHER_BRICKS = registerBlock(Blocks.NETHER_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CRACKED_NETHER_BRICKS = registerBlock(Blocks.CRACKED_NETHER_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CHISELED_NETHER_BRICKS = registerBlock(Blocks.CHISELED_NETHER_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item NETHER_BRICK_FENCE = registerBlock(Blocks.NETHER_BRICK_FENCE, ItemGroup.TAB_NATURAL);
   public static final Item NETHER_BRICK_STAIRS = registerBlock(Blocks.NETHER_BRICK_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item ENCHANTING_TABLE = registerBlock(Blocks.ENCHANTING_TABLE, ItemGroup.TAB_NATURAL);
   public static final Item END_PORTAL_FRAME = registerBlock(Blocks.END_PORTAL_FRAME, ItemGroup.TAB_NATURAL);
   public static final Item END_STONE = registerBlock(Blocks.END_STONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item END_STONE_BRICKS = registerBlock(Blocks.END_STONE_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DRAGON_EGG = registerBlock(new BlockItem(Blocks.DRAGON_EGG, (new Item.Properties()).rarity(Rarity.EPIC)));
   public static final Item REDSTONE_LAMP = registerBlock(Blocks.REDSTONE_LAMP, ItemGroup.TAB_FUNCTIONAL);
   public static final Item SANDSTONE_STAIRS = registerBlock(Blocks.SANDSTONE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item EMERALD_ORE = registerBlock(Blocks.EMERALD_ORE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item ENDER_CHEST = registerBlock(Blocks.ENDER_CHEST, ItemGroup.TAB_NATURAL);
   public static final Item TRIPWIRE_HOOK = registerBlock(Blocks.TRIPWIRE_HOOK, ItemGroup.TAB_FUNCTIONAL);
   public static final Item EMERALD_BLOCK = registerBlock(Blocks.EMERALD_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SPRUCE_STAIRS = registerBlock(Blocks.SPRUCE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BIRCH_STAIRS = registerBlock(Blocks.BIRCH_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item JUNGLE_STAIRS = registerBlock(Blocks.JUNGLE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CRIMSON_STAIRS = registerBlock(Blocks.CRIMSON_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item WARPED_STAIRS = registerBlock(Blocks.WARPED_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item COMMAND_BLOCK = registerBlock(new OperatorOnlyItem(Blocks.COMMAND_BLOCK, (new Item.Properties()).rarity(Rarity.EPIC)));
   public static final Item BEACON = registerBlock(new BlockItem(Blocks.BEACON, (new Item.Properties()).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE)));
   public static final Item COBBLESTONE_WALL = registerBlock(Blocks.COBBLESTONE_WALL, ItemGroup.TAB_NATURAL);
   public static final Item MOSSY_COBBLESTONE_WALL = registerBlock(Blocks.MOSSY_COBBLESTONE_WALL, ItemGroup.TAB_NATURAL);
   public static final Item BRICK_WALL = registerBlock(Blocks.BRICK_WALL, ItemGroup.TAB_NATURAL);
   public static final Item PRISMARINE_WALL = registerBlock(Blocks.PRISMARINE_WALL, ItemGroup.TAB_NATURAL);
   public static final Item RED_SANDSTONE_WALL = registerBlock(Blocks.RED_SANDSTONE_WALL, ItemGroup.TAB_NATURAL);
   public static final Item MOSSY_STONE_BRICK_WALL = registerBlock(Blocks.MOSSY_STONE_BRICK_WALL, ItemGroup.TAB_NATURAL);
   public static final Item GRANITE_WALL = registerBlock(Blocks.GRANITE_WALL, ItemGroup.TAB_NATURAL);
   public static final Item STONE_BRICK_WALL = registerBlock(Blocks.STONE_BRICK_WALL, ItemGroup.TAB_NATURAL);
   public static final Item NETHER_BRICK_WALL = registerBlock(Blocks.NETHER_BRICK_WALL, ItemGroup.TAB_NATURAL);
   public static final Item ANDESITE_WALL = registerBlock(Blocks.ANDESITE_WALL, ItemGroup.TAB_NATURAL);
   public static final Item RED_NETHER_BRICK_WALL = registerBlock(Blocks.RED_NETHER_BRICK_WALL, ItemGroup.TAB_NATURAL);
   public static final Item SANDSTONE_WALL = registerBlock(Blocks.SANDSTONE_WALL, ItemGroup.TAB_NATURAL);
   public static final Item END_STONE_BRICK_WALL = registerBlock(Blocks.END_STONE_BRICK_WALL, ItemGroup.TAB_NATURAL);
   public static final Item DIORITE_WALL = registerBlock(Blocks.DIORITE_WALL, ItemGroup.TAB_NATURAL);
   public static final Item BLACKSTONE_WALL = registerBlock(Blocks.BLACKSTONE_WALL, ItemGroup.TAB_NATURAL);
   public static final Item POLISHED_BLACKSTONE_WALL = registerBlock(Blocks.POLISHED_BLACKSTONE_WALL, ItemGroup.TAB_NATURAL);
   public static final Item POLISHED_BLACKSTONE_BRICK_WALL = registerBlock(Blocks.POLISHED_BLACKSTONE_BRICK_WALL, ItemGroup.TAB_NATURAL);
   public static final Item STONE_BUTTON = registerBlock(Blocks.STONE_BUTTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item OAK_BUTTON = registerBlock(Blocks.OAK_BUTTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item SPRUCE_BUTTON = registerBlock(Blocks.SPRUCE_BUTTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item PALE_OAK_BUTTON = registerBlock(Blocks.PALE_OAK_BUTTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item PALE_OAK_PRESSURE_PLATE = registerBlock(Blocks.PALE_OAK_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);

   public static final Item BIRCH_BUTTON = registerBlock(Blocks.BIRCH_BUTTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item JUNGLE_BUTTON = registerBlock(Blocks.JUNGLE_BUTTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item ACACIA_BUTTON = registerBlock(Blocks.ACACIA_BUTTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item DARK_OAK_BUTTON = registerBlock(Blocks.DARK_OAK_BUTTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item CRIMSON_BUTTON = registerBlock(Blocks.CRIMSON_BUTTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item WARPED_BUTTON = registerBlock(Blocks.WARPED_BUTTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item POLISHED_BLACKSTONE_BUTTON = registerBlock(Blocks.POLISHED_BLACKSTONE_BUTTON, ItemGroup.TAB_FUNCTIONAL);
   public static final Item ANVIL = registerBlock(Blocks.ANVIL, ItemGroup.TAB_NATURAL);
   public static final Item CHIPPED_ANVIL = registerBlock(Blocks.CHIPPED_ANVIL, ItemGroup.TAB_NATURAL);
   public static final Item DAMAGED_ANVIL = registerBlock(Blocks.DAMAGED_ANVIL, ItemGroup.TAB_NATURAL);
   public static final Item TRAPPED_CHEST = registerBlock(Blocks.TRAPPED_CHEST, ItemGroup.TAB_FUNCTIONAL);
   public static final Item LIGHT_WEIGHTED_PRESSURE_PLATE = registerBlock(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item HEAVY_WEIGHTED_PRESSURE_PLATE = registerBlock(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, ItemGroup.TAB_FUNCTIONAL);
   public static final Item DAYLIGHT_DETECTOR = registerBlock(Blocks.DAYLIGHT_DETECTOR, ItemGroup.TAB_FUNCTIONAL);
   public static final Item SCULK_SENSOR = registerBlock(Blocks.SCULK_SENSOR, ItemGroup.TAB_FUNCTIONAL);
   public static final Item REDSTONE_BLOCK = registerBlock(Blocks.REDSTONE_BLOCK, ItemGroup.TAB_FUNCTIONAL);
   public static final Item NETHER_QUARTZ_ORE = registerBlock(Blocks.NETHER_QUARTZ_ORE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item HOPPER = registerBlock(Blocks.HOPPER, ItemGroup.TAB_FUNCTIONAL);
   public static final Item CHISELED_QUARTZ_BLOCK = registerBlock(Blocks.CHISELED_QUARTZ_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item QUARTZ_BLOCK = registerBlock(Blocks.QUARTZ_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item QUARTZ_BRICKS = registerBlock(Blocks.QUARTZ_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item QUARTZ_PILLAR = registerBlock(Blocks.QUARTZ_PILLAR, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item QUARTZ_STAIRS = registerBlock(Blocks.QUARTZ_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item ACTIVATOR_RAIL = registerBlock(Blocks.ACTIVATOR_RAIL, ItemGroup.TAB_REDSTONE);
   public static final Item DROPPER = registerBlock(Blocks.DROPPER, ItemGroup.TAB_FUNCTIONAL);

   public static final Item BARRIER = registerBlock(Blocks.BARRIER);
   public static final Item IRON_TRAPDOOR = registerBlock(Blocks.IRON_TRAPDOOR, ItemGroup.TAB_FUNCTIONAL);
   public static final Item HAY_BLOCK = registerBlock(Blocks.HAY_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item COAL_BLOCK = registerBlock(Blocks.COAL_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PACKED_ICE = registerBlock(Blocks.PACKED_ICE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item ACACIA_STAIRS = registerBlock(Blocks.ACACIA_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DARK_OAK_STAIRS = registerBlock(Blocks.DARK_OAK_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SLIME_BLOCK = registerBlock(Blocks.SLIME_BLOCK, ItemGroup.TAB_NATURAL);
   public static final Item GRASS_PATH = registerBlock(Blocks.GRASS_PATH, ItemGroup.TAB_NATURAL);
   public static final Item SUNFLOWER = registerBlock(new TallBlockItem(Blocks.SUNFLOWER, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item LILAC = registerBlock(new TallBlockItem(Blocks.LILAC, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item ROSE_BUSH = registerBlock(new TallBlockItem(Blocks.ROSE_BUSH, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item PEONY = registerBlock(new TallBlockItem(Blocks.PEONY, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item TALL_GRASS = registerBlock(new TallBlockItem(Blocks.TALL_GRASS, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item LARGE_FERN = registerBlock(new TallBlockItem(Blocks.LARGE_FERN, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));

   public static final Item PRISMARINE = registerBlock(Blocks.PRISMARINE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PRISMARINE_BRICKS = registerBlock(Blocks.PRISMARINE_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DARK_PRISMARINE = registerBlock(Blocks.DARK_PRISMARINE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PRISMARINE_STAIRS = registerBlock(Blocks.PRISMARINE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PRISMARINE_BRICK_STAIRS = registerBlock(Blocks.PRISMARINE_BRICK_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DARK_PRISMARINE_STAIRS = registerBlock(Blocks.DARK_PRISMARINE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SEA_LANTERN = registerBlock(Blocks.SEA_LANTERN, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item RED_SANDSTONE = registerBlock(Blocks.RED_SANDSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CHISELED_RED_SANDSTONE = registerBlock(Blocks.CHISELED_RED_SANDSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CUT_RED_SANDSTONE = registerBlock(Blocks.CUT_RED_SANDSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item RED_SANDSTONE_STAIRS = registerBlock(Blocks.RED_SANDSTONE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item REPEATING_COMMAND_BLOCK = registerBlock(new OperatorOnlyItem(Blocks.REPEATING_COMMAND_BLOCK, (new Item.Properties()).rarity(Rarity.EPIC)));
   public static final Item CHAIN_COMMAND_BLOCK = registerBlock(new OperatorOnlyItem(Blocks.CHAIN_COMMAND_BLOCK, (new Item.Properties()).rarity(Rarity.EPIC)));
   public static final Item MAGMA_BLOCK = registerBlock(Blocks.MAGMA_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item NETHER_WART_BLOCK = registerBlock(Blocks.NETHER_WART_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item WARPED_WART_BLOCK = registerBlock(Blocks.WARPED_WART_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item RED_NETHER_BRICKS = registerBlock(Blocks.RED_NETHER_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BONE_BLOCK = registerBlock(Blocks.BONE_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STRUCTURE_VOID = registerBlock(Blocks.STRUCTURE_VOID);
   public static final Item OBSERVER = registerBlock(Blocks.OBSERVER, ItemGroup.TAB_FUNCTIONAL);



   public static final Item TURTLE_EGG = registerBlock(Blocks.TURTLE_EGG, ItemGroup.TAB_MISC);
   public static final Item SNIFFER_EGG = registerBlock(Blocks.SNIFFER_EGG, ItemGroup.TAB_MISC);
   public static final Item DEAD_TUBE_CORAL_BLOCK = registerBlock(Blocks.DEAD_TUBE_CORAL_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEAD_BRAIN_CORAL_BLOCK = registerBlock(Blocks.DEAD_BRAIN_CORAL_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEAD_BUBBLE_CORAL_BLOCK = registerBlock(Blocks.DEAD_BUBBLE_CORAL_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEAD_FIRE_CORAL_BLOCK = registerBlock(Blocks.DEAD_FIRE_CORAL_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEAD_HORN_CORAL_BLOCK = registerBlock(Blocks.DEAD_HORN_CORAL_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item TUBE_CORAL_BLOCK = registerBlock(Blocks.TUBE_CORAL_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BRAIN_CORAL_BLOCK = registerBlock(Blocks.BRAIN_CORAL_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BUBBLE_CORAL_BLOCK = registerBlock(Blocks.BUBBLE_CORAL_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item FIRE_CORAL_BLOCK = registerBlock(Blocks.FIRE_CORAL_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item HORN_CORAL_BLOCK = registerBlock(Blocks.HORN_CORAL_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item TUBE_CORAL = registerBlock(Blocks.TUBE_CORAL, ItemGroup.TAB_NATURAL);
   public static final Item BRAIN_CORAL = registerBlock(Blocks.BRAIN_CORAL, ItemGroup.TAB_NATURAL);
   public static final Item BUBBLE_CORAL = registerBlock(Blocks.BUBBLE_CORAL, ItemGroup.TAB_NATURAL);
   public static final Item FIRE_CORAL = registerBlock(Blocks.FIRE_CORAL, ItemGroup.TAB_NATURAL);
   public static final Item HORN_CORAL = registerBlock(Blocks.HORN_CORAL, ItemGroup.TAB_NATURAL);
   public static final Item DEAD_BRAIN_CORAL = registerBlock(Blocks.DEAD_BRAIN_CORAL, ItemGroup.TAB_NATURAL);
   public static final Item DEAD_BUBBLE_CORAL = registerBlock(Blocks.DEAD_BUBBLE_CORAL, ItemGroup.TAB_NATURAL);
   public static final Item DEAD_FIRE_CORAL = registerBlock(Blocks.DEAD_FIRE_CORAL, ItemGroup.TAB_NATURAL);
   public static final Item DEAD_HORN_CORAL = registerBlock(Blocks.DEAD_HORN_CORAL, ItemGroup.TAB_NATURAL);
   public static final Item DEAD_TUBE_CORAL = registerBlock(Blocks.DEAD_TUBE_CORAL, ItemGroup.TAB_NATURAL);
   public static final Item TUBE_CORAL_FAN = registerBlock(new WallOrFloorItem(Blocks.TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item BRAIN_CORAL_FAN = registerBlock(new WallOrFloorItem(Blocks.BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item BUBBLE_CORAL_FAN = registerBlock(new WallOrFloorItem(Blocks.BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item FIRE_CORAL_FAN = registerBlock(new WallOrFloorItem(Blocks.FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item HORN_CORAL_FAN = registerBlock(new WallOrFloorItem(Blocks.HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item DEAD_TUBE_CORAL_FAN = registerBlock(new WallOrFloorItem(Blocks.DEAD_TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item DEAD_BRAIN_CORAL_FAN = registerBlock(new WallOrFloorItem(Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item DEAD_BUBBLE_CORAL_FAN = registerBlock(new WallOrFloorItem(Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item DEAD_FIRE_CORAL_FAN = registerBlock(new WallOrFloorItem(Blocks.DEAD_FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item DEAD_HORN_CORAL_FAN = registerBlock(new WallOrFloorItem(Blocks.DEAD_HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item BLUE_ICE = registerBlock(Blocks.BLUE_ICE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CONDUIT = registerBlock(new BlockItem(Blocks.CONDUIT, (new Item.Properties()).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE)));
   public static final Item POLISHED_GRANITE_STAIRS = registerBlock(Blocks.POLISHED_GRANITE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SMOOTH_RED_SANDSTONE_STAIRS = registerBlock(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item MOSSY_STONE_BRICK_STAIRS = registerBlock(Blocks.MOSSY_STONE_BRICK_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_DIORITE_STAIRS = registerBlock(Blocks.POLISHED_DIORITE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item MOSSY_COBBLESTONE_STAIRS = registerBlock(Blocks.MOSSY_COBBLESTONE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item END_STONE_BRICK_STAIRS = registerBlock(Blocks.END_STONE_BRICK_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item STONE_STAIRS = registerBlock(Blocks.STONE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SMOOTH_SANDSTONE_STAIRS = registerBlock(Blocks.SMOOTH_SANDSTONE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SMOOTH_QUARTZ_STAIRS = registerBlock(Blocks.SMOOTH_QUARTZ_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item GRANITE_STAIRS = registerBlock(Blocks.GRANITE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item ANDESITE_STAIRS = registerBlock(Blocks.ANDESITE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item RED_NETHER_BRICK_STAIRS = registerBlock(Blocks.RED_NETHER_BRICK_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_ANDESITE_STAIRS = registerBlock(Blocks.POLISHED_ANDESITE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DIORITE_STAIRS = registerBlock(Blocks.DIORITE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_GRANITE_SLAB = registerBlock(Blocks.POLISHED_GRANITE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SMOOTH_RED_SANDSTONE_SLAB = registerBlock(Blocks.SMOOTH_RED_SANDSTONE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item MOSSY_STONE_BRICK_SLAB = registerBlock(Blocks.MOSSY_STONE_BRICK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_DIORITE_SLAB = registerBlock(Blocks.POLISHED_DIORITE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item MOSSY_COBBLESTONE_SLAB = registerBlock(Blocks.MOSSY_COBBLESTONE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item END_STONE_BRICK_SLAB = registerBlock(Blocks.END_STONE_BRICK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SMOOTH_SANDSTONE_SLAB = registerBlock(Blocks.SMOOTH_SANDSTONE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SMOOTH_QUARTZ_SLAB = registerBlock(Blocks.SMOOTH_QUARTZ_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item GRANITE_SLAB = registerBlock(Blocks.GRANITE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item ANDESITE_SLAB = registerBlock(Blocks.ANDESITE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item RED_NETHER_BRICK_SLAB = registerBlock(Blocks.RED_NETHER_BRICK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_ANDESITE_SLAB = registerBlock(Blocks.POLISHED_ANDESITE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DIORITE_SLAB = registerBlock(Blocks.DIORITE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item SCAFFOLDING = registerBlock(new ScaffoldingItem(Blocks.SCAFFOLDING, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item IRON_DOOR = registerBlock(new TallBlockItem(Blocks.IRON_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item OAK_DOOR = registerBlock(new TallBlockItem(Blocks.OAK_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item PALE_OAK_DOOR = registerBlock(new TallBlockItem(Blocks.PALE_OAK_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));

   public static final Item SPRUCE_DOOR = registerBlock(new TallBlockItem(Blocks.SPRUCE_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item BIRCH_DOOR = registerBlock(new TallBlockItem(Blocks.BIRCH_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item JUNGLE_DOOR = registerBlock(new TallBlockItem(Blocks.JUNGLE_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item ACACIA_DOOR = registerBlock(new TallBlockItem(Blocks.ACACIA_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item DARK_OAK_DOOR = registerBlock(new TallBlockItem(Blocks.DARK_OAK_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item CRIMSON_DOOR = registerBlock(new TallBlockItem(Blocks.CRIMSON_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item WARPED_DOOR = registerBlock(new TallBlockItem(Blocks.WARPED_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item REPEATER = registerBlock(Blocks.REPEATER, ItemGroup.TAB_FUNCTIONAL);
   public static final Item COMPARATOR = registerBlock(Blocks.COMPARATOR, ItemGroup.TAB_FUNCTIONAL);
   public static final Item STRUCTURE_BLOCK = registerBlock(new OperatorOnlyItem(Blocks.STRUCTURE_BLOCK, (new Item.Properties()).rarity(Rarity.EPIC)));
   public static final Item JIGSAW = registerBlock(new OperatorOnlyItem(Blocks.JIGSAW, (new Item.Properties()).rarity(Rarity.EPIC)));
   public static final Item SCUTE = registerItem("scute", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item TOASTED_SCUTE = registerItem("toasted_scute", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item FLINT_AND_STEEL = registerItem("flint_and_steel", new FlintAndSteelItem((new Item.Properties()).durability(64).tab(ItemGroup.TAB_TOOLS)));


   public static final Item COAL = registerItem("coal", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item CHARCOAL = registerItem("charcoal", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item DIAMOND = registerItem("diamond", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item RAW_IRON = Items.registerItem("raw_iron", new Item(new Item.Properties()));
   public static final Item IRON_INGOT = registerItem("iron_ingot", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS).weight(3)));
   public static final Item ROSE_GOLD_INGOT = registerItem("rose_gold_ingot", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));

   public static final Item RAW_COPPER = Items.registerItem("raw_copper", new Item(new Item.Properties()));
   public static final Item COPPER_INGOT = registerItem("copper_ingot", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS).weight(3)));
   public static final Item RAW_ROSE_GOLD = Items.registerItem("raw_rose_gold", new Item(new Item.Properties()));

   public static final Item RAW_GOLD = Items.registerItem("raw_gold", new Item(new Item.Properties()));


   public static final Item GOLD_INGOT = registerItem("gold_ingot", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item NETHERITE_INGOT = registerItem("netherite_ingot", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS).fireResistant()));
   public static final Item NETHERITE_SCRAP = registerItem("netherite_scrap", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS).fireResistant()));
   public static final Item WOODEN_SHOVEL = registerItem("wooden_shovel", new ShovelItem(ItemTier.WOOD, 1.5F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item WOODEN_PICKAXE = registerItem("wooden_pickaxe", new PickaxeItem(ItemTier.WOOD, 1, -2.8F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item WOODEN_HOE = registerItem("wooden_hoe", new HoeItem(ItemTier.WOOD, 0, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item STONE_SHOVEL = registerItem("stone_shovel", new ShovelItem(ItemTier.STONE, 1.5F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item STONE_PICKAXE = registerItem("stone_pickaxe", new PickaxeItem(ItemTier.STONE, 1, -2.8F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item STONE_HOE = registerItem("stone_hoe", new HoeItem(ItemTier.STONE, -1, -2.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item GOLDEN_SHOVEL = registerItem("golden_shovel", new ShovelItem(ItemTier.GOLD, 1.5F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item GOLDEN_PICKAXE = registerItem("golden_pickaxe", new PickaxeItem(ItemTier.GOLD, 1, -2.8F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item GOLDEN_HOE = registerItem("golden_hoe", new HoeItem(ItemTier.GOLD, 0, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));

   public static final Item ROSE_GOLD_SHOVEL = registerItem("rose_gold_shovel", new ShovelItem(ItemTier.ROSE_GOLD, 1.5F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item ROSE_GOLD_PICKAXE = registerItem("rose_gold_pickaxe", new PickaxeItem(ItemTier.ROSE_GOLD, 1, -2.8F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item ROSE_GOLD_HOE = registerItem("rose_gold_hoe", new HoeItem(ItemTier.ROSE_GOLD, 0, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));


   public static final Item IRON_SHOVEL = registerItem("iron_shovel", new ShovelItem(ItemTier.IRON, 1.5F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item IRON_PICKAXE = registerItem("iron_pickaxe", new PickaxeItem(ItemTier.IRON, 1, -2.8F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));


   public static final Item IRON_HOE = registerItem("iron_hoe", new HoeItem(ItemTier.IRON, -2, -1.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item DIAMOND_SHOVEL = registerItem("diamond_shovel", new ShovelItem(ItemTier.DIAMOND, 1.5F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item DIAMOND_PICKAXE = registerItem("diamond_pickaxe", new PickaxeItem(ItemTier.DIAMOND, 1, -2.8F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));


   public static final Item BEEKEEPER = registerItem("beekeeper", new BeekeeperItem());
   public static final Item STARFURY = registerItem("starfury", new StarfuryItem());
   public static final Item MEOWMERE = registerItem("meowmere", new MeowmereItem());
   public static final Item STAR_WRATH = registerItem("star_wrath", new StarWrathItem());
   public static final Item ACCESSORY_HOLDER = registerItem("accessory_holder", new AccessoryHolderItem());
   public static final Item HONEY_COMB_ACCESSORY = registerItem("honey_comb_accessory", new HoneyCombAccessoryItem());
   public static final Item ANKH_SHIELD = registerItem("ankh_shield", new AnkhShieldItem());
   public static final Item BAND_OF_REGENERATION = registerItem("band_of_regeneration", new RegenerationBandItem());
   public static final Item STAR_CLOAK = registerItem("star_cloak", new StarCloakItem());
   public static final Item BEE_CLOAK = registerItem("bee_cloak", new BeeCloakItem());
   public static final Item LAVA_CHARM = registerItem("lava_charm", new LavaCharmItem());
   public static final Item OBSIDIAN_SKULL = registerItem("obsidian_skull", new ObsidianSkullItem());
   public static final Item OBSIDIAN_ROSE = registerItem("obsidian_rose", new ObsidianRoseItem());
   public static final Item MOLTEN_SKULL_ROSE = registerItem("molten_skull_rose", new MoltenSkullRoseItem());
   public static final Item CROSS_NECKLACE = registerAccessoryItem("cross_necklace", 100, Rarity.REDD);
   public static final Item FROZEN_SHIELD = registerAccessoryItem("frozen_shield", 1400, Rarity.PINK);
   public static final Item WORM_SCARF = registerAccessoryItem("worm_scarf", 760, Rarity.PINK);
  // public static final Item SHIELD_OF_CTHULHU = registerItem("shield_of_cthulhu", new ShieldOfCthulhuItem());



   private static SimpleAccessoryItem registerAccessoryItem(String name, int durability, Rarity rarity) {
      return (SimpleAccessoryItem) registerItem(name, new SimpleAccessoryItem(durability, rarity));
   }











   public static final Item DIAMOND_HOE = registerItem("diamond_hoe", new HoeItem(ItemTier.DIAMOND, -3, 0.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));

   public static final Item NETHERITE_SHOVEL = registerItem("netherite_shovel", new ShovelItem(ItemTier.NETHERITE, 1.5F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS).fireResistant()));
   public static final Item NETHERITE_PICKAXE = registerItem("netherite_pickaxe", new PickaxeItem(ItemTier.NETHERITE, 1, -2.8F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS).fireResistant()));
   public static final Item NETHERITE_HOE = registerItem("netherite_hoe", new HoeItem(ItemTier.NETHERITE, -4, 0.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS).fireResistant()));
   public static final Item STICK = registerItem("stick", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));


   public static final Item FIREWORK_STAFF = registerItem("firework_staff", new Item((new Item.Properties().stacksTo(1)).tab(ItemGroup.TAB_MATERIALS)));



   public static final Item BOWL = registerItem("bowl", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item STRING = registerItem("string", new BlockNamedItem(Blocks.TRIPWIRE, (new Item.Properties()).tab(ItemGroup.TAB_MISC)));
   public static final Item FEATHER = registerItem("feather", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item ROADRUNNER_FEATHER = registerItem("roadrunner_feather", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item MEEP_FEATHER = registerItem("meep_feather", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));


   public static final Item GUNPOWDER = registerItem("gunpowder", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item WHEAT_SEEDS = registerItem("wheat_seeds", new BlockNamedItem(Blocks.WHEAT, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item WHEAT = registerItem("wheat", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));

   public static final Item FLINT = registerItem("flint", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));

   public static final Item PAINTING = registerItem("painting", new HangingEntityItem(EntityType.PAINTING, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));

   public static final Item OAK_SIGN = registerItem("oak_sign", new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_NATURAL), Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN));
   public static final Item PALE_OAK_SIGN = registerItem("pale_oak_sign", new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_NATURAL), Blocks.PALE_OAK_SIGN, Blocks.PALE_OAK_WALL_SIGN));

   public static final Item SPRUCE_SIGN = registerItem("spruce_sign", new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_NATURAL), Blocks.SPRUCE_SIGN, Blocks.SPRUCE_WALL_SIGN));
   public static final Item BIRCH_SIGN = registerItem("birch_sign", new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_NATURAL), Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN));
   public static final Item JUNGLE_SIGN = registerItem("jungle_sign", new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_NATURAL), Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN));
   public static final Item ACACIA_SIGN = registerItem("acacia_sign", new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_NATURAL), Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN));
   public static final Item DARK_OAK_SIGN = registerItem("dark_oak_sign", new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_NATURAL), Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_WALL_SIGN));
   public static final Item CRIMSON_SIGN = registerItem("crimson_sign", new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_NATURAL), Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN));
   public static final Item WARPED_SIGN = registerItem("warped_sign", new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_NATURAL), Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN));
   public static final Item WATER_BUCKET = registerItem("water_bucket", new BucketItem(Fluids.WATER, (new Item.Properties()).craftRemainder(BUCKET).stacksTo(1).tab(ItemGroup.TAB_MISC)));
   public static final Item LAVA_BUCKET = registerItem("lava_bucket", new BucketItem(Fluids.LAVA, (new Item.Properties()).craftRemainder(BUCKET).stacksTo(1).tab(ItemGroup.TAB_MISC)));
   public static final Item MINECART = registerItem("minecart", new MinecartItem(AbstractMinecartEntity.Type.RIDEABLE, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_REDSTONE)));
   public static final Item SADDLE = registerItem("saddle", new SaddleItem((new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_REDSTONE)));
   public static final Item REDSTONE = registerItem("redstone", new BlockNamedItem(Blocks.REDSTONE_WIRE, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));


   public static final Item OAK_BOAT = registerItem("oak_boat", new BoatItem(BoatEntity.Type.OAK, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_REDSTONE)));
   public static final Item LEATHER = registerItem("leather", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item PUFFERFISH_BUCKET = registerItem("pufferfish_bucket", new FishBucketItem(EntityType.PUFFERFISH, Fluids.WATER, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC)));
   public static final Item SALMON_BUCKET = registerItem("salmon_bucket", new FishBucketItem(EntityType.SALMON, Fluids.WATER, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC)));
   public static final Item COD_BUCKET = registerItem("cod_bucket", new FishBucketItem(EntityType.COD, Fluids.WATER, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC)));
   public static final Item TROPICAL_FISH_BUCKET = registerItem("tropical_fish_bucket", new FishBucketItem(EntityType.TROPICAL_FISH, Fluids.WATER, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC)));
   public static final Item BRICK = registerItem("brick", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item CLAY_BALL = registerItem("clay_ball", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item DRIED_KELP_BLOCK = registerBlock(Blocks.DRIED_KELP_BLOCK, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item PAPER = registerItem("paper", new Item((new Item.Properties()).tab(ItemGroup.TAB_MISC)));
   public static final Item BOOK = registerItem("book", new BookItem((new Item.Properties()).tab(ItemGroup.TAB_MISC)));
   public static final Item SLIME_BALL = registerItem("slime_ball", new Item((new Item.Properties()).tab(ItemGroup.TAB_MISC)));
   public static final Item CHEST_MINECART = registerItem("chest_minecart", new MinecartItem(AbstractMinecartEntity.Type.CHEST, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_REDSTONE)));
   public static final Item FURNACE_MINECART = registerItem("furnace_minecart", new MinecartItem(AbstractMinecartEntity.Type.FURNACE, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_REDSTONE)));

   public static final Item STAR = registerItem("star", new AirItem(Blocks.AIR, new Item.Properties()));
   public static final Item MEOWMERE_CAT = registerItem("meowmere_cat", new AirItem(Blocks.AIR, new Item.Properties()));

   public static final Item PURPLE_STAR = registerItem("purple_star", new AirItem(Blocks.AIR, new Item.Properties()));

   public static final Item COMPASS = registerItem("compass", new CompassItem((new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item FISHING_ROD = registerItem("fishing_rod", new FishingRodItem((new Item.Properties()).durability(64).tab(ItemGroup.TAB_TOOLS)));
   public static final Item GRAPPLING_HOOK = registerItem("grappling_hook", new GrapplingHookItem((new Item.Properties()).durability(128).tab(ItemGroup.TAB_TOOLS)));
   public static final Item CLOCK = registerItem("clock", new Item((new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item GLOWSTONE_DUST = registerItem("glowstone_dust", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));

   public static final Item INK_SAC = registerItem("ink_sac", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item COCOA_BEANS = registerItem("cocoa_beans", new BlockNamedItem(Blocks.COCOA, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item LAPIS_LAZULI = registerItem("lapis_lazuli", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item AMETHYST_SHARD = registerItem("amethyst_shard", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item ECHO_SHARD = registerItem("echo_shard", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));

   public static final Item WHITE_DYE = registerItem("white_dye", new DyeItem(DyeColor.WHITE, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item ORANGE_DYE = registerItem("orange_dye", new DyeItem(DyeColor.ORANGE, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item MAGENTA_DYE = registerItem("magenta_dye", new DyeItem(DyeColor.MAGENTA, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item LIGHT_BLUE_DYE = registerItem("light_blue_dye", new DyeItem(DyeColor.LIGHT_BLUE, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item YELLOW_DYE = registerItem("yellow_dye", new DyeItem(DyeColor.YELLOW, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item LIME_DYE = registerItem("lime_dye", new DyeItem(DyeColor.LIME, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item PINK_DYE = registerItem("pink_dye", new DyeItem(DyeColor.PINK, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item GRAY_DYE = registerItem("gray_dye", new DyeItem(DyeColor.GRAY, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item LIGHT_GRAY_DYE = registerItem("light_gray_dye", new DyeItem(DyeColor.LIGHT_GRAY, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item CYAN_DYE = registerItem("cyan_dye", new DyeItem(DyeColor.CYAN, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item PURPLE_DYE = registerItem("purple_dye", new DyeItem(DyeColor.PURPLE, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item BLUE_DYE = registerItem("blue_dye", new DyeItem(DyeColor.BLUE, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item BROWN_DYE = registerItem("brown_dye", new DyeItem(DyeColor.BROWN, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item GREEN_DYE = registerItem("green_dye", new DyeItem(DyeColor.GREEN, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item RED_DYE = registerItem("red_dye", new DyeItem(DyeColor.RED, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item BLACK_DYE = registerItem("black_dye", new DyeItem(DyeColor.BLACK, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));


   public static final Item WHITE_PAINT_TUBE = registerItem("white_paint_tube", new PaintTubeItem(DyeColor.WHITE, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item ORANGE_PAINT_TUBE = registerItem("orange_paint_tube", new PaintTubeItem(DyeColor.ORANGE, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item MAGENTA_PAINT_TUBE = registerItem("magenta_paint_tube", new PaintTubeItem(DyeColor.MAGENTA, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item LIGHT_BLUE_PAINT_TUBE = registerItem("light_blue_paint_tube", new PaintTubeItem(DyeColor.LIGHT_BLUE, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item YELLOW_PAINT_TUBE = registerItem("yellow_paint_tube", new PaintTubeItem(DyeColor.YELLOW, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item LIME_PAINT_TUBE = registerItem("lime_paint_tube", new PaintTubeItem(DyeColor.LIME, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item PINK_PAINT_TUBE = registerItem("pink_paint_tube", new PaintTubeItem(DyeColor.PINK, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item GRAY_PAINT_TUBE = registerItem("gray_paint_tube", new PaintTubeItem(DyeColor.GRAY, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item LIGHT_GRAY_PAINT_TUBE = registerItem("light_gray_paint_tube", new PaintTubeItem(DyeColor.LIGHT_GRAY, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item CYAN_PAINT_TUBE = registerItem("cyan_paint_tube", new PaintTubeItem(DyeColor.CYAN, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item PURPLE_PAINT_TUBE = registerItem("purple_paint_tube", new PaintTubeItem(DyeColor.PURPLE, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item BLUE_PAINT_TUBE = registerItem("blue_paint_tube", new PaintTubeItem(DyeColor.BLUE, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item BROWN_PAINT_TUBE = registerItem("brown_paint_tube", new PaintTubeItem(DyeColor.BROWN, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item GREEN_PAINT_TUBE = registerItem("green_paint_tube", new PaintTubeItem(DyeColor.GREEN, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item RED_PAINT_TUBE = registerItem("red_paint_tube", new PaintTubeItem(DyeColor.RED, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item BLACK_PAINT_TUBE = registerItem("black_paint_tube", new PaintTubeItem(DyeColor.BLACK, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));



   public static final Item BONE_MEAL = registerItem("bone_meal", new BoneMealItem((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item BONE = registerItem("bone", new Item((new Item.Properties()).tab(ItemGroup.TAB_MISC)));
   public static final Item WITHER_BONE = registerItem("wither_bone", new Item((new Item.Properties()).tab(ItemGroup.TAB_MISC)));
   public static final Item FORTIFIED_WITHER_BONE = registerItem("fortified_wither_bone", new Item((new Item.Properties()).tab(ItemGroup.TAB_MISC)));

   public static final Item SUGAR = registerItem("sugar", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item SPYGLASS = Items.registerItem("spyglass", (Item)new SpyglassItem(new Item.Properties().stacksTo(1)));


   public static final Item FILLED_MAP = registerItem("filled_map", new FilledMapItem(new Item.Properties()));
   public static final Item SHEARS = registerItem("shears", new ShearsItem((new Item.Properties()).durability(238).tab(ItemGroup.TAB_TOOLS)));
   public static final Item PUMPKIN_SEEDS = registerItem("pumpkin_seeds", new BlockNamedItem(Blocks.PUMPKIN_STEM, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item MELON_SEEDS = registerItem("melon_seeds", new BlockNamedItem(Blocks.MELON_STEM, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item ENDER_PEARL = registerItem("ender_pearl", new EnderPearlItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_MISC)));
   public static final Item BLAZE_ROD = registerItem("blaze_rod", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item GHAST_TEAR = registerItem("ghast_tear", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item GOLD_NUGGET = registerItem("gold_nugget", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item NETHER_WART = registerItem("nether_wart", new BlockNamedItem(Blocks.NETHER_WART, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item FERMENTED_SPIDER_EYE = registerItem("fermented_spider_eye", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item BLAZE_POWDER = registerItem("blaze_powder", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item MAGMA_CREAM = registerItem("magma_cream", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item BREWING_STAND = registerBlock(Blocks.BREWING_STAND, ItemGroup.TAB_MATERIALS);
   public static final Item CAULDRON = registerBlock(Blocks.CAULDRON, ItemGroup.TAB_MATERIALS);
   public static final Item ENDER_EYE = registerItem("ender_eye", new EnderEyeItem((new Item.Properties()).tab(ItemGroup.TAB_MISC)));
   public static final Item GLISTERING_MELON_SLICE = registerItem("glistering_melon_slice", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item BAT_SPAWN_EGG = registerItem("bat_spawn_egg", new SpawnEggItem(EntityType.BAT, 4996656, 986895, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item OWL_SPAWN_EGG = registerItem("owl_spawn_egg", new SpawnEggItem(EntityType.OWL, 15582019, 15582019, (new Item.Properties().tab(ItemGroup.TAB_SPAWN_EGGS))));
   public static final Item BEE_SPAWN_EGG = registerItem("bee_spawn_egg", new SpawnEggItem(EntityType.BEE, 15582019, 4400155, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item BLAZE_SPAWN_EGG = registerItem("blaze_spawn_egg", new SpawnEggItem(EntityType.BLAZE, 16167425, 16775294, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item CAT_SPAWN_EGG = registerItem("cat_spawn_egg", new SpawnEggItem(EntityType.CAT, 15714446, 9794134, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item CAVE_SPIDER_SPAWN_EGG = registerItem("cave_spider_spawn_egg", new SpawnEggItem(EntityType.CAVE_SPIDER, 803406, 11013646, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item CHICKEN_SPAWN_EGG = registerItem("chicken_spawn_egg", new SpawnEggItem(EntityType.CHICKEN, 10592673, 16711680, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item COD_SPAWN_EGG = registerItem("cod_spawn_egg", new SpawnEggItem(EntityType.COD, 12691306, 15058059, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item COW_SPAWN_EGG = registerItem("cow_spawn_egg", new SpawnEggItem(EntityType.COW, 4470310, 10592673, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item CREEPER_SPAWN_EGG = registerItem("creeper_spawn_egg", new SpawnEggItem(EntityType.CREEPER, 894731, 0, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item DOLPHIN_SPAWN_EGG = registerItem("dolphin_spawn_egg", new SpawnEggItem(EntityType.DOLPHIN, 2243405, 16382457, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item DONKEY_SPAWN_EGG = registerItem("donkey_spawn_egg", new SpawnEggItem(EntityType.DONKEY, 5457209, 8811878, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item DROWNED_SPAWN_EGG = registerItem("drowned_spawn_egg", new SpawnEggItem(EntityType.DROWNED, 9433559, 7969893, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item ELDER_GUARDIAN_SPAWN_EGG = registerItem("elder_guardian_spawn_egg", new SpawnEggItem(EntityType.ELDER_GUARDIAN, 13552826, 7632531, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item ENDERMAN_SPAWN_EGG = registerItem("enderman_spawn_egg", new SpawnEggItem(EntityType.ENDERMAN, 1447446, 0, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item ENDERMITE_SPAWN_EGG = registerItem("endermite_spawn_egg", new SpawnEggItem(EntityType.ENDERMITE, 1447446, 7237230, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item WITHER_SPAWN_EGG = registerItem((String)"wither_spawn_egg", new SpawnEggItem(EntityType.WITHER, 1315860, 5075616, new Item.Properties()));
   public static final Item ENDER_DRAGON_SPAWN_EGG = registerItem((String)"ender_dragon_spawn_egg", new SpawnEggItem(EntityType.ENDER_DRAGON, 1842204, 14711290, new Item.Properties()));
   public static final Item EVOKER_SPAWN_EGG = registerItem("evoker_spawn_egg", new SpawnEggItem(EntityType.EVOKER, 9804699, 1973274, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));

   public static final Item RACCOON_SPAWN_EGG = registerItem("raccoon_spawn_egg", new SpawnEggItem(EntityType.RACCOON, 14005919, 13396256, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item FOX_SPAWN_EGG = registerItem("fox_spawn_egg", new SpawnEggItem(EntityType.FOX, 14005919, 13396256, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item GHAST_SPAWN_EGG = registerItem("ghast_spawn_egg", new SpawnEggItem(EntityType.GHAST, 16382457, 12369084, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item GUARDIAN_SPAWN_EGG = registerItem("guardian_spawn_egg", new SpawnEggItem(EntityType.GUARDIAN, 5931634, 15826224, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item HOGLIN_SPAWN_EGG = registerItem("hoglin_spawn_egg", new SpawnEggItem(EntityType.HOGLIN, 13004373, 6251620, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item HORSE_SPAWN_EGG = registerItem("horse_spawn_egg", new SpawnEggItem(EntityType.HORSE, 12623485, 15656192, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item HUSK_SPAWN_EGG = registerItem("husk_spawn_egg", new SpawnEggItem(EntityType.HUSK, 7958625, 15125652, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item LLAMA_SPAWN_EGG = registerItem("llama_spawn_egg", new SpawnEggItem(EntityType.LLAMA, 12623485, 10051392, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item MAGMA_CUBE_SPAWN_EGG = registerItem("magma_cube_spawn_egg", new SpawnEggItem(EntityType.MAGMA_CUBE, 3407872, 16579584, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item MOOSHROOM_SPAWN_EGG = registerItem("mooshroom_spawn_egg", new SpawnEggItem(EntityType.MOOSHROOM, 10489616, 12040119, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item MULE_SPAWN_EGG = registerItem("mule_spawn_egg", new SpawnEggItem(EntityType.MULE, 1769984, 5321501, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item OCELOT_SPAWN_EGG = registerItem("ocelot_spawn_egg", new SpawnEggItem(EntityType.OCELOT, 15720061, 5653556, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item PANDA_SPAWN_EGG = registerItem("panda_spawn_egg", new SpawnEggItem(EntityType.PANDA, 15198183, 1776418, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item PARROT_SPAWN_EGG = registerItem("parrot_spawn_egg", new SpawnEggItem(EntityType.PARROT, 894731, 16711680, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item PHANTOM_SPAWN_EGG = registerItem("phantom_spawn_egg", new SpawnEggItem(EntityType.PHANTOM, 4411786, 8978176, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item PIG_SPAWN_EGG = registerItem("pig_spawn_egg", new SpawnEggItem(EntityType.PIG, 15771042, 14377823, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item PIGLIN_SPAWN_EGG = registerItem("piglin_spawn_egg", new SpawnEggItem(EntityType.PIGLIN, 10051392, 16380836, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item PIGLIN_BRUTE_SPAWN_EGG = registerItem("piglin_brute_spawn_egg", new SpawnEggItem(EntityType.PIGLIN_BRUTE, 5843472, 16380836, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item PILLAGER_SPAWN_EGG = registerItem("pillager_spawn_egg", new SpawnEggItem(EntityType.PILLAGER, 5451574, 9804699, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item POLAR_BEAR_SPAWN_EGG = registerItem("polar_bear_spawn_egg", new SpawnEggItem(EntityType.POLAR_BEAR, 15921906, 9803152, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item PUFFERFISH_SPAWN_EGG = registerItem("pufferfish_spawn_egg", new SpawnEggItem(EntityType.PUFFERFISH, 16167425, 3654642, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item RABBIT_SPAWN_EGG = registerItem("rabbit_spawn_egg", new SpawnEggItem(EntityType.RABBIT, 10051392, 7555121, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item RAVAGER_SPAWN_EGG = registerItem("ravager_spawn_egg", new SpawnEggItem(EntityType.RAVAGER, 7697520, 5984329, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item SALMON_SPAWN_EGG = registerItem("salmon_spawn_egg", new SpawnEggItem(EntityType.SALMON, 10489616, 951412, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item SHEEP_SPAWN_EGG = registerItem("sheep_spawn_egg", new SpawnEggItem(EntityType.SHEEP, 15198183, 16758197, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item SHULKER_SPAWN_EGG = registerItem("shulker_spawn_egg", new SpawnEggItem(EntityType.SHULKER, 9725844, 5060690, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item SILVERFISH_SPAWN_EGG = registerItem("silverfish_spawn_egg", new SpawnEggItem(EntityType.SILVERFISH, 7237230, 3158064, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item SKELETON_SPAWN_EGG = registerItem("skeleton_spawn_egg", new SpawnEggItem(EntityType.SKELETON, 12698049, 4802889, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item BOGGED_SPAWN_EGG = registerItem("bogged_spawn_egg", new SpawnEggItem(EntityType.BOGGED, 9084018, 3231003, new Item.Properties().tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item SKELETON_HORSE_SPAWN_EGG = registerItem("skeleton_horse_spawn_egg", new SpawnEggItem(EntityType.SKELETON_HORSE, 6842447, 15066584, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item SLIME_SPAWN_EGG = registerItem("slime_spawn_egg", new SpawnEggItem(EntityType.SLIME, 5349438, 8306542, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item SPIDER_SPAWN_EGG = registerItem("spider_spawn_egg", new SpawnEggItem(EntityType.SPIDER, 3419431, 11013646, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item SQUID_SPAWN_EGG = registerItem("squid_spawn_egg", new SpawnEggItem(EntityType.SQUID, 2243405, 7375001, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item STRAY_SPAWN_EGG = registerItem("stray_spawn_egg", new SpawnEggItem(EntityType.STRAY, 6387319, 14543594, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item STRIDER_SPAWN_EGG = registerItem("strider_spawn_egg", new SpawnEggItem(EntityType.STRIDER, 10236982, 5065037, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item TRADER_LLAMA_SPAWN_EGG = registerItem("trader_llama_spawn_egg", new SpawnEggItem(EntityType.TRADER_LLAMA, 15377456, 4547222, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item TROPICAL_FISH_SPAWN_EGG = registerItem("tropical_fish_spawn_egg", new SpawnEggItem(EntityType.TROPICAL_FISH, 15690005, 16775663, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item TURTLE_SPAWN_EGG = registerItem("turtle_spawn_egg", new SpawnEggItem(EntityType.TURTLE, 15198183, 44975, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item VEX_SPAWN_EGG = registerItem("vex_spawn_egg", new SpawnEggItem(EntityType.VEX, 8032420, 15265265, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));

   public static final Item VILLAGER_SPAWN_EGG = registerItem("villager_spawn_egg", new SpawnEggItem(EntityType.VILLAGER, 5651507, 12422002, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item IRON_GOLEM_SPAWN_EGG = registerItem((String)"iron_golem_spawn_egg", new SpawnEggItem(EntityType.IRON_GOLEM, 14405058, 7643954, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS), (mob, world, pos, player) -> {
      if (mob instanceof IronGolemEntity) {
         mob.as(IronGolemEntity.class).setPlayerCreated(player != null);
      }
   }));
   public static final Item SNOW_GOLEM_SPAWN_EGG = registerItem((String)"snow_golem_spawn_egg", new SpawnEggItem(EntityType.SNOW_GOLEM, 14283506, 8496292, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item VINDICATOR_SPAWN_EGG = registerItem("vindicator_spawn_egg", new SpawnEggItem(EntityType.VINDICATOR, 9804699, 2580065, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item WANDERING_TRADER_SPAWN_EGG = registerItem("wandering_trader_spawn_egg", new SpawnEggItem(EntityType.WANDERING_TRADER, 4547222, 15377456, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item WARDEN_SPAWN_EGG = Items.registerItem("warden_spawn_egg", (Item)new SpawnEggItem(EntityType.WARDEN, 1001033, 3790560, new Item.Properties().tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item WITCH_SPAWN_EGG = registerItem("witch_spawn_egg", new SpawnEggItem(EntityType.WITCH, 3407872, 5349438, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item WITHER_SKELETON_SPAWN_EGG = registerItem("wither_skeleton_spawn_egg", new SpawnEggItem(EntityType.WITHER_SKELETON, 1315860, 4672845, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item WOLF_SPAWN_EGG = registerItem("wolf_spawn_egg", new SpawnEggItem(EntityType.WOLF, 14144467, 13545366, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item ZOGLIN_SPAWN_EGG = registerItem("zoglin_spawn_egg", new SpawnEggItem(EntityType.ZOGLIN, 13004373, 15132390, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item GIANT_SPAWN_EGG = registerItem("giant_spawn_egg", new SpawnEggItem(EntityType.GIANT, 44975, 7969893, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));

   public static final Item ZOMBIE_SPAWN_EGG = registerItem("zombie_spawn_egg", new SpawnEggItem(EntityType.ZOMBIE, 44975, 7969893, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item ZOMBIE_HORSE_SPAWN_EGG = registerItem("zombie_horse_spawn_egg", new SpawnEggItem(EntityType.ZOMBIE_HORSE, 3232308, 9945732, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item ZOMBIE_VILLAGER_SPAWN_EGG = registerItem("zombie_villager_spawn_egg", new SpawnEggItem(EntityType.ZOMBIE_VILLAGER, 5651507, 7969893, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item ZOMBIFIED_PIGLIN_SPAWN_EGG = registerItem("zombified_piglin_spawn_egg", new SpawnEggItem(EntityType.ZOMBIFIED_PIGLIN, 15373203, 5009705, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item CREAKING_SPAWN_EGG = registerItem("creaking_spawn_egg", new SpawnEggItem(EntityType.CREAKING, 0x5F5F5F, 16545810, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));


   public static final Item RATTATA_SPAWN_EGG = registerItem("rattata_spawn_egg", new SpawnEggItem(EntityType.RATTATA, 0x8e6ab0, 0xbb9985, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS)));
   public static final Item SHINY_RATTATA_SPAWN_EGG = registerItem("shiny_rattata_spawn_egg", new SpawnEggItem(EntityType.RATTATA, 0xbaba5c, 0xbb9985, (new Item.Properties()).tab(ItemGroup.TAB_SPAWN_EGGS), (mob, world, pos, player) -> {
      if (mob instanceof Pokemon) {
         mob.getEntityData().set(Pokemon.IS_SHINY, true);
      }
   }));


   static {
      for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
         if (SpawnEggItem.byId(entityType) == null && entityType.canSummon() && entityType.getCategory() != EntityClassification.MISC) {
            String entityName = entityType.getRegistryName().getPath();
            int primaryColor = generateColor(entityType);
            int secondaryColor = generateSecondaryColor(entityType);

            registerItem(entityName + "_spawn_egg", new SpawnEggItem(entityType, primaryColor, secondaryColor, new Item.Properties().tab(ItemGroup.TAB_SPAWN_EGGS)));

            createModelJsonIfNotExists(entityName);
         }
      }
   }

   private static final String MODEL_DIRECTORY = "G:\\MCP-Reborn-1.16-MOJO\\src\\main\\resources\\assets\\minecraft\\models\\item";

   private static void createModelJsonIfNotExists(String entityName) {
      File jsonFile = new File(MODEL_DIRECTORY, entityName + "_spawn_egg.json");

      if (!jsonFile.exists()) {
         try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write("{\n  \"parent\": \"minecraft:item/template_spawn_egg\"\n}");
            System.out.println("Created model JSON for: " + entityName + "_spawn_egg");
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private static int generateColor(EntityType<?> entityType) {
      return entityType.hashCode() & 0xFFFFFF;
   }

   private static int generateSecondaryColor(EntityType<?> entityType) {
      return (~entityType.hashCode()) & 0xFFFFFF;
   }




   public static final Item EXPERIENCE_BOTTLE = registerItem("experience_bottle", new ExperienceBottleItem((new Item.Properties()).tab(ItemGroup.TAB_MISC).rarity(Rarity.UNCOMMON)));
   public static final Item FIRE_CHARGE = registerItem("fire_charge", new FireChargeItem((new Item.Properties()).tab(ItemGroup.TAB_MISC)));
   public static final Item WRITABLE_BOOK = registerItem("writable_book", new WritableBookItem((new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC)));
   public static final Item WRITTEN_BOOK = registerItem("written_book", new WrittenBookItem((new Item.Properties()).stacksTo(16)));
   public static final Item EMERALD = registerItem("emerald", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item ITEM_FRAME = registerItem("item_frame", new ItemFrameItem((new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));
   public static final Item FLOWER_POT = registerBlock(Blocks.FLOWER_POT, ItemGroup.TAB_NATURAL);
   public static final Item MAP = registerItem("map", new MapItem((new Item.Properties()).tab(ItemGroup.TAB_MISC)));
   public static final Item SKELETON_SKULL = registerBlock(new WallOrFloorItem(Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL).rarity(Rarity.UNCOMMON)));
   public static final Item WITHER_SKELETON_SKULL = registerBlock(new WallOrFloorItem(Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL).rarity(Rarity.UNCOMMON)));
   public static final Item WITHER_SKELETON_RIBCAGE = registerItem("wither_skeleton_ribcage", new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
   public static final Item PLAYER_HEAD = registerBlock(new SkullItem(Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL).rarity(Rarity.UNCOMMON)));
   public static final Item ZOMBIE_HEAD = registerBlock(new WallOrFloorItem(Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL).rarity(Rarity.UNCOMMON)));
   public static final Item CREEPER_HEAD = registerBlock(new WallOrFloorItem(Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL).rarity(Rarity.UNCOMMON)));
   public static final Item PIGLIN_HEAD = registerBlock(new WallOrFloorItem(Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL).rarity(Rarity.UNCOMMON)));
   public static final Item DRAGON_HEAD = registerBlock(new WallOrFloorItem(Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL).rarity(Rarity.UNCOMMON)));
   public static final Item CARROT_ON_A_STICK = registerItem("carrot_on_a_stick", new OnAStickItem<>((new Item.Properties()).durability(25).tab(ItemGroup.TAB_REDSTONE), EntityType.PIG, 7));
   public static final Item WARPED_FUNGUS_ON_A_STICK = registerItem("warped_fungus_on_a_stick", new OnAStickItem<>((new Item.Properties()).durability(100).tab(ItemGroup.TAB_REDSTONE), EntityType.STRIDER, 1));
   public static final Item NETHER_STAR = registerItem("nether_star", new SimpleFoiledItem((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS).rarity(Rarity.UNCOMMON)));
   public static final Item FIREWORK_STAR = registerItem("firework_star", new FireworkStarItem((new Item.Properties()).tab(ItemGroup.TAB_MISC)));
   public static final Item ENCHANTED_BOOK = registerItem("enchanted_book", new EnchantedBookItem((new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON)));
   public static final Item NETHER_BRICK = registerItem("nether_brick", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item QUARTZ = registerItem("quartz", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item TNT_MINECART = registerItem("tnt_minecart", new MinecartItem(AbstractMinecartEntity.Type.TNT, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_REDSTONE)));
   public static final Item HOPPER_MINECART = registerItem("hopper_minecart", new MinecartItem(AbstractMinecartEntity.Type.HOPPER, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_REDSTONE)));
   public static final Item PRISMARINE_SHARD = registerItem("prismarine_shard", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item PRISMARINE_CRYSTALS = registerItem("prismarine_crystals", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));

   public static final Item RABBIT_FOOT = registerItem("rabbit_foot", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item RABBIT_HIDE = registerItem("rabbit_hide", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item ARMOR_STAND = registerItem("armor_stand", new ArmorStandItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_NATURAL)));
   public static final Item LEAD = registerItem("lead", new LeadItem((new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item NAME_TAG = registerItem("name_tag", new NameTagItem((new Item.Properties()).tab(ItemGroup.TAB_TOOLS)));
   public static final Item COMMAND_BLOCK_MINECART = registerItem("command_block_minecart", new MinecartItem(AbstractMinecartEntity.Type.COMMAND_BLOCK, (new Item.Properties()).stacksTo(1)));


   public static final Item POPPED_CHORUS_FRUIT = registerItem("popped_chorus_fruit", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));

   public static final Item BEETROOT_SEEDS = registerItem("beetroot_seeds", new BlockNamedItem(Blocks.BEETROOTS, (new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item DRAGON_BREATH = registerItem("dragon_breath", new Item((new Item.Properties()).craftRemainder(GLASS_BOTTLE).tab(ItemGroup.TAB_MATERIALS).rarity(Rarity.UNCOMMON)));



   public static final Item ELYTRA = registerItem("elytra", new ElytraItem((new Item.Properties()).durability(432).tab(ItemGroup.TAB_REDSTONE).rarity(Rarity.UNCOMMON)));

   // capes
   public static final Item BLACK_CAPE = registerItem("black_cape", new AbstractCapeItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1), "black_cape", "black_cape"));
   public static final Item MOJANG_CAPE = registerItem("mojang_cape", new AbstractCapeItem(new Item.Properties().rarity(Rarity.RED).stacksTo(1), "mojang_cape", "mojang_cape"));










   public static final Item SPRUCE_BOAT = registerItem("spruce_boat", new BoatItem(BoatEntity.Type.SPRUCE, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_REDSTONE)));
   public static final Item BIRCH_BOAT = registerItem("birch_boat", new BoatItem(BoatEntity.Type.BIRCH, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_REDSTONE)));
   public static final Item JUNGLE_BOAT = registerItem("jungle_boat", new BoatItem(BoatEntity.Type.JUNGLE, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_REDSTONE)));
   public static final Item ACACIA_BOAT = registerItem("acacia_boat", new BoatItem(BoatEntity.Type.ACACIA, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_REDSTONE)));
   public static final Item DARK_OAK_BOAT = registerItem("dark_oak_boat", new BoatItem(BoatEntity.Type.DARK_OAK, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_REDSTONE)));
   public static final Item SHULKER_SHELL = registerItem("shulker_shell", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item IRON_NUGGET = registerItem("iron_nugget", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item KNOWLEDGE_BOOK = registerItem("knowledge_book", new KnowledgeBookItem((new Item.Properties()).stacksTo(1)));
   public static final Item DEBUG_STICK = registerItem("debug_stick", new DebugStickItem((new Item.Properties()).stacksTo(1)));
   public static final Item PHANTOM_MEMBRANE = registerItem("phantom_membrane", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item NAUTILUS_SHELL = registerItem("nautilus_shell", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item HEART_OF_THE_SEA = registerItem("heart_of_the_sea", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS).rarity(Rarity.UNCOMMON)));





   public static final Item SUSPICIOUS_STEW = registerItem("suspicious_stew", new SuspiciousStewItem((new Item.Properties()).stacksTo(1).food(Foods.SUSPICIOUS_STEW)));
   public static final Item LOOM = registerBlock(Blocks.LOOM, ItemGroup.TAB_NATURAL);
   public static final Item FLOWER_BANNER_PATTERN = registerItem("flower_banner_pattern", new BannerPatternItem(BannerPattern.FLOWER, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC)));
   public static final Item CREEPER_BANNER_PATTERN = registerItem("creeper_banner_pattern", new BannerPatternItem(BannerPattern.CREEPER, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.UNCOMMON)));
   public static final Item SKULL_BANNER_PATTERN = registerItem("skull_banner_pattern", new BannerPatternItem(BannerPattern.SKULL, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.UNCOMMON)));
   public static final Item MOJANG_BANNER_PATTERN = registerItem("mojang_banner_pattern", new BannerPatternItem(BannerPattern.MOJANG, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.EPIC)));
   public static final Item GLOBE_BANNER_PATTER = registerItem("globe_banner_pattern", new BannerPatternItem(BannerPattern.GLOBE, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC)));
   public static final Item PIGLIN_BANNER_PATTERN = registerItem("piglin_banner_pattern", new BannerPatternItem(BannerPattern.PIGLIN, (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC)));
   public static final Item COMPOSTER = registerBlock(Blocks.COMPOSTER, ItemGroup.TAB_NATURAL);
   public static final Item BARREL = registerBlock(Blocks.BARREL, ItemGroup.TAB_NATURAL);
   public static final Item SMOKER = registerBlock(Blocks.SMOKER, ItemGroup.TAB_NATURAL);
   public static final Item BLAST_FURNACE = registerBlock(Blocks.BLAST_FURNACE, ItemGroup.TAB_NATURAL);
   public static final Item CARTOGRAPHY_TABLE = registerBlock(Blocks.CARTOGRAPHY_TABLE, ItemGroup.TAB_NATURAL);
   public static final Item FLETCHING_TABLE = registerBlock(Blocks.FLETCHING_TABLE, ItemGroup.TAB_NATURAL);
   public static final Item GRINDSTONE = registerBlock(Blocks.GRINDSTONE, ItemGroup.TAB_NATURAL);
   public static final Item LECTERN = registerBlock(Blocks.LECTERN, ItemGroup.TAB_FUNCTIONAL);
   public static final Item SMITHING_TABLE = registerBlock(Blocks.SMITHING_TABLE, ItemGroup.TAB_NATURAL);
   public static final Item STONECUTTER = registerBlock(Blocks.STONECUTTER, ItemGroup.TAB_NATURAL);
   public static final Item WOODCUTTER = registerBlock(Blocks.WOODCUTTER, ItemGroup.TAB_NATURAL);

   public static final Item WAX_MELTER = registerBlock(Blocks.WAX_MELTER, ItemGroup.TAB_NATURAL);

   public static final Item BELL = registerBlock(Blocks.BELL, ItemGroup.TAB_NATURAL);
   public static final Item LANTERN = registerBlock(Blocks.LANTERN, ItemGroup.TAB_NATURAL);
   public static final Item SOUL_LANTERN = registerBlock(Blocks.SOUL_LANTERN, ItemGroup.TAB_NATURAL);
   public static final Item CAMPFIRE = registerBlock(Blocks.CAMPFIRE, ItemGroup.TAB_NATURAL);
   public static final Item SOUL_CAMPFIRE = registerBlock(Blocks.SOUL_CAMPFIRE, ItemGroup.TAB_NATURAL);
   public static final Item SHROOMLIGHT = registerBlock(Blocks.SHROOMLIGHT, ItemGroup.TAB_NATURAL);
   public static final Item HONEYCOMB = registerItem("honeycomb", new HoneyCombItem((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item BEE_POLLEN = registerItem("pollen", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));

   public static final Item BEE_NEST = registerBlock(Blocks.BEE_NEST, ItemGroup.TAB_NATURAL);
   public static final Item BEEHIVE = registerBlock(Blocks.BEEHIVE, ItemGroup.TAB_NATURAL);






   public static final Item HONEY_BLOCK = registerBlock(Blocks.HONEY_BLOCK, ItemGroup.TAB_NATURAL);

   public static final Item HONEYCOMB_BLOCK = registerBlock(Blocks.HONEYCOMB_BLOCK, ItemGroup.TAB_NATURAL);
   public static final Item LODESTONE = registerBlock(Blocks.LODESTONE, ItemGroup.TAB_NATURAL);
   public static final Item NETHERITE_BLOCK = registerBlock(new BlockItem(Blocks.NETHERITE_BLOCK, (new Item.Properties()).tab(ItemGroup.TAB_BUILDING_BLOCKS).fireResistant()));
   public static final Item ANCIENT_DEBRIS = registerBlock(new BlockItem(Blocks.ANCIENT_DEBRIS, (new Item.Properties()).tab(ItemGroup.TAB_BUILDING_BLOCKS).fireResistant()));
   public static final Item TARGET = registerBlock(Blocks.TARGET, ItemGroup.TAB_FUNCTIONAL);
   public static final Item CRYING_OBSIDIAN = registerBlock(Blocks.CRYING_OBSIDIAN, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BLACKSTONE = registerBlock(Blocks.BLACKSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BLACKSTONE_SLAB = registerBlock(Blocks.BLACKSTONE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item BLACKSTONE_STAIRS = registerBlock(Blocks.BLACKSTONE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item GILDED_BLACKSTONE = registerBlock(Blocks.GILDED_BLACKSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_BLACKSTONE = registerBlock(Blocks.POLISHED_BLACKSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_BLACKSTONE_SLAB = registerBlock(Blocks.POLISHED_BLACKSTONE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_BLACKSTONE_STAIRS = registerBlock(Blocks.POLISHED_BLACKSTONE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CHISELED_POLISHED_BLACKSTONE = registerBlock(Blocks.CHISELED_POLISHED_BLACKSTONE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_BLACKSTONE_BRICKS = registerBlock(Blocks.POLISHED_BLACKSTONE_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_BLACKSTONE_BRICK_SLAB = registerBlock(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_BLACKSTONE_BRICK_STAIRS = registerBlock(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CRACKED_POLISHED_BLACKSTONE_BRICKS = registerBlock(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item RESPAWN_ANCHOR = registerBlock(Blocks.RESPAWN_ANCHOR, ItemGroup.TAB_NATURAL);

   public static final Chem RadAway = (Chem) registerItem("radaway", new RadAwayItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COMBAT)));
   public static final Chem RadX = (Chem) registerItem("radx", new RadXItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_COMBAT)));
   public static final Chem MedX = (Chem) registerItem("medx", new MedXItem((new Item.Properties()).stacksTo(8).tab(ItemGroup.TAB_COMBAT)));
   public static final Chem Mentats = (Chem) registerItem("mentats", new MentatsItem((new Item.Properties()).stacksTo(4).tab(ItemGroup.TAB_COMBAT)));





   public static final Item MUSIC_DISC_13 = registerItem("music_disc_13",
           new MusicDiscItem(1, SoundEvents.MUSIC_DISC_13,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(100),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(5.0D).setSpeed(4).setDistanceToComeBack(20).setReducWhenHittingMob(0.85).setFireResistant(false).build("disc_13")));

   public static final Item MUSIC_DISC_CAT = registerItem("music_disc_cat",
           new MusicDiscItem(2, SoundEvents.MUSIC_DISC_CAT,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(150),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(3.0D).setSpeed(8).setDistanceToComeBack(25).setReducWhenHittingMob(0.90).setFireResistant(false).build("disc_cat")));

   public static final Item MUSIC_DISC_BLOCKS = registerItem("music_disc_blocks",
           new MusicDiscItem(3, SoundEvents.MUSIC_DISC_BLOCKS,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(200),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(6.0D).setSpeed(6).setDistanceToComeBack(30).setReducWhenHittingMob(0.80).setFireResistant(false).build("disc_blocks")));

   public static final Item MUSIC_DISC_CHIRP = registerItem("music_disc_chirp",
           new MusicDiscItem(4, SoundEvents.MUSIC_DISC_CHIRP,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(160),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(4.0D).setSpeed(7).setDistanceToComeBack(22).setReducWhenHittingMob(0.88).setFireResistant(false).build("disc_chirp")));

   public static final Item MUSIC_DISC_FAR = registerItem("music_disc_far",
           new MusicDiscItem(5, SoundEvents.MUSIC_DISC_FAR,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(250),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(7.0D).setSpeed(5).setDistanceToComeBack(35).setReducWhenHittingMob(0.75).setFireResistant(true).build("disc_far")));

   public static final Item MUSIC_DISC_MALL = registerItem("music_disc_mall",
           new MusicDiscItem(6, SoundEvents.MUSIC_DISC_MALL,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(180),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(5.5D).setSpeed(6).setDistanceToComeBack(28).setReducWhenHittingMob(0.82).setFireResistant(false).build("disc_mall")));

   public static final Item MUSIC_DISC_MELLOHI = registerItem("music_disc_mellohi",
           new MusicDiscItem(7, SoundEvents.MUSIC_DISC_MELLOHI,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(170),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(6.0D).setSpeed(5).setDistanceToComeBack(25).setReducWhenHittingMob(0.88).setFireResistant(false).build("disc_mellohi")));

   public static final Item MUSIC_DISC_STAL = registerItem("music_disc_stal",
           new MusicDiscItem(8, SoundEvents.MUSIC_DISC_STAL,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(220),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(8.0D).setSpeed(5).setDistanceToComeBack(32).setReducWhenHittingMob(0.78).setFireResistant(true).build("disc_stal")));

   public static final Item MUSIC_DISC_STRAD = registerItem("music_disc_strad",
           new MusicDiscItem(9, SoundEvents.MUSIC_DISC_STRAD,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(210),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(6.5D).setSpeed(7).setDistanceToComeBack(30).setReducWhenHittingMob(0.80).setFireResistant(false).build("disc_strad")));

   public static final Item MUSIC_DISC_WARD = registerItem("music_disc_ward",
           new MusicDiscItem(10, SoundEvents.MUSIC_DISC_WARD,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(300),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(9.0D).setSpeed(4).setDistanceToComeBack(40).setReducWhenHittingMob(0.70).setFireResistant(true).build("disc_ward")));

   public static final Item MUSIC_DISC_11 = registerItem("music_disc_11",
           new MusicDiscItem(11, SoundEvents.MUSIC_DISC_11,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(120),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(4.5D).setSpeed(3).setDistanceToComeBack(15).setReducWhenHittingMob(0.85).setFireResistant(false).build("disc_11")));

   public static final Item MUSIC_DISC_WAIT = registerItem("music_disc_wait",
           new MusicDiscItem(12, SoundEvents.MUSIC_DISC_WAIT,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(200),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(7.5D).setSpeed(6).setDistanceToComeBack(30).setReducWhenHittingMob(0.82).setFireResistant(true).build("disc_wait")));

   public static final Item MUSIC_DISC_PIGSTEP = registerItem("music_disc_pigstep",
           new MusicDiscItem(13, SoundEvents.MUSIC_DISC_PIGSTEP,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(280),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(10.0D).setSpeed(8).setDistanceToComeBack(35).setReducWhenHittingMob(0.75).setFireResistant(true).setOnHitEntityBehavior((frisbee, result) -> {
                      if (!result.getEntity().fireImmune() && result.getEntity() != frisbee.getOwner()) {
                         result.getEntity().setSecondsOnFire(5);
                      }
                   }).build("disc_pigstep")));


   public static final Item MUSIC_DISC_RELIC = registerItem("music_disc_relic",
           new MusicDiscItem(14, SoundEvents.MUSIC_DISC_RELIC,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.RARE).durability(344),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(12.0D).setSpeed(11).setDistanceToComeBack(56).setReducWhenHittingMob(0.85).setFireResistant(true)
                           .build("disc_relic")));


   public static final Item MUSIC_DISC_PEARTO = registerItem("music_disc_pearto",
           new MusicDiscItem(14, SoundEvents.PEARTO,
                   (new Item.Properties()).stacksTo(1).tab(ItemGroup.TAB_MISC).rarity(Rarity.LEGENDARY).durability(10000),
                   new FrisbeeData.FrisbeeDataBuilder().setBaseDamage(99D).setSpeed(20).setDistanceToComeBack(60).bypassArmour().phase(true).windResistant().setReducWhenHittingMob(0.995).setFireResistant(true).build("disc_pearto")));


   public static final FrisbeeItem WOODEN_FRISBEE = (FrisbeeItem) registerItem("wooden_frisbee",
           new FrisbeeItem(
                   new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(59),
                   new FrisbeeData.FrisbeeDataBuilder()
                           .setBaseDamage(3D)
                           .setSpeed(4)
                           .setDistanceToComeBack(10)
                           .setReducWhenHittingMob(0.90D)
                           .setFireResistant(false)
                           .build("wooden_frisbee")
           )
   );

   public static final Item IRON_FRISBEE = registerItem("iron_frisbee",
           new FrisbeeItem(
                   new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(250),
                   new FrisbeeData.FrisbeeDataBuilder()
                           .setBaseDamage(5D)
                           .setSpeed(6)
                           .setDistanceToComeBack(15)
                           .setReducWhenHittingMob(0.92D)
                           .setFireResistant(false)
                           .build("iron_frisbee")
           )
   );

   public static final Item GOLD_FRISBEE = registerItem("gold_frisbee",
           new FrisbeeItem(
                   new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(32),
                   new FrisbeeData.FrisbeeDataBuilder()
                           .setBaseDamage(4D)
                           .setSpeed(12)
                           .setDistanceToComeBack(12)
                           .setReducWhenHittingMob(0.88D)
                           .setFireResistant(false)
                           .build("gold_frisbee")
           )
   );

   public static final Item DIAMOND_FRISBEE = registerItem("diamond_frisbee",
           new FrisbeeItem(
                   new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(1561),
                   new FrisbeeData.FrisbeeDataBuilder()
                           .setBaseDamage(7D)
                           .setSpeed(8)
                           .setDistanceToComeBack(20)
                           .setReducWhenHittingMob(0.95D)
                           .setFireResistant(true)
                           .build("diamond_frisbee")
           )
   );

   public static final Item NETHERITE_FRISBEE = registerItem("netherite_frisbee",
           new FrisbeeItem(
                   new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(2031).fireResistant(),
                   new FrisbeeData.FrisbeeDataBuilder()
                           .setBaseDamage(8D)
                           .setSpeed(10)
                           .setDistanceToComeBack(25)
                           .setReducWhenHittingMob(0.97D)
                           .setFireResistant(true)
                           .build("netherite_frisbee")
           )
   );

   public static final Item PIZZA_FRISBEE = registerItem("pizza_frisbee",
           new FrisbeeItem(
                   new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(150),
                   new FrisbeeData.FrisbeeDataBuilder()
                           .setBaseDamage(4D)
                           .setSpeed(6)
                           .setDistanceToComeBack(20)
                           .setReducWhenHittingMob(0.80D)
                           .setFireResistant(false)
                           .setOnHitEntityBehavior((frisbee, entityRayTraceResult) -> {
                              Entity target = entityRayTraceResult.getEntity();
                              if (target instanceof LivingEntity && target != frisbee.getOwner()) {
                                 ((LivingEntity) target).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20, 4));
                              }
                           })
                           .setOnHitBlockBehavior((frisbee, blockRayTraceResult) -> {
                              if (Math.random() < 0.3) {
                                 World world = frisbee.level;
                                 BlockPos pos = blockRayTraceResult.getBlockPos();
                                 ItemStack drop = Math.random() < 0.5 ? new ItemStack(Items.BREAD) : new ItemStack(Items.COOKED_CHICKEN);
                                 ItemEntity foodDrop = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, drop);
                                 world.addFreshEntity(foodDrop);
                              }
                           })
                           .setOnThrowBehavior((frisbee, player) -> {
                              frisbee.level.addParticle(ParticleTypes.ITEM_SNOWBALL, frisbee.getX(), frisbee.getY(), frisbee.getZ(), 0, 0, 0);
                              frisbee.level.getEntitiesOfClass(LivingEntity.class, frisbee.getBoundingBox().inflate(1.0D), entity -> entity != frisbee.getOwner()).forEach(entity -> {
                                 entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20, 1));
                              });
                           })
                           .setOnReturnBehavior((frisbee, player) -> {
                              if (Math.random() < 0.1) {
                                 player.addEffect(new EffectInstance(Effects.SATURATION, 600, 0));
                              }
                           })
                           .build("pizza_frisbee")
           )
   );

   public static final Item ENDER_FRISBEE = registerItem("ender_frisbee",
           new FrisbeeItem(
                   new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(150),
                   new FrisbeeData.FrisbeeDataBuilder()
                           .setBaseDamage(4D)
                           .setSpeed(12)
                           .setDistanceToComeBack(25)
                           .setReducWhenHittingMob(0.85D)
                           .setFireResistant(false)
                           .setOnHitBlockBehavior((frisbeeEntity, blockRayTraceResult) -> {
                              LivingEntity frisbee = (LivingEntity) frisbeeEntity.getOwner();
                              if (frisbee != null && !EnchantmentHelper.has(frisbeeEntity.frisbeeItemStack, Enchantments.SPECTRAL_THROW)) {
                                 for(int i = 0; i < 32; ++i) {
                                    frisbee.level.addParticle(ParticleTypes.PORTAL, frisbee.getX(), frisbee.getY() + frisbee.level.random.nextDouble() * 2.0D, frisbee.getZ(), frisbee.level.random.nextGaussian(), 0.0D, frisbee.level.random.nextGaussian());
                                 }

                                 frisbeeEntity.teleportTo(frisbee.getX(), frisbee.getY() + 1, frisbee.getZ());
                                 frisbeeEntity.dropItem();
                                 frisbeeEntity.hasDropped = true;
                                 frisbeeEntity.fallDistance = 0.0F;
                              }
                           })
                           .build("ender_frisbee")
           )
   );

   public static final Item OBSIDIAN_FRISBEE = registerItem("obsidian_frisbee",
           new FrisbeeItem(
                   new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(700),
                   new FrisbeeData.FrisbeeDataBuilder()
                           .setBaseDamage(8D)
                           .setSpeed(4)
                           .setDistanceToComeBack(20)
                           .setReducWhenHittingMob(0.9D)
                           .fireResistant()
                           .bypassArmour()
                           .setCooldown(25)
                           .setOnHitBlockBehavior((frisbeeEntity, blockRayTraceResult) -> {
                              frisbeeEntity.breakSoft(3, blockRayTraceResult, true);
                           })
                           .build("obsidian_frisbee")
           )
   );

   public static final Item SPONGE_FRISBEE = registerItem("sponge_frisbee",
           new FrisbeeItem(
                   new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(353),
                   new FrisbeeData.FrisbeeDataBuilder()
                           .setBaseDamage(3D)
                           .setSpeed(2)
                           .setDistanceToComeBack(14)
                           .setReducWhenHittingMob(0.6D)
                           .setCooldown(8)
                           .setFlyingBehavior((frisbee) -> {
                              World world = frisbee.level;
                              BlockPos frisbeePos = frisbee.blockPosition();

                              frisbee.absorbWater(frisbee, world, frisbeePos);
                           })
                           .build("sponge_frisbee")
           ));

   public static final Item PHANTOM_FRISBEE = registerItem("phantom_frisbee",
           new FrisbeeItem(
                   new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(220),
                   new FrisbeeData.FrisbeeDataBuilder()
                           .setBaseDamage(5.5D)
                           .setSpeed(10)
                           .setDistanceToComeBack(40)
                           .setReducWhenHittingMob(0.85D)
                           .setCooldown(6)
                           .phase(true)
                           .setOnHitEntityBehavior((frisbee, entityResult) -> {
                              if (entityResult.getEntity() != frisbee.getOwner() && entityResult.getEntity() instanceof LivingEntity) {
                                 ((LivingEntity) entityResult.getEntity()).addEffect(new EffectInstance(Effects.LEVITATION, 5 * 20, 0));
                              }
                           })
                           .setFlyingBehavior((frisbee) -> {
                              if (!frisbee.level.isClientSide) {
                                 float f = MathHelper.cos((float)(frisbee.getId() * 3 + frisbee.tickCount) * 0.13F + (float)Math.PI);
                                 int i = 1;
                                 float f2 = MathHelper.cos(frisbee.yRot * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
                                 float f3 = MathHelper.sin(frisbee.yRot * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
                                 float f4 = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
                                 frisbee.level.addParticle(ParticleTypes.MYCELIUM, frisbee.getX() + (double)f2, frisbee.getY() + (double)f4, frisbee.getZ() + (double)f3, 0.0D, 0.0D, 0.0D);
                                 frisbee.level.addParticle(ParticleTypes.MYCELIUM, frisbee.getX() - (double)f2, frisbee.getY() + (double)f4, frisbee.getZ() - (double)f3, 0.0D, 0.0D, 0.0D);
                              }
                           })
                           .build("phantom_frisbee")
           )
   );

   public static final Item FRISBEE /* replace frisbee with custom name here */ = registerItem("frisbee", // put name here too
           new FrisbeeItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(1/* put durability here*/),
           new FrisbeeData.FrisbeeDataBuilder()
                   .setBaseDamage(0) // double
                   .setSpeed(0) // int (0-15, over 8 is very fast, you could also go higher than 15 but max 15 is recommended)
                   .setDistanceToComeBack(0) // int (blocks to travel before starting to come back to player)
                   .setReducWhenHittingMob(0) // etc 0.7 for 30% reduction in speed when hitting a mob, 0.1 for 90% speed reduction
                   .setCooldown(0) // int
                   .setAvailableDimensions(Arrays.asList(World.OVERWORLD,World.NETHER,World.END)) // don't include this part if you want the frisbee to be able to be used in every dimension, or you can limit it to a certain one or 2
                   .fireResistant() // use this if you want the item to be fire-resistant
                   .windResistant() // use this if you want the frisbee to be wind-resistant and not affected by wind.
                   .bypassArmour() // use this if you want it to ignore armour
                   .phase(false) // boolean (if it can go through blocks)
                   .setFlyingBehavior((frisbee) -> {
                        // tick while flying
                   })
                   .setOnHitBlockBehavior((frisbee, blockHitResult) -> {
                        // when it hits a block
                   })
                   .setOnHitEntityBehavior((frisbee, entityHitResult) -> {
                        // when it hits an entity
                   })
                   .setOnThrowBehavior((frisbee, player) -> {
                        // logic as soon as the frisbee is thrown
                   })
                   .setOnReturnBehavior((frisbee, player) -> {
                        // logic when frisbee is added back to player's inventory
                   })
                   .DEFAULT()
           )
   );


   public static final Item AETHER_FRISBEE = registerItem("aether_frisbee",
           new FrisbeeItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(120),
                   new FrisbeeData.FrisbeeDataBuilder()
                           .setBaseDamage(7.0)
                           .setSpeed(12)
                           .setDistanceToComeBack(40)
                           .setReducWhenHittingMob(0.8)
                           .setCooldown(10)
                           .fireResistant()
                           .windResistant()
                           .bypassArmour()
                           .phase(false)
                           .setFlyingBehavior((frisbee) -> {
                              World world = frisbee.level;
                              if (!world.isClientSide) {
                                 for (int i = 0; i < 5; i++) {
                                    world.addParticle(ParticleTypes.DRAGON_BREATH, frisbee.getX(), frisbee.getY(), frisbee.getZ(), 0, 0, 0);
                                 }
                              }
                           })
                           .setOnHitEntityBehavior((frisbee, entityHitResult) -> {
                              World world = frisbee.level;
                              if (!world.isClientSide) {
                                 for (Entity entity : world.getEntities(frisbee, frisbee.getBoundingBox().inflate(2), e -> e != frisbee.getOwner())) {
                                    if (entity instanceof LivingEntity) {
                                       ((LivingEntity)entity).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 60, 0));
                                       ((LivingEntity)entity).knockback(0.5F, frisbee.getX() - entity.getX(), frisbee.getZ() - entity.getZ());
                                    }
                                 }
                              }
                           })
                           .setOnThrowBehavior((frisbee, player) -> {
                              World world = frisbee.level;
                              if (!world.isClientSide) {
                                 world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                              }
                           })
                           .setOnReturnBehavior((frisbee, player) -> {
                              if (player.getRandom().nextFloat() < 0.1) {
                                 player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 100, 1));
                                 World world = frisbee.level;
                                 if (!world.isClientSide) {
                                    world.addParticle(ParticleTypes.PORTAL, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
                                 }
                              }
                           })
                           .build("aether_frisbee")
           )
   );


   public static final Item GHOSTLY_FRISBEE = registerItem("ghostly_frisbee", // name
           new FrisbeeItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(250), // durability
                   new FrisbeeData.FrisbeeDataBuilder()
                           .setBaseDamage(8.5)
                           .setSpeed(12)
                           .setDistanceToComeBack(30)
                           .setReducWhenHittingMob(0.5)
                           .setCooldown(15)
                           .fireResistant()
                           .phase(true)
                           .setAvailableDimensions(Arrays.asList(World.NETHER, World.END))
                           .setFlyingBehavior((frisbee) -> {
                              frisbee.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, frisbee.getX(), frisbee.getY(), frisbee.getZ(), 0, 0, 0);
                           })
                           .setOnHitBlockBehavior((frisbee, blockHitResult) -> {
                              frisbee.setDeltaMovement(frisbee.getDeltaMovement().multiply(1.05, 1.05, 1.05));
                           })
                           .setOnHitEntityBehavior((frisbee, entityHitResult) -> {
                              if (entityHitResult.getEntity() instanceof LivingEntity) {
                                 ((LivingEntity) entityHitResult.getEntity()).addEffect(new EffectInstance(Effects.GLOWING, 200, 0));
                              }
                           })
                           .setOnThrowBehavior((frisbee, player) -> {
                              player.level.playSound(null, player.blockPosition(), SoundEvents.WITHER_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                           })
                           .setOnReturnBehavior((frisbee, player) -> {
                              player.level.playSound(null, player.blockPosition(), SoundEvents.GHAST_SHOOT, SoundCategory.PLAYERS, 0.5F, 1.2F);
                           })
                           .build("ghostly_frisbee")
           )
   );

   public static final Item LIGHTNING_ROD = registerBlock(Blocks.LIGHTNING_ROD, ItemGroup.TAB_NATURAL);
   public static final Item COPPER_BULB = Items.registerBlock(Blocks.COPPER_BULB, ItemGroup.TAB_NATURAL);
   public static final Item EXPOSED_COPPER_BULB = Items.registerBlock(Blocks.EXPOSED_COPPER_BULB, ItemGroup.TAB_NATURAL);
   public static final Item WEATHERED_COPPER_BULB = Items.registerBlock(Blocks.WEATHERED_COPPER_BULB, ItemGroup.TAB_NATURAL);
   public static final Item OXIDIZED_COPPER_BULB = Items.registerBlock(Blocks.OXIDIZED_COPPER_BULB, ItemGroup.TAB_NATURAL);
   public static final Item COPPER_BLOCK = Items.registerBlock(Blocks.COPPER_BLOCK, ItemGroup.TAB_NATURAL);

   public static final Item EXPOSED_COPPER = Items.registerBlock(Blocks.EXPOSED_COPPER, ItemGroup.TAB_NATURAL);

   public static final Item WEATHERED_COPPER = Items.registerBlock(Blocks.WEATHERED_COPPER, ItemGroup.TAB_NATURAL);

   public static final Item OXIDIZED_COPPER = Items.registerBlock(Blocks.OXIDIZED_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item CUT_COPPER_BLOCK = Items.registerBlock(Blocks.CUT_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item CUT_EXPOSED_COPPER = Items.registerBlock(Blocks.EXPOSED_CUT_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item CUT_WEATHERED_COPPER = Items.registerBlock(Blocks.WEATHERED_CUT_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item CUT_OXIDIZED_COPPER = Items.registerBlock(Blocks.OXIDIZED_CUT_COPPER, ItemGroup.TAB_NATURAL);

   // Copper Grates
   public static final Item COPPER_GRATE = Items.registerBlock(Blocks.COPPER_GRATE, ItemGroup.TAB_NATURAL);
   public static final Item EXPOSED_COPPER_GRATE = Items.registerBlock(Blocks.EXPOSED_COPPER_GRATE, ItemGroup.TAB_NATURAL);
   public static final Item WEATHERED_COPPER_GRATE = Items.registerBlock(Blocks.WEATHERED_COPPER_GRATE, ItemGroup.TAB_NATURAL);
   public static final Item OXIDIZED_COPPER_GRATE = Items.registerBlock(Blocks.OXIDIZED_COPPER_GRATE, ItemGroup.TAB_NATURAL);


   // Waxed Copper Blocks

   // Waxed Cut Copper Blocks


   // Slabs
   public static final Item CUT_COPPER_SLAB = Items.registerBlock(Blocks.CUT_COPPER_SLAB, ItemGroup.TAB_NATURAL);
   public static final Item EXPOSED_CUT_COPPER_SLAB = Items.registerBlock(Blocks.EXPOSED_CUT_COPPER_SLAB, ItemGroup.TAB_NATURAL);
   public static final Item WEATHERED_CUT_COPPER_SLAB = Items.registerBlock(Blocks.WEATHERED_CUT_COPPER_SLAB, ItemGroup.TAB_NATURAL);
   public static final Item OXIDIZED_CUT_COPPER_SLAB = Items.registerBlock(Blocks.OXIDIZED_CUT_COPPER_SLAB, ItemGroup.TAB_NATURAL);
   public static final Item CUT_COPPER_STAIRS = Items.registerBlock(Blocks.CUT_COPPER_STAIRS, ItemGroup.TAB_NATURAL);
   public static final Item EXPOSED_CUT_COPPER_STAIRS = Items.registerBlock(Blocks.EXPOSED_CUT_COPPER_STAIRS, ItemGroup.TAB_NATURAL);
   public static final Item WEATHERED_CUT_COPPER_STAIRS = Items.registerBlock(Blocks.WEATHERED_CUT_COPPER_STAIRS, ItemGroup.TAB_NATURAL);
   public static final Item OXIDIZED_CUT_COPPER_STAIRS = Items.registerBlock(Blocks.OXIDIZED_CUT_COPPER_STAIRS, ItemGroup.TAB_NATURAL);
   public static final Item CHISELED_COPPER = Items.registerBlock(Blocks.CHISELED_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item EXPOSED_CHISELED_COPPER = Items.registerBlock(Blocks.EXPOSED_CHISELED_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item WEATHERED_CHISELED_COPPER = Items.registerBlock(Blocks.WEATHERED_CHISELED_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item OXIDIZED_CHISELED_COPPER = Items.registerBlock(Blocks.OXIDIZED_CHISELED_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item COPPER_DOOR = registerBlock(new TallBlockItem(Blocks.COPPER_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item EXPOSED_COPPER_DOOR = registerBlock(new TallBlockItem(Blocks.EXPOSED_COPPER_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item WEATHERED_COPPER_DOOR = registerBlock(new TallBlockItem(Blocks.WEATHERED_COPPER_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item OXIDIZED_COPPER_DOOR = registerBlock(new TallBlockItem(Blocks.OXIDIZED_COPPER_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item COPPER_TRAPDOOR = Items.registerBlock(Blocks.COPPER_TRAPDOOR, ItemGroup.TAB_NATURAL);
   public static final Item EXPOSED_COPPER_TRAPDOOR = Items.registerBlock(Blocks.EXPOSED_COPPER_TRAPDOOR, ItemGroup.TAB_NATURAL);
   public static final Item WEATHERED_COPPER_TRAPDOOR = Items.registerBlock(Blocks.WEATHERED_COPPER_TRAPDOOR, ItemGroup.TAB_NATURAL);
   public static final Item OXIDIZED_COPPER_TRAPDOOR = Items.registerBlock(Blocks.OXIDIZED_COPPER_TRAPDOOR, ItemGroup.TAB_NATURAL);
   public static final Item COPPER_BUTTON = registerBlock(Blocks.COPPER_BUTTON, ItemGroup.TAB_NATURAL);
   public static final Item EXPOSED_COPPER_BUTTON = registerBlock(Blocks.EXPOSED_COPPER_BUTTON, ItemGroup.TAB_NATURAL);
   public static final Item WEATHERED_COPPER_BUTTON = registerBlock(Blocks.WEATHERED_COPPER_BUTTON, ItemGroup.TAB_NATURAL);
   public static final Item OXIDIZED_COPPER_BUTTON = registerBlock(Blocks.OXIDIZED_COPPER_BUTTON, ItemGroup.TAB_NATURAL);



   public static final Item WAXED_COPPER_BULB = Items.registerBlock(Blocks.WAXED_COPPER_BULB, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_EXPOSED_COPPER_BULB = Items.registerBlock(Blocks.WAXED_EXPOSED_COPPER_BULB, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_WEATHERED_COPPER_BULB = Items.registerBlock(Blocks.WAXED_WEATHERED_COPPER_BULB, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_OXIDIZED_COPPER_BULB = Items.registerBlock(Blocks.WAXED_OXIDIZED_COPPER_BULB, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_COPPER_BLOCK = Items.registerBlock(Blocks.WAXED_COPPER_BLOCK, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_WEATHERED_COPPER = Items.registerBlock(Blocks.WAXED_WEATHERED_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_EXPOSED_COPPER = Items.registerBlock(Blocks.WAXED_EXPOSED_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_OXIDIZED_COPPER = Items.registerBlock(Blocks.WAXED_OXIDIZED_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_CUT_COPPER = Items.registerBlock(Blocks.WAXED_CUT_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_EXPOSED_CUT_COPPER = Items.registerBlock(Blocks.WAXED_EXPOSED_CUT_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_WEATHERED_CUT_COPPER = Items.registerBlock(Blocks.WAXED_WEATHERED_CUT_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_OXIDIZED_CUT_COPPER = Items.registerBlock(Blocks.WAXED_OXIDIZED_CUT_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item OXIDIZED_CUT_COPPER = Items.registerBlock(Blocks.OXIDIZED_CUT_COPPER, ItemGroup.TAB_NATURAL);

   public static final Item WAXED_COPPER_GRATE = Items.registerBlock(Blocks.WAXED_COPPER_GRATE, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_EXPOSED_COPPER_GRATE = Items.registerBlock(Blocks.WAXED_EXPOSED_COPPER_GRATE, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_WEATHERED_COPPER_GRATE = Items.registerBlock(Blocks.WAXED_WEATHERED_COPPER_GRATE, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_OXIDIZED_COPPER_GRATE = Items.registerBlock(Blocks.WAXED_OXIDIZED_COPPER_GRATE, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_CUT_COPPER_SLAB = Items.registerBlock(Blocks.WAXED_CUT_COPPER_SLAB, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_EXPOSED_CUT_COPPER_SLAB = Items.registerBlock(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_WEATHERED_CUT_COPPER_SLAB = Items.registerBlock(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_OXIDIZED_CUT_COPPER_SLAB = Items.registerBlock(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_CUT_COPPER_STAIRS = Items.registerBlock(Blocks.WAXED_CUT_COPPER_STAIRS, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_EXPOSED_CUT_COPPER_STAIRS = Items.registerBlock(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_WEATHERED_CUT_COPPER_STAIRS = Items.registerBlock(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_OXIDIZED_CUT_COPPER_STAIRS = Items.registerBlock(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_CHISELED_COPPER = Items.registerBlock(Blocks.WAXED_CHISELED_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_EXPOSED_CHISELED_COPPER = Items.registerBlock(Blocks.WAXED_EXPOSED_CHISELED_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_WEATHERED_CHISELED_COPPER = Items.registerBlock(Blocks.WAXED_WEATHERED_CHISELED_COPPER, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_OXIDIZED_CHISELED_COPPER = Items.registerBlock(Blocks.WAXED_OXIDIZED_CHISELED_COPPER, ItemGroup.TAB_NATURAL);


   public static final Item WAXED_COPPER_DOOR = registerBlock(new TallBlockItem(Blocks.WAXED_COPPER_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item WAXED_EXPOSED_COPPER_DOOR = registerBlock(new TallBlockItem(Blocks.WAXED_EXPOSED_COPPER_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item WAXED_WEATHERED_COPPER_DOOR = registerBlock(new TallBlockItem(Blocks.WAXED_WEATHERED_COPPER_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item WAXED_OXIDIZED_COPPER_DOOR = registerBlock(new TallBlockItem(Blocks.WAXED_OXIDIZED_COPPER_DOOR, (new Item.Properties()).tab(ItemGroup.TAB_FUNCTIONAL)));
   public static final Item WAXED_COPPER_TRAPDOOR = Items.registerBlock(Blocks.WAXED_COPPER_TRAPDOOR, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_EXPOSED_COPPER_TRAPDOOR = Items.registerBlock(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_WEATHERED_COPPER_TRAPDOOR = Items.registerBlock(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_OXIDIZED_COPPER_TRAPDOOR = Items.registerBlock(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_COPPER_BUTTON = registerBlock(Blocks.WAXED_COPPER_BUTTON, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_EXPOSED_COPPER_BUTTON = registerBlock(Blocks.WAXED_EXPOSED_COPPER_BUTTON, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_WEATHERED_COPPER_BUTTON = registerBlock(Blocks.WAXED_WEATHERED_COPPER_BUTTON, ItemGroup.TAB_NATURAL);
   public static final Item WAXED_OXIDIZED_COPPER_BUTTON = registerBlock(Blocks.WAXED_OXIDIZED_COPPER_BUTTON, ItemGroup.TAB_NATURAL);


   public static final BundleItem BUNDLE = (BundleItem) registerItem("bundle", new BundleItem());
   //public static final Item QUIVER = registerItem("quiver", new QuiverItem());


   public static final Item PALE_MOSS_CARPET = Items.registerBlock(Blocks.PALE_MOSS_CARPET);
   public static final Item PALE_MOSS_BLOCK = Items.registerBlock(Blocks.PALE_MOSS_BLOCK);
   public static final Item PALE_STICK = registerItem("pale_stick", new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));

   public static final Item PALE_HANGING_MOSS = Items.registerBlock(Blocks.PALE_HANGING_MOSS);
   public static final Item CREAKING_HEART_ITEM = registerItem("creaking_heart_core", new CreakingHeartItem((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));
   public static final Item CREAKING_HEART = registerBlock(Blocks.CREAKING_HEART, ItemGroup.TAB_BUILDING_BLOCKS);

   public static final Item TISSUE_SAMPLE = registerItem("tissue_sample", new Item(new Item.Properties().fireResistant().tab(ItemGroup.TAB_MISC)));
   public static final TreasureBagItem EYE_OF_CTHULHU_BAG = (TreasureBagItem) registerItem("treasure_bag_cthulhu", new TreasureBagItem(EntityType.EYE_OF_CTHULHU_SECOND_FORM, dropTable -> {
      dropTable.addAlwaysDrop(new ItemStack(SHIELD_OF_CTHULHU, 1));
      dropTable.addAlwaysDrop(new ItemStack(Items.GOLD_INGOT), 2, 6);
      dropTable.addAlwaysDrop(new ItemStack(NETHERRACK), 30, 87);


      OneDropGroup rareDropGroup = new OneDropGroup()
              .addItem(new ItemStack(Items.NETHERITE_INGOT), 1, 3)
              .addItem(new ItemStack(Items.GOLD_INGOT), 4, 7)
              .addItem(new ItemStack(Items.TISSUE_SAMPLE), 2, 8);

      dropTable.addOneDropGroup(rareDropGroup);

   }));
   public static final Item POKEBALL = registerItem("pokeball", new PokeballItem());
   public static final Item MASTER_BALL = registerItem("masterball", new MasterBallItem());

   public static final Item FIREFLY_BUSH = registerBlock(Blocks.FIREFLY_BUSH, ItemGroup.TAB_NATURAL);
   public static final Item CACTUS_FLOWER = Items.registerBlock(Blocks.CACTUS_FLOWER, ItemGroup.TAB_NATURAL);
   public static final Item SHORT_DRY_GRASS = Items.registerBlock(Blocks.SHORT_DRY_GRASS, ItemGroup.TAB_NATURAL);
   public static final Item TALL_DRY_GRASS = Items.registerBlock(Blocks.TALL_DRY_GRASS, ItemGroup.TAB_NATURAL);
   public static final Item RESIN_CLUMP = Items.registerBlock(Blocks.RESIN_CLUMP, ItemGroup.TAB_NATURAL);
   public static final Item RESIN_BRICK = registerItem("resin_brick", new Item(new Item.Properties().stacksTo(64)));

   public static final Item RESIN_BLOCK = registerBlock(Blocks.RESIN_BLOCK, ItemGroup.TAB_NATURAL);

   public static final Item GOAT_HORN = Items.registerItem("goat_horn", (Item)new InstrumentItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC)));

   public static final Item AXOLOTL_BUCKET = Items.registerItem("axolotl_bucket", (Item)new FishBucketItem(EntityType.AXOLOTL, Fluids.WATER, new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC)));
   public static final Item TADPOLE_BUCKET = Items.registerItem("tadpole_bucket", (Item)new FishBucketItem(EntityType.TADPOLE, Fluids.WATER, new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC)));
   public static final Item DNA = registerItem("dna", new Item(new Item.Properties()));
   public static final Item ENDER_FLU_ICON = registerItem("ender_flu_icon", new Item(new Item.Properties()));


   public static final Item SCULK = registerBlock(Blocks.SCULK, ItemGroup.TAB_NATURAL);
   public static final Item SCULK_VEIN = registerBlock(Blocks.SCULK_VEIN, ItemGroup.TAB_NATURAL);
   public static final Item SCULK_CATALYST = registerBlock(Blocks.SCULK_CATALYST, ItemGroup.TAB_NATURAL);



   public static final Item DEEPSLATE = Items.registerBlock(Blocks.DEEPSLATE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item COBBLED_DEEPSLATE = Items.registerBlock(Blocks.COBBLED_DEEPSLATE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_DEEPSLATE = Items.registerBlock(Blocks.POLISHED_DEEPSLATE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEEPSLATE_BRICKS = Items.registerBlock(Blocks.DEEPSLATE_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CRACKED_DEEPSLATE_BRICKS = Items.registerBlock(Blocks.CRACKED_DEEPSLATE_BRICKS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEEPSLATE_TILES = Items.registerBlock(Blocks.DEEPSLATE_TILES, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CRACKED_DEEPSLATE_TILES = Items.registerBlock(Blocks.CRACKED_DEEPSLATE_TILES, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CHISELED_DEEPSLATE = Items.registerBlock(Blocks.CHISELED_DEEPSLATE, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item COBBLED_DEEPSLATE_WALL = Items.registerBlock(Blocks.COBBLED_DEEPSLATE_WALL, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_DEEPSLATE_WALL = Items.registerBlock(Blocks.POLISHED_DEEPSLATE_WALL, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEEPSLATE_BRICK_WALL = Items.registerBlock(Blocks.DEEPSLATE_BRICK_WALL, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEEPSLATE_TILE_WALL = Items.registerBlock(Blocks.DEEPSLATE_TILE_WALL, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item COBBLED_DEEPSLATE_SLAB = Items.registerBlock(Blocks.COBBLED_DEEPSLATE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_DEEPSLATE_SLAB = Items.registerBlock(Blocks.POLISHED_DEEPSLATE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEEPSLATE_BRICK_SLAB = Items.registerBlock(Blocks.DEEPSLATE_BRICK_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEEPSLATE_TILE_SLAB = Items.registerBlock(Blocks.DEEPSLATE_TILE_SLAB, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item COBBLED_DEEPSLATE_STAIRS = Items.registerBlock(Blocks.COBBLED_DEEPSLATE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item POLISHED_DEEPSLATE_STAIRS = Items.registerBlock(Blocks.POLISHED_DEEPSLATE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEEPSLATE_BRICK_STAIRS = Items.registerBlock(Blocks.DEEPSLATE_BRICK_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item DEEPSLATE_TILE_STAIRS = Items.registerBlock(Blocks.DEEPSLATE_TILE_STAIRS, ItemGroup.TAB_BUILDING_BLOCKS);
   public static final Item CATTAILS = registerBlock(Blocks.CATTAILS, ItemGroup.TAB_NATURAL);

   public static final Item BREEZE_ROD = Items.registerItem("breeze_rod", new Item(new Item.Properties().stacksTo(64)));

   public static final Item BRUSH = Items.registerItem("brush", (Item)new BrushItem());


   public static final Item EDGE_TOOL_TRIM_SMITHING_TEMPLATE = Items.registerItem("edge_tool_trim_smithing_template", (Item)SmithingTemplateItem.createToolTrimTemplate(Rarity.UNCOMMON, ToolTrimPatterns.EDGE, Items.CACTUS));
   public static final Item COAT_TOOL_TRIM_SMITHING_TEMPLATE = Items.registerItem("coat_tool_trim_smithing_template", (Item)SmithingTemplateItem.createToolTrimTemplate(Rarity.UNCOMMON, ToolTrimPatterns.COAT, Items.CACTUS));


   public static final Item DIAMOND_UPGRADE_SMITHING_TEMPLATE = Items.registerItem("diamond_upgrade_smithing_template", (Item)SmithingTemplateItem.createDiamondUpgradeTemplate());
   public static final Item NETHERITE_UPGRADE_SMITHING_TEMPLATE = Items.registerItem("netherite_upgrade_smithing_template", (Item)SmithingTemplateItem.createNetheriteUpgradeTemplate());
   public static final Item SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("sentry_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.SENTRY, Items.COBBLESTONE));
   public static final Item DUNE_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("dune_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.DUNE, Items.SANDSTONE));
   public static final Item COAST_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("coast_armor_trim_smithing_template", (Item) SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.COAST, Items.COBBLESTONE));
   public static final Item WILD_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("wild_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.WILD, Items.MOSSY_COBBLESTONE));
   public static final Item WARD_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("ward_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.RARE, TrimPatterns.WARD, Items.COBBLED_DEEPSLATE));
   public static final Item EYE_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("eye_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.RARE, TrimPatterns.EYE, Items.END_STONE));
   public static final Item VEX_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("vex_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.RARE, TrimPatterns.VEX, Items.COBBLESTONE));
   public static final Item TIDE_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("tide_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.TIDE, Items.PRISMARINE));
   public static final Item SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("snout_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.SNOUT, Items.BLACKSTONE));
   public static final Item RIB_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("rib_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.RIB, Items.NETHERRACK));
   public static final Item SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("spire_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.RARE, TrimPatterns.SPIRE, Items.PURPUR_BLOCK));
   public static final Item WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("wayfinder_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.WAYFINDER, Items.TERRACOTTA));
   public static final Item SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("shaper_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.SHAPER, Items.TERRACOTTA));
   public static final Item SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("silence_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.EPIC, TrimPatterns.SILENCE, Items.COBBLED_DEEPSLATE));
   public static final Item RAISER_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("raiser_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.RAISER, Items.TERRACOTTA));
   public static final Item HOST_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("host_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.HOST, Items.TERRACOTTA));
   public static final Item BOLT_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("bolt_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.BOLT, Items.COPPER_BLOCK));
   public static final Item FLOW_ARMOR_TRIM_SMITHING_TEMPLATE = Items.registerItem("flow_armor_trim_smithing_template", (Item)SmithingTemplateItem.createArmorTrimTemplate(Rarity.UNCOMMON, TrimPatterns.FLOW, Items.BREEZE_ROD));

   public static final Item ANGLER_POTTERY_SHERD = Items.registerItem("angler_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item ARCHER_POTTERY_SHERD = Items.registerItem("archer_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item ARMS_UP_POTTERY_SHERD = Items.registerItem("arms_up_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item BLADE_POTTERY_SHERD = Items.registerItem("blade_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item BREWER_POTTERY_SHERD = Items.registerItem("brewer_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item BURN_POTTERY_SHERD = Items.registerItem("burn_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item DANGER_POTTERY_SHERD = Items.registerItem("danger_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item EXPLORER_POTTERY_SHERD = Items.registerItem("explorer_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item FRIEND_POTTERY_SHERD = Items.registerItem("friend_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item HEART_POTTERY_SHERD = Items.registerItem("heart_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item HEARTBREAK_POTTERY_SHERD = Items.registerItem("heartbreak_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item HOWL_POTTERY_SHERD = Items.registerItem("howl_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item MINER_POTTERY_SHERD = Items.registerItem("miner_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item MOURNER_POTTERY_SHERD = Items.registerItem("mourner_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item PLENTY_POTTERY_SHERD = Items.registerItem("plenty_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item PRIZE_POTTERY_SHERD = Items.registerItem("prize_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item SHEAF_POTTERY_SHERD = Items.registerItem("sheaf_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item SHELTER_POTTERY_SHERD = Items.registerItem("shelter_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item SKULL_POTTERY_SHERD = Items.registerItem("skull_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item SNORT_POTTERY_SHERD = Items.registerItem("snort_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item POT_POTTERY_SHERD = Items.registerItem("pot_pottery_sherd", new PotterySherdItem(new Item.Properties()));
   public static final Item DUST_POTTERY_SHERD = Items.registerItem("dust_pottery_sherd", new PotterySherdItem(new Item.Properties()));




   public static final Item CANDLE = Items.registerBlock(Blocks.CANDLE);
   public static final Item WHITE_CANDLE = Items.registerBlock(Blocks.WHITE_CANDLE);
   public static final Item ORANGE_CANDLE = Items.registerBlock(Blocks.ORANGE_CANDLE);
   public static final Item MAGENTA_CANDLE = Items.registerBlock(Blocks.MAGENTA_CANDLE);
   public static final Item LIGHT_BLUE_CANDLE = Items.registerBlock(Blocks.LIGHT_BLUE_CANDLE);
   public static final Item YELLOW_CANDLE = Items.registerBlock(Blocks.YELLOW_CANDLE);
   public static final Item LIME_CANDLE = Items.registerBlock(Blocks.LIME_CANDLE);
   public static final Item PINK_CANDLE = Items.registerBlock(Blocks.PINK_CANDLE);
   public static final Item GRAY_CANDLE = Items.registerBlock(Blocks.GRAY_CANDLE);
   public static final Item LIGHT_GRAY_CANDLE = Items.registerBlock(Blocks.LIGHT_GRAY_CANDLE);
   public static final Item CYAN_CANDLE = Items.registerBlock(Blocks.CYAN_CANDLE);
   public static final Item PURPLE_CANDLE = Items.registerBlock(Blocks.PURPLE_CANDLE);
   public static final Item BLUE_CANDLE = Items.registerBlock(Blocks.BLUE_CANDLE);
   public static final Item BROWN_CANDLE = Items.registerBlock(Blocks.BROWN_CANDLE);
   public static final Item GREEN_CANDLE = Items.registerBlock(Blocks.GREEN_CANDLE);
   public static final Item RED_CANDLE = Items.registerBlock(Blocks.RED_CANDLE);
   public static final Item BLACK_CANDLE = Items.registerBlock(Blocks.BLACK_CANDLE);



   public static final Item FROGSPAWN = registerBlock(new LilyPadItem(Blocks.FROGSPAWN, (new Item.Properties()).tab(ItemGroup.TAB_NATURAL)));


   public static final Item MUD_BRICKS = registerBlock(Blocks.MUD_BRICKS);
   public static final Item MUD_BRICK_SLAB = registerBlock(Blocks.MUD_BRICK_SLAB);
   public static final Item MUD_BRICK_WALL = registerBlock(Blocks.MUD_BRICK_WALL);
   public static final Item MUD_BRICK_STAIRS = registerBlock(Blocks.MUD_BRICK_STAIRS);

   public static final Item PACKED_MUD = registerBlock(Blocks.PACKED_MUD);

   public static final Item WHITE_HARNESS      = createHarness(DyeColor.WHITE);
   public static final Item ORANGE_HARNESS     = createHarness(DyeColor.ORANGE);
   public static final Item MAGENTA_HARNESS    = createHarness(DyeColor.MAGENTA);
   public static final Item LIGHT_BLUE_HARNESS = createHarness(DyeColor.LIGHT_BLUE);
   public static final Item YELLOW_HARNESS     = createHarness(DyeColor.YELLOW);
   public static final Item LIME_HARNESS       = createHarness(DyeColor.LIME);
   public static final Item PINK_HARNESS       = createHarness(DyeColor.PINK);
   public static final Item GRAY_HARNESS       = createHarness(DyeColor.GRAY);
   public static final Item LIGHT_GRAY_HARNESS = createHarness(DyeColor.LIGHT_GRAY);
   public static final Item CYAN_HARNESS       = createHarness(DyeColor.CYAN);
   public static final Item PURPLE_HARNESS     = createHarness(DyeColor.PURPLE);
   public static final Item BLUE_HARNESS       = createHarness(DyeColor.BLUE);
   public static final Item BROWN_HARNESS      = createHarness(DyeColor.BROWN);
   public static final Item GREEN_HARNESS      = createHarness(DyeColor.GREEN);
   public static final Item RED_HARNESS        = createHarness(DyeColor.RED);
   public static final Item BLACK_HARNESS      = createHarness(DyeColor.BLACK);



   private static HarnessItem createHarness(DyeColor color) {
      return (HarnessItem) registerItem(color.getName() + "_harness", new HarnessItem(color, new Item.Properties()));
   }

   private static Item registerBlock(Block p_221545_0_) {
      return registerBlock(new BlockItem(p_221545_0_, new Item.Properties()));
   }

   private static Item registerBlock(Block p_221542_0_, ItemGroup p_221542_1_) {
      return registerBlock(new BlockItem(p_221542_0_, (new Item.Properties()).tab(p_221542_1_)));
   }

   private static Item colouredBlock(Block p_221542_0_) {
      return registerBlock(new BlockItem(p_221542_0_, (new Item.Properties()).tab(ItemGroup.TAB_COLOURED_BLOCKS)));
   }

   private static Item registerBlock(BlockItem p_221543_0_) {
      return registerBlock(p_221543_0_.getBlock(), p_221543_0_);
   }

   protected static Item registerBlock(Block p_221546_0_, Item p_221546_1_) {
      return registerItem(Registry.BLOCK.getKey(p_221546_0_), p_221546_1_);
   }

   private static Item registerItem(String p_221547_0_, Item p_221547_1_) {
      return registerItem(new ResourceLocation(p_221547_0_), p_221547_1_);
   }

   public static Item registerItem(ResourceLocation resourceLocation, Item item) {
      if (item instanceof BlockItem) {
         ((BlockItem)item).registerBlocks(Item.BY_BLOCK, item);
      }

      return Registry.register(Registry.ITEM, resourceLocation, item);
   }
}