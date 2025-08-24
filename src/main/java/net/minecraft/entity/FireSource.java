package net.minecraft.entity;

public enum FireSource {
    SOUL_FIRE("soul_fire"),
    FIRE("fire");


    FireSource(String id) {
        this.id = id;
    }

    public final String id;

    public static FireSource fromId(String id) {
        return switch (id) {
            case "soul_fire" -> SOUL_FIRE;
            case "fire" -> FIRE;
            default -> FIRE;
        };
    }
}
