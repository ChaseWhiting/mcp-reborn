package net.minecraft.world.gen.trunkplacer;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
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

   public List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader worldReader, Random random, int number, BlockPos position, Set<BlockPos> blockSet, MutableBoundingBox boundingBox, BaseTreeFeatureConfig configuration) {
      List<FoliagePlacer.Foliage> list = Lists.newArrayList();
      list.addAll(super.placeTrunk(worldReader, random, number, position, blockSet, boundingBox, configuration));

      for(int i = number - 2 - random.nextInt(4); i > number / 2; i -= 2 + random.nextInt(4)) {
         float f = random.nextFloat() * ((float)Math.PI * 2F);
         int j = 0;
         int k = 0;

         for(int l = 0; l < 5; ++l) {
            j = (int)(1.5F + MathHelper.cos(f) * (float)l);
            k = (int)(1.5F + MathHelper.sin(f) * (float)l);
            BlockPos blockpos = position.offset(j, i - 3 + l / 2, k);
            placeLog(worldReader, random, blockpos, blockSet, boundingBox, configuration);
         }

         list.add(new FoliagePlacer.Foliage(position.offset(j, i, k), -2, false));
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
