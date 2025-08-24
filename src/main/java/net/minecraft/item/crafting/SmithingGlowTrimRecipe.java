package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Optional;

public class SmithingGlowTrimRecipe implements NewSmithingRecipe {
    private final ResourceLocation id;
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;

    public SmithingGlowTrimRecipe(ResourceLocation resourceLocation, Ingredient pattern, Ingredient trimmedItem, Ingredient glowItem) {
        this.id = resourceLocation;
        this.template = pattern;
        this.base = trimmedItem;
        this.addition = glowItem;
    }

    @Override
    public boolean matches(IInventory container, World world) {
        return this.template.test(container.getItem(0))
            && this.base.test(container.getItem(1))
            && this.addition.test(container.getItem(2))
            && ArmorTrim.getTrim(null, container.getItem(1)).isPresent();
    }

    @Override
    public ItemStack assemble(IInventory container, DynamicRegistries registryAccess) {
        ItemStack trimmedItem = container.getItem(1);
        if (this.base.test(trimmedItem)) {
            Optional<ArmorTrim> existingTrim = ArmorTrim.getTrim(registryAccess, trimmedItem);
            Optional<TrimPattern> newPattern = TrimPatterns.getFromTemplate(container.getItem(0));
            if (existingTrim.isPresent() && newPattern.isPresent()) {
                ArmorTrim currentTrim = existingTrim.get();
                TrimMaterial material = currentTrim.getMaterial();
                ArmorTrim glowTrim = new ArmorTrim(material, newPattern.get(), true);


                if (existingTrim.get().glow && existingTrim.get().getPattern() == newPattern.get()) {
                    return ItemStack.EMPTY;
                }

                if (existingTrim.get().getMaterial() == Registry.TRIM_MATERIAL.get(TrimMaterials.SCULK)) {
                    return ItemStack.EMPTY;
                }

                ItemStack result = trimmedItem.copy();
                result.setCount(1);
                if (ArmorTrim.setTrim(registryAccess, result, glowTrim)) {
                    return result;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem() {
        ItemStack sample = new ItemStack(Items.IRON_CHESTPLATE);
        Optional<TrimMaterial> material = Registry.TRIM_MATERIAL.getOptional(TrimMaterials.REDSTONE);
        Optional<TrimPattern> pattern = Registry.TRIM_PATTERN.stream().findFirst();
        if (pattern.isPresent() && material.isPresent()) {
            ArmorTrim glowTrim = new ArmorTrim(material.get(), pattern.get(), true);
            ArmorTrim.setTrim(null, sample, glowTrim);
        }
        return sample;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack itemStack) {
        return this.template.test(itemStack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack itemStack) {
        return this.base.test(itemStack)
            && ArmorTrim.getTrim(null, itemStack).isPresent();
    }

    @Override
    public boolean isAdditionIngredient(ItemStack itemStack) {
        return this.addition.test(itemStack);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return IRecipeSerializer.SMITHING_GLOW_TRIM;
    }

    public static class Serializer implements IRecipeSerializer<SmithingGlowTrimRecipe> {
        public SmithingGlowTrimRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient pattern = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "template"));
            Ingredient trimmedItem = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "base"));
            Ingredient glowItem = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "addition"));
            return new SmithingGlowTrimRecipe(id, pattern, trimmedItem, glowItem);
        }

        public SmithingGlowTrimRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
            Ingredient pattern = Ingredient.fromNetwork(buffer);
            Ingredient trimmedItem = Ingredient.fromNetwork(buffer);
            Ingredient glowItem = Ingredient.fromNetwork(buffer);
            return new SmithingGlowTrimRecipe(id, pattern, trimmedItem, glowItem);
        }

        public void toNetwork(PacketBuffer buffer, SmithingGlowTrimRecipe recipe) {
            recipe.template.toNetwork(buffer);
            recipe.base.toNetwork(buffer);
            recipe.addition.toNetwork(buffer);
        }
    }
}
