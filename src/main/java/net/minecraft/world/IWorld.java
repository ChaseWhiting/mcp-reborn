package net.minecraft.world;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.level.GameEvent;
import net.minecraft.world.storage.IWorldInfo;

public interface IWorld extends IBiomeReader, IDayTimeReader {
   default long dayTime() {
      return this.getLevelData().getDayTime();
   }

   ITickList<Block> getBlockTicks();

   ITickList<Fluid> getLiquidTicks();

   IWorldInfo getLevelData();

   DifficultyInstance getCurrentDifficultyAt(BlockPos p_175649_1_);

   default Difficulty getDifficulty() {
      return this.getLevelData().getDifficulty();
   }

   AbstractChunkProvider getChunkSource();

   default boolean hasChunk(int p_217354_1_, int p_217354_2_) {
      return this.getChunkSource().hasChunk(p_217354_1_, p_217354_2_);
   }

   Random getRandom();

   default void blockUpdated(BlockPos p_230547_1_, Block p_230547_2_) {
   }

   void playSound(@Nullable PlayerEntity p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_);

   void addParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_);

   void levelEvent(@Nullable PlayerEntity player, int eventId, BlockPos position, int eventData);

   void gameEvent(@Nullable PlayerEntity player, GameEvent event, BlockPos position, int eventData);

   void onGameEvent(GameEvent gameEvent, BlockPos position, @Nullable PlayerEntity player);

   default public void gameEvent(@Nullable Entity entity, net.minecraft.entity.warden.event.GameEvent gameEvent, BlockPos blockPos) {
      this.gameEvent(gameEvent, blockPos, new net.minecraft.entity.warden.event.GameEvent.Context(entity, null));
   }

   default public void gameEvent(@Nullable Entity entity, net.minecraft.entity.warden.event.GameEvent gameEvent, Vector3d vector3D) {
      this.gameEvent(gameEvent, vector3D, new net.minecraft.entity.warden.event.GameEvent.Context(entity, null));
   }

   default public void gameEvent(net.minecraft.entity.warden.event.GameEvent gameEvent, BlockPos blockPos, net.minecraft.entity.warden.event.GameEvent.Context context) {
      this.gameEvent(gameEvent, Vector3d.atCenterOf(blockPos), context);
   }

   default int getHeight() {
      return this.dimensionType().logicalHeight();
   }

   default void levelEvent(int eventId, BlockPos position, int eventData) {
      this.levelEvent((PlayerEntity)null, eventId, position, eventData);
   }

   public void gameEvent(net.minecraft.entity.warden.event.GameEvent var1, Vector3d var2, net.minecraft.entity.warden.event.GameEvent.Context var3);

   default void gameEvent(GameEvent event, BlockPos position, int eventData) {
      this.gameEvent((PlayerEntity)null, event, position, eventData);
   }

   default void onGameEvent(GameEvent event, BlockPos position) {
      this.onGameEvent(event, position, null);
   }



   public static boolean forEachBlockIntersectedBetween(Vector3d vec3, Vector3d vec32, AxisAlignedBB aABB, BlockStepVisitor blockStepVisitor) {
      Vector3d vec33 = vec32.subtract(vec3);
      if (vec33.lengthSqr() < (double) MathHelper.square(0.99999f)) {
         for (BlockPos blockPos : BlockPos.betweenClosed(aABB)) {
            if (blockStepVisitor.visit(blockPos, 0)) continue;
            return false;
         }
         return true;
      }
      LongOpenHashSet longOpenHashSet = new LongOpenHashSet();
      Vector3d vec34 = aABB.getMinPosition();
      Vector3d vec35 = vec34.subtract(vec33);
      int n = addCollisionsAlongTravel((LongSet)longOpenHashSet, vec35, vec34, aABB, blockStepVisitor);
      if (n < 0) {
         return false;
      }
      for (BlockPos blockPos : BlockPos.betweenClosed(aABB)) {
         if (longOpenHashSet.contains(blockPos.asLong()) || blockStepVisitor.visit(blockPos, n + 1)) continue;
         return false;
      }
      return true;
   }

   private static int addCollisionsAlongTravel(LongSet longSet, Vector3d vec3, Vector3d vec32, AxisAlignedBB aABB, BlockStepVisitor blockStepVisitor) {
      Vector3d vec33 = vec32.subtract(vec3);
      int n = MathHelper.floor(vec3.x);
      int n2 = MathHelper.floor(vec3.y);
      int n3 = MathHelper.floor(vec3.z);
      int n4 = MathHelper.sign(vec33.x);
      int n5 = MathHelper.sign(vec33.y);
      int n6 = MathHelper.sign(vec33.z);
      double d = n4 == 0 ? Double.MAX_VALUE : (double)n4 / vec33.x;
      double d2 = n5 == 0 ? Double.MAX_VALUE : (double)n5 / vec33.y;
      double d3 = n6 == 0 ? Double.MAX_VALUE : (double)n6 / vec33.z;
      double d4 = d * (n4 > 0 ? 1.0 - MathHelper.frac(vec3.x) : MathHelper.frac(vec3.x));
      double d5 = d2 * (n5 > 0 ? 1.0 - MathHelper.frac(vec3.y) : MathHelper.frac(vec3.y));
      double d6 = d3 * (n6 > 0 ? 1.0 - MathHelper.frac(vec3.z) : MathHelper.frac(vec3.z));
      int n7 = 0;
      BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
      while (d4 <= 1.0 || d5 <= 1.0 || d6 <= 1.0) {
         if (d4 < d5) {
            if (d4 < d6) {
               n += n4;
               d4 += d;
            } else {
               n3 += n6;
               d6 += d3;
            }
         } else if (d5 < d6) {
            n2 += n5;
            d5 += d2;
         } else {
            n3 += n6;
            d6 += d3;
         }
         if (n7++ > 16) break;
         Optional<Vector3d> optional = AxisAlignedBB.clip(n, n2, n3, n + 1, n2 + 1, n3 + 1, vec3, vec32);
         if (optional.isEmpty()) continue;
         Vector3d vec34 = optional.get();
         double d7 = MathHelper.clamp(vec34.x, (double)n + (double)1.0E-5f, (double)n + 1.0 - (double)1.0E-5f);
         double d8 = MathHelper.clamp(vec34.y, (double)n2 + (double)1.0E-5f, (double)n2 + 1.0 - (double)1.0E-5f);
         double d9 = MathHelper.clamp(vec34.z, (double)n3 + (double)1.0E-5f, (double)n3 + 1.0 - (double)1.0E-5f);
         int n8 = MathHelper.floor(d7 + aABB.getXsize());
         int n9 = MathHelper.floor(d8 + aABB.getYsize());
         int n10 = MathHelper.floor(d9 + aABB.getZsize());
         for (int i = n; i <= n8; ++i) {
            for (int j = n2; j <= n9; ++j) {
               for (int k = n3; k <= n10; ++k) {
                  if (!longSet.add(BlockPos.asLong(i, j, k)) || blockStepVisitor.visit(mutableBlockPos.set(i, j, k), n7)) continue;
                  return -1;
               }
            }
         }
      }
      return n7;
   }

   @FunctionalInterface
   public static interface BlockStepVisitor {
      public boolean visit(BlockPos var1, int var2);
   }
}