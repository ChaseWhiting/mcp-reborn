package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.happy_ghast.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.dyeable.WoolBlock;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;

public class HarnessItem extends Item implements Equipable {
    private final DyeColor color;

    public HarnessItem(DyeColor color, Item.Properties properties) {
        super(properties.stacksTo(1));
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    public ResourceLocation getHarnessTextureFile() {
        return ResourceLocation.withDefaultNamespace("textures/entity/ghast/harness/" + this.getColor().getName() + "_harness.png");
    }

    public Block getWoolColorBlock() {
        return Registry.BLOCK.stream().filter(block -> block instanceof WoolBlock wool && wool.getColour() == this.color)
                .findFirst().orElse(Blocks.WHITE_WOOL);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {

        if (entity instanceof HappyGhastEntity happyGhast) {
            if (!happyGhast.isBaby() && !happyGhast.isRidden()) {
                ItemStack chestSlot = happyGhast.getItemBySlot(EquipmentSlotType.CHEST);

                if (!ItemStack.isSameItemSameTags(chestSlot, stack) && stack.getItem().is(ItemTags.HARNESSES) || chestSlot.isEmpty() || !chestSlot.isEmpty() && !chestSlot.getItem().is(ItemTags.HARNESSES)) {
                    ActionResult<ItemStack> result = this.swapWithEquipmentSlot(entity.level, EquipmentSlotType.CHEST, happyGhast, stack, player, hand);
                    if (result.getResult().consumesAction()) {
                        entity.level.playSound(player, happyGhast, SoundEvents.HARNESS_EQUIP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        happyGhast.setDropChance(EquipmentSlotType.CHEST, 2.0F);
                        return result.getResult();
                    }
                }
            }
        }


        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public EquipmentSlotType getEquipmentSlot() {
        return EquipmentSlotType.CHEST;
    }
}
