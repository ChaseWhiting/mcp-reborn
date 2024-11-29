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

   public List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader p_230382_1_, Random p_230382_2_, int p_230382_3_, BlockPos p_230382_4_, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_) {
      List<FoliagePlacer.Foliage> list = Lists.newArrayList();
      list.addAll(super.placeTrunk(p_230382_1_, p_230382_2_, p_230382_3_, p_230382_4_, p_230382_5_, p_230382_6_, p_230382_7_));

      // More frequent branches, start at lower heights, with longer branches
      for (int i = p_230382_3_ - 2 - p_230382_2_.nextInt(4); i > p_230382_3_ / 3; i -= 1 + p_230382_2_.nextInt(2)) {
         float f = p_230382_2_.nextFloat() * ((float)Math.PI * 1.5F); // Reduce randomness in angle
         int j = 0;
         int k = 0;

         // Increase branch length
         for (int l = 0; l < 3; ++l) {
            j = (int)(1.5F + MathHelper.cos(f) * (float)l);
            k = (int)(1.5F + MathHelper.sin(f) * (float)l);
            BlockPos blockpos = p_230382_4_.offset(j, i - 3 + l / 2, k);
            placeLog(p_230382_1_, p_230382_2_, blockpos, p_230382_5_, p_230382_6_, p_230382_7_);
         }

         // Add foliage at the end of the branch
         list.add(new FoliagePlacer.Foliage(p_230382_4_.offset(j, i, k), -2, false));
      }

      // Adding DarkOak-like 2x2 trunk at the top of the tree
      int topLogHeight = p_230382_3_ - p_230382_2_.nextInt(4); // Determine where the logs at the top will start
      BlockPos blockposTop = p_230382_4_.above(topLogHeight);
      place2x2Top(p_230382_1_, p_230382_2_, blockposTop, p_230382_5_, p_230382_6_, p_230382_7_);

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