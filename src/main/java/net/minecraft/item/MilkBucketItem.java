package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MilkBucketItem extends Item {
   public MilkBucketItem(Item.Properties p_i48481_1_) {
      super(p_i48481_1_);
   }

   public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity entity) {
      if (entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entity;
         CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemStack);
         serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
      }

      if (entity instanceof PlayerEntity && !((PlayerEntity) entity).abilities.instabuild) {
         itemStack.shrink(1);
      }

      if (!world.isClientSide) {
         if (!(entity.radiationManager.getLevel() > 5))
            entity.removeAllEffects();
      }

      return itemStack.isEmpty() ? new ItemStack(Items.BUCKET) : itemStack;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 32;
   }

   public UseAction getUseAnimation(ItemStack p_77661_1_) {
      return UseAction.DRINK;
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      return DrinkHelper.useDrink(p_77659_1_, p_77659_2_, p_77659_3_);
   }
}