package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
   private final LootEntry[] entries;
   private final ILootCondition[] conditions;
   final Predicate<LootContext> compositeCondition;
   final ILootFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
   final IRandomRange rolls;
   final RandomValueRange bonusRolls;

   private LootPool(LootEntry[] p_i51268_1_, ILootCondition[] p_i51268_2_, ILootFunction[] p_i51268_3_, IRandomRange p_i51268_4_, RandomValueRange p_i51268_5_) {
      this.entries = p_i51268_1_;
      this.conditions = p_i51268_2_;
      this.compositeCondition = LootConditionManager.andConditions(p_i51268_2_);
      this.functions = p_i51268_3_;
      this.compositeFunction = LootFunctionManager.compose(p_i51268_3_);
      this.rolls = p_i51268_4_;
      this.bonusRolls = p_i51268_5_;
   }

   private void addRandomItem(Consumer<ItemStack> p_216095_1_, LootContext p_216095_2_) {
      Random random = p_216095_2_.getRandom();
      List<ILootGenerator> list = Lists.newArrayList();
      MutableInt mutableint = new MutableInt();

      for(LootEntry lootentry : this.entries) {
         lootentry.expand(p_216095_2_, (p_216097_3_) -> {
            int k = p_216097_3_.getWeight(p_216095_2_.getLuck());
            if (k > 0) {
               list.add(p_216097_3_);
               mutableint.add(k);
            }

         });
      }

      int i = list.size();
      if (mutableint.intValue() != 0 && i != 0) {
         if (i == 1) {
            list.get(0).createItemStack(p_216095_1_, p_216095_2_);
         } else {
            int j = random.nextInt(mutableint.intValue());

            for(ILootGenerator ilootgenerator : list) {
               j -= ilootgenerator.getWeight(p_216095_2_.getLuck());
               if (j < 0) {
                  ilootgenerator.createItemStack(p_216095_1_, p_216095_2_);
                  return;
               }
            }

         }
      }
   }

   public void addRandomItems(Consumer<ItemStack> p_216091_1_, LootContext p_216091_2_) {
      if (this.compositeCondition.test(p_216091_2_)) {
         Consumer<ItemStack> consumer = ILootFunction.decorate(this.compositeFunction, p_216091_1_, p_216091_2_);
         Random random = p_216091_2_.getRandom();
         int i = this.rolls.getInt(random) + MathHelper.floor(this.bonusRolls.getFloat(random) * p_216091_2_.getLuck());

         for(int j = 0; j < i; ++j) {
            this.addRandomItem(consumer, p_216091_2_);
         }

      }
   }

   public void validate(ValidationTracker p_227505_1_) {
      for(int i = 0; i < this.conditions.length; ++i) {
         this.conditions[i].validate(p_227505_1_.forChild(".condition[" + i + "]"));
      }

      for(int j = 0; j < this.functions.length; ++j) {
         this.functions[j].validate(p_227505_1_.forChild(".functions[" + j + "]"));
      }

      for(int k = 0; k < this.entries.length; ++k) {
         this.entries[k].validate(p_227505_1_.forChild(".entries[" + k + "]"));
      }

   }

   public static LootPool.Builder lootPool() {
      return new LootPool.Builder();
   }

   public static class Builder implements ILootFunctionConsumer<LootPool.Builder>, ILootConditionConsumer<LootPool.Builder> {
      private final List<LootEntry> entries = Lists.newArrayList();
      private final List<ILootCondition> conditions = Lists.newArrayList();
      private final List<ILootFunction> functions = Lists.newArrayList();
      private IRandomRange rolls = new RandomValueRange(1.0F);
      private RandomValueRange bonusRolls = new RandomValueRange(0.0F, 0.0F);

      public LootPool.Builder setRolls(IRandomRange p_216046_1_) {
         this.rolls = p_216046_1_;
         return this;
      }

      public LootPool.Builder unwrap() {
         return this;
      }

      public LootPool.Builder add(LootEntry.Builder<?> p_216045_1_) {
         this.entries.add(p_216045_1_.build());
         return this;
      }

      public LootPool.Builder when(ILootCondition.IBuilder p_212840_1_) {
         this.conditions.add(p_212840_1_.build());
         return this;
      }

      public LootPool.Builder apply(ILootFunction.IBuilder p_212841_1_) {
         this.functions.add(p_212841_1_.build());
         return this;
      }

      public LootPool build() {
         if (this.rolls == null) {
            throw new IllegalArgumentException("Rolls not set");
         } else {
            return new LootPool(this.entries.toArray(new LootEntry[0]), this.conditions.toArray(new ILootCondition[0]), this.functions.toArray(new ILootFunction[0]), this.rolls, this.bonusRolls);
         }
      }
   }

   public static class Serializer implements JsonDeserializer<LootPool>, JsonSerializer<LootPool> {
      public LootPool deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_deserialize_1_, "loot pool");
         LootEntry[] alootentry = JSONUtils.getAsObject(jsonobject, "entries", p_deserialize_3_, LootEntry[].class);
         ILootCondition[] ailootcondition = JSONUtils.getAsObject(jsonobject, "conditions", new ILootCondition[0], p_deserialize_3_, ILootCondition[].class);
         ILootFunction[] ailootfunction = JSONUtils.getAsObject(jsonobject, "functions", new ILootFunction[0], p_deserialize_3_, ILootFunction[].class);
         IRandomRange irandomrange = RandomRanges.deserialize(jsonobject.get("rolls"), p_deserialize_3_);
         RandomValueRange randomvaluerange = JSONUtils.getAsObject(jsonobject, "bonus_rolls", new RandomValueRange(0.0F, 0.0F), p_deserialize_3_, RandomValueRange.class);
         return new LootPool(alootentry, ailootcondition, ailootfunction, irandomrange, randomvaluerange);
      }

      public JsonElement serialize(LootPool p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("rolls", RandomRanges.serialize(p_serialize_1_.rolls, p_serialize_3_));
         jsonobject.add("entries", p_serialize_3_.serialize(p_serialize_1_.entries));
         if (p_serialize_1_.bonusRolls.getMin() != 0.0F && p_serialize_1_.bonusRolls.getMax() != 0.0F) {
            jsonobject.add("bonus_rolls", p_serialize_3_.serialize(p_serialize_1_.bonusRolls));
         }

         if (!ArrayUtils.isEmpty((Object[])p_serialize_1_.conditions)) {
            jsonobject.add("conditions", p_serialize_3_.serialize(p_serialize_1_.conditions));
         }

         if (!ArrayUtils.isEmpty((Object[])p_serialize_1_.functions)) {
            jsonobject.add("functions", p_serialize_3_.serialize(p_serialize_1_.functions));
         }

         return jsonobject;
      }
   }
}