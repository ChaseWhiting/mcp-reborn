package net.minecraft.tileentity;


import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrushableBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class BrushableBlockEntity
extends TileEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrushableBlockEntity.class);
    private static final String LOOT_TABLE_TAG = "LootTable";
    private static final String LOOT_TABLE_SEED_TAG = "LootTableSeed";
    private static final String HIT_DIRECTION_TAG = "hit_direction";
    private static final String ITEM_TAG = "item";
    private static final int BRUSH_COOLDOWN_TICKS = 10;
    private static final int BRUSH_RESET_TICKS = 40;
    private static final int REQUIRED_BRUSHES_TO_BREAK = 10;
    private int brushCount;
    private long brushCountResetsAtTick;
    private long coolDownEndsAtTick;
    private ItemStack item = ItemStack.EMPTY;
    @Nullable
    private Direction hitDirection;
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;

    public BrushableBlockEntity() {
        super(TileEntityType.BRUSHABLE_BLOCK);
    }

    public boolean brush(long l, PlayerEntity player, Direction direction, ItemStack brush) {
        if (this.level != null) {
            if (this.level.getBlockState(worldPosition).getBlock() == Blocks.SNOW_BLOCK) {
                this.item = new ItemStack(Items.SNOWBALL, 4);
            }
        }

        if (this.hitDirection == null) {
            this.hitDirection = direction;
        }

        long brushCountReset = l + 40L;
        long cooldownEndTick = l + 10L;
        int brushCountToComplete = 10;

        if (this.level instanceof ServerWorld) {
            Block block = this.level.getBlockState(this.worldPosition).getBlock();

            if (block instanceof BrushableBlock brushableBlock) {
                brushCountReset = brushableBlock.brushCountResetsAtTick(l);
                cooldownEndTick = brushableBlock.coolDownEndsAtTick(l);
                brushCountToComplete = brushableBlock.brushCountToComplete(this.worldPosition, level);

                long currentTime = l;
                long originalEnd = brushableBlock.coolDownEndsAtTick(currentTime);

// Extract the original duration
                long baseDuration = originalEnd - currentTime; // This is what we want to reduce

// Apply enchantment-based reduction
                int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.EXCAVATION, brush);
                long reducedDuration = Math.max(1L, baseDuration - 2L * level); // Ensure it’s at least 1 tick

// Recalculate new cooldown end time
                cooldownEndTick = currentTime + reducedDuration;
            }
        }






        this.brushCountResetsAtTick = brushCountReset;
        if (l < this.coolDownEndsAtTick || !(this.level instanceof ServerWorld)) {
            return false;
        }
        this.coolDownEndsAtTick = cooldownEndTick;
        this.unpackLootTable(player);
        int n = this.getCompletionState();
        if (++this.brushCount >= brushCountToComplete) {
            this.brushingCompleted(player);
            return true;
        }
        this.level.getBlockTicks().scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 40);
        int n2 = this.getCompletionState();
        if (n != n2) {
            BlockState blockState = this.getBlockState();
            BlockState blockState2 = (BlockState)blockState.setValue(BlockStateProperties.DUSTED, n2);
            this.level.setBlock(this.getBlockPos(), blockState2, 3);
        }
        return false;
    }

    public void unpackLootTable(PlayerEntity player) {
        Object object;
        if (this.lootTable == null || this.level == null || this.level.isClientSide() || this.level.getServer() == null) {
            return;
        }
        LootTable lootTable = this.level.getServer().getLootTables().get(this.lootTable);
        if (player instanceof ServerPlayerEntity) {
            object = (ServerPlayerEntity)player;
            CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayerEntity) object, this.lootTable);
        }
        object = new LootContext.Builder((ServerWorld) this.level).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(this.worldPosition)).withLuck(player.getLuck()).withParameter(LootParameters.THIS_ENTITY, player);
        List<ItemStack> objectArrayList = lootTable.getRandomItems((LootContext.Builder) object, this.lootTableSeed);
        this.item = switch (objectArrayList.size()) {
            case 0 -> ItemStack.EMPTY;
            case 1 -> (ItemStack)objectArrayList.get(0);
            default -> {
                LOGGER.warn("Expected max 1 loot from loot table " + this.lootTable + " got " + objectArrayList.size());
                yield (ItemStack)objectArrayList.get(0);
            }
        };
        this.lootTable = null;
        this.setChanged();
    }

    private void brushingCompleted(PlayerEntity player) {
        Block block;
        if (this.level == null || this.level.getServer() == null) {
            return;
        }
        this.dropContent(player);
        BlockState blockState = this.getBlockState();
        this.level.levelEvent(3010, this.getBlockPos(), Block.getId(blockState));
        Block block2 = this.getBlockState().getBlock();
        if (block2 instanceof BrushableBlock brushableBlock) {
            block = brushableBlock.getTurnsInto();
        } else {
            block = Blocks.AIR;
        }
        this.level.setBlock(this.worldPosition, block.defaultBlockState(), 3);
    }

    private void dropContent(PlayerEntity player) {
        if (this.level == null || this.level.getServer() == null) {
            return;
        }
        this.unpackLootTable(player);
        if (!this.item.isEmpty()) {
            double d = EntityType.ITEM.getWidth();
            double d2 = 1.0 - d;
            double d3 = d / 2.0;
            Direction direction = Objects.requireNonNullElse(this.hitDirection, Direction.UP);
            BlockPos blockPos = this.worldPosition.relative(direction, 1);
            double d4 = (double)blockPos.getX() + 0.5 * d2 + d3;
            double d5 = (double)blockPos.getY() + 0.5 + (double)(EntityType.ITEM.getHeight() / 2.0f);
            double d6 = (double)blockPos.getZ() + 0.5 * d2 + d3;
            ItemEntity itemEntity = new ItemEntity(this.level, d4, d5, d6, this.item.split(this.level.random.nextInt(21) + 10));
            itemEntity.setDeltaMovement(Vector3d.ZERO);
            this.level.addFreshEntity(itemEntity);
            this.item = ItemStack.EMPTY;
        }
    }

    public void checkReset() {
        if (this.level == null) {
            return;
        }
        if (this.brushCount != 0 && this.level.getGameTime() >= this.brushCountResetsAtTick) {
            int n = this.getCompletionState();
            this.brushCount = Math.max(0, this.brushCount - 2);
            int n2 = this.getCompletionState();
            if (n != n2) {
                this.level.setBlock(this.getBlockPos(), (BlockState)this.getBlockState().setValue(BlockStateProperties.DUSTED, n2), 3);
            }
            int n3 = 4;
            this.brushCountResetsAtTick = this.level.getGameTime() + 4L;
        }
        if (this.brushCount == 0) {
            this.hitDirection = null;
            this.brushCountResetsAtTick = 0L;
            this.coolDownEndsAtTick = 0L;
        } else {
            this.level.getBlockTicks().scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), (int)(this.brushCountResetsAtTick - this.level.getGameTime()));
        }
    }

    private boolean tryLoadLootTable(CompoundNBT compoundTag) {
        if (compoundTag.contains(LOOT_TABLE_TAG, 8)) {
            this.lootTable = new ResourceLocation(compoundTag.getString(LOOT_TABLE_TAG));
            this.lootTableSeed = compoundTag.getLong(LOOT_TABLE_SEED_TAG);
            return true;
        }
        return false;
    }

    private boolean trySaveLootTable(CompoundNBT compoundTag) {
        if (this.lootTable == null) {
            return false;
        }
        compoundTag.putString(LOOT_TABLE_TAG, this.lootTable.toString());
        if (this.lootTableSeed != 0L) {
            compoundTag.putLong(LOOT_TABLE_SEED_TAG, this.lootTableSeed);
        }
        return true;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compoundTag = super.getUpdateTag();
        if (this.hitDirection != null) {
            compoundTag.putInt(HIT_DIRECTION_TAG, this.hitDirection.ordinal());
        }
        compoundTag.put(ITEM_TAG, this.item.save(new CompoundNBT()));
        return compoundTag;
    }

    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 16, this.getUpdateTag());
    }



    @Override
    public void load(BlockState s, CompoundNBT compoundTag) {
        super.load(s, compoundTag);
        if (!this.tryLoadLootTable(compoundTag) && compoundTag.contains(ITEM_TAG)) {
            this.item = ItemStack.of(compoundTag.getCompound(ITEM_TAG));
        }
        if (compoundTag.contains(HIT_DIRECTION_TAG)) {
            this.hitDirection = Direction.values()[compoundTag.getInt(HIT_DIRECTION_TAG)];
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundTag) {
        if (!this.trySaveLootTable(compoundTag)) {
            compoundTag.put(ITEM_TAG, this.item.save(new CompoundNBT()));
        }

        return super.save(compoundTag);
    }

    public void setLootTable(ResourceLocation resourceLocation, long l) {
        this.lootTable = resourceLocation;
        this.lootTableSeed = l;
    }

    private int getCompletionState() {
        if (level instanceof ServerWorld && level.getBlockState(worldPosition).getBlock() instanceof BrushableBlock brushableBlock) {
            if (level.getBlockState(worldPosition).getBlock() == Blocks.CRACKED_NETHER_BRICKS && (item.isEmpty() && lootTable == null)) {
                if (brushCount == 0) return 1;
                if (brushCount == 1 || brushCount == 2) return 2;
                if (brushCount >= 3) return 3;
            }

            return brushableBlock.getCompletionState(brushCount, worldPosition, level);
        }

        if (this.brushCount == 0) {
            return 0;
        }
        if (this.brushCount < 3) {
            return 1;
        }
        if (this.brushCount < 6) {
            return 2;
        }
        return 3;
    }

    @Nullable
    public Direction getHitDirection() {
        return this.hitDirection;
    }

    public ItemStack getItem() {
        return this.item;
    }
}