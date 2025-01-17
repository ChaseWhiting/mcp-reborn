package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Optional;

public class JigsawStructure extends Structure<VillageConfig> {
   private final int startY;
   private final boolean doExpansionHack;
   private final boolean projectStartToHeightmap;

   public JigsawStructure(Codec<VillageConfig> p_i241978_1_, int p_i241978_2_, boolean p_i241978_3_, boolean p_i241978_4_) {
      super(p_i241978_1_);
      this.startY = p_i241978_2_;
      this.doExpansionHack = p_i241978_3_;
      this.projectStartToHeightmap = p_i241978_4_;
   }

   public Structure.IStartFactory<VillageConfig> getStartFactory() {
      return (p_242778_1_, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_) -> {
         return new JigsawStructure.Start(this, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_);
      };
   }

   public static class Start extends MarginedStructureStart<VillageConfig> {
      private final JigsawStructure feature;

      public Start(JigsawStructure p_i241979_1_, int p_i241979_2_, int p_i241979_3_, MutableBoundingBox p_i241979_4_, int p_i241979_5_, long p_i241979_6_) {
         super(p_i241979_1_, p_i241979_2_, p_i241979_3_, p_i241979_4_, p_i241979_5_, p_i241979_6_);
         this.feature = p_i241979_1_;
      }

      @Override
      public void generatePieces(DynamicRegistries registries, ChunkGenerator generator, TemplateManager templateManager, int chunkX, int chunkZ, Biome biome, VillageConfig config) {
         BlockPos startPos = new BlockPos(chunkX * 16, this.feature.startY, chunkZ * 16);
         JigsawPatternRegistry.bootstrap();

         // Generate jigsaw pieces
         JigsawManager.addPieces(registries, config, AbstractVillagePiece::new, generator, templateManager, startPos, this.pieces, this.random, this.feature.doExpansionHack, this.feature.projectStartToHeightmap);

//         if (config instanceof PaleVIllageConfig) {
//            // Filter pieces that extend outside the biome
//            RegistryKey<Biome> targetBiomeKey = Biomes.PALE_GARDEN;
//
//            // Filter pieces that extend outside the biome
//            this.pieces.removeIf(piece -> !isPieceInBiome(registries, generator, piece, targetBiomeKey));
//         }


         this.calculateBoundingBox();
      }

      private boolean isPieceInBiome(DynamicRegistries registries, ChunkGenerator generator, StructurePiece piece, RegistryKey<Biome> targetBiomeKey) {
         MutableBoundingBox box = piece.getBoundingBox();
         return isBoundingBoxInBiome(registries, generator, box, targetBiomeKey);
      }

      private boolean isBoundingBoxInBiome(DynamicRegistries registries, ChunkGenerator generator, MutableBoundingBox boundingBox, RegistryKey<Biome> targetBiomeKey) {
         Registry<Biome> biomeRegistry = registries.registryOrThrow(Registry.BIOME_REGISTRY);

         for (int x = boundingBox.x0; x <= boundingBox.x1; x += 16) {
            for (int z = boundingBox.z0; z <= boundingBox.z1; z += 16) {
               // Get the biome at this position
               Biome biome = generator.getBiomeSource().getNoiseBiome(x >> 2, 0, z >> 2);

               // Find the RegistryKey for this biome
               Optional<RegistryKey<Biome>> biomeKey = biomeRegistry.getResourceKey(biome);

               // Check if it matches the target
               if (!biomeKey.isPresent() || !biomeKey.get().equals(targetBiomeKey)) {
                  return false;
               }
            }
         }

         return true;
      }
   }
}