package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class WallSignBlock extends AbstractSignBlock {
   public static final DirectionProperty FACING = HorizontalBlock.FACING;
   private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D), Direction.SOUTH, Block.box(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D), Direction.EAST, Block.box(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D), Direction.WEST, Block.box(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D)));

   public WallSignBlock(AbstractBlock.Properties p_i225766_1_, WoodType p_i225766_2_) {
      super(p_i225766_1_, p_i225766_2_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      return AABBS.get(state.getValue(FACING));
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.relative(p_196260_1_.getValue(FACING).getOpposite())).getMaterial().isSolid();
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = this.defaultBlockState();
      FluidState fluidstate = p_196258_1_.getLevel().getFluidState(p_196258_1_.getClickedPos());
      IWorldReader iworldreader = p_196258_1_.getLevel();
      BlockPos blockpos = p_196258_1_.getClickedPos();
      Direction[] adirection = p_196258_1_.getNearestLookingDirections();

      for(Direction direction : adirection) {
         if (direction.getAxis().isHorizontal()) {
            Direction direction1 = direction.getOpposite();
            blockstate = blockstate.setValue(FACING, direction1);
            if (blockstate.canSurvive(iworldreader, blockpos)) {
               return blockstate.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
            }
         }
      }

      return null;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_.getOpposite() == p_196271_1_.getValue(FACING) && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirroring) {
      return state.rotate(mirroring.getRotation(state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING, WATERLOGGED);
   }
}