package net.minecraft.world.biome.newBiome;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;

import java.util.function.Function;

public interface ToFloatFunction<C> {
    public static final ToFloatFunction<Float> IDENTITY = ToFloatFunction.createUnlimited(f -> f);

    public float apply(C var1);

    public float minValue();

    public float maxValue();

    public static ToFloatFunction<Float> createUnlimited(final Float2FloatFunction float2FloatFunction) {
        return new ToFloatFunction<Float>(){

            @Override
            public float apply(Float f) {
                return ((Float)float2FloatFunction.apply(f)).floatValue();
            }

            @Override
            public float minValue() {
                return Float.NEGATIVE_INFINITY;
            }

            @Override
            public float maxValue() {
                return Float.POSITIVE_INFINITY;
            }
        };
    }

    default public <C2> ToFloatFunction<C2> comap(final Function<C2, C> function) {
        final ToFloatFunction toFloatFunction = this;
        return new ToFloatFunction<C2>(){

            @Override
            public float apply(C2 C22) {
                return toFloatFunction.apply(function.apply(C22));
            }

            @Override
            public float minValue() {
                return toFloatFunction.minValue();
            }

            @Override
            public float maxValue() {
                return toFloatFunction.maxValue();
            }
        };
    }
}

