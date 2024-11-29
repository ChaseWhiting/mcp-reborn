package net.minecraft.util.math.vector;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;

/**
 * Represents a 3D vector with double precision.
 */
public class Vector3d implements IPosition {
   public static final Vector3d ZERO = new Vector3d(0.0D, 0.0D, 0.0D);
   public static final Vector3d UP = new Vector3d(0.0, 1.0, 0.0);
   public final double x;
   public final double y;
   public final double z;

   /**
    * Creates a Vector3d from an RGB color represented as an integer.
    *
    * @param rgb the RGB color
    * @return a new Vector3d with x, y, and z components corresponding to the RGB color
    */
   @OnlyIn(Dist.CLIENT)
   public static Vector3d fromRGB24(int rgb) {
      double d0 = (double)(rgb >> 16 & 255) / 255.0D;
      double d1 = (double)(rgb >> 8 & 255) / 255.0D;
      double d2 = (double)(rgb & 255) / 255.0D;
      return new Vector3d(d0, d1, d2);
   }

   /**
    * Creates a Vector3d at the center of a given block position.
    *
    * @param position the block position
    * @return a new Vector3d at the center of the given block position
    */
   public static Vector3d atCenterOf(Vector3i position) {
      return new Vector3d((double)position.getX() + 0.5D, (double)position.getY() + 0.5D, (double)position.getZ() + 0.5D);
   }

   /**
    * Creates a Vector3d at the lower corner of a given block position.
    *
    * @param position the block position
    * @return a new Vector3d at the lower corner of the given block position
    */
   public static Vector3d atLowerCornerOf(Vector3i position) {
      return new Vector3d((double)position.getX(), (double)position.getY(), (double)position.getZ());
   }

   /**
    * Creates a Vector3d at the bottom center of a given block position.
    *
    * @param position the block position
    * @return a new Vector3d at the bottom center of the given block position
    */
   public static Vector3d atBottomCenterOf(Vector3i position) {
      return new Vector3d((double)position.getX() + 0.5D, (double)position.getY(), (double)position.getZ() + 0.5D);
   }

   /**
    * Creates a Vector3d at the bottom center of a given block position, offset by a specified height.
    *
    * @param position the block position
    * @param yOffset the height offset
    * @return a new Vector3d at the bottom center of the given block position, offset by the specified height
    */
   public static Vector3d upFromBottomCenterOf(Vector3i position, double yOffset) {
      return new Vector3d((double)position.getX() + 0.5D, (double)position.getY() + yOffset, (double)position.getZ() + 0.5D);
   }

   /**
    * Constructs a Vector3d with the specified x, y, and z components.
    *
    * @param x the x component
    * @param y the y component
    * @param z the z component
    */
   public Vector3d(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   /**
    * Constructs a Vector3d from a Vector3f.
    *
    * @param vector the Vector3f to copy from
    */
   public Vector3d(Vector3f vector) {
      this((double)vector.x(), (double)vector.y(), (double)vector.z());
   }

   /**
    * Returns a new vector pointing from this vector to the given vector.
    *
    * @param target the target vector
    * @return a new Vector3d pointing from this vector to the target vector
    */
   public Vector3d vectorTo(Vector3d target) {
      return new Vector3d(target.x - this.x, target.y - this.y, target.z - this.z);
   }

   /**
    * Normalizes this vector to have a length of 1.
    *
    * @return a new normalized Vector3d
    */
   public Vector3d normalize() {
      double length = (double)MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      return length < 1.0E-4D ? ZERO : new Vector3d(this.x / length, this.y / length, this.z / length);
   }

   /**
    * Computes the dot product of this vector with another vector.
    *
    * @param vector the other vector
    * @return the dot product
    */
   public double dot(Vector3d vector) {
      return this.x * vector.x + this.y * vector.y + this.z * vector.z;
   }

   /**
    * Computes the cross product of this vector with another vector.
    *
    * @param vector the other vector
    * @return the cross product as a new Vector3d
    */
   public Vector3d cross(Vector3d vector) {
      return new Vector3d(this.y * vector.z - this.z * vector.y, this.z * vector.x - this.x * vector.z, this.x * vector.y - this.y * vector.x);
   }

   /**
    * Subtracts the given vector from this vector.
    *
    * @param vector the vector to subtract
    * @return a new Vector3d representing the result
    */
   public Vector3d subtract(Vector3d vector) {
      return this.subtract(vector.x, vector.y, vector.z);
   }

   /**
    * Subtracts the given components from this vector.
    *
    * @param x the x component to subtract
    * @param y the y component to subtract
    * @param z the z component to subtract
    * @return a new Vector3d representing the result
    */
   public Vector3d subtract(double x, double y, double z) {
      return this.add(-x, -y, -z);
   }

   /**
    * Adds the given vector to this vector.
    *
    * @param vector the vector to add
    * @return a new Vector3d representing the result
    */
   public Vector3d add(Vector3d vector) {
      return this.add(vector.x, vector.y, vector.z);
   }

   /**
    * Adds the given components to this vector.
    *
    * @param x the x component to add
    * @param y the y component to add
    * @param z the z component to add
    * @return a new Vector3d representing the result
    */
   public Vector3d add(double x, double y, double z) {
      return new Vector3d(this.x + x, this.y + y, this.z + z);
   }

   /**
    * Checks if this vector is closer than a specified distance to a given position.
    *
    * @param position the position to check against
    * @param distance the distance to check
    * @return true if this vector is closer than the specified distance to the position, false otherwise
    */
   public boolean closerThan(IPosition position, double distance) {
      return this.distanceToSqr(position.x(), position.y(), position.z()) < distance * distance;
   }

   /**
    * Computes the Euclidean distance to another vector.
    *
    * @param vector the other vector
    * @return the distance
    */
   public double distanceTo(Vector3d vector) {
      double dx = vector.x - this.x;
      double dy = vector.y - this.y;
      double dz = vector.z - this.z;
      return (double)MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
   }

   /**
    * Computes the squared Euclidean distance to another vector.
    *
    * @param vector the other vector
    * @return the squared distance
    */
   public double distanceToSqr(Vector3d vector) {
      double dx = vector.x - this.x;
      double dy = vector.y - this.y;
      double dz = vector.z - this.z;
      return dx * dx + dy * dy + dz * dz;
   }

   /**
    * Computes the squared Euclidean distance to specified coordinates.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    * @param z the z coordinate
    * @return the squared distance
    */
   public double distanceToSqr(double x, double y, double z) {
      double dx = x - this.x;
      double dy = y - this.y;
      double dz = z - this.z;
      return dx * dx + dy * dy + dz * dz;
   }

   /**
    * Scales this vector by a given factor.
    *
    * @param factor the factor to scale by
    * @return a new scaled Vector3d
    */
   public Vector3d scale(double factor) {
      return this.multiply(factor, factor, factor);
   }

   /**
    * Reverses the direction of this vector.
    *
    * @return a new reversed Vector3d
    */
   @OnlyIn(Dist.CLIENT)
   public Vector3d reverse() {
      return this.scale(-1.0D);
   }

   /**
    * Multiplies this vector by another vector component-wise.
    *
    * @param vector the other vector
    * @return a new Vector3d representing the result
    */
   public Vector3d multiply(Vector3d vector) {
      return this.multiply(vector.x, vector.y, vector.z);
   }

   public Vector3d multiply(double val) {
      return this.multiply(val, val, val);
   }

   /**
    * Multiplies this vector by specified components.
    *
    * @param x the x component to multiply by
    * @param y the y component to multiply by
    * @param z the z component to multiply by
    * @return a new Vector3d representing the result
    */
   public Vector3d multiply(double x, double y, double z) {
      return new Vector3d(this.x * x, this.y * y, this.z * z);
   }

   /**
    * Computes the length (magnitude) of this vector.
    *
    * @return the length of the vector
    */
   public double length() {
      return (double)MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   /**
    * Computes the squared length (magnitude) of this vector.
    *
    * @return the squared length of the vector
    */
   public double lengthSqr() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof Vector3d)) {
         return false;
      } else {
         Vector3d vector = (Vector3d) obj;
         return Double.compare(vector.x, this.x) == 0 &&
                 Double.compare(vector.y, this.y) == 0 &&
                 Double.compare(vector.z, this.z) == 0;
      }
   }

   @Override
   public int hashCode() {
      long j = Double.doubleToLongBits(this.x);
      int result = (int)(j ^ j >>> 32);
      j = Double.doubleToLongBits(this.y);
      result = 31 * result + (int)(j ^ j >>> 32);
      j = Double.doubleToLongBits(this.z);
      return 31 * result + (int)(j ^ j >>> 32);
   }

   @Override
   public String toString() {
      return "(" + this.x + ", " + this.y + ", " + this.z + ")";
   }

   /**
    * Rotates this vector around the x-axis.
    *
    * @param angle the angle to rotate by in radians
    * @return a new Vector3d representing the result
    */
   public Vector3d xRot(float angle) {
      float cosAngle = MathHelper.cos(angle);
      float sinAngle = MathHelper.sin(angle);
      double d0 = this.x;
      double d1 = this.y * cosAngle + this.z * sinAngle;
      double d2 = this.z * cosAngle - this.y * sinAngle;
      return new Vector3d(d0, d1, d2);
   }

   /**
    * Rotates this vector around the y-axis.
    *
    * @param angle the angle to rotate by in radians
    * @return a new Vector3d representing the result
    */
   public Vector3d yRot(float angle) {
      float cosAngle = MathHelper.cos(angle);
      float sinAngle = MathHelper.sin(angle);
      double d0 = this.x * cosAngle + this.z * sinAngle;
      double d1 = this.y;
      double d2 = this.z * cosAngle - this.x * sinAngle;
      return new Vector3d(d0, d1, d2);
   }

   /**
    * Rotates this vector around the z-axis.
    *
    * @param angle the angle to rotate by in radians
    * @return a new Vector3d representing the result
    */
   @OnlyIn(Dist.CLIENT)
   public Vector3d zRot(float angle) {
      float cosAngle = MathHelper.cos(angle);
      float sinAngle = MathHelper.sin(angle);
      double d0 = this.x * cosAngle + this.y * sinAngle;
      double d1 = this.y * cosAngle - this.x * sinAngle;
      double d2 = this.z;
      return new Vector3d(d0, d1, d2);
   }

   /**
    * Computes the direction vector from rotation angles.
    *
    * @param rotation the rotation angles
    * @return a new Vector3d representing the direction
    */
   @OnlyIn(Dist.CLIENT)
   public static Vector3d directionFromRotation(Vector2f rotation) {
      return directionFromRotation(rotation.x, rotation.y);
   }

   public static Vector3d randomPointBehindTarget(LivingEntity livingEntity, Random random, float minDistance, float maxDistance) {
      float yawBehindTarget = livingEntity.yHeadRot + 180.0F + (float)random.nextGaussian() * 45.0F;

      float randomDistance = MathHelper.lerp(random.nextFloat(), minDistance, maxDistance);

      Vector3d direction = directionFromRotation(0.0F, yawBehindTarget).scale((double)randomDistance);

      return livingEntity.position().add(direction);
   }

   /**
    * Computes the direction vector from rotation angles.
    *
    * @param pitch the pitch angle in degrees
    * @param yaw the yaw angle in degrees
    * @return a new Vector3d representing the direction
    */
   @OnlyIn(Dist.CLIENT)
   public static Vector3d directionFromRotation(float pitch, float yaw) {
      float cosYaw = MathHelper.cos(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
      float sinYaw = MathHelper.sin(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
      float cosPitch = -MathHelper.cos(-pitch * ((float)Math.PI / 180F));
      float sinPitch = MathHelper.sin(-pitch * ((float)Math.PI / 180F));
      return new Vector3d((double)(sinYaw * cosPitch), (double)sinPitch, (double)(cosYaw * cosPitch));
   }

   /**
    * Aligns this vector to the nearest lower integer values for the specified axes.
    *
    * @param axes the axes to align
    * @return a new Vector3d representing the aligned vector
    */
   public Vector3d align(EnumSet<Direction.Axis> axes) {
      double d0 = axes.contains(Direction.Axis.X) ? (double)MathHelper.floor(this.x) : this.x;
      double d1 = axes.contains(Direction.Axis.Y) ? (double)MathHelper.floor(this.y) : this.y;
      double d2 = axes.contains(Direction.Axis.Z) ? (double)MathHelper.floor(this.z) : this.z;
      return new Vector3d(d0, d1, d2);
   }

   /**
    * Gets the value of this vector along the specified axis.
    *
    * @param axis the axis
    * @return the value along the specified axis
    */
   public double get(Direction.Axis axis) {
      return axis.choose(this.x, this.y, this.z);
   }

   @Override
   public final double x() {
      return this.x;
   }

   @Override
   public final double y() {
      return this.y;
   }

   @Override
   public final double z() {
      return this.z;
   }

   public Vector3d(BlockPos pos) {
      this(pos.getX(),pos.getY(),pos.getZ());
   }

   public BlockPos asBlockPos() {
      return new BlockPos(this.x,this.y,this.z);
   }
   public static double lerp(double start, double end, double factor) {
      return start + factor * (end - start);
   }

   public static Vector3d lerp(Vector3d start, Vector3d end, double factor) {
      double x = lerp(start.x, end.x, factor);
      double y = lerp(start.y, end.y, factor);
      double z = lerp(start.z, end.z, factor);
      return new Vector3d(x, y, z);
   }

   public Vector3d orthogonal() throws MathArithmeticException {
      double threshold = 0.6 * this.getNorm();
      if (threshold == 0.0) {
         throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
      } else {
         double inverse;
         if (FastMath.abs(this.x) <= threshold) {
            inverse = 1.0 / FastMath.sqrt(this.y * this.y + this.z * this.z);
            return new Vector3d(0.0, inverse * this.z, -inverse * this.y);
         } else if (FastMath.abs(this.y) <= threshold) {
            inverse = 1.0 / FastMath.sqrt(this.x * this.x + this.z * this.z);
            return new Vector3d(-inverse * this.z, 0.0, inverse * this.x);
         } else {
            inverse = 1.0 / FastMath.sqrt(this.x * this.x + this.y * this.y);
            return new Vector3d(inverse * this.y, -inverse * this.x, 0.0);
         }
      }
   }



   public double getNorm() {
      return FastMath.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

}
