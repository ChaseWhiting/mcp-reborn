package net.minecraft.entity.ai.attributes;

import net.minecraft.util.math.MathHelper;

public class RangedAttribute extends Attribute {
   private final double minValue;
   private final double maxValue;

   public RangedAttribute(String name, double defaultValue, double minValue, double maxValue) {
      super(name, defaultValue);
      this.minValue = minValue;
      this.maxValue = maxValue;
      if (minValue > maxValue) {
         throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
      } else if (defaultValue < minValue) {
         throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
      } else if (defaultValue > maxValue) {
         throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
      }
   }

   public double sanitizeValue(double p_111109_1_) {
      return MathHelper.clamp(p_111109_1_, this.minValue, this.maxValue);
   }
}