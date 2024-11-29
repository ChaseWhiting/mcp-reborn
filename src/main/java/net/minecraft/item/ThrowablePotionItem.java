package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ThrowablePotionItem extends PotionItem {
   public ThrowablePotionItem(Item.Properties p_i225739_1_) {
      super(p_i225739_1_);
   }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (!world.isClientSide) {
         PotionEntity potionentity = new PotionEntity(world, player);
         potionentity.setItem(itemstack);
         potionentity.shootFromRotation(player, player.xRot, player.yRot, -20.0F, 0.5F, 1.0F);
         world.addFreshEntity(potionentity);
      }

      player.awardStat(Stats.ITEM_USED.get(this));
      if (!player.abilities.instabuild) {
         itemstack.shrink(1);
      }

      return ActionResult.sidedSuccess(itemstack, world.isClientSide());
   }
}