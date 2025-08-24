package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.server.ServerWorld;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYERS);
    }

    @Override
    protected void doTick(ServerWorld serverLevel, LivingEntity livingEntity) {
        List<PlayerEntity> list = serverLevel.players().stream().filter(EntityPredicates.NO_SPECTATORS).filter(serverPlayer -> livingEntity.closerThan(serverPlayer, this.getFollowDistance(livingEntity))).sorted(Comparator.comparingDouble(livingEntity::distanceToSqr)).collect(Collectors.toList());
        Brain<?> brain = livingEntity.getBrain();
        brain.setMemory(MemoryModuleType.NEAREST_PLAYERS, list);
        List<PlayerEntity> list2 = list.stream().filter(player -> PlayerSensor.isEntityTargetable(serverLevel, livingEntity, player)).collect(Collectors.toList());
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, list2.isEmpty() ? null : (PlayerEntity) list2.get(0));
        List<PlayerEntity> list3 = list2.stream().filter(player -> PlayerSensor.isEntityAttackable(serverLevel, livingEntity, player)).toList();
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYERS, list3);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, list3.isEmpty() ? null : list3.get(0));
    }

    protected double getFollowDistance(LivingEntity livingEntity) {
        return livingEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
    }
}

