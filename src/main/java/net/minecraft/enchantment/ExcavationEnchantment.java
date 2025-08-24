package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class ExcavationEnchantment extends Enchantment {
   public ExcavationEnchantment() {
      super(Rarity.UNCOMMON, EnchantmentType.BRUSH, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
   }

   public int getMinCost(int p_77321_1_) {
      return 40;
   }

   public int getMaxCost(int p_223551_1_) {
      return 60;
   }

   public int getMaxLevel() {
      return 4;
   }

   public boolean isTreasureOnly() {
      return false;
   }

}