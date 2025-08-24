package net.minecraft.bundle.item;

import net.minecraft.bundle.BundleLikeConfiguration;
import net.minecraft.bundle.IBundleLike;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

public class DyeableBundleItem extends Item implements IBundleLike, IItemProvider {
    private final DyeColor color;

    public DyeableBundleItem(DyeColor color) {
        super(new Item.Properties().tab(ItemGroup.TAB_COMBAT).stacksTo(1));

        this.color = color;
    }

    @Override
    public BundleLikeConfiguration getConfiguration() {
        return BundleLikeConfiguration.defaultForBundle();
    }

    public DyeColor getColor() {
        return color;
    }



    @Override
    public boolean canScrollItems() {
        return false;
    }
}
