package net.minecraft.entity.ai.goal;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.tool.BowItem;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;
import java.util.List;

public class AdvancedBowAttackGoal<T extends Monster & IRangedAttackMob> extends Goal {
    private final T mob;
    private final double speedModifier;
    private int attackIntervalMin;
    private final float attackRadiusSqr;
    private int attackTime = -1;
    private float strafeSpeed;
    private int moveFartherBackwards = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;


    public AdvancedBowAttackGoal(T p_i47515_1_, double p_i47515_2_, int p_i47515_4_, float p_i47515_5_) {
        this.mob = p_i47515_1_;
        this.speedModifier = p_i47515_2_;
        this.attackIntervalMin = p_i47515_4_;

        this.attackRadiusSqr = p_i47515_5_ * p_i47515_5_;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public void setMinAttackInterval(int p_189428_1_) {
        this.attackIntervalMin = p_189428_1_;
    }

    public boolean canUse() {
        return this.mob.getTarget() == null ? false : this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        if (mob.hasEffect(Effects.CONFUSED)) return false;
        
        return this.mob.isHolding(Items.BOW) || this.mob.isHolding(Items.BONE_BOW) || this.mob.isHolding(Items.AERIAL_BANE);
    }

    public boolean canContinueToUse() {
        return (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingBow();
    }

    public void start() {
        super.start();
        this.mob.setAggressive(true);
    }

    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.mob.stopUsingItem();
    }

    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
            boolean flag = this.mob.getSensing().canSee(livingentity);
            boolean flag1 = this.seeTime > 0;
            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            boolean avoidMonster = this.shouldAvoidMonsterNearby();
            if (!(d0 > (double)this.attackRadiusSqr) && this.seeTime >= 20) {
                if (!avoidMonster) {
                    this.mob.getNavigation().stop();
                    ++this.strafingTime;
                }
            } else {
                if (!avoidMonster && !(livingentity instanceof IronGolemEntity)) {
                    this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
                }
                this.strafingTime = -1;
            }

            if (livingentity instanceof IronGolemEntity) {
                IronGolemStrafe(livingentity);
            } else {

                if (this.strafingTime >= 10) {
                    if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                        this.strafingClockwise = !this.strafingClockwise;
                    }

                    if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                        this.strafingBackwards = !this.strafingBackwards;
                    }

                    this.strafingTime = 0;
                }

                if (this.strafingTime > -1) {
                    if (d0 > (double)(this.attackRadiusSqr * 0.75F)) {
                        this.strafingBackwards = false;
                    } else if (d0 < (double)(this.attackRadiusSqr * 0.25F)) {
                        this.strafingBackwards = true;
                    }

                    strafeSpeed = 0.7F;
                    boolean isClose = d0 < 27d;
                    if (isClose) {
                        strafeSpeed = 1.1F;
                        this.strafingClockwise = this.mob.nextBoolean();
                    }
                    if (this.strafingBackwards) {
                        if (this.moveFartherBackwards++ >= 35) {
                            if (!avoidMonster) {
                                this.mob.getNavigation().moveTo(livingentity, this.speedModifier * 1.12D);
                            }
                            this.moveFartherBackwards = -1;
                        }
                        if (this.mob.nextFloat() < (isClose ? 0.16 : 0.09)) {
                            this.mob.getJumpControl().jump();
                        }
                    }

                    if (livingentity instanceof VillagerEntity) {
                        if (!avoidMonster) {
                            this.mob.getNavigation().moveTo(livingentity, this.speedModifier * 1.1D);
                        }
                    }

                    this.mob.strafe(this.strafingBackwards ? -strafeSpeed : strafeSpeed,
                            avoidMonster ? (this.strafingClockwise ? -strafeSpeed : strafeSpeed)
                                    : (this.strafingClockwise ? strafeSpeed : -strafeSpeed));
                    this.mob.lookAt(livingentity, 30.0F, 30.0F);
                } else {
                    this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                }

            }


            attackLogic(flag, livingentity);
        }
    }

    private void IronGolemStrafe(LivingEntity livingentity) {
        this.strafingBackwards = true;
        this.strafingClockwise = !(this.mob.nextFloat() < 0.3) && this.mob.nextBoolean();
        strafeSpeed = 1.5F;

        if (this.strafingTime >= 10) {
            if ((double)this.mob.getRandom().nextFloat() < 0.5D) {
                this.strafingClockwise = !this.strafingClockwise;
            }
            this.strafingTime = 0;
        }

        if (this.strafingTime > -1) {

            strafeSpeed = 1.2F;
            this.mob.strafe(-strafeSpeed,
                    this.strafingClockwise ? strafeSpeed : -strafeSpeed);

            if (this.mob.nextFloat() < 0.1) {
                this.mob.getJumpControl().jump();
            }
        } else {
            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
        }

        this.mob.lookAt(livingentity, 30.0F, 30.0F);
    }


    public void attackLogic(boolean flag, LivingEntity livingentity) {
        if (this.mob.isUsingItem()) {
            if (!flag && this.seeTime < -60) {
                this.mob.stopUsingItem();
            } else if (flag) {
                LivingEntity targetEntity = this.mob.getTarget();
                boolean canShoot = anyBlocked(livingentity, targetEntity);

                if (canShoot) {
                    int i = this.mob.getTicksUsingItem();
                    if (i >= 20) {
                        this.mob.stopUsingItem();
                        this.mob.performRangedAttack(livingentity, BowItem.getPowerForTime(i));
                        this.attackTime = this.attackIntervalMin;
                    }
                } else {
                    this.mob.strafe(this.strafingBackwards ? -strafeSpeed : strafeSpeed, this.strafingClockwise ? strafeSpeed : -strafeSpeed);
                }
            }
        } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
            this.mob.startUsingItem(ProjectileHelper.getWeaponHoldingHand(this.mob, Items.BOW));
        }
    }

    private boolean anyBlocked(LivingEntity livingentity, LivingEntity targetEntity) {
        boolean canShoot = true;

        Vector3d mobPos = this.mob.position();
        Vector3d targetPos = targetEntity.position();
        Vector3d direction = targetPos.subtract(mobPos).normalize();
        double distanceToTarget = mobPos.distanceTo(targetPos);

        AxisAlignedBB lineOfSightArea = new AxisAlignedBB(
                mobPos, mobPos.add(direction.scale(distanceToTarget))
        ).inflate(0.3D);

        for (LivingEntity nearbyEntity : this.mob.level.getEntitiesOfClass(LivingEntity.class, lineOfSightArea)) {
            if (nearbyEntity instanceof Monster || nearbyEntity.isAlliedTo(this.mob)) {
                if (nearbyEntity != this.mob && nearbyEntity != livingentity && nearbyEntity != this.mob.getVehicle() && this.mob.getSensing().canSee(nearbyEntity)) {
                    canShoot = false;
                    break;
                }
            }
        }
        return canShoot;
    }

    private boolean shouldAvoidMonsterNearby() {
        AxisAlignedBB boundingBox = this.mob.getBoundingBox().inflate(1D);
        List<Monster> nearbyMonsters = this.mob.level.getEntitiesOfClass(Monster.class, boundingBox,
                entity -> entity != this.mob && !(entity instanceof AbstractSkeletonEntity));

        if (!nearbyMonsters.isEmpty()) {
            Monster closestMonster = nearbyMonsters.get(0);
            double deltaX = closestMonster.getX() - this.mob.getX();
            double deltaZ = closestMonster.getZ() - this.mob.getZ();
            if (Math.abs(deltaX) > Math.abs(deltaZ)) {
                this.strafingClockwise = deltaX < 0;
            } else {
                this.strafingBackwards = deltaZ > 0;
            }
            return true;
        }
        return false;
    }



}