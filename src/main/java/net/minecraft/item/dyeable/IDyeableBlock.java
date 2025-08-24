package net.minecraft.item.dyeable;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.function.Supplier;

public interface IDyeableBlock {

    public default DyeColor getColour() {
        return this.getDyeConversion().get().inverse().get(this.getBlock());
    }

    public Block getBlock();

    public default Supplier<BiMap<DyeColor, Block>> getDyeConversion() {
        return this.createDefaultDyeConversion();
    };

     public static boolean canAcceptDye(DyeColor colour, IDyeableBlock block) {
         return block.getColour() != colour;
     }

    public default BlockState asState(World world, BlockPos pos) {
         return this.getBlock().defaultBlockState();
    }

    public default BlockState colouredState(World world, BlockPos pos, DyeColor colour) {
        return this.getDyeConversion().get().get(colour).defaultBlockState();
    }

    public String getBlockPrefix();

    public default Supplier<BiMap<DyeColor, Block>> createDefaultDyeConversion() {
        ImmutableBiMap.Builder<DyeColor, Block> builder = ImmutableBiMap.builder();


        for (DyeColor color : DyeColor.values()) {
            ResourceLocation location = new ResourceLocation("minecraft", color.getName() + "_" + this.getBlockPrefix());
            builder.put(color, Registry.BLOCK.get(location));
        }

        return Suppliers.memoize(builder::build);
    }

    public default ItemStack getWashedItem() {
        return new ItemStack(Registry.BLOCK.get(new ResourceLocation("minecraft", "white_" + this.getBlockPrefix())));
    }

    static ItemStack turnWashed(int count, IDyeableBlock block) {
        return new ItemStack(Registry.BLOCK.get(new ResourceLocation("minecraft", "white_" + block.getBlockPrefix())), count);
    }
}
