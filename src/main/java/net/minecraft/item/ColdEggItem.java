package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractEggEntity;
import net.minecraft.entity.projectile.ColdEggEntity;
import net.minecraft.world.World;

public class ColdEggItem extends AbstractEggItem {
   public ColdEggItem(Properties p_i48508_1_) {
      super(p_i48508_1_);
   }


   @Override
   public AbstractEggEntity getChickenEgg(World world, PlayerEntity player) {
      return new ColdEggEntity(world, player);
   };

}