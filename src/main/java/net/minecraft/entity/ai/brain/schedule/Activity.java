package net.minecraft.entity.ai.brain.schedule;

import net.minecraft.util.registry.Registry;

public class Activity {
   public static final Activity CORE = register("core");
   public static final Activity IDLE = register("idle");
   public static final Activity WORK = register("work");
   public static final Activity PLAY = register("play");
   public static final Activity REST = register("rest");
   public static final Activity MEET = register("meet");
   public static final Activity PANIC = register("panic");
   public static final Activity RAID = register("raid");
   public static final Activity PRE_RAID = register("pre_raid");
   public static final Activity TONGUE = Activity.register("tongue");
   public static final Activity SWIM = Activity.register("swim");
   public static final Activity LAY_SPAWN = Activity.register("lay_spawn");

   public static final Activity HIDE = register("hide");
   public static final Activity FIGHT = register("fight");
   public static final Activity CELEBRATE = register("celebrate");
   public static final Activity ADMIRE_ITEM = register("admire_item");
   public static final Activity AVOID = register("avoid");
   public static final Activity RIDE = register("ride");
   public static final Activity LONG_JUMP = Activity.register("long_jump");
   public static final Activity RAM = Activity.register("ram");
   public static final Activity PLAY_DEAD = Activity.register("play_dead");
   public static final Activity SNIFF = Activity.register("sniff");
   public static final Activity INVESTIGATE = Activity.register("investigate");
   public static final Activity ROAR = Activity.register("roar");
   public static final Activity EMERGE = Activity.register("emerge");
   public static final Activity DIG = Activity.register("dig");
   private final String name;
   private final int hashCode;

   private Activity(String p_i50141_1_) {
      this.name = p_i50141_1_;
      this.hashCode = p_i50141_1_.hashCode();
   }

   public String getName() {
      return this.name;
   }

   private static Activity register(String p_221363_0_) {
      return Registry.register(Registry.ACTIVITY, p_221363_0_, new Activity(p_221363_0_));
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Activity activity = (Activity)p_equals_1_;
         return this.name.equals(activity.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.hashCode;
   }

   @Override
   public String toString() {
      return "Activity{name='" + this.name + "'}";
   }
}