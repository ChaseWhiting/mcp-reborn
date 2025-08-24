package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.*;

import java.util.Set;

public class Always implements ILootCondition {

   public Always() {
   }

   public LootConditionType getType() {
      return LootConditionManager.ALWAYS;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootParameters.ORIGIN);
   }

   public static ILootCondition.IBuilder always() {
      return Always::new;
   }

   public boolean test(LootContext p_test_1_) {
      return true;
   }

   public static class Serializer implements ILootSerializer<Always> {
      public void serialize(JsonObject p_230424_1_, Always p_230424_2_, JsonSerializationContext p_230424_3_) {
      }

      public Always deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         return new Always();
      }
   }
}