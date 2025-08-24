package net.minecraft.item;

import javax.annotation.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.tab.CreativeModeTabs;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ItemGroup {
    public static final ItemGroup[] TABS = new ItemGroup[13];
    public static final ItemGroup TAB_BUILDING_BLOCKS = (new ItemGroup(0, "buildingBlocks") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.BRICKS);
        }
    }).setRecipeFolderName("building_blocks");

    public static final ItemGroup TAB_COLOURED_BLOCKS = new ItemGroup(1, "colouredBlocks") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.CYAN_WOOL);
        }
    };

    public static final ItemGroup TAB_NATURAL = new ItemGroup(2, "decorations") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.GRASS_BLOCK); // Natural Blocks
        }
    };

    public static final ItemGroup TAB_FUNCTIONAL = new ItemGroup(3, "redstone") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.OAK_SIGN); // Functional Blocks
        }
    };

    public static final ItemGroup TAB_REDSTONE = new ItemGroup(4, "transportation") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.REDSTONE_WIRE); // Redstone
        }
    };

    public static final ItemGroup TAB_SEARCH = (new ItemGroup(5, "search") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.COMPASS);
        }
    }).setBackgroundSuffix("item_search.png");

    public static final ItemGroup TAB_TOOLS = (new ItemGroup(6, "tools") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.DIAMOND_PICKAXE); // Tools & Utilities
        }
    }).setEnchantmentCategories(new EnchantmentType[]{EnchantmentType.VANISHABLE, EnchantmentType.DIGGER, EnchantmentType.FISHING_ROD, EnchantmentType.BREAKABLE});

    public static final ItemGroup TAB_COMBAT = (new ItemGroup(7, "combat") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.NETHERITE_SWORD); // Combat
        }
    });

    public static final ItemGroup TAB_FOOD = new ItemGroup(8, "food") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.GOLDEN_APPLE); // Food and Drinks
        }
    };

    public static final ItemGroup TAB_MISC = new ItemGroup(9, "misc") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.IRON_INGOT); // Ingredients
        }
    };

    public static final ItemGroup TAB_SPAWN_EGGS = new ItemGroup(10, "spawn_eggs") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.CREEPER_SPAWN_EGG);
        }
    };

    public static final ItemGroup TAB_HOTBAR = new ItemGroup(12, "hotbar") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.BOOKSHELF);
        }

        @OnlyIn(Dist.CLIENT)
        public void fillItemList(NonNullList<ItemStack> p_78018_1_) {
            throw new RuntimeException("Implement exception client-side.");
        }

        @OnlyIn(Dist.CLIENT)
        public boolean isAlignedRight() {
            return true;
        }
    };

    public static final ItemGroup TAB_INVENTORY = (new ItemGroup(11, "inventory") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.CHEST);
        }
    }).setBackgroundSuffix("inventory.png").hideScroll().hideTitle();

    public static final ItemGroup TAB_MATERIALS = TAB_MISC;

    private final int id;
    private final String langId;
    private final ITextComponent displayName;
    private String recipeFolderName;
    private String backgroundSuffix = "items.png";
    private boolean canScroll = true;
    private boolean showTitle = true;
    private EnchantmentType[] enchantmentCategories = new EnchantmentType[0];
    private ItemStack iconItemStack;

    public ItemGroup(int p_i1853_1_, String p_i1853_2_) {
        this.id = p_i1853_1_;
        this.langId = p_i1853_2_;
        this.displayName = new TranslationTextComponent("itemGroup." + p_i1853_2_);
        this.iconItemStack = ItemStack.EMPTY;
        TABS[p_i1853_1_] = this;
    }

    @OnlyIn(Dist.CLIENT)
    public int getId() {
        return this.id;
    }

    public String getRecipeFolderName() {
        return this.recipeFolderName == null ? this.langId : this.recipeFolderName;
    }

    @OnlyIn(Dist.CLIENT)
    public ITextComponent getDisplayName() {
        return this.displayName;
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack getIconItem() {
        if (this.iconItemStack.isEmpty()) {
            this.iconItemStack = this.makeIcon();
        }

        return this.iconItemStack;
    }

    @OnlyIn(Dist.CLIENT)
    public abstract ItemStack makeIcon();

    @OnlyIn(Dist.CLIENT)
    public String getBackgroundSuffix() {
        return this.backgroundSuffix;
    }

    public ItemGroup setBackgroundSuffix(String p_78025_1_) {
        this.backgroundSuffix = p_78025_1_;
        return this;
    }

    public ItemGroup setRecipeFolderName(String p_199783_1_) {
        this.recipeFolderName = p_199783_1_;
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean showTitle() {
        return this.showTitle;
    }

    public ItemGroup hideTitle() {
        this.showTitle = false;
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean canScroll() {
        return this.canScroll;
    }

    public ItemGroup hideScroll() {
        this.canScroll = false;
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public int getColumn() {
        return this.id % 6;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isTopRow() {
        return this.id < 6;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isAlignedRight() {
        return this.getColumn() == 5;
    }

    public EnchantmentType[] getEnchantmentCategories() {
        return this.enchantmentCategories;
    }

    public ItemGroup setEnchantmentCategories(EnchantmentType... p_111229_1_) {
        this.enchantmentCategories = p_111229_1_;
        return this;
    }

    public boolean hasEnchantmentCategory(@Nullable EnchantmentType p_111226_1_) {
        if (p_111226_1_ != null) {
            for (EnchantmentType enchantmenttype : this.enchantmentCategories) {
                if (enchantmenttype == p_111226_1_) {
                    return true;
                }
            }
        }

        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void fillItemList(NonNullList<ItemStack> list) {
        if (this == TAB_BUILDING_BLOCKS) {
            list.addAll(CreativeModeTabs.BUILDING_BLOCKS.getDisplayItems());
            return;
        }
        if (this == TAB_COLOURED_BLOCKS) {
            list.addAll(CreativeModeTabs.COLORED_BLOCKS.getDisplayItems());
            return;
        }
        if (this == TAB_FUNCTIONAL) {
            list.addAll(CreativeModeTabs.FUNCTIONAL_BLOCKS.getDisplayItems());
            return;
        }
        if (this == TAB_REDSTONE) {
            list.addAll(CreativeModeTabs.REDSTONE_BLOCKS.getDisplayItems());
            return;
        }
        if (this == TAB_TOOLS) {
            list.addAll(CreativeModeTabs.TOOLS_AND_UTILITIES.getDisplayItems());
            return;
        }
        if (this == TAB_NATURAL) {
            list.addAll(CreativeModeTabs.NATURAL_BLOCKS.getDisplayItems());
            return;
        }
        if (this == TAB_COMBAT) {
            list.addAll(CreativeModeTabs.COMBAT.getDisplayItems());
            return;
        }
        if (this == TAB_FOOD) {
            list.addAll(CreativeModeTabs.FOOD_AND_DRINKS.getDisplayItems());
            return;
        }
        if (this == TAB_MISC) {
            list.addAll(CreativeModeTabs.INGREDIENTS.getDisplayItems());
            return;
        }
        if (this == TAB_SPAWN_EGGS) {
            list.addAll(CreativeModeTabs.SPAWN_EGGS.getDisplayItems());
            return;
        }

        if (this == TAB_SEARCH) {
            list.addAll(CreativeModeTabs.BUILDING_BLOCKS.getDisplayItems());
            list.addAll(CreativeModeTabs.COLORED_BLOCKS.getDisplayItems());
            list.addAll(CreativeModeTabs.FUNCTIONAL_BLOCKS.getDisplayItems());
            list.addAll(CreativeModeTabs.REDSTONE_BLOCKS.getDisplayItems());
            list.addAll(CreativeModeTabs.TOOLS_AND_UTILITIES.getDisplayItems());
            list.addAll(CreativeModeTabs.NATURAL_BLOCKS.getDisplayItems());
            list.addAll(CreativeModeTabs.COMBAT.getDisplayItems());
            list.addAll(CreativeModeTabs.FOOD_AND_DRINKS.getDisplayItems());
            list.addAll(CreativeModeTabs.INGREDIENTS.getDisplayItems());
            list.addAll(CreativeModeTabs.SPAWN_EGGS.getDisplayItems());
        }


    }
}