package net.minecraft.block;

public enum WallSide {
    NONE("none"),
    LOW("low"),
    TALL("tall");

    private final String name;

    WallSide(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public String getName() {
        return this.name;
    }
}