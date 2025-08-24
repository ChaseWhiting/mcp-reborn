package net.minecraft.entity.warden.event.position;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Optional;


public class BlockPositionSource
implements PositionSource {
    public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(BlockPos.CODEC.fieldOf("pos")
                            .forGetter(blockPositionSource -> blockPositionSource.pos)).apply(instance, BlockPositionSource::new));

    final net.minecraft.util.math.BlockPos pos;

    public BlockPositionSource(BlockPos blockPos) {
        this.pos = blockPos;
    }

    @Override
    public Optional<Vector3d> getPosition(World level) {
        return Optional.of(Vector3d.atCenterOf(this.pos));
    }

    @Override
    public PositionSourceType<?> getType() {
        return PositionSourceType.BLOCK;
    }

    public static class Type
    implements PositionSourceType<BlockPositionSource> {
        @Override
        public BlockPositionSource read(PacketBuffer friendlyByteBuf) {
            return new BlockPositionSource(friendlyByteBuf.readBlockPos());
        }

        @Override
        public void write(PacketBuffer friendlyByteBuf, BlockPositionSource blockPositionSource) {
            friendlyByteBuf.writeBlockPos(blockPositionSource.pos);
        }

        @Override
        public Codec<BlockPositionSource> codec() {
            return CODEC;
        }
    }
}

