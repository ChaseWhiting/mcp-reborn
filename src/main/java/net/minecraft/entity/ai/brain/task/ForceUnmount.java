package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;


public class ForceUnmount
extends Task<LivingEntity> {
    public ForceUnmount() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverWorld, LivingEntity livingEntity) {
        return livingEntity.isPassenger();
    }

    @Override
    protected void start(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        livingEntity.unRide();
    }
}

