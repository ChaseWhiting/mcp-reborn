package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class SoupItem extends Item {
   public SoupItem(Item.Properties p_i50054_1_) {
      super(p_i50054_1_);
   }

   public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity entity) {
      ItemStack itemstack = super.finishUsingItem(itemStack, world, entity);
      return entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.instabuild ? itemstack : new ItemStack(Items.BOWL);
   }
}