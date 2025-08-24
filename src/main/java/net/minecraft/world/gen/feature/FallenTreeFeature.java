package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.treedecorator.TreeDecorator;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FallenTreeFeature extends Feature<FallenTreeConfiguration> {
    private static final int STUMP_HEIGHT = 1;
    private static final int STUMP_HEIGHT_PLUS_EMPTY_SPACE = 2;
    private static final int FALLEN_LOG_MAX_FALL_HEIGHT_TO_GROUND = 5;
    private static final int FALLEN_LOG_MAX_GROUND_GAP = 2;
    private static final int FALLEN_LOG_MAX_SPACE_FROM_STUMP = 2;
    private static final int BLOCK_UPDATE_FLAGS = 19;

    public FallenTreeFeature(Codec<FallenTreeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos blockPos, FallenTreeConfiguration config) {
        this.placeFallenTree(config, blockPos, world, random);
        return true;
    }

    private void placeFallenTree(FallenTreeConfiguration configuration, BlockPos blockPos, ISeedReader world, Random random) {
        this.placeStump(configuration, world, random, blockPos.mutable());
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int n = configuration.logLength.sample(random) - 2;
        BlockPos.Mutable mutableBlockPos = blockPos.relative(direction, 2 + random.nextInt(2)).mutable();
        this.setGroundHeightForFallenLogStartPos(world, mutableBlockPos);
        if (this.canPlaceEntireFallenLog(world, n, mutableBlockPos, direction)) {
            this.placeFallenLog(configuration, world, random, n, mutableBlockPos, direction);
        }
    }

    private void setGroundHeightForFallenLogStartPos(ISeedReader worldGenLevel, BlockPos.Mutable mutableBlockPos) {
        mutableBlockPos.move(Direction.UP, 1);
        for (int i = 0; i < 6; ++i) {
            if (this.mayPlaceOn(worldGenLevel, mutableBlockPos)) {
                return;
            }
            mutableBlockPos.move(Direction.DOWN);
        }
    }

    private void placeStump(FallenTreeConfiguration fallenTreeConfiguration, ISeedReader worldGenLevel, Random randomSource, BlockPos.Mutable mutableBlockPos) {
        BlockPos blockPos = FallenTreeFeature.placeLogBlock(fallenTreeConfiguration, worldGenLevel, randomSource, mutableBlockPos, Function.identity());
        this.decorateLogs(worldGenLevel, randomSource, Set.of(blockPos), fallenTreeConfiguration.stumpDecorators);
    }

    private void placeFallenLog(FallenTreeConfiguration fallenTreeConfiguration, ISeedReader worldGenLevel, Random randomSource, int n, BlockPos.Mutable mutableBlockPos, Direction direction) {
        HashSet<BlockPos> hashSet = new HashSet<BlockPos>();
        for (int i = 0; i < n; ++i) {
            hashSet.add(FallenTreeFeature.placeLogBlock(fallenTreeConfiguration, worldGenLevel, randomSource, mutableBlockPos, FallenTreeFeature.getSidewaysStateModifier(direction)));
            mutableBlockPos.move(direction);
        }
        this.decorateLogs(worldGenLevel, randomSource, hashSet, fallenTreeConfiguration.logDecorators);
    }

    private boolean mayPlaceOn(ISeedReader levelAccessor, BlockPos blockPos) {
        return TreeFeature.validTreePos(levelAccessor, blockPos) && this.isOverSolidGround(levelAccessor, blockPos);
    }

    private boolean isOverSolidGround(ISeedReader levelAccessor, BlockPos blockPos) {
        return levelAccessor.getBlockState(blockPos.below()).isFaceSturdy(levelAccessor, blockPos, Direction.UP);
    }

    private static BlockPos placeLogBlock(FallenTreeConfiguration fallenTreeConfiguration, ISeedReader worldGenLevel, Random randomSource, BlockPos.Mutable mutableBlockPos, Function<BlockState, BlockState> function) {
        worldGenLevel.setBlock(mutableBlockPos, function.apply(fallenTreeConfiguration.trunkProvider.getState(randomSource, mutableBlockPos)), 19);
        return mutableBlockPos.immutable();
    }

    private void decorateLogs(
            ISeedReader worldGenLevel,
            Random randomSource,
            Set<BlockPos> logPositions,
            List<TreeDecorator> decorators
    ) {
        if (!decorators.isEmpty()) {
            // Convert logPositions to a List (since 1.16.5 uses List instead of Set)
            List<BlockPos> logList = List.copyOf(logPositions);
            List<BlockPos> emptyLeafList = List.of(); // No leaves
            Set<BlockPos> decoratedPositions = new HashSet<>();
            MutableBoundingBox boundingBox = new MutableBoundingBox();

            // Apply each tree decorator
            for (TreeDecorator decorator : decorators) {
                decorator.place(worldGenLevel, randomSource, logList, emptyLeafList, decoratedPositions, boundingBox);
            }
        }
    }


    private BiConsumer<BlockPos, BlockState> getDecorationSetter(ISeedReader worldGenLevel) {
        return (blockPos, blockState) -> worldGenLevel.setBlock((BlockPos)blockPos, (BlockState)blockState, 19);
    }

    private static Function<BlockState, BlockState> getSidewaysStateModifier(Direction direction) {
        return blockState -> (BlockState)blockState.setValue(RotatedPillarBlock.AXIS, direction.getAxis());
    }

    private boolean canPlaceEntireFallenLog(ISeedReader worldGenLevel, int n, BlockPos.Mutable mutableBlockPos, Direction direction) {
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            if (!TreeFeature.validTreePos(worldGenLevel, mutableBlockPos)) {
                return false;
            }
            if (!this.isOverSolidGround(worldGenLevel, mutableBlockPos)) {
                if (++n2 > 2) {
                    return false;
                }
            } else {
                n2 = 0;
            }
            mutableBlockPos.move(direction);
        }
        mutableBlockPos.move(direction.getOpposite(), n);
        return true;
    }


}
