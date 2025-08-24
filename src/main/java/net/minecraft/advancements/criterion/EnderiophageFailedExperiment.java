package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class EnderiophageFailedExperiment extends AbstractCriterionTrigger<EnderiophageFailedExperiment.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("watch_enderiophage_breed_failed");

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   @Override
   public EnderiophageFailedExperiment.Instance createInstance(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionArrayParser) {
      return new EnderiophageFailedExperiment.Instance(entityPredicate);
   }

   public void trigger(ServerPlayerEntity player) {
      this.trigger(player, instance -> true);
   }

   public static class Instance extends CriterionInstance {
      public Instance(EntityPredicate.AndPredicate player) {
         super(EnderiophageFailedExperiment.ID, player);
      }

      @Override
      public JsonObject serializeToJson(ConditionArraySerializer serializer) {
         return super.serializeToJson(serializer); // nothing special, just basic serialization
      }
   }
}