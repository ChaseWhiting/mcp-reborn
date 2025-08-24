package net.minecraft.item.equipment.trim;

import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class ModelModifier {

    private static final String DIR_PATH = "G:\\MCP-Reborn-1.16-MOJO\\src\\main\\resources\\assets\\minecraft\\models\\item";

    public static void main(String[] args) throws IOException {
        String[] fileNames = {"netherite_helmet.json", "netherite_chestplate.json", "netherite_leggings.json", "netherite_boots.json",
        "diamond_helmet.json", "diamond_chestplate.json", "diamond_leggings.json", "diamond_boots.json",
        "iron_helmet.json", "iron_chestplate.json", "iron_leggings.json", "iron_boots.json",
        "chainmail_helmet.json", "chainmail_chestplate.json", "chaimmail.leggings.json", "chainmail_boots.json",
        "leather_helmet.json", "leather_chestplate.json", "leather_leggings.json", "leather_boots.json",
        "golden_helmet.json", "golden_chestplate.json", "golden_leggings.json", "golden_boots.json",

                "wither_bone_helmet.json",
                "wither_bone_chestplate.json",
                "wither_bone_leggings.json",
                "wither_bone_boots.json"};

        String[] fileNames1 = {"rose_gold_helmet.json", "rose_gold_chestplate.json", "rose_gold_leggings.json", "rose_gold_boots.json"};


        Map<String, Double> map = Map.ofEntries(
                Map.entry("quartz", 0.1D),
                Map.entry("iron", 0.2D),
                Map.entry("netherite", 0.3D),
                Map.entry("redstone", 0.4D),
                Map.entry("copper", 0.5D),
                Map.entry("gold", 0.6D),
                Map.entry("emerald", 0.7D),
                Map.entry("diamond", 0.8D),
                Map.entry("lapis", 0.9D),
                Map.entry("amethyst", 1.0D),
                Map.entry("resin", 1.1D),
                Map.entry("sculk", 1.2D),
                Map.entry("rose_gold", 1.3D));

        Map<String, Double> map1 = Map.ofEntries(
                Map.entry("rose_gold_darker", 1.3D));

        for (String fileName : fileNames) {
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                processFile(fileName, entry.getKey(), entry.getValue());
            }
        }
    }

    private static void processFile(String fileName, String trimMaterial, double trimType) throws IOException {
        Path filePath = Paths.get(DIR_PATH, fileName);
        if (!Files.exists(filePath)) {
            System.out.println("File not found: " + fileName);
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject json = gson.fromJson(new FileReader(filePath.toFile()), JsonObject.class);

        String itemName = fileName.replace(".json", "");
        String modelName = "minecraft:item/" + itemName + "_" + trimMaterial + "_trim";

        JsonObject predicateObj = new JsonObject();
        predicateObj.addProperty("trim_type", trimType);

        JsonObject overrideObj = new JsonObject();
        overrideObj.addProperty("model", modelName);
        overrideObj.add("predicate", predicateObj);

        // Only prevent duplicates in the original item file
        JsonArray overrides = json.has("overrides") ? json.getAsJsonArray("overrides") : new JsonArray();

        boolean alreadyExists = false;
        for (JsonElement el : overrides) {
            if (!el.isJsonObject()) continue;
            JsonObject existing = el.getAsJsonObject();
            String existingModel = existing.has("model") ? existing.get("model").getAsString() : "";
            if (existingModel.equals(modelName)) {
                alreadyExists = true;
                break;
            }
            if (existing.has("predicate")) {
                JsonObject pred = existing.getAsJsonObject("predicate");
                if (pred.has("trim_type") && pred.get("trim_type").getAsDouble() == trimType) {
                    alreadyExists = true;
                    break;
                }
            }
        }

        // Only add override if it doesn't exist
        if (!alreadyExists) {
            overrides.add(overrideObj);
            json.add("overrides", overrides);

            // Write modified JSON back only if changed
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                gson.toJson(json, writer);
            }
            System.out.println("Added override to: " + fileName);
        } else {
            System.out.println("Override for " + trimMaterial + " already exists in: " + fileName);
        }

        // Always (re)write the *_trim.json file to fix errors
        String slot = getSlotFromItemName(itemName);

        JsonObject newModelJson = new JsonObject();
        newModelJson.addProperty("parent", "minecraft:item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "minecraft:item/" + itemName);
        textures.addProperty("layer1", "minecraft:trims/items/" + slot + "_trim_" + trimMaterial);
        newModelJson.add("textures", textures);

        String newFileName = itemName + "_" + trimMaterial + "_trim.json";
        Path newFilePath = Paths.get(DIR_PATH, newFileName);

        try (FileWriter writer = new FileWriter(newFilePath.toFile())) {
            gson.toJson(newModelJson, writer);
        }
        System.out.println("Overwrote model file: " + newFileName);
    }


    // Helper to extract "slot" (helmet, chestplate, etc.)
    private static String getSlotFromItemName(String itemName) {
        // Assumes format like "iron_helmet", "diamond_chestplate", etc.
        String[] parts = itemName.split("_");
        if (parts.length >= 2) {
            // Last part is usually the slot
            return parts[parts.length - 1];
        } else {
            // fallback: just use itemName
            return itemName;
        }
    }
}
