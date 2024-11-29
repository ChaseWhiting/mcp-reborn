
package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.BiFunction;

@SuppressWarnings("deprecation")
public class PetalBlock
extends BushBlock implements IGrowable{
    public static final int MAX_FLOWERS = 4;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty AMOUNT = BlockStateProperties.FLOWER_AMOUNT;
    private static final BiFunction<Direction, Integer, VoxelShape> SHAPE_BY_PROPERTIES = Util.memoize(($$0, $$1) -> {
        VoxelShape[] shapes = new VoxelShape[]{Block.box(8.0, 0.0, 8.0, 16.0, 3.0, 16.0), Block.box(8.0, 0.0, 0.0, 16.0, 3.0, 8.0), Block.box(0.0, 0.0, 0.0, 8.0, 3.0, 8.0), Block.box(0.0, 0.0, 8.0, 8.0, 3.0, 16.0)};
        VoxelShape shape = VoxelShapes.empty();
        for (int i = 0; i < $$1; ++i) {
            int a = Math.floorMod(i - $$0.get2DDataValue(), MAX_FLOWERS);
            shape = VoxelShapes.or(shape, shapes[a]);
        }
        return shape.singleEncompassing();
    });

    protected PetalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(AMOUNT, 1));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirroring) {
        return state.rotate(mirroring.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockItemUseContext useContext) {
        if (!useContext.isSecondaryUseActive() && useContext.getItemInHand().getItem() == (this.asItem()) && state.getValue(AMOUNT) < MAX_FLOWERS) {
            return true;
        }
        return super.canBeReplaced(state, useContext);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE_BY_PROPERTIES.apply(p_220053_1_.getValue(FACING), p_220053_1_.getValue(AMOUNT));
    }

    @Override
    public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        if (!player.isCreative()) {
            popResource(worldIn, pos, new ItemStack(this, state.getValue(AMOUNT)));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext useContext) {
        BlockState currentState = useContext.getLevel().getBlockState(useContext.getClickedPos());
        if (currentState.is(this)) {
            return currentState.setValue(AMOUNT, Math.min(MAX_FLOWERS, currentState.getValue(AMOUNT) + 1));
        }
        return this.defaultBlockState().setValue(FACING, useContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> containerBlockBuilder) {
        containerBlockBuilder.add(FACING, AMOUNT);
    }

    @Override
    public boolean isValidBonemealTarget(IBlockReader world, BlockPos position, BlockState state, boolean b) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(World world, Random random, BlockPos position, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerWorld world, Random random, BlockPos position, BlockState state) {
        int currentFlowerCount = state.getValue(AMOUNT);
        if (currentFlowerCount < MAX_FLOWERS) {
            world.setBlock(position, state.setValue(AMOUNT, currentFlowerCount + 1), 2);
        } else {
            PetalBlock.popResource(world, position, new ItemStack(this));
        }
    }
}
