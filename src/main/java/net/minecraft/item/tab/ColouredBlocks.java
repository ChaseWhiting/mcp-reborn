package net.minecraft.item.tab;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedHashSet;

public class ColouredBlocks extends ItemGroup {
    private final LinkedHashSet<Item> items = Util.make(new LinkedHashSet<>(), set -> {
        set.add(Items.WHITE_WOOL);
        set.add(Items.LIGHT_GRAY_WOOL);
        set.add(Items.GRAY_WOOL);
        set.add(Items.BLACK_WOOL);
        set.add(Items.BROWN_WOOL);
        set.add(Items.RED_WOOL);
        set.add(Items.ORANGE_WOOL);
        set.add(Items.YELLOW_WOOL);
        set.add(Items.LIME_WOOL);
        set.add(Items.GREEN_WOOL);
        set.add(Items.CYAN_WOOL);
        set.add(Items.LIGHT_BLUE_WOOL);
        set.add(Items.BLUE_WOOL);
        set.add(Items.PURPLE_WOOL);
        set.add(Items.MAGENTA_WOOL);
        set.add(Items.PINK_WOOL);


    });



    public ColouredBlocks() {
        super(1, "colouredBlocks");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemStack makeIcon() {
        return new ItemStack(Items.CYAN_WOOL);
    }

    public void fillList(NonNullList<ItemStack> itemStacks, NonNullList<Item> simpleItems) {

    }
}
