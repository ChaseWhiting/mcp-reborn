package net.minecraft.entity;

import com.mojang.serialization.Codec;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import java.util.*;
import java.util.stream.Collectors;

public enum WarmColdVariant {
    TEMPERATE(ModelType.NORMAL, 0, "temperate"),
    WARM(ModelType.WARM, 1, "warm", Biomes.WARM_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.SANDY_DRYLANDS, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS, Biomes.BADLANDS_PLATEAU, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.BAMBOO_JUNGLE_HILLS, Biomes.BAMBOO_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.MODIFIED_JUNGLE, Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.DESERT_LAKES),
    COLD(ModelType.COLD, 2, "cold", Biomes.DEEP_FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.DEEP_COLD_OCEAN, Biomes.COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.TAIGA_HILLS, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.TAIGA, Biomes.SNOWY_MOUNTAINS, Biomes.MOUNTAINS, Biomes.MOUNTAIN_EDGE, Biomes.WOODED_MOUNTAINS, Biomes.ICE_SPIKES, Biomes.SNOWY_TUNDRA);

    private static final WarmColdVariant[] BY_ID =
            Arrays.stream(
                            values())
                    .sorted(
                            Comparator.comparingInt(WarmColdVariant::getId)).toArray(WarmColdVariant[]::new
                    );

    private static final Map<String, WarmColdVariant> BY_NAME =
            Arrays.stream(
                            values())
                    .collect(
                            Collectors.toMap(WarmColdVariant::getName, (variant) -> variant)
                    );

    private final int id;
    private final String name;
    private final List<RegistryKey<Biome>> biomes;
    private final ModelType modelType;

    @SafeVarargs
    WarmColdVariant(ModelType modelType, int id, String name, RegistryKey<Biome>... biomeList) {
        this.id = id;
        this.name = name;
        this.biomes = Arrays.asList(biomeList);
        this.modelType = modelType;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public List<RegistryKey<Biome>> getBiomes() {
        return biomes;
    }

    public boolean isWarm() {
        return this == WARM;
    }

    public boolean isCold() {
        return this == COLD;
    }


    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public static WarmColdVariant byName(String name) {
        return BY_NAME.getOrDefault(name, TEMPERATE);
    }

    public static WarmColdVariant byId(int id) {
        if (id < 0 || id > BY_ID.length) {
            id = 0;
        }

        return BY_ID[id];
    }

    public static <T extends VariantHolder<WarmColdVariant>> void setVariant(T t, RegistryKey<Biome> b) {
        t.setVariant(byBiome(b));
    }

    public static WarmColdVariant byBiome(RegistryKey<Biome> key) {
        if (WARM.biomes.contains(key)) {
            return WARM;
        }
        if (COLD.biomes.contains(key)) {
            return COLD;
        }


        return TEMPERATE;
    }

    public static enum ModelType implements IStringSerializable {
        NORMAL("normal"),
        WARM("warm"),
        COLD("cold");

        public static final Codec<ModelType> CODEC;
        private final String name;

        private ModelType(String string2) {
            this.name = string2;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = IStringSerializable.fromEnum(ModelType::values);
        }
    }
}