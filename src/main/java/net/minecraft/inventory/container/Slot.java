package net.minecraft.inventory.container;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

public class Slot {
   private final int slot;
   public final IInventory container;
   public int index;
   public final int x;
   public final int y;

   public Slot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
      this.container = p_i1824_1_;
      this.slot = p_i1824_2_;
      this.x = p_i1824_3_;
      this.y = p_i1824_4_;
   }

   public void onQuickCraft(ItemStack p_75220_1_, ItemStack p_75220_2_) {
      int i = p_75220_2_.getCount() - p_75220_1_.getCount();
      if (i > 0) {
         this.onQuickCraft(p_75220_2_, i);
      }

   }

   protected void onQuickCraft(ItemStack p_75210_1_, int p_75210_2_) {
   }

   protected void onSwapCraft(int p_190900_1_) {
   }

   protected void checkTakeAchievements(ItemStack p_75208_1_) {
   }

   public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
      this.setChanged();
      return p_190901_2_;
   }

   public boolean mayPlace(ItemStack p_75214_1_) {
      return true;
   }

   public ItemStack getItem() {
      return this.container.getItem(this.slot);
   }

   public boolean hasItem() {
      return !this.getItem().isEmpty();
   }

   public void set(ItemStack p_75215_1_) {
      this.container.setItem(this.slot, p_75215_1_);
      this.setChanged();
   }

   public void setChanged() {
      this.container.setChanged();
   }

   public int getMaxStackSize() {
      return this.container.getMaxStackSize();
   }

   public int getMaxStackSize(ItemStack p_178170_1_) {
      return this.getMaxStackSize();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
      return null;
   }

   public ItemStack remove(int p_75209_1_) {
      return this.container.removeItem(this.slot, p_75209_1_);
   }

   public boolean mayPickup(PlayerEntity p_82869_1_) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isActive() {
      return true;
   }



   public ItemStack safeInsert(ItemStack p_150660_) {
      return this.safeInsert(p_150660_, p_150660_.getCount());
   }

   public ItemStack safeInsert(ItemStack p_150657_, int p_150658_) {
      if (!p_150657_.isEmpty() && this.mayPlace(p_150657_)) {
         ItemStack itemstack = this.getItem();
         int i = Math.min(Math.min(p_150658_, p_150657_.getCount()), this.getMaxStackSize(p_150657_) - itemstack.getCount());
         if (itemstack.isEmpty()) {
            this.set(p_150657_.split(i));
         } else if (ItemStack.tagMatches(itemstack, p_150657_)) {
            p_150657_.shrink(i);
            itemstack.grow(i);
            this.set(itemstack);
         }

         return p_150657_;
      } else {
         return p_150657_;
      }
   }

   public ItemStack safeTake(int p_150648_, int p_150649_, PlayerEntity p_150650_) {
      Optional<ItemStack> optional = this.tryRemove(p_150648_, p_150649_, p_150650_);
      optional.ifPresent((p_150655_) -> {
         this.onTake(p_150650_, p_150655_);
      });
      return optional.orElse(ItemStack.EMPTY);
   }

   public Optional<ItemStack> tryRemove(int p_150642_, int p_150643_, PlayerEntity p_150644_) {
      if (!this.mayPickup(p_150644_)) {
         return Optional.empty();
      } else if (!this.allowModification(p_150644_) && p_150643_ < this.getItem().getCount()) {
         return Optional.empty();
      } else {
         p_150642_ = Math.min(p_150642_, p_150643_);
         ItemStack itemstack = this.remove(p_150642_);
         if (itemstack.isEmpty()) {
            return Optional.empty();
         } else {
            if (this.getItem().isEmpty()) {
               this.set(itemstack);
            }

            return Optional.of(itemstack);
         }
      }
   }

   public boolean allowModification(PlayerEntity p_150652_) {
      return this.mayPickup(p_150652_) && this.mayPlace(this.getItem());
   }


}