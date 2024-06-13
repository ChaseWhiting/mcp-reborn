package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class ShootTargetTask<E extends MobEntity & ICrossbowUser, T extends LivingEntity> extends Task<E> {
    private int attackDelay;
    private ShootTargetTask.Status crossbowState = ShootTargetTask.Status.UNCHARGED;
    private final int shootingRangeDistance = 16;

    public ShootTargetTask() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT), 1200);
    }

    protected boolean checkExtraStartConditions(ServerWorld world, E entity) {
        LivingEntity target = getAttackTarget(entity);
        return entity.isHolding(Items.CROSSBOW) && BrainUtil.canSee(entity, target) && isWithinShootingRange(entity, target);
    }

    protected boolean canStillUse(ServerWorld world, E entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(world, entity);
    }

    protected void tick(ServerWorld world, E entity, long gameTime) {
        LivingEntity target = getAttackTarget(entity);
        this.lookAtTarget(entity, target);
        this.crossbowAttack(entity, target);
    }

    protected void stop(ServerWorld world, E entity, long gameTime) {
        if (entity.isUsingItem()) {
            entity.stopUsingItem();
        }

        if (entity.isHolding(Items.CROSSBOW)) {
            entity.setChargingCrossbow(false);
            CrossbowItem.setCharged(entity.getUseItem(), false);
        }
    }

    private void crossbowAttack(E entity, LivingEntity target) {
        if (this.crossbowState == ShootTargetTask.Status.UNCHARGED) {
            entity.startUsingItem(ProjectileHelper.getWeaponHoldingHand(entity, Items.CROSSBOW));
            this.crossbowState = ShootTargetTask.Status.CHARGING;
            entity.setChargingCrossbow(true);
        } else if (this.crossbowState == ShootTargetTask.Status.CHARGING) {
            if (!entity.isUsingItem()) {
                this.crossbowState = ShootTargetTask.Status.UNCHARGED;
            }

            int i = entity.getTicksUsingItem();
            ItemStack itemstack = entity.getUseItem();
            if (i >= CrossbowItem.getChargeDuration(itemstack)) {
                entity.releaseUsingItem();
                this.crossbowState = ShootTargetTask.Status.CHARGED;
                this.attackDelay = 20 + entity.getRandom().nextInt(20);
                entity.setChargingCrossbow(false);
            }
        } else if (this.crossbowState == ShootTargetTask.Status.CHARGED) {
            --this.attackDelay;
            if (this.attackDelay == 0) {
                if (entity instanceof PiglinBruteEntity) {
                    this.attackDelay = 10;
                } else {
                    this.attackDelay = 20 + entity.getRandom().nextInt(20);
                }
                this.crossbowState = ShootTargetTask.Status.READY_TO_ATTACK;
            }
        } else if (this.crossbowState == ShootTargetTask.Status.READY_TO_ATTACK) {
            entity.performRangedAttack(target, 1.0F);
            ItemStack itemstack1 = entity.getItemInHand(ProjectileHelper.getWeaponHoldingHand(entity, Items.CROSSBOW));
            CrossbowItem.setCharged(itemstack1, false);
            this.crossbowState = ShootTargetTask.Status.UNCHARGED;
        }
    }

    private void lookAtTarget(MobEntity entity, LivingEntity target) {
        entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(target, true));
    }

    private static LivingEntity getAttackTarget(LivingEntity entity) {
        return entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    private boolean isWithinShootingRange(E entity, LivingEntity target) {
        double distance = target.distanceToSqr(entity);
        return distance <= (double) this.shootingRangeDistance * this.shootingRangeDistance;
    }

    static enum Status {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK;
    }
}
