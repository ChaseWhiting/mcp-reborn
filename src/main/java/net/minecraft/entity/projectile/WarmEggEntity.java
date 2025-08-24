package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.WarmColdVariant;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class WarmEggEntity extends AbstractEggEntity {
   public WarmEggEntity(EntityType<? extends WarmEggEntity> egg, World world) {
      super(egg, world);
   }

   public WarmEggEntity(World level, double d0, double d1, double d2) {
      super(EntityType.WARM_EGG, level, d0, d1, d2);
   }

   public WarmEggEntity(World world, LivingEntity owner) {
      super(EntityType.WARM_EGG, world, owner);
   }


   @Override
   public WarmColdVariant getChickenType() {
      return WarmColdVariant.WARM;
   }

   protected Item getDefaultItem() {
      return Items.WARM_EGG;
   }
}