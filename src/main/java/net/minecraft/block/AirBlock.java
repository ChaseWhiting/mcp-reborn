package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class AirBlock extends Block {
   protected AirBlock(AbstractBlock.Properties p_i48451_1_) {
      super(p_i48451_1_);
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.INVISIBLE;
   }

   public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      return VoxelShapes.empty();
   }
}