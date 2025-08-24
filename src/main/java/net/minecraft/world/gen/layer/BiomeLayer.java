package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeConstants;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class BiomeLayer implements IC0Transformer {
   private static final int[] LEGACY_WARM_BIOMES = new int[]
           {
                   BiomeConstants.get(Biomes.DESERT),
                   BiomeConstants.get(Biomes.FOREST),
                   BiomeConstants.get(Biomes.MOUNTAINS),
                   BiomeConstants.get(Biomes.SWAMP),
                   BiomeConstants.get(Biomes.PLAINS),
                   BiomeConstants.get(Biomes.TAIGA)
           };
   private static final int[] WARM_BIOMES = new int[]
           {
                   BiomeConstants.get(Biomes.DESERT),
                   BiomeConstants.get(Biomes.DESERT),
                   BiomeConstants.get(Biomes.DESERT),
                   BiomeConstants.get(Biomes.SAVANNA),
                   BiomeConstants.get(Biomes.SAVANNA),
                   BiomeConstants.get(Biomes.PLAINS)
           };
   private static final int[] MEDIUM_BIOMES = new int[]
           {
                   BiomeConstants.get(Biomes.FOREST),
                   BiomeConstants.get(Biomes.DARK_FOREST),
                   BiomeConstants.get(Biomes.MOUNTAINS),
                   BiomeConstants.get(Biomes.PLAINS),
                   BiomeConstants.get(Biomes.BIRCH_FOREST),
                   BiomeConstants.get(Biomes.SWAMP)
           };
   private static final int[] COLD_BIOMES = new int[]
           {
                   BiomeConstants.get(Biomes.FOREST),
                   BiomeConstants.get(Biomes.MOUNTAINS),
                   BiomeConstants.get(Biomes.TAIGA),
                   BiomeConstants.get(Biomes.PLAINS)
           };
   private static final int[] ICE_BIOMES = new int[]
           {
                   BiomeConstants.get(Biomes.SNOWY_TUNDRA),
                   BiomeConstants.get(Biomes.SNOWY_TUNDRA),
                   BiomeConstants.get(Biomes.SNOWY_TUNDRA),
                   BiomeConstants.get(Biomes.SNOWY_TAIGA)
           };
   private int[] warmBiomes = WARM_BIOMES;

   public BiomeLayer(boolean p_i232147_1_) {
      if (p_i232147_1_) {
         this.warmBiomes = LEGACY_WARM_BIOMES;
      }

   }

   public int apply(INoiseRandom random, int biomeID) {
      int i = (biomeID & 3840) >> 8;
      biomeID = biomeID & -3841;
      if (!LayerUtil.isOcean(biomeID) && biomeID != BiomeConstants.get(Biomes.MUSHROOM_FIELDS)) {
         switch(biomeID) {
         case 1:
            if (i > 0) {
               return random.nextRandom(3) == 0 ? BiomeConstants.get(Biomes.BADLANDS_PLATEAU) : BiomeConstants.get(Biomes.WOODED_BADLANDS_PLATEAU);
            }

            int id = this.warmBiomes[random.nextRandom(this.warmBiomes.length)];

            return id == BiomeConstants.get(Biomes.DESERT) ? random.nextRandom(40) == 0 ? BiomeConstants.get(Biomes.SANDY_DRYLANDS) : id : id;
         case 2:
            if (i > 0) {
               return BiomeConstants.get(Biomes.JUNGLE);
            }

            return MEDIUM_BIOMES[random.nextRandom(MEDIUM_BIOMES.length)];
         case 3:
            if (i > 0) {
               return BiomeConstants.get(Biomes.GIANT_TREE_TAIGA);
            }

            return COLD_BIOMES[random.nextRandom(COLD_BIOMES.length)];
         case 4:
            return ICE_BIOMES[random.nextRandom(ICE_BIOMES.length)];
         default:
            return BiomeConstants.get(Biomes.MUSHROOM_FIELDS);
         }
      } else {
         return biomeID;
      }
   }
}