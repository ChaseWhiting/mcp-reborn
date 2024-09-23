package net.minecraft.block;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CooledMagmaBlock extends MagmaBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

   public CooledMagmaBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   @Override
   public void stepOn(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {

   }

   public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      this.tick(state, world, pos, random);
      super.randomTick(state,world,pos,random);
   }

   public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if ((random.nextInt(3) == 0 || this.fewerNeighborsThan(world, pos, 4)) && world.getMaxLocalRawBrightness(pos) > 11 - state.getValue(AGE) - state.getLightBlock(world, pos) && this.slightlyMelt(state, world, pos)) {
         BlockPos.Mutable mutablePos = new BlockPos.Mutable();

         for (Direction direction : Direction.values()) {
            mutablePos.setWithOffset(pos, direction);
            BlockState neighborState = world.getBlockState(mutablePos);
            if (neighborState.is(this) && !this.slightlyMelt(neighborState, world, mutablePos)) {
               world.getBlockTicks().scheduleTick(mutablePos, this, MathHelper.nextInt(random, 20, 40));
            }
         }

      } else {
         world.getBlockTicks().scheduleTick(pos, this, MathHelper.nextInt(random, 20, 40));
      }
   }

   private boolean slightlyMelt(BlockState state, World world, BlockPos pos) {
      int age = state.getValue(AGE);
      if (age < 3) {
         world.setBlock(pos, state.setValue(AGE, Integer.valueOf(age + 1)), 2);
         return false;
      } else {
         this.melt(state, world, pos);
         return true;
      }
   }

   private void melt(BlockState state, World world, BlockPos pos) {
      world.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3); // Revert to lava
   }

   public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
      if (block == this && this.fewerNeighborsThan(world, pos, 2)) {
         this.melt(state, world, pos);
      }

      super.neighborChanged(state, world, pos, block, neighborPos, isMoving);
   }

   private boolean fewerNeighborsThan(IBlockReader world, BlockPos pos, int count) {
      int neighbors = 0;
      BlockPos.Mutable mutablePos = new BlockPos.Mutable();

      for (Direction direction : Direction.values()) {
         mutablePos.setWithOffset(pos, direction);
         if (world.getBlockState(mutablePos).is(this)) {
            ++neighbors;
            if (neighbors >= count) {
               return false;
            }
         }
      }

      return true;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(AGE);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, BlockState state) {
      return ItemStack.EMPTY;
   }
}
