package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

public class PillagerOutpostStructure extends JigsawStructure {
   private static final List<MobSpawnInfo.Spawners> OUTPOST_ENEMIES = ImmutableList.of(
           new MobSpawnInfo.Spawners(EntityType.PILLAGER, 10, 1, 1),
           new MobSpawnInfo.Spawners(EntityType.PILLAGER_CAPTAIN, 2, 1, 1)
   );

   public PillagerOutpostStructure(Codec<VillageConfig> p_i231977_1_) {
      super(p_i231977_1_, 0, true, true);
   }

   public List<MobSpawnInfo.Spawners> getSpecialEnemies() {
      return OUTPOST_ENEMIES;
   }

   protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeProvider, long worldSeed, SharedSeedRandom random, int x, int z, Biome biome, ChunkPos chunkPos, VillageConfig villageConfig) {
      // Calculate the chunk coordinates
      int chunkX = x >> 4;
      int chunkZ = z >> 4;

      // Set the seed for the random number generator
      random.setSeed((long)(chunkX ^ (chunkZ << 4)) ^ worldSeed);

      // Advance the random number generator's internal state
      random.nextInt();

      // Check if the next random number is divisible by 5
      if (random.nextInt(5) != 0) {
         return false;
      } else {
         // Check if the feature (e.g., village) is near another village
         return !this.isNearVillage(chunkGenerator, worldSeed, random, x, z);
      }
   }


   private boolean isNearVillage(ChunkGenerator p_242782_1_, long p_242782_2_, SharedSeedRandom p_242782_4_, int p_242782_5_, int p_242782_6_) {
      StructureSeparationSettings structureseparationsettings = p_242782_1_.getSettings().getConfig(Structure.VILLAGE);
      if (structureseparationsettings == null) {
         return false;
      } else {
         for(int i = p_242782_5_ - 10; i <= p_242782_5_ + 10; ++i) {
            for(int j = p_242782_6_ - 10; j <= p_242782_6_ + 10; ++j) {
               ChunkPos chunkpos = Structure.VILLAGE.getPotentialFeatureChunk(structureseparationsettings, p_242782_2_, p_242782_4_, i, j);
               if (i == chunkpos.x && j == chunkpos.z) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}