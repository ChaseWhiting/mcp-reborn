package net.minecraft.entity.projectile;

import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.entity.monster.crimson_mosquito.CrimsonMosquitoEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMosquitoSpit extends ProjectileEntity {

    private static final DataParameter<Integer> BLOOD_TYPE = EntityDataManager.defineId(EntityMosquitoSpit.class, DataSerializers.INT);

    public EntityMosquitoSpit(EntityType<EntityMosquitoSpit> p_i50162_1_, World p_i50162_2_) {
        super(p_i50162_1_, p_i50162_2_);
    }

    public void setType(int t) {
        entityData.set(BLOOD_TYPE, t);
    }


    protected void defineSynchedData() {
        this.entityData.define(BLOOD_TYPE, 0);
    }

    public IPacket<?> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new SSpawnObjectPacket(this, entity == null ? 0 : entity.getId());
    }

    public EntityMosquitoSpit(World worldIn, CrimsonMosquitoEntity p_i47273_2_) {
        this(EntityType.MOSQUITO_SPIT, worldIn);
        this.setOwner(p_i47273_2_);
        this.setPos(p_i47273_2_.getX() - (double)(p_i47273_2_.getBbWidth() + 1.0F) * 0.35D * (double) MathHelper.sin(p_i47273_2_.yBodyRot * ((float)Math.PI / 180F)), p_i47273_2_.getEyeY() + (double)0.2F, p_i47273_2_.getZ() + (double)(p_i47273_2_.getBbWidth() + 1.0F) * 0.35D * (double)MathHelper.cos(p_i47273_2_.yBodyRot * ((float)Math.PI / 180F)));
    }

    public EntityMosquitoSpit(World worldIn, LivingEntity p_i47273_2_, boolean right) {
        this(EntityType.MOSQUITO_SPIT, worldIn);
        this.setOwner(p_i47273_2_);
        float rot = p_i47273_2_.yHeadRot + (right ? 60 : -60);
        this.setPos(p_i47273_2_.getX() - (double)(p_i47273_2_.getBbWidth()) * 0.5D * (double) MathHelper.sin(rot * ((float)Math.PI / 180F)), p_i47273_2_.getEyeY() - (double)0.2F, p_i47273_2_.getZ() + (double)(p_i47273_2_.getBbWidth()) * 0.5D * (double)MathHelper.cos(rot * ((float)Math.PI / 180F)));
    }

    @OnlyIn(Dist.CLIENT)
    public EntityMosquitoSpit(World worldIn, double x, double y, double z, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
        this(EntityType.MOSQUITO_SPIT, worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(p_i47274_8_, p_i47274_10_, p_i47274_12_);
    }

    public boolean isOnFire() {
        return this.getBloodType() == 4;
    }

    public void tick() {
        super.tick();
        Vector3d vector3d = this.getDeltaMovement();
        RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
        if (raytraceresult != null) {
            this.onHit(raytraceresult);
        }

        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        this.updateRotation();
        float f = 0.99F;
        float f1 = 0.06F;
        if (this.level.getBlockStates(this.getBoundingBox()).noneMatch(AbstractBlock.AbstractBlockState::isAir)) {
            this.remove();
        } else if (this.isInWaterOrBubble()) {
            this.remove();
        } else {
            this.setDeltaMovement(vector3d.scale((double)0.99F));
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double)-0.06F, 0.0D));
            }

            this.setPos(d0, d1, d2);
        }
    }

    public int getBloodType() {
        return entityData.get(BLOOD_TYPE);
    }

    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        Entity entity = this.getOwner();
        if (entity instanceof LivingEntity && !(result.getEntity() instanceof CrimsonMosquitoEntity)) {
            result.getEntity().hurt(DamageSource.indirectMobAttack(this, (LivingEntity)entity).setProjectile(), this.getBloodType() == CrimsonMosquitoEntity.BloodType.ENDER_BLOOD.id() || this.getBloodType() == CrimsonMosquitoEntity.BloodType.LAVA.id() ? 6F : 4F);

            if (this.getBloodType() == CrimsonMosquitoEntity.BloodType.LAVA.id()) {
                if (!(result.getEntity().fireImmune()) && !result.getEntity().isInWaterRainOrBubble()) {
                    result.getEntity().setSecondsOnFire((result.getEntity().getRemainingFireTicks() * 20) + MathHelper.randomBetweenInclusive(random, 7, 14));
                }
            }

            if (this.getBloodType() == CrimsonMosquitoEntity.BloodType.RESIN.id() && !(result.getEntity() instanceof CreakingEntity)) {
                ((LivingEntity) result.getEntity()).addEffect(new EffectInstance(Effects.ROOTED, 12 * 20, 0, false, true));
            }
        }

        if (result.getEntity() instanceof CrimsonMosquitoEntity) {
            CrimsonMosquitoEntity crimsonMosquitoEntity = (CrimsonMosquitoEntity) result.getEntity();
            if (crimsonMosquitoEntity.getBloodType() == this.getBloodType() && crimsonMosquitoEntity.isBaby()) {
                crimsonMosquitoEntity.heal(6.0F);
                crimsonMosquitoEntity.setAge(crimsonMosquitoEntity.getAge() + (150 * 20));
            }
            if (crimsonMosquitoEntity.getBloodType() != this.getBloodType()) {
                crimsonMosquitoEntity.setBloodLevel(0);
            }
            if (this.getBloodType() == CrimsonMosquitoEntity.BloodType.RED.id() &&
                    crimsonMosquitoEntity.getBloodType() == CrimsonMosquitoEntity.BloodType.BLUE.id()
                            || this.getBloodType() == CrimsonMosquitoEntity.BloodType.BLUE.id() &&
                    crimsonMosquitoEntity.getBloodType() == CrimsonMosquitoEntity.BloodType.RED.id()) {
                crimsonMosquitoEntity.setBloodType(CrimsonMosquitoEntity.BloodType.ENDER_BLOOD.id());
            } else {
                if (crimsonMosquitoEntity.getBloodType() != CrimsonMosquitoEntity.BloodType.ENDER_BLOOD.id()) {
                    if (this.getBloodType() != CrimsonMosquitoEntity.BloodType.ENDER_BLOOD.id()) {
                        crimsonMosquitoEntity.setBloodType(this.getBloodType());
                    }
                }
            }
            crimsonMosquitoEntity.setBloodLevel(crimsonMosquitoEntity.getBloodLevel() + 1);
        }

    }

    protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
        super.onHitBlock(p_230299_1_);
        if (!this.level.isClientSide) {
            this.remove();
        }

    }

    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("BloodType", this.entityData.get(BLOOD_TYPE));
    }

    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        entityData.set(BLOOD_TYPE, nbt.getInt("BloodType"));
    }

}