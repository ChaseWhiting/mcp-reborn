package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.groovy.MinecraftUUIDFetcher;
import net.minecraft.groovy.UUIDData;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.ITextComponent;

import java.util.concurrent.CompletableFuture;

public class UUIDCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("getuuid")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("username", StringArgumentType.greedyString())
                                .suggests(UUIDCommand::suggestPlayers)
                                .executes(context -> getUUID(context, StringArgumentType.getString(context, "username")))
                        )
        );
    }

    private static int getUUID(CommandContext<CommandSource> context, String username) {
        CommandSource source = context.getSource();

        try {
            UUIDData uuidData = MinecraftUUIDFetcher.getUUIDFromUsername(username);
            source.sendSuccess(new StringTextComponent("UUID for " + uuidData.getPlayerName() + " is: " + uuidData.getUUID()), true);
        } catch (Exception e) {
            source.sendFailure(new StringTextComponent("Failed to retrieve UUID for username: " + username));
        }

        return 1;
    }

    private static CompletableFuture<Suggestions> suggestPlayers(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        context.getSource().getLevel().players().stream()
                .map(player -> player.getGameProfile().getName())
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
