package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomSource;

import java.util.function.Function;

public class ClampedInt
extends IntProvider {
    public static final Codec<ClampedInt> CODEC = RecordCodecBuilder.<ClampedInt>create(instance -> instance.group(IntProvider.CODEC.fieldOf("source").forGetter(clampedInt -> clampedInt.source), Codec.INT.fieldOf("min_inclusive").forGetter(clampedInt -> clampedInt.minInclusive), Codec.INT.fieldOf("max_inclusive").forGetter(clampedInt -> clampedInt.maxInclusive)).apply(instance, ClampedInt::new)).comapFlatMap(clampedInt -> {
        if (clampedInt.maxInclusive < clampedInt.minInclusive) {
            return DataResult.error("Max must be at least min, min_inclusive: " + clampedInt.minInclusive + ", max_inclusive: " + clampedInt.maxInclusive);
        }
        return DataResult.success(clampedInt);
    }, Function.identity());
    private final IntProvider source;
    private final int minInclusive;
    private final int maxInclusive;

    public static ClampedInt of(IntProvider intProvider, int n, int n2) {
        return new ClampedInt(intProvider, n, n2);
    }

    public ClampedInt(IntProvider intProvider, int n, int n2) {
        this.source = intProvider;
        this.minInclusive = n;
        this.maxInclusive = n2;
    }

    @Override
    public int sample(RandomSource randomSource) {
        return MathHelper.clamp(this.source.sample(randomSource), this.minInclusive, this.maxInclusive);
    }

    @Override
    public int getMinValue() {
        return Math.max(this.minInclusive, this.source.getMinValue());
    }

    @Override
    public int getMaxValue() {
        return Math.min(this.maxInclusive, this.source.getMaxValue());
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.CLAMPED;
    }
}

