package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.TargetingConditions;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;


public class PrepareRamNearestTarget<E extends Mob>
extends Task<E> {
    public static final int TIME_OUT_DURATION = 160;
    private final ToIntFunction<E> getCooldownOnFail;
    private final int minRamDistance;
    private final int maxRamDistance;
    private final float walkSpeed;
    private final TargetingConditions ramTargeting;
    private final int ramPrepareTime;
    private final Function<E, SoundEvent> getPrepareRamSound;
    private Optional<Long> reachedRamPositionTimestamp = Optional.empty();
    private Optional<RamCandidate> ramCandidate = Optional.empty();

    public PrepareRamNearestTarget(ToIntFunction<E> toIntFunction, int n, int n2, float f, TargetingConditions targetingConditions, int n3, Function<E, SoundEvent> function) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES,MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.RAM_TARGET, MemoryModuleStatus.VALUE_ABSENT), 160);
        this.getCooldownOnFail = toIntFunction;
        this.minRamDistance = n;
        this.maxRamDistance = n2;
        this.walkSpeed = f;
        this.ramTargeting = targetingConditions;
        this.ramPrepareTime = n3;
        this.getPrepareRamSound = function;
    }

    @Override
    protected void start(ServerWorld serverLevel, Mob pathfinderMob, long l) {
        Brain<?> brain = pathfinderMob.getBrain();


        brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES)
                .ifPresent(visibleEntities -> {
                    Optional<LivingEntity> closestEntity = visibleEntities.stream()
                            .filter(Objects::nonNull) // Ensure it's a LivingEntity
                            .map(entity -> (LivingEntity) entity) // Cast safely
                            .filter(livingEntity -> this.ramTargeting.test(pathfinderMob, livingEntity)) // Apply targeting conditions
                            .min(Comparator.comparingDouble(pathfinderMob::distanceTo)); // Find closest entity

                    closestEntity.ifPresent(livingEntity -> this.chooseRamPosition(pathfinderMob, livingEntity));
                });
    }

    @Override
    protected void stop(ServerWorld serverLevel, E e, long l) {
        Brain<?> brain = ((LivingEntity)e).getBrain();
        if (!brain.hasMemoryValue(MemoryModuleType.RAM_TARGET)) {
            serverLevel.broadcastEntityEvent((Entity)e, (byte)59);
            brain.setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, this.getCooldownOnFail.applyAsInt(e));
        }
    }

    @Override
    protected boolean canStillUse(ServerWorld serverLevel, Mob pathfinderMob, long l) {
        return this.ramCandidate.isPresent() && this.ramCandidate.get().getTarget().isAlive();
    }

    @Override
    protected void tick(ServerWorld serverLevel, E e, long l) {
        boolean bl;
        if (this.ramCandidate.isEmpty()) {
            return;
        }
        ((LivingEntity)e).getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.ramCandidate.get().getStartPosition(), this.walkSpeed, 0));
        ((LivingEntity)e).getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(this.ramCandidate.get().getTarget(), true));
        boolean bl2 = bl = !this.ramCandidate.get().getTarget().blockPosition().equals(this.ramCandidate.get().getTargetPosition());
        if (bl) {
            serverLevel.broadcastEntityEvent((Entity)e, (byte)59);
            ((Mob)e).getNavigation().stop();
            this.chooseRamPosition((Mob)e, this.ramCandidate.get().target);
        } else {
            BlockPos blockPos = ((Entity)e).blockPosition();
            if (blockPos.equals(this.ramCandidate.get().getStartPosition())) {
                serverLevel.broadcastEntityEvent((Entity)e, (byte)58);
                if (this.reachedRamPositionTimestamp.isEmpty()) {
                    this.reachedRamPositionTimestamp = Optional.of(l);
                }
                if (l - this.reachedRamPositionTimestamp.get() >= (long)this.ramPrepareTime) {
                    ((LivingEntity)e).getBrain().setMemory(MemoryModuleType.RAM_TARGET, this.getEdgeOfBlock(blockPos, this.ramCandidate.get().getTargetPosition()));
                    serverLevel.playSound(null, (Entity)e, this.getPrepareRamSound.apply(e), SoundCategory.NEUTRAL, 1.0f, ((LivingEntity)e).getVoicedPitch());
                    this.ramCandidate = Optional.empty();
                }
            }
        }
    }

    private Vector3d getEdgeOfBlock(BlockPos blockPos, BlockPos blockPos2) {
        double d = 0.5;
        double d2 = 0.5 * (double)MathHelper.sign(blockPos2.getX() - blockPos.getX());
        double d3 = 0.5 * (double) MathHelper.sign(blockPos2.getZ() - blockPos.getZ());
        return Vector3d.atBottomCenterOf(blockPos2).add(d2, 0.0, d3);
    }

    private Optional<BlockPos> calculateRammingStartPosition(Mob pathfinderMob, LivingEntity livingEntity) {
        BlockPos blockPos2 = livingEntity.blockPosition();
        if (!this.isWalkableBlock(pathfinderMob, blockPos2)) {
            return Optional.empty();
        }
        ArrayList arrayList = Lists.newArrayList();
        BlockPos.Mutable mutableBlockPos = blockPos2.mutable();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            mutableBlockPos.set(blockPos2);
            for (int i = 0; i < this.maxRamDistance; ++i) {
                if (this.isWalkableBlock(pathfinderMob, mutableBlockPos.move(direction))) continue;
                mutableBlockPos.move(direction.getOpposite());
                break;
            }
            if (mutableBlockPos.distManhattan(blockPos2) < this.minRamDistance) continue;
            arrayList.add(mutableBlockPos.immutable());
        }
        PathNavigator pathNavigation = pathfinderMob.getNavigation();
        return arrayList.stream().sorted(Comparator.comparingDouble(pathfinderMob.blockPosition()::distanceSqr)).filter(blockPos -> {
            Path path = pathNavigation.createPath((BlockPos)blockPos, 0);
            return path != null && path.canReach();
        }).findFirst();
    }

    private boolean isWalkableBlock(Mob pathfinderMob, BlockPos blockPos) {
        return pathfinderMob.getNavigation().isStableDestination(blockPos) && pathfinderMob.getPathfindingMalus(WalkNodeProcessor.getBlockPathTypeStatic(pathfinderMob.level, blockPos.mutable())) == 0.0f;
    }

    private void chooseRamPosition(Mob pathfinderMob, LivingEntity livingEntity) {
        this.reachedRamPositionTimestamp = Optional.empty();
        this.ramCandidate = this.calculateRammingStartPosition(pathfinderMob, livingEntity).map(blockPos -> new RamCandidate((BlockPos)blockPos, livingEntity.blockPosition(), livingEntity));
    }

    public static class RamCandidate {
        private final BlockPos startPosition;
        private final BlockPos targetPosition;
        final LivingEntity target;

        public RamCandidate(BlockPos blockPos, BlockPos blockPos2, LivingEntity livingEntity) {
            this.startPosition = blockPos;
            this.targetPosition = blockPos2;
            this.target = livingEntity;
        }

        public BlockPos getStartPosition() {
            return this.startPosition;
        }

        public BlockPos getTargetPosition() {
            return this.targetPosition;
        }

        public LivingEntity getTarget() {
            return this.target;
        }
    }
}

