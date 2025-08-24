package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;

import java.util.function.Function;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;


public class SetWalkTargetFromAttackTargetIfTargetOutOfReach
extends Task<Mob> {
    private static final int PROJECTILE_ATTACK_RANGE_BUFFER = 1;
    private final Function<LivingEntity, Float> speedModifier;

    public SetWalkTargetFromAttackTargetIfTargetOutOfReach(float f) {
        this((LivingEntity livingEntity) -> f);
    }

    public SetWalkTargetFromAttackTargetIfTargetOutOfReach(Function<LivingEntity, Float> function) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET,  MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET,MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.REGISTERED));
        this.speedModifier = function;
    }

    @Override
    protected void start(ServerWorld serverLevel, Mob mob, long l) {
        LivingEntity livingEntity = mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        if (BrainUtil.canSee(mob, livingEntity) && BrainUtil.isWithinAttackRange(mob, livingEntity, 1)) {
            this.clearWalkTarget(mob);
        } else {
            this.setWalkAndLookTarget(mob, livingEntity);
        }
    }

    private void setWalkAndLookTarget(LivingEntity livingEntity, LivingEntity livingEntity2) {
        Brain<?> brain = livingEntity.getBrain();
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(livingEntity2, true));
        WalkTarget walkTarget = new WalkTarget(new EntityPosWrapper(livingEntity2, false), this.speedModifier.apply(livingEntity), 0);
        brain.setMemory(MemoryModuleType.WALK_TARGET, walkTarget);
    }

    private void clearWalkTarget(LivingEntity livingEntity) {
        livingEntity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }
}

