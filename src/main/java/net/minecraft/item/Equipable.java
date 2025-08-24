package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface Equipable extends IArmorVanishable {
    public EquipmentSlotType getEquipmentSlot();

    default public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GENERIC;
    }

    default public ActionResult<ItemStack> swapWithEquipmentSlot(Item item, World level, PlayerEntity player, Hand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        EquipmentSlotType equipmentSlot = Mob.getEquipmentSlotForItem(itemStack);
        ItemStack itemStack2 = player.getItemBySlot(equipmentSlot);
        if (EnchantmentHelper.hasBindingCurse(itemStack2) || ItemStack.matches(itemStack, itemStack2)) {
            return ActionResult.fail(itemStack);
        }
        if (!level.isClientSide()) {
            player.awardStat(Stats.ITEM_USED.get(item));
        }
        ItemStack itemStack3 = itemStack2.isEmpty() ? itemStack : itemStack2.copyAndClear();
        ItemStack itemStack4 = itemStack.copyAndClear();
        player.setItemSlot(equipmentSlot, itemStack4);
        return ActionResult.sidedSuccess(itemStack3, level.isClientSide());
    }

    default public ActionResult<ItemStack> swapWithEquipmentSlot(
            World level, EquipmentSlotType equipmentSlot,
            LivingEntity entity,
            ItemStack itemStack,
            PlayerEntity player,
            Hand hand
    ) {

        ItemStack itemStack2 = entity.getItemBySlot(equipmentSlot);

        if (EnchantmentHelper.hasBindingCurse(itemStack2) || ItemStack.matches(itemStack, itemStack2)) {
            return ActionResult.fail(itemStack);
        }

        // These are the swapped stacks
        ItemStack itemStack3 = itemStack2.isEmpty() ? itemStack : itemStack2.copyAndClear();
        ItemStack itemStack4 = itemStack.copyAndClear();
        entity.setItemSlot(equipmentSlot, itemStack4);

        // If entity isn't the player, set the swapped item back into player's hand
        if (!entity.equals(player)) {
            player.setItemInHand(hand, itemStack3);
        }

        return ActionResult.sidedSuccess(itemStack3, level.isClientSide());
    }



    @Nullable
    public static Equipable get(ItemStack itemStack) {
        BlockItem blockItem;
        Object object = itemStack.getItem();
        if (object instanceof Equipable) {
            Equipable equipable = (Equipable)object;
            return equipable;
        }

        return null;
    }
}
