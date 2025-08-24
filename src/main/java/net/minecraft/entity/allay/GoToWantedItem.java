package net.minecraft.entity.allay;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;
import java.util.function.Predicate;

public class GoToWantedItem extends Task<AllayEntity> {

    private final Predicate<LivingEntity> predicate;
    private final float f;
    private final int n;

    public GoToWantedItem(Predicate<LivingEntity> predicate, float f, boolean bl, int n) {
        super(Map.of(MemoryModuleType.WALK_TARGET, (bl ? MemStatus.REGISTERED : MemStatus.ABSENT), MemoryModuleType.LOOK_TARGET, MemStatus.REGISTERED, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemStatus.PRESENT, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemStatus.REGISTERED));
        this.predicate=predicate;this.f =f;this.n=n;
    }

    @Override
    protected void start(ServerWorld world, AllayEntity allay, long time) {
        ItemEntity itemEntity = allay.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();

        if (allay.getBrain().getMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS).isEmpty() && predicate.test(allay) && itemEntity.closerThan(allay, n) && allay.level.getWorldBorder().isWithinBounds(itemEntity.blockPosition())) {
            WalkTarget walkTarget = new WalkTarget(new EntityPosWrapper(itemEntity, false), f, 0);
            allay.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walkTarget);
            allay.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(itemEntity, true));
        }
    }
}
