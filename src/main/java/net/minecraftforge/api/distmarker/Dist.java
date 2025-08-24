package net.minecraftforge.api.distmarker;

public enum Dist {
    CLIENT,
    DEDICATED_SERVER;

    private Dist() {
    }

    public boolean isDedicatedServer() {
        return !this.isClient();
    }

    public boolean isClient() {
        return this == CLIENT;
    }
}