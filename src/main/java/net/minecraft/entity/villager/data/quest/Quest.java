package net.minecraft.entity.villager.data.quest;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quest {
    private String name;
    private String description;
    private List<ItemStack> requiredItems;
    private Map<EntityType<?>, Integer> requiredKills;
    private List<ItemStack> rewards;
    private int xpAmount;
    private boolean isCompleted;

    public Quest(String name, String description) {
        this.name = name;
        this.description = description;
        this.requiredItems = new ArrayList<>();
        this.requiredKills = new HashMap<>();
        this.rewards = new ArrayList<>();
        this.xpAmount = 0;
        this.isCompleted = false;
    }

    // Getters and setters for the fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getXp() {
        return xpAmount;
    }

    public void addXPReward(int xp) {
        this.xpAmount = xp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }

    public void addRequiredItem(ItemStack item) {
        this.requiredItems.add(item);
    }



    public List<ItemStack> getRewards() {
        return rewards;
    }

    public void addReward(ItemStack reward) {
        this.rewards.add(reward);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void addRequiredKill(EntityType<?> entityType, int count) {
        this.requiredKills.put(entityType, count);
    }

    public Map<EntityType<?>, Integer> getRequiredKills() {
        return requiredKills;
    }

    public boolean hasKills(Quest quest, PlayerEntity player) {
        for (Map.Entry<EntityType<?>, Integer> entry : quest.requiredKills.entrySet()) {
            EntityType<?> entityType = entry.getKey();
            int requiredCount = entry.getValue();
            if (player.getKillCount(entityType) < requiredCount) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        this.isCompleted = false;
        resetQuest();
    }

    public void resetQuest() {
        Quest matchingQuest = QuestTypes.quests.stream()
                .filter(q -> q.getName().equalsIgnoreCase(this.name))
                .findFirst()
                .orElse(null);

        if (matchingQuest != null) {
            this.isCompleted = false;
            this.requiredKills = matchingQuest.requiredKills;
            this.requiredItems = matchingQuest.getRequiredItems();
            this.rewards = new ArrayList<>(matchingQuest.rewards); // Ensure deep copy
            this.xpAmount = matchingQuest.xpAmount;
            System.out.println("Reset quest rewards: " + this.rewards); // Debugging statement
            // Reset other relevant fields
        } else {
            System.out.println("Matching quest not found for: " + this.name); // Debugging statement
        }
    }

    public void tick() {
        // Potentially do not reset quest every tick if not necessary
        // resetQuest();
    }

    @Override
    public Quest clone() {
        Quest clone = new Quest(this.name, this.description);
        clone.requiredKills = this.requiredKills;
        clone.requiredItems = this.requiredItems;
        clone.rewards = new ArrayList<>(this.rewards);
        clone.xpAmount = this.xpAmount;
        return clone;
    }

    public void giveRewards(PlayerEntity player) {
        if (isCompleted) {
            player.sendMessage(new StringTextComponent("You completed the quest: " + this.getName() + "!"), player.getUUID());
            player.sendMessage(new StringTextComponent("Rewards: " + rewards), player.getUUID());
            for (ItemStack reward : rewards) {
                player.inventory.add(reward);
            }
            player.getQuests().remove(this);
            player.giveExperiencePoints(this.xpAmount);
            // Reset kill counts for the quest-specific mobs
            for (EntityType<?> entityType : requiredKills.keySet()) {
                player.resetKillCount(entityType);
            }
        }
    }


    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("Name", name);
        nbt.putString("Description", description);

        // Serialize requiredItems
        ListNBT requiredItemsNBT = new ListNBT();
        for (ItemStack item : requiredItems) {
            requiredItemsNBT.add(item.save(new CompoundNBT()));
        }
        nbt.put("RequiredItems", requiredItemsNBT);

        // Serialize requiredKills
        CompoundNBT requiredKillsNBT = new CompoundNBT();
        for (Map.Entry<EntityType<?>, Integer> entry : requiredKills.entrySet()) {
            requiredKillsNBT.putInt(entry.getKey().toString(), entry.getValue());
        }
        nbt.put("RequiredKills", requiredKillsNBT);

        // Serialize rewards
        ListNBT rewardsNBT = new ListNBT();
        for (ItemStack reward : rewards) {
            rewardsNBT.add(reward.save(new CompoundNBT()));
        }
        nbt.put("Rewards", rewardsNBT);

        nbt.putBoolean("IsCompleted", isCompleted);
        return nbt;
    }

    public static Quest fromNBT(CompoundNBT nbt) {
        Quest quest = new Quest(nbt.getString("Name"), nbt.getString("Description"));

        // Deserialize requiredItems
        ListNBT requiredItemsNBT = nbt.getList("RequiredItems", 10);
        for (INBT inbt : requiredItemsNBT) {
            quest.requiredItems.add(ItemStack.of((CompoundNBT) inbt));
        }

        // Deserialize requiredKills
        CompoundNBT requiredKillsNBT = nbt.getCompound("RequiredKills");
        for (String key : requiredKillsNBT.getAllKeys()) {
            quest.requiredKills.put(Registry.ENTITY_TYPE.get(new ResourceLocation(key)), requiredKillsNBT.getInt(key));
        }

        // Deserialize rewards
        ListNBT rewardsNBT = nbt.getList("Rewards", 10);
        for (INBT inbt : rewardsNBT) {
            quest.rewards.add(ItemStack.of((CompoundNBT) inbt));
        }

        quest.isCompleted = nbt.getBoolean("IsCompleted");
        return quest;
    }
}

