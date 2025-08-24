package net.minecraft.client.renderer.entity.state.hitbox;

public record HitboxRenderState(double x0, double y0, double z0, double x1, double y1, double z1, float offsetX, float offsetY, float offsetZ, float red, float green, float blue) {
    public HitboxRenderState(double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3) {
        this(d, d2, d3, d4, d5, d6, 0.0f, 0.0f, 0.0f, f, f2, f3);
    }
}

