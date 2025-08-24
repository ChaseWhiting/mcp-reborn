package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.*;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DyeRandomly extends LootFunction {


   private DyeRandomly(ILootCondition[] conditions) {
      super(conditions);
   }

   public LootFunctionType getType() {
      return LootFunctionManager.DYE_RANDOMLY;
   }

   public ItemStack run(ItemStack stack, LootContext context) {
      Random random = context.getRandom();

      if (stack.getItem() instanceof IDyeableArmorItem) {
         List<DyeColor> dyeColors = new ArrayList<>(Arrays.asList(DyeColor.values()));
         Util.shuffle(dyeColors, random);

         List<DyeItem> dyeItems = new ArrayList<>();


         int count = random.nextInt(2, 6);

         for (int i = 0; i < count; i++) {
            dyeItems.add(DyeItem.byColor(dyeColors.get(i)));
         }

         stack = IDyeableArmorItem.dyeArmor(stack, dyeItems);
      }

      return stack;
   }

   public static DyeRandomly.Builder dyeRandomly() {
      return new DyeRandomly.Builder();
   }

   public static class Builder extends LootFunction.Builder<DyeRandomly.Builder> {

      protected DyeRandomly.Builder getThis() {
         return this;
      }


      public ILootFunction build() {
         return new DyeRandomly(this.getConditions());
      }
   }

   public static class Serializer extends LootFunction.Serializer<DyeRandomly> {
      public void serialize(JsonObject p_230424_1_, DyeRandomly p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
      }

      public DyeRandomly deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return new DyeRandomly(p_186530_3_);
      }
   }
}