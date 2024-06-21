package net.minecraft.inventory.container;

import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.villager.data.quest.QuestManager;
import net.minecraft.inventory.QuestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.QuestOffer;
import net.minecraft.stats.Stats;
import net.minecraft.inventory.container.Slot;

public class QuestResultSlot extends Slot {
   private final QuestInventory slots;
   private final PlayerEntity player;
   private int removeCount;
   private final IMerchant merchant;
   private final QuestManager questManager;

   public QuestResultSlot(PlayerEntity playerEntity, IMerchant iMerchant, QuestInventory questInventory, int index, int xPosition, int yPosition) {
      super(questInventory, index, xPosition, yPosition);
      this.player = playerEntity;
      this.merchant = iMerchant;
      this.questManager = playerEntity.getQuestManager();
      this.slots = questInventory;
   }

   @Override
   public boolean mayPlace(ItemStack stack) {
      return false;
   }

   @Override
   public ItemStack remove(int amount) {
      if (this.hasItem()) {
         this.removeCount += Math.min(amount, this.getItem().getCount());
      }

      return super.remove(amount);
   }

   @Override
   protected void onQuickCraft(ItemStack stack, int amount) {
      this.removeCount += amount;
      this.checkTakeAchievements(stack);
   }

   @Override
   protected void checkTakeAchievements(ItemStack stack) {
      stack.onCraftedBy(this.player.level, this.player, this.removeCount);
      this.removeCount = 0;
   }

   @Override
   public ItemStack onTake(PlayerEntity player, ItemStack stack) {
      this.checkTakeAchievements(stack);
      QuestOffer questOffer = this.slots.getActiveOffer();
      if (questOffer != null) {
         if (questOffer.take(player)) {
            questOffer.completeQuest(player);
            player.awardStat(Stats.TRADED_WITH_VILLAGER);
         }

         this.merchant.overrideXp(this.merchant.getVillagerXp() + questOffer.getQuest().getXp());
      }

      return stack;
   }
}
