package net.minecraft.block.sounds;

import net.minecraft.block.BlockState;
import net.minecraft.block.DryVegetationBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;

import java.util.Random;


public class AmbientDesertBlockSoundsPlayer {
    private static final int IDLE_SOUND_CHANCE = 1600;
    private static final int WIND_SOUND_CHANCE = 10000;
    private static final int SURROUNDING_BLOCKS_PLAY_SOUND_THRESHOLD = 3;
    private static final int SURROUNDING_BLOCKS_DISTANCE_CHECK = 8;

    public static void playAmbientBlockSounds(BlockState blockState, World level, BlockPos blockPos, Random randomSource) {
        if (!DryVegetationBlock.PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS.contains(blockState.getBlock()) || !level.canSeeSky(blockPos.above())) {
            return;
        }
        if (randomSource.nextInt(IDLE_SOUND_CHANCE) == 0 && AmbientDesertBlockSoundsPlayer.shouldPlayAmbientSound(level, blockPos)) {
            level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.SAND_IDLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        }
        if (randomSource.nextInt(WIND_SOUND_CHANCE) == 0 && AmbientDesertBlockSoundsPlayer.isInAmbientSoundBiome(level.getBiomeName(blockPos).get()) && AmbientDesertBlockSoundsPlayer.shouldPlayAmbientSound(level, blockPos)) {
            level.playPlayerSound(SoundEvents.SAND_WIND, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    private static boolean isInAmbientSoundBiome(RegistryKey<Biome> holder) {
        return holder == (Biomes.DESERT) || isBadlands(holder);
    }

    public static boolean isBadlands(RegistryKey<Biome> biomeRegistryKey) {
        return biomeRegistryKey == Biomes.BADLANDS
                || biomeRegistryKey == Biomes.ERODED_BADLANDS
                || biomeRegistryKey == Biomes.BADLANDS_PLATEAU
                || biomeRegistryKey == Biomes.MODIFIED_BADLANDS_PLATEAU
                || biomeRegistryKey == Biomes.WOODED_BADLANDS_PLATEAU
                || biomeRegistryKey == Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU;
    }

    private static boolean shouldPlayAmbientSound(World level, BlockPos blockPos) {
        int n = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.relative(direction, SURROUNDING_BLOCKS_DISTANCE_CHECK);
            BlockState blockState = level.getBlockState(blockPos2.atY(level.getHeight(Heightmap.Type.WORLD_SURFACE, blockPos2.getX(), blockPos2.getZ()) - 1));
            if (!DryVegetationBlock.PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS.contains(blockState.getBlock()) || ++n < SURROUNDING_BLOCKS_PLAY_SOUND_THRESHOLD) continue;
            return true;
        }
        return false;
    }
}

