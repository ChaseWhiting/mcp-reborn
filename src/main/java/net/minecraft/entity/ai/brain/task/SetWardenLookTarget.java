package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class SetWardenLookTarget extends Task<LivingEntity> {

    public SetWardenLookTarget() {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED,
                MemoryModuleType.DISTURBANCE_LOCATION, MemoryModuleStatus.REGISTERED,
                MemoryModuleType.ROAR_TARGET, MemoryModuleStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_ABSENT
        ));
    }

    @Override
    protected void start(ServerWorld serverWorld, LivingEntity livingEntity, long gameTime) {
        Brain<?> brain = livingEntity.getBrain();

        Optional<BlockPos> roarTargetPos = brain.getMemory(MemoryModuleType.ROAR_TARGET)
                .map(LivingEntity::blockPosition);

        Optional<BlockPos> disturbancePos = brain.getMemory(MemoryModuleType.DISTURBANCE_LOCATION);

        Optional<BlockPos> targetPos = roarTargetPos.isPresent() ? roarTargetPos : disturbancePos;

        targetPos.ifPresent(pos -> brain.setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(pos)));
    }
}
