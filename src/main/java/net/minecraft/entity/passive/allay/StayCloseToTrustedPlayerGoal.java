package net.minecraft.entity.passive.allay;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

class StayCloseToTrustedPlayerGoal extends Goal {
    private final AllayEntity allay;
    private final float speed;
    private final double maxDistance;

    public StayCloseToTrustedPlayerGoal(AllayEntity allay, float speed, double maxDistance) {
        this.allay = allay;
        this.speed = speed;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean canUse() {
        return this.allay.getTrustedPlayer().isPresent();
    }

    @Override
    public void tick() {
        this.allay.getTrustedPlayer().ifPresent(uuid -> {
            PlayerEntity player = this.allay.level.getPlayerByUUID(uuid);
            if (player != null && this.allay.distanceTo(player) > this.maxDistance) {
                this.allay.getNavigation().moveTo(player, this.speed);
            }
        });
    }
}
