package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TryFindWater
extends Task<Mob> {
    private final int range;
    private final float speedModifier;
    private long nextOkStartTime;

    public TryFindWater(int n, float f) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET,MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET,MemoryModuleStatus.REGISTERED));
        this.range = n;
        this.speedModifier = f;
    }

    @Override
    protected void stop(ServerWorld serverLevel, Mob pathfinderMob, long l) {
        this.nextOkStartTime = l + 20L + 2L;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverLevel, Mob pathfinderMob) {
        return !pathfinderMob.level.getFluidState(pathfinderMob.blockPosition()).is(FluidTags.WATER);
    }

    @Override
    protected void start(ServerWorld serverLevel, Mob pathfinderMob, long l) {
        if (l < this.nextOkStartTime) {
            return;
        }
        BlockPos blockPos = null;
        BlockPos blockPos2 = null;
        BlockPos blockPos3 = pathfinderMob.blockPosition();
        Iterable<BlockPos> iterable = BlockPos.withinManhattan(blockPos3, this.range, this.range, this.range);
        for (BlockPos blockPos4 : iterable) {
            if (blockPos4.getX() == blockPos3.getX() && blockPos4.getZ() == blockPos3.getZ()) continue;
            BlockState blockState = pathfinderMob.level.getBlockState(blockPos4.above());
            BlockState blockState2 = pathfinderMob.level.getBlockState(blockPos4);
            if (!blockState2.is(Blocks.WATER)) continue;
            if (blockState.isAir()) {
                blockPos = blockPos4.immutable();
                break;
            }
            if (blockPos2 != null || blockPos4.closerThan(pathfinderMob.position(), 1.5)) continue;
            blockPos2 = blockPos4.immutable();
        }
        if (blockPos == null) {
            blockPos = blockPos2;
        }
        if (blockPos != null) {
            this.nextOkStartTime = l + 40L;
            BrainUtil.setWalkAndLookTargetMemories((LivingEntity)pathfinderMob, blockPos, this.speedModifier, 0);
        }
    }
}

