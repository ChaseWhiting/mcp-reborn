package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public class BuildWithBlockGoal<T extends LivingEntity> extends Goal {
    private Function<T, ItemStack> blockToPlace;
    private final T mob;

    public BuildWithBlockGoal(T mob, Function<T, ItemStack> blockToPlace) {
        this.blockToPlace = blockToPlace;
        this.mob = mob;
    }

    public BuildWithBlockGoal(T mob, Block blockToPlace) {
        this.blockToPlace = (t -> {
            return new ItemStack(blockToPlace);
        });
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return !blockToPlace.apply(mob).isEmpty() && blockToPlace.apply(mob).getItem().asItem() instanceof BlockItem;
    }


}
