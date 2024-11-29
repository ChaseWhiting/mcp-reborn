package net.minecraft.terraria.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OneDropEntry {
    private final ItemStack itemStack;
    private final int minCount;
    private final int maxCount;
    private final Random random = new Random();

    public OneDropEntry(ItemStack itemStack, int minCount, int maxCount) {
        this.itemStack = itemStack;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    public OneDropEntry(ItemStack itemStack) {
        this(itemStack, itemStack.getCount(), itemStack.getCount());
    }

    // Method to get a list of ItemStacks with the required count
    public List<ItemStack> getItemStacks() {
        int randomCount = MathHelper.nextInt(random, minCount, maxCount);
        List<ItemStack> stacks = new ArrayList<>();

        while (randomCount > 0) {
            int stackSize = Math.min(randomCount, itemStack.getMaxStackSize());
            ItemStack stack = itemStack.copy();
            stack.setCount(stackSize);
            stacks.add(stack);
            randomCount -= stackSize;
        }
        return stacks;
    }
}
