package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MerchantInventory implements IInventory {
   private final IMerchant merchant;
   private final NonNullList<ItemStack> itemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
   @Nullable
   private MerchantOffer activeOffer;
   private int selectionHint;
   private int futureXp;

   public MerchantInventory(IMerchant merchant) {
      this.merchant = merchant;
   }

   @Override
   public int getContainerSize() {
      return this.itemStacks.size();
   }

   @Override
   public boolean isEmpty() {
      for(ItemStack itemStack : this.itemStacks) {
         if (!itemStack.isEmpty()) {
            return false;
         }
      }
      return true;
   }

   @Override
   public ItemStack getItem(int index) {
      return this.itemStacks.get(index);
   }

   @Override
   public ItemStack removeItem(int index, int count) {
      ItemStack itemStack = this.itemStacks.get(index);
      if (index == 2 && !itemStack.isEmpty()) {
         return ItemStackHelper.removeItem(this.itemStacks, index, itemStack.getCount());
      } else {
         ItemStack removedStack = ItemStackHelper.removeItem(this.itemStacks, index, count);
         if (!removedStack.isEmpty() && isPaymentSlot(index)) {
            updateSellItem();
         }
         return removedStack;
      }
   }

   private boolean isPaymentSlot(int index) {
      return index == 0 || index == 1;
   }

   @Override
   public ItemStack removeItemNoUpdate(int index) {
      return ItemStackHelper.takeItem(this.itemStacks, index);
   }

   @Override
   public void setItem(int index, ItemStack stack) {
      this.itemStacks.set(index, stack);
      if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
         stack.setCount(getMaxStackSize());
      }

      if (isPaymentSlot(index)) {
         updateSellItem();
      }
   }

   @Override
   public boolean stillValid(PlayerEntity player) {
      return this.merchant.getTradingPlayer() == player;
   }

   @Override
   public void setChanged() {
      updateSellItem();
   }

   public void updateSellItem() {
      this.activeOffer = null;
      ItemStack primaryPayment;
      ItemStack secondaryPayment;

      if (this.itemStacks.get(0).isEmpty()) {
         primaryPayment = this.itemStacks.get(1);
         secondaryPayment = ItemStack.EMPTY;
      } else {
         primaryPayment = this.itemStacks.get(0);
         secondaryPayment = this.itemStacks.get(1);
      }

      if (primaryPayment.isEmpty()) {
         setItem(2, ItemStack.EMPTY);
         this.futureXp = 0;
      } else {
         MerchantOffers offers = this.merchant.getOffers();
         if (!offers.isEmpty()) {
            MerchantOffer offer = offers.getRecipeFor(primaryPayment, secondaryPayment, this.selectionHint);
            if (offer == null || offer.isOutOfStock()) {
               this.activeOffer = offer;
               offer = offers.getRecipeFor(secondaryPayment, primaryPayment, this.selectionHint);
            }

            if (offer != null && !offer.isOutOfStock()) {
               this.activeOffer = offer;
               setItem(2, offer.assemble());
               this.futureXp = offer.getXp();
            } else {
               setItem(2, ItemStack.EMPTY);
               this.futureXp = 0;
            }
         }

         this.merchant.notifyTradeUpdated(this.getItem(2));
      }
   }

   @Nullable
   public MerchantOffer getActiveOffer() {
      return this.activeOffer;
   }

   public void setSelectionHint(int hint) {
      this.selectionHint = hint;
      updateSellItem();
   }

   @Override
   public void clearContent() {
      this.itemStacks.clear();
   }

   @OnlyIn(Dist.CLIENT)
   public int getFutureXp() {
      return this.futureXp;
   }
}
