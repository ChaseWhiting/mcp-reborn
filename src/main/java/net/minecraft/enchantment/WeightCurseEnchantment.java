package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class WeightCurseEnchantment extends Enchantment {
   public WeightCurseEnchantment(Rarity p_i47254_1_, EquipmentSlotType... p_i47254_2_) {
      super(p_i47254_1_, EnchantmentType.WEARABLE, p_i47254_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 25;
   }

   public int getMaxCost(int p_223551_1_) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean isTreasureOnly() {
      return true;
   }

   public boolean isCurse() {
      return true;
   }
}