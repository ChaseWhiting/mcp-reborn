package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class TallDryGrassBlock extends DryVegetationBlock implements IGrowable {
    protected TallDryGrassBlock(Properties properties) {
        super(properties);
    }

    private static final VoxelShape SHAPE = column(14, 0, 16);

    public VoxelShape getShape() {
        return SHAPE;
    }



    @Override
    public boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
        if (p_176473_1_ instanceof IWorldReader) {
            return IGrowable.hasSpreadableNeighbourPos((IWorldReader) p_176473_1_, p_176473_2_, Blocks.SHORT_DRY_GRASS.defaultBlockState());
        }
        return false;
    }

    @Override
    public boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
        return true;
    }

    @Override
    public void performBonemeal(ServerWorld serverLevel, Random random, BlockPos blockPos2, BlockState blockState) {
        IGrowable.findSpreadableNeighbourPos(serverLevel, blockPos2, Blocks.SHORT_DRY_GRASS.defaultBlockState()).ifPresent(blockPos -> serverLevel.setBlockAndUpdate((BlockPos)blockPos, Blocks.SHORT_DRY_GRASS.defaultBlockState()));
    }
}
