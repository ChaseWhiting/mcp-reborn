package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.World;

public class SmithingTransformRecipe implements NewSmithingRecipe {
    private final ResourceLocation id;
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public SmithingTransformRecipe(ResourceLocation resourceLocation, Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
        this.id = resourceLocation;
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public boolean matches(IInventory container, World world) {
        return  this.template.test(container.getItem(0)) &&
                this.base.test(container.getItem(1)) &&
                this.addition.test(container.getItem(2));
    }

    @Override
    public ItemStack assemble(IInventory inventory, DynamicRegistries registryAccess) {
        ItemStack itemStack = this.result.copy();
        CompoundNBT compoundNBT = inventory.getItem(1).getTag();
        if (compoundNBT != null) {
            itemStack.setTag(compoundNBT.copy());
        }
        return itemStack;
    }


    @Override
    public ItemStack getResultItem() {
        return this.result;
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
        return IRecipeSerializer.SMITHING_TRANSFORM;
    }

    public static class Serializer implements IRecipeSerializer<SmithingTransformRecipe> {
        public SmithingTransformRecipe fromJson(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
            Ingredient ingredient4 = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "template"));

            Ingredient ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "base"));
            Ingredient ingredient1 = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "addition"));
            ItemStack itemstack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(p_199425_2_, "result"));
            return new SmithingTransformRecipe(p_199425_1_, ingredient4, ingredient, ingredient1, itemstack);
        }

        public SmithingTransformRecipe fromNetwork(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
            Ingredient ingredient = Ingredient.fromNetwork(p_199426_2_);
            Ingredient ingredient2 = Ingredient.fromNetwork(p_199426_2_);
            Ingredient ingredient3 = Ingredient.fromNetwork(p_199426_2_);
            ItemStack itemstack = p_199426_2_.readItem();
            return new SmithingTransformRecipe(p_199426_1_, ingredient, ingredient2, ingredient3, itemstack);
        }

        public void toNetwork(PacketBuffer p_199427_1_, SmithingTransformRecipe p_199427_2_) {
            p_199427_2_.template.toNetwork(p_199427_1_);
            p_199427_2_.base.toNetwork(p_199427_1_);
            p_199427_2_.addition.toNetwork(p_199427_1_);
            p_199427_1_.writeItem(p_199427_2_.result);
        }
    }

}
