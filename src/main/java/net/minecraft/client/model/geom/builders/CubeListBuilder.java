package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.util.Direction;

public class CubeListBuilder {
    private final List<CubeDefinition> cubes = Lists.newArrayList();
    public static final Set<Direction> ALL_VISIBLE = EnumSet.allOf(Direction.class);
    private int xTexOffs;
    private int yTexOffs;
    private boolean mirror;

    public CubeListBuilder texOffs(int n, int n2) {
        this.xTexOffs = n;
        this.yTexOffs = n2;
        return this;
    }

    public CubeListBuilder mirror() {
        return this.mirror(true);
    }

    public CubeListBuilder mirror(boolean bl) {
        this.mirror = bl;
        return this;
    }

    public CubeListBuilder addBox(String string, float f, float f2, float f3, int n, int n2, int n3, CubeDeformation cubeDeformation, int n4, int n5) {
        this.texOffs(n4, n5);
        this.cubes.add(new CubeDefinition(string, this.xTexOffs, this.yTexOffs, f, f2, f3, n, n2, n3, cubeDeformation, this.mirror, 1.0f, 1.0f));
        return this;
    }

    public CubeListBuilder addBox(String string, float f, float f2, float f3, int n, int n2, int n3, int n4, int n5) {
        this.texOffs(n4, n5);
        this.cubes.add(new CubeDefinition(string, this.xTexOffs, this.yTexOffs, f, f2, f3, n, n2, n3, CubeDeformation.NONE, this.mirror, 1.0f, 1.0f));
        return this;
    }

    public CubeListBuilder addBox(float f, float f2, float f3, float f4, float f5, float f6) {
        this.cubes.add(new CubeDefinition(null, this.xTexOffs, this.yTexOffs, f, f2, f3, f4, f5, f6, CubeDeformation.NONE, this.mirror, 1.0f, 1.0f));
        return this;
    }

    public CubeListBuilder addBox(float f, float f2, float f3, float f4, float f5, float f6, Set<Direction> set) {
        this.cubes.add(new CubeDefinition(null, this.xTexOffs, this.yTexOffs, f, f2, f3, f4, f5, f6, CubeDeformation.NONE, this.mirror, 1.0f, 1.0f, set));
        return this;
    }

    public CubeListBuilder addBox(String string, float f, float f2, float f3, float f4, float f5, float f6) {
        this.cubes.add(new CubeDefinition(string, this.xTexOffs, this.yTexOffs, f, f2, f3, f4, f5, f6, CubeDeformation.NONE, this.mirror, 1.0f, 1.0f));
        return this;
    }

    public CubeListBuilder addBox(String string, float f, float f2, float f3, float f4, float f5, float f6, CubeDeformation cubeDeformation) {
        this.cubes.add(new CubeDefinition(string, this.xTexOffs, this.yTexOffs, f, f2, f3, f4, f5, f6, cubeDeformation, this.mirror, 1.0f, 1.0f));
        return this;
    }

    public CubeListBuilder addBox(float f, float f2, float f3, float f4, float f5, float f6, boolean bl) {
        this.cubes.add(new CubeDefinition(null, this.xTexOffs, this.yTexOffs, f, f2, f3, f4, f5, f6, CubeDeformation.NONE, bl, 1.0f, 1.0f));
        return this;
    }

    public CubeListBuilder addBox(float f, float f2, float f3, float f4, float f5, float f6, CubeDeformation cubeDeformation, float f7, float f8) {
        this.cubes.add(new CubeDefinition(null, this.xTexOffs, this.yTexOffs, f, f2, f3, f4, f5, f6, cubeDeformation, this.mirror, f7, f8));
        return this;
    }

    public CubeListBuilder addBox(float f, float f2, float f3, float f4, float f5, float f6, CubeDeformation cubeDeformation) {
        this.cubes.add(new CubeDefinition(null, this.xTexOffs, this.yTexOffs, f, f2, f3, f4, f5, f6, cubeDeformation, this.mirror, 1.0f, 1.0f));
        return this;
    }

    public List<CubeDefinition> getCubes() {
        return ImmutableList.copyOf(this.cubes);
    }

    public static CubeListBuilder create() {
        return new CubeListBuilder();
    }
}

