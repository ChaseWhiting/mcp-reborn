package net.minecraft.potion;

import com.google.common.collect.ComparisonChain;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EffectInstance implements Comparable<EffectInstance> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Effect effect;
   private int duration;
   private int amplifier;
   private boolean splash;
   private boolean ambient;
   @OnlyIn(Dist.CLIENT)
   private boolean noCounter;
   private boolean visible;
   private boolean showIcon;
   @Nullable
   private EffectInstance hiddenEffect;

   public EffectInstance(Effect effect) {
      this(effect, 0, 0);
   }

   public EffectInstance(Effect effect, int duration) {
      this(effect, duration, 0);
   }

   public EffectInstance(Effect effect, int duration, int amplifier) {
      this(effect, duration, amplifier, false, true);
   }

   public EffectInstance(Effect effect, int duration, int level, boolean ambient, boolean visible) {
      this(effect, duration, level, ambient, visible, visible);
   }

   public EffectInstance(Effect effect, int duration, int level, boolean ambient, boolean visible, boolean showIcon) {
      this(effect, duration, level, ambient, visible, showIcon, null);
   }

   public EffectInstance(Effect effect, int duration, int level, boolean ambient, boolean visible, boolean showIcon, @Nullable EffectInstance hiddenEffect) {
      this.effect = effect;
      this.duration = duration;
      this.amplifier = level;
      this.ambient = ambient;
      this.visible = visible;
      this.showIcon = showIcon;
      this.hiddenEffect = hiddenEffect;
   }

   public EffectInstance(EffectInstance other) {
      this.effect = other.effect;
      this.setDetailsFrom(other);
   }


   void setDetailsFrom(EffectInstance p_230117_1_) {
      this.duration = p_230117_1_.duration;
      this.amplifier = p_230117_1_.amplifier;
      this.ambient = p_230117_1_.ambient;
      this.visible = p_230117_1_.visible;
      this.showIcon = p_230117_1_.showIcon;
   }

   public boolean update(EffectInstance p_199308_1_) {
      if (this.effect != p_199308_1_.effect) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      boolean flag = false;
      if (p_199308_1_.amplifier > this.amplifier) {
         if (p_199308_1_.duration < this.duration) {
            EffectInstance effectinstance = this.hiddenEffect;
            this.hiddenEffect = new EffectInstance(this);
            this.hiddenEffect.hiddenEffect = effectinstance;
         }

         this.amplifier = p_199308_1_.amplifier;
         this.duration = p_199308_1_.duration;
         flag = true;
      } else if (p_199308_1_.duration > this.duration) {
         if (p_199308_1_.amplifier == this.amplifier) {
            this.duration = p_199308_1_.duration;
            flag = true;
         } else if (this.hiddenEffect == null) {
            this.hiddenEffect = new EffectInstance(p_199308_1_);
         } else {
            this.hiddenEffect.update(p_199308_1_);
         }
      }

      if (!p_199308_1_.ambient && this.ambient || flag) {
         this.ambient = p_199308_1_.ambient;
         flag = true;
      }

      if (p_199308_1_.visible != this.visible) {
         this.visible = p_199308_1_.visible;
         flag = true;
      }

      if (p_199308_1_.showIcon != this.showIcon) {
         this.showIcon = p_199308_1_.showIcon;
         flag = true;
      }

      return flag;
   }

   public Effect getEffect() {
      return this.effect;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public boolean isAmbient() {
      return this.ambient;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public boolean showIcon() {
      return this.showIcon;
   }

   public boolean tick(LivingEntity p_76455_1_, Runnable p_76455_2_) {
      if (this.duration > 0) {
         if (this.effect.isDurationEffectTick(this.duration, this.amplifier)) {
            this.applyEffect(p_76455_1_);
         }

         this.tickDownDuration();
         if (this.duration == 0 && this.hiddenEffect != null) {
            this.setDetailsFrom(this.hiddenEffect);
            this.hiddenEffect = this.hiddenEffect.hiddenEffect;
            p_76455_2_.run();
         }
      }

      return this.duration > 0;
   }

   private int tickDownDuration() {
      if (this.hiddenEffect != null) {
         this.hiddenEffect.tickDownDuration();
      }

      return --this.duration;
   }

   public void applyEffect(LivingEntity p_76457_1_) {
      if (this.duration > 0) {
         this.effect.applyEffectTick(p_76457_1_, this.amplifier);
      }

   }

   public String getDescriptionId() {
      return this.effect.getDescriptionId();
   }

   public String toString() {
      String s;
      if (this.amplifier > 0) {
         s = this.getDescriptionId() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
      } else {
         s = this.getDescriptionId() + ", Duration: " + this.duration;
      }

      if (this.splash) {
         s = s + ", Splash: true";
      }

      if (!this.visible) {
         s = s + ", Particles: false";
      }

      if (!this.showIcon) {
         s = s + ", Show Icon: false";
      }

      return s;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof EffectInstance)) {
         return false;
      } else {
         EffectInstance effectinstance = (EffectInstance)p_equals_1_;
         return this.duration == effectinstance.duration && this.amplifier == effectinstance.amplifier && this.splash == effectinstance.splash && this.ambient == effectinstance.ambient && this.effect.equals(effectinstance.effect);
      }
   }

   public int hashCode() {
      int i = this.effect.hashCode();
      i = 31 * i + this.duration;
      i = 31 * i + this.amplifier;
      i = 31 * i + (this.splash ? 1 : 0);
      return 31 * i + (this.ambient ? 1 : 0);
   }

   public CompoundNBT save(CompoundNBT p_82719_1_) {
      p_82719_1_.putByte("Id", (byte)Effect.getId(this.getEffect()));
      this.writeDetailsTo(p_82719_1_);
      return p_82719_1_;
   }

   private void writeDetailsTo(CompoundNBT p_230119_1_) {
      p_230119_1_.putByte("Amplifier", (byte)this.getAmplifier());
      p_230119_1_.putInt("Duration", this.getDuration());
      p_230119_1_.putBoolean("Ambient", this.isAmbient());
      p_230119_1_.putBoolean("ShowParticles", this.isVisible());
      p_230119_1_.putBoolean("ShowIcon", this.showIcon());
      if (this.hiddenEffect != null) {
         CompoundNBT compoundnbt = new CompoundNBT();
         this.hiddenEffect.save(compoundnbt);
         p_230119_1_.put("HiddenEffect", compoundnbt);
      }

   }

   public static EffectInstance load(CompoundNBT p_82722_0_) {
      int i = p_82722_0_.getByte("Id");
      Effect effect = Effect.byId(i);
      return effect == null ? null : loadSpecifiedEffect(effect, p_82722_0_);
   }

   private static EffectInstance loadSpecifiedEffect(Effect p_230116_0_, CompoundNBT p_230116_1_) {
      int i = p_230116_1_.getByte("Amplifier");
      int j = p_230116_1_.getInt("Duration");
      boolean flag = p_230116_1_.getBoolean("Ambient");
      boolean flag1 = true;
      if (p_230116_1_.contains("ShowParticles", 1)) {
         flag1 = p_230116_1_.getBoolean("ShowParticles");
      }

      boolean flag2 = flag1;
      if (p_230116_1_.contains("ShowIcon", 1)) {
         flag2 = p_230116_1_.getBoolean("ShowIcon");
      }

      EffectInstance effectinstance = null;
      if (p_230116_1_.contains("HiddenEffect", 10)) {
         effectinstance = loadSpecifiedEffect(p_230116_0_, p_230116_1_.getCompound("HiddenEffect"));
      }

      return new EffectInstance(p_230116_0_, j, i < 0 ? 0 : i, flag, flag1, flag2, effectinstance);
   }

   @OnlyIn(Dist.CLIENT)
   public void setNoCounter(boolean p_100012_1_) {
      this.noCounter = p_100012_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNoCounter() {
      return this.noCounter;
   }

   public int compareTo(EffectInstance p_compareTo_1_) {
      int i = 32147;
      return (this.getDuration() <= 32147 || p_compareTo_1_.getDuration() <= 32147) && (!this.isAmbient() || !p_compareTo_1_.isAmbient()) ? ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getDuration(), p_compareTo_1_.getDuration()).compare(this.getEffect().getColor(), p_compareTo_1_.getEffect().getColor()).result() : ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getEffect().getColor(), p_compareTo_1_.getEffect().getColor()).result();
   }
}