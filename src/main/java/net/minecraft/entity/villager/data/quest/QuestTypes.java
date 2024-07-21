package net.minecraft.entity.villager.data.quest;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class QuestTypes {
    public static List<Quest> quests = new ArrayList<>();
    public static List<Quest> clonedQuests = new ArrayList<>();

    public static final Quest killCows = (new Quest.Builder())
            .name("Kill Cows")
            .description("Defeat 3 cows.")
            .addRequiredKill(EntityType.COW, 3)
            .addReward(Items.EMERALD, 3)
            .setXP(2)
            .build();

    public static final Quest fetchMeat = (new Quest.Builder())
            .name("Gather meat")
            .description("Get 4 raw beef.")
            .addRequiredItem(Items.BEEF, 4)
            .addReward(Items.EMERALD, 1)
            .setXP(1)
            .build();

    public static final Quest killWither = (new Quest.Builder())
            .name("Kill the wither.")
            .description("Defeat the wither")
            .addRequiredKill(EntityType.WITHER, 1)
            .setXP(40).addReward(Items.DIAMOND, 12)
            .build();

    public static final Quest defeatAllMob = (new Quest.Builder())
            .setXP(3000)
            .addReward(Items.DIAMOND, 40)
            .addReward(Items.NETHERITE_INGOT, 4)
            .addReward(Items.GOLD_BLOCK, 6)
            .addReward(Items.TOTEM_OF_UNDYING)
            .build();

    static {

        addQuest(fetchMeat);
        addQuest(killWither);
        addQuest(killCows);
    }

    public static void addQuest(Quest quest) {
        quests.add(quest);
        clonedQuests.add(quest);
    }

    public static Quest getQuestByName(String name) {
        return clonedQuests.stream()
                .filter(q -> q.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(Quest::clone) // Use the clone method
                .orElse(null);
    }
}
