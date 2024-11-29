package net.minecraft.terraria.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DropTable {
    private final List<DropEntry> drops = new ArrayList<>();
    private final List<DropEntry> alwaysDrops = new ArrayList<>();
    private final List<OneDropGroup> oneDropGroups = new ArrayList<>();
    private final Random random = new Random();

    // Add drop with min and max count
    public DropTable addDrop(ItemStack itemStack, float chance, int minCount, int maxCount) {
        drops.add(new DropEntry(itemStack, chance, minCount, maxCount));
        return this;
    }

    // Add drop with default count
    public DropTable addDrop(ItemStack itemStack, float chance) {
        drops.add(new DropEntry(itemStack, chance));
        return this;
    }

    public DropTable addAlwaysDrop(ItemStack stack) {
        alwaysDrops.add(new DropEntry(stack, 1f));
        return this;
    }

    public DropTable addAlwaysDrop(ItemStack stack, int mincount, int maxcount) {
        alwaysDrops.add(new DropEntry(stack, 1f, mincount, maxcount));
        return this;
    }

    public DropTable addOneDropGroup(OneDropGroup group) {
        oneDropGroups.add(group);
        return this;
    }

    public List<ItemStack> generateDrops(PlayerEntity player) {
        List<ItemStack> result = new ArrayList<>();

        for (DropEntry drop : drops) {
            if (random.nextFloat() < drop.getChance()) {
                result.addAll(drop.getItemStacks()); // Adds all stacks for the entry
            }
        }

        for (DropEntry dropEntry : alwaysDrops) {
            result.addAll(dropEntry.getItemStacks());
        }

        for (OneDropGroup group : oneDropGroups) {
            List<ItemStack> groupDrop = group.generateDrop(player);
            if (groupDrop != null) {
                result.addAll(groupDrop);
            }
        }

        return result;
    }
}
