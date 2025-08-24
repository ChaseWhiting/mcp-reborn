package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

import java.util.UUID;

public class LifeforceEffect extends Effect {

    private int lastDuration = -1;
    private boolean applied = false;
    private double savedHealth = 0;
    private double healthToAdd;

    private static final UUID HEALTH_EFFECT = UUID.fromString("585cb35c-2aa4-4df3-a360-0ff174e960ec");
    private static AttributeModifier HEALTH_MOD = new AttributeModifier(HEALTH_EFFECT, "Health Bonus", 0.0D, AttributeModifier.Operation.ADDITION);

    public LifeforceEffect() {
        super(EffectType.BENEFICIAL, 16722020);
    }

    public int getColor() {
        return 16722020;
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!applied) {
            applied = true;
            this.savedHealth = entity.getAttributeBaseValue(Attributes.MAX_HEALTH);
        }

        if (lastDuration > 1 && this.savedHealth != 0) {
            healthToAdd = ((amplifier + 2) * (savedHealth * savedHealth) / 100);

            HEALTH_MOD = new AttributeModifier(HEALTH_EFFECT, "Health Bonus", this.healthToAdd, AttributeModifier.Operation.ADDITION);
            if (!entity.getAttribute(Attributes.MAX_HEALTH).hasModifierForID(HEALTH_EFFECT)) {
                entity.getAttribute(Attributes.MAX_HEALTH).addTransientModifier(HEALTH_MOD);

            }
            if (entity.getAttribute(Attributes.MAX_HEALTH).hasModifierForID(HEALTH_EFFECT) && entity.getAttribute(Attributes.MAX_HEALTH).getModifier(HEALTH_EFFECT).getAmount() != healthToAdd) {
                entity.getAttribute(Attributes.MAX_HEALTH).removeModifier(HEALTH_MOD);
            }
        }

        if (lastDuration == 1) {
            if (entity.getAttribute(Attributes.MAX_HEALTH).hasModifierForID(HEALTH_EFFECT)) {
                entity.getAttribute(Attributes.MAX_HEALTH).removeModifier(HEALTH_MOD);
            }
            entity.setHealth((float) savedHealth);
            applied = false;
        }
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeModifierManager manager, int amplifier) {
        ModifiableAttributeInstance modifiableattributeinstance = manager.getInstance(Attributes.MAX_HEALTH);
        if (modifiableattributeinstance != null && modifiableattributeinstance.hasModifierForID(HEALTH_EFFECT)) {
            modifiableattributeinstance.removeModifier(HEALTH_EFFECT);
            entity.setHealth((float) savedHealth);

        }
    }
}