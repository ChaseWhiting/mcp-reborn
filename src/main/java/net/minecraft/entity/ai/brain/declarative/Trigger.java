package net.minecraft.entity.ai.brain.declarative;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public interface Trigger<E extends LivingEntity> {
    public boolean trigger(ServerWorld var1, E var2, long var3);
}