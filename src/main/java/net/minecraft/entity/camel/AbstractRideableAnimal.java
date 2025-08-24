package net.minecraft.entity.camel;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class AbstractRideableAnimal extends Animal {

    public AbstractRideableAnimal(EntityType<AbstractRideableAnimal> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof LivingEntity;
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    public abstract int getMaxPassengers(Entity passenger);

    @Override
    public boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < this.getMaxPassengers(passenger);
    }

    @Override
    public void travel(@NotNull Vector3d motion) {
        if (this.isAlive() && this.isVehicle()) {
            LivingEntity rider = (LivingEntity) this.getControllingPassenger();
            if (rider != null) {
                this.yRot = rider.yRot;
                this.xRot = rider.xRot * 0.5F;
                this.yBodyRot = this.yRot;
                this.yHeadRot = this.yRot;

                float strafe = rider.xxa * 0.5F;
                float forward = rider.zza;

                if (forward <= 0.0F) forward *= 0.25F;

                this.flyingSpeed = this.getSpeed() * 0.1F;
                this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vector3d(strafe, motion.y, forward));
            }
        } else {
            super.travel(motion);
        }
    }

    protected void doPlayerRide(PlayerEntity player) {
        if (!this.level.isClientSide) {
            player.yRot = this.yRot;
            player.xRot = this.xRot;
            player.startRiding(this);
        }

    }

    protected abstract boolean canRide(LivingEntity tryingToRideMob);

    protected abstract boolean canBeRidden();

    protected boolean isImmobile() {
        return super.isImmobile() && this.isVehicle() && this.canBeRidden();
    }

}
