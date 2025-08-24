package net.minecraft.world.biome.newBiome.climate.densityFunctions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record KeyDispatchDataCodec<A>(Codec<A> codec) {
    @Deprecated
    public static <A> KeyDispatchDataCodec<A> of(Codec<A> codec) {
        return new KeyDispatchDataCodec<A>(codec);
    }

    public static <A> KeyDispatchDataCodec<A> of(MapCodec<A> mapCodec) {
        return new KeyDispatchDataCodec<A>(mapCodec.codec());
    }
}

