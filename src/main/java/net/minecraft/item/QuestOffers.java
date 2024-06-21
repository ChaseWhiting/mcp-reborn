package net.minecraft.item;

import java.util.ArrayList;
import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;

public class QuestOffers extends ArrayList<QuestOffer> {
   public QuestOffers() {
   }

   public QuestOffers(CompoundNBT nbt) {
      ListNBT listnbt = nbt.getList("Quests", 10);
      for (int i = 0; i < listnbt.size(); ++i) {
         this.add(new QuestOffer(listnbt.getCompound(i)));
      }
   }

   @Nullable
   public QuestOffer getOfferFor(ItemStack primaryPayment, ItemStack secondaryPayment, int selectionHint, PlayerEntity player) {
      if (selectionHint > 0 && selectionHint < this.size()) {
         QuestOffer questOffer = this.get(selectionHint);
         return questOffer.satisfiedBy(player) ? questOffer : null;
      } else {
         for (QuestOffer questOffer : this) {
            if (questOffer.satisfiedBy(player)) {
               return questOffer;
            }
         }
         return null;
      }
   }

   public void writeToStream(PacketBuffer buffer) {
      buffer.writeByte((byte) (this.size() & 255));
      for (QuestOffer questOffer : this) {
         buffer.writeNbt(questOffer.createTag());
      }
   }

   public static QuestOffers createFromStream(PacketBuffer buffer) {
      QuestOffers questOffers = new QuestOffers();
      int size = buffer.readByte() & 255;
      for (int i = 0; i < size; ++i) {
         CompoundNBT nbt = buffer.readNbt();
         questOffers.add(new QuestOffer(nbt));
      }
      return questOffers;
   }

   public CompoundNBT createTag() {
      CompoundNBT compoundnbt = new CompoundNBT();
      ListNBT listnbt = new ListNBT();
      for (QuestOffer questOffer : this) {
         listnbt.add(questOffer.createTag());
      }
      compoundnbt.put("Quests", listnbt);
      return compoundnbt;
   }
}
