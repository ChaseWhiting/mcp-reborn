package net.minecraft.terraria.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DropEntry {
    private final ItemStack itemStack;
    private final float chance;
    private final int minCount;
    private final int maxCount;
    private final Random random = new Random();

    public DropEntry(ItemStack itemStack, float chance, int minCount, int maxCount) {
        this.itemStack = itemStack;
        this.chance = chance;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    public DropEntry(ItemStack itemStack, float chance) {
        this(itemStack, chance, itemStack.getCount(), itemStack.getCount());
    }

    public float getChance() {
        return chance;
    }

    // Method to get ItemStacks with the required count, handling stacks over 64
    public List<ItemStack> getItemStacks() {
        int randomCount = MathHelper.nextInt(random, minCount, maxCount);
        List<ItemStack> stacks = new ArrayList<>();

        while (randomCount > 0) {
            int stackSize = Math.min(randomCount, itemStack.getMaxStackSize()); // Max stack size is 64
            ItemStack stack = itemStack.copy();
            stack.setCount(stackSize);
            stacks.add(stack);
            randomCount -= stackSize;
        }
        return stacks;
    }
}
