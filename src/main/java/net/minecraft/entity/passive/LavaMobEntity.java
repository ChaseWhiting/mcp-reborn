package net.minecraft.entity.passive;

import net.minecraft.entity.Creature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class LavaMobEntity extends Creature {
   protected LavaMobEntity(EntityType<? extends LavaMobEntity> p_i48565_1_, World p_i48565_2_) {
      super(p_i48565_1_, p_i48565_2_);
      this.setPathfindingMalus(PathNodeType.LAVA, 0.0F);
      this.setPathfindingMalus(PathNodeType.WATER, 0.0F);
   }


   public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
      return p_205019_1_.isUnobstructed(this);
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   protected int getExperienceReward(PlayerEntity p_70693_1_) {
      return 1 + this.level.random.nextInt(3);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean canBeLeashed(PlayerEntity p_184652_1_) {
      return false;
   }
}