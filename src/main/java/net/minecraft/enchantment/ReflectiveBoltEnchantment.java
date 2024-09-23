package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class ReflectiveBoltEnchantment extends Enchantment {
   public ReflectiveBoltEnchantment() {
      super(Rarity.VERY_RARE, EnchantmentType.GILDED_CROSSBOW, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
   }

   public int getMinCost(int p_77321_1_) {
      return 1 + (p_77321_1_ - 1) * 10;
   }

   @Override
   public boolean isTradeable() {
      return false;
   }

   @Override
   public boolean isDiscoverable() {
      return true;
   }

   public int getMaxCost(int p_223551_1_) {
      return 50;
   }



   public int getMaxLevel() {
      return 4;
   }

   public boolean checkCompatibility(Enchantment enchantment) {
      return super.checkCompatibility(enchantment) && enchantment != Enchantments.MULTISHOT && enchantment != Enchantments.GRAVITY;
   }
}