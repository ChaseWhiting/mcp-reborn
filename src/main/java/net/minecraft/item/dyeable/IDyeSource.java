package net.minecraft.item.dyeable;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.camel.CamelEntity;
import net.minecraft.entity.happy_ghast.HappyGhastEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface IDyeSource extends IItemProvider {

    public default boolean consumesDurability() {
        return false;
    }

    public DyeColor getDyeColor();

    public default DyeItem dye() {
        return DyeItem.byColor(this.getDyeColor());
    }

    public default void shrinkOrHurt(ItemStack stack, PlayerEntity player) {
        if (this.consumesDurability()) {
            stack.hurt(1, player);
        } else {
            stack.shrink(1);
        }
    }

    public default void playDyeingSound(@Nullable PlayerEntity player, LivingEntity entity) {
        entity.level.playSound(player, entity, SoundEvents.DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    public default ActionResultType interactEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        boolean success = false;
        if (entity instanceof SheepEntity sheepentity) {
            if (sheepentity.isAlive() && !sheepentity.isSheared() && sheepentity.getColor() != this.getDyeColor()) {
                success = true;
                if (!player.level.isClientSide) {
                    sheepentity.setColor(this.getDyeColor());
                    shrinkOrHurt(stack, player);
                }
            }
        }

        if (entity instanceof ShulkerEntity shulkerEntity) {

            if (shulkerEntity.isAlive() && shulkerEntity.getColor() != this.getDyeColor()) {
                success = true;
                if (!player.level.isClientSide) {
                    shulkerEntity.setColor(this.getDyeColor());
                    shrinkOrHurt(stack, player);
                }
            }
        }

        if (entity instanceof HorseEntity horseEntity && horseEntity.canWearArmor()) {

            if (horseEntity.getArmor().getItem() == Items.LEATHER_HORSE_ARMOR && horseEntity.isAlive()) {
                ItemStack armour = horseEntity.getArmor();
                success = true;
                if (!player.level.isClientSide) {
                    horseEntity.getInventory().setItem(1, IDyeableArmorItem.dyeArmor(armour, List.of(this.dye())));
                    horseEntity.getInventory().setChanged();
                }
            }

        }

        if (entity instanceof HappyGhastEntity happyGhast) {
            if (!happyGhast.isRidden() && !happyGhast.isBaby()) {
                ItemStack chestItem = happyGhast.getItemBySlot(EquipmentSlotType.CHEST);

                if (chestItem.getItem().is(ItemTags.HARNESSES) && ((HarnessItem)chestItem.getItem()).getColor() != this.getDyeColor()) {
                    ItemStack newHarness = Registry.ITEM.get(ResourceLocation.withDefaultNamespace(this.getDyeColor().getName() + "_harness"))
                            .getDefaultInstance().copyWithCount(1);

                    newHarness.setTag(chestItem.getOrCreateTag().copy());

                    newHarness.getTag().remove("id");

                    happyGhast.setItemSlot(EquipmentSlotType.CHEST, newHarness);
                    success = true;
                    shrinkOrHurt(stack, player);

                }
            }
        }

        if (entity instanceof CamelEntity camel && camel.getCarpetColor().isPresent() && camel.getCarpetColor().get() != this.getDyeColor()) {
            success = true;
            camel.getEntityData().set(CamelEntity.CARPET_COLOUR, Optional.of(this.getDyeColor()));
            shrinkOrHurt(stack, player);
        }


        if (success) {
            playDyeingSound(player, entity);
            return ActionResultType.sidedSuccess(player.level.isClientSide);
        }

        return ActionResultType.PASS;
    }

    public default ActionResultType useOnEntity(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof ShulkerBoxBlock) {
            DyeColor currentColor = ((ShulkerBoxBlock) block).getColor();
            DyeColor newColor = this.getDyeColor();
            if (newColor == currentColor) {
                return ActionResultType.PASS;
            }
            if (player != null) {
                player.level.playSound(player, pos, SoundEvents.DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
            if (!world.isClientSide) {
                TileEntity tileEntity = world.getBlockEntity(pos);
                if (tileEntity instanceof ShulkerBoxTileEntity) {
                    CompoundNBT oldNBT = ((ShulkerBoxTileEntity) tileEntity).saveToTag(new CompoundNBT());
                    Block newBlock = ShulkerBoxBlock.getBlockByColor(newColor);
                    BlockState newState = newBlock.defaultBlockState().setValue(ShulkerBoxBlock.FACING, state.getValue(ShulkerBoxBlock.FACING));
                    world.setBlock(pos, newState, 3);
                    TileEntity newTile = world.getBlockEntity(pos);
                    if (newTile instanceof ShulkerBoxTileEntity) {
                        ((ShulkerBoxTileEntity) newTile).loadFromTag(oldNBT);
                    }
                    if (player != null && !player.isCreative()) {
                        if (!consumesDurability()) {
                            itemStack.shrink(1);
                        } else {
                            itemStack.hurt(1, player);
                        }
                    }
                }
            }
            return ActionResultType.SUCCESS;
        }


        if (block instanceof IDyeableBlock) {
            IDyeableBlock iDyeableBlock = (IDyeableBlock) block;

            if (IDyeableBlock.canAcceptDye(this.getDyeColor(), iDyeableBlock)) {
                if (player != null) {
                    player.level.playSound(player, pos, SoundEvents.DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
                if (!world.isClientSide) {

                    world.setBlock(pos, iDyeableBlock.colouredState(world, pos, this.getDyeColor()), 3);

                    if (!player.abilities.instabuild) {
                        if (!this.consumesDurability()) {
                            if (!(block instanceof PaneBlock) && !(block instanceof AbstractGlassBlock) && !(block instanceof ConcretePowderBlock)) {
                                itemStack.shrink(1);
                            } else {
                                splitStack(player, itemStack);
                            }
                        } else {
                            itemStack.hurt(1, player);
                        }


                    }

                }

                return ActionResultType.SUCCESS;
            }
        }

        if (block == Blocks.GLASS || block == Blocks.GLASS_PANE) {
            if (player != null) {
                player.level.playSound(player, pos, SoundEvents.DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }

            if (!world.isClientSide) {
                IDyeableBlock iDyeableBlock;
                if (block == Blocks.GLASS) {
                    iDyeableBlock = (IDyeableBlock) (getDyeColor() == DyeColor.BLACK ? Blocks.RED_STAINED_GLASS : Blocks.BLACK_STAINED_GLASS);
                } else {
                    iDyeableBlock = (IDyeableBlock) (getDyeColor() == DyeColor.BLACK ? Blocks.RED_STAINED_GLASS_PANE : Blocks.BLACK_STAINED_GLASS_PANE);
                }
                world.setBlock(pos, iDyeableBlock.colouredState(world, pos, this.getDyeColor()), 3);

                if (!player.abilities.instabuild) {
                    if (!this.consumesDurability()) {
                        splitStack(player, itemStack);
                    } else {
                        itemStack.hurt(1, player);
                    }
                }

                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    public static void splitStack(PlayerEntity player, ItemStack itemStack) {
        if (itemStack.getCount() > 1) {
            ItemStack singleItem = itemStack.split(1);
            CompoundNBT tag = singleItem.getOrCreateTag();
            tag.putInt("Uses", tag.getInt("Uses") + 1);

            player.setItemInHand(Hand.MAIN_HAND, singleItem);

            if (!player.inventory.add(itemStack)) {
                player.drop(itemStack, false);
            }
        } else {
            CompoundNBT tag = itemStack.getOrCreateTag();
            tag.putInt("Uses", tag.getInt("Uses") + 1);

            if (tag.getInt("Uses") > 8) {
                itemStack.shrink(1);
            }
        }
        ItemStack current = player.getItemInHand(Hand.MAIN_HAND);
        if (current.getOrCreateTag().getInt("Uses") > 8) {
            player.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }
    }
}
