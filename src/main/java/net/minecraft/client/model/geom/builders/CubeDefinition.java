package net.minecraft.client.model.geom.builders;

import javax.annotation.Nullable;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.util.Direction;
import org.joml.Vector3f;

import java.util.Set;

public final class CubeDefinition {
    @Nullable
    private final String comment;
    private final Vector3f origin;
    private final Vector3f dimensions;
    private final CubeDeformation grow;
    private final boolean mirror;
    private final UVPair texCoord;
    private final UVPair texScale;
    private final Set<Direction> visibleFaces;

    protected CubeDefinition(@Nullable String string, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, CubeDeformation cubeDeformation, boolean bl, float f9, float f10) {
        this.comment = string;
        this.texCoord = new UVPair(f, f2);
        this.origin = new Vector3f(f3, f4, f5);
        this.dimensions = new Vector3f(f6, f7, f8);
        this.grow = cubeDeformation;
        this.mirror = bl;
        this.texScale = new UVPair(f9, f10);
        this.visibleFaces = CubeListBuilder.ALL_VISIBLE;
    }

    protected CubeDefinition(@Nullable String string, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, CubeDeformation cubeDeformation, boolean bl, float f9, float f10, Set<Direction> directions) {
        this.comment = string;
        this.texCoord = new UVPair(f, f2);
        this.origin = new Vector3f(f3, f4, f5);
        this.dimensions = new Vector3f(f6, f7, f8);
        this.grow = cubeDeformation;
        this.mirror = bl;
        this.texScale = new UVPair(f9, f10);
        this.visibleFaces = directions;
    }

    public ModelPart.Cube bake(int n, int n2) {
        return new ModelPart.Cube((int)this.texCoord.u(), (int)this.texCoord.v(), this.origin.x(), this.origin.y(), this.origin.z(), this.dimensions.x(), this.dimensions.y(), this.dimensions.z(), this.grow.growX, this.grow.growY, this.grow.growZ, this.mirror, (float)n * this.texScale.u(), (float)n2 * this.texScale.v(), visibleFaces);
    }
}

