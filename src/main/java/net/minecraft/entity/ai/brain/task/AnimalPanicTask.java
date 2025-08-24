package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Creature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Optional;

public class AnimalPanicTask extends Task<Creature> {
    private static final int PANIC_MIN_DURATION = 100;
    private static final int PANIC_MAX_DURATION = 120;
    private static final int PANIC_DISTANCE_HORIZONTAL = 5;
    private static final int PANIC_DISTANCE_VERTICAL = 4;
    private final float speedMultiplier;

    public AnimalPanicTask(float speed) {
        super(
                ImmutableMap.of(
                        MemoryModuleType.IS_PANICKING, MemoryModuleStatus.REGISTERED,
                        MemoryModuleType.HURT_BY, MemoryModuleStatus.VALUE_PRESENT
                ),
                PANIC_MIN_DURATION,
                PANIC_MAX_DURATION
        );
        this.speedMultiplier = speed;
    }


    @Override
    protected boolean canStillUse(ServerWorld world, Creature animal, long time) {
        return true;
    }

    @Override
    protected void start(ServerWorld world, Creature animal, long time) {
        animal.getBrain().setMemory(MemoryModuleType.IS_PANICKING, true);
        animal.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected void stop(ServerWorld world, Creature animal, long time) {
        Brain<?> brain = animal.getBrain();
        brain.eraseMemory(MemoryModuleType.IS_PANICKING);
    }

    @Override
    protected void tick(ServerWorld world, Creature animal, long time) {
        Vector3d vector3d;
        if (animal.getNavigation().isDone() && (vector3d = this.getPanicPos(animal, world)) != null) {
            animal.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vector3d, this.speedMultiplier, 0));
        }
    }

    @Nullable
    private Vector3d getPanicPos(Creature animal, ServerWorld serverWorld) {
        Optional<Vector3d> opt;
        if (animal.isOnFire() && (opt = this.lookForWater(serverWorld, animal).map(Vector3d::atBottomCenterOf)).isPresent()) {
            return opt.get();
        }
        return RandomPositionGenerator.getLandPos(animal, PANIC_DISTANCE_HORIZONTAL, PANIC_DISTANCE_VERTICAL);
    }

    private Optional<BlockPos> lookForWater(ServerWorld world, Creature entity) {
        BlockPos blockPos2 = entity.blockPosition();
        if (!world.getBlockState(blockPos2).getCollisionShape(world, blockPos2).isEmpty()) {
            return Optional.empty();
        }
        return BlockPos.findClosestMatch(blockPos2, 5, 1, blockPos -> world.getFluidState((BlockPos)blockPos).is(FluidTags.WATER));
    }
}
