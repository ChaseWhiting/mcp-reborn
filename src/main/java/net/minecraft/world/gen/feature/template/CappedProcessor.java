package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class CappedProcessor extends StructureProcessor {
    public static final Codec<CappedProcessor> CODEC = ExtraCodecs.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(IStructureProcessorType.SINGLE_CODEC.fieldOf("delegate").forGetter(cappedProcessor -> cappedProcessor.delegate), IntProvider.POSITIVE_CODEC.fieldOf("limit").forGetter(cappedProcessor -> cappedProcessor.limit)).apply(instance, CappedProcessor::new)));
    private final StructureProcessor delegate;
    private final IntProvider limit;

    public CappedProcessor(StructureProcessor structureProcessor, IntProvider intProvider) {
        this.delegate = structureProcessor;
        this.limit = intProvider;
    }

    @Override
    public @Nullable Template.BlockInfo processBlock(IWorldReader worldReader, BlockPos blockPos, BlockPos relativePos, Template.BlockInfo originalBlockInfo, Template.BlockInfo currentBlockInfo, PlacementSettings settings) {

        return currentBlockInfo;
    }


    public final List<Template.BlockInfo> finalizeProcessing(IWorld serverLevelAccessor, BlockPos blockPos, BlockPos blockPos2, List<Template.BlockInfo> list, List<Template.BlockInfo> list2, PlacementSettings structurePlaceSettings) {
        if (this.limit.getMaxValue() == 0 || list2.isEmpty()) {
            return list2;
        }
        if (list.size() != list2.size()) {
            Util.logAndPauseIfInIde("Original block info list not in sync with processed list, skipping processing. Original size: " + list.size() + ", Processed size: " + list2.size());
            return list2;
        }
        RandomSource randomSource = RandomSource.create(serverLevelAccessor instanceof ServerWorld serverWorld ? serverWorld.getSeed() : serverLevelAccessor.getRandom().nextLong()).forkPositional().at(blockPos);
        int n = Math.min(this.limit.sample(randomSource), list2.size());
        if (n < 1) {
            return list2;
        }
        IntArrayList intArrayList = Util.toShuffledList(IntStream.range(0, list2.size()), randomSource);
        IntIterator intIterator = intArrayList.iterator();
        int n2 = 0;
        while (intIterator.hasNext() && n2 < n) {
            Template.BlockInfo structureBlockInfo;
            int n3 = intIterator.nextInt();
            Template.BlockInfo structureBlockInfo2 = list.get(n3);
            Template.BlockInfo structureBlockInfo3 = this.delegate.processBlock(serverLevelAccessor, blockPos, blockPos2, structureBlockInfo2, structureBlockInfo = list2.get(n3), structurePlaceSettings);
            if (structureBlockInfo3 == null || structureBlockInfo.equals(structureBlockInfo3)) continue;
            ++n2;
            list2.set(n3, structureBlockInfo3);
        }
        return list2;
    }





    @Override
    protected IStructureProcessorType<?> getType() {
        return IStructureProcessorType.CAPPED;
    }
}
