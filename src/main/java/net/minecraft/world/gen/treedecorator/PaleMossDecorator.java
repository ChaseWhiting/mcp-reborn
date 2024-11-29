package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HangingMossBlock;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.Feature;

public class PaleMossDecorator extends TreeDecorator {
    public static final Codec<PaleMossDecorator> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.floatRange(0.0F, 1.0F).fieldOf("leaves_probability").forGetter((decorator) -> decorator.leavesProbability),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("trunk_probability").forGetter((decorator) -> decorator.trunkProbability),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("leave_chance").forGetter((decorator) -> decorator.leaveChance)
            ).apply(instance, PaleMossDecorator::new)
    );

    private final float leavesProbability;
    private final float trunkProbability;
    private final float leaveChance;
    public PaleMossDecorator(float leavesProbability, float trunkProbability) {
        this(leavesProbability, trunkProbability, 0.5F);
    }

    public PaleMossDecorator(float leavesProbability, float trunkProbability, float leaveChance) {
        this.leavesProbability = leavesProbability;
        this.trunkProbability = trunkProbability;
        this.leaveChance = leaveChance;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.PALE_MOSS_DECORATE;
    }

    @Override
    public void place(ISeedReader reader, Random random, List<BlockPos> logs, List<BlockPos> leaves, Set<BlockPos> set, MutableBoundingBox box) {
        // Iterate over logs to place moss on the trunk
        for (BlockPos logPos : logs) {
            BlockPos belowPos = logPos.below();
            BlockPos abovePos = logPos.above();

            // Place moss with trunk probability
            if (random.nextFloat() < this.trunkProbability && Feature.isAir(reader, belowPos) && reader.getBlockState(logPos).is(Blocks.PALE_OAK_LOG)) {
                addMossHanger(belowPos.above(), reader, random, set, box);
            }
            if (random.nextFloat() < this.trunkProbability && Feature.isAir(reader, abovePos) && reader.getBlockState(logPos).is(Blocks.PALE_OAK_LOG)) {
                reader.setBlock(abovePos, Blocks.PALE_MOSS_CARPET.defaultBlockState(), 2);
            }
        }

        // Iterate over leaves to place moss underneath using leaves probability
        for (BlockPos leafPos : leaves) {
            BlockPos belowPos = leafPos.below();
            if (random.nextFloat() < this.leavesProbability && Feature.isAir(reader, belowPos)) {
                addMossHanger(belowPos, reader, random, set, box);
            }
        }
    }

    private void addMossHanger(BlockPos pos, ISeedReader reader, Random random, Set<BlockPos> set, MutableBoundingBox box) {
        while (Feature.isAir(reader, pos.below()) && random.nextFloat() > this.leaveChance) {
            reader.setBlock(pos, Blocks.PALE_HANGING_MOSS.defaultBlockState().setValue(HangingMossBlock.TIP, false), 2);
            pos = pos.below();
        }
        reader.setBlock(pos, Blocks.PALE_HANGING_MOSS.defaultBlockState().setValue(HangingMossBlock.TIP, true), 2);
    }
}
