
package net.minecraft.block;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;


public class WaterloggedTransparentBlock
extends TransparentBlock
implements IWaterLoggable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;


    protected WaterloggedTransparentBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext blockPlaceContext) {
        FluidState fluidState = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
        return (BlockState)super.getStateForPlacement(blockPlaceContext).setValue(WATERLOGGED, fluidState.is(FluidTags.WATER));
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, IWorld iWorld, BlockPos blockPos, BlockPos blockPos1) {
        if (blockState.getValue(WATERLOGGED).booleanValue()) {
            iWorld.getLiquidTicks().scheduleTick(blockPos, Fluids.WATER, 11);
        }
        return super.updateShape(blockState, direction, blockState1, iWorld, blockPos, blockPos1);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(true);
        }
        return super.getFluidState(blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }
}

