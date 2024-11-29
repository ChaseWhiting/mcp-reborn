package net.minecraft.world.gen.trunkplacer;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

public class PaleOakTrunkPlacer extends GiantTrunkPlacer {
   public static final Codec<PaleOakTrunkPlacer> CODEC = RecordCodecBuilder.create((instance) -> {
      return trunkPlacerParts(instance).apply(instance, PaleOakTrunkPlacer::new);
   });

   public PaleOakTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
      super(baseHeight, heightRandA, heightRandB);
   }

   @Override
   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.PALE_OAK_TRUNK_PLACER;  // Change this if you have your own trunk placer type
   }

   public List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader p_230382_1_, Random p_230382_2_, int p_230382_3_, BlockPos p_230382_4_, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_) {
      List<FoliagePlacer.Foliage> list = Lists.newArrayList();
      list.addAll(super.placeTrunk(p_230382_1_, p_230382_2_, p_230382_3_, p_230382_4_, p_230382_5_, p_230382_6_, p_230382_7_));

      for(int i = p_230382_3_ - 2 - p_230382_2_.nextInt(4); i > p_230382_3_ / 2; i -= 2 + p_230382_2_.nextInt(4)) {
         float f = p_230382_2_.nextFloat() * ((float)Math.PI * 2F);
         int j = 0;
         int k = 0;

         for(int l = 0; l < 5; ++l) {
            j = (int)(1.5F + MathHelper.cos(f) * (float)l);
            k = (int)(1.5F + MathHelper.sin(f) * (float)l);
            BlockPos blockpos = p_230382_4_.offset(j, i - 3 + l / 2, k);
            placeLog(p_230382_1_, p_230382_2_, blockpos, p_230382_5_, p_230382_6_, p_230382_7_);
         }

         list.add(new FoliagePlacer.Foliage(p_230382_4_.offset(j, i, k), -2, false));
      }

      return list;
   }

   // Helper method to place a 2x2 trunk at a given position
   private void place2x2Trunk(IWorldGenerationReader reader, Random random, BlockPos pos, Set<BlockPos> logPositions, MutableBoundingBox boundingBox, BaseTreeFeatureConfig config) {
      placeLog(reader, random, pos, logPositions, boundingBox, config);
      placeLog(reader, random, pos.east(), logPositions, boundingBox, config);
      placeLog(reader, random, pos.south(), logPositions, boundingBox, config);
      placeLog(reader, random, pos.south().east(), logPositions, boundingBox, config);
   }
}
