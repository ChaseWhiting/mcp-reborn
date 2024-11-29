package net.minecraft.block;

import java.util.Map;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class HugeMushroomBlock extends Block {
   public static final BooleanProperty NORTH = SixWayBlock.NORTH;
   public static final BooleanProperty EAST = SixWayBlock.EAST;
   public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
   public static final BooleanProperty WEST = SixWayBlock.WEST;
   public static final BooleanProperty UP = SixWayBlock.UP;
   public static final BooleanProperty DOWN = SixWayBlock.DOWN;
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = SixWayBlock.PROPERTY_BY_DIRECTION;

   public HugeMushroomBlock(AbstractBlock.Properties p_i49982_1_) {
      super(p_i49982_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.valueOf(true)).setValue(EAST, Boolean.valueOf(true)).setValue(SOUTH, Boolean.valueOf(true)).setValue(WEST, Boolean.valueOf(true)).setValue(UP, Boolean.valueOf(true)).setValue(DOWN, Boolean.valueOf(true)));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader iblockreader = p_196258_1_.getLevel();
      BlockPos blockpos = p_196258_1_.getClickedPos();
      return this.defaultBlockState().setValue(DOWN, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.below()).getBlock())).setValue(UP, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.above()).getBlock())).setValue(NORTH, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.north()).getBlock())).setValue(EAST, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.east()).getBlock())).setValue(SOUTH, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.south()).getBlock())).setValue(WEST, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.west()).getBlock()));
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_3_.is(this) ? p_196271_1_.setValue(PROPERTY_BY_DIRECTION.get(p_196271_2_), Boolean.valueOf(false)) : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return state.setValue(PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.NORTH)), state.getValue(NORTH)).setValue(PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.SOUTH)), state.getValue(SOUTH)).setValue(PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.EAST)), state.getValue(EAST)).setValue(PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.WEST)), state.getValue(WEST)).setValue(PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.UP)), state.getValue(UP)).setValue(PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.DOWN)), state.getValue(DOWN));
   }

   public BlockState mirror(BlockState state, Mirror mirroring) {
      return state.setValue(PROPERTY_BY_DIRECTION.get(mirroring.mirror(Direction.NORTH)), state.getValue(NORTH)).setValue(PROPERTY_BY_DIRECTION.get(mirroring.mirror(Direction.SOUTH)), state.getValue(SOUTH)).setValue(PROPERTY_BY_DIRECTION.get(mirroring.mirror(Direction.EAST)), state.getValue(EAST)).setValue(PROPERTY_BY_DIRECTION.get(mirroring.mirror(Direction.WEST)), state.getValue(WEST)).setValue(PROPERTY_BY_DIRECTION.get(mirroring.mirror(Direction.UP)), state.getValue(UP)).setValue(PROPERTY_BY_DIRECTION.get(mirroring.mirror(Direction.DOWN)), state.getValue(DOWN));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
   }
}