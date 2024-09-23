package net.minecraft.inventory.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.bundle.SlotAccess;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Container {
   private final NonNullList<ItemStack> lastSlots = NonNullList.create();
   public final List<Slot> slots = Lists.newArrayList();
   private final List<IntReferenceHolder> dataSlots = Lists.newArrayList();
   @Nullable
   private final ContainerType<?> menuType;
   public final int containerId;
   @OnlyIn(Dist.CLIENT)
   private short changeUid;
   private int quickcraftType = -1;
   private int quickcraftStatus;
   private final Set<Slot> quickcraftSlots = Sets.newHashSet();
   private final List<IContainerListener> containerListeners = Lists.newArrayList();
   private final Set<PlayerEntity> unSynchedPlayers = Sets.newHashSet();

   protected Container(@Nullable ContainerType<?> p_i50105_1_, int p_i50105_2_) {
      this.menuType = p_i50105_1_;
      this.containerId = p_i50105_2_;
   }

   public ItemStack getItem(int index) {
      return slots.get(index).getItem();
   }

   protected static boolean stillValid(IWorldPosCallable p_216963_0_, PlayerEntity p_216963_1_, Block p_216963_2_) {
      return p_216963_0_.evaluate((p_216960_2_, p_216960_3_) -> {
         return !p_216960_2_.getBlockState(p_216960_3_).is(p_216963_2_) ? false : p_216963_1_.distanceToSqr((double)p_216960_3_.getX() + 0.5D, (double)p_216960_3_.getY() + 0.5D, (double)p_216960_3_.getZ() + 0.5D) <= 64.0D;
      }, true);
   }

   public ContainerType<?> getType() {
      if (this.menuType == null) {
         throw new UnsupportedOperationException("Unable to construct this menu by type");
      } else {
         return this.menuType;
      }
   }

   protected static void checkContainerSize(IInventory p_216962_0_, int p_216962_1_) {
      int i = p_216962_0_.getContainerSize();
      if (i < p_216962_1_) {
         throw new IllegalArgumentException("Container size " + i + " is smaller than expected " + p_216962_1_);
      }
   }

   protected static void checkContainerDataCount(IIntArray p_216959_0_, int p_216959_1_) {
      int i = p_216959_0_.getCount();
      if (i < p_216959_1_) {
         throw new IllegalArgumentException("Container data count " + i + " is smaller than expected " + p_216959_1_);
      }
   }

   protected Slot addSlot(Slot p_75146_1_) {
      p_75146_1_.index = this.slots.size();
      this.slots.add(p_75146_1_);
      this.lastSlots.add(ItemStack.EMPTY);
      return p_75146_1_;
   }

   protected IntReferenceHolder addDataSlot(IntReferenceHolder p_216958_1_) {
      this.dataSlots.add(p_216958_1_);
      return p_216958_1_;
   }

   protected void addDataSlots(IIntArray p_216961_1_) {
      for(int i = 0; i < p_216961_1_.getCount(); ++i) {
         this.addDataSlot(IntReferenceHolder.forContainer(p_216961_1_, i));
      }

   }

   public void addSlotListener(IContainerListener p_75132_1_) {
      if (!this.containerListeners.contains(p_75132_1_)) {
         this.containerListeners.add(p_75132_1_);
         p_75132_1_.refreshContainer(this, this.getItems());
         this.broadcastChanges();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void removeSlotListener(IContainerListener p_82847_1_) {
      this.containerListeners.remove(p_82847_1_);
   }

   public NonNullList<ItemStack> getItems() {
      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(int i = 0; i < this.slots.size(); ++i) {
         nonnulllist.add(this.slots.get(i).getItem());
      }

      return nonnulllist;
   }

   public void broadcastChanges() {
      for(int i = 0; i < this.slots.size(); ++i) {
         ItemStack itemstack = this.slots.get(i).getItem();
         ItemStack itemstack1 = this.lastSlots.get(i);
         if (!ItemStack.matches(itemstack1, itemstack)) {
            ItemStack itemstack2 = itemstack.copy();
            this.lastSlots.set(i, itemstack2);

            for(IContainerListener icontainerlistener : this.containerListeners) {
               icontainerlistener.slotChanged(this, i, itemstack2);
            }
         }
      }

      for(int j = 0; j < this.dataSlots.size(); ++j) {
         IntReferenceHolder intreferenceholder = this.dataSlots.get(j);
         if (intreferenceholder.checkAndClearUpdateFlag()) {
            for(IContainerListener icontainerlistener1 : this.containerListeners) {
               icontainerlistener1.setContainerData(this, j, intreferenceholder.get());
            }
         }
      }

   }

   public boolean clickMenuButton(PlayerEntity p_75140_1_, int p_75140_2_) {
      return false;
   }

   public Slot getSlot(int p_75139_1_) {
      return this.slots.get(p_75139_1_);
   }

   public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
      Slot slot = this.slots.get(p_82846_2_);
      return slot != null ? slot.getItem() : ItemStack.EMPTY;
   }

   public ItemStack clicked(int p_184996_1_, int p_184996_2_, ClickType p_184996_3_, PlayerEntity p_184996_4_) {
      try {
         return this.doClick(p_184996_1_, p_184996_2_, p_184996_3_, p_184996_4_);
      } catch (Exception exception) {
         CrashReport crashreport = CrashReport.forThrowable(exception, "Container click");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Click info");
         crashreportcategory.setDetail("Menu BoggedType", () -> {
            return this.menuType != null ? Registry.MENU.getKey(this.menuType).toString() : "<no type>";
         });
         crashreportcategory.setDetail("Menu Class", () -> {
            return this.getClass().getCanonicalName();
         });
         crashreportcategory.setDetail("Slot Count", this.slots.size());
         crashreportcategory.setDetail("Slot", p_184996_1_);
         crashreportcategory.setDetail("Button", p_184996_2_);
         crashreportcategory.setDetail("BoggedType", p_184996_3_);
         throw new ReportedException(crashreport);
      }
   }

   private ItemStack doClick(int slotId, int mouseButton, ClickType clickType, PlayerEntity player) {
      ItemStack resultStack = ItemStack.EMPTY;
      PlayerInventory playerInventory = player.inventory;

      if (clickType == ClickType.QUICK_CRAFT) {
         int prevQuickCraftStatus = this.quickcraftStatus;
         this.quickcraftStatus = getQuickcraftHeader(mouseButton);

         if ((prevQuickCraftStatus != 1 || this.quickcraftStatus != 2) && prevQuickCraftStatus != this.quickcraftStatus) {
            this.resetQuickCraft();
         } else if (playerInventory.getCarried().isEmpty()) {
            this.resetQuickCraft();
         } else if (this.quickcraftStatus == 0) {
            this.quickcraftType = getQuickcraftType(mouseButton);
            if (isValidQuickcraftType(this.quickcraftType, player)) {
               this.quickcraftStatus = 1;
               this.quickcraftSlots.clear();
            } else {
               this.resetQuickCraft();
            }
         } else if (this.quickcraftStatus == 1) {
            Slot slot = this.slots.get(slotId);
            ItemStack carriedStack = playerInventory.getCarried();
            if (slot != null && canItemQuickReplace(slot, carriedStack, true) && slot.mayPlace(carriedStack)
                    && (this.quickcraftType == 2 || carriedStack.getCount() > this.quickcraftSlots.size())
                    && this.canDragTo(slot)) {
               this.quickcraftSlots.add(slot);
            }
         } else if (this.quickcraftStatus == 2) {
            if (!this.quickcraftSlots.isEmpty()) {
               ItemStack carriedStackCopy = playerInventory.getCarried().copy();
               int carriedCount = playerInventory.getCarried().getCount();

               for (Slot slot : this.quickcraftSlots) {
                  ItemStack currentCarriedStack = playerInventory.getCarried();
                  if (slot != null && canItemQuickReplace(slot, currentCarriedStack, true)
                          && slot.mayPlace(currentCarriedStack)
                          && (this.quickcraftType == 2 || currentCarriedStack.getCount() >= this.quickcraftSlots.size())
                          && this.canDragTo(slot)) {
                     ItemStack stackToPlace = carriedStackCopy.copy();
                     int slotItemCount = slot.hasItem() ? slot.getItem().getCount() : 0;
                     getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, stackToPlace, slotItemCount);
                     int maxCount = Math.min(stackToPlace.getMaxStackSize(), slot.getMaxStackSize(stackToPlace));
                     if (stackToPlace.getCount() > maxCount) {
                        stackToPlace.setCount(maxCount);
                     }

                     carriedCount -= stackToPlace.getCount() - slotItemCount;
                     slot.set(stackToPlace);
                  }
               }

               carriedStackCopy.setCount(carriedCount);
               playerInventory.setCarried(carriedStackCopy);
            }

            this.resetQuickCraft();
         } else {
            this.resetQuickCraft();
         }
      } else if (this.quickcraftStatus != 0) {
         this.resetQuickCraft();
      } else if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && (mouseButton == 0 || mouseButton == 1)) {
         ClickAction clickaction = mouseButton == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
         if (slotId == -999) {
            if (!playerInventory.getCarried().isEmpty()) {
               if (mouseButton == 0) {
                  player.drop(playerInventory.getCarried(), true);
                  playerInventory.setCarried(ItemStack.EMPTY);
               } else {
                  player.drop(playerInventory.getCarried().split(1), true);
               }
            }
         } else if (clickType == ClickType.QUICK_MOVE) {
            if (slotId < 0) {
               return ItemStack.EMPTY;
            }

            Slot slot = this.slots.get(slotId);
            if (slot == null || !slot.mayPickup(player)) {
               return ItemStack.EMPTY;
            }

            for (ItemStack movedStack = this.quickMoveStack(player, slotId);
                 !movedStack.isEmpty() && ItemStack.isSame(slot.getItem(), movedStack);
                 movedStack = this.quickMoveStack(player, slotId)) {
               resultStack = movedStack.copy();
            }
         } else {
            if (slotId < 0) {
               return ItemStack.EMPTY;
            }

            Slot slot = this.slots.get(slotId);
            if (slot != null) {
               ItemStack slotItemStack = slot.getItem();
               ItemStack carriedStack = playerInventory.getCarried();

               // Insert tryItemClickBehaviourOverride check here
               if (!this.tryItemClickBehaviourOverride(player, clickaction, slot, slotItemStack, carriedStack, playerInventory, clickType)) {
                  if (slotItemStack.isEmpty()) {
                     if (!carriedStack.isEmpty() && slot.mayPlace(carriedStack)) {
                        int toPlaceCount = mouseButton == 0 ? carriedStack.getCount() : 1;
                        if (toPlaceCount > slot.getMaxStackSize(carriedStack)) {
                           toPlaceCount = slot.getMaxStackSize(carriedStack);
                        }

                        slot.set(carriedStack.split(toPlaceCount));
                     }
                  } else if (slot.mayPickup(player)) {
                     if (carriedStack.isEmpty()) {
                        int toPickupCount = mouseButton == 0 ? slotItemStack.getCount() : (slotItemStack.getCount() + 1) / 2;
                        playerInventory.setCarried(slot.remove(toPickupCount));

                        if (slotItemStack.isEmpty()) {
                           slot.set(ItemStack.EMPTY);
                        }

                        slot.onTake(player, playerInventory.getCarried()); // Ensure this is called when the item is taken
                     } else if (slot.mayPlace(carriedStack)) {
                        if (consideredTheSameItem(slotItemStack, carriedStack)) {
                           int growCount = mouseButton == 0 ? carriedStack.getCount() : 1;
                           if (growCount > slot.getMaxStackSize(carriedStack) - slotItemStack.getCount()) {
                              growCount = slot.getMaxStackSize(carriedStack) - slotItemStack.getCount();
                           }

                           if (growCount > carriedStack.getMaxStackSize() - slotItemStack.getCount()) {
                              growCount = carriedStack.getMaxStackSize() - slotItemStack.getCount();
                           }

                           carriedStack.shrink(growCount);
                           slotItemStack.grow(growCount);
                        } else if (carriedStack.getCount() <= slot.getMaxStackSize(carriedStack)) {
                           slot.set(carriedStack);
                           playerInventory.setCarried(slotItemStack);
                        }
                     } else if (carriedStack.getMaxStackSize() > 1 && consideredTheSameItem(slotItemStack, carriedStack) && !slotItemStack.isEmpty()) {
                        int i3 = slotItemStack.getCount();
                        if (i3 + carriedStack.getCount() <= carriedStack.getMaxStackSize()) {
                           carriedStack.grow(i3);
                           slotItemStack = slot.remove(i3);
                           if (slotItemStack.isEmpty()) {
                              slot.set(ItemStack.EMPTY);
                           }

                           slot.onTake(player, playerInventory.getCarried()); // Ensure this is called when the item is taken
                        }
                     }
                  }

                  slot.setChanged();
               }

            }
         }
      } else if (clickType == ClickType.SWAP) {
         Slot slot = this.slots.get(slotId);
         ItemStack hotbarStack = playerInventory.getItem(mouseButton);
         ItemStack slotStack = slot.getItem();

         if (!hotbarStack.isEmpty() || !slotStack.isEmpty()) {
            if (hotbarStack.isEmpty()) {
               if (slot.mayPickup(player)) {
                  playerInventory.setItem(mouseButton, slotStack);
                  slot.onSwapCraft(slotStack.getCount());
                  slot.set(ItemStack.EMPTY);
                  slot.onTake(player, slotStack);
               }
            } else if (slotStack.isEmpty()) {
               if (slot.mayPlace(hotbarStack)) {
                  int maxStackSize = slot.getMaxStackSize(hotbarStack);
                  if (hotbarStack.getCount() > maxStackSize) {
                     slot.set(hotbarStack.split(maxStackSize));
                  } else {
                     slot.set(hotbarStack);
                     playerInventory.setItem(mouseButton, ItemStack.EMPTY);
                  }
               }
            } else if (slot.mayPickup(player) && slot.mayPlace(hotbarStack)) {
               int maxStackSize = slot.getMaxStackSize(hotbarStack);
               if (hotbarStack.getCount() > maxStackSize) {
                  slot.set(hotbarStack.split(maxStackSize));
                  slot.onTake(player, slotStack);
                  if (!playerInventory.add(slotStack)) {
                     player.drop(slotStack, true);
                  }
               } else {
                  slot.set(hotbarStack);
                  playerInventory.setItem(mouseButton, slotStack);
                  slot.onTake(player, slotStack);
               }
            }
         }
      } else if (clickType == ClickType.CLONE && player.abilities.instabuild && playerInventory.getCarried().isEmpty() && slotId >= 0) {
         Slot slot = this.slots.get(slotId);
         if (slot != null && slot.hasItem()) {
            ItemStack clonedStack = slot.getItem().copy();
            clonedStack.setCount(clonedStack.getMaxStackSize());
            playerInventory.setCarried(clonedStack);
         }
      } else if (clickType == ClickType.THROW && playerInventory.getCarried().isEmpty() && slotId >= 0) {
         Slot slot = this.slots.get(slotId);
         if (slot != null && slot.hasItem() && slot.mayPickup(player)) {
            ItemStack thrownStack = slot.remove(mouseButton == 0 ? 1 : slot.getItem().getCount());
            slot.onTake(player, thrownStack);
            player.drop(thrownStack, true);
         }
      } else if (clickType == ClickType.PICKUP_ALL && slotId >= 0) {
         Slot slot = this.slots.get(slotId);
         ItemStack carriedStack = playerInventory.getCarried();
         if (!carriedStack.isEmpty() && (slot == null || !slot.hasItem() || !slot.mayPickup(player))) {
            int start = mouseButton == 0 ? 0 : this.slots.size() - 1;
            int direction = mouseButton == 0 ? 1 : -1;

            for (int round = 0; round < 2; ++round) {
               for (int i = start; i >= 0 && i < this.slots.size() && carriedStack.getCount() < carriedStack.getMaxStackSize(); i += direction) {
                  Slot pickAllSlot = this.slots.get(i);
                  if (pickAllSlot.hasItem() && canItemQuickReplace(pickAllSlot, carriedStack, true)
                          && pickAllSlot.mayPickup(player)
                          && this.canTakeItemForPickAll(carriedStack, pickAllSlot)) {
                     ItemStack pickAllSlotStack = pickAllSlot.getItem();
                     if (round != 0 || pickAllSlotStack.getCount() != pickAllSlotStack.getMaxStackSize()) {
                        int amountToMove = Math.min(carriedStack.getMaxStackSize() - carriedStack.getCount(), pickAllSlotStack.getCount());
                        ItemStack movedStack = pickAllSlot.remove(amountToMove);
                        carriedStack.grow(amountToMove);
                        if (movedStack.isEmpty()) {
                           pickAllSlot.set(ItemStack.EMPTY);
                        }

                        pickAllSlot.onTake(player, movedStack);
                     }
                  }
               }
            }
         }

         this.broadcastChanges();
      }

      return resultStack;
   }


   public static boolean consideredTheSameItem(ItemStack p_195929_0_, ItemStack p_195929_1_) {
      return p_195929_0_.getItem() == p_195929_1_.getItem() && ItemStack.tagMatches(p_195929_0_, p_195929_1_);
   }

   public boolean canTakeItemForPickAll(ItemStack p_94530_1_, Slot p_94530_2_) {
      return true;
   }

   public void removed(PlayerEntity p_75134_1_) {
      PlayerInventory playerinventory = p_75134_1_.inventory;
      if (!playerinventory.getCarried().isEmpty()) {
         p_75134_1_.drop(playerinventory.getCarried(), false);
         playerinventory.setCarried(ItemStack.EMPTY);
      }

   }

   protected void clearContainer(PlayerEntity p_193327_1_, World p_193327_2_, IInventory p_193327_3_) {
      if (!p_193327_1_.isAlive() || p_193327_1_ instanceof ServerPlayerEntity && ((ServerPlayerEntity)p_193327_1_).hasDisconnected()) {
         for(int j = 0; j < p_193327_3_.getContainerSize(); ++j) {
            p_193327_1_.drop(p_193327_3_.removeItemNoUpdate(j), false);
         }

      } else {
         for(int i = 0; i < p_193327_3_.getContainerSize(); ++i) {
            p_193327_1_.inventory.placeItemBackInInventory(p_193327_2_, p_193327_3_.removeItemNoUpdate(i));
         }

      }
   }

   public void slotsChanged(IInventory p_75130_1_) {
      this.broadcastChanges();
   }

   public void setItem(int p_75141_1_, ItemStack p_75141_2_) {
      this.getSlot(p_75141_1_).set(p_75141_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setAll(List<ItemStack> p_190896_1_) {
      for(int i = 0; i < p_190896_1_.size(); ++i) {
         this.getSlot(i).set(p_190896_1_.get(i));
      }

   }

   public void setData(int p_75137_1_, int p_75137_2_) {
      this.dataSlots.get(p_75137_1_).set(p_75137_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public short backup(PlayerInventory p_75136_1_) {
      ++this.changeUid;
      return this.changeUid;
   }

   public boolean isSynched(PlayerEntity p_75129_1_) {
      return !this.unSynchedPlayers.contains(p_75129_1_);
   }

   public void setSynched(PlayerEntity p_75128_1_, boolean p_75128_2_) {
      if (p_75128_2_) {
         this.unSynchedPlayers.remove(p_75128_1_);
      } else {
         this.unSynchedPlayers.add(p_75128_1_);
      }

   }

   public abstract boolean stillValid(PlayerEntity p_75145_1_);

   protected boolean moveItemStackTo(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_) {
      boolean flag = false;
      int i = p_75135_2_;
      if (p_75135_4_) {
         i = p_75135_3_ - 1;
      }

      if (p_75135_1_.isStackable()) {
         while(!p_75135_1_.isEmpty()) {
            if (p_75135_4_) {
               if (i < p_75135_2_) {
                  break;
               }
            } else if (i >= p_75135_3_) {
               break;
            }

            Slot slot = this.slots.get(i);
            ItemStack itemstack = slot.getItem();
            if (!itemstack.isEmpty() && consideredTheSameItem(p_75135_1_, itemstack)) {
               int j = itemstack.getCount() + p_75135_1_.getCount();
               if (j <= p_75135_1_.getMaxStackSize()) {
                  p_75135_1_.setCount(0);
                  itemstack.setCount(j);
                  slot.setChanged();
                  flag = true;
               } else if (itemstack.getCount() < p_75135_1_.getMaxStackSize()) {
                  p_75135_1_.shrink(p_75135_1_.getMaxStackSize() - itemstack.getCount());
                  itemstack.setCount(p_75135_1_.getMaxStackSize());
                  slot.setChanged();
                  flag = true;
               }
            }

            if (p_75135_4_) {
               --i;
            } else {
               ++i;
            }
         }
      }

      if (!p_75135_1_.isEmpty()) {
         if (p_75135_4_) {
            i = p_75135_3_ - 1;
         } else {
            i = p_75135_2_;
         }

         while(true) {
            if (p_75135_4_) {
               if (i < p_75135_2_) {
                  break;
               }
            } else if (i >= p_75135_3_) {
               break;
            }

            Slot slot1 = this.slots.get(i);
            ItemStack itemstack1 = slot1.getItem();
            if (itemstack1.isEmpty() && slot1.mayPlace(p_75135_1_)) {
               if (p_75135_1_.getCount() > slot1.getMaxStackSize()) {
                  slot1.set(p_75135_1_.split(slot1.getMaxStackSize()));
               } else {
                  slot1.set(p_75135_1_.split(p_75135_1_.getCount()));
               }

               slot1.setChanged();
               flag = true;
               break;
            }

            if (p_75135_4_) {
               --i;
            } else {
               ++i;
            }
         }
      }

      return flag;
   }

   public static int getQuickcraftType(int p_94529_0_) {
      return p_94529_0_ >> 2 & 3;
   }

   public static int getQuickcraftHeader(int p_94532_0_) {
      return p_94532_0_ & 3;
   }

   @OnlyIn(Dist.CLIENT)
   public static int getQuickcraftMask(int p_94534_0_, int p_94534_1_) {
      return p_94534_0_ & 3 | (p_94534_1_ & 3) << 2;
   }

   public static boolean isValidQuickcraftType(int p_180610_0_, PlayerEntity p_180610_1_) {
      if (p_180610_0_ == 0) {
         return true;
      } else if (p_180610_0_ == 1) {
         return true;
      } else {
         return p_180610_0_ == 2 && p_180610_1_.abilities.instabuild;
      }
   }

   protected void resetQuickCraft() {
      this.quickcraftStatus = 0;
      this.quickcraftSlots.clear();
   }

   public static boolean canItemQuickReplace(@Nullable Slot p_94527_0_, ItemStack p_94527_1_, boolean p_94527_2_) {
      boolean flag = p_94527_0_ == null || !p_94527_0_.hasItem();
      if (!flag && p_94527_1_.sameItem(p_94527_0_.getItem()) && ItemStack.tagMatches(p_94527_0_.getItem(), p_94527_1_)) {
         return p_94527_0_.getItem().getCount() + (p_94527_2_ ? 0 : p_94527_1_.getCount()) <= p_94527_1_.getMaxStackSize();
      } else {
         return flag;
      }
   }

   public static void getQuickCraftSlotCount(Set<Slot> p_94525_0_, int p_94525_1_, ItemStack p_94525_2_, int p_94525_3_) {
      switch(p_94525_1_) {
      case 0:
         p_94525_2_.setCount(MathHelper.floor((float)p_94525_2_.getCount() / (float)p_94525_0_.size()));
         break;
      case 1:
         p_94525_2_.setCount(1);
         break;
      case 2:
         p_94525_2_.setCount(p_94525_2_.getItem().getMaxStackSize());
      }

      p_94525_2_.grow(p_94525_3_);
   }

   public boolean canDragTo(Slot p_94531_1_) {
      return true;
   }

   public static int getRedstoneSignalFromBlockEntity(@Nullable TileEntity p_178144_0_) {
      return p_178144_0_ instanceof IInventory ? getRedstoneSignalFromContainer((IInventory)p_178144_0_) : 0;
   }

   public static int getRedstoneSignalFromContainer(@Nullable IInventory p_94526_0_) {
      if (p_94526_0_ == null) {
         return 0;
      } else {
         int i = 0;
         float f = 0.0F;

         for(int j = 0; j < p_94526_0_.getContainerSize(); ++j) {
            ItemStack itemstack = p_94526_0_.getItem(j);
            if (!itemstack.isEmpty()) {
               f += (float)itemstack.getCount() / (float)Math.min(p_94526_0_.getMaxStackSize(), itemstack.getMaxStackSize());
               ++i;
            }
         }

         f = f / (float)p_94526_0_.getContainerSize();
         return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
      }
   }

   private boolean tryItemClickBehaviourOverride(PlayerEntity p_249615_, ClickAction p_250300_, Slot p_249384_, ItemStack p_251073_, ItemStack p_252026_, PlayerInventory playerInventory, ClickType clickType) {
      if (p_252026_.overrideStackedOnOther(p_249384_, p_250300_, p_249615_, clickType)) {
         return true;
      } else {
         return p_251073_.overrideOtherStackedOnMe(p_252026_, p_249384_, p_250300_, p_249615_, createCarriedSlotAccess(playerInventory), clickType);
      }
   }

   private SlotAccess createCarriedSlotAccess(PlayerInventory playerInventory) {
      return new SlotAccess() {
         public ItemStack get() {
            return playerInventory.getCarried();
         }

         public boolean set(ItemStack p_150452_) {
            playerInventory.setCarried(p_150452_);
            return true;
         }
      };
   }

}