package net.minecraft.world.biome.newBiome.noise;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.PositionalRandomFactory;
import net.minecraft.util.random.RandomSource;
import org.jetbrains.annotations.VisibleForTesting;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.IntStream;

public class PerlinNoise {
    private static final int ROUND_OFF = 0x2000000;
    private final ImprovedNoise[] noiseLevels;
    private final int firstOctave;
    private final DoubleList amplitudes;
    private final double lowestFreqValueFactor;
    private final double lowestFreqInputFactor;
    private final double maxValue;

    @Deprecated
    public static PerlinNoise createLegacyForBlendedNoise(RandomSource randomSource, IntStream intStream) {
        return new PerlinNoise(randomSource, PerlinNoise.makeAmplitudes((IntSortedSet)new IntRBTreeSet(intStream.boxed().collect(ImmutableList.toImmutableList()))), false);
    }

    @Deprecated
    public static PerlinNoise createLegacyForLegacyNetherBiome(RandomSource randomSource, int n, DoubleList doubleList) {
        return new PerlinNoise(randomSource,Pair.of(n, doubleList), false);
    }

    public static PerlinNoise create(RandomSource randomSource, IntStream intStream) {
        return PerlinNoise.create(randomSource, (List)intStream.boxed().collect(ImmutableList.toImmutableList()));
    }

    public static PerlinNoise create(RandomSource randomSource, List<Integer> list) {
        return new PerlinNoise(randomSource, PerlinNoise.makeAmplitudes((IntSortedSet)new IntRBTreeSet(list)), true);
    }

    public static PerlinNoise create(RandomSource randomSource, int n, double d, double ... dArray) {
        DoubleArrayList doubleArrayList = new DoubleArrayList(dArray);
        doubleArrayList.add(0, d);
        return new PerlinNoise(randomSource, Pair.of(n, doubleArrayList), true);
    }

    public static PerlinNoise create(RandomSource randomSource, int n, DoubleList doubleList) {
        return new PerlinNoise(randomSource, Pair.of(n, doubleList), true);
    }

    private static Pair<Integer, DoubleList> makeAmplitudes(IntSortedSet intSortedSet) {
        int n;
        if (intSortedSet.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }
        int n2 = -intSortedSet.firstInt();
        int n3 = n2 + (n = intSortedSet.lastInt()) + 1;
        if (n3 < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        DoubleArrayList doubleArrayList = new DoubleArrayList(new double[n3]);
        IntBidirectionalIterator intBidirectionalIterator = intSortedSet.iterator();
        while (intBidirectionalIterator.hasNext()) {
            int n4 = intBidirectionalIterator.nextInt();
            doubleArrayList.set(n4 + n2, 1.0);
        }
        return Pair.of((-n2), doubleArrayList);
    }

    protected PerlinNoise(RandomSource randomSource, Pair<Integer, DoubleList> pair, boolean bl) {
        this.firstOctave = (Integer)pair.getFirst();
        this.amplitudes = (DoubleList)pair.getSecond();
        int n = this.amplitudes.size();
        int n2 = -this.firstOctave;
        this.noiseLevels = new ImprovedNoise[n];
        if (bl) {
            PositionalRandomFactory positionalRandomFactory = randomSource.forkPositional();
            for (int i = 0; i < n; ++i) {
                if (this.amplitudes.getDouble(i) == 0.0) continue;
                int n3 = this.firstOctave + i;
                this.noiseLevels[i] = new ImprovedNoise(positionalRandomFactory.fromHashOf("octave_" + n3));
            }
        } else {
            double d2;
            ImprovedNoise improvedNoise = new ImprovedNoise(randomSource);
            if (n2 >= 0 && n2 < n && (d2 = this.amplitudes.getDouble(n2)) != 0.0) {
                this.noiseLevels[n2] = improvedNoise;
            }
            for (int i = n2 - 1; i >= 0; --i) {
                if (i < n) {
                    double d3 = this.amplitudes.getDouble(i);
                    if (d3 != 0.0) {
                        this.noiseLevels[i] = new ImprovedNoise(randomSource);
                        continue;
                    }
                    PerlinNoise.skipOctave(randomSource);
                    continue;
                }
                PerlinNoise.skipOctave(randomSource);
            }
            if (Arrays.stream(this.noiseLevels).filter(Objects::nonNull).count() != this.amplitudes.stream().filter(d -> d != 0.0).count()) {
                throw new IllegalStateException("Failed to create correct number of noise levels for given non-zero amplitudes");
            }
            if (n2 < n - 1) {
                throw new IllegalArgumentException("Positive octaves are temporarily disabled");
            }
        }
        this.lowestFreqInputFactor = Math.pow(2.0, -n2);
        this.lowestFreqValueFactor = Math.pow(2.0, n - 1) / (Math.pow(2.0, n) - 1.0);
        this.maxValue = this.edgeValue(2.0);
    }

    protected double maxValue() {
        return this.maxValue;
    }

    private static void skipOctave(RandomSource randomSource) {
        randomSource.consumeCount(262);
    }

    public double getValue(double d, double d2, double d3) {
        return this.getValue(d, d2, d3, 0.0, 0.0, false);
    }

    @Deprecated
    public double getValue(double d, double d2, double d3, double d4, double d5, boolean bl) {
        double d6 = 0.0;
        double d7 = this.lowestFreqInputFactor;
        double d8 = this.lowestFreqValueFactor;
        for (int i = 0; i < this.noiseLevels.length; ++i) {
            ImprovedNoise improvedNoise = this.noiseLevels[i];
            if (improvedNoise != null) {
                double d9 = improvedNoise.noise(PerlinNoise.wrap(d * d7), bl ? -improvedNoise.yo : PerlinNoise.wrap(d2 * d7), PerlinNoise.wrap(d3 * d7), d4 * d7, d5 * d7);
                d6 += this.amplitudes.getDouble(i) * d9 * d8;
            }
            d7 *= 2.0;
            d8 /= 2.0;
        }
        return d6;
    }

    public double maxBrokenValue(double d) {
        return this.edgeValue(d + 2.0);
    }

    private double edgeValue(double d) {
        double d2 = 0.0;
        double d3 = this.lowestFreqValueFactor;
        for (int i = 0; i < this.noiseLevels.length; ++i) {
            ImprovedNoise improvedNoise = this.noiseLevels[i];
            if (improvedNoise != null) {
                d2 += this.amplitudes.getDouble(i) * d * d3;
            }
            d3 /= 2.0;
        }
        return d2;
    }

    @Nullable
    public ImprovedNoise getOctaveNoise(int n) {
        return this.noiseLevels[this.noiseLevels.length - 1 - n];
    }

    public static double wrap(double d) {
        return d - (double) MathHelper.lfloor(d / 3.3554432E7 + 0.5) * 3.3554432E7;
    }

    protected int firstOctave() {
        return this.firstOctave;
    }

    protected DoubleList amplitudes() {
        return this.amplitudes;
    }

    @VisibleForTesting
    public void parityConfigString(StringBuilder stringBuilder) {
        stringBuilder.append("PerlinNoise{");
        List<String> list = this.amplitudes.stream().map(d -> String.format(Locale.ROOT, "%.2f", d)).toList();
        stringBuilder.append("first octave: ").append(this.firstOctave).append(", amplitudes: ").append(list).append(", noise levels: [");
        for (int i = 0; i < this.noiseLevels.length; ++i) {
            stringBuilder.append(i).append(": ");
            ImprovedNoise improvedNoise = this.noiseLevels[i];
            if (improvedNoise == null) {
                stringBuilder.append("null");
            } else {
                improvedNoise.parityConfigString(stringBuilder);
            }
            stringBuilder.append(", ");
        }
        stringBuilder.append("]");
        stringBuilder.append("}");
    }
}
