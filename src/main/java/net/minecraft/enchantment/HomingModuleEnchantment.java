package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class HomingModuleEnchantment extends Enchantment {
   private final Class<? extends LivingEntity> targetClass;
   private LivingEntity matchedEntity;

   public HomingModuleEnchantment(Rarity rarity, Class<? extends LivingEntity> targetClass) {
      super(rarity, EnchantmentType.FRISBEE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
      this.targetClass = targetClass;
      this.matchedEntity = null;
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

   public boolean checkCompatibility(Enchantment enchantment) {
      return super.checkCompatibility(enchantment) && enchantment != this;
   }

   public boolean trySetMatchedEntity(LivingEntity entity) {
      if (targetClass.isInstance(entity)) {
         this.matchedEntity = entity;
         return true;
      }
      return false;
   }

   public LivingEntity getMatchedEntity() {
      return this.matchedEntity;
   }
}
