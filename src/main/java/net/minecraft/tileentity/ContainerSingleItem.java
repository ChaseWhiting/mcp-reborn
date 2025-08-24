package net.minecraft.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ContainerSingleItem
extends IInventory {
    public ItemStack getTheItem();

    public ItemStack splitTheItem(int var1);

    public void setTheItem(ItemStack var1);

    public TileEntity getContainerBlockEntity();

    default public ItemStack removeTheItem() {
        return this.splitTheItem(this.getMaxStackSize());
    }

    @Override
    default public int getContainerSize() {
        return 1;
    }

    @Override
    default public boolean isEmpty() {
        return this.getTheItem().isEmpty();
    }

    @Override
    default public void clearContent() {
        this.removeTheItem();
    }

    @Override
    default public ItemStack removeItemNoUpdate(int n) {
        return this.removeItem(n, this.getMaxStackSize());
    }

    @Override
    default public ItemStack getItem(int n) {
        return n == 0 ? this.getTheItem() : ItemStack.EMPTY;
    }

    @Override
    default public ItemStack removeItem(int n, int n2) {
        if (n != 0) {
            return ItemStack.EMPTY;
        }
        return this.splitTheItem(n2);
    }

    @Override
    default public void setItem(int n, ItemStack itemStack) {
        if (n == 0) {
            this.setTheItem(itemStack);
        }
    }

    @Override
    default public boolean stillValid(PlayerEntity player) {
        return stillValidBlockEntity(this.getContainerBlockEntity(), player);
    }

    public static boolean stillValidBlockEntity(TileEntity blockEntity, PlayerEntity player) {
        return stillValidBlockEntity(blockEntity, player, 8);
    }

    public static boolean stillValidBlockEntity(TileEntity blockEntity, PlayerEntity player, int n) {
        World level = blockEntity.getLevel();
        BlockPos blockPos = blockEntity.getBlockPos();
        if (level == null) {
            return false;
        }
        if (level.getBlockEntity(blockPos) != blockEntity) {
            return false;
        }
        return player.distanceToSqr((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= (double)(n * n);
    }
}
