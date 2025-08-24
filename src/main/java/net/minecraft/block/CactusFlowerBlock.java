package net.minecraft.block;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class CactusFlowerBlock extends BushBlock {
    private static final VoxelShape SHAPE = column(14, 14, 0, 12);

    public static VoxelShape column(double d, double d2, double d3, double d4) {
        double d5 = d / 2.0;
        double d6 = d2 / 2.0;
        return Block.box(8.0 - d5, d3, 8.0 - d6, 8.0 + d5, d4, 8.0 + d6);
    }

    public CactusFlowerBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    protected boolean mayPlaceOn(BlockState state, IBlockReader world, BlockPos pos) {
        BlockState s1 = world.getBlockState(pos);
        return s1.is(Blocks.CACTUS) || s1.isFaceSturdy(world, pos, Direction.UP);
    }
}
