package net.minecraft.entity.ai.controller;


import net.minecraft.entity.Mob;
import net.minecraft.util.math.MathHelper;

public class SmoothSwimmingLookControl
extends LookController {
    private final int maxYRotFromCenter;
    private static final int HEAD_TILT_X = 10;
    private static final int HEAD_TILT_Y = 20;

    public SmoothSwimmingLookControl(Mob mob, int n) {
        super(mob);
        this.maxYRotFromCenter = n;
    }

    @Override
    public void tick() {
        if (this.hasWanted) {
            this.hasWanted = false;
            float f = this.getYRotD();

            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, f + 20.0f, this.yMaxRotSpeed);

            float f4 = this.getXRotD();
            this.mob.xRot = (this.rotateTowards(this.mob.xRot, f4 + 10.0f, this.xMaxRotAngle));
        } else {
            if (this.mob.getNavigation().isDone()) {
                this.mob.xRot = (this.rotateTowards(this.mob.xRot, 0.0f, 5.0f));
            }
            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, this.yMaxRotSpeed);
        }
        float f2 = MathHelper.wrapDegrees(this.mob.yHeadRot - this.mob.yBodyRot);
        if (f2 < (float)(-this.maxYRotFromCenter)) {
            this.mob.yBodyRot -= 4.0f;
        } else if (f2 > (float)this.maxYRotFromCenter) {
            this.mob.yBodyRot += 4.0f;
        }
    }
}

