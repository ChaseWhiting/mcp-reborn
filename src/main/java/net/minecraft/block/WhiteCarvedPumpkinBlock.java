package net.minecraft.block;

import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;

public class WhiteCarvedPumpkinBlock extends HorizontalBlock implements IArmorVanishable {
   public static final DirectionProperty FACING = HorizontalBlock.FACING;

   protected WhiteCarvedPumpkinBlock(Properties p_i48432_1_) {
      super(p_i48432_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }
}