/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.frog.FrogEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FrogAttackablesSensor
extends NearestVisibleLivingEntitySensor {
    public static final float TARGET_DETECTION_DISTANCE = 10.0f;

    @Override
    protected boolean isMatchingEntity(LivingEntity livingEntity, LivingEntity livingEntity2) {
        if (!livingEntity.getBrain().hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN) && isEntityTargetable(livingEntity, livingEntity2) && FrogEntity.canEat(livingEntity2) && !this.isUnreachableAttackTarget(livingEntity, livingEntity2)) {
            return livingEntity2.closerThan(livingEntity, 10.0);
        }
        return false;
    }

    private boolean isUnreachableAttackTarget(LivingEntity livingEntity, LivingEntity livingEntity2) {
        List<UUID> list = livingEntity.getBrain().getMemory(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS).orElseGet(ArrayList::new);
        return list.contains(livingEntity2.getUUID());
    }

    @Override
    protected MemoryModuleType<LivingEntity> getMemory() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }
}

