package net.minecraft.block;

import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailBlock extends AbstractRailBlock {
   public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;

   protected RailBlock(AbstractBlock.Properties p_i48346_1_) {
      super(false, p_i48346_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, RailShape.NORTH_SOUTH));
   }

   protected void updateState(BlockState p_189541_1_, World p_189541_2_, BlockPos p_189541_3_, Block p_189541_4_) {
      if (p_189541_4_.defaultBlockState().isSignalSource() && (new RailState(p_189541_2_, p_189541_3_, p_189541_1_)).countPotentialConnections() == 3) {
         this.updateDir(p_189541_2_, p_189541_3_, p_189541_1_, false);
      }

   }

   public Property<RailShape> getShapeProperty() {
      return SHAPE;
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      switch(rotation) {
      case CLOCKWISE_180:
         switch((RailShape) state.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return state.setValue(SHAPE, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return state.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return state.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return state.setValue(SHAPE, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape) state.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return state.setValue(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return state.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return state.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return state.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_SOUTH:
            return state.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return state.setValue(SHAPE, RailShape.NORTH_SOUTH);
         }
      case CLOCKWISE_90:
         switch((RailShape) state.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return state.setValue(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return state.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return state.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return state.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_SOUTH:
            return state.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return state.setValue(SHAPE, RailShape.NORTH_SOUTH);
         }
      default:
         return state;
      }
   }

   public BlockState mirror(BlockState state, Mirror mirroring) {
      RailShape railshape = state.getValue(SHAPE);
      switch(mirroring) {
      case LEFT_RIGHT:
         switch(railshape) {
         case ASCENDING_NORTH:
            return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return state.setValue(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return state.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return state.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return state.setValue(SHAPE, RailShape.SOUTH_EAST);
         default:
            return super.mirror(state, mirroring);
         }
      case FRONT_BACK:
         switch(railshape) {
         case ASCENDING_EAST:
            return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return state.setValue(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return state.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return state.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return state.setValue(SHAPE, RailShape.NORTH_WEST);
         }
      }

      return super.mirror(state, mirroring);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(SHAPE);
   }
}