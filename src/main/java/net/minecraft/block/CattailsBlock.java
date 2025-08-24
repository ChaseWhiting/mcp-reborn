package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class CattailsBlock extends DoublePlantBlock implements IWaterLoggable {
   public static final EnumProperty<DoubleBlockHalf> HALF = DoublePlantBlock.HALF;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   public CattailsBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any()
              .setValue(HALF, DoubleBlockHalf.LOWER)
              .setValue(WATERLOGGED, false));
   }

   @Override
   public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   @Override
   protected boolean mayPlaceOn(BlockState state, IBlockReader world, BlockPos pos) {
      return state.isFaceSturdy(world, pos, Direction.UP) && !state.is(Blocks.MAGMA_BLOCK);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      BlockPos pos = context.getClickedPos();
      IWorldReader world = context.getLevel();
      FluidState fluidBelow = world.getFluidState(pos);
      FluidState fluidAbove = world.getFluidState(pos.above());

      boolean isWaterBelow = fluidBelow.is(FluidTags.WATER) && fluidBelow.getAmount() == 8;
      boolean isWaterAbove = fluidAbove.is(FluidTags.WATER) && fluidAbove.getAmount() == 8;


      if (pos.getY() < world.getMaxBuildHeight() - 1 && world.getBlockState(pos.above()).isAir()) {
         boolean shouldBeWaterlogged = isWaterBelow && !isWaterAbove;
         return this.defaultBlockState()
                 .setValue(HALF, DoubleBlockHalf.LOWER)
                 .setValue(WATERLOGGED, shouldBeWaterlogged);
      } else {
         return null;
      }
   }

   @Override
   public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
      if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
         BlockState belowState = world.getBlockState(pos.below());
         return belowState.is(this) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER &&
                 !world.getFluidState(pos).is(FluidTags.WATER);
      } else {
         FluidState fluidState = world.getFluidState(pos);
         return mayPlaceOn(world.getBlockState(pos.below()), world, pos.below()) &&
                 (!fluidState.is(FluidTags.WATER) || fluidState.getAmount() == 8);
      }
   }

   @Override
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
      if (state.getValue(HALF) == DoubleBlockHalf.LOWER && state.getValue(WATERLOGGED)) {
         world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
      }

      return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
   }

   @Override
   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
   }

   @Override
   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(HALF, WATERLOGGED);
   }
}
