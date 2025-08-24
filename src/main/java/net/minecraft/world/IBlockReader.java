package net.minecraft.world;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.warden.event.vibrations.ClipBlockStateContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;

public interface IBlockReader {
   @Nullable
   TileEntity getBlockEntity(BlockPos p_175625_1_);

   BlockState getBlockState(BlockPos p_180495_1_);

   FluidState getFluidState(BlockPos p_204610_1_);

   default int getLightEmission(BlockPos p_217298_1_) {
      return this.getBlockState(p_217298_1_).getLightEmission();
   }

   default int getMaxLightLevel() {
      return 15;
   }

   default int getMaxBuildHeight() {
      return 256;
   }

   default Stream<BlockState> getBlockStates(AxisAlignedBB p_234853_1_) {
      return BlockPos.betweenClosedStream(p_234853_1_).map(this::getBlockState);
   }

   default BlockRayTraceResult clip(RayTraceContext context) {
      return traverseBlocks(context, (rayTraceContext, position) -> {
         BlockState blockstate = this.getBlockState(position);
         FluidState fluidstate = this.getFluidState(position);
         Vector3d vector3d = rayTraceContext.getFrom();
         Vector3d vector3d1 = rayTraceContext.getTo();
         VoxelShape voxelshape = rayTraceContext.getBlockShape(blockstate, this, position);
         BlockRayTraceResult blockraytraceresult = this.clipWithInteractionOverride(vector3d, vector3d1, position, voxelshape, blockstate);
         VoxelShape voxelshape1 = rayTraceContext.getFluidShape(fluidstate, this, position);
         BlockRayTraceResult blockraytraceresult1 = voxelshape1.clip(vector3d, vector3d1, position);
         double d0 = blockraytraceresult == null ? Double.MAX_VALUE : rayTraceContext.getFrom().distanceToSqr(blockraytraceresult.getLocation());
         double d1 = blockraytraceresult1 == null ? Double.MAX_VALUE : rayTraceContext.getFrom().distanceToSqr(blockraytraceresult1.getLocation());
         return d0 <= d1 ? blockraytraceresult : blockraytraceresult1;
      }, (nextContext) -> {
         Vector3d vector3d = nextContext.getFrom().subtract(nextContext.getTo());
         return BlockRayTraceResult.miss(nextContext.getTo(), Direction.getNearest(vector3d.x, vector3d.y, vector3d.z), new BlockPos(nextContext.getTo()));
      });
   }

   default public BlockRayTraceResult isBlockInLine(RayTraceContext rayTraceContext) {
      return traverseBlocks(rayTraceContext, (context, position) -> {
         BlockState blockState = this.getBlockState(position);
         Vector3d from = context.getFrom();
         Vector3d to = context.getTo();
         Vector3d directionVector = from.subtract(to);

         // Check if the block matches the desired condition
         if (context.getBlock() == RayTraceContext.BlockMode.COLLIDER && !blockState.getShape(this, position).isEmpty()) {
            return new BlockRayTraceResult(to, Direction.getNearest(directionVector.x, directionVector.y, directionVector.z), position, false);
         }

         return null;
      }, (context) -> {
         Vector3d from = context.getFrom();
         Vector3d to = context.getTo();
         Vector3d directionVector = from.subtract(to);

         return BlockRayTraceResult.miss(to, Direction.getNearest(directionVector.x, directionVector.y, directionVector.z), new BlockPos(to));
      });
   }

   default public BlockRayTraceResult isBlockInLine(ClipBlockStateContext clipBlockStateContext2) {
      return traverseBlocks(clipBlockStateContext2.getFrom(), clipBlockStateContext2.getTo(), clipBlockStateContext2, (clipBlockStateContext, blockPos) -> {
         BlockState blockState = this.getBlockState(blockPos);
         Vector3d vector3D = clipBlockStateContext.getFrom().subtract(clipBlockStateContext.getTo());
         return clipBlockStateContext.isTargetBlock().test(blockState) ? new BlockRayTraceResult(clipBlockStateContext.getTo(), Direction.getNearest(vector3D.x, vector3D.y, vector3D.z), new BlockPos(clipBlockStateContext.getTo()), false) : null;
      }, clipBlockStateContext -> {
         Vector3d vector3D = clipBlockStateContext.getFrom().subtract(clipBlockStateContext.getTo());
         return BlockRayTraceResult.miss(clipBlockStateContext.getTo(), Direction.getNearest(vector3D.x, vector3D.y, vector3D.z), new BlockPos(clipBlockStateContext.getTo()));
      });
   }


   @Nullable
   default BlockRayTraceResult clipWithInteractionOverride(Vector3d p_217296_1_, Vector3d p_217296_2_, BlockPos p_217296_3_, VoxelShape p_217296_4_, BlockState p_217296_5_) {
      BlockRayTraceResult blockraytraceresult = p_217296_4_.clip(p_217296_1_, p_217296_2_, p_217296_3_);
      if (blockraytraceresult != null) {
         BlockRayTraceResult blockraytraceresult1 = p_217296_5_.getInteractionShape(this, p_217296_3_).clip(p_217296_1_, p_217296_2_, p_217296_3_);
         if (blockraytraceresult1 != null && blockraytraceresult1.getLocation().subtract(p_217296_1_).lengthSqr() < blockraytraceresult.getLocation().subtract(p_217296_1_).lengthSqr()) {
            return blockraytraceresult.withDirection(blockraytraceresult1.getDirection());
         }
      }

      return blockraytraceresult;
   }

   default double getBlockFloorHeight(VoxelShape p_242402_1_, Supplier<VoxelShape> p_242402_2_) {
      if (!p_242402_1_.isEmpty()) {
         return p_242402_1_.max(Direction.Axis.Y);
      } else {
         double d0 = p_242402_2_.get().max(Direction.Axis.Y);
         return d0 >= 1.0D ? d0 - 1.0D : Double.NEGATIVE_INFINITY;
      }
   }

   default double getBlockFloorHeight(BlockPos p_242403_1_) {
      return this.getBlockFloorHeight(this.getBlockState(p_242403_1_).getCollisionShape(this, p_242403_1_), () -> {
         BlockPos blockpos = p_242403_1_.below();
         return this.getBlockState(blockpos).getCollisionShape(this, blockpos);
      });
   }

   static <T> T traverseBlocks(RayTraceContext p_217300_0_, BiFunction<RayTraceContext, BlockPos, T> p_217300_1_, Function<RayTraceContext, T> p_217300_2_) {
      Vector3d vector3d = p_217300_0_.getFrom();
      Vector3d vector3d1 = p_217300_0_.getTo();
      if (vector3d.equals(vector3d1)) {
         return p_217300_2_.apply(p_217300_0_);
      } else {
         double d0 = MathHelper.lerp(-1.0E-7D, vector3d1.x, vector3d.x);
         double d1 = MathHelper.lerp(-1.0E-7D, vector3d1.y, vector3d.y);
         double d2 = MathHelper.lerp(-1.0E-7D, vector3d1.z, vector3d.z);
         double d3 = MathHelper.lerp(-1.0E-7D, vector3d.x, vector3d1.x);
         double d4 = MathHelper.lerp(-1.0E-7D, vector3d.y, vector3d1.y);
         double d5 = MathHelper.lerp(-1.0E-7D, vector3d.z, vector3d1.z);
         int i = MathHelper.floor(d3);
         int j = MathHelper.floor(d4);
         int k = MathHelper.floor(d5);
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(i, j, k);
         T t = p_217300_1_.apply(p_217300_0_, blockpos$mutable);
         if (t != null) {
            return t;
         } else {
            double d6 = d0 - d3;
            double d7 = d1 - d4;
            double d8 = d2 - d5;
            int l = MathHelper.sign(d6);
            int i1 = MathHelper.sign(d7);
            int j1 = MathHelper.sign(d8);
            double d9 = l == 0 ? Double.MAX_VALUE : (double)l / d6;
            double d10 = i1 == 0 ? Double.MAX_VALUE : (double)i1 / d7;
            double d11 = j1 == 0 ? Double.MAX_VALUE : (double)j1 / d8;
            double d12 = d9 * (l > 0 ? 1.0D - MathHelper.frac(d3) : MathHelper.frac(d3));
            double d13 = d10 * (i1 > 0 ? 1.0D - MathHelper.frac(d4) : MathHelper.frac(d4));
            double d14 = d11 * (j1 > 0 ? 1.0D - MathHelper.frac(d5) : MathHelper.frac(d5));

            while(d12 <= 1.0D || d13 <= 1.0D || d14 <= 1.0D) {
               if (d12 < d13) {
                  if (d12 < d14) {
                     i += l;
                     d12 += d9;
                  } else {
                     k += j1;
                     d14 += d11;
                  }
               } else if (d13 < d14) {
                  j += i1;
                  d13 += d10;
               } else {
                  k += j1;
                  d14 += d11;
               }

               T t1 = p_217300_1_.apply(p_217300_0_, blockpos$mutable.set(i, j, k));
               if (t1 != null) {
                  return t1;
               }
            }

            return p_217300_2_.apply(p_217300_0_);
         }
      }
   }

   public static <T, C> T traverseBlocks(Vector3d vector3D, Vector3d vector32D, C c, BiFunction<C, BlockPos, T> biFunction, Function<C, T> function) {
      int n;
      int n2;
      if (vector3D.equals(vector32D)) {
         return function.apply(c);
      }
      double d = MathHelper.lerp(-1.0E-7, vector32D.x, vector3D.x);
      double d2 = MathHelper.lerp(-1.0E-7, vector32D.y, vector3D.y);
      double d3 = MathHelper.lerp(-1.0E-7, vector32D.z, vector3D.z);
      double d4 = MathHelper.lerp(-1.0E-7, vector3D.x, vector32D.x);
      double d5 = MathHelper.lerp(-1.0E-7, vector3D.y, vector32D.y);
      double d6 = MathHelper.lerp(-1.0E-7, vector3D.z, vector32D.z);
      int n3 = MathHelper.floor(d4);
      BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable(n3, n2 = MathHelper.floor(d5), n = MathHelper.floor(d6));
      T t = biFunction.apply(c, mutableBlockPos);
      if (t != null) {
         return t;
      }
      double d7 = d - d4;
      double d8 = d2 - d5;
      double d9 = d3 - d6;
      int n4 = MathHelper.sign(d7);
      int n5 = MathHelper.sign(d8);
      int n6 = MathHelper.sign(d9);
      double d10 = n4 == 0 ? Double.MAX_VALUE : (double)n4 / d7;
      double d11 = n5 == 0 ? Double.MAX_VALUE : (double)n5 / d8;
      double d12 = n6 == 0 ? Double.MAX_VALUE : (double)n6 / d9;
      double d13 = d10 * (n4 > 0 ? 1.0 - MathHelper.frac(d4) : MathHelper.frac(d4));
      double d14 = d11 * (n5 > 0 ? 1.0 - MathHelper.frac(d5) : MathHelper.frac(d5));
      double d15 = d12 * (n6 > 0 ? 1.0 - MathHelper.frac(d6) : MathHelper.frac(d6));
      while (d13 <= 1.0 || d14 <= 1.0 || d15 <= 1.0) {
         T t2;
         if (d13 < d14) {
            if (d13 < d15) {
               n3 += n4;
               d13 += d10;
            } else {
               n += n6;
               d15 += d12;
            }
         } else if (d14 < d15) {
            n2 += n5;
            d14 += d11;
         } else {
            n += n6;
            d15 += d12;
         }
         if ((t2 = biFunction.apply(c, mutableBlockPos.set(n3, n2, n))) == null) continue;
         return t2;
      }
      return function.apply(c);
   }
}