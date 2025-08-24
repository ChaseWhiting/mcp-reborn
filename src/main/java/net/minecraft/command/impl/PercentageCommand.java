package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.NumberArgumentType;
import net.minecraft.util.text.StringTextComponent;

public class PercentageCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("percentage")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("number", NumberArgumentType.numberArg(Float.class))
                                .then(Commands.argument("percentage", NumberArgumentType.numberArg(Float.class))
                                        .executes(context -> executeMathOperation(
                                                context,
                                                NumberArgumentType.getNumber(context, "number"),
                                                NumberArgumentType.getNumber(context, "percentage")
                                        ))
                                )
                        )
        );
    }

    private static int executeMathOperation(CommandContext<CommandSource> context, Number number, Number percentage) {
        CommandSource source = context.getSource();

        // Calculate the percentage
        double result = calculatePercentage(number.doubleValue(), percentage.doubleValue());

        // Output the result
        source.sendSuccess(new StringTextComponent(percentage.toString() + " percent of " + number + " is " + result), true);

        return 1;
    }

    private static double calculatePercentage(double number, double percentage) {
        return number * (percentage / 100);
    }
}
