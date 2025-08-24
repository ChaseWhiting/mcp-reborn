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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import groovy.lang.GroovyShell;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroovyCommand {
    private static final Logger LOGGER = Logger.getLogger(GroovyCommand.class.getName());
    private static final SimpleCommandExceptionType ERROR_EXECUTION_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.groovy.failed"));
    private static final SimpleCommandExceptionType ERROR_NO_PATH = new SimpleCommandExceptionType(new StringTextComponent("You need to start code input with /groovy start first."));
    private static final SimpleCommandExceptionType ERROR_NO_PERMISSION = new SimpleCommandExceptionType(new StringTextComponent("You aren't allowed to use this command!"));
    private static final Map<UUID, Path> playerFileMap = new ConcurrentHashMap<>();
    protected static final Set<UUID> allowedPersonnel = new HashSet<>();
    static {
        allowedPersonnel.add(UUID.fromString("133ef920-44e2-46dd-a160-9119baf2a964"));
        allowedPersonnel.add(UUID.fromString("6d48835f-5798-3dfe-91b7-de4161fd7397"));
    }

    protected static boolean hasPermission(PlayerEntity player) {
        return allowedPersonnel.contains(player.getUUID());
    }

    public static final String PATH = "debug";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("groovy")
                .requires((source) -> source.hasPermission(4))
                .then(Commands.literal("start")
                        .executes((context) -> startCodeInput(context.getSource())))
                .then(Commands.literal("end")
                        .executes((context) -> endCodeInput(context.getSource())))
                .then(Commands.literal("run")
                        .then(Commands.argument("filename", StringArgumentType.string())
                                .executes((context) -> runScript(context.getSource(), StringArgumentType.getString(context, "filename")))))
                .then(Commands.literal("code")
                        .then(Commands.argument("code", StringArgumentType.greedyString())
                                .executes((context) -> addCodeLine(context, StringArgumentType.getString(context, "code")))))
                .then(Commands.literal("reload")
                        .executes((context) -> reloadScripts(context.getSource())))
                .then(Commands.literal("edit")
                        .then(Commands.argument("filename", StringArgumentType.string())
                                .executes((context) -> editScript(context.getSource(), StringArgumentType.getString(context, "filename")))))
                .then(Commands.literal("line")
                        .then(Commands.argument("lineNumber", IntegerArgumentType.integer(1))
                                .then(Commands.argument("code", StringArgumentType.greedyString())
                                        .executes((context) -> editLine(context, IntegerArgumentType.getInteger(context, "lineNumber"), StringArgumentType.getString(context, "code"))))))
                .then(Commands.literal("template")
                        .then(Commands.argument("packages", StringArgumentType.greedyString())
                                .executes((context) -> createTemplate(context.getSource(), StringArgumentType.getString(context, "packages"))))));
    }

    private static int reloadScripts(CommandSource source) {
        GroovyScriptLoader.reloadGroovyScripts(PATH);
        source.sendSuccess(new StringTextComponent("Groovy scripts reloaded successfully."), true);
        return 1;
    }


    private static void executeMinecraftCommand(CommandSource source, String command) {
        CommandExecutor commandExecutor = new CommandExecutor(source.getServer());
        commandExecutor.executeCommand(source, command);
    }

    private static int createTemplate(CommandSource source, String packages) {
        MinecraftServer minecraftserver = source.getServer();
        assert source.getEntity() != null;
        UUID playerUUID = source.getEntity().getUUID();
        if (!allowedPersonnel.contains(playerUUID)) {
            source.sendFailure(new StringTextComponent(ERROR_NO_PERMISSION.toString()));
            return 0;
        }
        Path debugPath = minecraftserver.getFile(PATH).toPath();
        String filename = getUniqueFilename(debugPath, "template", "groovy");

        Path filePath = debugPath.resolve(filename + ".groovy");
        playerFileMap.put(playerUUID, filePath);

        try {
            Files.createDirectories(debugPath);
            Files.createFile(filePath);

            String[] imports = packages.split(",");
            StringBuilder importLines = new StringBuilder();
            for (String imp : imports) {
                importLines.append("import ").append(imp.trim()).append("\n");
            }

            Files.write(filePath, importLines.toString().getBytes(), StandardOpenOption.WRITE);
            source.sendSuccess(new StringTextComponent("Template created. Use /groovy <code> to add lines and /groovy end to execute. Script is saved as " + filename + ".groovy"), false);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating Groovy template file", e);
            source.sendFailure(new StringTextComponent("Error creating Groovy template file"));
        }
        return 1;
    }


    private static int startCodeInput(CommandSource source) {
        MinecraftServer minecraftserver = source.getServer();
        assert source.getEntity() != null;
        UUID playerUUID = source.getEntity().getUUID();
        if (!allowedPersonnel.contains(playerUUID)) {
            source.sendFailure(new StringTextComponent(ERROR_NO_PERMISSION.toString()));
            return 0;
        }
        Path debugPath = minecraftserver.getFile(PATH).toPath();
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
        if (!allowedPersonnel.contains(playerUUID)) {
            context.getSource().sendFailure(new StringTextComponent(ERROR_NO_PERMISSION.toString()));
            return 0;
        }
        Path filePath = playerFileMap.get(playerUUID);
        if (filePath != null) {
            try {
                Files.write(filePath, (code + "\n").getBytes(), StandardOpenOption.APPEND);
                context.getSource().sendSuccess(new StringTextComponent("Added line (" + (Files.lines(filePath).count()) + "): " + code), false);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error writing to Groovy script file!", e);
                context.getSource().sendFailure(new StringTextComponent("Error writing to Groovy script file"));
            }
        } else {
            context.getSource().sendFailure(new StringTextComponent(ERROR_NO_PATH.toString()));
        }
        return 1;
    }

    private static int editLine(CommandContext<CommandSource> context, int lineNumber, String code) {
        UUID playerUUID = context.getSource().getEntity().getUUID();
        if (!allowedPersonnel.contains(playerUUID)) {
            context.getSource().sendFailure(new StringTextComponent(ERROR_NO_PERMISSION.toString()));
            return 0;
        }
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
            context.getSource().sendFailure(new StringTextComponent(ERROR_NO_PATH.toString()));
        }
        return 1;
    }

    private static int endCodeInput(CommandSource source) throws CommandSyntaxException {
        UUID playerUUID = source.getEntity().getUUID();
        if (!allowedPersonnel.contains(playerUUID)) {
            source.sendFailure(new StringTextComponent(ERROR_NO_PERMISSION.toString()));
            return 0;
        }
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
            source.sendFailure(new StringTextComponent(ERROR_NO_PATH.toString()));
            return 0;
        }
    }

    private static int runScript(CommandSource source, String filename) throws CommandSyntaxException {
        MinecraftServer minecraftserver = source.getServer();
        Path debugPath = minecraftserver.getFile(PATH).toPath();
        Path filePath = debugPath.resolve(filename + ".groovy");
        assert source.getEntity() != null;
        if (!allowedPersonnel.contains(source.getEntity().getUUID())) {
            source.sendFailure(new StringTextComponent(ERROR_NO_PERMISSION.toString()));
            return 0;
        }
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
        Path debugPath = minecraftserver.getFile(PATH).toPath();
        assert source.getEntity() != null;
        if (!allowedPersonnel.contains(source.getEntity().getUUID())) {
            source.sendFailure(new StringTextComponent(ERROR_NO_PERMISSION.toString()));
            return 0;
        }
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
            MinecraftHelper helper = new MinecraftHelper(source.getServer(), ((PlayerEntity)source.getEntity()));
            shell.setProperty("helper", helper);

        StringWriter outputWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(outputWriter);
        shell.setProperty("out", printWriter);

        try {
            Object result = shell.evaluate(code);
            printWriter.flush();
            String output = outputWriter.toString().replace("\r\n", "\n").replace("\r", "\n");

            String message = (result != null ? result.toString() : output);

            if (message != null && !message.trim().isEmpty()) {
                for (String line : message.split("\n")) {
                    source.sendSuccess(new StringTextComponent(line), false);
                }
            }

            return 1;
        } catch (Exception e) {
            String errorMessage = e.getMessage().replace("\r\n", "\n").replace("\r", "\n");
            for (String line : errorMessage.split("\n")) {
                source.sendFailure(new StringTextComponent(line));
            }
            LOGGER.log(Level.SEVERE, "Error executing Groovy script", e);
            throw ERROR_EXECUTION_FAILED.create();
        }
    }


}
