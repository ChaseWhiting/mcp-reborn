package net.minecraft.bundle;

import java.util.*;
import java.util.stream.Stream;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.TooltipComponent;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
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
    private static final String TAG_COLOUR = "Colour";
    private BundleColour colour = BundleColour.REGULAR;
    private static final int BAR_COLOR = MathHelper.color(0.4F, 0.4F, 1.0F);

    public BundleItem() {
        super(new Item.Properties().tab(ItemGroup.TAB_COMBAT).stacksTo(1));
    }

    public boolean onScroll(PlayerEntity player, ItemStack bundleStack, int direction) {
        if (!player.inventory.contains(bundleStack)) return false;

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

    @Override
    public boolean hasCustomScrollBehaviour() {
        return true;
    }

    @Override
    public void fillItemCategory(ItemGroup itemGroup, NonNullList<ItemStack> items) {
        if (this.allowdedIn(ItemGroup.TAB_COMBAT) && (itemGroup == ItemGroup.TAB_COMBAT || itemGroup == ItemGroup.TAB_SEARCH)) {
            for (BundleColour colour : BundleColour.inOrder()) {
                ItemStack coloredBundle = new ItemStack(Items.BUNDLE);
                CompoundNBT nbt = coloredBundle.getOrCreateTag();
                nbt.putInt(TAG_COLOUR, colour.getId());
                coloredBundle.setTag(nbt);
                    items.add(coloredBundle);
            }
        }
    }

    public static void setWeight(ItemStack stack, int weight) {
        stack.getOrCreateTag().putInt("Weight", weight);
    }

    public static ItemStack lastItem(ItemStack bundle) {
        CompoundNBT nbt = bundle.getTag();

        if (nbt != null && nbt.contains(TAG_ITEMS, 9)) {
            ListNBT itemList = nbt.getList(TAG_ITEMS, 10);


            if (!itemList.isEmpty()) {

                CompoundNBT tag = itemList.getCompound(itemList.size() - 1);
                return ItemStack.of(tag);
            }
        }

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

                    int itemsInserted = add((BundleItem) bundleStack.getItem(), bundleStack, stackToInsert, clickAction);
                    if (itemsInserted > 0) {
                        this.playInsertSound(player);
                        slotStack.shrink(itemsInserted);
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

                    if (incomingStack.getCount() > 0) {
                        slotAccess.set(incomingStack);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isDiscrete()) {
            if (dropItems(itemstack, player, 1, false)) {
                this.playRemoveOneSound(player);
                player.awardStat(Stats.ITEM_USED.get(this));
                return ActionResult.sidedSuccess(itemstack, world.isClientSide);
            }
        }
        if (dropContents(itemstack, player)) {
            this.playDropContentsSound(player);
            player.awardStat(Stats.ITEM_USED.get(this));
            return ActionResult.sidedSuccess(itemstack, world.isClientSide());
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
    public boolean verifyTagAfterLoad(CompoundNBT nbt) {
        if (!nbt.contains(TAG_COLOUR)) {
            nbt.putInt(TAG_COLOUR, this.colour.getId());
            return true;
        }
        return super.verifyTagAfterLoad(nbt);
    }

    @Override
    public void inventoryTick(ItemStack bundle, World level, Entity entity, int slot, boolean holding) {
        super.inventoryTick(bundle, level, entity, slot, holding);
        updateMaxWeight(bundle);
        if (bundle.getOrCreateTag().contains(TAG_COLOUR)) {
            this.colour = BundleColour.byId(bundle.getOrCreateTag().getInt(TAG_COLOUR));
            bundle.getOrCreateTag().putInt(TAG_COLOUR, this.colour.getId());
        } else {
            bundle.getOrCreateTag().putInt(TAG_COLOUR, this.colour.getId());
        }
    }

    public static int getBundleId(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.contains(TAG_COLOUR)) {
            return nbt.getInt(TAG_COLOUR);
        } else {
            return 0;
        }
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
        int weight = nbt.getInt(TAG_WEIGHT);
        if (EnchantmentHelper.has(stack, Enchantments.DEEP_POCKETS)) {
            weight *= EnchantmentHelper.getItemEnchantmentLevel(Enchantments.DEEP_POCKETS, stack);
        }
        return weight;
    }

    public void setMaxWeight(ItemStack stack, int weight) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt(TAG_WEIGHT, weight);
    }

    public static void setColour(ItemStack stack, BundleColour colour) {
        stack.getOrCreateTag().putInt(TAG_COLOUR, colour.getId());
    }

    public static void setColour(ItemStack stack, DyeColor dye) {
        setColour(stack, BundleColour.byDye(dye));
    }

    public static void setColour(ItemStack stack, int colour) {
        stack.getOrCreateTag().putInt(TAG_COLOUR, BundleColour.byId(colour).getId());
    }

    private static int add(BundleItem bundle, ItemStack bundleStack, ItemStack itemStack, ClickAction action) {
        if (!itemStack.isEmpty() && itemStack.getItem().canFitInsideContainerItems()) {
            CompoundNBT tag = bundleStack.getOrCreateTag();
            if (!tag.contains(TAG_ITEMS)) {
                tag.put(TAG_ITEMS, new ListNBT());
            }

            int currentWeight = getContentWeight(bundleStack);
            int itemWeight = getItemWeight(itemStack, bundleStack);
            int availableSpace = bundle.getMaxWeight(bundleStack) - currentWeight;
            int insertCount = Math.min(itemStack.getCount(), availableSpace / itemWeight);

            if (insertCount <= 0) {
                return 0;
            }

            ListNBT itemList = tag.getList(TAG_ITEMS, 10);
            boolean itemAdded = false;
            int itemsInserted = 0;

            for (int i = 0; i < itemList.size(); i++) {
                CompoundNBT existingTag = itemList.getCompound(i);
                ItemStack existingStack = ItemStack.of(existingTag);

                if (ItemStack.isSame(existingStack, itemStack) && ItemStack.tagMatches(existingStack, itemStack)) {
                    int newCount = Math.min(existingStack.getCount() + insertCount, existingStack.getMaxStackSize());
                    int itemsToAdd = newCount - existingStack.getCount();

                    existingStack.setCount(newCount);
                    existingStack.save(existingTag);
                    itemList.set(i, existingTag);

                    itemStack.shrink(itemsToAdd);
                    insertCount -= itemsToAdd;
                    itemsInserted += itemsToAdd;
                    itemAdded = true;

                    if (insertCount <= 0) {
                        break;
                    }
                }
            }

            while (insertCount > 0) {
                ItemStack newStack = itemStack.copy();
                int countToAdd = Math.min(insertCount, newStack.getMaxStackSize());
                newStack.setCount(countToAdd);

                CompoundNBT newTag = new CompoundNBT();
                newStack.save(newTag);
                itemList.add(newTag);

                itemStack.shrink(countToAdd);
                insertCount -= countToAdd;
                itemsInserted += countToAdd;
            }

            tag.put(TAG_ITEMS, itemList);
            return itemsInserted;
        } else {
            return 0;
        }
    }

    private static Optional<CompoundNBT> getMatchingItem(ItemStack p_150757_, ListNBT p_150758_) {
        return p_150757_.getItem() == (Items.BUNDLE) ? Optional.empty() : p_150758_.stream().filter(CompoundNBT.class::isInstance).map(CompoundNBT.class::cast).filter((p_186350_) -> {
            return ItemStack.tagMatches(ItemStack.of(p_186350_), p_150757_);
        }).findFirst();
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
                int lastIndex = itemList.size() - 1;
                CompoundNBT itemTag = itemList.getCompound(lastIndex);
                ItemStack itemStack = ItemStack.of(itemTag);

                itemList.remove(lastIndex);

                if (itemList.isEmpty()) {
                    compoundNBT.remove(TAG_ITEMS);
                } else {
                    compoundNBT.put(TAG_ITEMS, itemList);
                }

                bundleStack.setTag(compoundNBT);

                return Optional.of(itemStack);
            }
        }
    }


    public static boolean dropContents(ItemStack bundle, PlayerEntity player) {
        CompoundNBT compoundtag = bundle.getOrCreateTag();
        if (!compoundtag.contains(TAG_ITEMS)) {
            return false;
        } else {
            if (player instanceof ServerPlayerEntity && player.level.isServerSide) {
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
                ListNBT listtag = nbt.getList(TAG_ITEMS, 10);
                if (listtag.isEmpty()) {
                    return false;
                }
                int lastIndex = listtag.size() - 1;
                CompoundNBT data = listtag.getCompound(lastIndex);
                ItemStack stack = ItemStack.of(data);
                player.drop(stack, true);
                listtag.remove(lastIndex);
                nbt.put(TAG_ITEMS, listtag);
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


    public static Stream<ItemStack> getContents(ItemStack p_150783_) {
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

    public void appendHoverText(ItemStack bundle, World level, List<ITextComponent> flags, ITooltipFlag tooltip) {
        // Display the fullness of the bundle
        flags.add(new TranslationTextComponent("item.minecraft.bundle.fullness", getContentWeight(bundle), getMaxWeight(bundle)).withStyle(TextFormatting.GRAY));

        // Get the NBT data from the bundle
        CompoundNBT nbt = bundle.getOrCreateTag();

        if (nbt.contains(TAG_ITEMS)) {
            ListNBT itemList = nbt.getList(TAG_ITEMS, 10);
            int lastIndex = itemList.size() - 1;

            if (lastIndex >= 0) {
                // Show the last item separately
                CompoundNBT lastItemTag = itemList.getCompound(lastIndex);
                ItemStack lastItem = ItemStack.of(lastItemTag);

                // Display the "Current Item" (last item)
                flags.add(new StringTextComponent("Selected Item: ").withStyle(TextFormatting.GRAY));

                // Get the item's name and rarity
                IFormattableTextComponent lastItemNameComponent = new StringTextComponent(lastItem.getHoverName().getString()).rarity(lastItem);

                // Get the item count, displayed in gray
                ITextComponent lastItemCountComponent = new StringTextComponent(" (" + lastItem.getCount() + ")").withStyle(TextFormatting.GRAY);

                // Combine the name and count, then add to the tooltip
                flags.add(lastItemNameComponent.append(lastItemCountComponent));

                // Only show "Current Items" and the separator if there's more than one item
                if (itemList.size() > 1) {
                    // Separator line (optional, adds a little clarity between sections)
                    flags.add(new StringTextComponent("-------------------").withStyle(TextFormatting.DARK_GRAY));

                    // Display the rest of the current items
                    flags.add(new StringTextComponent("Current Items: ").withStyle(TextFormatting.GRAY));

                    // Show the rest of the items in the bundle
                    int maxItemsToShow = Minecraft.getInstance().options.amountBundleLineShow; // Assume this controls the number of items shown
                    int startIndex = Math.max(0, lastIndex - maxItemsToShow + 1);

                    for (int i = lastIndex - 1; i >= startIndex; i--) {
                        CompoundNBT tag = itemList.getCompound(i);
                        ItemStack item = ItemStack.of(tag);

                        // Get the item's name and rarity
                        IFormattableTextComponent itemNameComponent = new StringTextComponent(item.getHoverName().getString()).rarity(item);

                        // Get the item count in gray
                        ITextComponent itemCountComponent = new StringTextComponent(" (" + item.getCount() + ")").withStyle(TextFormatting.GRAY);

                        // Combine name and count, then add to the tooltip
                        flags.add(itemNameComponent.append(itemCountComponent));
                    }

                    // If there are more items than maxItemsToShow, add a "..." to indicate hidden items
                    if (startIndex > 0) {
                        flags.add(new StringTextComponent("...").withStyle(TextFormatting.GRAY));
                    }
                }
            }
        }
        super.appendHoverText(bundle, level, flags, tooltip);
    }


    public void onDestroyed(ItemEntity p_150728_) {
        dropAllContents(p_150728_, getContents(p_150728_.getItem()));
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

    public static void dropAllContents(ItemEntity p_150953_, Stream<ItemStack> p_150954_) {
        World level = p_150953_.level;
        if (!level.isClientSide) {
            p_150954_.forEach((p_296893_) -> {
                level.addFreshEntity(new ItemEntity(level, p_150953_.getX(), p_150953_.getY(), p_150953_.getZ(), p_296893_));
            });
            p_150953_.getItem().removeTagKey(TAG_ITEMS);
        }
    }


    public String getDescriptionId(ItemStack p_77667_1_) {
        BundleColour colour = BundleColour.byId(p_77667_1_.getOrCreateTag().getInt("Colour"));
        return colour.getTranslation().getString();
    }


}
