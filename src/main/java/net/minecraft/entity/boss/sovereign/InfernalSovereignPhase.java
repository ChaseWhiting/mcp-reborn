package net.minecraft.entity.boss.sovereign;

public enum InfernalSovereignPhase {
    HELLFIRE_THROW("hellfire_throw", 1),
    DODGE_FLYING("dodge_flying", 2),
    FIRE_CIRCLES_AND_FIREBALL("fire_circles",3),
    WITHER_SKELETON_BARRAGE("wither_skeletons", 4);


    private final String name;
    private final Integer id;
    InfernalSovereignPhase(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return this.id;
    }

}
