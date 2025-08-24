package net.minecraft.util.math.vector;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Represents a quaternion, a mathematical object used to encode rotations in 3D space.
 */
public final class Quaternion {
   public static final Quaternion ONE = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   private float i;
   private float j;
   private float k;
   private float r;

   /**
    * Constructs a quaternion with the specified components.
    *
    * @param i the i (x) component of the quaternion
    * @param j the j (y) component of the quaternion
    * @param k the k (z) component of the quaternion
    * @param r the r (w) component of the quaternion
    */
   public Quaternion(float i, float j, float k, float r) {
      this.i = i;
      this.j = j;
      this.k = k;
      this.r = r;
   }

   public float getW() {
      return r;
   }

   public float getX() {
      return i;
   }

   public float getY() {
      return j;
   }

   public float getZ() {
      return k;
   }

   /**
    * Constructs a quaternion representing a rotation around a given axis.
    *
    * @param axis the axis to rotate around
    * @param angle the angle of rotation in degrees
    * @param degrees if true, the angle is specified in degrees; otherwise, it's in radians
    */
   public Quaternion(Vector3f axis, float angle, boolean degrees) {
      if (degrees) {
         angle *= ((float)Math.PI / 180F);
      }

      float sinHalfAngle = sin(angle / 2.0F);
      this.i = axis.x() * sinHalfAngle;
      this.j = axis.y() * sinHalfAngle;
      this.k = axis.z() * sinHalfAngle;
      this.r = cos(angle / 2.0F);
   }

   /**
    * Constructs a quaternion representing rotations around the x, y, and z axes.
    *
    * @param x the rotation around the x-axis
    * @param y the rotation around the y-axis
    * @param z the rotation around the z-axis
    * @param degrees if true, the angles are specified in degrees; otherwise, they're in radians
    */
   @OnlyIn(Dist.CLIENT)
   public Quaternion(float x, float y, float z, boolean degrees) {
      if (degrees) {
         x *= ((float)Math.PI / 180F);
         y *= ((float)Math.PI / 180F);
         z *= ((float)Math.PI / 180F);
      }

      float sinX = sin(0.5F * x);
      float cosX = cos(0.5F * x);
      float sinY = sin(0.5F * y);
      float cosY = cos(0.5F * y);
      float sinZ = sin(0.5F * z);
      float cosZ = cos(0.5F * z);
      this.i = sinX * cosY * cosZ + cosX * sinY * sinZ;
      this.j = cosX * sinY * cosZ - sinX * cosY * sinZ;
      this.k = sinX * sinY * cosZ + cosX * cosY * sinZ;
      this.r = cosX * cosY * cosZ - sinX * sinY * sinZ;
   }

   /**
    * Constructs a quaternion by copying another quaternion.
    *
    * @param other the quaternion to copy
    */
   public Quaternion(Quaternion other) {
      this.i = other.i;
      this.j = other.j;
      this.k = other.k;
      this.r = other.r;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         Quaternion quaternion = (Quaternion) obj;
         return Float.compare(quaternion.i, this.i) == 0 &&
                 Float.compare(quaternion.j, this.j) == 0 &&
                 Float.compare(quaternion.k, this.k) == 0 &&
                 Float.compare(quaternion.r, this.r) == 0;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = Float.floatToIntBits(this.i);
      result = 31 * result + Float.floatToIntBits(this.j);
      result = 31 * result + Float.floatToIntBits(this.k);
      return 31 * result + Float.floatToIntBits(this.r);
   }

   @Override
   public String toString() {
      return "Quaternion[" + this.r() + " + " + this.i() + "i + " + this.j() + "j + " + this.k() + "k]";
   }

   public float i() {
      return this.i;
   }

   public float j() {
      return this.j;
   }

   public float k() {
      return this.k;
   }

   public float r() {
      return this.r;
   }

   /**
    * Multiplies this quaternion by another quaternion.
    *
    * @param other the quaternion to multiply by
    */
   public void mul(Quaternion other) {
      float newI = this.r * other.i + this.i * other.r + this.j * other.k - this.k * other.j;
      float newJ = this.r * other.j - this.i * other.k + this.j * other.r + this.k * other.i;
      float newK = this.r * other.k + this.i * other.j - this.j * other.i + this.k * other.r;
      float newR = this.r * other.r - this.i * other.i - this.j * other.j - this.k * other.k;
      this.i = newI;
      this.j = newJ;
      this.k = newK;
      this.r = newR;
   }

   /**
    * Multiplies this quaternion by a scalar value.
    *
    * @param scalar the scalar value to multiply by
    */
   @OnlyIn(Dist.CLIENT)
   public void mul(float scalar) {
      this.i *= scalar;
      this.j *= scalar;
      this.k *= scalar;
      this.r *= scalar;
   }

   /**
    * Conjugates this quaternion (inverts the vector part).
    */
   public void conj() {
      this.i = -this.i;
      this.j = -this.j;
      this.k = -this.k;
   }

   /**
    * Sets the components of this quaternion.
    *
    * @param i the i (x) component
    * @param j the j (y) component
    * @param k the k (z) component
    * @param r the r (w) component
    */
   @OnlyIn(Dist.CLIENT)
   public void set(float i, float j, float k, float r) {
      this.i = i;
      this.j = j;
      this.k = k;
      this.r = r;
   }

   public static float cos(float value) {
      return (float)Math.cos((double)value);
   }

   public static float sin(float value) {
      return (float)Math.sin((double)value);
   }

   /**
    * Normalizes this quaternion to have a unit length of 1.
    */
   @OnlyIn(Dist.CLIENT)
   public void normalize() {
      float lengthSquared = this.i * this.i + this.j * this.j + this.k * this.k + this.r * this.r;
      if (lengthSquared > 1.0E-6F) {
         float inverseSqrt = MathHelper.fastInvSqrt(lengthSquared);
         this.i *= inverseSqrt;
         this.j *= inverseSqrt;
         this.k *= inverseSqrt;
         this.r *= inverseSqrt;
      } else {
         this.i = 0.0F;
         this.j = 0.0F;
         this.k = 0.0F;
         this.r = 0.0F;
      }
   }

   /**
    * Creates a copy of this quaternion.
    *
    * @return a new quaternion that is a copy of this one
    */
   @OnlyIn(Dist.CLIENT)
   public Quaternion copy() {
      return new Quaternion(this);
   }
}
