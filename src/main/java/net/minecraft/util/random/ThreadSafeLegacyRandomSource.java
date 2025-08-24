package net.minecraft.util.random;

import java.util.concurrent.atomic.AtomicLong;

@Deprecated
public class ThreadSafeLegacyRandomSource
implements BitRandomSource {
    private static final int MODULUS_BITS = 48;
    private static final long MODULUS_MASK = 0xFFFFFFFFFFFFL;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private final AtomicLong seed = new AtomicLong();
    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    public ThreadSafeLegacyRandomSource(long l) {
        this.setSeed(l);
    }

    @Override
    public RandomSource fork() {
        return new ThreadSafeLegacyRandomSource(this.nextLong());
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return new LegacyRandomSource.LegacyPositionalRandomFactory(this.nextLong());
    }

    @Override
    public void setSeed(long l) {
        this.seed.set((l ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL);
    }

    @Override
    public int next(int n) {
        long l;
        long l2;
        while (!this.seed.compareAndSet(l2 = this.seed.get(), l = l2 * 25214903917L + 11L & 0xFFFFFFFFFFFFL)) {
        }
        return (int)(l >>> 48 - n);
    }

    @Override
    public double nextGaussian() {
        return this.gaussianSource.nextGaussian();
    }
}