package net.minecraft.client.audio;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MusicTicker {
   private final Random random = new Random();
   private final Minecraft minecraft;
   @Nullable
   private ISound currentMusic;
   private int nextSongDelay = 100;

   public MusicTicker(Minecraft p_i45112_1_) {
      this.minecraft = p_i45112_1_;
   }

   public void tick() {
      BackgroundMusicSelector backgroundmusicselector = this.minecraft.getSituationalMusic();
      if (this.currentMusic != null) {
         if (!backgroundmusicselector.getEvent().getLocation().equals(this.currentMusic.getLocation()) && backgroundmusicselector.replaceCurrentMusic()) {
            this.minecraft.getSoundManager().stop(this.currentMusic);
            this.nextSongDelay = MathHelper.nextInt(this.random, 0, backgroundmusicselector.getMinDelay() / 2);
         }

         if (!this.minecraft.getSoundManager().isActive(this.currentMusic)) {
            this.currentMusic = null;
            this.nextSongDelay = Math.min(this.nextSongDelay, MathHelper.nextInt(this.random, backgroundmusicselector.getMinDelay(), backgroundmusicselector.getMaxDelay()));
         }
      }

      this.nextSongDelay = Math.min(this.nextSongDelay, backgroundmusicselector.getMaxDelay());
      if (this.currentMusic == null && this.nextSongDelay-- <= 0) {
         this.startPlaying(backgroundmusicselector);
      }

   }

   public void startPlaying(BackgroundMusicSelector backgroundMusicSelector) {
      this.currentMusic = SimpleSound.forMusic(backgroundMusicSelector.getEvent());
      if (this.currentMusic.getSound() != SoundHandler.EMPTY_SOUND) {
         this.minecraft.getSoundManager().play(this.currentMusic);
      }

      this.nextSongDelay = MathHelper.nextInt(this.random, backgroundMusicSelector.getMinDelay(), backgroundMusicSelector.getMaxDelay());;
   }

   public void stopPlaying() {
      if (this.currentMusic != null) {
         this.minecraft.getSoundManager().stop(this.currentMusic);
         this.currentMusic = null;
      }

      this.nextSongDelay += 100;
   }

   public float getMusicVolume() {
      return this.currentMusic == null ? 0f : this.currentMusic.getVolume();
   }

   public void setMusicVolume(float volume) {
      if (this.currentMusic==null)return;
   }

   public boolean isPlayingMusic(BackgroundMusicSelector p_239540_1_) {
      return this.currentMusic == null ? false : p_239540_1_.getEvent().getLocation().equals(this.currentMusic.getLocation());
   }
}