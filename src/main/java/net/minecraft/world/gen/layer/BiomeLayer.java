package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class BiomeLayer implements IC0Transformer {
   private static final int[] LEGACY_WARM_BIOMES = new int[]{2, 4, 3, 6, 1, 5};
   private static final int[] WARM_BIOMES = new int[]{2, 2, 2, 35, 35, 1};
   private static final int[] MEDIUM_BIOMES = new int[]{4, 29, 3, 1, 27, 6};
   private static final int[] COLD_BIOMES = new int[]{4, 3, 5, 1};
   private static final int[] ICE_BIOMES = new int[]{12, 12, 12, 30};
   private int[] warmBiomes = WARM_BIOMES;

   public BiomeLayer(boolean p_i232147_1_) {
      if (p_i232147_1_) {
         this.warmBiomes = LEGACY_WARM_BIOMES;
      }

   }

   public int apply(INoiseRandom p_202726_1_, int p_202726_2_) {
      int i = (p_202726_2_ & 3840) >> 8;
      p_202726_2_ = p_202726_2_ & -3841;
      if (!LayerUtil.isOcean(p_202726_2_) && p_202726_2_ != 14) {
         switch(p_202726_2_) {
         case 1:
            if (i > 0) {
               return p_202726_1_.nextRandom(3) == 0 ? 39 : 38;
            }

            return this.warmBiomes[p_202726_1_.nextRandom(this.warmBiomes.length)];
         case 2:
            if (i > 0) {
               return 21;
            }

            return MEDIUM_BIOMES[p_202726_1_.nextRandom(MEDIUM_BIOMES.length)];
         case 3:
            if (i > 0) {
               return 32;
            }

            return COLD_BIOMES[p_202726_1_.nextRandom(COLD_BIOMES.length)];
         case 4:
            return ICE_BIOMES[p_202726_1_.nextRandom(ICE_BIOMES.length)];
         default:
            return 14;
         }
      } else {
         return p_202726_2_;
      }
   }
}