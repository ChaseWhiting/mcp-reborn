package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;

public class AbsorptionEffect extends Effect {
   protected AbsorptionEffect(EffectType absorption, int effectLevel) {
      super(absorption, effectLevel);
   }

   public void removeAttributeModifiers(LivingEntity entity, AttributeModifierManager attribute, int effectLevel) {
      entity.setAbsorptionAmount(entity.getAbsorptionAmount() - (float)(4 * (effectLevel + 1)));
      super.removeAttributeModifiers(entity, attribute, effectLevel);
   }

   public void addAttributeModifiers(LivingEntity entity, AttributeModifierManager attribute, int effectLevel) {
      entity.setAbsorptionAmount(entity.getAbsorptionAmount() + (float)(4 * (effectLevel + 1)));
      super.addAttributeModifiers(entity, attribute, effectLevel);
   }
}