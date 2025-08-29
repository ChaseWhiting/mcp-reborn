package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;

import java.util.*;
import java.util.stream.StreamSupport;

public class CoarseDirtSandFeature extends Feature<ProbabilityConfig> {
   public CoarseDirtSandFeature(Codec<ProbabilityConfig> codec) {
      super(codec);
   }

   @Override
   public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos origin, ProbabilityConfig config) {
      int successfulPlacements = 0;
      BlockPos.Mutable mutablePos = origin.mutable();

      int radius = random.nextInt(3) + 4;
      boolean[][] patchMap = new boolean[radius * 2 + 1][radius * 2 + 1];
      int size = radius * 2 + 1;

      for (int dx = -radius; dx <= radius; dx++) {
         for (int dz = -radius; dz <= radius; dz++) {
            double distance = Math.sqrt(dx * dx + dz * dz);
            double normalizedDistance = distance / radius;
            if (random.nextFloat() < (1.2 - normalizedDistance)) {
               patchMap[dx + radius][dz + radius] = true;
            }
         }
      }

      for (int i = 0; i < 2; i++) {
         boolean[][] newPatchMap = new boolean[size][size];
         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               int neighborCount = countNeighbors(patchMap, dx + radius, dz + radius, radius);
               if (patchMap[dx + radius][dz + radius] || neighborCount >= 3) {
                  newPatchMap[dx + radius][dz + radius] = true;
               }
            }
         }
         patchMap = newPatchMap;
      }

      for (int dx = -radius; dx <= radius; dx++) {
         for (int dz = -radius; dz <= radius; dz++) {
            if (patchMap[dx + radius][dz + radius]) {
               int wx = origin.getX() + dx;
               int wz = origin.getZ() + dz;
               IChunk chunk = world.getChunk(wx >> 4, wz >> 4);
               chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE);
               int worldHeight = chunk.getHeight(Heightmap.Type.WORLD_SURFACE, wx & 15, wz & 15);
               mutablePos.set(wx, worldHeight, wz);
               Block blockBelow = world.getBlockState(mutablePos).getBlock();

               if (isValidBase(blockBelow)) {
                  float materialChance = random.nextFloat();
                  double distance = Math.sqrt(dx * dx + dz * dz);
                  double blendFactor = distance / radius;

                  if (materialChance < 0.65F - (0.3F * (float)blendFactor)) {
                     world.setBlock(mutablePos, Blocks.SAND.defaultBlockState(), 2);

                     // World seed for determinism across gen passes
                     long seed = world.getSeed(); // ISeedReader/IWorld has getSeed() in 1.16.x

// Low-frequency noise sampled at world coords (wx, wz).
// Try 32–48 for broad clusters. Larger -> fewer, larger cactus zones.
                     double n = valueNoise2D(seed, wx, wz, 40);

// Convert noise to a boost that only applies in positive “hotspot” regions.
// Threshold means: only boost when n > ~0.2; maxBoost caps cluster density.
                     float base = 0.013F;
                     float maxBoost = 0.15F; // hotspots can reach base + ~0.075 ≈ 0.088 total
                     double t = Math.max(0.0, (n - 0.2) / 0.8); // 0 at n<=0.2, 1 near n=1.0
                     t = t * t; // bias: fewer but more distinct hotspots (optional)
                     float cactusChance = base + (float)(t * maxBoost);

// Call unchanged
                     addCacti(world, mutablePos, random, cactusChance);

                  } else {
                     world.setBlock(mutablePos, Blocks.COARSE_DIRT.defaultBlockState(), 2);
                  }
                  ++successfulPlacements;
               }
            }
         }
      }


      int featherWidth = 3;
      for (int dx = -(radius + featherWidth); dx <= (radius + featherWidth); dx++) {
         for (int dz = -(radius + featherWidth); dz <= (radius + featherWidth); dz++) {
            int mapX = dx + radius;
            int mapZ = dz + radius;

            boolean inMap = (mapX >= 0 && mapX < size && mapZ >= 0 && mapZ < size);
            boolean isCore = inMap && patchMap[mapX][mapZ];
            if (isCore) continue;

            int nearest = distanceToPatch(patchMap, mapX, mapZ, featherWidth);
            if (nearest < 0) continue;

            int wx = origin.getX() + dx;
            int wz = origin.getZ() + dz;
            int wy = world.getHeight(Heightmap.Type.WORLD_SURFACE, wx, wz) - 1;
            mutablePos.set(wx, wy, wz);

            if (!world.getBlockState(mutablePos).is(Blocks.GRASS_BLOCK)) continue;


            double s = (nearest - 1) / Math.max(1.0, (double)(featherWidth - 1));
            double edgeBlend = 1.0 + 0.5 * s;
            float sandChance = (float)Math.max(0.05, 0.65 - 0.3 * edgeBlend);

            if (random.nextFloat() < sandChance) {
               world.setBlock(mutablePos, Blocks.SAND.defaultBlockState(), 2);
            } else {
               world.setBlock(mutablePos, Blocks.COARSE_DIRT.defaultBlockState(), 2);
            }
            successfulPlacements++;
         }
      }

      return successfulPlacements > 0;
   }



   // Hash -> [0,1)
   private static double rand01(long s) {
      s ^= (s >>> 33);
      s *= 0xff51afd7ed558ccdL;
      s ^= (s >>> 33);
      s *= 0xc4ceb9fe1a85ec53L;
      s ^= (s >>> 33);
      // convert to [0,1)
      return (Double.longBitsToDouble((s & ((1L << 52) - 1)) | 0x3ff0000000000000L) - 1.0);
   }

   // Smoothstep for interpolation
   private static double smooth(double t) {
      return t * t * (3.0 - 2.0 * t);
   }

   /**
    * Low-frequency, bilinearly interpolated value noise in [-1,1].
    * cellSize controls how large "clusters" are (bigger -> larger patches).
    */
   private static double valueNoise2D(long worldSeed, int x, int z, int cellSize) {
      int gx = Math.floorDiv(x, cellSize);
      int gz = Math.floorDiv(z, cellSize);
      double fx = (double)(x - gx * cellSize) / cellSize;
      double fz = (double)(z - gz * cellSize) / cellSize;

      long k1 = 0x9e3779b97f4a7c15L; // golden ratio constant
      long k2 = 0xc2b2ae3d27d4eb4fL;

      // Corner randoms in [0,1)
      double v00 = rand01(worldSeed ^ (gx * k1) ^ (gz * k2));
      double v10 = rand01(worldSeed ^ ((gx + 1) * k1) ^ (gz * k2));
      double v01 = rand01(worldSeed ^ (gx * k1) ^ ((gz + 1) * k2));
      double v11 = rand01(worldSeed ^ ((gx + 1) * k1) ^ ((gz + 1) * k2));

      // Bilinear with smoothstep
      double sx = smooth(fx);
      double sz = smooth(fz);
      double a = v00 + sx * (v10 - v00);
      double b = v01 + sx * (v11 - v01);
      double v = a + sz * (b - a);

      return v * 2.0 - 1.0; // map to [-1,1]
   }



   private boolean isAirOnSides(BlockPos pos, ISeedReader world) {
      for (BlockPos p : List.of(pos.west(), pos.east(), pos.south(), pos.north())) {
         if (world.getBlockState(p).isSolidRender(world, pos)) return false;
      }

      return true;
   }

   private void addCacti(ISeedReader world, BlockPos.Mutable mutablePos, Random random, float chance) {
      if (random.nextFloat() < chance) {
         BlockPos.Mutable mutable = mutablePos.above().mutable();

         int b = random.nextInt(0, 4);

         int height = 1 + random.nextInt(random.nextInt(2 + 1) + 1);
         for (int j = 0; j < height; ++j) {
            world.setBlock(mutable, Blocks.CACTUS.defaultBlockState(), 2);
            if (b <= 4 && random.nextFloat() < 0.15) {
               b++;
               j -= 1;
            }
            mutable.move(Direction.UP);
         }
         if (random.nextFloat() < 0.85F && world.getBlockState(mutable.below()).is(Blocks.CACTUS)) {
            world.setBlock(mutable, Blocks.CACTUS_FLOWER.defaultBlockState(), 2);

         }


      }
   }

   private boolean canPlaceCactus(ISeedReader world, BlockPos pos) {
      // Make sure the target position is replaceable (air, plants, etc.)
      if (!world.getBlockState(pos).getMaterial().isReplaceable()) {
         return false;
      }

      // Check the four horizontal neighbors
      for (Direction dir : Direction.Plane.HORIZONTAL) {
         BlockState neighborState = world.getBlockState(pos.relative(dir));
         if (neighborState.getMaterial().isSolid()) {
            return false; // would break instantly
         }
      }

      return true;
   }



   private Optional<BlockPos> isSandBelow(ISeedReader world, BlockPos pos) {

      BlockPos p = pos;

      while (world.getBlockState(p).isAir()) {
         p = p.below();
      }

      if (world.getBlockState(p).is(Blocks.SAND)) {
         return Optional.of(p);
      }

      return Optional.empty();
   }

   // helper: diamond-distance to any true cell within a small radius (1..featherWidth), else -1
   private int distanceToPatch(boolean[][] map, int x, int z, int featherWidth) {
      int w = map.length, h = map[0].length;
      for (int d = 1; d <= featherWidth; d++) {
         for (int dx = -d; dx <= d; dx++) {
            int dz1 = d - Math.abs(dx);
            int[] zs = (dz1 == 0) ? new int[]{0} : new int[]{-dz1, dz1};
            for (int zoff : zs) {
               int nx = x + dx, nz = z + zoff;
               if (nx >= 0 && nx < w && nz >= 0 && nz < h && map[nx][nz]) return d;
            }
         }
      }
      return -1;
   }


   private int countNeighbors(boolean[][] map, int x, int z, int radius) {
      int count = 0;
      for (int dx = -1; dx <= 1; dx++) {
         for (int dz = -1; dz <= 1; dz++) {
            if (dx == 0 && dz == 0) continue;
            int nx = x + dx, nz = z + dz;
            if (nx >= 0 && nx < map.length && nz >= 0 && nz < map[0].length && map[nx][nz]) {
               count++;
            }
         }
      }
      return count;
   }

   public static boolean isValidBase(Block block) {
      return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK;
   }
}
