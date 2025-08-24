package net.minecraft.world.biome.newBiome.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import net.minecraft.util.Util;
import net.minecraft.util.random.RandomSource;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;

public class NormalNoise {
    private static final double INPUT_FACTOR = 1.0181268882175227;
    private static final double TARGET_DEVIATION = 0.3333333333333333;
    private final double valueFactor;
    private final PerlinNoise first;
    private final PerlinNoise second;
    private final double maxValue;
    private final NoiseParameters parameters;

    @Deprecated
    public static NormalNoise createLegacyNetherBiome(RandomSource randomSource, NoiseParameters noiseParameters) {
        return new NormalNoise(randomSource, noiseParameters, false);
    }

    public static NormalNoise create(RandomSource randomSource, int n, double ... dArray) {
        return NormalNoise.create(randomSource, new NoiseParameters(n, new DoubleArrayList(dArray)));
    }

    public static NormalNoise create(RandomSource randomSource, NoiseParameters noiseParameters) {
        return new NormalNoise(randomSource, noiseParameters, true);
    }

    private NormalNoise(RandomSource randomSource, NoiseParameters noiseParameters, boolean bl) {
        int n = noiseParameters.firstOctave;
        DoubleList doubleList = noiseParameters.amplitudes;
        this.parameters = noiseParameters;
        if (bl) {
            this.first = PerlinNoise.create(randomSource, n, doubleList);
            this.second = PerlinNoise.create(randomSource, n, doubleList);
        } else {
            this.first = PerlinNoise.createLegacyForLegacyNetherBiome(randomSource, n, doubleList);
            this.second = PerlinNoise.createLegacyForLegacyNetherBiome(randomSource, n, doubleList);
        }
        int n2 = Integer.MAX_VALUE;
        int n3 = Integer.MIN_VALUE;
        DoubleListIterator doubleListIterator = doubleList.iterator();
        while (doubleListIterator.hasNext()) {
            int n4 = doubleListIterator.nextIndex();
            double d = doubleListIterator.nextDouble();
            if (d == 0.0) continue;
            n2 = Math.min(n2, n4);
            n3 = Math.max(n3, n4);
        }
        this.valueFactor = 0.16666666666666666 / NormalNoise.expectedDeviation(n3 - n2);
        this.maxValue = (this.first.maxValue() + this.second.maxValue()) * this.valueFactor;
    }

    public double maxValue() {
        return this.maxValue;
    }

    private static double expectedDeviation(int n) {
        return 0.1 * (1.0 + 1.0 / (double)(n + 1));
    }

    public double getValue(double d, double d2, double d3) {
        double d4 = d * 1.0181268882175227;
        double d5 = d2 * 1.0181268882175227;
        double d6 = d3 * 1.0181268882175227;
        return (this.first.getValue(d, d2, d3) + this.second.getValue(d4, d5, d6)) * this.valueFactor;
    }

    public NoiseParameters parameters() {
        return this.parameters;
    }

    @VisibleForTesting
    public void parityConfigString(StringBuilder stringBuilder) {
        stringBuilder.append("NormalNoise {");
        stringBuilder.append("first: ");
        this.first.parityConfigString(stringBuilder);
        stringBuilder.append(", second: ");
        this.second.parityConfigString(stringBuilder);
        stringBuilder.append("}");
    }

    public static final class NoiseParameters {
        final int firstOctave;
        final DoubleList amplitudes;
        public static final Codec<NoiseParameters> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("firstOctave").forGetter(NoiseParameters::firstOctave), Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(NoiseParameters::amplitudes)).apply(instance, NoiseParameters::new));
        //public static final Codec<NoiseParameters> CODEC = RegistryKeyCodec.create(Registry.NOISE_REGISTRY, DIRECT_CODEC);

        public NoiseParameters(int n, List<Double> list) {
            this(n, (DoubleList)new DoubleArrayList(list));
        }

        public NoiseParameters(int n, double d, double ... dArray) {
            this(n, (DoubleList) Util.make(new DoubleArrayList(dArray), doubleArrayList -> doubleArrayList.add(0, d)));
        }

        public NoiseParameters(int n, DoubleList doubleList) {
            this.firstOctave = n;
            this.amplitudes = doubleList;
        }

        public int firstOctave() {
            return this.firstOctave;
        }

        public DoubleList amplitudes() {
            return this.amplitudes;
        }
    }
}

