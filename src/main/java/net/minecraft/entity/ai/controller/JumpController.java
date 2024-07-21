package net.minecraft.entity.ai.controller;

import net.minecraft.entity.Mob;

public class JumpController {
   private final Mob mob;
   protected boolean jump;

   public JumpController(Mob p_i1612_1_) {
      this.mob = p_i1612_1_;
   }

   public void jump() {
      this.jump = true;
   }

   public void tick() {
      this.mob.setJumping(this.jump);
      this.jump = false;
   }
}