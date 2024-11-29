package net.minecraft.terraria.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OneDropGroup {
    private final List<OneDropEntry> items = new ArrayList<>();

    public OneDropGroup addItem(ItemStack itemStack, int minCount, int maxCount) {
        items.add(new OneDropEntry(itemStack, minCount, maxCount));
        return this;
    }

    public OneDropGroup addItem(ItemStack itemStack) {
        items.add(new OneDropEntry(itemStack));
        return this;
    }

    public List<ItemStack> generateDrop(PlayerEntity player) {
        if (items.isEmpty()) {
            return null;
        }
        int index = player.getRandom().nextInt(items.size());
        return items.get(index).getItemStacks(); // Get item stacks with random count
    }
}
