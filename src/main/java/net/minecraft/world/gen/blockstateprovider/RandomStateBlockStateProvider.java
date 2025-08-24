package net.minecraft.world.gen.blockstateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PetalBlock;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Random;

public class RandomStateBlockStateProvider extends BlockStateProvider {
    public static final Codec<RandomStateBlockStateProvider> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Registry.BLOCK.fieldOf("block").forGetter(provider -> provider.block), // Block to place
                    Codec.INT.fieldOf("min").forGetter(provider -> provider.min), // Minimum value for AMOUNT
                    Codec.INT.fieldOf("max").forGetter(provider -> provider.max)  // Maximum value for AMOUNT
            ).apply(instance, RandomStateBlockStateProvider::new)
    );

    private final Block block;
    private final int min;
    private final int max;

    // Properties for PetalBlock
    private static final IntegerProperty AMOUNT = PetalBlock.AMOUNT;
    private static final EnumProperty<Direction> FACING = PetalBlock.FACING;

    public RandomStateBlockStateProvider(Block block, int min, int max) {
        this.block = block;
        this.min = min;
        this.max = max;
    }

    @Override
    protected BlockStateProviderType<?> type() {
        return BlockStateProviderType.RANDOM_STATE_PROVIDER; // Use the SIMPLE_STATE_PROVIDER type
    }

    @Override
    public BlockState getState(Random random, BlockPos pos) {
        // Validate min and max
        if (min < 1 || max > 4 || min > max) {
            throw new IllegalArgumentException("Invalid range for PetalBlock.AMOUNT: min=" + min + ", max=" + max);
        }

        // Generate random values within the specified range
        int randomAmount = min + random.nextInt(max - min + 1); // Random value between min and max (inclusive)
        Direction randomFacing = Direction.Plane.HORIZONTAL.getRandomDirection(random); // Random direction: NORTH, EAST, SOUTH, WEST

        // Return the BlockState with the random values
        return block.defaultBlockState()
                .setValue(AMOUNT, randomAmount)
                .setValue(FACING, randomFacing);
    }
}
