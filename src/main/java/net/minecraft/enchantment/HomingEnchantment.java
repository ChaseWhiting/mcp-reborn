package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class HomingEnchantment extends Enchantment {
   public HomingEnchantment(Rarity rarity, EquipmentSlotType... types) {
      super(rarity, EnchantmentType.FRISBEE, types);
   }

   public int getMinCost(int val) {
      return val * 12;
   }

   public int getMaxCost(int val) {
      return this.getMinCost(val) + 50;
   }


   public int getMaxLevel() {
      return 3;
   }

   public boolean checkCompatibility(Enchantment enchantment) {
      return super.checkCompatibility(enchantment) && enchantment != Enchantments.RETURNING;
   }
}