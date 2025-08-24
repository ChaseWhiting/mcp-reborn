package net.minecraft.entity.allay;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class StayCloseToTarget extends Task<AllayEntity> {
    private final Function<LivingEntity, Optional<IPosWrapper>> function;
    private final int n;
    private final int n2;
    private final float f;
    public StayCloseToTarget(Function<LivingEntity, Optional<IPosWrapper>> function, int n, int n2, float f) {
        super(Map.of(MemoryModuleType.LOOK_TARGET, MemStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemStatus.ABSENT));
        this.function = function;
        this.n = n;this.n2 = n2;this.f = f;
    }

    @Override
    protected void start(ServerWorld world, AllayEntity allay, long time) {
        Optional<IPosWrapper> optional = function.apply(allay);
        if (optional.isEmpty()) return;
        IPosWrapper iPosWrapper = optional.get();
        if (allay.position().closerThan(iPosWrapper.currentPosition(), n2)){
            return;
        }
        IPosWrapper iPosWrapper1 = optional.get();
        allay.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, iPosWrapper1);
        allay.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(iPosWrapper1, f, n));
    }
}
