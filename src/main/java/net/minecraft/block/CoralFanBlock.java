package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class CoralFanBlock extends AbstractCoralPlantBlock {
   private static final VoxelShape AABB = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

   protected CoralFanBlock(AbstractBlock.Properties p_i49777_1_) {
      super(p_i49777_1_);
   }

   public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      return AABB;
   }
}