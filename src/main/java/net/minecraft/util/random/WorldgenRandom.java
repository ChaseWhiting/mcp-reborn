package net.minecraft.util.random;

import java.util.function.LongFunction;

public class WorldgenRandom
extends LegacyRandomSource {
    private final RandomSource randomSource;
    private int count;

    public WorldgenRandom(RandomSource randomSource) {
        super(0L);
        this.randomSource = randomSource;
    }

    public int getCount() {
        return this.count;
    }

    @Override
    public RandomSource fork() {
        return this.randomSource.fork();
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return this.randomSource.forkPositional();
    }

    @Override
    public int next(int n) {
        ++this.count;
        RandomSource randomSource = this.randomSource;
        if (randomSource instanceof LegacyRandomSource) {
            LegacyRandomSource legacyRandomSource = (LegacyRandomSource)randomSource;
            return legacyRandomSource.next(n);
        }
        return (int)(this.randomSource.nextLong() >>> 64 - n);
    }

    @Override
    public synchronized void setSeed(long l) {
        if (this.randomSource == null) {
            return;
        }
        this.randomSource.setSeed(l);
    }

    public long setDecorationSeed(long l, int n, int n2) {
        this.setSeed(l);
        long l2 = this.nextLong() | 1L;
        long l3 = this.nextLong() | 1L;
        long l4 = (long)n * l2 + (long)n2 * l3 ^ l;
        this.setSeed(l4);
        return l4;
    }

    public void setFeatureSeed(long l, int n, int n2) {
        long l2 = l + (long)n + (long)(10000 * n2);
        this.setSeed(l2);
    }

    public void setLargeFeatureSeed(long l, int n, int n2) {
        this.setSeed(l);
        long l2 = this.nextLong();
        long l3 = this.nextLong();
        long l4 = (long)n * l2 ^ (long)n2 * l3 ^ l;
        this.setSeed(l4);
    }

    public void setLargeFeatureWithSalt(long l, int n, int n2, int n3) {
        long l2 = (long)n * 341873128712L + (long)n2 * 132897987541L + l + (long)n3;
        this.setSeed(l2);
    }

    public static RandomSource seedSlimeChunk(int n, int n2, long l, long l2) {
        return RandomSource.create(l + (long)(n * n * 4987142) + (long)(n * 5947611) + (long)(n2 * n2) * 4392871L + (long)(n2 * 389711) ^ l2);
    }

    public static enum Algorithm {
        LEGACY(LegacyRandomSource::new),
        XOROSHIRO(XoroshiroRandomSource::new);

        private final LongFunction<RandomSource> constructor;

        private Algorithm(LongFunction<RandomSource> longFunction) {
            this.constructor = longFunction;
        }

        public RandomSource newInstance(long l) {
            return this.constructor.apply(l);
        }
    }
}

