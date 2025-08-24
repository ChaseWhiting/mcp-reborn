package net.minecraft.inventory.container;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.StonecuttingRecipe;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StonecutterContainer extends CuttingContainer<StonecuttingRecipe> {

   public StonecutterContainer(int p_i50059_1_, PlayerInventory p_i50059_2_) {
      this(p_i50059_1_, p_i50059_2_, IWorldPosCallable.NULL);
   }

   public StonecutterContainer(int p_i50060_1_, PlayerInventory p_i50060_2_, final IWorldPosCallable p_i50060_3_) {
      super(p_i50060_1_, p_i50060_2_, p_i50060_3_, ContainerType.STONECUTTER);
   }

   @Override
   public IRecipeType<StonecuttingRecipe> getRecipeType() {
      return IRecipeType.STONECUTTING;
   }

   @Override
   public ContainerType<?> getType() {
      return ContainerType.STONECUTTER;
   }
}