package net.minecraft.world.gen.layer;

import net.minecraft.util.Util;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public class SelectiveZoomLayer implements IAreaTransformer1 {
    private final int targetBiomeId;

    public SelectiveZoomLayer(int targetBiomeId) {
        this.targetBiomeId = targetBiomeId;
    }

    @Override
    public int applyPixel(IExtendedNoiseRandom<?> random, IArea area, int x, int y) {
        int currentBiome = area.get(x, y);

        // If the current biome is not the target biome, check surroundings
        if (currentBiome != this.targetBiomeId) {
            int left = area.get(x - 1, y);
            int right = area.get(x + 1, y);
            int up = area.get(x, y - 1);
            int down = area.get(x, y + 1);

            // Expand target biome into neighboring cells
            if (left == this.targetBiomeId || right == this.targetBiomeId || up == this.targetBiomeId || down == this.targetBiomeId) {
                // Chance to convert neighboring biomes to the target biome
                if (true) { // 70% chance to expand
                    return this.targetBiomeId;
                }
            }
        }

        return currentBiome;
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
