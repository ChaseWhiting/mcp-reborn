package net.minecraft.entity.monster.bogged;

import com.google.common.collect.Maps;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public enum BoggedType {
    BOGGED(0, "bogged", 45, true),
    BLOSSOMED(1, "blossomed", 65, true),
    WITHERED(2, "withered", 50, true),
    PARCHED(3, "parched", 45, true),
    FESTERED(4, "festered", 75, true),
    FESTERED_BROWN(5, "festered_brown", 75, true),
    FROSTED(6, "frosted", 55, false);


    private static final BoggedType[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(BoggedType::getId)).toArray(BoggedType[]::new);
    private static final Map<String, BoggedType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(BoggedType::getName, (p_221081_0_) -> {
        return p_221081_0_;
    }));

    static final Map<RegistryKey<Biome>, BoggedType> BY_BIOME = Util.make(Maps.newHashMap(), (map) -> {
        // Bogged biomes
        map.put(Biomes.BAMBOO_JUNGLE, BOGGED);
        map.put(Biomes.BAMBOO_JUNGLE_HILLS, BOGGED);
        map.put(Biomes.JUNGLE, BOGGED);
        map.put(Biomes.JUNGLE_EDGE, BOGGED);
        map.put(Biomes.JUNGLE_HILLS, BOGGED);
        map.put(Biomes.MODIFIED_JUNGLE, BOGGED);
        map.put(Biomes.MODIFIED_JUNGLE_EDGE, BOGGED);
        map.put(Biomes.SWAMP, BOGGED);
        map.put(Biomes.SWAMP_HILLS, BOGGED);
        map.put(Biomes.GRAVELLY_MOUNTAINS, BOGGED);
        map.put(Biomes.MODIFIED_GRAVELLY_MOUNTAINS, BOGGED);
        map.put(Biomes.MOUNTAIN_EDGE, BOGGED);
        map.put(Biomes.MOUNTAINS, BOGGED);
        map.put(Biomes.WOODED_MOUNTAINS, BOGGED);

        map.put(Biomes.DEEP_FROZEN_OCEAN, FROSTED);
        map.put(Biomes.FROZEN_OCEAN, FROSTED);
        map.put(Biomes.DEEP_COLD_OCEAN, FROSTED);
        map.put(Biomes.COLD_OCEAN, FROSTED);
        map.put(Biomes.FROZEN_RIVER, FROSTED);
        map.put(Biomes.ICE_SPIKES, FROSTED);
        map.put(Biomes.SNOWY_BEACH, FROSTED);
        map.put(Biomes.SNOWY_MOUNTAINS, FROSTED);
        map.put(Biomes.SNOWY_TAIGA, FROSTED);
        map.put(Biomes.SNOWY_TAIGA_HILLS, FROSTED);
        map.put(Biomes.SNOWY_TAIGA_MOUNTAINS, FROSTED);
        map.put(Biomes.SNOWY_TUNDRA, FROSTED);


        map.put(Biomes.MUSHROOM_FIELDS, FESTERED);
        map.put(Biomes.MUSHROOM_FIELD_SHORE, FESTERED);


        // Parched biomes
        map.put(Biomes.BADLANDS, PARCHED);
        map.put(Biomes.BADLANDS_PLATEAU, PARCHED);
        map.put(Biomes.DESERT, PARCHED);
        map.put(Biomes.DESERT_HILLS, PARCHED);
        map.put(Biomes.DESERT_LAKES, PARCHED);
        map.put(Biomes.ERODED_BADLANDS, PARCHED);
        map.put(Biomes.MODIFIED_BADLANDS_PLATEAU, PARCHED);
        map.put(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, PARCHED);
        map.put(Biomes.WOODED_BADLANDS_PLATEAU, PARCHED);
        map.put(Biomes.SAVANNA, PARCHED);
        map.put(Biomes.SAVANNA_PLATEAU, PARCHED);
        map.put(Biomes.GOLDEN_SAVANNA, PARCHED);
        map.put(Biomes.SHATTERED_SAVANNA, PARCHED);
        map.put(Biomes.SHATTERED_SAVANNA_PLATEAU, PARCHED);

        // Blossomed biomes
        map.put(Biomes.FLOWER_FOREST, BLOSSOMED);
        map.put(Biomes.SUNFLOWER_PLAINS, BLOSSOMED);
        map.put(Biomes.PLAINS, BLOSSOMED);
        map.put(Biomes.DARK_FOREST, BLOSSOMED);
        map.put(Biomes.DARK_FOREST_HILLS, BLOSSOMED);
        map.put(Biomes.GIANT_SPRUCE_TAIGA, BLOSSOMED);
        map.put(Biomes.GIANT_SPRUCE_TAIGA_HILLS, BLOSSOMED);
        map.put(Biomes.GIANT_TREE_TAIGA, BLOSSOMED);
        map.put(Biomes.GIANT_TREE_TAIGA_HILLS, BLOSSOMED);
        map.put(Biomes.TAIGA, BLOSSOMED);
        map.put(Biomes.TAIGA_HILLS, BLOSSOMED);
        map.put(Biomes.TAIGA_MOUNTAINS, BLOSSOMED);
        map.put(Biomes.BIRCH_FOREST, BLOSSOMED);
        map.put(Biomes.BIRCH_FOREST_HILLS, BLOSSOMED);
        map.put(Biomes.TALL_BIRCH_FOREST, BLOSSOMED);
        map.put(Biomes.TALL_BIRCH_HILLS, BLOSSOMED);

        // Withered biomes
        map.put(Biomes.NETHER_WASTES, WITHERED);
        map.put(Biomes.WARPED_FOREST, WITHERED);
        map.put(Biomes.SOUL_SAND_VALLEY, WITHERED);
        map.put(Biomes.CRIMSON_FOREST, WITHERED);
        map.put(Biomes.BASALT_DELTAS, WITHERED);
    });


    private final int id;
    private final String name;
    private final int arrowCooldown;
    final boolean hasMushrooms;


    private BoggedType(int p_i241911_3_, String p_i241911_4_, int arrowShooCooldown, boolean hasMushrooms) {
        this.id = p_i241911_3_;
        this.name = p_i241911_4_;
        this.arrowCooldown = arrowShooCooldown;
        this.hasMushrooms = hasMushrooms;
    }

    public int getArrowCooldown() {
        return arrowCooldown;
    }

    public static boolean canConvertToType(BoggedType type, BoggedEntity bogged) {
        if (bogged.getBoggedType() == type) return false;

        RegistryKey<Biome> currentBiome = bogged.level.getBiomeName(bogged.blockPosition()).orElse(null);


        if (currentBiome == null) {
            return false;
        }


        BoggedType biomeType = BY_BIOME.get(currentBiome);
        return biomeType != null && biomeType.equals(type);
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public static BoggedType byName(String p_221087_0_) {
        return BY_NAME.getOrDefault(p_221087_0_, BOGGED);
    }

    public static BoggedType byId(int p_221080_0_) {
        if (p_221080_0_ < 0 || p_221080_0_ > BY_ID.length) {
            p_221080_0_ = 0;
        }

        return BY_ID[p_221080_0_];
    }


}
