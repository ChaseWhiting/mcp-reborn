package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;

public class ScatteredPodzolFeature extends Feature<ProbabilityConfig> {
   public ScatteredPodzolFeature(Codec<ProbabilityConfig> codec) {
      super(codec);
   }

   @Override
   public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos origin, ProbabilityConfig config) {
      int successfulPlacements = 0;
      BlockPos.Mutable mutablePos = origin.mutable();

      // Slightly scattered placement logic
      int radius = random.nextInt(4) + 3; // Random radius between 3 and 6
      for (int dx = -radius; dx <= radius; ++dx) {
         for (int dz = -radius; dz <= radius; ++dz) {
            if (random.nextFloat() < 0.3F) { // 40% chance to place podzol at a given position
               mutablePos.set(origin.getX() + dx, world.getHeight(Heightmap.Type.WORLD_SURFACE, origin.getX() + dx, origin.getZ() + dz) - 1, origin.getZ() + dz);
               Block blockBelow = world.getBlockState(mutablePos).getBlock();
               if (isDirt(blockBelow)) { // Only replace dirt-like blocks
                  world.setBlock(mutablePos, Blocks.PODZOL.defaultBlockState(), 2);
                  if (random.nextFloat() > 0.8f) {
                     world.setBlock(mutablePos.above(), Blocks.GRASS.defaultBlockState(), 3);

                  }
                  ++successfulPlacements;
               }
            }
         }
      }
      return successfulPlacements > 0;
   }

   public static boolean isDirt(Block block) {
      return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.COARSE_DIRT;
   }
}
