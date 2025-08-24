package net.minecraft.entity.allay;

import net.minecraft.entity.Mob;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public interface InventoryCarrier {
    public static final String TAG_INVENTORY = "Inventory";

    public Inventory getInventory();

    public static void pickUpItem(Mob mob, InventoryCarrier inventoryCarrier, ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getItem();
        if (mob.wantsToPickUp(itemStack)) {
            Inventory simpleContainer = inventoryCarrier.getInventory();
            boolean bl = simpleContainer.canAddItem(itemStack);
            if (!bl) {
                return;
            }
            mob.onItemPickup(itemEntity);
            int n = itemStack.getCount();
            ItemStack itemStack2 = simpleContainer.addItem(itemStack);
            mob.take(itemEntity, n - itemStack2.getCount());
            if (itemStack2.isEmpty()) {
                itemEntity.discard();
            } else {
                itemStack.setCount(itemStack2.getCount());
            }
        }
    }

    default public void readInventoryFromTag(CompoundNBT compoundTag) {
        if (compoundTag.contains(TAG_INVENTORY, 9)) {
            this.getInventory().fromTag(compoundTag.getList(TAG_INVENTORY, 10));
        }
    }

    default public void writeInventoryToTag(CompoundNBT compoundTag) {
        compoundTag.put(TAG_INVENTORY, this.getInventory().createTag());
    }
}

