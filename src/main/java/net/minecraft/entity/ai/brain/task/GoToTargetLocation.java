package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.MemStatus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class GoToTargetLocation extends Task<Mob> {
    private final MemoryModuleType<BlockPos> memoryModuleType;
    private final int dis;
    private final float speed;
    private static BlockPos getNearbyPos(Mob mob, BlockPos pos) {
        Random random = mob.getRandom();
        return pos.offset(getRandomOffset(random), 0, getRandomOffset(random));
    }

    private static int getRandomOffset(Random randomSource) {
        return randomSource.nextInt(3) - 1;
    }

    public GoToTargetLocation(MemoryModuleType<BlockPos> memoryModuleType, int n, float f) {
        super(ImmutableMap.of(memoryModuleType, MemStatus.PRESENT, MemoryModuleType.ATTACK_TARGET, MemStatus.ABSENT, MemoryModuleType.WALK_TARGET, MemStatus.ABSENT, MemoryModuleType.LOOK_TARGET, MemStatus.REGISTERED));
        this.memoryModuleType = memoryModuleType;
        this.dis = n;
        this.speed = f;
    }

    @Override
    protected void start(ServerWorld world, Mob mob, long time) {
        BlockPos pos = mob.getBrain().getMemory(memoryModuleType).orElse(null);
        if (pos == null) return;
        boolean bl = pos.closerThan(mob.blockPosition(), dis);
        if (!bl) {
            BrainUtil.setWalkAndLookTargetMemories(mob, getNearbyPos(mob, pos), speed, dis);
        }
    }
}
