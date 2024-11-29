package net.minecraft.block.trees;

import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;

import javax.annotation.Nullable;
import java.util.Random;

public class PaleOakTree extends BigTree {
   @Nullable
   protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(Random p_225546_1_, boolean p_225546_2_) {
      return null;
   }

   @Nullable
   protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredMegaFeature(Random p_225547_1_) {
      return p_225547_1_.nextInt(20) == 0 ? Features.HUGE_PALE_OAK : p_225547_1_.nextInt(10) == 0 ? Features.TALL_PALE_OAK : Features.PALE_OAK;
   }
}