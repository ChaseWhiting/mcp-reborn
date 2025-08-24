package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface NewSmithingRecipe extends IRecipe<IInventory> {

    default public IRecipeType<?> getType() {
        return IRecipeType.NEW_SMITHING;
    }

    @Override
    default public boolean canCraftInDimensions(int n, int n2) {
        return n >= 3 && n2 >= 1;
    }

    @Override
    default public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    public boolean isTemplateIngredient(ItemStack var1);

    public boolean isBaseIngredient(ItemStack var1);

    public boolean isAdditionIngredient(ItemStack var1);
}
