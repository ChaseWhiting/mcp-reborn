package net.minecraft.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum DyeColor implements IStringSerializable {
   WHITE(0, "white", 16383998, MaterialColor.SNOW, 15790320, 16777215),
   ORANGE(1, "orange", 16351261, MaterialColor.COLOR_ORANGE, 15435844, 16738335),
   MAGENTA(2, "magenta", 13061821, MaterialColor.COLOR_MAGENTA, 12801229, 16711935),
   LIGHT_BLUE(3, "light_blue", 3847130, MaterialColor.COLOR_LIGHT_BLUE, 6719955, 10141901),
   YELLOW(4, "yellow", 16701501, MaterialColor.COLOR_YELLOW, 14602026, 16776960),
   LIME(5, "lime", 8439583, MaterialColor.COLOR_LIGHT_GREEN, 4312372, 12582656),
   PINK(6, "pink", 15961002, MaterialColor.COLOR_PINK, 14188952, 16738740),
   GRAY(7, "gray", 4673362, MaterialColor.COLOR_GRAY, 4408131, 8421504),
   LIGHT_GRAY(8, "light_gray", 10329495, MaterialColor.COLOR_LIGHT_GRAY, 11250603, 13882323),
   CYAN(9, "cyan", 1481884, MaterialColor.COLOR_CYAN, 2651799, 65535),
   PURPLE(10, "purple", 8991416, MaterialColor.COLOR_PURPLE, 8073150, 10494192),
   BLUE(11, "blue", 3949738, MaterialColor.COLOR_BLUE, 2437522, 255),
   BROWN(12, "brown", 8606770, MaterialColor.COLOR_BROWN, 5320730, 9127187),
   GREEN(13, "green", 6192150, MaterialColor.COLOR_GREEN, 3887386, 65280),
   RED(14, "red", 11546150, MaterialColor.COLOR_RED, 11743532, 16711680),
   BLACK(15, "black", 1908001, MaterialColor.COLOR_BLACK, 1973019, 0);


//   TURQUOISE(16, "turquoise", 4220606, MaterialColor.COLOR_CYAN, 4883792, 4780720),
//   TEAL(17, "teal", 32896, MaterialColor.COLOR_CYAN, 32896, 32896),
//   MINT(18, "mint", 8454016, MaterialColor.COLOR_LIGHT_GREEN, 8454016, 8454016),
//   PEACH(19, "peach", 16753920, MaterialColor.COLOR_PINK, 16753920, 16753920),
//   LAVENDER(20, "lavender", 15132410, MaterialColor.COLOR_PURPLE, 15132410, 15132410),
//   MAROON(21, "maroon", 8388608, MaterialColor.COLOR_RED, 8388608, 8388608),
//   OLIVE(22, "olive", 8421376, MaterialColor.COLOR_GREEN, 8421376, 8421376),
//   BRONZE(23, "bronze", 13382451, MaterialColor.COLOR_ORANGE, 13382451, 13382451),
//   NAVY(24, "navy", 128, MaterialColor.COLOR_BLUE, 128, 128),

   private static final DyeColor[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(DyeColor::getId)).toArray((p_199795_0_) -> {
      return new DyeColor[p_199795_0_];
   });
   private static final Int2ObjectOpenHashMap<DyeColor> BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap<>(Arrays.stream(values()).collect(Collectors.toMap((p_199793_0_) -> {
      return p_199793_0_.fireworkColor;
   }, (p_199794_0_) -> {
      return p_199794_0_;
   })));
   private final int id;
   private final String name;
   private final MaterialColor color;
   private final int textureDiffuseColor;
   private final int textureDiffuseColorBGR;
   private final float[] textureDiffuseColors;
   private final int fireworkColor;
   private final int textColor;

   private DyeColor(int p_i50049_3_, String p_i50049_4_, int p_i50049_5_, MaterialColor p_i50049_6_, int p_i50049_7_, int p_i50049_8_) {
      this.id = p_i50049_3_;
      this.name = p_i50049_4_;
      this.textureDiffuseColor = p_i50049_5_;
      this.color = p_i50049_6_;
      this.textColor = p_i50049_8_;
      int i = (p_i50049_5_ & 16711680) >> 16;
      int j = (p_i50049_5_ & '\uff00') >> 8;
      int k = (p_i50049_5_ & 255) >> 0;
      this.textureDiffuseColorBGR = k << 16 | j << 8 | i << 0;
      this.textureDiffuseColors = new float[]{(float)i / 255.0F, (float)j / 255.0F, (float)k / 255.0F};
      this.fireworkColor = p_i50049_7_;
   }

   public static DyeColor getRandomColor() {
      DyeColor[] shapes = DyeColor.values();
      int randomIndex = new Random().nextInt(shapes.length);
      return shapes[randomIndex];
   }


   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public float[] getTextureDiffuseColors() {
      return this.textureDiffuseColors;
   }

   public MaterialColor getMaterialColor() {
      return this.color;
   }

   public int getFireworkColor() {
      return this.fireworkColor;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTextColor() {
      return this.textColor;
   }

   public static DyeColor byId(int p_196056_0_) {
      if (p_196056_0_ < 0 || p_196056_0_ >= BY_ID.length) {
         p_196056_0_ = 0;
      }

      return BY_ID[p_196056_0_];
   }

   public static DyeColor byName(String p_204271_0_, DyeColor p_204271_1_) {
      for(DyeColor dyecolor : values()) {
         if (dyecolor.name.equals(p_204271_0_)) {
            return dyecolor;
         }
      }

      return p_204271_1_;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static DyeColor byFireworkColor(int p_196058_0_) {
      return BY_FIREWORK_COLOR.get(p_196058_0_);
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }
}