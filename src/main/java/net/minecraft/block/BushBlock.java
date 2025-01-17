package net.minecraft.block;

import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class BushBlock extends Block {
   protected BushBlock(AbstractBlock.Properties p_i48437_1_) {
      super(p_i48437_1_);
   }

   protected boolean mayPlaceOn(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.is(Blocks.GRASS_BLOCK) || p_200014_1_.is(Blocks.PALE_MOSS_BLOCK) || p_200014_1_.is(Blocks.DIRT) || p_200014_1_.is(Blocks.COARSE_DIRT) || p_200014_1_.is(Blocks.PODZOL) || p_200014_1_.is(Blocks.FARMLAND) || this == Blocks.PALE_LEAF_PILE && p_200014_1_.isFaceSturdy(p_200014_2_, p_200014_3_, Direction.UP);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.below();
      return this.mayPlaceOn(p_196260_2_.getBlockState(blockpos), p_196260_2_, blockpos);
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return p_200123_1_.getFluidState().isEmpty();
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return p_196266_4_ == PathType.AIR && !this.hasCollision ? true : super.isPathfindable(p_196266_1_, p_196266_2_, p_196266_3_, p_196266_4_);
   }
}