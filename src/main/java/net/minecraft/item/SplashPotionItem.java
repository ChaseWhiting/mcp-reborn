package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SplashPotionItem extends ThrowablePotionItem {
   public SplashPotionItem(Item.Properties p_i48463_1_) {
      super(p_i48463_1_);
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      return super.use(world, player, hand);
   }
}