package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.WarmColdVariant;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class ColdEggEntity extends AbstractEggEntity {
   public ColdEggEntity(EntityType<? extends ColdEggEntity> egg, World world) {
      super(egg, world);
   }

   public ColdEggEntity(World level, double d0, double d1, double d2) {
      super(EntityType.COLD_EGG, level, d0, d1, d2);
   }

   public ColdEggEntity(World world, LivingEntity owner) {
      super(EntityType.COLD_EGG, world, owner);
   }


   @Override
   public WarmColdVariant getChickenType() {
      return WarmColdVariant.COLD;
   }

   protected Item getDefaultItem() {
      return Items.COLD_EGG;
   }
}