package net.minecraft.entity.terraria.boss;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.world.Difficulty;

public enum BossPhase {
    MAX_HEALTH("MAX_HEALTH"),
    BELOW_MINIMUM("BELOW_MAX");

    public final String name;

    BossPhase(String id) {
        this.name = id;
    }

    public static BossPhase fromName(String name) {
        return switch (name) {
            case "MAX_HEALTH" -> MAX_HEALTH;
            case "BELOW_MAX" -> BELOW_MINIMUM;
            default -> MAX_HEALTH;
        };
    }

    public static BossPhase getCurrentPhase(Monster monster, Difficulty difficulty) {
        if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD || monster.veryHardmode()) {
            if (!isHealthBelowPercentage(monster, 65)) {
                return MAX_HEALTH;
            } else {
                return BELOW_MINIMUM;
            }
        } else {
            if (!isHealthBelowPercentage(monster, 50)) {
                return MAX_HEALTH;
            } else {
                return BELOW_MINIMUM;
            }
        }
    }

    public static boolean isHealthBelowPercentage(LivingEntity entity, double percentage) {
        double currentHealth = entity.getHealth();      // Current health of the player
        double maxHealth = entity.getMaxHealth(); // Max health

        double thresholdHealth = (percentage / 100) * maxHealth;


        return currentHealth < thresholdHealth;
    }
}
