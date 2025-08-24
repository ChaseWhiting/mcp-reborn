package net.minecraft.bundle;

import net.minecraft.item.ItemStack;

public interface IBundleLike {

    public default boolean supports(ItemStack stack) {
        return this.getConfiguration().supports(stack);
    }

    public BundleLikeConfiguration getConfiguration();

    public default boolean canScrollItems() {
        return this.getConfiguration().canScrollItems();
    }
}
