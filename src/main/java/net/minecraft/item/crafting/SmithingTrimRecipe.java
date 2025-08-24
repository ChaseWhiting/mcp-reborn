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

public class SmithingTrimRecipe implements NewSmithingRecipe {
    private final ResourceLocation id;
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;

    public SmithingTrimRecipe(ResourceLocation resourceLocation, Ingredient pattern, Ingredient trimmable, Ingredient material) {
        this.id = resourceLocation;
        this.template = pattern;
        this.base = trimmable;
        this.addition = material;
    }

    @Override
    public boolean matches(IInventory container, World world) {
        return  this.template.test(container.getItem(0)) &&
                this.base.test(container.getItem(1)) &&
                this.addition.test(container.getItem(2));
    }

    @Override
    public ItemStack assemble(IInventory container, DynamicRegistries registryAccess) {
        ItemStack itemStack = container.getItem(1);
        if (this.base.test(itemStack)) {
            Optional<TrimMaterial> optional = TrimMaterials.getFromIngredient(container.getItem(2));
            Optional<TrimPattern> optional2 = TrimPatterns.getFromTemplate(container.getItem(0));
            if (optional.isPresent() && optional2.isPresent()) {
                Optional<ArmorTrim> optional3 = ArmorTrim.getTrim(registryAccess, itemStack);
                if (optional3.isPresent() && optional3.get().hasPatternAndMaterial(optional2.get(), optional.get())) {
                    return ItemStack.EMPTY;
                }
                ItemStack itemStack2 = itemStack.copy();
                itemStack2.setCount(1);
                ArmorTrim armorTrim = new ArmorTrim(optional.get(), optional2.get());
                if (ArmorTrim.setTrim(registryAccess, itemStack2, armorTrim)) {
                    return itemStack2;
                }
            }

        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem() {
        Optional<TrimMaterial> optional;
        ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
        Optional<TrimPattern> optional2 = Registry.TRIM_PATTERN.stream().findFirst();
        if (optional2.isPresent() && (optional = Registry.TRIM_MATERIAL.getOptional(TrimMaterials.REDSTONE)).isPresent()) {
            ArmorTrim armorTrim = new ArmorTrim(optional.get(), optional2.get());
            ArmorTrim.setTrim(null, itemStack, armorTrim);
        }

        return itemStack;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack itemStack) {
        return this.template.test(itemStack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack itemStack) {
        return this.base.test(itemStack);
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
        return IRecipeSerializer.SMITHING_TRIM;
    }

    public static class Serializer implements IRecipeSerializer<SmithingTrimRecipe> {
        public SmithingTrimRecipe fromJson(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
            Ingredient ingredient4 = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "template"));

            Ingredient ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "base"));
            Ingredient ingredient1 = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "addition"));
            return new SmithingTrimRecipe(p_199425_1_, ingredient4, ingredient, ingredient1);
        }

        public SmithingTrimRecipe fromNetwork(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
            Ingredient ingredient = Ingredient.fromNetwork(p_199426_2_);
            Ingredient ingredient2 = Ingredient.fromNetwork(p_199426_2_);
            Ingredient ingredient3 = Ingredient.fromNetwork(p_199426_2_);
            return new SmithingTrimRecipe(p_199426_1_, ingredient, ingredient2, ingredient3);
        }

        public void toNetwork(PacketBuffer p_199427_1_, SmithingTrimRecipe p_199427_2_) {
            p_199427_2_.template.toNetwork(p_199427_1_);
            p_199427_2_.base.toNetwork(p_199427_1_);
            p_199427_2_.addition.toNetwork(p_199427_1_);
        }
    }
}
