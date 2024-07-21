package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class AmbientEntity extends Mob {
   protected AmbientEntity(EntityType<? extends AmbientEntity> p_i48570_1_, World p_i48570_2_) {
      super(p_i48570_1_, p_i48570_2_);
   }

   public boolean canBeLeashed(PlayerEntity p_184652_1_) {
      return false;
   }
}