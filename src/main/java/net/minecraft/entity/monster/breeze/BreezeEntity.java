package net.minecraft.entity.monster.breeze;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.Monster;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BreezeEntity extends Monster {
    private static final int SLIDE_PARTICLES_AMOUNT = 20;
    private static final int IDLE_PARTICLES_AMOUNT = 1;
    private static final int JUMP_DUST_PARTICLES_AMOUNT = 20;
    private static final int JUMP_TRAIL_PARTICLES_AMOUNT = 3;
    private static final int JUMP_TRAIL_DURATION_TICKS = 5;
    private static final int JUMP_CIRCLE_DISTANCE_Y = 10;
    private static final float FALL_DISTANCE_SOUND_TRIGGER_THRESHOLD = 3.0f;
    private static final int WHIRL_SOUND_FREQUENCY_MIN = 1;
    private static final int WHIRL_SOUND_FREQUENCY_MAX = 80;

    public BreezeEntity(EntityType<? extends Monster> entityType, World level) {
        super(entityType, level);
        this.setPathfindingMalus(PathNodeType.TRAPDOOR, -1.0f);
        this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, -1.0f);
        this.xpReward = 10;
    }

    private int jumpTrailStartedTick = 0;
    private int soundTick = 0;

    public AnimationState idle = new AnimationState();
    public AnimationState slide = new AnimationState();
    public AnimationState slideBack = new AnimationState();
    public AnimationState longJump = new AnimationState();
    public AnimationState shoot = new AnimationState();
    public AnimationState inhale = new AnimationState();

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.63f)
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0);
    }


    @Override
    public void onSyncedDataUpdated(DataParameter<?> entityDataAccessor) {
        if (this.level().isClientSide() && DATA_POSE.equals(entityDataAccessor)) {
            this.resetAnimations();
            Pose pose = this.getPose();
            switch (pose) {
                case SHOOTING: {
                    this.shoot.startIfStopped(this.tickCount);
                    break;
                }
                case INHALING: {
                    this.inhale.startIfStopped(this.tickCount);
                    break;
                }
                case SLIDING: {
                    this.slide.startIfStopped(this.tickCount);
                }
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    private void resetAnimations() {
        this.shoot.stop();
        this.idle.stop();
        this.inhale.stop();
        this.longJump.stop();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomWalkingGoal(this, 0.4d));

    }


    @Override
    public void tick() {
        Pose pose = this.getPose();
        switch (pose) {
            case SLIDING: {
                this.emitGroundParticles(20);
                break;
            }
            case SHOOTING:
            case INHALING:
            case STANDING: {
                this.resetJumpTrail().emitGroundParticles(1 + this.getRandom().nextInt(1));
                break;
            }
            case LONG_JUMPING: {
                this.longJump.startIfStopped(this.tickCount);
                this.emitJumpTrailParticles();
            }
        }
        this.idle.startIfStopped(this.tickCount);
        if (pose != Pose.SLIDING && this.slide.isStarted()) {
            this.slideBack.start(this.tickCount);
            this.slide.stop();
        }
        int n = this.soundTick = this.soundTick == 0 ? ParticleTypes.randomBetweenInclusive(random, 1, 80) : this.soundTick - 1;
        if (this.soundTick == 0) {
            this.playWhirlSound();
        }
        super.tick();
    }

    public void playWhirlSound() {
        float f = 0.7f + 0.4f * this.random.nextFloat();
        float f2 = 0.8f + 0.2f * this.random.nextFloat();
        this.level.playSound(null, this, SoundEvents.BREEZE_WHIRL, this.getSoundSource(), f2, f);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BREEZE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.BREEZE_HURT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.onGround ? SoundEvents.BREEZE_IDLE_GROUND : SoundEvents.BREEZE_IDLE_AIR;
    }

    public BreezeEntity resetJumpTrail() {
        this.jumpTrailStartedTick = 0;
        return this;
    }

    public BlockState getInBlockState() {
        return this.level.getBlockState(this.blockPosition());
    }

    public void emitJumpTrailParticles() {
        if (++this.jumpTrailStartedTick > 5) {
            return;
        }
        BlockState blockState = !this.getInBlockState().isAir() ? this.getInBlockState() : this.getBlockStateOn();
        Vector3d vec3 = this.getDeltaMovement();
        Vector3d vec32 = this.position().add(vec3).add(0.0, 0.1f, 0.0);
        for (int i = 0; i < 3; ++i) {
            this.level().addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockState), vec32.x, vec32.y, vec32.z, 0.0, 0.0, 0.0);
        }
    }

    public void emitGroundParticles(int n) {
        BlockState blockState;
        if (this.isPassenger()) {
            return;
        }
        Vector3d vec3 = this.getBoundingBox().getCenter();
        Vector3d vec32 = new Vector3d(vec3.x, this.position().y, vec3.z);
        blockState = !this.getInBlockState().isAir() ? this.getInBlockState() : this.getBlockStateOn();
        if (blockState.getRenderShape() == BlockRenderType.INVISIBLE) {
            return;
        }
        for (int i = 0; i < n; ++i) {
            this.level().addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockState), vec32.x, vec32.y, vec32.z, 0.0, 0.0, 0.0);
        }
    }

    public boolean withinInnerCircleRange(Vector3d vec3) {
        Vector3d vec32 = this.blockPosition().getCenter();
        return vec3.closerThan(vec32, 4.0, 10.0);
    }

    @Override
    public boolean canAttackType(EntityType<?> entityType) {
        return entityType == EntityType.PLAYER || entityType == EntityType.IRON_GOLEM;
    }

    @Override
    public int getMaxHeadYRot() {
        return 30;
    }

    @Override
    public int getHeadRotSpeed() {
        return 25;
    }

    public double getFiringYPosition() {
        return this.getY() + (double)(this.getBbHeight() / 2.0f) + (double)0.3f;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.getEntity() instanceof BreezeEntity || super.isInvulnerableTo(damageSource);

    }

    @Override
    public boolean causeFallDamage(float f, float f2) {
        if (f > 3.0f) {
            this.playSound(SoundEvents.BREEZE_LAND, 1.0f, 1.0f);
        }
        return super.causeFallDamage(f, f2);
    }
}
