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
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedItemStack;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LootTable EMPTY = new LootTable(LootParameterSets.EMPTY, new LootPool[0], new ILootFunction[0]);
   public static final LootParameterSet DEFAULT_PARAM_SET = LootParameterSets.ALL_PARAMS;
   private final LootParameterSet paramSet;
   private final LootPool[] pools;
   private final ILootFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

   private LootTable(LootParameterSet p_i51265_1_, LootPool[] p_i51265_2_, ILootFunction[] p_i51265_3_) {
      this.paramSet = p_i51265_1_;
      this.pools = p_i51265_2_;
      this.functions = p_i51265_3_;
      this.compositeFunction = LootFunctionManager.compose(p_i51265_3_);
   }



   /**
    * Applies the loot functions to the item stack, based on the given context and functions.
    *
    * @param stack    The original item stack
    * @param context  The loot context to apply
    * @param functions The functions to apply
    * @return A modified item stack after applying all functions
    */
   private ItemStack applyLootFunctions(ItemStack stack, LootContext context, ILootFunction[] functions) {
      BiFunction<ItemStack, LootContext, ItemStack> compositeFunction = LootFunctionManager.compose(functions);
      return compositeFunction.apply(stack, context);
   }


   public static Consumer<ItemStack> createStackSplitter(Consumer<ItemStack> p_216124_0_) {
      return (p_216125_1_) -> {
         if (p_216125_1_.getCount() < p_216125_1_.getMaxStackSize()) {
            p_216124_0_.accept(p_216125_1_);
         } else {
            int i = p_216125_1_.getCount();

            while(i > 0) {
               ItemStack itemstack = p_216125_1_.copy();
               itemstack.setCount(Math.min(p_216125_1_.getMaxStackSize(), i));
               i -= itemstack.getCount();
               p_216124_0_.accept(itemstack);
            }
         }

      };
   }

   public void getRandomItemsRaw(LootContext p_216114_1_, Consumer<ItemStack> p_216114_2_) {
      if (p_216114_1_.addVisitedTable(this)) {
         Consumer<ItemStack> consumer = ILootFunction.decorate(this.compositeFunction, p_216114_2_, p_216114_1_);

         for(LootPool lootpool : this.pools) {
            lootpool.addRandomItems(consumer, p_216114_1_);
         }

         p_216114_1_.removeVisitedTable(this);
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
      }

   }

   public void getRandomItems(LootContext p_216120_1_, Consumer<ItemStack> p_216120_2_) {
      this.getRandomItemsRaw(p_216120_1_, createStackSplitter(p_216120_2_));
   }

   public List<ItemStack> getRandomItems(LootContext p_216113_1_) {
      List<ItemStack> list = Lists.newArrayList();
      this.getRandomItems(p_216113_1_, list::add);
      return list;
   }

   public LootParameterSet getParamSet() {
      return this.paramSet;
   }

   public void validate(ValidationTracker p_227506_1_) {
      for(int i = 0; i < this.pools.length; ++i) {
         this.pools[i].validate(p_227506_1_.forChild(".pools[" + i + "]"));
      }

      for(int j = 0; j < this.functions.length; ++j) {
         this.functions[j].validate(p_227506_1_.forChild(".functions[" + j + "]"));
      }

   }

   public void fill(IInventory p_216118_1_, LootContext p_216118_2_) {
      List<ItemStack> list = this.getRandomItems(p_216118_2_);
      Random random = p_216118_2_.getRandom();
      List<Integer> list1 = this.getAvailableSlots(p_216118_1_, random);
      this.shuffleAndSplitItems(list, list1.size(), random);

      for(ItemStack itemstack : list) {
         if (list1.isEmpty()) {
            LOGGER.warn("Tried to over-fill a container");
            return;
         }

         if (itemstack.isEmpty()) {
            p_216118_1_.setItem(list1.remove(list1.size() - 1), ItemStack.EMPTY);
         } else {
            p_216118_1_.setItem(list1.remove(list1.size() - 1), itemstack);
         }
      }

   }

   private void shuffleAndSplitItems(List<ItemStack> p_186463_1_, int p_186463_2_, Random p_186463_3_) {
      List<ItemStack> list = Lists.newArrayList();
      Iterator<ItemStack> iterator = p_186463_1_.iterator();

      while(iterator.hasNext()) {
         ItemStack itemstack = iterator.next();
         if (itemstack.isEmpty()) {
            iterator.remove();
         } else if (itemstack.getCount() > 1) {
            list.add(itemstack);
            iterator.remove();
         }
      }

      while(p_186463_2_ - p_186463_1_.size() - list.size() > 0 && !list.isEmpty()) {
         ItemStack itemstack2 = list.remove(MathHelper.nextInt(p_186463_3_, 0, list.size() - 1));
         int i = MathHelper.nextInt(p_186463_3_, 1, itemstack2.getCount() / 2);
         ItemStack itemstack1 = itemstack2.split(i);
         if (itemstack2.getCount() > 1 && p_186463_3_.nextBoolean()) {
            list.add(itemstack2);
         } else {
            p_186463_1_.add(itemstack2);
         }

         if (itemstack1.getCount() > 1 && p_186463_3_.nextBoolean()) {
            list.add(itemstack1);
         } else {
            p_186463_1_.add(itemstack1);
         }
      }

      p_186463_1_.addAll(list);
      Collections.shuffle(p_186463_1_, p_186463_3_);
   }

   private List<Integer> getAvailableSlots(IInventory p_186459_1_, Random p_186459_2_) {
      List<Integer> list = Lists.newArrayList();

      for(int i = 0; i < p_186459_1_.getContainerSize(); ++i) {
         if (p_186459_1_.getItem(i).isEmpty()) {
            list.add(i);
         }
      }

      Collections.shuffle(list, p_186459_2_);
      return list;
   }

   public static LootTable.Builder lootTable() {
      return new LootTable.Builder();
   }

   public static class Builder implements ILootFunctionConsumer<LootTable.Builder> {
      private final List<LootPool> pools = Lists.newArrayList();
      private final List<ILootFunction> functions = Lists.newArrayList();
      private LootParameterSet paramSet = LootTable.DEFAULT_PARAM_SET;

      public LootTable.Builder withPool(LootPool.Builder p_216040_1_) {
         this.pools.add(p_216040_1_.build());
         return this;
      }

      public LootTable.Builder setParamSet(LootParameterSet p_216039_1_) {
         this.paramSet = p_216039_1_;
         return this;
      }

      public LootTable.Builder apply(ILootFunction.IBuilder p_212841_1_) {
         this.functions.add(p_212841_1_.build());
         return this;
      }

      public LootTable.Builder unwrap() {
         return this;
      }

      public LootTable build() {
         return new LootTable(this.paramSet, this.pools.toArray(new LootPool[0]), this.functions.toArray(new ILootFunction[0]));
      }
   }

   public static class Serializer implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {
      public LootTable deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_deserialize_1_, "loot table");
         LootPool[] alootpool = JSONUtils.getAsObject(jsonobject, "pools", new LootPool[0], p_deserialize_3_, LootPool[].class);
         LootParameterSet lootparameterset = null;
         if (jsonobject.has("type")) {
            String s = JSONUtils.getAsString(jsonobject, "type");
            lootparameterset = LootParameterSets.get(new ResourceLocation(s));
         }

         ILootFunction[] ailootfunction = JSONUtils.getAsObject(jsonobject, "functions", new ILootFunction[0], p_deserialize_3_, ILootFunction[].class);
         return new LootTable(lootparameterset != null ? lootparameterset : LootParameterSets.ALL_PARAMS, alootpool, ailootfunction);
      }

      public JsonElement serialize(LootTable p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         if (p_serialize_1_.paramSet != LootTable.DEFAULT_PARAM_SET) {
            ResourceLocation resourcelocation = LootParameterSets.getKey(p_serialize_1_.paramSet);
            if (resourcelocation != null) {
               jsonobject.addProperty("type", resourcelocation.toString());
            } else {
               LootTable.LOGGER.warn("Failed to find id for param set " + p_serialize_1_.paramSet);
            }
         }

         if (p_serialize_1_.pools.length > 0) {
            jsonobject.add("pools", p_serialize_3_.serialize(p_serialize_1_.pools));
         }

         if (!ArrayUtils.isEmpty((Object[])p_serialize_1_.functions)) {
            jsonobject.add("functions", p_serialize_3_.serialize(p_serialize_1_.functions));
         }

         return jsonobject;
      }
   }
}