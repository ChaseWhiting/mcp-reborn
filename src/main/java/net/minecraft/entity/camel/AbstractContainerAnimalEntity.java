package net.minecraft.entity.camel;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.dyeable.IDyeSource;
import net.minecraft.item.dyeable.IDyeableBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public abstract class AbstractContainerAnimalEntity extends Animal implements IInventory, INamedContainerProvider {
    protected NonNullList<ItemStack> itemStacks = NonNullList.withSize(54, ItemStack.EMPTY);

    protected AbstractContainerAnimalEntity(EntityType<? extends Animal> type, World world) {
        super(type, world);
    }

    protected static final DataParameter<Boolean> HAS_CHEST =
            EntityDataManager.defineId(AbstractContainerAnimalEntity.class, DataSerializers.BOOLEAN);

    // --- Data Parameter registration ---
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_CHEST, false);
    }

    // --- Has Chest logic ---
    public boolean hasChest() {
        return this.entityData.get(HAS_CHEST);
    }

    public void setHasChest(boolean hasChest) {
        this.entityData.set(HAS_CHEST, hasChest);
        if (!hasChest) {
            this.clearContent();
        }
    }

    // ----- Inventory implementation -----
    @Override
    public int getContainerSize() {
        return 54;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : itemStacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return itemStacks.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ItemStackHelper.removeItem(itemStacks, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = itemStacks.get(index);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        itemStacks.set(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        itemStacks.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(PlayerEntity player) {
        return !this.removed && player.distanceToSqr(this) <= 64.0D;
    }

    @Override
    public void clearContent() {
        itemStacks.clear();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    // ----- NBT Saving/Loading -----
    // --- Save/Load only if hasChest ---
    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("HasChest", hasChest());
        if (hasChest()) ItemStackHelper.saveAllItems(nbt, itemStacks);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        setHasChest(nbt.getBoolean("HasChest"));
        itemStacks = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (hasChest() && nbt.contains("Items", 9)) {
            ItemStackHelper.loadAllItems(nbt, itemStacks);
        }
    }

    // ----- Container GUI -----
    // --- Only open inventory if hasChest ---
    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (this instanceof CamelEntity camel && camel.getCarpetColor().isEmpty()) {
            if (itemstack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CarpetBlock carpetBlock) {
                if (!player.abilities.instabuild) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.PIG_SADDLE, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                camel.entityData.set(CamelEntity.CARPET_COLOUR, Optional.of(carpetBlock.getColour()));
                return ActionResultType.sidedSuccess(this.level.isClientSide);
            }
        }

        if (!this.hasChest() && itemstack.getItem() == Blocks.CHEST.asItem() && !this.isBaby()) {
            this.setHasChest(true);
            this.playChestEquipsSound();
            if (!player.abilities.instabuild) {
                itemstack.shrink(1);
            }

            return ActionResultType.sidedSuccess(this.level.isClientSide);
        }

        boolean pass = true;

        if (this instanceof CamelEntity camel) {
            if (camel.getCarpetColor().isPresent()) {
                if (itemstack.getItem() instanceof IDyeSource iDyeSource) {
                    if (iDyeSource.getDyeColor() != camel.getCarpetColor().get()) {
                        pass = false;
                    }
                }
            }
        }

        if (hasChest() && pass) {
            if (!this.level.isClientSide) player.openMenu(this);
            return ActionResultType.sidedSuccess(this.level.isClientSide);
        }

        return super.mobInteract(player, hand);
    }


    public abstract Vector3d[] getVisualChestLocations();

    @Override
    public boolean attemptToShearEquipment(PlayerEntity player, Hand interactionHand, ItemStack itemStack, Mob self) {

        if (self instanceof CamelEntity camel && camel.getCarpetColor().isPresent()) {
            itemStack.hurt(1, player);
            this.gameEvent(GameEvent.SHEAR, player);
            this.level.playSound(player, self, SoundEvents.SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);

            if (level instanceof ServerWorld serverWorld) {
                Vector3d vector3d = camel.getCarpetDismountLocation();

                this.spawnAtLocationNoWorldCoordinates(serverWorld, new ItemStack(CarpetBlock.getDyeConversionMap().get().get(camel.getCarpetColor().get())), vector3d);
            }

            camel.entityData.set(CamelEntity.CARPET_COLOUR, Optional.empty());

            return true;
        }

        if (this.hasChest()) {
            itemStack.hurt(1, player);
            this.gameEvent(GameEvent.SHEAR, player);
            this.level.playSound(player, self, SoundEvents.SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);

            if (this.level instanceof ServerWorld serverWorld) {
                this.spawnAtLocation(serverWorld, Items.CHEST);

                Vector3d[] chestLocations = this.getVisualChestLocations();

                for (int i = 0; i < this.getContainerSize(); ++i) {
                    ItemStack stack = this.getItem(i);

                    Vector3d location = chestLocations[this.random.nextInt(chestLocations.length)];

                    if (!stack.isEmpty()) {
                        this.spawnAtLocationNoWorldCoordinates(serverWorld, stack, location);
                    }
                }
                this.setHasChest(false);
            }

            if (this.level.isClientSide) {
                this.setHasChest(false);
            }

            return true;
        }


        return false;
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        // Drop the chest block itself
        if (!this.level.isClientSide) {
            if (this instanceof CamelEntity camel && camel.getCarpetColor().isPresent()) {
                Block carpet = camel.getCarpetColor().get() == DyeColor.BLACK ? Blocks.RED_CARPET : Blocks.BLACK_CARPET;

                Vector3d vector3d = camel.getCarpetDismountLocation();

                this.spawnAtLocationNoWorldCoordinates(((ServerWorld) level), new ItemStack(((CarpetBlock)carpet).getDyeConversion().get().get(camel.getCarpetColor().get())), vector3d);
            }
        }

        if (this.hasChest()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(net.minecraft.block.Blocks.CHEST);


            }
            // Drop all inventory items, skipping ones with Curse of Vanishing
            Vector3d[] chestLocations = this.getVisualChestLocations();
            for (int i = 0; i < this.getContainerSize(); ++i) {
                ItemStack stack = this.getItem(i);
                if (!stack.isEmpty() && !net.minecraft.enchantment.EnchantmentHelper.hasVanishingCurse(stack)) {
                    Vector3d location = chestLocations[this.random.nextInt(chestLocations.length)];

                    this.spawnAtLocationNoWorldCoordinates(((ServerWorld) level), stack, location);
                }
            }
            // Remove the chest and clear inventory after dropping
            this.setHasChest(false);
        }
    }


    protected void playChestEquipsSound() {
        this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return hasChest() ? createContainerMenu(id, playerInventory, player) : null;
    }

    // Abstract method for subclasses to provide a specific container/menu
    @Nullable
    protected Container createContainerMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return ChestContainer.sixRows(id, playerInventory, this);
    };

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(this.getType().getDescriptionId());
    }
}