package net.minecraft.client.renderer;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.bundle.BundleItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer implements IResourceManagerReloadListener {
   public static final ResourceLocation ENCHANT_GLINT_LOCATION = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final Set<Item> IGNORED = Sets.newHashSet(Items.AIR);
   public float blitOffset;
   private final ItemModelMesher itemModelShaper;
   private final TextureManager textureManager;
   private final ItemColors itemColors;

   public ItemRenderer(TextureManager p_i46552_1_, ModelManager p_i46552_2_, ItemColors p_i46552_3_) {
      this.textureManager = p_i46552_1_;
      this.itemModelShaper = new ItemModelMesher(p_i46552_2_);

      for(Item item : Registry.ITEM) {
         if (!IGNORED.contains(item)) {
            this.itemModelShaper.register(item, new ModelResourceLocation(Registry.ITEM.getKey(item), "inventory"));
         }
      }

      this.itemColors = p_i46552_3_;
   }

   public ItemModelMesher getItemModelShaper() {
      return this.itemModelShaper;
   }

   private void renderModelLists(IBakedModel p_229114_1_, ItemStack p_229114_2_, int p_229114_3_, int p_229114_4_, MatrixStack p_229114_5_, IVertexBuilder p_229114_6_) {
      Random random = new Random();
      long i = 42L;

      for(Direction direction : Direction.values()) {
         random.setSeed(42L);
         this.renderQuadList(p_229114_5_, p_229114_6_, p_229114_1_.getQuads((BlockState)null, direction, random), p_229114_2_, p_229114_3_, p_229114_4_);
      }

      random.setSeed(42L);
      this.renderQuadList(p_229114_5_, p_229114_6_, p_229114_1_.getQuads((BlockState)null, (Direction)null, random), p_229114_2_, p_229114_3_, p_229114_4_);
   }

   public void render(ItemStack p_229111_1_, ItemCameraTransforms.TransformType p_229111_2_, boolean p_229111_3_, MatrixStack p_229111_4_, IRenderTypeBuffer p_229111_5_, int p_229111_6_, int p_229111_7_, IBakedModel p_229111_8_) {
      if (!p_229111_1_.isEmpty()) {
         p_229111_4_.pushPose();
         boolean flag = p_229111_2_ == ItemCameraTransforms.TransformType.GUI || p_229111_2_ == ItemCameraTransforms.TransformType.GROUND || p_229111_2_ == ItemCameraTransforms.TransformType.FIXED;
         if (p_229111_1_.getItem() == Items.TRIDENT && flag) {
            p_229111_8_ = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
         }

         p_229111_8_.getTransforms().getTransform(p_229111_2_).apply(p_229111_3_, p_229111_4_);
         p_229111_4_.translate(-0.5D, -0.5D, -0.5D);
         if (!p_229111_8_.isCustomRenderer() && (p_229111_1_.getItem() != Items.TRIDENT || flag)) {
            boolean flag1;
            if (p_229111_2_ != ItemCameraTransforms.TransformType.GUI && !p_229111_2_.firstPerson() && p_229111_1_.getItem() instanceof BlockItem) {
               Block block = ((BlockItem)p_229111_1_.getItem()).getBlock();
               flag1 = !(block instanceof BreakableBlock) && !(block instanceof StainedGlassPaneBlock);
            } else {
               flag1 = true;
            }

            RenderType rendertype = RenderTypeLookup.getRenderType(p_229111_1_, flag1);
            IVertexBuilder ivertexbuilder;
            if (p_229111_1_.getItem() == Items.COMPASS && p_229111_1_.hasFoil()) {
               p_229111_4_.pushPose();
               MatrixStack.Entry matrixstack$entry = p_229111_4_.last();
               if (p_229111_2_ == ItemCameraTransforms.TransformType.GUI) {
                  matrixstack$entry.pose().multiply(0.5F);
               } else if (p_229111_2_.firstPerson()) {
                  matrixstack$entry.pose().multiply(0.75F);
               }

               if (flag1) {
                  ivertexbuilder = getCompassFoilBufferDirect(p_229111_5_, rendertype, matrixstack$entry);
               } else {
                  ivertexbuilder = getCompassFoilBuffer(p_229111_5_, rendertype, matrixstack$entry);
               }

               p_229111_4_.popPose();
            } else if (flag1) {
               ivertexbuilder = getFoilBufferDirect(p_229111_5_, rendertype, true, p_229111_1_.hasFoil());
            } else {
               ivertexbuilder = getFoilBuffer(p_229111_5_, rendertype, true, p_229111_1_.hasFoil());
            }

            this.renderModelLists(p_229111_8_, p_229111_1_, p_229111_6_, p_229111_7_, p_229111_4_, ivertexbuilder);
         } else {
            ItemStackTileEntityRenderer.instance.renderByItem(p_229111_1_, p_229111_2_, p_229111_4_, p_229111_5_, p_229111_6_, p_229111_7_);
         }

         p_229111_4_.popPose();
      }
   }

   public static IVertexBuilder getArmorFoilBuffer(IRenderTypeBuffer p_239386_0_, RenderType p_239386_1_, boolean p_239386_2_, boolean p_239386_3_) {
      return p_239386_3_ ? VertexBuilderUtils.create(p_239386_0_.getBuffer(p_239386_2_ ? RenderType.armorGlint() : RenderType.armorEntityGlint()), p_239386_0_.getBuffer(p_239386_1_)) : p_239386_0_.getBuffer(p_239386_1_);
   }

   public static IVertexBuilder getCompassFoilBuffer(IRenderTypeBuffer p_241731_0_, RenderType p_241731_1_, MatrixStack.Entry p_241731_2_) {
      return VertexBuilderUtils.create(new MatrixApplyingVertexBuilder(p_241731_0_.getBuffer(RenderType.glint()), p_241731_2_.pose(), p_241731_2_.normal()), p_241731_0_.getBuffer(p_241731_1_));
   }

   public static IVertexBuilder getCompassFoilBufferDirect(IRenderTypeBuffer p_241732_0_, RenderType p_241732_1_, MatrixStack.Entry p_241732_2_) {
      return VertexBuilderUtils.create(new MatrixApplyingVertexBuilder(p_241732_0_.getBuffer(RenderType.glintDirect()), p_241732_2_.pose(), p_241732_2_.normal()), p_241732_0_.getBuffer(p_241732_1_));
   }

   public static IVertexBuilder getFoilBuffer(IRenderTypeBuffer p_229113_0_, RenderType p_229113_1_, boolean p_229113_2_, boolean p_229113_3_) {
      if (p_229113_3_) {
         return Minecraft.useShaderTransparency() && p_229113_1_ == Atlases.translucentItemSheet() ? VertexBuilderUtils.create(p_229113_0_.getBuffer(RenderType.glintTranslucent()), p_229113_0_.getBuffer(p_229113_1_)) : VertexBuilderUtils.create(p_229113_0_.getBuffer(p_229113_2_ ? RenderType.glint() : RenderType.entityGlint()), p_229113_0_.getBuffer(p_229113_1_));
      } else {
         return p_229113_0_.getBuffer(p_229113_1_);
      }
   }

   public static IVertexBuilder getFoilBufferDirect(IRenderTypeBuffer p_239391_0_, RenderType p_239391_1_, boolean p_239391_2_, boolean p_239391_3_) {
      return p_239391_3_ ? VertexBuilderUtils.create(p_239391_0_.getBuffer(p_239391_2_ ? RenderType.glintDirect() : RenderType.entityGlintDirect()), p_239391_0_.getBuffer(p_239391_1_)) : p_239391_0_.getBuffer(p_239391_1_);
   }

   private void renderQuadList(MatrixStack p_229112_1_, IVertexBuilder p_229112_2_, List<BakedQuad> p_229112_3_, ItemStack p_229112_4_, int p_229112_5_, int p_229112_6_) {
      boolean flag = !p_229112_4_.isEmpty();
      MatrixStack.Entry matrixstack$entry = p_229112_1_.last();

      for(BakedQuad bakedquad : p_229112_3_) {
         int i = -1;
         if (flag && bakedquad.isTinted()) {
            i = this.itemColors.getColor(p_229112_4_, bakedquad.getTintIndex());
         }

         float f = (float)(i >> 16 & 255) / 255.0F;
         float f1 = (float)(i >> 8 & 255) / 255.0F;
         float f2 = (float)(i & 255) / 255.0F;
         p_229112_2_.putBulkData(matrixstack$entry, bakedquad, f, f1, f2, p_229112_5_, p_229112_6_);
      }

   }

   public IBakedModel getModel(ItemStack p_184393_1_, @Nullable World p_184393_2_, @Nullable LivingEntity p_184393_3_) {
      Item item = p_184393_1_.getItem();
      IBakedModel ibakedmodel;
      if (item == Items.TRIDENT) {
         ibakedmodel = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      } else {
         ibakedmodel = this.itemModelShaper.getItemModel(p_184393_1_);
      }

      ClientWorld clientworld = p_184393_2_ instanceof ClientWorld ? (ClientWorld)p_184393_2_ : null;
      IBakedModel ibakedmodel1 = ibakedmodel.getOverrides().resolve(ibakedmodel, p_184393_1_, clientworld, p_184393_3_);
      return ibakedmodel1 == null ? this.itemModelShaper.getModelManager().getMissingModel() : ibakedmodel1;
   }

   public void renderStatic(ItemStack p_229110_1_, ItemCameraTransforms.TransformType p_229110_2_, int p_229110_3_, int p_229110_4_, MatrixStack p_229110_5_, IRenderTypeBuffer p_229110_6_) {
      this.renderStatic((LivingEntity)null, p_229110_1_, p_229110_2_, false, p_229110_5_, p_229110_6_, (World)null, p_229110_3_, p_229110_4_);
   }

   public void renderStatic(@Nullable LivingEntity p_229109_1_, ItemStack p_229109_2_, ItemCameraTransforms.TransformType p_229109_3_, boolean p_229109_4_, MatrixStack p_229109_5_, IRenderTypeBuffer p_229109_6_, @Nullable World p_229109_7_, int p_229109_8_, int p_229109_9_) {
      if (!p_229109_2_.isEmpty()) {
         IBakedModel ibakedmodel = this.getModel(p_229109_2_, p_229109_7_, p_229109_1_);
         this.render(p_229109_2_, p_229109_3_, p_229109_4_, p_229109_5_, p_229109_6_, p_229109_8_, p_229109_9_, ibakedmodel);
      }
   }

   public void renderGuiItem(ItemStack p_175042_1_, int p_175042_2_, int p_175042_3_) {
      this.renderGuiItem(p_175042_1_, p_175042_2_, p_175042_3_, this.getModel(p_175042_1_, (World)null, (LivingEntity)null));
   }

   protected void renderGuiItem(ItemStack p_191962_1_, int p_191962_2_, int p_191962_3_, IBakedModel p_191962_4_) {
      RenderSystem.pushMatrix();
      this.textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
      this.textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS).setFilter(false, false);
      RenderSystem.enableRescaleNormal();
      RenderSystem.enableAlphaTest();
      RenderSystem.defaultAlphaFunc();
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.translatef((float)p_191962_2_, (float)p_191962_3_, 100.0F + this.blitOffset);
      RenderSystem.translatef(8.0F, 8.0F, 0.0F);
      RenderSystem.scalef(1.0F, -1.0F, 1.0F);
      RenderSystem.scalef(16.0F, 16.0F, 16.0F);
      MatrixStack matrixstack = new MatrixStack();
      IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
      boolean flag = !p_191962_4_.usesBlockLight();
      if (flag) {
         RenderHelper.setupForFlatItems();
      }

      this.render(p_191962_1_, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, p_191962_4_);
      irendertypebuffer$impl.endBatch();
      RenderSystem.enableDepthTest();
      if (flag) {
         RenderHelper.setupFor3DItems();
      }

      RenderSystem.disableAlphaTest();
      RenderSystem.disableRescaleNormal();
      RenderSystem.popMatrix();
   }


   public void renderAndDecorateItem(ItemStack p_180450_1_, int p_180450_2_, int p_180450_3_) {
      this.tryRenderGuiItem(Minecraft.getInstance().player, p_180450_1_, p_180450_2_, p_180450_3_);
   }

   public void renderAndDecorateFakeItem(ItemStack p_239390_1_, int p_239390_2_, int p_239390_3_) {
      this.tryRenderGuiItem((LivingEntity)null, p_239390_1_, p_239390_2_, p_239390_3_);
   }

   public void renderAndDecorateItem(LivingEntity p_184391_1_, ItemStack p_184391_2_, int p_184391_3_, int p_184391_4_) {
      this.tryRenderGuiItem(p_184391_1_, p_184391_2_, p_184391_3_, p_184391_4_);
   }

   private void tryRenderGuiItem(@Nullable LivingEntity p_239387_1_, ItemStack p_239387_2_, int p_239387_3_, int p_239387_4_) {
      if (!p_239387_2_.isEmpty()) {
         this.blitOffset += 50.0F;

         try {
            this.renderGuiItem(p_239387_2_, p_239387_3_, p_239387_4_, this.getModel(p_239387_2_, (World)null, p_239387_1_));
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
            crashreportcategory.setDetail("Item Type", () -> {
               return String.valueOf((Object)p_239387_2_.getItem());
            });
            crashreportcategory.setDetail("Item Damage", () -> {
               return String.valueOf(p_239387_2_.getDamageValue());
            });
            crashreportcategory.setDetail("Item NBT", () -> {
               return String.valueOf((Object)p_239387_2_.getTag());
            });
            crashreportcategory.setDetail("Item Foil", () -> {
               return String.valueOf(p_239387_2_.hasFoil());
            });
            throw new ReportedException(crashreport);
         }

         this.blitOffset -= 50.0F;
      }
   }

   public void renderGuiItemDecorations(FontRenderer p_175030_1_, ItemStack p_175030_2_, int p_175030_3_, int p_175030_4_) {
      this.renderGuiItemDecorations(p_175030_1_, p_175030_2_, p_175030_3_, p_175030_4_, (String)null);
   }

   public void renderGuiItemDecorations(FontRenderer p_180453_1_, ItemStack item, int p_180453_3_, int p_180453_4_, @Nullable String p_180453_5_) {
      if (!item.isEmpty()) {
         MatrixStack matrixstack = new MatrixStack();
         if (item.getCount() != 1 || p_180453_5_ != null) {
            String s = p_180453_5_ == null ? String.valueOf(item.getCount()) : p_180453_5_;
            matrixstack.translate(0.0D, 0.0D, (double)(this.blitOffset + 200.0F));
            IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
            p_180453_1_.drawInBatch(s, (float)(p_180453_3_ + 19 - 2 - p_180453_1_.width(s)), (float)(p_180453_4_ + 6 + 3), 16777215, true, matrixstack.last().pose(), irendertypebuffer$impl, false, 0, 15728880);
            irendertypebuffer$impl.endBatch();
         }


         if (item.isDamaged()) {
            // Disable rendering features
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();

            // Calculate the damage ratio
            float f = (float)item.getDamageValue();
            float f1 = (float)item.getMaxDamage();
            float f2 = Math.max(0.0F, (f1 - f) / f1);

            // Determine how much of the bar to fill
            int i = Math.round(13.0F - f * 13.0F / f1);

            // Calculate the color based on damage
            int j = MathHelper.hsvToRgb(f2 / 3.0F, 1.0F, 1.0F);

            // Render the damage bar
            this.fillRect(bufferbuilder, p_180453_3_ + 2, p_180453_4_ + 13, 13, 2, 0, 0, 0, 255);
            this.fillRect(bufferbuilder, p_180453_3_ + 2, p_180453_4_ + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);

            // Re-enable rendering features
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
         }

         if (item.getItem() instanceof BundleItem && ((BundleItem)item.getItem()).isBarVisible(item)) {
            BundleItem bundleItem = (BundleItem) item.getItem();
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();

            // Calculate the weight ratio
            float currentWeight = (float) BundleItem.getContentWeight(item);
            float maxWeight = (float) bundleItem.getMaxWeight(item);
            float weightRatio = Math.max(0.0F, currentWeight / maxWeight);

            // Determine how much of the bar to fill (left to right)
            int barWidth = Math.round(weightRatio * 13.0F);

            // Get the color for the bar
            int color = BundleItem.getBarColor();

            // Render the weight bar (left to right)
            this.fillRect(bufferbuilder, p_180453_3_ + 2, p_180453_4_ + 13, 13, 2, 0, 0, 0, 255); // background bar
            this.fillRect(bufferbuilder, p_180453_3_ + 2, p_180453_4_ + 13, barWidth, 1, color >> 16 & 255, color >> 8 & 255, color & 255, 255); // filled bar

            // Re-enable rendering features
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();



         }

         ClientPlayerEntity clientplayerentity = Minecraft.getInstance().player;
         float f3 = clientplayerentity == null ? 0.0F : clientplayerentity.getCooldowns().getCooldownPercent(item.getItem(), Minecraft.getInstance().getFrameTime());
         if (f3 > 0.0F) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tessellator tessellator1 = Tessellator.getInstance();
            BufferBuilder bufferbuilder1 = tessellator1.getBuilder();
            this.fillRect(bufferbuilder1, p_180453_3_, p_180453_4_ + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
         }

      }
   }

   private void fillRect(BufferBuilder p_181565_1_, int p_181565_2_, int p_181565_3_, int p_181565_4_, int p_181565_5_, int p_181565_6_, int p_181565_7_, int p_181565_8_, int p_181565_9_) {
      p_181565_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
      p_181565_1_.vertex((double)(p_181565_2_ + 0), (double)(p_181565_3_ + 0), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      p_181565_1_.vertex((double)(p_181565_2_ + 0), (double)(p_181565_3_ + p_181565_5_), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      p_181565_1_.vertex((double)(p_181565_2_ + p_181565_4_), (double)(p_181565_3_ + p_181565_5_), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      p_181565_1_.vertex((double)(p_181565_2_ + p_181565_4_), (double)(p_181565_3_ + 0), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      Tessellator.getInstance().end();
   }


   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.itemModelShaper.rebuildCache();
   }








//   public void blit(int p_282225_, int p_281487_, int p_281985_, int p_281329_, int p_283035_, TextureAtlasSprite p_281614_) {
//      this.blitSprite(p_281614_, p_282225_, p_281487_, p_281985_, p_281329_, p_283035_);
//   }
//
//   public void blit(int p_282416_, int p_282989_, int p_282618_, int p_282755_, int p_281717_, TextureAtlasSprite p_281874_, float p_283559_, float p_282730_, float p_283530_, float p_282246_) {
//      this.innerBlit(p_281874_.atlasLocation(), p_282416_, p_282416_ + p_282755_, p_282989_, p_282989_ + p_281717_, p_282618_, p_281874_.getU0(), p_281874_.getU1(), p_281874_.getV0(), p_281874_.getV1(), p_283559_, p_282730_, p_283530_, p_282246_);
//   }
//
//   public void renderOutline(int p_281496_, int p_282076_, int p_281334_, int p_283576_, int p_283618_) {
//      this.fill(p_281496_, p_282076_, p_281496_ + p_281334_, p_282076_ + 1, p_283618_);
//      this.fill(p_281496_, p_282076_ + p_283576_ - 1, p_281496_ + p_281334_, p_282076_ + p_283576_, p_283618_);
//      this.fill(p_281496_, p_282076_ + 1, p_281496_ + 1, p_282076_ + p_283576_ - 1, p_283618_);
//      this.fill(p_281496_ + p_281334_ - 1, p_282076_ + 1, p_281496_ + p_281334_, p_282076_ + p_283576_ - 1, p_283618_);
//   }
//
//   public void blitSprite(ResourceLocation p_300860_, int p_298718_, int p_298541_, int p_300996_, int p_298426_) {
//      this.blitSprite(p_300860_, p_298718_, p_298541_, 0, p_300996_, p_298426_);
//   }
//
//   public void blitSprite(ResourceLocation p_299503_, int p_297264_, int p_301178_, int p_297744_, int p_299331_, int p_300334_) {
//      TextureAtlasSprite textureatlassprite = this.sprites.getSprite(p_299503_);
//      GuiSpriteScaling guispritescaling = this.sprites.getSpriteScaling(textureatlassprite);
//      if (guispritescaling instanceof GuiSpriteScaling.Stretch) {
//         this.blitSprite(textureatlassprite, p_297264_, p_301178_, p_297744_, p_299331_, p_300334_);
//      } else if (guispritescaling instanceof GuiSpriteScaling.Tile) {
//         GuiSpriteScaling.Tile guispritescaling$tile = (GuiSpriteScaling.Tile)guispritescaling;
//         this.blitTiledSprite(textureatlassprite, p_297264_, p_301178_, p_297744_, p_299331_, p_300334_, 0, 0, guispritescaling$tile.width(), guispritescaling$tile.height(), guispritescaling$tile.width(), guispritescaling$tile.height());
//      } else if (guispritescaling instanceof GuiSpriteScaling.NineSlice) {
//         GuiSpriteScaling.NineSlice guispritescaling$nineslice = (GuiSpriteScaling.NineSlice)guispritescaling;
//         this.blitNineSlicedSprite(textureatlassprite, guispritescaling$nineslice, p_297264_, p_301178_, p_297744_, p_299331_, p_300334_);
//      }
//
//   }
//
//   public void blitSprite(ResourceLocation p_298820_, int p_300417_, int p_298256_, int p_299965_, int p_300008_, int p_299688_, int p_300153_, int p_299047_, int p_298424_) {
//      this.blitSprite(p_298820_, p_300417_, p_298256_, p_299965_, p_300008_, p_299688_, p_300153_, 0, p_299047_, p_298424_);
//   }

//   public void blitSprite(ResourceLocation p_300222_, int p_301241_, int p_298760_, int p_299400_, int p_299966_, int p_298806_, int p_298412_, int p_300874_, int p_297763_, int p_300904_) {
//      TextureAtlasSprite textureatlassprite = this.sprites.getSprite(p_300222_);
//      GuiSpriteScaling guispritescaling = this.sprites.getSpriteScaling(textureatlassprite);
//      if (guispritescaling instanceof GuiSpriteScaling.Stretch) {
//         this.blitSprite(textureatlassprite, p_301241_, p_298760_, p_299400_, p_299966_, p_298806_, p_298412_, p_300874_, p_297763_, p_300904_);
//      } else {
//         this.blitSprite(textureatlassprite, p_298806_, p_298412_, p_300874_, p_297763_, p_300904_);
//      }
//
//   }
//
//   private void blitSprite(TextureAtlasSprite p_299198_, int p_300402_, int p_300310_, int p_300994_, int p_297577_, int p_299466_, int p_301260_, int p_298369_, int p_300819_, int p_299583_) {
//      if (p_300819_ != 0 && p_299583_ != 0) {
//         this.innerBlit(p_299198_.atlasLocation(), p_299466_, p_299466_ + p_300819_, p_301260_, p_301260_ + p_299583_, p_298369_, p_299198_.getU((float)p_300994_ / (float)p_300402_), p_299198_.getU((float)(p_300994_ + p_300819_) / (float)p_300402_), p_299198_.getV((float)p_297577_ / (float)p_300310_), p_299198_.getV((float)(p_297577_ + p_299583_) / (float)p_300310_));
//      }
//   }
//
//   private void blitSprite(TextureAtlasSprite p_299484_, int p_297573_, int p_300435_, int p_299725_, int p_300673_, int p_301130_) {
//      if (p_300673_ != 0 && p_301130_ != 0) {
//         this.innerBlit(p_299484_.atlasLocation(), p_297573_, p_297573_ + p_300673_, p_300435_, p_300435_ + p_301130_, p_299725_, p_299484_.getU0(), p_299484_.getU1(), p_299484_.getV0(), p_299484_.getV1());
//      }
//   }
//
//   public void blit(ResourceLocation p_283377_, int p_281970_, int p_282111_, int p_283134_, int p_282778_, int p_281478_, int p_281821_) {
//      this.blit(p_283377_, p_281970_, p_282111_, 0, (float)p_283134_, (float)p_282778_, p_281478_, p_281821_, 256, 256);
//   }

//   public void blit(ResourceLocation p_283573_, int p_283574_, int p_283670_, int p_283545_, float p_283029_, float p_283061_, int p_282845_, int p_282558_, int p_282832_, int p_281851_) {
//      this.blit(p_283573_, p_283574_, p_283574_ + p_282845_, p_283670_, p_283670_ + p_282558_, p_283545_, p_282845_, p_282558_, p_283029_, p_283061_, p_282832_, p_281851_);
//   }
//
//   public void blit(ResourceLocation p_282034_, int p_283671_, int p_282377_, int p_282058_, int p_281939_, float p_282285_, float p_283199_, int p_282186_, int p_282322_, int p_282481_, int p_281887_) {
//      this.blit(p_282034_, p_283671_, p_283671_ + p_282058_, p_282377_, p_282377_ + p_281939_, 0, p_282186_, p_282322_, p_282285_, p_283199_, p_282481_, p_281887_);
//   }
//
//   public void blit(ResourceLocation p_283272_, int p_283605_, int p_281879_, float p_282809_, float p_282942_, int p_281922_, int p_282385_, int p_282596_, int p_281699_) {
//      this.blit(p_283272_, p_283605_, p_281879_, p_281922_, p_282385_, p_282809_, p_282942_, p_281922_, p_282385_, p_282596_, p_281699_);
//   }

//   void blit(ResourceLocation p_282639_, int p_282732_, int p_283541_, int p_281760_, int p_283298_, int p_283429_, int p_282193_, int p_281980_, float p_282660_, float p_281522_, int p_282315_, int p_281436_) {
//      this.innerBlit(p_282639_, p_282732_, p_283541_, p_281760_, p_283298_, p_283429_, (p_282660_ + 0.0F) / (float)p_282315_, (p_282660_ + (float)p_282193_) / (float)p_282315_, (p_281522_ + 0.0F) / (float)p_281436_, (p_281522_ + (float)p_281980_) / (float)p_281436_);
//   }

//   void innerBlit(ResourceLocation p_283461_, int p_281399_, int p_283222_, int p_283615_, int p_283430_, int p_281729_, float p_283247_, float p_282598_, float p_282883_, float p_283017_) {
//      RenderSystem.setShaderTexture(0, p_283461_);
//      RenderSystem.setShader(GameRenderer::getPositionTexShader);
//      Matrix4f matrix4f = this.pose.last().pose();
//      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
//      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//      bufferbuilder.vertex(matrix4f, (float)p_281399_, (float)p_283615_, (float)p_281729_).uv(p_283247_, p_282883_).endVertex();
//      bufferbuilder.vertex(matrix4f, (float)p_281399_, (float)p_283430_, (float)p_281729_).uv(p_283247_, p_283017_).endVertex();
//      bufferbuilder.vertex(matrix4f, (float)p_283222_, (float)p_283430_, (float)p_281729_).uv(p_282598_, p_283017_).endVertex();
//      bufferbuilder.vertex(matrix4f, (float)p_283222_, (float)p_283615_, (float)p_281729_).uv(p_282598_, p_282883_).endVertex();
//      BufferUploader.drawWithShader(bufferbuilder.end());
//   }
//
//   void innerBlit(ResourceLocation p_283254_, int p_283092_, int p_281930_, int p_282113_, int p_281388_, int p_283583_, float p_281327_, float p_281676_, float p_283166_, float p_282630_, float p_282800_, float p_282850_, float p_282375_, float p_282754_) {
//      RenderSystem.setShaderTexture(0, p_283254_);
//      RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
//      RenderSystem.enableBlend();
//      Matrix4f matrix4f = this.pose.last().pose();
//      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
//      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
//      bufferbuilder.vertex(matrix4f, (float)p_283092_, (float)p_282113_, (float)p_283583_).color(p_282800_, p_282850_, p_282375_, p_282754_).uv(p_281327_, p_283166_).endVertex();
//      bufferbuilder.vertex(matrix4f, (float)p_283092_, (float)p_281388_, (float)p_283583_).color(p_282800_, p_282850_, p_282375_, p_282754_).uv(p_281327_, p_282630_).endVertex();
//      bufferbuilder.vertex(matrix4f, (float)p_281930_, (float)p_281388_, (float)p_283583_).color(p_282800_, p_282850_, p_282375_, p_282754_).uv(p_281676_, p_282630_).endVertex();
//      bufferbuilder.vertex(matrix4f, (float)p_281930_, (float)p_282113_, (float)p_283583_).color(p_282800_, p_282850_, p_282375_, p_282754_).uv(p_281676_, p_283166_).endVertex();
//      BufferUploader.drawWithShader(bufferbuilder.end());
//      RenderSystem.disableBlend();
//   }

//   private void blitTiledSprite(TextureAtlasSprite p_298835_, int p_297456_, int p_300732_, int p_297241_, int p_300646_, int p_299561_, int p_298797_, int p_299557_, int p_297684_, int p_299756_, int p_297303_, int p_299619_) {
//      if (p_300646_ > 0 && p_299561_ > 0) {
//         if (p_297684_ > 0 && p_299756_ > 0) {
//            for(int i = 0; i < p_300646_; i += p_297684_) {
//               int j = Math.min(p_297684_, p_300646_ - i);
//
//               for(int k = 0; k < p_299561_; k += p_299756_) {
//                  int l = Math.min(p_299756_, p_299561_ - k);
//                  this.blitSprite(p_298835_, p_297303_, p_299619_, p_298797_, p_299557_, p_297456_ + i, p_300732_ + k, p_297241_, j, l);
//               }
//            }
//
//         } else {
//            throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + p_297684_ + "x" + p_299756_);
//         }
//      }
//   }


}