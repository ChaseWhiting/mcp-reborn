package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.server.ServerWorld;

public class TryFindLandNearWater
        extends Task<Mob> {
    private final int range;
    private final float speedModifier;
    private long nextOkStartTime;

    public TryFindLandNearWater(int n, float f) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET,MemoryModuleStatus.REGISTERED));
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
        ISelectionContext collisionContext = ISelectionContext.of(pathfinderMob);
        BlockPos blockPos = pathfinderMob.blockPosition();
        BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
        block0: for (BlockPos blockPos2 : BlockPos.withinManhattan(blockPos, range, range, range)) {
            if (blockPos2.getX() == blockPos.getX() && blockPos2.getZ() == blockPos.getZ() || !serverLevel.getBlockState(blockPos2).getCollisionShape(serverLevel, blockPos2, collisionContext).isEmpty() || serverLevel.getBlockState(mutableBlockPos.setWithOffset(blockPos2, Direction.DOWN)).getCollisionShape(serverLevel, blockPos2, collisionContext).isEmpty()) continue;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                mutableBlockPos.setWithOffset(blockPos2, direction);
                if (!serverLevel.getBlockState(mutableBlockPos).isAir() || !serverLevel.getBlockState(mutableBlockPos.move(Direction.DOWN)).is(Blocks.WATER)) continue;
                BrainUtil.setWalkAndLookTargetMemories((LivingEntity)pathfinderMob, blockPos, this.speedModifier, 0);
                break block0;
            }
        }
    }
}

