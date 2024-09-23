package net.minecraft.util;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class RandomValueRange {
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

      public static int getInt(Random random, int min, int max) {
         RandomValueRange randomValueRange = new RandomValueRange(min,max);
         return randomValueRange.getInt(random);
      }

   public static int getInt(Random random, int max) {
      return getInt(random, 0, max);
   }

      public float getFloat(Random random) {
         return random.nextFloat() * (max - min) + min;
      }

}
