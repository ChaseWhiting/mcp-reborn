package net.minecraft.item.tab;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.Set;

public class ItemStackLinkedSet {
   private static final Hash.Strategy<ItemStack> TYPE_AND_TAG = new Hash.Strategy<ItemStack>() {
      public int hashCode(@Nullable ItemStack p_251266_) {
         return ItemStackLinkedSet.hashStackAndTag(p_251266_);
      }

      public boolean equals(@Nullable ItemStack p_250623_, @Nullable ItemStack p_251135_) {
         return p_250623_ == p_251135_ || p_250623_ != null && p_251135_ != null && p_250623_.isEmpty() == p_251135_.isEmpty() && ItemStack.isSameItemSameTags(p_250623_, p_251135_);
      }
   };

   static int hashStackAndTag(@Nullable ItemStack p_262160_) {
      if (p_262160_ != null) {
         CompoundNBT compoundtag = p_262160_.getTag();
         int i = 31 + p_262160_.getItem().hashCode();
         return 31 * i + (compoundtag == null ? 0 : compoundtag.hashCode());
      } else {
         return 0;
      }
   }

   public static Set<ItemStack> createTypeAndTagSet() {
      return new ObjectLinkedOpenCustomHashSet<>(TYPE_AND_TAG);
   }
}