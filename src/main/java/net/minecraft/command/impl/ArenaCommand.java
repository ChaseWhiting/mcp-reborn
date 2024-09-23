package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class ArenaCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("buildarena")
                .requires((source) -> source.hasPermission(2))
                .executes((context) -> {
                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                    buildArena(context.getSource(), player.blockPosition());
                    return 1;
                }));
    }

    private static void buildArena(CommandSource source, BlockPos playerPos) {
        int radius = 27;
        int wallHeight = 5;

        BlockPos.Mutable blockPos = new BlockPos.Mutable();
        int yStart = playerPos.getY();

        // Build the floor and walls
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int distanceSquared = dx * dx + dz * dz;

                // Floor
                if (distanceSquared <= radius * radius) {
                    blockPos.set(playerPos.getX() + dx, yStart, playerPos.getZ() + dz);
                    source.getLevel().setBlock(blockPos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                }

                // Walls
                if (distanceSquared >= (radius - 1) * (radius - 1) && distanceSquared <= radius * radius) {
                    for (int dy = 0; dy < wallHeight; dy++) {
                        blockPos.set(playerPos.getX() + dx, yStart + dy, playerPos.getZ() + dz);
                        source.getLevel().setBlock(blockPos, Blocks.COBBLESTONE.defaultBlockState(), 3);
                    }
                }
            }
        }

        // Place lava fountains
        placeLavaFountain(source, playerPos.offset(0, 1, 0));
        placeLavaFountain(source, playerPos.offset(10, 1, 10));
        placeLavaFountain(source, playerPos.offset(-10, 1, 10));
        placeLavaFountain(source, playerPos.offset(10, 1, -10));
        placeLavaFountain(source, playerPos.offset(-10, 1, -10));

        // Place glowstone lights
        placeGlowstonePattern(source, playerPos.offset(radius - 2, 1, 0));
        placeGlowstonePattern(source, playerPos.offset(-(radius - 2), 1, 0));
        placeGlowstonePattern(source, playerPos.offset(0, 1, radius - 2));
        placeGlowstonePattern(source, playerPos.offset(0, 1, -(radius - 2)));
        placeGlowstonePattern(source, playerPos.offset(radius - 2, 1, radius - 2));
        placeGlowstonePattern(source, playerPos.offset(-(radius - 2), 1, -(radius - 2)));
        placeGlowstonePattern(source, playerPos.offset(radius - 2, 1, -(radius - 2)));
        placeGlowstonePattern(source, playerPos.offset(-(radius - 2), 1, radius - 2));

        source.sendSuccess(new StringTextComponent("Arena built with radius " + radius + ", walls of cobblestone, and lava fountains."), true);
    }

    private static void placeLavaFountain(CommandSource source, BlockPos centerPos) {
        source.getLevel().setBlock(centerPos.below(2), Blocks.OBSIDIAN.defaultBlockState(), 3);
        source.getLevel().setBlock(centerPos.below(1), Blocks.AIR.defaultBlockState(), 3);
        source.getLevel().setBlock(centerPos.above(), Blocks.LAVA.defaultBlockState(), 3);
        source.getLevel().setBlock(centerPos.above(2), Blocks.OBSIDIAN.defaultBlockState(), 3);
    }

    private static void placeGlowstonePattern(CommandSource source, BlockPos pos) {
        source.getLevel().setBlock(pos, Blocks.GLOWSTONE.defaultBlockState(), 3);
        source.getLevel().setBlock(pos.above(), Blocks.GLOWSTONE.defaultBlockState(), 3);
    }
}
