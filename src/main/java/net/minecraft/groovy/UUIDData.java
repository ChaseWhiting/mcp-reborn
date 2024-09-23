package net.minecraft.groovy;

import java.util.UUID;
import java.util.regex.Pattern;

public class UUIDData {

    private static final Pattern HEX_PATTERN = Pattern.compile("[0-9a-fA-F]+");
    private static final int DASHLESS_UUID_LENGTH = 32;

    private final UUID uuid;
    private final String dashlessUUID;
    private String playerName = "Notch";

    // Constructor that accepts a dashless UUID string
    public UUIDData(String dashlessUUID, String playerName) {
        if (!isValidDashlessUUID(dashlessUUID)) {
            throw new IllegalArgumentException("Invalid dashless UUID: " + dashlessUUID);
        }
        this.dashlessUUID = dashlessUUID;
        this.playerName = playerName;
        this.uuid = getUUIDFromDashlessString(dashlessUUID);
    }



    // Constructor that accepts a UUID object
    public UUIDData(UUID uuid) {
        this.uuid = uuid;
        this.dashlessUUID = getDashlessUUIDFromUUID(uuid);
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getDashlessUUID() {
        return dashlessUUID;
    }

    public UUID getUUID() {
        return uuid;
    }

    private static UUID getUUIDFromDashlessString(String dashlessUUID) {
        // Insert dashes at the appropriate positions to match the UUID format
        String formattedUUID = dashlessUUID.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"
        );

        // Convert the formatted string into a UUID object
        return UUID.fromString(formattedUUID);
    }

    private static String getDashlessUUIDFromUUID(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    private static boolean isValidDashlessUUID(String dashlessUUID) {
        return dashlessUUID != null &&
                dashlessUUID.length() == DASHLESS_UUID_LENGTH &&
                HEX_PATTERN.matcher(dashlessUUID).matches();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UUIDData uuidData = (UUIDData) obj;
        return uuid.equals(uuidData.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "UUIDData{" +
                "uuid=" + uuid +
                ", dashlessUUID='" + dashlessUUID + '\'' +
                '}';
    }
}
