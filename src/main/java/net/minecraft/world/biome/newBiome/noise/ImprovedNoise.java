package net.minecraft.world.biome.newBiome.noise;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomSource;
import org.jetbrains.annotations.VisibleForTesting;

public final class ImprovedNoise {
    private static final float SHIFT_UP_EPSILON = 1.0E-7f;
    private final byte[] p;
    public final double xo;
    public final double yo;
    public final double zo;

    public ImprovedNoise(RandomSource randomSource) {
        int n;
        this.xo = randomSource.nextDouble() * 256.0;
        this.yo = randomSource.nextDouble() * 256.0;
        this.zo = randomSource.nextDouble() * 256.0;
        this.p = new byte[256];
        for (n = 0; n < 256; ++n) {
            this.p[n] = (byte)n;
        }
        for (n = 0; n < 256; ++n) {
            int n2 = randomSource.nextInt(256 - n);
            byte by = this.p[n];
            this.p[n] = this.p[n + n2];
            this.p[n + n2] = by;
        }
    }

    public double noise(double d, double d2, double d3) {
        return this.noise(d, d2, d3, 0.0, 0.0);
    }

    @Deprecated
    public double noise(double d, double d2, double d3, double d4, double d5) {
        double d6;
        double d7 = d + this.xo;
        double d8 = d2 + this.yo;
        double d9 = d3 + this.zo;
        int n = MathHelper.floor(d7);
        int n2 = MathHelper.floor(d8);
        int n3 = MathHelper.floor(d9);
        double d10 = d7 - (double)n;
        double d11 = d8 - (double)n2;
        double d12 = d9 - (double)n3;
        if (d4 != 0.0) {
            double d13 = d5 >= 0.0 && d5 < d11 ? d5 : d11;
            d6 = (double)MathHelper.floor(d13 / d4 + (double)SHIFT_UP_EPSILON) * d4;
        } else {
            d6 = 0.0;
        }
        return this.sampleAndLerp(n, n2, n3, d10, d11 - d6, d12, d11);
    }

    public double noiseWithDerivative(double d, double d2, double d3, double[] dArray) {
        double d4 = d + this.xo;
        double d5 = d2 + this.yo;
        double d6 = d3 + this.zo;
        int n = MathHelper.floor(d4);
        int n2 = MathHelper.floor(d5);
        int n3 = MathHelper.floor(d6);
        double d7 = d4 - (double)n;
        double d8 = d5 - (double)n2;
        double d9 = d6 - (double)n3;
        return this.sampleWithDerivative(n, n2, n3, d7, d8, d9, dArray);
    }

    private static double gradDot(int n, double d, double d2, double d3) {
        return SimplexNoise.dot(SimplexNoise.GRADIENT[n & 0xF], d, d2, d3);
    }

    private int p(int n) {
        return this.p[n & 0xFF] & 0xFF;
    }

    private double sampleAndLerp(int n, int n2, int n3, double d, double d2, double d3, double d4) {
        int n4 = this.p(n);
        int n5 = this.p(n + 1);
        int n6 = this.p(n4 + n2);
        int n7 = this.p(n4 + n2 + 1);
        int n8 = this.p(n5 + n2);
        int n9 = this.p(n5 + n2 + 1);
        double d5 = ImprovedNoise.gradDot(this.p(n6 + n3), d, d2, d3);
        double d6 = ImprovedNoise.gradDot(this.p(n8 + n3), d - 1.0, d2, d3);
        double d7 = ImprovedNoise.gradDot(this.p(n7 + n3), d, d2 - 1.0, d3);
        double d8 = ImprovedNoise.gradDot(this.p(n9 + n3), d - 1.0, d2 - 1.0, d3);
        double d9 = ImprovedNoise.gradDot(this.p(n6 + n3 + 1), d, d2, d3 - 1.0);
        double d10 = ImprovedNoise.gradDot(this.p(n8 + n3 + 1), d - 1.0, d2, d3 - 1.0);
        double d11 = ImprovedNoise.gradDot(this.p(n7 + n3 + 1), d, d2 - 1.0, d3 - 1.0);
        double d12 = ImprovedNoise.gradDot(this.p(n9 + n3 + 1), d - 1.0, d2 - 1.0, d3 - 1.0);
        double d13 = MathHelper.smoothstep(d);
        double d14 = MathHelper.smoothstep(d4);
        double d15 = MathHelper.smoothstep(d3);
        return MathHelper.lerp3(d13, d14, d15, d5, d6, d7, d8, d9, d10, d11, d12);
    }

    private double sampleWithDerivative(int n, int n2, int n3, double d, double d2, double d3, double[] dArray) {
        int n4 = this.p(n);
        int n5 = this.p(n + 1);
        int n6 = this.p(n4 + n2);
        int n7 = this.p(n4 + n2 + 1);
        int n8 = this.p(n5 + n2);
        int n9 = this.p(n5 + n2 + 1);
        int n10 = this.p(n6 + n3);
        int n11 = this.p(n8 + n3);
        int n12 = this.p(n7 + n3);
        int n13 = this.p(n9 + n3);
        int n14 = this.p(n6 + n3 + 1);
        int n15 = this.p(n8 + n3 + 1);
        int n16 = this.p(n7 + n3 + 1);
        int n17 = this.p(n9 + n3 + 1);
        int[] nArray = SimplexNoise.GRADIENT[n10 & 0xF];
        int[] nArray2 = SimplexNoise.GRADIENT[n11 & 0xF];
        int[] nArray3 = SimplexNoise.GRADIENT[n12 & 0xF];
        int[] nArray4 = SimplexNoise.GRADIENT[n13 & 0xF];
        int[] nArray5 = SimplexNoise.GRADIENT[n14 & 0xF];
        int[] nArray6 = SimplexNoise.GRADIENT[n15 & 0xF];
        int[] nArray7 = SimplexNoise.GRADIENT[n16 & 0xF];
        int[] nArray8 = SimplexNoise.GRADIENT[n17 & 0xF];
        double d4 = SimplexNoise.dot(nArray, d, d2, d3);
        double d5 = SimplexNoise.dot(nArray2, d - 1.0, d2, d3);
        double d6 = SimplexNoise.dot(nArray3, d, d2 - 1.0, d3);
        double d7 = SimplexNoise.dot(nArray4, d - 1.0, d2 - 1.0, d3);
        double d8 = SimplexNoise.dot(nArray5, d, d2, d3 - 1.0);
        double d9 = SimplexNoise.dot(nArray6, d - 1.0, d2, d3 - 1.0);
        double d10 = SimplexNoise.dot(nArray7, d, d2 - 1.0, d3 - 1.0);
        double d11 = SimplexNoise.dot(nArray8, d - 1.0, d2 - 1.0, d3 - 1.0);
        double d12 = MathHelper.smoothstep(d);
        double d13 = MathHelper.smoothstep(d2);
        double d14 = MathHelper.smoothstep(d3);
        double d15 = MathHelper.lerp3(d12, d13, d14, nArray[0], nArray2[0], nArray3[0], nArray4[0], nArray5[0], nArray6[0], nArray7[0], nArray8[0]);
        double d16 = MathHelper.lerp3(d12, d13, d14, nArray[1], nArray2[1], nArray3[1], nArray4[1], nArray5[1], nArray6[1], nArray7[1], nArray8[1]);
        double d17 = MathHelper.lerp3(d12, d13, d14, nArray[2], nArray2[2], nArray3[2], nArray4[2], nArray5[2], nArray6[2], nArray7[2], nArray8[2]);
        double d18 = MathHelper.lerp2(d13, d14, d5 - d4, d7 - d6, d9 - d8, d11 - d10);
        double d19 = MathHelper.lerp2(d14, d12, d6 - d4, d10 - d8, d7 - d5, d11 - d9);
        double d20 = MathHelper.lerp2(d12, d13, d8 - d4, d9 - d5, d10 - d6, d11 - d7);
        double d21 = MathHelper.smoothstepDerivative(d);
        double d22 = MathHelper.smoothstepDerivative(d2);
        double d23 = MathHelper.smoothstepDerivative(d3);
        double d24 = d15 + d21 * d18;
        double d25 = d16 + d22 * d19;
        double d26 = d17 + d23 * d20;
        dArray[0] = dArray[0] + d24;
        dArray[1] = dArray[1] + d25;
        dArray[2] = dArray[2] + d26;
        return MathHelper.lerp3(d12, d13, d14, d4, d5, d6, d7, d8, d9, d10, d11);
    }

    @VisibleForTesting
    public void parityConfigString(StringBuilder stringBuilder) {
        NoiseUtils.parityNoiseOctaveConfigString(stringBuilder, this.xo, this.yo, this.zo, this.p);
    }
}

