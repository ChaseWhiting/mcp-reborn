package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShulkerBoxColoringRecipe extends SpecialRecipe {
   public ShulkerBoxColoringRecipe(ResourceLocation p_i48159_1_) {
      super(p_i48159_1_);
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      int i = 0;
      int j = 0;

      for(int k = 0; k < p_77569_1_.getContainerSize(); ++k) {
         ItemStack itemstack = p_77569_1_.getItem(k);
         if (!itemstack.isEmpty()) {
            if (Block.byItem(itemstack.getItem()) instanceof ShulkerBoxBlock) {
               ++i;
            } else {
               if (!(itemstack.getItem() instanceof DyeItem)) {
                  return false;
               }

               ++j;
            }

            if (j > 1 || i > 1) {
               return false;
            }
         }
      }

      return i == 1 && j == 1;
   }

   public ItemStack assemble(CraftingInventory p_77572_1_, DynamicRegistries registryAccess) {
      ItemStack itemstack = ItemStack.EMPTY;
      DyeItem dyeitem = (DyeItem)Items.WHITE_DYE;

      for(int i = 0; i < p_77572_1_.getContainerSize(); ++i) {
         ItemStack itemstack1 = p_77572_1_.getItem(i);
         if (!itemstack1.isEmpty()) {
            Item item = itemstack1.getItem();
            if (Block.byItem(item) instanceof ShulkerBoxBlock) {
               itemstack = itemstack1;
            } else if (item instanceof DyeItem) {
               dyeitem = (DyeItem)item;
            }
         }
      }

      ItemStack itemstack2 = ShulkerBoxBlock.getColoredItemStack(dyeitem.getDyeColor());
      if (itemstack.hasTag()) {
         itemstack2.setTag(itemstack.getTag().copy());
      }

      return itemstack2;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.SHULKER_BOX_COLORING;
   }
}