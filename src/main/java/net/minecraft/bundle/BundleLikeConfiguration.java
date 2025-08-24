package net.minecraft.bundle;

import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class BundleLikeConfiguration {

    public BundleLikeConfiguration(Predicate<ItemStack> support, boolean canScroll) {
        this.supportPredicate = support;
        this.canScroll = canScroll;
    }


    private final Predicate<ItemStack> supportPredicate;
    private final boolean canScroll;


    public boolean supports(ItemStack stack) {
        return this.supportPredicate.test(stack);
    }

    public boolean canScrollItems() {
        return this.canScroll;
    }

    public static BundleLikeConfiguration defaultForBundle() {
        return new BundleLikeConfiguration(item -> true, false);
    }
}
