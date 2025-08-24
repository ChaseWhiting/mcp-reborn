package net.minecraft.entity.warden.event.position;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;


public interface PositionSource {
    public static final Codec<PositionSource> CODEC = Registry.POSITION_SOURCE_TYPE.dispatch(PositionSource::getType, PositionSourceType::codec);
    public Optional<Vector3d> getPosition(World world);

    public PositionSourceType<?> getType();
}

