package net.minecraft.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.codec.StreamCodec;

public enum Unit {
   INSTANCE;

   public static final Codec<Unit> CODEC;
   public static final StreamCodec<ByteBuf, Unit> STREAM_CODEC;

   static {
      CODEC = Codec.unit(INSTANCE);
      STREAM_CODEC = StreamCodec.unit(INSTANCE);
   }
}