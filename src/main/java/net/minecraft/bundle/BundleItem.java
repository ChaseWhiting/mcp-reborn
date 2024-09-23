package net.minecraft.bundle;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.TooltipComponent;
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
import net.minecraft.stats.Stats;
import net.minecraft.tags.Tag;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;

import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BundleItem extends Item {
    private static final String TAG_ITEMS = "Items";
    private static final String TAG_WEIGHT = "Weight";
    private static final int BUNDLE_IN_BUNDLE_WEIGHT = 4;
    private static final int BAR_COLOR = MathHelper.color(0.4F, 0.4F, 1.0F);

    public BundleItem() {
        super(new Item.Properties().tab(ItemGroup.TAB_COMBAT).stacksTo(1));
    }

    public ItemStack withWeight(int weight) {
        ItemStack bundle = new ItemStack(Items.BUNDLE, 1);
        CompoundNBT nbt = bundle.getOrCreateTag();
        nbt.putInt(TAG_WEIGHT, weight);
        bundle.setTag(nbt);
        return bundle;
    }

    public ItemStack lastItem(ItemStack bundle) {
        // Retrieve the NBT data from the bundle
        CompoundNBT nbt = bundle.getTag();

        // Check if the NBT data exists and contains the "Items" list
        if (nbt != null && nbt.contains(TAG_ITEMS, 9)) { // 9 represents the tag type for LIST
            ListNBT itemList = nbt.getList(TAG_ITEMS, 10); // 10 represents the tag type for COMPOUND

            // Check if the item list is not empty
            if (!itemList.isEmpty()) {
                // Get the last item in the list
                CompoundNBT tag = itemList.getCompound(itemList.size() - 1);
                return ItemStack.of(tag);
            }
        }

        // Return an empty ItemStack if there are no items in the bundle
        return ItemStack.EMPTY;
    }

    public static float getFullnessDisplay(ItemStack p_150767_) {
        return (float) getContentWeight(p_150767_) / (float) ((BundleItem) p_150767_.getItem()).getMaxWeight(p_150767_);
    }

    public boolean overrideStackedOnOther(ItemStack bundleStack, Slot slot, ClickAction clickAction, PlayerEntity player, ClickType clickType) {
        if (clickAction != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack slotStack = slot.getItem();
            if (slotStack.isEmpty()) {
                if (!player.isShiftKeyDown()) {
                    this.playRemoveOneSound(player);
                    removeOne(bundleStack, player).ifPresent(slot::safeInsert);
                } else {
                    dropContents(bundleStack, player);
                }
            } else if (slotStack.getItem().canFitInsideContainerItems()) {
                int availableSpace = (getMaxWeight(bundleStack) - getContentWeight(bundleStack)) / getItemWeight(slotStack, bundleStack);
                int itemsToAdd = Math.min(slotStack.getCount(), availableSpace);

                if (itemsToAdd > 0) {
                    ItemStack stackToInsert = slotStack.copy();
                    stackToInsert.setCount(itemsToAdd);

                    int itemsInserted = add((BundleItem) bundleStack.getItem(), bundleStack, slot.safeTake(stackToInsert.getCount(), availableSpace, player), clickAction);
                    if (itemsInserted > 0) {
                        this.playInsertSound(player);
                        slotStack.shrink(itemsInserted);
                    }

                    // If there are leftover items that couldn't be added to the bundle, return them to the slot
                    if (slotStack.getCount() > 0) {
                        slot.set(slotStack); // Return the remaining items back to the slot
                    }
                }
            }

            return true;
        }
    }


    public boolean overrideOtherStackedOnMe(ItemStack bundleStack, ItemStack incomingStack, Slot slot, ClickAction clickAction, PlayerEntity player, SlotAccess slotAccess, ClickType clickType) {
        if (clickAction == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (incomingStack.isEmpty()) {
                if (!player.isShiftKeyDown()) {
                    removeOne(bundleStack, player).ifPresent((removedItem) -> {
                        this.playRemoveOneSound(player);
                        slotAccess.set(removedItem);
                    });
                } else {
                    dropContents(bundleStack, player);
                }
            } else {
                int availableSpace = (getMaxWeight(bundleStack) - getContentWeight(bundleStack)) / getItemWeight(incomingStack, bundleStack);
                int itemsToAdd = Math.min(incomingStack.getCount(), availableSpace);

                if (itemsToAdd > 0) {
                    ItemStack stackToInsert = incomingStack.copy();
                    stackToInsert.setCount(itemsToAdd);

                    int itemsInserted = add((BundleItem) bundleStack.getItem(), bundleStack, stackToInsert, clickAction);

                    if (itemsInserted > 0) {
                        this.playInsertSound(player);
                        incomingStack.shrink(itemsInserted);
                    }

                    // If there are leftover items that couldn't be added to the bundle, return them to the player's inventory
                    if (incomingStack.getCount() > 0) {
                        slotAccess.set(incomingStack); // Return the remaining items back to the slot
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public ActionResult<ItemStack> use(World p_150760_, PlayerEntity p_150761_, Hand p_150762_) {
        ItemStack itemstack = p_150761_.getItemInHand(p_150762_);
        if (p_150761_.isDiscrete()) {
            if (dropOne(itemstack, p_150761_)) {
                this.playRemoveOneSound(p_150761_);
                p_150761_.awardStat(Stats.ITEM_USED.get(this));
                return ActionResult.sidedSuccess(itemstack, p_150760_.isClientSide);
            }
        }
        if (dropContents(itemstack, p_150761_)) {
            this.playDropContentsSound(p_150761_);
            p_150761_.awardStat(Stats.ITEM_USED.get(this));
            return ActionResult.sidedSuccess(itemstack, p_150760_.isClientSide());
        } else {
            return ActionResult.fail(itemstack);
        }
    }

    public boolean isBarVisible(ItemStack p_150769_) {
        return getContentWeight(p_150769_) > 0;
    }

    public int getBarWidth(ItemStack p_150771_) {
        return Math.min(1 + 12 * getContentWeight(p_150771_) / ((BundleItem) p_150771_.getItem()).getMaxWeight(p_150771_), 13);
    }

    public int getBarColor(ItemStack p_150773_) {
        return BAR_COLOR;
    }

    public static int getBarColor() {
        return BAR_COLOR;
    }

    @Override
    public void inventoryTick(ItemStack bundle, World level, Entity entity, int slot, boolean holding) {
        super.inventoryTick(bundle, level, entity, slot, holding);
        updateMaxWeight(bundle);
    }

    private void updateMaxWeight(ItemStack bundle) {
        CompoundNBT nbt = bundle.getOrCreateTag();
        if (nbt.contains(TAG_WEIGHT)) {
            setMaxWeight(bundle, nbt.getInt(TAG_WEIGHT));
        } else {
            nbt.putInt(TAG_WEIGHT, 64);
        }
    }

    public int getMaxWeight(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        if (!nbt.contains(TAG_WEIGHT)) {
            nbt.putInt(TAG_WEIGHT, 64);
        }
        return nbt.getInt(TAG_WEIGHT);
    }

    public void setMaxWeight(ItemStack stack, int weight) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt(TAG_WEIGHT, weight);
    }

    private static int add(BundleItem bundle, ItemStack bundleStack, ItemStack itemStack, ClickAction action) {
        if (!itemStack.isEmpty() && itemStack.getItem().canFitInsideContainerItems()) {
            CompoundNBT tag = bundleStack.getOrCreateTag();
            if (!tag.contains(TAG_ITEMS)) {
                tag.put(TAG_ITEMS, new ListNBT());
            }

            int currentWeight = getContentWeight(bundleStack);
            int itemWeight = getItemWeight(itemStack, bundleStack);
            int insertCount = Math.min(itemStack.getCount(), (bundle.getMaxWeight(bundleStack) - currentWeight) / itemWeight);
            int totalWeight = currentWeight + (itemWeight * itemStack.getCount());

            // Check if the total weight exceeds the maximum allowed weight
            if (totalWeight > bundle.getMaxWeight(bundleStack)) {
                // If the stack size is 1, skip this process
                if (itemStack.getMaxStackSize() > 1) {
                    // Decrease the item count until the total weight is within the limit
                    while (itemStack.getCount() > 0 && totalWeight > bundle.getMaxWeight(bundleStack)) {
                        itemStack.shrink(1);  // Decrease the item count by 1
                        totalWeight = currentWeight + (itemWeight * itemStack.getCount());  // Recalculate the total weight
                    }
                }

                // If after shrinking the count the total weight is still greater, return 0
                if (totalWeight > bundle.getMaxWeight(bundleStack)) {
                    return 0;
                }
            }

            // If the insert count is still 0, return 0
            if (insertCount == 0) {
                return 0;
            } else {
                ListNBT itemList = tag.getList(TAG_ITEMS, 10);
                boolean itemAdded = false;

                // Iterate through the list to find a matching item
                for (int i = 0; i < itemList.size(); i++) {
                    CompoundNBT existingTag = itemList.getCompound(i);
                    ItemStack existingStack = ItemStack.of(existingTag);

                    // Check if the existing item matches the one being added
                    if (ItemStack.isSame(existingStack, itemStack) && ItemStack.tagMatches(existingStack, itemStack)) {
                        // Calculate the potential new count
                        int potentialNewCount = existingStack.getCount() + insertCount;

                        // If adding items would exceed the max stack size, prevent any insertion
                        if (potentialNewCount > existingStack.getMaxStackSize()) {
                            return 0;
                        }

                        // Add the items to the existing stack
                        int newCount = Math.min(potentialNewCount, existingStack.getMaxStackSize());
                        existingStack.setCount(newCount);
                        existingStack.save(existingTag);
                        itemList.set(i, existingTag);

                        itemAdded = true;
                        break;
                    }
                }

                // If no matching item was found, add a new entry
                if (!itemAdded) {
                    // Ensure that the new item stack doesn't exceed the max stack size
                    if (insertCount > itemStack.getMaxStackSize()) {
                        return 0;
                    }

                    ItemStack newItemStack = itemStack.copy();
                    newItemStack.setCount(insertCount);
                    CompoundNBT newTag = new CompoundNBT();
                    newItemStack.save(newTag);
                    itemList.add(newTag);
                }

                tag.put(TAG_ITEMS, itemList);

                // Remove the added items from the player's inventory
                itemStack.shrink(insertCount);

                return insertCount;
            }
        } else {
            return 0;
        }
    }

    private static Optional<CompoundNBT> getMatchingItem(ItemStack p_150757_, ListNBT p_150758_) {
        return p_150757_.getItem() == (Items.BUNDLE) ? Optional.empty() : p_150758_.stream().filter(CompoundNBT.class::isInstance).map(CompoundNBT.class::cast).filter((p_186350_) -> {
            return ItemStack.tagMatches(ItemStack.of(p_186350_), p_150757_);
        }).findFirst();
    }

    private static int addTest(BundleItem bundle, ItemStack p_150764_, ItemStack p_150765_) {
        if (!p_150765_.isEmpty() && p_150765_.getItem().canFitInsideContainerItems()) {
            CompoundNBT compoundtag = p_150764_.getOrCreateTag();
            if (!compoundtag.contains(TAG_ITEMS)) {
                compoundtag.put(TAG_ITEMS, new ListNBT());
            }

            int i = getContentWeight(p_150764_);
            int j = bundle.getWeight(p_150765_);
            int k = Math.min(p_150765_.getCount(), (64 - i) / j);
            if (k == 0) {
                return 0;
            } else {
                ListNBT listtag = compoundtag.getList(TAG_ITEMS, 10);
                Optional<CompoundNBT> optional = getMatchingItem(p_150765_, listtag);
                if (optional.isPresent()) {
                    CompoundNBT compoundtag1 = optional.get();
                    ItemStack itemstack = ItemStack.of(compoundtag1);
                    itemstack.grow(k);
                    itemstack.save(compoundtag1);
                    listtag.remove(compoundtag1);
                    listtag.add(0, compoundtag1);
                } else {
                    ItemStack itemstack1 = p_150765_.copy();
                    itemstack1.setCount(k);
                    CompoundNBT compoundtag2 = new CompoundNBT();
                    itemstack1.save(compoundtag2);
                    listtag.add(0, compoundtag2);
                }

                return k;
            }
        } else {
            return 0;
        }
    }

    private static int getItemWeight(ItemStack p_150777_, ItemStack bundle) {
        if (p_150777_.getItem() == (Items.BUNDLE)) {
            if (!p_150777_.getOrCreateTag().contains("ItemWeight")) {
                p_150777_.getOrCreateTag().putInt("ItemWeight", 4);
            }
            return p_150777_.getOrCreateTag().getInt("ItemWeight") + getContentWeight(p_150777_);
        } else {
            if ((p_150777_.getItem() == (Items.BEEHIVE) || p_150777_.getItem() == (Items.BEE_NEST)) && p_150777_.hasTag()) {
                CompoundNBT compoundtag = BlockItem.getBlockEntityData(p_150777_);
                if (compoundtag != null && !compoundtag.getList("Bees", 10).isEmpty()) {
                    return 64;
                }
            }

            return p_150777_.getWeight(p_150777_, bundle);
        }
    }

    public static int getContentWeight(ItemStack p_150779_) {
        return getContents(p_150779_).mapToInt((p_186356_) -> {
            return getItemWeight(p_186356_, p_150779_) * p_186356_.getCount();
        }).sum();
    }

    private static Optional<ItemStack> removeOne(ItemStack bundleStack, PlayerEntity player) {
        CompoundNBT compoundNBT = bundleStack.getOrCreateTag();
        if (!compoundNBT.contains(TAG_ITEMS)) {
            return Optional.empty();
        } else {
            ListNBT itemList = compoundNBT.getList(TAG_ITEMS, 10);
            if (itemList.isEmpty()) {
                return Optional.empty();
            } else {
                // Get the last item in the list (the most recently added item)
                int lastIndex = itemList.size() - 1;
                CompoundNBT itemTag = itemList.getCompound(lastIndex);
                ItemStack itemStack = ItemStack.of(itemTag);

                // Remove the item from the bundle
                itemList.remove(lastIndex);

                if (itemList.isEmpty()) {
                    // If no items are left, clear the items list but keep the bundle
                    compoundNBT.remove(TAG_ITEMS);
                } else {
                    compoundNBT.put(TAG_ITEMS, itemList);
                }

                if (!player.isShiftKeyDown()) {
                    if (player.inventory.getCarried().getItem() != Items.BUNDLE) {
                        // If the player isn't carrying anything, set the carried item to the removed item
                        player.inventory.setCarried(itemStack);
                    } else {
                        // Try to find a free slot or a slot that matches the item type

                        player.inventory.add(itemStack);

                    }
                }

                return Optional.of(itemStack);
            }
        }
    }

    public static boolean dropContents(ItemStack bundle, PlayerEntity player) {
        CompoundNBT compoundtag = bundle.getOrCreateTag();
        if (!compoundtag.contains(TAG_ITEMS)) {
            return false;
        } else {
            if (player instanceof ServerPlayerEntity) {
                ListNBT listtag = compoundtag.getList(TAG_ITEMS, 10);

                for (int i = 0; i < listtag.size(); ++i) {
                    CompoundNBT compoundtag1 = listtag.getCompound(i);
                    ItemStack itemstack = ItemStack.of(compoundtag1);
                    player.drop(itemstack, true);
                }
            }

            bundle.removeTagKey(TAG_ITEMS);
            return true;
        }
    }

    public static boolean dropOne(ItemStack bundle, PlayerEntity player) {
        CompoundNBT nbt = bundle.getOrCreateTag();
        if (!nbt.contains(TAG_ITEMS)) {
            return false;
        } else {
            if (player instanceof ServerPlayerEntity) {
                ListNBT listtag = nbt.getList(TAG_ITEMS, 10); // Fetch the list of items stored in the bundle
                if (listtag.isEmpty()) { // Check if the list is empty to prevent out-of-bounds access
                    return false;
                }
                int lastIndex = listtag.size() - 1; // Calculate the index of the last item
                CompoundNBT data = listtag.getCompound(lastIndex); // Get the NBT data of the last item
                ItemStack stack = ItemStack.of(data); // Create an ItemStack from the NBT data
                player.drop(stack, true); // Drop the item in the world
                listtag.remove(lastIndex); // Remove the item from the list
                nbt.put(TAG_ITEMS, listtag); // Update the NBT in the ItemStack to reflect the removal
            }

            return true;
        }
    }

    public static boolean dropItems(ItemStack bundle, PlayerEntity player, int amount, boolean dropByIndex) {
        CompoundNBT nbt = bundle.getOrCreateTag();
        if (nbt.contains(TAG_ITEMS)) {
            if (player instanceof ServerPlayerEntity) {
                ListNBT listtag = nbt.getList(TAG_ITEMS, 10); // Fetch the list of items stored in the bundle
                if (listtag.isEmpty()) { // Check if the list is empty to prevent out-of-bounds access
                    return false;
                }

                if (dropByIndex) {
                    // Drop the item at the specified index if it exists
                    if (amount < listtag.size()) {
                        CompoundNBT data = listtag.getCompound(amount); // Get the NBT data of the item at the specified index
                        ItemStack stack = ItemStack.of(data); // Create an ItemStack from the NBT data
                        player.drop(stack, true); // Drop the item in the world
                        listtag.remove(amount); // Remove the item from the list
                        nbt.put(TAG_ITEMS, listtag); // Update the NBT in the ItemStack to reflect the removal
                        return true;
                    } else {
                        return false; // Index is out of bounds
                    }
                } else {
                    // Drop the specified amount of items
                    for (int i = 0; i < amount && !listtag.isEmpty(); i++) {
                        int lastIndex = listtag.size() - 1; // Calculate the index of the last item
                        CompoundNBT data = listtag.getCompound(lastIndex); // Get the NBT data of the last item
                        ItemStack stack = ItemStack.of(data); // Create an ItemStack from the NBT data
                        player.drop(stack, true); // Drop the item in the world
                        listtag.remove(lastIndex); // Remove the item from the list
                    }
                    nbt.put(TAG_ITEMS, listtag); // Update the NBT in the ItemStack to reflect the removal
                    return true;
                }
            }
        }
        return false;
    }


    private static Stream<ItemStack> getContents(ItemStack p_150783_) {
        CompoundNBT compound = p_150783_.getTag();
        if (compound == null) {
            return Stream.empty();
        } else {
            ListNBT listTag = compound.getList(TAG_ITEMS, 10);
            return listTag.stream().map(CompoundNBT.class::cast).map(ItemStack::of);
        }
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack p_150775_) {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();
        getContents(p_150775_).forEach(nonnulllist::add);
        return Optional.of(new BundleTooltip(nonnulllist, getContentWeight(p_150775_)));
    }

    public int getWeight(ItemStack bundle) {
        if (!bundle.getOrCreateTag().contains("ItemWeight")) {
            bundle.getOrCreateTag().putInt("ItemWeight", 4);
        }
        return super.getWeight(bundle) + bundle.getOrCreateTag().getInt("ItemWeight");
    }

    public void appendHoverText(ItemStack p_150749_, World p_150750_, List<ITextComponent> p_150751_, ITooltipFlag p_150752_) {
        p_150751_.add(new TranslationTextComponent("item.minecraft.bundle.fullness", getContentWeight(p_150749_), getMaxWeight(p_150749_)).withStyle(TextFormatting.GRAY));
        CompoundNBT nbt = p_150749_.getOrCreateTag();

        if (nbt.contains(TAG_ITEMS)) {
            ListNBT itemList = nbt.getList(TAG_ITEMS, 10);
            int lastIndex = itemList.size() - 1;
            int maxItemsToShow = Minecraft.getInstance().options.amountBundleLineShow;
            if (lastIndex >= 0) {
                p_150751_.add(new StringTextComponent("Current Items: ").withStyle(TextFormatting.GRAY));

                // Calculate the starting index to ensure we don't go out of bounds
                int startIndex = Math.max(0, lastIndex - maxItemsToShow + 1);

                for (int i = startIndex; i <= lastIndex; i++) {
                    CompoundNBT tag = itemList.getCompound(i);
                    ItemStack item = ItemStack.of(tag);

                    // Get the rarity color


                    // Create a text component for the item's name using the rarity color
                    IFormattableTextComponent itemNameComponent = new StringTextComponent(item.getHoverName().getString()).rarity(item);

                    // Create a text component for the item count in gray
                    ITextComponent itemCountComponent = new StringTextComponent(" (" + item.getCount() + ")").gray();

                    // Combine the item name and item count components
                    p_150751_.add(itemNameComponent.append(itemCountComponent));
                }

                // If there are more items than the maxItemsToShow, add a "..." to indicate there are more
                if (startIndex > 0) {
                    p_150751_.add(new StringTextComponent("...").withStyle(TextFormatting.GRAY));
                }
            }
        }
    }

    public void onDestroyed(ItemEntity p_150728_) {
        onContainerDestroyed(p_150728_, getContents(p_150728_.getItem()));
    }

    private void playRemoveOneSound(Entity p_186343_) {
        p_186343_.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + p_186343_.level.getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity p_186352_) {
        p_186352_.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + p_186352_.level.getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity p_186354_) {
        p_186354_.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + p_186354_.level.getRandom().nextFloat() * 0.4F);
    }

    public static void onContainerDestroyed(ItemEntity p_150953_, Stream<ItemStack> p_150954_) {
        World level = p_150953_.level;
        if (!level.isClientSide) {
            p_150954_.forEach((p_296893_) -> {
                level.addFreshEntity(new ItemEntity(level, p_150953_.getX(), p_150953_.getY(), p_150953_.getZ(), p_296893_));
            });
        }
    }
}
