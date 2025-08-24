package net.minecraft.item.dyeable;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;

import java.util.function.Supplier;

public class WoolBlock extends Block implements IDyeableBlock {
    private DyeColor colour;

    public WoolBlock(DyeColor colour, Properties properties) {
        super(properties);
        this.colour = colour;
    }

    @Override
    public Block getBlock() {
        return this;
    }

    public DyeColor getColour() {
        return this.colour;
    }

    @Override
    public Supplier<BiMap<DyeColor, Block>> getDyeConversion() {
        return Suppliers.memoize(() -> ImmutableBiMap.<DyeColor, Block>builder()
                .put(DyeColor.WHITE, Blocks.WHITE_WOOL)
                .put(DyeColor.ORANGE, Blocks.ORANGE_WOOL)
                .put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL)
                .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL)
                .put(DyeColor.YELLOW, Blocks.YELLOW_WOOL)
                .put(DyeColor.LIME, Blocks.LIME_WOOL)
                .put(DyeColor.PINK, Blocks.PINK_WOOL)
                .put(DyeColor.GRAY, Blocks.GRAY_WOOL)
                .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL)
                .put(DyeColor.CYAN, Blocks.CYAN_WOOL)
                .put(DyeColor.PURPLE, Blocks.PURPLE_WOOL)
                .put(DyeColor.BLUE, Blocks.BLUE_WOOL)
                .put(DyeColor.BROWN, Blocks.BROWN_WOOL)
                .put(DyeColor.GREEN, Blocks.GREEN_WOOL)
                .put(DyeColor.RED, Blocks.RED_WOOL)
                .put(DyeColor.BLACK, Blocks.BLACK_WOOL)
                .build());
    }

    public String getBlockPrefix() {
        return "wool";
    }

}
