package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class SpectralThrowEnchantment extends Enchantment {
   public SpectralThrowEnchantment(Rarity rarity, EquipmentSlotType... types) {
      super(rarity, EnchantmentType.FRISBEE, types);
   }

   public int getMinCost(int val) {
      return val * 12;
   }

   public int getMaxCost(int val) {
      return this.getMinCost(val) + 50;
   }


   public int getMaxLevel() {
      return 1;
   }

}