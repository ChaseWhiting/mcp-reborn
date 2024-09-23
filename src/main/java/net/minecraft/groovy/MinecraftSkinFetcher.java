package net.minecraft.groovy;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;

public class MinecraftSkinFetcher {

    public static String getSkin(UUID playerUUID) throws Exception {
        BufferedReader br = getBufferedReader(playerUUID);
        String output;
        StringBuilder response = new StringBuilder();
        while ((output = br.readLine()) != null) {
            response.append(output);
        }
        br.close();

        // Step 2: Parse the response to get the skin URL
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(response.toString()).getAsJsonObject();
        JsonObject properties = jsonResponse.getAsJsonArray("properties").get(0).getAsJsonObject();

        // Decode the base64-encoded value
        String encodedValue = properties.get("value").getAsString();
        String decodedValue = new String(Base64.getDecoder().decode(encodedValue));

        // Step 3: Extract the skin URL from the decoded JSON
        JsonObject decodedJson = parser.parse(decodedValue).getAsJsonObject();
        String skinUrl = decodedJson.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

        // Output the skin URL
        System.out.println("Skin URL: " + skinUrl);
        return skinUrl;
    }

    private static BufferedReader getBufferedReader(UUID playerUUID) throws IOException {
        String uuid = playerUUID.toString();

        String profileUrl = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;

        URL url = new URL(profileUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        return br;
    }


    public static BufferedImage downloadSkin(String skinUrl) throws Exception {
        URL url = new URL(skinUrl);
        InputStream inputStream = url.openStream();
        BufferedImage skinImage = ImageIO.read(inputStream);
        inputStream.close();
        return skinImage;
    }

    public static File cacheSkin(BufferedImage skinImage, UUID uuid) throws Exception {
        File skinFile = new File("cached_skins", uuid.toString() + ".png");
        if (!skinFile.getParentFile().exists()) {
            skinFile.getParentFile().mkdirs();
        }
        ImageIO.write(skinImage, "png", skinFile);
        return skinFile;
    }
}
