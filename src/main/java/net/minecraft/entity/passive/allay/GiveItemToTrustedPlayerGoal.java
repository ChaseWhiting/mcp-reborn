package net.minecraft.entity.passive.allay;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;

import java.util.Optional;

class GiveItemToTrustedPlayerGoal extends Goal {
    private final AllayEntity allay;
    private final float speed;

    public GiveItemToTrustedPlayerGoal(AllayEntity allay, float speed) {
        this.allay = allay;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        return this.allay.hasItemInHand() && getTrustedPlayer().isPresent();
    }

    @Override
    public void tick() {
        getTrustedPlayer().ifPresent(player -> {
            this.allay.getNavigation().moveTo(player, this.speed);
            if (this.allay.distanceTo(player) < 3.0F) {
                ItemStack item = this.allay.getItemInHand(Hand.MAIN_HAND);
                player.addItem(item);
                this.allay.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
               // this.allay.playSound(SoundEvents.ALLAY_ITEM_TAKEN, 1.0F, 1.0F);
            }
        });
    }

    private Optional<PlayerEntity> getTrustedPlayer() {
        return this.allay.getTrustedPlayer().flatMap(uuid -> Optional.ofNullable(this.allay.level.getPlayerByUUID(uuid)));
    }
}
