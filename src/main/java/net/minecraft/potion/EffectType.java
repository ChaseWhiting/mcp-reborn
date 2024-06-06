package net.minecraft.potion;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum EffectType {
   BENEFICIAL(TextFormatting.BLUE),
   HARMFUL(TextFormatting.RED),
   NEUTRAL(TextFormatting.BLUE);

   private final TextFormatting tooltipFormatting;

   private EffectType(TextFormatting p_i50390_3_) {
      this.tooltipFormatting = p_i50390_3_;
   }

   @OnlyIn(Dist.CLIENT)
   public TextFormatting getTooltipFormatting() {
      return this.tooltipFormatting;
   }
}