package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.sculk.SculkPatchConfiguration;
import net.minecraft.block.sculk.SculkVeinBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.UniformInt;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

public class AttachedToLogsDecorator extends TreeDecorator {
    public static final Codec<Direction> DIRECTION_CODEC = Codec.STRING.xmap(
            Direction::byName, // Convert String to Direction
            Direction::getName // Convert Direction to String
    );


    public static final Codec<AttachedToLogsDecorator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.floatRange(0.0f, 1.0f)
                    .fieldOf("probability")
                    .forGetter(attachedToLogsDecorator -> attachedToLogsDecorator.probability),
            BlockStateProvider.CODEC
                    .fieldOf("block_provider")
                    .forGetter(attachedToLogsDecorator -> attachedToLogsDecorator.blockProvider),
            DIRECTION_CODEC.listOf()
                    .fieldOf("directions")
                    .forGetter(attachedToLogsDecorator -> attachedToLogsDecorator.directions)
    ).apply(instance, AttachedToLogsDecorator::new));


    private final float probability;
    private final BlockStateProvider blockProvider;
    private final List<Direction> directions;

    public AttachedToLogsDecorator(float f, BlockStateProvider blockStateProvider, List<Direction> list) {
        this.probability = f;
        this.blockProvider = blockStateProvider;
        this.directions = list;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.ATTACHED_TO_LOGS;
    }

    @Override
    public void place(ISeedReader world, Random random, List<BlockPos> logs, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
        for (BlockPos pos : Util.shuffledCopy(logs, random)) {
            Direction direction = Util.getRandom(this.directions, random);
            BlockPos bp2 = pos.relative(direction);
            if (!(random.nextFloat() <= this.probability) || !world.getBlockState(bp2).isAir()) continue;
            Consumer<BlockState> stateConsumer = (st) -> {};
            BlockState state = this.blockProvider.getState(random, bp2);
            if (state.getBlock() instanceof SculkVeinBlock) {
                ConfiguredFeature<?, ?> SCULK = new ConfiguredFeature<>(Feature.SCULK_PATCH, new SculkPatchConfiguration(2, 2, 4, 2, 2, UniformInt.of(0, 1), 0f));
                SCULK.place(world, null, random, bp2);
                continue;
            }
            stateConsumer.accept(state);
            world.setBlock(bp2, state, 2);
        }
    }
}
