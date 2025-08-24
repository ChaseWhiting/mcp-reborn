package net.minecraft.block.trees;

import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;

import javax.annotation.Nullable;
import java.util.Random;

public class DeadTree extends Tree {
   @Nullable
   protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(Random p_225546_1_, boolean p_225546_2_) {
      if (p_225546_1_.nextInt(10) == 0) {
         return p_225546_2_ ? Features.FANCY_OAK_BEES_005_DEAD_LEAVES : Features.FANCY_OAK_DEAD_LEAVES;
      } else {
         return p_225546_2_ ? Features.OAK_BEES_005_DEAD_LEAVES : Features.OAK_LEAF_LITTER_DEAD_LEAVES;
      }
   }
}