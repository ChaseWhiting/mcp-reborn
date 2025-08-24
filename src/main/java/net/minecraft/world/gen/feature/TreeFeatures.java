package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.UniformInt;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.treedecorator.LeafLitterTreeDecorator;
import net.minecraft.world.gen.treedecorator.TrunkVineTreeDecorator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TreeFeatures {

    public static FallenTreeConfiguration.FallenTreeConfigurationBuilder createFallenOak() {
        return TreeFeatures.createFallenTrees(Blocks.OAK_LOG, 4, 7).stumpDecorators(ImmutableList.of(TrunkVineTreeDecorator.INSTANCE));
    }

    public static FallenTreeConfiguration.FallenTreeConfigurationBuilder createFallenBirch(int n) {
        return TreeFeatures.createFallenTrees(Blocks.BIRCH_LOG, 5, n);
    }

    public static FallenTreeConfiguration.FallenTreeConfigurationBuilder createFallenJungle() {
        return TreeFeatures.createFallenTrees(Blocks.JUNGLE_LOG, 4, 11).stumpDecorators(ImmutableList.of(TrunkVineTreeDecorator.INSTANCE));
    }

    public static FallenTreeConfiguration.FallenTreeConfigurationBuilder createFallenSpruce() {
        return TreeFeatures.createFallenTrees(Blocks.SPRUCE_LOG, 6, 10);
    }

    public static FallenTreeConfiguration.FallenTreeConfigurationBuilder createSculkInfectedTree() {
        return TreeFeatures.createFallenTreeNoVines(Blocks.OAK_LOG, 2, 6).stumpDecorators(ImmutableList.of(new LeafLitterTreeDecorator(3, 3), sculk(0.85F, List.of(Direction.DOWN)))).logDecorators(ImmutableList.of(sculk(0.75f, List.of(Direction.DOWN, Direction.UP)), sculk(0.9F, List.of(Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.DOWN))));
    }

    public static AttachedToLogsDecorator sculk(float chance, List<Direction> directions) {
        return new AttachedToLogsDecorator(chance, new SimpleBlockStateProvider(Blocks.SCULK_VEIN.defaultBlockState()), Arrays.stream(Direction.values()).filter(dir -> !directions.contains(dir)).collect(Collectors.toList()));
    }

    public static FallenTreeConfiguration.FallenTreeConfigurationBuilder createFallenTrees(Block block, int n, int n2) {
        return new FallenTreeConfiguration.FallenTreeConfigurationBuilder(new SimpleBlockStateProvider(block.defaultBlockState()), UniformInt.of(n, n2)).logDecorators(ImmutableList.of(new AttachedToLogsDecorator(0.1f, new WeightedBlockStateProvider().add(Blocks.RED_MUSHROOM.defaultBlockState(), 2).add(Blocks.BROWN_MUSHROOM.defaultBlockState(), 1), List.of(Direction.UP))));
    }

    public static FallenTreeConfiguration.FallenTreeConfigurationBuilder createFallenTreeNoVines(Block block, int n, int n2) {
        return new FallenTreeConfiguration.FallenTreeConfigurationBuilder(new SimpleBlockStateProvider(block.defaultBlockState()), UniformInt.of(n, n2));
    }
}
