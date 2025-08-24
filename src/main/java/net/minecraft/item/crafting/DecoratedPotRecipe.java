package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.DecoratedPotTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.stream.Collectors;

public class DecoratedPotRecipe extends SpecialRecipe {

    public DecoratedPotRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    public boolean matches(CraftingInventory inventory, World world) {

        block3: for(int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemstack1 = inventory.getItem(i);
            switch (i) {
                case 1:
                case 3:
                case 5:
                case 7: {
                    if (itemstack1.getItem().is(Registry.ITEM.stream().filter(item -> item.getRegistryName().contains("sherd") || item == Items.BRICK).collect(Collectors.toList()))) continue block3;
                    return false;
                }
                default: {
                    if (itemstack1.getItem() == (Items.AIR)) continue block3;
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingInventory craftingContainer, DynamicRegistries registryAccess) {
        DecoratedPotTileEntity.Decorations decorations = new DecoratedPotTileEntity.Decorations(craftingContainer.getItem(1).getItem(), craftingContainer.getItem(3).getItem(), craftingContainer.getItem(5).getItem(), craftingContainer.getItem(7).getItem());
        return DecoratedPotTileEntity.createDecoratedPotItem(decorations);
    }

    @Override
    public boolean canCraftInDimensions(int n, int n2) {
        return n == 3 && n2 == 3;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return IRecipeSerializer.DECORATED_POT_RECIPE;
    }
}
