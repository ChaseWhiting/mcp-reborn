package net.minecraft.item.tab;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BuildingBlocks extends ItemGroup {




    public BuildingBlocks() {
        super(0, "buildingBlocks");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemStack makeIcon() {
        return new ItemStack(Items.BRICKS);
    }

    @Override
    public String getRecipeFolderName() {
        return "building_blocks";
    }

    public void addItems() {

    }
}
