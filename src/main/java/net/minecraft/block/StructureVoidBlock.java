package net.minecraft.block;

import net.minecraft.block.material.PushReaction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StructureVoidBlock extends Block {
   private static final VoxelShape SHAPE = Block.box(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

   protected StructureVoidBlock(AbstractBlock.Properties p_i48313_1_) {
      super(p_i48313_1_);
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.INVISIBLE;
   }

   public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   @OnlyIn(Dist.CLIENT)
   public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
      return 1.0F;
   }

   public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }
}