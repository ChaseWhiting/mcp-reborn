package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Creature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import javax.annotation.Nullable;

public class RandomStroll
extends Task<Creature> {
    private static final int MAX_XZ_DIST = 10;
    private static final int MAX_Y_DIST = 7;
    private final float speedModifier;
    protected final int maxHorizontalDistance;
    protected final int maxVerticalDistance;

    public RandomStroll(float f) {
        this(f, 10, 7);
    }

    public RandomStroll(float f, int n, int n2) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
        this.speedModifier = f;
        this.maxHorizontalDistance = n;
        this.maxVerticalDistance = n2;
    }

    @Override
    protected void start(ServerWorld serverLevel, Creature pathfinderMob, long l) {
        Optional<Vector3d> optional = Optional.ofNullable(this.getTargetPos(pathfinderMob));
        pathfinderMob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map(vec3 -> new WalkTarget((Vector3d) vec3, this.speedModifier, 0)));
    }

    @Nullable
    protected Vector3d getTargetPos(Creature pathfinderMob) {
        return RandomPositionGenerator.getLandPos(pathfinderMob, this.maxHorizontalDistance, this.maxVerticalDistance);
    }
}

