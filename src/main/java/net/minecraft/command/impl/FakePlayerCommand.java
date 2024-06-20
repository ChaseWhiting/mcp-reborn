package net.minecraft.command.impl;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.testing.AddFakePlayer;

public class FakePlayerCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("addFakePlayer")
                        .requires(commandSource -> commandSource.hasPermission(2))
                        .executes(context -> {
                            MinecraftServer server = context.getSource().getServer();
                            AddFakePlayer.createAndAddFakePlayer(server);
                            return 1;
                        })
        );
    }
}
