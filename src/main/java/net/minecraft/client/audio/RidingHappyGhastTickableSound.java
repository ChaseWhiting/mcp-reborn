package net.minecraft.client.audio;

import net.minecraft.entity.happy_ghast.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RidingHappyGhastTickableSound extends TickableSound {
   private final PlayerEntity player;
   private final HappyGhastEntity happyGhast;

   public RidingHappyGhastTickableSound(PlayerEntity player, HappyGhastEntity ghast) {
      super(SoundEvents.HAPPY_GHAST_RIDING, ghast.getSoundSource());
      this.player = player;
      this.happyGhast = ghast;
      this.attenuation = AttenuationType.NONE;
      this.looping = true;
      this.delay = 0;
      this.volume = 0.0F;
   }

   @Override
   public boolean canPlaySound() {
      return true;
   }


   @Override
   public boolean canStartSilent() {
      return true;
   }

   @Override
   public void tick() {
      if (this.happyGhast.removed || !this.player.isPassenger() || this.player.getVehicle() != this.happyGhast) {
         this.stop();
         return;
      }
      float f = (float)this.happyGhast.getDeltaMovement().horizontalDistance();
      if (f >= 0.01f) {
         this.volume = MathHelper.clampedLerp(0.2f, 0.75f, f);  // minimum 0.2 for audibility
      } else if (this.happyGhast.isRidden()) {
         this.volume = 0.2f;  // still play a soft idle hum
      } else {
         this.volume = 0.0f;
      }
   }

}