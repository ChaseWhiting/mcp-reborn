package net.minecraft.inventory.container;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.NewSmithingRecipe;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

public class SmithingTableContainer extends AbstractRepairContainer {
    public static final int TEMPLATE_SLOT = 0;
    public static final int BASE_SLOT = 1;
    public static final int ADDITIONAL_SLOT = 2;
    public static final int RESULT_SLOT = 3;
    public static final int TEMPLATE_SLOT_X_PLACEMENT = 8;
    public static final int BASE_SLOT_X_PLACEMENT = 26;
    public static final int ADDITIONAL_SLOT_X_PLACEMENT = 44;
    private static final int RESULT_SLOT_X_PLACEMENT = 98;
    public static final int SLOT_Y_PLACEMENT = 48;
    private final World level;
    @Nullable
    private NewSmithingRecipe selectedRecipe;
    private final List<NewSmithingRecipe> recipes;


    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(TEMPLATE_SLOT, TEMPLATE_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT, itemStack -> this.recipes.stream()
                        .anyMatch(smithingRecipe -> smithingRecipe.isTemplateIngredient(itemStack)))
                .withSlot(BASE_SLOT, BASE_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT, itemStack -> this.recipes.stream()
                        .anyMatch(smithingRecipe -> smithingRecipe.isBaseIngredient(itemStack)))
                .withSlot(ADDITIONAL_SLOT, ADDITIONAL_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT, itemStack -> this.recipes.stream()
                        .anyMatch(smithingRecipe -> smithingRecipe.isAdditionIngredient(itemStack)))
                .withResultSlot(RESULT_SLOT, RESULT_SLOT_X_PLACEMENT, SLOT_Y_PLACEMENT).build();
    }


    public SmithingTableContainer(int n, PlayerInventory inventory) {
        this(n, inventory, IWorldPosCallable.NULL);
    }

    public SmithingTableContainer(int n, PlayerInventory inventory, IWorldPosCallable containerAccess) {
        super(ContainerType.SMITHING, n, inventory, containerAccess);
        this.level = inventory.player.level;
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(IRecipeType.NEW_SMITHING);
    }

    protected boolean isValidBlock(BlockState state) {
        return state.is(Blocks.SMITHING_TABLE);
    }

    protected boolean mayPickup(PlayerEntity player, boolean b) {
        return this.selectedRecipe != null && this.selectedRecipe.matches(this.inputSlots, this.level);
    }

    protected ItemStack onTake(PlayerEntity player, ItemStack stack) {
        stack.onCraftedBy(player.level, player, stack.getCount());
        this.resultSlots.awardUsedRecipes(player);
        this.shrinkStackInSlot(0);
        this.shrinkStackInSlot(1);
        this.shrinkStackInSlot(2);
        this.access.execute((world, pos) -> {
            world.levelEvent(1044, pos, 0);
        });
        return stack;
    }

    private void shrinkStackInSlot(int p_234654_1_) {
        ItemStack itemstack = this.inputSlots.getItem(p_234654_1_);
        itemstack.shrink(1);
        this.inputSlots.setItem(p_234654_1_, itemstack);
    }

    private List<ItemStack> getRelevantItems() {
        return List.of(this.inputSlots.getItem(0), this.inputSlots.getItem(1), this.inputSlots.getItem(2));
    }

    @Override
    public void createResult() {
        List<NewSmithingRecipe> list = this.level.getRecipeManager().getRecipesFor(IRecipeType.NEW_SMITHING, this.inputSlots, this.level);
        if (list.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            NewSmithingRecipe smithingRecipe = list.get(0);
            ItemStack itemStack = smithingRecipe.assemble(this.inputSlots, this.level.registryAccess());
            this.selectedRecipe = smithingRecipe;
            this.resultSlots.setRecipeUsed(smithingRecipe);
            this.resultSlots.setItem(0, itemStack);
        }
    }

    protected boolean shouldQuickMoveToAdditionalSlot(ItemStack p_241210_1_) {
        return this.recipes.stream().anyMatch((p_241444_1_) -> {
            return p_241444_1_.isAdditionIngredient(p_241210_1_);
        });
    }

   private static Optional<Integer> findSlotMatchingIngredient(NewSmithingRecipe smithingRecipe, ItemStack itemStack) {
      if (smithingRecipe.isTemplateIngredient(itemStack)) {
         return Optional.of(0);
      }
      if (smithingRecipe.isBaseIngredient(itemStack)) {
         return Optional.of(1);
      }
      if (smithingRecipe.isAdditionIngredient(itemStack)) {
         return Optional.of(2);
      }
      return Optional.empty();
   }


    public ItemStack quickMoveStack(PlayerEntity player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            int n2;
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            int n3 = this.getInventorySlotStart();
            int n4 = this.getUseRowEnd();
            if (n == this.getResultSlot()) {
                if (!this.moveItemStackTo(itemStack2, n3, n4, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (this.inputSlotIndexes.contains(n) ? !this.moveItemStackTo(itemStack2, n3, n4, false) :
                    (this.canMoveIntoInputSlots(itemStack2) && n >= this.getInventorySlotStart() && n < this.getUseRowEnd() ?
                            !this.moveItemStackTo(itemStack2, n2 = this.getSlotToQuickMoveTo(itemStack), this.getResultSlot(), false) :
                            (n >= this.getInventorySlotStart() && n < this.getInventorySlotEnd() ?
                                    !this.moveItemStackTo(itemStack2, this.getUseRowStart(), this.getUseRowEnd(), false) :
                                    n >= this.getUseRowStart() && n < this.getUseRowEnd() && !this.moveItemStackTo(itemStack2, this.getInventorySlotStart(), this.getInventorySlotEnd(), false)))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack2);
        }

        return itemStack;
    }

    public boolean canTakeItemForPickAll(ItemStack p_94530_1_, Slot p_94530_2_) {
        return p_94530_2_.container != this.resultSlots && super.canTakeItemForPickAll(p_94530_1_, p_94530_2_);
    }

   @Override
   public int getSlotToQuickMoveTo(ItemStack itemStack) {
      return this.recipes.stream().map(smithingRecipe -> findSlotMatchingIngredient(smithingRecipe, itemStack)).filter(Optional::isPresent).findFirst().orElse(Optional.of(0)).get();
   }

    @Override
    public boolean canMoveIntoInputSlots(ItemStack itemStack) {
        return this.recipes.stream().map(smithingRecipe -> findSlotMatchingIngredient(smithingRecipe, itemStack)).anyMatch(Optional::isPresent);
    }
}