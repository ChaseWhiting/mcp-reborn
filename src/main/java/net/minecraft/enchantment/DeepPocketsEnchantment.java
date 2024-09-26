package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class DeepPocketsEnchantment extends Enchantment {
   public DeepPocketsEnchantment() {
      super(Rarity.VERY_RARE, EnchantmentType.BUNDLE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
   }

   public int getMinCost(int p_77321_1_) {
      return 40;
   }

   public int getMaxCost(int p_223551_1_) {
      return 60;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean isTreasureOnly() {
      return true;
   }

}