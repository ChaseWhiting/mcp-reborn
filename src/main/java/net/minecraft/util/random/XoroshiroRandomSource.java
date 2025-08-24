package net.minecraft.util.random;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.VisibleForTesting;

public class XoroshiroRandomSource
implements RandomSource {
    private static final float FLOAT_UNIT = 5.9604645E-8f;
    private static final double DOUBLE_UNIT = (double)1.110223E-16f;
    public static final Codec<XoroshiroRandomSource> CODEC = Xoroshiro128PlusPlus.CODEC.xmap(xoroshiro128PlusPlus -> new XoroshiroRandomSource((Xoroshiro128PlusPlus)xoroshiro128PlusPlus), xoroshiroRandomSource -> xoroshiroRandomSource.randomNumberGenerator);
    private Xoroshiro128PlusPlus randomNumberGenerator;
    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    public XoroshiroRandomSource(long l) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit(l));
    }

    public XoroshiroRandomSource(RandomSupport.Seed128bit seed128bit) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(seed128bit);
    }

    public XoroshiroRandomSource(long l, long l2) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(l, l2);
    }

    private XoroshiroRandomSource(Xoroshiro128PlusPlus xoroshiro128PlusPlus) {
        this.randomNumberGenerator = xoroshiro128PlusPlus;
    }

    @Override
    public RandomSource fork() {
        return new XoroshiroRandomSource(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return new XoroshiroPositionalRandomFactory(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
    }

    @Override
    public void setSeed(long l) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit(l));
        this.gaussianSource.reset();
    }

    @Override
    public int nextInt() {
        return (int)this.randomNumberGenerator.nextLong();
    }

    @Override
    public int nextInt(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        long l = Integer.toUnsignedLong(this.nextInt());
        long l2 = l * (long)n;
        long l3 = l2 & 0xFFFFFFFFL;
        if (l3 < (long)n) {
            int n2 = Integer.remainderUnsigned(~n + 1, n);
            while (l3 < (long)n2) {
                l = Integer.toUnsignedLong(this.nextInt());
                l2 = l * (long)n;
                l3 = l2 & 0xFFFFFFFFL;
            }
        }
        long l4 = l2 >> 32;
        return (int)l4;
    }

    @Override
    public long nextLong() {
        return this.randomNumberGenerator.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return (this.randomNumberGenerator.nextLong() & 1L) != 0L;
    }

    @Override
    public float nextFloat() {
        return (float)this.nextBits(24) * 5.9604645E-8f;
    }

    @Override
    public double nextDouble() {
        return (double)this.nextBits(53) * (double)1.110223E-16f;
    }

    @Override
    public double nextGaussian() {
        return this.gaussianSource.nextGaussian();
    }

    @Override
    public void consumeCount(int n) {
        for (int i = 0; i < n; ++i) {
            this.randomNumberGenerator.nextLong();
        }
    }

    private long nextBits(int n) {
        return this.randomNumberGenerator.nextLong() >>> 64 - n;
    }

    public static class XoroshiroPositionalRandomFactory
    implements PositionalRandomFactory {
        private final long seedLo;
        private final long seedHi;

        public XoroshiroPositionalRandomFactory(long l, long l2) {
            this.seedLo = l;
            this.seedHi = l2;
        }

        @Override
        public RandomSource at(int n, int n2, int n3) {
            long l = MathHelper.getSeed(n, n2, n3);
            long l2 = l ^ this.seedLo;
            return new XoroshiroRandomSource(l2, this.seedHi);
        }

        @Override
        public RandomSource fromHashOf(String string) {
            RandomSupport.Seed128bit seed128bit = RandomSupport.seedFromHashOf(string);
            return new XoroshiroRandomSource(seed128bit.xor(this.seedLo, this.seedHi));
        }

        @Override
        public RandomSource fromSeed(long l) {
            return new XoroshiroRandomSource(l ^ this.seedLo, l ^ this.seedHi);
        }

        @Override
        @VisibleForTesting
        public void parityConfigString(StringBuilder stringBuilder) {
            stringBuilder.append("seedLo: ").append(this.seedLo).append(", seedHi: ").append(this.seedHi);
        }
    }
}