package net.minecraft.entity.projectile;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.PerlinNoiseGenerator;

import java.util.stream.IntStream;

public class FrisbeeWindManager {
    private final PerlinNoiseGenerator perlinNoiseGenerator;
    private final SharedSeedRandom random;
    private final RegistryKey<World> level;

    public FrisbeeWindManager(World world, long worldSeed) {
        this.level = world.dimension();
        long modifiedSeed = getModifiedSeed(worldSeed, this.level);
        this.random = new SharedSeedRandom(modifiedSeed);
        this.perlinNoiseGenerator = new PerlinNoiseGenerator(this.random, IntStream.rangeClosed(-7, 7)); // Using 15 octaves
    }

    private long getModifiedSeed(long worldSeed, RegistryKey<World> level) {
        if (level == World.NETHER) {
            return worldSeed ^ 0xCAFEBABE; // XOR with a unique constant for the Nether
        } else if (level == World.END) {
            return worldSeed ^ 0xDEADBEEF; // XOR with a different unique constant for the End
        } else {
            return worldSeed; // No modification for the Overworld
        }
    }

    public Vector3d getWindVectorAtLocation(World world, BlockPos pos) {
        double x = pos.getX() * 0.1; // Scale down to control frequency
        double y = pos.getY() * 0.1;
        double z = pos.getZ() * 0.1;

        double baseWindSpeed = perlinNoiseGenerator.getValue(x, z, true) * getWeatherModifier(world);
        double windDirection = perlinNoiseGenerator.getValue(x + 100, z + 100, true);

        // Adjust wind speed based on Y-level
        double yLevelModifier = calculateYLevelModifier(pos.getY(), pos, world);
        double adjustedWindSpeed = baseWindSpeed * yLevelModifier;

        double windX = Math.cos(windDirection) * adjustedWindSpeed;
        double windZ = Math.sin(windDirection) * adjustedWindSpeed;

        return new Vector3d(windX, 0, windZ);
    }

    private double getWeatherModifier(World world) {
        if (level == World.NETHER) {
            return 3.5; // Stronger winds in the Nether
        } else if (level == World.END) {
            return 4; // Stronger winds in the End
        } else if (world.isThundering()) {
            return 2.0;
        } else if (world.isRaining()) {
            return 1.5;
        } else {
            return 1.0;
        }
    }

    private double calculateYLevelModifier(int yLevel, BlockPos pos, World world) {
        Biome biome = world.getBiome(pos);
        if (yLevel < 60) {
            return 0.1; // Minimal wind below Y-level 60
        } else if (yLevel < 100) {
            if (world.isRainingAt(pos) || world.isThundering()) {
                return 2.0 + (yLevel - 60) / 40.0;
            }
            return (yLevel - 60) / 40.0; // Linear increase from Y-level 60 to 100
        } else {
            if (biome.getPrecipitation() == Biome.RainType.SNOW) {
                return 3.0 + ((yLevel - 100) / 100.0);
            } else if (biome.getPrecipitation() == Biome.RainType.NONE) {
                return 2.0 + ((yLevel - 100) / 100.0);
            }
            return 1.0 + ((yLevel - 100) / 100.0); // Increase further above Y-level 100
        }
    }
}
