package net.minecraft.block.trees;

import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;

import javax.annotation.Nullable;
import java.util.Random;

public class AppleTree extends Tree {
   @Nullable
   protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(Random p_225546_1_, boolean p_225546_2_) {
      if (p_225546_1_.nextInt(10) == 0) {
         return Features.FANCY_APPLE_TREE;
      } else {
         return Features.APPLE;
      }
   }
}