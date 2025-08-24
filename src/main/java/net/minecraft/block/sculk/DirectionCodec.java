package net.minecraft.block.sculk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Direction;

public class DirectionCodec {
    public static final Codec<Direction> CODEC = Codec.STRING.comapFlatMap(
        name -> {
            try {
                return DataResult.success(Direction.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return DataResult.error("Invalid direction: " + name);
            }
        },
        Direction::name
    );
}
