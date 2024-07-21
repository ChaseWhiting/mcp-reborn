package net.minecraft.client.main;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroovyScriptLoader {
    private static final Logger LOGGER = Logger.getLogger(GroovyScriptLoader.class.getName());
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
                LOGGER.log(Level.WARNING, "Directory not found: " + folderPath);
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
                    LOGGER.log(Level.SEVERE, "Error loading script: " + filePath.getFileName().toString(), e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading directory: " + folderPath, e);
        } catch (NullPointerException n) {
            LOGGER.log(Level.SEVERE, "A script was being used while being reloaded: " + n);
        }
    }

    private static void logScriptLoad(String scriptName, Path filePath) {
        String logName = hideScriptNames ? obfuscateScriptName(random.nextInt(), filePath) : scriptName;
        LOGGER.log(Level.INFO, "Loaded script: " + logName);
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
            LOGGER.log(Level.SEVERE, "Error generating obfuscated script name", e);
            return file.getFileName().toString();
        }
    }

    @Nullable
    public static List<Object> runCachedGroovyScript(String scriptName) {
        Script script = getScript(scriptName);
        if (script != null) {
            try {
                return (List<Object>) script.run();
            } catch (Exception e) {
                throwException(e);
                return null;
            }
        } else {
            logNullScript(scriptName);
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
        if (script != null) {
            try {
                return (Boolean) script.run();
            } catch (Exception e) {
                throwException(e);
                return null;
            }
        } else {
            logNullScript(scriptName);
            return null;
        }
    }

    @Nullable
    public static Integer getInt(String scriptName) {
        Script script = getScript(scriptName);
        if (script != null) {
            try {
                return (Integer) script.run();
            } catch (Exception e) {
                throwException(e);
                return null;
            }
        } else {
            logNullScript(scriptName);
            return null;
        }
    }

    @Nullable
    public static Double getDouble(String scriptName) {
        Script script = getScript(scriptName);
        if (script != null) {
            try {
                return (Double) script.run();
            } catch (Exception e) {
                throwException(e);
                return null;
            }
        } else {
            logNullScript(scriptName);
            return null;
        }
    }

    @Nullable
    public static String getString(String scriptName) {
        Script script = getScript(scriptName);
        if (script != null) {
            try {
                return (String) script.run();
            } catch (Exception e) {
                throwException(e);
                return null;
            }
        } else {
            logNullScript(scriptName);
            return null;
        }
    }

    public static void throwException(Exception e) {
        LOGGER.log(Level.SEVERE, "Error executing cached Groovy script", e);
    }

    public static void logNullScript(String scriptName) {
        LOGGER.log(Level.SEVERE, "Cached script not found: " + scriptName);
    }

}
