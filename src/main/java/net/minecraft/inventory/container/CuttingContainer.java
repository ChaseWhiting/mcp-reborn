package net.minecraft.inventory.container;

import com.google.common.collect.Lists;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CuttingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.StonecuttingRecipe;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public abstract class CuttingContainer <CR extends CuttingRecipe> extends Container {
    private final IWorldPosCallable access;
    private final IntReferenceHolder selectedRecipeIndex = IntReferenceHolder.standalone();
    private final World level;
    private List<CR> recipes = Lists.newArrayList();
    private ItemStack input = ItemStack.EMPTY;
    private long lastSoundTime;
    final Slot inputSlot;
    final Slot resultSlot;
    private Runnable slotUpdateListener = () -> {
    };
    public final IInventory container = new Inventory(1) {
        public void setChanged() {
            super.setChanged();
            CuttingContainer.this.slotsChanged(this);
            CuttingContainer.this.slotUpdateListener.run();
        }
    };
    private final CraftResultInventory resultContainer = new CraftResultInventory();

    public CuttingContainer(int p_i50059_1_, PlayerInventory p_i50059_2_, ContainerType<?> type) {
        this(p_i50059_1_, p_i50059_2_, IWorldPosCallable.NULL, type);
    }

    public CuttingContainer(int p_i50060_1_, PlayerInventory p_i50060_2_, final IWorldPosCallable p_i50060_3_, ContainerType<?> container) {
        super(container, p_i50060_1_);
        this.access = p_i50060_3_;
        this.level = p_i50060_2_.player.level;
        this.inputSlot = this.addSlot(new Slot(this.container, 0, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
            public boolean mayPlace(ItemStack p_75214_1_) {
                return false;
            }

            public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
                p_190901_2_.onCraftedBy(p_190901_1_.level, p_190901_1_, p_190901_2_.getCount());
                CuttingContainer.this.resultContainer.awardUsedRecipes(p_190901_1_);
                ItemStack itemstack = CuttingContainer.this.inputSlot.remove(1);
                if (!itemstack.isEmpty()) {
                    CuttingContainer.this.setupResultSlot();
                }

                p_i50060_3_.execute((p_216954_1_, p_216954_2_) -> {
                    long l = p_216954_1_.getGameTime();
                    if (CuttingContainer.this.lastSoundTime != l) {
                        p_216954_1_.playSound((PlayerEntity)null, p_216954_2_, CuttingContainer.this.getRecipeType() == IRecipeType.STONECUTTING ? SoundEvents.UI_STONECUTTER_TAKE_RESULT : SoundEvents.VILLAGER_WORK_FLETCHER, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        CuttingContainer.this.lastSoundTime = l;
                    }

                });
                return super.onTake(p_190901_1_, p_190901_2_);
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(p_i50060_2_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(p_i50060_2_, k, 8 + k * 18, 142));
        }

        this.addDataSlot(this.selectedRecipeIndex);
    }

    @OnlyIn(Dist.CLIENT)
    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    @OnlyIn(Dist.CLIENT)
    public List<CR> getRecipes() {
        return this.recipes;
    }

    @OnlyIn(Dist.CLIENT)
    public int getNumRecipes() {
        return this.recipes.size();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.recipes.isEmpty();
    }

    public boolean stillValid(PlayerEntity p_75145_1_) {
        return stillValid(this.access, p_75145_1_, Blocks.STONECUTTER) || stillValid(this.access, p_75145_1_, Blocks.WOODCUTTER);
    }

    public boolean clickMenuButton(PlayerEntity p_75140_1_, int p_75140_2_) {
        if (this.isValidRecipeIndex(p_75140_2_)) {
            this.selectedRecipeIndex.set(p_75140_2_);
            this.setupResultSlot();
        }

        return true;
    }

    private boolean isValidRecipeIndex(int p_241818_1_) {
        return p_241818_1_ >= 0 && p_241818_1_ < this.recipes.size();
    }

    public void slotsChanged(IInventory p_75130_1_) {
        ItemStack itemstack = this.inputSlot.getItem();
        if (itemstack.getItem() != this.input.getItem()) {
            this.input = itemstack.copy();
            this.setupRecipeList(p_75130_1_, itemstack);
        }

    }

    private void setupRecipeList(IInventory p_217074_1_, ItemStack p_217074_2_) {
        this.recipes.clear();
        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        if (!p_217074_2_.isEmpty()) {
            this.recipes = this.level.getRecipeManager().getRecipesFor(this.getRecipeType(), p_217074_1_, this.level);
        }

    }

    private void setupResultSlot() {
        if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            CuttingRecipe stonecuttingrecipe = this.recipes.get(this.selectedRecipeIndex.get());
            this.resultContainer.setRecipeUsed(stonecuttingrecipe);
            this.resultSlot.set(stonecuttingrecipe.assemble(this.container, this.level.registryAccess()));
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    @OnlyIn(Dist.CLIENT)
    public void registerUpdateListener(Runnable p_217071_1_) {
        this.slotUpdateListener = p_217071_1_;
    }

    public boolean canTakeItemForPickAll(ItemStack p_94530_1_, Slot p_94530_2_) {
        return p_94530_2_.container != this.resultContainer && super.canTakeItemForPickAll(p_94530_1_, p_94530_2_);
    }

    public abstract IRecipeType<CR> getRecipeType();

    public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_82846_2_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (p_82846_2_ == 1) {
                item.onCraftedBy(itemstack1, p_82846_1_.level, p_82846_1_);
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (p_82846_2_ == 0) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.level.getRecipeManager().getRecipeFor(this.getRecipeType(), new Inventory(itemstack1), this.level).isPresent()) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (p_82846_2_ >= 2 && p_82846_2_ < 29) {
                if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (p_82846_2_ >= 29 && p_82846_2_ < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(p_82846_1_, itemstack1);
            this.broadcastChanges();
        }

        return itemstack;
    }

    public void removed(PlayerEntity p_75134_1_) {
        super.removed(p_75134_1_);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((p_217079_2_, p_217079_3_) -> {
            this.clearContainer(p_75134_1_, p_75134_1_.level, this.container);
        });
    }
}
