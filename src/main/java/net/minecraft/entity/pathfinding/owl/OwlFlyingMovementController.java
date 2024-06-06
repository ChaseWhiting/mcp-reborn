package net.minecraft.entity.pathfinding.owl;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.MathHelper;

public class OwlFlyingMovementController extends MovementController {
    private final int maxTurn;
    private final boolean hoversInPlace;

    public OwlFlyingMovementController(MobEntity mob, int maxTurn, boolean hoversInPlace) {
        super(mob);
        this.maxTurn = maxTurn;
        this.hoversInPlace = hoversInPlace;
    }

    @Override
    public void tick() {
        if (this.operation == MovementController.Action.MOVE_TO) {
            this.operation = MovementController.Action.WAIT;
            this.mob.setNoGravity(true);
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedY - this.mob.getY();
            double d2 = this.wantedZ - this.mob.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d3 < 2.5000003E-7F) {
                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
                return;
            }

            float targetYaw = (float) (MathHelper.atan2(d2, d0) * (180F / (float) Math.PI)) - 90.0F;
            this.mob.yRot = this.rotlerp(this.mob.yRot, targetYaw, this.maxTurn);
            float speed;
            if (this.mob.isOnGround()) {
                speed = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            } else {
                speed = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
            }

            this.mob.setSpeed(speed);
            double horizontalDistance = MathHelper.sqrt(d0 * d0 + d2 * d2);
            float targetPitch = (float) (-(MathHelper.atan2(d1, horizontalDistance) * (180F / (float) Math.PI)));
            this.mob.xRot = this.rotlerp(this.mob.xRot, targetPitch, (float) this.maxTurn);
            this.mob.setYya(d1 > 0.0D ? speed : -speed);
        } else {
            if (!this.hoversInPlace) {
                this.mob.setNoGravity(false);
            }

            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
        }
    }
}
