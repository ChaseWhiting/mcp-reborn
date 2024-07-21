package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class MultishotEnchantment extends Enchantment {
   public MultishotEnchantment(Enchantment.Rarity p_i50017_1_, EquipmentSlotType... p_i50017_2_) {
      super(p_i50017_1_, EnchantmentType.CROSSBOW, p_i50017_2_);
   }

   public int getMinCost(int p_77321_1_) {
      return 20;
   }

   public int getMaxCost(int p_223551_1_) {
      return 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean checkCompatibility(Enchantment enchantment) {
      return super.checkCompatibility(enchantment) && enchantment != Enchantments.PIERCING && enchantment != Enchantments.RICOCHET;
   }
}