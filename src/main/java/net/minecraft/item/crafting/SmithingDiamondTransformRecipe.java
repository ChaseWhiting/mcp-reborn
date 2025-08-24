package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.World;

import java.util.Map;

public class SmithingDiamondTransformRecipe implements NewSmithingRecipe {
    private final ResourceLocation id;
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public SmithingDiamondTransformRecipe(ResourceLocation resourceLocation, Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
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
        // Step 1: Start with a copy of the defined result item (e.g., diamond chestplate)
        ItemStack result = this.result.copy();

        ItemStack fromItem = inventory.getItem(1); // e.g., Iron Chestplate with Protection I
        ItemStack toItem = inventory.getItem(2);   // e.g., Diamond Chestplate with Protection II

        if (fromItem.isEmpty() || toItem.isEmpty()) {
            return result; // No valid source or target, return result unchanged
        }


        // ----- REPAIR LOGIC -----
        if (result.isDamageableItem()) {
            int durability1 = fromItem.getMaxDamage() - fromItem.getDamageValue();
            int durability2 = toItem.getMaxDamage() - toItem.getDamageValue();

            int totalDurability = durability1 + durability2 + (result.getMaxDamage() * 5 / 100);
            int newDamage = result.getMaxDamage() - totalDurability;
            if (newDamage < 0) newDamage = 0;

            result.setDamageValue(newDamage);
        }

        // Step 2: Get existing enchantments from both input items
        Map<Enchantment, Integer> fromEnchants = EnchantmentHelper.getEnchantments(fromItem);
        Map<Enchantment, Integer> toEnchants = EnchantmentHelper.getEnchantments(toItem);

        // Step 3: Merge enchantments following anvil rules
        for (Map.Entry<Enchantment, Integer> entry : fromEnchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int levelFrom = entry.getValue();
            int levelTo = toEnchants.getOrDefault(enchantment, 0);

            // Upgrade level if they're equal, or take the higher one
            int finalLevel = (levelFrom == levelTo)
                    ? Math.min(levelFrom + 1, enchantment.getMaxLevel())
                    : Math.max(levelFrom, levelTo);

            // Check compatibility
            boolean compatible = true;
            for (Enchantment existing : toEnchants.keySet()) {
                if (!existing.equals(enchantment) && !enchantment.isCompatibleWith(existing)) {
                    compatible = false;
                    break;
                }
            }

            if (compatible) {
                toEnchants.put(enchantment, finalLevel);
            }
        }

        // Step 4: Apply the merged enchantments onto the result item
        EnchantmentHelper.setEnchantments(toEnchants, result);

        // Optional: merge NBT data from toItem without overriding enchantments
        CompoundNBT toTag = fromItem.getTag();
        if (toTag != null) {
            CompoundNBT resultTag = result.getTag();
            if (resultTag == null) resultTag = new CompoundNBT();

            for (String key : toTag.getAllKeys()) {
                if (!key.equals("Enchantments") && !key.equals("Damage")) {
                    resultTag.put(key, toTag.get(key).copy());
                }
            }

            result.setTag(resultTag);
        }

        return result;
    }



    public ItemStack combineEnchantments(ItemStack base, ItemStack addition) {
        if (base.isEmpty() || addition.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = base.copy();

        Map<Enchantment, Integer> baseEnchants = EnchantmentHelper.getEnchantments(result);
        Map<Enchantment, Integer> additionEnchants = EnchantmentHelper.getEnchantments(addition);

        for (Map.Entry<Enchantment, Integer> entry : additionEnchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int levelAddition = entry.getValue();
            int levelBase = baseEnchants.getOrDefault(enchantment, 0);

            int newLevel;
            if (levelBase == levelAddition) {
                newLevel = Math.min(levelAddition + 1, enchantment.getMaxLevel());
            } else {
                newLevel = Math.max(levelBase, levelAddition);
            }

            // Only combine if compatible
            boolean compatible = true;
            for (Enchantment existing : baseEnchants.keySet()) {
                if (!enchantment.isCompatibleWith(existing)) {
                    compatible = false;
                    break;
                }
            }

            if (compatible || base.getItem() == Items.ENCHANTED_BOOK) {
                baseEnchants.put(enchantment, newLevel);
            }
        }

        EnchantmentHelper.setEnchantments(baseEnchants, result);
        return result;
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

    public static class Serializer implements IRecipeSerializer<SmithingDiamondTransformRecipe> {
        public SmithingDiamondTransformRecipe fromJson(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
            Ingredient ingredient4 = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "template"));

            Ingredient ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "base"));
            Ingredient ingredient1 = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "addition"));
            ItemStack itemstack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(p_199425_2_, "result"));
            return new SmithingDiamondTransformRecipe(p_199425_1_, ingredient4, ingredient, ingredient1, itemstack);
        }

        public SmithingDiamondTransformRecipe fromNetwork(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
            Ingredient ingredient = Ingredient.fromNetwork(p_199426_2_);
            Ingredient ingredient2 = Ingredient.fromNetwork(p_199426_2_);
            Ingredient ingredient3 = Ingredient.fromNetwork(p_199426_2_);
            ItemStack itemstack = p_199426_2_.readItem();
            return new SmithingDiamondTransformRecipe(p_199426_1_, ingredient, ingredient2, ingredient3, itemstack);
        }

        public void toNetwork(PacketBuffer p_199427_1_, SmithingDiamondTransformRecipe p_199427_2_) {
            p_199427_2_.template.toNetwork(p_199427_1_);
            p_199427_2_.base.toNetwork(p_199427_1_);
            p_199427_2_.addition.toNetwork(p_199427_1_);
            p_199427_1_.writeItem(p_199427_2_.result);
        }
    }

}
