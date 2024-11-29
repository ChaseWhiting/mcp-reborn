package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class FourWayBlock extends Block implements IWaterLoggable {
   public static final BooleanProperty NORTH = SixWayBlock.NORTH;
   public static final BooleanProperty EAST = SixWayBlock.EAST;
   public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
   public static final BooleanProperty WEST = SixWayBlock.WEST;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = SixWayBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((p_199775_0_) -> {
      return p_199775_0_.getKey().getAxis().isHorizontal();
   }).collect(Util.toMap());
   protected final VoxelShape[] collisionShapeByIndex;
   protected final VoxelShape[] shapeByIndex;
   private final Object2IntMap<BlockState> stateToIndex = new Object2IntOpenHashMap<>();

   protected FourWayBlock(float p_i48420_1_, float p_i48420_2_, float p_i48420_3_, float p_i48420_4_, float p_i48420_5_, AbstractBlock.Properties p_i48420_6_) {
      super(p_i48420_6_);
      this.collisionShapeByIndex = this.makeShapes(p_i48420_1_, p_i48420_2_, p_i48420_5_, 0.0F, p_i48420_5_);
      this.shapeByIndex = this.makeShapes(p_i48420_1_, p_i48420_2_, p_i48420_3_, 0.0F, p_i48420_4_);

      for(BlockState blockstate : this.stateDefinition.getPossibleStates()) {
         this.getAABBIndex(blockstate);
      }

   }

   protected VoxelShape[] makeShapes(float p_196408_1_, float p_196408_2_, float p_196408_3_, float p_196408_4_, float p_196408_5_) {
      float f = 8.0F - p_196408_1_;
      float f1 = 8.0F + p_196408_1_;
      float f2 = 8.0F - p_196408_2_;
      float f3 = 8.0F + p_196408_2_;
      VoxelShape voxelshape = Block.box((double)f, 0.0D, (double)f, (double)f1, (double)p_196408_3_, (double)f1);
      VoxelShape voxelshape1 = Block.box((double)f2, (double)p_196408_4_, 0.0D, (double)f3, (double)p_196408_5_, (double)f3);
      VoxelShape voxelshape2 = Block.box((double)f2, (double)p_196408_4_, (double)f2, (double)f3, (double)p_196408_5_, 16.0D);
      VoxelShape voxelshape3 = Block.box(0.0D, (double)p_196408_4_, (double)f2, (double)f3, (double)p_196408_5_, (double)f3);
      VoxelShape voxelshape4 = Block.box((double)f2, (double)p_196408_4_, (double)f2, 16.0D, (double)p_196408_5_, (double)f3);
      VoxelShape voxelshape5 = VoxelShapes.or(voxelshape1, voxelshape4);
      VoxelShape voxelshape6 = VoxelShapes.or(voxelshape2, voxelshape3);
      VoxelShape[] avoxelshape = new VoxelShape[]{VoxelShapes.empty(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, VoxelShapes.or(voxelshape2, voxelshape1), VoxelShapes.or(voxelshape3, voxelshape1), VoxelShapes.or(voxelshape6, voxelshape1), voxelshape4, VoxelShapes.or(voxelshape2, voxelshape4), VoxelShapes.or(voxelshape3, voxelshape4), VoxelShapes.or(voxelshape6, voxelshape4), voxelshape5, VoxelShapes.or(voxelshape2, voxelshape5), VoxelShapes.or(voxelshape3, voxelshape5), VoxelShapes.or(voxelshape6, voxelshape5)};

      for(int i = 0; i < 16; ++i) {
         avoxelshape[i] = VoxelShapes.or(voxelshape, avoxelshape[i]);
      }

      return avoxelshape;
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return !p_200123_1_.getValue(WATERLOGGED);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.shapeByIndex[this.getAABBIndex(p_220053_1_)];
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return this.collisionShapeByIndex[this.getAABBIndex(p_220071_1_)];
   }

   private static int indexFor(Direction p_196407_0_) {
      return 1 << p_196407_0_.get2DDataValue();
   }

   protected int getAABBIndex(BlockState p_196406_1_) {
      return this.stateToIndex.computeIntIfAbsent(p_196406_1_, (p_223007_0_) -> {
         int i = 0;
         if (p_223007_0_.getValue(NORTH)) {
            i |= indexFor(Direction.NORTH);
         }

         if (p_223007_0_.getValue(EAST)) {
            i |= indexFor(Direction.EAST);
         }

         if (p_223007_0_.getValue(SOUTH)) {
            i |= indexFor(Direction.SOUTH);
         }

         if (p_223007_0_.getValue(WEST)) {
            i |= indexFor(Direction.WEST);
         }

         return i;
      });
   }

   public FluidState getFluidState(BlockState p_204507_1_) {
      return p_204507_1_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_204507_1_);
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      switch(rotation) {
      case CLOCKWISE_180:
         return state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
      case COUNTERCLOCKWISE_90:
         return state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
      case CLOCKWISE_90:
         return state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
      default:
         return state;
      }
   }

   public BlockState mirror(BlockState state, Mirror mirroring) {
      switch(mirroring) {
      case LEFT_RIGHT:
         return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
      case FRONT_BACK:
         return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
      default:
         return super.mirror(state, mirroring);
      }
   }
}