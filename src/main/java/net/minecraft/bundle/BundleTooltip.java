package net.minecraft.bundle;


import net.minecraft.client.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;


public class BundleTooltip implements TooltipComponent {
   private final NonNullList<ItemStack> items;
   private final int weight;

   public BundleTooltip(NonNullList<ItemStack> p_150677_, int p_150678_) {
      this.items = p_150677_;
      this.weight = p_150678_;
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public int getWeight() {
      return this.weight;
   }


}