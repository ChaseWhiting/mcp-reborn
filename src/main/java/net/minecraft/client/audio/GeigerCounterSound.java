package net.minecraft.client.audio;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GeigerCounterSound extends TickableSound {
   private final PlayerEntity player;
   private final int radiationLevel;

   public GeigerCounterSound(PlayerEntity player, int radiationLevel) {
      super(SoundEvents.PIP_RADIATION, SoundCategory.PLAYERS);
      this.player = player;
      this.radiationLevel = radiationLevel;
      this.x = player.getX();
      this.y = player.getY();
      this.z = player.getZ();
      this.looping = false;
      this.delay = 0;
      this.volume = 1.0F;
      this.pitch = 1.0F;
   }

   @Override
   public void tick() {
      if (!this.player.isAlive()) {
         this.stop();
      } else {
         this.x = this.player.getX();
         this.y = this.player.getY();
         this.z = this.player.getZ();
         this.updateVolumeAndPitch();
      }
   }


   private void updateVolumeAndPitch() {
      // Modify pitch and volume based on radiation level
      this.volume = 1.0F;
      this.pitch = 1.0F;
   }

   @Override
   public boolean canStartSilent() {
      return true;
   }

   @Override
   public boolean canPlaySound() {
      return player.radiationManager.gainingRads();
   }
}
