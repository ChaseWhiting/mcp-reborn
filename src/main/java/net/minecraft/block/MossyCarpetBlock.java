package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class MossyCarpetBlock extends Block {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

   public MossyCarpetBlock(Properties properties) {
      super(properties);
   }


   public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
      return !worldIn.isEmptyBlock(pos.below());
   }

//   @Nullable
//   @Override
//   public BlockState getStateForPlacement(BlockItemUseContext context) {
//      BlockPos pos = context.getClickedPos();
//      IWorldReader world = context.getLevel();
//
//      if (canSurvive(this.defaultBlockState(), world, pos)) {
//         return this.defaultBlockState();
//      }
//
//      return null;
//   }
//
//   @Override
//   public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
//      return true;
//   }

   @Override
   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }



   // Ensure the player can destroy the block and drop resources
   @Override
   public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
      super.playerDestroy(worldIn, player, pos, state, te, stack);
      popResource(worldIn, pos, new ItemStack(this));
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }


}
