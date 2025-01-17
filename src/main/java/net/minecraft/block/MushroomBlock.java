package net.minecraft.block;

import java.util.Random;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.server.ServerWorld;

public class MushroomBlock extends BushBlock implements IGrowable {
   protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

   public MushroomBlock(AbstractBlock.Properties p_i48363_1_) {
      super(p_i48363_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public void randomTick(BlockState state, ServerWorld level, BlockPos position, Random random) {
      if (random.nextInt(25) == 0) {
         int i = 5;
         int j = 4;
         int f = 3;

         for(BlockPos blockpos : BlockPos.betweenClosed(position.offset(-j, -1, -j), position.offset(j, 1, j))) {
            if (level.getBlockState(blockpos).is(this)) {
               --i;
               if (i <= 0) {
                  return;
               }
            }
         }

         BlockPos blockpos1 = position.offset(random.nextInt(f) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(f) - 1);

         for(int k = 0; k < j; ++k) {
            if (level.isEmptyBlock(blockpos1) && state.canSurvive(level, blockpos1)) {
               position = blockpos1;
            }

            blockpos1 = position.offset(random.nextInt(f) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(f) - 1);
         }

         if (level.isEmptyBlock(blockpos1) && state.canSurvive(level, blockpos1)) {
            level.setBlock(blockpos1, state, 2);
         }
      }

   }

   protected boolean mayPlaceOn(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.isSolidRender(p_200014_2_, p_200014_3_);
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.below();
      BlockState blockstate = p_196260_2_.getBlockState(blockpos);
      if (blockstate.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
         return true;
      } else {
         return p_196260_2_.getRawBrightness(p_196260_3_, 0) < 13 && this.mayPlaceOn(blockstate, p_196260_2_, blockpos);
      }
   }

   public boolean growMushroom(ServerWorld p_226940_1_, BlockPos p_226940_2_, BlockState p_226940_3_, Random p_226940_4_) {
      p_226940_1_.removeBlock(p_226940_2_, false);
      ConfiguredFeature<?, ?> configuredfeature;
      if (this == Blocks.BROWN_MUSHROOM) {
         configuredfeature = Features.HUGE_BROWN_MUSHROOM;
      } else {
         if (this != Blocks.RED_MUSHROOM) {
            p_226940_1_.setBlock(p_226940_2_, p_226940_3_, 3);
            return false;
         }

         configuredfeature = Features.HUGE_RED_MUSHROOM;
      }

      if (configuredfeature.place(p_226940_1_, p_226940_1_.getChunkSource().getGenerator(), p_226940_4_, p_226940_2_)) {
         return true;
      } else {
         p_226940_1_.setBlock(p_226940_2_, p_226940_3_, 3);
         return false;
      }
   }

   public boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return true;
   }

   public boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return (double)p_180670_2_.nextFloat() < 0.4D;
   }

   public void performBonemeal(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      this.growMushroom(p_225535_1_, p_225535_3_, p_225535_4_, p_225535_2_);
   }
}