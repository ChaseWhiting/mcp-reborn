package net.minecraft.entity.passive;


import net.minecraft.entity.axolotl.AxolotlEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.WeightedList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import java.util.Random;

public class AxolotlVariantSpawnRules {
    private static final AxolotlVariantSpawnConfiguration SWAMP_CONFIG =
            new AxolotlVariantSpawnConfiguration(weighted(newList()
                    .add(commonColors(AxolotlEntity.AxolotlVariant.GOLD), 95)
                    .add(commonColors(AxolotlEntity.AxolotlVariant.WILD), 95)
                    .add(single(AxolotlEntity.AxolotlVariant.LUCY), 5)
                    .add(single(AxolotlEntity.AxolotlVariant.CYAN), 5)));

    private static final AxolotlVariantSpawnConfiguration WARM_OCEAN_CONFIG =
            new AxolotlVariantSpawnConfiguration(weighted(newList()
                    .add(commonColors(AxolotlEntity.AxolotlVariant.LUCY), 95)
                    .add(commonColors(AxolotlEntity.AxolotlVariant.CYAN), 95)
                    .add(single(AxolotlEntity.AxolotlVariant.WILD), 5)
                    .add(single(AxolotlEntity.AxolotlVariant.GOLD), 5)));

    private static final AxolotlVariantSpawnConfiguration NO_CONFIG =
            new AxolotlVariantSpawnConfiguration(weighted(newList()
                    .add(single(AxolotlEntity.AxolotlVariant.WILD), 299)
                    .add(single(AxolotlEntity.AxolotlVariant.GOLD), 299)
                    .add(single(AxolotlEntity.AxolotlVariant.CYAN), 299)
                    .add(single(AxolotlEntity.AxolotlVariant.LUCY), 299)
                    .add(single(AxolotlEntity.AxolotlVariant.BLUE), 1)));


    private static AxolotlVariantSpawnProvider commonColors(AxolotlEntity.AxolotlVariant variant) {
        return AxolotlVariantSpawnRules.weighted(AxolotlVariantSpawnRules.newList()
                .add(AxolotlVariantSpawnRules.single(variant), 1199)
                .add(AxolotlVariantSpawnRules.single(AxolotlEntity.AxolotlVariant.BLUE), 1));
    }

    public static AxolotlEntity.AxolotlVariant getVariant(RegistryKey<Biome> holder, Random randomSource) {
        AxolotlVariantSpawnConfiguration sheepColorSpawnConfiguration = AxolotlVariantSpawnRules.getAxolotlVariantConfig(holder);
        return sheepColorSpawnConfiguration.provider().get(randomSource);
    }

    private static AxolotlVariantSpawnConfiguration getAxolotlVariantConfig(RegistryKey<Biome> holder) {
        if (holder == Biomes.SWAMP || holder == Biomes.SWAMP_HILLS) {
            return SWAMP_CONFIG;
        }
        if (holder == Biomes.WARM_OCEAN || holder == Biomes.DEEP_WARM_OCEAN || holder == Biomes.DEEP_LUKEWARM_OCEAN || holder == Biomes.LUKEWARM_OCEAN) {
            return WARM_OCEAN_CONFIG;
        }

        return NO_CONFIG;
    }

    private static AxolotlVariantSpawnProvider weighted(WeightedList<AxolotlVariantSpawnProvider> variantList) {
        if (variantList.isEmpty()) {
            throw new IllegalArgumentException("List must be non-empty");
        }
        return randomSource -> (variantList.getOne(randomSource)).get(randomSource);
    }

    private static AxolotlVariantSpawnProvider single(AxolotlEntity.AxolotlVariant variant) {
        return randomSource -> variant;
    }

    private static WeightedList<AxolotlVariantSpawnProvider> newList() {
        return new WeightedList<>();
    }

    @FunctionalInterface
    static interface AxolotlVariantSpawnProvider {
        public AxolotlEntity.AxolotlVariant get(Random random);
    }

    static class AxolotlVariantSpawnConfiguration {
        final AxolotlVariantSpawnProvider provider;
        protected AxolotlVariantSpawnConfiguration(AxolotlVariantSpawnProvider provider) {
            this.provider = provider;
        }

        public AxolotlVariantSpawnProvider provider() {
            return provider;
        }
    }
}

