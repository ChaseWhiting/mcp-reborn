
package net.minecraft.entity.warden.event.position;

import com.mojang.serialization.Codec;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Objects;


public interface PositionSourceType<T extends PositionSource> {
    public static final PositionSourceType<BlockPositionSource> BLOCK = PositionSourceType.register("block", new BlockPositionSource.Type());
    public static final PositionSourceType<EntityPositionSource> ENTITY = PositionSourceType.register("entity", new EntityPositionSource.Type());

    public T read(PacketBuffer buffer);

    public void write(PacketBuffer buffer, T pos);

    public Codec<T> codec();

    public static <S extends PositionSourceType<T>, T extends PositionSource> S register(String string, S s) {
        return (S) Registry.register(Registry.POSITION_SOURCE_TYPE, string, s);
    }

    public static PositionSource fromNetwork(PacketBuffer friendlyByteBuf) {
        ResourceLocation resourceLocation = friendlyByteBuf.readResourceLocation();
        return Registry.POSITION_SOURCE_TYPE.getOptional(resourceLocation).orElseThrow(() -> new IllegalArgumentException("Unknown position source type " + resourceLocation)).read(friendlyByteBuf);
    }

    public static <T extends PositionSource> void toNetwork(T t, PacketBuffer friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(Objects.requireNonNull(
                Registry.POSITION_SOURCE_TYPE.getKey(t.getType())
        ));

        // Explicitly cast to PositionSourceType<T> to satisfy type constraints
        @SuppressWarnings("unchecked")
        PositionSourceType<T> type = (PositionSourceType<T>) t.getType();

        type.write(friendlyByteBuf, t);
    }

}

