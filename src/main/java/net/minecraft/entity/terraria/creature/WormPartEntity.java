package net.minecraft.entity.terraria.creature;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.Pose;
import net.minecraft.entity.EntitySize;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

public class WormPartEntity extends Entity {
    public final WormEntity parentWorm;
    public final String partName;
    private final EntitySize size;

    public WormPartEntity(WormEntity parent, String name, float width, float height) {
        super(parent.getType(), parent.level);  // Replace EntityType.MOB with an appropriate EntityType
        this.parentWorm = parent;
        this.partName = name;
        this.size = EntitySize.scalable(width, height);
        this.refreshDimensions();
    }

    public void movePart(Vector3d previousPosition) {
        // Move this segment towards the previous part’s position with some lag to create a trailing effect
        Vector3d direction = previousPosition.subtract(this.position()).normalize();
        this.setPos(this.getX() + direction.x * 0.5, this.getY() + direction.y * 0.5, this.getZ() + direction.z * 0.5);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {

    }

    public WormEntity getParentWorm() {
        return parentWorm;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntitySize getDimensions(Pose pose) {
        return this.size;
    }

    public boolean is(Entity p_70028_1_) {
        return this == p_70028_1_ || this.parentWorm == p_70028_1_;
    }
}
