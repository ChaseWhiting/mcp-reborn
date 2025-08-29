package net.minecraft.util.time;

public final class TimeUtil {

    // Minecraft default TPS (Ticks Per Second)
    public static final int TICKS_PER_SECOND = 20;

    private TimeUtil() {
        // Prevent instantiation
    }

    // ----------------------------
    // Seconds <-> Ticks
    // ----------------------------
    public static int secondsToTicks(double seconds) {
        return (int) Math.round(seconds * TICKS_PER_SECOND);
    }

    public static double ticksToSeconds(int ticks) {
        return (double) ticks / TICKS_PER_SECOND;
    }

    // ----------------------------
    // Minutes <-> Ticks
    // ----------------------------
    public static int minutesToTicks(double minutes) {
        return (int) Math.round(minutes * 60 * TICKS_PER_SECOND);
    }

    public static double ticksToMinutes(int ticks) {
        return (double) ticks / (60 * TICKS_PER_SECOND);
    }

    // ----------------------------
    // Hours <-> Ticks
    // ----------------------------
    public static int hoursToTicks(double hours) {
        return (int) Math.round(hours * 3600 * TICKS_PER_SECOND);
    }

    public static double ticksToHours(int ticks) {
        return (double) ticks / (3600 * TICKS_PER_SECOND);
    }

    // ----------------------------
    // Convenience conversions
    // ----------------------------
    public static double minutesToSeconds(double minutes) {
        return minutes * 60;
    }

    public static double secondsToMinutes(double seconds) {
        return seconds / 60.0;
    }

    public static double hoursToMinutes(double hours) {
        return hours * 60;
    }

    public static double minutesToHours(double minutes) {
        return minutes / 60.0;
    }

    public static double hoursToSeconds(double hours) {
        return hours * 3600;
    }

    public static double secondsToHours(double seconds) {
        return seconds / 3600.0;
    }
}
