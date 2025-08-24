package net.minecraft.entity.gumbeeper;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigatorNoSpin;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.EnumSet;
import java.util.Random;

public class GumbeeperEntity extends Monster implements IChargeableMob {

    private float explodeProgress;
    private float prevExplodeProgress;
    private float prevDialRot;
    private float dialRot;
    private float shootProgress;
    private float prevShootProgress;
    private static final int DEFAULT_GUMBALLS = 6;
    private static final float MAX_DIAL_ROT = 450;
    private int catScareTime = 0;
    private int postShootTime = 0;
    private boolean hasExploded;
    private static final DataParameter<Boolean> EXPLODING = EntityDataManager.defineId(GumbeeperEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SHOOTING = EntityDataManager.defineId(GumbeeperEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> ATTACK_CHARGE = EntityDataManager.defineId(GumbeeperEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> GUMBALLS_LEFT = EntityDataManager.defineId(GumbeeperEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> CUSTOM_GUMBALL_AMOUNT = EntityDataManager.defineId(GumbeeperEntity.class, DataSerializers.INT);

    private static final DataParameter<Boolean> CHARGED = EntityDataManager.defineId(GumbeeperEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> POSSESSOR_LICOWITCH_ID = EntityDataManager.defineId(GumbeeperEntity.class, DataSerializers.INT);

    public GumbeeperEntity(EntityType<? extends Monster> entityType, World level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, CatEntity.class, 10.0F, 1.0D, 1.2D) {
            public void tick() {
                super.tick();
                GumbeeperEntity.this.catScareTime = 20;
            }
        });
        this.goalSelector.addGoal(2, new GumbeeperEntity.AttackGoal());
        this.goalSelector.addGoal(3, new RandomWalkingGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 15.0F));
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, HuskEntity.class, true, false));
    }

    protected PathNavigator createNavigation(World level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.ARMOR, 4.0D).add(Attributes.ATTACK_DAMAGE, 4.0D).add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public static boolean checkGumbeeperSpawnRules(EntityType<? extends Monster> entityType, IServerWorld levelAccessor, SpawnReason mobSpawnType, BlockPos blockPos, Random randomSource) {
        return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource) && randomSource.nextInt(10) == 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EXPLODING, false);
        this.entityData.define(ATTACK_CHARGE, 0.0F);
        this.entityData.define(GUMBALLS_LEFT, DEFAULT_GUMBALLS);
        this.entityData.define(CUSTOM_GUMBALL_AMOUNT, 0);
        this.entityData.define(SHOOTING, false);
        this.entityData.define(CHARGED, false);
        this.entityData.define(POSSESSOR_LICOWITCH_ID, -1);
    }

    @Override
    public void tick(){
        super.tick();
        prevExplodeProgress = explodeProgress;
        prevDialRot = dialRot;
        prevShootProgress = shootProgress;
        float attackCharge = getAttackCharge();
        if (this.isExploding() && explodeProgress < 20F) {
            explodeProgress++;
        }
        if (!this.isExploding() && explodeProgress > 0F) {
            explodeProgress--;
        }
        if (this.isShooting() && shootProgress < 5F) {
            shootProgress = Math.min(5F, shootProgress + 2.5F);
        }
        if (!this.isShooting() && shootProgress > 0F) {
            shootProgress = Math.max(0F, shootProgress - 1);
        }
        if(attackCharge == 0){
            if(MathHelper.wrapDegrees(dialRot) != 0){
                dialRot = MathHelper.approachDegrees(dialRot, 0, 30);
            }else{
                dialRot = 0;
            }
        }else{
            dialRot = MathHelper.approach(dialRot, MAX_DIAL_ROT * attackCharge, 10);
        }
        if(postShootTime > 0){
            postShootTime--;
        }else{
            this.setShooting(false);
        }
        if(this.isExploding()){
            if(level().isClientSide && explodeProgress >= 18.0F){
                for(int i = 0; i < 3 + random.nextInt(2); i++){
                    level().addParticle(ParticleTypes.EXPLOSION, this.getRandomX(0.3F), this.getRandomY(), this.getRandomZ(0.3F), 0, 0, 0);
                }
            }
            if(explodeProgress >= 20.0F){
                if(!level().isClientSide && !hasExploded){
                    int gumballs = this.isCharged() ? 30 : 15;

                    if (this.entityData.get(CUSTOM_GUMBALL_AMOUNT) != 0) {
                        gumballs = this.entityData.get(CUSTOM_GUMBALL_AMOUNT);
                    }
                    for(int i = 0; i < gumballs + random.nextInt(5); i++){
                        GumballEntity gumball = new GumballEntity(this.level(), this);
                        gumball.setPos(new Vector3d(this.getRandomX(0.3F), this.getY() + 0.7F + random.nextFloat() * 0.5F, this.getRandomZ(0.3F)));
                        Vector3d delta = new Vector3d(random.nextFloat() - 0.5F, random.nextFloat() - 0.25F, random.nextFloat() - 0.5F).normalize().scale(random.nextFloat() * 0.25F + 0.75F);
                        gumball.setDeltaMovement(delta);
                        this.level().addFreshEntity(gumball);
                        if(this.isCharged()){
                            gumball.setMaximumBounces(10);
                            gumball.setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() + 2);
                        }else{
                            gumball.setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                        }
                    }
                    hasExploded = true;
                    this.discard();
                }
                this.playSound(SoundEvents.GUMBEEPER_EXPLODE, 1.0F, 1.0F);
            }
        }
        if(this.isCharged() && this.isAlive() && this.tickCount % 150 == 0){
            this.heal(1);
        }
    }

    @Override
    public void calculateEntityAnimation(LivingEntity entity, boolean flying) {
        entity.animationSpeedOld = entity.animationSpeed;

        double dx = entity.getX() - entity.xo;
        double dy = flying ? entity.getY() - entity.yo : 0.0D;
        double dz = entity.getZ() - entity.zo;

        float movement = MathHelper.sqrt((float)(dx * dx + dy * dy + dz * dz)) * 4.0F;
        if (movement > 1.0F) {
            movement = 1.0F;
        }

        entity.animationSpeed += (movement - entity.animationSpeed) * 0.4F;
        entity.animationPosition += entity.animationSpeed;
    }


    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == Items.FLINT_AND_STEEL || itemstack.getItem() == Items.FIRE_CHARGE) {
            SoundEvent soundevent = itemstack.get() == (Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), soundevent, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level().isClientSide) {
                this.setExploding(true);
                itemstack.hurtAndBreak(1, player, (p_32290_) -> {
                    p_32290_.broadcastBreakEvent(hand);
                });
            }

            return ActionResultType.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    public void thunderHit(ServerWorld serverLevel, LightningBoltEntity lightningBolt) {
        super.thunderHit(serverLevel, lightningBolt);
        this.setCharged(true);
    }

    public boolean isExploding() {
        return this.entityData.get(EXPLODING);
    }

    public void setExploding(boolean explode) {
        this.entityData.set(EXPLODING, explode);
    }

    public void setGumballsLeft(int i) {
        this.entityData.set(GUMBALLS_LEFT, i);
    }

    public int getGumballsLeft() {
        return this.entityData.get(GUMBALLS_LEFT);
    }

    public void setAttackCharge(float f) {
        this.entityData.set(ATTACK_CHARGE, f);
    }

    public float getAttackCharge() {
        return this.entityData.get(ATTACK_CHARGE);
    }

    public boolean isShooting() {
        return this.entityData.get(SHOOTING);
    }

    public void setShooting(boolean shooting) {
        this.entityData.set(SHOOTING, shooting);
    }

    public boolean isCharged() {
        return this.entityData.get(CHARGED);
    }

    public void setCharged(boolean explode) {
        this.entityData.set(CHARGED, explode);
    }

    public float getExplodeProgress(float partialTick) {
        return (prevExplodeProgress + (explodeProgress - prevExplodeProgress) * partialTick) * 0.05F;
    }

    public float getShootProgress(float partialTick) {
        return (prevShootProgress + (shootProgress - prevShootProgress) * partialTick) * 0.2F;
    }

    public boolean canShootGumball(){
        return getGumballsLeft() > 0 && dialRot >= MAX_DIAL_ROT && getAttackCharge() == 1.0F;
    }

    public void shootGumball(LivingEntity target) {
        Vector3d spawnGumballFrom = new Vector3d(0F, 0.3F, 0.4F).yRot(-this.yBodyRot * ((float) Math.PI / 180F)).add(position());
        int shotCount = this.isCharged() ? 3 : 1;
        this.playSound(SoundEvents.GUMBALL_LAUNCH, 1.0F, 1.0F);
        for(int i = 0; i < shotCount; i++){
            GumballEntity gumball = new GumballEntity(this.level(), this);
            gumball.setPos(spawnGumballFrom);
            Vector3d targetVec = new Vector3d(target.getX(), target.getY(0.6D), target.getZ());
            if(isCharged() && i != shotCount / 2){
                Vector3d vec3 = new Vector3d(i < shotCount / 2 ? 3.0F : -3.0F, 0.0F, 0.0F).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
                targetVec = targetVec.add(vec3);
            }
            double d0 = targetVec.x() - spawnGumballFrom.x;
            double d1 = targetVec.y() - spawnGumballFrom.y;
            double d2 = targetVec.z() - spawnGumballFrom.z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            gumball.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.2F, (float) (14 - this.level().getDifficulty().getId() * 4));
            this.level().addFreshEntity(gumball);
            if(isCharged()){
                gumball.setMaximumBounces(10);
                gumball.setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() + 2.0F);
            }else{
                gumball.setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            }
        }
        this.playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, 1.0F, this.getRandom().nextFloat() * 0.4F + 0.8F);
        if(!this.isCharged() || random.nextFloat() < 0.33F){
            this.setGumballsLeft(this.getGumballsLeft() - 1);
        }
        this.setAttackCharge(0.0F);
        this.setShooting(true);
        this.postShootTime = 5;
    }

    public double getDialRot(float partialTick) {
        return (prevDialRot + (dialRot - prevDialRot) * partialTick);
    }

    public boolean hasLineOfSightToGumballHole(Entity entity) {
        if (entity.level() != this.level()) {
            return false;
        } else {
            Vector3d vec3 = new Vector3d(this.getX(), this.getY() + 0.3F, this.getZ());
            Vector3d vec31 = new Vector3d(entity.getX(), entity.getEyeY(), entity.getZ());
            if (vec31.distanceTo(vec3) > 128.0D) {
                return false;
            } else {
                return this.level().clip(new RayTraceContext(vec3, vec31, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() == RayTraceResult.Type.MISS;
            }
        }
    }

    public void addAdditionalSaveData(CompoundNBT compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Charged", this.isCharged());
        compoundTag.putInt("Gumballs", this.getGumballsLeft());
        compoundTag.putInt("CustomGumballAmount", this.entityData.get(CUSTOM_GUMBALL_AMOUNT));
    }

    public void readAdditionalSaveData(CompoundNBT compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setCharged(compoundTag.getBoolean("Charged"));
        this.setGumballsLeft(compoundTag.getInt("Gumballs"));
        this.entityData.set(CUSTOM_GUMBALL_AMOUNT, compoundTag.getInt("CustomGumballAmount"));
    }


    @Override
    public boolean isPowered() {
        return this.isCharged();
    }

    public boolean canBeAffected(EffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != Effects.HUNGER;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GUMBEEPER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.GUMBEEPER_DEATH;
    }

    public class AttackGoal extends Goal {

        private int seeTime;
        private int strafingTime = -1;
        private boolean strafingClockwise;
        private boolean strafingBackwards;

        public AttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = GumbeeperEntity.this.getTarget();
            return target != null && target.isAlive();
        }


        public void stop() {
            super.stop();
            this.seeTime = 0;
            this.strafingTime = -1;
            GumbeeperEntity.this.setAttackCharge(0.0F);
            GumbeeperEntity.this.setExploding(false);
        }

        @Override
        public void tick() {
            LivingEntity target = GumbeeperEntity.this.getTarget();
            boolean canRange = GumbeeperEntity.this.getGumballsLeft() > 0;
            if(target != null){
                double dist = GumbeeperEntity.this.distanceTo(target);
                if(!canRange){
                    if(dist < target.getBbWidth() + 1.5F){
                        GumbeeperEntity.this.setExploding(true);
                    }else{
                        GumbeeperEntity.this.getNavigation().moveTo(target, 1.5F);
                    }
                }else if(dist < 16.0F && hasLineOfSightToGumballHole(target)){
                    GumbeeperEntity.this.getNavigation().stop();
                    strafingTime++;
                }else{
                    GumbeeperEntity.this.getNavigation().moveTo(target, 1F);
                    strafingTime = -1;
                }
                if (this.strafingTime >= 20) {
                    if ((double)GumbeeperEntity.this.getRandom().nextFloat() < 0.3D) {
                        this.strafingClockwise = !this.strafingClockwise;
                    }
                    if ((double)GumbeeperEntity.this.getRandom().nextFloat() < 0.3D) {
                        this.strafingBackwards = !this.strafingBackwards;
                    }
                    this.strafingTime = 0;
                }
                if(this.strafingTime > -1){
                    if (dist > 12.0F) {
                        this.strafingBackwards = false;
                    } else if (dist < 5.0F) {
                        this.strafingBackwards = true;
                    }
                    GumbeeperEntity.this.getMoveControl().strafe(this.strafingBackwards ? -1F : 1F, this.strafingClockwise ? 0.5F : -0.5F);
                    GumbeeperEntity.this.lookAt(target, 30.0F, 30.0F);
                }
                if(canRange && GumbeeperEntity.this.hasLineOfSightToGumballHole(target)){
                    GumbeeperEntity.this.setAttackCharge(Math.min(1F, GumbeeperEntity.this.getAttackCharge() + (GumbeeperEntity.this.isCharged() ? 0.3F : 0.1F)));
                    if(GumbeeperEntity.this.canShootGumball()){
                        GumbeeperEntity.this.shootGumball(target);
                    }
                }
            }
        }
    }
}