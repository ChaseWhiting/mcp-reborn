package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.registry.DynamicRegistries;

import java.util.function.Function;

public class RegistryPacketBuffer extends PacketBuffer {
    private final DynamicRegistries registryAccess;

    public RegistryPacketBuffer(ByteBuf byteBuf, DynamicRegistries registryAccess) {
        super(byteBuf);
        this.registryAccess = registryAccess;
    }

    public DynamicRegistries registryAccess() {
        return this.registryAccess;
    }

    public static Function<ByteBuf, RegistryPacketBuffer> decorator(DynamicRegistries registryAccess) {
        return byteBuf -> new RegistryPacketBuffer((ByteBuf)byteBuf, registryAccess);
    }
}
