package net.minecraft.util;

public enum Hand {
   MAIN_HAND,
   OFF_HAND;



   public Hand other() {
      return this == MAIN_HAND ? OFF_HAND : MAIN_HAND;
   }
}