package net.minecraft.util.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryPacketBuffer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.util.codec.ByteBufCodecs;
import net.minecraft.util.codec.StreamCodec;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public interface DataComponentType<T> {
    public static final Codec<DataComponentType<?>> CODEC = ExtraCodecs.lazyInitialized(Registry.DATA_COMPONENT_TYPE::byNameCodec);
    public static final StreamCodec<RegistryPacketBuffer, DataComponentType<?>> STREAM_CODEC =
            StreamCodec.<RegistryPacketBuffer, DataComponentType<?>>recursive(
                    codec -> ByteBufCodecs.registry(Registry.DATA_COMPONENT_TYPE_REGISTRY)
            );
    public static final Codec<DataComponentType<?>> PERSISTENT_CODEC = ExtraCodecs.validate(CODEC, dataComponentType -> dataComponentType.isTransient() ? DataResult.error("Encountered transient component " + Registry.DATA_COMPONENT_TYPE.getKey(dataComponentType)) : DataResult.success(dataComponentType));
    public static final Codec<Map<DataComponentType<?>, Object>> VALUE_MAP_CODEC = ExtraCodecs.dispatchedMap(PERSISTENT_CODEC, DataComponentType::codecOrThrow);

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    @Nullable
    public Codec<T> codec();

    default public Codec<T> codecOrThrow() {
        Codec<T> codec = this.codec();
        if (codec == null) {
            throw new IllegalStateException(this + " is not a persistent component");
        }
        return codec;
    }

    default public boolean isTransient() {
        return this.codec() == null;
    }

    public StreamCodec<? super RegistryPacketBuffer, T> streamCodec();

    public static class Builder<T> {
        @Nullable
        private Codec<T> codec;
        @Nullable
        private StreamCodec<? super RegistryPacketBuffer, T> streamCodec;
        private boolean cacheEncoding;

        public Builder<T> persistent(Codec<T> codec) {
            this.codec = codec;
            return this;
        }

        public Builder<T> networkSynchronized(StreamCodec<? super RegistryPacketBuffer, T> streamCodec) {
            this.streamCodec = streamCodec;
            return this;
        }

        public Builder<T> cacheEncoding() {
            this.cacheEncoding = true;
            return this;
        }

        public DataComponentType<T> build() {
            StreamCodec streamCodec = Objects.requireNonNullElseGet(this.streamCodec, () -> ByteBufCodecs.fromCodecWithRegistries(Objects.requireNonNull(this.codec, "Missing Codec for component")));
            Codec<T> codec = this.cacheEncoding && this.codec != null ? DataComponents.ENCODER_CACHE.wrap(this.codec) : this.codec;
            return new SimpleType<T>(codec, streamCodec);
        }

        record SimpleType<T>(@Nullable Codec<T> codec, StreamCodec<? super RegistryPacketBuffer, T> streamCodec)
                implements DataComponentType<T> {

            public String toString() {
                        return Util.getRegisteredName(Registry.DATA_COMPONENT_TYPE, this);
                    }
                }
    }
}