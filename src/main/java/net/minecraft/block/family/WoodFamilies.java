package net.minecraft.block.family;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;

import java.util.List;

public final class WoodFamilies {

    public static final SimpleWoodFamily PALE_OAK = SimpleWoodFamily.builder("pale_oak").planks(Blocks.PALE_OAK_PLANKS).stairs(Blocks.PALE_OAK_STAIRS).slab(Blocks.PALE_OAK_SLAB)
            .fence(Blocks.PALE_OAK_FENCE).fenceGate(Blocks.PALE_OAK_FENCE_GATE).door(Blocks.PALE_OAK_DOOR).trapdoor(Blocks.PALE_OAK_TRAPDOOR)
            .pressurePlate(Blocks.PALE_OAK_PRESSURE_PLATE).button(Blocks.PALE_OAK_BUTTON).log(Blocks.PALE_OAK_LOG).strippedLog(Blocks.STRIPPED_PALE_LOG)
            .wood(Blocks.PALE_OAK_WOOD).strippedWood(Blocks.STRIPPED_PALE_OAK_WOOD).sign(Blocks.PALE_OAK_SIGN).build();

    public static final SimpleWoodFamily OAK = SimpleWoodFamily.builder("oak").planks(Blocks.OAK_PLANKS).stairs(Blocks.OAK_STAIRS).slab(Blocks.OAK_SLAB)
            .fence(Blocks.OAK_FENCE).fenceGate(Blocks.OAK_FENCE_GATE).door(Blocks.OAK_DOOR).trapdoor(Blocks.OAK_TRAPDOOR)
            .pressurePlate(Blocks.OAK_PRESSURE_PLATE).button(Blocks.OAK_BUTTON).log(Blocks.OAK_LOG).strippedLog(Blocks.STRIPPED_OAK_LOG)
            .wood(Blocks.OAK_WOOD).strippedWood(Blocks.STRIPPED_OAK_WOOD).sign(Blocks.OAK_SIGN).boat(Items.OAK_BOAT).build();

    public static final SimpleWoodFamily SPRUCE = SimpleWoodFamily.builder("spruce").planks(Blocks.SPRUCE_PLANKS).stairs(Blocks.SPRUCE_STAIRS).slab(Blocks.SPRUCE_SLAB)
            .fence(Blocks.SPRUCE_FENCE).fenceGate(Blocks.SPRUCE_FENCE_GATE).door(Blocks.SPRUCE_DOOR).trapdoor(Blocks.SPRUCE_TRAPDOOR)
            .pressurePlate(Blocks.SPRUCE_PRESSURE_PLATE).button(Blocks.SPRUCE_BUTTON).log(Blocks.SPRUCE_LOG).strippedLog(Blocks.STRIPPED_SPRUCE_LOG)
            .wood(Blocks.SPRUCE_WOOD).strippedWood(Blocks.STRIPPED_SPRUCE_WOOD).sign(Blocks.SPRUCE_SIGN).boat(Items.SPRUCE_BOAT).build();

    public static final SimpleWoodFamily BIRCH = SimpleWoodFamily.builder("birch").planks(Blocks.BIRCH_PLANKS).stairs(Blocks.BIRCH_STAIRS).slab(Blocks.BIRCH_SLAB)
            .fence(Blocks.BIRCH_FENCE).fenceGate(Blocks.BIRCH_FENCE_GATE).door(Blocks.BIRCH_DOOR).trapdoor(Blocks.BIRCH_TRAPDOOR)
            .pressurePlate(Blocks.BIRCH_PRESSURE_PLATE).button(Blocks.BIRCH_BUTTON).log(Blocks.BIRCH_LOG).strippedLog(Blocks.STRIPPED_BIRCH_LOG)
            .wood(Blocks.BIRCH_WOOD).strippedWood(Blocks.STRIPPED_BIRCH_WOOD).sign(Blocks.BIRCH_SIGN).boat(Items.BIRCH_BOAT).build();

    public static final SimpleWoodFamily JUNGLE = SimpleWoodFamily.builder("jungle").planks(Blocks.JUNGLE_PLANKS).stairs(Blocks.JUNGLE_STAIRS).slab(Blocks.JUNGLE_SLAB)
            .fence(Blocks.JUNGLE_FENCE).fenceGate(Blocks.JUNGLE_FENCE_GATE).door(Blocks.JUNGLE_DOOR).trapdoor(Blocks.JUNGLE_TRAPDOOR)
            .pressurePlate(Blocks.JUNGLE_PRESSURE_PLATE).button(Blocks.JUNGLE_BUTTON).log(Blocks.JUNGLE_LOG).strippedLog(Blocks.STRIPPED_JUNGLE_LOG)
            .wood(Blocks.JUNGLE_WOOD).strippedWood(Blocks.STRIPPED_JUNGLE_WOOD).sign(Blocks.JUNGLE_SIGN).boat(Items.JUNGLE_BOAT).build();

    public static final SimpleWoodFamily ACACIA = SimpleWoodFamily.builder("acacia").planks(Blocks.ACACIA_PLANKS).stairs(Blocks.ACACIA_STAIRS).slab(Blocks.ACACIA_SLAB)
            .fence(Blocks.ACACIA_FENCE).fenceGate(Blocks.ACACIA_FENCE_GATE).door(Blocks.ACACIA_DOOR).trapdoor(Blocks.ACACIA_TRAPDOOR)
            .pressurePlate(Blocks.ACACIA_PRESSURE_PLATE).button(Blocks.ACACIA_BUTTON).log(Blocks.ACACIA_LOG).strippedLog(Blocks.STRIPPED_ACACIA_LOG)
            .wood(Blocks.ACACIA_WOOD).strippedWood(Blocks.STRIPPED_ACACIA_WOOD).sign(Blocks.ACACIA_SIGN).boat(Items.ACACIA_BOAT).build();

    public static final SimpleWoodFamily DARK_OAK = SimpleWoodFamily.builder("dark_oak").planks(Blocks.DARK_OAK_PLANKS).stairs(Blocks.DARK_OAK_STAIRS).slab(Blocks.DARK_OAK_SLAB)
            .fence(Blocks.DARK_OAK_FENCE).fenceGate(Blocks.DARK_OAK_FENCE_GATE).door(Blocks.DARK_OAK_DOOR).trapdoor(Blocks.DARK_OAK_TRAPDOOR)
            .pressurePlate(Blocks.DARK_OAK_PRESSURE_PLATE).button(Blocks.DARK_OAK_BUTTON).log(Blocks.DARK_OAK_LOG).strippedLog(Blocks.STRIPPED_DARK_OAK_LOG)
            .wood(Blocks.DARK_OAK_WOOD).strippedWood(Blocks.STRIPPED_DARK_OAK_WOOD).sign(Blocks.DARK_OAK_SIGN).boat(Items.DARK_OAK_BOAT).build();

//    public static final SimpleWoodFamily MANGROVE = SimpleWoodFamily.builder("mangrove").planks(Blocks.MANGROVE_PLANKS).stairs(Blocks.MANGROVE_STAIRS).slab(Blocks.MANGROVE_SLAB)
//            .fence(Blocks.MANGROVE_FENCE).fenceGate(Blocks.MANGROVE_FENCE_GATE).door(Blocks.MANGROVE_DOOR).trapdoor(Blocks.MANGROVE_TRAPDOOR)
//            .pressurePlate(Blocks.MANGROVE_PRESSURE_PLATE).button(Blocks.MANGROVE_BUTTON).log(Blocks.MANGROVE_LOG).strippedLog(Blocks.STRIPPED_MANGROVE_LOG)
//            .wood(Blocks.MANGROVE_WOOD).strippedWood(Blocks.STRIPPED_MANGROVE_WOOD).build();

    public static final SimpleWoodFamily CRIMSON = SimpleWoodFamily.builder("crimson").planks(Blocks.CRIMSON_PLANKS).stairs(Blocks.CRIMSON_STAIRS).slab(Blocks.CRIMSON_SLAB)
            .fence(Blocks.CRIMSON_FENCE).fenceGate(Blocks.CRIMSON_FENCE_GATE).door(Blocks.CRIMSON_DOOR).trapdoor(Blocks.CRIMSON_TRAPDOOR)
            .pressurePlate(Blocks.CRIMSON_PRESSURE_PLATE).button(Blocks.CRIMSON_BUTTON).log(Blocks.CRIMSON_STEM).strippedLog(Blocks.STRIPPED_CRIMSON_STEM)
            .wood(Blocks.CRIMSON_HYPHAE).strippedWood(Blocks.STRIPPED_CRIMSON_HYPHAE).sign(Blocks.CRIMSON_SIGN).build();

    public static final SimpleWoodFamily WARPED = SimpleWoodFamily.builder("warped").planks(Blocks.WARPED_PLANKS).stairs(Blocks.WARPED_STAIRS).slab(Blocks.WARPED_SLAB)
            .fence(Blocks.WARPED_FENCE).fenceGate(Blocks.WARPED_FENCE_GATE).door(Blocks.WARPED_DOOR).trapdoor(Blocks.WARPED_TRAPDOOR)
            .pressurePlate(Blocks.WARPED_PRESSURE_PLATE).button(Blocks.WARPED_BUTTON).log(Blocks.WARPED_STEM).strippedLog(Blocks.STRIPPED_WARPED_STEM)
            .wood(Blocks.WARPED_HYPHAE).strippedWood(Blocks.STRIPPED_WARPED_HYPHAE).sign(Blocks.WARPED_SIGN).build();


    public static List<SimpleWoodFamily> allFamilies() {
        return List.of(PALE_OAK, OAK, JUNGLE, ACACIA, DARK_OAK, SPRUCE, BIRCH);
    }
}
