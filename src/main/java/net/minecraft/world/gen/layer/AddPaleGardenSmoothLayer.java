package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeConstants;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;

public enum AddPaleGardenSmoothLayer implements IAreaTransformer2 {
    INSTANCE;

    private static final int CUSTOM_BIOME = 200;
    private static final int DARK_FOREST = 29;
    private static final int DARK_FOREST_HILLS = 157;

    private static final int[] AVOID = {1, 4, 18, 2, 17, 130, 6, 7, 16, 25, 26};

    private boolean isAvoidBiome(int id) {
        for (int b : AVOID) {
            if (b == id) return true;
        }
        return false;
    }

    private boolean isDarkForestFamily(int id) {
        return id == DARK_FOREST || id == DARK_FOREST_HILLS || id == CUSTOM_BIOME;
    }

    @Override
    public int applyPixel(INoiseRandom random, IArea area1, IArea area2, int x, int y) {
        int center = area1.get(x, y);

        if (center != DARK_FOREST && center != DARK_FOREST_HILLS) {
            return center;
        }

        int north = area1.get(x, y - 1);
        int south = area1.get(x, y + 1);
        int west  = area1.get(x - 1, y);
        int east  = area1.get(x + 1, y);

        if (isAvoidBiome(north) || isAvoidBiome(south) || isAvoidBiome(west) || isAvoidBiome(east)) {
            return center;
        }

        int surroundingDarkCount = 0;
        if (isDarkForestFamily(north)) surroundingDarkCount++;
        if (isDarkForestFamily(south)) surroundingDarkCount++;
        if (isDarkForestFamily(west)) surroundingDarkCount++;
        if (isDarkForestFamily(east)) surroundingDarkCount++;

        if (surroundingDarkCount >= 4) {
            if (random.nextRandom(10) == 0) {
                return BiomeConstants.get(Biomes.PALE_GARDEN);
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
