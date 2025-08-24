package net.minecraft.bundle;

import net.minecraft.item.Item;

public abstract class SimpleItemHolderItem extends Item implements IBundleLike {
    private final BundleLikeConfiguration configuration;

    public SimpleItemHolderItem(Properties properties, BundleLikeConfiguration configuration) {
        super(properties);
        this.configuration = configuration;
    }

    @Override
    public BundleLikeConfiguration getConfiguration() {
        return configuration;
    }


}
