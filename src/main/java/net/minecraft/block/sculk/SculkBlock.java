package net.minecraft.block.sculk;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;

import java.util.Random;

public class SculkBlock extends DropExperienceBlock implements SculkBehaviour {

    public SculkBlock() {
        super(AbstractBlock.Properties.of(Material.SCULK).strength(0.2f).sound(SoundType.SCULK), 1);
    }

    @Override
    public int attemptUseCharge(SculkSpreader.ChargeCursor chargeCursor, ISeedReader levelAccessor, BlockPos blockPos, Random randomSource, SculkSpreader sculkSpreader, boolean bl) {
        int n = chargeCursor.getCharge();
        if (n == 0 || randomSource.nextInt(sculkSpreader.chargeDecayRate()) != 0) {
            return n;
        }
        BlockPos blockPos2 = chargeCursor.getPos();
        boolean bl2 = blockPos2.closerThan(blockPos, sculkSpreader.noGrowthRadius());
        if (bl2 || !SculkBlock.canPlaceGrowth(levelAccessor, blockPos2)) {
            if (randomSource.nextInt(sculkSpreader.additionalDecayRate()) != 0) {
                return n;
            }
            return n - (bl2 ? 1 : SculkBlock.getDecayPenalty(sculkSpreader, blockPos2, blockPos, n));
        }
        int n2 = sculkSpreader.growthSpawnCost();
        if (randomSource.nextInt(n2) < n) {
            BlockPos blockPos3 = blockPos2.above();
            BlockState blockState = this.getRandomGrowthState(levelAccessor, blockPos3, randomSource, sculkSpreader.isWorldGeneration());
            levelAccessor.setBlock(blockPos3, blockState, 3);
            levelAccessor.playSound(null, blockPos2, blockState.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return Math.max(0, n - n2);
    }

    private static int getDecayPenalty(SculkSpreader sculkSpreader, BlockPos blockPos, BlockPos blockPos2, int n) {
        int n2 = sculkSpreader.noGrowthRadius();
        float f = MathHelper.square((float)Math.sqrt(blockPos.distSqr(blockPos2)) - (float)n2);
        int n3 = MathHelper.square(24 - n2);
        float f2 = Math.min(1.0f, f / (float)n3);
        return Math.max(1, (int)((float)n * f2 * 0.5f));
    }

    private BlockState getRandomGrowthState(ISeedReader levelAccessor, BlockPos blockPos, Random randomSource, boolean bl) {
        BlockState blockState = randomSource.nextInt(11) == 0 ? (BlockState)Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, bl) : Blocks.SCULK_SENSOR.defaultBlockState();
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && !levelAccessor.getFluidState(blockPos).isEmpty()) {
            return (BlockState)blockState.setValue(BlockStateProperties.WATERLOGGED, true);
        }
        return blockState;
    }

    private static boolean canPlaceGrowth(ISeedReader levelAccessor, BlockPos blockPos) {
        BlockState blockState = levelAccessor.getBlockState(blockPos.above());
        if (!(blockState.isAir() || blockState.is(Blocks.WATER) && blockState.getFluidState().is(FluidTags.WATER))) {
            return false;
        }
        int n = 0;
        for (BlockPos blockPos2 : BlockPos.betweenClosed(blockPos.offset(-4, 0, -4), blockPos.offset(4, 2, 4))) {
            BlockState blockState2 = levelAccessor.getBlockState(blockPos2);
            if (blockState2.is(Blocks.SCULK_SENSOR) || blockState2.is(Blocks.SCULK_SHRIEKER)) {
                ++n;
            }
            if (n <= 2) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean canChangeBlockStateOnSpread() {
        return false;
    }
}
