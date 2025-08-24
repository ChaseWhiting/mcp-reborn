
package net.minecraft.block;

import net.minecraft.util.Direction;


public class HalfTransparentBlock
extends Block {


    protected HalfTransparentBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
        if (blockState2.is(this)) {
            return true;
        }
        return super.skipRendering(blockState, blockState2, direction);
    }
}

