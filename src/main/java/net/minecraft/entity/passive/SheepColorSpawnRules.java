package net.minecraft.entity.passive;


import net.minecraft.entity.WarmColdVariant;
import net.minecraft.item.DyeColor;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.WeightedList;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class SheepColorSpawnRules {
    private static final SheepColorSpawnConfiguration TEMPERATE_SPAWN_CONFIGURATION = new SheepColorSpawnConfiguration(SheepColorSpawnRules.weighted(SheepColorSpawnRules.newList().add(SheepColorSpawnRules.single(DyeColor.BLACK), 5).add(SheepColorSpawnRules.single(DyeColor.GRAY), 5).add(SheepColorSpawnRules.single(DyeColor.LIGHT_GRAY), 5).add(SheepColorSpawnRules.single(DyeColor.BROWN), 3).add(SheepColorSpawnRules.commonColors(DyeColor.WHITE), 82)));
    private static final SheepColorSpawnConfiguration WARM_SPAWN_CONFIGURATION = new SheepColorSpawnConfiguration(SheepColorSpawnRules.weighted(SheepColorSpawnRules.newList().add(SheepColorSpawnRules.single(DyeColor.GRAY), 5).add(SheepColorSpawnRules.single(DyeColor.LIGHT_GRAY), 5).add(SheepColorSpawnRules.single(DyeColor.WHITE), 5).add(SheepColorSpawnRules.single(DyeColor.BLACK), 3).add(SheepColorSpawnRules.commonColors(DyeColor.BROWN), 82)));
    private static final SheepColorSpawnConfiguration COLD_SPAWN_CONFIGURATION = new SheepColorSpawnConfiguration(SheepColorSpawnRules.weighted(SheepColorSpawnRules.newList().add(SheepColorSpawnRules.single(DyeColor.LIGHT_GRAY), 5).add(SheepColorSpawnRules.single(DyeColor.GRAY), 5).add(SheepColorSpawnRules.single(DyeColor.WHITE), 5).add(SheepColorSpawnRules.single(DyeColor.BROWN), 3).add(SheepColorSpawnRules.commonColors(DyeColor.BLACK), 82)));

    private static SheepColorProvider commonColors(DyeColor dyeColor) {
        return SheepColorSpawnRules.weighted(SheepColorSpawnRules.newList()
                .add(SheepColorSpawnRules.single(dyeColor), 499)
                .add(SheepColorSpawnRules.single(DyeColor.PINK), 1));
    }

    public static DyeColor getSheepColor(RegistryKey<Biome> holder, Random randomSource) {
        SheepColorSpawnConfiguration sheepColorSpawnConfiguration = SheepColorSpawnRules.getSheepColorConfiguration(holder);
        return sheepColorSpawnConfiguration.colors().get(randomSource);
    }

    private static SheepColorSpawnConfiguration getSheepColorConfiguration(RegistryKey<Biome> holder) {
        if (WarmColdVariant.WARM.getBiomes().contains(holder)) {
            return WARM_SPAWN_CONFIGURATION;
        }
        if (WarmColdVariant.COLD.getBiomes().contains(holder)) {
            return COLD_SPAWN_CONFIGURATION;
        }
        return TEMPERATE_SPAWN_CONFIGURATION;
    }

    private static SheepColorProvider weighted(WeightedList<SheepColorProvider> colorList) {
        if (colorList.isEmpty()) {
            throw new IllegalArgumentException("List must be non-empty");
        }
        return randomSource -> (colorList.getOne(randomSource)).get(randomSource);
    }

    private static SheepColorProvider single(DyeColor dyeColor) {
        return randomSource -> dyeColor;
    }

    private static WeightedList<SheepColorProvider> newList() {
        return new WeightedList<>();
    }

    @FunctionalInterface
    static interface SheepColorProvider {
        public DyeColor get(Random random);
    }

    static class SheepColorSpawnConfiguration {
        final SheepColorProvider colorProvider;
        protected SheepColorSpawnConfiguration(SheepColorProvider colors) {
            this.colorProvider = colors;
        }

        public SheepColorProvider colors() {
            return colorProvider;
        }
    }
}

