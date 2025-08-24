package net.minecraft.util.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.RegistryPacketBuffer;
import net.minecraft.util.codec.StreamCodec;

import java.util.Map;

public record TypedDataComponent<T>(DataComponentType<T> type, T value) {
    public static final StreamCodec<RegistryPacketBuffer, TypedDataComponent<?>> STREAM_CODEC = new StreamCodec<RegistryPacketBuffer, TypedDataComponent<?>>(){

        @Override
        public TypedDataComponent<?> decode(RegistryPacketBuffer registryFriendlyByteBuf) {
            DataComponentType dataComponentType = (DataComponentType)DataComponentType.STREAM_CODEC.decode(registryFriendlyByteBuf);
            return decodeTyped(registryFriendlyByteBuf, dataComponentType);
        }

        private static <T> TypedDataComponent<T> decodeTyped(RegistryPacketBuffer registryFriendlyByteBuf, DataComponentType<T> dataComponentType) {
            return new TypedDataComponent<T>(dataComponentType, dataComponentType.streamCodec().decode(registryFriendlyByteBuf));
        }

        @Override
        public void encode(RegistryPacketBuffer registryFriendlyByteBuf, TypedDataComponent<?> typedDataComponent) {
            encodeCap(registryFriendlyByteBuf, typedDataComponent);
        }

        private static <T> void encodeCap(RegistryPacketBuffer registryFriendlyByteBuf, TypedDataComponent<T> typedDataComponent) {
            DataComponentType.STREAM_CODEC.encode(registryFriendlyByteBuf, typedDataComponent.type());
            typedDataComponent.type().streamCodec().encode(registryFriendlyByteBuf, typedDataComponent.value());
        }
    };

    static TypedDataComponent<?> fromEntryUnchecked(Map.Entry<DataComponentType<?>, Object> entry) {
        return TypedDataComponent.createUnchecked(entry.getKey(), entry.getValue());
    }


    public static <T> TypedDataComponent<T> createUnchecked(DataComponentType<T> dataComponentType, Object object) {
        return new TypedDataComponent<>(dataComponentType, (T) object);
    }


    public void applyTo(PatchedDataComponentMap patchedDataComponentMap) {
        patchedDataComponentMap.set(this.type, this.value);
    }

    public <D> DataResult<D> encodeValue(DynamicOps<D> dynamicOps) {
        Codec<T> codec = this.type.codec();
        if (codec == null) {
            return DataResult.error("Component of type " + String.valueOf(this.type) + " is not encodable");
        }
        return codec.encodeStart(dynamicOps, this.value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.type) + "=>" + String.valueOf(this.value);
    }
}