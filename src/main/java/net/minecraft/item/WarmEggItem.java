package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractEggEntity;
import net.minecraft.entity.projectile.WarmEggEntity;
import net.minecraft.world.World;

public class WarmEggItem extends AbstractEggItem {
   public WarmEggItem(Properties p_i48508_1_) {
      super(p_i48508_1_);
   }


   @Override
   public AbstractEggEntity getChickenEgg(World world, PlayerEntity player) {
      return new WarmEggEntity(world, player);
   };

}