package net.minecraft.item.tool.terraria;

import java.util.*;
import java.util.stream.Stream;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.bundle.SlotAccess;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickAction;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.*;

import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AccessoryHolderItem extends Item {
    private static final String TAG_ITEMS = "Items";

    public AccessoryHolderItem() {
        super(new Item.Properties().tab(ItemGroup.TAB_MISC).stacksTo(1));
    }

    @Override
    public boolean hasCustomScrollBehaviour() {
        return true;
    }

    @Override
    public void fillItemCategory(ItemGroup itemGroup, NonNullList<ItemStack> items) {
        if (this.allowdedIn(ItemGroup.TAB_MISC) && (itemGroup == ItemGroup.TAB_MISC || itemGroup == ItemGroup.TAB_SEARCH)) {
            items.add(new ItemStack(this));
        }
    }

    public boolean overrideStackedOnOther(ItemStack holderStack, Slot slot, ClickAction clickAction, PlayerEntity player, ClickType clickType) {
        if (clickAction != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack slotStack = slot.getItem();
            if (slotStack.isEmpty()) {
                removeOne(holderStack, player).ifPresent(slot::safeInsert);
            } else if (slotStack.getItem() instanceof AccessoryItem || slotStack.getItem() == Items.SHIELD_OF_CTHULHU) {
                int itemsInserted = add(this, holderStack, slotStack, clickAction);
                if (itemsInserted > 0) {
                    slotStack.shrink(itemsInserted);
                }
            }

            return true;
        }
    }

    public boolean overrideOtherStackedOnMe(ItemStack holderStack, ItemStack incomingStack, Slot slot, ClickAction clickAction, PlayerEntity player, SlotAccess slotAccess, ClickType clickType) {
        if (clickAction == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (incomingStack.isEmpty()) {
                removeOne(holderStack, player).ifPresent(slotAccess::set);
            } else if (incomingStack.getItem() instanceof AccessoryItem || incomingStack.getItem() == Items.SHIELD_OF_CTHULHU) {
                int itemsInserted = add(this, holderStack, incomingStack, clickAction);
                if (itemsInserted > 0) {
                    incomingStack.shrink(itemsInserted);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean onScroll(PlayerEntity player, ItemStack bundleStack, int direction) {
        CompoundNBT nbt = bundleStack.getOrCreateTag();
        ListNBT itemList = nbt.getList(TAG_ITEMS, 10);

        if (!itemList.isEmpty()) {
            if (direction > 0) {
                CompoundNBT firstItem = itemList.getCompound(0);
                itemList.remove(0);
                itemList.add(firstItem);
            } else {
                CompoundNBT lastItem = itemList.getCompound(itemList.size() - 1);
                itemList.remove(itemList.size() - 1);
                itemList.add(0, lastItem);
            }


            nbt.put(TAG_ITEMS, itemList);
            bundleStack.setTag(nbt);
        }

        return true;
    }

    public static boolean dropContents(ItemStack holder, PlayerEntity player) {
        CompoundNBT nbt = holder.getOrCreateTag();
        if (!nbt.contains(TAG_ITEMS)) {
            return false;
        } else {
            if (player instanceof ServerPlayerEntity && player.level.isServerSide) {
                ListNBT itemList = nbt.getList(TAG_ITEMS, 10);
                for (int i = 0; i < itemList.size(); ++i) {
                    CompoundNBT itemTag = itemList.getCompound(i);
                    ItemStack itemStack = ItemStack.of(itemTag);
                    player.drop(itemStack, true);
                }
            }
            holder.removeTagKey(TAG_ITEMS);
            return true;
        }
    }

    private static Optional<ItemStack> removeOne(ItemStack holderStack, PlayerEntity player) {
        CompoundNBT compoundNBT = holderStack.getOrCreateTag();
        if (!compoundNBT.contains(TAG_ITEMS)) {
            return Optional.empty();
        } else {
            ListNBT itemList = compoundNBT.getList(TAG_ITEMS, 10);
            if (itemList.isEmpty()) {
                return Optional.empty();
            } else {
                int lastIndex = itemList.size() - 1;
                CompoundNBT itemTag = itemList.getCompound(lastIndex);
                ItemStack itemStack = ItemStack.of(itemTag);

                itemList.remove(lastIndex);
                if (itemList.isEmpty()) {
                    compoundNBT.remove(TAG_ITEMS);
                } else {
                    compoundNBT.put(TAG_ITEMS, itemList);
                }

                holderStack.setTag(compoundNBT);
                return Optional.of(itemStack);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack holder, World world, Entity entity, int slotNumber, boolean inHand) {
        super.inventoryTick(holder, world, entity, slotNumber, inHand);

        Set<Item> tickedItems = new HashSet<>(); // Track item types we’ve ticked

        getContents(holder).forEach(item -> {
            Item itemType = item.getItem();
            if (!tickedItems.contains(itemType)) {
                if (itemType instanceof AccessoryItem) {
                    AccessoryItem accessoryItem = (AccessoryItem) itemType;
                    accessoryItem.tick(item, world, entity, slotNumber, inHand);
                    tickedItems.add(itemType); // Mark this item type as ticked
                }
            }
        });
    }

    private static int add(AccessoryHolderItem holder, ItemStack holderStack, ItemStack itemStack, ClickAction action) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof AccessoryItem || !itemStack.isEmpty() && itemStack.getItem() == Items.SHIELD_OF_CTHULHU) {
            CompoundNBT tag = holderStack.getOrCreateTag();
            if (!tag.contains(TAG_ITEMS)) {
                tag.put(TAG_ITEMS, new ListNBT());
            }

            ListNBT itemList = tag.getList(TAG_ITEMS, 10);

            // Check if the holder already contains 5 unique items
            if (itemList.size() >= 5) {
                return 0; // Don't add more if the limit is reached
            }

            // Check if an item of the same type is already in the holder
            for (int i = 0; i < itemList.size(); i++) {
                CompoundNBT existingTag = itemList.getCompound(i);
                ItemStack existingStack = ItemStack.of(existingTag);
                if (existingStack.getItem() == itemStack.getItem()) {
                    return 0; // Prevent adding if the item type is already present
                }
            }

            int itemsInserted = 0;
            ItemStack newStack = itemStack.copy();
            int countToAdd = Math.min(itemStack.getCount(), newStack.getMaxStackSize());
            newStack.setCount(countToAdd);

            CompoundNBT newTag = new CompoundNBT();
            newStack.save(newTag);
            itemList.add(newTag);

            itemStack.shrink(countToAdd);
            itemsInserted += countToAdd;

            tag.put(TAG_ITEMS, itemList);
            return itemsInserted;
        } else {
            return 0;
        }
    }

    public static Stream<ItemStack> getContents(ItemStack holderStack) {
        CompoundNBT compound = holderStack.getTag();
        if (compound == null) {
            return Stream.empty();
        } else {
            ListNBT listTag = compound.getList(TAG_ITEMS, 10);
            return listTag.stream().map(CompoundNBT.class::cast).map(ItemStack::of);
        }
    }

    public void onDestroyed(ItemEntity itemEntity) {
        dropAllContents(itemEntity, getContents(itemEntity.getItem()));
    }

    public static void dropAllContents(ItemEntity itemEntity, Stream<ItemStack> contents) {
        World level = itemEntity.level;
        if (!level.isClientSide) {
            contents.forEach((itemStack) -> {
                level.addFreshEntity(new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), itemStack));
            });
            itemEntity.getItem().removeTagKey(TAG_ITEMS);
        }
    }

    public void appendHoverText(ItemStack holderStack, World level, List<ITextComponent> flags, ITooltipFlag tooltip) {
        flags.add(new TranslationTextComponent("item.minecraft.accessory_holder.fullness", getContents(holderStack).count()).withStyle(TextFormatting.GRAY));

        CompoundNBT nbt = holderStack.getOrCreateTag();
        if (nbt.contains(TAG_ITEMS)) {
            ListNBT itemList = nbt.getList(TAG_ITEMS, 10);
            int lastIndex = itemList.size() - 1;

            if (lastIndex >= 0) {
                CompoundNBT lastItemTag = itemList.getCompound(lastIndex);
                ItemStack lastItem = ItemStack.of(lastItemTag);

                flags.add(new StringTextComponent("Selected Accessory: ").withStyle(TextFormatting.GRAY));
                IFormattableTextComponent lastItemNameComponent = new StringTextComponent(lastItem.getHoverName().getString()).withStyle(lastItem.getRarity().color);
                ITextComponent lastItemCountComponent = new StringTextComponent(" (" + lastItem.getCount() + ")").withStyle(TextFormatting.GRAY);

                flags.add(lastItemNameComponent.append(lastItemCountComponent));
                if (itemList.size() > 1) {
                    flags.add(new StringTextComponent("-------------------").withStyle(TextFormatting.DARK_GRAY));
                    flags.add(new StringTextComponent("Current Accessories: ").withStyle(TextFormatting.GRAY));

                    int maxItemsToShow = 5;
                    int startIndex = Math.max(0, lastIndex - maxItemsToShow + 1);

                    for (int i = lastIndex - 1; i >= startIndex; i--) {
                        CompoundNBT tag = itemList.getCompound(i);
                        ItemStack item = ItemStack.of(tag);

                        IFormattableTextComponent itemNameComponent = new StringTextComponent(item.getHoverName().getString()).withStyle(item.getRarity().color);
                        ITextComponent itemCountComponent = new StringTextComponent(" (" + item.getCount() + ")").withStyle(TextFormatting.GRAY);
                        flags.add(itemNameComponent.append(itemCountComponent));
                    }
                    if (startIndex > 0) {
                        flags.add(new StringTextComponent("...").withStyle(TextFormatting.GRAY));
                    }
                }
            }
        }
        super.appendHoverText(holderStack, level, flags, tooltip);
    }

}
