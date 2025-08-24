package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.HangingMossBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class PaleGardenCaveCarver extends CaveWorldCarver {

   public PaleGardenCaveCarver(Codec<ProbabilityConfig> codec, int maxHeight) {
      super(codec, maxHeight);
   }

   @Override
   public boolean carve(IChunk chunk, Function<BlockPos, Biome> biomeFunction, Random random, int seaLevel, int chunkX, int chunkZ, int originalX, int originalZ, BitSet carvingMask, ProbabilityConfig config) {
      int caveRange = (this.getRange() * 2 - 1) * 16;
      int caveCount = random.nextInt(random.nextInt(random.nextInt(this.getCaveBound()) + 1) + 1);

      for (int i = 0; i < caveCount; ++i) {
         double caveX = (double) (chunkX * 16 + random.nextInt(16));
         double caveY = (double) this.getCaveY(random);
         double caveZ = (double) (chunkZ * 16 + random.nextInt(16));
         float caveRadius = 1.0F + random.nextFloat() * 6.0F;

         if (random.nextInt(4) == 0) {
            this.genRoom(chunk, biomeFunction, random.nextLong(), seaLevel, originalX, originalZ, caveX, caveY, caveZ, caveRadius, 0.5D, carvingMask);
         } else {
            this.genTunnel(chunk, biomeFunction, random.nextLong(), seaLevel, originalX, originalZ, caveX, caveY, caveZ, caveRadius, random.nextFloat() * ((float) Math.PI * 2F), (random.nextFloat() - 0.5F) / 4.0F, 0, caveRange, this.getYScale(), carvingMask);
         }
      }
      return true;
   }

   @Override
   protected boolean carveSphere(IChunk chunk, Function<BlockPos, Biome> biomeFunction, long seed, int seaLevel, int chunkX, int chunkZ, double posX, double posY, double posZ, double radiusX, double radiusY, BitSet carvingMask) {
      Random random = new Random(seed + (long) chunkX + (long) chunkZ);
      double d0 = (double) (chunkX * 16 + 8);
      double d1 = (double) (chunkZ * 16 + 8);

      if (!(posX < d0 - 16.0D - radiusX * 2.0D) && !(posZ < d1 - 16.0D - radiusX * 2.0D) && !(posX > d0 + 16.0D + radiusX * 2.0D) && !(posZ > d1 + 16.0D + radiusX * 2.0D)) {
         int minX = Math.max(MathHelper.floor(posX - radiusX) - chunkX * 16 - 1, 0);
         int maxX = Math.min(MathHelper.floor(posX + radiusX) - chunkX * 16 + 1, 16);
         int minY = Math.max(MathHelper.floor(posY - radiusY) - 1, 1);
         int maxY = Math.min(MathHelper.floor(posY + radiusY) + 1, this.genHeight - 8);
         int minZ = Math.max(MathHelper.floor(posZ - radiusX) - chunkZ * 16 - 1, 0);
         int maxZ = Math.min(MathHelper.floor(posZ + radiusX) - chunkZ * 16 + 1, 16);

         if (this.hasWater(chunk, chunkX, chunkZ, minX, maxX, minY, maxY, minZ, maxZ)) {
            return false;
         }

         boolean carved = false;
         BlockPos.Mutable blockPos = new BlockPos.Mutable();
         BlockPos.Mutable belowPos = new BlockPos.Mutable();

         for (int x = minX; x < maxX; ++x) {
            int realX = x + chunkX * 16;
            double dx = ((double) realX + 0.5D - posX) / radiusX;

            for (int z = minZ; z < maxZ; ++z) {
               int realZ = z + chunkZ * 16;
               double dz = ((double) realZ + 0.5D - posZ) / radiusX;

               if (dx * dx + dz * dz < 1.0D) {
                  MutableBoolean mutableBoolean = new MutableBoolean(false);

                  for (int y = maxY; y > minY; --y) {
                     double dy = ((double) y - 0.5D - posY) / radiusY;

                     if (!this.skip(dx, dy, dz, y)) {
                        carved |= this.carveBlock(chunk, biomeFunction, carvingMask, random, blockPos, belowPos, realX, y, realZ, mutableBoolean);
                     }
                  }
               }
            }
         }
         return carved;
      } else {
         return false;
      }
   }


   protected boolean carveBlock(IChunk chunk, Function<BlockPos, Biome> biomeFunction, BitSet carvingMask, Random random, BlockPos.Mutable pos, BlockPos.Mutable belowPos, int realX, int y, int realZ, MutableBoolean mutableBoolean) {
      if (y < 0 || y >= this.genHeight) {
         return false; // Skip blocks that are out of bounds vertically
      }

      int chunkX = realX & 15;
      int chunkZ = realZ & 15;
      int maskIndex = chunkX | chunkZ << 4 | y << 8;

      if (carvingMask.get(maskIndex)) {
         return false;
      } else {
         carvingMask.set(maskIndex);
         pos.set(realX, y, realZ);
         BlockState currentBlockState = chunk.getBlockState(pos);
         BlockState blockAbove = chunk.getBlockState(belowPos.setWithOffset(pos, Direction.UP));

         if (!this.canReplaceBlock(currentBlockState, blockAbove)) {
            return false;
         } else {
            if (y < 11) {
               chunk.setBlockState(pos, Blocks.LAVA.defaultBlockState(), false);
            } else {
               chunk.setBlockState(pos, CAVE_AIR, false);

               // Place moss if this is a valid stone or dirt block and the block below is solid
               if ((currentBlockState.is(Blocks.STONE) || currentBlockState.is(Blocks.DIRT)) && chunk.getBlockState(belowPos.below()).isSolidRender(chunk, belowPos.below())) {
                  chunk.setBlockState(pos, Blocks.PALE_MOSS_BLOCK.defaultBlockState(), false);
               }

               // Handle placing hanging moss on the ceiling if there's air below and the block is solid above
               if (blockAbove.isAir() && currentBlockState.isSolidRender(chunk, pos)) {
                  addHangingMoss(chunk, pos, random);
               }
            }
            return true;
         }
      }
   }

   private void addHangingMoss(IChunk chunk, BlockPos.Mutable pos, Random random) {
      BlockPos.Mutable hangingPos = pos.mutable();

      // Check if the block directly above is STONE (start condition)
      if (chunk.getBlockState(pos.below()).is(Blocks.STONE)) {
         System.out.println("Valid moss start point at: " + pos.toString());  // Debug output

         // Place moss directly below the solid block and continue downward
         while (chunk.getBlockState(pos.below().below()).isAir()) {
            chunk.setBlockState(hangingPos.above(), Blocks.PALE_HANGING_MOSS.defaultBlockState().setValue(HangingMossBlock.TIP, false), false);
            System.out.println("Placing moss at: " + hangingPos.toString());  // Debug output
            hangingPos.move(0, -1, 0);
         }

         // Set the final block as the moss TIP
         chunk.setBlockState(pos, Blocks.PALE_HANGING_MOSS.defaultBlockState().setValue(HangingMossBlock.TIP, true), false);
         System.out.println("Moss tip placed at: " + hangingPos.toString());  // Debug output
      }
   }



   public static boolean canGenerateMoss(IChunk chunk, BlockPos pos) {
      BlockState blockAbove = chunk.getBlockState(pos.above());

      // Check if the block above is STONE or PALE_HANGING_MOSS
      return blockAbove.is(Blocks.STONE) || blockAbove.is(Blocks.PALE_HANGING_MOSS);
   }
}
