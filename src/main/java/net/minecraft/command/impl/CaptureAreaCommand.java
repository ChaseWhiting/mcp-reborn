package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CaptureAreaCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("capturearea")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("center", BlockPosArgument.blockPos())
                        .then(Commands.argument("expand_x", IntegerArgumentType.integer(0))
                                .then(Commands.argument("expand_y", IntegerArgumentType.integer(0))
                                        .then(Commands.argument("expand_z", IntegerArgumentType.integer(0))
                                                .then(Commands.argument("depth", IntegerArgumentType.integer(0))
                                                        .executes(context -> {
                                                            try {
                                                                return captureArea(
                                                                        context.getSource(),
                                                                        BlockPosArgument.getLoadedBlockPos(context, "center"),
                                                                        IntegerArgumentType.getInteger(context, "expand_x"),
                                                                        IntegerArgumentType.getInteger(context, "expand_y"),
                                                                        IntegerArgumentType.getInteger(context, "expand_z"),
                                                                        IntegerArgumentType.getInteger(context, "depth")
                                                                );
                                                            } catch (IOException e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                        })
                                                ))))));
    }

    private static int captureArea(CommandSource source, BlockPos center, int expandX, int expandY, int expandZ, int depth) throws IOException {
        ServerWorld world = source.getLevel();
        MutableBoundingBox box = new MutableBoundingBox(
                center.getX() - expandX, center.getY() - expandY, center.getZ() - expandZ,
                center.getX() + expandX, center.getY() + expandY, center.getZ() + expandZ
        );

        Map<BlockPos, String> blocks = new HashMap<>();
        Map<BlockPos, Boolean> visited = new HashMap<>();
        List<String> waterAreas = new ArrayList<>();
        List<String> lavaAreas = new ArrayList<>();
        List<String> entities = new ArrayList<>();

        for (BlockPos pos : BlockPos.betweenClosed(box.x0, box.y0, box.z0, box.x1, box.y1, box.z1)) {
            Block block = world.getBlockState(pos).getBlock();
            if (block == Blocks.AIR) continue;

            boolean isSurface = isSurfaceBlock(world, pos);
            if (!isSurface && !(depth > 0 && isWithinDepth(world, pos, depth))) continue;

            if (block == Blocks.WATER) {
                waterAreas.add(pos.toShortString());
                continue;
            }
            if (block == Blocks.LAVA) {
                lavaAreas.add(pos.toShortString());
                continue;
            }

            blocks.put(pos.immutable(), block.getName().getString());
            visited.put(pos.immutable(), false);
        }

        for (Entity entity : world.getEntities(null, new net.minecraft.util.math.AxisAlignedBB(box.x0, box.y0, box.z0, box.x1, box.y1, box.z1))) {
            if (entity instanceof LivingEntity) {
                entities.add(String.format("%s at (%d, %d, %d)", entity.getName().getString(), entity.blockPosition().getX(), entity.blockPosition().getY(), entity.blockPosition().getZ()));
            }
        }

        File exportDir = new File("capture_exports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        String baseName = "capture_" + System.currentTimeMillis();
        File file = new File(exportDir, baseName + ".txt");
        int counter = 1;
        while (file.exists()) {
            file = new File(exportDir, baseName + "_" + counter++ + ".txt");
        }

        try (FileWriter writer = new FileWriter(file)) {
            Map<String, List<BlockPos>> groupedBlocks = groupConnectedBlocks(blocks, visited);

            writer.write("Captured Area:\n\n");
            for (String blockName : groupedBlocks.keySet()) {
                List<BlockPos> positions = groupedBlocks.get(blockName);
                if (positions.size() >= 4) {
                    BlockPos min = getMinPos(positions);
                    BlockPos max = getMaxPos(positions);
                    writer.write(String.format("%s spanning from (%d,%d,%d) to (%d,%d,%d)\n", blockName, min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
                } else {
                    for (BlockPos p : positions) {
                        writer.write(String.format("%s at (%d,%d,%d)\n", blockName, p.getX(), p.getY(), p.getZ()));
                    }
                }
            }

            writer.write("\nWater Areas:\n");
            writer.write(groupLiquid(waterAreas, "Water"));

            writer.write("\nLava Areas:\n");
            writer.write(groupLiquid(lavaAreas, "Lava"));

            writer.write("\nEntities:\n");
            for (String entity : entities) {
                writer.write(entity + "\n");
            }
        }

        source.sendSuccess(new TranslationTextComponent("Captured area saved to " + file.getAbsolutePath()), true);
        return 1;
    }

    private static boolean isSurfaceBlock(ServerWorld world, BlockPos pos) {
        BlockPos up = pos.above();
        return world.getBlockState(up).isAir();
    }

    private static boolean isWithinDepth(ServerWorld world, BlockPos pos, int depth) {
        BlockPos.Mutable searchPos = pos.mutable();
        int count = 0;
        while (count < depth && searchPos.getY() > 0) {
            searchPos.move(0, 1, 0);
            if (world.getBlockState(searchPos).isAir()) return true;
            count++;
        }
        return false;
    }

    private static Map<String, List<BlockPos>> groupConnectedBlocks(Map<BlockPos, String> blocks, Map<BlockPos, Boolean> visited) {
        Map<String, List<BlockPos>> grouped = new HashMap<>();
        for (Map.Entry<BlockPos, String> entry : blocks.entrySet()) {
            BlockPos pos = entry.getKey();
            String blockName = entry.getValue();
            if (visited.get(pos)) continue;

            List<BlockPos> group = new ArrayList<>();
            Queue<BlockPos> queue = new LinkedList<>();
            queue.add(pos);
            visited.put(pos, true);

            while (!queue.isEmpty()) {
                BlockPos current = queue.poll();
                group.add(current);

                for (BlockPos neighbor : getNeighbors(current)) {
                    if (blocks.containsKey(neighbor) && blocks.get(neighbor).equals(blockName) && !visited.get(neighbor)) {
                        queue.add(neighbor);
                        visited.put(neighbor, true);
                    }
                }
            }

            grouped.computeIfAbsent(blockName, k -> new ArrayList<>()).addAll(group);
        }
        return grouped;
    }

    private static List<BlockPos> getNeighbors(BlockPos pos) {
        return Arrays.asList(
                pos.north(), pos.south(), pos.east(), pos.west(), pos.above(), pos.below()
        );
    }

    private static BlockPos getMinPos(List<BlockPos> positions) {
        int minX = positions.stream().mapToInt(BlockPos::getX).min().orElse(0);
        int minY = positions.stream().mapToInt(BlockPos::getY).min().orElse(0);
        int minZ = positions.stream().mapToInt(BlockPos::getZ).min().orElse(0);
        return new BlockPos(minX, minY, minZ);
    }

    private static BlockPos getMaxPos(List<BlockPos> positions) {
        int maxX = positions.stream().mapToInt(BlockPos::getX).max().orElse(0);
        int maxY = positions.stream().mapToInt(BlockPos::getY).max().orElse(0);
        int maxZ = positions.stream().mapToInt(BlockPos::getZ).max().orElse(0);
        return new BlockPos(maxX, maxY, maxZ);
    }

    private static String groupLiquid(List<String> areas, String type) {
        if (areas.isEmpty()) return "None\n";
        return String.format("%s spanning %d blocks\n", type, areas.size());
    }
}