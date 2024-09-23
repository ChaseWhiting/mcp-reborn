package net.minecraft.entity;

import net.minecraft.util.WeightedItemStack;

import java.util.List;

public interface IDropsCustomLoot {

    public List<WeightedItemStack> getDropItems();
}
