package net.minecraft.fallout;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.EnumMap;
import java.util.Map;

public class Skills {

    public enum SkillType {
        BARTER,
        ENERGY_WEAPONS,
        EXPLOSIVES,
        GUNS,
        LOCKPICK,
        MEDICINE,
        MELEE_WEAPONS,
        REPAIR,
        SCIENCE,
        SNEAK,
        SPEECH,
        SURVIVAL,
        UNARMED
    }

    private final Special.SpecialManager specialManager;
    private final EnumMap<SkillType, Double> skills;
    private final LivingEntity entity;
    public int skillPoints = 30;
    public int specialSkillPoints = 3;

    public Skills(LivingEntity entity, Special.SpecialManager specialManager) {
        this.entity = entity;
        this.specialManager = specialManager;
        this.skills = new EnumMap<>(SkillType.class);
        calculateInitialSkills();
    }

    private void calculateInitialSkills() {
        for (SkillType skill : SkillType.values()) {
            this.skills.put(skill, calculateSkillValue(skill));
        }
    }

    private double calculateSkillValue(SkillType skill) {
        int relevantStatValue = getRelevantStatForSkill(skill);
        int luckValue = specialManager.getStat(Special.SpecialStats.LUCK);
        return 2 + (2 * relevantStatValue) + Math.ceil(luckValue / 2.0);
    }

    private int getRelevantStatForSkill(SkillType skill) {
        return switch (skill) {
            case BARTER, SPEECH -> specialManager.getStat(Special.SpecialStats.CHARISMA);
            case ENERGY_WEAPONS, SCIENCE, MEDICINE -> specialManager.getStat(Special.SpecialStats.INTELLIGENCE);
            case EXPLOSIVES, SURVIVAL -> specialManager.getStat(Special.SpecialStats.ENDURANCE);
            case GUNS, REPAIR -> specialManager.getStat(Special.SpecialStats.PERCEPTION);
            case LOCKPICK, SNEAK -> specialManager.getStat(Special.SpecialStats.AGILITY);
            case MELEE_WEAPONS, UNARMED -> specialManager.getStat(Special.SpecialStats.STRENGTH);
        };
    }

    public double getSkill(SkillType skill) {
        return this.skills.getOrDefault(skill, 0.0);
    }

    public void setSkill(SkillType skill, double value) {
        this.skills.put(skill, value);
    }

    public void increaseSkill(SkillType skill, double amount) {
        this.skills.put(skill, this.getSkill(skill) + amount);
    }

    public void decreaseSkill(SkillType skill, double amount) {
        this.skills.put(skill, Math.max(0, this.getSkill(skill) - amount)); // Ensure skill doesn't go below 0
    }

    public void addAdditionalSaveData(CompoundNBT nbt) {
        specialManager.addAdditionalSaveData(nbt);

        CompoundNBT skillNBT = new CompoundNBT();
        skillNBT.putInt("SkillPoints", skillPoints);
        skillNBT.putInt("SpecialSkillPoints", specialSkillPoints);
        for (Map.Entry<SkillType, Double> entry : skills.entrySet()) {
            skillNBT.putDouble(entry.getKey().name(), entry.getValue());
        }
        nbt.put("Skills", skillNBT);
    }

    public void readAdditionalSaveData(CompoundNBT nbt) {
        specialManager.readAdditionalSaveData(nbt);
        if (nbt.contains("Skills")) {
            CompoundNBT skillNBT = nbt.getCompound("Skills");
            skillPoints = skillNBT.getInt("SkillPoints");
            specialSkillPoints = skillNBT.getInt("SpecialSkillPoints");
            for (SkillType skill : SkillType.values()) {
                if (skillNBT.contains(skill.name())) {
                    this.skills.put(skill, skillNBT.getDouble(skill.name()));
                }
            }
        }

    }

    public void tick() {
        // Optional: Implement any per-tick logic here, such as modifying skills based on certain conditions
    }
}
