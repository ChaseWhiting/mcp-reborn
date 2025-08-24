package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class EnderiophageRestoration extends AbstractCriterionTrigger<EnderiophageRestoration.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enderiophage_restoration");

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   @Override
   public EnderiophageRestoration.Instance createInstance(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionArrayParser) {
      return new EnderiophageRestoration.Instance(entityPredicate);
   }

   public void trigger(ServerPlayerEntity player) {
      this.trigger(player, instance -> true);
   }

   public static class Instance extends CriterionInstance {
      public Instance(EntityPredicate.AndPredicate player) {
         super(EnderiophageRestoration.ID, player);
      }

      @Override
      public JsonObject serializeToJson(ConditionArraySerializer serializer) {
         return super.serializeToJson(serializer); // nothing special, just basic serialization
      }
   }
}