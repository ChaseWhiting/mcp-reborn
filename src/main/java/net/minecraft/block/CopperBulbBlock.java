
package net.minecraft.block;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;


public class CopperBulbBlock
extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;


    public CopperBulbBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false).setValue(POWERED, false));
    }

    @Override
    public void onPlace(BlockState blockState, World level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.getBlock() != blockState.getBlock() && level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld)level;
            this.checkAndFlip(blockState, serverLevel, blockPos);
        }
    }



    public void neighborChanged(BlockState state, World level, BlockPos blockPos, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        if (level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld)level;
            this.checkAndFlip(state, serverLevel, blockPos);
        }
    }

    public void checkAndFlip(BlockState blockState, ServerWorld serverLevel, BlockPos blockPos) {
        boolean bl = serverLevel.hasNeighborSignal(blockPos);
        if (bl == blockState.getValue(POWERED)) {
            return;
        }
        BlockState blockState2 = blockState;
        if (!blockState.getValue(POWERED)) {
            serverLevel.playSound(null, blockPos, (blockState2 = blockState2.cycle(LIT)).getValue(LIT) ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        serverLevel.setBlock(blockPos, blockState2.setValue(POWERED, bl), 3);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT, POWERED);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, World level, BlockPos blockPos) {
        return level.getBlockState(blockPos).getValue(LIT) ? 15 : 0;
    }
}

