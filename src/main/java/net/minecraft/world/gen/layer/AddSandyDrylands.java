package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeConstants;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum AddSandyDrylands implements IC1Transformer {
    INSTANCE;

    public int apply(INoiseRandom random, int biomeCheck) {

        if (biomeCheck != BiomeConstants.get(Biomes.DESERT)
                && biomeCheck != BiomeConstants.get(Biomes.DESERT_HILLS)
                && biomeCheck != BiomeConstants.get(Biomes.BADLANDS)
                && biomeCheck != BiomeConstants.get(Biomes.WOODED_BADLANDS_PLATEAU)) {
            return biomeCheck;
        }

        return random.nextRandom(30) == 0 ? random.nextRandom(15) == 0 ? BiomeConstants.get(Biomes.DEEP_DARK) : BiomeConstants.get(Biomes.SANDY_DRYLANDS) : biomeCheck;
    }
}