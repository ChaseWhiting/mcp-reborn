package net.minecraft.util;

import net.minecraft.potion.EffectInstance;

import javax.annotation.Nullable;

public class CrossbowConfig {
    private final float[] shootingPower;
    private final int[] chargeDuration;
    private final boolean isCrit;
    private final int extraDamage;
    private final int range;
    private final EffectInstance[] effectInstances;

    public CrossbowConfig(float[] shootingPower, int[] chargeDuration, boolean isCrit, int range, int extraDamage, @Nullable EffectInstance... effects) {
        this.shootingPower = shootingPower.clone();
        this.chargeDuration = chargeDuration;
        this.isCrit = isCrit;
        this.extraDamage = extraDamage;
        this.range = range;
        if (effects != null) {
            this.effectInstances = effects;
        } else {
            this.effectInstances = new EffectInstance[]{};
        }
    }

    public float[] getShootingPower() {
        return shootingPower;
    }

    public int[] getChargeDuration() {
        return chargeDuration;
    }

    public boolean isCrit() {
        return isCrit;
    }

    public int getExtraDamage() {
        return extraDamage;
    }

    public int getRange() {
        return range;
    }

    public EffectInstance[] getEffectInstances() {
        return effectInstances;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CrossbowConfig{");
        sb.append("shootingPower=").append(java.util.Arrays.toString(shootingPower));
        sb.append(", chargeDuration=").append(java.util.Arrays.toString(chargeDuration));
        sb.append(", isCrit=").append(isCrit);
        sb.append(", extraDamage=").append(extraDamage);
        sb.append(", range=").append(range);
        sb.append(", effectInstances=");
        if (effectInstances != null && effectInstances.length > 0) {
            sb.append("[");
            for (int i = 0; i < effectInstances.length; i++) {
                sb.append(effectInstances[i].toString());
                if (i < effectInstances.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        } else {
            sb.append("[]");
        }
        sb.append('}');
        return sb.toString();
    }
}
