package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.StonecuttingRecipe;
import net.minecraft.item.crafting.WoodcuttingRecipe;
import net.minecraft.util.IWorldPosCallable;

public class WoodcutterContainer extends CuttingContainer<WoodcuttingRecipe> {

   public WoodcutterContainer(int p_i50059_1_, PlayerInventory p_i50059_2_) {
      this(p_i50059_1_, p_i50059_2_, IWorldPosCallable.NULL);
   }

   public WoodcutterContainer(int p_i50060_1_, PlayerInventory p_i50060_2_, final IWorldPosCallable p_i50060_3_) {
      super(p_i50060_1_, p_i50060_2_, p_i50060_3_, ContainerType.WOODCUTTER);
   }

   @Override
   public IRecipeType<WoodcuttingRecipe> getRecipeType() {
      return IRecipeType.WOODCUTTING;
   }

   @Override
   public ContainerType<?> getType() {
      return ContainerType.WOODCUTTER;
   }
}