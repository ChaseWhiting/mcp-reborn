package net.minecraft.block;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class HellfireBlock extends AbstractFireBlock {
   public HellfireBlock(Properties p_i241187_1_) {
      super(p_i241187_1_, 6.0F);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return this.canSurvive(p_196271_1_, p_196271_4_, p_196271_5_) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return canSurviveOnBlock(p_196260_2_.getBlockState(p_196260_3_.below()).getBlock());
   }

   public static boolean canSurviveOnBlock(Block p_235577_0_) {
      return !(p_235577_0_ instanceof AirBlock);
   }

   protected boolean canBurn(BlockState p_196446_1_) {
      return true;
   }
}