package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeConstants;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;

public enum AddSandyDrylandsSmoothLayer implements IAreaTransformer2 {
    INSTANCE;

    private static final int CUSTOM_BIOME = 204;
    private static final int DESERT = BiomeConstants.get(Biomes.DESERT);

    private static final int[] AVOID = {BiomeConstants.get(Biomes.DESERT_HILLS),BiomeConstants.get(Biomes.PLAINS), BiomeConstants.get(Biomes.DESERT_LAKES), BiomeConstants.get(Biomes.RIVER), BiomeConstants.get(Biomes.BEACH), BiomeConstants.get(Biomes.STONE_SHORE)};

    private boolean isAvoidBiome(int id) {
        for (int b : AVOID) {
            if (b == id) return true;
        }
        return false;
    }

    private boolean isDesert(int id) {
        return id == DESERT || id == CUSTOM_BIOME;
    }

    @Override
    public int applyPixel(INoiseRandom random, IArea area1, IArea area2, int x, int y) {
        int center = area1.get(x, y);

        if (center != DESERT) {
            return center;
        }

        int[][] offsets = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},      // cardinal
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1},    // diagonal
                {-2, 0}, {2, 0}, {0, -2}, {0, 2}       // extended
        };

        int surroundingDesert = 0;
        for (int[] offset : offsets) {
            int id = area1.get(x + offset[0], y + offset[1]);
            if (isDesert(id)) surroundingDesert++;
            if (isAvoidBiome(id)) return center;
        }

        if (surroundingDesert >= 8) {
            if (random.nextRandom(50) == 0) {
                return random.nextRandom(8) == 0 ? BiomeConstants.get(Biomes.DEEP_DARK) : CUSTOM_BIOME;
            }
        }

        return center;
    }

    @Override
    public int getParentX(int p_215721_1_) {
        return p_215721_1_ - 1;
    }

    @Override
    public int getParentY(int p_215722_1_) {
        return p_215722_1_ - 1;
    }
}
