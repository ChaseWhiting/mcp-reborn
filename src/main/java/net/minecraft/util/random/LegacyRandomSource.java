package net.minecraft.util.random;

import net.minecraft.util.ThreadingDetector;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.concurrent.atomic.AtomicLong;

public class LegacyRandomSource
implements BitRandomSource {
    private static final int MODULUS_BITS = 48;
    private static final long MODULUS_MASK = 0xFFFFFFFFFFFFL;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private final AtomicLong seed = new AtomicLong();
    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    public LegacyRandomSource(long l) {
        this.setSeed(l);
    }

    @Override
    public RandomSource fork() {
        return new LegacyRandomSource(this.nextLong());
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return new LegacyPositionalRandomFactory(this.nextLong());
    }

    @Override
    public void setSeed(long l) {
        if (!this.seed.compareAndSet(this.seed.get(), (l ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL)) {
            throw ThreadingDetector.makeThreadingException("LegacyRandomSource", null);
        }
        this.gaussianSource.reset();
    }

    @Override
    public int next(int n) {
        long l;
        long l2 = this.seed.get();
        if (!this.seed.compareAndSet(l2, l = l2 * 25214903917L + 11L & 0xFFFFFFFFFFFFL)) {
            throw ThreadingDetector.makeThreadingException("LegacyRandomSource", null);
        }
        return (int)(l >> 48 - n);
    }

    @Override
    public double nextGaussian() {
        return this.gaussianSource.nextGaussian();
    }

    public static class LegacyPositionalRandomFactory
    implements PositionalRandomFactory {
        private final long seed;

        public LegacyPositionalRandomFactory(long l) {
            this.seed = l;
        }

        @Override
        public RandomSource at(int n, int n2, int n3) {
            long l = MathHelper.getSeed(n, n2, n3);
            long l2 = l ^ this.seed;
            return new LegacyRandomSource(l2);
        }

        @Override
        public RandomSource fromHashOf(String string) {
            int n = string.hashCode();
            return new LegacyRandomSource((long)n ^ this.seed);
        }

        @Override
        public RandomSource fromSeed(long l) {
            return new LegacyRandomSource(l);
        }

        @Override
        @VisibleForTesting
        public void parityConfigString(StringBuilder stringBuilder) {
            stringBuilder.append("LegacyPositionalRandomFactory{").append(this.seed).append("}");
        }
    }
}

