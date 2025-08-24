package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.random.RandomSource;
import net.minecraft.util.registry.Registry;

import java.util.Random;

public abstract class IntProvider {
    private static final Codec<Either<Integer, IntProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.INT, Registry.INT_PROVIDER_TYPE.byNameCodec().dispatch(IntProvider::getType, IntProviderType::codec));
    public static final Codec<IntProvider> CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap(either -> (IntProvider)either.map(ConstantInt::of, intProvider -> intProvider), intProvider -> intProvider.getType() == IntProviderType.CONSTANT ? Either.left(((ConstantInt)intProvider).getValue()) : Either.right(intProvider));
    public static final Codec<IntProvider> NON_NEGATIVE_CODEC = IntProvider.codec(0, Integer.MAX_VALUE);
    public static final Codec<IntProvider> POSITIVE_CODEC = IntProvider.codec(1, Integer.MAX_VALUE);

    public static Codec<IntProvider> codec(int n, int n2) {
        return IntProvider.codec(n, n2, CODEC);
    }

    public static <T extends IntProvider> Codec<T> codec(int n, int n2, Codec<T> codec) {
        return ExtraCodecs.validate(codec, intProvider -> {
            if (intProvider.getMinValue() < n) {
                return DataResult.error("Value provider too low: " + n + " [" + intProvider.getMinValue() + "-" + intProvider.getMaxValue() + "]");
            }
            if (intProvider.getMaxValue() > n2) {
                return DataResult.error("Value provider too high: " + n2 + " [" + intProvider.getMinValue() + "-" + intProvider.getMaxValue() + "]");
            }
            return DataResult.success(intProvider);
        });
    }

    public abstract int sample(RandomSource var1);

    public int sample(Random var1) {
        return this.sample(RandomSource.create(var1.nextLong()));
    };

    public abstract int getMinValue();

    public abstract int getMaxValue();

    public abstract IntProviderType<?> getType();
}
