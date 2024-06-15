package net.minecraft.groovy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.main.GroovyScriptLoader;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import groovy.lang.GroovyShell;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroovyCommand {
    private static final Logger LOGGER = Logger.getLogger(GroovyCommand.class.getName());
    private static final SimpleCommandExceptionType ERROR_EXECUTION_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.groovy.failed"));
    private static final Map<UUID, Path> playerFileMap = new ConcurrentHashMap<>();

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("groovy")
                .requires((source) -> source.hasPermission(4)) // Adjust permission level as needed
                .then(Commands.literal("start")
                        .executes((context) -> startCodeInput(context.getSource())))
                .then(Commands.literal("end")
                        .executes((context) -> endCodeInput(context.getSource())))
                .then(Commands.literal("run")
                        .then(Commands.argument("filename", StringArgumentType.string())
                                .executes((context) -> runScript(context.getSource(), StringArgumentType.getString(context, "filename")))))
                .then(Commands.argument("code", StringArgumentType.greedyString())
                        .executes((context) -> addCodeLine(context, StringArgumentType.getString(context, "code"))))
                .then(Commands.literal("reload")
                        .executes((context) -> reloadScripts(context.getSource())))
                .then(Commands.literal("edit")
                        .then(Commands.argument("filename", StringArgumentType.string())
                                .executes((context) -> editScript(context.getSource(), StringArgumentType.getString(context, "filename")))))
                .then(Commands.literal("line")
                        .then(Commands.argument("lineNumber", IntegerArgumentType.integer(1))
                                .then(Commands.argument("code", StringArgumentType.greedyString())
                                        .executes((context) -> editLine(context, IntegerArgumentType.getInteger(context, "lineNumber"), StringArgumentType.getString(context, "code")))))));
    }

    private static int reloadScripts(CommandSource source) {
        GroovyScriptLoader.reloadGroovyScripts("debug");
        source.sendSuccess(new StringTextComponent("Groovy scripts reloaded successfully."), true);
        return 1;
    }

    private static int startCodeInput(CommandSource source) {
        MinecraftServer minecraftserver = source.getServer();
        assert source.getEntity() != null;
        UUID playerUUID = source.getEntity().getUUID();
        Path debugPath = minecraftserver.getFile("debug").toPath();
        String filename = getUniqueFilename(debugPath, "groovy", "groovy");

        Path filePath = debugPath.resolve(filename + ".groovy");
        playerFileMap.put(playerUUID, filePath);

        try {
            Files.createDirectories(debugPath);
            Files.createFile(filePath);
            source.sendSuccess(new StringTextComponent("Started code input. Use /groovy <code> to add lines and /groovy end to execute. Script is saved as " + filename + ".groovy"), false);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating Groovy script file", e);
            source.sendFailure(new StringTextComponent("Error creating Groovy script file"));
        }
        return 1;
    }

    private static String getUniqueFilename(Path directory, String baseName, String extension) {
        int counter = 1;
        String filename = baseName;
        while (Files.exists(directory.resolve(filename + "." + extension))) {
            filename = baseName + counter++;
        }
        return filename;
    }

    private static int addCodeLine(CommandContext<CommandSource> context, String code) {
        UUID playerUUID = context.getSource().getEntity().getUUID();
        Path filePath = playerFileMap.get(playerUUID);
        if (filePath != null) {
            try {
                Files.write(filePath, (code + "\n").getBytes(), StandardOpenOption.APPEND);
                context.getSource().sendSuccess(new StringTextComponent("Added line (" + (Files.lines(filePath).count()) + "): " + code), false);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error writing to Groovy script file", e);
                context.getSource().sendFailure(new StringTextComponent("Error writing to Groovy script file"));
            }
        } else {
            context.getSource().sendFailure(new StringTextComponent("You need to start code input with /groovy start first."));
        }
        return 1;
    }

    private static int editLine(CommandContext<CommandSource> context, int lineNumber, String code) {
        UUID playerUUID = context.getSource().getEntity().getUUID();
        Path filePath = playerFileMap.get(playerUUID);
        if (filePath != null) {
            try {
                List<String> lines = Files.readAllLines(filePath);
                if (lineNumber <= lines.size() && lineNumber > 0) {
                    lines.set(lineNumber - 1, code);
                    Files.write(filePath, lines);
                    context.getSource().sendSuccess(new StringTextComponent("Edited line (" + lineNumber + "): " + code), false);
                } else {
                    context.getSource().sendFailure(new StringTextComponent("Line number out of bounds."));
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error editing Groovy script file", e);
                context.getSource().sendFailure(new StringTextComponent("Error editing Groovy script file"));
            }
        } else {
            context.getSource().sendFailure(new StringTextComponent("You need to start code input with /groovy start first."));
        }
        return 1;
    }

    private static int endCodeInput(CommandSource source) throws CommandSyntaxException {
        UUID playerUUID = source.getEntity().getUUID();
        Path filePath = playerFileMap.get(playerUUID);
        if (filePath != null) {
            source.sendSuccess(new StringTextComponent("Script saved as " + filePath.getFileName().toString()), false);
            playerFileMap.remove(playerUUID);
            try {
                return executeGroovy(source, new String(Files.readAllBytes(filePath)));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error reading Groovy script file", e);
                source.sendFailure(new StringTextComponent("Error reading Groovy script file"));
                return 0;
            }
        } else {
            source.sendFailure(new StringTextComponent("You need to start code input with /groovy start first."));
            return 0;
        }
    }

    private static int runScript(CommandSource source, String filename) throws CommandSyntaxException {
        MinecraftServer minecraftserver = source.getServer();
        Path debugPath = minecraftserver.getFile("debug").toPath();
        Path filePath = debugPath.resolve(filename + ".groovy");
        if (Files.exists(filePath)) {
            try {
                return executeGroovy(source, new String(Files.readAllBytes(filePath)));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error reading Groovy script file", e);
                source.sendFailure(new StringTextComponent("Error reading Groovy script file"));
                return 0;
            }
        } else {
            source.sendFailure(new StringTextComponent("Script file not found: " + filename + ".groovy"));
            return 0;
        }
    }

    private static int editScript(CommandSource source, String filename) throws CommandSyntaxException {
        MinecraftServer minecraftserver = source.getServer();
        Path debugPath = minecraftserver.getFile("debug").toPath();
        Path filePath = debugPath.resolve(filename + ".groovy");
        if (Files.exists(filePath)) {
            playerFileMap.put(source.getEntity().getUUID(), filePath);
            source.sendSuccess(new StringTextComponent("Editing script: " + filename + ".groovy"), false);
            return 1;
        } else {
            source.sendFailure(new StringTextComponent("Script file not found: " + filename + ".groovy"));
            return 0;
        }
    }

    private static int executeGroovy(CommandSource source, String code) throws CommandSyntaxException {
        GroovyShell shell = new GroovyShell();
        StringWriter outputWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(outputWriter);
        shell.setProperty("out", printWriter); // Redirect Groovy's `out` to capture output

        try {
            Object result = shell.evaluate(code);
            printWriter.flush(); // Ensure all output is captured
            String output = outputWriter.toString().replace("\r\n", "\n").replace("\r", "\n"); // Normalize newlines

            // If the result is null, use the captured output, otherwise use the result
            String message = (result != null ? result.toString() : output.isEmpty() ? "null" : output);

            // Split output by newlines and send each line separately for better chat formatting
            for (String line : message.split("\n")) {
                source.sendSuccess(new StringTextComponent(line), false);
            }

            return 1;
        } catch (Exception e) {
            String errorMessage = e.getMessage().replace("\r\n", "\n").replace("\r", "\n"); // Normalize newlines
            for (String line : errorMessage.split("\n")) {
                source.sendFailure(new StringTextComponent(line));
            }
            LOGGER.log(Level.SEVERE, "Error executing Groovy script", e);
            throw ERROR_EXECUTION_FAILED.create();
        }
    }
}
