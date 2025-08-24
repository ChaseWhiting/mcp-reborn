package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

import java.util.List;

public class SimpleEnchantment extends Enchantment {
   public SimpleEnchantment(Rarity rarity, int maxLevel, int minCost, int maxCost, boolean isTreasure, EnchantmentType type, List<Enchantment> incompatible, EquipmentSlotType... p_i46728_2_) {
      super(rarity, type, p_i46728_2_);
      this.maxLevel = maxLevel;
      this.minCost = minCost;
      this.maxCost = maxCost;
      this.isTreasure = isTreasure;
      this.incompatible = incompatible;
   }

   private final List<Enchantment> incompatible;

   private final int maxLevel; private final int minCost; private final int maxCost; private final boolean isTreasure;

   public int getMinCost(int p_77321_1_) {
      return p_77321_1_ * 10;
   }

   public int getMaxCost(int p_223551_1_) {
      return this.getMinCost(p_223551_1_) + 15;
   }

   public boolean isTreasureOnly() {
      return isTreasure;
   }

   public int getMaxLevel() {
      return this.maxLevel;
   }

   public boolean checkCompatibility(Enchantment p_77326_1_) {
      return super.checkCompatibility(p_77326_1_) && !incompatible.contains(p_77326_1_);
   }
}