package net.minecraft.client.renderer.entity.model.newmodels.player;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.AgeableListModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.newmodels.HumanoidModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public class HumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
extends LayerRenderer<T, M> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
    private final A innerModel;
    private final A outerModel;

    public HumanoidArmorLayer(IEntityRenderer<T, M> renderLayerParent, A a, A a2) {
        super(renderLayerParent);
        this.innerModel = a;
        this.outerModel = a2;
    }

    @Override
    public void render(MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        this.renderArmorPiece(poseStack, multiBufferSource, t, EquipmentSlotType.CHEST, n, this.getArmorModel(EquipmentSlotType.CHEST));
        this.renderArmorPiece(poseStack, multiBufferSource, t, EquipmentSlotType.LEGS, n, this.getArmorModel(EquipmentSlotType.LEGS));
        this.renderArmorPiece(poseStack, multiBufferSource, t, EquipmentSlotType.FEET, n, this.getArmorModel(EquipmentSlotType.FEET));
        this.renderArmorPiece(poseStack, multiBufferSource, t, EquipmentSlotType.HEAD, n, this.getArmorModel(EquipmentSlotType.HEAD));
    }

    private void renderArmorPiece(MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, T t, EquipmentSlotType equipmentSlot, int n, A a) {
        ItemStack itemStack = ((LivingEntity)t).getItemBySlot(equipmentSlot);
        Item item = itemStack.getItem();
        if (!(item instanceof ArmorItem)) {
            return;
        }
        ArmorItem armorItem = (ArmorItem)item;
        if (armorItem.getEquipmentSlot() != equipmentSlot) {
            return;
        }
        ((HumanoidModel)this.getParentModel()).copyPropertiesTo(a);
        this.setPartVisibility(a, equipmentSlot);
        boolean bl = this.usesInnerModel(equipmentSlot);
        boolean bl2 = itemStack.hasFoil();
        if (armorItem instanceof DyeableArmorItem) {
            int n2 = ((DyeableArmorItem)armorItem).getColor(itemStack);
            float f = (float)(n2 >> 16 & 0xFF) / 255.0f;
            float f2 = (float)(n2 >> 8 & 0xFF) / 255.0f;
            float f3 = (float)(n2 & 0xFF) / 255.0f;
            this.renderModel(poseStack, multiBufferSource, n, armorItem, bl2, a, bl, f, f2, f3, null);
            this.renderModel(poseStack, multiBufferSource, n, armorItem, bl2, a, bl, 1.0f, 1.0f, 1.0f, "overlay");
        } else {
            this.renderModel(poseStack, multiBufferSource, n, armorItem, bl2, a, bl, 1.0f, 1.0f, 1.0f, null);
        }

        boolean flag = this.usesInnerModel(equipmentSlot);



        ArmorTrim.getTrim(((Entity)t).level().registryAccess(), itemStack).ifPresent(armorTrim -> this.renderTrim((ArmorMaterial) armorItem.getMaterial(), poseStack, multiBufferSource, n, (ArmorTrim)armorTrim, a, flag));

    }

    protected void setPartVisibility(A a, EquipmentSlotType equipmentSlot) {
        ((HumanoidModel)a).setAllVisible(false);
        switch (equipmentSlot) {
            case HEAD: {
                ((HumanoidModel)a).head.visible = true;
                ((HumanoidModel)a).hat.visible = true;
                break;
            }
            case CHEST: {
                ((HumanoidModel)a).body.visible = true;
                ((HumanoidModel)a).rightArm.visible = true;
                ((HumanoidModel)a).leftArm.visible = true;
                break;
            }
            case LEGS: {
                ((HumanoidModel)a).body.visible = true;
                ((HumanoidModel)a).rightLeg.visible = true;
                ((HumanoidModel)a).leftLeg.visible = true;
                break;
            }
            case FEET: {
                ((HumanoidModel)a).rightLeg.visible = true;
                ((HumanoidModel)a).leftLeg.visible = true;
            }
        }
    }

    private void renderModel(MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int n, ArmorItem armorItem, boolean bl, A a, boolean bl2, float f, float f2, float f3, @Nullable String string) {
        IVertexBuilder vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(this.getArmorLocation(armorItem, bl2, string)), false, bl);
        ((AgeableListModel)a).renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, f, f2, f3, 1.0f);
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
        (a).renderToBuffer(poseStack, vertexConsumer, armorTrimMaterial.contains("sculk") || armorTrim.glow ? 15728880 : n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    private A getArmorModel(EquipmentSlotType equipmentSlot) {
        return this.usesInnerModel(equipmentSlot) ? this.innerModel : this.outerModel;
    }

    private boolean usesInnerModel(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == EquipmentSlotType.LEGS;
    }

    private ResourceLocation getArmorLocation(ArmorItem armorItem, boolean bl, @Nullable String string) {
        String string2 = "textures/models/armor/" + armorItem.getMaterial().getName() + "_layer_" + (bl ? 2 : 1) + (String)(string == null ? "" : "_" + string) + ".png";
        return ARMOR_LOCATION_CACHE.computeIfAbsent(string2, ResourceLocation::new);
    }
}

