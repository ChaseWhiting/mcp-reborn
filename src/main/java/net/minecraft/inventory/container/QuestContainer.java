package net.minecraft.inventory.container;

import net.minecraft.entity.Entity;
import net.minecraft.entity.NPCMerchant;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.villager.data.quest.QuestManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.QuestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.QuestOffer;
import net.minecraft.item.QuestOffers;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class QuestContainer extends Container {
   private final IMerchant merchant;
   private final QuestInventory tradeInventory;

   @OnlyIn(Dist.CLIENT)
   private int merchantLevel;

   @OnlyIn(Dist.CLIENT)
   private boolean showProgressBar;

   @OnlyIn(Dist.CLIENT)
   private boolean canRestock = true;
   private QuestManager questManager;

   public QuestContainer(int containerId, PlayerInventory playerInventory, QuestManager questManager) {

      this(containerId, playerInventory, new NPCMerchant(playerInventory.player), questManager);
   }

   public QuestContainer(int containerId, PlayerInventory playerInventory, IMerchant merchant, QuestManager questManager) {
      super(ContainerType.QUEST, containerId);
      this.merchant = merchant;
      this.questManager = questManager;
      this.tradeInventory = new QuestInventory(merchant, questManager);


      this.addSlot(new Slot(this.tradeInventory, 0, 136, 37));
      this.addSlot(new Slot(this.tradeInventory, 1, 162, 37));
      this.addSlot(new QuestResultSlot(playerInventory.player, merchant, this.tradeInventory, 2, 220, 37));
      this.setShowProgressBar(false);
      for (int i = 0; i < 3; ++i) {
         for (int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 108 + j * 18, 84 + i * 18));
         }
      }
      for (int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(playerInventory, k, 108 + k * 18, 142));
      }
   }



   @OnlyIn(Dist.CLIENT)
   public void setShowProgressBar(boolean showProgressBar) {
      this.showProgressBar = showProgressBar;
   }

   public void slotsChanged(IInventory inventory) {
      this.tradeInventory.updateSellItem();
      super.slotsChanged(inventory);
   }

   public void setSelectionHint(int selectionHint) {
      this.tradeInventory.setSelectionHint(selectionHint);
   }

   public boolean stillValid(PlayerEntity player) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTraderXp() {
      return this.merchant.getVillagerXp();
   }

   @OnlyIn(Dist.CLIENT)
   public int getFutureTraderXp() {
      return this.tradeInventory.getFutureXp();
   }

   @OnlyIn(Dist.CLIENT)
   public void setXp(int xp) {
      this.merchant.overrideXp(xp);
   }

   @OnlyIn(Dist.CLIENT)
   public int getMerchantLevel() {
      return this.merchantLevel;
   }

   @OnlyIn(Dist.CLIENT)
   public void setMerchantLevel(int merchantLevel) {
      this.merchantLevel = merchantLevel;
   }

   @OnlyIn(Dist.CLIENT)
   public void setCanRestock(boolean canRestock) {
      this.canRestock = canRestock;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canRestock() {
      return true;
   }

   public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
      return false;
   }

   public ItemStack quickMoveStack(PlayerEntity player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack currentItem = slot.getItem();
         itemstack = currentItem.copy();
         if (index == 2) {
            if (!this.moveItemStackTo(currentItem, 3, 39, true)) {
               return ItemStack.EMPTY;
            }
            slot.onQuickCraft(currentItem, itemstack);
            this.playTradeSound();
         } else if (index != 0 && index != 1) {
            if (index >= 3 && index < 30) {
               if (!this.moveItemStackTo(currentItem, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (index >= 30 && index < 39 && !this.moveItemStackTo(currentItem, 3, 30, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(currentItem, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (currentItem.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (currentItem.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, currentItem);
      }

      return itemstack;
   }

   private void playTradeSound() {
      if (!this.merchant.getLevel().isClientSide) {
         Entity entity = (Entity) this.merchant;
         this.merchant.getLevel().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), this.merchant.getNotifyTradeSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
      }
   }

   public void removed(PlayerEntity player) {
      super.removed(player);
      this.merchant.setTradingPlayer((PlayerEntity) null);
      if (!this.merchant.getLevel().isClientSide) {
         if (!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).hasDisconnected()) {
            ItemStack itemstack = this.tradeInventory.removeItemNoUpdate(0);
            if (!itemstack.isEmpty()) {
               player.drop(itemstack, false);
            }

            itemstack = this.tradeInventory.removeItemNoUpdate(1);
            if (!itemstack.isEmpty()) {
               player.drop(itemstack, false);
            }
         } else {
            player.inventory.placeItemBackInInventory(player.level, this.tradeInventory.removeItemNoUpdate(0));
            player.inventory.placeItemBackInInventory(player.level, this.tradeInventory.removeItemNoUpdate(1));
         }
      }
   }

   public void tryMoveItems(int tradeIndex) {
      if (this.getOffers().size() > tradeIndex) {
         ItemStack itemstack = this.tradeInventory.getItem(0);
         if (!itemstack.isEmpty()) {
            if (!this.moveItemStackTo(itemstack, 3, 39, true)) {
               return;
            }
            this.tradeInventory.setItem(0, itemstack);
         }

         ItemStack itemstack1 = this.tradeInventory.getItem(1);
         if (!itemstack1.isEmpty()) {
            if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
               return;
            }
            this.tradeInventory.setItem(1, itemstack1);
         }

         if (this.tradeInventory.getItem(0).isEmpty() && this.tradeInventory.getItem(1).isEmpty()) {
            QuestOffer activeOffer = this.getOffers().get(tradeIndex);
            List<ItemStack> requiredItems = activeOffer.getQuest().getRequiredItems();

            if (!requiredItems.isEmpty()) {
               // Assuming there are at most 2 required items for simplicity
               if (!requiredItems.isEmpty()) {
                  ItemStack costA = requiredItems.get(0);
                  this.moveFromInventoryToPaymentSlot(0, costA);
               }
               if (requiredItems.size() > 1) {
                  ItemStack costB = requiredItems.get(1);
                  this.moveFromInventoryToPaymentSlot(1, costB);
               }
            }
         }
      }
   }

   private void moveFromInventoryToPaymentSlot(int slotIndex, ItemStack stack) {
      if (!stack.isEmpty()) {
         for (int i = 3; i < 39; ++i) {
            ItemStack currentStack = this.slots.get(i).getItem();
            if (!currentStack.isEmpty() && this.isSameItem(stack, currentStack)) {
               ItemStack tradeStack = this.tradeInventory.getItem(slotIndex);
               int tradeStackCount = tradeStack.isEmpty() ? 0 : tradeStack.getCount();
               int maxTransfer = Math.min(stack.getMaxStackSize() - tradeStackCount, currentStack.getCount());
               ItemStack transferStack = currentStack.copy();
               int newCount = tradeStackCount + maxTransfer;
               currentStack.shrink(maxTransfer);
               transferStack.setCount(newCount);
               this.tradeInventory.setItem(slotIndex, transferStack);
               if (newCount >= stack.getMaxStackSize()) {
                  break;
               }
            }
         }
      }
   }

   private boolean isSameItem(ItemStack stack1, ItemStack stack2) {
      return stack1.getItem() == stack2.getItem() && ItemStack.tagMatches(stack1, stack2);
   }

   @OnlyIn(Dist.CLIENT)
   public void setOffers(QuestOffers offers) {
      this.merchant.overrideQuestOffers(offers);
   }

   public QuestOffers getOffers() {
      return this.merchant.getQuestOffers();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean showProgressBar() {
      return this.showProgressBar;
   }

   public boolean hasOffers() {
      return this.getOffers() != null;
   }
}
