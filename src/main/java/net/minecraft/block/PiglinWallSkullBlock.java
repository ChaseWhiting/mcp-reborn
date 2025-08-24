package net.minecraft.block;

import com.google.common.collect.Maps;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import java.util.Map;

public class PiglinWallSkullBlock
extends WallSkullBlock {
    private static final Map<Direction, VoxelShape> AABBS = Maps.immutableEnumMap(Map.of(Direction.NORTH, Block.box(3.0, 4.0, 8.0, 13.0, 12.0, 16.0), Direction.SOUTH, Block.box(3.0, 4.0, 0.0, 13.0, 12.0, 8.0), Direction.EAST, Block.box(0.0, 4.0, 3.0, 8.0, 12.0, 13.0), Direction.WEST, Block.box(8.0, 4.0, 3.0, 16.0, 12.0, 13.0)));


    public PiglinWallSkullBlock(AbstractBlock.Properties properties) {
        super(SkullBlock.Types.PIGLIN, properties);
    }

    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return AABBS.get(state.getValue(FACING));
    }
}

