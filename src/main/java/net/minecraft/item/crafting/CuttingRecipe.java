package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class CuttingRecipe extends SingleItemRecipe {
   public CuttingRecipe(ResourceLocation p_i50021_1_, String p_i50021_2_, Ingredient p_i50021_3_, ItemStack p_i50021_4_, IRecipeType<?> type, IRecipeSerializer<?> serializer) {
      super(type, serializer, p_i50021_1_, p_i50021_2_, p_i50021_3_, p_i50021_4_);
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      return this.ingredient.test(p_77569_1_.getItem(0));
   }
}