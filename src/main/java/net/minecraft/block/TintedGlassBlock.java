package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class TintedGlassBlock extends AbstractGlassBlock {
   public TintedGlassBlock(Properties p_i48392_1_) {
      super(p_i48392_1_);
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return false;
   }

   @Override
   public int getLightBlock(BlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
      return p_200011_2_.getMaxLightLevel();
   }
}