
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.Mob;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.UniformInt;
import net.minecraft.world.server.ServerWorld;


public class LongJumpMidJump
extends Task<Mob> {
    public static final int TIME_OUT_DURATION = 100;
    private final UniformInt timeBetweenLongJumps;
    private final SoundEvent landingSound;

    public LongJumpMidJump(UniformInt uniformInt, SoundEvent soundEvent) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleStatus.VALUE_PRESENT), 100);
        this.timeBetweenLongJumps = uniformInt;
        this.landingSound = soundEvent;
    }

    @Override
    protected boolean canStillUse(ServerWorld serverLevel, Mob mob, long l) {
        return !mob.isOnGround();
    }

    @Override
    protected void start(ServerWorld serverLevel, Mob mob, long l) {
        mob.setDiscardFriction(true);
        mob.setPose(Pose.LONG_JUMPING);
    }

    @Override
    protected void stop(ServerWorld serverLevel, Mob mob, long l) {
        if (mob.isOnGround()) {
            mob.setDeltaMovement(mob.getDeltaMovement().multiply(0.1f, 1.0, 0.1f));
            serverLevel.playSound(null, mob, this.landingSound, SoundCategory.NEUTRAL, 2.0f, 1.0f);
        }
        mob.setDiscardFriction(false);
        mob.setPose(Pose.STANDING);
        mob.getBrain().eraseMemory(MemoryModuleType.LONG_JUMP_MID_JUMP);
        mob.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, this.timeBetweenLongJumps.sample(serverLevel.random));
    }
}

