package net.minecraft.entity.villager.data.quest;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class QuestTypes {
    public static List<Quest> quests = new ArrayList<>();
    public static List<Quest> clonedQuests = new ArrayList<>();

    public static final Quest killCows = new Quest("Kill Cows", "Defeat 3 cows.");
    public static final Quest fetchMeat = new Quest("Gather meat", "Get 4 raw beef.");

    static {
        killCows.addRequiredKill(EntityType.COW, 3);
        killCows.addReward(new ItemStack(Items.EMERALD, 3));
        killCows.addXPReward(2);

        fetchMeat.addRequiredItem(new ItemStack(Items.BEEF, 3));
        fetchMeat.addReward(new ItemStack(Items.EMERALD, 4));

        quests.add(killCows);
        quests.add(fetchMeat);
        clonedQuests.add(killCows);
        clonedQuests.add(fetchMeat);
    }

    public static Quest getQuestByName(String name) {
        return clonedQuests.stream()
                .filter(q -> q.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(Quest::clone) // Use the clone method
                .orElse(null);
    }
}
