package net.minecraft.groovy;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MinecraftUUIDFetcher {
    public static UUIDData getUUIDFromUsername(String username) throws Exception {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        Scanner scanner = new Scanner(reader);
        StringBuilder response = new StringBuilder();
        while (scanner.hasNextLine()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        // Check if the response is empty (which means the username does not exist)
        if (response.length() == 0) {
            throw new Exception("No UUID found for username: " + username);
        }

        // Parse the JSON response using Gson
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(response.toString()).getAsJsonObject();
        String uuid = jsonResponse.get("id").getAsString();
        String playerName = jsonResponse.get("name").getAsString();

        // Return the UUID
        UUIDData uuidData = new UUIDData(uuid, playerName);
        System.out.println(MinecraftSkinFetcher.getSkin(uuidData.getUUID()));
        return new UUIDData(uuid, playerName);
    }

    public static String getUsernameFromUUID(String uuid) {
        try {
            // Mojang API URL to get the player profile based on the UUID
            String url = "https://api.mojang.com/user/profiles/" + uuid + "/names";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            Scanner scanner = new Scanner(reader);
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Check if the response is empty (which means the UUID does not exist or has no names)
            if (response.length() == 0) {
                return ""; // Return an empty string if no names are found
            }

            // Parse the JSON response using Gson
            JsonParser parser = new JsonParser();
            JsonArray jsonResponse = parser.parse(response.toString()).getAsJsonArray();

            // If the array is empty, return an empty string
            if (jsonResponse.size() == 0) {
                return "";
            }

            // The latest username is usually the last one in the array
            JsonObject latestUsernameObj = jsonResponse.get(jsonResponse.size() - 1).getAsJsonObject();
            return latestUsernameObj.get("name").getAsString();

        } catch (Exception e) {
            // In case of any exception, return an empty string
            return "";
        }
    }



}
