package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.BrushableBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class DesertWellsFeature extends Feature<NoFeatureConfig> {
   private static final BlockStateMatcher IS_SAND = BlockStateMatcher.forBlock(Blocks.SAND);
   private final BlockState sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
   private final BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
   private final BlockState water = Blocks.WATER.defaultBlockState();

   public DesertWellsFeature(Codec<NoFeatureConfig> p_i231948_1_) {
      super(p_i231948_1_);
   }

   @Override
   public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      pos = pos.above();
      while (world.isEmptyBlock(pos) && pos.getY() > 2) {
         pos = pos.below();
      }

      if (!IS_SAND.test(world.getBlockState(pos))) {
         return false;
      }

      for (int x = -2; x <= 2; ++x) {
         for (int z = -2; z <= 2; ++z) {
            if (world.isEmptyBlock(pos.offset(x, -1, z)) && world.isEmptyBlock(pos.offset(x, -2, z))) {
               return false;
            }
         }
      }

      for (int y = -1; y <= 0; ++y) {
         for (int x = -2; x <= 2; ++x) {
            for (int z = -2; z <= 2; ++z) {
               world.setBlock(pos.offset(x, y, z), this.sandstone, 2);
            }
         }
      }

      world.setBlock(pos, this.water, 2);
      for (Direction dir : Direction.Plane.HORIZONTAL) {
         world.setBlock(pos.relative(dir), this.water, 2);
      }

      for (int x = -2; x <= 2; ++x) {
         for (int z = -2; z <= 2; ++z) {
            if (x == -2 || x == 2 || z == -2 || z == 2) {
               world.setBlock(pos.offset(x, 1, z), this.sandstone, 2);
            }
         }
      }

      world.setBlock(pos.offset(2, 1, 0), this.sandSlab, 2);
      world.setBlock(pos.offset(-2, 1, 0), this.sandSlab, 2);
      world.setBlock(pos.offset(0, 1, 2), this.sandSlab, 2);
      world.setBlock(pos.offset(0, 1, -2), this.sandSlab, 2);

      for (int x = -1; x <= 1; ++x) {
         for (int z = -1; z <= 1; ++z) {
            world.setBlock(pos.offset(x, 4, z), (x == 0 && z == 0) ? this.sandstone : this.sandSlab, 2);
         }
      }

      for (int y = 1; y <= 3; ++y) {
         world.setBlock(pos.offset(-1, y, -1), this.sandstone, 2);
         world.setBlock(pos.offset(-1, y, 1), this.sandstone, 2);
         world.setBlock(pos.offset(1, y, -1), this.sandstone, 2);
         world.setBlock(pos.offset(1, y, 1), this.sandstone, 2);
      }

      // Place Suspicious Sand in random positions beneath well
      BlockPos center = pos.below();
      BlockPos[] candidates = new BlockPos[] {
              center,
              center.east(),
              center.west(),
              center.north(),
              center.south()
      };

      placeSusSand(world, candidates[rand.nextInt(candidates.length)].below(), rand);
      placeSusSand(world, candidates[rand.nextInt(candidates.length)].below(), rand);

      return true;
   }

   private void placeSusSand(ISeedReader world, BlockPos pos, Random rand) {
      BlockState susSand = Blocks.SUSPICIOUS_SAND.defaultBlockState(); // Replace with your block reference
      world.setBlock(pos, susSand, 3);
      TileEntity te = world.getBlockEntity(pos);
      if (te instanceof BrushableBlockEntity brushableBlock) {
         brushableBlock.setLootTable(LootTables.DESERT_WELL_ARCHAEOLOGY, rand.nextLong());
      }
   }

}