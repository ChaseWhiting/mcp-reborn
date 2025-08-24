package net.minecraft.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class DecoratedPotTileEntity extends TileEntity implements RandomizableContainer, ContainerSingleItem {
    public static final String TAG_SHERDS = "sherds";
    public static final String TAG_ITEM = "item";
    public static final int EVENT_POT_WOBBLES = 1;
    public long wobbleStartedAtTick;
    @Nullable
    public WobbleStyle lastWobbleStyle;
    private Decorations decorations;
    private ItemStack item = ItemStack.EMPTY;
    @Nullable
    protected ResourceLocation lootTable;
    protected long lootTableSeed;

    public DecoratedPotTileEntity() {
        super(TileEntityType.DECORATED_POT);
        this.decorations = Decorations.EMPTY;
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundTag) {
        this.decorations.save(compoundTag);
        if (!this.trySaveLootTable(compoundTag) && !this.item.isEmpty()) {
            compoundTag.put(TAG_ITEM, this.item.save(new CompoundNBT()));
        }

        return super.save(compoundTag);
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundTag) {
        super.load(state, compoundTag);
        this.decorations = Decorations.load(compoundTag);
        if (!this.tryLoadLootTable(compoundTag)) {
            this.item = compoundTag.contains(TAG_ITEM, 10) ? ItemStack.of(compoundTag.getCompound(TAG_ITEM)) : ItemStack.EMPTY;
        }
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public final CompoundNBT saveWithoutMetadata() {
        CompoundNBT compoundTag = new CompoundNBT();
        this.save(compoundTag);
        return compoundTag;
    }

    public Direction getDirection() {
        return this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    public Decorations getDecorations() {
        return this.decorations;
    }

    public void setFromItem(ItemStack itemStack) {
        this.decorations = Decorations.load(BlockItem.getBlockEntityData(itemStack));
    }

    public ItemStack getPotAsItem() {
        return createDecoratedPotItem(this.decorations);
    }

    public static ItemStack createDecoratedPotItem(Decorations decorations) {
        ItemStack itemStack = Items.DECORATED_POT.getDefaultInstance();
        CompoundNBT compoundTag = decorations.save(new CompoundNBT());
        BlockItem.setBlockEntityData(itemStack, TileEntityType.DECORATED_POT, compoundTag);
        return itemStack;
    }

    @Override
    @Nullable
    public ResourceLocation getLootTable() {
        return this.lootTable;
    }

    @Override
    public void setLootTable(@Nullable ResourceLocation resourceLocation) {
        this.lootTable = resourceLocation;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long l) {
        this.lootTableSeed = l;
    }

    @Override
    public ItemStack getTheItem() {
        this.unpackLootTable(null);
        return this.item;
    }

    @Override
    public ItemStack splitTheItem(int n) {
        this.unpackLootTable(null);
        ItemStack itemStack = this.item.split(n);
        if (this.item.isEmpty()) {
            this.item = ItemStack.EMPTY;
        }
        return itemStack;
    }

    @Override
    public void setTheItem(ItemStack itemStack) {
        this.unpackLootTable(null);
        this.item = itemStack;
    }

    @Override
    public TileEntity getContainerBlockEntity() {
        return this;
    }

    public void wobble(WobbleStyle wobbleStyle) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 1, wobbleStyle.ordinal());
    }

    @Override
    public boolean triggerEvent(int n, int n2) {
        if (this.level != null && n == 1 && n2 >= 0 && n2 < WobbleStyle.values().length) {
            this.wobbleStartedAtTick = this.level.getGameTime();
            this.lastWobbleStyle = WobbleStyle.values()[n2];
            return true;
        }
        return super.triggerEvent(n, n2);
    }

    public record Decorations(Item back, Item left, Item right, Item front) {
        public static final Decorations EMPTY = new Decorations(Items.BRICK, Items.BRICK, Items.BRICK, Items.BRICK);

        public CompoundNBT save(CompoundNBT compoundTag) {
            if (this.equals(EMPTY)) {
                return compoundTag;
            }
            ListNBT listTag = new ListNBT();
            this.sorted().forEach(item -> listTag.add(StringNBT.valueOf(Registry.ITEM.getKey((Item)item).toString())));
            compoundTag.put(TAG_SHERDS, listTag);
            return compoundTag;
        }

        public Stream<Item> sorted() {
            return Stream.of(this.back, this.left, this.right, this.front);
        }

        public static Decorations load(@Nullable CompoundNBT compoundTag) {
            if (compoundTag == null || !compoundTag.contains(TAG_SHERDS, 9)) {
                return EMPTY;
            }
            ListNBT listTag = compoundTag.getList(TAG_SHERDS, 8);
            return new Decorations(Decorations.itemFromTag(listTag, 0), Decorations.itemFromTag(listTag, 1), Decorations.itemFromTag(listTag, 2), Decorations.itemFromTag(listTag, 3));
        }

        private static Item itemFromTag(ListNBT listTag, int n) {
            if (n >= listTag.size()) {
                return Items.BRICK;
            }
            INBT tag = listTag.get(n);
            return Registry.ITEM.get(ResourceLocation.tryParse(tag.getAsString()));
        }
    }

    public static enum WobbleStyle {
        POSITIVE(7),
        NEGATIVE(10);

        public final int duration;

        private WobbleStyle(int n2) {
            this.duration = n2;
        }
    }



}
