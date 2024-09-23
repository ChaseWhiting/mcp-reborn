package net.minecraft.entity;

import net.minecraft.entity.projectile.FrisbeeEntity;
import net.minecraft.item.FrisbeeData;
import net.minecraft.item.ItemStack;

public interface IFrisbeeUser {

   /**
    * Mobs that implement this interface should handle the frisbee-throwing behavior.
    *
    * @param frisbee The Frisbee entity being thrown.
    * @param velocity The speed at which the Frisbee is thrown.
    */
   void throwFrisbee(FrisbeeEntity frisbee, float velocity);

   /**
    * Get the current target of the mob, if applicable.
    *
    * @return The current target entity, or null if no target is set.
    */
   LivingEntity getTarget();

   /**
    * Called when the mob successfully throws the frisbee.
    */
   void onFrisbeeThrown();

   /**
    * Retrieve the item representing the frisbee being thrown.
    *
    * @return The item stack of the frisbee.
    */
   ItemStack getFrisbeeItem();

   /**
    * Perform the frisbee-throwing attack, handling the actual throw mechanics.
    *
    * @param shooter The entity throwing the frisbee.
    * @param velocity The speed at which the frisbee should be thrown.
    */
   default void performFrisbeeThrow(LivingEntity shooter, float velocity) {
      ItemStack frisbeeItem = getFrisbeeItem();
      if (!frisbeeItem.isEmpty()) {
         FrisbeeEntity frisbeeEntity = new FrisbeeEntity(EntityType.FRISBEE, shooter, shooter.level, getFrisbeeData(), frisbeeItem, true);
         frisbeeEntity.setOwner(shooter);
         frisbeeEntity.shootFromRotation(shooter, shooter.xRot, shooter.yRot, 0.0F, velocity, 1.0F);

         // Trigger any custom behavior on throw
         this.throwFrisbee(frisbeeEntity, velocity);

         // Register the entity in the world
         shooter.level.addFreshEntity(frisbeeEntity);

         // Call the method to handle what happens after the throw
         this.onFrisbeeThrown();
      }
   }

   /**
    * Retrieve the data associated with the frisbee (range, speed, etc.)
    *
    * @return Frisbee data containing various settings.
    */
   FrisbeeData getFrisbeeData();
}
