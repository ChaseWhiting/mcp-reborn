package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.BrushableBlockEntity;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.random.RandomSource;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class DesertPyramidStructure extends Structure<NoFeatureConfig> {
   public DesertPyramidStructure(Codec<NoFeatureConfig> p_i231947_1_) {
      super(p_i231947_1_);
   }

   public Structure.IStartFactory<NoFeatureConfig> getStartFactory() {
      return DesertPyramidStructure.Start::new;
   }

   public static class Start extends StructureStart<NoFeatureConfig> {
      public Start(Structure<NoFeatureConfig> p_i225801_1_, int p_i225801_2_, int p_i225801_3_, MutableBoundingBox p_i225801_4_, int p_i225801_5_, long p_i225801_6_) {
         super(p_i225801_1_, p_i225801_2_, p_i225801_3_, p_i225801_4_, p_i225801_5_, p_i225801_6_);
      }

      public void generatePieces(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, NoFeatureConfig p_230364_7_) {
         DesertPyramidPiece desertpyramidpiece = new DesertPyramidPiece(this.random, p_230364_4_ * 16, p_230364_5_ * 16);
         this.pieces.add(desertpyramidpiece);
         this.calculateBoundingBox();
      }
   }

   @Override
   public void afterPlace(ISeedReader world, StructureManager structureManager, ChunkGenerator generator, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos, List<StructurePiece> pieces) {
      SortedArraySet<Vector3i> sortedArraySet = SortedArraySet.create(64);

      for (StructurePiece piece : pieces) {
         if (!(piece instanceof DesertPyramidPiece desertPyramidPiece)) continue;

         sortedArraySet.addAll(desertPyramidPiece.getPotentialSuspiciousSandWorldPositions());
         placeSuspiciousSand(boundingBox, world, desertPyramidPiece.getRandomCollapsedRoofPos());
      }
      ObjectArrayList<Vector3i> objectArrayList = new ObjectArrayList<>(sortedArraySet.stream().toList());
      Random random1 = new Random(world.getSeed());
      int n = Math.min(sortedArraySet.size(), MathHelper.randomBetweenInclusive(random1, 5, 8));
      Util.shuffle(objectArrayList, random1);
      for (Vector3i i3 : objectArrayList) {
         BlockPos pos = new BlockPos(i3.getX(), i3.getY(), i3.getZ());
         if (n > 0) {
            --n;
            placeSuspiciousSand(boundingBox, world, pos);
            continue;
         }
         if (!boundingBox.isInside(pos)) continue;
         world.setBlock(pos, Blocks.SAND.defaultBlockState(), 2);
      }
   }

   private static void placeSuspiciousSand(MutableBoundingBox box, ISeedReader world, BlockPos pos) {
      if (box.isInside(pos)) {
         world.setBlock(pos, Blocks.SUSPICIOUS_SAND.defaultBlockState(), 2);

         if (world.getBlockEntity(pos) instanceof BrushableBlockEntity brushableBlock) {
            brushableBlock.setLootTable(LootTables.DESERT_PYRAMID_ARCHAEOLOGY, RandomSource.create(world.getSeed()).forkPositional().at(pos).nextLong());
         }
      }
   }
}