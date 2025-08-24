package net.minecraft.inventory.container;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

import java.util.List;

public abstract class AbstractRepairContainer extends Container {
   protected final CraftResultInventory resultSlots = new CraftResultInventory();
   protected final IInventory inputSlots;
   protected final IWorldPosCallable access;
   protected final PlayerEntity player;
   private final int resultSlotIndex;
   final List<Integer> inputSlotIndexes;


   public int getResultSlot() {
      return this.resultSlotIndex;
   }

   protected abstract boolean mayPickup(PlayerEntity p_230303_1_, boolean p_230303_2_);

   protected abstract ItemStack onTake(PlayerEntity p_230301_1_, ItemStack p_230301_2_);

   protected abstract boolean isValidBlock(BlockState p_230302_1_);

   public AbstractRepairContainer(@Nullable ContainerType<?> p_i231587_1_, int p_i231587_2_, PlayerInventory p_i231587_3_, IWorldPosCallable p_i231587_4_) {
      super(p_i231587_1_, p_i231587_2_);
      this.access = p_i231587_4_;
      this.player = p_i231587_3_.player;
      ItemCombinerMenuSlotDefinition itemCombinerMenuSlotDefinition = this.createInputSlotDefinitions();
      this.inputSlots = this.createContainer(itemCombinerMenuSlotDefinition.getNumOfInputSlots());
      this.inputSlotIndexes = itemCombinerMenuSlotDefinition.getInputSlotIndexes();
      this.resultSlotIndex = itemCombinerMenuSlotDefinition.getResultSlotIndex();
      this.createInputSlots(itemCombinerMenuSlotDefinition);
      this.createResultSlot(itemCombinerMenuSlotDefinition);
      this.createInventorySlots(p_i231587_3_);
   }

   protected abstract ItemCombinerMenuSlotDefinition createInputSlotDefinitions();

   private void createInputSlots(ItemCombinerMenuSlotDefinition itemCombinerMenuSlotDefinition) {
      for (final ItemCombinerMenuSlotDefinition.SlotDefinition slotDefinition : itemCombinerMenuSlotDefinition.getSlots()) {
         this.addSlot(new Slot(this.inputSlots, slotDefinition.slotIndex(), slotDefinition.x, slotDefinition.y){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
               return slotDefinition.mayPlace.test(itemStack);
            }
         });
      }
   }

   private IInventory createContainer(int n) {
      return new Inventory(n){

         @Override
         public void setChanged() {
            super.setChanged();
            AbstractRepairContainer.this.slotsChanged(this);
         }
      };
   }

   private void createResultSlot(ItemCombinerMenuSlotDefinition itemCombinerMenuSlotDefinition) {
      this.addSlot(new Slot(this.resultSlots, itemCombinerMenuSlotDefinition.getResultSlot().slotIndex(), itemCombinerMenuSlotDefinition.getResultSlot().x, itemCombinerMenuSlotDefinition.getResultSlot().y){

         @Override
         public boolean mayPlace(ItemStack itemStack) {
            return false;
         }

         @Override
         public boolean mayPickup(PlayerEntity player) {
            return AbstractRepairContainer.this.mayPickup(player, this.hasItem());
         }

         @Override
         public ItemStack onTake(PlayerEntity player, ItemStack itemStack) {
            return AbstractRepairContainer.this.onTake(player, itemStack);
         }
      });
   }

   private void createInventorySlots(IInventory inventory) {
      int n;
      for (n = 0; n < 3; ++n) {
         for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i + n * 9 + 9, 8 + i * 18, 84 + n * 18));
         }
      }
      for (n = 0; n < 9; ++n) {
         this.addSlot(new Slot(inventory, n, 8 + n * 18, 142));
      }
   }

   public abstract void createResult();

   public void slotsChanged(IInventory p_75130_1_) {
      super.slotsChanged(p_75130_1_);
      if (p_75130_1_ == this.inputSlots) {
         this.createResult();
      }

   }

   public void removed(PlayerEntity p_75134_1_) {
      super.removed(p_75134_1_);
      this.access.execute((p_234647_2_, p_234647_3_) -> {
         this.clearContainer(p_75134_1_, p_234647_2_, this.inputSlots);
      });
   }

   public boolean stillValid(PlayerEntity p_75145_1_) {
      return this.access.evaluate((p_234646_2_, p_234646_3_) -> {
         return !this.isValidBlock(p_234646_2_.getBlockState(p_234646_3_)) ? false : p_75145_1_.distanceToSqr((double)p_234646_3_.getX() + 0.5D, (double)p_234646_3_.getY() + 0.5D, (double)p_234646_3_.getZ() + 0.5D) <= 64.0D;
      }, true);
   }

   protected boolean shouldQuickMoveToAdditionalSlot(ItemStack p_241210_1_) {
      return false;
   }

   public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_82846_2_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == 2) {
            if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         } else if (p_82846_2_ != 0 && p_82846_2_ != 1) {
            if (p_82846_2_ >= 3 && p_82846_2_ < 39) {
               int i = this.shouldQuickMoveToAdditionalSlot(itemstack) ? 1 : 0;
               if (!this.moveItemStackTo(itemstack1, i, 2, false)) {
                  return ItemStack.EMPTY;
               }
            }
         } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p_82846_1_, itemstack1);
      }

      return itemstack;
   }



   public int getSlotToQuickMoveTo(ItemStack itemStack) {
      return this.inputSlots.isEmpty() ? 0 : this.inputSlotIndexes.get(0);
   }

   protected int getInventorySlotStart() {
      return this.getResultSlot() + 1;
   }

   protected int getInventorySlotEnd() {
      return this.getInventorySlotStart() + 27;
   }

   protected int getUseRowStart() {
      return this.getInventorySlotEnd();
   }

   protected int getUseRowEnd() {
      return this.getUseRowStart() + 9;
   }

   protected boolean canMoveIntoInputSlots(ItemStack itemStack) {
      return true;
   }
}