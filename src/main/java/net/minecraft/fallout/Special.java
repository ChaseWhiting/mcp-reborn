package net.minecraft.fallout;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.EnumMap;
import java.util.Map;

public class Special {

    public enum SpecialStats {
        STRENGTH,
        PERCEPTION,
        ENDURANCE,
        CHARISMA,
        INTELLIGENCE,
        AGILITY,
        LUCK
    }

    public SpecialStats LUCK() {
        return SpecialStats.LUCK;
    }

    public static class SpecialManager {
        private final LivingEntity entity;
        private final EnumMap<SpecialStats, Integer> specialStats;

        public SpecialManager(LivingEntity entity) {
            this.entity = entity;
            this.specialStats = new EnumMap<>(SpecialStats.class);

            // Initialize all stats to a default value, e.g., 5
            for (SpecialStats stat : SpecialStats.values()) {
                this.specialStats.put(stat, 5);
            }
        }

        public int getStat(SpecialStats stat) {
            return this.specialStats.getOrDefault(stat, 5);
        }

        public void setStat(SpecialStats stat, int value) {
            this.specialStats.put(stat, value);
        }

        public void increaseStat(SpecialStats stat, int amount) {
            this.specialStats.put(stat, this.getStat(stat) + amount);
        }

        public void decreaseStat(SpecialStats stat, int amount) {
            this.specialStats.put(stat, Math.max(1, this.getStat(stat) - amount)); // Ensure stat doesn't go below 1
        }

        public void addAdditionalSaveData(CompoundNBT nbt) {
            CompoundNBT specialNBT = new CompoundNBT();
            for (Map.Entry<SpecialStats, Integer> entry : specialStats.entrySet()) {
                specialNBT.putInt(entry.getKey().name(), entry.getValue());
            }
            nbt.put("Special", specialNBT);
        }

        public void readAdditionalSaveData(CompoundNBT nbt) {
            if (nbt.contains("Special")) {
                CompoundNBT specialNBT = nbt.getCompound("Special");

                for (SpecialStats stat : SpecialStats.values()) {
                    if (specialNBT.contains(stat.name())) {
                        this.specialStats.put(stat, specialNBT.getInt(stat.name()));
                    }
                }
            }
        }

        public void tick() {
            // Optional: Implement any per-tick logic here, such as modifying stats based on certain conditions
        }
    }
}
