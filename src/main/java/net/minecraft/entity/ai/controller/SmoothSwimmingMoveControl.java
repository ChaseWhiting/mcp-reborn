package net.minecraft.entity.ai.controller;


import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.math.MathHelper;

public class SmoothSwimmingMoveControl
extends MovementController {
    private final int maxTurnX;
    private final int maxTurnY;
    private final float inWaterSpeedModifier;
    private final float outsideWaterSpeedModifier;
    private final boolean applyGravity;

    public SmoothSwimmingMoveControl(Mob mob, int n, int n2, float f, float f2, boolean bl) {
        super(mob);
        this.maxTurnX = n;
        this.maxTurnY = n2;
        this.inWaterSpeedModifier = f;
        this.outsideWaterSpeedModifier = f2;
        this.applyGravity = bl;
    }

    @Override
    public void tick() {
        double d;
        double d2;
        if (this.applyGravity && this.mob.isInWater()) {
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, 0.005, 0.0));
        }
        if (this.operation != MovementController.Action.MOVE_TO || this.mob.getNavigation().isDone()) {
            this.mob.setSpeed(0.0f);
            this.mob.setXxa(0.0f);
            this.mob.setYya(0.0f);
            this.mob.setZza(0.0f);
            return;
        }
        double d3 = this.wantedX - this.mob.getX();
        double d4 = d3 * d3 + (d2 = this.wantedY - this.mob.getY()) * d2 + (d = this.wantedZ - this.mob.getZ()) * d;
        if (d4 < 2.500000277905201E-7) {
            this.mob.setZza(0.0f);
            return;
        }
        float f = (float)(MathHelper.atan2(d, d3) * 57.2957763671875) - 90.0f;
        this.mob.yRot = (this.rotlerp(this.mob.yRot, f, this.maxTurnY));
        this.mob.yBodyRot = this.mob.yRot;
        this.mob.yHeadRot = this.mob.yRot;
        float f2 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
        if (this.mob.isInWater()) {
            float f3;
            this.mob.setSpeed(f2 * this.inWaterSpeedModifier);
            double d5 = Math.sqrt(d3 * d3 + d * d);
            if (Math.abs(d2) > (double)1.0E-5f || Math.abs(d5) > (double)1.0E-5f) {
                f3 = -((float)(MathHelper.atan2(d2, d5) * 57.2957763671875));
                f3 = MathHelper.clamp(MathHelper.wrapDegrees(f3), (float)(-this.maxTurnX), (float)this.maxTurnX);
                this.mob.xRot = (this.rotlerp(this.mob.xRot, f3, 5.0f));
            }
            f3 = MathHelper.cos(this.mob.xRot * ((float)Math.PI / 180));
            float f4 = MathHelper.sin(this.mob.xRot * ((float)Math.PI / 180));
            this.mob.zza = f3 * f2;
            this.mob.yya = -f4 * f2;
        } else {
            this.mob.setSpeed(f2 * this.outsideWaterSpeedModifier);
        }
    }
}

