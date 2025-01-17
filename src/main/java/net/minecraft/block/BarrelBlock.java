package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BarrelBlock extends ContainerBlock {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

   public BarrelBlock(AbstractBlock.Properties p_i49996_1_) {
      super(p_i49996_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.valueOf(false)));
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isClientSide) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity tileentity = p_225533_2_.getBlockEntity(p_225533_3_);
         if (tileentity instanceof BarrelTileEntity) {
            p_225533_4_.openMenu((BarrelTileEntity)tileentity);
            p_225533_4_.awardStat(Stats.OPEN_BARREL);
            PiglinTasks.angerNearbyPiglins(p_225533_4_, true);
         }

         return ActionResultType.CONSUME;
      }
   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_1_.is(p_196243_4_.getBlock())) {
         TileEntity tileentity = p_196243_2_.getBlockEntity(p_196243_3_);
         if (tileentity instanceof IInventory) {
            InventoryHelper.dropContents(p_196243_2_, p_196243_3_, (IInventory)tileentity);
            p_196243_2_.updateNeighbourForOutputSignal(p_196243_3_, this);
         }

         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      TileEntity tileentity = p_225534_2_.getBlockEntity(p_225534_3_);
      if (tileentity instanceof BarrelTileEntity) {
         ((BarrelTileEntity)tileentity).recheckOpen();
      }

   }

   @Nullable
   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new BarrelTileEntity();
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasCustomHoverName()) {
         TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
         if (tileentity instanceof BarrelTileEntity) {
            ((BarrelTileEntity)tileentity).setCustomName(p_180633_5_.getHoverName());
         }
      }

   }

   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.getRedstoneSignalFromBlockEntity(p_180641_2_.getBlockEntity(p_180641_3_));
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirroring) {
      return state.rotate(mirroring.getRotation(state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, OPEN);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(FACING, p_196258_1_.getNearestLookingDirection().getOpposite());
   }
}