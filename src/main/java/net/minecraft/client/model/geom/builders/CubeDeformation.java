package net.minecraft.client.model.geom.builders;

public class CubeDeformation {
    public static final CubeDeformation NONE = new CubeDeformation(0.0f);
    final float growX;
    final float growY;
    final float growZ;

    public CubeDeformation(float f, float f2, float f3) {
        this.growX = f;
        this.growY = f2;
        this.growZ = f3;
    }

    public CubeDeformation(float f) {
        this(f, f, f);
    }

    public CubeDeformation extend(float f) {
        return new CubeDeformation(this.growX + f, this.growY + f, this.growZ + f);
    }

    public CubeDeformation extend(float f, float f2, float f3) {
        return new CubeDeformation(this.growX + f, this.growY + f2, this.growZ + f3);
    }
}

