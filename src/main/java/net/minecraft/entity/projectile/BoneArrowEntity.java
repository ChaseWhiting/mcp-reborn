package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BoneArrowEntity extends AbstractArrowEntity {

    private double baseDamage = 3.5D;

    public BoneArrowEntity(EntityType<? extends BoneArrowEntity> arrow, World world) {
        super(arrow, world);
    }

    public BoneArrowEntity(World world, double x, double y, double z) {
        super(EntityType.BONE_ARROW, x, y, z, world);
    }

    public BoneArrowEntity(World world, LivingEntity entity) {
        super(EntityType.BONE_ARROW, entity, world);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {

        } else if (this.inGround && this.inGroundTime != 0 && this.inGroundTime >= 600) {
            this.level.broadcastEntityEvent(this, (byte)0);
        }

    }

    private void makeParticle(int particle) {

    }



    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
    }

    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
    }

    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
    }

    protected ItemStack getPickupItem() {
        return new ItemStack(Items.BONE_ARROW);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte b) {
        super.handleEntityEvent(b);
    }
}