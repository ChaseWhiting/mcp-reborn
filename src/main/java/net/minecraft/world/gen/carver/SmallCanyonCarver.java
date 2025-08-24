package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class SmallCanyonCarver extends WorldCarver<ProbabilityConfig> {
   private final float[] rs = new float[1024];

   public SmallCanyonCarver(Codec<ProbabilityConfig> codec) {
      super(codec, 128); // Keeps carver from going too deep
   }

   public boolean isStartChunk(Random random, int chunkX, int chunkZ, ProbabilityConfig config) {
      return random.nextFloat() <= 0.8F; // 80% chance to generate, making them VERY frequent
   }

   public boolean carve(IChunk chunk, Function<BlockPos, Biome> biomeGetter, Random random, int regionX, int chunkX, int chunkZ, int chunkY, int seaLevel, BitSet carvingMask, ProbabilityConfig config) {
      double startX = (double)(chunkX * 16 + random.nextInt(16));
      double startY = (double)(random.nextInt(7) + 63); // Forces cracks to always appear near surface (Y=63-70)
      double startZ = (double)(chunkZ * 16 + random.nextInt(16));
      float yaw = random.nextFloat() * ((float)Math.PI * 2F);
      float pitch = 0F; // No vertical inclination since these should be surface cracks
      double scale = 1.0D; // Keeps cracks narrow
      float width = (random.nextFloat() * 1.0F + 1.0F); // Width between 2-3 blocks
      int length = 4 + random.nextInt(3); // Length between 4-6 blocks

      this.generateSmallCrack(chunk, biomeGetter, random.nextLong(), regionX, chunkY, seaLevel, startX, startY, startZ, width, yaw, pitch, 0, length, scale, carvingMask);
      return true;
   }

   private void generateSmallCrack(IChunk chunk, Function<BlockPos, Biome> biomeGetter, long seed, int regionX, int chunkY, int seaLevel, double startX, double startY, double startZ, float width, float yaw, float pitch, int startStep, int maxSteps, double scale, BitSet carvingMask) {
      Random random = new Random(seed);
      float yawVariation = 0.0F;

      for (int step = startStep; step < maxSteps; ++step) {
         double d0 = 0.6D + (double)(MathHelper.sin((float)step * (float)Math.PI / (float)maxSteps) * width);
         double d1 = d0 * scale;

         d0 = d0 * ((double)random.nextFloat() * 0.2D + 0.9D);
         d1 = d1 * ((double)random.nextFloat() * 0.2D + 0.9D);

         float cosYaw = MathHelper.cos(yaw);
         float sinYaw = MathHelper.sin(yaw);

         startX += (double)(cosYaw * 0.8F); // Moves forward to keep cracks long but narrow
         startZ += (double)(sinYaw * 0.8F);
         startY -= random.nextFloat() * 0.2F + 0.2F; // Keeps the cracks shallow (only 1-3 blocks deep)

         // Slight zigzag effect
         yaw = yaw * 0.9F + yawVariation * 0.1F;
         yawVariation = yawVariation * 0.6F;
         yawVariation += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 0.3F;

         if (random.nextInt(2) != 0) { // Ensures carving happens most of the time
            this.carveSphere(chunk, biomeGetter, seed, regionX, chunkY, seaLevel, startX, startY, startZ, d0, d1, carvingMask);
         }
      }
   }

   protected boolean skip(double x, double y, double z, int yIndex) {
      return (x * x + z * z) * (double)this.rs[yIndex - 1] + y * y / 4.0D >= 1.0D;
   }
}
