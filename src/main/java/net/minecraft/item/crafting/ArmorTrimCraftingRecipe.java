package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;

import java.util.Optional;




public class ArmorTrimCraftingRecipe extends SpecialRecipe {
    public ArmorTrimCraftingRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        ItemStack armor = ItemStack.EMPTY;
        ItemStack material = ItemStack.EMPTY;
        ItemStack pattern = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (ArmorTrim.TRIMMABLE_ARMOR.contains(stack.getItem())) {
                    if (!armor.isEmpty()) return false; // Only one armor allowed
                    armor = stack;
                } else if (TrimMaterials.getFromIngredient(world.registryAccess(), stack).isPresent()) {
                    if (!material.isEmpty()) return false; // Only one material allowed
                    material = stack;
                } else if (TrimPatterns.getFromTemplate(world.registryAccess(), stack).isPresent()) {
                    if (!pattern.isEmpty()) return false; // Only one pattern allowed
                    pattern = stack;
                } else {
                    return false;
                }
            }
        }

        return !armor.isEmpty() && !material.isEmpty() && !pattern.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInventory inv, DynamicRegistries registryAccess) {
        ItemStack armor = ItemStack.EMPTY;
        Optional<TrimMaterial> material = Optional.empty();
        Optional<TrimPattern> pattern = Optional.empty();

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (ArmorTrim.TRIMMABLE_ARMOR.contains(stack.getItem())) {
                    if (!armor.isEmpty()) return ItemStack.EMPTY;
                    armor = stack.copy();
                } else if (!material.isPresent()) {
                    material = TrimMaterials.getFromIngredient(stack);
                } else if (!pattern.isPresent()) {
                    pattern = TrimPatterns.getFromTemplate(stack);
                }
            }
        }

        if (armor.isEmpty() || !material.isPresent() || !pattern.isPresent()) {
            return ItemStack.EMPTY;
        }

        // Check if the armor already has this trim (optional, add if you want)
        // Apply the trim
        ArmorTrim.setTrim(null, armor, new ArmorTrim(material.get(), pattern.get()));
        return armor;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        // Register your serializer accordingly
        return IRecipeSerializer.ARMOR_TRIM;
    }
}
