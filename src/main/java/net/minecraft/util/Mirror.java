package net.minecraft.util;

import net.minecraft.util.math.vector.Orientation;

public enum Mirror {
   NONE(Orientation.IDENTITY),
   LEFT_RIGHT(Orientation.INVERT_Z),
   FRONT_BACK(Orientation.INVERT_X);

   private final Orientation rotation;

   private Mirror(Orientation orientation) {
      this.rotation = orientation;
   }

   public int mirror(int i1, int i2) {
      int i = i2 / 2;
      int j = i1 > i ? i1 - i2 : i1;
      switch(this) {
      case FRONT_BACK:
         return (i2 - j) % i2;
      case LEFT_RIGHT:
         return (i - j + i2) % i2;
      default:
         return i1;
      }
   }

   public Rotation getRotation(Direction direction) {
      Direction.Axis direction$axis = direction.getAxis();
      return (this != LEFT_RIGHT || direction$axis != Direction.Axis.Z) && (this != FRONT_BACK || direction$axis != Direction.Axis.X) ? Rotation.NONE : Rotation.CLOCKWISE_180;
   }

   public Direction mirror(Direction direction) {
      if (this == FRONT_BACK && direction.getAxis() == Direction.Axis.X) {
         return direction.getOpposite();
      } else {
         return this == LEFT_RIGHT && direction.getAxis() == Direction.Axis.Z ? direction.getOpposite() : direction;
      }
   }

   public Orientation rotation() {
      return this.rotation;
   }
}