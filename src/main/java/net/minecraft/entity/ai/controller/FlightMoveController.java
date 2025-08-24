package net.minecraft.entity.ai.controller;

import net.minecraft.entity.Mob;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class FlightMoveController extends MovementController {
    private final Mob parentEntity;
    private final float speedGeneral;
    private final boolean shouldLookAtTarget;
    private final boolean needsYSupport;


    public FlightMoveController(Mob bird, float speedGeneral, boolean shouldLookAtTarget, boolean needsYSupport) {
        super(bird);
        this.parentEntity = bird;
        this.shouldLookAtTarget = shouldLookAtTarget;
        this.speedGeneral = speedGeneral;
        this.needsYSupport = needsYSupport;
    }

    public FlightMoveController(Mob bird, float speedGeneral, boolean shouldLookAtTarget) {
        this(bird, speedGeneral, shouldLookAtTarget, false);
    }

    public FlightMoveController(Mob bird, float speedGeneral) {
        this(bird, speedGeneral, true);
    }

    public void tick() {
        if (this.operation == Action.MOVE_TO) {
            Vector3d vector3d = new Vector3d(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
            double d0 = vector3d.length();
            if (d0 < parentEntity.getBoundingBox().getSize()) {
                this.operation = Action.WAIT;
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
            } else {
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * speedGeneral * 0.05D / d0)));
                if (needsYSupport) {
                    double d1 = this.wantedY - parentEntity.getY();
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(0.0D, (double) parentEntity.getSpeed() * speedGeneral * MathHelper.clamp(d1, -1, 1) * 0.6F, 0.0D));
                }
                if (parentEntity.getTarget() == null || !shouldLookAtTarget) {
                    Vector3d vector3d1 = parentEntity.getDeltaMovement();
                    parentEntity.yRot = (float) (-((float) MathHelper.atan2(vector3d1.x, vector3d1.z)) * MathHelper.RAD_TO_DEG);
                    parentEntity.yBodyRot = parentEntity.yRot;
                } else {
                    double d2 = parentEntity.getTarget().getX() - parentEntity.getX();
                    double d1 = parentEntity.getTarget().getZ() - parentEntity.getZ();
                    parentEntity.yRot = (float) (-((float) MathHelper.atan2(d2, d1)) * MathHelper.RAD_TO_DEG);
                    parentEntity.yBodyRot = parentEntity.yRot;
                }
            }

        } else if (this.operation == Action.STRAFE) {
            this.operation = Action.WAIT;
        }
    }
    private boolean canReach(Vector3d p_220673_1_, int p_220673_2_) {
        AxisAlignedBB axisalignedbb = this.parentEntity.getBoundingBox();

        for (int i = 1; i < p_220673_2_; ++i) {
            axisalignedbb = axisalignedbb.move(p_220673_1_);
            if (!this.parentEntity.level().noCollision(this.parentEntity, axisalignedbb)) {
                return false;
            }
        }

        return true;
    }
}