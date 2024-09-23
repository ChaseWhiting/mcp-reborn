package net.minecraft.fallout;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.Difficulty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Addiction {

    public static class Addictions {
        public static final Addiction MED_X_ADDICTION = new Addiction(Chem.MED_X_EFFECTS);
        public static final Addiction RAD_X_ADDICTION = new Addiction(Chem.RADAWAY_EFFECTS);
        public static final Addiction MENTATS_ADDICTION = new Addiction(Chem.MENTATS_EFFECTS);



    }

    public EffectInstance getBadAddictionEffect(EffectInstance effectInstance) {
        Effect effect = effectInstance.getEffect();
        Random random = new Random(effectInstance.hashCode());

        if (!effect.isBeneficial()) {
            return effectInstance;
        }

        if (effect == Effects.DIG_SPEED) {
            return new EffectInstance(Effects.DIG_SLOWDOWN, effectInstance.getDuration(), effectInstance.getAmplifier());
        } else if (effect == Effects.NIGHT_VISION) {
            return new EffectInstance(Effects.BLINDNESS, effectInstance.getDuration(), effectInstance.getAmplifier());
        } else if (effect == Effects.MOVEMENT_SPEED || effect == Effects.JUMP) {
            return new EffectInstance(Effects.MOVEMENT_SLOWDOWN, effectInstance.getDuration(), effectInstance.getAmplifier());
        } else if (effect == Effects.REGENERATION || effect == Effects.ABSORPTION || effect == Effects.HEALTH_BOOST) {
            return new EffectInstance(random.nextBoolean() ? Effects.POISON : Effects.WITHER, effectInstance.getDuration(), effectInstance.getAmplifier());
        } else if (effect == Effects.DAMAGE_BOOST) {
            return new EffectInstance(Effects.WEAKNESS, effectInstance.getDuration(), effectInstance.getAmplifier());
        } else if (effect == Effects.LUCK) {
            return new EffectInstance(Effects.UNLUCK, effectInstance.getDuration(), effectInstance.getAmplifier());
        } else {
            return new EffectInstance(Effects.CONFUSION, effectInstance.getDuration(), effectInstance.getAmplifier());
        }


    }


    private EffectInstance[] effectInstances;


    public Addiction(EffectInstance[] effects) {
        List<EffectInstance> tempList = new ArrayList<>();
        for (EffectInstance effectInstance : effects) {
            EffectInstance badEffect = getBadAddictionEffect(effectInstance);
            if (badEffect != null) {
                tempList.add(badEffect);
            }
        }
        effectInstances = tempList.toArray(new EffectInstance[0]);
    }


    public static class AddictionManager {
        private final PlayerEntity player;
        private int cooldown = 10;
        private List<Addiction> addictions;

        public void tick() {
            if (!addictions.isEmpty()) {
                if (!player.level.isClientSide) {
                    if (cooldown > 0) {
                        cooldown--;
                    } else {
                        Difficulty difficulty = player.level.getDifficulty();
                        float chancePerTick = switch (difficulty) {
                            case PEACEFUL -> 0.01F;
                            case EASY -> 0.05F;
                            case NORMAL -> 0.1F;
                            case HARD -> 0.2F;
                        };

                        Random random = new Random(player.level.getDayTime());
                        if (random.nextFloat() < chancePerTick) {
                            Addiction addiction = addictions.get(random.nextInt(addictions.size()));
                            EffectInstance chosenEffect = getRandomEffect(addiction.effectInstances);

                            if (!player.hasEffect(chosenEffect.getEffect())) {
                                player.addEffect(chosenEffect);
                                cooldown = getCooldown(difficulty);
                            }
                        }
                    }
                }
            }
        }

        private int getCooldown(Difficulty difficulty) {
            return switch (difficulty) {
                case PEACEFUL -> 95 * 20; // 30 seconds
                case EASY -> 80 * 20; // 20 seconds
                case NORMAL -> 50 * 20; // 10 seconds
                case HARD -> 30 * 20; // 5 seconds
            };
        }

        private EffectInstance getRandomEffect(EffectInstance[] effects) {
            if (effects == null || effects.length == 0) {
                return new EffectInstance(Effects.CONFUSION, 30 * 20, 1);
            }
            int index = new Random(player.level.getDayTime()).nextInt(effects.length);
            return effects[index];
        }

        public AddictionManager(PlayerEntity player) {
            this.player = player;
            this.addictions = new ArrayList<>();

        }

        public List<Addiction> getAddictions() {
            return new ArrayList<>(addictions);
        }

        public void addAddiction(Addiction addiction) {
            if (!addictions.contains(addiction)) {
                addictions.add(addiction);
            }
        }

        public boolean hasAddiction(Addiction addiction) {
            return addictions.contains(addiction);
        }

        public void removeAddiction(Addiction addiction) {
            addictions.remove(addiction);
        }

        public void addAdditionalSaveData(CompoundNBT nbt) {
            CompoundNBT addictionNBT = new CompoundNBT();
            addictionNBT.putBoolean("MedX", this.hasAddiction(Addictions.MED_X_ADDICTION));
            addictionNBT.putBoolean("RadAway", this.hasAddiction(Addictions.RAD_X_ADDICTION));
            addictionNBT.putBoolean("Mentats", this.hasAddiction(Addictions.MENTATS_ADDICTION));
            nbt.put("Addictions", addictionNBT);
        }

        public void readAdditionalSaveData(CompoundNBT nbt) {
            if (nbt.contains("Addictions")) {
                CompoundNBT addictionNBT = nbt.getCompound("Addictions");
                this.addictions.clear();
                if (addictionNBT.getBoolean("MedX")) {
                    this.addictions.add(Addictions.MED_X_ADDICTION);
                }
                if (addictionNBT.getBoolean("RadAway")) {
                    this.addictions.add(Addictions.RAD_X_ADDICTION);
                }
                if (addictionNBT.getBoolean("Mentats")) {
                    this.addictions.add(Addictions.MENTATS_ADDICTION);
                }
            }
        }

    }


}
