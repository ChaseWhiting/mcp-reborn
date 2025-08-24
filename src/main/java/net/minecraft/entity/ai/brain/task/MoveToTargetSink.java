package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Creature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import javax.annotation.Nullable;

public class MoveToTargetSink
extends Task<Creature> {
    private static final int MAX_COOLDOWN_BEFORE_RETRYING = 40;
    private int remainingCooldown;
    @Nullable
    private Path path;
    @Nullable
    private BlockPos lastTargetPos;
    private float speedModifier;

    public MoveToTargetSink() {
        this(150, 250);
    }

    public MoveToTargetSink(int n, int n2) {
        super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleStatus.REGISTERED, MemoryModuleType.PATH, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_PRESENT), n, n2);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverLevel, Creature mob) {
        if (this.remainingCooldown > 0) {
            --this.remainingCooldown;
            return false;
        }
        Brain<?> brain = mob.getBrain();
        WalkTarget walkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
        boolean bl = this.reachedTarget(mob, walkTarget);
        if (!bl && this.tryComputePath(mob, walkTarget, serverLevel.getGameTime())) {
            this.lastTargetPos = walkTarget.getTarget().currentBlockPosition();
            return true;
        }
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        if (bl) {
            brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
        return false;
    }

    @Override
    protected boolean canStillUse(ServerWorld serverLevel, Creature mob, long l) {
        if (this.path == null || this.lastTargetPos == null) {
            return false;
        }
        Optional<WalkTarget> optional = mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
        PathNavigator pathNavigation = mob.getNavigation();
        return !pathNavigation.isDone() && optional.isPresent() && !this.reachedTarget(mob, optional.get());
    }

    @Override
    protected void stop(ServerWorld serverLevel, Creature mob, long l) {
        if (mob.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET) && !this.reachedTarget(mob, mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get()) && mob.getNavigation().isStuck()) {
            this.remainingCooldown = serverLevel.getRandom().nextInt(40);
        }
        mob.getNavigation().stop();
        mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        mob.getBrain().eraseMemory(MemoryModuleType.PATH);
        this.path = null;
    }

    @Override
    protected void start(ServerWorld serverLevel, Creature mob, long l) {
        mob.getBrain().setMemory(MemoryModuleType.PATH, this.path);
        mob.getNavigation().moveTo(this.path, (double)this.speedModifier);
    }

    @Override
    protected void tick(ServerWorld serverLevel, Creature mob, long l) {
        Path path = mob.getNavigation().getPath();
        Brain<?> brain = mob.getBrain();
        if (this.path != path) {
            this.path = path;
            brain.setMemory(MemoryModuleType.PATH, path);
        }
        if (path == null || this.lastTargetPos == null) {
            return;
        }
        WalkTarget walkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
        if (walkTarget.getTarget().currentBlockPosition().distSqr(this.lastTargetPos) > 4.0 && this.tryComputePath(mob, walkTarget, serverLevel.getGameTime())) {
            this.lastTargetPos = walkTarget.getTarget().currentBlockPosition();
            this.start(serverLevel, mob, l);
        }
    }

    private boolean tryComputePath(Creature mob, WalkTarget walkTarget, long l) {
        BlockPos blockPos = walkTarget.getTarget().currentBlockPosition();
        this.path = mob.getNavigation().createPath(blockPos, 0);
        this.speedModifier = walkTarget.getSpeedModifier();
        Brain<?> brain = mob.getBrain();
        if (this.reachedTarget(mob, walkTarget)) {
            brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        } else {
            boolean bl;
            boolean bl2 = bl = this.path != null && this.path.canReach();
            if (bl) {
                brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            } else if (!brain.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
                brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, l);
            }
            if (this.path != null) {
                return true;
            }
            Vector3d vec3 = RandomPositionGenerator.getPosTowards(mob, 10, 7, Vector3d.atBottomCenterOf(blockPos), 1.5707963705062866);
            if (vec3 != null) {
                this.path = mob.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0);
                return this.path != null;
            }
        }
        return false;
    }

    private boolean reachedTarget(Creature mob, WalkTarget walkTarget) {
        return walkTarget.getTarget().currentBlockPosition().distManhattan(mob.blockPosition()) <= walkTarget.getCloseEnoughDist();
    }
}

