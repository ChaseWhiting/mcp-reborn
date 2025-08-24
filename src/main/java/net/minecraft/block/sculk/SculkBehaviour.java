package net.minecraft.block.sculk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MultifaceSpreadeableBlock;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

public interface SculkBehaviour {
    public static final SculkBehaviour DEFAULT = new SculkBehaviour(){

        @Override
        public boolean attemptSpreadVein(ISeedReader levelAccessor, BlockPos blockPos, BlockState blockState, @Nullable Collection<Direction> collection, boolean bl) {
            if (collection == null) {
                return ((SculkVeinBlock) Blocks.SCULK_VEIN).getSameSpaceSpreader().spreadAll(levelAccessor.getBlockState(blockPos), levelAccessor, blockPos, bl) > 0L;
            }
            if (!collection.isEmpty()) {
                if (blockState.isAir() || blockState.getFluidState().is(FluidTags.WATER)) {
                    return SculkVeinBlock.regrow(levelAccessor, blockPos, blockState, collection);
                }
                return false;
            }
            return SculkBehaviour.super.attemptSpreadVein(levelAccessor, blockPos, blockState, collection, bl);
        }

        @Override
        public int attemptUseCharge(SculkSpreader.ChargeCursor chargeCursor, ISeedReader levelAccessor, BlockPos blockPos, Random randomSource, SculkSpreader sculkSpreader, boolean bl) {
            return chargeCursor.getDecayDelay() > 0 ? chargeCursor.getCharge() : 0;
        }

        @Override
        public int updateDecayDelay(int n) {
            return Math.max(n - 1, 0);
        }
    };

    default public byte getSculkSpreadDelay() {
        return 1;
    }

    default public void onDischarged(ISeedReader levelAccessor, BlockState blockState, BlockPos blockPos, Random randomSource) {
    }

    default public boolean depositCharge(ISeedReader levelAccessor, BlockPos blockPos, Random randomSource) {
        return false;
    }

    default public boolean attemptSpreadVein(ISeedReader levelAccessor, BlockPos blockPos, BlockState blockState, @Nullable Collection<Direction> collection, boolean bl) {
        return ((MultifaceSpreadeableBlock)Blocks.SCULK_VEIN).getSpreader().spreadAll(blockState, levelAccessor, blockPos, bl) > 0L;
    }

    default public boolean canChangeBlockStateOnSpread() {
        return true;
    }

    default public int updateDecayDelay(int n) {
        return 1;
    }

    public int attemptUseCharge(SculkSpreader.ChargeCursor var1, ISeedReader var2, BlockPos var3, Random var4, SculkSpreader var5, boolean var6);
}

