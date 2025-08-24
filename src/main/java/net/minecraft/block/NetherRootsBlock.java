package net.minecraft.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class NetherRootsBlock extends BushBlock {
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

   protected NetherRootsBlock(AbstractBlock.Properties p_i241186_1_) {
      super(p_i241186_1_);
   }

   public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.is(BlockTags.NYLIUM) || p_200014_1_.is(Blocks.SOUL_SOIL) || super.mayPlaceOn(p_200014_1_, p_200014_2_, p_200014_3_);
   }

   public AbstractBlock.OffsetType getOffsetType() {
      return AbstractBlock.OffsetType.XZ;
   }
}