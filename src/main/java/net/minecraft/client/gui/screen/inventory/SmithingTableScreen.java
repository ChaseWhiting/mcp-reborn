package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.SmithingTableContainer;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.Component;
import net.minecraft.item.equipment.trim.SmithingTemplateItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class SmithingTableScreen extends AbstractRepairScreen<SmithingTableContainer> {
   private static final ResourceLocation SMITHING_LOCATION = new ResourceLocation("textures/gui/container/smithing.png");
   private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM = new ResourceLocation("textures/item/empty_slot_smithing_template_armor_trim.png");
   private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE = new ResourceLocation("textures/item/empty_slot_smithing_template_netherite_upgrade.png");
   private static final TranslationTextComponent MISSING_TEMPLATE_TOOLTIP = Component.translatable("container.upgrade.missing_template_tooltip");
   private static final TranslationTextComponent ERROR_TOOLTIP = Component.translatable("container.upgrade.error_tooltip");
   private static final List<ResourceLocation> EMPTY_SLOT_SMITHING_TEMPLATES = List.of(EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE);
   private static final int TITLE_LABEL_X = 44;
   private static final int TITLE_LABEL_Y = 15;
   private static final int ERROR_ICON_HEIGHT = 21;
   private static final int ERROR_ICON_X = 65;
   private static final int ERROR_ICON_Y = 46;
   private static final int TOOLTIP_WIDTH = 115;
   public static final int ARMOR_STAND_SCALE = 25;
   public static final int ARMOR_STAND_OFFSET_Y = 65;
   public static final int ARMOR_STAND_OFFSET_X = 141;
   private final CyclingSlotBackground templateIcon = new CyclingSlotBackground(0);
   private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(1);
   private final CyclingSlotBackground additionalIcon = new CyclingSlotBackground(2);
   public static final Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ(0.43633232f, 0.0f, (float)Math.PI);

   @Nullable
   private ArmorStandEntity armorStandPreview;

   public SmithingTableScreen(SmithingTableContainer container, PlayerInventory inventory, ITextComponent textComponent) {
      super(container, inventory, textComponent, SMITHING_LOCATION);
      this.titleLabelX = TITLE_LABEL_X;
      this.titleLabelY = TITLE_LABEL_Y;
   }

   @Override
   protected void subInit() {
      this.armorStandPreview = new ArmorStandEntity(this.minecraft.level, 0.0, 0.0, 0.0);
      this.armorStandPreview.showBasePlate(false);
      this.armorStandPreview.showArms(true);
      this.armorStandPreview.yBodyRot = 210.0f;
      this.armorStandPreview.setAllLight(true);
      this.armorStandPreview.xRot = (25.0f);
      this.armorStandPreview.yHeadRot = this.armorStandPreview.yRot;
      this.armorStandPreview.yHeadRotO = this.armorStandPreview.yRot;
      this.updateArmorStandPreview(((SmithingTableContainer)this.menu).getSlot(3).getItem());
   }

   @Override
   public void tick() {
      super.tick();
      Optional<SmithingTemplateItem> optional = this.getTemplateItem();
      this.templateIcon.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
      this.baseIcon.tick(optional.map(SmithingTemplateItem::getBaseSlotEmptyIcons).orElse(List.of()));
      this.additionalIcon.tick(optional.map(SmithingTemplateItem::getAdditionalSlotEmptyIcons).orElse(List.of()));
   }

   protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
      RenderSystem.disableBlend();
      super.renderLabels(p_230451_1_, p_230451_2_, p_230451_3_);
   }

   private Optional<SmithingTemplateItem> getTemplateItem() {
      Item item;
      ItemStack itemStack = this.menu.getSlot(0).getItem();
      if (!itemStack.isEmpty() && (item = itemStack.getItem()) instanceof SmithingTemplateItem) {
         SmithingTemplateItem smithingTemplateItem = (SmithingTemplateItem)item;
         return Optional.of(smithingTemplateItem);
      }
      return Optional.empty();
   }

   @Override
   public void render(MatrixStack stack, int n, int n2, float f) {
      super.render(stack, n, n2, f);
      this.renderOnboardingTooltips(stack, n, n2);
   }

   @Override
   protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(SMITHING_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(stack, i, j, 0, 0, this.imageWidth, this.imageHeight);
      this.blit(stack, i + 59, j + 20, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);

      renderErrorIcon(stack, i, j);

      this.templateIcon.render(stack,this.menu, partialTicks, this.leftPos, this.topPos);
      this.baseIcon.render(stack,this.menu, partialTicks, this.leftPos, this.topPos);
      this.additionalIcon.render(stack,this.menu, partialTicks, this.leftPos, this.topPos);

      InventoryScreen.renderEntityInInventory(this.leftPos + ARMOR_STAND_OFFSET_X, this.topPos + ARMOR_STAND_OFFSET_Y, ARMOR_STAND_SCALE, 65.2638583977629F, -120.38278695451325F,  this.armorStandPreview);
   }


   @Override
   public void slotChanged(Container abstractContainerMenu, int n, ItemStack itemStack) {
      if (n == 3) {
         this.updateArmorStandPreview(itemStack);
      }
   }

   private void updateArmorStandPreview(ItemStack itemStack) {
      if (this.armorStandPreview == null) {
         return;
      }
      for (EquipmentSlotType equipmentSlot : EquipmentSlotType.values()) {
         this.armorStandPreview.setItemSlot(equipmentSlot, ItemStack.EMPTY);
      }
      if (!itemStack.isEmpty()) {
         ItemStack itemStack2 = itemStack.copy();
         Item item = itemStack.getItem();
         if (item instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem)item;
            this.armorStandPreview.setItemSlot(armorItem.getSlot(), itemStack2);
         } else {
            this.armorStandPreview.setItemSlot(EquipmentSlotType.OFFHAND, itemStack2);
         }
      }
   }

   @Override
   protected void renderErrorIcon(MatrixStack stack, int leftPos, int topPos) {
      if (this.hasRecipeError()) {
         Minecraft.getInstance().getTextureManager().bind(SMITHING_LOCATION);
         this.blit(stack, leftPos + ERROR_ICON_X, topPos + ERROR_ICON_Y, 176, 0, ERROR_ICON_HEIGHT, ERROR_ICON_HEIGHT);
      }
   }




   private void renderOnboardingTooltips(MatrixStack guiGraphics, int n, int n2) {
      Optional<ITextComponent> optional = Optional.empty();
      if (this.hasRecipeError() && this.isHovering(65, 46, 28, 21, n, n2)) {
         optional = Optional.of(ERROR_TOOLTIP);
      }
      if (this.hoveredSlot != null) {
         ItemStack itemStack = ((SmithingTableContainer)this.menu).getSlot(0).getItem();
         ItemStack itemStack2 = this.hoveredSlot.getItem();
         if (itemStack.isEmpty()) {
            if (this.hoveredSlot.index == 0) {
               optional = Optional.of(MISSING_TEMPLATE_TOOLTIP);
            }
         } else {
            Item item = itemStack.getItem();
            if (item instanceof SmithingTemplateItem) {
               SmithingTemplateItem smithingTemplateItem = (SmithingTemplateItem)item;
               if (itemStack2.isEmpty()) {
                  if (this.hoveredSlot.index == 1) {
                     optional = Optional.of(smithingTemplateItem.getBaseSlotDescription());
                  } else if (this.hoveredSlot.index == 2) {
                     optional = Optional.of(smithingTemplateItem.getAdditionSlotDescription());
                  }
               }
            }
         }
      }
      optional.ifPresent(component -> renderTooltip(guiGraphics, this.font.split(component, TOOLTIP_WIDTH), n, n2));
   }



   private boolean hasRecipeError() {
      return this.menu.getSlot(0).hasItem() &&
              this.menu.getSlot(1).hasItem() &&
              this.menu.getSlot(2).hasItem() &&
              !this.menu.getSlot(this.menu.getResultSlot()).hasItem();
   }


}