package net.minecraft.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProfilerParser {

    public static void parseProfilerFile(Path inputFile) {
        // Create a temp file to write the output to
        Path tempFile = Paths.get(inputFile.toString() + ".tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile.toFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {

            String line;
            boolean inProfileDump = false;

            while ((line = reader.readLine()) != null) {
                // Skip non-useful sections (e.g., comments or unnecessary lines)
                if (line.trim().startsWith("//") || line.trim().isEmpty()) {
                    continue;
                }

                // Detect the start and end of the actual profiler data
                if (line.contains("--- BEGIN PROFILE DUMP ---")) {
                    inProfileDump = true;
                    continue;
                }
                if (line.contains("--- END PROFILE DUMP ---")) {
                    inProfileDump = false;
                    continue;
                }

                // Process lines within the profile dump
                if (inProfileDump) {
                    // Remove redundant level indicators like "[00]", "[01]", etc.
                    String cleanedLine = line.replaceAll("\\[\\d{2}\\]", "").trim();

                    // Replace specific patterns for better clarity
                    cleanedLine = cleanedLine.replace("#getChunkCacheMiss", "Chunk Cache Miss")
                            .replace("#getEntities", "Entity Retrieval")
                            .replace("minecraft:", "Entity: ")
                            .replace("#tickNonPassenger", "Non-Passenger Tick")
                            .replace("#getLoadedEntities", "Loaded Entities")
                            .replace("#getChunk", "Chunk Retrieval")
                            .replace("#getChunkNow", "Immediate Chunk Retrieval")
                            .replace("travel", "Entity Travel")
                            .replace("move", "Entity Move")
                            .replace("rest", "Entity Rest")
                            .replace("goalSelector", "AI Goal Selector")
                            .replace("goalUpdate", "AI Goal Update")
                            .replace("ai", "AI Processing")
                            .replace("navigation", "Entity Navigation")
                            .replace("newAi", "New AI Processing")
                            .replace("targetSelector", "Target Selector")
                            .replace("goalCleanup", "AI Goal Cleanup")
                            .replace("jump", "Entity Jump")
                            .replace("look", "Entity Look Direction")
                            .replace("controls", "Entity Controls")
                            .replace("pathfind", "Pathfinding")
                            .replace("canSee", "Visibility Check")
                            .replace("push", "Entity Push")
                            .replace("entityBaseTick", "Entity Base Tick")
                            .replace("livingEntityBaseTick", "Living Entity Base Tick")
                            .replace("mob tick", "Mob Tick")
                            .replace("sensing", "Entity Sensing")
                            .replace("rangeChecks", "Range Checks")
                            .replace("headTurn", "Entity Head Turn")
                            .replace("mobBaseTick", "Mob Base Tick")
                            .replace("looting", "Entity Looting");

                    // Handle patterns like "(24206/98)" for meaningful labels
                    cleanedLine = cleanedLine.replaceAll("\\((\\d+)/(\\d+)\\)", " (Total: $1, Average: $2)");

                    // Write cleaned and processed lines to output
                    writer.write(cleanedLine);
                    writer.newLine();
                }
            }

            System.out.println("Processing complete for: " + inputFile.getFileName());

        } catch (IOException e) {
            System.err.println("Error processing the profiler file: " + e.getMessage());
        }

        // After processing, replace the original file with the temp file
        try {
            Files.move(tempFile, inputFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error replacing original file with processed file: " + e.getMessage());
        }
    }

    public static void processProfilerFiles(String folderPath) {
        Path directoryPath = Paths.get(folderPath);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath, "profile-results*")) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath) && filePath.getFileName().toString().startsWith("profile-results")) {
                    parseProfilerFile(filePath);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading files from folder: " + e.getMessage());
        }
    }

    public static void parse(String folderPath) {
        processProfilerFiles(folderPath);
    }
}
