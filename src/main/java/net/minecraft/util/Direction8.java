package net.minecraft.util;

import com.google.common.collect.Sets;
import net.minecraft.util.math.vector.Vector3i;

import java.util.Arrays;
import java.util.Set;

public enum Direction8 {
   NORTH(Direction.NORTH),
   NORTH_EAST(Direction.NORTH, Direction.EAST),
   EAST(Direction.EAST),
   SOUTH_EAST(Direction.SOUTH, Direction.EAST),
   SOUTH(Direction.SOUTH),
   SOUTH_WEST(Direction.SOUTH, Direction.WEST),
   WEST(Direction.WEST),
   NORTH_WEST(Direction.NORTH, Direction.WEST);

   private static final int NORTH_WEST_MASK = 1 << NORTH_WEST.ordinal();
   private static final int WEST_MASK = 1 << WEST.ordinal();
   private static final int SOUTH_WEST_MASK = 1 << SOUTH_WEST.ordinal();
   private static final int SOUTH_MASK = 1 << SOUTH.ordinal();
   private static final int SOUTH_EAST_MASK = 1 << SOUTH_EAST.ordinal();
   private static final int EAST_MASK = 1 << EAST.ordinal();
   private static final int NORTH_EAST_MASK = 1 << NORTH_EAST.ordinal();
   private static final int NORTH_MASK = 1 << NORTH.ordinal();
   private final Set<Direction> directions;
   private final Vector3i step;

   private Direction8(Direction... p_i47954_3_) {
      this.directions = Sets.immutableEnumSet(Arrays.asList(p_i47954_3_));

      this.step = new Vector3i(0, 0, 0);
      for (Direction direction : p_i47954_3_) {
         this.step.setXPublic(this.step.getX() + direction.getStepX()).setYPublic(this.step.getY() + direction.getStepY()).setZPublic(this.step.getZ() + direction.getStepZ());
      }
   }

   public Set<Direction> getDirections() {
      return this.directions;
   }


   public int getStepX() {
      return this.step.getX();
   }

   public int getStepZ() {
      return this.step.getZ();
   }
}