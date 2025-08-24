package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractButtonBlock extends HorizontalFaceBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   protected static final VoxelShape CEILING_AABB_X = Block.box(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
   protected static final VoxelShape CEILING_AABB_Z = Block.box(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
   protected static final VoxelShape FLOOR_AABB_X = Block.box(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
   protected static final VoxelShape FLOOR_AABB_Z = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
   protected static final VoxelShape WEST_AABB = Block.box(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
   protected static final VoxelShape PRESSED_CEILING_AABB_X = Block.box(6.0D, 15.0D, 5.0D, 10.0D, 16.0D, 11.0D);
   protected static final VoxelShape PRESSED_CEILING_AABB_Z = Block.box(5.0D, 15.0D, 6.0D, 11.0D, 16.0D, 10.0D);
   protected static final VoxelShape PRESSED_FLOOR_AABB_X = Block.box(6.0D, 0.0D, 5.0D, 10.0D, 1.0D, 11.0D);
   protected static final VoxelShape PRESSED_FLOOR_AABB_Z = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 1.0D, 10.0D);
   protected static final VoxelShape PRESSED_NORTH_AABB = Block.box(5.0D, 6.0D, 15.0D, 11.0D, 10.0D, 16.0D);
   protected static final VoxelShape PRESSED_SOUTH_AABB = Block.box(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 1.0D);
   protected static final VoxelShape PRESSED_WEST_AABB = Block.box(15.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
   protected static final VoxelShape PRESSED_EAST_AABB = Block.box(0.0D, 6.0D, 5.0D, 1.0D, 10.0D, 11.0D);
   private final boolean sensitive;

   protected AbstractButtonBlock(boolean p_i48436_1_, Properties p_i48436_2_) {
      super(p_i48436_2_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(FACE, AttachFace.WALL));
      this.sensitive = p_i48436_1_;
   }

   public int getPressDuration() {
      if (this.isCopper()) {
         CopperButtonBlock block = (CopperButtonBlock) this;
         return switch(block.getAge()) {
             case UNAFFECTED -> 15;
             case EXPOSED -> 30;
             case WEATHERED -> 45;
             case OXIDIZED -> 75;
         };
      }

      return this.sensitive ? 30 : 20;
   }

   public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      Direction direction = state.getValue(FACING);
      boolean flag = state.getValue(POWERED);
      switch((AttachFace) state.getValue(FACE)) {
      case FLOOR:
         if (direction.getAxis() == Direction.Axis.X) {
            return flag ? PRESSED_FLOOR_AABB_X : FLOOR_AABB_X;
         }

         return flag ? PRESSED_FLOOR_AABB_Z : FLOOR_AABB_Z;
      case WALL:
         switch(direction) {
         case EAST:
            return flag ? PRESSED_EAST_AABB : EAST_AABB;
         case WEST:
            return flag ? PRESSED_WEST_AABB : WEST_AABB;
         case SOUTH:
            return flag ? PRESSED_SOUTH_AABB : SOUTH_AABB;
         case NORTH:
         default:
            return flag ? PRESSED_NORTH_AABB : NORTH_AABB;
         }
      case CEILING:
      default:
         if (direction.getAxis() == Direction.Axis.X) {
            return flag ? PRESSED_CEILING_AABB_X : CEILING_AABB_X;
         } else {
            return flag ? PRESSED_CEILING_AABB_Z : CEILING_AABB_Z;
         }
      }
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_1_.getValue(POWERED)) {
         return ActionResultType.CONSUME;
      } else {
         this.press(p_225533_1_, p_225533_2_, p_225533_3_);
         this.playSound(p_225533_4_, p_225533_2_, p_225533_3_, true);
         p_225533_2_.gameEvent(p_225533_4_, GameEvent.BLOCK_ACTIVATE, p_225533_3_);
         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      }
   }

   public void press(BlockState p_226910_1_, World p_226910_2_, BlockPos p_226910_3_) {
      p_226910_2_.setBlock(p_226910_3_, p_226910_1_.setValue(POWERED, Boolean.valueOf(true)), 3);
      this.updateNeighbours(p_226910_1_, p_226910_2_, p_226910_3_);
      p_226910_2_.getBlockTicks().scheduleTick(p_226910_3_, this, this.getPressDuration());
   }

   protected void playSound(@Nullable PlayerEntity p_196367_1_, IWorld p_196367_2_, BlockPos p_196367_3_, boolean p_196367_4_) {
      p_196367_2_.playSound(p_196367_4_ ? p_196367_1_ : null, p_196367_3_, this.getSound(p_196367_4_), SoundCategory.BLOCKS, this.isCopper() ? 1.2F : 0.3F, p_196367_4_ ? 0.6F : this.isCopper() ? 1.0F : 0.5F);
   }

   public boolean isCopper() {
      return this instanceof CopperButtonBlock;
   }

   protected abstract SoundEvent getSound(boolean p_196369_1_);

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && !p_196243_1_.is(p_196243_4_.getBlock())) {
         if (p_196243_1_.getValue(POWERED)) {
            this.updateNeighbours(p_196243_1_, p_196243_2_, p_196243_3_);
         }

         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return p_180656_1_.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return p_176211_1_.getValue(POWERED) && getConnectedDirection(p_176211_1_) == p_176211_4_ ? 15 : 0;
   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return true;
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_1_.getValue(POWERED)) {
         if (this.sensitive) {
            this.checkPressed(p_225534_1_, p_225534_2_, p_225534_3_);
         } else {
            p_225534_2_.setBlock(p_225534_3_, p_225534_1_.setValue(POWERED, Boolean.valueOf(false)), 3);
            this.updateNeighbours(p_225534_1_, p_225534_2_, p_225534_3_);
            this.playSound((PlayerEntity)null, p_225534_2_, p_225534_3_, false);
         }

      }
   }

   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isClientSide && this.sensitive && !p_196262_1_.getValue(POWERED)) {
         this.checkPressed(p_196262_1_, p_196262_2_, p_196262_3_);
      }
   }

   private void checkPressed(BlockState p_185616_1_, World level, BlockPos blockPos) {
      List<? extends Entity> list = level.getEntitiesOfClass(AbstractArrowEntity.class, p_185616_1_.getShape(level, blockPos).bounds().move(blockPos));
      boolean flag = !list.isEmpty();
      boolean flag1 = p_185616_1_.getValue(POWERED);
      if (flag != flag1) {
         level.setBlock(blockPos, p_185616_1_.setValue(POWERED, Boolean.valueOf(flag)), 3);
         this.updateNeighbours(p_185616_1_, level, blockPos);
         this.playSound((PlayerEntity)null, level, blockPos, flag);
         level.gameEvent((Entity)list.stream().findFirst().orElse(null), flag1 ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, blockPos);

      }

      if (flag) {
         level.getBlockTicks().scheduleTick(new BlockPos(blockPos), this, this.getPressDuration());
      }

   }

   public void updateNeighbours(BlockState p_196368_1_, World p_196368_2_, BlockPos p_196368_3_) {
      p_196368_2_.updateNeighborsAt(p_196368_3_, this);
      p_196368_2_.updateNeighborsAt(p_196368_3_.relative(getConnectedDirection(p_196368_1_).getOpposite()), this);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING, POWERED, FACE);
   }
}