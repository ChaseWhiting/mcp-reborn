package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JSONBlendingMode {
   private static JSONBlendingMode lastApplied;
   private final int srcColorFactor;
   private final int srcAlphaFactor;
   private final int dstColorFactor;
   private final int dstAlphaFactor;
   private final int blendFunc;
   private final boolean separateBlend;
   private final boolean opaque;

   private JSONBlendingMode(boolean separateBlend, boolean isOpaque, int i, int i1, int i2, int i3, int i4) {
      this.separateBlend = separateBlend;
      this.srcColorFactor = i;
      this.dstColorFactor = i1;
      this.srcAlphaFactor = i2;
      this.dstAlphaFactor = i3;
      this.opaque = isOpaque;
      this.blendFunc = i4;
   }

   public JSONBlendingMode() {
      this(false, true, 1, 0, 1, 0, 32774);
   }

   public JSONBlendingMode(int i, int i1, int i2) {
      this(false, false, i, i1, i, i1, i2);
   }

   public JSONBlendingMode(int i, int i1, int i2, int i3, int i4) {
      this(true, false, i, i1, i2, i3, i4);
   }

   public void apply() {
      if (!this.equals(lastApplied)) {
         if (lastApplied == null || this.opaque != lastApplied.isOpaque()) {
            lastApplied = this;
            if (this.opaque) {
               RenderSystem.disableBlend();
               return;
            }

            RenderSystem.enableBlend();
         }

         RenderSystem.blendEquation(this.blendFunc);
         if (this.separateBlend) {
            RenderSystem.blendFuncSeparate(this.srcColorFactor, this.dstColorFactor, this.srcAlphaFactor, this.dstAlphaFactor);
         } else {
            RenderSystem.blendFunc(this.srcColorFactor, this.dstColorFactor);
         }

      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof JSONBlendingMode)) {
         return false;
      } else {
         JSONBlendingMode jsonblendingmode = (JSONBlendingMode)p_equals_1_;
         if (this.blendFunc != jsonblendingmode.blendFunc) {
            return false;
         } else if (this.dstAlphaFactor != jsonblendingmode.dstAlphaFactor) {
            return false;
         } else if (this.dstColorFactor != jsonblendingmode.dstColorFactor) {
            return false;
         } else if (this.opaque != jsonblendingmode.opaque) {
            return false;
         } else if (this.separateBlend != jsonblendingmode.separateBlend) {
            return false;
         } else if (this.srcAlphaFactor != jsonblendingmode.srcAlphaFactor) {
            return false;
         } else {
            return this.srcColorFactor == jsonblendingmode.srcColorFactor;
         }
      }
   }

   public int hashCode() {
      int i = this.srcColorFactor;
      i = 31 * i + this.srcAlphaFactor;
      i = 31 * i + this.dstColorFactor;
      i = 31 * i + this.dstAlphaFactor;
      i = 31 * i + this.blendFunc;
      i = 31 * i + (this.separateBlend ? 1 : 0);
      return 31 * i + (this.opaque ? 1 : 0);
   }

   public boolean isOpaque() {
      return this.opaque;
   }

   public static int stringToBlendFunc(String string) {
      String s = string.trim().toLowerCase(Locale.ROOT);
      if ("add".equals(s)) {
         return 32774;
      } else if ("subtract".equals(s)) {
         return 32778;
      } else if ("reversesubtract".equals(s)) {
         return 32779;
      } else if ("reverse_subtract".equals(s)) {
         return 32779;
      } else if ("min".equals(s)) {
         return 32775;
      } else {
         return "max".equals(s) ? '\u8008' : '\u8006';
      }
   }

   public static int stringToBlendFactor(String string) {
      String s = string.trim().toLowerCase(Locale.ROOT);
      s = s.replaceAll("_", "");
      s = s.replaceAll("one", "1");
      s = s.replaceAll("zero", "0");
      s = s.replaceAll("minus", "-");
      if ("0".equals(s)) {
         return 0;
      } else if ("1".equals(s)) {
         return 1;
      } else if ("srccolor".equals(s)) {
         return 768;
      } else if ("1-srccolor".equals(s)) {
         return 769;
      } else if ("dstcolor".equals(s)) {
         return 774;
      } else if ("1-dstcolor".equals(s)) {
         return 775;
      } else if ("srcalpha".equals(s)) {
         return 770;
      } else if ("1-srcalpha".equals(s)) {
         return 771;
      } else if ("dstalpha".equals(s)) {
         return 772;
      } else {
         return "1-dstalpha".equals(s) ? 773 : -1;
      }
   }
}