package net.minecraft.util;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

import java.util.Random;


public class UniformInt extends IntProvider {

    public static final Codec<UniformInt> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("min_inclusive").forGetter(i -> i.minInclusive),
            Codec.INT.fieldOf("max_inclusive").forGetter(i -> i.maxInclusive)
    ).apply(instance, UniformInt::new));
    private final int minInclusive;
    private final int maxInclusive;

    public UniformInt(int n, int n2) {
        this.minInclusive = n;
        this.maxInclusive = n2;
    }

    public static UniformInt of(int n, int n2) {
        return new UniformInt(n, n2);
    }

    public int sample(Random randomSource) {
        return MathHelper.randomBetweenInclusive(randomSource, this.minInclusive, this.maxInclusive);
    }

    public int sample(RandomSource randomSource) {
        return this.sample(new Random(randomSource.nextLong()));
    }

    public int getMinValue() {
        return this.minInclusive;
    }


    public int getMaxValue() {
        return this.maxInclusive;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.UNIFORM;
    }


    public String toString() {
        return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}

