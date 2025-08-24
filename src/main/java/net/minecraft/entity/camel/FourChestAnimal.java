package net.minecraft.entity.camel;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public abstract class FourChestAnimal extends Animal implements IInventoryChangedListener {
    private static final DataParameter<Boolean> DATA_ID_CHEST_1 = EntityDataManager.defineId(FourChestAnimal.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_ID_CHEST_2 = EntityDataManager.defineId(FourChestAnimal.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_ID_CHEST_3 = EntityDataManager.defineId(FourChestAnimal.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_ID_CHEST_4 = EntityDataManager.defineId(FourChestAnimal.class, DataSerializers.BOOLEAN);

    public Inventory getInventory() {
        return inventory;
    }

    protected Inventory inventory;

    private static final DataParameter<Boolean>[] CHEST_PARAMETERS = new DataParameter[]{DATA_ID_CHEST_1, DATA_ID_CHEST_2, DATA_ID_CHEST_3, DATA_ID_CHEST_4};

    protected FourChestAnimal(EntityType<? extends Animal> type, World world) {
        super(type, world);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        for (DataParameter<Boolean> dataParameter : CHEST_PARAMETERS) {
            this.entityData.define(dataParameter, false);
        }
    }

    public boolean hasChest(int id) {
        return this.entityData.get(CHEST_PARAMETERS[id + 1]);
    }

    public void setChest(int id, boolean b) {
        this.entityData.set(CHEST_PARAMETERS[id + 1], b);
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.hasChest(1)) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Blocks.CHEST);
            }

            this.setChest(1, false);
        }
        if (this.hasChest(2)) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Blocks.CHEST);
            }

            this.setChest(2, false);
        }
        if (this.hasChest(3)) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Blocks.CHEST);
            }

            this.setChest(3, false);
        }
        if (this.hasChest(4)) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Blocks.CHEST);
            }

            this.setChest(4, false);
        }
    }

    protected int getInventorySize() {
        return this.hasChest(4) ? 9 * 4 : hasChest(3) ? 9 * 3 : hasChest(2) ? 9 * 2 : hasChest(1) ? 9 : 0;
    }

    protected void createInventory() {
        Inventory inventory = this.inventory;
        this.inventory = new Inventory(this.getInventorySize());
        if (inventory != null) {
            inventory.removeListener(this);
            int i = Math.min(inventory.getContainerSize(), this.inventory.getContainerSize());

            for(int j = 0; j < i; ++j) {
                ItemStack itemstack = inventory.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(j, itemstack.copy());
                }
            }
        }

        this.inventory.addListener(this);
    }

    public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
        super.addAdditionalSaveData(p_213281_1_);
        p_213281_1_.putBoolean("hasChest1", this.hasChest(1));
        p_213281_1_.putBoolean("hasChest2", this.hasChest(2));
        p_213281_1_.putBoolean("hasChest3", this.hasChest(3));
        p_213281_1_.putBoolean("hasChest4", this.hasChest(4));
        if (this.hasChest(1)) {
            ListNBT listnbt = new ListNBT();

            for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    CompoundNBT compoundnbt = new CompoundNBT();
                    compoundnbt.putByte("Slot", (byte)i);
                    itemstack.save(compoundnbt);
                    listnbt.add(compoundnbt);
                }
            }

            p_213281_1_.put("Items", listnbt);
        }

    }

    public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
        super.readAdditionalSaveData(p_70037_1_);
        this.setChest(1, p_70037_1_.getBoolean("HasChest1"));
        this.setChest(2, p_70037_1_.getBoolean("HasChest2"));
        this.setChest(3, p_70037_1_.getBoolean("HasChest3"));
        this.setChest(4, p_70037_1_.getBoolean("HasChest4"));


        if (this.hasChest(1)) {
            ListNBT listnbt = p_70037_1_.getList("Items", 10);
            this.createInventory();

            for(int i = 0; i < listnbt.size(); ++i) {
                CompoundNBT compoundnbt = listnbt.getCompound(i);
                int j = compoundnbt.getByte("Slot") & 255;
                if (j >= 2 && j < this.inventory.getContainerSize()) {
                    this.inventory.setItem(j, ItemStack.of(compoundnbt));
                }
            }
        }
    }

}
