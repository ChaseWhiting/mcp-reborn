package net.minecraft.world.gen.trunkplacer;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

public class ForkyTrunkPlacer extends AbstractTrunkPlacer {
   public static final Codec<ForkyTrunkPlacer> CODEC = RecordCodecBuilder.create((p_236897_0_) -> {
      return trunkPlacerParts(p_236897_0_).apply(p_236897_0_, ForkyTrunkPlacer::new);
   });

   public ForkyTrunkPlacer(int p_i232056_1_, int p_i232056_2_, int p_i232056_3_) {
      super(p_i232056_1_, p_i232056_2_, p_i232056_3_);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.FORKING_TRUNK_PLACER;
   }

   public List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader worldReader, Random random, int number, BlockPos position, Set<BlockPos> blockSet, MutableBoundingBox boundingBox, BaseTreeFeatureConfig configuration) {
      setDirtAt(worldReader, position.below());
      List<FoliagePlacer.Foliage> list = Lists.newArrayList();
      Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
      int i = number - random.nextInt(4) - 1;
      int j = 3 - random.nextInt(3);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      int k = position.getX();
      int l = position.getZ();
      int i1 = 0;

      for(int j1 = 0; j1 < number; ++j1) {
         int k1 = position.getY() + j1;
         if (j1 >= i && j > 0) {
            k += direction.getStepX();
            l += direction.getStepZ();
            --j;
         }

         if (placeLog(worldReader, random, blockpos$mutable.set(k, k1, l), blockSet, boundingBox, configuration)) {
            i1 = k1 + 1;
         }
      }

      list.add(new FoliagePlacer.Foliage(new BlockPos(k, i1, l), 1, false));
      k = position.getX();
      l = position.getZ();
      Direction direction1 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
      if (direction1 != direction) {
         int k2 = i - random.nextInt(2) - 1;
         int l1 = 1 + random.nextInt(3);
         i1 = 0;

         for(int i2 = k2; i2 < number && l1 > 0; --l1) {
            if (i2 >= 1) {
               int j2 = position.getY() + i2;
               k += direction1.getStepX();
               l += direction1.getStepZ();
               if (placeLog(worldReader, random, blockpos$mutable.set(k, j2, l), blockSet, boundingBox, configuration)) {
                  i1 = j2 + 1;
               }
            }

            ++i2;
         }

         if (i1 > 1) {
            list.add(new FoliagePlacer.Foliage(new BlockPos(k, i1, l), 0, false));
         }
      }

      return list;
   }
}