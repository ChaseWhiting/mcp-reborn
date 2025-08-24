package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
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
               return false;
            }
         }
      }

      return !bundle.isEmpty() && !dye.isEmpty();
   }

   @Override
   public ItemStack assemble(CraftingInventory inv, DynamicRegistries registryAccess) {
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

      // Get the current color ID of the bundle
      int currentColourId = BundleItem.getColourId(bundle);

      // Get the dye's target color ID
      int dyeColourId = BundleColour.byDye(dye.getDyeColor()).getId();

      // Check if the bundle is already dyed with the same color
      if (currentColourId == dyeColourId) {
         return ItemStack.EMPTY; // Recipe is invalid if the bundle is already that color
      }

      // Apply the new color to the bundle
      BundleColour newColour = BundleColour.byDye(dye.getDyeColor());
      BundleItem.setColour(bundle, newColour);

      return bundle;
   }

   @Override
   @OnlyIn(Dist.CLIENT)
   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   @Override
   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.BUNDLE_DYE;
   }
}
