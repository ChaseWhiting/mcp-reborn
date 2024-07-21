package net.minecraft.command;

import com.mojang.brigadier.CommandDispatcher;
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
                        .executes(context -> randomItem(context.getSource()))
        );
    }

    private static int randomItem(CommandSource source) {
        try {
            ServerPlayerEntity player = source.getPlayerOrException();
            Item item = Registry.ITEM.getRandom(new Random());
            ItemStack stack = new ItemStack(item);
            player.addItem(stack);
        } catch (CommandSyntaxException e) {
            source.sendFailure(new StringTextComponent("No player found."));
            return 0;
        }

        return 1;
    }
}
