package net.minecraft.fallout;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GeigerCounterSound;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.Difficulty;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class Radiation {

    public static boolean effected(LivingEntity entity) {
        return !(entity instanceof ZombieEntity) && !(entity instanceof ZombieHorseEntity) && !(entity instanceof AbstractSkeletonEntity) && !(entity instanceof SkeletonHorseEntity)
                && !(entity instanceof EnderDragonEntity) && !(entity instanceof WitherEntity);
    }

    public static class RadiationResistance {
        private static final float DEFAULT_RESISTANCE = 0.25F;
        private static final float MAX_ALLOWED_RESISTANCE = 0.9F;
        private final LivingEntity entity;
        private float currentResistance;
        private float maxResistance;

        public RadiationResistance(LivingEntity entity) {
            this.entity = entity;
            this.currentResistance = DEFAULT_RESISTANCE;
            this.maxResistance = DEFAULT_RESISTANCE;
        }

        public void tick() {
            if (this.currentResistance > maxResistance) {
                this.currentResistance = maxResistance;
            }
        }

        public void setResistance(float resistance) {
            this.maxResistance = Math.min(resistance, MAX_ALLOWED_RESISTANCE);
            this.currentResistance = this.maxResistance;
        }

        public static int calculateRadiation(int originalRadiation, LivingEntity entity) {
            float effectiveResistance = Math.min(entity.radResistance, MAX_ALLOWED_RESISTANCE);

            int reducedRadiation = Math.round(originalRadiation * (1 - effectiveResistance));

            int minRadiation = Math.max(1, originalRadiation / 10);

            return Math.max(reducedRadiation, minRadiation);
        }

        public float getCurrentResistance() {
            return this.currentResistance;
        }

        public float getMaxResistance() {
            return this.maxResistance;
        }

        public void increaseResistance(float amount) {
            this.setResistance(this.currentResistance + amount);
        }

        public void decreaseResistance(float amount) {
            this.currentResistance = Math.max(0, this.currentResistance - amount);
        }
    }


    public static int radDefaultThreshold = 2000;

    public static int radPerLevel(int rads) {
        if (rads < 2000) {
            return 0;
        }
        if (rads < 4000) {
            return 1;
        } else if (rads < 5000) {
            return 2;
        } else if (rads < 6000) {
            return 3;
        } else if (rads < 7000) {
            return 4;
        } else if (rads < 8500) {
            return 5;
        } else if (rads <= 8500) {
            return 6;
        } else {
            return 6 + (rads - 8500) / 2000 + 1;
        }
    }


    public static int cooldown(Difficulty difficulty) {
        if (difficulty == Difficulty.EASY) {
            return 300;
        } else if (difficulty == Difficulty.NORMAL) {
            return 200;
        } else if (difficulty == Difficulty.HARD) {
            return 100;
        } else {
            return 500;
        }
    }


    public static class RadiationManager {

        public EffectInstance randomForLevel(int level) {
            switch (level) {
                case 1:
                    return getRandomEffect(effects.levelOneEffects);
                case 2:
                    return getRandomEffect(effects.levelTwoEffects);
                case 3:
                    return getRandomEffect(effects.levelThreeEffects);
                case 4:
                    return getRandomEffect(effects.levelFourEffects);
                case 5:
                    return getRandomEffect(effects.levelFiveEffects);
                default:
                    if (level == 6) {
                        return getRandomEffect(effects.overLevelFiveEffects);
                    } else {
                        return effects.getEffectOver6(level, entity);
                    }
            }
        }


        private final LivingEntity entity;
        private int rads;
        private int prevRads;
        private int cooldown = 80;
        private boolean radIncreasing = false;
        private RadiationResistance radiationResistance;
        private final Map<String, Integer> resistanceBuffs = new HashMap<>(); // Buff name to remaining ticks
        private float baseResistance = 0.25F;

        public RadiationManager(LivingEntity entity) {
            this.entity = entity;
            this.radiationResistance = new RadiationResistance(entity);
        }

        private EffectInstance getRandomEffect(EffectInstance[] effects) {
            if (effects == null || effects.length == 0) {
                throw new IllegalArgumentException("Effect array is null or empty");
            }
            int index = new Random(entity.level.getDayTime()).nextInt(effects.length);
            return effects[index];
        }

        public int getRads() {
            return rads;
        }


        public int getPrevRads() {
            return prevRads;
        }

        public void setCooldown(int cooldown) {
            this.cooldown = cooldown;
        }


        public int getLevel(int rads) {
            return Radiation.radPerLevel(rads);
        }

        public int getLevel() {
            return Radiation.radPerLevel(this.rads);
        }

        public boolean gainingRads() {
            return radIncreasing;
        }

        public void tick() {
            updateResistanceBuffs();
            radiationResistance.tick();
            if (rads > prevRads) {
                radIncreasing = true;
            } else {
                radIncreasing = false;
            }

            if(this.gainingRads() && entity instanceof PlayerEntity) {
                Minecraft.getInstance().getSoundManager().queueTickingSound(new GeigerCounterSound(((PlayerEntity)entity), getLevel()));
            }



            if (!entity.level.isClientSide) {
                entity.rads = rads;
                if (cooldown < 0) {
                    cooldown = 0;
                } else if (cooldown > 0 && rads >= Radiation.radDefaultThreshold) {
                    cooldown--;
                }
                if (Radiation.effected(entity) && Radiation.radPerLevel(rads) > 0)
                    applyEffects(entity);
            }
            prevRads = rads;
        }


        private void updateResistanceBuffs() {
            Iterator<Map.Entry<String, Integer>> iterator = resistanceBuffs.entrySet().iterator();
            float totalResistance = baseResistance;

            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                int remainingTicks = entry.getValue();
                if (remainingTicks > 0) {
                    totalResistance += getResistanceForBuff(entry.getKey());
                    entry.setValue(remainingTicks - 1);
                } else {
                    iterator.remove(); // Remove expired buffs
                }
            }

            // Update the radiation resistance with the accumulated value
            radiationResistance.setResistance(Math.min(totalResistance, 0.9F));
        }

        private float getResistanceForBuff(String buffName) {
            // Implement logic to determine how much resistance each buff should provide
            if (buffName.equals("RadX")) {
                return 0.15F; // Example value for Rad-X
            }
            return 0.0F;
        }

        public void addResistanceBuff(String buffName, int durationTicks) {
            // Add or refresh the buff
            resistanceBuffs.put(buffName, durationTicks);
        }

        public void setRads(int rads) {
            this.rads = rads;
            if(cooldown == 0) {
                setCooldown(Radiation.cooldown(entity.level.getDifficulty()));
            }
            if (this.rads < 0) {
                this.rads = 0;
            }
        }


        public void applyEffects(LivingEntity entity) {
            Difficulty difficulty = entity.level.getDifficulty();
            float chancePerTick = switch (difficulty) {
                case PEACEFUL -> 0.01F;
                case EASY -> 0.07F;
                case NORMAL -> 0.1F;
                case HARD -> 0.2F;
            };

            Random random = new Random(entity.level.getDayTime());

            if (cooldown <= 0 && random.nextFloat() < chancePerTick || entity.tickCount % Radiation.cooldown(difficulty) == 0 && cooldown == 0 && random.nextBoolean()) {
                EffectInstance chosenEffect;
                int attempts = 0;
                do {
                    chosenEffect = randomForLevel(getLevel(rads));
                    attempts++;
                } while (entity.hasEffect(chosenEffect.getEffect()) && attempts < 10);

                if (!entity.hasEffect(chosenEffect.getEffect())) {
                    setCooldown(Radiation.cooldown(difficulty));
                    entity.addEffect(chosenEffect);
                }
            }
        }

        public void readAdditionalSaveData(CompoundNBT nbt) {
            if (nbt.contains("Rads")) {
                this.rads = nbt.getInt("Rads");
                this.cooldown = nbt.getInt("RadCooldownTillEffect");
                this.radIncreasing = nbt.getBoolean("RadsIncreasing");
                radiationResistance.setResistance(nbt.getFloat("RadResistance"));
            }

            // Load resistance buffs
            if (nbt.contains("ResistanceBuffs")) {
                ListNBT buffsList = nbt.getList("ResistanceBuffs", 10); // 10 is the type ID for CompoundNBT
                resistanceBuffs.clear(); // Clear existing buffs before loading new ones
                for (int i = 0; i < buffsList.size(); i++) {
                    CompoundNBT buffTag = buffsList.getCompound(i);
                    String buffName = buffTag.getString("BuffName");
                    int remainingTicks = buffTag.getInt("RemainingTicks");
                    resistanceBuffs.put(buffName, remainingTicks);
                }
            }
        }

        public void addAdditionalSaveData(CompoundNBT nbt) {
            nbt.putInt("Rads", rads);
            nbt.putInt("RadCooldownTillEffect", cooldown);
            nbt.putInt("RadLevel", Radiation.radPerLevel(rads));
            nbt.putBoolean("RadsIncreasing", radIncreasing);
            nbt.putFloat("RadResistance", radiationResistance.currentResistance);

            // Save resistance buffs
            ListNBT buffsList = new ListNBT();
            for (Map.Entry<String, Integer> entry : resistanceBuffs.entrySet()) {
                CompoundNBT buffTag = new CompoundNBT();
                buffTag.putString("BuffName", entry.getKey());
                buffTag.putInt("RemainingTicks", entry.getValue());
                buffsList.add(buffTag);
            }
            nbt.put("ResistanceBuffs", buffsList);
        }



    }


    public static class effects {
        public static EffectInstance[] levelOneEffects = new EffectInstance[]{
                new EffectInstance(Effects.CONFUSION, 600, 0),
                new EffectInstance(Effects.WEAKNESS, 600, 0),
        };

        public static EffectInstance[] levelTwoEffects = new EffectInstance[]{
                new EffectInstance(Effects.CONFUSION, 1200, 1),
                new EffectInstance(Effects.WEAKNESS, 1200, 1),
                new EffectInstance(Effects.POISON, 600, 0),
        };

        public static EffectInstance[] levelThreeEffects = new EffectInstance[]{
                new EffectInstance(Effects.CONFUSION, 1800, 2),
                new EffectInstance(Effects.WEAKNESS, 1800, 2),
                new EffectInstance(Effects.POISON, 1200, 1),
                new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 600, 0),
        };

        public static EffectInstance[] levelFourEffects = new EffectInstance[]{
                new EffectInstance(Effects.CONFUSION, 2400, 3),
                new EffectInstance(Effects.WEAKNESS, 2400, 3),
                new EffectInstance(Effects.POISON, 1800, 2),
                new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 1200, 1),
                new EffectInstance(Effects.BLINDNESS, 600, 0),
        };

        public static EffectInstance[] levelFiveEffects = new EffectInstance[]{
                new EffectInstance(Effects.CONFUSION, 3000, 4),
                new EffectInstance(Effects.WEAKNESS, 3000, 4),
                new EffectInstance(Effects.POISON, 2400, 3),
                new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 1800, 2),
                new EffectInstance(Effects.BLINDNESS, 1200, 1),
                new EffectInstance(Effects.WITHER, 600, 0),
        };

        public static EffectInstance[] overLevelFiveEffects = new EffectInstance[]{
                new EffectInstance(Effects.CONFUSION, 3000, 4),
                new EffectInstance(Effects.WEAKNESS, 3000, 4),
                new EffectInstance(Effects.POISON, 2400, 3),
                new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 1800, 2),
                new EffectInstance(Effects.BLINDNESS, 1200, 1),
                new EffectInstance(Effects.WITHER, 600, 1),
                new EffectInstance(Effects.HUNGER, 30 * 20, 2),
        };

        private static EffectInstance getRandomEffect(EffectInstance[] effects, LivingEntity entity) {
            if (effects == null || effects.length == 0) {
                throw new IllegalArgumentException("Effect array is null or empty");
            }
            int index = new Random(entity.level.getDayTime()).nextInt(effects.length);
            return effects[index];
        }

        public static EffectInstance getEffectOver6(int radLevel, LivingEntity entity) {

            EffectInstance[] eff = new EffectInstance[]{
                    new EffectInstance(Effects.CONFUSION, 160 + 5 * radLevel * 20, radLevel - 1),
                    new EffectInstance(Effects.WEAKNESS, 190 + 20 * radLevel * 20, radLevel - 1),
                    new EffectInstance(Effects.POISON, 90 + 3 * radLevel * 20, radLevel - 1),
                    new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 160 + 15 * radLevel * 20, radLevel - 1),
                    new EffectInstance(Effects.BLINDNESS, 60 + 10 * radLevel * 20, radLevel - 1),
                    new EffectInstance(Effects.WITHER, 30 + 10 * radLevel * 20, radLevel - 1),
                    new EffectInstance(Effects.HUNGER, 3200, radLevel - 1),
            };

            return getRandomEffect(eff, entity);
        }


    }


}
