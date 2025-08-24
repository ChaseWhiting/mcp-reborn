package net.minecraft.util.codec;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.RegistryPacketBuffer;
import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryOps;

import java.util.Optional;
import java.util.function.*;

public class ByteBufCodecs {

    public static final StreamCodec<ByteBuf, Boolean> BOOL = new StreamCodec<ByteBuf, Boolean>(){

        @Override
        public Boolean decode(ByteBuf byteBuf) {
            return byteBuf.readBoolean();
        }

        @Override
        public void encode(ByteBuf byteBuf, Boolean bl) {
            byteBuf.writeBoolean(bl.booleanValue());
        }

    };

    public static <T> StreamCodec<RegistryPacketBuffer, T> holderRegistry(RegistryKey<? extends Registry<T>> resourceKey) {
        return ByteBufCodecs.registry(resourceKey, Registry::asHolderIdMap);
    }

    public static <B extends ByteBuf, V> StreamCodec<B, Optional<V>> optional(final StreamCodec<B, V> streamCodec) {
        return new StreamCodec<B, Optional<V>>(){

            @Override
            public Optional<V> decode(B b) {
                if (b.readBoolean()) {
                    return Optional.of(streamCodec.decode(b));
                }
                return Optional.empty();
            }

            @Override
            public void encode(B b, Optional<V> optional) {
                if (optional.isPresent()) {
                    b.writeBoolean(true);
                    streamCodec.encode(b, optional.get());
                } else {
                    b.writeBoolean(false);
                }
            }


        };
    }


    public static <T> StreamCodec<ByteBuf, T> idMapper(final IntFunction<T> intFunction, final ToIntFunction<T> toIntFunction) {
        return new StreamCodec<ByteBuf, T>(){

            @Override
            public T decode(ByteBuf byteBuf) {
                int n = VarInt.read(byteBuf);
                return intFunction.apply(n);
            }

            @Override
            public void encode(ByteBuf byteBuf, T t) {
                int n = toIntFunction.applyAsInt(t);
                VarInt.write(byteBuf, n);
            }

        };
    }

    public static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, V> lengthPrefixed(final int n, final BiFunction<B, ByteBuf, B> biFunction) {
        return streamCodec -> new StreamCodec<B, V>(){

            @Override
            public V decode(B b) {
                int n3 = VarInt.read(b);
                if (n3 > n) {
                    throw new DecoderException("Buffer size " + n3 + " is larger than allowed limit of " + n);
                }
                int n2 = b.readerIndex();
                ByteBuf byteBuf = (ByteBuf)biFunction.apply(b, b.slice(n2, n3));
                b.readerIndex(n2 + n3);
                return streamCodec.decode((B) byteBuf);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void encode(B b, V v) {
                ByteBuf byteBuf = biFunction.apply(b, b.alloc().buffer());
                try {
                    streamCodec.encode((B) byteBuf, v);
                    int n2 = byteBuf.readableBytes();
                    if (n2 > n) {
                        throw new EncoderException("Buffer size " + n2 + " is  larger than allowed limit of " + n);
                    }
                    VarInt.write(b, n2);
                    b.writeBytes(byteBuf);
                }
                finally {
                    byteBuf.release();
                }
            }
        };
    }

    public static <V> StreamCodec.CodecOperation<ByteBuf, V, V> lengthPrefixed(int n) {
        return ByteBufCodecs.lengthPrefixed(n, (byteBuf, byteBuf2) -> byteBuf2);
    }

    public static <V> StreamCodec.CodecOperation<RegistryPacketBuffer, V, V> registryFriendlyLengthPrefixed(int n) {
        return ByteBufCodecs.lengthPrefixed(n, (registryFriendlyByteBuf, byteBuf) -> new RegistryPacketBuffer((ByteBuf)byteBuf, registryFriendlyByteBuf.registryAccess()));
    }

    public static final StreamCodec<ByteBuf, Integer> VAR_INT = new StreamCodec<ByteBuf, Integer>(){

        @Override
        public Integer decode(ByteBuf byteBuf) {
            return VarInt.read(byteBuf);
        }

        @Override
        public void encode(ByteBuf byteBuf, Integer n) {
            VarInt.write(byteBuf, n);
        }

    };

    private static <T, R> StreamCodec<RegistryPacketBuffer, R> registry(final RegistryKey<? extends Registry<T>> resourceKey, final Function<Registry<T>, IObjectIntIterable<R>> function) {
        return new StreamCodec<RegistryPacketBuffer, R>(){

            private IObjectIntIterable<R> getRegistryOrThrow(RegistryPacketBuffer registryFriendlyByteBuf) {
                return function.apply(registryFriendlyByteBuf.registryAccess().registryOrThrow(resourceKey));
            }

            @Override
            public R decode(RegistryPacketBuffer registryFriendlyByteBuf) {
                int n = VarInt.read(registryFriendlyByteBuf);
                return this.getRegistryOrThrow(registryFriendlyByteBuf).byIdOrThrow(n);
            }

            @Override
            public void encode(RegistryPacketBuffer registryFriendlyByteBuf, R r) {
                int n = this.getRegistryOrThrow(registryFriendlyByteBuf).getIdOrThrow(r);
                VarInt.write(registryFriendlyByteBuf, n);
            }

        };
    }

    public static <T> StreamCodec<RegistryPacketBuffer, T> registry(RegistryKey<? extends Registry<T>> resourceKey) {
        return ByteBufCodecs.registry(resourceKey, registry -> registry);
    }




    public static <T> StreamCodec<RegistryPacketBuffer, T> fromCodecWithRegistries(Codec<T> codec) {
        return fromCodecWithRegistries(codec, () -> new NBTSizeTracker(0x200000L));
    }

    public static <T> StreamCodec<RegistryPacketBuffer, T> fromCodecWithRegistries(final Codec<T> codec, Supplier<NBTSizeTracker> supplier) {
        final StreamCodec<ByteBuf, INBT> tagCodec = tagCodec(supplier);

        return new StreamCodec<RegistryPacketBuffer, T>() {
            @Override
            public T decode(RegistryPacketBuffer buffer) {
                INBT tag = tagCodec.decode(buffer);
                RegistryOps<INBT> ops = buffer.registryAccess().createSerializationContext(NBTDynamicOps.INSTANCE);
                return codec.parse(ops, tag).getOrThrow(false, error -> new DecoderException("Failed to decode: " + error + " " + tag));
            }

            @Override
            public void encode(RegistryPacketBuffer buffer, T value) {
                RegistryOps<INBT> ops = buffer.registryAccess().createSerializationContext(NBTDynamicOps.INSTANCE);
                INBT tag = codec.encodeStart(ops, value).getOrThrow(false, error -> new EncoderException("Failed to encode: " + error + " " + value));
                tagCodec.encode(buffer, tag);
            }
        };
    }

    public static StreamCodec<ByteBuf, INBT> tagCodec(final Supplier<NBTSizeTracker> supplier) {
        return new StreamCodec<ByteBuf, INBT>() {
            @Override
            public INBT decode(ByteBuf byteBuf) {
                INBT tag = PacketBuffer.readNbt(byteBuf, supplier.get());
                if (tag == null) {
                    throw new DecoderException("Expected non-null tag");
                }
                return tag;
            }

            @Override
            public void encode(ByteBuf byteBuf, INBT tag) {
                if (tag == EndNBT.INSTANCE) {
                    throw new EncoderException("Cannot encode EndTag");
                }
                PacketBuffer.writeNbt(byteBuf, tag);
            }
        };
    }


}
