package net.minecraft.entity.villager.data.quest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.List;

public class QuestManager {
    private List<Quest> activeQuests;
    private List<Quest> completedQuests;
    private PlayerEntity player;

    public QuestManager(PlayerEntity player) {
        this.activeQuests = new ArrayList<>();
        this.completedQuests = new ArrayList<>();
        this.player = player;
    }

    public void addQuest(Quest quest) {
        if (!activeQuests.contains(quest) && !completedQuests.contains(quest)) {
            activeQuests.add(quest);
            quest.setCompleted(false); // Ensure the quest is marked as not completed
        }
    }

    public void completeQuest(Quest quest) {
        if (activeQuests.remove(quest)) { // Remove the quest from activeQuests
            completedQuests.add(quest);
            quest.setCompleted(true);
            quest.giveRewards(player);
        }
    }

    public Boolean tryGiveReward(Quest quest, PlayerEntity player) {
        return quest != null && player.inventory.containsAll(quest.getRequiredItems()) && quest.hasKills(quest, player);
    }

    public List<Quest> getActiveQuests() {
        return activeQuests;
    }

    public List<Quest> getCompletedQuests() {
        return completedQuests;
    }

    public void saveQuestData(CompoundNBT compound) {
        player.writeQuestsToNBT(compound);
    }

    public void loadQuestData(CompoundNBT compound) {
        player.readQuestsFromNBT(compound);
    }

    public void removeQuest(Quest quest) {
        activeQuests.remove(quest);
        completedQuests.remove(quest);
    }


    // Other quest management methods...
}