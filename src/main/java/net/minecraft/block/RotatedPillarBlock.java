package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

public class RotatedPillarBlock extends Block {
   public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

   public RotatedPillarBlock(AbstractBlock.Properties p_i48339_1_) {
      super(p_i48339_1_);
      this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
       return switch (p_185499_2_) {
           case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch ((Direction.Axis) p_185499_1_.getValue(AXIS)) {
               case X -> p_185499_1_.setValue(AXIS, Direction.Axis.Z);
               case Z -> p_185499_1_.setValue(AXIS, Direction.Axis.X);
               default -> p_185499_1_;
           };
           default -> p_185499_1_;
       };
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AXIS);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(AXIS, p_196258_1_.getClickedFace().getAxis());
   }
}