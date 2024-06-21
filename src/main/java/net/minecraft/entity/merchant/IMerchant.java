package net.minecraft.entity.merchant;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.QuestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.QuestOffers;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IMerchant {
   void setTradingPlayer(@Nullable PlayerEntity p_70932_1_);

   @Nullable
   PlayerEntity getTradingPlayer();

   MerchantOffers getOffers();

   QuestOffers getQuestOffers();

   @OnlyIn(Dist.CLIENT)
   void overrideOffers(@Nullable MerchantOffers p_213703_1_);

   @OnlyIn(Dist.CLIENT)
   void overrideQuestOffers(@Nullable QuestOffers offers);

   void notifyTrade(MerchantOffer p_213704_1_);

   void notifyTradeUpdated(ItemStack p_110297_1_);

   World getLevel();

   int getVillagerXp();

   void overrideXp(int p_213702_1_);

   boolean showProgressBar();

   SoundEvent getNotifyTradeSound();

   default boolean canRestock() {
      return false;
   }

   default void openTradingScreen(PlayerEntity player, ITextComponent displayName, int villagerLevel) {
      OptionalInt containerId = player.openMenu(new SimpleNamedContainerProvider((id, playerInventory, playerEntity) -> {
         return new MerchantContainer(id, playerInventory, this);
      }, displayName));

      if (containerId.isPresent()) {
         MerchantOffers offers = this.getOffers();
         if (!offers.isEmpty()) {
            player.sendMerchantOffers(containerId.getAsInt(), offers, villagerLevel, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
         }
      }
   }

   // Now, modify the method to open the QuestScreen
   default void openQuestScreen(PlayerEntity player, ITextComponent displayName) {
      OptionalInt containerId = player.openMenu(new SimpleNamedContainerProvider((id, playerInventory, playerEntity) -> {
         return new QuestContainer(id, playerInventory, this, player.getQuestManager());
      }, displayName));
      if (containerId.isPresent()) {
         // Send quest data to the client, if necessary
         // player.sendQuestData(containerId.getAsInt(), questData);
      }
   }


}