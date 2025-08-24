package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.google.gson.JsonElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WorldGenExtractor {

    // Registry key for biome source (BiomeProvider)
    public static final RegistryKey<Registry<Codec<? extends BiomeProvider>>> BIOME_SOURCE_REGISTRY = createRegistryKey("worldgen/biome_source");

    public static void extractBiomeSource(MinecraftServer server) {
        // Get the dynamic registry for biome providers (biome source)
        DynamicRegistries registries = server.registryAccess();
        OverworldBiomeProvider provider = (OverworldBiomeProvider) server.getLevel(World.OVERWORLD).getChunkSource().getGenerator().getBiomeSource();



        if (Registry.BIOME_SOURCE != null) {
            if (BiomeProvider.CODEC != null) {
                // Use Codec to serialize the BiomeProvider settings
                saveBiomeSourceAsJson(BiomeProvider.CODEC,provider, "overworld_biome_source.json");
            } else {
                System.out.println("Overworld biome source not found!");
            }
        } else {
            System.out.println("Biome source registry not found!");
        }
    }

    // Use Codec to serialize BiomeProvider and write it to a file
    private static <T extends BiomeProvider> void saveBiomeSourceAsJson(Codec<T> biomeProviderCodec, T biomeProviderInstance, String fileName) {
        // Using JsonOps for converting the object to JSON
        DynamicOps<JsonElement> jsonOps = JsonOps.INSTANCE;

        // Encode the BiomeProvider using its Codec
        JsonElement jsonElement = biomeProviderCodec.encodeStart(jsonOps, biomeProviderInstance)
                .resultOrPartial(error -> System.err.println("Failed to encode BiomeProvider: " + error))
                .orElse(null);  // Handle possible errors gracefully

        if (jsonElement != null) {
            // Get file path
            Path outputPath = Paths.get("config", "worldgen", fileName);
            File outputFile = outputPath.toFile();
            outputFile.getParentFile().mkdirs();

            // Write JSON element to file
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(jsonElement.toString());
                System.out.println("Saved biome source data to " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to create registry key
    private static <T> RegistryKey<Registry<T>> createRegistryKey(String path) {
        return RegistryKey.createRegistryKey(new ResourceLocation(path));
    }
}
