package net.minecraft.util.math;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AxisAlignedBB {
   public final double minX;
   public final double minY;
   public final double minZ;
   public final double maxX;
   public final double maxY;
   public final double maxZ;

   /**
    * Constructs an AxisAlignedBB (Axis Aligned Bounding Box) with the specified minimum and maximum coordinates.
    *
    * @param x1 The first x-coordinate.
    * @param y1 The first y-coordinate.
    * @param z1 The first z-coordinate.
    * @param x2 The second x-coordinate.
    * @param y2 The second y-coordinate.
    * @param z2 The second z-coordinate.
    */
   public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
      this.minX = Math.min(x1, x2);
      this.minY = Math.min(y1, y2);
      this.minZ = Math.min(z1, z2);
      this.maxX = Math.max(x1, x2);
      this.maxY = Math.max(y1, y2);
      this.maxZ = Math.max(z1, z2);
   }

   /**
    * Constructs an AxisAlignedBB (Axis Aligned Bounding Box) with the specified BlockPos.
    * The bounding box will cover the block at the given position.
    *
    * @param blockPos The BlockPos representing the position of the block.
    */
   public AxisAlignedBB(BlockPos blockPos) {
      this((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(),
              (double)(blockPos.getX() + 1), (double)(blockPos.getY() + 1), (double)(blockPos.getZ() + 1));
   }

   /**
    * Constructs an AxisAlignedBB (Axis Aligned Bounding Box) with two BlockPos.
    * The bounding box will span from the first position to the second position.
    *
    * @param pos1 The first BlockPos representing one corner of the bounding box.
    * @param pos2 The second BlockPos representing the opposite corner of the bounding box.
    */
   public AxisAlignedBB(BlockPos pos1, BlockPos pos2) {
      this((double)pos1.getX(), (double)pos1.getY(), (double)pos1.getZ(),
              (double)pos2.getX(), (double)pos2.getY(), (double)pos2.getZ());
   }

   /**
    * Constructs an AxisAlignedBB (Axis Aligned Bounding Box) with two Vector3d.
    * The bounding box will span from the first vector to the second vector.
    *
    * @param vec1 The first Vector3d representing one corner of the bounding box.
    * @param vec2 The second Vector3d representing the opposite corner of the bounding box.
    */
   public AxisAlignedBB(Vector3d vec1, Vector3d vec2) {
      this(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z);
   }

   /**
    * Creates an AxisAlignedBB (Axis Aligned Bounding Box) from a MutableBoundingBox.
    * The bounding box will cover the area defined by the MutableBoundingBox.
    *
    * @param boundingBox The MutableBoundingBox representing the area to cover.
    * @return A new AxisAlignedBB covering the specified area.
    */
   public static AxisAlignedBB of(MutableBoundingBox boundingBox) {
      return new AxisAlignedBB((double)boundingBox.x0, (double)boundingBox.y0, (double)boundingBox.z0,
              (double)(boundingBox.x1 + 1), (double)(boundingBox.y1 + 1), (double)(boundingBox.z1 + 1));
   }

   /**
    * Creates an AxisAlignedBB (Axis Aligned Bounding Box) representing a unit cube
    * from the specified lower corner.
    *
    * @param lowerCorner The Vector3d representing the lower corner of the unit cube.
    * @return A new AxisAlignedBB representing the unit cube from the lower corner.
    */
   public static AxisAlignedBB unitCubeFromLowerCorner(Vector3d lowerCorner) {
      return new AxisAlignedBB(lowerCorner.x, lowerCorner.y, lowerCorner.z,
              lowerCorner.x + 1.0D, lowerCorner.y + 1.0D, lowerCorner.z + 1.0D);
   }

   public double min(Direction.Axis p_197745_1_) {
      return p_197745_1_.choose(this.minX, this.minY, this.minZ);
   }

   public double max(Direction.Axis p_197742_1_) {
      return p_197742_1_.choose(this.maxX, this.maxY, this.maxZ);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof AxisAlignedBB)) {
         return false;
      } else {
         AxisAlignedBB axisalignedbb = (AxisAlignedBB)p_equals_1_;
         if (Double.compare(axisalignedbb.minX, this.minX) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.minY, this.minY) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.minZ, this.minZ) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.maxX, this.maxX) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.maxY, this.maxY) != 0) {
            return false;
         } else {
            return Double.compare(axisalignedbb.maxZ, this.maxZ) == 0;
         }
      }
   }

   public int hashCode() {
      long i = Double.doubleToLongBits(this.minX);
      int j = (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.minY);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.minZ);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.maxX);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.maxY);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.maxZ);
      return 31 * j + (int)(i ^ i >>> 32);
   }

   public AxisAlignedBB contract(double p_191195_1_, double p_191195_3_, double p_191195_5_) {
      double d0 = this.minX;
      double d1 = this.minY;
      double d2 = this.minZ;
      double d3 = this.maxX;
      double d4 = this.maxY;
      double d5 = this.maxZ;
      if (p_191195_1_ < 0.0D) {
         d0 -= p_191195_1_;
      } else if (p_191195_1_ > 0.0D) {
         d3 -= p_191195_1_;
      }

      if (p_191195_3_ < 0.0D) {
         d1 -= p_191195_3_;
      } else if (p_191195_3_ > 0.0D) {
         d4 -= p_191195_3_;
      }

      if (p_191195_5_ < 0.0D) {
         d2 -= p_191195_5_;
      } else if (p_191195_5_ > 0.0D) {
         d5 -= p_191195_5_;
      }

      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   /**
    * Expands the current AxisAlignedBB (Axis Aligned Bounding Box) towards the specified vector.
    *
    * @param vector The Vector3d representing the direction and distance to expand towards.
    * @return A new AxisAlignedBB expanded towards the specified vector.
    */
   public AxisAlignedBB expandTowards(Vector3d vector) {
      return this.expandTowards(vector.x, vector.y, vector.z);
   }

   /**
    * Expands the current AxisAlignedBB (Axis Aligned Bounding Box) towards the specified coordinates.
    * The expansion is directional based on the sign of the provided values.
    *
    * @param x The distance to expand along the x-axis.
    * @param y The distance to expand along the y-axis.
    * @param z The distance to expand along the z-axis.
    * @return A new AxisAlignedBB expanded towards the specified coordinates.
    */
   public AxisAlignedBB expandTowards(double x, double y, double z) {
      double minX = this.minX;
      double minY = this.minY;
      double minZ = this.minZ;
      double maxX = this.maxX;
      double maxY = this.maxY;
      double maxZ = this.maxZ;

      if (x < 0.0D) {
         minX += x;
      } else if (x > 0.0D) {
         maxX += x;
      }

      if (y < 0.0D) {
         minY += y;
      } else if (y > 0.0D) {
         maxY += y;
      }

      if (z < 0.0D) {
         minZ += z;
      } else if (z > 0.0D) {
         maxZ += z;
      }

      return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   /**
    * Inflates the current AxisAlignedBB (Axis Aligned Bounding Box) by the specified amounts along each axis.
    * The inflation is symmetric along each axis.
    *
    * @param x The amount to inflate along the x-axis.
    * @param y The amount to inflate along the y-axis.
    * @param z The amount to inflate along the z-axis.
    * @return A new AxisAlignedBB inflated by the specified amounts.
    */
   public AxisAlignedBB inflate(double x, double y, double z) {
      double minX = this.minX - x;
      double minY = this.minY - y;
      double minZ = this.minZ - z;
      double maxX = this.maxX + x;
      double maxY = this.maxY + y;
      double maxZ = this.maxZ + z;
      return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   /**
    * Inflates the current AxisAlignedBB (Axis Aligned Bounding Box) by the specified amount uniformly along each axis.
    * The inflation is symmetric along each axis.
    *
    * @param amount The amount to inflate uniformly along each axis.
    * @return A new AxisAlignedBB inflated by the specified amount.
    */
   public AxisAlignedBB inflate(double amount) {
      return this.inflate(amount, amount, amount);
   }

   public AxisAlignedBB intersect(AxisAlignedBB p_191500_1_) {
      double d0 = Math.max(this.minX, p_191500_1_.minX);
      double d1 = Math.max(this.minY, p_191500_1_.minY);
      double d2 = Math.max(this.minZ, p_191500_1_.minZ);
      double d3 = Math.min(this.maxX, p_191500_1_.maxX);
      double d4 = Math.min(this.maxY, p_191500_1_.maxY);
      double d5 = Math.min(this.maxZ, p_191500_1_.maxZ);
      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public AxisAlignedBB minmax(AxisAlignedBB p_111270_1_) {
      double d0 = Math.min(this.minX, p_111270_1_.minX);
      double d1 = Math.min(this.minY, p_111270_1_.minY);
      double d2 = Math.min(this.minZ, p_111270_1_.minZ);
      double d3 = Math.max(this.maxX, p_111270_1_.maxX);
      double d4 = Math.max(this.maxY, p_111270_1_.maxY);
      double d5 = Math.max(this.maxZ, p_111270_1_.maxZ);
      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public AxisAlignedBB move(double p_72317_1_, double p_72317_3_, double p_72317_5_) {
      return new AxisAlignedBB(this.minX + p_72317_1_, this.minY + p_72317_3_, this.minZ + p_72317_5_, this.maxX + p_72317_1_, this.maxY + p_72317_3_, this.maxZ + p_72317_5_);
   }

   public AxisAlignedBB move(BlockPos p_186670_1_) {
      return new AxisAlignedBB(this.minX + (double)p_186670_1_.getX(), this.minY + (double)p_186670_1_.getY(), this.minZ + (double)p_186670_1_.getZ(), this.maxX + (double)p_186670_1_.getX(), this.maxY + (double)p_186670_1_.getY(), this.maxZ + (double)p_186670_1_.getZ());
   }

   public AxisAlignedBB move(Vector3d p_191194_1_) {
      return this.move(p_191194_1_.x, p_191194_1_.y, p_191194_1_.z);
   }

   public boolean intersects(AxisAlignedBB p_72326_1_) {
      return this.intersects(p_72326_1_.minX, p_72326_1_.minY, p_72326_1_.minZ, p_72326_1_.maxX, p_72326_1_.maxY, p_72326_1_.maxZ);
   }

   public boolean intersects(double p_186668_1_, double p_186668_3_, double p_186668_5_, double p_186668_7_, double p_186668_9_, double p_186668_11_) {
      return this.minX < p_186668_7_ && this.maxX > p_186668_1_ && this.minY < p_186668_9_ && this.maxY > p_186668_3_ && this.minZ < p_186668_11_ && this.maxZ > p_186668_5_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean intersects(Vector3d p_189973_1_, Vector3d p_189973_2_) {
      return this.intersects(Math.min(p_189973_1_.x, p_189973_2_.x), Math.min(p_189973_1_.y, p_189973_2_.y), Math.min(p_189973_1_.z, p_189973_2_.z), Math.max(p_189973_1_.x, p_189973_2_.x), Math.max(p_189973_1_.y, p_189973_2_.y), Math.max(p_189973_1_.z, p_189973_2_.z));
   }

   public boolean contains(Vector3d p_72318_1_) {
      return this.contains(p_72318_1_.x, p_72318_1_.y, p_72318_1_.z);
   }

   public boolean contains(double p_197744_1_, double p_197744_3_, double p_197744_5_) {
      return p_197744_1_ >= this.minX && p_197744_1_ < this.maxX && p_197744_3_ >= this.minY && p_197744_3_ < this.maxY && p_197744_5_ >= this.minZ && p_197744_5_ < this.maxZ;
   }

   public double getSize() {
      double d0 = this.getXsize();
      double d1 = this.getYsize();
      double d2 = this.getZsize();
      return (d0 + d1 + d2) / 3.0D;
   }

   public double getXsize() {
      return this.maxX - this.minX;
   }

   public double getYsize() {
      return this.maxY - this.minY;
   }

   public double getZsize() {
      return this.maxZ - this.minZ;
   }

   public AxisAlignedBB deflate(double p_186664_1_) {
      return this.inflate(-p_186664_1_);
   }

   public Optional<Vector3d> clip(Vector3d p_216365_1_, Vector3d p_216365_2_) {
      double[] adouble = new double[]{1.0D};
      double d0 = p_216365_2_.x - p_216365_1_.x;
      double d1 = p_216365_2_.y - p_216365_1_.y;
      double d2 = p_216365_2_.z - p_216365_1_.z;
      Direction direction = getDirection(this, p_216365_1_, adouble, (Direction)null, d0, d1, d2);
      if (direction == null) {
         return Optional.empty();
      } else {
         double d3 = adouble[0];
         return Optional.of(p_216365_1_.add(d3 * d0, d3 * d1, d3 * d2));
      }
   }

   @Nullable
   public static BlockRayTraceResult clip(Iterable<AxisAlignedBB> p_197743_0_, Vector3d p_197743_1_, Vector3d p_197743_2_, BlockPos p_197743_3_) {
      double[] adouble = new double[]{1.0D};
      Direction direction = null;
      double d0 = p_197743_2_.x - p_197743_1_.x;
      double d1 = p_197743_2_.y - p_197743_1_.y;
      double d2 = p_197743_2_.z - p_197743_1_.z;

      for(AxisAlignedBB axisalignedbb : p_197743_0_) {
         direction = getDirection(axisalignedbb.move(p_197743_3_), p_197743_1_, adouble, direction, d0, d1, d2);
      }

      if (direction == null) {
         return null;
      } else {
         double d3 = adouble[0];
         return new BlockRayTraceResult(p_197743_1_.add(d3 * d0, d3 * d1, d3 * d2), direction, p_197743_3_, false);
      }
   }

   @Nullable
   private static Direction getDirection(AxisAlignedBB p_197741_0_, Vector3d p_197741_1_, double[] p_197741_2_, @Nullable Direction p_197741_3_, double p_197741_4_, double p_197741_6_, double p_197741_8_) {
      if (p_197741_4_ > 1.0E-7D) {
         p_197741_3_ = clipPoint(p_197741_2_, p_197741_3_, p_197741_4_, p_197741_6_, p_197741_8_, p_197741_0_.minX, p_197741_0_.minY, p_197741_0_.maxY, p_197741_0_.minZ, p_197741_0_.maxZ, Direction.WEST, p_197741_1_.x, p_197741_1_.y, p_197741_1_.z);
      } else if (p_197741_4_ < -1.0E-7D) {
         p_197741_3_ = clipPoint(p_197741_2_, p_197741_3_, p_197741_4_, p_197741_6_, p_197741_8_, p_197741_0_.maxX, p_197741_0_.minY, p_197741_0_.maxY, p_197741_0_.minZ, p_197741_0_.maxZ, Direction.EAST, p_197741_1_.x, p_197741_1_.y, p_197741_1_.z);
      }

      if (p_197741_6_ > 1.0E-7D) {
         p_197741_3_ = clipPoint(p_197741_2_, p_197741_3_, p_197741_6_, p_197741_8_, p_197741_4_, p_197741_0_.minY, p_197741_0_.minZ, p_197741_0_.maxZ, p_197741_0_.minX, p_197741_0_.maxX, Direction.DOWN, p_197741_1_.y, p_197741_1_.z, p_197741_1_.x);
      } else if (p_197741_6_ < -1.0E-7D) {
         p_197741_3_ = clipPoint(p_197741_2_, p_197741_3_, p_197741_6_, p_197741_8_, p_197741_4_, p_197741_0_.maxY, p_197741_0_.minZ, p_197741_0_.maxZ, p_197741_0_.minX, p_197741_0_.maxX, Direction.UP, p_197741_1_.y, p_197741_1_.z, p_197741_1_.x);
      }

      if (p_197741_8_ > 1.0E-7D) {
         p_197741_3_ = clipPoint(p_197741_2_, p_197741_3_, p_197741_8_, p_197741_4_, p_197741_6_, p_197741_0_.minZ, p_197741_0_.minX, p_197741_0_.maxX, p_197741_0_.minY, p_197741_0_.maxY, Direction.NORTH, p_197741_1_.z, p_197741_1_.x, p_197741_1_.y);
      } else if (p_197741_8_ < -1.0E-7D) {
         p_197741_3_ = clipPoint(p_197741_2_, p_197741_3_, p_197741_8_, p_197741_4_, p_197741_6_, p_197741_0_.maxZ, p_197741_0_.minX, p_197741_0_.maxX, p_197741_0_.minY, p_197741_0_.maxY, Direction.SOUTH, p_197741_1_.z, p_197741_1_.x, p_197741_1_.y);
      }

      return p_197741_3_;
   }

   @Nullable
   private static Direction clipPoint(double[] p_197740_0_, @Nullable Direction p_197740_1_, double p_197740_2_, double p_197740_4_, double p_197740_6_, double p_197740_8_, double p_197740_10_, double p_197740_12_, double p_197740_14_, double p_197740_16_, Direction p_197740_18_, double p_197740_19_, double p_197740_21_, double p_197740_23_) {
      double d0 = (p_197740_8_ - p_197740_19_) / p_197740_2_;
      double d1 = p_197740_21_ + d0 * p_197740_4_;
      double d2 = p_197740_23_ + d0 * p_197740_6_;
      if (0.0D < d0 && d0 < p_197740_0_[0] && p_197740_10_ - 1.0E-7D < d1 && d1 < p_197740_12_ + 1.0E-7D && p_197740_14_ - 1.0E-7D < d2 && d2 < p_197740_16_ + 1.0E-7D) {
         p_197740_0_[0] = d0;
         return p_197740_18_;
      } else {
         return p_197740_1_;
      }
   }

   public String toString() {
      return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasNaN() {
      return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
   }

   public Vector3d getCenter() {
      return new Vector3d(MathHelper.lerp(0.5D, this.minX, this.maxX), MathHelper.lerp(0.5D, this.minY, this.maxY), MathHelper.lerp(0.5D, this.minZ, this.maxZ));
   }

   public static AxisAlignedBB ofSize(double p_241550_0_, double p_241550_2_, double p_241550_4_) {
      return new AxisAlignedBB(-p_241550_0_ / 2.0D, -p_241550_2_ / 2.0D, -p_241550_4_ / 2.0D, p_241550_0_ / 2.0D, p_241550_2_ / 2.0D, p_241550_4_ / 2.0D);
   }
}