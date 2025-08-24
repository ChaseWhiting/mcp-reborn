package net.minecraft.testing;
// Import necessary classes
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AddFakePlayer {
    public static Map<String, UUID> fakePlayers = new ConcurrentHashMap<>();
    // Method to add a fake player
    public static void addFakePlayer(MinecraftServer server, RegistryKey<World> worldKey, GameProfile profile) {
        // Get the world from the server and cast it to ServerWorld
        ServerWorld world = server.getLevel(worldKey);
        if (world == null) {
            throw new IllegalArgumentException("World not found");
        }

        // Create the Player Interaction Manager for the fake player
        PlayerInteractionManager interactionManager = new PlayerInteractionManager(world);

        // Create the ServerPlayerEntity instance
        ServerPlayerEntity fakePlayer = new ServerPlayerEntity(server, world, profile, interactionManager);

        // Add the fake player to the server's player list (handles most of the setup)
        server.getPlayerList().placeNewPlayer(new NetworkManager(PacketDirection.SERVERBOUND), fakePlayer);

        // Set the player's invulnerability if needed

    }

    public static void sendPacketToAllPlayers(ServerWorld world, PacketBuffer buffer, ResourceLocation resourceLocation) {
        SCustomPayloadPlayPacket packet = new SCustomPayloadPlayPacket(resourceLocation, buffer);

        for (PlayerEntity player : world.players()) {
            ((ServerPlayerEntity) player).connection.send(packet);
        }
    }

    // Method to create and add a fake player
    public static void createAndAddFakePlayer(MinecraftServer server) {
        // Base name for the fake player
        String baseName = "FakePlayer";
        String playerName = baseName;
        int count = 1;

        // Check if the name already exists and generate a unique name
        while (fakePlayers.containsKey(playerName)) {
            playerName = baseName + count;
            count++;
        }

        // Create a GameProfile with a unique UUID and the generated player name
        GameProfile fakePlayerProfile = new GameProfile(UUID.randomUUID(), playerName);

        // Get the world key for the desired world (e.g., the overworld)
        RegistryKey<World> worldKey = World.OVERWORLD;

        // Call the addFakePlayer method with the server, world key, and fake player profile
        addFakePlayer(server, worldKey, fakePlayerProfile);

        // Add the fake player to the map
        fakePlayers.put(playerName, fakePlayerProfile.getId());
    }
}