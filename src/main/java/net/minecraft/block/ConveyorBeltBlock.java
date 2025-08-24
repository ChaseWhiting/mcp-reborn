package net.minecraft.block;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ConveyorBeltBlock extends Block implements IWaterLoggable {
    private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;


    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(3, 0, 0, 13, 6, 16);
    private static final VoxelShape SHAPE_EAST_WEST = Block.box(0, 0, 3, 16, 6, 13);

    public ConveyorBeltBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(POWERED, false)
                .setValue(POWER, 0));
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, IWorld world, BlockPos pos, BlockPos pos2) {
        if (state.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, direction, state2, world, pos, pos2);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, direction).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }

        return super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("all")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH, SOUTH -> SHAPE_NORTH_SOUTH;
            case WEST, EAST -> SHAPE_EAST_WEST;

            default -> SHAPE_NORTH_SOUTH;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, POWER, POWERED);
    }

    @Override
    public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.getValue(WATERLOGGED) || fluidState.getType() != Fluids.WATER) {
            return false;
        }

        world.getLiquidTicks().scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
        return true;
    }


    public boolean isConnectedConveyor(BlockState state, Direction facing) {
        return state.getBlock() instanceof ConveyorBeltBlock && state.getValue(FACING) == facing;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!world.isClientSide) {
            int maxPower = world.getBestNeighborSignal(pos);

            // Redstone power from the environment
            maxPower = Math.max(maxPower, world.getBestNeighborSignal(pos));

            // Power from the conveyor behind us
            Direction facing = state.getValue(FACING);
            BlockPos behind = pos.relative(facing.getOpposite());
            BlockState behindState = world.getBlockState(behind);
            if (isConnectedConveyor(behindState, facing)) {
                maxPower = Math.max(maxPower, behindState.getValue(POWER) - 1);
            }

            // Only update if needed
            if (state.getValue(POWER) != maxPower) {
                world.setBlock(pos, state.setValue(POWER, maxPower), 2);
            }
        }
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        this.neighborChanged(state, world, pos, state.getBlock(), pos, false); // Kick off power update
    }



}
