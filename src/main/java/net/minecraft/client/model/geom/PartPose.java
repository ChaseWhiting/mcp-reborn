package net.minecraft.client.model.geom;

public record PartPose(float x, float y, float z, float xRot, float yRot, float zRot, float xScale, float yScale, float zScale) {

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public float z() {
        return z;
    }

    @Override
    public float xRot() {
        return xRot;
    }

    @Override
    public float yRot() {
        return yRot;
    }

    @Override
    public float zRot() {
        return zRot;
    }

    @Override
    public float xScale() {
        return xScale;
    }

    @Override
    public float yScale() {
        return yScale;
    }

    @Override
    public float zScale() {
        return zScale;
    }



    public static final PartPose ZERO = PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);

    public static PartPose offset(float f, float f2, float f3) {
        return PartPose.offsetAndRotation(f, f2, f3, 0.0f, 0.0f, 0.0f);
    }

    public static PartPose rotation(float f, float f2, float f3) {
        return PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, f, f2, f3);
    }

    public static PartPose offsetAndRotation(float f, float f2, float f3, float f4, float f5, float f6) {
        return new PartPose(f, f2, f3, f4, f5, f6, 1.0f, 1.0f, 1.0f);
    }

    public PartPose translated(float f, float f2, float f3) {
        return new PartPose(this.x + f, this.y + f2, this.z + f3, this.xRot, this.yRot, this.zRot, this.xScale, this.yScale, this.zScale);
    }

    public PartPose withScale(float f) {
        return new PartPose(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot, f, f, f);
    }

    public PartPose scaled(float f) {
        if (f == 1.0f) {
            return this;
        }
        return this.scaled(f, f, f);
    }

    public PartPose scaled(float f, float f2, float f3) {
        return new PartPose(this.x * f, this.y * f2, this.z * f3, this.xRot, this.yRot, this.zRot, this.xScale * f, this.yScale * f2, this.zScale * f3);
    }
}


