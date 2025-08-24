
package net.minecraft.util.component;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.network.RegistryPacketBuffer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.codec.ByteBufCodecs;
import net.minecraft.util.codec.StreamCodec;
import net.minecraft.util.registry.Registry;

public final class DataComponentPatch {
    public static final DataComponentPatch EMPTY = new DataComponentPatch(Reference2ObjectMaps.emptyMap());
    public static final Codec<DataComponentPatch> CODEC = ExtraCodecs.dispatchedMap(PatchKey.CODEC, PatchKey::valueCodec).xmap(map -> {
        if (map.isEmpty()) {
            return EMPTY;
        }
        Reference2ObjectArrayMap reference2ObjectArrayMap = new Reference2ObjectArrayMap(map.size());
        for (Map.Entry entry : map.entrySet()) {
            PatchKey patchKey = (PatchKey)entry.getKey();
            if (patchKey.removed()) {
                reference2ObjectArrayMap.put(patchKey.type(), Optional.empty());
                continue;
            }
            reference2ObjectArrayMap.put(patchKey.type(), Optional.of(entry.getValue()));
        }
        return new DataComponentPatch((Reference2ObjectMap<DataComponentType<?>, Optional<?>>)reference2ObjectArrayMap);
    }, dataComponentPatch -> {
        Reference2ObjectArrayMap reference2ObjectArrayMap = new Reference2ObjectArrayMap(dataComponentPatch.map.size());
        for (Map.Entry entry : Reference2ObjectMaps.fastIterable(dataComponentPatch.map)) {
            DataComponentType dataComponentType = (DataComponentType)entry.getKey();
            if (dataComponentType.isTransient()) continue;
            Optional optional = (Optional)entry.getValue();
            if (optional.isPresent()) {
                reference2ObjectArrayMap.put((Object)new PatchKey(dataComponentType, false), optional.get());
                continue;
            }
            reference2ObjectArrayMap.put((Object)new PatchKey(dataComponentType, true), (Object)Unit.INSTANCE);
        }
        return reference2ObjectArrayMap;
    });
    public static final StreamCodec<RegistryPacketBuffer, DataComponentPatch> STREAM_CODEC = DataComponentPatch.createStreamCodec(new CodecGetter(){

        public <T> StreamCodec<RegistryPacketBuffer, T> apply(DataComponentType<T> dataComponentType) {
            return dataComponentType.streamCodec().cast();
        }
    });
    public static final StreamCodec<RegistryPacketBuffer, DataComponentPatch> DELIMITED_STREAM_CODEC = DataComponentPatch.createStreamCodec(new CodecGetter(){

        public <T> StreamCodec<RegistryPacketBuffer, T> apply(DataComponentType<T> dataComponentType) {
            StreamCodec streamCodec = dataComponentType.streamCodec().cast();
            return streamCodec.apply(ByteBufCodecs.registryFriendlyLengthPrefixed(Integer.MAX_VALUE));
        }
    });
    private static final String REMOVED_PREFIX = "!";
    final Reference2ObjectMap<DataComponentType<?>, Optional<?>> map;

    private static StreamCodec<RegistryPacketBuffer, DataComponentPatch> createStreamCodec(final CodecGetter codecGetter) {
        return new StreamCodec<RegistryPacketBuffer, DataComponentPatch>(){

            @Override
            public DataComponentPatch decode(RegistryPacketBuffer registryFriendlyByteBuf) {
                DataComponentType dataComponentType;
                int n;
                int n2 = registryFriendlyByteBuf.readVarInt();
                int n3 = registryFriendlyByteBuf.readVarInt();
                if (n2 == 0 && n3 == 0) {
                    return EMPTY;
                }
                int n4 = n2 + n3;
                Reference2ObjectArrayMap reference2ObjectArrayMap = new Reference2ObjectArrayMap(Math.min(n4, 65536));
                for (n = 0; n < n2; ++n) {
                    dataComponentType = (DataComponentType)DataComponentType.STREAM_CODEC.decode(registryFriendlyByteBuf);
                    Object t = codecGetter.apply(dataComponentType).decode(registryFriendlyByteBuf);
                    reference2ObjectArrayMap.put((Object)dataComponentType, Optional.of(t));
                }
                for (n = 0; n < n3; ++n) {
                    dataComponentType = (DataComponentType)DataComponentType.STREAM_CODEC.decode(registryFriendlyByteBuf);
                    reference2ObjectArrayMap.put((Object)dataComponentType, Optional.empty());
                }
                return new DataComponentPatch((Reference2ObjectMap<DataComponentType<?>, Optional<?>>)reference2ObjectArrayMap);
            }

            @Override
            public void encode(RegistryPacketBuffer registryFriendlyByteBuf, DataComponentPatch dataComponentPatch) {
                Object object;
                if (dataComponentPatch.isEmpty()) {
                    registryFriendlyByteBuf.writeVarInt(0);
                    registryFriendlyByteBuf.writeVarInt(0);
                    return;
                }
                int n = 0;
                int n2 = 0;
                for (Reference2ObjectMap.Entry entry : Reference2ObjectMaps.fastIterable(dataComponentPatch.map)) {
                    if (((Optional)entry.getValue()).isPresent()) {
                        ++n;
                        continue;
                    }
                    ++n2;
                }
                registryFriendlyByteBuf.writeVarInt(n);
                registryFriendlyByteBuf.writeVarInt(n2);
                for (Reference2ObjectMap.Entry entry : Reference2ObjectMaps.fastIterable(dataComponentPatch.map)) {
                    object = (Optional)entry.getValue();
                    if (!((Optional)object).isPresent()) continue;
                    DataComponentType dataComponentType = (DataComponentType)entry.getKey();
                    DataComponentType.STREAM_CODEC.encode(registryFriendlyByteBuf, dataComponentType);
                    this.encodeComponent(registryFriendlyByteBuf, dataComponentType, ((Optional)object).get());
                }
                for (Reference2ObjectMap.Entry entry : Reference2ObjectMaps.fastIterable(dataComponentPatch.map)) {
                    if (!((Optional)entry.getValue()).isEmpty()) continue;
                    object = (DataComponentType)entry.getKey();
                    DataComponentType.STREAM_CODEC.encode(registryFriendlyByteBuf, (DataComponentType<?>) object);
                }
            }

            private <T> void encodeComponent(RegistryPacketBuffer registryFriendlyByteBuf, DataComponentType<T> dataComponentType, Object object) {
                codecGetter.apply(dataComponentType).encode(registryFriendlyByteBuf, (T) object);
            }
        };
    }

    DataComponentPatch(Reference2ObjectMap<DataComponentType<?>, Optional<?>> reference2ObjectMap) {
        this.map = reference2ObjectMap;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    public <T> Optional<? extends T> get(DataComponentType<? extends T> dataComponentType) {
        return (Optional)this.map.get(dataComponentType);
    }

    public Set<Map.Entry<DataComponentType<?>, Optional<?>>> entrySet() {
        return this.map.entrySet();
    }

    public int size() {
        return this.map.size();
    }

    public DataComponentPatch forget(Predicate<DataComponentType<?>> predicate) {
        if (this.isEmpty()) {
            return EMPTY;
        }
        Reference2ObjectArrayMap reference2ObjectArrayMap = new Reference2ObjectArrayMap(this.map);
        reference2ObjectArrayMap.keySet().removeIf(predicate);
        if (reference2ObjectArrayMap.isEmpty()) {
            return EMPTY;
        }
        return new DataComponentPatch((Reference2ObjectMap<DataComponentType<?>, Optional<?>>)reference2ObjectArrayMap);
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public SplitResult split() {
        if (this.isEmpty()) {
            return SplitResult.EMPTY;
        }
        DataComponentMap.Builder builder = DataComponentMap.builder();
        Set set = Sets.newIdentityHashSet();
        this.map.forEach((dataComponentType, optional) -> {
            if (optional.isPresent()) {
                builder.setUnchecked(dataComponentType, optional.get());
            } else {
                set.add(dataComponentType);
            }
        });
        return new SplitResult(builder.build(), set);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DataComponentPatch)) return false;
        DataComponentPatch dataComponentPatch = (DataComponentPatch)object;
        if (!this.map.equals(dataComponentPatch.map)) return false;
        return true;
    }

    public int hashCode() {
        return this.map.hashCode();
    }

    public String toString() {
        return DataComponentPatch.toString(this.map);
    }

    static String toString(Reference2ObjectMap<DataComponentType<?>, Optional<?>> reference2ObjectMap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('{');
        boolean bl = true;
        for (Map.Entry entry : Reference2ObjectMaps.fastIterable(reference2ObjectMap)) {
            if (bl) {
                bl = false;
            } else {
                stringBuilder.append(", ");
            }
            Optional optional = (Optional)entry.getValue();
            if (optional.isPresent()) {
                stringBuilder.append(entry.getKey());
                stringBuilder.append("=>");
                stringBuilder.append(optional.get());
                continue;
            }
            stringBuilder.append(REMOVED_PREFIX);
            stringBuilder.append(entry.getKey());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @FunctionalInterface
    static interface CodecGetter {
        public <T> StreamCodec<? super RegistryPacketBuffer, T> apply(DataComponentType<T> var1);
    }

    public static class Builder {
        private final Reference2ObjectMap<DataComponentType<?>, Optional<?>> map = new Reference2ObjectArrayMap();

        Builder() {
        }

        public <T> Builder set(DataComponentType<T> dataComponentType, T t) {
            this.map.put(dataComponentType, Optional.of(t));
            return this;
        }

        public <T> Builder remove(DataComponentType<T> dataComponentType) {
            this.map.put(dataComponentType, Optional.empty());
            return this;
        }

        public <T> Builder set(TypedDataComponent<T> typedDataComponent) {
            return this.set(typedDataComponent.type(), typedDataComponent.value());
        }

        public DataComponentPatch build() {
            if (this.map.isEmpty()) {
                return EMPTY;
            }
            return new DataComponentPatch(this.map);
        }
    }

    public record SplitResult(DataComponentMap added, Set<DataComponentType<?>> removed) {
        public static final SplitResult EMPTY = new SplitResult(DataComponentMap.EMPTY, Set.of());
    }

    record PatchKey(DataComponentType<?> type, boolean removed) {
        public static final Codec<PatchKey> CODEC = Codec.STRING.flatXmap(string -> {
            ResourceLocation resourceLocation;
            DataComponentType<?> dataComponentType;
            boolean bl = string.startsWith(DataComponentPatch.REMOVED_PREFIX);
            if (bl) {
                string = string.substring(DataComponentPatch.REMOVED_PREFIX.length());
            }
            if ((dataComponentType = Registry.DATA_COMPONENT_TYPE.get(resourceLocation = ResourceLocation.tryParse(string))) == null) {
                return DataResult.error("No component with type: '" + String.valueOf(resourceLocation) + "'");
            }
            if (dataComponentType.isTransient()) {
                return DataResult.error("'" + String.valueOf(resourceLocation) + "' is not a persistent component");
            }
            return DataResult.success(new PatchKey(dataComponentType, bl));
        }, patchKey -> {
            DataComponentType<?> dataComponentType = patchKey.type();
            ResourceLocation resourceLocation = Registry.DATA_COMPONENT_TYPE.getKey(dataComponentType);
            if (resourceLocation == null) {
                return DataResult.error("Unregistered component: " + String.valueOf(dataComponentType));
            }
            return DataResult.success((patchKey.removed() ? DataComponentPatch.REMOVED_PREFIX + String.valueOf(resourceLocation) : resourceLocation.toString()));
        });

        public Codec<?> valueCodec() {
            return this.removed ? Codec.EMPTY.codec() : this.type.codecOrThrow();
        }
    }
}

