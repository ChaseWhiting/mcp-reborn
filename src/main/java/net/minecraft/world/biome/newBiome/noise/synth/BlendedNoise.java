package net.minecraft.world.biome.newBiome.noise.synth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomSource;
import net.minecraft.util.random.XoroshiroRandomSource;
import net.minecraft.world.biome.newBiome.climate.densityFunctions.DensityFunction;
import net.minecraft.world.biome.newBiome.climate.densityFunctions.KeyDispatchDataCodec;
import net.minecraft.world.biome.newBiome.noise.ImprovedNoise;
import net.minecraft.world.biome.newBiome.noise.PerlinNoise;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Locale;
import java.util.stream.IntStream;

public class BlendedNoise
implements DensityFunction.SimpleFunction {
    private static final Codec<Double> SCALE_RANGE = Codec.doubleRange((double)0.001, (double)1000.0);
    private static final MapCodec<BlendedNoise> DATA_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(SCALE_RANGE.fieldOf("xz_scale").forGetter(blendedNoise -> blendedNoise.xzScale), SCALE_RANGE.fieldOf("y_scale").forGetter(blendedNoise -> blendedNoise.yScale), SCALE_RANGE.fieldOf("xz_factor").forGetter(blendedNoise -> blendedNoise.xzFactor), SCALE_RANGE.fieldOf("y_factor").forGetter(blendedNoise -> blendedNoise.yFactor), Codec.doubleRange((double)1.0, (double)8.0).fieldOf("smear_scale_multiplier").forGetter(blendedNoise -> blendedNoise.smearScaleMultiplier)).apply(instance, BlendedNoise::createUnseeded));
    public static final KeyDispatchDataCodec<BlendedNoise> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
    private final PerlinNoise minLimitNoise;
    private final PerlinNoise maxLimitNoise;
    private final PerlinNoise mainNoise;
    private final double xzMultiplier;
    private final double yMultiplier;
    private final double xzFactor;
    private final double yFactor;
    private final double smearScaleMultiplier;
    private final double maxValue;
    private final double xzScale;
    private final double yScale;

    public static BlendedNoise createUnseeded(double d, double d2, double d3, double d4, double d5) {
        return new BlendedNoise(new XoroshiroRandomSource(0L), d, d2, d3, d4, d5);
    }

    private BlendedNoise(PerlinNoise perlinNoise, PerlinNoise perlinNoise2, PerlinNoise perlinNoise3, double d, double d2, double d3, double d4, double d5) {
        this.minLimitNoise = perlinNoise;
        this.maxLimitNoise = perlinNoise2;
        this.mainNoise = perlinNoise3;
        this.xzScale = d;
        this.yScale = d2;
        this.xzFactor = d3;
        this.yFactor = d4;
        this.smearScaleMultiplier = d5;
        this.xzMultiplier = 684.412 * this.xzScale;
        this.yMultiplier = 684.412 * this.yScale;
        this.maxValue = perlinNoise.maxBrokenValue(this.yMultiplier);
    }

    @VisibleForTesting
    public BlendedNoise(RandomSource randomSource, double d, double d2, double d3, double d4, double d5) {
        this(PerlinNoise.createLegacyForBlendedNoise(randomSource, IntStream.rangeClosed(-15, 0)), PerlinNoise.createLegacyForBlendedNoise(randomSource, IntStream.rangeClosed(-15, 0)), PerlinNoise.createLegacyForBlendedNoise(randomSource, IntStream.rangeClosed(-7, 0)), d, d2, d3, d4, d5);
    }

    public BlendedNoise withNewRandom(RandomSource randomSource) {
        return new BlendedNoise(randomSource, this.xzScale, this.yScale, this.xzFactor, this.yFactor, this.smearScaleMultiplier);
    }

    @Override
    public double compute(DensityFunction.FunctionContext functionContext) {
        double d = (double)functionContext.blockX() * this.xzMultiplier;
        double d2 = (double)functionContext.blockY() * this.yMultiplier;
        double d3 = (double)functionContext.blockZ() * this.xzMultiplier;
        double d4 = d / this.xzFactor;
        double d5 = d2 / this.yFactor;
        double d6 = d3 / this.xzFactor;
        double d7 = this.yMultiplier * this.smearScaleMultiplier;
        double d8 = d7 / this.yFactor;
        double d9 = 0.0;
        double d10 = 0.0;
        double d11 = 0.0;
        boolean bl = true;
        double d12 = 1.0;
        for (int i = 0; i < 8; ++i) {
            ImprovedNoise improvedNoise = this.mainNoise.getOctaveNoise(i);
            if (improvedNoise != null) {
                d11 += improvedNoise.noise(PerlinNoise.wrap(d4 * d12), PerlinNoise.wrap(d5 * d12), PerlinNoise.wrap(d6 * d12), d8 * d12, d5 * d12) / d12;
            }
            d12 /= 2.0;
        }
        double d13 = (d11 / 10.0 + 1.0) / 2.0;
        boolean bl2 = d13 >= 1.0;
        boolean bl3 = d13 <= 0.0;
        d12 = 1.0;
        for (int i = 0; i < 16; ++i) {
            ImprovedNoise improvedNoise;
            double d14 = PerlinNoise.wrap(d * d12);
            double d15 = PerlinNoise.wrap(d2 * d12);
            double d16 = PerlinNoise.wrap(d3 * d12);
            double d17 = d7 * d12;
            if (!bl2 && (improvedNoise = this.minLimitNoise.getOctaveNoise(i)) != null) {
                d9 += improvedNoise.noise(d14, d15, d16, d17, d2 * d12) / d12;
            }
            if (!bl3 && (improvedNoise = this.maxLimitNoise.getOctaveNoise(i)) != null) {
                d10 += improvedNoise.noise(d14, d15, d16, d17, d2 * d12) / d12;
            }
            d12 /= 2.0;
        }
        return MathHelper.clampedLerp(d9 / 512.0, d10 / 512.0, d13) / 128.0;
    }

    @Override
    public double minValue() {
        return -this.maxValue();
    }

    @Override
    public double maxValue() {
        return this.maxValue;
    }

    @VisibleForTesting
    public void parityConfigString(StringBuilder stringBuilder) {
        stringBuilder.append("BlendedNoise{minLimitNoise=");
        this.minLimitNoise.parityConfigString(stringBuilder);
        stringBuilder.append(", maxLimitNoise=");
        this.maxLimitNoise.parityConfigString(stringBuilder);
        stringBuilder.append(", mainNoise=");
        this.mainNoise.parityConfigString(stringBuilder);
        stringBuilder.append(String.format(Locale.ROOT, ", xzScale=%.3f, yScale=%.3f, xzMainScale=%.3f, yMainScale=%.3f, cellWidth=4, cellHeight=8", 684.412, 684.412, 8.555150000000001, 4.277575000000001)).append('}');
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}

