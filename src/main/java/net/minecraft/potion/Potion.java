package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class Potion {
   private final String name;
   private final ImmutableList<EffectInstance> effects;

   public static Potion byName(String p_185168_0_) {
      return Registry.POTION.get(ResourceLocation.tryParse(p_185168_0_));
   }

   public Potion(EffectInstance... effectInstances) {
      this((String)null, effectInstances);
   }

   public Potion(@Nullable String p_i46740_1_, EffectInstance... p_i46740_2_) {
      this.name = p_i46740_1_;
      this.effects = ImmutableList.copyOf(p_i46740_2_);
   }

   public String getName(String p_185174_1_) {
      return p_185174_1_ + (this.name == null ? Registry.POTION.getKey(this).getPath() : this.name);
   }

   public List<EffectInstance> getEffects() {
      return this.effects;
   }

   public boolean hasInstantEffects() {
      if (!this.effects.isEmpty()) {
         for(EffectInstance effectinstance : this.effects) {
            if (effectinstance.getEffect().isInstantenous()) {
               return true;
            }
         }
      }

      return false;
   }
}