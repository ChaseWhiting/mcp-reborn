package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

public class GiveSpawnDispenserCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("dispenser")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("item", StringArgumentType.greedyString())
                                .suggests(GiveSpawnDispenserCommand::suggestItems)
                                .executes(context -> giveItemDispenser(context, StringArgumentType.getString(context, "item")))
                        )
        );
    }

    private static int giveItemDispenser(CommandContext<CommandSource> context, String item) {
        CommandSource source = context.getSource();
        try {
            ServerPlayerEntity player = source.getPlayerOrException();
            Item targetItem = getItemByName(item);
            if (targetItem == null) {
                source.sendFailure(new StringTextComponent("No item found with the name: " + item));
                return 0;
            }
            ItemStack dispenser = createDispenserWithItem(targetItem);
            player.addItem(dispenser);

            // Create the translation key for the item
            String translationKey = item.replace("minecraft:", "item.minecraft.");
            TranslationTextComponent itemNameComponent = new TranslationTextComponent(translationKey);

            // Send the success message with the item name
            source.sendSuccess(new StringTextComponent("Given a dispenser filled with " + itemNameComponent.getString() + "!"), true);
        } catch (CommandSyntaxException e) {
            source.sendFailure(new StringTextComponent("No player found."));
            return 0;
        }
        return 1;
    }

    private static Item getItemByName(String name) {
        return Registry.ITEM.getOptional(new ResourceLocation(name))
                .orElse(Items.AIR);
    }

    private static ItemStack createDispenserWithItem(Item item) {
        ItemStack dispenser = new ItemStack(Blocks.DISPENSER);
        CompoundNBT blockEntityTag = new CompoundNBT();
        ListNBT items = new ListNBT();

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = new ItemStack(item, 64);
            CompoundNBT itemTag = new CompoundNBT();
            itemTag.putByte("Slot", (byte) i);
            itemStack.save(itemTag);
            items.add(itemTag);
        }

        blockEntityTag.put("Items", items);
        dispenser.addTagElement("BlockEntityTag", blockEntityTag);

        return dispenser;
    }

    private static CompletableFuture<Suggestions> suggestItems(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        StreamSupport.stream(Registry.ITEM.spliterator(), false)
                .map(Registry.ITEM::getKey)
                .map(ResourceLocation::toString)
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
