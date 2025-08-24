package net.minecraft.world.biome;

import net.minecraft.util.RegistryKey;

import java.util.HashMap;
import java.util.Map;

public final class BiomeConstants {

    public static Map<RegistryKey<Biome>, Integer> ID_BIOME_MAP = new HashMap<>();



    public static int get(RegistryKey<Biome> biome) {
        return ID_BIOME_MAP.get(biome);
    }
}
