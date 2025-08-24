package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.warden.event.position.BlockPositionSource;
import net.minecraft.entity.warden.event.position.PositionSource;
import net.minecraft.entity.warden.event.position.PositionSourceType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

public class VibrationParticleOption implements IParticleData {
    private final PositionSource destination;
    private final int arrivalInTicks;

    public static final Codec<VibrationParticleOption> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    PositionSource.CODEC.fieldOf("destination").forGetter(VibrationParticleOption::getDestination),
                    Codec.INT.fieldOf("arrival_in_ticks").forGetter(VibrationParticleOption::getArrivalInTicks)
            ).apply(instance, VibrationParticleOption::new)
    );

    public static final IParticleData.IDeserializer<VibrationParticleOption> DESERIALIZER = new IParticleData.IDeserializer<VibrationParticleOption>() {
        @Override
        public VibrationParticleOption fromCommand(ParticleType<VibrationParticleOption> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            double x = reader.readDouble();
            reader.expect(' ');
            double y = reader.readDouble();
            reader.expect(' ');
            double z = reader.readDouble();
            reader.expect(' ');
            int ticks = reader.readInt();
            BlockPos blockPos = new BlockPos(x, y, z);
            return new VibrationParticleOption(new BlockPositionSource(blockPos), ticks);
        }

        @Override
        public VibrationParticleOption fromNetwork(ParticleType<VibrationParticleOption> type, PacketBuffer buffer) {
            PositionSource positionSource = PositionSourceType.fromNetwork(buffer);
            int ticks = buffer.readVarInt();
            return new VibrationParticleOption(positionSource, ticks);
        }
    };

    public VibrationParticleOption(PositionSource destination, int arrivalInTicks) {
        this.destination = destination;
        this.arrivalInTicks = arrivalInTicks;
    }

    public PositionSource getDestination() {
        return destination;
    }

    public int getArrivalInTicks() {
        return arrivalInTicks;
    }

    @Override
    public void writeToNetwork(PacketBuffer buffer) {
        PositionSourceType.toNetwork(this.destination, buffer);
        buffer.writeVarInt(this.arrivalInTicks);
    }

    @Override
    public String writeToString() {
        Vector3d vector = this.destination.getPosition(null).get();
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d",
                Registry.PARTICLE_TYPE.getKey(this.getType()), vector.x, vector.y, vector.z, this.arrivalInTicks);
    }

    @Override
    public ParticleType<VibrationParticleOption> getType() {
        return ParticleTypes.VIBRATION;
    }

    @OnlyIn(Dist.CLIENT)
    public double getTargetX() {
        return this.destination.getPosition(null).get().x;
    }

    @OnlyIn(Dist.CLIENT)
    public double getTargetY() {
        return this.destination.getPosition(null).get().y;
    }

    @OnlyIn(Dist.CLIENT)
    public double getTargetZ() {
        return this.destination.getPosition(null).get().z;
    }
}
