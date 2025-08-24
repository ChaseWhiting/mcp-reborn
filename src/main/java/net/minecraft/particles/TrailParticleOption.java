package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

public class TrailParticleOption implements IParticleData {
    private final Vector3d target;
    private final int color;
    private final int duration;

    public static final Codec<TrailParticleOption> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("targetX").forGetter(p -> p.target.x),
                    Codec.DOUBLE.fieldOf("targetY").forGetter(p -> p.target.y),
                    Codec.DOUBLE.fieldOf("targetZ").forGetter(p -> p.target.z),
                    Codec.INT.fieldOf("color").forGetter(TrailParticleOption::getColor),
                    Codec.INT.fieldOf("duration").forGetter(TrailParticleOption::getDuration)
            ).apply(instance, (x, y, z, color, duration) -> new TrailParticleOption(new Vector3d(x, y, z), color, duration))
    );

    public static final IParticleData.IDeserializer<TrailParticleOption> DESERIALIZER = new IParticleData.IDeserializer<TrailParticleOption>() {
        public TrailParticleOption fromCommand(ParticleType<TrailParticleOption> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            double x = reader.readDouble();
            reader.expect(' ');
            double y = reader.readDouble();
            reader.expect(' ');
            double z = reader.readDouble();
            reader.expect(' ');
            int color = reader.readInt();
            reader.expect(' ');
            int duration = reader.readInt();
            return new TrailParticleOption(new Vector3d(x, y, z), color, duration);
        }

        public TrailParticleOption fromNetwork(ParticleType<TrailParticleOption> type, PacketBuffer buffer) {
            return new TrailParticleOption(
                    new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
                    buffer.readInt(),
                    buffer.readVarInt()
            );
        }
    };

    public TrailParticleOption(Vector3d target, int color, int duration) {
        this.target = target;
        this.color = color;
        this.duration = duration;
    }

    public Vector3d getTarget() {
        return target;
    }

    public int getColor() {
        return color;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public void writeToNetwork(PacketBuffer buffer) {
        buffer.writeDouble(target.x);
        buffer.writeDouble(target.y);
        buffer.writeDouble(target.z);
        buffer.writeInt(color);
        buffer.writeVarInt(duration);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d %d",
                Registry.PARTICLE_TYPE.getKey(this.getType()), target.x, target.y, target.z, color, duration);
    }

    @Override
    public ParticleType<TrailParticleOption> getType() {
        return ParticleTypes.TRAIL;
    }

    @OnlyIn(Dist.CLIENT)
    public double getTargetX() {
        return this.target.x;
    }

    @OnlyIn(Dist.CLIENT)
    public double getTargetY() {
        return this.target.y;
    }

    @OnlyIn(Dist.CLIENT)
    public double getTargetZ() {
        return this.target.z;
    }
}
