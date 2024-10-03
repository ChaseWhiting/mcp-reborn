package net.minecraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;

import java.util.Random;

public class RandomItemCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("randomitem")
                        .requires(source -> source.hasPermission(3))
                        .executes(context -> randomItem(context.getSource(), 1)) // Default to 1 item if no number is specified
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1)) // Allow specifying the number of items, up to a maximum of 64
                                .executes(context -> randomItem(context.getSource(), IntegerArgumentType.getInteger(context, "amount"))))
        );
    }

    private static int randomItem(CommandSource source, int amount) {
        try {
            ServerPlayerEntity player = source.getPlayerOrException();
            Random random = new Random();

            for (int i = 0; i < amount; i++) {
                Item item = Registry.ITEM.getRandom(random); // Get a random item
                ItemStack stack = new ItemStack(item);

                if (!player.addItem(stack)) {
                    player.drop(stack, true); // Drop the item if the inventory is full
                }
            }

            source.sendSuccess(new StringTextComponent("Gave " + amount + " random item(s) to " + player.getName().getString()), true);
        } catch (CommandSyntaxException e) {
            source.sendFailure(new StringTextComponent("No player found."));
            return 0;
        }

        return 1;
    }
}
