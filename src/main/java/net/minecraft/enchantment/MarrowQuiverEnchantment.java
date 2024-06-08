package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class MarrowQuiverEnchantment extends Enchantment {
    public MarrowQuiverEnchantment(Enchantment.Rarity rarity, EquipmentSlotType... slotTypes) {
        super(rarity, EnchantmentType.BONE_BOW, slotTypes);
    }

    public int getMinCost(int minCost) {
        return 1 + (minCost - 1) * 10;
    }

    public int getMaxCost(int maxCost) {
        return this.getMinCost(maxCost) + 15;
    }

    public int getMaxLevel() {
        return 3;
    }
}