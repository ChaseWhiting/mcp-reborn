package net.minecraft.entity.gumbeeper;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class GumballEntity extends ThrowableEntity {
    private static final DataParameter<Integer> MAXIMUM_BOUNCES = EntityDataManager.defineId(GumballEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> BOUNCES = EntityDataManager.defineId(GumballEntity.class, DataSerializers.INT);
    private static final DataParameter<Float> DAMAGE = EntityDataManager.defineId(GumballEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> COLOR = EntityDataManager.defineId(GumballEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> TARGETS_ON_BOUNCE = EntityDataManager.defineId(GumballEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SPLITS_ON_HIT = EntityDataManager.defineId(GumballEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> EXPLOSIVE = EntityDataManager.defineId(GumballEntity.class, DataSerializers.BOOLEAN);

    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    private float explodeProgress;
    private float prevExplodeProgress;

    private List<Integer> hitEntityIds = new ArrayList<>();

    private int bounceSoundCooldown = 0;

    public GumballEntity(EntityType entityType, World level) {
        super(entityType, level);
        this.setColor(level.random.nextInt(12));
    }

    public GumballEntity(World level, LivingEntity shooter) {
        this(EntityType.GUMBALL, level);
        this.setOwner(shooter);
    }

    public GumballEntity(World p_i1781_1_, double p_i1781_2_, double p_i1781_4_, double p_i1781_6_) {
        super(EntityType.GUMBALL, p_i1781_2_, p_i1781_4_, p_i1781_6_, p_i1781_1_);
        this.setColor(level.random.nextInt(12));
    }
//
//    public GumballEntity(PlayMessages.SpawnEntity spawnEntity, World level) {
//        this(ACEntityRegistry.GUMBALL.get(), level);
//    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(MAXIMUM_BOUNCES, 5);
        this.entityData.define(BOUNCES, 0);
        this.entityData.define(COLOR, 0);
        this.entityData.define(DAMAGE, 2F);
        this.entityData.define(TARGETS_ON_BOUNCE, false);
        this.entityData.define(SPLITS_ON_HIT, false);
        this.entityData.define(EXPLOSIVE, false);
    }

//    @Override
//    public Packet<ClientGamePacketListener> getAddEntityPacket() {
//        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
//    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yr;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void tick() {
        super.tick();
        prevExplodeProgress = explodeProgress;
        if (this.level().isClientSide) {
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.yRot = (MathHelper.wrapDegrees((float) this.lyr));
                this.xRot = (this.xRot + (float) (this.lxr - (double) this.xRot) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
            } else {
                this.reapplyPosition();
            }
        } else {
            this.reapplyPosition();
            this.setRot(this.yRot, this.xRot);
        }
        if(this.isExplosive() && this.getBounces() >= this.getMaximumBounces()){
            if(explodeProgress > 20.0F){
                if(!level().isClientSide){
                    this.level().explode(this.getOwner(), this.getX(), this.getY() + 0.5F, this.getZ(), (float)2.0, false, Explosion.Mode.NONE);
                    this.discard();
                }
            }else{
                explodeProgress++;
            }
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.4F, this.getZ(), 0, 0.1F, 0);
        }
        if(bounceSoundCooldown > 0){
            bounceSoundCooldown--;
        }
    }

    public void bounceFromDirection(Direction hitDirection) {
        boolean flag = false;
        if(this.getBounces() > this.getMaximumBounces() - 1 && this.isExplosive()) {
            this.setDeltaMovement(Vector3d.ZERO);
            this.setBounces(this.getMaximumBounces());
            return;
        }else if(targetsOnBounce()){
            Entity shooter = this.getOwner();
            Vector3d position = this.getEyePosition(1.0F);
            Entity nearestBounceTarget = null;
            for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(position.add(-10, -10, -10), position.add(10, 10, 10)))) {
                if((shooter == null || !entity.is(shooter) && !entity.isAlliedTo(shooter)) && !hitEntityIds.contains(entity.getId())){
                    if(nearestBounceTarget == null || entity.distanceTo(this) < nearestBounceTarget.distanceTo(this)){
                        nearestBounceTarget = entity;
                    }
                }
            }
            if(nearestBounceTarget != null){
                flag = true;
                if(!level().isClientSide){
                    this.setBounces(this.getBounces() + 1);
                }
                if(this.getBounces() > this.getMaximumBounces()){
                    this.discard();
                }else{
                    Vector3d vec3 = nearestBounceTarget.getEyePosition(1.0F).subtract(this.position()).normalize().scale(0.8F);
                    this.setDeltaMovement(vec3.x, vec3.y, vec3.z);
                }
            }
        }
        if(bounceSoundCooldown == 0){
            bounceSoundCooldown = 5;
            this.makeSound(SoundEvents.GUMBALL_BOUNCE);
        }
        if(!flag){
            Vector3d deltaMovement = this.getDeltaMovement();
            double x = deltaMovement.x();
            double y = deltaMovement.y();
            double z = deltaMovement.z();
            switch (hitDirection.getAxis()) {
                case X:
                    x = -x * 0.8F;
                    break;
                case Y:
                    y = -y * 0.5F;
                    break;
                case Z:
                    z = -z * 0.8F;
                    break;
            }
            if(!level().isClientSide){
                this.setBounces(this.getBounces() + 1);
            }
            if(this.getBounces() > this.getMaximumBounces()){
                this.discard();
            }else{
                this.setDeltaMovement(x, y, z);
            }
        }
    }

    public int getMaximumBounces() {
        return this.entityData.get(MAXIMUM_BOUNCES);
    }

    public void setMaximumBounces(int bounces) {
        this.entityData.set(MAXIMUM_BOUNCES, bounces);
    }

    public int getBounces() {
        return this.entityData.get(BOUNCES);
    }

    public void setBounces(int bounces) {
        this.entityData.set(BOUNCES, bounces);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }

    public boolean targetsOnBounce(){
        return this.entityData.get(TARGETS_ON_BOUNCE);
    }

    public void setTargetsOnBounce(boolean targets){
        this.entityData.set(TARGETS_ON_BOUNCE, targets);
    }

    public boolean splitsOnHit(){
        return this.entityData.get(SPLITS_ON_HIT);
    }

    public void setSplitsOnHit(boolean splits){
        this.entityData.set(SPLITS_ON_HIT, splits);
    }

    public boolean isExplosive(){
        return this.entityData.get(EXPLOSIVE);
    }

    public void setExplosive(boolean explosive){
        this.entityData.set(EXPLOSIVE, explosive);
    }

    protected float getGravity() {
        return 0.08F;
    }

    protected void onHit(RayTraceResult hitResult) {
        super.onHit(hitResult);
        if(hitResult instanceof BlockRayTraceResult){
            BlockRayTraceResult blockHitResult = (BlockRayTraceResult)hitResult;
            BlockState state = this.level().getBlockState(blockHitResult.getBlockPos());
            if(!state.getCollisionShape(this.level(), blockHitResult.getBlockPos()).isEmpty()){
                bounceFromDirection(blockHitResult.getDirection());
            }
        }else if(hitResult instanceof EntityRayTraceResult){
            EntityRayTraceResult entityHitResult = (EntityRayTraceResult)hitResult;
            if (!ownedBy(entityHitResult.getEntity()) && !(entityHitResult.getEntity() instanceof GumballEntity)) {
                this.hitEntityIds.add(entityHitResult.getEntity().getId());
                Vector3d vec3 = entityHitResult.getEntity().getEyePosition(1.0F).subtract(this.getEyePosition(1.0F));
                float f = -((float) MathHelper.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
                if(!splitsOnHit()){
                    bounceFromDirection(Direction.fromYRot(f));
                }
            }
        }
    }

    public boolean ownedBy(Entity entity) {
        return this.getOwner() == entity;
    }

    protected void onHitEntity(EntityRayTraceResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        float damage = this.getDamage();
//        if(owner instanceof GumbeeperEntity gumbeeper && gumbeeper.getPossessedByLicowitchId() != -1){
//
//            LicowitchEntity witch = gumbeeper.getPossessingLicowitch(level());
//            if(witch != null && entity instanceof LivingEntity living && witch.isFriendlyFire(living)){
//                return;
//            }
//        }
        DamageSource damageSource = DamageSource.causeGumballDamage(owner == null ? null : owner);
        if ((owner == null || !entity.is(owner) && !entity.isAlliedTo(owner) && !owner.isAlliedTo(entity))) {
            this.makeSound(SoundEvents.GUMBALL_HIT);
            entity.hurt(damageSource, damage);
        }
        if(splitsOnHit()){
            for(int i = 0; i < 3; i++){
                GumballEntity gumballEntity = EntityType.GUMBALL.create(level());
                Vector3d vec3 = this.getDeltaMovement().normalize();
                float f = -((float) MathHelper.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
                Vector3d vec31 = new Vector3d(0, 0, this.isExplosive() ? 0.7F : 1.5F).yRot((float) -Math.toRadians(f + 30.0F - 30.0F * i));
                gumballEntity.setPos(entity.getEyePosition(1.0F).add(vec31).asBlockPos());
                gumballEntity.setDeltaMovement(vec31);
                gumballEntity.setSplitsOnHit(false);
                gumballEntity.setDamage(this.getDamage());
                gumballEntity.setTargetsOnBounce(this.targetsOnBounce());
                gumballEntity.setExplosive(this.isExplosive());
                gumballEntity.setMaximumBounces(this.getMaximumBounces());
                level().addFreshEntity(gumballEntity);
            }
            this.discard();
        }
    }

    public void addAdditionalSaveData(CompoundNBT compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Color", this.getColor());
    }

    public void readAdditionalSaveData(CompoundNBT compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setColor(compoundTag.getInt("Color"));
    }

    public float getExplodeProgress(float partialTicks) {
        return (prevExplodeProgress + (explodeProgress - prevExplodeProgress) * partialTicks) / 20F;
    }
}
