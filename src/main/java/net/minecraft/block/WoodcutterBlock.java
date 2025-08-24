package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.StonecutterContainer;
import net.minecraft.inventory.container.WoodcutterContainer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WoodcutterBlock extends Block {
   private static final ITextComponent CONTAINER_TITLE = new TranslationTextComponent("container.woodcutter");
   public static final DirectionProperty FACING = HorizontalBlock.FACING;
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);

   public WoodcutterBlock(Properties p_i49972_1_) {
      super(p_i49972_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isClientSide) {
         return ActionResultType.SUCCESS;
      } else {
         p_225533_4_.openMenu(p_225533_1_.getMenuProvider(p_225533_2_, p_225533_3_));
         p_225533_4_.awardStat(Stats.INTERACT_WITH_WOODCUTTER);
         return ActionResultType.CONSUME;
      }
   }

   @Nullable
   public INamedContainerProvider getMenuProvider(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      return new SimpleNamedContainerProvider((p_220283_2_, p_220283_3_, p_220283_4_) -> {
         return new WoodcutterContainer(p_220283_2_, p_220283_3_, IWorldPosCallable.create(p_220052_2_, p_220052_3_));
      }, CONTAINER_TITLE);
   }

   public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
      return true;
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirroring) {
      return state.rotate(mirroring.getRotation(state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}