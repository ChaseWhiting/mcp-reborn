package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

public class ConditionalLootFunction extends LootFunction {
    private final ILootFunction wrappedFunction;
    private final float chance;

    protected ConditionalLootFunction(ILootCondition[] conditions, ILootFunction wrappedFunction, float chance) {
        super(conditions);
        this.wrappedFunction = wrappedFunction;
        this.chance = chance;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionManager.CONDITIONAL; // Register your type somewhere!
    }

    // 🔨 Builder
    public static class Builder extends LootFunction.Builder<Builder> {
        private final ILootFunction wrappedFunction;
        private final float chance;

        protected Builder(ILootFunction wrappedFunction, float chance) {
            this.wrappedFunction = wrappedFunction;
            this.chance = chance;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public ILootFunction build() {
            return new ConditionalLootFunction(this.getConditions(), wrappedFunction, chance);
        }
    }

    // 🚀 Entry point
    public static Builder function(ILootFunction function, float chance) {
        return new Builder(function, chance);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        Random random = context.getRandom();
        if (random.nextFloat() < this.chance && wrappedFunction instanceof LootFunction function) {
            return function.run(stack, context);
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<ConditionalLootFunction> {
        @Override
        public void serialize(JsonObject json, ConditionalLootFunction function, JsonSerializationContext context) {
            super.serialize(json, function, context);
            json.add("function", context.serialize(function.wrappedFunction));
            json.addProperty("chance", function.chance);
        }

        @Override
        public ConditionalLootFunction deserialize(JsonObject json, JsonDeserializationContext context, ILootCondition[] conditions) {
            LootFunction wrapped = context.deserialize(json.get("function"), LootFunction.class);
            float chance = JSONUtils.getAsFloat(json, "chance");
            return new ConditionalLootFunction(conditions, wrapped, chance);
        }
    }
}
