package net.minecraft.item;

import net.minecraft.util.text.TextFormatting;

public enum Rarity {
   COMMON(TextFormatting.WHITE),
   UNCOMMON(TextFormatting.YELLOW),
   RARE(TextFormatting.AQUA),
   EPIC(TextFormatting.LIGHT_PURPLE),
   RED(TextFormatting.DARK_RED),
   LEGENDARY(TextFormatting.GOLD),

   // New rarities with custom colors
   WHITEE(TextFormatting.WHITEE),        // White ( #FFFFFF )
   BLUE(TextFormatting.LIGHT_BLUE), // Blue ( #9696FF )
   GREEN(TextFormatting.LIGHT_GREEN), // Green ( #96FF96 )
   ORANGEE(TextFormatting.ORANGEE),      // Orange ( #FFC896 )
   LIGHT_RED(TextFormatting.LIGHT_RED),   // Light Red ( #FF9696 )
   PINK(TextFormatting.PINK),            // Pink ( #FF96FF )
   LIGHT_PURPLEE(TextFormatting.LIGHT_PURPLEE), // Light Purple ( #D2A0FF )
   LIME(TextFormatting.LIME),            // Lime ( #96FF0A )
   YELLOWW(TextFormatting.YELLOWW),       // Yellow ( #FFFF0A )
   CYAN(TextFormatting.CYAN),            // Cyan ( #05C8FF )
   REDD(TextFormatting.REDD),            // Red ( #FF2864 )
   PURPLEE(TextFormatting.PURPLEE);      // Purple ( #B428FF )

   public final TextFormatting color;

   private Rarity(TextFormatting p_i48837_3_) {
      this.color = p_i48837_3_;
   }
}
