package net.minecraft.util;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.math.MathHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class WeightedItemStack {
   private final List<WeightedItem> weightedItems;
   private final Predicate<LootContext> condition;
   private final BiFunction<ItemStack, LootContext, ItemStack> function;
   private final RandomValueRange bonusRolls;
   private final int minRolls; // New field for minimum rolls
   private final int maxRolls; // New field for maximum rolls
   private boolean atLeastOne;

   public WeightedItemStack(List<WeightedItem> weightedItems, int minRolls, int maxRolls) {
      this(weightedItems, context -> true, (stack, context) -> stack, new RandomValueRange(0.0F, 0.0F), minRolls, maxRolls);
   }

   public WeightedItemStack(List<WeightedItem> weightedItems, Predicate<LootContext> condition,
                            BiFunction<ItemStack, LootContext, ItemStack> function, RandomValueRange bonusRolls, int minRolls, int maxRolls) {
      this.weightedItems = weightedItems;
      this.condition = condition;
      this.function = function;
      this.bonusRolls = bonusRolls;
      this.minRolls = minRolls;
      this.maxRolls = maxRolls;
   }

   public List<ItemStack> generateRandomItems(LootContext context) {
      if (!condition.test(context)) {
         return new ArrayList<>(); // Return empty if condition fails
      }

      List<ItemStack> generatedItems = new ArrayList<>();
      Random random = context.getRandom();

      // Calculate the overall number of rolls based on minRolls and maxRolls
      int totalRolls = MathHelper.floor(random.nextInt(maxRolls - minRolls + 1)) + minRolls;
      totalRolls += MathHelper.floor(bonusRolls.getFloat(random) * context.getLuck());

      for (int i = 0; i < totalRolls; i++) {
         // Pick a weighted item randomly based on its weight
         WeightedItem selectedItem = selectWeightedItem(random);

         // Get the number of items for the selected weighted item
         int itemRolls = MathHelper.floor(random.nextInt(selectedItem.getMaxRolls() - selectedItem.getMinRolls() + 1))
                 + selectedItem.getMinRolls();

         // Ensure the generated item count is within the bounds of the weighted item's min/max
         itemRolls = MathHelper.clamp(itemRolls, selectedItem.getMinRolls(), selectedItem.getMaxRolls());

         // Add this item to the list with the determined count
         ItemStack stack = selectedItem.getItemStack().copy();
         stack.setCount(itemRolls); // Set the correct item count based on itemRolls
         generatedItems.add(function.apply(stack, context));
      }

      return generatedItems;
   }

   // Helper function to select a random WeightedItem based on its weight
   private WeightedItem selectWeightedItem(Random random) {
      int totalWeight = 0;
      for (WeightedItem weightedItem : weightedItems) {
         totalWeight += weightedItem.getWeight();
      }

      int randomWeight = random.nextInt(totalWeight);
      int currentWeight = 0;

      for (WeightedItem weightedItem : weightedItems) {
         currentWeight += weightedItem.getWeight();
         if (randomWeight < currentWeight) {
            return weightedItem;
         }
      }

      return weightedItems.get(0); // Fallback, though this should never happen
   }

   public WeightedItemStack atLeastOne() {
      this.atLeastOne = true;
      return this;
   }

   public boolean needsAtLeastOne() {
      return atLeastOne;
   }

   public List<WeightedItem> getWeightedItems() {
      return weightedItems;
   }

   public static class WeightedItem {
      private final ItemStack itemStack;
      private final int weight;
      private final int minRolls;
      private final int maxRolls;

      public WeightedItem(ItemStack itemStack, int weight, int minRolls, int maxRolls) {
         this.itemStack = itemStack;
         itemStack.setCount(new RandomValueRange(minRolls, maxRolls).getInt(new Random()));
         this.weight = weight;
         this.minRolls = minRolls;
         this.maxRolls = maxRolls;
      }

      public ItemStack getItemStack() {
         return itemStack;
      }

      public int getWeight() {
         return weight;
      }

      public int getMinRolls() {
         return minRolls;
      }

      public int getMaxRolls() {
         return maxRolls;
      }
   }

   public static class Builder {
      private List<WeightedItem> weightedItems = new ArrayList<>();
      private Predicate<LootContext> condition = context -> true;
      private BiFunction<ItemStack, LootContext, ItemStack> function = (stack, context) -> stack;
      private RandomValueRange bonusRolls = new RandomValueRange(0.0F, 0.0F);
      private int minRolls = 0; // Default min rolls
      private int maxRolls = 1; // Default max rolls

      public Builder addWeightedItem(ItemStack itemStack, int weight, int minRolls, int maxRolls) {
         this.weightedItems.add(new WeightedItem(itemStack, weight, minRolls, maxRolls));
         return this;
      }

      public Builder addWeightedItem(ItemStack itemStack, int weight, int maxRolls) {
         return addWeightedItem(itemStack, weight, 0, maxRolls);
      }

      public Builder withCondition(Predicate<LootContext> condition) {
         this.condition = condition;
         return this;
      }

      public Builder withFunction(BiFunction<ItemStack, LootContext, ItemStack> function) {
         this.function = function;
         return this;
      }

      public Builder setBonusRolls(RandomValueRange bonusRolls) {
         this.bonusRolls = bonusRolls;
         return this;
      }

      public Builder setMinRolls(int minRolls) {
         this.minRolls = minRolls;
         return this;
      }

      public Builder setRolls(int minRolls, int maxRolls) {
         this.minRolls = minRolls;
         this.maxRolls = maxRolls;
         return this;
      }

      public Builder setMaxRolls(int maxRolls) {
         this.maxRolls = maxRolls;
         return this;
      }

      public WeightedItemStack build() {
         if (maxRolls < minRolls) {
            throw new IllegalStateException("maxRolls must be above minRolls!");
         }

         return new WeightedItemStack(weightedItems, condition, function, bonusRolls, minRolls, maxRolls);
      }
   }

   public static class RandomValueRange {
      private final float min;
      private final float max;

      public RandomValueRange(float min) {
         this(min, min);
      }

      public RandomValueRange(float min, float max) {
         this.min = min;
         this.max = max;
      }

      public int getInt(Random random) {
         return MathHelper.floor(random.nextFloat() * (max - min + 1) + min);
      }

      public float getFloat(Random random) {
         return random.nextFloat() * (max - min) + min;
      }
   }
}
