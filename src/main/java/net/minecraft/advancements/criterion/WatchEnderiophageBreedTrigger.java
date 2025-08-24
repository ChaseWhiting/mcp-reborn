package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class WatchEnderiophageBreedTrigger extends AbstractCriterionTrigger<WatchEnderiophageBreedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("watch_enderiophage_breed");

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   @Override
   public WatchEnderiophageBreedTrigger.Instance createInstance(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionArrayParser) {
      return new WatchEnderiophageBreedTrigger.Instance(entityPredicate);
   }

   public void trigger(ServerPlayerEntity player) {
      this.trigger(player, instance -> true);
   }

   public static class Instance extends CriterionInstance {
      public Instance(EntityPredicate.AndPredicate player) {
         super(WatchEnderiophageBreedTrigger.ID, player);
      }

      @Override
      public JsonObject serializeToJson(ConditionArraySerializer serializer) {
         return super.serializeToJson(serializer); // nothing special, just basic serialization
      }
   }
}