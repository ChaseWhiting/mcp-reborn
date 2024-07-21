package net.minecraft.groovy;

import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;

public class CommandExecutor {
    private final MinecraftServer server;

    public CommandExecutor(MinecraftServer server) {
        this.server = server;
    }

    public void executeCommand(CommandSource source, String command) {
        try {
            server.getCommands().performCommand(source, command);
        } catch (Exception e) {
            source.sendFailure(new StringTextComponent("Error executing command: " + command));
        }
    }
}
