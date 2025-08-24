package net.minecraft.util.random;

import com.google.common.base.Charsets;
import com.google.common.primitives.Longs;
import org.jetbrains.annotations.VisibleForTesting;
import org.nd4j.shade.guava.hash.HashFunction;
import org.nd4j.shade.guava.hash.Hashing;

import java.util.concurrent.atomic.AtomicLong;

public final class RandomSupport {
    public static final long GOLDEN_RATIO_64 = -7046029254386353131L;
    public static final long SILVER_RATIO_64 = 7640891576956012809L;
    private static final HashFunction MD5_128 = Hashing.md5();
    private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

    @VisibleForTesting
    public static long mixStafford13(long l) {
        l = (l ^ l >>> 30) * -4658895280553007687L;
        l = (l ^ l >>> 27) * -7723592293110705685L;
        return l ^ l >>> 31;
    }

    public static Seed128bit upgradeSeedTo128bitUnmixed(long l) {
        long l2 = l ^ 0x6A09E667F3BCC909L;
        long l3 = l2 + -7046029254386353131L;
        return new Seed128bit(l2, l3);
    }

    public static Seed128bit upgradeSeedTo128bit(long l) {
        return RandomSupport.upgradeSeedTo128bitUnmixed(l).mixed();
    }

    public static Seed128bit seedFromHashOf(String string) {
        byte[] byArray = MD5_128.hashString((CharSequence)string, Charsets.UTF_8).asBytes();
        long l = Longs.fromBytes((byte)byArray[0], (byte)byArray[1], (byte)byArray[2], (byte)byArray[3], (byte)byArray[4], (byte)byArray[5], (byte)byArray[6], (byte)byArray[7]);
        long l2 = Longs.fromBytes((byte)byArray[8], (byte)byArray[9], (byte)byArray[10], (byte)byArray[11], (byte)byArray[12], (byte)byArray[13], (byte)byArray[14], (byte)byArray[15]);
        return new Seed128bit(l, l2);
    }

    public static long generateUniqueSeed() {
        return SEED_UNIQUIFIER.updateAndGet(l -> l * 1181783497276652981L) ^ System.nanoTime();
    }

    public record Seed128bit(long seedLo, long seedHi) {
        public Seed128bit xor(long l, long l2) {
            return new Seed128bit(this.seedLo ^ l, this.seedHi ^ l2);
        }

        public Seed128bit xor(Seed128bit seed128bit) {
            return this.xor(seed128bit.seedLo, seed128bit.seedHi);
        }

        public Seed128bit mixed() {
            return new Seed128bit(RandomSupport.mixStafford13(this.seedLo), RandomSupport.mixStafford13(this.seedHi));
        }
    }
}
