package net.minecraft.world.biome.newBiome.noise;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.minecraft.util.random.LegacyRandomSource;
import net.minecraft.util.random.RandomSource;
import net.minecraft.util.random.WorldgenRandom;

import java.util.List;

public class PerlinSimplexNoise {
    private final SimplexNoise[] noiseLevels;
    private final double highestFreqValueFactor;
    private final double highestFreqInputFactor;

    public PerlinSimplexNoise(RandomSource randomSource, List<Integer> list) {
        this(randomSource, (IntSortedSet)new IntRBTreeSet(list));
    }

    private PerlinSimplexNoise(RandomSource randomSource, IntSortedSet intSortedSet) {
        int n;
        if (intSortedSet.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }
        int n2 = -intSortedSet.firstInt();
        int n3 = n2 + (n = intSortedSet.lastInt()) + 1;
        if (n3 < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        SimplexNoise simplexNoise = new SimplexNoise(randomSource);
        int n4 = n;
        this.noiseLevels = new SimplexNoise[n3];
        if (n4 >= 0 && n4 < n3 && intSortedSet.contains(0)) {
            this.noiseLevels[n4] = simplexNoise;
        }
        for (int i = n4 + 1; i < n3; ++i) {
            if (i >= 0 && intSortedSet.contains(n4 - i)) {
                this.noiseLevels[i] = new SimplexNoise(randomSource);
                continue;
            }
            randomSource.consumeCount(262);
        }
        if (n > 0) {
            long l = (long)(simplexNoise.getValue(simplexNoise.xo, simplexNoise.yo, simplexNoise.zo) * 9.223372036854776E18);
            WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(l));
            for (int i = n4 - 1; i >= 0; --i) {
                if (i < n3 && intSortedSet.contains(n4 - i)) {
                    this.noiseLevels[i] = new SimplexNoise(worldgenRandom);
                    continue;
                }
                worldgenRandom.consumeCount(262);
            }
        }
        this.highestFreqInputFactor = Math.pow(2.0, n);
        this.highestFreqValueFactor = 1.0 / (Math.pow(2.0, n3) - 1.0);
    }

    public double getValue(double d, double d2, boolean bl) {
        double d3 = 0.0;
        double d4 = this.highestFreqInputFactor;
        double d5 = this.highestFreqValueFactor;
        for (SimplexNoise simplexNoise : this.noiseLevels) {
            if (simplexNoise != null) {
                d3 += simplexNoise.getValue(d * d4 + (bl ? simplexNoise.xo : 0.0), d2 * d4 + (bl ? simplexNoise.yo : 0.0)) * d5;
            }
            d4 /= 2.0;
            d5 *= 2.0;
        }
        return d3;
    }
}

