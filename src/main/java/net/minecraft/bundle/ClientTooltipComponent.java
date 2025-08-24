package net.minecraft.bundle;

import net.minecraft.client.TooltipComponent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ClientTooltipComponent {
   static ClientTooltipComponent create(IReorderingProcessor p_169949_) {
      return new ClientTextTooltip(p_169949_);
   }

   static ClientTooltipComponent create(TooltipComponent p_169951_) {
      if (p_169951_ instanceof BundleTooltip) {
         return new ClientBundleTooltip((BundleTooltip)p_169951_);
      } else {
         throw new IllegalArgumentException("Unknown TooltipComponent");
      }
   }

   public int getHeight();

   public int getWidth(FontRenderer var1);

   default void renderText(FontRenderer p_169953_, int p_169954_, int p_169955_, Matrix4f p_253692_, IRenderTypeBuffer.Impl p_169957_) {
   }

   default void renderImage(FontRenderer p_194048_, int p_194049_, int p_194050_, ItemRenderer p_283459_) {
   }
}