package net.minecraft.item;

import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class EnderPearlItem extends Item {
   public EnderPearlItem(Item.Properties p_i48501_1_) {
      super(p_i48501_1_);
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      player.getCooldowns().addCooldown(this, 20);
      if (!world.isClientSide) {
         EnderPearlEntity enderpearlentity = new EnderPearlEntity(world, player);
         enderpearlentity.setItem(itemstack);
         enderpearlentity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 1.5F, 1.0F);
         world.addFreshEntity(enderpearlentity);
      }

      player.awardStat(Stats.ITEM_USED.get(this));
      if (!player.abilities.instabuild) {
         itemstack.shrink(1);
      }

      return ActionResult.sidedSuccess(itemstack, world.isClientSide());
   }
}