package net.minecraft.bundle;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class ClientTextTooltip implements ClientTooltipComponent {
   private final IReorderingProcessor text;

   public ClientTextTooltip(IReorderingProcessor p_169938_) {
      this.text = p_169938_;
   }



   public int getHeight() {
      return 10;
   }

   @Override
   public int getWidth(FontRenderer font) {
      return font.width(this.text);
   }

   @Override
   public void renderText(FontRenderer font, int n, int n2, Matrix4f matrix4f, IRenderTypeBuffer.Impl bufferSource) {
      font.drawInBatch(this.text, (float)n, (float)n2, -1, true, matrix4f, (IRenderTypeBuffer) bufferSource, true, 0, 0xF000F0);
   }

}