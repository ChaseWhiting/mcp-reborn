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
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

public class DarkOakTrunkPlacer extends AbstractTrunkPlacer {
   public static final Codec<DarkOakTrunkPlacer> CODEC = RecordCodecBuilder.create((p_236883_0_) -> {
      return trunkPlacerParts(p_236883_0_).apply(p_236883_0_, DarkOakTrunkPlacer::new);
   });

   public DarkOakTrunkPlacer(int p_i232053_1_, int p_i232053_2_, int p_i232053_3_) {
      super(p_i232053_1_, p_i232053_2_, p_i232053_3_);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
   }

   public List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader worldReader, Random random, int number, BlockPos position, Set<BlockPos> blockSet, MutableBoundingBox boundingBox, BaseTreeFeatureConfig configuration) {
      List<FoliagePlacer.Foliage> list = Lists.newArrayList();
      BlockPos blockpos = position.below();
      setDirtAt(worldReader, blockpos);
      setDirtAt(worldReader, blockpos.east());
      setDirtAt(worldReader, blockpos.south());
      setDirtAt(worldReader, blockpos.south().east());
      Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
      int i = number - random.nextInt(4);
      int j = 2 - random.nextInt(3);
      int k = position.getX();
      int l = position.getY();
      int i1 = position.getZ();
      int j1 = k;
      int k1 = i1;
      int l1 = l + number - 1;

      for(int i2 = 0; i2 < number; ++i2) {
         if (i2 >= i && j > 0) {
            j1 += direction.getStepX();
            k1 += direction.getStepZ();
            --j;
         }

         int j2 = l + i2;
         BlockPos blockpos1 = new BlockPos(j1, j2, k1);
         if (TreeFeature.isAirOrLeaves(worldReader, blockpos1)) {
            placeLog(worldReader, random, blockpos1, blockSet, boundingBox, configuration);
            placeLog(worldReader, random, blockpos1.east(), blockSet, boundingBox, configuration);
            placeLog(worldReader, random, blockpos1.south(), blockSet, boundingBox, configuration);
            placeLog(worldReader, random, blockpos1.east().south(), blockSet, boundingBox, configuration);
         }
      }

      list.add(new FoliagePlacer.Foliage(new BlockPos(j1, l1, k1), 0, true));

      for(int l2 = -1; l2 <= 2; ++l2) {
         for(int i3 = -1; i3 <= 2; ++i3) {
            if ((l2 < 0 || l2 > 1 || i3 < 0 || i3 > 1) && random.nextInt(3) <= 0) {
               int j3 = random.nextInt(3) + 2;

               for(int k2 = 0; k2 < j3; ++k2) {
                  placeLog(worldReader, random, new BlockPos(k + l2, l1 - k2 - 1, i1 + i3), blockSet, boundingBox, configuration);
               }

               list.add(new FoliagePlacer.Foliage(new BlockPos(j1 + l2, l1, k1 + i3), 0, false));
            }
         }
      }

      return list;
   }
}