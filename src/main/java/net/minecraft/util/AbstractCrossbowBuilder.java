package net.minecraft.util;

import net.minecraft.item.AbstractCrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.registry.Registry;


public class AbstractCrossbowBuilder {

    public static class Builder {
        private Item.Properties properties;
        private int id;
        private float[] shootingPower;
        private int[] chargeDuration;
        private int extraDamage = 0;
        private boolean isCrit;
        private boolean hasEffect = false;
        private int range;
        private String name;
        private EffectInstance[] effectInstances = new EffectInstance[]{};

        public Builder properties(Item.Properties properties) {
            this.properties = properties;
            return this;
        }

        public Builder(String resource) {
            name = resource;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder shootingPower(float[] shootingPower) {
            this.shootingPower = shootingPower.clone();
            return this;
        }

        public Builder chargeDuration(int[] chargeDuration) {
            this.chargeDuration = chargeDuration;
            return this;
        }

        public Builder isCrit(boolean isCrit) {
            this.isCrit = isCrit;
            return this;
        }

        public Builder addDamage(int extraDamage) {
            this.extraDamage += extraDamage;
            return this;
        }

        public Builder range(int range) {
            this.range = range;
            return this;
        }

        public Builder effects(EffectInstance... effects) {
            this.effectInstances = effects;
            this.hasEffect = true;
            return this;
        }

        public AbstractCrossbowItem build() {
            CrossbowConfig config = new CrossbowConfig(
                    shootingPower,
                    chargeDuration,
                    isCrit,
                    range,
                    extraDamage,
                    effectInstances
            );
            registerCrossbowConfig(id, name, config);
            AbstractCrossbowItem crossbow = new AbstractCrossbowItem(properties, config);
            registerCrossbow(name, crossbow);
            return crossbow;
        }
    }

    private static CrossbowConfig registerCrossbowConfig(int id, String name, CrossbowConfig config) {
        return Registry.registerMapping(Registry.CROSSBOW_CONFIG, id, name, config);
    }



    private static Item registerCrossbow(String resource, AbstractCrossbowItem crossbow) {
        return Items.registerItem(new ResourceLocation(resource), crossbow);
    }
}
