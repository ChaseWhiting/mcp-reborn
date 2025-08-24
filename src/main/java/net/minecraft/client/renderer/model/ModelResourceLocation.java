package net.minecraft.client.renderer.model;

import java.util.Locale;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelResourceLocation extends ResourceLocation {
   private final String variant;

   protected ModelResourceLocation(String[] p_i48111_1_) {
      super(p_i48111_1_);
      this.variant = p_i48111_1_[2].toLowerCase(Locale.ROOT);
   }

   public ModelResourceLocation(String p_i46079_1_) {
      this(decompose(p_i46079_1_));
   }

   public ModelResourceLocation(ResourceLocation p_i46080_1_, String p_i46080_2_) {
      this(p_i46080_1_.toString(), p_i46080_2_);
   }

   public ModelResourceLocation(String string, String string2, String string3) {
      super(string, string2);
      this.variant = ModelResourceLocation.lowercaseVariant(string3);
   }

   private static String lowercaseVariant(String string) {
      return string.toLowerCase(Locale.ROOT);
   }


   public ModelResourceLocation(String p_i46081_1_, String p_i46081_2_) {
      this(decompose(p_i46081_1_ + '#' + p_i46081_2_));
   }

   public static ModelResourceLocation vanilla(String string, String string2) {
      return new ModelResourceLocation("minecraft", string, string2);
   }

   protected static String[] decompose(String p_177517_0_) {
      String[] astring = new String[]{null, p_177517_0_, ""};
      int i = p_177517_0_.indexOf(35);
      String s = p_177517_0_;
      if (i >= 0) {
         astring[2] = p_177517_0_.substring(i + 1, p_177517_0_.length());
         if (i > 1) {
            s = p_177517_0_.substring(0, i);
         }
      }

      System.arraycopy(ResourceLocation.decompose(s, ':'), 0, astring, 0, 2);
      return astring;
   }

   public String getVariant() {
      return this.variant;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof ModelResourceLocation && super.equals(p_equals_1_)) {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)p_equals_1_;
         return this.variant.equals(modelresourcelocation.variant);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 31 * super.hashCode() + this.variant.hashCode();
   }

   public String toString() {
      return super.toString() + '#' + this.variant;
   }
}