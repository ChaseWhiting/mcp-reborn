/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package net.minecraft.util;


import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class ARGB {
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

    public static int color(int n, int n2, int n3) {
        return ARGB.color(255, n, n2, n3);
    }

    public static int color(Vector3d vec3) {
        return ARGB.color(ARGB.as8BitChannel((float)vec3.x()), ARGB.as8BitChannel((float)vec3.y()), ARGB.as8BitChannel((float)vec3.z()));
    }

    public static int multiply(int n, int n2) {
        if (n == -1) {
            return n2;
        }
        if (n2 == -1) {
            return n;
        }
        return ARGB.color(ARGB.alpha(n) * ARGB.alpha(n2) / 255, ARGB.red(n) * ARGB.red(n2) / 255, ARGB.green(n) * ARGB.green(n2) / 255, ARGB.blue(n) * ARGB.blue(n2) / 255);
    }

    public static int scaleRGB(int n, float f) {
        return ARGB.scaleRGB(n, f, f, f);
    }

    public static int scaleRGB(int n, float f, float f2, float f3) {
        return ARGB.color(ARGB.alpha(n), clamp((long)((int)((float)ARGB.red(n) * f)), 0, 255), clamp((long)((int)((float)ARGB.green(n) * f2)), 0, 255), clamp((long)((int)((float)ARGB.blue(n) * f3)), 0, 255));
    }

    public static int scaleRGB(int n, int n2) {
        return ARGB.color(ARGB.alpha(n), clamp((long)ARGB.red(n) * (long)n2 / 255L, 0, 255), clamp((long)ARGB.green(n) * (long)n2 / 255L, 0, 255), clamp((long)ARGB.blue(n) * (long)n2 / 255L, 0, 255));
    }

    public static int greyscale(int n) {
        int n2 = (int)((float)ARGB.red(n) * 0.3f + (float)ARGB.green(n) * 0.59f + (float)ARGB.blue(n) * 0.11f);
        return ARGB.color(n2, n2, n2);
    }

    public static int clamp(long value, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(min + " > " + max);
        }
        return (int) Math.min(max, Math.max(value, min));
    }

    public static int lerp(float f, int n, int n2) {
        int n3 = MathHelper.lerpInt(f, ARGB.alpha(n), ARGB.alpha(n2));
        int n4 = MathHelper.lerpInt(f, ARGB.red(n), ARGB.red(n2));
        int n5 = MathHelper.lerpInt(f, ARGB.green(n), ARGB.green(n2));
        int n6 = MathHelper.lerpInt(f, ARGB.blue(n), ARGB.blue(n2));
        return ARGB.color(n3, n4, n5, n6);
    }

    public static int opaque(int n) {
        return n | 0xFF000000;
    }

    public static int transparent(int n) {
        return n & 0xFFFFFF;
    }

    public static int color(int n, int n2) {
        return n << 24 | n2 & 0xFFFFFF;
    }

    public static int white(float f) {
        return ARGB.as8BitChannel(f) << 24 | 0xFFFFFF;
    }

    public static int colorFromFloat(float f, float f2, float f3, float f4) {
        return ARGB.color(ARGB.as8BitChannel(f), ARGB.as8BitChannel(f2), ARGB.as8BitChannel(f3), ARGB.as8BitChannel(f4));
    }

    public static Vector3f vector3fFromRGB24(int n) {
        float f = (float)ARGB.red(n) / 255.0f;
        float f2 = (float)ARGB.green(n) / 255.0f;
        float f3 = (float)ARGB.blue(n) / 255.0f;
        return new Vector3f(f, f2, f3);
    }

    public static int average(int n, int n2) {
        return ARGB.color((ARGB.alpha(n) + ARGB.alpha(n2)) / 2, (ARGB.red(n) + ARGB.red(n2)) / 2, (ARGB.green(n) + ARGB.green(n2)) / 2, (ARGB.blue(n) + ARGB.blue(n2)) / 2);
    }

    public static int as8BitChannel(float f) {
        return MathHelper.floor(f * 255.0f);
    }

    public static float alphaFloat(int n) {
        return ARGB.from8BitChannel(ARGB.alpha(n));
    }

    public static float redFloat(int n) {
        return ARGB.from8BitChannel(ARGB.red(n));
    }

    public static float greenFloat(int n) {
        return ARGB.from8BitChannel(ARGB.green(n));
    }

    public static float blueFloat(int n) {
        return ARGB.from8BitChannel(ARGB.blue(n));
    }

    private static float from8BitChannel(int n) {
        return (float)n / 255.0f;
    }

    public static int toABGR(int n) {
        return n & 0xFF00FF00 | (n & 0xFF0000) >> 16 | (n & 0xFF) << 16;
    }

    public static int fromABGR(int n) {
        return ARGB.toABGR(n);
    }
}

