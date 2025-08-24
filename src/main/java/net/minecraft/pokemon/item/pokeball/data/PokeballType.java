package net.minecraft.pokemon.item.pokeball.data;

public enum PokeballType {
    POKEBALL(1d, "pokeball"),
    GREAT(1.5d, "great"),
    ULTRA(2d, "ultra"),
    MASTER(255d, "master");


    public double getCatchRate() {
        return catchRate;
    }

    public String getName() {
        return this.name;
    }

    private final String name;

    public static PokeballType from(String name) {
        for (PokeballType type : values()) {
            if (type.name.equals(name)) return type;
        }

        return POKEBALL;
    }

    private final double catchRate;

    private PokeballType(double catchRate, String name) {
        this.catchRate = catchRate;
        this.name = name;
    }
}
