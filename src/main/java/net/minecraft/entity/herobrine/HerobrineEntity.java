package net.minecraft.entity.herobrine;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.potion.Effects;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class HerobrineEntity extends MonsterEntity implements ICrossbowUser {
    private static final DataParameter<Boolean> IS_CHARGING_CROSSBOW = EntityDataManager.defineId(HerobrineEntity.class, DataSerializers.BOOLEAN);
    private static final ResourceLocation HEROBRINE_TEXTURE = new ResourceLocation("textures/entity/herobrine.png");
    private AttackGoal meleeAttackGoal = new AttackGoal(this);

    public HerobrineEntity(EntityType<? extends HerobrineEntity> herobrine, World world) {
        super(herobrine, world);
        this.applyOpenDoorsAbility();
        this.setCanPickUpLoot(true);
    }

    private void applyOpenDoorsAbility() {
        if (GroundPathHelper.hasGroundPathNavigation(this)) {
            ((GroundPathNavigator)this.getNavigation()).setCanOpenDoors(true);
        }

    }

    @Nullable
    public ILivingEntityData finalizeSpawn(IServerWorld iServerWorld, DifficultyInstance difficulty, SpawnReason spawnReason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        if (this.random.nextFloat() < 0.2) {
            this.setItemInHand(Hand.MAIN_HAND, new ItemStack(Items.CROSSBOW));
        } else {
            this.setItemInHand(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND_SWORD));
        }

        return super.finalizeSpawn(iServerWorld, difficulty, spawnReason, data, compoundNBT);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(this, 1.0D, 8.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.addGoal(1, new RandomWalkingGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
    }




    @OnlyIn(Dist.CLIENT)
    public boolean isChargingCrossbow() {
        return this.entityData.get(IS_CHARGING_CROSSBOW);
    }

    public void setChargingCrossbow(boolean value) {
        this.entityData.set(IS_CHARGING_CROSSBOW, value);
    }

    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    public void performRangedAttack(LivingEntity entity, float v) {
        this.performCrossbowAttack(this, 1.6F);
    }

    public void shootCrossbowProjectile(LivingEntity entity, ItemStack stack, ProjectileEntity projectileEntity, float v) {
        this.shootCrossbowProjectile(this, entity, projectileEntity, v, 1.6F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CHARGING_CROSSBOW, false);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 90.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    public ResourceLocation getSkinTextureLocation() {
        return HEROBRINE_TEXTURE;
    }

    public void tick() {
        super.tick();
        ItemStack handItem = this.getMainHandItem();
        if (handItem.getItem() == Items.CROSSBOW && !this.goalSelector.getAvailableGoals().anyMatch(Predicate.isEqual(meleeAttackGoal))) {
            this.goalSelector.removeGoal(meleeAttackGoal);
        } else {
            this.goalSelector.addGoal(1, meleeAttackGoal);
        }


    }
    @Override
    protected void jumpFromGround() {
        float f = this.getJumpPower();
        if (this.hasEffect(Effects.JUMP)) {
            f += 0.1F * (float)(this.getEffect(Effects.JUMP).getAmplifier() + 1);
        }

        Vector3d vector3d = this.getDeltaMovement();
        this.setDeltaMovement(vector3d.x, (double)f, vector3d.z);
        if (this.isSprinting()) {
            float f1 = this.yRot * ((float)Math.PI / 180F);
            this.setDeltaMovement(this.getDeltaMovement().add((double)(-MathHelper.sin(f1) * 0.2F), 0.0D, (double)(MathHelper.cos(f1) * 0.2F)));
        }

        this.hasImpulse = true;
    }

    public boolean doHurtTarget(Entity entity) {

        return super.doHurtTarget(entity);
    }

    class AttackGoal extends MeleeAttackGoal {
        private Entity entity;
        private HerobrineEntity herobrine;
        public AttackGoal(HerobrineEntity herobrine) {
            super(herobrine, 0.68D, true);
            this.herobrine = herobrine;
        }

        public boolean canContinueToUse() {
            if (this.mob.getTarget() != null)
                entity = this.mob.getTarget();

            if (entity == null || !entity.isAlive() || !this.mob.getSensing().canSee(entity)) {
                return false;
            }

            // Check if Herobrine is facing the target
            Vector3d lookVec = this.herobrine.getViewVector(1.0F).normalize();
            Vector3d vecToTarget = new Vector3d(entity.getX() - this.herobrine.getX(), entity.getEyeY() - this.herobrine.getEyeY(), entity.getZ() - this.herobrine.getZ()).normalize();

            double dotProduct = lookVec.dot(vecToTarget);
            double threshold = Math.cos(Math.toRadians(30)); // 30 degrees field of view to consider "facing"

            return dotProduct > threshold;
        }

        public void stop() {
            super.stop();
            this.herobrine.setSprinting(false);
        }

        public void start() {
            super.start();
            this.herobrine.setSprinting(true);
        }



        public void tick() {
            super.tick();
            if(this.herobrine.getTarget() != null) {
                this.herobrine.getLookControl().setLookAt(this.herobrine.getTarget(), 30.0F, 30.0F);
            }
            if(this.mob.tickCount % 24 == 0 && this.mob.isOnGround()) {
                this.herobrine.jumpFromGround();
            }
        }

        @Override
        protected double getAttackReachSqr(LivingEntity livingEntity) {
            return 8.96D;
        }
    }
}
