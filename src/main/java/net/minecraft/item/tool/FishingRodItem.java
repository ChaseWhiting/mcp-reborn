package net.minecraft.item.tool;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class FishingRodItem extends Item implements IVanishable {
   public FishingRodItem(Item.Properties p_i48494_1_) {
      super(p_i48494_1_);
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (player.fishing != null) {
         if (!world.isClientSide) {
            int i = player.fishing.retrieve(itemstack);
            itemstack.hurt(i, player);
         }

         world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
      } else {
         world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         if (!world.isClientSide) {
            int k = EnchantmentHelper.getFishingSpeedBonus(itemstack);
            int j = EnchantmentHelper.getFishingLuckBonus(itemstack);
            world.addFreshEntity(new FishingBobberEntity(player, world, j, k));
         }

         player.awardStat(Stats.ITEM_USED.get(this));
         player.gameEvent(GameEvent.ITEM_INTERACT_START);
      }

      return ActionResult.sidedSuccess(itemstack, world.isClientSide());
   }

   public int getEnchantmentValue() {
      return 1;
   }

   public int getWeight(ItemStack bundle) {
      return 8;
   }
}