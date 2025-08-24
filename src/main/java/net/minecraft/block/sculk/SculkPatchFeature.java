package net.minecraft.block.sculk;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.List;
import java.util.Random;

public class SculkPatchFeature extends Feature<SculkPatchConfiguration> {
    private final boolean grass;
    public SculkPatchFeature(Codec<SculkPatchConfiguration> codec, boolean grass) {
        super(codec);
        this.grass = grass;
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random randomSource, BlockPos position, SculkPatchConfiguration sculkPatchConfiguration) {
        int n;
        int n2;
        if (!this.canSpreadFrom(world, position)) {
            return false;
        }

        SculkSpreader sculkSpreader = SculkSpreader.createWorldGenSpreader();
        int n3 = sculkPatchConfiguration.spreadRounds() + sculkPatchConfiguration.growthRounds();
        for (int i = 0; i < n3; ++i) {
            for (n2 = 0; n2 < sculkPatchConfiguration.chargeCount(); n2 += 1) {
                sculkSpreader.addCursors(position, sculkPatchConfiguration.amountPerCharge());
            }
            n2 = i < sculkPatchConfiguration.spreadRounds() ? 1 : 0;
            for (n = 0; n < sculkPatchConfiguration.spreadAttempts(); ++n) {
                sculkSpreader.updateCursors(world, position, randomSource, n2 != 0);
            }
            sculkSpreader.clear();
        }
        BlockPos blockPos2 = position.below();
        if (randomSource.nextFloat() <= sculkPatchConfiguration.catalystChance() && world.getBlockState(blockPos2).isCollisionShapeFullBlock(world, blockPos2)) {
            world.setBlock(position, Blocks.SCULK_CATALYST.defaultBlockState(), 3);
        }
        n2 = sculkPatchConfiguration.extraRareGrowths().sample(randomSource);
        for (n = 0; n < n2; ++n) {
            BlockPos blockPos3 = position.offset(randomSource.nextInt(5) - 2, 0, randomSource.nextInt(5) - 2);
            if (!world.getBlockState(blockPos3).isAir() || !world.getBlockState(blockPos3.below()).isFaceSturdy(world, blockPos3.below(), Direction.UP)) continue;
            world.setBlock(blockPos3, (BlockState)Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, true), 3);
        }
        return true;
    }


    private boolean canSpreadFrom(ISeedReader levelAccessor, BlockPos blockPos2) {
        block5: {
            block4: {
                BlockState blockState = levelAccessor.getBlockState(blockPos2);
                if (blockState.getBlock() instanceof SculkBehaviour || grass && blockState.is(List.of(Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT, Blocks.SAND))) {
                    return true;
                }
                if (blockState.isAir()) break block4;
                if (!blockState.is(Blocks.WATER) || !blockState.getFluidState().isSource()) break block5;
            }
            return Direction.stream().map(blockPos2::relative).anyMatch(blockPos -> levelAccessor.getBlockState((BlockPos)blockPos).isCollisionShapeFullBlock(levelAccessor, (BlockPos)blockPos));
        }
        return false;
    }
}
