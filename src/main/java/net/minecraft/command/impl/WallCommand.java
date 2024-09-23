package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class WallCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("buildwall")
                .requires((source) -> source.hasPermission(2))
                // Case 1: Specifying both blocks, height, and radius
                .then(Commands.argument("block", BlockStateArgument.block())
                        .then(Commands.argument("topBlock", BlockStateArgument.block())
                                .then(Commands.argument("height", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("radius", IntegerArgumentType.integer(1))
                                                .executes((context) -> {
                                                    BlockStateInput blockArg = BlockStateArgument.getBlock(context, "block");
                                                    BlockStateInput topBlockArg = BlockStateArgument.getBlock(context, "topBlock");
                                                    int height = IntegerArgumentType.getInteger(context, "height");
                                                    int radius = IntegerArgumentType.getInteger(context, "radius");
                                                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                                                    buildWall(context.getSource(), player.blockPosition(), blockArg.getState().getBlock(), topBlockArg.getState().getBlock(), height, radius);
                                                    return 1;
                                                })))))

                // Case 2: Only specifying height and radius, using Stone Brick Wall and Torch as defaults
                .then(Commands.argument("height", IntegerArgumentType.integer(1))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1))
                                .executes((context) -> {
                                    int height = IntegerArgumentType.getInteger(context, "height");
                                    int radius = IntegerArgumentType.getInteger(context, "radius");
                                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                                    buildWall(context.getSource(), player.blockPosition(), Blocks.STONE_BRICK_WALL, Blocks.TORCH, height, radius);
                                    return 1;
                                })))

                // Case 3: Specifying block, height, and radius, using Torch as the default top block
                .then(Commands.argument("block", BlockStateArgument.block())
                        .then(Commands.argument("height", IntegerArgumentType.integer(1))
                                .then(Commands.argument("radius", IntegerArgumentType.integer(1))
                                        .executes((context) -> {
                                            BlockStateInput blockArg = BlockStateArgument.getBlock(context, "block");
                                            int height = IntegerArgumentType.getInteger(context, "height");
                                            int radius = IntegerArgumentType.getInteger(context, "radius");
                                            ServerPlayerEntity player = context.getSource().getPlayerOrException();
                                            buildWall(context.getSource(), player.blockPosition(), blockArg.getState().getBlock(), Blocks.TORCH.defaultBlockState().getBlock(), height, radius);
                                            return 1;
                                        }))))

        );
    }

    private static void buildWall(CommandSource source, BlockPos playerPos, Block block, Block torchBlock, int height, int radius) throws CommandSyntaxException {
        BlockPos.Mutable blockPos = new BlockPos.Mutable();
        int yStart = playerPos.getY();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int manhattanDistance = Math.abs(dx) + Math.abs(dz);
                if (manhattanDistance >= radius - 1 && manhattanDistance <= radius) {
                    BlockPos topBlockPos = null;

                    for (int dy = 0; dy < height; dy++) {
                        blockPos.set(playerPos.getX() + dx, yStart + dy, playerPos.getZ() + dz);
                        source.getLevel().setBlock(blockPos, block.defaultBlockState(), 3);

                        BlockPos belowPos = blockPos.below();
                        while ((source.getLevel().isEmptyBlock(belowPos) || source.getLevel().isWaterAt(belowPos) || source.getLevel().getBlockState(belowPos).getBlock() == Blocks.GRASS) && belowPos.getY() > 0) {
                            source.getLevel().setBlock(belowPos, block.defaultBlockState(), 3);
                            belowPos = belowPos.below();
                        }

                        topBlockPos = blockPos.immutable(); // Save the position of the top block
                    }

                    // Place a torch on the top block
                    if (topBlockPos != null) {
                        BlockPos torchPos = topBlockPos.above();
                        source.getLevel().setBlock(torchPos, torchBlock.defaultBlockState(), 3);
                    }
                }
            }
        }
        source.sendSuccess(new StringTextComponent("Wall built with height " + height + ", radius " + radius + " using " + block.getName().getString() + " and " + torchBlock.getName().getString() + " on top."), true);
    }
}
