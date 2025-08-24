package net.minecraft.world.gen.trunkplacer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.random.RandomSource;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class UpwardsBranchingTrunkPlacer extends AbstractTrunkPlacer {
    public static final Codec<UpwardsBranchingTrunkPlacer> CODEC =
            RecordCodecBuilder.create(instance -> trunkPlacerParts(instance)
                    .and(instance.group(
                            IntProvider.POSITIVE_CODEC.fieldOf("extra_branch_steps").forGetter(brp -> brp.extraBranchSteps),
                            Codec.floatRange(0.0f, 1.0f).fieldOf("place_branch_per_log_probability").forGetter(brp -> brp.placeBranchPerLogProbability),
                            IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter(brp -> brp.extraBranchLength),
                            Registry.BLOCK.listOf().xmap(ImmutableSet::copyOf, ImmutableList::copyOf).optionalFieldOf("can_grow_through",ImmutableSet.of()).forGetter(brp -> brp.canGrowThrough)
                    )).apply(instance, UpwardsBranchingTrunkPlacer::new)); // This was missing

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.UPWARDS_BRANCHING_TRUNK_PLACER_TRUNK;
    }

    private final IntProvider extraBranchSteps;
    private final float placeBranchPerLogProbability;
    private final IntProvider extraBranchLength;
    private final ImmutableSet<Block> canGrowThrough;

    public UpwardsBranchingTrunkPlacer(int n, int n2, int n3, IntProvider uniformInt, float f, IntProvider uniformInt1, ImmutableSet<Block> set) {
        super(n, n2, n3);
        this.extraBranchSteps = uniformInt;
        this.extraBranchLength = uniformInt1;
        this.placeBranchPerLogProbability = f;
        this.canGrowThrough = set;
    }

    @Override
    public List<FoliagePlacer.Foliage> placeTrunk(IWorldGenerationReader worldReader, Random random, int number, BlockPos position, Set<BlockPos> blockSet, MutableBoundingBox boundingBox, BaseTreeFeatureConfig configuration) {
        ArrayList<FoliagePlacer.Foliage> list = new ArrayList<>();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i < number; i++) {
            int ab = position.getY() + i;

            if (placeLogHere(worldReader, random, mutable.set(position.getX(), ab, position.getZ()), blockSet, boundingBox, configuration) && i < number - 1 && random.nextFloat() < this.placeBranchPerLogProbability) {
                Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                int branchExtra = this.extraBranchLength.sample(RandomSource.create(random.nextLong()));
                int n4 = Math.max(0, branchExtra - this.extraBranchLength.sample(RandomSource.create(random.nextLong())) - 1);
                int branchSteps = this.extraBranchSteps.sample(RandomSource.create(random.nextLong()));
                this.placeBranch(worldReader, random, number, configuration, list, mutable, ab,  direction, n4, branchSteps, blockSet, boundingBox);
            }
            if (i != number - 1) continue;
            list.add(new FoliagePlacer.Foliage(mutable.set(position.getX(), ab + 1, position.getZ()), 0, false));

        }
        return list;
    }

    private void placeBranch(IWorldGenerationReader worldReader, Random random, int n, BaseTreeFeatureConfig config, ArrayList<FoliagePlacer.Foliage> list, BlockPos.Mutable mutable, int ab, Direction direction, int n4, int branchSteps, Set<BlockPos> set, MutableBoundingBox box) {
        int n5 = ab + n4;
        int x = mutable.getX();
        int z = mutable.getZ();
        for (int i = ab; i < n && n4 > 0; i++, --n4) {
            if (i < 1) continue;
            int n8 = ab + i;
            n5 = n8;
            if (placeLogHere(worldReader, random, mutable.set(x += direction.getStepX(), n8, z += direction.getStepZ()), set, box, config)) {
                n5++;
            }
            list.add(new FoliagePlacer.Foliage(mutable.immutable(), 0, false));
        }
        if (n5 - ab > 1) {
            BlockPos blockPos = new BlockPos(x, n5, z);
            list.add(new FoliagePlacer.Foliage(blockPos, 0, false));
            list.add(new FoliagePlacer.Foliage(blockPos.below(2), 0, false));
        }
    }

    protected boolean placeLogHere(IWorldGenerationReader p_236911_0_, Random p_236911_1_, BlockPos p_236911_2_, Set<BlockPos> p_236911_3_, MutableBoundingBox p_236911_4_, BaseTreeFeatureConfig p_236911_5_) {
        if (TreeFeature.validTreePos(p_236911_0_, p_236911_2_) || canGrowThrough.contains(p_236911_5_.trunkProvider.getState(p_236911_1_, p_236911_2_).getBlock())) {
            setBlock(p_236911_0_, p_236911_2_, p_236911_5_.trunkProvider.getState(p_236911_1_, p_236911_2_), p_236911_4_);
            p_236911_3_.add(p_236911_2_.immutable());
            return true;
        } else {
            return false;
        }
    }
}
