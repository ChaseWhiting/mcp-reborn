package net.minecraft.world.gen.trunkplacer;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

public class StraightTrunkPlacer extends AbstractTrunkPlacer {
   public static final Codec<StraightTrunkPlacer> CODEC = RecordCodecBuilder.create((p_236904_0_) -> {
      return trunkPlacerParts(p_236904_0_).apply(p_236904_0_, StraightTrunkPlacer::new);
   });

   public StraightTrunkPlacer(int p_i232059_1_, int p_i232059_2_, int p_i232059_3_) {
      super(p_i232059_1_, p_i232059_2_, p_i232059_3_);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
   }

   public List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader worldReader, Random random, int number, BlockPos position, Set<BlockPos> blockSet, MutableBoundingBox boundingBox, BaseTreeFeatureConfig configuration) {
      setDirtAt(worldReader, position.below());

      for(int i = 0; i < number; ++i) {
         placeLog(worldReader, random, position.above(i), blockSet, boundingBox, configuration);
      }

      return ImmutableList.of(new FoliagePlacer.Foliage(position.above(number), 0, false));
   }
}