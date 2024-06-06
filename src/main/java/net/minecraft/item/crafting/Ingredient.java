package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class Ingredient implements Predicate<ItemStack> {
   public static final Ingredient EMPTY = new Ingredient(Stream.empty());
   private final Ingredient.IItemList[] values;
   private ItemStack[] itemStacks;
   private IntList stackingIds;

   private Ingredient(Stream<? extends Ingredient.IItemList> p_i49381_1_) {
      this.values = p_i49381_1_.toArray((p_209360_0_) -> {
         return new Ingredient.IItemList[p_209360_0_];
      });
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack[] getItems() {
      this.dissolve();
      return this.itemStacks;
   }

   private void dissolve() {
      if (this.itemStacks == null) {
         this.itemStacks = Arrays.stream(this.values).flatMap((p_209359_0_) -> {
            return p_209359_0_.getItems().stream();
         }).distinct().toArray((p_209358_0_) -> {
            return new ItemStack[p_209358_0_];
         });
      }

   }

   public boolean test(@Nullable ItemStack p_test_1_) {
      if (p_test_1_ == null) {
         return false;
      } else {
         this.dissolve();
         if (this.itemStacks.length == 0) {
            return p_test_1_.isEmpty();
         } else {
            for(ItemStack itemstack : this.itemStacks) {
               if (itemstack.getItem() == p_test_1_.getItem()) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public IntList getStackingIds() {
      if (this.stackingIds == null) {
         this.dissolve();
         this.stackingIds = new IntArrayList(this.itemStacks.length);

         for(ItemStack itemstack : this.itemStacks) {
            this.stackingIds.add(RecipeItemHelper.getStackingIndex(itemstack));
         }

         this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
      }

      return this.stackingIds;
   }

   public void toNetwork(PacketBuffer p_199564_1_) {
      this.dissolve();
      p_199564_1_.writeVarInt(this.itemStacks.length);

      for(int i = 0; i < this.itemStacks.length; ++i) {
         p_199564_1_.writeItem(this.itemStacks[i]);
      }

   }

   public JsonElement toJson() {
      if (this.values.length == 1) {
         return this.values[0].serialize();
      } else {
         JsonArray jsonarray = new JsonArray();

         for(Ingredient.IItemList ingredient$iitemlist : this.values) {
            jsonarray.add(ingredient$iitemlist.serialize());
         }

         return jsonarray;
      }
   }

   public boolean isEmpty() {
      return this.values.length == 0 && (this.itemStacks == null || this.itemStacks.length == 0) && (this.stackingIds == null || this.stackingIds.isEmpty());
   }

   private static Ingredient fromValues(Stream<? extends Ingredient.IItemList> p_209357_0_) {
      Ingredient ingredient = new Ingredient(p_209357_0_);
      return ingredient.values.length == 0 ? EMPTY : ingredient;
   }

   public static Ingredient of(IItemProvider... p_199804_0_) {
      return of(Arrays.stream(p_199804_0_).map(ItemStack::new));
   }

   @OnlyIn(Dist.CLIENT)
   public static Ingredient of(ItemStack... p_193369_0_) {
      return of(Arrays.stream(p_193369_0_));
   }

   public static Ingredient of(Stream<ItemStack> p_234819_0_) {
      return fromValues(p_234819_0_.filter((p_234824_0_) -> {
         return !p_234824_0_.isEmpty();
      }).map((p_209356_0_) -> {
         return new Ingredient.SingleItemList(p_209356_0_);
      }));
   }

   public static Ingredient of(ITag<Item> p_199805_0_) {
      return fromValues(Stream.of(new Ingredient.TagList(p_199805_0_)));
   }

   public static Ingredient fromNetwork(PacketBuffer p_199566_0_) {
      int i = p_199566_0_.readVarInt();
      return fromValues(Stream.generate(() -> {
         return new Ingredient.SingleItemList(p_199566_0_.readItem());
      }).limit((long)i));
   }

   public static Ingredient fromJson(@Nullable JsonElement p_199802_0_) {
      if (p_199802_0_ != null && !p_199802_0_.isJsonNull()) {
         if (p_199802_0_.isJsonObject()) {
            return fromValues(Stream.of(valueFromJson(p_199802_0_.getAsJsonObject())));
         } else if (p_199802_0_.isJsonArray()) {
            JsonArray jsonarray = p_199802_0_.getAsJsonArray();
            if (jsonarray.size() == 0) {
               throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            } else {
               return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map((p_209355_0_) -> {
                  return valueFromJson(JSONUtils.convertToJsonObject(p_209355_0_, "item"));
               }));
            }
         } else {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
         }
      } else {
         throw new JsonSyntaxException("Item cannot be null");
      }
   }

   private static Ingredient.IItemList valueFromJson(JsonObject p_199803_0_) {
      if (p_199803_0_.has("item") && p_199803_0_.has("tag")) {
         throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
      } else if (p_199803_0_.has("item")) {
         ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getAsString(p_199803_0_, "item"));
         Item item = Registry.ITEM.getOptional(resourcelocation1).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + resourcelocation1 + "'");
         });
         return new Ingredient.SingleItemList(new ItemStack(item));
      } else if (p_199803_0_.has("tag")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_199803_0_, "tag"));
         ITag<Item> itag = TagCollectionManager.getInstance().getItems().getTag(resourcelocation);
         if (itag == null) {
            throw new JsonSyntaxException("Unknown item tag '" + resourcelocation + "'");
         } else {
            return new Ingredient.TagList(itag);
         }
      } else {
         throw new JsonParseException("An ingredient entry needs either a tag or an item");
      }
   }

   interface IItemList {
      Collection<ItemStack> getItems();

      JsonObject serialize();
   }

   static class SingleItemList implements Ingredient.IItemList {
      private final ItemStack item;

      private SingleItemList(ItemStack p_i48195_1_) {
         this.item = p_i48195_1_;
      }

      public Collection<ItemStack> getItems() {
         return Collections.singleton(this.item);
      }

      public JsonObject serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("item", Registry.ITEM.getKey(this.item.getItem()).toString());
         return jsonobject;
      }
   }

   static class TagList implements Ingredient.IItemList {
      private final ITag<Item> tag;

      private TagList(ITag<Item> p_i48193_1_) {
         this.tag = p_i48193_1_;
      }

      public Collection<ItemStack> getItems() {
         List<ItemStack> list = Lists.newArrayList();

         for(Item item : this.tag.getValues()) {
            list.add(new ItemStack(item));
         }

         return list;
      }

      public JsonObject serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("tag", TagCollectionManager.getInstance().getItems().getIdOrThrow(this.tag).toString());
         return jsonobject;
      }
   }
}