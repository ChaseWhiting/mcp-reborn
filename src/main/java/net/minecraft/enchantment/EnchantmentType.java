package net.minecraft.enchantment;

import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.dagger.DesolateDaggerItem;
import net.minecraft.item.tool.*;

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
   BRUSH {
      @Override
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof BrushItem;
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
   DESOLATE_DAGGER {
      @Override
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof DesolateDaggerItem;
      }
   },
   CONDUCTIVE {
      @Override
      public boolean canEnchant(Item item) {
         boolean flag = item instanceof IArmorVanishable || Block.byItem(item) instanceof IArmorVanishable;
         boolean flag1 = item instanceof TieredItem;
         boolean flag3 = item instanceof CrossbowItem || item instanceof GildedCrossbowItem;
         boolean flag2 = flag1 && (((TieredItem)item).tier() == ItemTier.IRON || ((TieredItem)item).tier() == ItemTier.GOLD || ((TieredItem)item).tier() == ItemTier.NETHERITE);


         return flag || flag2 || item instanceof TridentItem ||
                 item instanceof FishingRodItem || flag3 ||
                 item instanceof ShearsItem ||
                 item instanceof BucketItem ||
                 item instanceof CompassItem ||
                 item == Items.CLOCK ||
                 item == Items.TOTEM_OF_UNDYING ||
                 item instanceof ShieldItem || item == Items.IRON_NUGGET || item == Items.GOLD_NUGGET || item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE;
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
   FRISBEE {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof FrisbeeItem || p_77557_1_ instanceof MusicDiscItem;
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
   HOE {
      @Override
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof HoeItem;
      }
   },
   CROSSBOW {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof CrossbowItem || p_77557_1_ instanceof GildedCrossbowItem || p_77557_1_ instanceof AbstractCrossbowItem;
      }
   },
   CROSSBOW_OR_BONE_BOW {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof CrossbowItem || p_77557_1_ instanceof BoneBowItem || p_77557_1_ instanceof GildedCrossbowItem || p_77557_1_ instanceof AbstractCrossbowItem;
      }
   },
   GILDED_CROSSBOW {
      public boolean canEnchant(Item item) {
         return item == Items.GILDED_CROSSBOW;
      }
   },
   SPECIAL_CROSSBOW {
      public boolean canEnchant(Item item) {
         return item instanceof AbstractCrossbowItem || item == Items.GILDED_CROSSBOW;
      }
   },
   ABSTRACT_CROSSBOW {
     public boolean canEnchant(Item item) {

        return item instanceof AbstractCrossbowItem;
     }
   },
   VANISHABLE {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof IVanishable || Block.byItem(p_77557_1_) instanceof IVanishable || BREAKABLE.canEnchant(p_77557_1_);
      }
   },
   BUNDLE {
      @Override
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ == Items.BUNDLE;
      }
   },
   ANY {
      @Override
      public boolean canEnchant(Item item) {
         return item.getWeight() != 1 && item != Items.BUNDLE;
      }
   };

   private EnchantmentType() {
   }

   public abstract boolean canEnchant(Item p_77557_1_);

}