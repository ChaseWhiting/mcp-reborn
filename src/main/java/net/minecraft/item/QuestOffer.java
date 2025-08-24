package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.villager.data.quest.Quest;
import net.minecraft.nbt.CompoundNBT;

public class QuestOffer {
   private Quest quest;

   public QuestOffer(CompoundNBT compoundNBT) {
      this.quest = Quest.fromNBT(compoundNBT);
   }

   public QuestOffer(Quest quest) {
      this.quest = quest;
   }

   public Quest getQuest() {
      return this.quest;
   }

   public void completeQuest(PlayerEntity player) {
      if (quest.isCompleted()) {
         quest.giveRewards(player);
      }
   }

   public void resetQuest() {
      quest.reset();
   }

   public CompoundNBT createTag() {
      return quest.toNBT();
   }

   public boolean satisfiedBy(PlayerEntity player) {
      for (ItemStack requiredItem : quest.getRequiredItems()) {
         if (!player.inventory.contains(requiredItem)) {
            return false;
         }
      }

      return quest.hasKills(quest, player);
   }

   public boolean isOutOfStock() {
      return quest.isCompleted();
   }

   public boolean take(PlayerEntity player) {
      if (!satisfiedBy(player)) {
         return false;
      }

      for (ItemStack requiredItem : quest.getRequiredItems()) {
         player.inventory.removeItem(requiredItem);
      }

      return true;
   }
}
