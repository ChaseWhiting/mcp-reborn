package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;

import javax.annotation.Nullable;
import java.util.Random;

public class HangingMossBlock extends Block {
   // Define the TIP property
   public static final BooleanProperty TIP = BlockStateProperties.TIP;

   // Define shapes for the TIP and BASE part of the moss
   protected static final VoxelShape TIP_SHAPE = Block.box(1.0D, 2.0D, 1.0D, 15.0D, 16.0D, 15.0D);
   protected static final VoxelShape BASE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public HangingMossBlock() {
      super(Properties.of(Material.PLANT, MaterialColor.PALE_MOSS).noCollission().strength(0.2F).sound(SoundType.MOSS));
      this.registerDefaultState(this.stateDefinition.any().setValue(TIP, true));
   }

   // Override to define the shape of the block based on its TIP state
   @Override
   public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      return state.getValue(TIP) ? TIP_SHAPE : BASE_SHAPE;
   }

   public static boolean canGenerateMoss(IChunk chunk, BlockPos.Mutable pos, Random random) {
      BlockState blockAbove = chunk.getBlockState(pos.above());
      BlockState blockBelow = chunk.getBlockState(pos.below());

      // Check if block above is stone or another hanging moss and if the block below is air
      boolean validAbove = blockAbove.is(Blocks.STONE) || blockAbove.is(Blocks.PALE_HANGING_MOSS);
      boolean validBelow = blockBelow.isAir();

      return validAbove && validBelow;
   }

   // Propagate skylight down since moss is thin
   @Override
   public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
      return true;
   }

   // Check if the moss block can survive at the given position
   @Override
   public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
      return canAttachTo(worldIn, pos);
   }

   // Ensure the moss can hang from a solid block above or another moss block
   private boolean canAttachTo(IBlockReader worldIn, BlockPos pos) {
      BlockPos blockAbove = pos.above();
      BlockState stateAbove = worldIn.getBlockState(blockAbove);
      return (stateAbove.isFaceSturdy(worldIn, blockAbove, Direction.DOWN) || stateAbove.getBlock() == Blocks.PALE_OAK_LEAVES) || stateAbove.getBlock() instanceof HangingMossBlock;
   }

   @Override
   public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
      super.animateTick(state, world, pos, random);
      BlockState state1 = world.getBlockState(pos.above());
      if (random.nextInt(400) == 0 && ((world.getBlockState(pos.above())).is(Blocks.PALE_OAK_LOG) || state1.is(Blocks.PALE_OAK_LEAVES))) {
         world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.PALE_HANGING_MOSS_IDLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
      }
   }

   // This updates the block's shape when its neighbors change
   @Override
   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
      if (!this.canSurvive(state, world, currentPos)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         // Set TIP based on whether the block below is another moss block
         return state.setValue(TIP, !world.getBlockState(currentPos.below()).is(this));
      }
   }

   // Define block state container to add the TIP property
   @Override
   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(TIP);
   }

   // Handle block placement logic
   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      BlockPos pos = context.getClickedPos();
      if (canAttachTo(context.getLevel(), pos)) {
         return this.defaultBlockState().setValue(TIP, !context.getLevel().getBlockState(pos.below()).is(this));
      }
      return null;
   }
}
