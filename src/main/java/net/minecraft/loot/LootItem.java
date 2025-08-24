package net.minecraft.loot;

import net.minecraft.util.IItemProvider;

public class LootItem {
    public static StandaloneLootEntry.Builder<?> lootTableItem(IItemProvider p_216168_0_) {
        return ItemLootEntry.lootTableItem(p_216168_0_);
    }
}
