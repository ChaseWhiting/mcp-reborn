package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.BlockPosWrapper;


import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class SetEntityLookTargetSometimes extends Task<LivingEntity> {
    private final Predicate<LivingEntity> targetPredicate;
    private final float maxDistanceSquared;
    private final int minInterval;
    private final int maxInterval;
    private int ticksUntilNextStart;

    public SetEntityLookTargetSometimes(EntityType<?> entityType, float maxDistance, int minInterval, int maxInterval) {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT,
                MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.VALUE_PRESENT
        ));

        this.targetPredicate = entity -> entityType.equals(entity.getType());
        this.maxDistanceSquared = maxDistance * maxDistance;
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
        this.ticksUntilNextStart = getRandomInterval(new Random());
    }

    @Override
    protected boolean checkExtraStartConditions(net.minecraft.world.server.ServerWorld world, LivingEntity entity) {
        if (entity.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).isPresent()) {
            return false;
        }

        List<LivingEntity> visibleEntities = world.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(Math.sqrt(maxDistanceSquared)), targetPredicate);
        return !visibleEntities.isEmpty();
    }

    @Override
    protected void start(net.minecraft.world.server.ServerWorld world, LivingEntity entity, long time) {
        if (--ticksUntilNextStart > 0) return; // Only runs at random intervals

        List<LivingEntity> visibleEntities = world.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(Math.sqrt(maxDistanceSquared)), targetPredicate);

        if (!visibleEntities.isEmpty()) {
            LivingEntity target = visibleEntities.get(world.getRandom().nextInt(visibleEntities.size()));
            entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(target.getX(), target.getY(), target.getZ()));
            ticksUntilNextStart = getRandomInterval(world.getRandom()); // Reset the cooldown
        }
    }

    private int getRandomInterval(Random random) {
        return minInterval + random.nextInt(maxInterval - minInterval + 1);
    }
}
