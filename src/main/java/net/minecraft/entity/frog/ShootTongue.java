
package net.minecraft.entity.frog;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ShootTongue
extends Task<FrogEntity> {
    public static final int TIME_OUT_DURATION = 100;
    public static final int CATCH_ANIMATION_DURATION = 6;
    public static final int TONGUE_ANIMATION_DURATION = 10;
    private static final float EATING_DISTANCE = 1.75f;
    private static final float EATING_MOVEMENT_FACTOR = 0.75f;
    public static final int UNREACHABLE_TONGUE_TARGETS_COOLDOWN_DURATION = 100;
    public static final int MAX_UNREACHBLE_TONGUE_TARGETS_IN_MEMORY = 5;
    private int eatAnimationTimer;
    private int calculatePathCounter;
    private final SoundEvent tongueSound;
    private final SoundEvent eatSound;
    private Vector3d itemSpawnPos;
    private State state = State.DONE;

    public ShootTongue(SoundEvent soundEvent, SoundEvent soundEvent2) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.IS_PANICKING, MemoryModuleStatus.VALUE_ABSENT), 100);
        this.tongueSound = soundEvent;
        this.eatSound = soundEvent2;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverLevel, FrogEntity frog) {
        LivingEntity livingEntity = frog.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        boolean bl = this.canPathfindToTarget(frog, livingEntity);
        if (!bl) {
            frog.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            this.addUnreachableTargetToMemory(frog, livingEntity);
        }
        return bl && frog.getPose() != Pose.CROAKING && FrogEntity.canEat(livingEntity);
    }

    @Override
    protected boolean canStillUse(ServerWorld serverLevel, FrogEntity frog, long l) {
        return frog.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.state != State.DONE && !frog.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING);
    }

    @Override
    protected void start(ServerWorld serverLevel, FrogEntity frog, long l) {
        LivingEntity livingEntity = frog.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        BrainUtil.lookAtEntity(frog, livingEntity);
        frog.setTongueTarget(livingEntity);
        frog.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.position(), 2.0f, 0));
        this.calculatePathCounter = 10;
        this.state = State.MOVE_TO_TARGET;
    }

    @Override
    protected void stop(ServerWorld serverLevel, FrogEntity frog, long l) {
        frog.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        frog.eraseTongueTarget();
        frog.setPose(Pose.STANDING);
    }

    private void eatEntity(ServerWorld serverLevel, FrogEntity frog) {
        Entity entity;
        serverLevel.playSound(null, frog, this.eatSound, SoundCategory.NEUTRAL, 2.0f, 1.0f);
        Optional<Entity> optional = frog.getTongueTarget();
        if (optional.isPresent() && (entity = optional.get()).isAlive()) {
            frog.doHurtTarget(entity);
            if (!entity.isAlive()) {
                entity.remove();
            }
        }
    }

    @Override
    protected void tick(ServerWorld serverLevel, FrogEntity frog, long l) {
        LivingEntity livingEntity = frog.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        frog.setTongueTarget(livingEntity);
        switch (this.state) {
            case MOVE_TO_TARGET: {
                if (livingEntity.distanceTo(frog) < 1.75f) {
                    serverLevel.playSound(null, frog, this.tongueSound, SoundCategory.NEUTRAL, 2.0f, 1.0f);
                    frog.setPose(Pose.USING_TONGUE);
                    livingEntity.setDeltaMovement(livingEntity.position().vectorTo(frog.position()).normalize().scale(0.75));
                    this.itemSpawnPos = livingEntity.position();
                    this.eatAnimationTimer = 0;
                    this.state = State.CATCH_ANIMATION;
                    break;
                }
                if (this.calculatePathCounter <= 0) {
                    frog.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.position(), 2.0f, 0));
                    this.calculatePathCounter = 10;
                    break;
                }
                --this.calculatePathCounter;
                break;
            }
            case CATCH_ANIMATION: {
                if (this.eatAnimationTimer++ < 6) break;
                this.state = State.EAT_ANIMATION;
                this.eatEntity(serverLevel, frog);
                break;
            }
            case EAT_ANIMATION: {
                if (this.eatAnimationTimer >= 10) {
                    this.state = State.DONE;
                    break;
                }
                ++this.eatAnimationTimer;
                break;
            }
        }
    }

    private boolean canPathfindToTarget(FrogEntity frog, LivingEntity livingEntity) {
        Path path = frog.getNavigation().createPath(livingEntity, 0);
        return path != null && path.getDistToTarget() < 1.75f;
    }

    private void addUnreachableTargetToMemory(FrogEntity frog, LivingEntity livingEntity) {
        boolean bl;
        List list = frog.getBrain().getMemory(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS).orElseGet(ArrayList::new);
        boolean bl2 = bl = !list.contains(livingEntity.getUUID());
        if (list.size() == 5 && bl) {
            list.remove(0);
        }
        if (bl) {
            list.add(livingEntity.getUUID());
        }
        frog.getBrain().setMemoryWithExpiry(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS, list, 100L);
    }

    static enum State {
        MOVE_TO_TARGET,
        CATCH_ANIMATION,
        EAT_ANIMATION,
        DONE;

    }
}

