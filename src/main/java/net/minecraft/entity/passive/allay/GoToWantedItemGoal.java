package net.minecraft.entity.passive.allay;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;

import java.util.Optional;

class GoToWantedItemGoal extends Goal {
    private final AllayEntity allay;
    private final float speed;
    private final int searchRadius;

    public GoToWantedItemGoal(AllayEntity allay, float speed, int searchRadius) {
        this.allay = allay;
        this.speed = speed;
        this.searchRadius = searchRadius;
    }

    @Override
    public boolean canUse() {
        return !this.allay.hasItemInHand() && findNearbyItem().isPresent();
    }

    @Override
    public void tick() {
        findNearbyItem().ifPresent(itemEntity -> this.allay.getNavigation().moveTo(itemEntity, this.speed));
    }

    private Optional<ItemEntity> findNearbyItem() {
        return this.allay.level.getEntitiesOfClass(ItemEntity.class, this.allay.getBoundingBox().inflate(this.searchRadius))
                .stream().filter(itemEntity -> itemEntity.isAlive() && !itemEntity.getItem().isEmpty())
                .findFirst();
    }
}
