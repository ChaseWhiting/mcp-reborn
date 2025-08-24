package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;


public class MeleeAttack
extends Task<Mob> {
    private final int cooldownBetweenAttacks;

    public MeleeAttack(int n) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleStatus.VALUE_ABSENT));
        this.cooldownBetweenAttacks = n;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverLevel, Mob mob) {
        LivingEntity livingEntity = this.getAttackTarget(mob);
        return !this.isHoldingUsableProjectileWeapon(mob) && BrainUtil.canSee(mob, livingEntity) && BrainUtil.isWithinMeleeAttackRange(mob, livingEntity);
    }

    private boolean isHoldingUsableProjectileWeapon(Mob mob) {
        return mob.isHolding(item -> {

            return item instanceof ShootableItem && mob.canFireProjectileWeapon((ShootableItem) item);
        });
    }

    @Override
    protected void start(ServerWorld serverLevel, Mob mob, long l) {
        LivingEntity livingEntity = this.getAttackTarget(mob);
        BrainUtil.lookAtEntity(mob, livingEntity);
        mob.swing(Hand.MAIN_HAND);
        mob.doHurtTarget(livingEntity);
        mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, this.cooldownBetweenAttacks);
    }

    private LivingEntity getAttackTarget(Mob mob) {
        return mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}

