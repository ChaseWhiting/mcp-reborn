package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;

import java.util.Random;

import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.UniformInt;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;


public class RandomLookAround
extends Task<Mob> {
    private final UniformInt interval;
    private final float maxYaw;
    private final float minPitch;
    private final float pitchRange;

    public RandomLookAround(UniformInt intProvider, float f, float f2, float f3) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.GAZE_COOLDOWN_TICKS, MemoryModuleStatus.VALUE_ABSENT));
        if (f2 > f3) {
            throw new IllegalArgumentException("Minimum pitch is larger than maximum pitch! " + f2 + " > " + f3);
        }
        this.interval = intProvider;
        this.maxYaw = f;
        this.minPitch = f2;
        this.pitchRange = f3 - f2;
    }

    @Override
    protected void start(ServerWorld serverLevel, Mob mob, long l) {
        Random randomSource = mob.getRandom();
        float f = MathHelper.clamp(randomSource.nextFloat() * this.pitchRange + this.minPitch, -90.0f, 90.0f);
        float f2 = MathHelper.wrapDegrees(mob.yRot + 2.0f * randomSource.nextFloat() * this.maxYaw - this.maxYaw);
        Vector3d vec3 = Vector3d.directionFromRotation(f, f2);
        mob.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(mob.getEyePosition(0f).add(vec3).asBlockPos()));
        mob.getBrain().setMemory(MemoryModuleType.GAZE_COOLDOWN_TICKS, this.interval.sample(randomSource));
    }
}

