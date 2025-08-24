package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.Creature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class AnimalPanic<E extends Creature>
extends Task<E> {
    private static final int PANIC_MIN_DURATION = 100;
    private static final int PANIC_MAX_DURATION = 120;
    private static final int PANIC_DISTANCE_HORIZONTAL = 5;
    private static final int PANIC_DISTANCE_VERTICAL = 4;
    private final float speedMultiplier;
    private final Function<E, Vector3d> positionGetter;

    public AnimalPanic(float f, int n) {
        this(f, pathfinderMob -> AirAndWaterRandomPos.getPos(pathfinderMob, 5, 4, n, pathfinderMob.getViewVector((float)0.0f).x, pathfinderMob.getViewVector((float)0.0f).z, 1.5707963705062866));
    }

    public AnimalPanic(float f) {
        this(f, pathfinderMob -> RandomPositionGenerator.getPos(pathfinderMob, 5, 4));
    }

    public AnimalPanic(float f, Function<E, Vector3d> function2) {
        super(Map.of(MemoryModuleType.IS_PANICKING, MemoryModuleStatus.REGISTERED, MemoryModuleType.HURT_BY, MemoryModuleStatus.REGISTERED), 100, 120);
        this.speedMultiplier = f;
        this.positionGetter = function2;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverLevel, E e) {
        return ((LivingEntity)e).getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING);
    }

    @Override
    protected boolean canStillUse(ServerWorld serverLevel, E e, long l) {
        return true;
    }

    @Override
    protected void start(ServerWorld serverLevel, E e, long l) {
        ((LivingEntity)e).getBrain().setMemory(MemoryModuleType.IS_PANICKING, true);
        ((LivingEntity)e).getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        ((Mob)e).getNavigation().stop();
    }

    @Override
    protected void stop(ServerWorld serverLevel, E e, long l) {
        Brain<?> brain = ((LivingEntity)e).getBrain();
        brain.eraseMemory(MemoryModuleType.IS_PANICKING);
    }

    @Override
    protected void tick(ServerWorld serverLevel, E e, long l) {
        Vector3d vec3;
        if (((Mob)e).getNavigation().isDone() && (vec3 = this.getPanicPos(e, serverLevel)) != null) {
            ((LivingEntity)e).getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, this.speedMultiplier, 0));
        }
    }

    @Nullable
    private Vector3d getPanicPos(E e, ServerWorld serverLevel) {
        Optional<Vector3d> optional;
        if (((Entity)e).isOnFire() && (optional = this.lookForWater(serverLevel, (Entity)e).map(Vector3d::atBottomCenterOf)).isPresent()) {
            return optional.get();
        }
        return this.positionGetter.apply(e);
    }

    private Optional<BlockPos> lookForWater(IBlockReader blockGetter, Entity entity) {
        BlockPos blockPos3 = entity.blockPosition();
        if (!blockGetter.getBlockState(blockPos3).getCollisionShape(blockGetter, blockPos3).isEmpty()) {
            return Optional.empty();
        }
        Predicate<BlockPos> predicate = MathHelper.ceil(entity.getBbWidth()) == 2 ? blockPos2 -> BlockPos.squareOutSouthEast(blockPos2).allMatch(blockPos -> blockGetter.getFluidState((BlockPos)blockPos).is(FluidTags.WATER)) : blockPos -> blockGetter.getFluidState((BlockPos)blockPos).is(FluidTags.WATER);
        return BlockPos.findClosestMatch(blockPos3, 5, 1, predicate);
    }
}

