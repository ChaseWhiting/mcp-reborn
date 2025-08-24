package net.minecraft.world.gen.trunkplacer;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class PaleOakLargeTrunkPlacer extends GiantTrunkPlacer {
   public static final Codec<PaleOakLargeTrunkPlacer> CODEC = RecordCodecBuilder.create((p_236902_0_) -> {
      return trunkPlacerParts(p_236902_0_).apply(p_236902_0_, PaleOakLargeTrunkPlacer::new);
   });

   public PaleOakLargeTrunkPlacer(int p_i232058_1_, int p_i232058_2_, int p_i232058_3_) {
      super(p_i232058_1_, p_i232058_2_, p_i232058_3_);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.PALE_OAK_TRUNK_PLACER;
   }

   public List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader worldReader, Random random, int number, BlockPos position, Set<BlockPos> blockSet, MutableBoundingBox boundingBox, BaseTreeFeatureConfig configuration) {
      List<FoliagePlacer.Foliage> list = Lists.newArrayList();
      list.addAll(super.placeTrunk(worldReader, random, number, position, blockSet, boundingBox, configuration));

      // More frequent branches, start at lower heights, with longer branches
      for (int i = number - 2 - random.nextInt(4); i > number / 3; i -= 1 + random.nextInt(2)) {
         float f = random.nextFloat() * ((float)Math.PI * 1.5F); // Reduce randomness in angle
         int j = 0;
         int k = 0;

         // Increase branch length
         for (int l = 0; l < 3; ++l) {
            j = (int)(1.5F + MathHelper.cos(f) * (float)l);
            k = (int)(1.5F + MathHelper.sin(f) * (float)l);
            BlockPos blockpos = position.offset(j, i - 3 + l / 2, k);
            placeLog(worldReader, random, blockpos, blockSet, boundingBox, configuration);
         }

         // Add foliage at the end of the branch
         list.add(new FoliagePlacer.Foliage(position.offset(j, i, k), -2, false));
      }

      // Adding DarkOak-like 2x2 trunk at the top of the tree
      int topLogHeight = number - random.nextInt(4); // Determine where the logs at the top will start
      BlockPos blockposTop = position.above(topLogHeight);
      place2x2Top(worldReader, random, blockposTop, blockSet, boundingBox, configuration);

      return list;
   }

   // Method for placing 2x2 logs at the top (borrowed from DarkOakTrunkPlacer)
   private void place2x2Top(IWorldGenerationReader p_230382_1_, Random p_230382_2_, BlockPos blockPos, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_) {
      // Place 2x2 logs near the top
      placeLog(p_230382_1_, p_230382_2_, blockPos, p_230382_5_, p_230382_6_, p_230382_7_);
      placeLog(p_230382_1_, p_230382_2_, blockPos.east(), p_230382_5_, p_230382_6_, p_230382_7_);
      placeLog(p_230382_1_, p_230382_2_, blockPos.south(), p_230382_5_, p_230382_6_, p_230382_7_);
      placeLog(p_230382_1_, p_230382_2_, blockPos.east().south(), p_230382_5_, p_230382_6_, p_230382_7_);
   }
}