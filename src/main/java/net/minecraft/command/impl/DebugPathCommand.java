// DebugPathCommand.java
package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class DebugPathCommand {
    private static boolean debugPathEnabled = false;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("debugpath")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("enabled", BoolArgumentType.bool())
                        .executes(context -> {
                            debugPathEnabled = BoolArgumentType.getBool(context, "enabled");
                            context.getSource().sendSuccess(new StringTextComponent("Debug path rendering " + (debugPathEnabled ? "enabled" : "disabled")), true);
                            return 1;
                        })));
    }

    public static boolean isDebugPathEnabled() {
        return debugPathEnabled;
    }
}
