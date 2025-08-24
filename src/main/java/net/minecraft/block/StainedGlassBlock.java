package net.minecraft.block;

import net.minecraft.item.DyeColor;
import net.minecraft.item.dyeable.IDyeableBlock;

public class StainedGlassBlock extends AbstractGlassBlock implements IBeaconBeamColorProvider, IDyeableBlock {
   private final DyeColor color;

   public StainedGlassBlock(DyeColor p_i48323_1_, AbstractBlock.Properties p_i48323_2_) {
      super(p_i48323_2_);
      this.color = p_i48323_1_;
   }

   public DyeColor getColor() {
      return this.color;
   }


   @Override
   public Block getBlock() {
      return this;
   }

   @Override
   public String getBlockPrefix() {
      return "stained_glass";
   }
}