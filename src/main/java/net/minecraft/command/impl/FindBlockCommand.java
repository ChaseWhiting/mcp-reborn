package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.server.ServerWorld;

public class FindBlockCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("findblock")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.argument("block", BlockStateArgument.block())
                                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 300))
                                        .executes(FindBlockCommand::execute)))
        );
    }

    private static int execute(CommandContext<CommandSource> context) {
        ServerWorld world = context.getSource().getLevel();
        BlockPos origin = new BlockPos(context.getSource().getPosition());
        BlockState targetBlock = BlockStateArgument.getBlock(context, "block").getState();
        int radius = IntegerArgumentType.getInteger(context, "radius");

        BlockPos closestBlock = findClosestBlock(world, origin, targetBlock, radius);
        try {
            showLocateResult(context.getSource(), "Nearest block", origin, closestBlock, "Nearest block found: %s at %s (distance: %s blocks)");
        } catch (NullPointerException e) {
            context.getSource().sendFailure(new StringTextComponent("An error occurred while trying to find the nearest block."));
        }
        return 1;
    }

    private static void showLocateResult(CommandSource source, String description, BlockPos origin, BlockPos target, String message) {
        double squaredDistance = origin.distSqr(target.getX(), target.getY(), target.getZ(), true);
        int distance = MathHelper.floor(Math.sqrt(squaredDistance));
        ITextComponent coordinates = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("chat.coordinates", target.getX(), target.getY(), target.getZ())).withStyle((style) ->
                style.withColor(TextFormatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + target.getX() + " " + target.getY() + " " + target.getZ()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip")))
        );

        ITextComponent resultMessage = new StringTextComponent(description + " found: ").append(coordinates).append(" (distance: " + distance + " blocks)");
        source.sendSuccess(resultMessage, false);
    }

    private static BlockPos findClosestBlock(ServerWorld world, BlockPos origin, BlockState targetBlock, int radius) {
        for (int currentRadius = 0; currentRadius <= radius; currentRadius++) {
            for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                for (int dy = -currentRadius; dy <= currentRadius; dy++) {
                    for (int dz = -currentRadius; dz <= currentRadius; dz++) {
                        // Check if the current position is within the current shell
                        if (Math.abs(dx) == currentRadius || Math.abs(dy) == currentRadius || Math.abs(dz) == currentRadius) {
                            BlockPos pos = origin.offset(dx, dy, dz);
                            if (world.getBlockState(pos).equals(targetBlock)) {
                                return pos; // Return immediately when the block is found
                            }
                        }
                    }
                }
            }
        }
        return null; // Return null if no block is found within the given radius
    }
}
