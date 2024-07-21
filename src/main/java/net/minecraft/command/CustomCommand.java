package net.minecraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Mob;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.text.StringTextComponent;

public class CustomCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("customgear")
                        .requires(source -> source.hasPermission(3))
                        .executes(context -> giveCustomGear(context.getSource()))
        );
    }

    private static int giveCustomGear(CommandSource source) {
        try {
            ServerPlayerEntity player = source.getPlayerOrException();
            giveEnchantedItems(player);
            giveEnchantedArmor(player);
            source.sendSuccess(new StringTextComponent("Custom gear given successfully."), false);
        } catch (CommandSyntaxException e) {
            source.sendFailure(new StringTextComponent("No player found."));
            return 0;
        }

        return 1;
    }

    private static void giveEnchantedArmor(ServerPlayerEntity player) {
        ItemStack[] armor = new ItemStack[]{
                new ItemStack(Items.DIAMOND_HELMET),
                new ItemStack(Items.DIAMOND_CHESTPLATE),
                new ItemStack(Items.DIAMOND_LEGGINGS),
                new ItemStack(Items.DIAMOND_BOOTS)
        };

        for (ItemStack piece : armor) {
            piece.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
            piece.enchant(Enchantments.UNBREAKING, 5);
            piece.enchant(Enchantments.MENDING, 1);
            player.setItemSlot(Mob.getEquipmentSlotForItem(piece), piece);
        }
    }

    private static void giveEnchantedItems(ServerPlayerEntity player) {
        ItemStack shield = new ItemStack(Items.SHIELD);
        shield.enchant(Enchantments.UNBREAKING, 5);
        shield.enchant(Enchantments.MENDING, 1);

        ItemStack crossbow = new ItemStack(Items.GILDED_CROSSBOW);
        crossbow.enchant(Enchantments.QUICK_CHARGE, 4);
        crossbow.enchant(Enchantments.UNBREAKING, 5);
        crossbow.enchant(Enchantments.MENDING, 1);
        crossbow.enchant(Enchantments.MULTISHOT, 3);

        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.getOrCreateTag().putBoolean("Unbreakable", true);

        ItemStack boneBow = new ItemStack(Items.BONE_BOW);
    boneBow.enchant(Enchantments.MENDING, 1);
    boneBow.enchant(Enchantments.UNBREAKING, 5);
    boneBow.enchant(Enchantments.MARROW_QUIVER, 3);

        player.addItem(crossbow);
        player.addItem(boneBow);
        player.addItem(sword);
        player.setItemSlot(EquipmentSlotType.OFFHAND, shield);
        player.addItem(new ItemStack(Items.COOKED_BEEF, 64));
        player.addItem(new ItemStack(Items.GOLDEN_APPLE, 16));
        player.addItem(new ItemStack(Items.FIREWORK_ARROW, 128));
        player.addItem(new ItemStack(Items.BONE_ARROW, 128));
        player.addItem(new ItemStack(Items.TOTEM_OF_UNDYING));
    }
}
