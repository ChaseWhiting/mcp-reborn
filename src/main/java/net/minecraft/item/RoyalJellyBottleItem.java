package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class RoyalJellyBottleItem extends Item {
   public RoyalJellyBottleItem(Properties p_i225737_1_) {
      super(p_i225737_1_);
   }

   public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity entity) {
      super.finishUsingItem(itemStack, world, entity);
      if (entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entity;
         CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemStack);
         serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
      }

      if (!world.isClientSide) {
         entity.removeEffect(Effects.POISON);
      }

      if (itemStack.isEmpty()) {
         return new ItemStack(Items.GLASS_BOTTLE);
      } else {
         if (entity instanceof PlayerEntity && !((PlayerEntity) entity).abilities.instabuild) {
            ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
            PlayerEntity playerentity = (PlayerEntity) entity;
            if (!playerentity.inventory.add(itemstack)) {
               playerentity.drop(itemstack, false);
            }
         }

         return itemStack;
      }
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 40;
   }

   public UseAction getUseAnimation(ItemStack p_77661_1_) {
      return UseAction.DRINK;
   }

   public SoundEvent getDrinkingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   public SoundEvent getEatingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      return DrinkHelper.useDrink(p_77659_1_, p_77659_2_, p_77659_3_);
   }
}