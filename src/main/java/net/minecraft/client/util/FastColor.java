package net.minecraft.client.util;

import net.minecraft.util.math.MathHelper;

public class FastColor {

    public static class ABGR32 {
        public static int alpha(int n) {
            return n >>> 24;
        }

        public static int red(int n) {
            return n & 0xFF;
        }

        public static int green(int n) {
            return n >> 8 & 0xFF;
        }

        public static int blue(int n) {
            return n >> 16 & 0xFF;
        }

        public static int transparent(int n) {
            return n & 0xFFFFFF;
        }

        public static int opaque(int n) {
            return n | 0xFF000000;
        }

        public static int color(int n, int n2, int n3, int n4) {
            return n << 24 | n2 << 16 | n3 << 8 | n4;
        }

        public static int color(int n, int n2) {
            return n << 24 | n2 & 0xFFFFFF;
        }
    }

    public static class ARGB32 {
        public static int alpha(int n) {
            return n >>> 24;
        }

        public static int red(int n) {
            return n >> 16 & 0xFF;
        }

        public static int green(int n) {
            return n >> 8 & 0xFF;
        }

        public static int blue(int n) {
            return n & 0xFF;
        }

        public static int color(int n, int n2, int n3, int n4) {
            return n << 24 | n2 << 16 | n3 << 8 | n4;
        }

        public static int multiply(int n, int n2) {
            return ARGB32.color(ARGB32.alpha(n) * ARGB32.alpha(n2) / 255, ARGB32.red(n) * ARGB32.red(n2) / 255, ARGB32.green(n) * ARGB32.green(n2) / 255, ARGB32.blue(n) * ARGB32.blue(n2) / 255);
        }

        public static int lerp(float f, int n, int n2) {
            int n3 = MathHelper.lerpInt(f, ARGB32.alpha(n), ARGB32.alpha(n2));
            int n4 = MathHelper.lerpInt(f, ARGB32.red(n), ARGB32.red(n2));
            int n5 = MathHelper.lerpInt(f, ARGB32.green(n), ARGB32.green(n2));
            int n6 = MathHelper.lerpInt(f, ARGB32.blue(n), ARGB32.blue(n2));
            return ARGB32.color(n3, n4, n5, n6);
        }
    }
}