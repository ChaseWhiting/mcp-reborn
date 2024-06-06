package net.minecraft.item;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HorseArmorItem extends Item {
   private final int protection;
   private final String texture;

   public HorseArmorItem(int p_i50042_1_, String p_i50042_2_, Item.Properties p_i50042_3_) {
      super(p_i50042_3_);
      this.protection = p_i50042_1_;
      this.texture = "textures/entity/horse/armor/horse_armor_" + p_i50042_2_ + ".png";
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getTexture() {
      return new ResourceLocation(this.texture);
   }

   public int getProtection() {
      return this.protection;
   }
}