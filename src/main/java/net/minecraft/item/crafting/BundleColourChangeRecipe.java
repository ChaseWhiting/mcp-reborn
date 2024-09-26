package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.bundle.BundleItem;
import net.minecraft.bundle.BundleColour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BundleColourChangeRecipe extends SpecialRecipe {
   public BundleColourChangeRecipe(ResourceLocation id) {
      super(id);
   }

   @Override
   public boolean matches(CraftingInventory inv, World world) {
      ItemStack bundle = ItemStack.EMPTY;
      ItemStack dye = ItemStack.EMPTY;

      for (int i = 0; i < inv.getContainerSize(); ++i) {
         ItemStack currentItem = inv.getItem(i);
         if (!currentItem.isEmpty()) {
            if (currentItem.getItem() instanceof BundleItem) {
               if (!bundle.isEmpty()) {
                  return false; // Only one bundle allowed
               }
               bundle = currentItem;
            } else if (currentItem.getItem() instanceof DyeItem) {
               if (!dye.isEmpty()) {
                  return false; // Only one dye allowed
               }
               dye = currentItem;
            } else {
               return false; // Invalid item in crafting grid
            }
         }
      }

      return !bundle.isEmpty() && !dye.isEmpty();
   }

   @Override
   public ItemStack assemble(CraftingInventory inv) {
      ItemStack bundle = ItemStack.EMPTY;
      DyeItem dye = null;

      for (int i = 0; i < inv.getContainerSize(); ++i) {
         ItemStack currentItem = inv.getItem(i);
         if (!currentItem.isEmpty()) {
            if (currentItem.getItem() instanceof BundleItem) {
               if (!bundle.isEmpty()) {
                  return ItemStack.EMPTY; // Invalid recipe if more than one bundle
               }
               bundle = currentItem.copy();
            } else if (currentItem.getItem() instanceof DyeItem) {
               if (dye != null) {
                  return ItemStack.EMPTY; // Invalid recipe if more than one dye
               }
               dye = (DyeItem) currentItem.getItem();
            }
         }
      }

      if (bundle.isEmpty() || dye == null) {
         return ItemStack.EMPTY;
      }

      // Apply the color to the bundle
      BundleColour colour = BundleColour.byDye(dye.getDyeColor());
      BundleItem.setColour(bundle, colour);

      return bundle;
   }

   @Override
   @OnlyIn(Dist.CLIENT)
   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   @Override
   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.BUNDLE_DYE; // Register your own serializer
   }
}
