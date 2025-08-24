package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class MultifaceGrowthFeature extends Feature<MultifaceGrowthConfiguration> {

    public MultifaceGrowthFeature(Codec<MultifaceGrowthConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos position, MultifaceGrowthConfiguration config) {
        if (!isAirOrWater(world.getBlockState(position))) return false;
        List<Direction> list = config.getShuffledDirections(random);
        if (placeGrowthIfPossible(world, position, world.getBlockState(position), config, random, list)) return true;
        BlockPos.Mutable mutableBlockPos = position.mutable();
        block0: for (Direction direction : list) {
            mutableBlockPos.set(position);
            List<Direction> list2 = config.getShuffledDirectionsExcept(random, direction.getOpposite());
            for (int i = 0; i < config.searchRange; ++i) {
                mutableBlockPos.setWithOffset(position, direction);
                BlockState blockState = world.getBlockState(mutableBlockPos);
                if (!MultifaceGrowthFeature.isAirOrWater(blockState) && !blockState.is(config.placeBlock)) continue block0;
                if (!MultifaceGrowthFeature.placeGrowthIfPossible(world, mutableBlockPos, blockState, config, random, list2)) continue;
                return true;
            }
        }


        return false;
    }



    public static boolean placeGrowthIfPossible(ISeedReader world, BlockPos position, BlockState state, MultifaceGrowthConfiguration config, Random random, List<Direction> listDirection) {
        BlockPos.Mutable mutable = position.mutable();
        for (Direction direction : listDirection) {
            BlockState s2 = world.getBlockState(mutable.setWithOffset(position, direction));
            if (!s2.is(config.canBePlacedOn)) continue;
            BlockState s3 = config.placeBlock.getStateForPlacement(state, world, position, direction);
            if (s3 == null) return false;
            world.setBlock(position, s3, 3);
            world.getChunk(position).markPosForPostprocessing(position);
            if (random.nextFloat() < config.chanceOfSpreading) {
                config.placeBlock.getSpreader().spreadFromFaceTowardRandomDirection(s3, world, position, direction, random, true);
            }
            return true;
        }
        return false;
    }

    private static boolean isAirOrWater(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER);
    }

}
