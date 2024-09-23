package net.minecraft.util;

public class TickRangeConverter {
   public static RangedInteger rangeOfSeconds(int p_233037_0_, int p_233037_1_) {
      return new RangedInteger(secondsToTicks(p_233037_0_), secondsToTicks(p_233037_1_));
   }

   public static RangedInteger rangeOfTicks(int tickmin, int tickmax) {
      return new RangedInteger(tickmin, tickmax);
   }

   public static int secondsToTicks(float seconds) {
      return (int) (seconds * 20);
   }

   public static int secondsToTicks(int seconds) {
      return seconds * 20;
   }
}