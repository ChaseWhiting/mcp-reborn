package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Creature;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.GroundPathHelper;

public class RestrictSunGoal extends Goal {
   private final Creature mob;

   public RestrictSunGoal(Creature p_i1652_1_) {
      this.mob = p_i1652_1_;
   }

   public boolean canUse() {
      return this.mob.level.isDay() && this.mob.getItemBySlot(EquipmentSlotType.HEAD).isEmpty() && GroundPathHelper.hasGroundPathNavigation(this.mob);
   }

   public void start() {
      ((GroundPathNavigator)this.mob.getNavigation()).setAvoidSun(true);
   }

   public void stop() {
      if (GroundPathHelper.hasGroundPathNavigation(this.mob)) {
         ((GroundPathNavigator)this.mob.getNavigation()).setAvoidSun(false);
      }

   }
}