package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class EggItem extends Item {
   public EggItem(Item.Properties p_i48508_1_) {
      super(p_i48508_1_);
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!world.isClientSide) {
         EggEntity eggentity = new EggEntity(world, player);
         eggentity.setItem(itemstack);
         eggentity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 1.5F, 1.0F);
         world.addFreshEntity(eggentity);
      }

      player.awardStat(Stats.ITEM_USED.get(this));
      if (!player.abilities.instabuild) {
         itemstack.shrink(1);
      }

      return ActionResult.sidedSuccess(itemstack, world.isClientSide());
   }
}