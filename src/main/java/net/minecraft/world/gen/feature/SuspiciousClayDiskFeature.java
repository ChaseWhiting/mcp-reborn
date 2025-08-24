package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.BrushableBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomSource;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class SuspiciousClayDiskFeature extends Feature<SphereReplaceConfig> {
    public SuspiciousClayDiskFeature(Codec<SphereReplaceConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos center, SphereReplaceConfig config) {
        int radius = config.radius.sample(random);
        Set<BlockPos> clayPositions = new HashSet<>();

        // Step 1: Collect valid disk positions
        for (int x = center.getX() - radius; x <= center.getX() + radius; ++x) {
            for (int z = center.getZ() - radius; z <= center.getZ() + radius; ++z) {
                int dx = x - center.getX();
                int dz = z - center.getZ();
                if (dx * dx + dz * dz <= radius * radius) {
                    for (int y = center.getY() - config.halfHeight; y <= center.getY() + config.halfHeight; ++y) {
                        BlockPos pos = new BlockPos(x, y, z);
                        Block currentBlock = world.getBlockState(pos).getBlock();
                        for (BlockState target : config.targets) {
                            if (target.is(currentBlock)) {
                                clayPositions.add(pos);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (clayPositions.isEmpty()) return false;

        // Step 2: Determine how many suspicious blocks to place
        int totalClay = clayPositions.size();
        int suspiciousCount = 5 + random.nextInt(Math.max(1, (int) (totalClay * 0.10) - 5 + 1)); // between 5% and 10%

        if (random.nextFloat() < 0.6F) {
            if (random.nextInt(6) == 0) suspiciousCount = 0;
        }

        // Step 3: Choose suspicious clay positions via random walk (to keep them clustered)
        List<BlockPos> clayList = new ArrayList<>(clayPositions);
        Collections.shuffle(clayList, random);
        Set<BlockPos> suspiciousSet = new HashSet<>();

        BlockPos start = clayList.get(0);
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        suspiciousSet.add(start);

        while (!queue.isEmpty() && suspiciousSet.size() < suspiciousCount) {
            BlockPos current = queue.poll();
            for (BlockPos offset : getNeighborOffsets()) {
                BlockPos neighbor = current.offset(offset);
                if (clayPositions.contains(neighbor) && !suspiciousSet.contains(neighbor)) {
                    suspiciousSet.add(neighbor);
                    queue.add(neighbor);
                    if (suspiciousSet.size() >= suspiciousCount) break;
                }
            }
        }

        // Step 4: Place blocks in world
        for (BlockPos pos : clayPositions) {
            if (suspiciousSet.contains(pos)) {
                BlockPos below = pos.below();


                    // Place regular clay at top
                    world.setBlock(pos, Blocks.CLAY.defaultBlockState(), 2);

                    // Place suspicious clay below
                    world.setBlock(below, Blocks.SUSPICIOUS_CLAY.defaultBlockState(), 2);
                    TileEntity tileEntity = world.getBlockEntity(below);
                    if (tileEntity instanceof BrushableBlockEntity brushableBlock) {
                        brushableBlock.setLootTable(config.river ? LootTables.RIVER_CLAY_ARCHAEOLOGY : LootTables.RIVER_SWAMP_CLAY_ARCHAEOLOGY, RandomSource.create(world.getSeed()).forkPositional().at(below).nextLong());
                    }

                    // Cover suspicious clay's exposed sides
                    for (BlockPos dir : getHorizontalOffsets()) {
                        BlockPos side = below.offset(dir);
                        if (world.isEmptyBlock(side) || world.getBlockState(side).getBlock() == Blocks.DIRT) {
                            world.setBlock(side, Blocks.CLAY.defaultBlockState(), 2);
                        }
                    }

            } else {
                // Regular clay
                world.setBlock(pos, Blocks.CLAY.defaultBlockState(), 2);
            }
        }

        return true;
    }

    private List<BlockPos> getNeighborOffsets() {
        return Arrays.asList(
                new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0),
                new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)
        );
    }

    private List<BlockPos> getHorizontalOffsets() {
        return Arrays.asList(
                new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0),
                new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)
        );
    }
}
