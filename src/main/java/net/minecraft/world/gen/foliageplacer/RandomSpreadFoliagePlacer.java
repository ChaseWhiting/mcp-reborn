package net.minecraft.world.gen.foliageplacer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.TreeFeature;

public class RandomSpreadFoliagePlacer extends FoliagePlacer {
    public static final Codec<RandomSpreadFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> 
        foliagePlacerParts(instance)
        .and(instance.group(
            FeatureSpread.codec(1, 512, 512).fieldOf("foliage_height").forGetter(placer -> placer.foliageHeight),
            Codec.intRange(0, 256).fieldOf("leaf_placement_attempts").forGetter(placer -> placer.leafPlacementAttempts)
        )).apply(instance, RandomSpreadFoliagePlacer::new)
    );

    private final FeatureSpread foliageHeight;
    private final int leafPlacementAttempts;

    public RandomSpreadFoliagePlacer(FeatureSpread radius, FeatureSpread offset, FeatureSpread foliageHeight, int leafPlacementAttempts) {
        super(radius, offset);
        this.foliageHeight = foliageHeight;
        this.leafPlacementAttempts = leafPlacementAttempts;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FoliagePlacerType.RANDOM_SPREAD_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(
        IWorldGenerationReader world, Random random, BaseTreeFeatureConfig config, int trunkHeight, 
        Foliage foliage, int foliageHeight, int foliageRadius, Set<BlockPos> leaves, int offset, 
        MutableBoundingBox boundingBox) {

        BlockPos basePos = foliage.foliagePos();
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int i = 0; i < this.leafPlacementAttempts; i++) {
            int xOffset = random.nextInt(foliageRadius * 2 + 1) - foliageRadius;
            int yOffset = random.nextInt(foliageHeight * 2 + 1) - foliageHeight;
            int zOffset = random.nextInt(foliageRadius * 2 + 1) - foliageRadius;

            mutablePos.set(basePos).move(xOffset, yOffset, zOffset);

            if (TreeFeature.validTreePos(world, mutablePos)) {
                world.setBlock(mutablePos, config.leavesProvider.getState(random, mutablePos), 19);
                leaves.add(mutablePos.immutable());
                boundingBox.expand(new MutableBoundingBox(mutablePos, mutablePos));
            }
        }
    }

    @Override
    public int foliageHeight(Random random, int trunkHeight, BaseTreeFeatureConfig config) {
        return this.foliageHeight.sample(random);
    }

    @Override
    protected boolean shouldSkipLocation(Random random, int dx, int dy, int dz, int radius, boolean largeTrunk) {
        return false;
    }
}
