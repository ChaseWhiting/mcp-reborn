package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix4f;

public class ColorVertexBuilder implements IVertexBuilder {
    private final IVertexBuilder parent;
    private final float r, g, b, a;

    public ColorVertexBuilder(IVertexBuilder parent, float r, float g, float b, float a) {
        this.parent = parent;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public IVertexBuilder vertex(Matrix4f matrix4f, float x, float y, float z) {
        parent.vertex(matrix4f, x, y, z);
        return this;
    }

    @Override
    public IVertexBuilder vertex(double p_225582_1_, double p_225582_3_, double p_225582_5_) {
        parent.vertex(p_225582_1_, p_225582_3_, p_225582_5_);
        return this;
    }

    @Override
    public IVertexBuilder color(int red, int green, int blue, int alpha) {
        parent.color(
            (int)(r * 255.0F),
            (int)(g * 255.0F),
            (int)(b * 255.0F),
            (int)(a * 255.0F)
        );
        return this;
    }

    @Override
    public IVertexBuilder uv(float u, float v) {
        parent.uv(u, v);return this;
    }

    @Override
    public IVertexBuilder overlayCoords(int u, int v) {
        parent.overlayCoords(u, v);return this;
    }

    @Override
    public IVertexBuilder uv2(int u, int v) {
        parent.uv2(u, v);return this;
    }

    @Override
    public IVertexBuilder normal(float normalX, float normalY, float normalZ) {
        parent.normal(normalX, normalY, normalZ);
        return this;
    }

    @Override
    public void endVertex() {
        parent.endVertex();
    }


}
