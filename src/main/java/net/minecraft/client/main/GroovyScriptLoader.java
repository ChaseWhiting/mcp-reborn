package net.minecraft.client.main;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class GroovyScriptLoader {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Marker MARKER = MarkerManager.getMarker("GROOVY");
    private static final GroovyShell shell = new GroovyShell();
    private static final Map<String, Script> loadedScripts = new ConcurrentHashMap<>();
    private static final Random random = new Random();
    private static boolean hideScriptNames = false;

    public static void loadGroovyScripts(String folderPath, boolean hideScriptNames) {
        GroovyScriptLoader.hideScriptNames = hideScriptNames;
        reloadGroovyScripts(folderPath);
    }


    public static void reloadGroovyScripts(String folderPath) {
        try {
            loadedScripts.clear();
            Path path = Paths.get(folderPath);
            if (!Files.exists(path) || !Files.isDirectory(path)) {
                LOGGER.warn(MARKER, "Directory not found: " + folderPath);
                return;
            }

            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, "*.groovy");
            for (Path filePath : directoryStream) {
                try {
                    Script script = shell.parse(filePath.toFile());
                    String scriptName = filePath.getFileName().toString();
                    loadedScripts.put(scriptName, script);
                    logScriptLoad(scriptName, filePath);
                } catch (Exception e) {
                    LOGGER.error(MARKER, "Error loading script: " + filePath.getFileName().toString(), e);
                }
            }
        } catch (IOException e) {
            LOGGER.error(MARKER, "Error reading directory: " + folderPath, e);
        } catch (NullPointerException n) {
            LOGGER.error(MARKER, "A script was being used while being reloaded: " + n);
        }
    }

    private static void logScriptLoad(String scriptName, Path filePath) {
        String logName = hideScriptNames ? obfuscateScriptName(random.nextInt(), filePath) : scriptName;
        LOGGER.info(MARKER, "Loaded script: {}", logName);
    }

    public static Script getScript(String scriptName) {
        return loadedScripts.get(scriptName);
    }

    private static String obfuscateScriptName(int seed, Path file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update((seed + file.toString()).getBytes());
            byte[] hash = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(Integer.toHexString(0xFF & b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(MARKER, "Error generating obfuscated script name", e);
            return file.getFileName().toString();
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static List<Object> runCachedGroovyScript(String scriptName) {
        Script script = getScript(scriptName);
        try {
            return (List<Object>) script.run();
        } catch (Exception e) {
            throwException(e);
            return null;
        }
    }

    @Nullable
    public static List<Object> getList(String scriptName) {
        return runCachedGroovyScript(scriptName);
    }

    @Nullable
    public static Boolean getBoolean(String scriptName) {
        Script script = getScript(scriptName);
        try {
            return (Boolean) script.run();
        } catch (Exception e) {
            throwException(e);
            return null;
        }
    }

    @Nullable
    public static Integer getInt(String scriptName) {
        Script script = getScript(scriptName);
        try {
            return (Integer) script.run();
        } catch (Exception e) {
            throwException(e);
            return null;
        }
    }

    @Nullable
    public static Double getDouble(String scriptName) {
        Script script = getScript(scriptName);
        try {
            return (Double) script.run();
        } catch (Exception e) {
            throwException(e);
            return null;
        }
    }

    @Nullable
    public static String getString(String scriptName) {
        Script script = getScript(scriptName);
        try {
            return (String) script.run();
        } catch (Exception e) {
            throwException(e);
            return null;
        }
    }

    public static void throwException(Exception e) {
        LOGGER.error(MARKER, "Error executing cached Groovy script", e);
    }

    public static void logNullScript(String scriptName) {
        LOGGER.error(MARKER, "Cached script not found: {}", scriptName);
    }

}
