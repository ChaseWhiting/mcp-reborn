package net.minecraft.item;

import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ExperienceBottleItem extends Item {
   public ExperienceBottleItem(Item.Properties p_i48500_1_) {
      super(p_i48500_1_);
   }

   public boolean isFoil(ItemStack p_77636_1_) {
      return true;
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!world.isClientSide) {
         ExperienceBottleEntity experiencebottleentity = new ExperienceBottleEntity(world, player);
         experiencebottleentity.setItem(itemstack);
         experiencebottleentity.shootFromRotation(player, player.xRot, player.yRot, -20.0F, 0.7F, 1.0F);
         world.addFreshEntity(experiencebottleentity);
      }

      player.awardStat(Stats.ITEM_USED.get(this));
      if (!player.abilities.instabuild) {
         itemstack.shrink(1);
      }

      return ActionResult.sidedSuccess(itemstack, world.isClientSide());
   }
}