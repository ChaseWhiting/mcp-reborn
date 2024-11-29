package net.minecraft.block;

import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public abstract class HorizontalBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

   protected HorizontalBlock(AbstractBlock.Properties p_i48377_1_) {
      super(p_i48377_1_);
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirroring) {
      return state.rotate(mirroring.getRotation(state.getValue(FACING)));
   }
}