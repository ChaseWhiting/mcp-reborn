package net.minecraft.block;

import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

public interface ChangeOverTimeBlock<T extends Enum<T>> {
    public static final int SCAN_DISTANCE = 4;

    public Optional<BlockState> getNext(BlockState var1);

    public float getChanceModifier();

    default void changeOverTime(BlockState blockState2, ServerWorld serverLevel, BlockPos blockPos, Random random) {
        float chance = 0.05688889f;
        if (random.nextFloat() < chance) {
            this.getNextState(blockState2, serverLevel, blockPos, random).ifPresent(newState -> {
                if (newState.getBlock() instanceof DoorBlock) {
                    // Handle door state change
                    updateDoorState(serverLevel, blockPos, newState);
                } else {
                    // Non-door blocks
                    serverLevel.setBlockAndUpdate(blockPos, newState);
                }
            });
        }
    }

    public T getAge();

    default public Optional<BlockState> getNextState(BlockState blockState, ServerWorld serverLevel, BlockPos blockPos, Random random) {
        BlockPos blockPos2;
        int n;
        int n2 = ((Enum)this.getAge()).ordinal();
        int n3 = 0;
        int n4 = 0;
        Iterator<BlockPos> iterator = BlockPos.withinManhattan(blockPos, SCAN_DISTANCE, SCAN_DISTANCE, SCAN_DISTANCE).iterator();
        while (iterator.hasNext() && (n = (blockPos2 = iterator.next()).distManhattan(blockPos)) <= SCAN_DISTANCE) {
            Block block;
            if (blockPos2.equals(blockPos) || !((block = serverLevel.getBlockState(blockPos2).getBlock()) instanceof ChangeOverTimeBlock)) {
                continue;
            }
            ChangeOverTimeBlock<T> changeOverTimeBlock = (ChangeOverTimeBlock<T>) block;
            Enum<?> age = changeOverTimeBlock.getAge();
            if (!this.getAge().getClass().equals(age.getClass())) {
                continue;
            }
            int n5 = age.ordinal();
            if (n5 < n2) {
                return Optional.empty();
            }
            if (n5 > n2) {
                ++n4;
                continue;
            }
            ++n3;
        }
        float f = (float)(n4 + 1) / (float)(n4 + n3 + 1);
        float f2 = f * f * this.getChanceModifier();
        if (random.nextFloat() < f2) {
            return this.getNext(blockState);
        }
        return Optional.empty();
    }

    default void updateDoorState(ServerWorld world, BlockPos pos, BlockState newState) {
        DoubleBlockHalf half = newState.getValue(DoorBlock.HALF);
        BlockPos bottomPos = (half == DoubleBlockHalf.UPPER) ? pos.below() : pos;
        BlockPos topPos = bottomPos.above();
        int flags = 2 | 8 | 16 | 32;


        world.setBlock(topPos, Blocks.AIR.defaultBlockState(), flags);
        world.setBlock(bottomPos, Blocks.AIR.defaultBlockState(), flags);

        world.setBlock(bottomPos, newState.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 11);

        BlockState bottomState = world.getBlockState(bottomPos);
        if (bottomState.getBlock() instanceof DoorBlock) {
            bottomState.getBlock().setPlacedBy(world, bottomPos, bottomState, null, ItemStack.EMPTY);
        }
    }

    default void updateCopperDoor(ServerWorld world, BlockPos pos, BlockState newState) {
        DoubleBlockHalf half = newState.getValue(DoorBlock.HALF);
        BlockPos bottomPos = (half == DoubleBlockHalf.UPPER) ? pos.below() : pos;

        int flags = 2 | 8 | 16 | 32;

        world.setBlock(pos, Blocks.AIR.defaultBlockState(), flags);
        world.setBlock(bottomPos, Blocks.AIR.defaultBlockState(), flags);

        world.setBlock(bottomPos, newState.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 3);
        world.setBlock(pos, newState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);

    }
}

