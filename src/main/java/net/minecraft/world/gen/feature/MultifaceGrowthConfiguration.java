package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MultifaceSpreadeableBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Random;


public class MultifaceGrowthConfiguration implements IFeatureConfig {
    public static final Codec<MultifaceGrowthConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Registry.BLOCK.fieldOf("block")
                            .flatXmap(MultifaceGrowthConfiguration::apply, DataResult::success)
                            .orElse((MultifaceSpreadeableBlock) Blocks.SCULK_VEIN)
                            .forGetter(cfg -> cfg.placeBlock),
                    Codec.intRange(1, 64).fieldOf("search_range").orElse(10)
                            .forGetter(cfg -> cfg.searchRange),
                    Codec.BOOL.fieldOf("can_place_on_floor").orElse(false)
                            .forGetter(cfg -> cfg.canPlaceOnFloor),
                    Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false)
                            .forGetter(cfg -> cfg.canPlaceOnCeiling),
                    Codec.BOOL.fieldOf("can_place_on_wall").orElse(false)
                            .forGetter(cfg -> cfg.canPlaceOnWall),
                    Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_spreading").orElse(0.5f)
                            .forGetter(cfg -> cfg.chanceOfSpreading),
                    Codec.list(Registry.BLOCK).fieldOf("can_be_placed_on").orElse(List.of()) // Store list of blocks
                            .forGetter(cfg -> cfg.canBePlacedOn)
            ).apply(instance, MultifaceGrowthConfiguration::new)
    );

    public final MultifaceSpreadeableBlock placeBlock;
    public final int searchRange;
    public final boolean canPlaceOnFloor;
    public final boolean canPlaceOnCeiling;
    public final boolean canPlaceOnWall;
    public final float chanceOfSpreading;
    public final List<Block> canBePlacedOn;
    private final ObjectArrayList<Direction> validDirections;

    private static DataResult<MultifaceSpreadeableBlock> apply(Block block) {
        DataResult<MultifaceSpreadeableBlock> dataResult;
        if (block instanceof MultifaceSpreadeableBlock) {
            MultifaceSpreadeableBlock multifaceBlock = (MultifaceSpreadeableBlock)block;
            dataResult = DataResult.success(multifaceBlock);
        } else {
            dataResult = DataResult.error("Growth block should be a multiface block");
        }
        return dataResult;
    }

    public MultifaceGrowthConfiguration(MultifaceSpreadeableBlock multifaceBlock, int searchRange, boolean canPlaceOnFloor, boolean canPlaceOnCeiling, boolean canPlaceOnWall, float chanceOfSpreading, List<Block> canBePlacedOn) {
        this.placeBlock = multifaceBlock;
        this.searchRange = searchRange;
        this.canPlaceOnFloor = canPlaceOnFloor;
        this.canPlaceOnCeiling = canPlaceOnCeiling;
        this.canPlaceOnWall = canPlaceOnWall;
        this.chanceOfSpreading = chanceOfSpreading;
        this.canBePlacedOn = canBePlacedOn;
        this.validDirections = new ObjectArrayList<>(6);

        if (canPlaceOnCeiling) {
            this.validDirections.add(Direction.UP);
        }
        if (canPlaceOnFloor) {
            this.validDirections.add(Direction.DOWN);
        }
        if (canPlaceOnWall) {
            Direction.Plane.HORIZONTAL.forEach(this.validDirections::add);
        }
    }


    public List<Direction> getShuffledDirectionsExcept(Random randomSource, Direction direction) {
        return Util.toShuffledList(this.validDirections.stream().filter(direction2 -> direction2 != direction), randomSource);
    }

    public List<Direction> getShuffledDirections(Random randomSource) {
        return Util.shuffledCopy(this.validDirections, randomSource);
    }
}

