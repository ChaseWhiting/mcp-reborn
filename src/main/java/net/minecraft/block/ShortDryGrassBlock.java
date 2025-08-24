package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class ShortDryGrassBlock extends DryVegetationBlock implements IGrowable {
    protected ShortDryGrassBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    private static final VoxelShape SHAPE = column(12, 0, 10);


    public VoxelShape getShape() {
        return SHAPE;
    }


    @Override
    public boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
        return true;
    }

    @Override
    public void performBonemeal(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
        p_225535_1_.setBlockAndUpdate(p_225535_3_, Blocks.TALL_DRY_GRASS.defaultBlockState());
    }
}
