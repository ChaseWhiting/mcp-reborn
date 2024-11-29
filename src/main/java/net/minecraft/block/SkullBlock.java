package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class SkullBlock extends AbstractSkullBlock {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);

   protected SkullBlock(SkullBlock.ISkullType p_i48332_1_, AbstractBlock.Properties p_i48332_2_) {
      super(p_i48332_1_, p_i48332_2_);
      this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public VoxelShape getOcclusionShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
      return VoxelShapes.empty();
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(ROTATION, Integer.valueOf(MathHelper.floor((double)(p_196258_1_.getRotation() * 16.0F / 360.0F) + 0.5D) & 15));
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return state.setValue(ROTATION, Integer.valueOf(rotation.rotate(state.getValue(ROTATION), 16)));
   }

   public BlockState mirror(BlockState state, Mirror mirroring) {
      return state.setValue(ROTATION, Integer.valueOf(mirroring.mirror(state.getValue(ROTATION), 16)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(ROTATION);
   }

   public interface ISkullType {
   }

   public static enum Types implements SkullBlock.ISkullType {
      SKELETON,
      WITHER_SKELETON,
      PLAYER,
      ZOMBIE,
      CREEPER,
      DRAGON;
   }
}