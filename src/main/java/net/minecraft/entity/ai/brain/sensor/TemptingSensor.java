package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.entity.Mob;
import net.minecraft.entity.TargetingConditions;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.server.ServerWorld;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TemptingSensor extends Sensor<Mob> {

    public static final int TEMPTATION_RANGE = 10;
    private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().range(TEMPTATION_RANGE).ignoreLineOfSight();
    private final Ingredient temptations;

    public TemptingSensor(Ingredient ingredient) {
        this.temptations = ingredient;
    }

    @Override
    protected void doTick(ServerWorld world, Mob mob) {
        Brain<?> brain = mob.getBrain();
        List<PlayerEntity> nearbyPlayers = world
                .players()
                .stream()
                .filter(EntityPredicates.NO_SPECTATORS)
                .filter(serverPlayerEntity -> TEMPT_TARGETING.test(mob, serverPlayerEntity))
                .filter(serverPlayerEntity -> mob.closerThan(serverPlayerEntity, TEMPTATION_RANGE))
                .filter(this::playerHoldingTemptation)
                .filter(serverPlayerEntity -> !mob.hasPassenger(serverPlayerEntity))
                .sorted(Comparator.comparingDouble(mob::distanceToSqr))
                .collect(Collectors.toList());

        if (!nearbyPlayers.isEmpty()) {
            PlayerEntity player = nearbyPlayers.get(0);
            brain.setMemory(MemoryModuleType.TEMPTING_PLAYER, player);
        } else {
            brain.eraseMemory(MemoryModuleType.TEMPTING_PLAYER);
        }
    }

    private boolean playerHoldingTemptation(PlayerEntity player) {
        return this.isTemptation(player.getMainHandItem()) || this.isTemptation(player.getOffhandItem());
    }

    private boolean isTemptation(ItemStack itemStack) {
        return this.temptations.test(itemStack);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleType.TEMPTING_PLAYER);
    }
}
