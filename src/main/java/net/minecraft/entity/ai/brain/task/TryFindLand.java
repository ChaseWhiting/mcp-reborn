package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TryFindLand extends Task<Mob> {
    private final int range;
    private final float speedModifier;
    private long nextOkStartTime;

    public TryFindLand(int range, float speedModifier) {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT,
                MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED
        ));
        this.range = range;
        this.speedModifier = speedModifier;
    }

    @Override
    protected void stop(ServerWorld world, Mob entity, long time) {
        this.nextOkStartTime = time + 20L + 2L;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld world, Mob entity) {
        // Run only if the entity is currently in water
        return entity.level.getFluidState(entity.blockPosition()).is(FluidTags.WATER);
    }

    @Override
    protected void start(ServerWorld world, Mob entity, long time) {
        if (time < this.nextOkStartTime) {
            return;
        }

        BlockPos entityPos = entity.blockPosition();
        BlockPos targetPos = null;
        Iterable<BlockPos> positions = BlockPos.withinManhattan(entityPos, this.range, this.range, this.range);

        for (BlockPos pos : positions) {
            if (pos.getX() == entityPos.getX() && pos.getZ() == entityPos.getZ()) continue; // Skip the current position

            BlockPos belowPos = pos.below();
            BlockState blockAbove = world.getBlockState(pos);
            BlockState blockBelow = world.getBlockState(belowPos);

            // Check if it's land: The block itself must be air & the block below must be solid
            if (blockAbove.isAir() && blockBelow.isFaceSturdy(world, belowPos, net.minecraft.util.Direction.UP)) {
                targetPos = pos.immutable();
                break;
            }
        }

        if (targetPos != null) {
            this.nextOkStartTime = time + 40L;
            BrainUtil.setWalkAndLookTargetMemories(entity, targetPos, this.speedModifier, 0);
        }
    }
}
