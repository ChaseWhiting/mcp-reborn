package net.minecraft.item.tool;

import net.minecraft.enchantment.IVanishable;
import net.minecraft.item.ItemStack;

public class BowItem extends SimpleShootableBowItem implements IVanishable, BowSource {
   public BowItem(Properties p_i48522_1_) {
      super(p_i48522_1_);
   }

   public int getWeight(ItemStack bundle) {
      return 14;
   }
}
