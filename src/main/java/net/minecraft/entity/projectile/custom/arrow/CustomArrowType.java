package net.minecraft.entity.projectile.custom.arrow;

public class CustomArrowType {

    private final int type;
    private final String name;
    private final double baseDamage;

    public CustomArrowType(String name, int type, double baseDamage){
        this.type = type;
        this.name = name;
        this.baseDamage = baseDamage;
    }

    public int getType(){
        return this.type;
    }

    public String getName(){
        return this.name;
    }

    public double getBaseDamage() {
        return this.baseDamage;
    }

    public static final CustomArrowType NULL = new CustomArrowType("null", -1, 0.0D);
    public static final CustomArrowType FROZEN = new CustomArrowType("frozen", 0, 2.5D);
    public static final CustomArrowType BURNING = new CustomArrowType("burning", 1,  4.0D);
    public static final CustomArrowType POISON = new CustomArrowType("poison", 2, 2.0D);
    public static final CustomArrowType TELEPORTATION = new CustomArrowType("teleportation", 3, 2.5D);
    public static final CustomArrowType HEALING = new CustomArrowType("healing", 4, 0D);
    public static final CustomArrowType FIREWORK = new CustomArrowType("firework", 5, 4.5D);
    public static final CustomArrowType GILDED = new CustomArrowType("gilded", 6, 5.5D);
    public static final CustomArrowType[] ALL_TYPES = new CustomArrowType[]{FROZEN, BURNING, POISON, TELEPORTATION, HEALING, FIREWORK, GILDED};

    public static CustomArrowType getCustomArrowTypeByName(String name){
        for(CustomArrowType type : ALL_TYPES){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return NULL;
    }

    public static CustomArrowType getCustomArrowTypeByType(int type){
        for(CustomArrowType customArrowType : ALL_TYPES){
            if(customArrowType.getType() == type){
                return customArrowType;
            }
        }
        return NULL;
    }

}