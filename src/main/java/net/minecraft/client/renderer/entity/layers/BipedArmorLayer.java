package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BipedArmorLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends LayerRenderer<T, M> {
   private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
   private final A innerModel;
   private final A outerModel;
   private AtlasTexture armorTrimAtlas;


   public BipedArmorLayer(IEntityRenderer<T, M> p_i50936_1_, A p_i50936_2_, A p_i50936_3_) {
      super(p_i50936_1_);
      this.innerModel = p_i50936_2_;
      this.outerModel = p_i50936_3_;
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      this.renderArmorPiece(p_225628_1_, p_225628_2_, p_225628_4_, EquipmentSlotType.CHEST, p_225628_3_, this.getArmorModel(EquipmentSlotType.CHEST));
      this.renderArmorPiece(p_225628_1_, p_225628_2_, p_225628_4_, EquipmentSlotType.LEGS, p_225628_3_, this.getArmorModel(EquipmentSlotType.LEGS));
      this.renderArmorPiece(p_225628_1_, p_225628_2_, p_225628_4_, EquipmentSlotType.FEET, p_225628_3_, this.getArmorModel(EquipmentSlotType.FEET));
      this.renderArmorPiece(p_225628_1_, p_225628_2_, p_225628_4_, EquipmentSlotType.HEAD, p_225628_3_, this.getArmorModel(EquipmentSlotType.HEAD));
   }

   private void renderArmorPiece(MatrixStack p_241739_1_, IRenderTypeBuffer p_241739_2_, T p_241739_3_, EquipmentSlotType p_241739_4_, int p_241739_5_, A p_241739_6_) {
      ItemStack itemstack = p_241739_3_.getItemBySlot(p_241739_4_);
      if (itemstack.getItem() instanceof ArmorItem) {
         ArmorItem armoritem = (ArmorItem)itemstack.getItem();
         if (armoritem.getSlot() == p_241739_4_) {
            this.getParentModel().copyPropertiesTo(p_241739_6_);
            this.setPartVisibility(p_241739_6_, p_241739_4_);
            boolean flag = this.usesInnerModel(p_241739_4_);
            boolean flag1 = itemstack.hasFoil();
            if (armoritem instanceof DyeableArmorItem) {
               int i = ((DyeableArmorItem)armoritem).getColor(itemstack);
               float f = (float)(i >> 16 & 255) / 255.0F;
               float f1 = (float)(i >> 8 & 255) / 255.0F;
               float f2 = (float)(i & 255) / 255.0F;
               this.renderModel(p_241739_1_, p_241739_2_, p_241739_5_, armoritem, flag1, p_241739_6_, flag, f, f1, f2, (String)null);
               this.renderModel(p_241739_1_, p_241739_2_, p_241739_5_, armoritem, flag1, p_241739_6_, flag, 1.0F, 1.0F, 1.0F, "overlay");
            } else {
               this.renderModel(p_241739_1_, p_241739_2_, p_241739_5_, armoritem, flag1, p_241739_6_, flag, 1.0F, 1.0F, 1.0F, (String)null);
            }

            ArmorTrim.getTrim(((Entity)p_241739_3_).level().registryAccess(), itemstack).ifPresent(armorTrim -> this.renderTrim((ArmorMaterial) armoritem.getMaterial(), p_241739_1_, p_241739_2_, p_241739_5_, (ArmorTrim)armorTrim, p_241739_6_, flag));


         }
      }
   }

   protected void setPartVisibility(A p_188359_1_, EquipmentSlotType p_188359_2_) {
      p_188359_1_.setAllVisible(false);
      switch(p_188359_2_) {
      case HEAD:
         p_188359_1_.head.visible = true;
         p_188359_1_.hat.visible = true;
         break;
      case CHEST:
         p_188359_1_.body.visible = true;
         p_188359_1_.rightArm.visible = true;
         p_188359_1_.leftArm.visible = true;
         break;
      case LEGS:
         p_188359_1_.body.visible = true;
         p_188359_1_.rightLeg.visible = true;
         p_188359_1_.leftLeg.visible = true;
         break;
      case FEET:
         p_188359_1_.rightLeg.visible = true;
         p_188359_1_.leftLeg.visible = true;
      }

   }

   private void renderModel(MatrixStack p_241738_1_, IRenderTypeBuffer p_241738_2_, int p_241738_3_, ArmorItem p_241738_4_, boolean p_241738_5_, A p_241738_6_, boolean p_241738_7_, float p_241738_8_, float p_241738_9_, float p_241738_10_, @Nullable String p_241738_11_) {
      IVertexBuilder ivertexbuilder = ItemRenderer.getArmorFoilBuffer(p_241738_2_, RenderType.armorCutoutNoCull(this.getArmorLocation(p_241738_4_, p_241738_7_, p_241738_11_)), false, p_241738_5_);
      p_241738_6_.renderToBuffer(p_241738_1_, ivertexbuilder, p_241738_3_, OverlayTexture.NO_OVERLAY, p_241738_8_, p_241738_9_, p_241738_10_, 1.0F);
   }

   private void renderTrim(ArmorMaterial armorMaterial, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int n, ArmorTrim armorTrim, A a, boolean usesInnerModel) {
      ResourceLocation trimTexture;

      String armorTrimMaterial = armorTrim.material().get().getAssetName().replace("minecraft:", "");
      String armorTrimPattern = armorTrim.pattern().get().getAssetName();

      boolean darker = armorMaterial == ArmorMaterial.NETHERITE && armorTrimMaterial.contains("netherite") ||
              armorMaterial == ArmorMaterial.IRON && armorTrimMaterial.contains("iron") ||
              armorMaterial == ArmorMaterial.GOLD && armorTrimMaterial.contains("gold") ||
              armorMaterial == ArmorMaterial.DIAMOND && armorTrimMaterial.contains("diamond")
              || armorMaterial == ArmorMaterial.ROSE_GOLD && armorTrimMaterial.contains("rose_gold");


      if (usesInnerModel) {
         trimTexture = new ResourceLocation("minecraft", "textures/trims/models/armor/" + armorTrimPattern + "_leggings_" + armorTrimMaterial + (darker ? "_darker.png" : ".png"));
      } else {
         trimTexture = new ResourceLocation("minecraft", "textures/trims/models/armor/" + armorTrimPattern + "_" + armorTrimMaterial + (darker ? "_darker.png" : ".png"));
      }

      Minecraft.getInstance().getTextureManager().bind(trimTexture);

      IVertexBuilder vertexConsumer = multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(trimTexture));
      ((BipedModel)a).renderToBuffer(poseStack, vertexConsumer, armorTrimMaterial.contains("sculk") || armorTrim.glow ? 15728880 : n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
   }


   private A getArmorModel(EquipmentSlotType p_241736_1_) {
      return (A)(this.usesInnerModel(p_241736_1_) ? this.innerModel : this.outerModel);
   }

   private boolean usesInnerModel(EquipmentSlotType p_188363_1_) {
      return p_188363_1_ == EquipmentSlotType.LEGS;
   }

   private ResourceLocation getArmorLocation(ArmorItem p_241737_1_, boolean p_241737_2_, @Nullable String p_241737_3_) {
      String s = "textures/models/armor/" + p_241737_1_.getMaterial().getName() + "_layer_" + (p_241737_2_ ? 2 : 1) + (p_241737_3_ == null ? "" : "_" + p_241737_3_) + ".png";
      return ARMOR_LOCATION_CACHE.computeIfAbsent(s, ResourceLocation::new);
   }
}