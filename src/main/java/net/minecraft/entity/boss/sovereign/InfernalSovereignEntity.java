package net.minecraft.entity.boss.sovereign;

import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.InfernalFireballEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class InfernalSovereignEntity extends Monster {
    public static final DataParameter<Integer> DATA_PHASE = EntityDataManager.defineId(InfernalSovereignEntity.class, DataSerializers.INT);
    public static final DataParameter<Boolean> HAS_TARGET = EntityDataManager.defineId(InfernalSovereignEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Integer> ATTACK_TICKS = EntityDataManager.defineId(InfernalSovereignEntity.class, DataSerializers.INT);

    public InfernalSovereignPhase oPhase = InfernalSovereignPhase.HELLFIRE_THROW;
    public InfernalSovereignPhase phase = InfernalSovereignPhase.HELLFIRE_THROW;
    private static final Map<Integer, InfernalSovereignPhase> PHASE_MAP = Map.of(
            1, InfernalSovereignPhase.HELLFIRE_THROW,
            2, InfernalSovereignPhase.DODGE_FLYING,
            3, InfernalSovereignPhase.FIRE_CIRCLES_AND_FIREBALL,
            4, InfernalSovereignPhase.WITHER_SKELETON_BARRAGE
    );

    public static AttributeModifierMap.MutableAttribute createMonsterAttributes() {
        return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE, 6).add(Attributes.ARMOR_TOUGHNESS, 8D).add(Attributes.ARMOR, 20D).add(Attributes.MAX_HEALTH, 840).add(Attributes.FOLLOW_RANGE, 48).add(Attributes.MOVEMENT_SPEED, 0.24D).add(Attributes.KNOCKBACK_RESISTANCE, 3D);
    }
    public AnimationState attackingAnimation = new AnimationState();
    public int ramTimer = TickRangeConverter.secondsToTicks(5);
    public int hellfireTimer = TickRangeConverter.secondsToTicks(12);

    public RangedInteger ramTimeController = TickRangeConverter.rangeOfSeconds(4, 8);
    public RangedInteger hellfireTimeController = TickRangeConverter.rangeOfSeconds(8, 12);

    public InfernalSovereignEntity(EntityType<? extends Monster> entity, World world) {
        super(entity, world);
    }

    public void registerGoals() {
        this.targetSelector.addGoal(1, new PhaseOneGoal(this));
    }

    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PHASE, InfernalSovereignPhase.HELLFIRE_THROW.getId());
        this.entityData.define(HAS_TARGET, false);
        this.entityData.define(ATTACK_TICKS, 0);
    }

    public boolean isInPhase(int phaseNumber) {
        InfernalSovereignPhase targetPhase = PHASE_MAP.get(phaseNumber);
        if (targetPhase == null) {
            throw new IllegalArgumentException("Phase must be between 1 and 4");
        }
        return this.phase == targetPhase;
    }

    public boolean isInPhase(InfernalSovereignPhase phase) {
        return this.phase.getId() == phase.getId();
    }

    public int getPhase() {
        return entityData.get(DATA_PHASE);
    }

    public void addAdditionalSaveData(@NotNull CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);

        nbt.putInt("Phase", getPhase());
    }

    public void readAdditionalSaveData(@NotNull CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        this.entityData.set(DATA_PHASE, nbt.getInt("Phase"));
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.attackingAnimation.animateWhen(this.entityData.get(ATTACK_TICKS) > 0, this.tickCount);
        }
        if (entityData.get(ATTACK_TICKS) > 0) {
            this.entityData.set(ATTACK_TICKS, entityData.get(ATTACK_TICKS) - 1);
        }
        this.entityData.set(DATA_PHASE, this.phase.getId());
        this.oPhase = this.phase;



    }

    public boolean doHurtTarget(@NotNull Entity target) {
        entityData.set(ATTACK_TICKS, 14);

        return super.doHurtTarget(target);
    }


    public static abstract class PhaseGoal extends Goal {
        final InfernalSovereignPhase phase;
        final InfernalSovereignEntity mob;
        private int attackTime = 20;
        public PhaseGoal(InfernalSovereignPhase phase, InfernalSovereignEntity entity) {
            this.phase = phase;
            this.mob = entity;
        }

        public void tick() {
            super.tick();
            --this.attackTime;
            assert mob.getTarget() != null;
            double distance = mob.distanceToSqr(this.mob.getTarget());
            boolean flag = this.mob.getSensing().canSee(mob.getTarget());
            if (distance < 4 && flag && this.attackTime <= 0) {
                this.attackTime = 20;
                this.mob.doHurtTarget(mob.getTarget());
            }
        }

        @Override
        public boolean canUse() {
            return mob.isInPhase(phase) && mob.getTarget() != null && mob.getTarget().isAlive();
        }
    }



    public static class PhaseOneGoal extends PhaseGoal {
        final World level;
        final InfernalSovereignEntity mob;
        public PhaseOneGoal(InfernalSovereignEntity mob) {
            super(InfernalSovereignPhase.HELLFIRE_THROW, mob);
            this.mob = mob;
            this.level = mob.level;
        }

        public void tick() {
            super.tick();
            mob.getNavigation().moveTo(mob.getTarget().position(), 1.6);
            mob.lookControl.setLookAt(mob.getTarget().getEyePosition(1.0f));
            if (this.mob.ramTimer > 0) {
                --this.mob.ramTimer;
            } else {
                this.mob.ramTimer = mob.ramTimeController.randomValue(this.mob.random);
                this.ram();
            }

            if (this.mob.hellfireTimer > 0) {
                --this.mob.hellfireTimer;
            } else {
                this.mob.hellfireTimer = mob.hellfireTimeController.randomValue(this.mob.random);
                spawnHellfire();
            }
        }

        public void ram() {
            assert mob.getTarget() != null;
            LivingEntity target = mob.getTarget();

            // Calculate the directional vector towards the target
            double deltaX = target.getX() - this.mob.getX();
            double deltaY = target.getY() - this.mob.getY();
            double deltaZ = target.getZ() - this.mob.getZ();

            // Calculate the distance to the target
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

            // Normalize the direction vector
            double normX = deltaX / distance;
            double normY = deltaY / distance;
            double normZ = deltaZ / distance;

            // Adjust velocity dynamically based on distance and compensate for friction
            double velocityScale = Math.min(distance / 5.0, 3); // Scale velocity based on distance
            double frictionCompensation = 1.4; // Tweak this value to match the terrain friction in your game

            // Set the mob's movement vector
            mob.setDeltaMovement(
                    this.mob.getDeltaMovement()
                            .add(normX * velocityScale * frictionCompensation,
                                    normY * velocityScale * frictionCompensation,
                                    normZ * velocityScale * frictionCompensation)
                            .normalize().add(0, 0.2, 0)
                            .scale(velocityScale)
            );
        }

        public void spawnHellfire() {
            int fireballCount = 64; // Number of fireballs to spawn

            // Get the spawn position (above the mob)
            double spawnX = this.mob.getX();
            double spawnY = this.mob.getEyePosition(1.0f).y + 1; // Adjust height as needed
            double spawnZ = this.mob.getZ();

            for (int i = 0; i < fireballCount; i++) {
                // Calculate the angle for this fireball
                double angle = 4 * Math.PI * i / fireballCount;

                // Calculate the outward direction vector
                double directionX = Math.cos(angle);
                double directionZ = Math.sin(angle);

                // Create the fireball entity
                InfernalFireballEntity fireballEntity = new InfernalFireballEntity(this.level, this.mob);

                // Set the spawn position
                fireballEntity.setPos(spawnX, spawnY, spawnZ);

                // Shoot the fireball outward
                fireballEntity.shoot(directionX, 0, directionZ, 0.8f, 0f);

                // Add the fireball to the world
                this.level.addFreshEntity(fireballEntity);
            }
        }
    }
}
