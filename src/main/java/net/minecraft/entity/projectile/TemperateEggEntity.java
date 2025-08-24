package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.WarmColdVariant;
import net.minecraft.world.World;

public class TemperateEggEntity extends AbstractEggEntity {
   public TemperateEggEntity(EntityType<? extends TemperateEggEntity> egg, World world) {
      super(egg, world);
   }

   public TemperateEggEntity(World level, double d0, double d1, double d2) {
      super(EntityType.EGG, level, d0, d1, d2);
   }

   public TemperateEggEntity(World world, LivingEntity owner) {
      super(EntityType.EGG, world, owner);
   }


   @Override
   public WarmColdVariant getChickenType() {
      return WarmColdVariant.TEMPERATE;
   }
}