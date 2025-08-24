package net.minecraft.block;

import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MudBlock extends Block {

   private static final VoxelShape SHAPE = column(16.0, 16, 0.0, 14.0);

   public static VoxelShape column(double d, double d2, double d3, double d4) {
      double d5 = d / 2.0;
      double d6 = d2 / 2.0;
      return Block.box(8.0 - d5, d3, 8.0 - d6, 8.0 + d5, d4, 8.0 + d6);
   }

   public MudBlock(Properties properties) {
      super(properties);
   }

   // Override to define the shape of the block based on its TIP state
   @Override
   public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   @Override
   public VoxelShape getBlockSupportShape(BlockState p_230335_1_, IBlockReader p_230335_2_, BlockPos p_230335_3_) {
      return VoxelShapes.block();
   }

   @Override
   public VoxelShape getVisualShape(BlockState p_230322_1_, IBlockReader p_230322_2_, BlockPos p_230322_3_, ISelectionContext p_230322_4_) {
      return VoxelShapes.block();
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
      return 0.2F;
   }
}
