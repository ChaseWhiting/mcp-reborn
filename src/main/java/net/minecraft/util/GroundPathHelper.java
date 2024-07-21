package net.minecraft.util;

import net.minecraft.entity.Mob;
import net.minecraft.pathfinding.GroundPathNavigator;

public class GroundPathHelper {
   public static boolean hasGroundPathNavigation(Mob p_242319_0_) {
      return p_242319_0_.getNavigation() instanceof GroundPathNavigator;
   }
}