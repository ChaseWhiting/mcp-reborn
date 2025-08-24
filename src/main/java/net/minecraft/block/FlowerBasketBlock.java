package net.minecraft.block;

import com.google.common.collect.Maps;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.Map;

public class FlowerBasketBlock extends Block {
    private static final Map<Block, Block> POTTED_BY_CONTENT = Maps.newHashMap();

    private static final VoxelShape SHAPE = Block.box(2, 0, 4, 3, 5, 12);
    private static final VoxelShape SHAPE1 = Block.box(13, 0, 4, 14, 5, 12);
    private static final VoxelShape SHAPE2 = Block.box(2, 0, 3, 14, 5, 14);
    private static final VoxelShape SHAPE3 = Block.box(2, 0, 12, 14, 5, 13);


    public FlowerBasketBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }
}
