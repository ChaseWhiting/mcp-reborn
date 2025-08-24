package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;

import java.util.Random;

public class ScatteredMudFeature extends Feature<ProbabilityConfig> {
   public ScatteredMudFeature(Codec<ProbabilityConfig> codec) {
      super(codec);
   }

   @Override
   public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos origin, ProbabilityConfig config) {
      int successfulPlacements = 0;
      BlockPos.Mutable mutablePos = origin.mutable();

      int radius = random.nextInt(3) + 2;
      for (int dx = -radius; dx <= radius; ++dx) {
         for (int dz = -radius; dz <= radius; ++dz) {
            if (random.nextFloat() < 0.2F) {
               mutablePos.set(origin.getX() + dx, world.getHeight(Heightmap.Type.WORLD_SURFACE, origin.getX() + dx, origin.getZ() + dz) - 1, origin.getZ() + dz);
               Block blockBelow = world.getBlockState(mutablePos).getBlock();
               if (isDirt(blockBelow)) {
                  world.setBlock(mutablePos, Blocks.MUD.defaultBlockState(), 2);
                  for (int i = 0; i < 3; i++) {
                     if (hasAirOnSideAndDirtBelow(world, mutablePos.below(i))) {
                        world.setBlock(mutablePos.below(i), Blocks.MUD.defaultBlockState(), 2);
                     }
                  }

                  ++successfulPlacements;
               }
            }
         }
      }
      return successfulPlacements > 0;
   }

   public static boolean hasAirOnSideAndDirtBelow(ISeedReader world, BlockPos pos) {
      boolean b = world.getBlockState(pos).is(Blocks.DIRT);

      b = b && (isAir(world, pos.east()) || isAir(world, pos.west()) || isAir(world, pos.south()) || isAir(world, pos.north()));

      if (world.getRandom().nextFloat() > world.getRandom().nextFloat() * 0.6) {
         Block block = world.getBlockState(pos.below()).getBlock();
         if (block == Blocks.STONE && world.getBlockState(pos.above(1)).getBlock() == Blocks.MUD) {
            return false;
         }
      }

      if (world.getRandom().nextFloat() > world.getRandom().nextFloat() * 0.6) {
         BlockPos position = pos.above();
         for (Direction direction : Direction.Plane.HORIZONTAL) {
            position = position.relative(direction);
            Block block = world.getBlockState(position).getBlock();
            if (block == Blocks.PODZOL && b) {
               return false;
            }
            position = pos.above();
         }

      }

      return b;
   }

   public static boolean isDirt(Block block) {
      return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL;
   }
}
