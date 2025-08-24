package net.minecraft.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.codec.ByteBufCodecs;
import net.minecraft.util.codec.StreamCodec;
import net.minecraft.util.text.TextFormatting;

import java.util.function.IntFunction;

public enum Rarity implements IStringSerializable {
   COMMON(0, "common", TextFormatting.WHITE),
   UNCOMMON(1, "uncommon", TextFormatting.YELLOW),
   RARE(2, "rare", TextFormatting.AQUA),
   EPIC(3, "epic", TextFormatting.LIGHT_PURPLE),
   RED(4,"red",TextFormatting.DARK_RED),
   LEGENDARY(5,"legendary",TextFormatting.GOLD),
   DEMONIC(6,"demonic",TextFormatting.DARK_RED),
   WHITEE(7,"white",TextFormatting.WHITEE),        // White ( #FFFFFF )
   BLUE(8,"blue",TextFormatting.LIGHT_BLUE), // Blue ( #9696FF )
   GREEN(9,"green",TextFormatting.LIGHT_GREEN), // Green ( #96FF96 )
   ORANGEE(10,"orange",TextFormatting.ORANGEE),      // Orange ( #FFC896 )
   LIGHT_RED(11,"light_red",TextFormatting.LIGHT_RED),   // Light Red ( #FF9696 )
   PINK(12,"pink",TextFormatting.PINK),            // Pink ( #FF96FF )
   LIGHT_PURPLEE(13,"light_purple",TextFormatting.LIGHT_PURPLEE), // Light Purple ( #D2A0FF )
   LIME(14,"lime",TextFormatting.LIME),            // Lime ( #96FF0A )
   YELLOWW(15,"yellow",TextFormatting.YELLOWW),       // Yellow ( #FFFF0A )
   CYAN(16,"cyan",TextFormatting.CYAN),            // Cyan ( #05C8FF )
   REDD(17,"red2",TextFormatting.REDD),            // Red ( #FF2864 )
   PURPLEE(18,"purple",TextFormatting.PURPLEE);      // Purple ( #B428FF )

   private final int id;
   private final String name;
   public final TextFormatting color;

   private Rarity(int id, String name, TextFormatting p_i48837_3_) {
      this.color = p_i48837_3_;
      this.id = id;
      this.name = name;
   }


   public static final Codec<Rarity> CODEC;
   public static final IntFunction<Rarity> BY_ID;
   public static final StreamCodec<ByteBuf, Rarity> STREAM_CODEC;

   @Override
   public String getSerializedName() {
      return this.name;
   }

   static {
      CODEC = IStringSerializable.fromEnum(Rarity::values);
      BY_ID = ByIdMap.continuous(rarity -> rarity.id, Rarity.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, rarity -> rarity.id);
   }
}
