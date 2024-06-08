package net.minecraft.enchantment;

import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;

public enum EnchantmentType {
   ARMOR {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem;
      }
   },
   ARMOR_FEET {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem && ((ArmorItem)p_77557_1_).getSlot() == EquipmentSlotType.FEET;
      }
   },
   ARMOR_LEGS {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem && ((ArmorItem)p_77557_1_).getSlot() == EquipmentSlotType.LEGS;
      }
   },
   ARMOR_CHEST {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem && ((ArmorItem)p_77557_1_).getSlot() == EquipmentSlotType.CHEST;
      }
   },
   ARMOR_HEAD {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem && ((ArmorItem)p_77557_1_).getSlot() == EquipmentSlotType.HEAD;
      }
   },
   WEAPON {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof SwordItem;
      }
   },
   DIGGER {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof ToolItem;
      }
   },
   FISHING_ROD {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof FishingRodItem;
      }
   },
   TRIDENT {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof TridentItem;
      }
   },
   BREAKABLE {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_.canBeDepleted();
      }
   },
   BOW {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof BowItem;
      }
   },
   BOW_NOT_BONE_BOW {
      public boolean canEnchant(Item item) {
         return item instanceof BowItem && !(item instanceof BoneBowItem);
      }
   },
   BONE_BOW {
      public boolean canEnchant(Item item) {
         return item instanceof BoneBowItem;
      }
   },
   WEARABLE {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof IArmorVanishable || Block.byItem(p_77557_1_) instanceof IArmorVanishable;
      }
   },
   CROSSBOW {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof CrossbowItem;
      }
   },
   CROSSBOW_OR_BONE_BOW {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof CrossbowItem || p_77557_1_ instanceof BoneBowItem;
      }
   },
   VANISHABLE {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof IVanishable || Block.byItem(p_77557_1_) instanceof IVanishable || BREAKABLE.canEnchant(p_77557_1_);
      }
   };

   private EnchantmentType() {
   }

   public abstract boolean canEnchant(Item p_77557_1_);
}