package net.minecraft.tileentity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public interface RandomizableContainer
extends IInventory {
    public static final String LOOT_TABLE_TAG = "LootTable";
    public static final String LOOT_TABLE_SEED_TAG = "LootTableSeed";

    @Nullable
    public ResourceLocation getLootTable();

    public void setLootTable(@Nullable ResourceLocation var1);

    default public void setLootTable(ResourceLocation resourceLocation, long l) {
        this.setLootTable(resourceLocation);
        this.setLootTableSeed(l);
    }

    public long getLootTableSeed();

    public void setLootTableSeed(long var1);

    public BlockPos getBlockPos();

    @Nullable
    public World getLevel();

    public static void setBlockEntityLootTable(IWorld blockGetter, Random randomSource, BlockPos blockPos, ResourceLocation resourceLocation) {
        TileEntity blockEntity = blockGetter.getBlockEntity(blockPos);
        if (blockEntity instanceof RandomizableContainer) {
            RandomizableContainer randomizableContainer = (RandomizableContainer)((Object)blockEntity);
            randomizableContainer.setLootTable(resourceLocation, randomSource.nextLong());
        }
    }

    default public boolean tryLoadLootTable(CompoundNBT compoundTag) {
        if (compoundTag.contains(LOOT_TABLE_TAG, 8)) {
            this.setLootTable(new ResourceLocation(compoundTag.getString(LOOT_TABLE_TAG)));
            this.setLootTableSeed(compoundTag.getLong(LOOT_TABLE_SEED_TAG));
            return true;
        }
        return false;
    }

    default public boolean trySaveLootTable(CompoundNBT compoundTag) {
        ResourceLocation resourceLocation = this.getLootTable();
        if (resourceLocation == null) {
            return false;
        }
        compoundTag.putString(LOOT_TABLE_TAG, resourceLocation.toString());
        long l = this.getLootTableSeed();
        if (l != 0L) {
            compoundTag.putLong(LOOT_TABLE_SEED_TAG, l);
        }
        return true;
    }

    default public void unpackLootTable(@Nullable PlayerEntity player) {
        World level = this.getLevel();
        BlockPos blockPos = this.getBlockPos();
        ResourceLocation resourceLocation = this.getLootTable();
        if (resourceLocation != null && level != null && level.getServer() != null) {
            LootTable lootTable = level.getServer().getLootTables().get(resourceLocation);
            if (player instanceof ServerPlayerEntity) {
                CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayerEntity) player, resourceLocation);
            }
            this.setLootTable(null);
            LootContext.Builder builder = new LootContext.Builder((ServerWorld) this.getLevel()).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(this.getBlockPos()));
            if (player != null) {
                builder.withLuck(player.getLuck()).withParameter(LootParameters.THIS_ENTITY, player);
            }
            lootTable.fill(this, builder, this.getLootTableSeed());
        }
    }
}
