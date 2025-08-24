package net.minecraft.block.sculk;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class SculkVeinBlock extends MultifaceSpreadeableBlock implements SculkBehaviour {
    private final MultifaceSpreader veinSpreader = new MultifaceSpreader(new SculkVeinSpreaderConfig(MultifaceSpreader.DEFAULT_SPREAD_ORDER));
    private final MultifaceSpreader sameSpaceSpreader = new MultifaceSpreader(new SculkVeinSpreaderConfig(MultifaceSpreader.SpreadType.SAME_POSITION));

    @Override
    public MultifaceSpreader getSpreader() {
        return this.veinSpreader;
    }

    public MultifaceSpreader getSameSpaceSpreader() {
        return this.sameSpaceSpreader;
    }


    public SculkVeinBlock() {
        super(AbstractBlock.Properties.of(Material.SCULK).noCollission().strength(0.2f).sound(SoundType.SCULK_VEIN));
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
        return PushReaction.DESTROY;
    }


    class SculkVeinSpreaderConfig
            extends MultifaceSpreader.DefaultSpreaderConfig {
        private final MultifaceSpreader.SpreadType[] spreadTypes;

        public SculkVeinSpreaderConfig(MultifaceSpreader.SpreadType ... spreadTypeArray) {
            super(SculkVeinBlock.this);
            this.spreadTypes = spreadTypeArray;
        }

        @Override
        public boolean stateCanBeReplaced(ISeedReader blockGetter, BlockPos blockPos, BlockPos blockPos2, Direction direction, BlockState blockState) {
            Object object;
            BlockState blockState2 = blockGetter.getBlockState(blockPos2.relative(direction));
            if (blockState2.is(Blocks.SCULK) || blockState2.is(Blocks.SCULK_CATALYST) || blockState2.is(Blocks.MOVING_PISTON)) {
                return false;
            }
            if (blockPos.distManhattan(blockPos2) == 2 && blockGetter.getBlockState((BlockPos)(object = blockPos.relative(direction.getOpposite()))).isFaceSturdy(blockGetter, (BlockPos)object, direction)) {
                return false;
            }
            object = blockState.getFluidState();
            if (!((FluidState)object).isEmpty() && !((FluidState)object).is(FluidTags.WATER)) {
                return false;
            }
            Material material = blockState.getMaterial();
            if (material == Material.FIRE) {
                return false;
            }
            return blockState.canBeReplaced() || super.stateCanBeReplaced(blockGetter, blockPos, blockPos2, direction, blockState);
        }

        @Override
        public MultifaceSpreader.SpreadType[] getSpreadTypes() {
            return this.spreadTypes;
        }

        @Override
        public boolean isOtherBlockValidAsSource(BlockState blockState) {
            return !blockState.is(Blocks.SCULK_VEIN);
        }
    }

    public static boolean regrow(ISeedReader levelAccessor, BlockPos blockPos, BlockState blockState, Collection<Direction> collection) {
        boolean bl = false;
        BlockState blockState2 = Blocks.SCULK_VEIN.defaultBlockState();
        for (Direction direction : collection) {
            BlockPos blockPos2;
            if (!SculkVeinBlock.canAttachTo(levelAccessor, direction, blockPos2 = blockPos.relative(direction), levelAccessor.getBlockState(blockPos2))) continue;
            blockState2 = (BlockState)blockState2.setValue(SculkVeinBlock.getFaceProperty(direction), true);
            bl = true;
        }
        if (!bl) {
            return false;
        }
        if (!blockState.getFluidState().isEmpty()) {
            blockState2 = (BlockState)blockState2.setValue(WATERLOGGED, true);
        }
        levelAccessor.setBlock(blockPos, blockState2, 3);
        return true;
    }

    @Override
    public void onDischarged(ISeedReader levelAccessor, BlockState blockState, BlockPos blockPos, Random randomSource) {
        if (!blockState.is(this)) {
            return;
        }
        for (Direction direction : DIRECTIONS) {
            BooleanProperty booleanProperty = SculkVeinBlock.getFaceProperty(direction);
            if (!blockState.getValue(booleanProperty).booleanValue() || !levelAccessor.getBlockState(blockPos.relative(direction)).is(Blocks.SCULK)) continue;
            blockState = (BlockState)blockState.setValue(booleanProperty, false);
        }
        if (!SculkVeinBlock.hasAnyFace(blockState)) {
            FluidState fluidState = levelAccessor.getFluidState(blockPos);
            blockState = (fluidState.isEmpty() ? Blocks.AIR : Blocks.WATER).defaultBlockState();
        }
        levelAccessor.setBlock(blockPos, blockState, 3);
        SculkBehaviour.super.onDischarged(levelAccessor, blockState, blockPos, randomSource);
    }

    @Override
    public int attemptUseCharge(SculkSpreader.ChargeCursor chargeCursor, ISeedReader levelAccessor, BlockPos blockPos, Random randomSource, SculkSpreader sculkSpreader, boolean bl) {
        if (bl && this.attemptPlaceSculk(sculkSpreader, levelAccessor, chargeCursor.getPos(), randomSource)) {
            return chargeCursor.getCharge() - 1;
        }
        return randomSource.nextInt(sculkSpreader.chargeDecayRate()) == 0 ? MathHelper.floor((float)chargeCursor.getCharge() * 0.5f) : chargeCursor.getCharge();
    }

    private boolean attemptPlaceSculk(SculkSpreader sculkSpreader, ISeedReader levelAccessor, BlockPos blockPos, Random randomSource) {
        BlockState blockState = levelAccessor.getBlockState(blockPos);
        List<Block> tagKey = sculkSpreader.replaceableBlocks();
        for (Direction direction : Direction.allShuffled(randomSource)) {
            BlockPos blockPos2;
            BlockState blockState2;
            if (!SculkVeinBlock.hasFace(blockState, direction) || !(blockState2 = levelAccessor.getBlockState(blockPos2 = blockPos.relative(direction))).is(tagKey)) continue;
            BlockState blockState3 = Blocks.SCULK.defaultBlockState();
            levelAccessor.setBlock(blockPos2, blockState3, 3);
//            if (levelAccessor instanceof ServerWorld) {
//                Block.pushEntitiesUp(blockState2, blockState3, (World) levelAccessor, blockPos2);
//            }
            levelAccessor.playSound(null, blockPos2, SoundEvents.SCULK_BLOCK_SPREAD, SoundCategory.BLOCKS, 1.0f, 1.0f);
            this.veinSpreader.spreadAll(blockState3, levelAccessor, blockPos2, sculkSpreader.isWorldGeneration());
            Direction direction2 = direction.getOpposite();
            for (Direction direction3 : DIRECTIONS) {
                BlockPos blockPos3;
                BlockState blockState4;
                if (direction3 == direction2 || !(blockState4 = levelAccessor.getBlockState(blockPos3 = blockPos2.relative(direction3))).is(this)) continue;
                this.onDischarged(levelAccessor, blockState4, blockPos3, randomSource);
            }
            return true;
        }
        return false;
    }

    public static boolean hasSubstrateAccess(ISeedReader levelAccessor, BlockState blockState, BlockPos blockPos) {
        if (!blockState.is(Blocks.SCULK_VEIN)) {
            return false;
        }
        for (Direction direction : DIRECTIONS) {
            if (!SculkVeinBlock.hasFace(blockState, direction) || !levelAccessor.getBlockState(blockPos.relative(direction)).is(SculkSpreader.SCULK_REPLACEABLE)) continue;
            return true;
        }
        return false;
    }
}
