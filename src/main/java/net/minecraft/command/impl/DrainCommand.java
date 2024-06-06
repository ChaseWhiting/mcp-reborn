package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.concurrent.CompletableFuture;

public class DrainCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("drain")
                        .requires(source -> source.hasPermission(3))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, 100))
                                .executes(context -> executeDrain(context.getSource(), IntegerArgumentType.getInteger(context, "radius"), "both", false))
                                .then(Commands.argument("fluidType", StringArgumentType.word())
                                        .suggests(DrainCommand::suggestFluidTypes)
                                        .then(Commands.argument("includeSeaGrass", BoolArgumentType.bool())
                                                .executes(context -> {
                                                    CommandSource source = context.getSource();
                                                    int radius = IntegerArgumentType.getInteger(context, "radius");
                                                    String fluidType = StringArgumentType.getString(context, "fluidType");
                                                    boolean includeSeaGrass = BoolArgumentType.getBool(context, "includeSeaGrass");
                                                    return executeDrain(source, radius, fluidType, includeSeaGrass);
                                                })
                                        )
                                )
                        )
        );
    }

    private static int executeDrain(CommandSource source, int radius, String fluidType, boolean includeSeaGrass) {
        ServerWorld world = source.getLevel();
        Entity entity = source.getEntity() != null ? source.getEntity() : source.getServer().overworld().getRandomPlayer();
        if (entity == null) {
            source.sendFailure(new StringTextComponent("No player found to use as reference for position."));
            return 0;
        }
        BlockPos pos = new BlockPos(entity.position());
        int drained = drainFluid(world, pos, radius, fluidType, includeSeaGrass);

        source.sendSuccess(new StringTextComponent("Drained " + drained + " " + (drained == 1 ? "block" : "blocks") + "."), true);
        return 1;
    }

    private static int drainFluid(ServerWorld world, BlockPos center, int radius, String fluidType, boolean includeSeaGrass) {
        int count = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if ((fluidType.equals("water") && world.getBlockState(pos).getBlock() == Blocks.WATER) ||
                            (fluidType.equals("lava") && world.getBlockState(pos).getBlock() == Blocks.LAVA) ||
                            (fluidType.equals("both") && (world.getBlockState(pos).getBlock() == Blocks.WATER || world.getBlockState(pos).getBlock() == Blocks.LAVA))) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        count++;
                    }
                    BlockState blockState = world.getBlockState(pos);
                    if (includeSeaGrass && (world.getBlockState(pos).getBlock() == Blocks.SEAGRASS || world.getBlockState(pos).getBlock() == Blocks.TALL_SEAGRASS || blockState.getBlock() == Blocks.KELP || blockState.getBlock() == Blocks.KELP_PLANT)) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static CompletableFuture<Suggestions> suggestFluidTypes(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        builder.suggest("water").suggest("lava").suggest("both");
        return builder.buildFuture();
    }
}
