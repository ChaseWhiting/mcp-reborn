package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.villager.data.quest.QuestManager;
import net.minecraft.inventory.container.QuestContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.QuestOffer;
import net.minecraft.item.QuestOffers;
import net.minecraft.network.play.client.CSelectTradePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class QuestScreen extends ContainerScreen<QuestContainer> {
   private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
   private static final ITextComponent TRADES_LABEL = new TranslationTextComponent("merchant.trades");
   private static final ITextComponent LEVEL_SEPARATOR = new StringTextComponent(" - ");
   private static final ITextComponent DEPRECATED_TOOLTIP = new TranslationTextComponent("merchant.deprecated");
   private int shopItem;
   private final QuestScreen.TradeButton[] tradeOfferButtons = new QuestScreen.TradeButton[7];
   private int scrollOff;
   private boolean isDragging;
   private QuestManager questManager;

   public QuestScreen(QuestContainer questContainer, PlayerInventory playerInventory, ITextComponent title) {
      super(questContainer, playerInventory, title);
      this.imageWidth = 276;
      this.inventoryLabelX = 107;
      this.questManager = playerInventory.player.getQuestManager();
   }

   private void postButtonClick() {
      this.menu.setSelectionHint(this.shopItem);
      this.menu.tryMoveItems(this.shopItem);
      this.minecraft.getConnection().send(new CSelectTradePacket(this.shopItem));
   }

   @Override
   protected void init() {
      super.init();
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      int k = j + 16 + 2;

      for (int l = 0; l < 7; ++l) {
         this.tradeOfferButtons[l] = this.addButton(new QuestScreen.TradeButton(i + 5, k, l, (button) -> {
            if (button instanceof QuestScreen.TradeButton) {
               this.shopItem = ((QuestScreen.TradeButton) button).getIndex() + this.scrollOff;
               this.postButtonClick();
            }
         }));
         k += 20;
      }
   }

   @Override
   protected void renderLabels(MatrixStack matrixStack, int x, int y) {
      int i = 6;
      if (i > 0 && i <= 5 && this.menu.showProgressBar()) {
         ITextComponent itextcomponent = this.title.copy().append(LEVEL_SEPARATOR).append(new TranslationTextComponent("merchant.level." + i));
         int j = this.font.width(itextcomponent);
         int k = 49 + this.imageWidth / 2 - j / 2;
         this.font.draw(matrixStack, itextcomponent, (float) k, 6.0F, 4210752);
      } else {
         this.font.draw(matrixStack, this.title, (float) (49 + this.imageWidth / 2 - this.font.width(this.title) / 2), 6.0F, 4210752);
      }

      this.font.draw(matrixStack, this.inventory.getDisplayName(), (float) this.inventoryLabelX, (float) this.inventoryLabelY, 4210752);
      int l = this.font.width(TRADES_LABEL);
      this.font.draw(matrixStack, TRADES_LABEL, (float) (5 - l / 2 + 48), 6.0F, 4210752);
   }

   @Override
   protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      blit(matrixStack, i, j, this.getBlitOffset(), 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 512);
      if (this.menu.hasOffers()) {
         int k = this.shopItem;
         if (k < 0 || k >= this.menu.getOffers().size()) {
            return;
         }

         QuestOffer questOffer = this.menu.getOffers().get(k);
         if (questOffer.isOutOfStock()) {
            this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            blit(matrixStack, this.leftPos + 83 + 99, this.topPos + 35, this.getBlitOffset(), 311.0F, 0.0F, 28, 21, 256, 512);
         }
      }
   }

   private void renderProgressBar(MatrixStack matrixStack, int x, int y, QuestOffer questOffer) {
      this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
      int i = 7;
      int j = this.menu.getTraderXp();
      if (i < 5) {
         blit(matrixStack, x + 136, y + 16, this.getBlitOffset(), 0.0F, 186.0F, 102, 5, 256, 512);
         int k = VillagerData.getMinXpPerLevel(i);
         if (j >= k && VillagerData.canLevelUp(i)) {
            int l = 100;
            float f = 100.0F / (float) (VillagerData.getMaxXpPerLevel(i) - k);
            int i1 = Math.min(MathHelper.floor(f * (float) (j - k)), 100);
            blit(matrixStack, x + 136, y + 16, this.getBlitOffset(), 0.0F, 191.0F, i1 + 1, 5, 256, 512);
            int j1 = this.menu.getFutureTraderXp();
            if (j1 > 0) {
               int k1 = Math.min(MathHelper.floor((float) j1 * f), 100 - i1);
               blit(matrixStack, x + 136 + i1 + 1, y + 16 + 1, this.getBlitOffset(), 2.0F, 182.0F, k1, 3, 256, 512);
            }
         }
      }
   }

   private void renderScroller(MatrixStack matrixStack, int x, int y, int offerCount) {
      int i = offerCount + 1 - 7;
      if (i > 1) {
         int j = 139 - (27 + (i - 1) * 139 / i);
         int k = 1 + j / i + 139 / i;
         int l = 113;
         int i1 = Math.min(113, this.scrollOff * k);
         if (this.scrollOff == i - 1) {
            i1 = 113;
         }

         blit(matrixStack, x + 94, y + 18 + i1, this.getBlitOffset(), 0.0F, 199.0F, 6, 27, 256, 512);
      } else {
         blit(matrixStack, x + 94, y + 18, this.getBlitOffset(), 6.0F, 199.0F, 6, 27, 256, 512);
      }
   }

   @Override
   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack);
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      QuestOffers questOffers = this.menu.getOffers();
      if (!questOffers.isEmpty()) {
         int i = (this.width - this.imageWidth) / 2;
         int j = (this.height - this.imageHeight) / 2;
         int k = j + 16 + 1;
         int l = i + 5 + 5;
         RenderSystem.pushMatrix();
         RenderSystem.enableRescaleNormal();
         this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
         this.renderScroller(matrixStack, i, j, questOffers.size());
         int i1 = 0;

         for (QuestOffer questOffer : questOffers) {
            if (this.canScroll(questOffers.size()) && (i1 < this.scrollOff || i1 >= 7 + this.scrollOff)) {
               ++i1;
            } else {
               ItemStack itemstack = questOffer.getQuest().getRequiredItems().isEmpty() ? ItemStack.EMPTY : questOffer.getQuest().getRequiredItems().get(0);
               ItemStack itemstack1 = questOffer.getQuest().getRewards().isEmpty() ? ItemStack.EMPTY : questOffer.getQuest().getRewards().get(0);
               this.itemRenderer.blitOffset = 100.0F;
               int j1 = k + 2;
               this.renderAndDecorateCostA(matrixStack, itemstack, itemstack, l, j1);
               this.renderButtonArrows(matrixStack, questOffer, i, j1);
               this.itemRenderer.renderAndDecorateFakeItem(itemstack1, i + 5 + 68, j1);
               this.itemRenderer.renderGuiItemDecorations(this.font, itemstack1, i + 5 + 68, j1);
               this.itemRenderer.blitOffset = 0.0F;
               k += 20;
               ++i1;
            }
         }

         int k1 = this.shopItem;
         QuestOffer questOffer1 = questOffers.isEmpty() ? null : questOffers.get(k1);
         if (questOffer1 != null && this.menu.showProgressBar()) {
            this.renderProgressBar(matrixStack, i, j, questOffer1);
         }

         if (questOffer1 != null && questOffer1.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double) mouseX, (double) mouseY) && this.menu.canRestock()) {
            this.renderTooltip(matrixStack, DEPRECATED_TOOLTIP, mouseX, mouseY);
         }

         for (QuestScreen.TradeButton tradeButton : this.tradeOfferButtons) {
            if (tradeButton.isHovered()) {
               tradeButton.renderToolTip(matrixStack, mouseX, mouseY);
            }

            tradeButton.visible = tradeButton.index < questOffers.size();
         }

         RenderSystem.popMatrix();
         RenderSystem.enableDepthTest();
      }

      this.renderTooltip(matrixStack, mouseX, mouseY);
   }

   private void renderButtonArrows(MatrixStack matrixStack, QuestOffer questOffer, int x, int y) {
      RenderSystem.enableBlend();
      this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
      if (questOffer.isOutOfStock()) {
         blit(matrixStack, x + 5 + 35 + 20, y + 3, this.getBlitOffset(), 25.0F, 171.0F, 10, 9, 256, 512);
      } else {
         blit(matrixStack, x + 5 + 35 + 20, y + 3, this.getBlitOffset(), 15.0F, 171.0F, 10, 9, 256, 512);
      }
   }

   private void renderAndDecorateCostA(MatrixStack matrixStack, ItemStack requiredItem, ItemStack displayedItem, int x, int y) {
      this.itemRenderer.renderAndDecorateFakeItem(requiredItem, x, y);
      if (displayedItem.getCount() == requiredItem.getCount()) {
         this.itemRenderer.renderGuiItemDecorations(this.font, requiredItem, x, y);
      } else {
         this.itemRenderer.renderGuiItemDecorations(this.font, displayedItem, x, y, displayedItem.getCount() == 1 ? "1" : null);
         this.itemRenderer.renderGuiItemDecorations(this.font, requiredItem, x + 14, y, requiredItem.getCount() == 1 ? "1" : null);
         this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
         this.setBlitOffset(this.getBlitOffset() + 300);
         blit(matrixStack, x + 7, y + 12, this.getBlitOffset(), 0.0F, 176.0F, 9, 2, 256, 512);
         this.setBlitOffset(this.getBlitOffset() - 300);
      }
   }

   private boolean canScroll(int offerCount) {
      return offerCount > 7;
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
      int i = this.menu.getOffers().size();
      if (this.canScroll(i)) {
         int j = i - 7;
         this.scrollOff = (int) ((double) this.scrollOff - scrollDelta);
         this.scrollOff = MathHelper.clamp(this.scrollOff, 0, j);
      }

      return true;
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      int i = this.menu.getOffers().size();
      if (this.isDragging) {
         int j = this.topPos + 18;
         int k = j + 139;
         int l = i - 7;
         float f = ((float) mouseY - (float) j - 13.5F) / ((float) (k - j) - 27.0F);
         f = f * (float) l + 0.5F;
         this.scrollOff = MathHelper.clamp((int) f, 0, l);
         return true;
      } else {
         return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
      }
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.isDragging = false;
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      if (this.canScroll(this.menu.getOffers().size()) && mouseX > (double) (i + 94) && mouseX < (double) (i + 94 + 6) && mouseY > (double) (j + 18) && mouseY <= (double) (j + 18 + 139 + 1)) {
         this.isDragging = true;
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   @OnlyIn(Dist.CLIENT)
   class TradeButton extends Button {
      final int index;

      public TradeButton(int x, int y, int index, IPressable onPress) {
         super(x, y, 89, 20, StringTextComponent.EMPTY, onPress);
         this.index = index;
         this.visible = false;
      }

      public int getIndex() {
         return this.index;
      }

      @Override
      public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
         if (this.isHovered && QuestScreen.this.menu.getOffers().size() > this.index + QuestScreen.this.scrollOff) {
            if (mouseX < this.x + 20) {
               ItemStack requiredItem = QuestScreen.this.menu.getOffers().get(this.index + QuestScreen.this.scrollOff).getQuest().getRequiredItems().get(0); // Assuming one required item for simplicity
               QuestScreen.this.renderTooltip(matrixStack, requiredItem, mouseX, mouseY);
            } else if (mouseX < this.x + 50 && mouseX > this.x + 30) {
               ItemStack rewardItem = QuestScreen.this.menu.getOffers().get(this.index + QuestScreen.this.scrollOff).getQuest().getRewards().get(0); // Assuming one reward for simplicity
               if (!rewardItem.isEmpty()) {
                  QuestScreen.this.renderTooltip(matrixStack, rewardItem, mouseX, mouseY);
               }
            } else if (mouseX > this.x + 65) {
               ItemStack resultItem = QuestScreen.this.menu.getOffers().get(this.index + QuestScreen.this.scrollOff).getQuest().getRewards().get(0); // Assuming one reward for simplicity
               QuestScreen.this.renderTooltip(matrixStack, resultItem, mouseX, mouseY);
            }
         }
      }
   }
}
