package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IRecipe<C extends IInventory> {
   boolean matches(C p_77569_1_, World p_77569_2_);

   ItemStack assemble(C p_77572_1_);

   @OnlyIn(Dist.CLIENT)
   boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_);

   ItemStack getResultItem();

   default NonNullList<ItemStack> getRemainingItems(C p_179532_1_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_179532_1_.getContainerSize(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         Item item = p_179532_1_.getItem(i).getItem();
         if (item.hasCraftingRemainingItem()) {
            nonnulllist.set(i, new ItemStack(item.getCraftingRemainingItem()));
         }
      }

      return nonnulllist;
   }

   default NonNullList<Ingredient> getIngredients() {
      return NonNullList.create();
   }

   default boolean isSpecial() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   default String getGroup() {
      return "";
   }

   @OnlyIn(Dist.CLIENT)
   default ItemStack getToastSymbol() {
      return new ItemStack(Blocks.CRAFTING_TABLE);
   }

   ResourceLocation getId();

   IRecipeSerializer<?> getSerializer();

   IRecipeType<?> getType();
}