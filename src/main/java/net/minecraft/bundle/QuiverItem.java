package net.minecraft.bundle;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickAction;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

public class QuiverItem extends Item {
    private static final String TAG_ITEMS = "Items";
    public int MAX_WEIGHT = 64;
    private static final int ARROWS_IN_QUIVER_WEIGHT = 1;
    private static final int BAR_COLOR = MathHelper.color(1.0F, 0.8F, 0.2F);

    public QuiverItem() {
        super(new Item.Properties().tab(ItemGroup.TAB_COMBAT).stacksTo(1));
    }

    public static float getFullnessDisplay(ItemStack quiverStack) {
        return (float) getContentWeight(quiverStack) / (float) ((QuiverItem) quiverStack.getItem()).getMaxWeight(quiverStack);
    }

    public boolean overrideStackedOnOther(ItemStack quiverStack, Slot slot, ClickAction clickType, PlayerEntity player) {
        if (clickType != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack slotStack = slot.getItem();
            if (slotStack.isEmpty()) {
                if (!player.isShiftKeyDown()) {
                    this.playRemoveOneSound(player);
                    removeOne(quiverStack, player).ifPresent(slot::safeInsert);
                } else {
                    dropContents(quiverStack, player);
                }
            } else if (slotStack.getItem() instanceof ArrowItem) {
                int availableSpace = (getMaxWeight(quiverStack) - getContentWeight(quiverStack)) / getItemWeight(slotStack, quiverStack);
                int itemsToAdd = Math.min(slotStack.getCount(), availableSpace);

                if (itemsToAdd > 0) {
                    ItemStack stackToInsert = slotStack.copy();
                    stackToInsert.setCount(itemsToAdd);

                    int itemsInserted = add((QuiverItem) quiverStack.getItem(), quiverStack, stackToInsert, clickType);
                    if (itemsInserted > 0) {
                        this.playInsertSound(player);
                        slotStack.shrink(itemsInserted);
                    }

                    // If there are leftover items that couldn't be added to the quiver, return them to the slot
                    if (slotStack.getCount() > 0) {
                        slot.set(slotStack); // Return the remaining items back to the slot
                    }
                }
            }

            return true;
        }
    }

    public boolean overrideOtherStackedOnMe(ItemStack quiverStack, ItemStack incomingStack, Slot slot, ClickAction clickType, PlayerEntity player, SlotAccess slotAccess) {
        if (clickType == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (incomingStack.isEmpty()) {
                if (!player.isShiftKeyDown()) {
                    removeOne(quiverStack, player).ifPresent((removedItem) -> {
                        this.playRemoveOneSound(player);
                        slotAccess.set(removedItem);
                    });
                } else {
                    dropContents(quiverStack, player);
                }
            } else if (incomingStack.getItem() instanceof ArrowItem) {
                int availableSpace = (getMaxWeight(quiverStack) - getContentWeight(quiverStack)) / getItemWeight(incomingStack, quiverStack);
                int itemsToAdd = Math.min(incomingStack.getCount(), availableSpace);

                if (itemsToAdd > 0) {
                    ItemStack stackToInsert = incomingStack.copy();
                    stackToInsert.setCount(itemsToAdd);

                    int itemsInserted = add((QuiverItem) quiverStack.getItem(), quiverStack, stackToInsert, clickType);
                    if (itemsInserted > 0) {
                        this.playInsertSound(player);
                        incomingStack.shrink(itemsInserted);
                    }

                    // If there are leftover items that couldn't be added to the quiver, return them to the player's inventory
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

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isDiscrete()) {
            if (dropOne(itemstack, player)) {
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

    public boolean isBarVisible(ItemStack stack) {
        return getContentWeight(stack) > 0;
    }

    public int getBarWidth(ItemStack stack) {
        return Math.min(1 + 12 * getContentWeight(stack) / getMaxWeight(stack), 13);
    }

    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    public static int getBarColor() {
        return BAR_COLOR;
    }

    @Override
    public void inventoryTick(ItemStack quiver, World level, Entity entity, int slot, boolean holding) {
        super.inventoryTick(quiver, level, entity, slot, holding);
        updateMaxWeight(quiver);
    }

    private void updateMaxWeight(ItemStack quiver) {
        CompoundNBT nbt = quiver.getOrCreateTag();
        if (nbt.contains("Weight")) {
            this.MAX_WEIGHT = nbt.getInt("Weight");
        } else {
            nbt.putInt("Weight", this.MAX_WEIGHT);
        }
    }

    public int getMaxWeight(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        return nbt.contains("Weight") ? nbt.getInt("Weight") : this.MAX_WEIGHT;
    }

    public void setMaxWeight(ItemStack stack, int weight) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt("Weight", weight);
    }

    private static int add(QuiverItem quiver, ItemStack quiverStack, ItemStack itemStack, ClickAction action) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof ArrowItem) {
            CompoundNBT tag = quiverStack.getOrCreateTag();
            if (!tag.contains("Items")) {
                tag.put("Items", new ListNBT());
            }

            int currentWeight = getContentWeight(quiverStack);
            int itemWeight = getItemWeight(itemStack, quiverStack);
            int insertCount = Math.min(itemStack.getCount(), (quiver.getMaxWeight(quiverStack) - currentWeight) / itemWeight);
            int totalWeight = currentWeight + (itemWeight * itemStack.getCount());

            // Check if the total weight exceeds the maximum allowed weight
            if (totalWeight > quiver.getMaxWeight(quiverStack)) {
                if (itemStack.getMaxStackSize() > 1) {
                    while (itemStack.getCount() > 0 && totalWeight > quiver.getMaxWeight(quiverStack)) {
                        itemStack.shrink(1);
                        totalWeight = currentWeight + (itemWeight * itemStack.getCount());
                    }
                }

                if (totalWeight > quiver.getMaxWeight(quiverStack)) {
                    return 0;
                }
            }

            if (insertCount == 0) {
                return 0;
            } else {
                ListNBT itemList = tag.getList("Items", 10);
                boolean itemAdded = false;

                for (int i = 0; i < itemList.size(); i++) {
                    CompoundNBT existingTag = itemList.getCompound(i);
                    ItemStack existingStack = ItemStack.of(existingTag);

                    if (ItemStack.isSame(existingStack, itemStack) && ItemStack.tagMatches(existingStack, itemStack)) {
                        int potentialNewCount = existingStack.getCount() + insertCount;

                        if (potentialNewCount > existingStack.getMaxStackSize()) {
                            return 0;
                        }

                        int newCount = Math.min(potentialNewCount, existingStack.getMaxStackSize());
                        existingStack.setCount(newCount);
                        existingStack.save(existingTag);
                        itemList.set(i, existingTag);

                        itemAdded = true;
                        break;
                    }
                }

                if (!itemAdded) {
                    if (insertCount > itemStack.getMaxStackSize()) {
                        return 0;
                    }

                    ItemStack newItemStack = itemStack.copy();
                    newItemStack.setCount(insertCount);
                    CompoundNBT newTag = new CompoundNBT();
                    newItemStack.save(newTag);
                    itemList.add(newTag);
                }

                tag.put("Items", itemList);
                itemStack.shrink(insertCount);

                return insertCount;
            }
        } else {
            return 0;
        }
    }

    private static int getItemWeight(ItemStack itemStack, ItemStack quiverStack) {
        return 1;
    }

    public static int getContentWeight(ItemStack stack) {
        return getContents(stack).mapToInt(itemStack -> getItemWeight(itemStack, stack) * itemStack.getCount()).sum();
    }

    private static Optional<ItemStack> removeOne(ItemStack quiverStack, PlayerEntity player) {
        CompoundNBT compoundNBT = quiverStack.getOrCreateTag();
        if (!compoundNBT.contains("Items")) {
            return Optional.empty();
        } else {
            ListNBT itemList = compoundNBT.getList("Items", 10);
            if (itemList.isEmpty()) {
                return Optional.empty();
            } else {
                int lastIndex = itemList.size() - 1;
                CompoundNBT itemTag = itemList.getCompound(lastIndex);
                ItemStack itemStack = ItemStack.of(itemTag);

                itemList.remove(lastIndex);

                if (itemList.isEmpty()) {
                    compoundNBT.remove("Items");
                } else {
                    compoundNBT.put("Items", itemList);
                }

                return Optional.of(itemStack);
            }
        }
    }

    public static boolean dropContents(ItemStack quiverStack, PlayerEntity player) {
        CompoundNBT compoundNBT = quiverStack.getOrCreateTag();
        if (!compoundNBT.contains("Items")) {
            return false;
        } else {
            if (player instanceof ServerPlayerEntity) {
                ListNBT listNBT = compoundNBT.getList("Items", 10);

                for (int i = 0; i < listNBT.size(); ++i) {
                    CompoundNBT compoundNBT1 = listNBT.getCompound(i);
                    ItemStack itemStack = ItemStack.of(compoundNBT1);
                    player.drop(itemStack, true);
                }
            }

            quiverStack.removeTagKey("Items");
            return true;
        }
    }

    public static boolean dropOne(ItemStack quiverStack, PlayerEntity player) {
        CompoundNBT nbt = quiverStack.getOrCreateTag();
        if (!nbt.contains("Items")) {
            return false;
        } else {
            if (player instanceof ServerPlayerEntity) {
                ListNBT listNBT = nbt.getList("Items", 10);
                if (listNBT.isEmpty()) {
                    return false;
                }
                int lastIndex = listNBT.size() - 1;
                CompoundNBT data = listNBT.getCompound(lastIndex);
                ItemStack stack = ItemStack.of(data);
                player.drop(stack, true);
                listNBT.remove(lastIndex);
                nbt.put("Items", listNBT);
            }

            return true;
        }
    }

    public static void removeArrowFromQuiver(PlayerEntity player, ItemStack quiverStack) {
        QuiverItem quiverItem = (QuiverItem) quiverStack.getItem();
        // Retrieve the contents of the quiver
        List<ItemStack> quiverContents = QuiverItem.getContents(quiverStack).collect(Collectors.toList());

        // Find the first arrow in the quiver and remove one from its stack
        for (ItemStack stack : quiverContents) {
            if (stack.getItem() instanceof ArrowItem) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    // If the stack is empty, remove it from the quiver's NBT
                    quiverContents.remove(stack);
                }
                break;
            }
        }

        // Update the quiver's NBT with the modified contents
        CompoundNBT nbt = quiverStack.getOrCreateTag();
        ListNBT itemList = new ListNBT();
        for (ItemStack item : quiverContents) {
            CompoundNBT itemTag = new CompoundNBT();
            item.save(itemTag);
            itemList.add(itemTag);
        }
        nbt.put("Items", itemList);
        quiverStack.setTag(nbt);
    }

    public static Stream<ItemStack> getContents(ItemStack stack) {
        CompoundNBT compoundNBT = stack.getTag();
        if (compoundNBT == null) {
            return Stream.empty();
        } else {
            ListNBT listNBT = compoundNBT.getList("Items", 10);
            return listNBT.stream().map(CompoundNBT.class::cast).map(ItemStack::of);
        }
    }


    public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent("item.minecraft.quiver.fullness", getContentWeight(stack), getMaxWeight(stack)).withStyle(TextFormatting.GRAY));
    }

    public void onDestroyed(ItemEntity entity) {
        onContainerDestroyed(entity, getContents(entity.getItem()));
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.CROSSBOW_QUICK_CHARGE_1, 0.8F, 0.8F + entity.level.getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.CROSSBOW_QUICK_CHARGE_2, 0.8F, 0.8F + entity.level.getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.CROSSBOW_QUICK_CHARGE_3, 0.8F, 0.8F + entity.level.getRandom().nextFloat() * 0.4F);
    }

    public static void onContainerDestroyed(ItemEntity entity, Stream<ItemStack> contents) {
        World level = entity.level;
        if (!level.isClientSide) {
            contents.forEach(stack -> {
                level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), stack));
            });
        }
    }
}
