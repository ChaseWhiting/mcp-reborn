package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.random.RandomSource;

public class BiasedToBottomInt
extends IntProvider {
    public static final Codec<BiasedToBottomInt> CODEC = RecordCodecBuilder.<BiasedToBottomInt>create(instance -> instance.group(
            Codec.INT.fieldOf("min_inclusive").forGetter(b -> b.minInclusive),
            Codec.INT.fieldOf("max_inclusive").forGetter(b -> b.maxInclusive)
    ).apply(instance, BiasedToBottomInt::new)).flatXmap(
            biased -> biased.maxInclusive < biased.minInclusive
                    ? DataResult.error("Max must be at least min, min_inclusive: " + biased.minInclusive + ", max_inclusive: " + biased.maxInclusive)
                    : DataResult.success(biased),
            DataResult::success
    );


    private final int minInclusive;
    private final int maxInclusive;

    private BiasedToBottomInt(int n, int n2) {
        this.minInclusive = n;
        this.maxInclusive = n2;
    }

    public static BiasedToBottomInt of(int n, int n2) {
        return new BiasedToBottomInt(n, n2);
    }

    @Override
    public int sample(RandomSource randomSource) {
        return this.minInclusive + randomSource.nextInt(randomSource.nextInt(this.maxInclusive - this.minInclusive + 1) + 1);
    }

    @Override
    public int getMinValue() {
        return this.minInclusive;
    }

    @Override
    public int getMaxValue() {
        return this.maxInclusive;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.BIASED_TO_BOTTOM;
    }

    public String toString() {
        return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}
